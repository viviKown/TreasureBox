## Android中的单元测试

## 分类

Android中的单元测试分为两种，Local Unit Tests 和 Instrumented Tests，前者运行在JVM，后者需要运行再Android设备

### Local Unit TestsLocal Unit Tests

运行在本地JVM，不需要安装APP，所以运行时间很快。也因此不能依赖Android的API，所以大多数时候需要用Mock的形式来做替换（后面会提到）

#### 配置与使用

测试代码目录：module-name/src/test/java

一般使用到的测试框架

JUnit4

Mockito

使用Gradle添加相应的库

```gradle
dependencies {
// Required -- JUnit 4 framework
testCompile 'junit:junit:4.12'
// Optional -- Mockito framework
testCompile 'org.mockito:mockito-core:1.10.19'
}
```

Mockito

Mockito的两个重要的功能是，验证Mock对象的方法的调用和可以指定mock对象的某些方法的行为。（对于不懂Mock概念的同学来说，第一次看到的确很可能很难理解）

**为什么要使用Mockito？**

这是项目中的一个例子：

```java
/**
* @param <T> 用于过滤的实体类型
*/
public interface BaseItemFilter<T> {
/**
* @param item
* @return true：不过滤;false：需要过滤
*/
boolean accept(T item);
}
```

BaseItemFilter是用来判断某种指定类型的实体是否需要过滤的，类似java中的FileFilter，目的是为了用了过滤不符合要求的实体。

以下是我们的关键服务过滤器的实现：

```java
public class EssentialProcessFilter implements BaseItemFilter<RunningAppBean> {
/**
* 系统关键进程及用户主要的进程
*/
private static HashSet<String> sCoreList = new HashSet<String>();

/**
* 加载系统核心进程列表
* @param context
*/
public static void loadCoreList(Context context) {
if (sCoreList.isEmpty()) {
final Resources r = context.getResources();
String[] corePackages = r.getStringArray(R.array.default_core_list);
Collections.addAll(sCoreList, corePackages);
}
}

@Override
public boolean accept(RunningAppBean appModle) {
return appModle != null && !(isEssentialProcess(appModle.mPackageName) || isEssentialProcessMock(appModle.mPackageName, appModle.mIsSysApp));
}

/**
* 判断进程是否属于重要进程
* @param process
* @return
*/
public static boolean isEssentialProcess(String process) {
return sCoreList.contains(process);
}

/**
* 系统关键进程关键词模糊匹配
* @param packageName
* @param isSystemApp
* @return
*/
public static boolean isEssentialProcessMock (String packageName, boolean isSystemApp) {
return 省略...额外的一些判断;
}

}
```

可以看到，这里的关键服务的判断的判断规则可以分两部分，一个是从String.xml中预设的一段Arrays数组查找是否右符合的，这个需要在初始化或某个时机预先调用EssentialProcessFilter#loadCoreList（Context context）方法来加载，另外的一个判断是在EssentialProcessFilter#isEssentialProcessMock方法中定义，这个类中accept方法，定义了只要符合其中一种规则，那么我们就需要把它过滤。

这个时候我们来写单元测试，你一开始就会发现你没有办法新建一个Context对象来读取String.xml，即使你想尽任何方法新建一个ContextImpl实例，最后你还是会出错的，主要原因再在于Gradle运行Local Unit Test 所使用的android.jar里面所有API都是空实现，并抛出异常的。 现在想想，我们实际上并不需要真的读取String.xml，我们需要验证的是记录在我们的关键列表集合是否生效，既然这样，我们前面说过了，Mockito的两个重要的功能是，验证Mock对象的方法的调用和可以指定mock对象的某些方法的行为。我们是否可以Mock一个Context对象并且指定它读取String.xml的行为？答案是可以的，如下就是使用Mockito的一段测试代码

```java
public class TestListFilter2 {
@Mock
Context mContext;
@Mock
Resources mResources;
@Before
public void setup() {
MockitoAnnotations.initMocks(this);
Mockito.when (mContext.getResources() ).thenReturn(mResources);
Mockito.when( mResources.getStringArray (R.array.default_core_list) ).thenReturn ( getEssentialProcessArray());
//模拟加载XML资源
EssentialProcessFilter.loadCoreList (mContext);
}

/**
* 测试关键服务的过滤器
*/
@Test
public void testEssentialFilter() {
EssentialProcessFilter processFilter = new EssentialProcessFilter();
ListFilter<RunningAppBean> listFilter = Mockito.spy(ListFilter.class);
listFilter.addFilter(processFilter);
List<RunningAppBean> list = new ArrayList< RunningAppBean>();
list.addAll(getEssentialAppBean());
list.addAll(getNormalRunningApp());
List<RunningAppBean> result = Mockito.mock (ArrayList.class);
for (RunningAppBean runningAppBean : list) {
if (listFilter.accept (runningAppBean)) {
result.add (runningAppBean);
}
}
Mockito.verify (listFilter, Mockito.times (list.size())).accept (Mockito.any( RunningAppBean.class));
Mockito.verify(result, Mockito.times (getNormalRunningApp().size()) ).add( Mockito.any(RunningAppBean.class));
}
/**
* 关键服务应用包名
*/
public String[] getEssentialProcessArray() {
return new String[]{"android.process.acore", "android.process.media", "android.tts", "android.uid.phone", "android.uid.shared", "android.uid.system"};
}
}
```

上面的代码，我们使用@Mock来Mock了Context和Resource对象，这需要我们在setup的时候使用MockitoAnnotations.initMocks(this)方法来使得这些注解生效，如果再不使用@Mock注解的时候，我们还可以使用Mockito.mock方法来Mock对象。这里我们指定了Context对象在调用getResources方法的时候返回一个同样是Mock的Resources对象，这里的Resources对象，指定了在调用getStringArray(R.array.default_core_list)方法的时候返回的字符串数组的数据是通过我们的getEssentialProcessArray方法获得的，而不是真的是加载String.xml资源。最后调用EssentialProcessFilter.loadCoreList(mContext)方法使得EssentialProcessFilter内记录的关键服务集合的数据源就是我们指定的。目前，我们使用的就是改变Mock对象的行为的功能。

在测试单元testEssentialFilter方法中，使用Mockito.spy(ListFilter.class)来Mock一个ListFilter对象（这是一个BaseItemFilter的实现，里面记录了BaseItemFilter的集合，用了记录一系列的过滤规则），这里使用spy方法Mock出来的对象除非指定它的行为，否者调用这个对象的默认实现，而使用mock方法Mock出来的对象，如果不指定对象的行为的话，所有非void方法都将返回默认值：int、long类型方法将返回0，boolean方法将返回false，对象方法将返回null等等，我们也同样可以使用@spy注解来Mock对象。这里的listFilter对象使用spy是为了使用默认的行为，确保accept方法的逻辑正确执行，而result对象使用mock方式来Mock，是因为我们不需要真的把过滤后的数据添加到集合中，而只需要验证这个Mock对象的add方法调用的多少次即可。

最后就是对Mock对象的行为的验证，分别验证了listFilter#accept方法和result#add方法的执行次数，其中Mockito#any系列方法用来指定无论传入任何参数值。

#### Local Unit Tests的优点

不依赖Android的API，运行速度快，所以更快地得到结果反馈

引导更好的代码设计（单一职责、依赖注入），如果一个类不好测，往往是因为这个类的设计是有问题

### Instrumented Tests

Instrumented Unit tests是需要运行再Android设备上的（物理/虚拟），通常我们使用Mock的方式不能很好解决对Android的API的依赖的这个问题，而使用这种测试方式可以依赖Android的API，使用Android提供的Instrumentation系统，将单元测试代码运行在模拟器或者是真机上，但很多情况来说，我们还是会需要和Mockito一起使用的。这中方案速度相对慢，因为每次运行一次单元测试，都需要将整个项目打包成apk，上传到模拟器或真机上，就跟运行了一次app似得

#### 配置

测试代码目录： module-name/src/androidTests/java/

一般使用到的测试框架

AndroidJUnitRunner ： JUnit 4-compatible test runner for Android

Espresso ：UI testing framework; suitable for functional UI testing within an app

UI Automator ： UI testing framework; suitable for cross-app functional UI testing across system and installed apps

通过Gralde添加相应的库

```
dependencies {
androidTestCompile 'com.android.support:support-annotations:24.0.0'
androidTestCompile 'com.android.support.test:runner:0.5'
androidTestCompile 'com.android.support.test:rules:0.5'
// Optional -- Hamcrest library
androidTestCompile 'org.hamcrest:hamcrest-library:1.3'
// Optional -- UI testing with Espresso
androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2.2'
// Optional -- UI testing with UI Automator
androidTestCompile 'com.android.support.test.uiautomator:uiautomator-v18:2.1.2'
}
```

另外还需要在你的App的模块的build.gralde文件添加如下设置：

```
android {
defaultConfig {
testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
}
}
```



使用

AndroidJUnitRunner

AndroidJUnitRunner是JUnit4运行在Android平台上的兼容版本，使用也是很简单的且方法类似，以上面测试异步任务为例子

```java
@RunWith(AndroidJUnit4.class)
@MediumTest
public class TestRunningAppScanner {
CountDownLatch mSignal = null;
RunningAppScanner mRunningAppScanner;
@Mock
BaseScanner.ScanListener <List<RunningAppBean >> mRunningAppBeanScanListener;

@Before
public void setUp() {
mSignal = new CountDownLatch(1);
MockitoAnnotations.initMocks(this);
mRunningAppScanner = Mockito.spy(new RunningAppScanner (InstrumentationRegistry .getTargetContext()));
}

@After
public void tearDown() {
mSignal.countDown();
}

@Test
public void testRunningApp() throws InterruptedException {
Assert.assertFalse (mRunningAppScanner.isRunning());
Mockito.doAnswer(new Answer() {
@Override
public Object answer (InvocationOnMock invocation) throws Throwable {
mSignal.countDown();
return invocation.getArguments();
}
}).when(mRunningAppBeanScanListener ).onScannedCompleted ((List<RunningAppBean>) Mockito.any());
mRunningAppScanner.setScanListener (mRunningAppBeanScanListener);
mRunningAppScanner .startScanning();
Assert.assertTrue (mRunningAppScanner.isRunning());
mSignal.await();
Assert.assertFalse (mRunningAppScanner.isRunning());
Mockito.verify (mRunningAppBeanScanListener, Mockito.times(1) ).onScannedCompleted ((List<RunningAppBean>) Mockito.any());
}
}
```



需要在测试类的定义上加上@RunWith(AndroidJUnit4.class)标注，另外@MediumTest标注是用来指定测试规模，有三种类型可以指定，限制如下，因为这里的异步任务的目的是扫描所有正在运行的App，所以这里使用@MediumTest，其他的Assert#XXX、@Befor，@After使用一致，另外这里还搭配Mockito使用。因为需要真正用到系统的一些服务（AMS，PKMS）或资源，这里的InstrumentationRegistry可以为我们提供Instrumentation对象，测试App的Context对象，目标App的Context对象和通过命令行启动测试所提供的参数信息。这里的 InstrumentationRegistry.getTargetContext() 就是用来获取目标App的Context对象。

![1607511913431](../../../../markdown_img/1607511913431.png)
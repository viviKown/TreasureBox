
//==============================================================================
/**
 * @mainpage AllApp

 # AllApp

ALlApp是一个应用助手，用于显示应用相关信息，主要有以下信息：

 - 1.需要显示所有TV安装的应用名称，图标。
 - 2.选中应用后，显示通过列表方式显示应用使用权限情况。
 - 3.选中应用后，显示应用内存，CPU，存储，进程号，应用包名等信息进行显示。

作为一个信息展示软件，我们将所有信息在一个页面显示。

 */
//==============================================================================


package com.tcl.allapp;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @brief AllApp主类
 *
 * AllApp是软件主类，控制软件整体逻辑，显示TV所有应用列表信息，并控制指定应用信息的更新。
 */
public class AllApp extends AppCompatActivity {
    public final String Tag = "AllApp";         /**< 软件Tag，用于logcat使用 */
    public final int TimerMs = 2000;            /**< 定时器间隔，单位：milliseconds */
    private List<AppInfo> appInfoList;          /**< 所有应用信息列表 */
    private Timer timer;                        /**< 定时器，用于定时查询信息 */
    private boolean timerRunning;               /**< Timer是否正在运行 */
    private TimerTask task;                     /**< 定时器任务 */
    private TimerHandler timerHandler;          /**< 任务句柄 */
    private StorageHandler storageHandler;      /**< Storage消息传递Handler */
    private AppListening appListening;          /**< 应用安装卸载监听对象 */
    private AppPermission appPermission;        /**< 应用信息实例 */
    private AppCpu appCpu;                      /**< 应用Cpu、Pid和内存信息实例 */
    private AppStorage appStorage;              /**< 应用存储信息实例 */
    private ListView lstApp;                    /**< 应用列表显示 */
    private ListView lstPermission;             /**< 应用权限显示 */
    private TextView txtCpu;                    /**< 应用Cpu、Pid信息显示 */
    private TextView txtMemory;                 /**< 应用内存信息显示 */
    private TextView txtStorage;                /**< 应用存储信息显示 */

    /**
     * @brief 应用信息结构，用于保存所有应用的基本信息。
     *
     */
    private class AppInfo {
        public String name;         /**< 应用名称 */
        public String pkgName;      /**< 应用包名称 */
        public String version;      /**< 应用版本 */
        public Drawable icon;       /**< 应用图标 */
        public boolean sysFlg;      /**< 是否是系统应用 */

        /**
         * @brief 生成String函数
         */
        @Override
        public String toString() {
            if (true == this.sysFlg) {
                return name + "[sys]\n" + pkgName + "\n" + version;
            } else {
                return name + "\n" + pkgName + "\n" + version;
            }
        }
    }

    /**
     * @brief APP应用列表适配器
     *
     * 自行实现ListView的Item显示内容。
     *
     */
    private class AppInfoAdapter extends BaseAdapter {
        /** 需要显示的应用信息列表 */
        private List<AppInfo> appInfoList;

        /**
         * @brief 构造函数
         * 适配器实例化时，需要一个应用信息列表实例，适配器本身不会产生应用信息列表数据。
         * @param list 应用信息列表
         */
        public AppInfoAdapter(List<AppInfo> list) {
            this.appInfoList = list;
        }

        /**
         * @brief 获取Item的数量
         */
        @Override
        public int getCount() {
            return this.appInfoList.size();
        }

        /**
         * @brief 获取Item对象
         *
         * @param pos Item的下标
         */
        @Override
        public Object getItem(int pos) {
            return this.appInfoList.get(pos);
        }

        /**
         * @brief 获取Item下标
         */
        @Override
        public long getItemId(int pos) {
            return pos;
        }

        /**
         * @brief 设置并返回一个Item视图
         *
         * @param pos Item所在位置
         * @param convertView Item的视图，若为Null，需要先初始化
         * @param viewParent Item的分组
         * @return 返回Item视图
         */
        @Override
        public View getView(int pos, @Nullable View convertView, ViewGroup viewParent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(AllApp.this).inflate(R.layout.listview_items, null);
            }
            // 获取Text和Icon控制，并显示相应内容
            String txt = this.appInfoList.get(pos).toString();
            Drawable img = this.appInfoList.get(pos).icon;
            ImageView imgView = (ImageView)convertView.findViewById(R.id.item_img);
            TextView  txtView = (TextView)convertView.findViewById(R.id.item_txt);
            imgView.setImageDrawable(img);
            txtView.setText(txt);
            return convertView;
        }
    }

    /**
     * @brief 静态Handler类
     *
     */
    private static class TimerHandler extends Handler {
        private final WeakReference<AllApp> waa;

        public TimerHandler(AllApp aa) {
            this.waa = new WeakReference<AllApp>(aa);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AllApp aa = this.waa.get();
            if (aa != null) {
                if (0 == msg.what) {
                    aa.updateAppInfoRunning(aa.getCurentPkgInfo());
                }
            }
        }
    }

    /**
     * @brief 静态Handler类，与Storage子线程传递消息
     *
     */
    public static class StorageHandler extends Handler {
        private final WeakReference<AllApp> waa;

        public StorageHandler(AllApp aa) {
            this.waa = new WeakReference<AllApp>(aa);
        }

        /**
         * @brief 处理Storage消息
         *
         * @param msg 来自AppStorage的消息
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AllApp aa = this.waa.get();
            if (aa != null) {
                if (0 == msg.what) {
                    Bundle bundle = msg.getData();
                    AppStorage.StorageInfo si = bundle.getParcelable("StorageInfo");
                    aa.txtStorage.setText(si.toString(aa));
                }
            }
        }
    }

    /**
     * @brief Activity onCreate函数
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allapp);

        this.initAppClass();
        this.initActivity();
        this.updateAppList();
    }

    /**
     * @brief Activity onStart函数
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        // 注册应用安装和卸载的监听
        this.appListening = new AppListening(this);
        IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PACKAGE_ADDED");
		filter.addAction("android.intent.action.PACKAGE_REMOVED");
		filter.addDataScheme("package");
		this.registerReceiver(this.appListening, filter);
    }

    /**
     * @brief Activity onDestroy函数
     *
     */
    @Override
    protected void onDestroy() {
        // 关闭应用安装和卸载的监听
        if (this.appListening != null) {
            this.unregisterReceiver(this.appListening);
        }
        super.onDestroy();
    }

    /**
     * @brief 应用数据类初始
     *
     */
    private void initAppClass() {
        this.appInfoList = new ArrayList<AppInfo>();

        this.appPermission = new AppPermission();
        this.appCpu = new AppCpu();
        this.appStorage = new AppStorage();

        this.timerRunning = false;
        this.timerHandler = new TimerHandler(this);
        this.storageHandler = new StorageHandler(this);
    }

    /**
     * @brief 组件初始化
     */
    public void initActivity() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_view);
        LinearLayout ll_info = (LinearLayout)ll.findViewById(R.id.ll_info);
        LinearLayout ll_app = (LinearLayout)ll.findViewById(R.id.ll_app);
        LinearLayout ll_permission = (LinearLayout)ll.findViewById(R.id.ll_permission);
        this.lstApp = (ListView)ll.findViewById(R.id.lst_app);
        this.lstPermission = (ListView)ll.findViewById(R.id.lst_permission);
        this.txtStorage = (TextView)ll_info.findViewById(R.id.txt_storage);
        this.txtCpu = (TextView)ll_info.findViewById(R.id.txt_cpu);
        this.txtMemory = (TextView)ll_info.findViewById(R.id.txt_memory);
        ((TextView)ll_app.findViewById(R.id.txt_app_head)).setText(Html.fromHtml("<b>App列表</b>"));
        ((TextView)ll_permission.findViewById(R.id.txt_permission_head)).setText(Html.fromHtml("<b>App权限</b>"));
        ((TextView)ll_info.findViewById(R.id.txt_cpu_head)).setText(Html.fromHtml("<b>App CPU</b>"));
        ((TextView)ll_info.findViewById(R.id.txt_memory_head)).setText(Html.fromHtml("<b>App内存</b>"));
        ((TextView)ll_info.findViewById(R.id.txt_storage_head)).setText(Html.fromHtml("<b>App存储</b>"));
        this.lstApp.setSelector(R.color.gray);
        this.lstPermission.setSelector(R.color.gray);

        this.lstApp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 设置应用列表的Selected回调
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                PackageManager pm = AllApp.this.getPackageManager();
                try {
                    PackageInfo pkgInfo = pm.getPackageInfo(
                            AllApp.this.appInfoList.get(pos).pkgName,
                            PackageManager.GET_ACTIVITIES);
                    AllApp.this.updateAppInfo(pkgInfo);
                    AllApp.this.startTimer();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            // 设置应用列表的NoSelected回调
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                AllApp.this.stopTimer();
            }
        });

        // 设置应用列表焦点控制回调
        this.lstApp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    AllApp.this.lstApp.setSelector(R.color.selected);
                }
                else {
                    AllApp.this.lstApp.setSelector(R.color.gray);
                }
            }
        });

        // 设置权限列表焦点控制回调
        this.lstPermission.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    AllApp.this.lstPermission.setSelector(R.color.selected);
                else
                    AllApp.this.lstPermission.setSelector(R.color.gray);
            }
        });
    }

    /**
     * @brief 开启定时任务
     * 每次timer.cancel()或者task.cancel()后，Timer和Task均消耗掉，需要再次New。
     */
    public void startTimer() {
        if (!this.timerRunning) {
            this.timer = new Timer();
            this.task = new TimerTask() {
                @Override
                // 定时任务
                public void run() {
                    Message msg = new Message();
                    msg.what = 0;
                    AllApp.this.timerHandler.sendMessage(msg);
                }
            };
            this.timer.schedule(this.task, this.TimerMs, this.TimerMs);
            this.timerRunning = true;
        }
    }

    /**
     * @brief 关闭定时器
     *
     */
    public void stopTimer() {
        if (this.timerRunning && this.timer != null)  {
            Log.i("AllApp", "Stop timer");
            this.timer.cancel();
            this.timer.purge();
            this.timerRunning = false;
        }
    }

    /**
     * @brief  获取所有应用列表信息
     *
     * 注意：获取应用列表前，会将原有的所有数据清空
     *
     * @param list 将获取的应用列表信息保存在list中
     */
    public void getApps(List<AppInfo> list) {
        list.clear();
        PackageManager pm = this.getPackageManager();
        List<PackageInfo> pkgList = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);

        List<AppInfo> tmp = new ArrayList<AppInfo>();
        for (int k = 0; k < pkgList.size(); k ++) {
            PackageInfo pkgInfo = pkgList.get(k);
            AppInfo appInfo = new AppInfo();
            appInfo.name = pkgInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.pkgName = pkgInfo.packageName;
            appInfo.version = pkgInfo.versionName;
            appInfo.icon = pkgInfo.applicationInfo.loadIcon(pm);
            if ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appInfo.sysFlg = false;
                list.add(appInfo);
            } else {
                appInfo.sysFlg = true;
                tmp.add(appInfo);
            }
        }
        // 系统应用放在后面
        for (int k = 0; k < tmp.size(); k ++) {
            list.add(tmp.get(k));
        }
    }

    /**
     * @brief 更新所有应用列表
     *
     */
    public void updateAppList() {
        this.getApps(this.appInfoList);
        AppInfoAdapter adapter = new AppInfoAdapter(this.appInfoList);
        this.lstApp.setAdapter(adapter);
    }

    /**
     * @brief 更新指定应用的所有信息
     *
     * @param pkgInfo 指定应用
     */
    public void updateAppInfo(PackageInfo pkgInfo) {
        this.appPermission.updatePermission(this, pkgInfo, this.lstPermission);
        this.appStorage.updateStorage(this, pkgInfo, this.storageHandler);
        this.updateAppInfoRunning(pkgInfo);
    }

    /**
     * @brief 更新指定运行应用的所有信息
     *
     * @param pkgInfo 指定应用
     */
    public void updateAppInfoRunning(PackageInfo pkgInfo) {
        this.appCpu.updateCpu(this, pkgInfo, this.txtCpu, this.txtMemory);
    }

    /**
     * @brief 获取当前光标所在Item的PackageInfo对象
     *
     * @return 返回当前光标所在Item的PackageInfo对象
     * @retval null 当前没有选择的Item
     */
    public PackageInfo getCurentPkgInfo() {
        PackageManager pm = AllApp.this.getPackageManager();
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(
                    this.appInfoList.get(this.lstApp.getSelectedItemPosition()).pkgName,
                    PackageManager.GET_ACTIVITIES);
            return pkgInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @brief 查看是否有Usage Stats权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasUsageStatsPermission(final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        final int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        boolean granted = false;
        if (mode == AppOpsManager.MODE_DEFAULT)
            granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        else
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        return granted;
    }
}

### 安装sphinx包

```
pip install sphinx
pip install sphinx_rtd_theme //主题风格
```
### 对指定项目生成文档
例如有一个项目VOD,其源码结构如图所示
|-- VOD
	|--src
	|	|--demo1.py
	|	|--demo2.py

##### 1.创建doc目录与src同级
1.1进入VOD目录，创建doc目录与src同级
|-- VOD
	|--doc
	|--src
	|	|--demo1.py
	|	|--demo2.py

1.2进入doc目录，shift+鼠标右键打开power shell窗口（也可以cmd到该目录）
依次执行如下命令，基本都默认yes,填写项目名，作者名，版本号【名称随便写，不影响后续生成】
```
PS D:\PycharmWorkPlace\VOD\doc> sphinx-quickstart
Welcome to the Sphinx 2.2.1 quickstart utility.

Please enter values for the following settings (just press Enter to
accept a default value, if one is given in brackets).

Selected root path: .

You have two options for placing the build directory for Sphinx output.
Either, you use a directory "_build" within the root path, or you separate
"source" and "build" directories within the root path.
> Separate source and build directories (y/n) [n]: y

The project name will occur in several places in the built documentation.
> Project name: VOD
> Author name(s): SQA
> Project release []: 1.0

If the documents are to be written in a language other than English,
you can select a language here by its language code. Sphinx will then
translate text that it generates into that language.

For a list of supported codes, see
https://www.sphinx-doc.org/en/master/usage/configuration.html#confval-language.
> Project language [en]: zh_CN

Creating file .\source\conf.py.
Creating file .\source\index.rst.
Creating file .\Makefile.
Creating file .\make.bat.

Finished: An initial directory structure has been created.

You should now populate your master file .\source\index.rst and create other documentation
source files. Use the Makefile to build the docs, like so:
   make builder
where "builder" is one of the supported builders, e.g. html, latex or linkcheck.
```
操作完毕后会生成几个文件，此时目录结构变成：
|-- VOD
	|--doc
	|	|--build
	|	|--source
	|	|	|--_static
	|	|	|--_templates
	|	|	|--conf.py
	|	|	|--index.rst
	|	|--make.bat
	|	|--Makefile
	|--src
	|	|--demo1.py
	|	|--demo2.py

##### 2.修改配置文件
打开conf.py修改配置文件
2.1.修改源码路径
```
import os
import sys
sys.path.insert(0, os.path.abspath('../../src'))  这个是项目源码路径
```
2.2.修改模板参数
```
html_theme = 'alabaster' 	
修改为
html_theme = 'sphinx_rtd_theme'
```
2.3.增加扩展
```
extensions = ['sphinx.ext.autodoc', 
    'sphinx.ext.doctest',
    'sphinx.ext.intersphinx',
    'sphinx.ext.todo',
    'sphinx.ext.coverage',
    'sphinx.ext.mathjax']
    
'sphinx.ext.autodoc' //这个autodoc很重要，是自动抽取注释转为文档的关键
```
##### 3.为源码文件生成rst文件
输入如下命令：
	这里有一个非常需要注意的地方，-o表示输出，后面有两个参数，第一个参数是生成的rst文件存放位置，第2个参数是源码文件存放的位置。生成的rst文件存放位置必须与index.rst所在位置相同，不然容易出现生成的文档没有内容这个情况。源码文件存放的位置应该与2.1中设置的路径一致
```
PS D:\PycharmWorkPlace\VDO\doc> sphinx-apidoc -o source ../src/

若是文档需要加载源码文件中新的注释内容，需要重新生成rst文件，可执行如下命令进行覆盖
PS D:\PycharmWorkPlace\VDO\doc> sphinx-apidoc -o source ../src/ -f
```
生成rst后的目录结构
|-- VOD
	|--doc
	|	|--build
	|	|--source
	|	|	|--_static
	|	|	|--_templates
	|	|	|--conf.py
	|	|	|--index.rst
	|	|	|--modules.rst
	|	|	|--demo1.rst
	|	|	|--demo2.rst
	|	|--make.bat
	|	|--Makefile
	|--src
	|	|--demo1.py
	|	|--demo2.py

##### 4.修改index.rst文件
```
Welcome to VV's documentation!  //按需要修改成想展示的文本
==============================

.. toctree::
   :maxdepth: 2
   :caption: Contents:

   modules			
//把modules.rst文件加入，不需要后缀，注意前面空一行，不然会报错，modules中包含所有的rst文件。


Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`

```
##### 5.生成html文件

```
PS D:\PycharmWorkPlace\VOD\doc> ./make html

cmd下的话，直接输入make html
```
在build/下生成html文件夹
##### 6.生成pdf文件
环境要求安装了latex编译器
```
PS D:\PycharmWorkPlace\VOD\doc> ./make latex
```
在build/下生成latex文件夹,直接编译.tex文件为PDF格式即可
注意事项：编译latex时会出现
一些报错或警告说文件中要编译的module不存在，这要求你的程序引入的包必须要有这些包存在。

##### 7.Python文件的注释要求
目前采用reST风格
reST的全称是reStructredText。通过以冒号开头的几个关键字来说明类、函数中的参数、返回值、异常等。注意title要与参数空一行，不然html上会没有换行
    """
    获取指定路径下按采集顺序排列的所有图片名称

    :param img_dir_path: 给定的图片存储目录
    :return: 该目录下所有图片名称列表
    """
    
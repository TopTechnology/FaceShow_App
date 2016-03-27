---
title: 2016-3-27如何将so文件导入到android studio的项目中
tags: 新建,模板,小书匠
grammar_cjkRuby: true
---

首先我们得了解什么是so文件

****so文件是unix的动态连接库，是二进制文件，作用相当于windows下的.dll文件。
补充：
在Android中调用动态库文件(*.so)都是通过jni的方式。
Android中加载so文件的提供的API：
void System.load(String pathName);****


那么我们该怎么做呢？

步骤很简单。

- 第一步：

需要在项目的目下下的 app/src/main目录下创建一个名叫jniLibs文件夹。将对应的so文件拷贝到该目录下。拷贝后的结构如下图所示：


![enter description here][1]


  [1]: http://img.blog.csdn.net/20141222130003060
  
  
  
- 第二步：
点击一下上方的build选项，make project一下就可以正常使用了。
然后重新编译一下就可以正确使用第三方so库了。
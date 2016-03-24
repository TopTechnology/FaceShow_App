---
title: Android Camera
---
### 在APP中使用camera的两种方式
1. 调用系统相机，或具有相机功能的应用。

            /**
             * 启动相机应用
             * 函数名为按钮的点击事件名startCamera
             */
            public void startCamera(View view){
                //ACTION_IMAGE_CAPTURE捕捉图形功能
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }

+ 获取相机拍照的图片需要调用startActivityForResult方法，在重写onActivityResult方法。

        @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode==RESULT_OK){
                    if (requestCode==REQUSET_CODE){
                        /**
                         * 通过Bundle取出的图片只是一张缩略图，为了放在数据溢出                           
                         */
                        Bundle bundle=data.getExtras();
                        Bitmap bitmap= (Bitmap) bundle.get("data");
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }

+ 要想获取图片的原始质量，需要把在onCreate方法中自定义一个存储路径

        /**
         * 获取系统存储路径
         */
        mFilePath= Environment.getExternalStorageDirectory().getPath();
        mFilePath=mFilePath+"/"+"temp.png";
+ 在按钮的点击事件的方法中创建一个URI对象，再把它指向我们创建的文件路径对象，最后把系统默认的图片存储路径更改到我们自己创建的路径。

        //ACTION_IMAGE_CAPTURE捕捉图形功能
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //URI指向我们创建的文件对象
        Uri photeUri=Uri.fromFile(new File(mFilePath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photeUri);
        startActivityForResult(intent, REQUSET_2);
+ 在onActivityResult方法中创建一个FileInputStream对象，把存储路径封装到该对象中，再把输入流封装到Bitmap中。

        FileInputStream fileInputStream=null;
                try {
                    fileInputStream=new FileInputStream(mFilePath);
                    Bitmap bitmap= BitmapFactory.decodeStream(fileInputStream);
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
+ **这样就可以获取到图片的原始质量了，但在我的手机上运行的时候，ImageView并没有显示出该图片。android studio打印的信息是：Bitmap too large to be uploaded into a texture。这并不是一个异常或者错误，好像意思是图片过大。查阅资料后发现android默认启动硬件加速，而硬件加速中OpenGL对于内存是有限制的。解决的办法之一就是在AndroidManifest中的activity里取消硬件加速。**

        <activity
            android:name=".MainActivity"
            android:hardwareAccelerated="false">

2. 自定义相机。
+ 在AndroidManifest中注册应用具有相近功能。

        <intent-filter>
                        <action android:name="android.media.action.IMAGE_CAPTURE"/>
                        <category android:name="android.intent.category.DEFAULT"/>
        </intent-filter>


#### **未完待续**
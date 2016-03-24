package com.example.vampire.facelearning;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int REQUSET_1=1;
    private static final int REQUSET_2=2;
    private ImageView imageView;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView= (ImageView) findViewById(R.id.ivGetImage);
        /**
         * 获取系统存储路径
         */
        mFilePath= Environment.getExternalStorageDirectory().getPath();
        mFilePath=mFilePath+"/"+"temp.png";
    }

    /**
     * 启动相机应用
     * 函数名为按钮的点击事件名startCamera
     */
    public void startCamera(View view){
        //ACTION_IMAGE_CAPTURE捕捉图形功能
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUSET_1);
    }
    public void startCamera1(View view){
        //ACTION_IMAGE_CAPTURE捕捉图形功能
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //URI指向我们创建的文件对象
        Uri photeUri=Uri.fromFile(new File(mFilePath));
        //
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photeUri);

        startActivityForResult(intent, REQUSET_2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==REQUSET_1){
                /**
                 * 通过Bundle取出的图片只是一张缩略图，是为了放在数据溢出
                 */
                Bundle bundle=data.getExtras();
                Bitmap bitmap= (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bitmap);
            }else if (requestCode==REQUSET_2){
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
            }
        }
    }
}

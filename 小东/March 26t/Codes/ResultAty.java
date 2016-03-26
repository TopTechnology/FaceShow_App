package com.example.vampire.facelearning;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Vampire on 2016/3/26.
 */
public class ResultAty extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        String path=getIntent().getStringExtra("picPath");
        ImageView imageView= (ImageView) findViewById(R.id.pic);

        //对图像的显示角度进行调整
        try {
            FileInputStream fis=new FileInputStream(path);
            //把图像转换成字节流
            Bitmap bitmap=BitmapFactory.decodeStream(fis);
            Matrix matrix=new Matrix();
            matrix.setRotate(90);
            //调整角度
            bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        Bitmap bitmap= BitmapFactory.decodeFile(path);
//        imageView.setImageBitmap(bitmap);
    }
}

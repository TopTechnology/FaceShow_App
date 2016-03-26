package com.example.vampire.facelearning;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Vampire on 2016/3/25.
 */
public class CustomCamera extends Activity implements SurfaceHolder.Callback{
    private Camera mCamera;
    private SurfaceView mPreView;
    private SurfaceHolder mHolder;
    private Camera.PictureCallback mPictureCallback=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //将数据写入SD卡的temp.png文件当中
            File tempFile=new File("/sdcard/temp.png");
            try {
                FileOutputStream fileOutputStream=new FileOutputStream(tempFile);
                fileOutputStream.close();
                fileOutputStream.close();

                Intent intent=new Intent(CustomCamera.this,ResultAty.class);
                intent.putExtra("picPath",tempFile.getAbsolutePath());
                startActivity(intent);
                CustomCamera.this.finish();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom);
        mPreView= (SurfaceView) findViewById(R.id.priview);
        mHolder=mPreView.getHolder();
        mHolder.addCallback(this);
        //设置SurfaceView点击事件，自动对焦
        mPreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });
    }

    public void capture(View view){
        Camera.Parameters parameters=mCamera.getParameters();
        //设置拍照格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        //设置拍照大小
        parameters.setPictureSize(800,400);
        //设置自动对焦
        parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                mCamera.takePicture(null,null,mPictureCallback);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera==null){
            getCamera();
            if (mHolder!=null){
                setStartPreview(mCamera,mHolder);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * 获取Camera对象
     * @return
     */
    private Camera getCamera(){
        Camera camera;
        try{
             camera=Camera.open();
        }catch (Exception e){
            camera=null;
            e.getStackTrace();
        }
       return camera;
    }

    /**
     * 开始预览相机
     */
    private void setStartPreview(Camera camera,SurfaceHolder holder)  {
        //将Camera与surfaceView进行绑定
            try {
                    camera.setPreviewDisplay(holder);
                    //将camera竖屏显示
                    camera.setDisplayOrientation(90);
                    camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.getStackTrace();
            }

    }

    /**
     * 释放相机资源
     */
    private void releaseCamera(){
        if (mCamera!=null){
            //将相机回调置空
            mCamera.setPreviewCallback(null);
            //取消相机的取景功能
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(mCamera,mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            mCamera.stopPreview();
        }catch (NullPointerException e){
            e.getStackTrace();
        }
        setStartPreview(mCamera,mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }
}

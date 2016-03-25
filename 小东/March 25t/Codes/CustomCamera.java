package com.example.vampire.facelearning;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

/**
 * Created by Vampire on 2016/3/25.
 */
public class CustomCamera extends Activity implements SurfaceHolder.Callback{
    private Camera mCamera;
    private SurfaceView mPreView;
    private SurfaceHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom);
        mPreView= (SurfaceView) findViewById(R.id.priview);
        mHolder=mPreView.getHolder();
        mHolder.addCallback(this);
    }

    public void capture(View view){

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
        mCamera.stopPreview();
        setStartPreview(mCamera,mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }
}

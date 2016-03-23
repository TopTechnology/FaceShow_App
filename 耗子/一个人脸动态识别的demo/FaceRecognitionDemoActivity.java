package com.demo.face;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class FaceRecognitionDemoActivity extends Activity implements
		OnClickListener {

	private SurfaceView preview;
	private Camera camera;
	private Camera.Parameters parameters;
	private int orientionOfCamera;// ǰ������ͷ�İ�װ�Ƕ�
	private int faceNumber;// ʶ���������
	private FaceDetector.Face[] faces;
	private FindFaceView mFindFaceView;
	private ImageView iv_photo;
	private Button bt_camera;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onStart() {
		super.onStart();
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		bt_camera = (Button) findViewById(R.id.bt_camera);
		bt_camera.setOnClickListener(this);

		mFindFaceView = (FindFaceView) findViewById(R.id.my_preview);

		preview = (SurfaceView) findViewById(R.id.preview);
		// ���û�������
		preview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// ����surface�ķֱ���
		preview.getHolder().setFixedSize(176, 144);
		// ������Ļ����
		preview.getHolder().setKeepScreenOn(true);
		preview.getHolder().addCallback(new SurfaceCallback());
	}

	private final class MyPictureCallback implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				Matrix matrix = new Matrix();
				matrix.setRotate(-90);
				Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap
						.getWidth(), bitmap.getHeight(), matrix, true);
				bitmap.recycle();
				iv_photo.setImageBitmap(bmp);
				camera.startPreview();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private final class SurfaceCallback implements Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (camera != null) {
				parameters = camera.getParameters();
				parameters.setPictureFormat(PixelFormat.JPEG);
				// ����Ԥ������Ĵ�С
				parameters.setPreviewSize(width, height);
				// ����ÿ����Ԥ��֡��
				parameters.setPreviewFrameRate(5);
				// ����Ԥ��ͼƬ�Ĵ�С
				parameters.setPictureSize(width, height);
				parameters.setJpegQuality(80);
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			int cameraCount = 0;
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			cameraCount = Camera.getNumberOfCameras();
			for (int i = 0; i < cameraCount; i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					try {
						camera = Camera.open(i);
						camera.setPreviewDisplay(holder);
						setCameraDisplayOrientation(i, camera);
						camera.setPreviewCallback(new MyPreviewCallback());
						camera.startPreview();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				camera.setPreviewCallback(null);
				camera.stopPreview();
				camera.release();
				camera = null;
			}
		}

	}

	private class MyPreviewCallback implements PreviewCallback {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Size size = camera.getParameters().getPreviewSize();
			YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21,
					size.width, size.height, null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height),
					80, baos);
			byte[] byteArray = baos.toByteArray();
			detectionFaces(byteArray);
		}

	}

	/**
	 * �������
	 * 
	 * @param data
	 *            Ԥ����ͼ������
	 */
	private void detectionFaces(byte[] data) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length,
				options);
		int width = bitmap1.getWidth();
		int height = bitmap1.getHeight();
		Matrix matrix = new Matrix();
		Bitmap bitmap2 = null;
		FaceDetector detector = null;
		switch (orientionOfCamera) {
		case 0:
			detector = new FaceDetector(width, height, 1);
			matrix.postRotate(0.0f, width / 2, height / 2);
			// ��ָ���Ŀ�Ⱥ͸߶ȴ���һ�ſɱ��bitmap��ͼƬ��ʽ������RGB_565����Ȼ��ⲻ��������
			bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
			break;
		case 90:
			detector = new FaceDetector(height, width, 1);
			matrix.postRotate(-270.0f, height / 2, width / 2);
			bitmap2 = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565);
			break;
		case 180:
			detector = new FaceDetector(width, height, 1);
			matrix.postRotate(-180.0f, width / 2, height / 2);
			bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
			break;
		case 270:
			detector = new FaceDetector(height, width, 1);
			matrix.postRotate(-90.0f, height / 2, width / 2);
			bitmap2 = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565);
			break;
		}

		faces = new FaceDetector.Face[1];
		Paint paint = new Paint();
		paint.setDither(true);
		Canvas canvas = new Canvas();
		canvas.setBitmap(bitmap2);
		canvas.setMatrix(matrix);
		// ��bitmap1����bitmap2�ϣ������ƫ�Ʋ�������ʵ���������Ҫ�޸ģ�
		canvas.drawBitmap(bitmap1, -40.0F, 40.0F, paint);
		faceNumber = detector.findFaces(bitmap2, faces);
		Log.e("��⵽������", faceNumber + "");
		if (faceNumber != 0) {
			mFindFaceView.setVisibility(View.VISIBLE);
			mFindFaceView.drawRect(faces, faceNumber);
		} else {
			mFindFaceView.setVisibility(View.GONE);
		}
		bitmap2.recycle();
		bitmap1.recycle();
	}

	/**
	 * �����������ʾ�������������ô���ã���Ȼ��ⲻ��������
	 * 
	 * @param cameraId
	 *            ���ID(0�Ǻ�������ͷ��1��ǰ������ͷ��
	 * @param camera
	 *            �������
	 */
	private void setCameraDisplayOrientation(int cameraId, Camera camera) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		int degree = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degree = 0;
			break;
		case Surface.ROTATION_90:
			degree = 90;
			break;
		case Surface.ROTATION_180:
			degree = 180;
			break;
		case Surface.ROTATION_270:
			degree = 270;
			break;
		}

		orientionOfCamera = info.orientation;
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degree) % 360;
			result = (360 - result) % 360;
		} else {
			result = (info.orientation - degree + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_camera:
			if (camera != null) {
				try {
					camera.takePicture(null, null, new MyPictureCallback());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}
}
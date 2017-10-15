package com.szOCR.camera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.carOCR.RecogResult;
import com.carOCR.activity.ScanActivity;
import com.szOCR.general.CGlobal;
import com.szOCR.general.Defines;
import com.szOCR.general.ImageProcessing;
import com.szOCR.general.VideoEncoderFromBuffer;
import com.xiaomo.chcarappnew.R;

/**
 * This class assumes the parent layout is RelativeLayout.LayoutParams.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback, Camera.PreviewCallback
{
    private static boolean DEBUGGING = false;
    private static final String LOG_TAG = "CameraPreviewSample";
    private static final String CAMERA_PARAM_ORIENTATION = "orientation";
    private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
    private static final String CAMERA_PARAM_PORTRAIT = "portrait";
    //protected Activity mActivity;
    protected ScanActivity mActivity;
    
    private SurfaceHolder mHolder;
    public Camera mCamera;
    protected List<Camera.Size> mPreviewSizeList;
    protected List<Camera.Size> mPictureSizeList;
    protected Camera.Size mPreviewSize;
    protected Camera.Size mPictureSize;
    private int mSurfaceChangedCallDepth = 0;
    private int mCameraId;
    private LayoutMode mLayoutMode;
    private int mCenterPosX = -1;
    private int mCenterPosY;
    PreviewReadyCallback mPreviewReadyCallback = null;
    
    public boolean bIsFocusSuccessed = false;
    public boolean bIsRecognizing = false;
    Thread 			thread;
    
    public int nLayoutWidth = 0;
    public int nLayoutHeight = 0;

    private boolean mbHasSurface;
    
    private Handler previewHandler;
    private Handler autofocusHandler;
    private int previewMessage;
    private int autofocusMessage;
    private static final long AUTOFOCUS_INTERVAL_MS = 1500L;
    
    public boolean bIsRecoging			= true;
    public boolean bIsCameraReleased 	= false;
    public boolean bInitialized 		= false;
    public boolean bIsStateAutoFocusing = false;
    
    public static enum LayoutMode
    {
        FitToParent, // Scale to the size that no side is larger than the parent
        NoBlank // Scale to the size that no side is smaller than the parent
    };
    
    public interface PreviewReadyCallback {
        public void onPreviewReady();
    }
 
    /**
     * State flag: true when surface's layout size is set and surfaceChanged()
     * process has not been completed.
     */
    protected boolean mSurfaceConfiguring = false;

    public CameraPreview(Activity activity, int cameraId, LayoutMode mode) 
    {
        super(activity); // Always necessary
        mActivity = (ScanActivity)activity;
        mLayoutMode = mode;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (Camera.getNumberOfCameras() > cameraId) {
                mCameraId = cameraId;
            } else {
                mCameraId = 0;
            }
        } else {
            mCameraId = 0;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(mCameraId);
        } else {
            mCamera = Camera.open();
        }
        Camera.Parameters cameraParams = mCamera.getParameters();
        mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
        mPictureSizeList = cameraParams.getSupportedPictureSizes();
        mbHasSurface = false;
        bIsCameraReleased = true;
    }
    
   public int  setNextCamera(){
	   Log.e("-xiaomo-", "setNextCamera:Camera.getNumberOfCameras()="+Camera.getNumberOfCameras()+"|mCameraId="+mCameraId);
	   mCameraId = (Camera.getNumberOfCameras() - mCameraId - 1) % Camera.getNumberOfCameras();
	   mCamera.stopPreview();
	   cancelAutoFocus();
	   mCamera.release();
	   mCamera = null;
	   mCamera = Camera.open(mCameraId);
//	   mCamera = Camera.open(mCameraId);
       Camera.Parameters cameraParams = mCamera.getParameters();
       mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
       mPictureSizeList = cameraParams.getSupportedPictureSizes();
//       mbHasSurface = false;
//       bIsCameraReleased = true;
       mCamera.startPreview();
       return mCameraId;
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mHolder);
            if (mbHasSurface == false) {
    			mbHasSurface = true;
    		}

        } catch (IOException e) {
        	mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
    	mSurfaceChangedCallDepth++;
        doSurfaceChanged(width, height);
        mSurfaceChangedCallDepth--;
        mActivity.doInitZoom();
    }

    private void doSurfaceChanged(int width, int height) {
        mCamera.stopPreview();
        
        Camera.Parameters cameraParams = mCamera.getParameters();
        
        submitFocusAreaRect(cameraParams);
        
        boolean portrait = isPortrait();

        // The code in this if-statement is prevented from executed again when surfaceChanged is
        // called again due to the change of the layout size in this if-statement.
        if (!mSurfaceConfiguring) {
            Camera.Size previewSize = determinePreviewSize(portrait, width, height);
            Camera.Size pictureSize = determinePictureSize(previewSize);
            if (DEBUGGING) { Log.v(LOG_TAG, "Desired Preview Size - w: " + width + ", h: " + height); }
            mPreviewSize = previewSize;
            mPictureSize = pictureSize;
            
            //Toast.makeText(mActivity, "Change Change", Toast.LENGTH_LONG).show();
            
            mSurfaceConfiguring = adjustSurfaceLayoutSize(previewSize, portrait, width, height);
            
            Rect cropRect = getScreenCropRect(nLayoutWidth,nLayoutHeight);
            double wrate =  (double)mPreviewSize.width/nLayoutHeight;
            if(portrait==false) wrate =  (double)mPreviewSize.width/nLayoutWidth;
            int leftOff = (width-nLayoutWidth)/2;
            int topOff = (height-nLayoutHeight)/2;
            
            CGlobal.g_scrCropRect = new Rect(cropRect);//new Rect(cropRect.left+leftOff,cropRect.top+topOff,cropRect.right+leftOff,cropRect.bottom+topOff);
            Rect r = new Rect(cropRect);
        	int left = (int)(wrate * r.left);
        	int top = (int)(wrate * r.top);
        	int right = (int)(wrate * r.right);
        	int bottom = (int)(wrate * r.bottom);
        	
            CGlobal.g_imgCropRect = new Rect(left, top, right, bottom);
            
            mActivity.setAndshowPreviewSize();
            
            // Continue executing this method if this method is called recursively.
            // Recursive call of surfaceChanged is very special case, which is a path from
            // the catch clause at the end of this method.
            // The later part of this method should be executed as well in the recursive
            // invocation of this method, because the layout change made in this recursive
            // call will not trigger another invocation of this method.
            if (mSurfaceConfiguring && (mSurfaceChangedCallDepth <= 1)) {
            	mCamera.startPreview();
                setPreviewCallback(this);
            	//mCamera.setOneShotPreviewCallback(this);
                return;
            }
        }

        configureCameraParameters(cameraParams, portrait);
        mSurfaceConfiguring = false;

        try {
            mCamera.startPreview();
            setPreviewCallback(this);
            //mCamera.setOneShotPreviewCallback(this);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Failed to start preview: " + e.getMessage());

            // Remove failed size
            mPreviewSizeList.remove(mPreviewSize);
            mPreviewSize = null;

            // Reconfigure
            if (mPreviewSizeList.size() > 0) { // prevent infinite loop
                surfaceChanged(null, 0, width, height);
            } else {
                Toast.makeText(mActivity, "Can't start preview", Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG, "Gave up starting preview");
            }
        }
        
        if (null != mPreviewReadyCallback) {
            mPreviewReadyCallback.onPreviewReady();
        }
    }
    private Rect getScreenCropRect(int scrWidth,int scrHeight)
    {
		int x0,y0,x,y,wid,hei;
		boolean portrait = isPortrait();
	
		float width = scrWidth;
		float height = scrHeight;
		x0 = (int)(width/2);	        	
		y0 = (int)(height/2);
		
		if(portrait == true){
	    	if(mActivity.m_infor_prevent == 0)
	    	{//digit
	        	wid = (int)(width*2/3);     hei = (int)(width/6);
	    	}
	    	else
	    	{
	        	wid = (int)(width/4);      	hei = (int)(width/4);
	    	}
		}
		else{
			if(mActivity.m_infor_prevent == 0)
	    	{//digit
	        	wid = (int)(height*2/3);    hei = (int)(height/6);
	    	}
	    	else
	    	{
	        	wid = (int)(height/4);      hei = (int)(height/4);
	    	}
		}
		x = x0-wid/2;
		y = y0-hei/2;
		
	    Rect cropRect = new Rect(x, y, x+wid, y+hei);
	    return cropRect;
    }
	    
	private void submitFocusAreaRect(Camera.Parameters cameraParameters)
	{
	    if (cameraParameters.getMaxNumFocusAreas() == 0)
	    {
	        return;
	    }

	    // Convert from View's width and height to +/- 1000

	    Rect focusArea = CGlobal.g_scrCropRect;

	    // Submit focus area to camera

	    ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
	    if(focusAreas.size()>0){
	    	focusAreas.add(new Camera.Area(focusArea, 1000));
	    	cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	    	cameraParameters.setFocusAreas(focusAreas);
	    	mCamera.setParameters(cameraParameters);
	    }

	    // Start the autofocus operation
	}
    /**
     * @param cameraParams
     * @param portrait
     * @param reqWidth must be the value of the parameter passed in surfaceChanged
     * @param reqHeight must be the value of the parameter passed in surfaceChanged
     * @return Camera.Size object that is an element of the list returned from Camera.Parameters.getSupportedPreviewSizes.
     */

    protected Camera.Size determinePreviewSize(boolean portrait, int reqWidth, int reqHeight) 
    {
        // Meaning of width and height is switched for preview when portrait,
        // while it is the same as user's view for surface and metrics.
        // That is, width must always be larger than height for setPreviewSize.
        int reqPreviewWidth; // requested width in terms of camera hardware
        int reqPreviewHeight; // requested height in terms of camera hardware
        if (portrait) {
            reqPreviewWidth = reqHeight;
            reqPreviewHeight = reqWidth;
        } else {
            reqPreviewWidth = reqWidth;
            reqPreviewHeight = reqHeight;
        }

        if (DEBUGGING) {
            Log.v(LOG_TAG, "Listing all supported preview sizes");
            for (Camera.Size size : mPreviewSizeList) {
                Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
            }
            Log.v(LOG_TAG, "Listing all supported picture sizes");
            for (Camera.Size size : mPictureSizeList) {
                Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
            }
        }

        // Adjust surface size with the closest aspect-ratio
       double reqRatio =((double) 1280);//800);//640);//reqPreviewWidth);// ((double) reqPreviewWidth) / reqPreviewHeight;
       double curRatio, deltaRatio;
        double deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : mPreviewSizeList) 
        {
            //curRatio = ((double) size.width) / size.height;
            //deltaRatio = Math.abs(reqRatio - curRatio);
        	curRatio = ((double) size.width);
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) 
            {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
//        float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
//        float curRatio, deltaRatio;
//        float deltaRatioMin = Float.MAX_VALUE;
//        Camera.Size retSize = null;
//        for (Camera.Size size : mPreviewSizeList) {
//            curRatio = ((float) size.width) / size.height;
//            deltaRatio = Math.abs(reqRatio - curRatio);
//            if (deltaRatio < deltaRatioMin) {
//                deltaRatioMin = deltaRatio;
//                retSize = size;
//            }
//        }

        return retSize;
    }

    protected Camera.Size determinePictureSize(Camera.Size previewSize)
    {
        Camera.Size retSize = null;
        for (Camera.Size size : mPictureSizeList)
        {
            if (size.equals(previewSize)) 
            {
                return size;
            }
        }
        
        if (DEBUGGING) { Log.v(LOG_TAG, "Same picture size not found."); }
        
        // if the preview size is not supported as a picture size
        float reqRatio = ((float) previewSize.width) / previewSize.height;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        for (Camera.Size size : mPictureSizeList) 
        {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin)
            {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        
        return retSize;
    }
    
    protected boolean adjustSurfaceLayoutSize(Camera.Size previewSize, boolean portrait,
            int availableWidth, int availableHeight) {
        float tmpLayoutHeight, tmpLayoutWidth;
        if (portrait) {
            tmpLayoutHeight = previewSize.width;
            tmpLayoutWidth = previewSize.height;
        } else {
            tmpLayoutHeight = previewSize.height;
            tmpLayoutWidth = previewSize.width;
        }

        float factH, factW, fact;
        factH = (float)availableHeight / tmpLayoutHeight;
        factW = (float)availableWidth / tmpLayoutWidth;
        if (mLayoutMode == LayoutMode.FitToParent) {
            // Select smaller factor, because the surface cannot be set to the size larger than display metrics.
            if (factH < factW) {
                fact = factH;
            } else {
                fact = factW;
            }
        } else {
            if (factH < factW) {
                fact = factW;
            } else {
                fact = factH;
            }
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)this.getLayoutParams();

        int layoutHeight = (int) (tmpLayoutHeight * fact);
        int layoutWidth = (int) (tmpLayoutWidth * fact);
        if (DEBUGGING) {
            Log.v(LOG_TAG, "Preview Layout Size - w: " + layoutWidth + ", h: " + layoutHeight);
            Log.v(LOG_TAG, "Scale factor: " + fact);
        }

        boolean layoutChanged;
        if ((layoutWidth != this.getWidth()) || (layoutHeight != this.getHeight())) {
            layoutParams.height = layoutHeight;
            layoutParams.width = layoutWidth;
            if (mCenterPosX >= 0) {
                layoutParams.topMargin = mCenterPosY - (layoutHeight / 2);
                layoutParams.leftMargin = mCenterPosX - (layoutWidth / 2);
            }
            this.setLayoutParams(layoutParams); // this will trigger another surfaceChanged invocation.
            mActivity.mViewfinderView.setLayoutParams(layoutParams);
            layoutChanged = true;
        } else {
            layoutChanged = false;
        }

        nLayoutWidth = layoutWidth;
        nLayoutHeight = layoutHeight;
        
        return layoutChanged;
    }

    /**
     * @param x X coordinate of center position on the screen. Set to negative value to unset.
     * @param y Y coordinate of center position on the screen.
     */
    public void setCenterPosition(int x, int y) 
    {
        mCenterPosX = x;
        mCenterPosY = y;
    }
    
    protected void configureCameraParameters(Camera.Parameters cameraParams, boolean portrait) 
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO)
        { // for 2.1 and before
            if (portrait) 
            {
                cameraParams.set(CAMERA_PARAM_ORIENTATION, CAMERA_PARAM_PORTRAIT);
            }
            else
            {
                cameraParams.set(CAMERA_PARAM_ORIENTATION, CAMERA_PARAM_LANDSCAPE);
            }
        }
        else
        { // for 2.2 and later
        	android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
    		android.hardware.Camera.getCameraInfo(0, info);
    		
        	int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
    		int degrees = 0;
    		switch (rotation)
    		{
    		case Surface.ROTATION_0:
    			degrees = 0;
    			break;
    		case Surface.ROTATION_90:
    			degrees = 90;
    			break;
    		case Surface.ROTATION_180:
    			degrees = 180;
    			break;
    		case Surface.ROTATION_270:
    			degrees = 270;
    			break;
    		}
    		int resultAngle;
    		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) 
    		{
    			resultAngle = (info.orientation + degrees) % 360;
    			resultAngle = (360 - resultAngle) % 360; // compensate the mirror
    		} 
    		else
    		{ // back-facing
    			resultAngle = (info.orientation - degrees + 360) % 360;
    		}
    		mCamera.setDisplayOrientation(resultAngle);
    		CGlobal.g_iFrameRotation = resultAngle;
            Log.v(LOG_TAG, "angle: " + resultAngle);

        }

        cameraParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        cameraParams.setPictureSize(mPictureSize.width, mPictureSize.height);
        if (DEBUGGING) {
            Log.v(LOG_TAG, "Preview Actual Size - w: " + mPreviewSize.width + ", h: " + mPreviewSize.height);
            Log.v(LOG_TAG, "Picture Actual Size - w: " + mPictureSize.width + ", h: " + mPictureSize.height);
        }

      cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
      
      int maxZoom = cameraParams.getMaxZoom();
      int curZoom = cameraParams.getZoom();
      
      if (cameraParams.isZoomSupported()) 
      {
      	int zoom = (maxZoom*5)/10;
      	if(zoom<maxZoom && zoom>0)
      	{
      		cameraParams.setZoom(zoom);
      	}
      }
      cameraParams.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
      mCamera.setParameters(cameraParams);
        
    }
    public void setCameraZoomIndex(int iZoomValue)
    {
    	Camera.Parameters cameraParams = mCamera.getParameters();
    	if (cameraParams.isZoomSupported()) 
    	{
    		cameraParams.setZoom(iZoomValue);
    		mCamera.setParameters(cameraParams);
		}
	}
    public void stop()
    {
        if (null == mCamera)
        {
            return;
        }
        stopPreview();
        mCamera.release();
        mCamera = null;
        mbHasSurface = false;
    }
    
    public void stopCamera()
    {
    	if (null == mCamera) 
    	{
            return;
        }
        stopPreview();
     }

    public boolean isPortrait() 
    {
        return (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }
    
    public void setPreviewCallback(PreviewCallback callback)
    {        
        if(CGlobal.g_runMode == Defines.RUMMODE_RECORD)
        {
        	mCamera.setPreviewCallback(callback);
        }
        else
        {
            mCamera.setOneShotPreviewCallback(callback);
        }
    }
    
    public Camera.Size getPreviewSize() {
        return mPreviewSize;
    }
    
        
    public void setOnPreviewReady(PreviewReadyCallback cb) {
        mPreviewReadyCallback = cb;
    }

	public void stopPreview() 
	{
		if (mCamera != null) 
		{
			mCamera.setPreviewCallback(null);
			mCamera.setOneShotPreviewCallback(null);
			mCamera.stopPreview();
			setPreviewHandler(null, 0);
			setAutofocusHandler(null, 0);
			//previewing = false;
		}
		
	}
	public void autoCameraFocuse()
	{
		if (null == mCamera) {
            return;
        }
		if(mActivity.m_bAutoFocus == false) return;
		CGlobal.g_bAutoFocused = false;
		bIsStateAutoFocusing = true;
		mCamera.autoFocus(this);
		//imgAim.setImageResource(R.drawable.target_red);
	}
	public void forceCameraFocuse()
	{
		if (null == mCamera) {
            return;
        }
		CGlobal.g_bAutoFocused = false;
		bIsStateAutoFocusing = true;
		mCamera.autoFocus(this);
		//imgAim.setImageResource(R.drawable.target_red);
	}
	@Override
	public void onAutoFocus(boolean success, Camera camera) 
	{
		// TODO Auto-generated method stub
		if (null == mCamera) {
            return;
        }
		bIsStateAutoFocusing = false;
		// TODO Auto-generated method stub
		if (success == true)
		{
			CGlobal.g_bAutoFocused = true;
			//imgAim.setImageResource(R.drawable.target);
			//restartPreviewFrame();
		}
		else
		{
			//autoCameraFocuse();
		}
	}
	
	
	
	@Override
	public void onPreviewFrame(final byte[] data, Camera arg1) 
	{
		final int width = mCamera.getParameters().getPreviewSize().width;
		final int height = mCamera.getParameters().getPreviewSize().height;
		if(mActivity.m_bRecorderStarted == true && CGlobal.g_runMode == Defines.RUMMODE_RECORD)
		{
			if(m_Encoder != null)
			{
				if(CGlobal.getScreenOrientation(mActivity) == Configuration.ORIENTATION_LANDSCAPE)
				{
					m_Encoder.encodeFrame(data);
				}
				else
				{
					byte[] rotatedData = ImageProcessing.rotateYUV420Degree90(data, width, height);					
					m_Encoder.encodeFrame(rotatedData);
				}
			}	
		}
		
    	if (bIsRecognizing == true)
    	{    		
    		return;
    	}
    	
    	bIsRecognizing = true;
    			
		thread = null;
		thread = new Thread()
		{			
			public void run()
			{
				boolean bRet = recogCar(data, width, height);
				bIsRecognizing = false;
			}
		};
		thread.start();	
	}
	
	private boolean recogCar(byte[] data, int width, int height) 
	{
		if (mCamera == null) return false; 
		
		if( mActivity.m_bShowPopupResult == true)
		{
			Message msg = Message.obtain(mActivity.getHandler(), R.id.recog_failed);
			msg.sendToTarget();
			return false;
		}
		
		boolean bRet = false;
		mActivity.bIsAvailable = false;
		int rot = CGlobal.g_iFrameRotation;
		long start = System.currentTimeMillis();
		RecogResult rawResult = CGlobal.myEngine.RecogGrayImg(data, width, height, rot);
		long end = System.currentTimeMillis();
		
		Log.i(Defines.APP_TAG, "Recog time = " + String.valueOf(end - start) + "ms");
		
//		if(false)
//		{
//			Bitmap recogBitmap = CGlobal.getColorOrgBitmap(data, width, height);
//			Bitmap rotateBitmap = CGlobal.RotateBitmap(recogBitmap,(float)rot);
//			CGlobal.SaveRecogBitmap("", rotateBitmap);
//		}
		
		if (rawResult != null) 
		{		
			//save recoged image;
			Bitmap recogBitmap = CGlobal.getColorOrgBitmap(data, width, height);
			Bitmap rotateBitmap = CGlobal.RotateBitmap(recogBitmap,(float)rot);
//			CGlobal.SaveRecogBitmap("", rotateBitmap);
			
			//notify recog success to activity
			mActivity.m_bShowPopupResult = true;					
			Message msg = Message.obtain(mActivity.getHandler(), R.id.recog_succeeded, rawResult);
			if(mActivity.mPausing == false) msg.sendToTarget();			
			bRet = true;
		}
		else 
		{
			Message msg = Message.obtain(mActivity.getHandler(), R.id.recog_failed);
			if(mActivity.mPausing == false) msg.sendToTarget();
			bRet = false;
		}
		
		if (mCamera != null)
		{
			mActivity.bIsAvailable = true;
		}
		
		return bRet;
	}
   
	public void cancelAutoFocus()
	{
		if (mCamera != null)
			mCamera.cancelAutoFocus();
	}
	
	void setPreviewHandler(Handler previewHandler, int previewMessage) 
	{
	    this.previewHandler = previewHandler;
	    this.previewMessage = previewMessage;
	}
	void setAutofocusHandler(Handler autofocusHandler, int autofocusMessage) 
	{
	    this.autofocusHandler = autofocusHandler;
	    this.autofocusMessage = autofocusMessage;
	}
	
	public void requestAutoFocus(Handler handler, int message) 
	{
		if (mCamera != null)
		{
			setAutofocusHandler(handler, message);
			mCamera.autoFocus(this);
		}
	}
	
	public SurfaceHolder getSurfaceHolder()
	{
		return mHolder; 
	}
	
	VideoEncoderFromBuffer m_Encoder = null;
	public void startRecording() //throws IOException 
	{
		//int width = 1280;
		//int height = 720;
		int width = mCamera.getParameters().getPreviewSize().width;
		int height = mCamera.getParameters().getPreviewSize().height;
		
		String outFileName = CGlobal.getSaveVideoName();
		
		if(m_Encoder != null)
		{
			m_Encoder.close();
		}
		
		if(CGlobal.getScreenOrientation(mActivity) == Configuration.ORIENTATION_LANDSCAPE)
		{
			m_Encoder = new VideoEncoderFromBuffer(outFileName, width, height);
		}
		else
		{
			m_Encoder = new VideoEncoderFromBuffer(outFileName, height, width);
		}
		
	}

	public void stopRecording() 
	{
		if(m_Encoder != null)
		{
			m_Encoder.close();
			m_Encoder = null;
		}
	}

}

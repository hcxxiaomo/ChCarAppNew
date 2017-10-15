package com.carOCR.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONObject;

import com.carOCR.RecogEngine;
import com.carOCR.RecogResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.szOCR.camera.CameraPreview;
import com.szOCR.camera.ScanHandler;
import com.szOCR.camera.ScanIllegalHandler;
import com.szOCR.camera.ViewfinderView;
import com.szOCR.general.CGlobal;
import com.szOCR.general.Defines;
import com.xiaomo.chcarappnew.R;
import com.xiaomo.db.dao.CarNumberInfoDao;
import com.xiaomo.db.model.CarNumberInfo;
import com.xiaomo.util.BitmapThumb;
import com.xiaomo.util.MyDbHelper;
import com.xiaomo.util.RestClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ScanIllegalActivity extends Activity implements SensorEventListener,View.OnClickListener, OnTouchListener
{
	private static final String TAG = "ScanIllegalActivity";
	public static final int mdevelop_product = CGlobal.PRODUCT_VER ;//DEVELOP_VER;//
	
	private String lastCarNumber = null;;
	
	public int 				m_infor_prevent;
	
	public  CameraPreview 	mCameraPreview;
	public 	ViewfinderView	mViewfinderView;
	
    public  RelativeLayout 	mHomeLayout;
    private Vibrator 		vibrator;
    
    private RelativeLayout 	mResultLayout;
	private TextView		mTxtViewPreviewSize;
	private TextView		mTxtViewRecogTime;
	private TextView		record_car_number;

//	public  RelativeLayout 	car_number_layout;
//	private TextView		car_number;
//	private TextView		car_illegal;
	
	private MyDbHelper myDbHelper;
	private CarNumberInfoDao carNumberInfoDao;
	private SharedPreferences sp;
	
	public boolean			 m_bShowPopupResult;
	private PopupRecogResult m_PopupResult;
	public  PopupPassword 	 m_PopupPassword;

	private ImageView		mbtnPicture;
	private ImageView		imageView_animation1;
	private ImageView		scan_illegal_image_1;
	private ImageView		scan_illegal_image_2;
	
	private SoundPool soundPool;
//	private AnimationDrawable animationDrawable;
	
//	private MediaPlayer mp;//mediaPlayer对象 
	
	public boolean			m_bRecorderStarted;

    public ScanIllegalHandler 		m_scanHandler;
    PowerManager.WakeLock 	wakeLock;
    
    private long 			lastTime=0;
    private float 			lastX;
    private float 			lastY;
    private float 			lastZ;
    private SensorManager 	sensorManager;
    private Sensor 			accelerormeterSensor;
 
    public boolean bIsAvailable;
    
    private static final int UPDATE_PARAM_INITIALIZE = 1;
	public  static final int UPDATE_PARAM_ZOOM = 2;
	private static final int UPDATE_PARAM_PREFERENCE = 4;
	private static final int UPDATE_PARAM_ALL = -1;

    private static final int ZOOM_STOPPED = 0;
	private static final int ZOOM_START = 1;
	private static final int ZOOM_STOPPING = 2;
	
	private static final String SHOW_RESULTDIALOG = "SHOW_RESULTDIALOG";
	private static final String SHOW_VIDEORECORDER_BUTTON = "SHOW_VIDEORECORDER_BUTTON";
	private static final String SHOW_ZOOMBAR = "SHOW_ZOOMBAR";
	private static final String ENABLE_AUTOFOCUS = "ENABLE_AUTOFOCUS";
	private static final String SHOW_CAMERA_ID = "SHOW_CAMERA_ID";
	
	private ViewfinderView m_ViewfinderView;
	
	private int 		mZoomState = ZOOM_STOPPED;
	private boolean 	mSmoothZoomSupported = false;
	public  int 		mZoomValue; // The current zoom value.
	public static int 		mZoomMax = 1;
	
	private int 		mTargetZoomValue;
	private int 		mUpdateSet;
	
	private static final int FIRST_TIME_INIT = 2;
	private static final int CLEAR_SCREEN_DELAY = 3;
	private static final int SET_CAMERA_PARAMETERS_WHEN_IDLE = 4;
	
	private static final int PREVIEW_STOPPED = 0;
	protected static final int IDLE = 1; // preview is active
	// Focus is in progress. The exact focus state is in Focus.java.
	private static final int FOCUSING = 2;
	private static final int SNAPSHOT_IN_PROGRESS = 3;
	private static final int SELFTIMER_COUNTING = 4;
	protected static final int SAVING_PICTURES = 5;
	private int mCameraState = PREVIEW_STOPPED;
	public boolean mPausing = true;
	private boolean mFirstTimeInitialized;
	final Handler mHandler = new MainHandler();
	
	private boolean bFirstLayoutChanged;
	
	private SharedPreferences 	mPrefs;
	
	private SharedPreferences 	networkPrefs;
	public 	boolean				m_bShowResultDialog;
	public 	boolean				m_bShowVideoBtn;
	public 	boolean				m_bShowZoomBar;
	public 	boolean				m_bAutoFocus;
	int mCameraId;// =2 
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        mPrefs = getPreferences(MODE_PRIVATE);
        mCameraId = mPrefs.getInt(SHOW_CAMERA_ID , 0);
        
        Intent intent=getIntent();  
        m_infor_prevent = (int)intent.getIntExtra("info_prevent", 0);
        CGlobal.g_scanIllegalActivity = this;
        Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_scan_illegal);
		
		OptionActivity.loadOptionData(this);
		
        bIsAvailable 		= true;
        mHomeLayout = (RelativeLayout) findViewById(R.id.previewLayout);
        mTxtViewPreviewSize = (TextView)findViewById(R.id.txtViewPreviewSize);
        mTxtViewRecogTime = (TextView)findViewById(R.id.txtViewRecogTime);
        record_car_number = (TextView)findViewById(R.id.record_car_number);
        
        imageView_animation1 = (ImageView) findViewById(R.id.imageView_animation1);
        scan_illegal_image_1 = (ImageView) findViewById(R.id.scan_illegal_image_1);
        scan_illegal_image_2 = (ImageView) findViewById(R.id.scan_illegal_image_2);
//        imageView_animation1.setBackgroundResource(R.drawable.gif);
     // 获取AnimationDrawable对象 
//        animationDrawable = (AnimationDrawable)imageView_animation1.getBackground();

        //mTxtViewPreviewSize.setVisibility(View.GONE);
        //mTxtViewRecogTime.setVisibility(View.GONE);

        m_PopupResult = new PopupRecogResult(this);
//        m_PopupResult.m_btnOk.setOnClickListener(this);
//        m_PopupResult.m_btnCacel.setOnClickListener(this);
        m_bShowPopupResult = false;
        
    	m_bRecorderStarted = false;
		
//    	car_number_layout =  (RelativeLayout)findViewById(R.id.car_number_layout);
//    	car_number =  (TextView)findViewById(R.id.car_number);
//    	car_illegal =  (TextView)findViewById(R.id.car_illegal);
    	
        mResultLayout = (RelativeLayout)findViewById(R.id.layoutResult);
        mViewfinderView = (ViewfinderView)findViewById(R.id.viewfinder_view);
        mViewfinderView.setOnTouchListener(this);
        //mResultLayout.setVisibility(View.GONE);
    	
        mbtnPicture=(ImageView)findViewById(R.id.idStartPicture);
        mbtnPicture.setOnClickListener(this);
//        mbtnRecoder.setVisibility(View.GONE);

 		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
 		wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock( PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "WakeLockActivity");


		mFirstTimeInitialized = false;
		bFirstLayoutChanged = true;
		
		
		m_bShowResultDialog = mPrefs.getBoolean(SHOW_RESULTDIALOG, true);
		m_bShowVideoBtn = mPrefs.getBoolean(SHOW_VIDEORECORDER_BUTTON, false);
		m_bShowZoomBar  = mPrefs.getBoolean(SHOW_ZOOMBAR, false);
    	m_bAutoFocus	= mPrefs.getBoolean(ENABLE_AUTOFOCUS, false);
    	
    	m_bShowVideoBtn = true;
    	m_bShowResultDialog = true;
    	m_PopupResult.mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				m_PopupResult.hide();
				m_scanHandler.sendEmptyMessage(R.id.restart_preview);
			}
		});
		
    	
    	testRect();
    	
    	CGlobal.g_devicekey = CGlobal.myEngine.doGetDevId(this);
		SharedPreferences p = this.getSharedPreferences("SYS_KEY", MODE_PRIVATE);
        CGlobal.g_verifykey = p.getString("verifykey", "");
        boolean bInputable = true;
        
        if(CGlobal.g_verifykey.isEmpty() == true || CGlobal.g_verifykey.equalsIgnoreCase("") == true || CGlobal.g_verifykey == null) {
        	bInputable = false;
        }
        else{
        	if(RecogEngine.doVerifyKey(CGlobal.g_devicekey, CGlobal.g_verifykey)==false)
        		bInputable = false;
        }
        if(bInputable == false)
        {
        	m_PopupPassword = new PopupPassword(this);
    		m_PopupPassword.m_btnOk.setOnClickListener(this);
    		m_PopupPassword.m_btnCacel.setOnClickListener(this);
        	//View view = findViewById(android.R.id.content);
        	handler.sendEmptyMessageDelayed(0,2000);
        }
        //CGlobal.g_verifykey = "123456789012";
        
        //新增加数据库和联网查询部分
        myDbHelper = new MyDbHelper(this, "db_car_number", 1);
        carNumberInfoDao = new CarNumberInfoDao(myDbHelper.getWritableDatabase());//得到dao
        sp = this.getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        
        networkPrefs = this.getSharedPreferences("network_set", Activity.MODE_PRIVATE);
        
        soundPool= new SoundPool(2,AudioManager.STREAM_SYSTEM,5);//第二行将soundPool实例化，第一个参数为soundPool可以支持的声音数量，这决定了Android为其开设多大的缓冲区，第二个参数为声音类型，在这里标识为系统声音，除此之外还有AudioManager.STREAM_RING以及AudioManager.STREAM_MUSIC等，系统会根据不同的声音为其标志不同的优先级和缓冲区，最后参数为声音品质，品质越高，声音效果越好，但耗费更多的系统资源。
        soundPool.load(this,R.raw.illegal,1);//系统为soundPool加载声音，第一个参数为上下文参数，第二个参数为声音的id，一般我们将声音信息保存在res的raw文件夹下，如下图所示。
        
    }
    @SuppressLint("HandlerLeak")
	Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                {
                	try
                	{
                		m_PopupPassword.showAtLocation( 0, 0);
                	}
                	catch(Exception e)
					{
						e.printStackTrace();
					}
                	
                    break;
                }
            }
        }
    };
    public void testRect()
    {
        // This is to rotate about the Rectangles center
//    	RectF rc = new RectF(0, 0, 10, 10);
//    	Matrix transform = new Matrix();
//        
//    	transform.setRectToRect(new RectF(0, 0, 100, 100), RectF(-1000, -1000, 1000, 1000), );
//    	transform.mapRect(rc);
    	
    	
    }

	private boolean initializeZoomMax(Parameters parameters)
	{
		if (!parameters.isZoomSupported())
			return false;
		mZoomMax = parameters.getMaxZoom();
		// Currently we use immediate zoom for fast zooming to get better UX and
		// there is no plan to take advantage of the smooth zoom.		
		return true;
	}

	private void initializeZoom() {
		// Get the parameter to make sure we have the up-to-date zoom value.
		Parameters mParameters = mCameraPreview.mCamera.getParameters();
		if (!initializeZoomMax(mParameters))
			return;
		
    	if (mParameters.isZoomSupported() && CGlobal.g_nCameraZoomFactor != -1) {
    		mParameters.setZoom(CGlobal.g_nCameraZoomFactor);
    		mCameraPreview.mCamera.setParameters(mParameters);
    		
		}
    	    	
		mZoomValue = mParameters.getZoom();
	}
	// If the Camera is idle, update the parameters immediately, otherwise
		// accumulate them in mUpdateSet and update later.
	public void setCameraParametersWhenIdle(int additionalUpdateSet) 
	{
		mUpdateSet |= additionalUpdateSet;
		if (mCameraPreview.mCamera == null)
		{
			// We will update all the parameters when we open the device, so
			// we don't need to do anything now.
			mUpdateSet = 0;
			return;
		} 
		else if (isCameraIdle()) 
		{
			mCameraPreview.setCameraZoomIndex(mZoomValue);
			
			mUpdateSet = 0;
		}
		else 
		{
			mCameraPreview.setCameraZoomIndex(mZoomValue);
			mUpdateSet = 0;
//			if (!mHandler.hasMessages(SET_CAMERA_PARAMETERS_WHEN_IDLE)) {
//				mHandler.sendEmptyMessageDelayed(
//						SET_CAMERA_PARAMETERS_WHEN_IDLE, 1000);
//			}
		}
	}
	private boolean isCameraIdle() {
		return (mCameraState == IDLE)/* || (mFocusManager.isFocusCompleted()) */;
	}
	private void setCameraState(int state) {
		mCameraState = state;
	}
	private void initializeFirstTime() {
		//mCameraPreview.setOnTouchListener(this);
		initializeZoom();
	}

	// If the activity is paused and resumed, this method will be called in
	// onResume.
	private void initializeSecondTime() {
		initializeZoom();
	}
	private class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FIRST_TIME_INIT: {
					initializeFirstTime();
					break;
				}
				case SET_CAMERA_PARAMETERS_WHEN_IDLE: {
					setCameraParametersWhenIdle(0);
					break;
				}
			}
		}
	}

    public Handler getHandler() {
		return m_scanHandler;
	}


    @Override
    protected void onResume() 
    {
        super.onResume();

        
        mCameraPreview = new CameraPreview(this, mCameraId, CameraPreview.LayoutMode.FitToParent);//2
        RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        previewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        mHomeLayout.addView(mCameraPreview, 0, previewLayoutParams);
        
		if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);

     
        if (m_scanHandler == null) 
        	m_scanHandler = new ScanIllegalHandler(this,mCameraPreview);
        m_scanHandler.sendEmptyMessageDelayed(R.id.auto_focus,1000);
                
        if (wakeLock != null) 
        	wakeLock.acquire();  
        
        ShowResultCtrls();        
        
        doInitZoom();
        mPausing = false;
	}
    
    public void doInitZoom()
    {
        if (!mFirstTimeInitialized) 
        {
			mHandler.sendEmptyMessage(FIRST_TIME_INIT);
			
		} 
        else
        {
			initializeSecondTime();
		}
    }
    
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) 
    {        
        if (hasFocus) 
        {
        	if(mCameraPreview.getSurfaceHolder() != null)
            {
            	doInitZoom();
            }
        }
    }
    
    

    @Override
    protected void onPause() 
    {
        super.onPause();
        mPausing = true;
        
//        mCameraPreview.cancelAutoFocus();
        mCameraPreview.cancelAutoFocus();
        mCameraPreview.stop();
        mHomeLayout.removeView(mCameraPreview); // This is necessary.
        mCameraPreview = null;
        
        if (m_scanHandler != null) 
        {
        	m_scanHandler.quitSynchronously();
        	m_scanHandler = null;
		}
        
		if (sensorManager != null)
            sensorManager.unregisterListener(this);

	    if (wakeLock != null) 
		{  
		   	wakeLock.release();  
		}
	    mHandler.removeMessages(FIRST_TIME_INIT);
	    
		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putBoolean(SHOW_RESULTDIALOG, m_bShowResultDialog);
		ed.putBoolean(SHOW_VIDEORECORDER_BUTTON, m_bShowVideoBtn);
		ed.putBoolean(SHOW_ZOOMBAR, m_bShowZoomBar);
		ed.putBoolean(ENABLE_AUTOFOCUS, m_bAutoFocus);
		ed.commit();
    }
    
    @Override
	protected void onDestroy() 
    {
		super.onDestroy();
		//关闭数据库
		if (myDbHelper != null) {
			myDbHelper.close();
		}

	}
    @Override
    public void onBackPressed() 
    {
    	super.onBackPressed();
    }
    
    @Override
	public void onSensorChanged(SensorEvent event) 
	{
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			
			long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 1000 && bIsAvailable == true) 
            {
            	lastTime = currentTime;
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];
		
				if (!mCameraPreview.bInitialized){
					lastX = x;
					lastY = y;
					lastZ = z;
					mCameraPreview.bInitialized = true;
				}
				float deltaX  = Math.abs(lastX - x);
				float deltaY = Math.abs(lastY - y);
				float deltaZ = Math.abs(lastZ - z);
//				Log.println(Log.ASSERT, "tag",String.format("%f %f %f",deltaX,deltaY,deltaZ));
				if(mCameraPreview.mCamera == null)
					Log.println(Log.ASSERT, "tag","null");
				if (mCameraPreview.mCamera != null && deltaX > .2 && !mCameraPreview.bIsStateAutoFocusing){ //AUTOFOCUS (while it is not autofocusing)
					mCameraPreview.autoCameraFocuse();
				}
				if (mCameraPreview.mCamera != null && deltaY > .2 && !mCameraPreview.bIsStateAutoFocusing){ //AUTOFOCUS (while it is not autofocusing)
					mCameraPreview.autoCameraFocuse();

				}
				if (mCameraPreview.mCamera != null && deltaZ > 0.2 && !mCameraPreview.bIsStateAutoFocusing){ //AUTOFOCUS (while it is not autofocusing) */
					mCameraPreview.autoCameraFocuse();

				}
		
				lastX = x;
				lastY = y;
				lastZ = z;
	
            }
        }
	}
    public void setAndshowPreviewSize()
    {
    	Camera.Size previewSize = mCameraPreview.getPreviewSize(); 
        String strPreviewSize =   String.valueOf(previewSize.width) +" x "+String.valueOf(previewSize.height) ;
        mTxtViewPreviewSize.setText(strPreviewSize);
//        LayoutParams  lp = new LayoutParams(mivFitMessage1.getLayoutParams());
//        int dx = CGlobal.g_scrCropRect.centerX()-lp.
//        mivFitMessage1.setLayoutParams(lp);
    }
    
	public void returnRecogedData(RecogResult result)//,Bitmap bmImage)
    {
		//playBeepSoundAndVibrate();
//		Log.e("-xiaomo-", "m_bShowResultDialog="+m_bShowResultDialog+"-----m_bShowPopupResult="+m_bShowPopupResult);
		
		if(m_bShowResultDialog == true)
		{
			//下面这两行代码是允许查车时调用的代码
			m_bShowPopupResult = false;
			m_scanHandler.sendEmptyMessage(R.id.restart_preview);
			if (result.m_szRecogTxt[0].equals(lastCarNumber)) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			
//			m_bShowPopupResult = true;
			Log.e("-xiaomo-", "result.m_szRecogTxt[0]="+result.m_szRecogTxt[0]+"lastCarNumber="+lastCarNumber);
				
				if (vibrator != null)
					vibrator.vibrate(200L);
				CGlobal.g_RecogResult = result;
				mTxtViewRecogTime.setText(result.m_szRecogTxt[0]);
				record_car_number.setText(result.m_szRecogTxt[0]);
//				car_number_layout.setVisibility(View.VISIBLE);
//				car_number.setText(result.m_szRecogTxt[0]);
				lastCarNumber = result.m_szRecogTxt[0];
				//保存图片到本地去
//				CGlobal.carPath =  CGlobal.SaveRecogBitmap("", CGlobal.myEngine.getRecgBitmap());
				
				//调用接口查询车辆信息
				//保存到数据库中
				getCarInfoAndSave();
				
			
			
			
//			m_PopupResult.showAtLocation(mViewfinderView, 0, 0);
//			m_PopupResult.showAtLocation(mViewfinderView, 0, 0);
		}
		else
		{
			//CGlobal.SaveRecogBitmap("", CGlobal.myEngine.getRecgBitmap());
			//m_scanHandler.sendEmptyMessage(R.id.restart_preview);
		}

    }
	private void ShowResultCtrls()
	{		
//		if(CGlobal.g_runMode == Defines.RUMMODE_RECORD)
//		{
//			mbtnRecoder.setVisibility(View.VISIBLE);
//		}
//		else
//		{
//			mbtnRecoder.setVisibility(View.INVISIBLE);
//		}
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_system_info, menu);
        menu.findItem(R.id.mnuItemShowRecogResult).setCheckable(true);
        menu.findItem(R.id.mnuItemShowVideo).setCheckable(true);
        menu.findItem(R.id.mnuItemShowZoomBar).setCheckable(true);
        menu.findItem(R.id.mnuItemAutoFocus).setCheckable(true);
//        menu.findItem(R.id.mnuItemSelectCamera).setCheckable(true);
        
        menu.findItem(R.id.mnuItemShowRecogResult).setChecked(m_bShowResultDialog);
        menu.findItem(R.id.mnuItemShowVideo).setChecked(m_bShowVideoBtn);
        menu.findItem(R.id.mnuItemShowZoomBar).setChecked(m_bShowZoomBar);
        menu.findItem(R.id.mnuItemAutoFocus).setChecked(m_bAutoFocus);
//        menu.findItem(R.id.mnuItemSelectCamera).setChecked(m_bAutoFocus);
        
        menu.findItem(R.id.mnuItemShowRecogResult).setVisible(false);
        menu.findItem(R.id.mnuItemShowVideo).setVisible(false);
        menu.findItem(R.id.mnuItemShowZoomBar).setVisible(false);
        menu.findItem(R.id.mnuItemAutoFocus).setVisible(false);
        
        //隐藏录像方式 XXX 
        menu.findItem(R.id.mnuItemSelectRecord).setVisible(false);
        
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	
        boolean bCheck = !(item.isChecked());
        item.setChecked(bCheck);
        int listIndex = 0;
        switch (item.getItemId())
        {
        
        	case R.id.mnuItemShowRecogResult:
        		m_bShowResultDialog = bCheck;
        		break;
        	case R.id.mnuItemShowVideo:
        		m_bShowVideoBtn = bCheck;
        		break;
        	case R.id.mnuItemShowZoomBar:
        		m_bShowZoomBar = bCheck;
        		break;
        	case R.id.mnuItemAutoFocus:
        		m_bAutoFocus = bCheck;
        		break;	
        	case R.id.mnuItemSelectCamera:
//        		int cameraId = mCameraPreview.setNextCamera();
//        		initializeFirstTime();
//        		Toast.makeText(this, "切换到"+cameraId+"号摄像头", Toast.LENGTH_SHORT).show();;
        		 mCameraId = (Camera.getNumberOfCameras() - mCameraId - 1) % Camera.getNumberOfCameras();
    	    	SharedPreferences.Editor ed = mPrefs.edit();
    			ed.putInt(SHOW_CAMERA_ID, mCameraId);
    			ed.commit();
    			Intent intent = new Intent(this, ScanIllegalActivity.class);
    			this.startActivity(intent);
    			this.finish();
        		break;	
        	case R.id.mnuItemSelectZoom:
//        		showOption(OptionActivity.OPTION_SELECTZOOM);
        		//先找出设定的值 XXX
        		 String[] ZOOM_LIST_STRING = {"0~4m", "4~8m", "8~12m", "12~16m", "16~20m"};
        		 final double[] ZOOM_LIST = {0, 0.25, 0.5, 0.75, 1};
        		
    			if(CGlobal.g_nCameraZoomFactor >= 0)
    	        {
        	        
        	        double minDiffValue = Double.MAX_VALUE;
        	        
        	        for(int i=0;i<ZOOM_LIST.length; i++)
        	        {
        	        	double diff = Math.abs(ScanIllegalActivity.mZoomMax * ZOOM_LIST[i] - CGlobal.g_nCameraZoomFactor);
        	        	if(minDiffValue > diff)
        	        	{
        	        		minDiffValue = diff;
        	        		listIndex = i;
        	        	}
        	        }
        	        
            	}
        		
        		
        		//替换成弹窗方式的
        		new AlertDialog.Builder(this)
				.setTitle("请选择焦距")
				.setSingleChoiceItems(ZOOM_LIST_STRING, listIndex, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CGlobal.g_nCameraZoomFactor = (int)(ScanIllegalActivity.mZoomMax * ZOOM_LIST[which]);
				        OptionActivity.saveOptionData(ScanIllegalActivity.this);
					}
				})
				
//				(ZOOM_LIST_STRING, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//						  CGlobal.g_nCameraZoomFactor = (int)(ScanActivity.mZoomMax * ZOOM_LIST[which]);
//					        OptionActivity.saveOptionData(ScanActivity.this);
//					}
//				})
				.setPositiveButton("确定", null)
				.setNegativeButton("取消", null)
				.create()
				.show();
        		break;
        	case R.id.mnuItemSelectProvince:
//        		showOption(OptionActivity.OPTION_SELECTPROV);
        		 final String[] PROV_STRING_LIST = {"全部", "京", "津", "晋", "冀",  "蒙", "辽", "吉", "黑", "沪", "苏", "浙", 
        				"皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂", "琼", "川", "贵", "云", "藏", "陕", "甘", "青", "宁", "新","渝"};
        		 final int PROV_BASEINDEX = 36;
        		 
        		 if(CGlobal.g_provinceId != 0)
     	        {
     				listIndex = CGlobal.g_provinceId - PROV_BASEINDEX;	        		        	
     	        }			
     			
        			//替换成弹窗方式的
         		new AlertDialog.Builder(this)
 				.setTitle("请选择默认省份")
 				.setSingleChoiceItems(PROV_STRING_LIST, listIndex, new OnClickListener() {
 					
 					@Override
 					public void onClick(DialogInterface dialog, int which) {
 						  if(which == 0)
 					        {
 					        	CGlobal.g_provinceId = which;
 					        }
 					        else
 					        {
 					        	CGlobal.g_provinceId = which + PROV_BASEINDEX;
 					        }
 							
// 					        CGlobal.outputToast(this, text);
 					        
 					        OptionActivity.saveOptionData(ScanIllegalActivity.this);
 					}
 				})
 				.setPositiveButton("确定", null)
				.setNegativeButton("取消", null)
				.create()
				.show();;
         		
        		break;	
        	case R.id.mnuItemSelectRecord:
        		showOption(OptionActivity.OPTION_SELECTRECORD);
        		break;
//        	case R.id.mnuItemInputIds:
//        		showOption(OptionActivity.OPTION_INPUTIDS);
//        		break;
        	default:
    	        break;
        }
        ShowResultCtrls();
    	return super.onOptionsItemSelected(item);
    }
    
    
	
	@Override
	public void onClick(View v) 
	{
//		Log.i("-xiaomo-", "ScanActivity.onClick"+v.getId());
		if(v.getId() == R.id.idStartPicture)
		{
			ShowResultCtrls();
			//保存图片功能
			CGlobal.carPath =  CGlobal.SaveRecogBitmap("", CGlobal.myEngine.getRecgBitmap());
			if (scan_illegal_image_1.getDrawable() == null) {
				scan_illegal_image_1.setImageBitmap(
						(BitmapThumb.extractMiniThumb(BitmapFactory.decodeFile(CGlobal.carPath), 120, 160, true))	
				);
			}else if (scan_illegal_image_2.getDrawable() == null){
				scan_illegal_image_2.setImageBitmap(
						(BitmapThumb.extractMiniThumb(BitmapFactory.decodeFile(CGlobal.carPath), 120, 160, true))	
				);
			}else{
				Toast.makeText(this, "请点击对应的图片进行删除", Toast.LENGTH_SHORT).show();
			}
			
			
//			m_scanHandler.sendEmptyMessageDelayed(R.id.restart_preview,10);
			
//			if (m_bRecorderStarted == false) 
//		      {
//		    	  mbtnRecoder.setImageResource(R.drawable.shutter_bg_pressed);
//		    	  mCameraPreview.startRecording();
//	              m_bRecorderStarted = true;
//		      } else 
//		      {
//		    	  m_bRecorderStarted = false;
//		    	  mbtnRecoder.setImageResource(R.drawable.shutter_bg_normal);
//		    	  mCameraPreview.stopRecording();
//		      }
		}
    	else if (v.equals(m_PopupResult.m_btnOk) )
    	{
    		m_bShowPopupResult = false;
    		m_PopupResult.hide();
    		//RegisterRecogData(this);
//    		CGlobal.SaveRecogBitmap("", CGlobal.myEngine.getRecgBitmap());
    		m_scanHandler.sendEmptyMessage(R.id.restart_preview);
//    		Log.i("-xiaomo-", "ScanActivity.onClick else if (v.equals(m_PopupResult.m_btnOk)");
    	}
    	else if (v.equals(m_PopupResult.m_btnCacel))
    	{
    		m_bShowPopupResult = false;
    		m_PopupResult.hide();
    		m_scanHandler.sendEmptyMessage(R.id.restart_preview);
//    		Log.i("-xiaomo-", "m_PopupResult.hide()");
    	}
//		if(mCameraPreview != null){
//    		mCameraPreview.autoCameraFocuse();
//    	}
    	else if (v.equals(m_PopupPassword.m_btnOk))
    	{
    		String devicekey = this.m_PopupPassword.m_txtDeviceKey.getText().toString();
    		String verifykey = this.m_PopupPassword.m_editVerifyKey.getText().toString();
    		if(RecogEngine.doVerifyKey(devicekey,verifykey)==false){
    			Toast.makeText(this, "key is not true", Toast.LENGTH_SHORT).show();
    		}
    		else{
    			m_PopupPassword.hide();
    			SharedPreferences p = this.getSharedPreferences("SYS_KEY", MODE_PRIVATE);
    			p.edit().putString("verifykey", verifykey).apply();
    			CGlobal.g_verifykey = verifykey;
    		}
    		Log.i("-xiaomo-", "else if (v.equals(m_PopupResult.m_btnOk)");
    	}
    	else if (v.equals(m_PopupPassword.m_btnCacel))
    	{
    		Toast.makeText(this, "key is not inputed,", Toast.LENGTH_SHORT).show();
    		m_PopupPassword.hide();
    	}else if(v.equals(scan_illegal_image_1) || v.equals(scan_illegal_image_2) ){
    		Log.i("-xiaomo-", "else if (v.equals(m_PopupResult.scan_illegal_image_1/2)");
    		((ImageView)v).setImageDrawable(null);
    	}
	}
	
	public void showOption(int iOptionPage)
	{
		Intent m = new Intent(this, OptionActivity.class);
		m.putExtra("OptionPage", iOptionPage);
		startActivityForResult(m, OptionActivity.OPTIONACTIVITY_REQUESTCODE);
	}
	
	public boolean onTouch(View v, MotionEvent event)  	  
	 {  
	  //Toast.makeText(this, "Touch Touch", Toast.LENGTH_SHORT).show();
		if(m_bAutoFocus == false){
			m_scanHandler.sendEmptyMessageDelayed(R.id.force_focus,200);
		}
	  
	  return false;  
	 }  
	
	private void getCarInfoAndSave(){
		 //联网开始查询数据信息
		
/*        RequestParams params = new RequestParams();
		params.put("carNumberHpzm", CGlobal.g_RecogResult.m_szRecogTxt[0]);
		params.put("reportPoliceId", sp.getString("policeId", "001"));
		params.put("reportPoliceName", sp.getString("name", "姓名"));
		params.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA).format(new Date()));
		RestClient.post(
				networkPrefs.getString("server", "117.27.138.166")
				,networkPrefs.getString("port", "8080")
				,networkPrefs.getString("dir", "carplatenumber")
				,"carplatenumber/carNumber/getCarInfoWithoutType", params, new JsonHttpResponseHandler(){

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Log.i("-xiaomo-", response.toString());
				CarNumberInfo cni = new CarNumberInfo();
				//识别时间：2017-04-23 12:12:12    比对时间：2017-04-23 12:13:15
				cni.setCreateTime(response.optString("createTime"));
				cni.setCompareTime(response.optString("compareTime"));
				cni.setCarColor(response.optString("colorCsys"));
				cni.setCarNumber(CGlobal.g_RecogResult.m_szRecogTxt[0]);
				cni.setEngineNo(response.optString("engineNumberFdjh"));
				cni.setImg(CGlobal.carPath);
				cni.setMaker(response.optString("brandClpp1"));
				cni.setType(response.optString("typeClxh"));
				cni.setVin(response.optString("vinClsbdh"));
				cni.setCarType(response.optString("carType"));
				
				StringBuilder sb = new StringBuilder();
				
				boolean is_leage = false;//正常车辆
				if (response.optInt("isYellowCar") == 1) {
					sb.append("黄标车  ");
					cni.setIsBlackListCar(1);
					is_leage = true;
				}
				if (response.optInt("isBlackListCar") == 1) {
					sb.append("布控车  ");
					cni.setIsBlackListCar(1);
					is_leage = true;
				}
				if (response.optInt("isSeizedCar") == 1) {
					sb.append("查封车  ");
					cni.setIsSeizedCar(1);
					is_leage = true;
				}
				if (response.optInt("isCheckOkCar") == 1) {
					sb.append("逾期未年审车  ");
					cni.setIsCheckOkCar(1);
					is_leage = true;
				}
				if (response.optInt("isScrappedCar") == 1) {
					sb.append("报废车  ");
					is_leage = true;
				}
				if (response.optInt("legalNumber") >= 1) {
					sb.append("有");
					sb.append(String.valueOf(response.optInt("legalNumber")));
					sb.append("次违法未处理");
					cni.setIsLegalCar(1);
					cni.setLegalNumber(response.optInt("legalNumber"));
					is_leage = true;
				}
				if (is_leage) {
					soundPool.play(1,1, 1, 0, 0, 1); //播放了，第一个参数为id，id即为放入到soundPool中的顺序，比如现在collide.wav是第一个，因此它的id就是1。第二个和第三个参数为左右声道的音量控制。第四个参数为优先级，由于只有这一个声音，因此优先级在这里并不重要。第五个参数为是否循环播放，0为不循环，-1为循环。最后一个参数为播放比率，从0.5到2，一般为1，表示正常播放。
					car_illegal.setText(sb.toString());
					
					imageView_animation1.setVisibility(View.VISIBLE);
//			        // 动画是否正在运行  
//			        if(animationDrawable.isRunning()){  
//			            //停止动画播放  
//			            animationDrawable.stop();  
//			        }  
//			            //开始或者继续动画播放  
//			            animationDrawable.start();  
					
				}else{
					imageView_animation1.setVisibility(View.INVISIBLE);
//					// 动画是否正在运行  
//			        if(animationDrawable.isRunning()){  
//			            //停止动画播放  
//			            animationDrawable.stop();  
//			        }  
					car_illegal.setText("正常车辆");
					car_illegal.setTextColor(Color.BLACK);
				}
				cni.setServerCarId(response.optLong("serverCarId"));
				cni.setIsReported(1);
				Log.i("-xiaomo-", cni.toString());
				
				//把数据保存到数据库中去
				carNumberInfoDao.insertCarNumber(cni, sp.getString("policeId", "001"), sp.getString("name", "姓名"));
			Log.e("-xiaomo-", "getCarInfoAndSave end");
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
			}
			
			
		});*/
//		Log.i("-xiaomo-", "end--RestClient.post");
	}
	
	

}
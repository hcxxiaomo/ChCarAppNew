package com.szOCR.camera;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.carOCR.activity.ScanActivity;
import com.szOCR.general.CGlobal;
import com.xiaomo.chcarappnew.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 * 
 */
public final class ViewfinderView extends View implements OnTouchListener
{

  private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
  private static final long ANIMATION_DELAY = 500L;//100L;
  private static final int OPAQUE = 0xFF;

  private final Paint paint;
  private Bitmap resultBitmap;
  private final int maskColor;
  private final int laserColor;
  private int scannerAlpha;
  
  public int m_top = 0;
  public int m_height = 0;
  private float delta;
  
  private long stTime;
  
  public Activity  mActivity;
  
  float fOriginalDis = 0;
  
  int nZoomValue = 0;
  
  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) 
  {
	  	super(context, attrs);

	    // Initialize these once for performance rather than calling them every time in onDraw().
	    paint = new Paint();
	    Resources resources = getResources();
	    maskColor = resources.getColor(R.color.viewfinder_frame);
	    laserColor = resources.getColor(R.color.viewfinder_laser);
	    scannerAlpha = 0;
	    
	    delta = 1.0F;
	    delta = getResources().getDisplayMetrics().density / 1.5F;
	    
	    stTime = System.currentTimeMillis();
	    
	    mActivity = (Activity)context;
	    
		setOnTouchListener(this);

 }
  public ViewfinderView(Context context) {
	  super(context);
	    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    Resources resources = getResources();
	    maskColor = 0x60000000;//resources.getColor(viewfinder_mask);
	    //resultColor = 0xb0000000;//resources.getColor(R.color.result_view);
	    laserColor = 0xffcc0000;//resources.getColor(R.color.viewfinder_laser);
	    //resultPointColor = 0xc0ffbd21;//resources.getColor(R.color.possible_result_points);
	    scannerAlpha = 0;
	    	    
	    delta = 1.0F;
	    delta = getResources().getDisplayMetrics().density / 1.5F;
	    
	    stTime = System.currentTimeMillis();
	    
	    mActivity = (Activity)context;
	    
		setOnTouchListener(this);

	  }
  @Override
  public void onDraw(Canvas canvas) 
  {
	    //Rect frame = new Rect(50,50,320,320);//CameraManager.get().getFramingRect());
	  	Rect frame = new Rect(CGlobal.g_scrCropRect);
//	  	
//	  	int nTopMargin = 0;
//	  	int nLeftMargin = 0;
//	  	
//	  	if (CGlobal.g_scanActivity.mCameraPreview != null)
//	  	{
//	  		int nWidth = canvas.getWidth();
//	  		int nHeight = canvas.getHeight();
//	  		
//	  		Rect cameraView = new Rect(0,0,0,0);
//	  		CGlobal.g_scanActivity.mCameraPreview.getGlobalVisibleRect(cameraView);
//	  		int nCameraWidth = cameraView.width();
//	  		int nCameraHeight = cameraView.height();
//	  		
//	  		nTopMargin = (nHeight - nCameraHeight) / 2;
//	  		nLeftMargin = (nWidth - nCameraWidth) / 2;
//
//	  	}
//	  	  	
//	  	frame.top += (nTopMargin - 30);
//	  	frame.bottom += (nTopMargin - 30);
//	  	
//	  	frame.left += nLeftMargin;
//	  	frame.right += nLeftMargin;
//	  	
	  	
	  	if (isPortrait())
	  	{
	  		frame.top -= frame.height() / 12;
	  		frame.bottom -= frame.height() / 12;
	  		
	  	}
	  	else
	  	{
	  		frame.left -= frame.width() / 12;
	  		frame.right -= frame.width() / 12;
	  	}

  		
	    if (frame == null) 
	    {
	      return;
	    }
	    
	    int width = canvas.getWidth();
	    int height = canvas.getHeight();
	
	    // Draw the exterior (i.e. outside the framing rect) darkened
	    //paint.setColor(resultBitmap != null ? resultColor : maskColor);
	    paint.setColor(0x11111111);//22222222);//55555555);//77777777);
	    //paint.setAlpha(OPAQUE);
	    paint.setAlpha(200);
//	    
//	    canvas.drawRect(0, 0, width, frame.top, paint);
//	    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
//	    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
//	    canvas.drawRect(0, frame.bottom + 1, width, height, paint);
//	
	    if (resultBitmap != null) {
	      // Draw the opaque result bitmap over the scanning rectangle
	    	paint.setAlpha(OPAQUE);
	    	canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
	    } else {
	
	      // Draw a two pixel solid black border inside the framing rect
//	      paint.setColor(Color.WHITE);//frameColor);	      
//	      canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
//	      canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
//	      canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
//	      canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);
	      
	      int corner, margin;
	      corner = (frame.bottom - frame.top) / 8;
	        
	      //paint.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);//FILL);
	      if(CGlobal.g_bAutoFocused == false)
	    	  paint.setColor(Color.RED);
	      else								
	    	  paint.setColor(Color.GREEN);
	      
	      //paint.setColor(-0xFF0100);
	      paint.setColor(Color.GREEN);
	      
	      Rect inFrame = new Rect(frame);
	      
	      margin =  (frame.bottom - frame.top) / 6;
	      inFrame.left = inFrame.left + margin;
	      inFrame.top = inFrame.top + margin;
	      inFrame.right = inFrame.right - margin;
	      inFrame.bottom = inFrame.bottom - margin;
	      
//	      canvas.drawRect(createRect(inFrame.left, inFrame.top, inFrame.left + corner, inFrame.top), paint);
//	      canvas.drawRect(createRect(inFrame.left, inFrame.top, inFrame.left, inFrame.top + corner), paint);
//	      canvas.drawRect(createRect(inFrame.right, inFrame.top, inFrame.right - corner, inFrame.top), paint);
//	      canvas.drawRect(createRect(inFrame.right, inFrame.top, inFrame.right, inFrame.top + corner), paint);
//	      canvas.drawRect(createRect(inFrame.left, inFrame.bottom, inFrame.left + corner, inFrame.bottom), paint);
//	      canvas.drawRect(createRect(inFrame.left, inFrame.bottom, inFrame.left, inFrame.bottom - corner), paint);
//	      canvas.drawRect(createRect(inFrame.right, inFrame.bottom, inFrame.right - corner, inFrame.bottom), paint);
//	      canvas.drawRect(createRect(inFrame.right, inFrame.bottom, inFrame.right, inFrame.bottom - corner), paint);
	        
	
	      // Draw a red "laser scanner" line through the middle to show decoding is active
//	      paint.setColor(laserColor);
//	      paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//	      scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//	      int middle = frame.height() / 2 + frame.top;
//	      canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
	
	      
	      // Request another update at the animation interval, but only repaint the laser line,
	      // not the entire viewfinder mask.
	      postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
	   }
  }
  private Rect createRect(int left, int top, int right, int bottom)
  {
      int offset = (int)(2F * delta);
      Rect rect = new Rect();
      rect.left = Math.min(left, right) - offset;
      rect.right = Math.max(left, right) + offset;
      rect.top = Math.min(top, bottom) - offset;
      rect.bottom = Math.max(top, bottom) + offset;
      return rect;
  }
  
  public boolean isPortrait() {
      return (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
  }
  
  public void drawViewfinder() 
  {
    resultBitmap = null;
    invalidate();
  }

  public boolean onTouch(View v, MotionEvent event) 
	{
		int i1;
		int i2;
  	
  	if (event.getPointerCount() >= 3 || event.getPointerCount() < 2)
      {
      	return true;
      }
  	
      switch (event.getAction() & MotionEvent.ACTION_MASK) 
      {
          case MotionEvent.ACTION_DOWN:   // first finger down only
          		
        	  fOriginalDis = 0;

                  break;

          case MotionEvent.ACTION_UP: // first finger lifted
          {
          		
        	  fOriginalDis = 0;
        	  
        	  ScanActivity parentAct = (ScanActivity)mActivity;
  	  		  parentAct.mZoomValue = nZoomValue;
  	  		  CGlobal.g_nCameraZoomFactor = nZoomValue;
          		break;
          }	
          case MotionEvent.ACTION_POINTER_UP: // second finger lifted
          {
        	  fOriginalDis = 0;
        	  
        	  ScanActivity parentAct = (ScanActivity)mActivity;
  	  		  parentAct.mZoomValue = nZoomValue;
  	  		  CGlobal.g_nCameraZoomFactor = nZoomValue;
                  break;
          }
          case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
        	  
        	  fOriginalDis = distance(event);

          		  break;

          case MotionEvent.ACTION_MOVE:
          {
        	  	ScanActivity parentAct = (ScanActivity)mActivity;
        	  	if (parentAct != null)
        	  	{
        	  		if (fOriginalDis <= 0)
              			break;
        	  		
        	  		float fDis = distance(event) - fOriginalDis;
  	  				
        	  		
        	  		nZoomValue = (int)(parentAct.mZoomValue + (parentAct.mZoomMax * fDis / 1000));
        	  		
        	  		if (nZoomValue < 0)
        	  			nZoomValue = 0;
        	  		
        	  		if (nZoomValue > parentAct.mZoomMax)
        	  			nZoomValue = parentAct.mZoomMax;
        	  		
        	  		parentAct.mCameraPreview.setCameraZoomIndex(nZoomValue);
        	  		
        	  	}
		          break;
          }
         
      }
     
     // invalidate();
      
      return true;
  }
	
  private float distance(MotionEvent event){
      float x = event.getX(0) - event.getX(1);
      float y = event.getY(0) - event.getY(1);
      return FloatMath.sqrt(x * x + y * y);
  }

  private PointF middle( MotionEvent event) {
      float x = event.getX(0) + event.getX(1);
      float y = event.getY(0) + event.getY(1);
      return new PointF(x / 2, y / 2);
  }
  
}

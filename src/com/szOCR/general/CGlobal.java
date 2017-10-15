package com.szOCR.general;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.MediaPlayer;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.carOCR.RecogEngine;
import com.carOCR.RecogResult;
import com.carOCR.activity.ScanActivity;
import com.carOCR.activity.ScanIllegalActivity;

public class CGlobal 
{
	public static 	int 			DEVELOP_VER = 0;
	public static 	int 			PRODUCT_VER = 1;
	
	public static 	RecogEngine 	myEngine = new RecogEngine();
	public static 	RecogResult 	g_RecogResult = new RecogResult();
	
	public static String  g_devicekey = null;
	public static String  g_verifykey = null;
	
	public static 	int 			g_provinceId = 0;
	public static 	int 			g_runMode = Defines.RUNMODE_NONRECORD;
	
	
	public static 	int				g_iFrameRotation=0;
	public static 	Point 			g_screenSize;
	public static 	Rect 			g_scrCropRect = null;
	public static 	Rect 			g_imgCropRect = null;
	public static 	Bitmap 			g_bmpOriginal = null;
	public static 	int				g_topMargin = 0;
	public static	int				g_leftMargin = 0;
	
    public static	boolean 		g_bAutoFocused 	= false;
	
	public static String 			g_szSavePath = "/aCarImage";
	
	public static MediaPlayer 		g_mediaPlayerInformation = null;
	public static MediaPlayer 		g_mediaPlayerPrevention = null;
	public static MediaPlayer 		g_mediaPlayerHomeX = null;
	
	private static float 			sPixelDensity = 1;
	
	public static 	int				ROTATE_0	= 0;
	public static 	int				ROTATE_90	= 90;
	public static 	int				ROTATE_180	= 180;
	public static 	int				ROTATE_270	= 270;
	
	public static 	ScanActivity	g_scanActivity = null;
	public static 	ScanIllegalActivity	g_scanIllegalActivity = null;
	
	public static	int				g_nCameraZoomFactor = -1;
	
	public static String carPath = null;
	
	public static Long chriId = null;
	
	public static String carType = "02";
	
	public static String MakePhoneNumberTypeString(char[] szNumber)
	{
		String full = String.valueOf(szNumber);
		String typeStr = full.substring(0,3) + "-" + full.substring(3,7)+"-"+full.substring(7,11);
		return typeStr;
	}
	public static Rect GetRotateRect(Rect orgRect, int rot)
	{
		Rect rotRect = new Rect();
		if(rot == 0) rotRect.set(orgRect);
		else if(rot == 90){
			rotRect.left = orgRect.top;
			rotRect.top = orgRect.left;
			rotRect.right = orgRect.bottom;
			rotRect.bottom = orgRect.right;
		}
		return rotRect;
	}

	public static Rect getOrgCropRect(int width, int height, int rot, Rect rotRect)
	{
		Rect orgRect = new Rect();
		if(rot == ROTATE_90 || rot == ROTATE_270){//0,rot90
			orgRect.left = rotRect.top;
			orgRect.top = height - rotRect.right-1;
			orgRect.right = rotRect.bottom;
			orgRect.bottom = height - rotRect.left -1;
		}
		else if(rot == ROTATE_0 || rot == ROTATE_180){//1,rot0
			orgRect.set(rotRect);
		}
//		else if(rot == ROTATE_180){//2,rot180
//			orgRect.left = rotRect.top;
//			orgRect.top = rotRect.left;
//			orgRect.right = rotRect.bottom;
//			orgRect.bottom = rotRect.right;
//		}
//		else if(rot == ROTATE_270){//3,rot270
//			orgRect.set(rotRect);
//		}
		
		return orgRect;
	}
	public static void cropYuv420sp(byte[] des, byte[] src, int width, int height, Rect cropRect) {
		int cropwidth = cropRect.width();
		int cropheight = cropRect.height();
		int wh = width * height;
		int wh1 = cropwidth*cropheight;
		int k = 0;
		for (int j = cropRect.top; j <cropRect.bottom; j++) {
			for (int i = cropRect.left; i < cropRect.right; i++) {
				des[k] = src[width * j + i];
				k++;
			}
		}
		for (int j = cropRect.top/2; j <cropRect.bottom/2; j++){
			for (int i = cropRect.left; i < cropRect.right; i ++)  {
				des[k] = src[wh + width * j + i];
				k ++;
			}
		}
	}

	public static void Yuv420spRotate(byte[] des, byte[] src, int width, int height,int rot,int[] wh)
	{
		if(rot == 0) {
			int len = src.length;
			for (int i = 0; i < len; i++) {
				des[i] = src[i];
			}
			wh[0] = width; wh[1] = height;
		}
		else if(rot == ROTATE_90) {
			Yuv420spRotate90(des, src, width, height);
			wh[0] = height; wh[1] = width;
		}
		else if(rot == ROTATE_180) {
			Yuv420spRotate180(des, src, width, height);
			wh[0] = width; wh[1] = height;
		}
		else if(rot == ROTATE_270) {
			Yuv420spRotate270(des, src, width, height);
			wh[0] = height; wh[1] = width;
		}
	}
	private static void Yuv420spRotate90(byte[] des, byte[] src, int width, int height) {
		int wh = width * height;
		int k = 0;
		for (int i = 0; i < width; i++) {
			for (int j = height - 1; j >= 0; j--) {
				des[k] = src[width * j + i];
				k++;
			}
		}
		for (int i = 0; i < width; i += 2) {
			for (int j = height / 2 - 1; j >= 0; j--) {
				des[k] = src[wh + width * j + i];
				des[k + 1] = src[wh + width * j + i + 1];
				k += 2;
			}
		}
	}

	private static void Yuv420spRotate180(byte[] des, byte[] src, int width, int height) {
		int n = 0;
		int uh = height >> 1;
		int wh = width * height;
		// copy y
		for (int j = height - 1; j >= 0; j--) {
			for (int i = width - 1; i >= 0; i--) {
				des[n++] = src[width * j + i];
			}
		}

		for (int j = uh - 1; j >= 0; j--) {
			for (int i = width - 1; i > 0; i -= 2) {
				des[n] = src[wh + width * j + i - 1];
				des[n + 1] = src[wh + width * j + i];
				n += 2;
			}
		}
	}

	private static void Yuv420spRotate270(byte[] des, byte[] src, int width, int height) {
		int n = 0;
		int uvHeight = height >> 1;
		int wh = width * height;
		// copy y
		for (int j = width - 1; j >= 0; j--) {
			for (int i = 0; i < height; i++) {
				des[n++] = src[width * i + j];
			}
		}

		for (int j = width - 1; j > 0; j -= 2) {
			for (int i = 0; i < uvHeight; i++) {
				des[n++] = src[wh + width * i + j - 1];
				des[n++] = src[wh + width * i + j];
			}
		}
	}

	public static Bitmap convYuv420spBitmap(byte[] yuv420sp, int width, int height) {
		int[] rgb = new int[width * height];
		convYuv420spToRGBInt(yuv420sp, width, height, rgb);
		Bitmap bitmap = Bitmap.createBitmap(rgb, width, height, Config.ARGB_8888);
		return bitmap;
	}

	public static void convYuv420spToRGBInt(byte[] yuv420sp, int width, int height, int[] rgb) {

		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;

			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	public static Bitmap makeCropedGrayBitmap(byte[] data, int width, int height, int rot, Rect cropRect) 
	{	
		int cropwidth = cropRect.width(); 
		int cropheight= cropRect.height();
		int[] pixels = new int[cropwidth * cropheight];
		int grey,inputOffset,outputOffset,temp;
		byte[] yuv = data;
		
		if(rot == ROTATE_90){//rot90, screen_rot0
			int x,y,x1,y1;
			for ( y = 0; y < cropheight; y++) {
		    	y1 = cropRect.top + y;
			    for ( x = 0; x < cropwidth; x++) {
			    	x1 = cropRect.left + x;
			    	grey = yuv[y1*width+x1] & 0xff;
			        pixels[x*cropheight + cropheight - y - 1] = 0xFF000000 | (grey * 0x00010101);
			    }
			    
			}
			temp = cropwidth;
			cropwidth = cropheight; cropheight = temp;
		}
		else if(rot == ROTATE_0){//rot0, screen_rot90
			inputOffset = cropRect.top*width;
			for (int y = 0; y < cropheight; y++) {
				outputOffset = y * cropwidth;
			    for (int x = 0; x < cropwidth; x++) {
			    	grey = yuv[inputOffset + x+cropRect.left] & 0xff;
			        pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
			    }
			    inputOffset += width;
			}
			
		}
		else if(rot == ROTATE_180){//rot0, screen_rot90
			inputOffset = cropRect.top*width;
			for (int y = 0; y < cropheight; y++) {
				outputOffset = (cropheight-1-y) * cropwidth;
			    for (int x = 0; x < cropwidth; x++) {
			    	grey = yuv[inputOffset + x+cropRect.left] & 0xff;
			        pixels[outputOffset + cropwidth-1-x] = 0xFF000000 | (grey * 0x00010101);
			    }
			    inputOffset += width;
			}
			
		}
		else if(rot == ROTATE_270){//rot0, screen_rot90
			int x,y,x1,y1;
			for ( y = 0; y < cropheight; y++) {
		    	y1 = cropRect.top + y;
			    for ( x = 0; x < cropwidth; x++) {
			    	x1 = cropRect.left + x;
			    	grey = yuv[y1*width+x1] & 0xff;
			        pixels[(cropwidth-x-1)*cropheight +  y] = 0xFF000000 | (grey * 0x00010101);
			    }
			    
			}
			temp = cropwidth;
			cropwidth = cropheight; cropheight = temp;
		}
		Bitmap bitmap = Bitmap.createBitmap(cropwidth, cropheight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, cropwidth, 0, 0, cropwidth, cropheight);
		
		pixels = null;
		yuv = null;
		
		return bitmap;
	}
	
	
	public static int getByteLength(char[] str,int maxLen)
	{
	    int i,len = 0;
	    for(i=0;i<maxLen;++i)
	    {
	    	if(str[i] ==0) break;
	    }
	    len = i;
	    return len;
	}
	
	public static String getIMEI(Context mContext)
	{
		TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String strIMEI = telephonyManager.getDeviceId();
		
		if (strIMEI == null || strIMEI.equals(""))
			strIMEI = "0";
		
		return strIMEI;
	}
	
	public static String getIMSI(Context mContext)
	{
		TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String strIMSI = telephonyManager.getSubscriberId();
		
		if (strIMSI == null || strIMSI.equals(""))
			strIMSI = "0";
		
		return strIMSI;
	}
	public static String getSaveVideoName()
	{
		String _subdir=null;
		String _fname = null;
		String _path = null;
		_subdir = "/aCarImage/aVideoRecoder/";
		File dir = new File(Environment.getExternalStorageDirectory().toString(), _subdir); 
	    if (!dir.exists()) 
	    {
	        dir.mkdirs();
	    }    
	    _fname =  "/"+ getCurTimeString() + ".mp4";
	    _path = dir.getAbsolutePath()+_fname;
	    return _path;
	}   
	public static String SaveRecogData(String szFileName, byte[] data, int width, int height)
	{
		Bitmap bitmap = getColorOrgBitmap(data, width, height);
		
		int rot = CGlobal.g_iFrameRotation;
		Bitmap rotateBitmap = CGlobal.RotateBitmap(bitmap,(float)rot);
			
		String outFileName = SaveRecogBitmap(szFileName, rotateBitmap);
		return outFileName;
	}
	public static String SaveRecogBitmap(String szFileName, Bitmap bmImage)
    {
    	 File dir = new File(Environment.getExternalStorageDirectory().toString(), "/aCarImage/PreviewImages/");
         
         if (!dir.exists()) 
         {
             if (!dir.mkdirs()) 
             {
                 Log.d("App", "failed to create directory");
                 return "";
             }            
         }
         
 		if(szFileName.isEmpty())
 			szFileName =  getCurTimeString()+".jpg";
         
         File file = new File(dir.getAbsolutePath(), szFileName);
    	 
         try
         {
        	 FileOutputStream fOut = new FileOutputStream(file);
        	 bmImage.compress(Bitmap.CompressFormat.JPEG, 95	, fOut);
        	 fOut.flush();
        	 fOut.close();
         }
         catch(Exception ex)
         {
        	 ex.printStackTrace();
        	 return "";
         }
         Log.i("-xiaomo-", "save picture to " + file.getAbsolutePath());
         return file.getAbsolutePath();
    }
	public static String writeBitmap2File(Bitmap bitmap,int mode,int[] inRect) 
	{
		ByteArrayOutputStream baos = null;
		String _subdir=null;
		String _fname = null;
		String _path = null;
		try
		{
			if(mode == 0)
				_subdir = g_szSavePath + "/PreviewImages/";
			else
				_subdir = g_szSavePath + "/PictureImages/";
			File dir = new File(Environment.getExternalStorageDirectory().toString(), _subdir); 
	        if (!dir.exists()) 
	        {
	            if (!dir.mkdirs()) 
	             {
	                 Log.d("App", "failed to create directory");
	                 return "";
	             }            
	        }
	        int nWidth = bitmap.getWidth();
	        int nHeight = bitmap.getHeight();
	        String szTag = "_"+String.valueOf(nWidth)+"_"+String.valueOf(nHeight)+"_"+String.valueOf(inRect[0])+"_"+String.valueOf(inRect[1])+"_"+String.valueOf(inRect[2])+"_"+String.valueOf(inRect[3]);
	        _fname =  getCurTimeString() + szTag+ ".jpg";
	        _path = dir.getAbsolutePath()+_fname;
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 95, baos);
			byte[] photoBytes = baos.toByteArray();
			if (photoBytes != null) 
			{
				new FileOutputStream(new File(dir.getAbsolutePath(), _fname)).write(photoBytes);
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if (baos != null)
					baos.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return _path;
	}
	public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
          Matrix matrix = new Matrix();
          matrix.postRotate(angle);
          return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
	public static Bitmap getColorOrgBitmap(byte[] data, int width, int height) {
		
		Bitmap bmp=null;
		try{
          YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
          if(image!=null){
        	  Rect rc = new Rect(0, 0, width, height);
              ByteArrayOutputStream stream = new ByteArrayOutputStream();
              image.compressToJpeg(rc, 95, stream);
              bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
              stream.close();
          	}
		}
		catch(Exception ex)
		{
			Log.e("Sys","Error:"+ex.getMessage());
		}
		
		return bmp;
	}
	public static Bitmap getGrayOrgBitmap(byte[] data, int width, int height) 
	{
		int[] pixels = new int[width * height];
		byte[] yuv = data;
		int inputOffset = 0;

		for (int y = 0; y < height; y++) {
			int outputOffset = y * width;
			for (int x = 0; x < width; x++) {
				//pixels[outputOffset + x] = yuv[inputOffset + x];
		    	int grey = yuv[inputOffset + x] & 0xff;
		        pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
			}
			inputOffset += width;
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	public static String getCurTimeString()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String currentDateandTime = sdf.format(new Date());
		return currentDateandTime;
	}
    public static void initialize(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        sPixelDensity = metrics.density;
    }
    public static int dpToPixel(int dp)
    {
        return Math.round(sPixelDensity * dp);
    }
    
    public static int getScreenOrientation(Context con)
    {
    	return ((Activity)con).getResources().getConfiguration().orientation;
    }
    
    static private Toast prev_toast = null;
	public static synchronized void outputToast(Context con, String text) {
		if (con != null) {
			if (prev_toast != null)
				prev_toast.cancel();

			prev_toast = Toast.makeText(con, text, Toast.LENGTH_SHORT);
			prev_toast.show();
		}
	}
}

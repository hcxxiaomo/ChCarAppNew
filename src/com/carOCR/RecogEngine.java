package com.carOCR;

import com.szOCR.general.CGlobal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

public class RecogEngine{
	private static final String TAG = "RecogEngine";
	// JNI Interface
	public static native String doGetDevId(Context actObj);
	public static native boolean doVerifyKey(String devkey,String verkey);
	
	public native int doRecogBitmap(Bitmap bitmap,int rot,String devkey,String verkey);
	public native int doRecogGrayImg(byte[] data,int width,int height,int rot,int provinceId,String devkey,String verkey);
	public native int[] doGetRecogData(int index,char[] strNumbers,char[] strColor);

	
	static {
		System.loadLibrary("chCarRecog");
		//System.loadLibrary("barcodeRecog");
	}
	
	RecogResult		m_result = null;
	Bitmap			m_orgBitmap = null;
	public RecogResult getRecgResult(){
		return m_result;
	}
	public Bitmap getRecgBitmap(){
		return m_orgBitmap;
	}
	
	public RecogResult recogBitmap(Bitmap bitmap,int rot)
	{
		m_result = null;
		int numCars = doRecogBitmap(bitmap,rot,CGlobal.g_devicekey,CGlobal.g_verifykey);
		if(numCars == 0) return null;
		m_result = makeRecogResultFromBitmap(bitmap,rot,numCars);
		return m_result; 
	}
	public RecogResult RecogGrayImg(byte[] data,int width,int height,int rot)
	{
		m_result = null;
//		byte[] dst = new byte[data.length];
//		int[] wh = new int[2];
//		CGlobal.Yuv420spRotate(dst, data, width, height,rot,wh);
//		width = wh[0]; height = wh[1];
//		int numCars = doRecogGrayImg(dst,width,height,0);
		int numCars = doRecogGrayImg(data,width,height,rot,CGlobal.g_provinceId,CGlobal.g_devicekey,CGlobal.g_verifykey);
		if(numCars == 0) return null;
		m_result = makeRecogResultFromData(data,width,height,rot,numCars);
		return m_result;

	}
	private RecogResult makeRecogResultFromBitmap(Bitmap bitmap,int rot,int numCars)
	{
		char[] rstData = new char[256];
		char[] rstColor = new char[256];
		int len1,len2,i;
		RecogResult result = new RecogResult(); 
		for (i=0;i<numCars;i++)
		{
			int rect[] = doGetRecogData(i,rstData,rstColor);
			len1 = CGlobal.getByteLength(rstData,256);
			len2 = CGlobal.getByteLength(rstColor,256);
			result.m_szRecogTxt[i] = new String(rstData,0,len1);//,"UTF-8");
			result.m_nRecogColor[i] = new String(rstColor,0,len2);//,"UTF-8");
			result.m_rcRecogRect[i] =new Rect(rect[0], rect[1], rect[2], rect[3]);
			result.m_nRecogType[i] = rect[4];
			result.m_nRecogDis[i] = rect[5];
		}
		if(CGlobal.g_iFrameRotation == 0)
		{
			m_orgBitmap = bitmap;
		}
		else
		{
			if(CGlobal.g_iFrameRotation == 90)
				m_orgBitmap = CGlobal.RotateBitmap(bitmap, 90);
			else if(CGlobal.g_iFrameRotation == 180)
				m_orgBitmap = CGlobal.RotateBitmap(bitmap, 180);
			else if(CGlobal.g_iFrameRotation == 270)
				m_orgBitmap = CGlobal.RotateBitmap(bitmap, 270);
		}
		return result;
	}
	private RecogResult makeRecogResultFromData(byte[] data,int width,int height,int rot,int numCars)
	{
		char[] rstData = new char[256];
		char[] rstColor = new char[256];
		int len1,len2,i;
		Log.d(TAG, "makeRecogResultFromData");
		RecogResult result = new RecogResult(); 
		for (i=0;i<numCars;i++)
		{
			int rect[] = doGetRecogData(i,rstData,rstColor);
			len1 = CGlobal.getByteLength(rstData,256);
			len2 = CGlobal.getByteLength(rstColor,256);
			result.m_szRecogTxt[i] = new String(rstData,0,len1);//,"UTF-8");
			result.m_nRecogColor[i] = new String(rstColor,0,len2);//,"UTF-8");
			result.m_rcRecogRect[i] =new Rect(rect[0], rect[1], rect[2], rect[3]);
			result.m_nRecogType[i] = rect[4];
			result.m_nRecogDis[i] = rect[5];
		}
		if(CGlobal.g_iFrameRotation == 0){
			m_orgBitmap = CGlobal.getColorOrgBitmap(data, width, height);
		}
		else{
			Bitmap orgbitmap= CGlobal.getColorOrgBitmap(data, width, height);
			if(CGlobal.g_iFrameRotation == 90)
				m_orgBitmap = CGlobal.RotateBitmap(orgbitmap, 90);
			else if(CGlobal.g_iFrameRotation == 180)
				m_orgBitmap = CGlobal.RotateBitmap(orgbitmap, 180);
			else if(CGlobal.g_iFrameRotation == 270)
				m_orgBitmap = CGlobal.RotateBitmap(orgbitmap, 270);
		}
		return result;
	}
}
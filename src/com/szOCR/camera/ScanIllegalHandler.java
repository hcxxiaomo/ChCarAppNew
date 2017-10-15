
package com.szOCR.camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.carOCR.RecogResult;
import com.carOCR.activity.ScanActivity;
import com.carOCR.activity.ScanIllegalActivity;
import com.szOCR.general.CGlobal;
import com.szOCR.general.Defines;
import com.xiaomo.chcarappnew.R;

/* This class handles all the messaging which comprises the state machine for capture. */
public final class ScanIllegalHandler extends Handler {

	private static final String TAG = ScanIllegalHandler.class.getSimpleName();

	private final ScanIllegalActivity mActivity;
	private final CameraPreview m_CameraPreview;
	private State state;
	 
	private enum State {PREVIEW, SUCCESS, DONE}

	public ScanIllegalHandler(ScanIllegalActivity activity,CameraPreview cameraPreview) 
	{
		this.mActivity = activity;
		this.m_CameraPreview = cameraPreview;
		state = State.SUCCESS;
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) 
	{
		switch (message.what)
		{
			case R.id.auto_focus:
				// Log.d(TAG, "Got auto-focus message");
				// When one auto focus pass finishes, start another. This is the closest thing to continuous AF. 
				// It does seem to hunt a bit, but I'm not sure what else to do.
				//if (state == State.PREVIEW) {
				//	mCameraPreview.requestAutoFocus(this, R.id.auto_focus);
				//}
				m_CameraPreview.autoCameraFocuse();
				break;
			case R.id.force_focus:
				m_CameraPreview.forceCameraFocuse();
				break;
			case R.id.restart_preview:
				Log.d(TAG, "Got restart preview message");
				restartPreviewAndDecode();
				break;
			case R.id.recog_succeeded:
				Log.d(TAG, "Got decode succeeded message");
				state = State.SUCCESS;
				Bundle bundle = message.getData();				
				mActivity.returnRecogedData((RecogResult) message.obj);	
				
				if(CGlobal.g_runMode == Defines.RUNMODE_NONRECORD)
				{
					m_CameraPreview.setPreviewCallback(m_CameraPreview);
				}				
				break;
			case R.id.recog_failed:
				// We're decoding as fast as possible, so when one decode fails, start another.
				state = State.PREVIEW;				
				if(CGlobal.g_runMode == Defines.RUNMODE_NONRECORD)
				{
					m_CameraPreview.setPreviewCallback(m_CameraPreview);
				}
				
				break;
			case R.id.return_scan_result:
				Log.d(TAG, "Got return scan result message");
				mActivity.setResult(Activity.RESULT_OK, (Intent) message.obj);
				mActivity.finish();
				break;
			case R.id.launch_product_query:
				Log.d(TAG, "Got product query message");
				String url = (String) message.obj;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				mActivity.startActivity(intent);
				break;
		}
	}

	public void quitSynchronously() 
	{
		state = State.DONE;		
		
		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.recog_succeeded);
		removeMessages(R.id.recog_failed);
		removeMessages(R.id.auto_focus);
	}

	private void restartPreviewAndDecode() 
	{
		if (state == State.SUCCESS) 
		{
			state = State.PREVIEW;
		}
	}

}

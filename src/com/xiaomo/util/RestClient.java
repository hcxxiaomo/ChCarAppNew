package com.xiaomo.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestClient {
//	 private static final String BASE_URL = "http://127.0.0.1:5333/carplatenumber/";//127.0.0.1:5333
	 private static final String BASE_URL = "http://117.27.138.166:8080/carplatenumber/";//127.0.0.1:5333

	  private static AsyncHttpClient client = new AsyncHttpClient();

	  public static void get(String server, String port, String dir ,String relativeUrl , RequestParams params, AsyncHttpResponseHandler responseHandler) {
		  client.get(getAbsoluteUrl( server,  port,  dir , relativeUrl), params, responseHandler);
	  }
	  
	  public static void post(String server, String port, String dir , String relativeUrl , RequestParams params, AsyncHttpResponseHandler responseHandler) {
		  client.post(getAbsoluteUrl( server,  port,  dir ,relativeUrl), params, responseHandler);
	  }
	  
	  private static String getAbsoluteUrl(String server, String port, String dir ,String relativeUrl) {
		  if (dir.length() > 0 ) {
			  return "http://".concat(server).concat(":").concat(port).concat("/").concat(dir).concat("/").concat(relativeUrl);
		  }else{
			  return "http://".concat(server).concat(":").concat(port).concat("/").concat(relativeUrl);
		  }
	  }
	  
	  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	  }

	  private static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	  }
}

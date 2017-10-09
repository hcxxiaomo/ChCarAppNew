package com.szOCR.general;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;

public class ImageProcessing 
{

	public static final	int				ROTATE_0	= 0;
	public static final	int				ROTATE_90	= 90;
	public static final	int				ROTATE_180	= 180;
	public static final	int				ROTATE_270	= 270;
	
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
	
	 public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) 
     {
         byte [] yuv = new byte[imageWidth*imageHeight*3/2];
         // Rotate the Y luma
         int i = 0;
         for(int x = 0;x < imageWidth;x++)
         {
             for(int y = imageHeight-1;y >= 0;y--)                               
             {
                 yuv[i] = data[y*imageWidth+x];
                 i++;
             }
         }
         // Rotate the U and V color components 
         i = imageWidth*imageHeight*3/2-1;
         for(int x = imageWidth-1;x > 0;x=x-2)
         {
             for(int y = 0;y < imageHeight/2;y++)                                
             {
                 yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+x];
                 i--;
                 yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
                 i--;
             }
         }
         return yuv;
     }
}

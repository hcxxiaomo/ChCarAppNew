package com.carOCR;

import android.graphics.Rect;

public class RecogResult 
{
	public final int MAX_RESULT_COUNT = 10; 
	public int    m_nResultCount;

	public String m_szRecogTxt[];//platenumber
	public Rect   m_rcRecogRect[];//plate location
	public String m_nRecogColor[];//plate background color
	public int    m_nRecogType[];
	public double m_nRecogDis[];
	
	public RecogResult()
	{
		m_nResultCount = 0;
		m_szRecogTxt  = new String[MAX_RESULT_COUNT];
		m_rcRecogRect = new Rect[MAX_RESULT_COUNT];
		m_nRecogColor = new String[MAX_RESULT_COUNT];
		m_nRecogType  = new int[MAX_RESULT_COUNT];
		m_nRecogDis   = new double[MAX_RESULT_COUNT];
	}
	
	public RecogResult(int nCount)
	{
		m_nResultCount = nCount;
		m_szRecogTxt   = new String[m_nResultCount];
		m_rcRecogRect  = new Rect[m_nResultCount];
		m_nRecogColor  = new String[m_nResultCount];
		m_nRecogType   = new int[m_nResultCount];
		m_nRecogDis    = new double[m_nResultCount];
	}
}

package com.chyss.videoplaydemo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.text.TextUtils.StringSplitter;

/**
 * 时间转换工具类
 * 
 * 1. 获取当前年、月、日、日期
 * 2. 把milliseconds转换为00:00时间显示
 * 3. 通过年月定位月份天数，某年某月有多少天
 * 
 * @author qinchuo 2016-2-16
 * 
 */
public class TimeUtils
{
	/**
	 * 把milliseconds转换为00:00时间显示
	 * 
	 * @param milliseconds
	 * @return 00:00
	 */
	public static String changeToTimeStr(long milliseconds)
	{
		long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
		long minus = seconds / 60;
		long second = seconds % 60;
		if (second < 10)
		{
			return minus + ":0" + second;
		}
		else
		{
			return minus + ":" + second;
		}
	}

	/**
	 * 通过年月定位月份天数
	 * 
	 * @param dyear
	 * @param dmouth
	 * @return 某年某月有多少天
	 */
	public static int calDayByYearAndMonth(int dyear, int dmouth)
	{
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
		Calendar rightNow = Calendar.getInstance();
		try
		{
			rightNow.setTime(simpleDate.parse(dyear + "/" + dmouth));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);// 根据年月获取月份天数
	}

	/**
	 * 获取当前年份
	 * 
	 * @return 年份
	 */
	public static int getCurrenYear()
	{
		Calendar cd = Calendar.getInstance();
		int curr_year = cd.get(Calendar.YEAR);
		return curr_year;
	}

	/**
	 * 获取当前月份
	 * 
	 * @return 月份 
	 */
	public static int getCurrenMonth()
	{
		Calendar cd = Calendar.getInstance();
		int curr_month = cd.get(Calendar.MONTH) + 1;
		return curr_month;
	}
	
	/**
	 * 获取今天是本月第几天
	 * 
	 * @return 今天是本月第几天 
	 */
	public static int getCurrenday()
	{
		Calendar cd = Calendar.getInstance();
		int curr_day = cd.get(Calendar.DAY_OF_MONTH);
		return curr_day;
	}
	
	/**
	 * 获取当前日期
	 * 
	 * @param spli : 日期分割符合（如：“-”），传null为无分割
	 * @return 日期
	 */
	public static String getCurrendate(String spli)
	{
		Calendar cd = Calendar.getInstance();
		int curr_year = cd.get(Calendar.YEAR);
		int curr_month = cd.get(Calendar.MONTH) + 1;
		int curr_day = cd.get(Calendar.DAY_OF_MONTH);
		
		//使用StringBuffer减少内存消耗
		StringBuffer sb = new StringBuffer();
		
		if (spli != null)
		{
			sb.append(curr_year);
			sb.append(spli);
			sb.append(curr_month);
			sb.append(spli);
			sb.append(curr_day);
		}
		else
		{
			sb.append(curr_year);
			sb.append(curr_month);
			sb.append(curr_day);
		}
		return sb.toString();
	}
}

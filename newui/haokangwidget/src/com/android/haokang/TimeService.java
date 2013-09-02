package com.android.haokang;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class TimeService extends Service {
    private static List<News> li=new ArrayList<News>();
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		//Log.v("sinanews", "sinanews service fffffff");
//		String path = "http://news.yahoo.com/rss/";//英文
//		String path = "http://sports.cn.yahoo.com/rss/cn_news_world.xml";//中文
//		String path = "http://news.yahoo.com/rss/sports";
	    /* 调用getRss()取得解析后的List */
		//CharSequence ch=DateFormat.format("hh:mm:ss", time.toMillis(false));
//		sportsswidget.getRss(path);
		AppWidgetManager manager=AppWidgetManager.getInstance(this);
		RemoteViews views=widgethaokang.getTimeView(this);
		int[] appids=manager.getAppWidgetIds(new ComponentName(this, widgethaokang.class));
		manager.updateAppWidget(appids, views);
	    /* 调用getRss()取得解析后的List */
		//获取当前时间
		long now =System.currentTimeMillis();
		int updateRateSeconds = 30;
		long unit=updateRateSeconds*1000;//间隔一秒

		PendingIntent pintent=PendingIntent.getService(this, 0, intent, 0);
		
		//计时器
		AlarmManager alarm=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		//AlarmManager.RTC_WAKEUP设置服务在系统休眠时同样会运行
		//第二个参数是下一次启动service时间
		alarm.set(AlarmManager.RTC_WAKEUP, now+unit, pintent);
	}
}

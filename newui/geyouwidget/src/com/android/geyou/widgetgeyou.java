package com.android.geyou;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class widgetgeyou extends AppWidgetProvider {
	//private static Time time=new Time();
		private static List<News> li=new ArrayList<News>();
		private static URL url = null;
		@Override
		public void onUpdate(Context context, AppWidgetManager appWidgetManager,
				int[] appWidgetIds) {
			// TODO Auto-generated method stub
			super.onUpdate(context, appWidgetManager, appWidgetIds);
			if(li!=null&&li.size()!=0){
				
				RemoteViews views=getTimeView(context);
				appWidgetManager.updateAppWidget(appWidgetIds, views);
			}
			
			context.startService(new Intent(context,TimeService.class));
			
			//Log.v("sinanews", "sinanews service nnnnnnnnn");
		}
	    /** Called when the activity is first created. */
		public static RemoteViews getTimeView(Context context){
				 //String path = "http://news.yahoo.com/rss/sports/";
				 //String path = "http://chinatimes.feedsportal.com/c/33012/f/537199/index.rss";
				 String path = "http://rss.5286phone.com/4.xml";
			    /* ����getRss()ȡ�ý������List */
				//CharSequence ch=DateFormat.format("hh:mm:ss", time.toMillis(false));
			 getRss(path);
			 
			RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.newswidget);
			//time.setToNow();
			//ʱ���ʽ��
		    
			String currentInfo1 = null;
			String currentInfo2 = null;
			String currentInfo3 = null;
			String currentlink1 = null;
			String currentlink2 = null;
			String currentlink3 = null;
			if(li!=null&&li.size()>0)
			{	
			//zdd eboda 20120714
			 // int i = new java.util.Random().nextInt(li.size()-4);
			  int i = new java.util.Random().nextInt(li.size()-4)+1;
			 // currentInfo1 = li.get(i).getTitle();
			 // currentInfo2 = li.get(i+1).getTitle();
			 // currentInfo3 = li.get(i+2).getTitle();
			  currentInfo1 = li.get(0).getTitle();
			  currentInfo2 = li.get(i).getTitle();
			  currentInfo3 = li.get(i+1).getTitle();
			  
			 // currentlink1 = li.get(i).getLink();
			 // currentlink2 = li.get(i+1).getLink();
			 // currentlink3 = li.get(i+2).getLink();
			   currentlink1 = li.get(0).getLink();
			   currentlink2 = li.get(i).getLink();
			   currentlink3 = li.get(i+1).getLink();
			 
			  //Log.v("sinanews", "sinanews service cccccccc"+currentInfo1);
			  //Log.v("sinanews", "sinanews service link   kkkkkkkkkk"+currentlink1);
			  Uri uri1 = Uri.parse(currentlink1);
			  Intent intent1 = new Intent(Intent.ACTION_VIEW,uri1);
		      PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, 0);
			  views.setOnClickPendingIntent(R.id.newstitle1, pendingIntent1);
			  Uri uri2 = Uri.parse(currentlink2);
			  Intent intent2 = new Intent(Intent.ACTION_VIEW,uri2);
		      PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
			  views.setOnClickPendingIntent(R.id.newstitle2, pendingIntent2);
			  Uri uri3 = Uri.parse(currentlink3);
			  Intent intent3 = new Intent(Intent.ACTION_VIEW,uri3);
		      PendingIntent pendingIntent3 = PendingIntent.getActivity(context, 0, intent3, 0);
//			  views.setOnClickPendingIntent(R.id.newstitle1, pendingIntent1);
//			  views.setOnClickPendingIntent(R.id.newstitle2, pendingIntent2);
			  views.setOnClickPendingIntent(R.id.newstitle3, pendingIntent3);
			  views.setTextViewText(R.id.newstitle1, currentInfo1);
		  	  views.setTextViewText(R.id.newstitle2, currentInfo2);
			  views.setTextViewText(R.id.newstitle3, currentInfo3);
			  
			}
			return views;
		}
		 /* ����XML�ķ��� */
		public  static List<News> getRss(String path)
		  {
		    //List<News> data=new ArrayList<News>();
			if(li!=null){li.clear();}
			if(li==null){li=new ArrayList<News>();}
		    URL url = null;
		    try
		    {  
		      url = new URL(path);
		      /* ����SAXParser���� */ 
		      SAXParserFactory spf = SAXParserFactory.newInstance();
		      SAXParser sp = spf.newSAXParser(); 
		      /* ����XMLReader���� */ 
		      XMLReader xr = sp.getXMLReader(); 
		      /* �����Զ����MyHandler��XMLReader */ 
		      MyHandler myExampleHandler = new MyHandler(); 
		      //�鿴���
		      /*
		      InputStreamReader isr = new InputStreamReader(url.openStream(),"GBK");
		      
		      char[] buf =new char[1024]; 
		      while(isr.read(buf)!=-1){
		      
		      Log.v("yk","ppppp"+new String(buf));
		      
		      }
		     
		      */
		      /* ����XML */
		      
		      xr.setContentHandler(myExampleHandler);  
		  	InputStreamReader isr1 =new InputStreamReader(url.openStream(),"UTF-8");
        	InputSource is=new InputSource(isr1);
//		      xr.parse(new InputSource(url.openStream()));
        	xr.parse(is);
		      /* ȡ��RSS�����������б� */
		      
		      li =myExampleHandler.getParsedData(); 

		    }
		    catch (Exception e)
		    {
		      /* �������ʱ����result����һ��activity */
//		      Intent intent=new Intent();
//		      Bundle bundle = new Bundle();
//		      bundle.putString("error",""+e);
//		      intent.putExtras(bundle);
//		      /* ����ķ���ֵ����Ϊ99 */
//		      EX08_13_1.this.setResult(99, intent);
//		      EX08_13_1.this.finish();
		      //Toast.makeText(this, "ϵͳ���󣡣�", Toast.LENGTH_SHORT).show();
		    }
		    return li;
		  }
		@Override
		public void onDeleted(Context context, int[] appWidgetIds) {
			// TODO Auto-generated method stub
			super.onDeleted(context, appWidgetIds);
			AppWidgetManager manager=AppWidgetManager.getInstance(context);
			int[] appids=manager.getAppWidgetIds(new ComponentName(context, widgetgeyou.class));
			//Log.v("yy",""+appids.length);
			if(appids.length<1){//�����治���У�ֹͣ����
				context.stopService(new Intent(context,TimeService.class));
			}
		}
}

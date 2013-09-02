package qulip.revive;

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

public class widgetrevive extends AppWidgetProvider {
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
				 //中时电子报String path = "http://chinatimes.feedsportal.com/c/33012/f/537204/index.rss";
				 String path ="http://www.krtnews.com.tw/component/k2/itemlist/category/93?format=feed";

			    /* 调用getRss()取得解析后的List */
				//CharSequence ch=DateFormat.format("hh:mm:ss", time.toMillis(false));
			 getRss(path);
			 
			RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.newswidget);
			//time.setToNow();
			//时间格式化
		    
			String currentInfo1 = null;
			String currentInfo2 = null;
			String currentInfo3 = null;
			String currentlink1 = null;
			String currentlink2 = null;
			String currentlink3 = null;
			if(li!=null&&li.size()>0)
			{	
			  int i = new java.util.Random().nextInt(li.size()-4);
			  currentInfo1 = li.get(i).getTitle();
			  currentInfo2 = li.get(i+1).getTitle();
			  currentInfo3 = li.get(i+2).getTitle();
			  
			  currentlink1 = li.get(i).getLink();
			  currentlink2 = li.get(i+1).getLink();
			  currentlink3 = li.get(i+2).getLink();
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
		 /* 解析XML的方法 */
		public  static List<News> getRss(String path)
		  {
		    //List<News> data=new ArrayList<News>();
			if(li!=null){li.clear();}
			if(li==null){li=new ArrayList<News>();}
		    URL url = null;
		    try
		    {  
		      url = new URL(path);
		      /* 产生SAXParser对象 */ 
		      SAXParserFactory spf = SAXParserFactory.newInstance();
		      SAXParser sp = spf.newSAXParser(); 
		      /* 产生XMLReader对象 */ 
		      XMLReader xr = sp.getXMLReader(); 
		      /* 设置自定义的MyHandler给XMLReader */ 
		      MyHandler myExampleHandler = new MyHandler(); 
		      //查看数据
		      /*
		      InputStreamReader isr = new InputStreamReader(url.openStream(),"GBK");
		      
		      char[] buf =new char[1024]; 
		      while(isr.read(buf)!=-1){
		      
		      Log.v("yk","ppppp"+new String(buf));
		      
		      }
		     
		      */
		      /* 解析XML */
		      
		      xr.setContentHandler(myExampleHandler);  
		  	InputStreamReader isr1 =new InputStreamReader(url.openStream(),"UTF-8");
        	InputSource is=new InputSource(isr1);
//		      xr.parse(new InputSource(url.openStream()));
        	xr.parse(is);
		      /* 取得RSS标题与内容列表 */
		      
		      li =myExampleHandler.getParsedData(); 

		    }
		    catch (Exception e)
		    {
		      /* 发生错误时返回result回上一个activity */
//		      Intent intent=new Intent();
//		      Bundle bundle = new Bundle();
//		      bundle.putString("error",""+e);
//		      intent.putExtras(bundle);
//		      /* 错误的返回值设置为99 */
//		      EX08_13_1.this.setResult(99, intent);
//		      EX08_13_1.this.finish();
		      //Toast.makeText(this, "系统错误！！", Toast.LENGTH_SHORT).show();
		    }
		    return li;
		  }
		@Override
		public void onDeleted(Context context, int[] appWidgetIds) {
			// TODO Auto-generated method stub
			super.onDeleted(context, appWidgetIds);
			AppWidgetManager manager=AppWidgetManager.getInstance(context);
			int[] appids=manager.getAppWidgetIds(new ComponentName(context, widgetrevive.class));
			//Log.v("yy",""+appids.length);
			if(appids.length<1){//当桌面不再有，停止服务
				context.stopService(new Intent(context,TimeService.class));
			}
		}
}

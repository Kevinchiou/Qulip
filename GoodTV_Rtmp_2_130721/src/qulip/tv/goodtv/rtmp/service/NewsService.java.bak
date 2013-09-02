package qulip.tv.goodtv.rtmp.service;

import qulip.tv.goodtv.rtmp.Const;
import qulip.tv.goodtv.rtmp.util.HttpUtils;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class NewsService extends Service {
	
	public static final String MY_ACTION = "tv.goodtv.rtmp.newsservice";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		thread.start();

		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	private void sendResult(String imageFile) {
		Intent i = new Intent(MY_ACTION);
		Bundle b = new Bundle();
		b.putBoolean("success", imageFile != null && imageFile.trim().length() > 0);
		b.putString("imageFile", imageFile);
		i.putExtras(b);
		sendBroadcast(i);
	}
	
	private Runnable queryNews = new Runnable() {
		@Override
		public void run() {
			try {
				String url = "http://www.goodtv.tv/mobile/news";
				String html = HttpUtils.readString(url);
				int idx = html.indexOf("http");
				String imageFile = html.substring(idx, html.indexOf("\"", idx));
				Log.d(Const.APP_TAG, "the imageFile:" + imageFile);
				sendResult(imageFile);
			} catch (Exception e) {
				sendResult(null);
			}
		}
	};

	Thread thread = new Thread(queryNews);
}

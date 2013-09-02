package qulip.tv.goodtv.rtmp.service;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import qulip.tv.goodtv.rtmp.Const;
import qulip.tv.goodtv.rtmp.util.HttpUtils;
import qulip.tv.goodtv.rtmp.vo.AudioVO;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class QueryAudioService extends Service {
	
	public static final String MY_ACTION = "tv.goodtv.rtmp.queryaudioservice";

	private int type;
	private String id;
	
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
		type = intent.getExtras().getInt("type");
//		id = intent.getExtras().getString("id");
	}
	
	private void addAudio(AudioVO audio) {
		Intent i = new Intent(MY_ACTION);
		Bundle b = new Bundle();
		b.putInt("type", 1);
		b.putString("id", audio.getId());
		b.putString("title", audio.getTitle());
		b.putString("description", audio.getDescription());
		b.putString("updated", audio.getUpdated());
		b.putString("uploaded", audio.getUploaded());
		i.putExtras(b);
		sendBroadcast(i);
	}
	
	private void sendFind(boolean find) {
		Intent i = new Intent(MY_ACTION);
		Bundle b = new Bundle();
		b.putInt("type", 2);
		b.putBoolean("find", find);
		i.putExtras(b);
		sendBroadcast(i);
	}
	
	private void sendMsg(String err) {
		Intent i = new Intent(MY_ACTION);
		Bundle b = new Bundle();
		b.putInt("type", 9);
		b.putString("msg", err);
		i.putExtras(b);
		sendBroadcast(i);
	}
	
	private Runnable queryAudioServer = new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					String url;
					Log.d(Const.APP_TAG, "start to query type:" + type);

			//		url = "http://210.65.11.237/audio/index.php/service";
					url = "http://w2.goodtv.tv/audio/index.php/service/index";
					
					Log.d(Const.APP_TAG, "start to query audio url:" + url);
					boolean find = false;
					while(true) {
						String json = HttpUtils.readString(url);
						JSONArray items = new JSONArray(json);					
						Log.d(Const.APP_TAG, "totalItems: " + items.length());
						for (int i=0; i<items.length(); i++) {
							find = true;
							JSONObject item = items.getJSONObject(i);
					
							AudioVO audio = new AudioVO();
							audio.setId(item.getString("code"));
							audio.setTitle(item.getString("topic"));
							audio.setDescription(item.getString("describe"));
							audio.setUploaded(item.getString("create"));
							audio.setUpdated(item.getString("modify"));
							addAudio(audio);
						}
						break;//只撈一次
					}
				    sendFind(find);
					break;
				} catch (Exception e) {
					Log.e(Const.APP_TAG, e.toString(), e);
					sendMsg("err:" + e.toString());
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
						break;
					}
				}
			}
		}
	};
	
	Thread thread = new Thread(queryAudioServer);
}

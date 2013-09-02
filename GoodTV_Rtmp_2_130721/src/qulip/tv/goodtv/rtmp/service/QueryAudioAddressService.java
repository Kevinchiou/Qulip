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

public class QueryAudioAddressService extends Service {
	
	public static final String MY_ACTION = "tv.goodtv.rtmp.queryaudioaddrservice";

	private int type;
	private String id;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		thread.start();
		super.onStart(intent, startId);
	}
	
	private void addAudioAddr(String audioStreamAddr,String mediaType) {
		Intent i = new Intent(MY_ACTION);
		Bundle b = new Bundle();
		b.putInt("msgtype", 1);
		b.putString("AudioAddr", audioStreamAddr);
		b.putString("MediaType", mediaType);
		i.putExtras(b);
		sendBroadcast(i);
	}
	
	
	private void sendMsg(String err) {
		Intent i = new Intent(MY_ACTION);
		Bundle b = new Bundle();
		b.putInt("msgtype", 9);
		b.putString("msg", err);
		i.putExtras(b);
		sendBroadcast(i);
	}
	
	private Runnable queryAudioAddressServer = new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					String url;
					url = "http://w2.goodtv.tv/audio/index.php/service/audioStream";
					Log.d(Const.APP_TAG, "start to query audio addr url:" + url);
					
					for (int i = 0; i < 30; i++) {
						String json = HttpUtils.readString(url);
						JSONObject root = new JSONObject(json);						
						String audioStreamAddr = root.getString("audioStreamRoot");
						String mediaType = root.getString("type");				
						addAudioAddr(audioStreamAddr,mediaType);
						Thread.sleep(1000);
					}

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
	
	Thread thread = new Thread(queryAudioAddressServer);
}

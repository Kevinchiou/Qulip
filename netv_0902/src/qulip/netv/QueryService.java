package qulip.netv;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.netv.vo.VideoVO;

public class QueryService extends Service {
	
	public static final String MY_ACTION = "netv.query.service";

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
		if (type == 2) {
			id = intent.getExtras().getString("id");
		}
	}
	
	private void addVideo(VideoVO video) {
		Intent i = new Intent(MY_ACTION);
		Bundle b = new Bundle();
		b.putInt("type", 1);
		b.putString("id", video.getId());
		b.putString("title", video.getTitle());
		b.putString("description", video.getDescription());
		b.putString("sqDefault", video.getSqDefault());
		b.putString("updated", video.getUpdated());
		b.putString("uploaded", video.getUploaded());
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
	
	private Runnable queryYoutube = new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					String url;
					Log.d(Const.APP_TAG, "start to query type:" + type);
					if (type == 2) {
						url = "http://gdata.youtube.com/feeds/api/playlists/" + id + "?v=2&alt=jsonc&orderby=published&max-results=50&start-index=";
					} else {
						String keyword = DataSet.getInstace().getKeyword();
						url = "http://gdata.youtube.com/feeds/api/videos?v=2&alt=jsonc&max-results=50&orderby=published&author=netv203&q=" + URLEncoder.encode(keyword, "utf-8") + "&start-index=";
					}
					Log.d(Const.APP_TAG, "start to query url:" + url);
					int idx = 1;
					boolean find = false;
					while(true) {
						Log.d(Const.APP_TAG, "fetch idx: " + idx);
						if (type == 3 && idx >= 100) {
							break;
						}
						String json = FetchService.readJSON(idx, url + idx);
						Log.d(Const.APP_TAG, "fetch idx: " + idx + " json:" + json);
						JSONObject root = new JSONObject(json);
						JSONObject data = root.getJSONObject("data");
						
						int totalItems = data.getInt("totalItems");
						int startIndex = data.getInt("startIndex");
						int itemsPerPage = data.getInt("itemsPerPage");
						
						Log.d(Const.APP_TAG, "totalItems: " + totalItems);
						
						if (data.isNull("items")) {
							break;
						}
						
						JSONArray items = data.getJSONArray("items");
						for (int i=0; i<items.length(); i++) {
							find = true;
							JSONObject item = items.getJSONObject(i);
							if (type == 2) {
								item = item.getJSONObject("video");
							}
							VideoVO vo = new VideoVO();
							vo.setId(item.getString("id"));
							vo.setTitle(item.getString("title"));
							vo.setDescription(item.getString("description"));
							vo.setSqDefault(item.getJSONObject("thumbnail").getString("sqDefault"));
							
							vo.setUploaded(item.getString("uploaded"));
							vo.setUpdated(item.getString("updated"));
							addVideo(vo);
						}
						
						if (startIndex + itemsPerPage > totalItems) {
							break;
						}
						idx += itemsPerPage;
					}
					sendFind(find);
					break;
				} catch (Exception e) {
					Log.e(Const.APP_TAG, e.toString(), e);
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
						break;
					}
				}
			}
		}
	};

	Thread thread = new Thread(queryYoutube);
}

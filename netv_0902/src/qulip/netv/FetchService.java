package qulip.netv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import com.netv.vo.CatalogVO;
import com.netv.vo.PlaylistVO;

public class FetchService extends Service {

	private int count;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						//1. 抓取所有的 playlists
						Log.d(Const.APP_TAG, "抓取 playlists..");
						String url = "http://gdata.youtube.com/feeds/api/users/netv203/playlists?v=2&alt=jsonc&max-results=50&start-index=";
						List<PlaylistVO> playlists = new ArrayList<PlaylistVO>();
						int idx = 1;
						while(true) {
							String json = readJSON(idx, url + idx);
							JSONObject root = new JSONObject(json);
							JSONObject data = root.getJSONObject("data");
							
							int totalItems = data.getInt("totalItems");
							int startIndex = data.getInt("startIndex");
							int itemsPerPage = data.getInt("itemsPerPage");
							
							JSONArray items = data.getJSONArray("items");
							for (int i=0; i<items.length(); i++) {
								JSONObject item = items.getJSONObject(i);
								PlaylistVO vo = new PlaylistVO();
								vo.setId(item.getString("id"));
								vo.setCreated(item.getString("created"));
								vo.setUpdated(item.getString("updated"));
								vo.setAuthor(item.getString("author"));
								vo.setTitle(item.getString("title"));
								vo.setDescription(item.getString("description"));
								vo.setSize(item.getInt("size"));
								if (item.has("thumbnail")) {
									vo.setSqDefault(item.getJSONObject("thumbnail").getString("sqDefault"));
									vo.setHqDefault(item.getJSONObject("thumbnail").getString("hqDefault"));
								}
								DataSet.getInstace().getPlaylistMap().put(vo.getId(), vo);
								playlists.add(vo);
							}
							
							if (startIndex + itemsPerPage > totalItems) {
								break;
							}
							idx += itemsPerPage;
						}
						Log.d(Const.APP_TAG, "抓取 playlists 共 " + playlists.size());
						
						//2. parse 分類
						List<CatalogVO> catalogs = DataSet.getInstace().getCatalogs();
						for (PlaylistVO playlist : playlists) {
							for (CatalogVO catalog : catalogs) {
								if (catalog.getPlaylistId() != null && catalog.getPlaylistId().length() > 0) {
									if (catalog.getPlaylistId().equals(playlist.getId())) {
										catalog.getPlaylists().add(playlist);
										if (playlist.getDescription() == null || playlist.getDescription().trim().length() == 0) {
											playlist.setDescription(catalog.getDesc());
										}
									}
								} else {
//									for (CatalogVO c : catalog.getSubs()) {
//										if (c.getPlaylistId().equals(playlist.getId())) {
//											catalog.getPlaylists().add(playlist);
//											//c.getPlaylists().add(playlist);
//											if (playlist.getDescription() == null || playlist.getDescription().trim().length() == 0) {
//												playlist.setDescription(catalog.getDesc());
//											}
//										}
//									}
								}
							}
						}
						for (CatalogVO catalog : catalogs) {
							if (catalog.getPlaylistId() != null && catalog.getPlaylistId().length() > 0) {
								continue;
							}
							for (CatalogVO c : catalog.getSubs()) {
								for (PlaylistVO playlist : playlists) {
									if (c.getPlaylistId().equals(playlist.getId())) {
										catalog.getPlaylists().add(playlist);
										//c.getPlaylists().add(playlist);
										if (playlist.getDescription() == null || playlist.getDescription().trim().length() == 0) {
											playlist.setDescription(catalog.getDesc());
										}
									}
								}
							}
						}
						
						
						DataSet.getInstace().setStatus(Const.STATUS_PLAYLIST_DATA_READY);
						return;
					} catch (Exception e) {
						Log.e(Const.APP_TAG, "WorkThread exception", e);
						DataSet.getInstace().setStatus(Const.STATUS_ERROR);
						DataSet.getInstace().setMessage("無法讀取撥放清單!");
					}
					//if (!DataSet.getInstace().isNetworkError()) {
					//	break;
					//} else {
						try {
							Thread.sleep(5000);
							DataSet.getInstace().reset();
							Log.d(Const.APP_TAG, "reset!");
						} catch (InterruptedException e) {
							break;
						}
					//}
				}
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(Const.APP_TAG, "service on destroy");
	}

	public int getCount() {
		return count;
	}
	
	public static String readJSON(int idx, String url) throws Exception {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(Const.APP_TAG, "Failed to download file");
				throw new Exception("Failed to download file");
			}
		} catch (Exception e) {
			DataSet.getInstace().setNetworkError(true);
			throw e;
		}
		return builder.toString();
	}
	
	public static Bitmap readICON(String url) throws Exception {
		HttpClient client = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				Bitmap icon = BitmapFactory.decodeStream(content);
				content.close();
				return icon;
			} else {
				Log.e(Const.APP_TAG, "Failed to download pic");
			}
		} catch (Exception e) {
			DataSet.getInstace().setNetworkError(true);
			throw e;
		}
		return null;
	}
}

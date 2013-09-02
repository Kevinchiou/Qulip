package qulip.netv;

import java.util.List;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import qulip.netv.R;
import com.netv.vo.CatalogVO;
import com.netv.vo.PlaylistVO;
import com.netv.vo.VideoVO;

public class Playlist extends ListActivity {
	
	private TextView infoText;
	
	private int type;
	private CatalogVO catalog;
	private PlaylistVO playlist;
	
	private PlaylistAdapter adapter;
	private MyReceiver myReceiver;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        
        type = this.getIntent().getExtras().getInt("type");
        
        findViews();

        //設定ListView未取得內容時顯示的view
        getListView().setEmptyView(findViewById(R.id.emptyView));

        Log.d(Const.APP_TAG, "type=" + type);
        if (type == 1) {
        	int idx = this.getIntent().getExtras().getInt("idx");
            catalog = DataSet.getInstace().getCatalog(idx);
	        infoText.setText(catalog.getTitle());

	        Log.d(Const.APP_TAG, "playlist size=" + catalog.getPlaylists().size());
	        
	        adapter = new PlaylistAdapter(this, catalog.getPlaylists());
        } else if (type == 2) {
        	String id = this.getIntent().getExtras().getString("id");
            playlist = DataSet.getInstace().getPlaylistMap().get(id);
            infoText.setText(playlist.getTitle());
            
            adapter = new PlaylistAdapter(this);
        } else {
        	//search
        	infoText.setText("搜尋：" + DataSet.getInstace().getKeyword());
        	
        	adapter = new PlaylistAdapter(this);
        }
        
        //自訂方法載入ListView值
        setListAdapter(adapter);
    }
    
    @Override
    protected void onStart() {
    	if (type != 1) {
    		//註冊BroadcastReceiver
	    	myReceiver = new MyReceiver();
	        IntentFilter intentFilter = new IntentFilter();
	        intentFilter.addAction(QueryService.MY_ACTION);
	        registerReceiver(myReceiver, intentFilter);
	        
	        //開啟服務
	        Intent intent = new Intent(Playlist.this, QueryService.class);
	        Bundle bundle = new Bundle();
			bundle.putInt("type", type);
			if (type == 2) {
				bundle.putString("id", playlist.getId());
			}
			intent.putExtras(bundle);
			Log.d(Const.APP_TAG, "will start service type:" + type);
	        startService(intent);
    	}
        super.onStart();
    }
    
    @Override
    protected void onStop() {
    	if (type != 1) {
    		unregisterReceiver(myReceiver);
    		
    		Intent intent = new Intent(Playlist.this, QueryService.class);
            stopService(intent);
    	}
    	super.onStop();
    }
    
    private void findViews() {
    	infoText = (TextView) findViewById(R.id.infoText);
    }
    
    //當ListView的項目被按下
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		
		if (type == 1) {
			PlaylistVO playlist = catalog.getPlaylists().get(position);
			
			Intent intent = new Intent();
			intent.setClass(this, Playlist.class);
			Bundle bundle = new Bundle();
			bundle.putString("id", playlist.getId());
			bundle.putInt("type", 2);
			intent.putExtras(bundle);
			startActivity(intent);
		} else if (type == 2 || type == 3){
			VideoVO video = (VideoVO)adapter.getItem(position);
			startVideo(video.getId());
		}
		super.onListItemClick(l, v, position, id);
    }
    
	private void startVideo(String videoId) {
		
		String url = "http://www.youtube.com/v/" + videoId;
		
		//以下會 check 是否有 youtube 撥放器, 若無再開網頁.
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(i,
				PackageManager.MATCH_DEFAULT_ONLY);
		if (list.size() == 0) {
			i = new Intent(Intent.ACTION_VIEW,
					Uri.parse(url));
		}
		startActivity(i);
	}
	
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Bundle bundle = intent.getExtras();
			int type = bundle.getInt("type");
			if (type == 1) {
				VideoVO video = new VideoVO();
				video.setId(bundle.getString("id"));
				video.setTitle(bundle.getString("title"));
				video.setDescription(bundle.getString("description"));
				video.setSqDefault(bundle.getString("sqDefault"));
				video.setUploaded(bundle.getString("uploaded"));
				video.setUpdated(bundle.getString("updated"));
				adapter.addVideo(video);
				adapter.notifyDataSetChanged();
				getListView().requestFocus();
			} else {
				TextView textView = (TextView)findViewById(R.id.emptyView);
				if (!bundle.getBoolean("find")) {
					textView.setText(R.string.empty);
				}
			}
		}
	}
		   
}
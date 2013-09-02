package qulip.tv.goodtv.rtmp.activity;

import java.util.List;

import qulip.tv.goodtv.rtmp.Const;
import qulip.tv.goodtv.rtmp.PlaylistAdapter;
import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.service.QueryService;
import qulip.tv.goodtv.rtmp.vo.VideoVO;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistActivity extends BaseListActivity {

	private PlaylistAdapter adapter;
	private MyReceiver myReceiver;
	
	private int type;
	private String id;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.playlist);

        type = this.getIntent().getExtras().getInt("type");
        id = this.getIntent().getExtras().getString("id");
        String title = this.getIntent().getExtras().getString("title");
        
        this.setTitle(title);
        
        //設定ListView未取得內容時顯示的view, empty建構在Playlist.xml中。
        getListView().setEmptyView(findViewById(R.id.emptyView));
        
        adapter = new PlaylistAdapter(this);
        
        //自訂方法載入ListView值
        setListAdapter(adapter);
        
        setProgressBarIndeterminateVisibility(true);
        
	}
	
	@Override
    protected void onStart() {
		//註冊BroadcastReceiver
    	myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(QueryService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
        
        //開啟服務
        Intent intent = new Intent(PlaylistActivity.this, QueryService.class);
        Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		bundle.putString("id", id);
		intent.putExtras(bundle);
        startService(intent);
    	
        super.onStart();
    }
    
    @Override
    protected void onStop() {
		unregisterReceiver(myReceiver);
		
		Intent intent = new Intent(PlaylistActivity.this, QueryService.class);
        stopService(intent);
	
    	super.onStop();
    }
    
    //當ListView的項目被按下
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		
		VideoVO video = (VideoVO)adapter.getItem(position);
		startVideo(video.getId());
		
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
				video.setViewCount(bundle.getInt("viewCount"));
				adapter.addVideo(video);
				adapter.notifyDataSetChanged();
				getListView().requestFocus();
			} else if (type == 9){
				if (Const.DEBUG_MODE) {
					Toast.makeText(arg0, bundle.getString("msg"), Toast.LENGTH_LONG).show();
				}
			} else {
				setProgressBarIndeterminateVisibility(false);
				TextView textView = (TextView)findViewById(R.id.emptyView);
				if (!bundle.getBoolean("find")) {
					textView.setText(R.string.empty);
				}
			}
		}
	}
}

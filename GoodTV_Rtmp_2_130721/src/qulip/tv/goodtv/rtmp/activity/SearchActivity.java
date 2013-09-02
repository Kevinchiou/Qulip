package qulip.tv.goodtv.rtmp.activity;

import qulip.tv.goodtv.rtmp.Const;
import qulip.tv.goodtv.rtmp.PlaylistAdapter;
import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.service.QueryService;
import qulip.tv.goodtv.rtmp.vo.VideoVO;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends BaseListActivity implements OnClickListener {

	private PlaylistAdapter adapter;
	private MyReceiver myReceiver;
	
	private boolean serviceStart;
	
	protected boolean isSearch() {
		return true;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.search);

        adapter = new PlaylistAdapter(this);
        
        //自訂方法載入ListView值
        setListAdapter(adapter);
        
        Button btn = (Button)findViewById(R.id.searchBtn);
        btn.setOnClickListener(this);
        
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(QueryService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (serviceStart) {
			stopService(new Intent(SearchActivity.this, QueryService.class));
		}
	}
	
    //當ListView的項目被按下
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		
		VideoVO video = (VideoVO)adapter.getItem(position);
		startVideo(video.getId());
		
		super.onListItemClick(l, v, position, id);
    }
	
	@Override
	public void onClick(View v) {
		EditText editText = (EditText)findViewById(R.id.editText1);
		if (editText.getText().toString().trim().length() > 0) {
			if (serviceStart) {
				//若搜尋中則取消
				stopService(new Intent(SearchActivity.this, QueryService.class));
				serviceStart = false;
			}
			adapter.clear();
			adapter.notifyDataSetChanged();
			
			//設定ListView未取得內容時顯示的view, empty建構在list.xml中。
	        getListView().setEmptyView(findViewById(R.id.emptyView));
	        setProgressBarIndeterminateVisibility(true);
	        
			Intent intent = new Intent(this, QueryService.class);
	        Bundle bundle = new Bundle();
			bundle.putInt("type", 3);
			bundle.putString("id", editText.getText().toString());
			intent.putExtras(bundle);
	        startService(intent);
	        serviceStart = true;
		}
	}
	
	private void startVideo(String videoId) {
		String url = "http://www.youtube.com/v/" + videoId;
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
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
			}  else if (type == 9){
				if (Const.DEBUG_MODE) {
					Toast.makeText(arg0, bundle.getString("msg"), Toast.LENGTH_LONG).show();
				}
			} else {
				setProgressBarIndeterminateVisibility(false);
				TextView textView = (TextView)findViewById(R.id.emptyView);
				if (!bundle.getBoolean("find")) {
					textView.setText(R.string.empty);
				}
				stopService(new Intent(SearchActivity.this, QueryService.class));
				serviceStart = false;
			}
		}
	}
}

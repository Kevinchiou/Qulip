package qulip.tv.goodtv.rtmp.activity;

import qulip.tv.goodtv.rtmp.PlaylistAdapter;
import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.vo.VideoVO;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class SaveActivity extends BaseListActivity {

	PlaylistAdapter adapter;
	
	protected boolean isSave() {
		return true;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        adapter = new PlaylistAdapter(this, true);
        
        //自訂方法載入ListView值
        setListAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(i);
	}
}

package qulip.netv;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import qulip.netv.R;
import com.netv.vo.VideoVO;

public class SaveActivity extends ListActivity {

	private TextView infoText;
	VideoSQLiteHelper sqlHelper;
	PlaylistAdapter adapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        findViews();
        
        adapter = new PlaylistAdapter(this);
        
        //設定ListView未取得內容時顯示的view, empty建構在list.xml中。
        getListView().setEmptyView(findViewById(R.id.emptyView));
        ((TextView)findViewById(R.id.emptyView)).setText("無資料!");
        infoText.setVisibility(View.INVISIBLE);
        
        sqlHelper = new VideoSQLiteHelper(this, null, Const.DB_VER);
        List<VideoVO> videos = sqlHelper.queryAll();
        
        for (VideoVO v : videos) {
        	adapter.addVideo(v);
        }
        
        setListAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    private void findViews() {
    	infoText = (TextView) findViewById(R.id.infoText);
    }
	
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

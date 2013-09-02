package qulip.tv.goodtv.rtmp.activity;

import java.util.List;

import qulip.tv.goodtv.rtmp.BreakPointSQLiteHelper;
import qulip.tv.goodtv.rtmp.Const;
import qulip.tv.goodtv.rtmp.PlayMusiclistAdapter;
import qulip.tv.goodtv.rtmp.PlaylistAdapter;
import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.service.QueryAudioAddressService;
import qulip.tv.goodtv.rtmp.service.QueryAudioService;
import qulip.tv.goodtv.rtmp.vo.AudioBP;
import qulip.tv.goodtv.rtmp.vo.AudioVO;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayMusiclistActivity extends BaseListActivity {

	public static PlayMusiclistAdapter adapter;
	private MyReceiver myReceiver;
	
	private int type, played;
	private String id;
	private Intent intent;
	
	
	static final int MENU_PLAYALL = Menu.FIRST+6;
	static final int MENU_LISTPLAY = Menu.FIRST+7;
	static final int MENU_EDITLIST = Menu.FIRST+8;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.playlist);

        type = this.getIntent().getExtras().getInt("type");
 //       id = this.getIntent().getExtras().getString("id");
        String title = this.getIntent().getExtras().getString("title");
        
        this.setTitle(title);
        
        //設定ListView未取得內容時顯示的view, empty建構在list.xml中。
        getListView().setEmptyView(findViewById(R.id.emptyView));
        
        adapter = new PlayMusiclistAdapter(this);       
        //自訂方法載入ListView值
        setListAdapter(adapter);
        
//      bpSQLiteHelper = PlayMusicActivity.BPsqlHelper;
        
        setProgressBarIndeterminateVisibility(true);
        
        registerForContextMenu(getListView());
	}
	
	@Override
    protected void onStart() {
        //開啟AudioAddress服務
/*		
        intent = new Intent(PlayMusiclistActivity.this, QueryAudioAddressService.class);
        startService(intent);
 */       
		//註冊BroadcastReceiver
    	myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(QueryAudioService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
        
        //開啟服務
        if (played == 0){
          intent = new Intent(PlayMusiclistActivity.this, QueryAudioService.class);
          Bundle bundle = new Bundle();
		  bundle.putInt("type", type);
//		  bundle.putString("id", id);
	      intent.putExtras(bundle);
          startService(intent);
        }
        
        adapter.notifyDataSetChanged();
        
        super.onStart();
    }
    
    @Override
    protected void onStop() {
		unregisterReceiver(myReceiver);
		
		Intent intent = new Intent(PlayMusiclistActivity.this, QueryAudioService.class);
        stopService(intent);
	
    	super.onStop();
    }
    
    //當ListView的項目被按下
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		
		AudioVO audio = (AudioVO)adapter.getItem(position);
		// type = 0(官網節目表依序播放) ;  progress = 0(整首播放)
		startAudio(0, position, 0, 0, audio.getId(),audio.getTitle(),audio.getDescription(),adapter);
		
		super.onListItemClick(l, v, position, id);
    }
	
	private void startAudio(int type, int position, int curprogress, int maxprogress, String audioId,String Title ,String Description,PlayMusiclistAdapter adapter) {		

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setClass(this, NewPlayMusicActivity.class);
		bundle.putInt("type", type);       
		bundle.putInt("position", position);
Log.d(Const.APP_TAG, "startAudio() position:" + position);
		bundle.putInt("curprogress", curprogress);
		bundle.putInt("maxprogress", maxprogress);	
		bundle.putString("id", audioId);
		bundle.putString("title", Title);
		bundle.putString("description", Description);
		bundle.putInt("isBackground", 0);
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);	
		//startActivity(intent);
		
	}
	

	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Bundle bundle = intent.getExtras();
			int type = bundle.getInt("type"); 
			if (type == 1) {
				AudioVO audio = new AudioVO();
				audio.setId(bundle.getString("id"));
				audio.setTitle(bundle.getString("title"));
				audio.setDescription(bundle.getString("description"));
//				audio.setSqDefault(bundle.getString("sqDefault"));
				audio.setUploaded(bundle.getString("uploaded"));
				audio.setUpdated(bundle.getString("updated"));
//				audio.setViewCount(bundle.getInt("viewCount"));
				Log.d(Const.APP_TAG, "addAudio()--"+ audio.getId());
				if (!adapter.existAudio(audio.getId()))
	            	adapter.addAudio(audio);
				
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

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int type;
		AudioBP bp;
		Intent intent;
		Bundle bundle;
		
		switch (item.getItemId()) {
		case MENU_PLAYALL:
		
			type = 1; // 官網節目表斷點依續播放
			if (adapter.bpSQLHelper.existBreakPoint(type)){
			  bp = adapter.bpSQLHelper.queryBreakpoint(type);	
		      intent = new Intent();
			  bundle = new Bundle();
		  	  intent.setClass(this, NewPlayMusicActivity.class);
			  bundle.putInt("type", type);
			  bundle.putInt("position", bp.getChapter());
	Log.d(Const.APP_TAG, "節目表斷點播放 position:" + bp.getChapter());
			  bundle.putInt("curprogress", bp.getCurPos());	
			  bundle.putInt("maxprogress", bp.getMaxPos());	
			  bundle.putString("id", bp.getAudioId());
			  bundle.putString("title", bp.getTitle());
			  bundle.putString("description", bp.getDescription());
			  bundle.putInt("isBackground", 0);
			  intent.putExtras(bundle);
//			  startActivity(intent);
			  startActivityForResult(intent, 2);	
			}
			else
         		Toast.makeText(PlayMusiclistActivity.this, "請先執行官網節目表福音", Toast.LENGTH_LONG).show();
			break;
/*		case MENU_LISTPLAY:

			type = 2; // 播放清單斷點依續播放
			if (adapter.bpSQLHelper.existBreakPoint(type)){
	     	  bp = adapter.bpSQLHelper.queryBreakpoint(type);	
			  intent = new Intent();
			  bundle = new Bundle();
			  intent.setClass(this, NewPlayMusicActivity.class);
			  bundle.putInt("type", type);
			  bundle.putInt("position", bp.getChapter());
			  bundle.putInt("progress", bp.getPosition());	
			  bundle.putInt("maxprogress", 0);		
			  bundle.putString("id", bp.getAudioId());
			  bundle.putString("title", bp.getTitle());
			  bundle.putString("description", bp.getDescription());
			  bundle.putInt("isBackground", 0);
			  intent.putExtras(bundle);
			  startActivityForResult(intent, 3);		
			}
			else
         		Toast.makeText(PlayMusiclistActivity.this, "請先加入播放清單,並 執行播放福音", Toast.LENGTH_LONG).show();
			break;
*/			
		case MENU_EDITLIST:
			
			Intent intent1 = new Intent();
			intent1.setClass(this, DragMusicSaveListActivity.class);
			startActivity(intent1);
			
			break;
		}
		return super.onContextItemSelected(item);
		
		
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int type;
		AudioBP bp;
		Intent intent;
		Bundle bundle;
		
		switch (item.getItemId()) {
		case MENU_PLAYALL:
		
			type = 1; // 官網節目表斷點依續播放
			if (adapter.bpSQLHelper.existBreakPoint(type)){
			  bp = adapter.bpSQLHelper.queryBreakpoint(type);	
		      intent = new Intent();
			  bundle = new Bundle();
		  	  intent.setClass(this, NewPlayMusicActivity.class);
			  bundle.putInt("type", type);
			  bundle.putInt("position", bp.getChapter());
			  bundle.putInt("curprogress", bp.getCurPos());	
			  bundle.putInt("maxprogress", bp.getMaxPos());	
			  bundle.putString("id", bp.getAudioId());
			  bundle.putString("title", bp.getTitle());
			  bundle.putString("description", bp.getDescription());
			  bundle.putInt("isBackground", 0);
			  intent.putExtras(bundle);
//			  startActivity(intent);
			  startActivityForResult(intent, 2);	
			}
			else
         		Toast.makeText(PlayMusiclistActivity.this, "資料庫尚無節目表斷點,請先執行官網節目表福音", Toast.LENGTH_LONG).show();
			break;
			
		case MENU_EDITLIST:
			
			Intent intent1 = new Intent();
			intent1.setClass(this, DragMusicSaveListActivity.class);
			startActivity(intent1);
			
			break;
		}
		
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if (v == getListView()){
			if (menu.size() == 0){
	    	   menu.add(0, MENU_PLAYALL, 0, "資料庫節目表斷點播放");
		       menu.add(0, MENU_EDITLIST, 2, "自訂播放清單");
			}   
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
	    menu.add(0, MENU_PLAYALL, 0, "資料庫節目表斷點播放");
		menu.add(0, MENU_EDITLIST, 2, "自訂播放清單");
		//return super.onCreateOptionsMenu(menu);
		return true;
	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub	
		played = 1;
		super.onActivityResult(requestCode, resultCode, data);
	}	
}

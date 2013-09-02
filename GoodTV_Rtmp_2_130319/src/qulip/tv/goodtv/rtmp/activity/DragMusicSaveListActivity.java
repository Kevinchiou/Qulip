package qulip.tv.goodtv.rtmp.activity;

import qulip.tv.goodtv.rtmp.DragPlayMusiclistAdapter;
import qulip.tv.goodtv.rtmp.PlayMusiclistAdapter;
import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.DragListView;
import qulip.tv.goodtv.rtmp.vo.AudioBP;
import qulip.tv.goodtv.rtmp.vo.AudioVO;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DragMusicSaveListActivity extends Activity implements OnItemClickListener{
	

	public static DragPlayMusiclistAdapter dragMusicSaveListAdapter=null;
	
	static final int MENU_MLISTPLAY = Menu.FIRST+10;
	static final int MENU_MEDITLIST = Menu.FIRST+11;
	
	public DragListView dragListView; 
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_musiclist_activity);
        
        
        dragListView = (DragListView)findViewById(R.id.drag_music_list);        
        dragMusicSaveListAdapter = new DragPlayMusiclistAdapter(this, true);
        dragListView.setAdapter(dragMusicSaveListAdapter);   
        dragListView.setOnItemClickListener(this);     
      
        registerForContextMenu(dragListView);
	}

	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		dragMusicSaveListAdapter.clearAudioList();
		dragMusicSaveListAdapter.RebuildAudiolist();
		super.onStop();
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		AudioVO audio = (AudioVO)dragMusicSaveListAdapter.getItem(position);
		
		int Type =3; //自訂清單依續播放
		startSaveListAudio(Type, position, 0, 0, audio.getId(),audio.getTitle(),audio.getDescription());		
	}	
	
	

	private void startSaveListAudio(int type, int position, int curprogress, int maxprogress, String audioId,String Title ,String Description) {		

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setClass(this, NewPlayMusicActivity.class);
		bundle.putInt("type", type);       
		bundle.putInt("position", position);
		bundle.putInt("curprogress", curprogress);	
		bundle.putInt("maxprogress", maxprogress);	
		bundle.putString("id", audioId);
		bundle.putString("title", Title);
		bundle.putString("description", Description);
		bundle.putInt("isBackground", 0);
		intent.putExtras(bundle);
//		startActivity(intent);
        startActivityForResult(intent, 5);	
		
	}	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int type;
		AudioBP bp;
		Intent intent;
		Bundle bundle;
		
		switch (item.getItemId()) {

		case MENU_MLISTPLAY:

			type = 2; // 播放清單斷點依續播放
			if (dragMusicSaveListAdapter.bpSQLHelper.existBreakPoint(type)){
	     	  bp = dragMusicSaveListAdapter.bpSQLHelper.queryBreakpoint(type);	
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
			  startActivityForResult(intent, 6);	
			}
			else
         		Toast.makeText(DragMusicSaveListActivity.this, "資料庫無斷點資料,請先執行播放清單福音", Toast.LENGTH_LONG).show();
			break;
		case MENU_MEDITLIST:
			
			
			break;
		}
		return super.onContextItemSelected(item);
		
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int type;
		AudioBP bp;
		Intent intent;
		Bundle bundle;
		
		switch (item.getItemId()) {

		case MENU_MLISTPLAY:

			type = 2; // 播放清單斷點依續播放
			if (dragMusicSaveListAdapter.bpSQLHelper.existBreakPoint(type)){
	     	  bp = dragMusicSaveListAdapter.bpSQLHelper.queryBreakpoint(type);	
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
			  startActivityForResult(intent, 6);	
			}
			else
         		Toast.makeText(DragMusicSaveListActivity.this, "資料庫無斷點資料,請先執行播放清單福音", Toast.LENGTH_LONG).show();
			break;
		case MENU_MEDITLIST:
			
			
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if (v == dragListView){
			if (menu.size() == 0){
			   menu.add(0, MENU_MLISTPLAY, 0, "資料庫自訂清單斷點播放");
			}   
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
  	    menu.add(0, MENU_MLISTPLAY, 0, "資料庫自訂清單斷點播放");
		//return super.onCreateOptionsMenu(menu);
		return true;
	
	}
		
	
}

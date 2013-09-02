package qulip.tv.goodtv.rtmp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.vo.AudioVO;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedorvlasov.lazylist.ImageLoader;

/**
 * 自訂 List 內容中有 Button 等元件, 必須將內容之 Button 元件設定android:focusable="false" 屬性
 * 並將 父元件 設定 android:descendantFocusability="blocksDescendants" 屬性
 */
public class PlayMusiclistAdapter extends BaseAdapter implements OnClickListener {	
	
	private Context ctxt;
	private LayoutInflater myInflater;
	
	public  AudioSQLiteHelper sqlHelper;
	
	public  BreakPointSQLiteHelper bpSQLHelper;	
	
	List<AudioVO> audios = null;
	
	public ImageLoader imageLoader; 
   
	public PlayMusiclistAdapter(Context ctxt){
		myInflater = LayoutInflater.from(ctxt);
		audios = new ArrayList<AudioVO>();
		this.ctxt = ctxt;
		
		sqlHelper = new AudioSQLiteHelper(this.ctxt, null, Const.DB_VER);
		
		bpSQLHelper = new BreakPointSQLiteHelper(this.ctxt, null, Const.DB_VER); 		
	}
	
	public PlayMusiclistAdapter(Context ctxt, boolean loadSave){
		this(ctxt);
		
		if (loadSave) {
			audios = sqlHelper.queryAllAudio();
		}
	}
	
	public void addAudio(AudioVO audio) {
		audios.add(audio);
	}
	
	public boolean existAudio(String new_audio_id) {
		for(int i=0; i<audios.size();i++){ 
			AudioVO old_audio = audios.get(i);
			String old_audio_id =old_audio.getId();
			if(new_audio_id.equals(old_audio_id))
		    	return true; 
		} 
		return false;	 
	}
	
	public void clear() {
		audios.clear();
	}
	
	public void remove(AudioVO audio) {
		audios.remove(audio);
	}	
	
	public void insert(AudioVO audio, int position) {
		audios.add(position, audio);
	}	
	
	@Override
	public int getCount()
	{
		return audios.size(); 
	}

	@Override
	public Object getItem(int position)
	{
		return audios.get(position);
	}
  
	@Override
	public long getItemId(int position)
	{
		return position;
	}
  
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//自訂類別，表達個別listItem中的view物件集合。
		MusicViewTag musicviewTag;
	
		if(convertView == null){
			Log.d(Const.APP_TAG, "null!");
			//取得listItem容器 view
			convertView = myInflater.inflate(R.layout.musiclist, null);
			
			//建構listItem內容view
			musicviewTag = new MusicViewTag(
//					(ImageView)convertView.findViewById(R.id.List_ImageView_icon),
					(TextView) convertView.findViewById(R.id.List_TextView_title),
					(TextView) convertView.findViewById(R.id.List_TextView_desc),
					(Button) convertView.findViewById(R.id.List_Button_save)
					);
			//設置容器內容
			convertView.setTag(musicviewTag);
		}
		else{
			Log.d(Const.APP_TAG, Integer.toString(position) +"--"+"find!");
			musicviewTag = (MusicViewTag) convertView.getTag();
		}
		
		//設定內容圖案
		AudioVO audio = audios.get(position);
		Log.d(Const.APP_TAG, Integer.toString(position) +"--"+audio.getId());

		//設定內容文字
		musicviewTag.title.setText(audio.getTitle());
		musicviewTag.desc.setText(audio.getDisplayDescription());
		musicviewTag.btn.setTag(audio);
		musicviewTag.btn.setOnClickListener(this);
		if (sqlHelper.existAudio(audio.getId())) {
			Log.d(Const.APP_TAG, audio.getId() + "已收藏");
			musicviewTag.btn.setText("已收藏");
		}else
			musicviewTag.btn.setText("+");		
		return convertView;
	}

	@Override
	public void onClick(View view) {
		Button btn = (Button)view;
		AudioVO audio = (AudioVO)btn.getTag();
		if (sqlHelper.existAudio(audio.getId())) {
			sqlHelper.deleteAudio(audio.getId());
			Toast.makeText(ctxt, "已取消收藏 " + audio.getTitle(), Toast.LENGTH_SHORT).show();
			btn.setText("+");
		} else {
			sqlHelper.insertAudio(audio);
			Toast.makeText(ctxt, "已收藏 " + audio.getTitle(), Toast.LENGTH_SHORT).show();
			btn.setText("已收藏");
		}
	}
}

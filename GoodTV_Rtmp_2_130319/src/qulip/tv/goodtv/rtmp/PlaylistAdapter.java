package qulip.tv.goodtv.rtmp;
import java.util.ArrayList;
import java.util.List;

import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.vo.AudioVO;
import qulip.tv.goodtv.rtmp.vo.VideoVO;
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
public class PlaylistAdapter extends BaseAdapter implements OnClickListener {	
	
	private Context ctxt;
	private LayoutInflater myInflater;
	
	VideoSQLiteHelper sqlHelper;
	
	List<VideoVO> videos = null;
	
	public ImageLoader imageLoader; 
   
	public PlaylistAdapter(Context ctxt){
		myInflater = LayoutInflater.from(ctxt);
		videos = new ArrayList<VideoVO>();
		this.ctxt = ctxt;
		
		imageLoader = new ImageLoader(ctxt.getApplicationContext());
		
		sqlHelper = new VideoSQLiteHelper(this.ctxt, null, Const.DB_VER);
	}
	
	public PlaylistAdapter(Context ctxt, boolean loadSave){
		this(ctxt);
		if (loadSave) {
			videos = sqlHelper.queryAll();
		}
	}
	
	public void addVideo(VideoVO video) {
		videos.add(video);
    	this.notifyDataSetChanged(); 	
	}
	
	public void clear() {
		videos.clear();
    	this.notifyDataSetChanged(); 	
	}
	
	public void remove(VideoVO video) {
		videos.remove(video);
    	this.notifyDataSetChanged(); 	
	}	
	
	@Override
	public int getCount()
	{
		return videos.size(); 
	}

	@Override
	public Object getItem(int position)
	{
		return videos.get(position);
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
		ViewTag viewTag;
		
		if(convertView == null){
			Log.d(Const.APP_TAG, "null!");
			//取得listItem容器 view
			convertView = myInflater.inflate(R.layout.list, null);
			
			//建構listItem內容view
			viewTag = new ViewTag(
					(ImageView)convertView.findViewById(R.id.List_ImageView_icon),
					(TextView) convertView.findViewById(R.id.List_TextView_title),
					(TextView) convertView.findViewById(R.id.List_TextView_desc),
					(TextView) convertView.findViewById(R.id.List_TextView_date),
					(Button) convertView.findViewById(R.id.List_Button_save)
					);
			//設置容器內容
			convertView.setTag(viewTag);
		}
		else{
			Log.d(Const.APP_TAG, "find!");
			viewTag = (ViewTag) convertView.getTag();
		}
		
		//設定內容圖案
		VideoVO video = videos.get(position);
		/* 改用 lazy load
		try {
			viewTag.icon.setImageBitmap(FetchService.readICON(video.getSqDefault()));
		} catch (Exception e) {}
		*/
		imageLoader.DisplayImage(video.getSqDefault(), viewTag.icon);
		
		//設定內容文字
		viewTag.title.setText(video.getTitle());
		viewTag.desc.setText(video.getDisplayDescription());
		viewTag.count.setText("觀看:" + video.getViewCount() + "(次)");
		viewTag.btn.setTag(video);
		viewTag.btn.setOnClickListener(this);
		if (sqlHelper.exist(video.getId())) {
			viewTag.btn.setText("已收藏");
		}else
			viewTag.btn.setText("收藏");
		return convertView;
	}

	@Override
	public void onClick(View view) {
		Button btn = (Button)view;
		VideoVO video = (VideoVO)btn.getTag();
		if (sqlHelper.exist(video.getId())) {
			sqlHelper.delete(video.getId());
			Toast.makeText(ctxt, "已取消收藏 " + video.getTitle(), Toast.LENGTH_SHORT).show();
			remove(video);
			btn.setText("收藏");
		} else {
			sqlHelper.insert(video);
			Toast.makeText(ctxt, "已收藏 " + video.getTitle(), Toast.LENGTH_SHORT).show();
			btn.setText("已收藏");
		}
	}
}

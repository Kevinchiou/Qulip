package tv.goodtv;


import java.util.ArrayList;
import java.util.List;

import tv.goodtv.vo.PlaylistVO;
import tv.goodtv.vo.VideoVO;
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

public class PlaylistAdapter extends BaseAdapter implements OnClickListener{	
	
	VideoSQLiteHelper sqlHelper;
	
	private Context ctxt;
	private LayoutInflater myInflater;
	int type;
	
	List<PlaylistVO> playlists = null;
	
	PlaylistVO playlist = null;
	List<VideoVO> videos = null;
	
	public ImageLoader imageLoader; 
   
	public PlaylistAdapter(Context ctxt, List<PlaylistVO> playlists){
		type = 1;
		myInflater = LayoutInflater.from(ctxt);
		this.ctxt = ctxt;
		this.playlists = playlists;
	}
	
	public PlaylistAdapter(Context ctxt){
		type = 3;
		myInflater = LayoutInflater.from(ctxt);
		videos = new ArrayList<VideoVO>();
		this.ctxt = ctxt;
		
		sqlHelper = new VideoSQLiteHelper(this.ctxt, null, Const.DB_VER);
		
		imageLoader=new ImageLoader(ctxt.getApplicationContext());
	}
	
	public void addVideo(VideoVO video) {
		videos.add(video);
	}
	
	@Override
	public int getCount()
	{
		if (type == 1) {
			return playlists.size(); 
		} else {
			return videos.size(); 
		}
	}

	@Override
	public Object getItem(int position)
	{
		if (type == 1) {
			return playlists.get(position);
		} else {
			return videos.get(position);
		}
	}
  
	@Override
	public long getItemId(int position)
	{
		return position;
	}
  
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//�ۭq���O�A��F�ӧOlistItem����view���󶰦X�C
		ViewTag viewTag;
		
		if(convertView == null){
			Log.d(Const.APP_TAG, "null!");
			//��olistItem�e�� view
			convertView = myInflater.inflate(R.layout.playlist, null);
			
			//�غclistItem���eview
			viewTag = new ViewTag(
					(ImageView)convertView.findViewById(R.id.Playlist_ImageView_icon),
					(TextView) convertView.findViewById(R.id.Playlist_TextView_title),
					(TextView) convertView.findViewById(R.id.Playlist_TextView_desc),
					(TextView) convertView.findViewById(R.id.Playlist_TextView_date),
					(Button) convertView.findViewById(R.id.Playlist_Button_save)
					);
			if (type == 1) {
				viewTag.date.setHeight(0);
			}
			//�]�m�e�����e
			convertView.setTag(viewTag);
		}
		else{
			Log.d(Const.APP_TAG, "find!");
			viewTag = (ViewTag) convertView.getTag();
		}
		
		//�]�w���e�Ϯ�
		if (type == 1) {
			PlaylistVO playlist = playlists.get(position);
			try {
				viewTag.icon.setImageBitmap(FetchService.readICON(playlist.getSqDefault()));
			} catch (Exception e) {}
			
			//�]�w���e��r
			viewTag.title.setText(playlist.getDisplayTitle());
			viewTag.desc.setText(playlist.getDisplayDescription());
			viewTag.button.setVisibility(View.INVISIBLE);
		} else {
			VideoVO video = videos.get(position);
			/* ��� lazy load
			try {
				viewTag.icon.setImageBitmap(FetchService.readICON(video.getSqDefault()));
			} catch (Exception e) {}
			*/
			imageLoader.DisplayImage(video.getSqDefault(), viewTag.icon);
			
			//�]�w���e��r
			viewTag.title.setText(video.getTitle());
			viewTag.desc.setText(video.getDisplayDescription());
			viewTag.date.setText(video.getUploadDate());
			
			viewTag.button.setTag(video);
			viewTag.button.setOnClickListener(this);
			if (sqlHelper.exist(video.getId())) {
				viewTag.button.setText("已收藏");
			}else
				viewTag.button.setText("收藏");
		}
		
		return convertView;
	}
	
	@Override
	public void onClick(View view) {
		Button btn = (Button)view;
		VideoVO video = (VideoVO)btn.getTag();
		if (sqlHelper.exist(video.getId())) {
			sqlHelper.delete(video.getId());
			Toast.makeText(ctxt, "已取消收藏 " + video.getTitle(), Toast.LENGTH_SHORT).show();
			btn.setText("收藏");
		} else {
			sqlHelper.insert(video);
			Toast.makeText(ctxt, "已收藏" + video.getTitle(), Toast.LENGTH_SHORT).show();
			btn.setText("已收藏");
		}
	}
}

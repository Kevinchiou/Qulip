package qulip.tv.goodtv.rtmp.activity;

import qulip.tv.goodtv.rtmp.Const;
import qulip.tv.goodtv.rtmp.PlayMusiclistAdapter;
import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.VideoSQLiteHelper;
import qulip.tv.goodtv.rtmp.service.PlayMusicService;
import qulip.tv.goodtv.rtmp.service.QueryAudioAddressService;
import qulip.tv.goodtv.rtmp.service.QueryAudioService;
import qulip.tv.goodtv.rtmp.vo.AudioVO;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnItemClickListener{
	
	protected static final int MENU_HOME = Menu.FIRST;
	protected static final int MENU_SAVE = Menu.FIRST + 1;
	protected static final int MENU_SEARCH = Menu.FIRST + 2;
	protected static final int MENU_NEWS = Menu.FIRST + 3;
	protected static final int MENU_ABOUT = Menu.FIRST + 4;
	
	VideoSQLiteHelper sqlHelper;
	private AudioAddrReceiver myReceiver;
	private Intent intent;
	public static String audioStreamAddr=null;
	public static String audioMediaType;	
	
	protected boolean isHome() {
		return true;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ImageView myImageView = (ImageView)findViewById(R.id.imageview);
        myImageView.setImageResource(R.drawable.home_logo);
        
        sqlHelper = new VideoSQLiteHelper(this, null, Const.DB_VER);
        
        // Reference the Gallery view
        Gallery g = (Gallery) findViewById(R.id.gallery);
        // Set the adapter to our custom adapter (below)
        g.setAdapter(new ImageAdapter(this));
        
        // Set a item click listener, and just Toast the clicked position
        g.setOnItemClickListener(this);
        
        // We also want to show context menu for longpressed items in the gallery
        registerForContextMenu(g);
    }
    
    
    
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if (audioStreamAddr == null){
		  //註冊BroadcastReceiver
    	  myReceiver = new AudioAddrReceiver();
          IntentFilter intentFilter = new IntentFilter();
          intentFilter.addAction(QueryAudioAddressService.MY_ACTION);
          registerReceiver(myReceiver, intentFilter);
        
          //開啟AudioAddress服務
          intent = new Intent(MainActivity.this, QueryAudioAddressService.class);
          startService(intent);
		} 
		super.onStart();
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if ((NewPlayMusicActivity.iAudioPlayerState == 4) || (NewPlayMusicActivity.iAudioPlayerState == 0)){
	        //關閉服務
	        intent = new Intent(MainActivity.this, PlayMusicService.class);
	        stopService(intent);
		}    
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		//Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
		if (position == 0) {
        	//live 1 台
			//openLive(1);
			Uri uri = Uri.parse("http://w2.goodtv.tv/live/mobile1live.php");
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);
        } else if (position == 1){
        	//live 2 台
        	//openLive(2);
			Uri uri = Uri.parse("http://w2.goodtv.tv/live/mobile2live.php");
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);
        } else if (position == 2) {
        	openPlaylist(1, "每日音符", "PLA68B96AD6895220F");
        } else if (position == 3) {
        	openPlaylist(1, "真情推薦", "PL5DD4E805A23C7BFD");
        } else if (position == 4) {
        	openPlaylist(1, "最新預告", "PLE4DF216A0EC127CF");
        } else if (position == 5) {
        	openPlaylist(1, "心靈啟發", "PL58778A0385AAE36E");
        } else if (position == 6) {
        	openPlaylist(2, "最新焦點", "");
        } else if (position == 7) {
        	openPlayMusiclist(2, "福音播客", "");
        }
	}
	
	private void openLive(int type) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setClass(this, WebActivity.class);
		bundle.putInt("type", type);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	private void openPlaylist(int type, String title, String playlistId) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setClass(this, PlaylistActivity.class);
		bundle.putInt("type", type);
		bundle.putString("title", title);
		bundle.putString("id", playlistId);
		intent.putExtras(bundle);
		startActivity(intent);
	}
    
	
	private void openPlayMusiclist(int type, String title, String playlistId) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setClass(this, PlayMusiclistActivity.class);
		bundle.putInt("type", type);
		bundle.putString("title", title);
//		bundle.putString("id", playlistId);
		intent.putExtras(bundle);
		startActivity(intent);
	}
    	
	
    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        
        public ImageAdapter(Context c) {
            mContext = c;
            // See res/values/attrs.xml for the <declare-styleable> that defines
            // Gallery1.
            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();
        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);

            i.setImageResource(mImageIds[position]);
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            //i.setLayoutParams(new Gallery.LayoutParams(136, 88));
            
            // The preferred Gallery item background
            i.setBackgroundResource(mGalleryItemBackground);
            
            i.setBackgroundColor(Color.BLACK);
            return i;
        }

        private Context mContext;

        private Integer[] mImageIds = {
                R.drawable.app_a,
                R.drawable.app_b,
                R.drawable.app_c,
                R.drawable.app_d,
                R.drawable.app_e,
                R.drawable.app_f,
                R.drawable.app_g,
                R.drawable.app_h 
        };
    }
    
	private class AudioAddrReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Bundle bundle = intent.getExtras();
			int type = bundle.getInt("msgtype"); 
			if (type == 1) {
				audioStreamAddr = bundle.getString("AudioAddr");
				audioMediaType = bundle.getString("MediaType");
				if (audioStreamAddr != null){
//					Toast.makeText(MainActivity.this, "已收到福音播客伺服器位址", Toast.LENGTH_LONG).show();
			        //關閉服務
					
			        intent = new Intent(MainActivity.this, QueryAudioAddressService.class);
			        stopService(intent);
			        					
				}
			} else if (type == 9){
				if (Const.DEBUG_MODE) {
					Toast.makeText(arg0, bundle.getString("msg"), Toast.LENGTH_LONG).show();
				}
			} 
		}
	}
    
    
}
package qulip.tv.goodtv.rtmp.activity;

import java.io.IOException;

import qulip.tv.goodtv.rtmp.DragPlayMusiclistAdapter;
import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.AudioSQLiteHelper;
import qulip.tv.goodtv.rtmp.BreakPointSQLiteHelper;
import qulip.tv.goodtv.rtmp.PlayMusiclistAdapter;
import qulip.tv.goodtv.rtmp.service.PlayMusicService;
import qulip.tv.goodtv.rtmp.service.QueryAudioAddressService;
import qulip.tv.goodtv.rtmp.vo.AudioBP;
import qulip.tv.goodtv.rtmp.vo.AudioVO;
import qulip.tv.goodtv.rtmp.Const;
import qulip.tv.goodtv.rtmp.VideoSQLiteHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class NewPlayMusicActivity extends Activity  {

	private ImageButton mBtnMediaPlayPause,
						mBtnMediaNext,
						mBtnMediaBack;
	
	private TextView mTxtTitle, mTxtDescription, mTxtType, mtxtCurrentPlayTime, mtxtTotlePlayTime;
	private String sAudioId, sTitle, sDescription;
	private int iType, iChapter, iCurrentPosition, iMaxPosition, iListCount; // breakpoint data
	private SeekBar mAudioSeekBar = null;
	private SeekBar mAudioProgressBar = null;
	private PlayMusiclistAdapter mAudioList = null;
	private DragPlayMusiclistAdapter mDragAudioList = null;
	private AudioPlayReceiver audioPlayReceiver;
	AudioManager mAudioManager;
	private int	 iMaxDuration, iCurrentDuration, iBackground;
    private ProgressDialog progDlg, progBarDlg;	
    private boolean	mBoolIsInitial = true;
    private boolean	bTracking = false;
    private boolean	Err = false;  // 3.x 斷點播放會送出 Error state
    private Intent intent1;
    private ComponentName mRemoteControlResponder;
    
	public static int iAudioPlayerState= 0;
	static final int MENU_BACKTOLIST = Menu.FIRST+10;
	static final int MENU_BACKTOUSERLIST = Menu.FIRST+11;
	static final int MENU_BACKTOMAIN = Menu.FIRST+12;    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
/*  	
        // 平板電腦3.x 強制設成landscape
        Integer sdk=Integer.valueOf(android.os.Build.VERSION.SDK);  
		if ((sdk == 11) || (sdk == 12) || (sdk == 13)){
		   if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){ 
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
			 } 
		}
*/	
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.musicplay);
        
        iType = this.getIntent().getExtras().getInt("type");
        iChapter = this.getIntent().getExtras().getInt("position");
 Log.d(Const.APP_TAG, "PlayMusicActivity onCreate() position:" + iChapter);
        iCurrentPosition = this.getIntent().getExtras().getInt("curprogress");
        iMaxPosition = this.getIntent().getExtras().getInt("maxprogress");       
        sAudioId = this.getIntent().getExtras().getString("id");
        sTitle = this.getIntent().getExtras().getString("title");
        sDescription = this.getIntent().getExtras().getString("description");
        iBackground = this.getIntent().getExtras().getInt("isBackground");
        //由官網或自訂情單List進入且正在player執行中則關閉服務
        if (iBackground == 0){
        	if (NewPlayMusicActivity.iAudioPlayerState > 2){ 
        	  Intent intent = new Intent(NewPlayMusicActivity.this, PlayMusicService.class);
	          stopService(intent);
	          iAudioPlayerState= 0;
        	}
        }
        
        if ((iType == 0) || (iType == 1))
           mAudioList = PlayMusiclistActivity.adapter;
        else
           mDragAudioList = DragMusicSaveListActivity.dragMusicSaveListAdapter;
        setupViewComponent(sAudioId, sTitle ,sDescription); 
        
		//註冊Audio Player Service Broadcast Receiver
        audioPlayReceiver = new AudioPlayReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.ACTION_FEEDBACK);
        registerReceiver(audioPlayReceiver, intentFilter);      
        
    }

	private void setupViewComponent(String audioId,String Title ,String Description) {
    	mBtnMediaPlayPause = (ImageButton)findViewById(R.id.btnMediaPlayPause);
    	mBtnMediaNext = (ImageButton)findViewById(R.id.btnMediaNext);
    	mBtnMediaBack = (ImageButton)findViewById(R.id.btnMediaPrevious);
    	mTxtTitle = (TextView)findViewById(R.id.textTitle);
    	mTxtTitle.setText((CharSequence) Title);
    	mTxtDescription = (TextView)findViewById(R.id.textDescription);
    	mTxtDescription.setText((CharSequence) Description);
    	mTxtType = (TextView)findViewById(R.id.textView2);
    	if (iType == 0)
           mTxtType.setText((CharSequence) "官網節目表依續播放");
    	else if (iType == 1)
    	   mTxtType.setText((CharSequence) "官網節目表斷點依續播放");
    	else if (iType == 2)
     	   mTxtType.setText((CharSequence) "自訂清單斷點依續播放");  
       	else 
      	   mTxtType.setText((CharSequence) "自訂清單依續播放");    	   	
    	
    	mtxtCurrentPlayTime = (TextView)findViewById(R.id.txtCurrentPlayTime);
    	mtxtTotlePlayTime = (TextView)findViewById(R.id.txtTotlePlayTime);
    	mAudioSeekBar = (SeekBar) findViewById(R.id.seekBar1);
    	mAudioProgressBar = (SeekBar) findViewById(R.id.progressBar1);
    	
    	mBtnMediaPlayPause.setOnClickListener(btnMediaPlayPauseLis);
    	mBtnMediaNext.setOnClickListener(btnMediaNextLis);
    	mBtnMediaBack.setOnClickListener(btnMediaBackLis);
    	
		if ((iBackground == 1) && (iAudioPlayerState == 3)) { //isPlaying
			   mBtnMediaPlayPause.setImageResource(android.R.drawable.ic_media_pause);    	
		}
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxV = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int curV = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		
		// set Music key Control
/*		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	
        mRemoteControlResponder = new ComponentName(getPackageName(),
                RemoteControlReceiver.class.getName());		
        mAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);
*/		
		//Here we set the max and progress like I said above.
		mAudioSeekBar.setMax(maxV);
		mAudioSeekBar.setProgress(curV);
		mAudioProgressBar.setMax(iMaxPosition);
		mAudioProgressBar.setProgress(iCurrentPosition);
		mtxtTotlePlayTime.setText(ShowTime(iMaxPosition)); 
		
	    mAudioProgressBar.setOnSeekBarChangeListener(new ProgressBarListener());  
		mAudioSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
    }    
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        // 斷點直接播放,循序等user按play鍵再播放
		if ((iType == 1) || (iType == 2))  {			   
	       startMediaPlayer(1, iType, iChapter, iMaxPosition, iCurrentPosition, sAudioId, sTitle, sDescription);
		}   
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if ((NewPlayMusicActivity.iAudioPlayerState == 4) || (NewPlayMusicActivity.iAudioPlayerState == 0)){
	        //關閉服務
			Intent intent = new Intent(NewPlayMusicActivity.this, PlayMusicService.class);
	        stopService(intent);
	        iAudioPlayerState= 0;
		} 
	    super.onStop();		  
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}	
  
	
	
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
 Log.d(Const.APP_TAG, "onConfigurationChanged() start");
	}



	class ProgressBarListener implements OnSeekBarChangeListener{  
  
        @Override  
        public void onProgressChanged(SeekBar seekBar, int progress,  
                boolean fromUser) {  
            // TODO Auto-generated method stub  
            if (fromUser==true) {  
              if(iAudioPlayerState < 2) // before Prepared
                  return;   
              gotoPositionMediaPlayer(progress);
              mtxtCurrentPlayTime.setText(ShowTime(progress));  


            }  
              
        }  
  
        @Override  
        public void onStartTrackingTouch(SeekBar seekBar) {  
            // TODO Auto-generated method stub   
/*        	
          if((iAudioPlayerState ==3) && (!bTracking)){
   Log.d(Const.APP_TAG, "Dialog 緩衝處理中...");	 
   			    progBarDlg = new ProgressDialog(NewPlayMusicActivity.this);
   			    progBarDlg.setMessage("緩衝處理中...");
   			    progBarDlg.setCancelable(false);
                progBarDlg.show();
     		    bTracking = true;
   	       }  
   	       
*/   	       
        }  

        @Override  
        public void onStopTrackingTouch(SeekBar seekBar) {  
            // TODO Auto-generated method stub  
        }          
  
    }  
	
	
    class SeekBarListener implements OnSeekBarChangeListener{     	
    
      @Override
	  public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
        if (fromUser) {  
            int SeekPosition=seekBar.getProgress();  
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, SeekPosition, 0);  
        }  
  	  }

	  @Override
	  public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	  }

	  @Override
	  public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	  }
    }    

    private Button.OnClickListener btnMediaPlayPauseLis = new Button.OnClickListener() {
		public void onClick(View v) {	
			if ((iAudioPlayerState == 3) || (iAudioPlayerState == 5)) { //isPlaying
               pauseMediaPlayer();
			}
			else{ //isPauseing	
			   startMediaPlayer(1, iType, iChapter, iMaxPosition, iCurrentPosition, sAudioId, sTitle, sDescription);
			}				
		}
	};

    //下一首  
    private Button.OnClickListener btnMediaNextLis = new Button.OnClickListener() {
		public void onClick(View v) {
			if(iAudioPlayerState == 1)
				return;
			nextMusic();  
		}
	};	
	
    //上一首   
    private Button.OnClickListener btnMediaBackLis = new Button.OnClickListener() {
		public void onClick(View v) {
			if(iAudioPlayerState == 1)
				return;
			FrontMusic();
		}
	};	
 
	
    private void playMusic(int iPosition){  
		AudioVO mAudio = null;
		if ((iType == 0) || (iType == 1))
			mAudio = (AudioVO)mAudioList.getItem(iPosition);
		else
			mAudio = (AudioVO)mDragAudioList.getItem(iPosition);
		mTxtTitle.setText((CharSequence) mAudio.getTitle());
		mTxtDescription.setText((CharSequence) mAudio.getDescription());

		startMediaPlayer(2, iType, iPosition, 0, 0, mAudio.getId(), mAudio.getTitle(), mAudio.getDescription() );	
    }

	private void startMediaPlayer(int iAction,int iType, int iPosition, int iMaxProgress, int iCurProgress, String sAudioId, String sTitle, String sDescription) {
		if(iAudioPlayerState == 1)
			return;
		Intent intent = new Intent(NewPlayMusicActivity.this, PlayMusicService.class);
		if (iAction == 2)
		   intent.setAction(PlayMusicService.ACTION_NEXT);
		else
		   intent.setAction(PlayMusicService.ACTION_PLAY);			
		Bundle bundle = new Bundle();

		bundle.putInt("type", iType);       
		bundle.putInt("position", iPosition);
Log.d(Const.APP_TAG, "startMediaPlayer() position:" + iPosition);		
		bundle.putInt("maxprogress", iMaxProgress);
		bundle.putInt("curprogress", iCurProgress);
		bundle.putString("id", sAudioId);
		bundle.putString("title", sTitle);
		bundle.putString("description", sDescription);
		intent.putExtras(bundle);

		startService(intent);
		
		mBtnMediaPlayPause.setImageResource(android.R.drawable.ic_media_pause);	
	}       

	private void pauseMediaPlayer() {
		Intent intent = new Intent(NewPlayMusicActivity.this, PlayMusicService.class);
        intent.setAction(PlayMusicService.ACTION_PAUSE);			
		startService(intent);
		mBtnMediaPlayPause.setImageResource(android.R.drawable.ic_media_play);	
	}       	
	
	private void gotoPositionMediaPlayer(int iPosition) {
		Intent intent = new Intent(NewPlayMusicActivity.this, PlayMusicService.class);
   	    intent.setAction(PlayMusicService.ACTION_GOTO);			
		Bundle bundle = new Bundle(); 
		bundle.putInt("position", iPosition);
		intent.putExtras(bundle);
		startService(intent);
	} 	
	
    //下一首  
    private void nextMusic() {  
        // TODO Auto-generated method stub  
        if ((iType == 0) || (iType == 1))
        	iListCount = mAudioList.getCount()-1;
        else
        	iListCount = mDragAudioList.getCount()-1;
        if(++iChapter > iListCount)  
        	iChapter = 0;     
/*        
    	if (NewPlayMusicActivity.iAudioPlayerState > 2){ 
      	  Intent intent = new Intent(NewPlayMusicActivity.this, PlayMusicService.class);
	          stopService(intent);
	          iAudioPlayerState= 0;
      	}
*/      	
        playMusic(iChapter);           
    }  
      
    //上一首   
    private void FrontMusic(){  
        if ((iType == 0) || (iType == 1))
        	iListCount = mAudioList.getCount()-1;
        else
        	iListCount = mDragAudioList.getCount()-1;
        
        if (--iChapter < 0)  
        	iChapter = iListCount; 
/*        
    	if (NewPlayMusicActivity.iAudioPlayerState > 2){ 
      	  Intent intent = new Intent(NewPlayMusicActivity.this, PlayMusicService.class);
	          stopService(intent);
	          iAudioPlayerState= 0;
      	}
*/      	
        playMusic(iChapter);  
    }     
    
    
    public String ShowTime(int time){  
        time/=1000;  
        int minute=time/60;  
        int hour=minute/60;  
        int second=time%60;  
        minute%=60;  
        return String.format("%02d:%02d", minute, second);  
    }  
    
    /* state define ---------------------------
    0:Stop / Idle;
    1:Preparing;
    2:Prepared;
    3:Started;
    4:Paused;
    5:Completed;
    6:SeekCompleted;
    9:Error
    --------------------------------------------*/   
	private class AudioPlayReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Bundle bundle = intent.getExtras();
			int type = bundle.getInt("msgtype"); 
			if (type == 1) {
				iAudioPlayerState = bundle.getInt("state");
				Log.d(Const.APP_TAG, "iAudioPlayerState="+iAudioPlayerState);				
				if(iAudioPlayerState == 1){
					progDlg = new ProgressDialog(NewPlayMusicActivity.this);
					progDlg.setMessage("準備播放中...");
					progDlg.setCancelable(false);
					progDlg.show();
				}else if(iAudioPlayerState == 2){
					Err = false;
					if(progDlg != null)
						progDlg.cancel();
				}else if(iAudioPlayerState == 3){
					Err = false;
					if(progDlg != null)
						progDlg.cancel();
					mBtnMediaPlayPause.setImageResource(android.R.drawable.ic_media_pause);	    
				}else if(iAudioPlayerState == 4){
					mBtnMediaPlayPause.setImageResource(android.R.drawable.ic_media_play);
				}else if(iAudioPlayerState == 5){
					if(progDlg != null)
						progDlg.cancel();
					if (!Err)
					  nextMusic();	
				}else if(iAudioPlayerState == 6){
					if(progBarDlg != null)
						progBarDlg.cancel();
//				   	if (bTracking)
//				   		bTracking = false;				
				}else if(iAudioPlayerState == 9){ // error
					if(progDlg != null)
						progDlg.cancel();
					mBtnMediaPlayPause.setImageResource(android.R.drawable.ic_media_play);
		Toast.makeText(NewPlayMusicActivity.this, "播放器發生錯誤!", Toast.LENGTH_LONG).show();
					Err = true;
				}
			}else if (type == 2) {
				iMaxDuration = bundle.getInt("maxduration");
				mtxtTotlePlayTime.setText(ShowTime(iMaxDuration)); 
			    mAudioProgressBar.setMax(iMaxDuration); 		
			}else if (type == 3) {
				iCurrentDuration = bundle.getInt("currentduration");
		        mtxtCurrentPlayTime.setText(ShowTime(iCurrentDuration));  
		        mAudioProgressBar.setProgress(iCurrentDuration);
			}else if (type == 4) {
				int iBufferingPercent = bundle.getInt("percent");
				if (iMaxDuration > 0){ 
				   int iBufferingDuration = (int) (iMaxDuration*(iBufferingPercent*0.01));
		           mAudioProgressBar.setSecondaryProgress(iBufferingDuration);
				};
			}else if (type == 9){
				if (Const.DEBUG_MODE) {
					Toast.makeText(arg0, bundle.getString("msg"), Toast.LENGTH_LONG).show();
				}
			} 
		}
	}    
  

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
    	switch (item.getItemId()) {
		case MENU_BACKTOLIST:
			intent1 = new Intent();
			intent1.setClass(NewPlayMusicActivity.this, PlayMusiclistActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("type", iType);
			bundle.putString("title", sTitle);
//			bundle.putString("id", playlistId);
			intent1.putExtras(bundle);
			startActivity(intent1);	
			break;			
		case MENU_BACKTOUSERLIST:			
			intent1 = new Intent();
			intent1.setClass(NewPlayMusicActivity.this, DragMusicSaveListActivity.class);
			startActivity(intent1);
			break;
		case MENU_BACKTOMAIN:
			intent1 = new Intent();
			intent1.setClass(NewPlayMusicActivity.this, MainActivity.class);
			startActivity(intent1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
 	    menu.add(0, MENU_BACKTOLIST, 0, "返回節目表");
	    menu.add(0, MENU_BACKTOUSERLIST, 1, "返回播放清單");
        menu.add(0, MENU_BACKTOMAIN, 2, "返回主畫面");
		//return super.onCreateOptionsMenu(menu);
		return true;
	
	}
/*	
	public class RemoteControlReceiver extends BroadcastReceiver { 
		  @Override 
		  public void onReceive(Context context, Intent intent) { 
		    if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) { 
		      KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT); 
		      if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode()) { 
		        // Handle key press. 
		        // 处理播放按键的消息 
		      } 
		    } 
		  } 
	} 	
*/	
}
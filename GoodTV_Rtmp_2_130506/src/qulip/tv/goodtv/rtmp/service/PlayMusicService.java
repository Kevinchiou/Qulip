package qulip.tv.goodtv.rtmp.service;

import qulip.tv.goodtv.rtmp.Const;
import qulip.tv.goodtv.rtmp.R;
import qulip.tv.goodtv.rtmp.DragPlayMusiclistAdapter;
import qulip.tv.goodtv.rtmp.PlayMusiclistAdapter;
import qulip.tv.goodtv.rtmp.activity.DragMusicSaveListActivity;
import qulip.tv.goodtv.rtmp.activity.MainActivity;
import qulip.tv.goodtv.rtmp.activity.NewPlayMusicActivity;
import qulip.tv.goodtv.rtmp.activity.PlayMusiclistActivity;
import qulip.tv.goodtv.rtmp.vo.AudioVO;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayMusicService extends Service 
     implements MediaPlayer.OnPreparedListener,
                MediaPlayer.OnErrorListener,
                MediaPlayer.OnCompletionListener,
                MediaPlayer.OnSeekCompleteListener,
                MediaPlayer.OnBufferingUpdateListener,
                AudioManager.OnAudioFocusChangeListener
{
	
	public static final String 
	   ACTION_PLAY = "tw.qulip.mediaplayer.action.PLAY",
	   ACTION_PAUSE = "tw.qulip.mediaplayer.action.PAUSE",
	   ACTION_BACK = "tw.qulip.mediaplayer.action.BACK",
	   ACTION_NEXT = "tw.qulip.mediaplayer.action.NEXT",
	   ACTION_GOTO = "tw.qulip.mediaplayer.action.GOTO",
	   ACTION_FEEDBACK = "tw.qulip.mediaplayer.action.FEEDBACK";


//	private static final ComponentName RemoteControlReceiver = null;
	
    private String sAudioId,sTitle,sDescription;
    private int iType, iChapter, iCurrentPosition, iMaxPosition; // breakpoint data
    private MediaPlayer mMediaPlayer = null;
    private PlayMusiclistAdapter audiolist = null;
    private DragPlayMusiclistAdapter dragaudiolist = null;
    AudioManager audioManager;
    Context ctxt; 


    private boolean	mBoolIsInitial = true;
    /** The wake lock that keeps the screen on while video is playing */
    protected WakeLock mWakeLock;


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
        audiolist = PlayMusiclistActivity.adapter;

        dragaudiolist = DragMusicSaveListActivity.dragMusicSaveListAdapter;		

	    if(mMediaPlayer == null)
		   mMediaPlayer = new MediaPlayer();
	    else
		  return;
	
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnSeekCompleteListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);
					
		// grab a wake-lock so that the video can play without the screen being turned off
		PowerManager lPwrMgr = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = lPwrMgr.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MUSIC_WAKELOCK");
		mWakeLock.acquire();
		
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		AudioVO audio;
		int bpCheckType;
		if ((iType == 0) || (iType == 1)){
			audio = (AudioVO)audiolist.getItem(iChapter);
			bpCheckType = 1;
			if (!audiolist.bpSQLHelper.existBreakPoint(bpCheckType)) // check Type = 1
				audiolist.bpSQLHelper.insertBreakpoint(bpCheckType, iChapter, iMaxPosition, iCurrentPosition, audio.getId(),audio.getTitle(),audio.getDescription());
			else
				audiolist.bpSQLHelper.updateBreakpoint(bpCheckType, iChapter, iMaxPosition, iCurrentPosition, audio.getId(),audio.getTitle(),audio.getDescription());			
		}	
		else{
			audio = (AudioVO)dragaudiolist.getItem(iChapter);
			bpCheckType = 2;
			if (!dragaudiolist.bpSQLHelper.existBreakPoint(bpCheckType)) // check Type = 1
				dragaudiolist.bpSQLHelper.insertBreakpoint(bpCheckType, iChapter, iMaxPosition, iCurrentPosition, audio.getId(),audio.getTitle(),audio.getDescription());
			else
				dragaudiolist.bpSQLHelper.updateBreakpoint(bpCheckType, iChapter, iMaxPosition, iCurrentPosition, audio.getId(),audio.getTitle(),audio.getDescription());			
		}	
					
		handler.removeCallbacks(RunProgress);
		mMediaPlayer.release();
		mMediaPlayer = null;		
		
		stopForeground(true);
		
	    // release wake-lock 
		if(mWakeLock != null){
			mWakeLock.release();
		}
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		if (intent.getAction().equals(ACTION_PLAY)){
	        iType = intent.getExtras().getInt("type");
	        iChapter = intent.getExtras().getInt("position");
	        iMaxPosition = intent.getExtras().getInt("maxprogress");
	        iCurrentPosition = intent.getExtras().getInt("curprogress");
	        sAudioId = intent.getExtras().getString("id");
	        sTitle = intent.getExtras().getString("title");
	        sDescription = intent.getExtras().getString("description");
          
	        
			if (mBoolIsInitial) {
			  String url_addr = "http://webaod.goodtv.tv/webaod/" + sAudioId + ".mp3";
			  if(MainActivity.audioStreamAddr != null)
		 	    url_addr = MainActivity.audioStreamAddr + sAudioId + "." + MainActivity.audioMediaType;
			  else			
				Toast.makeText(PlayMusicService.this, "使用內定福音伺服器位置!", Toast.LENGTH_LONG).show();			
			  Uri uri = Uri.parse(url_addr);
		      try {
//				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);	
				mMediaPlayer.setDataSource(PlayMusicService.this, uri);
			  } catch (Exception e) {
				// TODO Auto-generated catch block			
				Toast.makeText(PlayMusicService.this, "播放檔有誤!", Toast.LENGTH_LONG)
					.show();				
			  }		
			  mMediaPlayer.prepareAsync();
			    sendStateChange(1); //Preparing
				mBoolIsInitial = false;
			}
			else{
				mMediaPlayer.start();
			    sendStateChange(3); //Started
			}
		}	
		else if (intent.getAction().equals(ACTION_PAUSE)){
			mMediaPlayer.pause();
			sendStateChange(4); //Paused  
		}	
		else if ((intent.getAction().equals(ACTION_BACK)) || (intent.getAction().equals(ACTION_NEXT))){
	        iType = intent.getExtras().getInt("type");
	        iChapter = intent.getExtras().getInt("position");
	        iMaxPosition = intent.getExtras().getInt("maxprogress");
	        iCurrentPosition = intent.getExtras().getInt("curprogress");
	        sAudioId = intent.getExtras().getString("id");
	        sTitle = intent.getExtras().getString("title");
	        sDescription = intent.getExtras().getString("description");
        
            mMediaPlayer.reset();//重置MediaPlayer 
			sendStateChange(0); //Idle          
            handler.removeCallbacks(RunProgress);  
	        
			String url_addr = "http://webaod.goodtv.tv/webaod/" + sAudioId + ".mp3";
			if(MainActivity.audioStreamAddr != null)
		 	    url_addr = MainActivity.audioStreamAddr + sAudioId + "." + MainActivity.audioMediaType;
			else			
				Toast.makeText(PlayMusicService.this, "使用內定福音伺服器位置!", Toast.LENGTH_LONG).show();			
			Uri uri = Uri.parse(url_addr);
			try {
//				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);	
				mMediaPlayer.setDataSource(PlayMusicService.this, uri);
			} catch (Exception e) {
				// TODO Auto-generated catch block			
				Toast.makeText(PlayMusicService.this, "播放檔有誤!", Toast.LENGTH_LONG)
					.show();				
			}		
    		mMediaPlayer.setOnPreparedListener(this);
    		mMediaPlayer.setOnErrorListener(this);
    		mMediaPlayer.setOnCompletionListener(this);
    		mMediaPlayer.setOnSeekCompleteListener(this);
    		mMediaPlayer.setOnBufferingUpdateListener(this);
		 
            mMediaPlayer.prepareAsync();//准备播放  
			sendStateChange(1); //preparing
			mBoolIsInitial = false;
	    }
		else if (intent.getAction().equals(ACTION_GOTO)) {
	        iCurrentPosition = intent.getExtras().getInt("position");
			mMediaPlayer.seekTo(iCurrentPosition); 
		}
		
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		// TODO Auto-generated method stub
		if (mMediaPlayer == null)
			return;

		switch (focusChange) {
		case AudioManager.AUDIOFOCUS_GAIN:
			mMediaPlayer.setVolume(0.8f, 0.8f);
			mMediaPlayer.start();	
			sendStateChange(3);
			break;
		case AudioManager.AUDIOFOCUS_LOSS:
			stopSelf();   // 停止播放Service
			break;
		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			if (mMediaPlayer.isPlaying()){
				mMediaPlayer.pause();
				sendStateChange(4);
			}
			break;
		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
			if (mMediaPlayer.isPlaying())
				mMediaPlayer.setVolume(0.1f, 0.1f);
			break;
		}			
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
//		Log.d(Const.APP_TAG, "onBufferingUpdate!"+",percent="+Integer.toString(percent));
		if(percent <= 100)
		   sendBufferingPercent(percent);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.d(Const.APP_TAG, "onSeekComplete!");
		sendStateChange(6);		
		mp.start();	
		sendStateChange(3);
	
		Intent it = new Intent(getApplicationContext(), NewPlayMusicActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("type", iType);       
		bundle.putInt("position", iChapter);
		bundle.putInt("curprogress", iCurrentPosition);	
		bundle.putInt("maxprogress", mp.getDuration());			
		bundle.putString("id", sAudioId);
		bundle.putString("title", sTitle);
		bundle.putString("description", sDescription);
		bundle.putInt("isBackground", 1);
		it.putExtras(bundle);
	
		PendingIntent pendIt = PendingIntent.getActivity(
				getApplicationContext(), 0, it, 
				PendingIntent.FLAG_UPDATE_CURRENT);
		Notification noti = new Notification(
				R.drawable.title_logo, 
				sTitle+"播放中...",
				System.currentTimeMillis());
		noti.flags |= Notification.FLAG_ONGOING_EVENT;
		noti.setLatestEventInfo(getApplicationContext(), 
				"福音播客", sTitle+"播放中...", pendIt);
		startForeground(1, noti);		
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		sendStateChange(5);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		Log.d(Const.APP_TAG, "onError!");
		sendStateChange(9);
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
 	
		//get AudioFocus
		AudioManager audioMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		int r = audioMgr.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		if (r != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
			mp.setVolume(0.1f, 0.1f);	

		
		iMaxPosition=mp.getDuration();  
        sendMaxDuration(iMaxPosition);    
	    if ((iType == 1) || (iType == 2)){	// 斷點播放   
	        sendCurrentDuration(iCurrentPosition); 	    	
		    mp.seekTo(iCurrentPosition);
		} else
	        mp.seekTo(0);			

		sendStateChange(2);
        ProgressbarUpdate();		
	}

    Handler handler=new Handler();  
    public void ProgressbarUpdate(){   
        handler.post(RunProgress);  
    }  
    
    Runnable RunProgress=new Runnable() {  
          
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
            iCurrentPosition=mMediaPlayer.getCurrentPosition();
	        sendCurrentDuration(iCurrentPosition);
			iMaxPosition=mMediaPlayer.getDuration();
	        sendMaxDuration(iMaxPosition);
            handler.postDelayed(RunProgress, 1000);  
        }  
    };

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
	private void sendStateChange(int state) {
		Intent i = new Intent(ACTION_FEEDBACK);
		Bundle b = new Bundle();
		b.putInt("msgtype", 1);
		b.putInt("state", state);
		i.putExtras(b);
		sendBroadcast(i);
	}
	private void sendMaxDuration(int duration) {
		Intent i = new Intent(ACTION_FEEDBACK);
		Bundle b = new Bundle();
		b.putInt("msgtype", 2);
		b.putInt("maxduration", duration);
		i.putExtras(b);
		sendBroadcast(i);
	}
	private void sendCurrentDuration(int duration) {
		Intent i = new Intent(ACTION_FEEDBACK);
		Bundle b = new Bundle();
		b.putInt("msgtype", 3);
		b.putInt("currentduration", duration);
		i.putExtras(b);
		sendBroadcast(i);
	}
	private void sendBufferingPercent(int percent) {
		Intent i = new Intent(ACTION_FEEDBACK);
		Bundle b = new Bundle();
		b.putInt("msgtype", 4);
		b.putInt("percent", percent);
		i.putExtras(b);
		sendBroadcast(i);
	}
	private void sendMsg(String err) {
		Intent i = new Intent(ACTION_FEEDBACK);
		Bundle b = new Bundle();
		b.putInt("msgtype", 9);
		b.putString("msg", err);
		i.putExtras(b);
		sendBroadcast(i);
	}	

	
}

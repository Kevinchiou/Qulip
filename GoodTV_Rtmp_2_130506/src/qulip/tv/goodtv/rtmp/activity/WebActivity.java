package qulip.tv.goodtv.rtmp.activity;

import qulip.tv.goodtv.rtmp.Const;
import qulip.tv.goodtv.rtmp.R;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class WebActivity extends BaseActivity {

	FrameLayout mWebContainer;
	WebView webView;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        int type = this.getIntent().getExtras().getInt("type");
        String html = html_live1;
        if (type == 2) {
        	html = html_live2;
        	this.setTitle("LIVE 2");
        }
        
        //WebView android 有 memory leak 問題, 所以必須動態加入
        //mWebContainer = (FrameLayout) findViewById(R.id.web_container);
        //webView = new WebView(getApplicationContext());
        //mWebContainer.addView(webView);
        
        webView = (WebView) findViewById(R.id.webview);
        //webView.setBackgroundColor(Color.BLACK);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setPluginsEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.setWebChromeClient(new WebChromeClient() {  
	        @Override  
	        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)  
	        {  
	            Log.d(Const.APP_TAG, message);
	            new AlertDialog.Builder(view.getContext()).setMessage(message).setCancelable(true).show();
	            result.confirm();
	            return true;
	        }
	    });
		
		webView.loadDataWithBaseURL("http://127.0.0.1",
				html, "text/html", "UTF-8", null);
    }
	
	protected void onDestroy() {
		super.onDestroy();
		//mWebContainer.removeAllViews();
		webView.destroy();
	}
	
	/*String html_live1 = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"></head>\n" + 
			"<body style=\"margin:0;margin-left:0;\">\n" + 
			"<script type='text/javascript' src='http://wpc.4B99.edgecastcdn.net/004B99/jw2011/jwplayer.js'></script>\n" + 
			"<div id='mediaplayer'>will load...</div>\n" + 
			"<script type=\"text/javascript\">\n" + 
			" jwplayer('mediaplayer').setup({\n" + 
			"  'width': '320',\n" + 
			"  'height': '160',\n" + 
			"  'image': 'http://wpc.4B99.edgecastcdn.net/004B99/jw/preview.jpg',\n" + 
			"  'modes': [{\n" + 
			"    'type': 'flash',\n" + 
			"    'src': 'http://wpc.4B99.edgecastcdn.net/004B99/jw2011/player.swf',\n" + 
			"    'config': {\n" + 
			"      'rtmp.subscribe': 'true',\n" + 
			"      'skin': 'http://wpc.4B99.edgecastcdn.net/004B99/jw/bekle.zip',\n" + 
			"      'dock': 'true',\n" + 
			"      'volume': '95',\n" + 
			"      'playlistfile': 'http://wpc.4B99.edgecastcdn.net/004B99/jw/goodtvpl1.xml',\n" + 
			"      'autostart' : 'true'\n" + 
			"    }},\n" + 
			"    {\n" + 
			"    'type': 'html5',\n" + 
			"    'config': {\n" + 
			"      'file': 'http://live.goodtv.org/live/smil:myStream.smil/playlist.m3u8',\n" + 
			"      'skin': 'http://wpc.4B99.edgecastcdn.net/004B99/jw/bekle.zip'\n" + 
			"    }}]\n" + 
			"});\n" + 
			"</script>" + 
			"</body>\n" + 
			"</html>";*/
	String html_live1 = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"></head>\r\n" + 
			"<body style=\"margin:0;margin-left:0;background-color: #000000;\">\r\n" + 
			"<script type='text/javascript' src='http://cdncache.goodtv.org/jw2011/jwplayer.js'></script>\r\n" + 
			"<div id='mediaplayer'>will load...</div>\r\n" + 
			"<script type=\"text/javascript\">\r\n" + 
			" var a = \r\n" + 
			" \"jwplayer('mediaplayer').setup({\\n\" +\r\n" + 
			"\"  'width': '\" + (screen.width-10) + \"',\\n\" +\r\n" + 
			"\"  'height': '\" + (screen.height-20) + \"',\\n\" +\r\n" + 
			//"\"  'width': '320',\\n\" +\r\n" + 
			//"\"  'height': '160',\\n\" +\r\n" + 
			"\"  'image': 'http://cdncache.goodtv.org/jw/preview.jpg',\\n\" +\r\n" + 
			"\"  'modes': [{\\n\" +\r\n" + 
			"\"    'type': 'flash',\\n\" +\r\n" + 
			"\"    'src': 'http://cdncache.goodtv.org/jw2011/player.swf',\\n\" +\r\n" + 
			"\"    'config': {\\n\" +\r\n" + 
			"\"      'rtmp.subscribe': 'true',\\n\" +\r\n" + 
			"\"      'skin': 'http://cdncache.goodtv.org/jw/bekle.zip',\\n\" +\r\n" + 
			"\"      'dock': 'true',\\n\" +\r\n" + 
			"\"      'volume': '95',\\n\" +\r\n" + 
			"\"      'playlistfile': 'http://cdncache.goodtv.org/jw/goodtv_seed_pl1.xml',\\n\" +\r\n" + 
			"\"      'autostart' : 'true'\\n\" +\r\n" + 
			"\"    }},\\n\" +\r\n" + 
			"\"    {\\n\" +\r\n" + 
			"\"    'type': 'html5',\\n\" +\r\n" + 
			"\"    'config': {\\n\" +\r\n" + 
			"\"      'file': 'http://live.goodtv.org/live/smil:myStream.smil/playlist.m3u8',\\n\" +\r\n" + 
			"\"      'skin': 'http://cdncache.goodtv.org/jw/bekle.zip'\\n\" +\r\n" + 
			"\"    }}]\\n\" +\r\n" + 
			"\"});\\n\";\r\n" + 
			"eval(a);\r\n" + 
			"</script> \r\n" + 
			"</body>\r\n" + 
			"</html>";
	
	/*String html_live2 = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"></head>\n" + 
			"<body style=\"margin:0;margin-left:0;\">\n" + 
			"<script type='text/javascript' src='http://wpc.4B99.edgecastcdn.net/004B99/jw2011/jwplayer.js'></script>\n" + 
			"<div id='mediaplayer'>will load...</div>\n" + 
			"<script type=\"text/javascript\">\n" + 
			" jwplayer('mediaplayer').setup({\n" + 
			"      'flashplayer': 'http://wpc.4B99.edgecastcdn.net/004B99/jw2011/player.swf',\n" + 
			"          'id': 'playerID',\n" + 
			"              'width': '320',\n" + 
			"                  'height': '160',\n" + 
			"                   'skin': 'http://wpc.4B99.edgecastcdn.net/004B99/jw/bekle.zip',\n" + 
			"                                'autostart': 'true',\n" + 
			"                                'dock': 'true',\n" + 
			"                                'volume': '95',\n" + 
			"                                'rtmp.subscribe': 'true',\n" + 
			"                                 'playlist': [\n" + 
			"        {\n" + 
			"           'title': 'GOODTV2 LIVE',\n" + 
			"           'provder': 'rtmp',\n" + 
			"           'image': 'http://wpc.4B99.edgecastcdn.net/004B99/jw/preview.jpg',\n" + 
			"           'streamer': 'rtmp://fms.4B99.edgecastcdn.net/204B99/goodtv2',\n" + 
			"           'levels': [\n" + 
			"	      { bitrate:\"314\", width:\"360\", file:\"live1\" },\n" + 
			"	      { bitrate:\"514\", width:\"360\", file:\"live2\" },\n" + 
			"              { bitrate:\"714\", width:\"480\", file:\"live3\" }\n" + 
			"           ]\n" + 
			"        }\n" + 
			"    ],\n" + 
			"    'plugins': { \n" + 
			"   } \n" + 
			"  });" + 
			"</script>" + 
			"</body>\n" + 
			"</html>";*/
	
	String html_live2 = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"></head>\r\n" + 
			"<body style=\"margin:0;margin-left:0;background-color: #000000; overflow: hidden;scrollbar-arrow-color:#000000;scrollbar-face-color:#000000;scrollbar-track-color:#000000;scrollbar-highlight-color:#000000;scrollbar-3dlight-color:#000000;scrollbar-shadow-color:#000000;scrollbar-darkshadow-color:#000000;\">\r\n" + 
			"<script type='text/javascript' src='http://cdncache.goodtv.org/jw2011/jwplayer.js'></script>\r\n" + 
			"<div id='mediaplayer'>will load...</div>\r\n" + 
			"<script type=\"text/javascript\">\r\n" + 
			"var a = \r\n" + 
			"\"jwplayer('mediaplayer').setup({\\n\" +\r\n" + 
			"\"      'flashplayer': 'http://cdncache.goodtv.org/jw2011/player.swf',\\n\" +\r\n" + 
			"\"          'id': 'playerID',\\n\" +\r\n" + 
			"\"  'width': '\" + (screen.width-10) + \"',\\n\" +\r\n" + 
			"\"  'height': '\" + (screen.height-20) + \"',\\n\" +\r\n" + 
			//"			\"  'width': '320',\\n\" +\r\n" + 
			//"			\"  'height': '160',\\n\" +\r\n" + 
			"\"                   'skin': 'http://cdncache.goodtv.org/jw/bekle.zip',\\n\" +\r\n" + 
			"\"                   'autostart': 'true', 'dock': 'true', 'volume': '95', 'rtmp.subscribe': 'true',\\n\" +\r\n" + 
			"\"                                 'playlist': [\\n\" +\r\n" + 
			"\"        {\\n\" +\r\n" + 
			"\"           'title': 'GOODTV2 LIVE',\\n\" +\r\n" + 
			"\"           'provder': 'rtmp',\\n\" +\r\n" + 
			"\"           'image': 'http://cdncache.goodtv.org/jw/preview.jpg',\\n\" +\r\n" + 
			"\"           'streamer': 'rtmp://amznelb.goodtv.org/live2',\\n\" +\r\n" + 
			"\"           'levels': [\\n\" +\r\n" + 
/*			"\"	      { bitrate:'300', width:'360', file:'origin_live2_1.stream' },\\n\" +\r\n" + 
			"\"	      { bitrate:'500', width:'360', file:'origin_live2_2.stream' },\\n\" +\r\n" + */
			"\"              { bitrate:'700', width:'480', file:'origin_live2_3.stream' }\\n\" +\r\n" + 
			"\"           ]\\n\" +\r\n" + 
			"\"        }\\n\" +\r\n" + 
			"\"    ]\\n\" +\r\n" + 
			"\"  });\\n\";\r\n" + 
			"eval(a);\r\n" + 
			"</script>\r\n" + 
			"</body>\r\n" + 
			"			</html>";
}

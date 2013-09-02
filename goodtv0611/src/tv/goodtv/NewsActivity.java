package tv.goodtv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.fedorvlasov.lazylist.ImageLoader;

public class NewsActivity extends Activity {

	private MyReceiver myReceiver;
	private ImageView imageView;
	
	public ImageLoader imageLoader;
	
	protected boolean isNews() {
		return true;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.news);
        
        imageView = (ImageView)findViewById(R.id.img);
        imageLoader = new ImageLoader(getApplicationContext());
        
        setProgressBarIndeterminateVisibility(true);
	}
	
	@Override
    protected void onStart() {
    	myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NewsService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);
        
        Intent intent = new Intent(this, NewsService.class);
        startService(intent);
    	
        super.onStart();
    }
	
	@Override
    protected void onStop() {
		unregisterReceiver(myReceiver);
		
		Intent intent = new Intent(this, NewsService.class);
        stopService(intent);
	
    	super.onStop();
    }
	
	@Override
	protected void onDestroy() {
		imageView.setImageBitmap(null);
		imageView = null;
		super.onDestroy();
	}

	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Bundle bundle = intent.getExtras();
			boolean success = bundle.getBoolean("success");
			setProgressBarIndeterminateVisibility(false);
			
			Intent intent3 = new Intent(Intent.ACTION_VIEW);  
			intent3.setData(Uri.parse("http://w2.goodtv.tv/mobile/news.html"));
			startActivity(intent3);
			finish();
/*			
			if (success) {
				
               //setImageBitmap error Exception 3.x 4.x OK	, 2.x error 無  Exception						
				try {
					String imageFile = bundle.getString("imageFile");
					//imageLoader.DisplayImage(imageFile, imageView);
					imageView.setImageBitmap(HttpUtils.readICON(imageFile));
					return;
				} catch (Exception e) {}

			}
		
			Intent intent3 = new Intent(Intent.ACTION_VIEW);  
			intent3.setData(Uri.parse("http://w2.goodtv.tv/mobile/news.html"));
			startActivity(intent3);
			finish();
*/			
		}
	}
}

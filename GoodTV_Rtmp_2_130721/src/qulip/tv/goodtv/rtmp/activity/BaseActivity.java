package qulip.tv.goodtv.rtmp.activity;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 此與 BaseListActivity code 一樣, 因無多重繼承所以需要分開. 將重複的 code 拉到 MenuProcess
 */
public class BaseActivity extends Activity {
	
	protected boolean isHome() {
		return false;
	}
	protected boolean isSave() {
		return false;
	}
	protected boolean isSearch() {
		return false;
	}
	protected boolean isNews() {
		return false;
	}
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuProcess.createMenu(getLayoutInflater(), menu, isHome(), isSave(), isSearch(), isNews());
    	return true;
	}
    
    public boolean onOptionsItemSelected(MenuItem item)
	{
    	super.onOptionsItemSelected(item);
		MenuProcess.selectItem(this, item);
	    return true;
	}
}
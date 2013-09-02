package qulip.tv.goodtv.rtmp.activity;

import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;

public class BaseListActivity extends ListActivity {
	
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
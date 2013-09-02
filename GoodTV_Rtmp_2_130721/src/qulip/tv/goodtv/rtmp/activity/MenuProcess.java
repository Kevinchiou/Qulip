package qulip.tv.goodtv.rtmp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MenuProcess {

	protected static final int MENU_HOME = Menu.FIRST;
	protected static final int MENU_SAVE = Menu.FIRST+1;
	protected static final int MENU_SEARCH = Menu.FIRST+2;
	protected static final int MENU_NEWS = Menu.FIRST+3;
	protected static final int MENU_ABOUT = Menu.FIRST+4;
	protected static final int MENU_FEEDBACK = Menu.FIRST+5;
	
	public static void createMenu(final LayoutInflater layoutInflater, Menu menu, boolean isHome, boolean isSave, boolean isSearch, boolean isNews) {
		//為了將 menu item 背景設為黑色才需此段
    	layoutInflater.setFactory(new Factory() {
            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
                    try{
                        LayoutInflater f = layoutInflater;
                        final View view = f.createView(name, null, attrs);
                        new Handler().post(new Runnable() {
                            public void run() {
                                // set the background drawable
                                view.setBackgroundColor(Color.BLACK);
                                // set the text color
                                ((TextView) view).setTextColor(Color.WHITE);
                            }
                        });
                        return view;
                    } catch (InflateException e) {
                        } catch (ClassNotFoundException e) {}
                }
                return null;
            }
        });
	    if (!isHome) {
	    	menu.add(Menu.NONE, MENU_HOME, 0, "首頁");	//.setIcon(R.drawable.icon_home);
	    }
	    if (!isSave) {
	    	menu.add(Menu.NONE, MENU_SAVE, 0, "收藏");	//.setIcon(R.drawable.icon_donat);
	    }
	    if (!isSearch) {
	    	menu.add(Menu.NONE, MENU_SEARCH, 0, "搜尋");	//.setIcon(R.drawable.icon_map);
	    }
	    if (!isNews) {
	    	menu.add(Menu.NONE, MENU_NEWS, 0, "NEWS");	//.setIcon(R.drawable.icon_news);
	    }
	    menu.add(Menu.NONE, MENU_ABOUT, 0, "關於");	//.setIcon(R.drawable.icon_about);
	    menu.add(Menu.NONE, MENU_FEEDBACK, 0, "問題反應");
	}
	
	public static void selectItem(Activity activity, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_HOME:
			Intent intent = new Intent();
			intent.setClass(activity, MainActivity.class);
			activity.startActivity(intent);
			activity.finish();
			break;
		case MENU_SAVE:
			Intent intent1 = new Intent();
			intent1.setClass(activity, SaveActivity.class);
			activity.startActivity(intent1);
			break;
		case MENU_SEARCH:
			Intent intent2 = new Intent();
			intent2.setClass(activity, SearchActivity.class);
			activity.startActivity(intent2);
			break;
		case MENU_NEWS:
			Intent intent3 = new Intent(Intent.ACTION_VIEW);  
			//intent3.setData(Uri.parse("http://www.goodtv.tv/mobile/news"));
			intent3.setClass(activity, NewsActivity.class);
			activity.startActivity(intent3);
			break;
		case MENU_ABOUT:
			openOptionsDialog(activity);
			break;
		case MENU_FEEDBACK:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			//emailIntent.setType("text/plain");
			emailIntent.setType("application/octet-stream");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"goodtv.live@qulip.com"});
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "問題報告");
			activity.startActivity(Intent.createChooser(emailIntent, "Choose Email Client."));
			break;
		}
	}
	
	private static void openOptionsDialog(Activity activity) {
		new AlertDialog.Builder(activity)
        .setTitle("關於本程式")
        .setMessage("1.GOOD TV好消息衛星電視台，是一個基督教的電視台，我們的期待是：關懷社會中的個人與家庭，我們的使命是：帶給人們 信心、希望、與真愛。\n" + 
        		"GOOD TV邀請您一起同工，我們需要您的代禱與奉獻，讓福音遍傳永不止息。\n" + 
        		"\n" + 
        		"2. 本程式為QULIP所開之YouTube/GoodTV 播放清單播放軟體。影片來源為GOOD TV 所上傳之影片與播放清單。所有影片，照片之著作權皆為GOOD TV 所持有，QULIP 保有軟體本身之著作權。\n" + 
        		"\n" + 
        		"3. 本程式為動態擷取網路最新影片版本, 若是網路速度不夠快, 目錄擷取可能會有錯誤訊息, 請等後5-10秒鐘再試試, 取決於網路的速度。\n" + 
        		"\n" + 
        		"4. 建議使用 YouTube Player 播放, 來享受最佳的流暢度與畫質。\n" + 
        		"\n" + 
        		"要常常喜樂，不住地禱告，凡事謝恩；因為這是　神在基督耶穌裏向你們所定的旨意。(帖撒羅尼迦前書 5: 16-18)\n" + 
        		"")
        .setPositiveButton("確定",
            new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialoginterface, int i){
                	
                }})
        .show();
	}
}

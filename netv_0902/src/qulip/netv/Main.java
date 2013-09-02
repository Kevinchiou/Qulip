package qulip.netv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import qulip.netv.R;
import com.netv.vo.CatalogVO;

public class Main extends ListActivity implements OnClickListener {
    
	protected static final int MENU_ABOUT = Menu.FIRST;
	protected static final int MENU_FEEDBACK = Menu.FIRST+1;
	protected static final int MENU_NEWS = Menu.FIRST+2;
	protected static final int MENU_SAVE = Menu.FIRST+3;
	
	private List<CatalogVO> catalogs;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //設定ListView未取得內容時顯示的view, empty建構在list.xml中。
        getListView().setEmptyView(findViewById(R.id.empty));
        
        //自訂方法載入ListView值
        catalogs = getCatalogs();
        setListAdapter(new CatalogAdapter(this, catalogs));
        
        DataSet.getInstace().setCatalogs(catalogs);
        
        this.startService(new Intent(this, FetchService.class));
        
        Button btn = (Button)findViewById(R.id.searchBtn);
        btn.setOnClickListener(this);
    }
    
    //當ListView的項目被按下
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		
		if (DataSet.getInstace().getStatus() == Const.STATUS_ERROR) {
			if (DataSet.getInstace().isNetworkError()) {
				Toast popup =  Toast.makeText(Main.this, "網路連線有誤!", Toast.LENGTH_SHORT);
				popup.show();
				return;
			} else {
				Toast popup =  Toast.makeText(Main.this, "非預期錯誤:" + DataSet.getInstace().getMessage(), Toast.LENGTH_SHORT);
				popup.show();
				return;
			}
		}
		
		if (DataSet.getInstace().getStatus() != Const.STATUS_PLAYLIST_DATA_READY) {
			Toast popup =  Toast.makeText(Main.this, "還未下載完清單, 請稍後再試!", Toast.LENGTH_SHORT);
			popup.show();
			return;
		} else {
			Intent intent = new Intent();
			intent.setClass(this, Playlist.class);
			Bundle bundle = new Bundle();
			bundle.putInt("idx", position);
			bundle.putInt("type", 1);
			intent.putExtras(bundle);
			startActivity(intent);
		}
		
		super.onListItemClick(l, v, position, id);
    }
	
	@Override
	public void onClick(View v) {
		EditText editText = (EditText)findViewById(R.id.editText1);
		if (editText.getText().toString().trim().length() > 0) {
			DataSet.getInstace().setKeyword(editText.getText().toString().trim());
			Intent intent = new Intent();
			intent.setClass(this, Playlist.class);
			Bundle bundle = new Bundle();
			bundle.putInt("type", 3);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.stopService(new Intent(this, FetchService.class));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    menu.add(Menu.NONE, MENU_ABOUT, 0, "關於本程式");
	    menu.add(Menu.NONE, MENU_FEEDBACK, 0, "問題反應");
	    menu.add(Menu.NONE, MENU_NEWS, 0, "NEWS");
	    menu.add(Menu.NONE, MENU_SAVE, 0, "收藏");
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_ABOUT:
			openOptionsDialog();
			break;
		case MENU_FEEDBACK:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			//emailIntent.setType("text/plain");
			emailIntent.setType("application/octet-stream");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"netv@qulip.com"});
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "問題報告");
			startActivity(Intent.createChooser(emailIntent, "Choose Email Client."));
			break;
		case MENU_NEWS:
			Intent intent3 = new Intent(Intent.ACTION_VIEW);  
			intent3.setData(Uri.parse("http://www.netv.org.tw/epaper/epaperqry.asp"));
			startActivity(intent3);
			break;
		case MENU_SAVE:
			Intent intent4 = new Intent(Intent.ACTION_VIEW);  
			intent4.setClass(this, SaveActivity.class);
			startActivity(intent4);
			break;
		}
	      return true;
	}
	
	private void openOptionsDialog() {
		new AlertDialog.Builder(Main.this)
        .setTitle("關於本程式")
        .setMessage("1. 新眼光電視台，是一個基督教的電視台，我們以基督之精神成立電台，除為服務國內外的基督徒之外，並以全體國民為關心對象，這個電視台不只是長老教會的電視台，更是一個公益關懷與生活藝文的電視台我們期許成為社會媒體的一股清流，透過傳播，使人們有「新的眼光」去看待事物，「新的啟發」、「新的體驗」、「新的生命」，最後得到「新的喜悅」，並能為台灣社會營造富裕的心並帶來更多上帝的祝福，展現 上帝對台灣的愛，讓台灣人民在「信」「望」「愛」中建立永恆幸福的根基。\n" + 
        		"\n" + 
        		"2. 本程式為QULIP所開之YouTube 播放清單播放軟體。影片來源為”新眼光電視台” 所上傳之影片與播放清單。所有影片，照片之著作權皆為”新眼光電視台” 所持有，QULIP 保有軟體本身之著作權。\n" + 
        		"\n" + 
        		"3. 本程式為動態擷取網路最新影片版本, 若是網路速度不夠快, 目錄擷取可能會有錯誤訊息, 請等後5-10秒鐘再試試, 取決於網路的速度。\n" + 
        		"\n" + 
        		"4. 建議使用 YouTube Player 播放, 來享受最佳的流暢度與畫質。\n" + 
        		"\n" + 
        		"要常常喜樂，不住地禱告，凡事謝恩；因為這是　神在基督耶穌裏向你們所定的旨意。(帖撒羅尼迦前書 5: 16-18)" + 
        		"")
        .setPositiveButton("確定",
            new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialoginterface, int i){
                	
                }})
        .show();
	}
	
    private List<CatalogVO> getCatalogs() {
    	Log.d(Const.APP_TAG, "start to get catalog");
    	try {
	    	SAXParserFactory spf = SAXParserFactory.newInstance();
		    SAXParser sp = spf.newSAXParser();
			XMLReader reader = sp.getXMLReader();
			List<CatalogVO> records = new ArrayList<CatalogVO>();
			ContentHandler contentHandler = new RecordContentHandler(records);
			reader.setContentHandler(contentHandler);
			reader.parse(new InputSource(getResources().getAssets().open("catalog.xml")));
			for (CatalogVO v : records) {
				Log.d(Const.APP_TAG, v.getIcon() + "," + v.getTitle() + "," + v.getDesc() + "," + v.getPlaylistId() + "," + v.getSubs().size());
				for (CatalogVO v0 : v.getSubs()) {
					Log.d(Const.APP_TAG, "   ==>" + v0.getTitle() + "," + v0.getDesc() + "," + v0.getPlaylistId());
				}
			}
			return records;
    	} catch (Exception e) {
    		Log.e(Const.APP_TAG, "error", e);
    		return new ArrayList<CatalogVO>();
    	}
    }
    
    class RecordContentHandler implements ContentHandler {

		private StringBuffer buf = new StringBuffer();
		private String tag = null;
		private List<CatalogVO> catalogs = null;
		private Map<String, String> atts = new HashMap<String, String>();
		private CatalogVO parent = null;
		private CatalogVO curr = null;
		
		RecordContentHandler(List<CatalogVO> records) {
			this.catalogs = records;
		}
		
		@Override
		public void characters(char[] chars, int start, int length)
				throws SAXException {
			buf.append(chars,start,length);
		}
		
		@Override
		public void startElement(String namespaceURI, String localName, String fullName,
				Attributes attributes) throws SAXException {
			buf.setLength(0);
			tag = localName;
			if (tag == null || tag.trim().length() == 0) {
				tag = fullName;
			}
			for (int i=0; i<attributes.getLength(); i++) {
				atts.put(attributes.getLocalName(i), attributes.getValue(i));
			}
			if (tag.equals("item")) {
				curr = new CatalogVO();
				curr.setIcon(atts.get("icon"));	
				catalogs.add(curr);
				parent = curr;
			}
			if (tag.equals("subitem")) {
				CatalogVO sub = new CatalogVO();
				//sub.setIcon(atts.get("icon"));	
				parent.addSub(sub);
				curr = sub;
			}
		}

		@Override
		public void endElement(String namespaceURI, String localName, String fullName)
				throws SAXException {
			String text = buf.toString();
			if (tag.equals("title")) {
				curr.setTitle(text);
			}
			if (tag.equals("desc")) {
				curr.setDesc(text);
			}
			if (tag.equals("playlist")) {
				curr.setPlaylistId(text);
			}
			tag = "";
			atts.clear();
		}

		@Override
		public void endDocument() throws SAXException {
			
		}

		@Override
		public void endPrefixMapping(String arg0) throws SAXException {
			
		}

		@Override
		public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
				throws SAXException {
			
		}

		@Override
		public void processingInstruction(String arg0, String arg1)
				throws SAXException {
			
		}

		@Override
		public void setDocumentLocator(Locator arg0) {
			
		}

		@Override
		public void skippedEntity(String arg0) throws SAXException {
			
		}

		@Override
		public void startDocument() throws SAXException {
			
		}

		@Override
		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			
		}
		
	}
}
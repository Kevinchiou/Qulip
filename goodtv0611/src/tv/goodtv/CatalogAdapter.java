package tv.goodtv;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

import tv.goodtv.vo.CatalogVO;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CatalogAdapter extends BaseAdapter{	
	
	private Context ctxt;
	private LayoutInflater myInflater;
	List<CatalogVO> catalogs = null;
   
	public CatalogAdapter(Context ctxt, List<CatalogVO> catalogs){
		myInflater = LayoutInflater.from(ctxt);
		this.ctxt = ctxt;
		this.catalogs = catalogs;
	}
	
	@Override
	public int getCount()
	{
		return catalogs.size();
	}

	@Override
	public Object getItem(int position)
	{
		return catalogs.get(position);
	}
  
	@Override
	public long getItemId(int position)
	{
		return position;
	}
  
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//自訂類別，表達個別listItem中的view物件集合。
		ViewTag viewTag;
		
		if(convertView == null){
			Log.d(Const.APP_TAG, "null!");
			//取得listItem容器 view
			convertView = myInflater.inflate(R.layout.catalog, null);
			
			//建構listItem內容view
			viewTag = new ViewTag(
					(ImageView)convertView.findViewById(R.id.CatalogAdapter_ImageView_icon),
					(TextView) convertView.findViewById(R.id.CatalogAdapter_TextView_title)
					);
			
			//設置容器內容
			convertView.setTag(viewTag);
		}
		else{
			Log.d(Const.APP_TAG, "find!");
			viewTag = (ViewTag) convertView.getTag();
		}
		
		//設定內容圖案
		CatalogVO catalog = catalogs.get(position);
		DecimalFormat df = new DecimalFormat("00");
		try {
			InputStream is = ctxt.getResources().getAssets().open("catalog/" + catalog.getIcon());
			Bitmap icon = BitmapFactory.decodeStream(is);
			viewTag.icon.setImageBitmap(icon);
		} catch (Exception e) {
			Log.e(Const.APP_TAG, e.toString(), e);
		}
		
		//設定內容文字
		viewTag.title.setText(catalog.getTitle());
		
		return convertView;
	}
}

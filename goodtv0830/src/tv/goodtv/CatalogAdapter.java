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
		//�ۭq���O�A��F�ӧOlistItem����view���󶰦X�C
		ViewTag viewTag;
		
		if(convertView == null){
			Log.d(Const.APP_TAG, "null!");
			//���olistItem�e�� view
			convertView = myInflater.inflate(R.layout.catalog, null);
			
			//�غclistItem���eview
			viewTag = new ViewTag(
					(ImageView)convertView.findViewById(R.id.CatalogAdapter_ImageView_icon),
					(TextView) convertView.findViewById(R.id.CatalogAdapter_TextView_title)
					);
			
			//�]�m�e�����e
			convertView.setTag(viewTag);
		}
		else{
			Log.d(Const.APP_TAG, "find!");
			viewTag = (ViewTag) convertView.getTag();
		}
		
		//�]�w���e�Ϯ�
		CatalogVO catalog = catalogs.get(position);
		DecimalFormat df = new DecimalFormat("00");
		try {
			InputStream is = ctxt.getResources().getAssets().open("catalog/" + catalog.getIcon());
			Bitmap icon = BitmapFactory.decodeStream(is);
			viewTag.icon.setImageBitmap(icon);
		} catch (Exception e) {
			Log.e(Const.APP_TAG, e.toString(), e);
		}
		
		//�]�w���e��r
		viewTag.title.setText(catalog.getTitle());
		
		return convertView;
	}
}

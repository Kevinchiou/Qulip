package qulip.tv.goodtv.rtmp;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

class ViewTag{
	ImageView icon;
	TextView title;
	TextView desc;
	TextView count;
	Button btn;

	public ViewTag(ImageView icon, TextView title, TextView desc, TextView count, Button btn) {
		this.icon = icon;
		this.title = title;
		this.desc = desc;
		this.count = count;
		this.btn = btn;
	}
}
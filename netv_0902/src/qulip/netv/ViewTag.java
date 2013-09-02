package qulip.netv;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

class ViewTag{
	ImageView icon;
	TextView title;
	TextView desc;
	TextView date;
	Button button;

	public ViewTag(ImageView icon, TextView title, TextView desc){
		this.icon = icon;
		this.title = title;
		this.desc = desc;
	}

	public ViewTag(ImageView icon, TextView title, TextView desc, TextView date, Button button) {
		this.icon = icon;
		this.title = title;
		this.desc = desc;
		this.date = date;
		this.button = button;
	}
}
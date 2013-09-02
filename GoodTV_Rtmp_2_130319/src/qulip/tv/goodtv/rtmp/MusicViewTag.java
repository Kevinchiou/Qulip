package qulip.tv.goodtv.rtmp;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

class MusicViewTag{
	TextView title;
	TextView desc;
	Button btn;


	public MusicViewTag(TextView title, TextView desc, Button btn) {
		this.title = title;
		this.desc = desc;
		this.btn = btn;

	}
}

class DragMusicViewTag{
	TextView title;
	TextView desc;
	Button btn;
	ImageView img;

	public DragMusicViewTag(TextView title, TextView desc, Button btn, ImageView img) {
		this.title = title;
		this.desc = desc;
		this.btn = btn;
		this.img = img;
	}
}
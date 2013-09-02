package qulip.tv.goodtv.rtmp.vo;

import android.graphics.Bitmap;

public class VideoVO {

	private String id;
	private String author;
	private String uploaded;
	private String updated;
	private String uploader;
	private String title;
	private String description;
	private String sqDefault;
	private String hqDefault;
	private int viewCount;
	
	private Bitmap icon;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public String getDisplayDescription() {
		String str = description;
		if (str == null) {
			return "";
		}
		str = str.trim();
		if (str.length() > 30) {
			str = str.substring(0, 30) + "...";
		}
		return str;
	}
	public String getUploadDate() {
		if (uploaded != null && uploaded.length()>10) {
			return uploaded.substring(0, 10);
		}
		return uploaded;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUploader() {
		return uploader;
	}
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	public String getSqDefault() {
		return sqDefault;
	}
	public void setSqDefault(String sqDefault) {
		this.sqDefault = sqDefault;
	}
	public String getHqDefault() {
		return hqDefault;
	}
	public void setHqDefault(String hqDefault) {
		this.hqDefault = hqDefault;
	}
	public String getUploaded() {
		return uploaded;
	}
	public void setUploaded(String uploaded) {
		this.uploaded = uploaded;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Bitmap getIcon() {
		return icon;
	}
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
	public int getViewCount() {
		return viewCount;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
}

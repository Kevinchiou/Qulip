package com.netv.vo;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class PlaylistVO {

	private String id;
	private String created;
	private String updated;
	private String author;
	private String title;
	private String description;
	private int size;
	
	private String sqDefault;
	private String hqDefault;
	
	private Bitmap icon;
	
	private List<VideoVO> videos = new ArrayList<VideoVO>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
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
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
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
	public List<VideoVO> getVideos() {
		return videos;
	}
	public void setVideos(List<VideoVO> videos) {
		this.videos = videos;
	}
	public Bitmap getIcon() {
		return icon;
	}
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
}

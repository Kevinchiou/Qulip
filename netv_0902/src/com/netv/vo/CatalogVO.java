package com.netv.vo;

import java.util.ArrayList;
import java.util.List;

public class CatalogVO {

	private String title;
	private String desc;
	private String icon;
	private String playlistId;
	private List<CatalogVO> subs = new ArrayList<CatalogVO>();
	private List<PlaylistVO> playlists = new ArrayList<PlaylistVO>();
	
	public void addSub(CatalogVO vo) {
		subs.add(vo);
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public List<CatalogVO> getSubs() {
		return subs;
	}
	public void setSubs(List<CatalogVO> subs) {
		this.subs = subs;
	}
	public List<PlaylistVO> getPlaylists() {
		return playlists;
	}
	public void setPlaylists(List<PlaylistVO> playlists) {
		this.playlists = playlists;
	}
	public String getPlaylistId() {
		return playlistId;
	}
	public void setPlaylistId(String playlistId) {
		this.playlistId = playlistId;
	}
}

package tv.goodtv.vo;

import java.util.ArrayList;
import java.util.List;

public class CatalogVO {

	private String title;
	private String icon;
	private int type;
	
	private List<PlaylistVO> playlists = new ArrayList<PlaylistVO>();
	
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<PlaylistVO> getPlaylists() {
		return playlists;
	}
	public void setPlaylists(List<PlaylistVO> playlists) {
		this.playlists = playlists;
	}
}

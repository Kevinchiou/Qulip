package qulip.netv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.netv.vo.CatalogVO;
import com.netv.vo.PlaylistVO;

public class DataSet {

	private static DataSet self = new DataSet();
	
	private int status = Const.STATUS_INIT;
	private String message = "";
	private boolean networkError = false;
	
	private List<CatalogVO> catalogs;
	private Map<String, PlaylistVO> playlistMap = new HashMap<String, PlaylistVO>();
	
	private String keyword;
	
	private DataSet() {}
	
	public void reset() {
		try {
			networkError = false;
			status = Const.STATUS_INIT;
			message = "";
			for (CatalogVO vo : catalogs) {
				vo.getPlaylists().clear();
			}
		} catch (Exception e) {}
	}
	
	public static DataSet getInstace() {
		return self;
	}
	
	public CatalogVO getCatalog(int idx) {
		return catalogs.get(idx);
	}

	public List<CatalogVO> getCatalogs() {
		return catalogs;
	}
	
	public void setCatalogs(List<CatalogVO> catalogs) {
		this.catalogs = catalogs;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
		Log.d(Const.APP_TAG, "change status to " + status);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isNetworkError() {
		return networkError;
	}

	public void setNetworkError(boolean networkError) {
		this.networkError = networkError;
	}

	public Map<String, PlaylistVO> getPlaylistMap() {
		return playlistMap;
	}

	public void setPlaylistMap(Map<String, PlaylistVO> playlistMap) {
		this.playlistMap = playlistMap;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	
	
}

package tv.goodtv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tv.goodtv.vo.VideoVO;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VideoSQLiteHelper extends SQLiteOpenHelper {

	private static final String TAG = Const.APP_TAG;
	private static final String dbName = "video.db";
	
	public VideoSQLiteHelper(Context context, CursorFactory factory, int version) {
		super(context, dbName, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "SQLiteHelper onCreate.");
		String sql = "create table if not exists video(" +
				"id integer primary key, " +	//SQLite ?ƒè‡ª?•ç”¢?Ÿæ?æ°´è? insert
				"title varchar, " +
				"desc varchar, " +
				"video_id varchar, " +
				//"view_count integer, " +
				"icon_path varchar, " +
				"add_time varchar " +
				")";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "SQLiteHelper onUpgrade.");
		String sql = "drop table video";
		db.execSQL(sql);
		onCreate(db);
	}

	public void insert(VideoVO video) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			ContentValues values = new ContentValues();
			values.put("title", video.getTitle());
			values.put("desc", video.getDescription());
			values.put("video_id", video.getId());
			values.put("icon_path", video.getSqDefault());
			values.put("add_time", sdf.format(new Date()));
			db.insert("video", null, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public List<VideoVO> queryAll() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query("video", null, null, null, null, null, "add_time desc");
			int titleIndex = cursor.getColumnIndex("title");
			int descIndex = cursor.getColumnIndex("desc");
			int videoIdIndex = cursor.getColumnIndex("video_id");
			int viewCountIndex = cursor.getColumnIndex("view_count");
			int iconPathIndex = cursor.getColumnIndex("icon_path");
			
			List<VideoVO> result = new ArrayList<VideoVO>();
			for (int i=0; i<cursor.getCount(); i++) {
				cursor.moveToNext();
				VideoVO v = new VideoVO();
				v.setTitle(cursor.getString(titleIndex));
				v.setDescription(cursor.getString(descIndex));
				v.setId(cursor.getString(videoIdIndex));
				//v.setViewCount(cursor.getInt(viewCountIndex));
				v.setSqDefault(cursor.getString(iconPathIndex));
				result.add(v);
			}
			Log.d(TAG, "total size:" + result.size());
			return result;
		} finally {
			if (cursor != null) {
				try { cursor.close(); } catch (Exception e) {}
			}
			db.close();
		}
	}
	
	public boolean exist(String id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query("video", null, "video_id=?", new String[]{id}, null, null, "add_time desc");
			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				try { cursor.close(); } catch (Exception e) {}
			}
			db.close();
		}
	}
	
	public void delete(String id) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("video", "video_id=?", new String[] {id});  
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
}

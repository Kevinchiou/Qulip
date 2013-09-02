package qulip.tv.goodtv.rtmp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import qulip.tv.goodtv.rtmp.vo.AudioBP;
import qulip.tv.goodtv.rtmp.vo.AudioVO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class AudioSQLiteHelper extends SQLiteOpenHelper {

	private static final String TAG = Const.APP_TAG;
	private static final String dbName = "audios.db";
	
	public AudioSQLiteHelper(Context context, CursorFactory factory, int version) {
		super(context, dbName, factory, version);
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "AudioSQLiteHelper onCreate.");
		String sql1 = "create table if not exists audio(" +
				"id integer primary key autoincrement, " +	//SQLite 會自動產生流水號 insert
				"title varchar, " +
				"desc varchar, " +
				"audio_id varchar, " +
				"add_time varchar " +
				")";
		db.execSQL(sql1);

	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "AudioSQLiteHelper onUpgrade.");
		db.execSQL("drop table if exists audio");	
/*        db.execSQL("drop table if exists breakpoint");  */
		onCreate(db);
	}


	public void insertAudio(AudioVO audio) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			ContentValues values = new ContentValues();
			values.put("title", audio.getTitle());
			values.put("desc", audio.getDescription());
			values.put("audio_id", audio.getId());
			values.put("add_time", sdf.format(new Date()));
			long ok =db.insert("audio", null, values);
			if (ok == -1){
				Log.d(TAG, "insertAudio error!");
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	

	public List<AudioVO> queryAllAudio() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query("audio", null, null, null, null, null, "add_time desc");
			int titleIndex = cursor.getColumnIndex("title");
			int descIndex = cursor.getColumnIndex("desc");
			int audioIdIndex = cursor.getColumnIndex("audio_id");
			
			List<AudioVO> result = new ArrayList<AudioVO>();
			for (int i=0; i<cursor.getCount(); i++) {
				cursor.moveToNext();
				AudioVO v = new AudioVO();
				v.setTitle(cursor.getString(titleIndex));
				v.setDescription(cursor.getString(descIndex));
				v.setId(cursor.getString(audioIdIndex));
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
	
	public boolean existAudio(String id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query("audio", null, "audio_id=?", new String[]{id}, null, null, "add_time desc");
			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				try { cursor.close(); } catch (Exception e) {}
			}
			db.close();
		}
	}
	
	public void deleteAudio(String id) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("audio", "audio_id=?", new String[] {id});  
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public void deleteAllAudio() {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("drop table if exists audio");	
 	    	onCreate(db);  
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}	
	
}

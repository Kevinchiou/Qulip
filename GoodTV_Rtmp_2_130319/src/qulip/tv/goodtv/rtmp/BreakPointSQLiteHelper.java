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

public class BreakPointSQLiteHelper extends SQLiteOpenHelper {

	private static final String TAG = Const.APP_TAG;
	private static final String dbName = "breakpoints.db";
	
	public BreakPointSQLiteHelper(Context context, CursorFactory factory, int version) {
		super(context, dbName, factory, version);
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "BreakPointSQLiteHelper onCreate.");
		
		String sql = "create table if not exists breakpoint(" +
				"id integer primary key autoincrement, " +	//SQLite 會自動產生流水號 insert
				"type integer, " +	//0 :自動播放 , 1:清單播放
				"chapter integer, " +
				"maxpos integer, " +
				"curpos integer, " +
				"title varchar, " +
				"description varchar, " +
				"audio_id varchar, " +
				"add_time varchar " +  
				")";
		db.execSQL(sql);		
		
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "BreakPointSQLiteHelper onUpgrade.");
		db.execSQL("drop table if exists breakpoint");
		onCreate(db);
	}


	public void updateBreakpoint(int type,int chapter,int maxposition,int curposition,String audio_id,String title,String description) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			ContentValues values = new ContentValues();
			values.put("type",type);
			values.put("chapter", chapter);
			values.put("maxpos", maxposition); 
			values.put("curpos", curposition);  
			values.put("title", title);
			values.put("description", description);
            values.put("audio_id",audio_id);			
			values.put("add_time", sdf.format(new Date()));
	//		String where = "type = "+ String.valueOf(type);
			long ok = db.update("breakpoint", values, "type=?",  new String[]{String.valueOf(type)});			
	//		long ok = db.update("breakpoint", values, where, null);
			if (ok == -1){
				Log.d(TAG, "updateBreakpoint error!");
			}
		
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	

	public void insertBreakpoint(int type,int chapter,int maxposition,int curposition,String audio_id,String title,String description) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			ContentValues values = new ContentValues();
			values.put("type",type);
			values.put("chapter", chapter);
			values.put("maxpos", maxposition);  
			values.put("curpos", curposition);  
			values.put("title", title);
			values.put("description", description);
            values.put("audio_id",audio_id);			
			values.put("add_time", sdf.format(new Date()));  
			long ok = db.insert("breakpoint", null, values);
			if (ok == -1){
				Log.d(TAG, "insertBreakpoint error!");
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}	
	
	
	public AudioBP queryBreakpoint(int type){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query("breakpoint", null, "type=?", new String[]{String.valueOf(type)}, null, null, null);
			int typeIndex = cursor.getColumnIndex("type");
			int chapterIndex = cursor.getColumnIndex("chapter");
			int maxposIndex = cursor.getColumnIndex("maxpos");
			int curposIndex = cursor.getColumnIndex("curpos");
			int titleIndex = cursor.getColumnIndex("title");
			int descriptionIndex = cursor.getColumnIndex("description");
			int audioidIndex = cursor.getColumnIndex("audio_id");			
			cursor.moveToFirst();
			AudioBP bp = new AudioBP();
			bp.setType(cursor.getInt(typeIndex));
			bp.setChapter(cursor.getInt(chapterIndex));
			bp.setMaxPos(cursor.getInt(maxposIndex));
			bp.setCurPos(cursor.getInt(curposIndex));
			bp.setTitle(cursor.getString(titleIndex));
			bp.setDescription(cursor.getString(descriptionIndex));
			bp.setAudioId(cursor.getString(audioidIndex));			
			return bp;
		} finally {
			if (cursor != null) {
				try { cursor.close(); } catch (Exception e) {}
			}
			db.close();
		}
	}	
	
	public void deleteBreakPoint(int type, String id) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete("breakpoint", "type=? and audio_id=?", new String[] {String.valueOf(type),id});  
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	
	public boolean existBreakPoint(int type) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query("breakpoint", null, "type=?", new String[]{String.valueOf(type)}, null, null, null);
			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				try { cursor.close(); } catch (Exception e) {}
			}
			db.close();
		}
	}
	
	public boolean existSelfAudioListBreakPoint(int type, String id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query("breakpoint", null, "type=? and audio_id=?", new String[]{String.valueOf(type),id}, null, null, null);
			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				try { cursor.close(); } catch (Exception e) {}
			}
			db.close();
		}
	}	
}

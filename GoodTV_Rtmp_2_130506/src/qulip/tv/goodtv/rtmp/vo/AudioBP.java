package qulip.tv.goodtv.rtmp.vo;


public class AudioBP {

	private int type;
	private int chapter;
	private int maxpos;
	private int curpos;
	private String audio_id;
	private String title;
	private String description;	
	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getChapter() {
		return chapter;
	}
	public void setChapter(int chapter) {
		this.chapter = chapter;
	}
	public int getMaxPos() {
		return maxpos;
	}
	public void setMaxPos(int maxpos) {
		this.maxpos = maxpos;
	}
	
	public int getCurPos() {
		return curpos;
	}
	public void setCurPos(int curpos) {
		this.curpos = curpos;
	}
	
	public String getAudioId() {
		return audio_id;
	}
	public void setAudioId(String audio_id) {
		this.audio_id =audio_id;
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
	public void setDescription(String description) {
		this.description = description;
	}
}

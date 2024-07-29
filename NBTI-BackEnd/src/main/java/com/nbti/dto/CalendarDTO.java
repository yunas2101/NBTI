package com.nbti.dto;

import java.sql.Timestamp;

public class CalendarDTO {
	private int seq;
	private String member_id;
	private int title;
	private String calendarTitle;
	private String contents;
	private Timestamp start_date;
	private Timestamp end_date;
	
	public CalendarDTO() {
		super();
	}
	public CalendarDTO(int seq, String member_id, int title, String calendarTitle, String contents,
			Timestamp start_date, Timestamp end_date) {
		super();
		this.seq = seq;
		this.member_id = member_id;
		this.title = title;
		this.calendarTitle = calendarTitle;
		this.contents = contents;
		this.start_date = start_date;
		this.end_date = end_date;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public int getTitle() {
		return title;
	}
	public void setTitle(int title) {
		this.title = title;
	}
	public String getCalendarTitle() {
		return calendarTitle;
	}
	public void setCalendarTitle(String calendarTitle) {
		this.calendarTitle = calendarTitle;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public Timestamp getStart_date() {
		return start_date;
	}
	public void setStart_date(Timestamp start_date) {
		this.start_date = start_date;
	}
	public Timestamp getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Timestamp end_date) {
		this.end_date = end_date;
	}
	
}

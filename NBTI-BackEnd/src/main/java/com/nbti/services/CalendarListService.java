package com.nbti.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nbti.dao.CalendarListDAO;
import com.nbti.dto.CalendarListDTO;

@Service
public class CalendarListService {
	@Autowired
	private CalendarListDAO cldao;
	
	// 공유 캘린더 목록
    public List<CalendarListDTO> list (String member_id) throws Exception{
    	
    	List<CalendarListDTO> list = cldao.list(member_id);
    	return list;
    }
    
	// 공유 캘린더 추가
	public void insert (CalendarListDTO dto)  throws Exception {
		cldao.insert(dto);
	}
	
	public int getLastCalendarID() throws Exception {
		return cldao.getLastCalendarID();
	}

	public void insertMember(int lastCalID, String calendarMember) throws Exception {
		cldao.insertMember(lastCalID, calendarMember);
	}
}

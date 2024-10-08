package com.nbti.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbti.commons.EncryptionUtils;
import com.nbti.dto.DepartmentDTO;
import com.nbti.dto.JobDTO;
import com.nbti.dto.M_LevelDTO;
import com.nbti.dto.MembersDTO;
import com.nbti.dto.TeamsDTO;
import com.nbti.services.DepartmentService;
import com.nbti.services.JobService;
import com.nbti.services.M_LevelService;
import com.nbti.services.MembersService;
import com.nbti.services.TeamsService;

import jakarta.servlet.http.HttpSession;
//import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/members")
public class MembersController {
	@Autowired
	private DepartmentService dServ;
	@Autowired
	private TeamsService tServ;
	@Autowired
	private JobService jServ;
	@Autowired
	private MembersService mServ;
	@Autowired
	private M_LevelService lServ;
	@Autowired
	private HttpSession session;
	
	@GetMapping
	public Map<String, Object> myPage() {
		String id = (String)session.getAttribute("loginID");
		MembersDTO dto = mServ.selectMyData(id);
		Map<String, Object> memberData = mServ.memberData(id);
		memberData.put("dto", dto);
		
		return memberData;
	}
	 @GetMapping("/{id}")
	    public ResponseEntity<MembersDTO> selectById(@PathVariable("id") String id) {
	        MembersDTO member = mServ.selectMyData(id);
	        if (member != null) {
	            return ResponseEntity.ok(member);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
	 
	@GetMapping("/memberInfo")
	public MembersDTO memberInfo() {
		String id = (String)session.getAttribute("loginID");
		return mServ.selectMyData(id);
	}
	
	@PutMapping
	public ResponseEntity<Void> update(@RequestBody MembersDTO dto) {
		String id = (String)session.getAttribute("loginID");
		dto.setId(id);
//		System.out.println(dto.getAddress() +":"+ dto.getEmail() +":"+ dto.getMember_call());
		mServ.updateMyData(dto);
		return ResponseEntity.ok().build();
	}
	// 멤버테이블 전체 추출
    @GetMapping("/selectAll")
    public ResponseEntity<List<MembersDTO>> selectAll() {
        List<MembersDTO> members = mServ.selectAll();
        return ResponseEntity.ok(members);  // HTTP 200 OK와 함께 members를 반환
    }
    // 사용자 조회
    @GetMapping("/selectMembers")
    public ResponseEntity<java.util.List<Map<String, Object>>> selectMembers(){
        List<Map<String, Object>> selectMembers = mServ.getMembers();
        return ResponseEntity.ok(selectMembers);
    }
    
    // 사용자 조회
    @GetMapping("/list")
    public ResponseEntity<java.util.List<Map<String, Object>>> list(@RequestParam int start,@RequestParam int end){
        List<Map<String, Object>> selectMembers = mServ.list(start, end);
        return ResponseEntity.ok(selectMembers);
    }
    
    // 부서 추출
    @GetMapping("/selectDepartment")
    public ResponseEntity<List<DepartmentDTO>> selectDepartment(){
    	List<DepartmentDTO> dapt = dServ.selectDepartment();
    	return ResponseEntity.ok(dapt);
    }
    
    // 팀 추출
    @GetMapping("/selectTeam")
    public ResponseEntity<List<TeamsDTO>> selectTeams(){
    	List<TeamsDTO> team = tServ.selectTeams();
    	return ResponseEntity.ok(team);
    }
    // 직급 추출
    @GetMapping("/selectJob")
    public ResponseEntity<List<JobDTO>> selectJob(){
    	List<JobDTO> job = jServ.selectJob();
    	return ResponseEntity.ok(job);
    }
    // 권한 추출
	@GetMapping("/selectLevel")
	public ResponseEntity<List<M_LevelDTO>>selectLevel(){
		List<M_LevelDTO> level = lServ.selectLevel();
		return ResponseEntity.ok(level);
	}
	
	// 암호화 회원가입
	@PostMapping
	public ResponseEntity<Void> insert(@RequestBody MembersDTO dto){
		String encryptedPassword = EncryptionUtils.getSHA512(dto.getPw());
	    dto.setPw(encryptedPassword);

		mServ.insert(dto);
		
		return ResponseEntity.ok().build();
	}
	@PostMapping("/checkId")
	public boolean checkId(@RequestBody Map<String, String> requestBody) {
	    String id = requestBody.get("id");
	    
	    MembersDTO member = mServ.selectMyData(id);
	  
	    return member == null;
	}
	@PostMapping("/checkEmail")
	public boolean checkEmail(@RequestBody Map<String, String> requestBody) {
	    String email = requestBody.get("email");
	    return mServ.checkEmail(email);
	}
	@PutMapping("/updateUser")
	public ResponseEntity<String> updateUser(@RequestBody MembersDTO dto) throws Exception {
		System.out.println(dto.getEnt_yn());
		System.out.println(dto.getMember_level());
	    try {
	        mServ.updateUser(dto);
	        return ResponseEntity.ok("회원 정보가 성공적으로 업데이트되었습니다."); // 응답 본문 설정
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 업데이트 중 오류가 발생했습니다."); // 오류 메시지 설정
	    }
	}
	// 관리자 회원 탈퇴
	@DeleteMapping("/deleteUser/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") String id){
		mServ.deleteUser(id);
		return ResponseEntity.ok().build();
	}
	// 사용자목록 검색
	@GetMapping("/searchUser")
	public ResponseEntity<List<Map<String, Object>>> searchUser(@RequestParam String name,@RequestParam int start,@RequestParam int end){
	    List<Map<String, Object>> users = mServ.searchUser(name,start,end);
	    return ResponseEntity.ok(users);
	}
	
	@GetMapping("/searchUserCount")
	public ResponseEntity<Integer> searchUserCount(@RequestParam String name){
		int count = mServ.searchUserCount(name);
	    return ResponseEntity.ok(count);
	}
	// 사용자목록 팀 조회
	@GetMapping("/selectByTeam")
	public ResponseEntity<List<MembersDTO>> selectByTeam(@RequestParam String team_code,@RequestParam int start,@RequestParam int end){
		List<MembersDTO> byteam = mServ.selectByTeam(team_code,start,end);
		return ResponseEntity.ok(byteam);
	}
	@GetMapping("/selectByTeamCount")
	public ResponseEntity<Integer> selectByTeamCount(@RequestParam String team_code){
		int count = mServ.selectByTeamCount(team_code);
		return ResponseEntity.ok(count);
	}
	 @GetMapping("/apply")
     public ResponseEntity<Map<String, Object>> getVacationInfo(HttpSession session, @RequestParam(required = false) String memberId) {
//       System.out.println("applyForVacation 컨트롤러 호출됨");  // 호출 여부 확인
         // 세션에서 로그인한 사용자의 ID를 가져옵니다.
         if (memberId == null) {
             memberId = (String) session.getAttribute("loginID");
             if (memberId == null) {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 로그인되지 않은 경우
             }
         }
         
         // days는 기본적으로 0으로 설정하여 초기 로딩 시 사용 휴가를 차감하지 않도록 합니다.
         Map<String, Object> vacationInfo = mServ.applyForVacation(memberId, 0);
         return ResponseEntity.ok(vacationInfo);
     }


	
	// 작성일 24.07.30 
	// 작성자 김지연
	// 마이페이지 비밀번호 변경 시 기존 비밀번호 체크
	@PostMapping("/checkPw")
	public ResponseEntity<Boolean> checkPw(@RequestBody Map<String, String> request){
		String pw = EncryptionUtils.getSHA512(request.get("pw"));
//		System.out.println("pw:" + pw);
		
		String id = (String)session.getAttribute("loginID");
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("pw", pw);
		Boolean result = mServ.checkPw(map);
		return ResponseEntity.ok(result);
	}
	@PostMapping("/updatePw")
	public ResponseEntity<Boolean> updatePw(@RequestBody Map<String, String> request) {
	    String newPassword = EncryptionUtils.getSHA512(request.get("newPassword"));
	    String id = request.get("id");

	    HashMap<String, String> map = new HashMap<>();
	    map.put("id", id);
	    map.put("pw", newPassword);

	    boolean result = mServ.changePw(map);
	    if (result) {
	        return ResponseEntity.ok(result);
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
	    }
	}
	
	// 작성일 24.07.30 
	// 작성자 김지연
	// 마이페이지 비밀번호 변경
	@PostMapping("/changePw")
	public ResponseEntity<Boolean> changePw(@RequestBody Map<String, String> request){
		String pw = EncryptionUtils.getSHA512((String)request.get("pw"));
//		System.out.println("pw:" + pw);
		String id = (String)session.getAttribute("loginID");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("pw", pw);
		Boolean result = mServ.changePw(map);
		return ResponseEntity.ok(result);
	}
	
	// 패스워드 변경 날짜 출력
	@GetMapping("/getPwChangeDate")
	public ResponseEntity<Timestamp> getPwChangeDate(){
		String id = (String)session.getAttribute("loginID");
		Timestamp date = mServ.getPwChangeDate(id);
		return ResponseEntity.ok(date);
	}
	
	// 작성일 24.07.31 
	// 작성자 김지연
	// 팀별 사용자 검색
    @GetMapping("/searchMembers/{selectTeam}")
    public ResponseEntity<List<Map<String, Object>>> searchMembers(@PathVariable("selectTeam") String team){
    	List<Map<String, Object>> selectMembers = mServ.searchMembers(team);
        return ResponseEntity.ok(selectMembers);
    }
    
    // 작성일 24.08.1 
 	// 작성자 김지연
 	// 접속한 아이디에 따른 이름, 팀코드, 팀명, 부서코드, 부서명, 관리자 권한 코드, 관리자 권한명 추출
     @GetMapping("/docData")
     public ResponseEntity<Map<String, Object>> docData(){
    	 String id = (String)session.getAttribute("loginID");
         Map<String, Object> memberData = mServ.memberData(id);
         return ResponseEntity.ok(memberData);
     }
	
    // 작성일 24.08.2
  	// 작성자 김지연
  	// 아이디에 따른 휴가일수 추출
      @GetMapping("/selectVacation")
      public ResponseEntity<String> selectVacation(){
     	 String id = (String)session.getAttribute("loginID");
         String day = String.valueOf(mServ.selectPeriod(id)); 
         return ResponseEntity.ok(day);
      }
      
      
      // 작성일 24.08.7
   	  // 작성자 김지연
      // 검색할 아이디에 따른 이름, 팀코드, 팀명, 부서코드, 부서명, 관리자 권한 코드, 관리자 권한명 추출
       @GetMapping("/approvaler/{id}")
       public ResponseEntity<Map<String, Object>> docData(@PathVariable String id){
          Map<String, Object> memberData = mServ.memberData(id);
          return ResponseEntity.ok(memberData);
       }
       
       // 작성일 24.08.12
       // 작성자 김지연
       // 검색할 아이디에 따른 이름, 팀코드, 팀명, 부서코드, 부서명, 관리자 권한 코드, 관리자 권한명 추출
       @PostMapping("/approvalSearch")
        public List<Map<String, Object>> approvalSearch(@RequestBody List<Map<String, Object>> approvalLine){
//    	   System.out.println("결재라인 확인?? : " + approvalLine);
    	   List<Map<String, Object>> list = new ArrayList<>();
    	   if(approvalLine.size() > 0) {
    	   	for (Map<String, Object> map : approvalLine) {
    	   		if(map != null) {
    	   		if(map.get("id") != null) {
					String id = (String)map.get("id");
					String order = map.get("order").toString();
					 Map<String, Object> memberData = mServ.memberData(id);
					 memberData.put("order",order);
					 list.add(memberData);
//					System.out.println(memberData);
				}else if(map.get("referer") != null) {
					
//					System.out.println("참조라인 들어옴"+map.get("referer"));
					String id = (String)map.get("referer");
					String name =  (String)map.get("name");
					Map<String, Object> memberData = mServ.memberData(id);
					memberData.put("name",name);
					list.add(memberData);
				}
    	   		}
			}
    	   }
           return list;
       }
       
   	@ExceptionHandler(Exception.class)
   	public String exceptionHandler(Exception e) {
   		e.printStackTrace();
   		return "error";
   	}
}

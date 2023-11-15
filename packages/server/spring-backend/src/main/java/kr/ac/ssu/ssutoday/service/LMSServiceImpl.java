/**
 * @filename    : LMSServiceImpl.java
 * @description : LMS Service Implementation
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.ssu.ssutoday.common.GlobalVariable;
import kr.ac.ssu.ssutoday.entity.Device;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.provider.APIProvider;
import kr.ac.ssu.ssutoday.provider.KafkaProvider;
import kr.ac.ssu.ssutoday.provider.dto.APIRequestDto;
import kr.ac.ssu.ssutoday.provider.dto.APIResponseDto;
import kr.ac.ssu.ssutoday.repository.DeviceRepository;
import kr.ac.ssu.ssutoday.repository.StudentRepository;
import kr.ac.ssu.ssutoday.vo.PushMessageVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LMSServiceImpl implements LMSService{
    /**
     * DI
     */
    private final StudentRepository studentRepository;
    private final DeviceRepository deviceRepository;
    private final APIProvider apiProvider;
    private final KafkaProvider kafkaProvider;
    private final GlobalVariable globalVariable;

    /**
     * Send LMS notification (scheduled job)
     * @author jonghokim27
     */
    @Override
    public void lmsNotificationSend() {
        log.info("Sending LMS notification");
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Student> students = studentRepository.findAllByXnApiTokenIsNotNull();
            for(Student student : students){
                String xnApiToken = student.getXnApiToken();

                // Phase 1 : Fetch terms(semester)
                String termsRequestUrl = globalVariable.canvasTermsAPIUrl;

                HashMap<String, String> termsRequestHeaders = new HashMap<>();
                termsRequestHeaders.put("Authorization", "Bearer " + xnApiToken);

                APIRequestDto termsRequestDto = APIRequestDto.builder()
                        .headers(termsRequestHeaders)
                        .url(termsRequestUrl)
                        .build();

                APIResponseDto termsResponseDto;
                try {
                    termsResponseDto = apiProvider.get(termsRequestDto);
                }
                catch (Exception e){
                    log.error("API request to canvas terms failed.", e);
                    continue;
                }

                JsonNode termsResponseBody = mapper.readTree(termsResponseDto.getBody());
                int termId = termsResponseBody.get("enrollment_terms").get(0).get("id").intValue();

                // Phase 2 : Fetch courses
                String coursesRequestUrl = globalVariable.canvasCoursesAPIUrl + termId;

                HashMap<String, String> coursesRequestHeaders = new HashMap<>();
                coursesRequestHeaders.put("Authorization", "Bearer " + xnApiToken);

                APIRequestDto coursesRequestDto = APIRequestDto.builder()
                        .headers(coursesRequestHeaders)
                        .url(coursesRequestUrl)
                        .build();

                APIResponseDto coursesResponseDto;
                try {
                    coursesResponseDto = apiProvider.get(coursesRequestDto);
                }
                catch (Exception e){
                    log.error("API request to canvas courses failed.", e);
                    continue;
                }

                JsonNode coursesResponseBody = mapper.readTree(coursesResponseDto.getBody());
                HashMap<Integer, String> courses = new HashMap<>();
                for(JsonNode course : coursesResponseBody){
                    int courseId = course.get("id").intValue();
                    String courseName = course.get("name").asText();
                    courses.put(courseId, courseName);
                }

                // Phase 3 : Fetch ToDos
                List<HashMap<String, String>> toDoMapList = new ArrayList<>();
                String toDosRequestUrl = globalVariable.canvasToDosAPIUrl + termId;

                HashMap<String, String> toDosRequestHeaders = new HashMap<>();
                toDosRequestHeaders.put("Authorization", "Bearer " + xnApiToken);

                APIRequestDto toDosRequestDto = APIRequestDto.builder()
                        .headers(toDosRequestHeaders)
                        .url(toDosRequestUrl)
                        .build();

                APIResponseDto toDosResponseDto;
                try {
                    toDosResponseDto = apiProvider.get(toDosRequestDto);
                }
                catch (Exception e){
                    log.error("API request to canvas toDos failed.", e);
                    continue;
                }

                JsonNode toDosResponseBody = mapper.readTree(toDosResponseDto.getBody());
                for(JsonNode toDo : toDosResponseBody.get("to_dos")){
                    int courseId = toDo.get("course_id").intValue();
                    String courseName = courses.get(courseId);

                    JsonNode toDoItems = toDo.get("todo_list");
                    for(JsonNode toDoItem : toDoItems){
                        String title = toDoItem.get("title").textValue();
                        String dueDate = toDoItem.get("due_date").textValue();
                        String type = toDoItem.get("component_type").textValue();

                        if(type.equals("smart_attendance")){
                            continue;
                        }

                        Calendar dueDateCal = Calendar.getInstance();
                        dueDateCal.setTimeZone(TimeZone.getTimeZone("UTC"));
                        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        isoFormat.setCalendar(dueDateCal);

                        if(dueDate == null){
                            continue;
                        }
                        dueDateCal.setTime(isoFormat.parse(dueDate));
                        dueDateCal.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                        if(dueDateCal.getTimeInMillis() - 1000L * 3600 * 60 > System.currentTimeMillis()
                            && dueDateCal.getTimeInMillis() != 0){
                            continue;
                        }
                        if(dueDateCal.getTimeInMillis() < System.currentTimeMillis()){
                            continue;
                        }

                        SimpleDateFormat hangulFormat = new SimpleDateFormat("MM월 dd일 HH시 mm분");
                        HashMap<String, String> toDoMap = new HashMap<>();
                        toDoMap.put("courseName", courseName);
                        toDoMap.put("title", title);
                        toDoMap.put("dueDate", hangulFormat.format(dueDateCal.getTime()));
                        toDoMap.put("type", type);
                        toDoMapList.add(toDoMap);
                    }
                }

                List<Device> devices = deviceRepository.findAllByStudentId(student.getId());
                for(HashMap<String, String> toDoMap : toDoMapList){
                    for(Device device : devices){
                        if(device.getLms() == 0){
                            continue;
                        }

                        PushMessageVo pushMessageVo = PushMessageVo.builder()
                                .type("token")
                                .token(device.getPushToken())
                                .title("\uD83D\uDCDA " + toDoMap.get("courseName"))
                                .body(toDoMap.get("title") + " " + switch (toDoMap.get("type")){
                                    case "commons" -> "동영상이 ";
                                    case "quiz" -> "퀴즈가 ";
                                    case "assignment" -> "과제가 ";
                                    default -> "";
                                } + toDoMap.get("dueDate") + "에 마감돼요.")
                                .link("ssutoday://lms")
                                .build();

                        kafkaProvider.sendMessage("pushMessage", mapper.writeValueAsString(pushMessageVo));
                    }
                }

                if(toDoMapList.size() == 0){
                    for(Device device : devices){
                        if(device.getLms() == 0){
                            continue;
                        }

                        PushMessageVo pushMessageVo = PushMessageVo.builder()
                                .type("token")
                                .token(device.getPushToken())
                                .title("\uD83C\uDF89 마감이 임박한 동영상, 퀴즈, 과제가 없어요.")
                                .body("슈투데이에서 확인하지 못한 내용이 있을 수 있으니, LMS 마이페이지를 꼭 확인해주세요.")
                                .link("ssutoday://lms")
                                .build();

                        kafkaProvider.sendMessage("pushMessage", mapper.writeValueAsString(pushMessageVo));
                    }
                }

            }
        } catch (Exception e){
            log.error("Failed to send LMS notification", e);
        }

        log.info("Finished sending LMS notification");
    }
}

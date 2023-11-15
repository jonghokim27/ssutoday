/**
 * @filename    : ReserveServiceImpl.java
 * @description : Reserve Service Implementation
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.ssu.ssutoday.common.GlobalVariable;
import kr.ac.ssu.ssutoday.entity.*;
import kr.ac.ssu.ssutoday.exception.ConfigDisabledException;
import kr.ac.ssu.ssutoday.exception.FileUploadFailedException;
import kr.ac.ssu.ssutoday.provider.KafkaProvider;
import kr.ac.ssu.ssutoday.provider.S3Provider;
import kr.ac.ssu.ssutoday.provider.TokenProvider;
import kr.ac.ssu.ssutoday.repository.*;
import kr.ac.ssu.ssutoday.service.dto.*;
import kr.ac.ssu.ssutoday.util.MaskUtil;
import kr.ac.ssu.ssutoday.vo.PushMessageVo;
import kr.ac.ssu.ssutoday.vo.ReserveDetailVo;
import kr.ac.ssu.ssutoday.vo.ReserveVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveServiceImpl implements ReserveService {
    /**
     * DI
     */
    private final ReserveRepository reserveRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final ReserveRequestRepository reserveRequestRepository;
    private final ConfigRepository configRepository;
    private final DeviceRepository deviceRepository;
    private final VerifyPhotoRepository verifyPhotoRepository;
    private final S3Provider s3Provider;
    private final KafkaProvider kafkaProvider;
    private final TokenProvider tokenProvider;
    private final MaskUtil maskUtil;
    private final GlobalVariable globalVariable;
    private final String RESERVE_KAFKA_TOPIC = "requestReserve";

    /**
     * Request reservation
     * @param reserveRequestParamDto request params
     * @return request result (ReserveRequestReturnDto)
     * @throws Exception thrown when failed to request reservation
     * @author jonghokim27
     */
    @NotNull
    @Override
    public ReserveRequestReturnDto reserveRequest(@NotNull ReserveRequestParamDto reserveRequestParamDto) throws Exception {
        Optional<Config> config = configRepository.findById("RESERVE_REQUEST_DISABLED");

        if(config.isEmpty() || config.get().getValue().equals("true")){
            throw new ConfigDisabledException();
        }

        ReserveRequest reserveRequest = reserveRequestRepository.save(reserveRequestParamDto.toReserveRequest());

        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(reserveRequest);

        try {
            kafkaProvider.sendMessage(RESERVE_KAFKA_TOPIC, message);
        } catch (Exception e){
            log.error("Failed to send message to Kafka", e);
            throw e;
        }

        return ReserveRequestReturnDto.builder()
                .idx(reserveRequest.getIdx())
                .build();
    }

    /**
     * List reservation
     * @param reserveListParamDto list params
     * @return list result (ReserveListReturnDto)
     * @author jonghokim27
     */
    @NotNull
    @Override
    public ReserveListReturnDto reserveList(@NotNull ReserveListParamDto reserveListParamDto) {
        final int pageSize = 10;

        int nowBlock;
        Calendar todayDate = Calendar.getInstance();
        todayDate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        todayDate.setTimeInMillis(System.currentTimeMillis());

        Calendar todayDateWithoutTime = (Calendar) todayDate.clone();
        todayDateWithoutTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        todayDateWithoutTime.set(Calendar.AM_PM, Calendar.AM);
        todayDateWithoutTime.set(Calendar.HOUR, 0);
        todayDateWithoutTime.set(Calendar.MINUTE, 0);
        todayDateWithoutTime.set(Calendar.SECOND, 0);
        todayDateWithoutTime.set(Calendar.MILLISECOND, 0);

        long todayMillis = todayDate.getTimeInMillis() - todayDateWithoutTime.getTimeInMillis();
        nowBlock = Math.toIntExact(todayMillis / 1000 / 60 / 30);

        PageRequest pageRequest;
        Page<Reserve> reservePage;
        if(reserveListParamDto.getType() == 0) {
            // Previous reservations
            pageRequest = PageRequest.of(reserveListParamDto.getPage(), pageSize, Sort.by("date", "endBlock", "idx").descending());
            reservePage = reserveRepository.findAllPreviousReserve(
                    reserveListParamDto.getStudentId(),
                    new Date(System.currentTimeMillis()),
                    nowBlock,
                    pageRequest
            );
        } else{
            // Waiting reservations
            pageRequest = PageRequest.of(reserveListParamDto.getPage(), pageSize, Sort.by("date", "startBlock", "idx").ascending());
            reservePage = reserveRepository.findAllWaitingReserve(
                    reserveListParamDto.getStudentId(),
                    new Date(System.currentTimeMillis()),
                    nowBlock,
                    pageRequest
            );
        }

        List<Reserve> reserves = reservePage.get().toList();
        List<ReserveDetailVo> reserveDetailVos = new ArrayList<>();

        for(Reserve reserve : reserves){
            ReserveDetailVo reserveDetailVo = reserve.toReserveDetailVo();

            // Check if previous -> this reservation is continuous
            long checkContinuousReserve = reserveRepository.countByStudentIdAndDateAndEndBlockAndRoomNoAndDeletedAtIsNull(
                    reserve.getStudentId(),
                    reserve.getDate(),
                    reserve.getStartBlock() - 1,
                    reserve.getRoomNo()
            );
            reserveDetailVo.setIsContinuous(checkContinuousReserve != 0);

            reserveDetailVos.add(reserveDetailVo);
        }

        return ReserveListReturnDto.builder()
                .reserves(reserveDetailVos)
                .totalPages(reservePage.getTotalPages())
                .build();
    }

    /**
     * List reservation (for Room)
     * @param roomReserveListParamDto list params
     * @return list result (RoomReserveListReturnDto)
     * @author jonghokim27
     */
    @NotNull
    @Override
    public RoomReserveListReturnDto roomReserveList(@NotNull RoomReserveListParamDto roomReserveListParamDto) {
        List<Reserve> reserveList = reserveRepository.findAllByRoomNoAndDateAndDeletedAtIsNull(
                roomReserveListParamDto.getRoomNo(),
                roomReserveListParamDto.getDate()
        );

        List<ReserveVo> reserveVoList = new ArrayList<>();
        for(Reserve reserve : reserveList){
            String studentInfo;
            if(roomReserveListParamDto.getIsAdmin()) {
                studentInfo = reserve.getStudentByStudentId().getName()
                        + " (" + reserve.getStudentByStudentId().getId() + "/"
                        + switch (reserve.getStudentByStudentId().getMajor()) {
                            case "cse" -> "컴";
                            case "sw" -> "솦";
                            case "media" -> "글";
                            case "mediamba" -> "미경";
                            default -> "";
                        } + ")";
            } else {
                studentInfo = maskUtil.maskName(reserve.getStudentByStudentId().getName())
                        + " (" + maskUtil.maskStudentId(reserve.getStudentByStudentId().getId()) + ")";
            }
            ReserveVo reserveVo = ReserveVo.builder()
                    .studentInfo(studentInfo)
                    .startBlock(reserve.getStartBlock())
                    .endBlock(reserve.getEndBlock())
                    .isMine(roomReserveListParamDto.getStudentId().intValue() == reserve.getStudentId().intValue())
                    .build();

            reserveVoList.add(reserveVo);
        }

        return RoomReserveListReturnDto.builder()
                .reserves(reserveVoList)
                .build();
    }

    /**
     * Get reservation status
     * @param reserveStatusParamDto reservation params
     * @return reservation status (ReserveStatusReturnDto)
     * @throws Exception thrown when reserve request with idx and studentId does not exist
     * @author jonghokim27
     */
    @NotNull
    @Override
    public ReserveStatusReturnDto reserveStatus(@NotNull ReserveStatusParamDto reserveStatusParamDto) throws Exception {
        Optional<ReserveRequest> reserveRequestOptional = reserveRequestRepository.findByIdxAndStudentId(
                reserveStatusParamDto.getIdx(),
                reserveStatusParamDto.getStudentId()
        );

        if(reserveRequestOptional.isEmpty()){
            throw new Exception();
        }

        return ReserveStatusReturnDto.builder()
                .status(reserveRequestOptional.get().getStatus())
                .build();
    }

    /**
     * Cancel reservation
     * @param reserveCancelParamDto cancel params
     * @return cancel result (ReserveCancelReturnDto)
     * @throws Exception thrown when reserve request with idx and studentId does not exist
     * @author jonghokim27
     */
    @NotNull
    @Override
    public ReserveCancelReturnDto reserveCancel(@NotNull ReserveCancelParamDto reserveCancelParamDto) throws Exception {
        Optional<Reserve> reserveOptional = reserveRepository.findByIdxAndStudentIdAndDeletedAtIsNull(
                reserveCancelParamDto.getIdx(),
                reserveCancelParamDto.getStudentId()
        );

        if(reserveOptional.isEmpty()){
            throw new Exception();
        }

        Reserve reserve = reserveOptional.get();

        Calendar todayDate = Calendar.getInstance();
        todayDate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        todayDate.setTimeInMillis(System.currentTimeMillis());

        Calendar todayDateWithoutTime = (Calendar) todayDate.clone();
        todayDateWithoutTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        todayDateWithoutTime.set(Calendar.AM_PM, Calendar.AM);
        todayDateWithoutTime.set(Calendar.HOUR, 0);
        todayDateWithoutTime.set(Calendar.MINUTE, 0);
        todayDateWithoutTime.set(Calendar.SECOND, 0);
        todayDateWithoutTime.set(Calendar.MILLISECOND, 0);

        Calendar reserveDate = Calendar.getInstance();
        reserveDate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        reserveDate.setTime(reserve.getDate());

        // Passed date
        if(reserveDate.before(todayDateWithoutTime)){
            log.debug("Reserved date has passed.");
            return ReserveCancelReturnDto.builder()
                    .status(2)
                    .build();
        }

        // Passed time
        if(reserveDate.getTimeInMillis() + (reserve.getEndBlock() + 1) * 30L * 60 * 1000 < todayDate.getTimeInMillis()){
            log.debug("Reserved end time has passed.");
            return ReserveCancelReturnDto.builder()
                    .status(3)
                    .build();
        }

        // Passed start time
        if(reserveDate.getTimeInMillis() + reserve.getStartBlock() * 30L * 60 * 1000 < todayDate.getTimeInMillis()){
            log.debug("Reserved start time has passed.");
            return ReserveCancelReturnDto.builder()
                    .status(4)
                    .build();
        }

        reserve.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        reserveRepository.save(reserve);

        return ReserveCancelReturnDto.builder()
                .status(1)
                .build();
    }

    /**
     * Upload verify photo
     * @param reserveVerifyPhotoUploadParamDto upload params
     * @return upload result (ReserveVerifyPhotoUploadReturnDto)
     * @throws Exception thrown when reserve request with idx and studentId does not exist
     * @author jonghokim27
     */
    @NotNull
    @Override
    public ReserveVerifyPhotoUploadReturnDto verifyPhotoUpload(@NotNull ReserveVerifyPhotoUploadParamDto reserveVerifyPhotoUploadParamDto) throws Exception {
        Optional<Reserve> reserveOptional = reserveRepository.findByIdxAndStudentIdAndDeletedAtIsNull(
                reserveVerifyPhotoUploadParamDto.getIdx(),
                reserveVerifyPhotoUploadParamDto.getStudentId()
        );

        if(reserveOptional.isEmpty()){
            throw new Exception();
        }

        Reserve reserve = reserveOptional.get();

        Optional<VerifyPhoto> verifyPhotoOptional = verifyPhotoRepository.findByReserveIdx(reserve.getIdx());
        if(verifyPhotoOptional.isPresent()){
            throw new Exception();
        }

        Calendar todayDate = Calendar.getInstance();
        todayDate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        todayDate.setTimeInMillis(System.currentTimeMillis());

        Calendar todayDateWithoutTime = (Calendar) todayDate.clone();
        todayDateWithoutTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        todayDateWithoutTime.set(Calendar.AM_PM, Calendar.AM);
        todayDateWithoutTime.set(Calendar.HOUR, 0);
        todayDateWithoutTime.set(Calendar.MINUTE, 0);
        todayDateWithoutTime.set(Calendar.SECOND, 0);
        todayDateWithoutTime.set(Calendar.MILLISECOND, 0);

        Calendar reserveDate = Calendar.getInstance();
        reserveDate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        reserveDate.setTime(reserve.getDate());

        // Passed date
        if(reserveDate.before(todayDateWithoutTime)){
            log.debug("Reserved date has passed.");
            return ReserveVerifyPhotoUploadReturnDto.builder()
                    .status(2)
                    .build();
        }

        // Passed time
        if(reserveDate.getTimeInMillis() + (reserve.getEndBlock() + 1) * 30L * 60 * 1000 < todayDate.getTimeInMillis()){
            log.debug("Reserved end time has passed.");
            return ReserveVerifyPhotoUploadReturnDto.builder()
                    .status(3)
                    .build();
        }

        // Not passed start time
        long reserveStartTimeMillis = reserveDate.getTimeInMillis() + reserve.getStartBlock() * 30L * 60 * 1000;
        if(reserveStartTimeMillis > todayDate.getTimeInMillis()){
            log.debug("Reserved start time has not passed.");
            return ReserveVerifyPhotoUploadReturnDto.builder()
                    .status(4)
                    .build();
        }

        long useStartTimeMillis;
        if(reserveStartTimeMillis < reserve.getCreatedAt().getTime()){
            // Reserved at a passing time
            useStartTimeMillis = reserve.getCreatedAt().getTime();
        } else{
            useStartTimeMillis = reserveStartTimeMillis;
        }

        // Passed use start time + 10 minutes
        if(useStartTimeMillis + 11L * 60 * 1000 <= todayDate.getTimeInMillis()){
            log.debug("Reserved start time + 10 minutes has passed.");
            return ReserveVerifyPhotoUploadReturnDto.builder()
                    .status(5)
                    .build();
        }

        String fileName = "verifyPhoto/" + tokenProvider.generateRandomHashToken(20) + ".jpeg";

        try {
            s3Provider.uploadFile("ssutoday", fileName, reserveVerifyPhotoUploadParamDto.getFile());
        } catch(Exception e){
            log.debug("Failed to upload image to S3.");
            throw new FileUploadFailedException();
        }

        VerifyPhoto verifyPhoto = VerifyPhoto.builder()
                .reserveIdx(reserve.getIdx())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .url(globalVariable.publicS3Url + fileName)
                .build();

        verifyPhotoRepository.save(verifyPhoto);

        return ReserveVerifyPhotoUploadReturnDto.builder()
                .status(1)
                .build();

    }

    /**
     * Process reservation
     * @param message reservation message
     * @author jonghokim27
     */
    @KafkaListener(topics = RESERVE_KAFKA_TOPIC)
    @Override
    public void reserveProcess(@NotNull String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ReserveRequest reserveRequest = mapper.readValue(message, ReserveRequest.class);

            Optional<Student> studentOptional = studentRepository.findById(reserveRequest.getStudentId());
            if(studentOptional.isEmpty()){
                throw new Exception("Student does not exist.");
            }
            Student student = studentOptional.get();

            Calendar todayDate = Calendar.getInstance();
            todayDate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            todayDate.setTimeInMillis(System.currentTimeMillis());

            Calendar todayDateWithoutTime = (Calendar) todayDate.clone();
            todayDateWithoutTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            todayDateWithoutTime.set(Calendar.AM_PM, Calendar.AM);
            todayDateWithoutTime.set(Calendar.HOUR, 0);
            todayDateWithoutTime.set(Calendar.MINUTE, 0);
            todayDateWithoutTime.set(Calendar.SECOND, 0);
            todayDateWithoutTime.set(Calendar.MILLISECOND, 0);

            Calendar requestDate = Calendar.getInstance();
            requestDate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            requestDate.setTime(reserveRequest.getDate());

            // Passed date
            if(requestDate.before(todayDateWithoutTime)){
                log.debug("Requested date has passed.");
                reserveRequest.setStatus(2);
                reserveRequest.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                reserveRequestRepository.save(reserveRequest);
                return;
            }

            // Passed end time
            if(requestDate.getTimeInMillis() + (reserveRequest.getEndBlock() + 1) * 30L * 60 * 1000 <= todayDate.getTimeInMillis()){
                log.debug("Requested end time has passed.");
                reserveRequest.setStatus(3);
                reserveRequest.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                reserveRequestRepository.save(reserveRequest);
                return;
            }

            // Passed start time + 15 minutes
            if(requestDate.getTimeInMillis() + reserveRequest.getStartBlock() * 30L * 60 * 1000 + 15L * 60 * 1000 < todayDate.getTimeInMillis()){
                log.debug("Requested start time + 15 minutes has passed.");
                reserveRequest.setStatus(3);
                reserveRequest.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                reserveRequestRepository.save(reserveRequest);
                return;
            }

            // Date not reservable yet
            if(requestDate.getTimeInMillis() - 60L * 60 * 1000 * 4 > todayDate.getTimeInMillis() && student.getIsAdmin() == 0){
                log.debug("Requested date is not reservable yet.");
                reserveRequest.setStatus(4);
                reserveRequest.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                reserveRequestRepository.save(reserveRequest);
                return;
            }

            // Already reserved
            boolean isReserved = false;
            for(int i = reserveRequest.getStartBlock(); i <= reserveRequest.getEndBlock(); i++){
                long count = reserveRepository.countByDateAndRoomNoAndStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndDeletedAtIsNull(reserveRequest.getDate(), reserveRequest.getRoomNo(), i, i);
                if(count > 0){
                    isReserved = true;
                    break;
                }
            }
            if(isReserved){
                log.debug("Requested date is already reserved.");
                reserveRequest.setStatus(5);
                reserveRequest.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                reserveRequestRepository.save(reserveRequest);
                return;
            }

            // Reserve time limit exceed
            List<Reserve> myReserveList = reserveRepository.findAllByStudentIdAndDateAndDeletedAtIsNull(reserveRequest.getStudentId(), reserveRequest.getDate());
            int myReserveBlocks = 0;
            for(Reserve myReserve : myReserveList){
                myReserveBlocks += myReserve.getEndBlock() - myReserve.getStartBlock() + 1;
            }
            if(myReserveBlocks + (reserveRequest.getEndBlock() - reserveRequest.getStartBlock() + 1) > 6 && student.getIsAdmin() == 0){
                log.debug("Reserve time limit exceeded.");
                reserveRequest.setStatus(6);
                reserveRequest.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                reserveRequestRepository.save(reserveRequest);
                return;
            }

            // Already reserved a room at the same time
            boolean isUsingSameTime = false;
            for(int i = reserveRequest.getStartBlock(); i <= reserveRequest.getEndBlock(); i++){
                long count = reserveRepository.countByStudentIdAndDateAndStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndDeletedAtIsNull(reserveRequest.getStudentId(), reserveRequest.getDate(), i, i);
                if(count > 0){
                    isUsingSameTime = true;
                    break;
                }
            }
            if(isUsingSameTime && student.getIsAdmin() == 0){
                log.debug("Student already reserved a room at the same time.");
                reserveRequest.setStatus(7);
                reserveRequest.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                reserveRequestRepository.save(reserveRequest);
                return;
            }

            reserveRequest.setStatus(1);
            reserveRequest.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            reserveRequestRepository.save(reserveRequest);

            Reserve reserve = reserveRequest.toReserve();
            reserveRepository.save(reserve);

            // Send push
            Optional<Room> roomOptional = roomRepository.findById(reserveRequest.getRoomNo());
            if(roomOptional.isEmpty()){
                throw new Exception("Room does not exist.");
            }

            List<Device> devices = deviceRepository.findAllByStudentId(reserveRequest.getStudentId());

            SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy년 MM월 dd");
            String pushMessageBody = todayFormat.format(requestDate.getTime());
            String []days = {"일","월","화","수","목","금","토"};
            pushMessageBody += "(" + days[requestDate.get(Calendar.DAY_OF_WEEK)-1] + ") ";

            int startTime = reserveRequest.getStartBlock() * 30;
            int startHour = startTime / 60;
            int startMin = startTime % 60;

            int endTime = (reserveRequest.getEndBlock() + 1) * 30;
            int endHour = endTime / 60;
            int endMin = endTime % 60;

            pushMessageBody += (startHour < 10 ? "0" : "") + startHour + ":" + (startMin < 10 ? "0" : "") + startMin;
            pushMessageBody += " ~ ";
            pushMessageBody += (endHour < 10 ? "0" : "") + endHour + ":" + (endMin < 10 ? "0" : "") + endMin;

            for(Device device : devices){
                if(device.getReserve() == 0) {
                    continue;
                }

                PushMessageVo pushMessageVo = PushMessageVo.builder()
                                .type("token")
                                .token(device.getPushToken())
                                .title("✅ " + roomOptional.get().getName() + " 예약이 확정되었어요.")
                                .body(pushMessageBody)
                                .link("ssutoday://reserve/list")
                                .build();

                kafkaProvider.sendMessage("pushMessage", mapper.writeValueAsString(pushMessageVo));


                if(requestDate.getTimeInMillis() + reserveRequest.getStartBlock() * 30L * 60 * 1000 < todayDate.getTimeInMillis()){
                    // Reserved at a passing time

                    // Check if previous -> this reservation is continuous
                    long checkContinuousReserve = reserveRepository.countByStudentIdAndDateAndEndBlockAndRoomNoAndDeletedAtIsNull(
                            reserve.getStudentId(),
                            reserve.getDate(),
                            reserve.getStartBlock() - 1,
                            reserve.getRoomNo()
                    );

                    if(checkContinuousReserve == 0) {
                        PushMessageVo verifyPhotoPushMessageVo = PushMessageVo.builder()
                                .type("token")
                                .token(device.getPushToken())
                                .title("\uD83D\uDCF7 입실 후 인증샷을 촬영해주세요.")
                                .body("10분 내로 이 알림을 터치하여 인증샷을 촬영해주세요.")
                                .link("ssutoday://reserve/list")
                                .build();

                        kafkaProvider.sendMessage("pushMessage", mapper.writeValueAsString(verifyPhotoPushMessageVo));
                    }
                }
            }
        }
        catch(Exception e){
            log.error("Failed to process reservation", e);
        }
    }

    /**
     * Send reservation start/end notification (scheduled job)
     * @author jonghokim27
     */
    public void reserveNotificationSend(){
        log.info("Sending reservation start/end notification");

        try {
            ObjectMapper mapper = new ObjectMapper();

            int nowBlock;
            Calendar todayDate = Calendar.getInstance();
            todayDate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            todayDate.setTimeInMillis(System.currentTimeMillis());

            Calendar todayDateWithoutTime = (Calendar) todayDate.clone();
            todayDateWithoutTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            todayDateWithoutTime.set(Calendar.AM_PM, Calendar.AM);
            todayDateWithoutTime.set(Calendar.HOUR, 0);
            todayDateWithoutTime.set(Calendar.MINUTE, 0);
            todayDateWithoutTime.set(Calendar.SECOND, 0);
            todayDateWithoutTime.set(Calendar.MILLISECOND, 0);

            long todayMillis = todayDate.getTimeInMillis() - todayDateWithoutTime.getTimeInMillis();
            nowBlock = Math.toIntExact(todayMillis / 1000 / 60 / 30);

            List<Reserve> startReserves = reserveRepository.findAllByDateAndStartBlockAndDeletedAtIsNull(new Date(todayDate.getTimeInMillis()), nowBlock + 1);
            for (Reserve reserve : startReserves) {
                Optional<Room> roomOptional = roomRepository.findById(reserve.getRoomNo());
                if(roomOptional.isEmpty()){
                    log.error("Cannot find room with no {}", reserve.getRoomNo());
                    continue;
                }

                List<Device> devices = deviceRepository.findAllByStudentId(reserve.getStudentId());
                for (Device device : devices) {
                    if(device.getReserve() == 0) {
                        continue;
                    }

                    PushMessageVo pushMessageVo = PushMessageVo.builder()
                            .type("token")
                            .token(device.getPushToken())
                            .title("\uD83D\uDECE️ " + roomOptional.get().getName() + " 이용이 곧 시작돼요.")
                            .body("5분 뒤 이용이 시작돼요. 이용이 불가능하다면, 다른 이용자를 위해 예약을 취소해주세요.")
                            .link("ssutoday://reserve/list")
                            .build();
                    try {
                        kafkaProvider.sendMessage("pushMessage", mapper.writeValueAsString(pushMessageVo));
                    } catch (Exception e){
                        log.error("Failed to send message to Kafka", e);
                    }
                }
            }

            List<Reserve> endReserves = reserveRepository.findAllByDateAndEndBlockAndDeletedAtIsNull(new Date(todayDate.getTimeInMillis()), nowBlock);
            for (Reserve reserve : endReserves) {
                Optional<Room> roomOptional = roomRepository.findById(reserve.getRoomNo());
                if(roomOptional.isEmpty()){
                    log.error("Cannot find room with no {}", reserve.getRoomNo());
                    continue;
                }

                // Check if this -> next reservation is continuous
                long checkContinuousReserve = reserveRepository.countByStudentIdAndDateAndStartBlockAndRoomNoAndDeletedAtIsNull(
                        reserve.getStudentId(),
                        reserve.getDate(),
                        reserve.getEndBlock() + 1,
                        reserve.getRoomNo()
                );

                // Do not need to send reservation end notification
                if(checkContinuousReserve != 0){
                    continue;
                }

                List<Device> devices = deviceRepository.findAllByStudentId(reserve.getStudentId());
                for (Device device : devices) {
                    if(device.getReserve() == 0) {
                        continue;
                    }

                    PushMessageVo pushMessageVo = PushMessageVo.builder()
                            .type("token")
                            .token(device.getPushToken())
                            .title("\uD83D\uDEA8 " + roomOptional.get().getName() + " 이용이 곧 종료돼요.")
                            .body("5분 뒤 이용이 종료돼요. 다음 이용자를 위해 자리를 정리하고 퇴실해주세요.")
                            .link("ssutoday://reserve/list")
                            .build();
                    try {
                        kafkaProvider.sendMessage("pushMessage", mapper.writeValueAsString(pushMessageVo));
                    } catch (Exception e){
                        log.error("Failed to send message to Kafka", e);
                    }
                }
            }
        } catch (Exception e){
            log.error("Failed to send reservation start/end notification", e);
        }

        log.info("Finished sending reservation start/end notification");
    }

    /**
     * Send verify photo notification (scheduled job)
     * @author jonghokim27
     */
    public void verifyPhotoNotificationSend(){
        log.info("Sending verify photo notification");

        try {
            ObjectMapper mapper = new ObjectMapper();

            int nowBlock;
            Calendar todayDate = Calendar.getInstance();
            todayDate.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            todayDate.setTimeInMillis(System.currentTimeMillis());

            Calendar todayDateWithoutTime = (Calendar) todayDate.clone();
            todayDateWithoutTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            todayDateWithoutTime.set(Calendar.AM_PM, Calendar.AM);
            todayDateWithoutTime.set(Calendar.HOUR, 0);
            todayDateWithoutTime.set(Calendar.MINUTE, 0);
            todayDateWithoutTime.set(Calendar.SECOND, 0);
            todayDateWithoutTime.set(Calendar.MILLISECOND, 0);

            long todayMillis = todayDate.getTimeInMillis() - todayDateWithoutTime.getTimeInMillis();
            nowBlock = Math.toIntExact(todayMillis / 1000 / 60 / 30);

            List<Reserve> startReserves = reserveRepository.findAllByDateAndStartBlockAndDeletedAtIsNull(new Date(todayDate.getTimeInMillis()), nowBlock);
            for (Reserve reserve : startReserves) {
                Optional<Room> roomOptional = roomRepository.findById(reserve.getRoomNo());
                if(roomOptional.isEmpty()){
                    log.error("Cannot find room with no {}", reserve.getRoomNo());
                    continue;
                }

                // Check if previous -> this reservation is continuous
                long checkContinuousReserve = reserveRepository.countByStudentIdAndDateAndEndBlockAndRoomNoAndDeletedAtIsNull(
                        reserve.getStudentId(),
                        reserve.getDate(),
                        reserve.getStartBlock() - 1,
                        reserve.getRoomNo()
                );

                // Do not need to shoot verify photo
                if(checkContinuousReserve != 0){
                    continue;
                }

                List<Device> devices = deviceRepository.findAllByStudentId(reserve.getStudentId());
                for (Device device : devices) {
                    if(device.getReserve() == 0) {
                        continue;
                    }

                    PushMessageVo pushMessageVo = PushMessageVo.builder()
                            .type("token")
                            .token(device.getPushToken())
                            .title("\uD83D\uDCF7 입실 후 인증샷을 촬영해주세요.")
                            .body("10분 내로 이 알림을 터치하여 인증샷을 촬영해주세요.")
                            .link("ssutoday://reserve/list")
                            .build();
                    try {
                        kafkaProvider.sendMessage("pushMessage", mapper.writeValueAsString(pushMessageVo));
                    } catch (Exception e){
                        log.error("Failed to send message to Kafka", e);
                    }
                }
            }

        } catch (Exception e){
            log.error("Failed to send verify photo notification", e);
        }

        log.info("Finished sending verify photo notification");
    }

}

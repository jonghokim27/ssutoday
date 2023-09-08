/**
 * @filename    : Reserve.java
 * @description : Reserve Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.service.dto.*;
import org.jetbrains.annotations.NotNull;

public interface ReserveService {
    /**
     * Request reservation
     * @param reserveRequestParamDto request params
     * @return request result (ReserveRequestReturnDto)
     * @throws Exception thrown when failed to request reservation
     * @author jonghokim27
     */
    @NotNull
    ReserveRequestReturnDto reserveRequest(@NotNull ReserveRequestParamDto reserveRequestParamDto) throws Exception;

    /**
     * List reservation
     * @param reserveListParamDto list params
     * @return list result (ReserveListReturnDto)
     * @author jonghokim27
     */
    @NotNull
    ReserveListReturnDto reserveList(@NotNull ReserveListParamDto reserveListParamDto);

    /**
     * List reservation (for Room)
     * @param roomReserveListParamDto list params
     * @return list result (RoomReserveListReturnDto)
     * @author jonghokim27
     */
    @NotNull
    RoomReserveListReturnDto roomReserveList(@NotNull RoomReserveListParamDto roomReserveListParamDto);

    /**
     * Get reservation status
     * @param reserveStatusParamDto reservation params
     * @return reservation status (ReserveStatusReturnDto)
     * @throws Exception thrown when reserve request with idx and studentId does not exist
     * @author jonghokim27
     */
    @NotNull
    ReserveStatusReturnDto reserveStatus(@NotNull ReserveStatusParamDto reserveStatusParamDto) throws Exception;

    /**
     * Cancel reservation
     * @param reserveCancelParamDto cancel params
     * @return cancel result (ReserveCancelReturnDto)
     * @throws Exception thrown when reserve request with idx and studentId does not exist
     * @author jonghokim27
     */
    @NotNull
    ReserveCancelReturnDto reserveCancel(@NotNull ReserveCancelParamDto reserveCancelParamDto) throws Exception;

    /**
     * Process reservation
     * @param message reservation message
     * @author jonghokim27
     */
    void reserveProcess(@NotNull String message);

    /**
     * Send reservation start/end notification (scheduled job)
     * @author jonghokim27
     */
    void reserveNotificationSend();
}

/**
 * @filename    : StatusCode.java
 * @description : Project status codes definition
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.common;

import org.springframework.stereotype.Component;

@Component
public class StatusCode {
    // ==================================================================
    // Global

    /**
     * SSU2000
     */
    public final String SSU2000 = "SSU2000";
    public final String SSU2000_MSG = "OK";

    /**
     * SSU4000
     */
    public final String SSU4000 = "SSU4000";
    public final String SSU4000_MSG = "Bad Request";

    /**
     * SSU4001
     */
    public final String SSU4001 = "SSU4001";
    public final String SSU4001_MSG = "Unauthorized";

    /**
     * SSU4003
     */
    public final String SSU4003 = "SSU4003";
    public final String SSU4003_MSG = "Forbidden";

    /**
     * SSU4001
     */
    public final String SSU4004 = "SSU4004";
    public final String SSU4004_MSG = "Not found";

    /**
     * SSU5000
     */
    public final String SSU5000 = "SSU5000";
    public final String SSU5000_MSG = "Internal Server Error";

    //
    // ==================================================================

    // ==================================================================
    // Login (01)

    /**
     * SSU2010
     */
    public final String SSU2010 = "SSU2010";
    public final String SSU2010_MSG = "Login success";

    /**
     * SSU4010
     */
    public final String SSU4010 = "SSU4010";
    public final String SSU4010_MSG = "Failed to authenticate with uSaint";

    /**
     * SSU4011
     */
    public final String SSU4011 = "SSU4011";
    public final String SSU4011_MSG = "Unsupported major";

    //
    // ==================================================================

    // ==================================================================
    // Profile (02)

    /**
     * SSU2020
     */
    public final String SSU2020 = "SSU2020";
    public final String SSU2020_MSG = "Get profile success";

    //
    // ==================================================================

    // ==================================================================
    // Logout (03)

    /**
     * SSU2030
     */
    public final String SSU2030 = "SSU2030";
    public final String SSU2030_MSG = "Logout success";

    //
    // ==================================================================

    // ==================================================================
    // Device register (04)

    /**
     * SSU2040
     */
    public final String SSU2040 = "SSU2040";
    public final String SSU2040_MSG = "Register success";

    //
    // ==================================================================

    // ==================================================================
    // Device unregister (05)

    /**
     * SSU2050
     */
    public final String SSU2050 = "SSU2050";
    public final String SSU2050_MSG = "Unregister success";

    /**
     * SSU4050
     */
    public final String SSU4050 = "SSU4050";
    public final String SSU4050_MSG = "Not an existing device";

    //
    // ==================================================================

    // ==================================================================
    // Article list (06)

    /**
     * SSU2060
     */
    public final String SSU2060 = "SSU2060";
    public final String SSU2060_MSG = "Article list success";

    //
    // ==================================================================

    // ==================================================================
    // Check version (07)

    /**
     * SSU2070
     */
    public final String SSU2070 = "SSU2070";
    public final String SSU2070_MSG = "Using latest version";

    /**
     * SSU2071
     */
    public final String SSU2071 = "SSU2071";
    public final String SSU2071_MSG = "Update required";

    //
    // ==================================================================

    // ==================================================================
    // Get article (08)

    /**
     * SSU2080
     */
    public final String SSU2080 = "SSU2080";
    public final String SSU2080_MSG = "Get article success";

    /**
     * SSU4080
     */
    public final String SSU4080 = "SSU4080";
    public final String SSU4080_MSG = "Not an existing article";

    //
    // ==================================================================

    // ==================================================================
    // Request reserve (09)

    /**
     * SSU2090
     */
    public final String SSU2090 = "SSU2090";
    public final String SSU2090_MSG = "Request reserve success";

    /**
     * SSU5090
     */
    public final String SSU5090 = "SSU5090";
    public final String SSU5090_MSG = "Failed to request reserve";

    /**
     * SSU5091
     */
    public final String SSU5091 = "SSU5091";
    public final String SSU5091_MSG = "Reserve request disabled";

    //
    // ==================================================================

    // ==================================================================
    // Get room (10)

    /**
     * SSU2100
     */
    public final String SSU2100 = "SSU2100";
    public final String SSU2100_MSG = "Get room success";

    //
    // ==================================================================

    // ==================================================================
    // List room (11)

    /**
     * SSU2110
     */
    public final String SSU2110 = "SSU2110";
    public final String SSU2110_MSG = "List room success";

    //
    // ==================================================================

    // ==================================================================
    // Get reservation status (12)

    /**
     * SSU2120
     */
    public final String SSU2120 = "SSU2120";
    public final String SSU2120_MSG = "Get status success";

    /**
     * SSU4120
     */
    public final String SSU4120 = "SSU4120";
    public final String SSU4120_MSG = "Not an existing reservation";

    //
    // ==================================================================

    // ==================================================================
    // Get reservation list (13)

    /**
     * SSU2130
     */
    public final String SSU2130 = "SSU2130";
    public final String SSU2130_MSG = "List reservation success";

    //
    // ==================================================================

    // ==================================================================
    // Cancel reservation (14)

    /**
     * SSU2140
     */
    public final String SSU2140 = "SSU2140";
    public final String SSU2140_MSG = "Cancel reservation success";

    /**
     * SSU4140
     */
    public final String SSU4140 = "SSU4140";
    public final String SSU4140_MSG = "Not an existing reservation";

    /**
     * SSU4141
     */
    public final String SSU4141 = "SSU4141";
    public final String SSU4141_MSG = "Reserved date has passed";

    /**
     * SSU4142
     */
    public final String SSU4142 = "SSU4142";
    public final String SSU4142_MSG = "Already finished using";

    /**
     * SSU4143
     */
    public final String SSU4143 = "SSU4143";
    public final String SSU4143_MSG = "Already started to use";

    //
    // ==================================================================

    // ==================================================================
    // Generate SSO Token (15)

    /**
     * SSU2150
     */
    public final String SSU2150 = "SSU2150";
    public final String SSU2150_MSG = "Generate SSO token success";

    /**
     * SSU4150
     */
    public final String SSU4150 = "SSU4150";
    public final String SSU4150_MSG = "Not an existing SSO client";

    //
    // ==================================================================

    // ==================================================================
    // Validate SSO Token (16)

    /**
     * SSU2160
     */
    public final String SSU2160 = "SSU2160";
    public final String SSU2160_MSG = "Validate SSO token success";

    /**
     * SSU4160
     */
    public final String SSU4160 = "SSU4160";
    public final String SSU4160_MSG = "Not an existing (or expired) SSO token";

    //
    // ==================================================================
    // ==================================================================

    // ==================================================================
    // Get device option (17)

    /**
     * SSU2170
     */
    public final String SSU2170 = "SSU2170";
    public final String SSU2170_MSG = "Get device option success";

    /**
     * SSU4170
     */
    public final String SSU4170 = "SSU4170";
    public final String SSU4170_MSG = "Not an existing device";

    //
    // ==================================================================
    // ==================================================================
    // Update device option (18)

    /**
     * SSU2170
     */
    public final String SSU2180 = "SSU2180";
    public final String SSU2180_MSG = "Update device option success";

    /**
     * SSU4170
     */
    public final String SSU4180 = "SSU4180";
    public final String SSU4180_MSG = "Not an existing device";

    //
    // ==================================================================
    // ==================================================================
    // Update user xnApiToken (19)

    /**
     * SSU2190
     */
    public final String SSU2190 = "SSU2190";
    public final String SSU2190_MSG = "Update user xnApiToken success";

    //
    // ==================================================================
    // ==================================================================
    // Upload verify photo (20)

    /**
     * SSU2200
     */
    public final String SSU2200 = "SSU2200";
    public final String SSU2200_MSG = "Upload verify photo success";

    /**
     * SSU4200
     */
    public final String SSU4200 = "SSU4200";
    public final String SSU4200_MSG = "Not an existing reservation";

    /**
     * SSU4201
     */
    public final String SSU4201 = "SSU4201";
    public final String SSU4201_MSG = "Reserved date has passed";

    /**
     * SSU4202
     */
    public final String SSU4202 = "SSU4202";
    public final String SSU4202_MSG = "Already finished using";

    /**
     * SSU4203
     */
    public final String SSU4203 = "SSU4203";
    public final String SSU4203_MSG = "Not started using";

    /**
     * SSU4204
     */
    public final String SSU4204 = "SSU4204";
    public final String SSU4204_MSG = "10 minutes passed since started using";

    /**
     * SSU5200
     */
    public final String SSU5200 = "SSU5200";
    public final String SSU5200_MSG = "File upload failed";

    //
    // ==================================================================
}
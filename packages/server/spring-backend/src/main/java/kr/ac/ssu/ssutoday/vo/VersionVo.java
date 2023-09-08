/**
 * @filename    : VersionVo.java
 * @description : Version Value Object
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.vo;

import lombok.Data;

@Data
public class VersionVo implements Comparable<VersionVo> {
    private int major;
    private int minor;
    private int patch;

    public VersionVo(String version) {
        String[] splitVersion = version.split("\\.");
        major = Integer.parseInt(splitVersion[0]);
        minor = Integer.parseInt(splitVersion[1]);
        patch = Integer.parseInt(splitVersion[2]);
    }

    @Override
    public int compareTo(VersionVo o) {
        if (major == o.major) {
            if (minor == o.minor) {
                if (patch == o.patch) {
                    return 0;
                }
                return patch > o.patch ? -1 : 1;
            }
            return minor > o.minor ? -1 : 1;
        }
        return major > o.major ? -1 : 1;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}

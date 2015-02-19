package jobs.support_classes;

/**
 * Specialization class holding certain device related data.
 */
public class DeviceStats {
    private Long deviceID;
    private Integer freqEventCount;
    private Integer voltEventCount;

    public DeviceStats(){

    }

    public DeviceStats(Long deviceID, Integer freqEventCount, Integer voltEventCount) {
        this.deviceID = deviceID;
        this.freqEventCount = freqEventCount;
        this.voltEventCount = voltEventCount;
    }

    public Long getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(Long deviceID) {
        this.deviceID = deviceID;
    }

    public Integer getFreqEventCount() {
        return freqEventCount;
    }

    public void setFreqEventCount(Integer freqEventCount) {
        this.freqEventCount = freqEventCount;
    }

    public Integer getVoltEventCount() {
        return voltEventCount;
    }

    public void setVoltEventCount(Integer voltEventCount) {
        this.voltEventCount = voltEventCount;
    }
}

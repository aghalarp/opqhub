package jobs.reports;

import jobs.support_classes.DeviceStats;
import jobs.support_classes.PersonDeviceInfo;
import models.AccessKey;
import models.Event;
import models.Person;
import org.openpowerquality.protocol.OpqPacket;

import java.util.*;

/**
 * Contains methods that gathers data from database used for event email reports.
 */
public class EventReports {

    public static Map<Long, PersonDeviceInfo> generateReportFromTimeFrame(Long startTimestamp, Long endTimestamp) {
        Map<Long, PersonDeviceInfo> retMap = new HashMap<Long, PersonDeviceInfo>();

        // Get all people.
        List<Person> persons = Person.getPersons();

        // For each person, grab event stats for each device associated to person's account.
        for (Person person : persons) {
            // A DeviceStats object holds deviceID, freqEventCount, and voltEventCount.
            List<DeviceStats> deviceStatsList = new ArrayList<DeviceStats>();

            // Reminder: Each AccessKey object holds a 1-1 relationship with an OpqDevice object.
            Set<AccessKey> accessKeys = person.getAccessKeys();
            for (AccessKey accessKey : accessKeys) {
                Integer freqEventCount = Event.find().where()
                        .eq("accessKey", accessKey)
                        .eq("eventType", OpqPacket.PacketType.EVENT_FREQUENCY)
                        .ge("timestamp", startTimestamp)
                        .le("timestamp", endTimestamp)
                        .findRowCount();

                Integer voltEventCount = Event.find().where()
                        .eq("accessKey", accessKey)
                        .eq("eventType", OpqPacket.PacketType.EVENT_VOLTAGE)
                        .ge("timestamp", startTimestamp)
                        .le("timestamp", endTimestamp)
                        .findRowCount();

                //For each device, grab deviceID, freqEvents, voltEvents
                DeviceStats ds = new DeviceStats(accessKey.getDeviceId(), freqEventCount, voltEventCount);
                deviceStatsList.add(ds);
            }

            // Some users may have not yet associated a device to their account, so don't add those users.
            if (!accessKeys.isEmpty()) {
                PersonDeviceInfo pdi = new PersonDeviceInfo(person.getFirstName(), person.getLastName(),
                        person.getEmail(), deviceStatsList);

                retMap.put(person.getPrimaryKey(), pdi);
            }
        }

        return retMap;
    }

    public static Map<Long, PersonDeviceInfo> generateAllReport() {
        Map<Long, PersonDeviceInfo> retMap = new HashMap<Long, PersonDeviceInfo>();

        // Get all people.
        List<Person> persons = Person.getPersons();

        // For each person, grab event stats for each device associated to person's account.
        for (Person person : persons) {
            // A DeviceStats object holds deviceID, freqEventCount, and voltEventCount.
            List<DeviceStats> deviceStatsList = new ArrayList<DeviceStats>();

            // Reminder: Each AccessKey object holds a 1-1 relationship with an OpqDevice object.
            Set<AccessKey> accessKeys = person.getAccessKeys();
            for (AccessKey accessKey : accessKeys) {
                Integer freqEventCount = Event.find().where()
                        .eq("accessKey", accessKey)
                        .eq("eventType", OpqPacket.PacketType.EVENT_FREQUENCY)
                        .findRowCount();

                Integer voltEventCount = Event.find().where()
                        .eq("accessKey", accessKey)
                        .eq("eventType", OpqPacket.PacketType.EVENT_VOLTAGE)
                        .findRowCount();

                //For each device, grab deviceID, freqEvents, voltEvents
                DeviceStats ds = new DeviceStats(accessKey.getDeviceId(), freqEventCount, voltEventCount);
                deviceStatsList.add(ds);
            }

            // Some users may have not yet associated a device to their account, so don't add those users.
            if (!accessKeys.isEmpty()) {
                PersonDeviceInfo pdi = new PersonDeviceInfo(person.getFirstName(), person.getLastName(),
                        person.getEmail(), deviceStatsList);
                retMap.put(person.getPrimaryKey(), pdi);
            }
        }

        return retMap;
    }

}

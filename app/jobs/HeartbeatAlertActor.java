package jobs;

import akka.actor.UntypedActor;
import utils.DateUtils;
import utils.Mailer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeartbeatAlertActor extends UntypedActor {
  private static final Map<Long, Long> deviceHeartbeats = new HashMap<>();

  public static synchronized void update(Long id, Long timestamp) {
    deviceHeartbeats.put(id, timestamp);
  }

  @Override
  public void onReceive(Object message) {
    //System.out.println(message.toString());
    Set<Long> deadDevices = checkHeartbeats();
    handleDeadDevices(deadDevices);
  }

  public Set<Long> checkHeartbeats() {
    long currentTime = DateUtils.getMillis();
    long cutoff = DateUtils.getPastTime(currentTime, DateUtils.TimeUnit.Minute, .25);
    Set<Long> deadDevices = new HashSet<>();

    for(Long deviceId : deviceHeartbeats.keySet()) {
      if(deviceHeartbeats.get(deviceId) < cutoff) {
        deadDevices.add(deviceId);
      }
    }

    return deadDevices;
  }

  public void handleDeadDevices(Set<Long> deadDevices) {
    for(Long deviceId : deadDevices) {
      // Send e-mail/sms alert
      Mailer.sendEmail("anthony.christe@gmail.com", "OPQ Alert", String.format("OPQ device [%d] heartbeat not detected.", deviceId));
      System.out.println("Send email?");

      // Delete device from list
      deviceHeartbeats.remove(deviceId);
    }
  }
}

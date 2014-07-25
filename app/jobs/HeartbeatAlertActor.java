package jobs;

import akka.actor.UntypedActor;
import models.Key;
import models.OpqDevice;
import models.Person;
import play.Logger;
import utils.DateUtils;
import utils.Mailer;
import utils.Sms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HeartbeatAlertActor extends UntypedActor {
  private static final Map<Key, Long> deviceHeartbeats = new HashMap<>();

  public static synchronized void update(Key key, Long timestamp) {
    deviceHeartbeats.put(key, timestamp);
  }

  @Override
  public void onReceive(Object message) {
    //System.out.println(message.toString());
    Set<Key> deadDevices = checkHeartbeats();
    handleDeadDevices(deadDevices);
  }

  public Set<Key> checkHeartbeats() {
    Logger.debug("Checking heartbeats");
    long currentTime = DateUtils.getMillis();
    long cutoff = DateUtils.getPastTime(currentTime, DateUtils.TimeUnit.Minute, 10);
    Set<Key> deadDevices = new HashSet<>();

    for(Key key : deviceHeartbeats.keySet()) {
      if(deviceHeartbeats.get(key) < cutoff) {
        deadDevices.add(key);
      }
    }

    return deadDevices;
  }

  public void handleDeadDevices(Set<Key> deadDevices) {
    Logger.debug(String.format("Removing %d dead devices %s", deadDevices.size(), deadDevices));
    for(Key key : deadDevices) {
      Mailer.sendAlerts(key.getPersons(), "OPQ Heartbeat Alert",
                        String.format("OPQBox with deviceId = %s not detected. The device was either shutoff or " +
                        "there is a problem with it.", key.getDeviceId()));

      // Delete device from list
      deviceHeartbeats.remove(key);
    }
  }
}

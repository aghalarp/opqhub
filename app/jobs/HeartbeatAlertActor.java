package jobs;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class HeartbeatAlertActor extends UntypedActor {

  @Override
  public void onReceive(Object message) {
    System.out.println(message.toString());
  }
}

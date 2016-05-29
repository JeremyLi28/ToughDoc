package actors;


import java.util.*;
import akka.actor.*;
import controllers.Application.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class DocActor extends UntypedActor {
    public static Props props() {
        return Props.create(DocActor.class);
    }

    private final ArrayList<ActorRef> users = new ArrayList<>();
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private void addUser(ActorRef user) {
        log.info("add user");
        users.add(user);
        getContext().watch(user);
    }

    private void removeUser(ActorRef user) {
        log.info("remove user");
        users.remove(user);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Join) {
            addUser(getSender());
        }
        else if (message instanceof Exit) {
            removeUser(getSender());
        }
        else {
            unhandled(message);
        }
    }
}

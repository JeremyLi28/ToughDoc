package actors;


import java.util.*;
import akka.actor.*;
import controllers.Application.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import modules.*;

public class DocActor extends UntypedActor {
    public static Props props(DocBus bus, int docId) {
        return Props.create(DocActor.class, bus);
    }

    private final HashMap<Integer, ActorRef> users = new HashMap<>();
    private final DocBus bus;
    private int userCount;
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public DocActor(DocBus bus) {
        this.bus = bus;
        this.userCount = 0;
    }


    private void addUser(ActorRef user) {
        log.info("add user");
        user.tell();
        users.put(userCount, user);
        getContext().watch(user);
    }

    private void removeUser(ActorRef user, int userId) {
        log.info("remove user");
        users.remove(userId);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Join) {
            addUser(getSender());
        }
        else if (message instanceof Exit) {
            removeUser(getSender());
        }
        else if (message instanceof Insert) {

        }
        else if (message instanceof Delete) {

        }
        else {
            unhandled(message);
        }
    }
}

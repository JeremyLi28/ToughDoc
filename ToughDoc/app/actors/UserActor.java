package actors;

import akka.actor.*;
import controllers.Application.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.*;

public class UserActor extends UntypedActor {

    public static Props props(ActorRef out) {
        return Props.create(UserActor.class, out);
    }

    private int userId;
    private int docId = -1;
    private final ActorRef out;
    private final ActorSelection doc = controllers.Application.system.actorSelection("/user/doc");
    ;
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public UserActor(ActorRef out) {
        log.info("UserActor");
        this.out = out;
    }


    @Override
    public void preStart() {
        log.info("preStart");
        System.out.println("User: Require for Join");
        doc.tell(new Join(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            out.tell("I received your message: " + message, self());
            System.out.println("User: Require for Join");
            doc.tell(new Join(), getSelf());
        }
        else if(message instanceof AllowJoin) {
            System.out.println("User: Receive Join grant");
            this.userId = ((AllowJoin) message).userId;
        }
        else if(message instanceof AllowJoinDoc) {
            this.docId = ((AllowJoinDoc) message).docId;
        }
        else if(message instanceof AllowLeaveDoc) {
            this.docId = ((AllowLeaveDoc) message).docId;
        }
        else {
            unhandled(message);
        }
    }

    @Override
    public void postStop() {
        log.info("Exit");
        doc.tell(new Exit(userId), getSelf());
    }

    public void JoinDoc(int docId) {
        doc.tell(new JoinDoc(userId,docId), getSelf());
    }

    public void LeaveDoc(int docId) {
        doc.tell(new LeaveDoc(userId, docId), getSelf());
    }
}


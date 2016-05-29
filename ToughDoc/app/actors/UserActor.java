package actors;

import akka.actor.*;
import controllers.Application.*;

public class UserActor extends UntypedActor {

    public static Props props(ActorRef out) {
        return Props.create(UserActor.class, out);
    }

    private final ActorRef out;
    private final ActorSelection doc;

    public UserActor(ActorRef out) {
        this.out = out;
        this.doc = getContext().actorSelection("/user/doc");
    }

    @Override
    public void preStart() {
        doc.tell(new Join(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            out.tell("I received your message: " + message, self());
        }
        else {
            unhandled(message);
        }
    }
}


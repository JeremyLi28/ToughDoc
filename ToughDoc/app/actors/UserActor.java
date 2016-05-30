package actors;

import akka.actor.*;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.Application.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;

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

    public int getDocId() {
        return docId;
    }


    @Override
    public void preStart() {
        log.info("preStart");
        System.out.println("NewUser: Require for Join");
        doc.tell(new Join(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree((String)message);
            switch (json.get("type").textValue()) {
                case "JoinDoc":
                    doc.tell(new JoinDoc(userId, json.get("docId").asInt()), getSelf());
                    break;
                case "LeaveDoc":
                    break;
                case "Insert":
                    break;
                case "Delete":
                    break;
            }
        }
        else if(message instanceof AllowJoin) {
            this.userId = ((AllowJoin) message).userId;
            System.out.println("User"+userId+": Receive Join grant");
        }
        else if(message instanceof AllowJoinDoc) {
            this.docId = ((AllowJoinDoc) message).docId;
            System.out.println("User"+userId+": Receive JoinDoc grant for doc" + docId);

        }
        else if(message instanceof AllowLeaveDoc) {
            this.docId = ((AllowLeaveDoc) message).docId;
            System.out.println("User"+userId+": Receive LeaveDoc grant for doc" + docId);
        }
        else {
            unhandled(message);
        }
    }

    @Override
    public void postStop() {
        log.info("Exit");
        System.out.println("User" + userId+ ": Exit");
        doc.tell(new Exit(userId), getSelf());
    }


    public void LeaveDoc(int docId) {
        doc.tell(new LeaveDoc(userId, docId), getSelf());
    }
}


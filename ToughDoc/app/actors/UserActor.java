package actors;

import akka.actor.*;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.Application.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import modules.*;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class UserActor extends UntypedActor {

    public static Props props(ActorRef out) {
        return Props.create(UserActor.class, out);
    }

    private int userId;
    private int docId = 0;
    private final ActorRef out;
    private final ActorSelection doc = controllers.Application.system.actorSelection("/user/doc");
    private final Queue<Operation> requestQueue = new LinkedList<>();
    private final ArrayList<Operation> requestLog = new ArrayList<>();
    private final ArrayList<Integer> stateVectors = new ArrayList<>();
    private final int priority = 0;
    private final Cancellable cancellable = getContext().system().scheduler().schedule(Duration.Zero(),
            Duration.create(50, TimeUnit.MILLISECONDS), getSelf(), new Execute(), getContext().system().dispatcher(), null);

    private final ObjectMapper mapper = new ObjectMapper();
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
        doc.tell(new JoinDoc(userId, 0), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            JsonNode json = mapper.readTree((String)message);
            switch (json.get("type").textValue()) {
                case "JoinDoc":
                    doc.tell(new JoinDoc(userId, json.get("docId").asInt()), getSelf());
                    break;
                case "LeaveDoc":
                    break;
                case "Insert":
                    Insert insert = new Insert(userId, stateVectors, priority, docId, json.get("character").asText(), json.get("position").asInt());
                    requestQueue.add(insert);
                    doc.tell(insert, getSelf());
                    System.out.println("User"+userId+": Receive Insert from front-end");
                    break;
                case "Delete":
                    Delete delete = new Delete(userId, stateVectors, priority, docId, json.get("position").asInt());
                    requestQueue.add(delete);
                    doc.tell(delete, getSelf());
                    System.out.println("User"+userId+": Receive Delete from front-end");
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
        else if(message instanceof Insert) {
            if (((Insert) message).getUserID() != userId) {
                requestQueue.add((Insert)message);
                System.out.println("User" + userId + ": Receive Insert request: Insert " + ((Insert) message).getCharacter() + " at " + ((Insert) message).getPosition() + " for " + ((Insert) message).getDocID());
            }
        }
        else if(message instanceof Delete) {
            if(((Delete) message).getUserID() != userId) {
                requestQueue.add((Delete)message);
                System.out.println("User" + userId + ": Receive Delete request: Delete character at " + ((Delete) message).getPosition() + " for " + ((Delete) message).getDocID());
            }
        }
        else if(message instanceof Execute) {
            if(!requestQueue.isEmpty())
                out.tell(mapper.writeValueAsString(requestQueue.remove()), getSelf());
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
        cancellable.cancel();
    }

}


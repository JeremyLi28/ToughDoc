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
import controllers.*;

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
    private final ArrayList<Integer> stateVectors = new ArrayList<>(Arrays.asList(0,0));
    private int priority = 0;
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
                    Insert insert = new Insert(userId, new ArrayList<>(stateVectors), priority, docId, json.get("character").asText(), json.get("position").asInt());
                    requestQueue.add(insert);
                    Thread.sleep(json.get("delay").asInt()*1000);
                    doc.tell(insert, getSelf());
                    System.out.println("User"+userId+": Receive Insert from front-end");
                    break;
                case "Delete":
                    Delete delete = new Delete(userId, new ArrayList<>(stateVectors), priority, docId, json.get("position").asInt());
                    requestQueue.add(delete);
                    Thread.sleep(json.get("delay").asInt()*1000);
                    doc.tell(delete, getSelf());
                    System.out.println("User"+userId+": Receive Delete from front-end");
                    break;
            }
        }
        else if(message instanceof AllowJoin) {
            this.userId = ((AllowJoin) message).userId;
            this.priority = ((AllowJoin) message).userId;
            doc.tell(new JoinDoc(userId, 0), getSelf());
            out.tell("{\"type\": \"Join\", \"userId\":"+this.userId+"}", getSelf());
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
            if (((Insert) message).getUserId() != userId) {
                requestQueue.add((Insert)message);
                System.out.print("User" + userId + ": Receive Insert request: Insert " + ((Insert) message).getCharacter() + " at " + ((Insert) message).getPosition() + " for Doc" + ((Insert) message).getDocID());
                printStateVector(((Insert) message).getStateVector());
                System.out.print("User"+userId+" StateVector: ");
                printStateVector(stateVectors);
            }
        }
        else if(message instanceof Delete) {
            if(((Delete) message).getUserId() != userId) {
                requestQueue.add((Delete)message);
                System.out.print("User" + userId + ": Receive Delete request: Delete character at " + ((Delete) message).getPosition() + " for Doc" + ((Delete) message).getDocID());
                printStateVector(((Delete) message).getStateVector());
                System.out.print("User"+userId+" StateVector: ");
                printStateVector(stateVectors);
            }
        }
        else if(message instanceof Execute) {
            if(!requestQueue.isEmpty()) {
                Operation operation = requestQueue.remove();
                if(compareStateVector(operation.getStateVector(), stateVectors) > 0) {
                    requestQueue.add(operation);
                }
                else {
                    if(compareStateVector(operation.getStateVector(), stateVectors) < 0) {
                        for(int i=0; i< requestLog.size(); i++) {
                            Operation log = requestLog.get(i);
                            if(compareStateVector(log.getStateVector(), operation.getStateVector()) < 0)
                                continue;
                            if(operation.getStateVector().get(log.getUserId()) <= log.getStateVector().get(log.getUserId())) {
                                switch(operation.getType()){
                                    case Insert:
                                        System.out.print("User"+userId+": Operation from User"+ operation.getUserId()+" Insert "+((Insert)operation).getCharacter()
                                                +" at "+((Insert)operation).getPosition());
                                        operation = Application.T(operation, log);
                                        if(operation == null) {
                                            System.out.print(" No Operation!");
                                            return;
                                        }
                                        System.out.print(" transform to Insert "+((Insert)operation).getCharacter()
                                                +" at "+((Insert)operation).getPosition());
                                        break;
                                    case Delete:
                                        System.out.print("User"+userId+": Operation from User"+ operation.getUserId()+" Delete "+" at "+((Delete)operation).getPosition());
                                        operation = Application.T(operation, log);
                                        if(operation == null) {
                                            System.out.print(" No Operation!");
                                            return;
                                        }
                                        System.out.print(" transform to Delete at "+((Delete)operation).getPosition());
                                        break;
                                }
                                printStateVector(operation.getStateVector());
                            }
                        }
                    }
                    out.tell(mapper.writeValueAsString(operation), getSelf());
                    operation.setStateVector(new ArrayList<>(stateVectors));
                    requestLog.add(operation);
                    if(operation.getUserId() >= stateVectors.size()) {
                        for(int i=0; i<operation.getUserId()-stateVectors.size()+1; i++)
                            stateVectors.add(0);
                    }

                    stateVectors.set(operation.getUserId(), stateVectors.get(operation.getUserId())+1);
                    switch(operation.getType()){
                        case Insert:
                            System.out.print("User"+userId+": Execute operation from User"+ operation.getUserId()+" Insert "+((Insert)operation).getCharacter()
                                    +" at "+((Insert)operation).getPosition());
                            break;
                        case Delete:
                            System.out.print("User"+userId+": Execute operation from User"+ operation.getUserId()+" Delete "+" at "+((Delete)operation).getPosition());
                            break;
                    }
                    printStateVector(operation.getStateVector());
                    System.out.print("User"+userId+" StateVector: ");
                    printStateVector(stateVectors);
                }
            }
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

    private int compareStateVector(ArrayList<Integer> s1, ArrayList<Integer> s2) {
        if(s1.size() > s2.size())
            return 1;
        else if(s1.size() == s2.size()) {
            int flag = 0;
            for(int i=0; i<s1.size(); i++) {
                if(s1.get(i) > s2.get(i))
                    return 1;
                if(s1.get(i) < s2.get(i))
                    flag = 1;
            }
            if(flag == 0)
                return 0;
        }
        return -1;
    }

    private void printStateVector(ArrayList<Integer> sv) {
        System.out.print(" [");
        for(int i=0; i<sv.size(); i++)
            System.out.print(sv.get(i)+" ");
        System.out.println("]");
    }

}


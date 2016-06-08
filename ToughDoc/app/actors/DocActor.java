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
    private final HashSet<Integer> docs = new HashSet<>();
    private final DocBus bus;
    private int userCount;
    private int docCount;
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public DocActor(DocBus bus) {
        this.bus = bus;
        this.userCount = 0;
        this.docs.add(0);
    }

    public void createDoc() {
        docs.add(docCount++);
    }

    public void deleteDoc(int docId) {
        docs.remove(docId);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Join) {
            ActorRef user = getSender();
            log.info("new user join");
            System.out.println("Doc: Receive New User Join, assign ID" + userCount + "send grant");
            users.put(userCount, user);
            getContext().watch(user);
            user.tell(new AllowJoin(userCount), getSelf());
            userCount++;
        }
        else if (message instanceof Exit) {
            log.info("remove user");
            System.out.println("Doc: Receive User" + ((Exit) message).userId+" Exit");
            ActorRef user = getSender();
            bus.unsubscribe(user);
            users.remove(((Exit) message).userId);
        }
        else if (message instanceof JoinDoc) {
            log.info("JoinDoc");
            System.out.println("Doc: Receive User" + ((JoinDoc) message).userId+" JoinDoc "+((JoinDoc) message).docId);
            ActorRef user = getSender();
            bus.subscribe(user, ((JoinDoc) message).docId);
            user.tell(new AllowJoinDoc(((JoinDoc) message).docId), getSelf());
        }
        else if (message instanceof LeaveDoc) {
            log.info("LeaveDoc");
            System.out.println("Doc: Receive User" + ((LeaveDoc) message).userId+" LeaveDoc "+((LeaveDoc) message).docId);
            ActorRef user = getSender();
            bus.unsubscribe(user, ((LeaveDoc) message).docId);
            user.tell(new AllowLeaveDoc(((LeaveDoc) message).docId), getSelf());
        }
        else if (message instanceof Insert) {
            System.out.println("Doc: Receive Insert request from user"+((Insert) message).getUserID()+": Insert " + ((Insert) message).getCharacter()+" at "+((Insert) message).getPosition()+" for "+((Insert) message).getDocID());
            Thread.sleep(5000);
            bus.publish((Insert)message);
        }
        else if (message instanceof Delete) {
            System.out.println("Doc: Receive Delete request from user"+((Delete) message).getUserID()+": Delete character at "+((Delete) message).getPosition()+" for "+((Delete) message).getDocID());
            Thread.sleep(5000);
            bus.publish((Delete)message);
        }
        else {
            System.out.println("Doc: Receive unhandled message");
            unhandled(message);
        }
    }



}

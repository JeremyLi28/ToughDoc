package modules;


import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;


public class DocBus extends LookupEventBus<Operation, ActorRef, Integer>{
    @Override
    public int mapSize() {
        return 128;
    }

    @Override
    public int compareSubscribers(ActorRef a, ActorRef b) {
//        return ((UserActor)a).getDocId() - ((UserActor)b).getDocId();
        return a.compareTo(b);
    }

    @Override
    public Integer classify(Operation operation) {
        return (operation).getDocID();
    }

    @Override
    public void publish(Operation operation, ActorRef subscriber) {
        subscriber.tell(operation, ActorRef.noSender());
    }
}

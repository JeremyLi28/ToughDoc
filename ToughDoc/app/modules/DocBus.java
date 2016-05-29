package modules;


import akka.actor.ActorRef;
import akka.event.japi.LookupEventBus;

import java.util.Objects;

public class DocBus extends LookupEventBus{
    @Override
    public int mapSize() {
        return 5;
    }

    @Override
    public int compareSubscribers(Object a, Object b) {
        return ((Operation)a).getDocID() - ((Operation)b).getDocID();
    }

    @Override
    public Object classify(Object operation) {
        return ((Operation)operation).getDocID();
    }

    @Override
    public void publish(Object operation, Object subscriber) {
        ((ActorRef)subscriber).tell(operation, ActorRef.noSender());
    }
}

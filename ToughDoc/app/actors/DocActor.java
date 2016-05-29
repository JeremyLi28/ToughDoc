package actors;


import akka.actor.Props;
import akka.actor.UntypedActor;

public class DocActor extends UntypedActor {
    public static Props props() {
        return Props.create(DocActor.class);
    }

    public DocActor() {
    }

    public void onReceive(Object message) throws Exception {

    }
}

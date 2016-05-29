package controllers;

import play.*;
import play.mvc.*;
import akka.actor.*;
import play.libs.F.*;
import play.mvc.WebSocket;
import actors.*;
import java.util.*;
import java.io.Serializable;


import views.html.*;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("ToughDoc"));
    }

    // centeral components
    private final ActorSystem system = ActorSystem.create("ToughDoc");
    private final ActorRef doc = system.actorOf(Props.create(DocActor.class), "doc");

    // messages
    public static class Join implements Serializable {}
    public static class Exit implements Serializable {}

    public WebSocket<String> ws() {
        return WebSocket.withActor(UserActor::props);
    }


}

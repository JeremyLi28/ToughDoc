package controllers;

import play.*;
import play.mvc.*;
import akka.actor.*;
import play.libs.F.*;
import play.mvc.WebSocket;
import actors.*;
import java.util.*;

import views.html.*;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("ToughDoc"));
    }

    private final ActorSystem system = ActorSystem.create("ToughDoc");
    private final ActorRef doc = system.actorOf(Props.create(DocActor.class), "doc");

    public WebSocket<String> ws() {
        return WebSocket.withActor(UserActor::props);
    }


}

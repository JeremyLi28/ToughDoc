package controllers;

import modules.*;
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

    public enum Type {
        Insert, Delete
    }

    public Result index() {
        return ok(index.render("ToughDoc"));
    }

    // centeral components
    private final ActorSystem system = ActorSystem.create("ToughDoc");
    private final DocBus bus = new DocBus();
    private final ActorRef doc = system.actorOf(Props.create(DocActor.class, bus), "doc");

    // messages

    // user message
    public static class Join implements Serializable {}

    public static class Exit implements Serializable {
        public int userId;
        public int docId;
        public Exit(int userId) {
            this.userId = userId;
            this.docId = docId;
        }
    }

    public static class JoinDoc implements Serializable {
        public int userId;
        public int docId;
        public JoinDoc(int userId, int docId) {
            this.userId = userId;
            this.docId = docId;
        }
    }

    public static class LeaveDoc implements Serializable {
        public int userId;
        public int docId;
        public LeaveDoc(int userId, int docId) {
            this.userId = userId;
            this.docId = docId;
        }
    }

    // doc message
    public static class AllowJoin implements Serializable {
        public int userId;
        public AllowJoin(int userId) {
            this.userId = userId;
        }
    }

    public static class AllowJoinDoc implements Serializable {
        public int docId;
        public AllowJoinDoc(int docId) {
            this.docId = docId;
        }
    }

    public static class AllowLeaveDoc implements Serializable {
        public int docId;
        public AllowLeaveDoc(int docId) {
            this.docId = docId;
        }
    }

    public WebSocket<String> ws() {

        return WebSocket.withActor(UserActor::props);
    }


}

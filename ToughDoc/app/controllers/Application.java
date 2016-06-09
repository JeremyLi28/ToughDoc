package controllers;

import modules.*;
import play.*;
import play.api.libs.iteratee.RunQueue;
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
    public static final ActorSystem system = ActorSystem.create("ToughDoc");
    private final DocBus bus = new DocBus();
    private final ActorRef doc = system.actorOf(Props.create(DocActor.class, bus), "doc");

    // Transformation functions
    public static Operation T(Operation op1, Operation op2) {
        switch (op1.getType()) {
            case Insert: {
                switch (op2.getType()) {
                    case Insert:
                        return T11((Insert)op1, (Insert)op2);
                    case Delete:
                        return T12((Insert)op1, (Delete)op2);
                }
            }
            case Delete: {
                switch (op2.getType()) {
                    case Insert:
                        return T21((Delete)op1, (Insert)op2);
                    case Delete:
                        return T22((Delete)op1, (Delete)op2);
                }
            }
        }
        return op1;
    }

    private static Insert T11(Insert op1, Insert op2) {
        if(op1.getPosition() < op2.getPosition())
            return op1;
        else if(op1.getPosition() > op2.getPosition()) {
            Insert new_op = new Insert(op1);
            new_op.setPosition(op1.getPosition() + op2.getCharacter().length());
            return new_op;
        }
        else {
            if(op1.getCharacter().equals(op2.getCharacter()))
                return null;
            else {
                if(op1.getPriority() < op2.getPriority())
                    return op1;
                else {
                    Insert new_op = new Insert(op1);
                    new_op.setPosition(op1.getPosition() + op2.getCharacter().length());
                    return new_op;
                }
            }
        }
    }

    private static Insert T12(Insert op1, Delete op2) {
        if(op1.getPosition() <= op2.getPosition())
            return op1;
        else {
            Insert new_op = new Insert(op1);
            new_op.setPosition(op1.getPosition()-1);
            return new_op;
        }
    }

    private static Delete T21(Delete op1, Insert op2) {
        if(op1.getPosition() < op2.getPosition())
            return op1;
        else {
            Delete new_op = new Delete(op1);
            new_op.setPosition(op1.getPosition()+op2.getCharacter().length());
            return new_op;
        }
    }

    private static Delete T22(Delete op1, Delete op2) {
        if(op1.getPosition() < op2.getPosition())
            return op1;
        else if(op1.getPosition() > op2.getPosition()) {
            Delete new_op = new Delete(op1);
            new_op.setPosition(op1.getPosition() - 1);
            return new_op;
        }
        else
            return null;
    }



    // messages

    // user message
    public static class Join implements Serializable {}

    public static class Exit implements Serializable {
        public int userId;
        public Exit(int userId) {
            this.userId = userId;
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

    public static class Execute implements Serializable {}

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

package plain;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import java.util.ArrayList;

class Ref<T> {
    T value;

    public Ref(T t) {
        this.value = t;
    }
}

class FinalActor extends UntypedActor {

    ArrayList<ActorRef> act = new ArrayList<>();

    int count = 0;

    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("msg: " + message);
        act.add(sender());
        if (act.size() == 3) {
            if (count == 10) return;
            count++;
            for (ActorRef a : act) {
                a.tell("123", self());
            }
            act = new ArrayList<>();
        }
    }
}

class WorkerActor extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("Calculate " + message);
        sender().tell("123", self());
    }
}

public class TestActor {

    public static void main(String[] args) {
        //        ActorSy
        ActorSystem system = ActorSystem.create("actor-demo-java");

        ActorRef a1 = system.actorOf(Props.create(WorkerActor.class));
        ActorRef a2 = system.actorOf(Props.create(WorkerActor.class));
        ActorRef a3 = system.actorOf(Props.create(WorkerActor.class));

        ActorRef f = system.actorOf(Props.create(FinalActor.class));

        f.tell("1", a1);
        f.tell("2", a2);
        f.tell("3", a3);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            /* ignore */
        }
        system.shutdown();
    }
}

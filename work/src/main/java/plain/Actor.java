package plain;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import java.util.ArrayList;
import java.util.Arrays;

// Stable object
// Optimization: Solution => Memory

class Vector {
    public final double[] array;

    public Vector(double[] arr) {
        this.array = arr;
    }

    public Vector addV(Vector v) {
        final double[] arr = new double[v.array.length];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = this.array[i] + v.array[i];
        }
        return new Vector(arr);
    }

    public Vector subV(Vector v) {
        final double[] arr = new double[v.array.length];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = this.array[i] - v.array[i];
        }
        return new Vector(arr);
    }

    public Vector mulInPlace(double v) {
        for (int i = 0; i < this.array.length; ++i) {
            array[i] = this.array[i] * v;
        }
        return this;
    }

    public Vector clone() {
        return new Vector(Arrays.copyOf(this.array, this.array.length));
    }

    public static Vector random(int number, double min, double max) {
        double[] arr = new double[number];
        for (int i = 0; i < number; ++i) {
            arr[i] = Math.random() * (max - min) + min;
        }
        return new Vector(arr);
    }
}

class Solution {
    public final Vector positions;
    public double value;

    public Solution(Vector positions) {
        this.positions = positions;
        this.constraint();
        this.value = this.evaluate();
    }

    public void constraint() {
        double rest = 400;
        for (int i = 0; i < 4; ++i) {
            if (this.positions.array[i] < 0) {
                this.positions.array[i] = 0;
                continue;
            }
            if (rest < this.positions.array[i]) {
                this.positions.array[i] = rest;
            }
            rest -= this.positions.array[i];
        }
    }

    public double evaluate() {
        double[] p = this.positions.array;
        double v1 =
                4 * Math.sqrt(p[0])
                        + 3 * Math.sqrt(p[1])
                        + 2 * Math.sqrt(p[2])
                        + 1 * Math.sqrt(p[3]);
        double v2 = p[1] * 1.1 + p[2] * 1.1 * 1.1 + p[3] * 1.1 * 1.1 * 1.1;
        this.value = v1 + v2;
        return this.value;
    }

    public boolean isBetter(Solution other) {
        return this.value > other.value;
    }

    public String toStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fitness: ");
        sb.append(this.value);
        sb.append("\n");
        for (double e : this.positions.array) {
            sb.append(e);
            sb.append(", ");
        }
        return sb.toString();
    }
}

class Partical {

    public Solution solution;
    public Vector velocities;
    public Solution bestSolution;

    public double w = 0.8;
    public double c1 = 2;
    public double c2 = 2;

    public Partical(Solution solution, Vector velocities) {
        this.solution = solution;
        this.velocities = velocities;
        this.bestSolution = solution;
    }

    public static Partical random(int num) {
        Solution s = new Solution(Vector.random(num, -100, 100));
        return new Partical(s, Vector.random(num, -10, 10));
    }

    //    v[] = w * v[] + c1 * rand() * (pbest[] - present[]) + c2 * rand() * (gbest[] - present[]) (a)
    public void updateVelocities(Solution gbest) {
        double[] v = this.velocities.array;
        this.velocities =
                this.velocities
                        .mulInPlace(w)
                        .addV(
                                this.bestSolution
                                        .positions
                                        .subV(solution.positions)
                                        .mulInPlace(c1 * Math.random()))
                        .addV(
                                gbest.positions
                                        .subV(solution.positions)
                                        .mulInPlace(c2 * Math.random()));
        return;
    }

    public void updatePositions() {
        this.solution = new Solution(this.solution.positions.addV(this.velocities));
    }

    public void run(Solution gbest) {
        this.updateVelocities(gbest);
        this.updatePositions();
        if (this.solution.isBetter(this.bestSolution)) {
            this.bestSolution = this.solution;
        }
    }
}

class ParticalSwarm {
    public ArrayList<Partical> ps;

    public int num;

    public Solution bestSolution;

    public ParticalSwarm(int num) {

        this.num = num;

        this.ps = new ArrayList<>(num);
        for (int i = 0; i < num; ++i) {
            this.ps.add(Partical.random(4));
        }

        this.bestSolution = this.ps.get(0).bestSolution;
    }

    public void chooseBest() {
        this.ps.forEach(
                (e) -> {
                    if (e.bestSolution.isBetter(this.bestSolution)) {
                        this.bestSolution = e.bestSolution;
                    }
                });
    }
}

class ParticalActor extends UntypedActor {

    Partical p;

    public ParticalActor(Partical p) {
        this.p = p;
    }

    public static Props props(Partical p) {
        return Props.create(ParticalActor.class, p);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Solution) {
            this.p.run((Solution) message);
            sender().tell(this, self());
        }
    }
}

class ParticalSwarmActor extends UntypedActor {

    ParticalSwarm particalSwarm;

    ArrayList<ActorRef> arr;

    ActorSystem system;

    int generation = 0;

    public ParticalSwarmActor(ActorSystem system, ParticalSwarm ps) {

        this.system = system;

        this.particalSwarm = ps;
        this.arr = new ArrayList<>(ps.ps.size());
        this.particalSwarm.ps.forEach(
                (e) -> {
                    this.arr.add(system.actorOf(ParticalActor.props(e)));
                });

        this.activate();
    }

    int sender_count = 0;

    @Override
    public void onReceive(Object message) throws Exception {
        this.sender_count++;
        if (this.sender_count < this.particalSwarm.ps.size()) {
            return;
        }
        this.activate();
    }

    public void activate() {

        if (this.generation > 100) {
            this.close();
        }

        this.generation++;

        this.sender_count = 0;
        this.particalSwarm.chooseBest();
        for (ActorRef e : this.arr) {
            e.tell(this.particalSwarm.bestSolution, self());
        }
    }

    public void close() {
        this.system.shutdown();
    }
}

public class Actor {

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("actor-demo-java");

        ParticalSwarm particalSwarm = new ParticalSwarm(30);

        system.actorOf(Props.create(ParticalSwarmActor.class, system, particalSwarm));

        system.awaitTermination();
        System.out.println(particalSwarm.bestSolution.toStr());
    }
}

package pso;

import java.util.Arrays;

// Stable object
// Optimization: Solution => Memory

class Solution {
    public double[] positions = new double[4];

    public Solution() {
        this.positions = new double[4];
        this.init();
    }

    public void init(){
        for (int i=0; i<4; ++i) {
            this.positions[i] = Math.random() * 400;
        }
        this.constraint();
    }

    public void constraint() {
        double rest = 400;
        for (int i = 0; i < 4; ++i) {
            if (this.positions[i] < 0) {
                this.positions[i] = 0;
                continue;
            }
            if (rest < this.positions[i]) {
                this.positions[i] = rest;
            }
            rest -= this.positions[i];
        }
    }

    public double evaluate() {
        double[] p = this.positions;
        double v1 =
                4 * Math.sqrt(p[0])
                        + 3 * Math.sqrt(p[1])
                        + 2 * Math.sqrt(p[2])
                        + 1 * Math.sqrt(p[3]);
        double v2 = p[1] * 1.1 + p[2] * 1.1 * 1.1 + p[3] * 1.1 * 1.1 * 1.1;
        return v1 + v2;
    }

    public Solution clone() {
        Solution a = new Solution();
        a.positions = Arrays.copyOf(this.positions, this.positions.length);
        return a;
    }

    public String toStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; ++i) {
            sb.append(this.positions[i]).append(", ");
        }
        return sb.toString();
    }
}

class Partical {

    public Solution solution;
    public double[] velocities = new double[4];

    public double w = 0.3;
    public double c1 = 2;
    public double c2 = 2;

    public Solution best;
    public double bestValue;

    public Partical() {
        this.solution = new Solution();
        this.velocities = new double[] {10, 10, 10, 10};
        this.best = this.solution;
        this.bestValue = this.solution.evaluate();
    }

    //    v[] = w * v[] + c1 * rand() * (pbest[] - present[]) + c2 * rand() * (gbest[] - present[]) (a)
    public void updateVelocities(Solution gbest) {
        double[] v = this.velocities;
        for (int i = 0; i < 4; ++i) {
            v[i] =
                    w * v[i]
                            + c1 * Math.random() * (this.best.positions[i] - solution.positions[i])
                            + c2 * Math.random() * (gbest.positions[i] - solution.positions[i]);
        }

        return;
    }

    public void updatePositions() {
        for (int i = 0; i < 4; ++i) {
            this.solution.positions[i] += this.velocities[i];
        }
        this.solution.constraint();
    }

    public double run(Solution gbest) {
        this.updateVelocities(gbest);
        this.updatePositions();
        double v = this.solution.evaluate();
        if (v > this.bestValue) {
            this.bestValue = v;
            this.best = this.solution.clone();
        }
        this.solution.constraint();
        return v;
    }

}

class ParticalManager {
    public Partical[] ps;

    public int num;

    public Solution best;
    public double bestValue;

    public ParticalManager(int num) {

        this.num = num;

        this.ps = new Partical[num];
        for (int i = 0; i < num; ++i) {
            this.ps[i] = new Partical();
        }

        this.best = this.ps[0].solution;
        this.bestValue = this.ps[0].bestValue;
    }

    public void run() {
        for (int i = 0; i < this.num; ++i) {
            double v = this.ps[i].run(this.best);
            if (v > this.bestValue) {
                this.bestValue = v;
                this.best = this.ps[i].solution;
            }
        }
    }


    public void work(int run){

        double lastBest = 0;
        int lastBestRun = 0;
        int i=0;
        for (; i < run; ++i) {
            if (i - lastBestRun > 30) {
                break;
            }

            this.run();
            if (this.bestValue > lastBest) {
                lastBest = this.bestValue;
                lastBestRun = i;
            }
        }

        System.out.println(this.bestValue + " " + i + "\n" + this.best.toStr());

    }

}

public class Main {

    public static void main(String[] args) {

        ParticalManager pm = new ParticalManager(50);

        pm.work(10000);

    }
}

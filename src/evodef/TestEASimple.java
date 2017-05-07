package evodef;

import bandits.MBanditEA;
import evogame.Mutator;
import ga.SimpleRMHC;
import ntuple.NTupleBanditEA;
import ntuple.NTupleSystem;
import utilities.ElapsedTimer;
import utilities.StatSummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provide a simple main method to test this...
 */

public class TestEASimple {

    static int nDims = 10;
    static int mValues = 2;

    static int nTrialsRMHC = 10000;
    static int nTrialsNTupleBanditEA = 30;

    static int nFitnessEvals = 500;

    static boolean useFirstHit = false;

    public static void main(String[] args) {

        ElapsedTimer t = new ElapsedTimer();


//        StatSummary nt = testNTupleBanditEA(nTrialsNTupleBanditEA);
//        System.out.println(t);

        // Mutator.totalRandomChaosMutation = true;

        testRMHCAlone();
        System.out.println(t);


    }

    static StatSummary testNTupleBanditEA(int nTrials) {
        StatSummary ss = runTrials(new NTupleBanditEA(), nTrials, 0);
        System.out.println(ss);
        return ss;
    }

    static void testRMHCAlone() {
        // simpler version does not compare performance with NT
        ArrayList<StatSummary> results = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            // System.out.println("Resampling rate: " + i);
            StatSummary ss2 = runTrials(new SimpleRMHC(i), nTrialsRMHC, i);
            results.add(ss2);
            // System.out.println(ss2);
            // System.out.format("Resample rate: %d\t %.3f\t %.3f \t %.3f   \n", i, ss2.mean(), ss2.stdErr(), ss2.max());
        }

    }


    static void testRMHC(StatSummary nt) {
        ArrayList<StatSummary> results = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            // System.out.println("Resampling rate: " + i);
            StatSummary ss2 = runTrials(new SimpleRMHC(i), nTrialsRMHC, i);
            results.add(ss2);
            // System.out.println(ss2);
            System.out.format("Resample rate: %d\t %.3f\t %.3f \t %.3f   \n", i, ss2.mean(), ss2.stdErr(), ss2.max());
        }

        printJS(results, nt);

        //
    }

    // this is to print out rows of JavaScript for use in a GoogleChart
    public static void printJS(List<StatSummary> ssl, StatSummary nt) {
        double ntm = nt.mean();
        double ntUpper = ntm + nt.stdErr() * 3;
        double ntLower = ntm - nt.stdErr() * 3;
        for (int i = 0; i < ssl.size(); i++) {
            StatSummary ss = ssl.get(i);
            double m = ss.mean();
            double upper = m + 3 * ss.stdErr();
            double lower = m - 3 * ss.stdErr();
            System.out.format("[ %d, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, ],\n", (1 + i), m, lower, upper, ntm, ntLower, ntUpper);
        }
    }


    public static StatSummary runTrials(EvoAlg ea, int nTrials, int nResamples) {
        StatSummary ss = new StatSummary();
        StatSummary nTrueOpt = new StatSummary("N True Opt Hits");

        for (int i = 0; i < nTrials; i++) {
            ss.add(runTrial(ea, nTrueOpt));
        }

        // System.out.println(ss);

        System.out.format("N Resamples: \t %2d ;\t n True Opt %s: %d\n",nResamples, useFirstHit ? "hits" : "returns", nTrueOpt.n());
        return ss;
    }
    static double runTrial(EvoAlg ea, StatSummary nTrueOpt) {

//        SolutionEvaluator evaluator = new EvalMaxM(nDims, mValues, 1.0);
//        SolutionEvaluator trueEvaluator = new EvalMaxM(nDims, mValues, 0.0);

        SolutionEvaluator evaluator = new EvalNoisyWinRate(nDims, mValues, 1.0);
        SolutionEvaluator trueEvaluator = new EvalNoisyWinRate(nDims, mValues, 0.0);

        // just remember how best to do this !!!

        evaluator.reset();

        int[] solution = ea.runTrial(evaluator, nFitnessEvals);

        if (useFirstHit && evaluator.logger().firstHit != null) {
            // System.out.println("Optimal first hit?: " + evaluator.logger().firstHit);
            nTrueOpt.add(evaluator.logger().firstHit);
        } else if (trueEvaluator.evaluate(solution) == 1.0) {
            nTrueOpt.add(1);
        }



//        System.out.println();
//        System.out.println("Returned solution: " + Arrays.toString(solution));
//        System.out.println("Fitness = " + trueEvaluator.evaluate(solution));

        return trueEvaluator.evaluate(solution);


    }
}

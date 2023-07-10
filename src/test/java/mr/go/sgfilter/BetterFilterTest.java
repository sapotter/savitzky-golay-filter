package mr.go.sgfilter;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.Linearizer;
import mr.go.sgfilter.MeanValuePadder;
import mr.go.sgfilter.RamerDouglasPeuckerFilter;
import mr.go.sgfilter.SGFilter;
import mr.go.sgfilter.ZeroEliminator;

import org.junit.Test;

public class BetterFilterTest {

    private void assertCoeffsEqual(double[] coeffs, double[] tabularCoeffs) {
        for (int i = 0; i < tabularCoeffs.length; i++) {
            double rDiff = relativeDifference(tabularCoeffs[i], coeffs[i]);
            assertTrue(String.format("real | calc | rDiff: %g | %g | %g", tabularCoeffs[i], coeffs[i], rDiff),
                      (rDiff <= 0.005));
//            assertTrue(relativeDifference(tabularCoeffs[i], coeffs[i]) <+ 0.001);
//            assertEquals(tabularCoeffs[i],
//                         coeffs[i],
//                         0.001);
        }
    }

    private void assertResultsEqual(double[] results, double[] real, double delta) {
        for (int i = 0; i < real.length; i++) {
            double rDiff = relativeDifference(real[i], results[i]);
            assertTrue(String.format("real | calc | rDiff:: %g | %g | %g", real[i], results[i], rDiff),
                      rDiff <= delta);
//            assertTrue(relativeDifference(real[i], results[i]) < delta);
//            assertEquals(real[i],
//                         results[i],
//                         delta);
        }
    }
    
    private void assertResultsEqual(double[] results, double[] real) {
        assertResultsEqual(results, real, 0.001);
    }

    private void assertResultsEqual(float[] results, double[] real, double delta) {
        for (int i = 0; i < real.length; i++) {
            double rDiff = relativeDifference(real[i], results[i]);
            assertTrue(String.format("real | calc | rDiff:: %g | %g | %g", real[i], results[i], rDiff),
                      rDiff <= delta);
//            assertTrue(relativeDifference(real[i], results[i]) < delta);
//             assertEquals(real[i],
//                         results[i],
//                         delta);
        }
    }
    
    private void assertResultsEqual(float[] results, double[] real) {
        assertResultsEqual(results, real, 0.001);
    }

    private void assertResultsEqual(float[] results, float[] real, double delta) {
        for (int i = 0; i < real.length; i++) {
            double rDiff = relativeDifference(real[i], results[i]);
            assertTrue(String.format("real | calc | rDiff:: %g | %g | %g", real[i], results[i], rDiff),
                      rDiff <= delta);
//            assertTrue(relativeDifference(real[i], results[i]) < delta);
//            assertEquals(real[i],
//                         results[i],
//                         delta);
        }
    }
    
    private void assertResultsEqual(float[] results, float[] real) {
        assertResultsEqual(results, real, 0.001);
    }

    private double relativeDifference(double real, double result) {
        double ref = Math.max(Math.abs(real), Math.abs(result));
        if (ref == 0.0) {
            return 0.0;
        } else {
           return Math.abs(result - real)/ref;
        }
    }

    private float relativeDifference(double real, float result) {
       return (float) relativeDifference(real, (double) result);
    }
    
    private float relativeDifference(float real, float result) {
       return (float) relativeDifference((double) real, (double) result);
    }
    
    @Test
    public final void testComputeSGCoefficients() {
        double[] coeffs = SGFilter.computeSGCoefficients(5,
                                                         5,
                                                         2);
        double[] tabularCoeffs = new double[]{-0.084,
                                              0.021,
                                              0.103,
                                              0.161,
                                              0.196,
                                              0.207,
                                              0.196,
                                              0.161,
                                              0.103,
                                              0.021,
                                              -0.084
        };
        assertEquals(11,
                     coeffs.length);
        assertCoeffsEqual(coeffs,
                          tabularCoeffs);
        coeffs = SGFilter.computeSGCoefficients(5,
                                                5,
                                                4);
        tabularCoeffs = new double[]{0.042,
                                     -0.105,
                                     -0.0233,
                                     0.140,
                                     0.280,
                                     0.333,
                                     0.280,
                                     0.140,
                                     -0.0233,
                                     -0.105,
                                     0.042};
        assertEquals(11,
                     coeffs.length);
        assertCoeffsEqual(coeffs,
                          tabularCoeffs);
        coeffs = SGFilter.computeSGCoefficients(4,
                                                0,
                                                2);
        tabularCoeffs = new double[]{0.086,
                                     -0.143,
                                     -0.086,
                                     0.257,
                                     0.886};
        assertEquals(5,
                     coeffs.length);
        assertCoeffsEqual(coeffs,
                          tabularCoeffs);
    }

    @Test
    public final void testDouglasPeuckerFilter() {
        double[] coeffs = SGFilter.computeSGCoefficients(5,
                                                         5,
                                                         4);
        float[] data = new float[]{2.9f,
                                   1.3f,
                                   1.5f,
                                   1.6f,
                                   1.6f,
                                   1,
                                   1.5f,
                                   2,
                                   1.5f,
                                   1,
                                   1,
                                   1,
                                   1,
                                   1,
                                   1};
        double[] real = new double[]{1.5680653,
                                     1.3634033,
                                     1.2237762};
        SGFilter sgFilter = new SGFilter(5,
                                         5);
        sgFilter.appendDataFilter(new RamerDouglasPeuckerFilter(0.5));
        float[] smooth = sgFilter.smooth(data,
                                         5,
                                         10,
                                         coeffs);
        assertResultsEqual(smooth,
                           real);
    }

    @Test
    public final void testMeanValuePadderLeft() {
        double[] data = new double[]{0, 0, 0, 0, 0,
                                     8915.06,
                                     8845.53,
                                     9064.17,
                                     8942.09,
                                     8780.87,
                                     8916.81,
                                     8934.24,
                                     9027.06,
                                     9160.79,
                                     7509.14};
        double[] real = new double[]{8909.544000000002, 8909.544000000002,
                                     8909.544000000002, 8909.544000000002,
                                     8909.544000000002,
                                     8915.06,
                                     8845.53,
                                     9064.17,
                                     8942.09,
                                     8780.87,
                                     8916.81,
                                     8934.24,
                                     9027.06,
                                     9160.79,
                                     7509.14};
        new MeanValuePadder(10, true, false).apply(data);
        assertResultsEqual(data, real);
    }

    @Test
    public final void testLinearizer() {
        double[] data = new double[]{6945.43,
                                     0,
                                     0,
                                     7221.76,
                                     4092.77,
                                     6607.28,
                                     6867.01};
        double[] real = new double[]{6945.43,
                                     0,
                                     0,
                                     2202.426666666667,
                                     4404.853333333333,
                                     6607.280000000001,
                                     6867.01};
        new Linearizer(0.08f).apply(data);
        assertResultsEqual(data, real);
    }

    @Test
    public final void testMeanValuePadderRight() {
        double[] data = new double[]{8915.06,
                                     8845.53,
                                     9064.17,
                                     8942.09,
                                     8780.87,
                                     8916.81,
                                     8934.24,
                                     9027.06,
                                     9160.79,
                                     7509.14,
                                     0, 0, 0, 0};
        double[] real = new double[]{8915.06,
                                     8845.53,
                                     9064.17,
                                     8942.09,
                                     8780.87,
                                     8916.81,
                                     8934.24,
                                     9027.06,
                                     9160.79,
                                     7509.14,
                                     8709.608, 8709.608, 8709.608, 8709.608};
        new MeanValuePadder(10, false, true).apply(data);
        assertResultsEqual(data, real);
    }

    @Test
    public final void testSmooth() {
        float[] data = new float[]{8916.81f,
                                   8934.24f,
                                   9027.06f,
                                   9160.79f,
                                   7509.14f};
        float[] leftPad = new float[]{8915.06f,
                                      8845.53f,
                                      9064.17f,
                                      8942.09f,
                                      8780.87f};
        double[] realResult1 = new double[]{8989.485464,
                                            9070.934158,
                                            8957.906284,
                                            8577.50381,
                                            8055.909912};
        double[] realResult2 = new double[]{9039.854903,
                                            8995.380001,
                                            8854.369105,
                                            8641.864759,
                                            8456.067118};

        double[] coeffs = SGFilter.computeSGCoefficients(5,
                                                         5,
                                                         4);
        ContinuousPadder padder1 = new ContinuousPadder(false,
                                                        true);
        SGFilter sgFilter = new SGFilter(5,
                                         5);
        sgFilter.appendPreprocessor(padder1);
        float[] smooth1 = sgFilter.smooth(data,
                                          leftPad,
                                          new float[0],
                                          coeffs);
        assertResultsEqual(smooth1,
                           realResult1,
                           0.01);
        MeanValuePadder padder2 = new MeanValuePadder(10,
                                                      false,
                                                      true);
        sgFilter.removePreprocessor(padder1);
        sgFilter.appendPreprocessor(padder2);
        float[] smooth2 = sgFilter.smooth(data,
                                          leftPad,
                                          new float[0],
                                          coeffs);
        assertResultsEqual(smooth2,
                           realResult2,
                           .01);
    }

    @Test
    public final void testSmoothWithBias() {
        double[] coeffs5_5 = SGFilter.computeSGCoefficients(5,
                                                            5,
                                                            4);
        double[] coeffs5_4 = SGFilter.computeSGCoefficients(5,
                                                            4,
                                                            4);
        double[] coeffs4_5 = SGFilter.computeSGCoefficients(4,
                                                            5,
                                                            4);
        float[] data = new float[]{12680.43f,
                                   18316.83f,
                                   18316.83f,
                                   18316.83f,
                                   18316.83f,
                                   18120.89f,
                                   18120.89f,
                                   18897.22f,
                                   18897.22f,
                                   18470.61f,
                                   18470.61f,
                                   18470.61f,
                                   18470.61f};
        double[] real = new double[]{18129.17,
                                     18018.18,
                                     18426.96,
                                     18598.67,
                                     18727.08};
        SGFilter sgFilter = new SGFilter(5,
                                         5);
        float[] smooth = sgFilter.smooth(data,
                                         4,
                                         9,
                                         1,
                                         new double[][]{
                    coeffs5_5,
                    coeffs5_4,
                    coeffs4_5});
        assertResultsEqual(smooth,
                           real,
                           .001);
    }
}

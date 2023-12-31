package mr.go.sgfilter;

import static org.junit.Assert.assertEquals;
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.Linearizer;
import mr.go.sgfilter.MeanValuePadder;
import mr.go.sgfilter.SGFilter;
import mr.go.sgfilter.ZeroEliminator;

import org.junit.Test;

public class FilterTest {

    private void assertCoeffsEqual(double[] coeffs, double[] tabularCoeffs) {
        for (int i = 0; i < tabularCoeffs.length; i++) {
            assertEquals("coefficient index: " + i, tabularCoeffs[i], coeffs[i], 0.001);
        }
    }

    private void assertCoeffsEqual(String msg, double[] coeffs, double[] tabularCoeffs) {
        for (int i = 0; i < tabularCoeffs.length; i++) {
            assertEquals(msg + i, tabularCoeffs[i], coeffs[i], 0.001);
        }
    }

    private void assertResultsEqual(float[] results, double[] real) {
        for (int i = 0; i < real.length; i++) {
            assertEquals("results index: " + i, real[i], results[i], 0.01);
        }
    }

    @Test
    public final void testComputeSGCoefficients() {
        double[] coeffs = SGFilter.computeSGCoefficients(3, 3, 2);
        double[] tabularCoeffs = new double[]{-0.095, 0.143, 0.286, 0.333,
            0.286, 0.143, -0.095};
        assertEquals(7, coeffs.length);
        assertCoeffsEqual(coeffs, tabularCoeffs);
        coeffs = SGFilter.computeSGCoefficients(5, 5, 4);
        tabularCoeffs = new double[]{0.042, -0.105, -0.023, 0.14, 0.28,
                    0.333, 0.28, 0.14, -0.023, -0.105, 0.042};
        assertEquals(11, coeffs.length);
        assertCoeffsEqual(coeffs, tabularCoeffs);
    }

    @Test
    public final void testComputeSGCoefficientsDerivative() {
        double[] coeffs = SGFilter.computeSGCoefficients(4, 4, 4, 1);
        double[] tabularCoeffs = new double[]{
            86 / 1188d,
            -142 / 1188d,
            -193 / 1188d,
            -126 / 1188d,
            0d,
            126 / 1188d,
            193 / 1188d,
            142 / 1188d,
            -86 / 1188d
        };
        assertEquals(9, coeffs.length);
        assertCoeffsEqual("ld=1, n=9, m=4 i=", coeffs, tabularCoeffs);
        coeffs = SGFilter.computeSGCoefficients(5, 5, 4, 1);
        tabularCoeffs = new double[]{
                    300 / 5148d,
                    -294 / 5148d,
                    -532 / 5148d,
                    -503 / 5148d,
                    -296 / 5148d,
                    0d,
                    296 / 5148d,
                    503 / 5148d,
                    532 / 5148d,
                    294 / 5148d,
                    -300 / 5148d
                };

        assertEquals(11, coeffs.length);
        assertCoeffsEqual("ld=1, n=11, m=4 i=", coeffs, tabularCoeffs);
    }

    @Test
    public final void testSmooth1() {
        float[] data = new float[]{8916.81f, 8934.24f, 9027.06f, 9160.79f,
            7509.14f};
        float[] leftPad = new float[]{8915.06f, 8845.53f, 9064.17f, 8942.09f,
            8780.87f};
        double[] realResult1 = new double[]{8989.485464, 9070.934158,
            8957.906284, 8577.50381, 8055.909912};
        double[] realResult2 = new double[]{9039.854903, 8995.380001,
            8854.369105, 8641.864759, 8456.067118};

        double[] coeffs = SGFilter.computeSGCoefficients(5, 5, 4);
        ContinuousPadder padder1 = new ContinuousPadder(false, true);
        SGFilter sgFilter = new SGFilter(5, 5);
        sgFilter.appendPreprocessor(padder1);
        float[] smooth1 = sgFilter.smooth(data, leftPad, new float[0], coeffs);
        assertResultsEqual(smooth1, realResult1);
        MeanValuePadder padder2 = new MeanValuePadder(10, false, true);
        sgFilter.removePreprocessor(padder1);
        sgFilter.appendPreprocessor(padder2);
        float[] smooth2 = sgFilter.smooth(data, leftPad, new float[0], coeffs);
        assertResultsEqual(smooth2, realResult2);
    }

    @Test
    public final void testSmooth2() {
        float[] data = new float[]{6945.43f, 0f, 0f, 7221.76f, 4092.77f,
            6607.28f, 6867.01f};
        double[] realResult = new double[]{7204.79, 7098.04, 6937.25,
            6806.47, 6803.55};
        double[] coeffs = SGFilter.computeSGCoefficients(5, 5, 4);
        ZeroEliminator preprocessor1 = new ZeroEliminator(false);
        ContinuousPadder preprocessor2 = new ContinuousPadder(true, false);
        MeanValuePadder preprocessor4 = new MeanValuePadder(10, false, true);
        Linearizer preprocessor3 = new Linearizer(0.08f);
        SGFilter sgFilter = new SGFilter(5, 5);
        sgFilter.appendPreprocessor(preprocessor1);
        sgFilter.appendPreprocessor(preprocessor2);
        sgFilter.appendPreprocessor(preprocessor3);
        sgFilter.appendPreprocessor(preprocessor4);
        float[] smooth = sgFilter.smooth(data, 2, data.length, coeffs);
        assertResultsEqual(smooth, realResult);
    }

    @Test
    public final void testSmooth3() {
        double[] coeffs5_5 = SGFilter.computeSGCoefficients(5, 5, 4);
        double[] coeffs5_4 = SGFilter.computeSGCoefficients(5, 4, 4);
        double[] coeffs4_5 = SGFilter.computeSGCoefficients(4, 5, 4);
        float[] data = new float[]{12680.43f, 18316.83f, 18316.83f,
            18316.83f, 18316.83f, 18120.89f, 18120.89f, 18897.22f,
            18897.22f, 18470.61f, 18470.61f, 18470.61f, 18470.61f};
        double[] real = new double[]{18129.17, 18018.18, 18426.96, 18598.67,
            18727.08};
        SGFilter sgFilter = new SGFilter(5, 5);
        float[] smooth = sgFilter.smooth(data, 4, 9, 1, new double[][]{
                    coeffs5_5, coeffs5_4, coeffs4_5});
        assertResultsEqual(smooth, real);
    }
}

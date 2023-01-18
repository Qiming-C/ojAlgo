/*
 * Copyright 1997-2022 Optimatika
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.function.special;

import static org.ojalgo.function.constant.PrimitiveMath.ONE;

import com.google.errorprone.annotations.Var;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Math utilities missing from {@link Math}.
 *
 * @author apete
 */
public abstract class MissingMath {

    /**
     * Corresponding to binary 256 octuple precision
     */
    private static final MathContext MC256 = new MathContext(71, RoundingMode.HALF_EVEN);

    public static double acosh( double arg) {
        return Math.log(arg + Math.sqrt(arg * arg - 1.0));
    }

    public static double asinh( double arg) {
        return Math.log(arg + Math.sqrt(arg * arg + 1.0));
    }

    /**
     * <p>
     * https://math.stackexchange.com/questions/1098487/atan2-faster-approximation/1105038
     * <p>
     * </p>
     * This is about 10x faster than {@link Math#atan2(double, double)}
     * </p>
     */
    public static double atan2( double y,  double x) {

        if (y == 0.0 && x == 0.0) {
            return 0.0;
        }

        double ay = Math.abs(y);
        double ax = Math.abs(x);
        double a = Math.min(ay, ax) / Math.max(ay, ax);
        double s = a * a;

        @Var double retVal = ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a;

        if (ay > ax) {
            retVal = 1.570796326794897 - retVal;
        }
        if (x < 0.0) {
            retVal = 3.141592653589793 - retVal;
        }
        if (y < 0.0) {
            retVal = -retVal;
        }

        return retVal;
    }

    public static double atanh( double arg) {
        return Math.log((1.0 + arg) / (1.0 - arg)) / 2.0;
    }

    public static BigDecimal divide( BigDecimal numerator,  BigDecimal denominator) {
        return numerator.divide(denominator, MC256);
    }

    /**
     * <code>13!</code> does not fit in an <code>int</code>, and <code>21!</code> does not fit in a
     * <code>long</code> - that's why this method returns a <code>double</code>.
     */
    public static double factorial( int arg) {
        if (arg < 0) {
            throw new IllegalArgumentException();
        }
        if (arg < 2) {
            return ONE;
        }
        if (arg < 13) {
            return MissingMath.factorialInt(arg);
        }
        if (arg < 21) {
            return MissingMath.factorialLong(arg);
        }
        return MissingMath.factorialDouble(arg);
    }

    /**
     * Greatest Common Denominator
     */
    public static int gcd( int val1,  int val2) {

        @Var int retVal = 1;

        int abs1 = Math.abs(val1);
        int abs2 = Math.abs(val2);

        @Var int tmpMax = Math.max(abs1, abs2);
        @Var int tmpMin = Math.min(abs1, abs2);

        while (tmpMin != 0) {
            retVal = tmpMin;
            tmpMin = tmpMax % tmpMin;
            tmpMax = retVal;
        }

        return retVal;
    }

    public static int gcd( int val1,  int... vals) {

        @Var int retVal = val1;

        if (retVal == 1) {
            return 1;
        }

        for (int i = 0; i < vals.length; i++) {
            retVal = MissingMath.gcd(retVal, vals[i]);
            if (retVal == 1) {
                return 1;
            }
        }

        return retVal;
    }

    public static int gcd( int[] vals) {
        return MissingMath.gcd(vals[0], vals);
    }

    public static long gcd( long val1,  long... vals) {

        @Var long retVal = val1;

        if (retVal == 1L) {
            return 1L;
        }

        for (int i = 0; i < vals.length; i++) {
            retVal = MissingMath.gcd(retVal, vals[i]);
            if (retVal == 1L) {
                return 1L;
            }
        }

        return retVal;
    }

    /**
     * Greatest Common Denominator
     */
    public static long gcd( long val1,  long val2) {

        @Var long retVal = 1L;

        long abs1 = Math.abs(val1);
        long abs2 = Math.abs(val2);

        @Var long tmpMax = Math.max(abs1, abs2);
        @Var long tmpMin = Math.min(abs1, abs2);

        while (tmpMin != 0L) {
            retVal = tmpMin;
            tmpMin = tmpMax % tmpMin;
            tmpMax = retVal;
        }

        return retVal;
    }

    public static long gcd( long[] vals) {
        return MissingMath.gcd(vals[0], vals);
    }

    public static BigDecimal hypot( BigDecimal arg1,  BigDecimal arg2) {
        BigDecimal prod1 = arg1.multiply(arg1);
        BigDecimal prod2 = arg2.multiply(arg2);
        return MissingMath.root(prod1.add(prod2), 2);
    }

    public static double hypot( double arg1,  double arg2) {

        if (Double.isNaN(arg1) || Double.isNaN(arg2)) {
            return Double.NaN;
        }

        double abs1 = Math.abs(arg1);
        double abs2 = Math.abs(arg2);

        @Var double retVal = 0.0;

        if (abs1 > abs2) {
            retVal = abs1 * MissingMath.sqrt1px2(abs2 / abs1);
        } else if (abs2 > 0.0) {
            retVal = abs2 * MissingMath.sqrt1px2(abs1 / abs2);
        }

        return retVal;
    }

    /**
     * For very small arguments (regardless of sign) the replacement is returned instead
     */
    public static double log10( double arg,  double replacement) {
        if (Math.abs(arg) < Double.MIN_NORMAL) {
            return replacement;
        }
        return Math.log10(arg);
    }

    public static double logistic( double arg) {
        return 1.0 / (1.0 + Math.exp(-arg));
    }

    public static double logit( double arg) {
        return Math.log(1.0 / (1.0 - arg));
    }

    public static double max( double... values) {
        @Var double retVal = values[0];
        for (int i = values.length; i-- != 1;) {
            retVal = values[i] > retVal ? values[i] : retVal;
        }
        return retVal;
    }

    public static double max( double a,  double b) {
        return Math.max(a, b);
    }

    public static double max( double a,  double b,  double c) {
        return Math.max(Math.max(a, b), c);
    }

    public static double max( double a,  double b,  double c,  double d) {
        return Math.max(Math.max(a, b), Math.max(c, d));
    }

    public static int max( int... values) {
        @Var int retVal = values[0];
        for (int i = values.length; i-- != 1;) {
            retVal = values[i] > retVal ? values[i] : retVal;
        }
        return retVal;
    }

    public static int max( int a,  int b) {
        return Math.max(a, b);
    }

    public static int max( int a,  int b,  int c) {
        return Math.max(Math.max(a, b), c);
    }

    public static int max( int a,  int b,  int c,  int d) {
        return Math.max(Math.max(a, b), Math.max(c, d));
    }

    public static long max( long... values) {
        @Var long retVal = values[0];
        for (int i = values.length; i-- != 1;) {
            retVal = values[i] > retVal ? values[i] : retVal;
        }
        return retVal;
    }

    public static long max( long a,  long b) {
        return Math.max(a, b);
    }

    public static long max( long a,  long b,  long c) {
        return Math.max(Math.max(a, b), c);
    }

    public static long max( long a,  long b,  long c,  long d) {
        return Math.max(Math.max(a, b), Math.max(c, d));
    }

    public static double min( double... values) {
        @Var double retVal = values[0];
        for (int i = values.length; i-- != 1;) {
            retVal = values[i] < retVal ? values[i] : retVal;
        }
        return retVal;
    }

    public static double min( double a,  double b) {
        return Math.min(a, b);
    }

    public static double min( double a,  double b,  double c) {
        return Math.min(Math.min(a, b), c);
    }

    public static double min( double a,  double b,  double c,  double d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    public static int min( int... values) {
        @Var int retVal = values[0];
        for (int i = values.length; i-- != 1;) {
            retVal = values[i] < retVal ? values[i] : retVal;
        }
        return retVal;
    }

    public static int min( int a,  int b) {
        return Math.min(a, b);
    }

    public static int min( int a,  int b,  int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int min( int a,  int b,  int c,  int d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    public static long min( long... values) {
        @Var long retVal = values[0];
        for (int i = values.length; i-- != 1;) {
            retVal = values[i] < retVal ? values[i] : retVal;
        }
        return retVal;
    }

    public static long min( long a,  long b) {
        return Math.min(a, b);
    }

    public static long min( long a,  long b,  long c) {
        return Math.min(Math.min(a, b), c);
    }

    public static long min( long a,  long b,  long c,  long d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    public static double norm( double... values) {
        @Var double retVal = Math.abs(values[0]);
        for (int i = values.length; i-- != 1;) {
            retVal = values[i] > retVal ? Math.abs(values[i]) : retVal;
        }
        return retVal;
    }

    public static double norm( double a,  double b) {
        return Math.max(Math.abs(a), Math.abs(b));
    }

    public static double norm( double a,  double b,  double c) {
        return Math.max(Math.max(Math.abs(a), Math.abs(b)), Math.abs(c));
    }

    public static double norm( double a,  double b,  double c,  double d) {
        return Math.max(Math.max(Math.abs(a), Math.abs(b)), Math.max(Math.abs(c), Math.abs(d)));
    }

    public static BigDecimal pow( BigDecimal arg1,  BigDecimal arg2) {
        if (arg2.signum() == 0) {
            return BigDecimal.ONE;
        }
        if (arg1.signum() == 0) {
            return BigDecimal.ZERO;
        }
        if (arg2.compareTo(BigDecimal.ONE) == 0) {
            return arg1;
        }
        return BigDecimal.valueOf(Math.pow(arg1.doubleValue(), arg2.doubleValue()));
    }

    public static BigDecimal power( BigDecimal arg,  int param) {
        switch (param) {
        case 0:
            return BigDecimal.ONE;
        case 1:
            return arg;
        case 2:
            return arg.multiply(arg, MC256);
        case 3:
            return arg.multiply(arg).multiply(arg, MC256);
        case 4:
            BigDecimal arg2 = arg.multiply(arg);
            return arg2.multiply(arg2, MC256);
        default:
            return arg.pow(param, MC256);
        }
    }

    public static double power( double arg, @Var int param) {

        if (param < 0) {

            return 1.0 / MissingMath.power(arg, -param);

        }
        @Var double retVal = 1.0;

        while (param > 0) {
            retVal = retVal * arg;
            param--;
        }

        return retVal;
    }

    public static long power( long arg,  int param) {

        if (param == 0) {
            return 1L;
        }
        if (param == 1) {
            return arg;
        }
        if (param == 2) {
            return arg * arg;
        }
        if (param < 0) {
            return Math.round(Math.pow((double) arg, param));
        }
        @Var long retVal = arg;
        for (int p = 1; p < param; p++) {
            retVal *= arg;
        }
        return retVal;
    }

    public static BigDecimal root( BigDecimal arg,  int param) {

        if (param <= 0) {
            throw new IllegalArgumentException();
        }

        if (param == 1) {
            return arg;
        }

        BigDecimal bigArg = arg.round(MC256);
        BigDecimal bigParam = BigDecimal.valueOf(param);

        @Var BigDecimal retVal = BigDecimal.ZERO;
        double primArg = bigArg.doubleValue();
        if (!Double.isInfinite(primArg) && !Double.isNaN(primArg)) {
            retVal = BigDecimal.valueOf(Math.pow(primArg, 1.0 / param)); // Intial guess
        }

        @Var BigDecimal shouldBeZero;
        while ((shouldBeZero = MissingMath.power(retVal, param).subtract(bigArg)).signum() != 0) {
            retVal = retVal.subtract(shouldBeZero.divide(bigParam.multiply(retVal.pow(param - 1)), MC256));
        }

        return retVal;
    }

    public static double root( double arg,  int param) {
        if (param != 0) {
            return Math.pow(arg, 1.0 / param);
        }
        throw new IllegalArgumentException();
    }

    public static int roundToInt( double value) {
        return Math.toIntExact(Math.round(value));
    }

    public static double scale( double arg, @Var int param) {

        if (param == 0) {

            return 1.0;

        }
        if (param < 0) {

            @Var int factor = 1;
            while (param < 0) {
                factor *= 10;
                param++;
            }

            return Math.rint(factor / arg) * factor;

        }
        @Var int factor = 1;
        while (param > 0) {
            factor *= 10;
            param--;
        }

        return Math.rint(factor * arg) / factor;
    }

    public static BigDecimal signum( BigDecimal arg) {
        switch (arg.signum()) {
        case 1:
            return BigDecimal.ONE;
        case -1:
            return BigDecimal.ONE.negate();
        default:
            return BigDecimal.ZERO;
        }
    }

    public static double sqrt1px2( double arg) {
        return Math.sqrt(1.0 + arg * arg);
    }

    public static int toMinIntExact( long... values) {
        return Math.toIntExact(MissingMath.min(values));
    }

    public static int toMinIntExact( long a,  long b) {
        return Math.toIntExact(Math.min(a, b));
    }

    public static int toMinIntExact( long a,  long b,  long c) {
        return Math.toIntExact(MissingMath.min(a, b, c));
    }

    public static int toMinIntExact( long a,  long b,  long c,  long d) {
        return Math.toIntExact(MissingMath.min(a, b, c, d));
    }

    static double factorialDouble( int arg) {
        @Var double retVal = ONE;
        for (int i = 2; i <= arg; i++) {
            retVal *= i;
        }
        return retVal;
    }

    static int factorialInt( int arg) {
        @Var int retVal = 1;
        for (int i = 2; i <= arg; i++) {
            retVal *= i;
        }
        return retVal;
    }

    static long factorialLong( int arg) {
        @Var long retVal = 1L;
        for (int i = 2; i <= arg; i++) {
            retVal *= i;
        }
        return retVal;
    }

}

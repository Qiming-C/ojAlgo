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
package org.ojalgo.random;

import static org.ojalgo.function.constant.PrimitiveMath.*;

import com.google.errorprone.annotations.Var;
import java.util.Arrays;
import org.ojalgo.ProgrammingError;
import org.ojalgo.array.ArrayR064;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.structure.Access1D;
import org.ojalgo.type.context.NumberContext;

public final class SampleSet implements Access1D<Double> {

    /**
     *Returns the sample set's variance.
 @param sumOfValues The sum of all values in a sample set
     * @param sumOfSquaredValues The sum of all squared values, in a sample set
     * @param numberOfValues The number of values in the sample set
     * 
     */
    public static double calculateVariance( double sumOfValues,  double sumOfSquaredValues,  int numberOfValues) {
        return (numberOfValues * sumOfSquaredValues - sumOfValues * sumOfValues) / (numberOfValues * (numberOfValues - 1));
    }

    /**
     * Create a sample set from counting occurrences of difference values in the iterable.
     */
    public static <T> SampleSet from( Iterable<T> keys) {

        FrequencyMap<T> frequencies = new FrequencyMap<>();

        for (T key : keys) {
            frequencies.increment(key);
        }

        return frequencies.sample();
    }

    public static SampleSet make() {
        return new SampleSet(ArrayR064.make(4));
    }

    public static SampleSet make( RandomNumber randomNumber,  int size) {

         ArrayR064 retVal = ArrayR064.make(size);
         double[] tmpData = retVal.data;

        for (int i = 0; i < size; i++) {
            tmpData[i] = randomNumber.doubleValue();
        }

        return new SampleSet(retVal);
    }

    public static SampleSet wrap( Access1D<?> someSamples) {
        return new SampleSet(someSamples);
    }

    public static SampleSet wrap( double[] someSamples) {
        return SampleSet.wrap(Access1D.wrap(someSamples));
    }

    private transient double myMax = NaN;
    private transient double myMean = NaN;
    private transient double myMin = NaN;
    private transient double myQuartile1 = NaN;
    private transient double myQuartile2 = NaN;
    private transient double myQuartile3 = NaN;
    private Access1D<?> mySamples;
    private transient double[] mySortedCopy = null;
    private transient double myStandardDeviation = NaN;
    private transient double myVariance = NaN;

    @SuppressWarnings("unused")
    private SampleSet() {

        this(null);

        ProgrammingError.throwForIllegalInvocation();
    }

    SampleSet( Access1D<?> samples) {

        super();

        mySamples = samples;

        this.reset();
    }

    @Override public long count() {
        return mySamples.count();
    }

    @Override public double doubleValue( long index) {
        return mySamples.doubleValue(index);
    }

    @Override public Double get( long index) {
        return mySamples.doubleValue(index);
    }

    public double getCorrelation( SampleSet anotherSampleSet) {

        @Var double retVal = ZERO;

         double tmpCovar = this.getCovariance(anotherSampleSet);

        // if (tmpCovar != ZERO) {
        if (NumberContext.compare(tmpCovar, ZERO) != 0) {

             double tmpThisStdDev = this.getStandardDeviation();
             double tmpThatStdDev = anotherSampleSet.getStandardDeviation();

            retVal = tmpCovar / (tmpThisStdDev * tmpThatStdDev);
        }

        return retVal;
    }

    public double getCovariance( SampleSet anotherSampleSet) {

        @Var double retVal = ZERO;

         double thisMean = this.getMean();
         double thatMean = anotherSampleSet.getMean();

         long limit = Math.min(mySamples.count(), anotherSampleSet.count());

         Access1D<?> otherValues = anotherSampleSet.getSamples();
        for (long i = 0L; i < limit; i++) {
            retVal += (mySamples.doubleValue(i) - thisMean) * (otherValues.doubleValue(i) - thatMean);
        }

        retVal /= limit - 1L;
        return retVal;
    }

    public double getFirst() {
        if (mySamples.count() > 0L) {
            return mySamples.doubleValue(0);
        }
        return ZERO;
    }

    public double getInterquartileRange() {
        return this.getQuartile3() - this.getQuartile1();
    }

    /**
     * max(abs(value))
     */
    public double getLargest() {

        @Var double retVal = ZERO;

         long tmpLimit = mySamples.count();
        for (long i = 0L; i < tmpLimit; i++) {
            retVal = PrimitiveMath.MAX.invoke(retVal, PrimitiveMath.ABS.invoke(mySamples.doubleValue(i)));
        }

        return retVal;
    }

    public double getLast() {
        if (mySamples.count() > 0L) {
            return mySamples.doubleValue(mySamples.count() - 1L);
        }
        return ZERO;
    }

    /**
     * max(value)
     */
    public double getMaximum() {

        if (Double.isNaN(myMax)) {

            myMax = NEGATIVE_INFINITY;

             long tmpLimit = mySamples.count();
            for (long i = 0L; i < tmpLimit; i++) {
                myMax = PrimitiveMath.MAX.invoke(myMax, mySamples.doubleValue(i));
            }
        }

        return myMax;
    }

    public double getMean() {

        if (Double.isNaN(myMean)) {

            myMean = ZERO;

             long tmpLimit = mySamples.count();
            for (long i = 0L; i < tmpLimit; i++) {
                myMean += mySamples.doubleValue(i);
            }

            myMean /= mySamples.count();
        }

        return myMean;
    }

    /**
     * Potentially expensive as it requires copying and sorting of the samples.
     */
    public double getMedian() {
        return this.getQuartile2();
    }

    /**
     * min(value)
     */
    public double getMinimum() {

        if (Double.isNaN(myMin)) {

            myMin = POSITIVE_INFINITY;

             long tmpLimit = mySamples.count();
            for (long i = 0L; i < tmpLimit; i++) {
                myMin = PrimitiveMath.MIN.invoke(myMin, mySamples.doubleValue(i));
            }
        }

        return myMin;
    }

    /**
     * https://en.wikipedia.org/wiki/Quartile
     * <p>
     * Potentially expensive as it requires copying and sorting of the samples.
     */
    public double getQuartile1() {

        if (Double.isNaN(myQuartile1)) {
            this.calculateQuartiles();
        }

        return myQuartile1;
    }

    /**
     * https://en.wikipedia.org/wiki/Quartile
     * <p>
     * Potentially expensive as it requires copying and sorting of the samples.
     */
    public double getQuartile2() {

        if (Double.isNaN(myQuartile2)) {
            this.calculateQuartiles();
        }

        return myQuartile2;
    }

    /**
     * https://en.wikipedia.org/wiki/Quartile
     * <p>
     * Potentially expensive as it requires copying and sorting of the samples.
     */
    public double getQuartile3() {

        if (Double.isNaN(myQuartile3)) {
            this.calculateQuartiles();
        }

        return myQuartile3;
    }

    /**
     * min(abs(value))
     */
    public double getSmallest() {

        @Var double retVal = POSITIVE_INFINITY;

         long tmpLimit = mySamples.count();
        for (long i = 0L; i < tmpLimit; i++) {
            retVal = PrimitiveMath.MIN.invoke(retVal, PrimitiveMath.ABS.invoke(mySamples.doubleValue(i)));
        }

        return retVal;
    }

    public double getStandardDeviation() {

        if (Double.isNaN(myStandardDeviation)) {
            myStandardDeviation = PrimitiveMath.SQRT.invoke(this.getVariance());
        }

        return myStandardDeviation;
    }

    /**
     * The standard score is the (signed) number of standard deviations an observation or datum is above the
     * mean. Thus, a positive standard score indicates a datum above the mean, while a negative standard score
     * indicates a datum below the mean. It is a dimensionless quantity obtained by subtracting the population
     * mean from an individual raw score and then dividing the difference by the population standard
     * deviation.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Standard_score">WikipediA</a>
     */
    public double getStandardScore( long index) {
        return (this.doubleValue(index) - this.getMean()) / this.getStandardDeviation();
    }

    /**
     * Sum of squares is a concept that permeates much of inferential statistics and descriptive statistics.
     * More properly, it is "the sum of the squared deviations". Mathematically, it is an unscaled, or
     * unadjusted measure of dispersion (also called variability). When scaled for the number of degrees of
     * freedom, it estimates the variance, or spread of the observations about their mean value.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Sum_of_squares">WikipediA</a>
     */
    public double getSumOfSquares() {

        @Var double retVal = ZERO;

         double mean = this.getMean();
        @Var double deviation;
        for (long i = 0L, limit = mySamples.count(); i < limit; i++) {
            deviation = mySamples.doubleValue(i) - mean;
            retVal += deviation * deviation;
        }

        return retVal;
    }

    /**
     *Returns a copy of the internal data (the samples).
 
     */
    public double[] getValues() {
        return mySamples.toRawCopy1D();
    }

    public double getVariance() {

        if (Double.isNaN(myVariance)) {
            myVariance = this.getCovariance(this);
        }

        return myVariance;
    }

    /**
     * If the underlying {@link Access1D} of samples is modified you must reset the sample set before using.
     */
    public void reset() {

        myMin = NaN;
        myMax = NaN;

        myMean = NaN;
        myVariance = NaN;
        myStandardDeviation = NaN;

        myQuartile1 = NaN;
        myQuartile2 = NaN;
        myQuartile3 = NaN;

        if (mySortedCopy != null) {
            Arrays.fill(mySortedCopy, Double.POSITIVE_INFINITY);
        }
    }

    @Override public int size() {
        return (int) mySamples.count();
    }

    /**
     * Replace the underlying samples and reset the sample set.
     */
    public SampleSet swap( Access1D<?> samples) {
        ProgrammingError.throwIfNull(samples);
        mySamples = samples;
        this.reset();
        return this;
    }

    public SampleSet swap( double[] samples) {
        return this.swap(Access1D.wrap(samples));
    }

    @Override
    public String toString() {
        return "Sample set Size=" + this.count() + ", Mean=" + this.getMean() + ", Var=" + this.getVariance() + ", StdDev=" + this.getStandardDeviation()
                + ", Min=" + this.getMinimum() + ", Max=" + this.getMaximum();
    }

    private void calculateQuartiles() {

         var tmpSize = (int) this.getSamples().count();
         double[] tmpSortedCopy = this.getSortedCopy();

        switch (tmpSize) {

        case 0:

            myMin = ZERO;
            myMax = ZERO;

            myQuartile1 = ZERO;
            myQuartile2 = ZERO;
            myQuartile3 = ZERO;

            break;

        case 1:

            myMin = tmpSortedCopy[0];
            myMax = tmpSortedCopy[0];

            myQuartile1 = tmpSortedCopy[0];
            myQuartile2 = tmpSortedCopy[0];
            myQuartile3 = tmpSortedCopy[0];

            break;

        default:

            myMin = tmpSortedCopy[0];
            myMax = tmpSortedCopy[tmpSize - 1];

             int n = tmpSize / 4;
             int r = tmpSize % 4;

            switch (r) {

            case 1:

                myQuartile1 = 0.25 * tmpSortedCopy[n - 1] + 0.75 * tmpSortedCopy[n];
                myQuartile2 = tmpSortedCopy[2 * n];
                myQuartile3 = 0.75 * tmpSortedCopy[3 * n] + 0.25 * tmpSortedCopy[3 * n + 1];

                break;

            case 2:

                myQuartile1 = tmpSortedCopy[n];
                myQuartile2 = 0.5 * tmpSortedCopy[2 * n] + 0.5 * tmpSortedCopy[2 * n + 1];
                myQuartile3 = tmpSortedCopy[3 * n + 1];

                break;

            case 3:

                myQuartile1 = 0.75 * tmpSortedCopy[n] + 0.25 * tmpSortedCopy[n + 1];
                myQuartile2 = tmpSortedCopy[2 * n + 1];
                myQuartile3 = 0.25 * tmpSortedCopy[3 * n + 1] + 0.75 * tmpSortedCopy[3 * n + 2];

                break;

            default:

                myQuartile1 = 0.5 * tmpSortedCopy[n - 1] + 0.5 * tmpSortedCopy[n];
                myQuartile2 = 0.5 * tmpSortedCopy[2 * n - 1] + 0.5 * tmpSortedCopy[2 * n];
                myQuartile3 = 0.5 * tmpSortedCopy[3 * n - 1] + 0.5 * tmpSortedCopy[3 * n];

                break;
            }

            break;
        }
    }

    Access1D<?> getSamples() {
        return mySamples;
    }

    double[] getSortedCopy() {

         Access1D<?> tmpSamples = this.getSamples();
         var tmpSamplesCount = (int) tmpSamples.count();

        if (mySortedCopy == null || mySortedCopy.length < tmpSamplesCount || mySortedCopy.length == 0) {
            mySortedCopy = tmpSamples.toRawCopy1D();
            Arrays.parallelSort(mySortedCopy);
        } else if (mySortedCopy[0] == Double.POSITIVE_INFINITY) {
            for (int i = 0; i < tmpSamplesCount; i++) {
                mySortedCopy[i] = tmpSamples.doubleValue(i);
            }
            Arrays.parallelSort(mySortedCopy, 0, tmpSamplesCount);
        }

        return mySortedCopy;
    }

}

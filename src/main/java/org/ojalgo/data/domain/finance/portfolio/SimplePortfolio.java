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
package org.ojalgo.data.domain.finance.portfolio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.ojalgo.data.domain.finance.portfolio.FinancePortfolio.Context;
import org.ojalgo.data.domain.finance.portfolio.simulator.PortfolioSimulator;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.MatrixR064;
import org.ojalgo.random.process.GeometricBrownianMotion;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access2D;

public final class SimplePortfolio extends FinancePortfolio implements Context {

    static List<SimpleAsset> toSimpleAssets( double[] someWeights) {

         ArrayList<SimpleAsset> retVal = new ArrayList<>(someWeights.length);

        for (int i = 0; i < someWeights.length; i++) {
            retVal.add(new SimpleAsset(someWeights[i]));
        }

        return retVal;
    }

    static List<SimpleAsset> toSimpleAssets( Comparable<?>[] someWeights) {

         ArrayList<SimpleAsset> retVal = new ArrayList<>(someWeights.length);

        for (int i = 0; i < someWeights.length; i++) {
            retVal.add(new SimpleAsset(someWeights[i]));
        }

        return retVal;
    }

    private transient MatrixR064 myAssetReturns = null;
    private transient MatrixR064 myAssetVolatilities = null;
    private transient MatrixR064 myAssetWeights = null;
    private final List<SimpleAsset> myComponents;
    private final MatrixR064 myCorrelations;
    private transient MatrixR064 myCovariances = null;
    private transient Comparable<?> myMeanReturn;
    private transient Comparable<?> myReturnVariance;

    private transient List<BigDecimal> myWeights;

    public SimplePortfolio( Access2D<?> correlationsMatrix,  List<SimpleAsset> someAssets) {

        super();

        if (someAssets.size() != correlationsMatrix.countRows() || someAssets.size() != correlationsMatrix.countColumns()) {
            throw new IllegalArgumentException("Input dimensions don't match!");
        }

        myCorrelations = MATRIX_FACTORY.copy(correlationsMatrix);
        myComponents = someAssets;
    }

    public SimplePortfolio( Context portfolioContext,  FinancePortfolio weightsPortfolio) {

        super();

        myCorrelations = portfolioContext.getCorrelations();

         MatrixR064 tmpCovariances = portfolioContext.getCovariances();
         MatrixR064 tmpAssetReturns = portfolioContext.getAssetReturns();

         List<BigDecimal> tmpWeights = weightsPortfolio.getWeights();

        if (tmpWeights.size() != myCorrelations.countRows() || tmpWeights.size() != myCorrelations.countColumns()) {
            throw new IllegalArgumentException("Input dimensions don't match!");
        }

        myComponents = new ArrayList<>(tmpWeights.size());
        for (int i = 0; i < tmpWeights.size(); i++) {
             double tmpMeanReturn = tmpAssetReturns.doubleValue(i, 0);
             double tmpVolatilty = PrimitiveMath.SQRT.invoke(tmpCovariances.doubleValue(i, i));
             BigDecimal tmpWeight = tmpWeights.get(i);
            myComponents.add(new SimpleAsset(tmpMeanReturn, tmpVolatilty, tmpWeight));
        }
    }

    public SimplePortfolio( double[] someWeights) {
        this(SimplePortfolio.toSimpleAssets(someWeights));
    }

    public SimplePortfolio( List<SimpleAsset> someAssets) {
        this(MATRIX_FACTORY.makeEye(someAssets.size(), someAssets.size()), someAssets);
    }

    public SimplePortfolio( Comparable<?>... someWeights) {
        this(SimplePortfolio.toSimpleAssets(someWeights));
    }

    @Override public double calculatePortfolioReturn( FinancePortfolio weightsPortfolio) {
         List<BigDecimal> tmpWeights = weightsPortfolio.getWeights();
         MatrixR064 tmpAssetWeights = MATRIX_FACTORY.columns(tmpWeights);
         MatrixR064 tmpAssetReturns = this.getAssetReturns();
        return MarketEquilibrium.calculatePortfolioReturn(tmpAssetWeights, tmpAssetReturns).doubleValue();
    }

    @Override public double calculatePortfolioVariance( FinancePortfolio weightsPortfolio) {
         List<BigDecimal> tmpWeights = weightsPortfolio.getWeights();
         MatrixR064 tmpAssetWeights = MATRIX_FACTORY.columns(tmpWeights);
        return new MarketEquilibrium(this.getCovariances()).calculatePortfolioVariance(tmpAssetWeights).doubleValue();
    }

    @Override public MatrixR064 getAssetReturns() {

        if (myAssetReturns == null) {

             int tmpSize = myComponents.size();

             MatrixR064.DenseReceiver tmpReturns = MATRIX_FACTORY.makeDense(tmpSize, 1);

            for (int i = 0; i < tmpSize; i++) {
                tmpReturns.set(i, 0, this.getMeanReturn(i));
            }

            myAssetReturns = tmpReturns.get();
        }

        return myAssetReturns;
    }

    @Override public MatrixR064 getAssetVolatilities() {

        if (myAssetVolatilities == null) {

             int tmpSize = myComponents.size();

             MatrixR064.DenseReceiver tmpVolatilities = MATRIX_FACTORY.makeDense(tmpSize, 1);

            for (int i = 0; i < tmpSize; i++) {
                tmpVolatilities.set(i, 0, this.getVolatility(i));
            }

            myAssetVolatilities = tmpVolatilities.get();
        }

        return myAssetVolatilities;
    }

    public double getCorrelation( int row,  int col) {
        return myCorrelations.doubleValue(row, col);
    }

    @Override public MatrixR064 getCorrelations() {
        return myCorrelations;
    }

    public double getCovariance( int row,  int col) {

         MatrixR064 tmpCovariances = myCovariances;

        if (tmpCovariances != null) {
            return tmpCovariances.doubleValue(row, col);
        }

         double tmpRowRisk = this.getVolatility(row);
         double tmpColRisk = this.getVolatility(col);

         double tmpCorrelation = this.getCorrelation(row, col);

        return tmpRowRisk * tmpCorrelation * tmpColRisk;
    }

    @Override public MatrixR064 getCovariances() {

        if (myCovariances == null) {

             int tmpSize = myComponents.size();

             MatrixR064.DenseReceiver tmpCovaris = MATRIX_FACTORY.makeDense(tmpSize, tmpSize);

            for (int j = 0; j < tmpSize; j++) {
                for (int i = 0; i < tmpSize; i++) {
                    tmpCovaris.set(i, j, this.getCovariance(i, j));
                }
            }

            myCovariances = tmpCovaris.get();
        }

        return myCovariances;
    }

    @Override
    public double getMeanReturn() {

        if (myMeanReturn == null) {
             MatrixR064 tmpWeightsVector = this.getAssetWeights();
             MatrixR064 tmpReturnsVector = this.getAssetReturns();
            myMeanReturn = MarketEquilibrium.calculatePortfolioReturn(tmpWeightsVector, tmpReturnsVector).get();
        }

        return Scalar.doubleValue(myMeanReturn);
    }

    public double getMeanReturn( int index) {
        return myComponents.get(index).getMeanReturn();
    }

    @Override
    public double getReturnVariance() {

        if (myReturnVariance == null) {
             var tmpMarketEquilibrium = new MarketEquilibrium(this.getCovariances());
             MatrixR064 tmpWeightsVector = this.getAssetWeights();
            myReturnVariance = tmpMarketEquilibrium.calculatePortfolioVariance(tmpWeightsVector).get();
        }

        return Scalar.doubleValue(myReturnVariance);
    }

    public double getReturnVariance( int index) {
        return myComponents.get(index).getReturnVariance();
    }

    public PortfolioSimulator getSimulator() {

         List<GeometricBrownianMotion> tmpAssetProcesses = new ArrayList<>(myComponents.size());

        for ( SimpleAsset tmpAsset : myComponents) {
             GeometricBrownianMotion tmpForecast = tmpAsset.forecast();
            tmpForecast.setValue(tmpAsset.getWeight().doubleValue());
            tmpAssetProcesses.add(tmpForecast);
        }

        return new PortfolioSimulator(myCorrelations, tmpAssetProcesses);
    }

    public double getVolatility( int index) {
        return myComponents.get(index).getVolatility();
    }

    public BigDecimal getWeight( int index) {
        return myComponents.get(index).getWeight();
    }

    @Override
    public List<BigDecimal> getWeights() {

        if (myWeights == null) {

            myWeights = new ArrayList<>(myComponents.size());

            for ( SimpleAsset tmpAsset : myComponents) {
                myWeights.add(tmpAsset.getWeight());
            }
        }

        return myWeights;
    }

    @Override public int size() {
        return myComponents.size();
    }

    @Override
    protected void reset() {

        myMeanReturn = null;
        myReturnVariance = null;
        myWeights = null;

        myCovariances = null;
        myAssetReturns = null;
        myAssetVolatilities = null;
        myAssetWeights = null;

        for ( SimpleAsset tmpAsset : myComponents) {
            tmpAsset.reset();
        }
    }

    MatrixR064 getAssetWeights() {

        if (myAssetWeights == null) {

             int tmpSize = myComponents.size();

             MatrixR064.DenseReceiver tmpWeights = MATRIX_FACTORY.makeDense(tmpSize, 1);

            for (int i = 0; i < tmpSize; i++) {
                tmpWeights.set(i, 0, this.getWeight(i));
            }

            myAssetWeights = tmpWeights.get();
        }

        return myAssetWeights;
    }

}

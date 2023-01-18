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
package org.ojalgo.ann;

import static org.ojalgo.function.constant.PrimitiveMath.*;

import com.google.errorprone.annotations.Var;
import java.util.function.DoubleUnaryOperator;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.random.Uniform;
import org.ojalgo.structure.Structure2D;

final class CalculationLayer {

    private ArtificialNeuralNetwork.Activator myActivator;
    private final PhysicalStore<Double> myBias;
    private final PhysicalStore<Double> myWeights;

    CalculationLayer( PhysicalStore.Factory<Double, ?> factory,  int numberOfInputs,  int numberOfOutputs,
             ArtificialNeuralNetwork.Activator activator) {

        super();

        myWeights = factory.make(numberOfInputs, numberOfOutputs);
        myBias = factory.make(1, numberOfOutputs);

        myActivator = activator;
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof CalculationLayer)) {
            return false;
        }
        var other = (CalculationLayer) obj;
        if (myActivator != other.myActivator) {
            return false;
        }
        if (myBias == null) {
            if (other.myBias != null) {
                return false;
            }
        } else if (!myBias.equals(other.myBias)) {
            return false;
        }
        if (myWeights == null) {
            if (other.myWeights != null) {
                return false;
            }
        } else if (!myWeights.equals(other.myWeights)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
         int prime = 31;
        @Var int result = 1;
        result = prime * result + (myActivator == null ? 0 : myActivator.hashCode());
        result = prime * result + (myBias == null ? 0 : myBias.hashCode());
        result = prime * result + (myWeights == null ? 0 : myWeights.hashCode());
        return result;
    }

    @Override
    public String toString() {
        var tmpBuilder = new StringBuilder();
        tmpBuilder.append("CalculationLayer [Weights=");
        tmpBuilder.append(myWeights);
        tmpBuilder.append(", Bias=");
        tmpBuilder.append(myBias);
        tmpBuilder.append(", Activator=");
        tmpBuilder.append(myActivator);
        tmpBuilder.append("]");
        return tmpBuilder.toString();
    }

    void adjust( PhysicalStore<Double> input,  PhysicalStore<Double> output,  PhysicalStore<Double> upstreamGradient,
             PhysicalStore<Double> downstreamGradient,  double learningRate,  double dropoutsFactor,  DoubleUnaryOperator regularisation) {

        downstreamGradient.modifyMatching(MULTIPLY, output.onAll(myActivator.getDerivativeInTermsOfOutput()).transpose());

        if (upstreamGradient != null) {
            // No need to do this multiplication for the input layer
            // input null to stop it...
            myWeights.multiply(downstreamGradient, upstreamGradient);
        }

        if (regularisation != null) {
            PrimitiveFunction.Unary modifier = arg -> arg + learningRate * regularisation.applyAsDouble(arg);
            myWeights.modifyAll(modifier);
        }

        for (long j = 0L, nbOutput = myWeights.countColumns(); j < nbOutput; j++) {
            for (long b = 0L, batchSize = input.countRows(); b < batchSize; b++) {

                double gradient = downstreamGradient.doubleValue(j, b);
                double ratedGradient = learningRate * gradient;
                myBias.add(j, ratedGradient);

                for (long i = 0L, nbInput = myWeights.countRows(); i < nbInput; i++) {
                    myWeights.add(i, j, ratedGradient * (input.doubleValue(b, i) / dropoutsFactor));
                }
            }
        }
    }

    int countInputNodes() {
        return Math.toIntExact(myWeights.countRows());
    }

    int countOutputNodes() {
        return Math.toIntExact(myWeights.countColumns());
    }

    ArtificialNeuralNetwork.Activator getActivator() {
        return myActivator;
    }

    double getBias( int output) {
        return myBias.doubleValue(output);
    }

    MatrixStore<Double> getLogicalWeights() {
        return myWeights.below(myBias);
    }

    Structure2D getStructure() {
        return myWeights;
    }

    double getWeight( int input,  int output) {
        return myWeights.doubleValue(input, output);
    }

    PhysicalStore<Double> invoke( PhysicalStore<Double> input,  PhysicalStore<Double> output) {
        myWeights.premultiply(input).onColumns(ADD, myBias).supplyTo(output);
        myActivator.activate(output);
        return output;
    }

    PhysicalStore<Double> invoke( PhysicalStore<Double> input,  PhysicalStore<Double> output,  double probabilityToKeep) {
        myWeights.premultiply(input).onColumns(ADD, myBias).supplyTo(output);
        myActivator.activate(output, probabilityToKeep);
        return output;
    }

    void randomise() {

        double magnitude = ONE / Math.sqrt(this.countInputNodes());

        var randomiser = new Uniform(-magnitude, 2 * magnitude);

        myWeights.fillAll(randomiser);

        myBias.fillAll(randomiser);
    }

    void scale( double factor) {
        myWeights.modifyAll(MULTIPLY.second(factor));
    }

    void setActivator( ArtificialNeuralNetwork.Activator activator) {
        myActivator = activator;
    }

    void setBias( int output,  double bias) {
        myBias.set(output, bias);
    }

    void setWeight( int input,  int output,  double weight) {
        myWeights.set(input, output, weight);
    }

}

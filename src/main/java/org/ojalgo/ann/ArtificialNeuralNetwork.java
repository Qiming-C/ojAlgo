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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.ojalgo.data.DataBatch;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.function.special.MissingMath;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.Primitive32Store;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Structure2D;

public final class ArtificialNeuralNetwork {

    /**
     * https://en.wikipedia.org/wiki/Activation_function
     *
     * @author apete
     */
    public enum Activator {

        /**
         * (-,+)
         */
        IDENTITY(ArtificialNeuralNetwork::doIdentity, arg -> ONE, true),
        /**
         * ReLU: [0,+)
         */
        RELU(ArtificialNeuralNetwork::doReLU, arg -> arg > ZERO ? ONE : ZERO, true),
        /**
         * [0,1]
         */
        SIGMOID(ArtificialNeuralNetwork::doSigmoid, arg -> arg * (ONE - arg), true),
        /**
         * [0,1] <br>
         * Currently this can only be used in the final layer in combination with
         * {@link ArtificialNeuralNetwork.Error#CROSS_ENTROPY}. All other usage will give incorrect network
         * training.
         */
        SOFTMAX(ArtificialNeuralNetwork::doSoftMax, arg -> ONE, false),
        /**
         * [-1,1]
         */
        TANH(ArtificialNeuralNetwork::doTanh, arg -> ONE - arg * arg, true);

        private final PrimitiveFunction.Unary myDerivativeInTermsOfOutput;
        private final Consumer<PhysicalStore<Double>> myFunction;
        private final boolean mySingleFolded;

        Activator( Consumer<PhysicalStore<Double>> function,  PrimitiveFunction.Unary derivativeInTermsOfOutput,  boolean singleFolded) {
            myFunction = function;
            myDerivativeInTermsOfOutput = derivativeInTermsOfOutput;
            mySingleFolded = singleFolded;
        }

        void activate( PhysicalStore<Double> output) {
            myFunction.accept(output);
        }

        void activate( PhysicalStore<Double> output,  double probabilityToKeep) {

            if (ZERO >= probabilityToKeep || probabilityToKeep > ONE) {
                throw new IllegalArgumentException();
            }

            myFunction.accept(output);
            output.modifyAll(NodeDropper.of(probabilityToKeep));
        }

        PrimitiveFunction.Unary getDerivativeInTermsOfOutput() {
            return myDerivativeInTermsOfOutput;
        }

        boolean isSingleFolded() {
            return mySingleFolded;
        }

    }

    public enum Error implements PrimitiveFunction.Binary {

        /**
         * Currently this can only be used in combination with {@link Activator#SOFTMAX} in the final layer.
         * All other usage will give incorrect network training.
         */
        CROSS_ENTROPY((target, current) -> -target * Math.log(current), (target, current) -> (current - target)),
        /**
         *
         */
        HALF_SQUARED_DIFFERENCE((target, current) -> HALF * (target - current) * (target - current), (target, current) -> (current - target));

        private final PrimitiveFunction.Binary myDerivative;
        private final PrimitiveFunction.Binary myFunction;

        Error( PrimitiveFunction.Binary function,  PrimitiveFunction.Binary derivative) {
            myFunction = function;
            myDerivative = derivative;
        }

        public double invoke( Access1D<?> target,  Access1D<?> current) {
            int limit = MissingMath.toMinIntExact(target.count(), current.count());
            @Var double retVal = ZERO;
            for (int i = 0; i < limit; i++) {
                retVal += myFunction.invoke(target.doubleValue(i), current.doubleValue(i));
            }
            return retVal;
        }

        @Override public double invoke( double target,  double current) {
            return myFunction.invoke(target, current);
        }

        PrimitiveFunction.Binary getDerivative() {
            return myDerivative;
        }

    }

    public static NetworkBuilder builder( int numberOfNetworkInputNodes) {
        return ArtificialNeuralNetwork.builder(Primitive64Store.FACTORY, numberOfNetworkInputNodes);
    }

    /**
     * @deprecated Use {@link #builder(int)} instead
     */
    @Deprecated
    public static NetworkTrainer builder( int numberOfInputNodes,  int... nodesPerCalculationLayer) {
        return ArtificialNeuralNetwork.builder(Primitive64Store.FACTORY, numberOfInputNodes, nodesPerCalculationLayer);
    }

    public static NetworkBuilder builder( PhysicalStore.Factory<Double, ?> factory,  int numberOfNetworkInputNodes) {
        return new NetworkBuilder(factory, numberOfNetworkInputNodes);
    }

    /**
     * @deprecated Use {@link #builder(org.ojalgo.matrix.store.PhysicalStore.Factory, int)} instead
     */
    @Deprecated
    public static NetworkTrainer builder( PhysicalStore.Factory<Double, ?> factory,  int numberOfInputNodes,  int... nodesPerCalculationLayer) {
        NetworkBuilder builder = ArtificialNeuralNetwork.builder(factory, numberOfInputNodes);
        for (int i = 0; i < nodesPerCalculationLayer.length; i++) {
            builder.layer(nodesPerCalculationLayer[i]);
        }
        return builder.get().newTrainer();
    }

    /**
     * Read (reconstruct) an ANN from the specified input previously written by {@link #writeTo(DataOutput)}.
     */
    public static ArtificialNeuralNetwork from( DataInput input) throws IOException {
        return FileFormat.read(null, input);
    }

    /**
     *See {@link #from(DataInput)}.
 
     */
    public static ArtificialNeuralNetwork from( File file) {
        return ArtificialNeuralNetwork.from(null, file);
    }

    /**
     *See {@link #from(DataInput)}.
 
     */
    public static ArtificialNeuralNetwork from( Path path,  OpenOption... options) {
        return ArtificialNeuralNetwork.from(null, path, options);
    }

    /**
     * Read (reconstruct) an ANN from the specified input previously written by {@link #writeTo(DataOutput)}.
     */
    public static ArtificialNeuralNetwork from( PhysicalStore.Factory<Double, ?> factory,  DataInput input) throws IOException {
        return FileFormat.read(factory, input);
    }

    /**
     *See {@link #from(DataInput)}.
 
     */
    public static ArtificialNeuralNetwork from( PhysicalStore.Factory<Double, ?> factory,  File file) {
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            return ArtificialNeuralNetwork.from(factory, input);
        } catch (IOException cause) {
            throw new RuntimeException(cause);
        }
    }

    /**
     *See {@link #from(DataInput)}.
 
     */
    public static ArtificialNeuralNetwork from( PhysicalStore.Factory<Double, ?> factory,  Path path,  OpenOption... options) {
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(Files.newInputStream(path, options)))) {
            return ArtificialNeuralNetwork.from(factory, input);
        } catch (IOException cause) {
            throw new RuntimeException(cause);
        }
    }

    static void doIdentity( PhysicalStore<Double> output) {
        // no-op activator
    }

    static void doReLU( PhysicalStore<Double> output) {
        output.modifyAll(MAX.second(ZERO));
    }

    static void doSigmoid( PhysicalStore<Double> output) {
        output.modifyAll(LOGISTIC);
    }

    static void doSoftMax( PhysicalStore<Double> output) {
        output.modifyAll(EXP);
        Primitive64Store totals = output.reduceRows(Aggregator.SUM).collect(Primitive64Store.FACTORY);
        output.onRows(DIVIDE, totals).supplyTo(output);
    }

    static void doTanh( PhysicalStore<Double> output) {
        output.modifyAll(PrimitiveMath.TANH);
    }

    private transient TrainingConfiguration myConfiguration = null;
    private final PhysicalStore.Factory<Double, ?> myFactory;
    private final CalculationLayer[] myLayers;

    ArtificialNeuralNetwork( NetworkBuilder builder) {

        super();

        myFactory = builder.getFactory();

        List<LayerTemplate> templates = builder.getLayers();
        myLayers = new CalculationLayer[templates.size()];
        for (int i = 0; i < myLayers.length; i++) {
            LayerTemplate layerTemplate = templates.get(i);
            myLayers[i] = new CalculationLayer(myFactory, layerTemplate.inputs, layerTemplate.outputs, layerTemplate.activator);
        }
    }

    ArtificialNeuralNetwork( PhysicalStore.Factory<Double, ?> factory,  int inputs,  int[] layers) {

        super();

        myFactory = factory;
        myLayers = new CalculationLayer[layers.length];
        @Var int tmpIn = inputs;
        @Var int tmpOut = inputs;
        for (int i = 0; i < layers.length; i++) {
            tmpIn = tmpOut;
            tmpOut = layers[i];
            myLayers[i] = new CalculationLayer(factory, tmpIn, tmpOut, ArtificialNeuralNetwork.Activator.SIGMOID);
        }
    }

    /**
     *Returns the number of calculation layers.
 
     */
    public int depth() {
        return myLayers.length;
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ArtificialNeuralNetwork)) {
            return false;
        }
        var other = (ArtificialNeuralNetwork) obj;
        if (!Arrays.equals(myLayers, other.myLayers)) {
            return false;
        }
        return true;
    }

    public Activator getActivator( int layer) {
        return myLayers[layer].getActivator();
    }

    public double getBias( int layer,  int output) {
        return myLayers[layer].getBias(output);
    }

    public double getWeight( int layer,  int input,  int output) {
        return myLayers[layer].getWeight(input, output);
    }

    @Override
    public int hashCode() {
         int prime = 31;
        int result = 1;
        return prime * result + Arrays.hashCode(myLayers);
    }

    /**
     * With batch size 1
     *
     * @see #newInvoker(int)
     */
    public NetworkInvoker newInvoker() {
        return this.newInvoker(1);
    }

    /**
     * If you create multiple invokers you can use them in different threads simutaneously - the invoker
     * contains any/all invocation specific state.
     *
     * @param batchSize The batch size - the number of batched invocations
     * @return The invoker
     */
    public NetworkInvoker newInvoker( int batchSize) {
        return new NetworkInvoker(this, batchSize);
    }

    /**
     * With batch size 1
     *
     * @see #newTrainer(int)
     */
    public NetworkTrainer newTrainer() {
        return this.newTrainer(1);
    }

    /**
     * Only 1 trainer at the time.
     *
     * @param batchSize The batch size - the number of batched training examples
     * @return The trainer
     */
    public NetworkTrainer newTrainer( int batchSize) {
        var trainer = new NetworkTrainer(this, batchSize);
        if (this.getOutputActivator() == Activator.SOFTMAX) {
            trainer.error(Error.CROSS_ENTROPY);
        } else {
            trainer.error(Error.HALF_SQUARED_DIFFERENCE);
        }
        return trainer;
    }

    public Structure2D[] structure() {

        Structure2D[] retVal = new Structure2D[myLayers.length];

        for (int l = 0; l < retVal.length; l++) {
            retVal[l] = myLayers[l].getStructure();
        }

        return retVal;
    }

    @Override
    public String toString() {
        var tmpBuilder = new StringBuilder();
        tmpBuilder.append("ArtificialNeuralNetwork [Layers=");
        for (CalculationLayer calculationLayer : myLayers) {
            tmpBuilder.append("\n");
            tmpBuilder.append(calculationLayer);
        }
        tmpBuilder.append("\n");
        tmpBuilder.append("]");
        return tmpBuilder.toString();
    }

    /**
     *Returns the max number of nodes in any layer.
 
     */
    public int width() {
        @Var int retVal = myLayers[0].countInputNodes();
        for (CalculationLayer layer : myLayers) {
            retVal = Math.max(retVal, layer.countOutputNodes());
        }
        return retVal;
    }

    /**
     * Will write (save) the ANN to the specified output. Can then later be read back by using
     * {@link #from(DataInput)}.
     */
    public void writeTo( DataOutput output) throws IOException {
        int version = myFactory == Primitive32Store.FACTORY ? 2 : 1;
        FileFormat.write(this, version, output);
    }

    /**
     *See {@link #writeTo(DataOutput)}.
 
     */
    public void writeTo( File file) {
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            this.writeTo(output);
        } catch (IOException cause) {
            throw new RuntimeException(cause);
        }
    }

    /**
     *See {@link #writeTo(DataOutput)}.
 
     */
    public void writeTo( Path path,  OpenOption... options) {
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path, options)))) {
            this.writeTo(output);
        } catch (IOException cause) {
            throw new RuntimeException(cause);
        }
    }

    void adjust( int layer,  PhysicalStore<Double> input,  PhysicalStore<Double> output,  PhysicalStore<Double> upstreamGradient,
             PhysicalStore<Double> downstreamGradient) {
        myLayers[layer].adjust(input, output, upstreamGradient, downstreamGradient, -myConfiguration.learningRate,
                myConfiguration.probabilityDidKeepInput(layer), myConfiguration.regularisation());
    }

    int countInputNodes() {
        return myLayers[0].countInputNodes();
    }

    int countInputNodes( int layer) {
        return myLayers[layer].countInputNodes();
    }

    int countOutputNodes() {
        return myLayers[myLayers.length - 1].countOutputNodes();
    }

    int countOutputNodes( int layer) {
        return myLayers[layer].countOutputNodes();
    }

    Activator getOutputActivator() {
        return myLayers[myLayers.length - 1].getActivator();
    }

    List<MatrixStore<Double>> getWeights() {
         ArrayList<MatrixStore<Double>> retVal = new ArrayList<>();
        for (int i = 0; i < myLayers.length; i++) {
            retVal.add(myLayers[i].getLogicalWeights());
        }
        return retVal;
    }

    PhysicalStore<Double> invoke( int layer,  PhysicalStore<Double> input,  PhysicalStore<Double> output) {
        if (myConfiguration != null) {
            return myLayers[layer].invoke(input, output, myConfiguration.probabilityWillKeepOutput(layer, this.depth()));
        }
        return myLayers[layer].invoke(input, output);
    }

    DataBatch newBatch( int rows,  int columns) {
        return DataBatch.from(myFactory, rows, columns);
    }

    PhysicalStore<Double> newStore( int rows,  int columns) {
        return myFactory.make(rows, columns);
    }

    void randomise() {
        for (int l = 0; l < myLayers.length; l++) {
            myLayers[l].randomise();
        }
    }

    void scale( int layer,  double factor) {
        myLayers[layer].scale(factor);
    }

    void setActivator( int layer,  Activator activator) {
        myLayers[layer].setActivator(activator);
    }

    void setBias( int layer,  int output,  double bias) {
        myLayers[layer].setBias(output, bias);
    }

    void setConfiguration( TrainingConfiguration configuration) {
        if (myConfiguration != null && configuration == null) {
            for (int l = 1, limit = this.depth(); l < limit; l++) {
                this.scale(l, myConfiguration.probabilityDidKeepInput(l));
            }
        }
        myConfiguration = configuration;
    }

    void setWeight( int layer,  int input,  int output,  double weight) {
        myLayers[layer].setWeight(input, output, weight);
    }

}

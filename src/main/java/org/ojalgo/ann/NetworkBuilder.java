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

import com.google.errorprone.annotations.Var;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.ojalgo.ann.ArtificialNeuralNetwork.Activator;
import org.ojalgo.matrix.store.PhysicalStore;

/**
 * An Artificial Neural Network (ANN) builder.
 *
 * @author apete
 */
public final class NetworkBuilder implements Supplier<ArtificialNeuralNetwork> {

    private final PhysicalStore.Factory<Double, ?> myFactory;
    private final List<LayerTemplate> myLayers = new ArrayList<>();
    private int myNextInputs = 0;

    NetworkBuilder( PhysicalStore.Factory<Double, ?> factory,  int networkInputs) {
        super();
        myFactory = factory;
        myNextInputs = networkInputs;
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NetworkBuilder)) {
            return false;
        }
        var other = (NetworkBuilder) obj;
        if (myNextInputs != other.myNextInputs) {
            return false;
        }
        if (myFactory == null) {
            if (other.myFactory != null) {
                return false;
            }
        } else if (!myFactory.equals(other.myFactory)) {
            return false;
        }
        if (!myLayers.equals(other.myLayers)) {
            return false;
        }
        return true;
    }

    @Override public ArtificialNeuralNetwork get() {
        var network = new ArtificialNeuralNetwork(this);
        network.randomise();
        return network;
    }

    @Override
    public int hashCode() {
         int prime = 31;
        @Var int result = 1;
        result = (prime * result) + ((myFactory == null) ? 0 : myFactory.hashCode());
        result = (prime * result) + myLayers.hashCode();
        result = (prime * result) + myNextInputs;
        return result;
    }

    public NetworkBuilder layer( int outputs) {
        return this.layer(outputs, ArtificialNeuralNetwork.Activator.SIGMOID);
    }

    public NetworkBuilder layer( int outputs,  Activator activator) {
        myLayers.add(new LayerTemplate(myNextInputs, outputs, activator));
        myNextInputs = outputs;
        return this;
    }

    PhysicalStore.Factory<Double, ?> getFactory() {
        return myFactory;
    }

    List<LayerTemplate> getLayers() {
        return myLayers;
    }

}

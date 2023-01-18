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
package org.ojalgo.equation;

import static org.ojalgo.function.constant.PrimitiveMath.*;

import com.google.errorprone.annotations.Var;
import java.util.ArrayList;
import java.util.List;
import org.ojalgo.array.ArrayR064;
import org.ojalgo.array.BasicArray;
import org.ojalgo.array.DenseArray;
import org.ojalgo.array.SparseArray;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.type.NumberDefinition;

public final class Equation implements Comparable<Equation>, Access1D<Double>, Mutate1D.Modifiable<Double> {

    public static Equation dense( int pivot,  int cols) {
        return Equation.dense(pivot, cols, ArrayR064.FACTORY);
    }

    public static Equation dense( int pivot,  int cols,  DenseArray.Factory<Double> factory) {
        return new Equation(factory.make(cols), pivot, ZERO);
    }

    public static List<Equation> denseSystem( int rows,  int cols) {
        return Equation.denseSystem(rows, cols, ArrayR064.FACTORY);
    }

    public static List<Equation> denseSystem( int rows,  int cols,  DenseArray.Factory<Double> factory) {

        List<Equation> system = new ArrayList<>(rows);

        for (int i = 0; i < rows; i++) {
            system.add(new Equation(factory.make(cols), i, ZERO));
        }

        return system;
    }

    public static Equation of( double rhs,  int pivot,  double... body) {
        return new Equation(ArrayR064.wrap(body), pivot, rhs);
    }

    public static Equation sparse( int pivot,  int cols) {
        return Equation.sparse(pivot, cols, ArrayR064.FACTORY);
    }

    public static Equation sparse( int pivot,  int cols,  DenseArray.Factory<Double> factory) {
        return new Equation(SparseArray.factory(factory).limit(cols).make(), pivot, ZERO);
    }

    public static Equation sparse( int pivot,  int cols,  DenseArray.Factory<Double> factory,  int numberOfNonzeros) {
        return new Equation(SparseArray.factory(factory).limit(cols).initial(numberOfNonzeros).make(), pivot, ZERO);
    }

    public static Equation sparse( int pivot,  int cols,  int numberOfNonzeros) {
        return Equation.sparse(pivot, cols, ArrayR064.FACTORY, numberOfNonzeros);
    }

    public static List<Equation> sparseSystem( int rows,  int cols) {
        return Equation.sparseSystem(rows, cols, ArrayR064.FACTORY);
    }

    public static List<Equation> sparseSystem( int rows,  int cols,  DenseArray.Factory<Double> factory) {

        List<Equation> system = new ArrayList<>(rows);

        for (int i = 0; i < rows; i++) {
            system.add(new Equation(SparseArray.factory(factory).limit(cols).make(), i, ZERO));
        }

        return system;
    }

    public static List<Equation> sparseSystem( int rows,  int cols,  DenseArray.Factory<Double> factory,  int numberOfNonzeros) {

        List<Equation> system = new ArrayList<>(rows);

        for (int i = 0; i < rows; i++) {
            system.add(new Equation(SparseArray.factory(factory).limit(cols).initial(numberOfNonzeros).make(), i, ZERO));
        }

        return system;
    }

    public static List<Equation> sparseSystem( int rows,  int cols,  int numberOfNonzeros) {
        return Equation.sparseSystem(rows, cols, ArrayR064.FACTORY, numberOfNonzeros);
    }

    public static Equation wrap( BasicArray<Double> body,  int pivot,  double rhs) {
        return new Equation(body, pivot, rhs);
    }

    /**
     * The row index of the original body matrix, [A].
     */
    public final int index;
    /**
     * The (nonzero) elements of this equation/row
     */
    private final BasicArray<Double> myBody;
    private double myPivot = ZERO;
    private double myRHS;

    /**
     * @deprecated v49 Use one of the factory methods instead
     */
    @Deprecated
    public Equation( int row,  long numberOfColumns,  double rhs) {
        this(SparseArray.factory(ArrayR064.FACTORY).limit(numberOfColumns).make(), row, rhs);
    }

    Equation( BasicArray<Double> body,  int pivot,  double rhs) {

        super();

        myBody = body;
        myRHS = rhs;

        index = pivot;
        myPivot = ZERO;
    }

    @Override public void add( long ind,  Comparable<?> addend) {
        this.add(ind, NumberDefinition.doubleValue(addend));
    }

    @Override public void add( long ind,  double addend) {
        myBody.add(ind, addend);
        if (ind == index) {
            myPivot = myBody.doubleValue(ind);
        }
    }

    /**
     * Will perform a (relaxed) GaussSeidel update.
     *
     * @param x The current solution (one element will be updated)
     * @param relaxation Typically 1.0 but could be anything (Most likely should be between 0.0 and 2.0).
     * @return The error in this equation
     */
    public <T extends Access1D<Double> & Mutate1D.Modifiable<Double>> double adjust( T x,  double relaxation) {
        return this.calculate(x, myRHS, relaxation);
    }

    @Override public int compareTo( Equation other) {
        return Integer.compare(index, other.index);
    }

    @Override public long count() {
        return myBody.count();
    }

    @Override public double dot( Access1D<?> vector) {
        return myBody.dot(vector);
    }

    @Override public double doubleValue( long ind) {
        return myBody.doubleValue(ind);
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Equation)) {
            return false;
        }
         var other = (Equation) obj;
        if (index != other.index) {
            return false;
        }
        return true;
    }

    @Override public Double get( long ind) {
        return myBody.get(ind);
    }

    public BasicArray<Double> getBody() {
        return myBody;
    }

    /**
     *Returns the element at {@link #index}.
 
     */
    public double getPivot() {
        return myPivot;
    }

    /**
     *Returns the equation RHS.
 
     */
    public double getRHS() {
        return myRHS;
    }

    @Override
    public int hashCode() {
         int prime = 31;
        int result = 1;
        return prime * result + index;
    }

    public <T extends Access1D<Double> & Mutate1D.Modifiable<Double>> void initialise( T x) {
        this.calculate(x, ZERO, ONE);
    }

    @Override public void modifyOne( long ind,  UnaryFunction<Double> modifier) {
        myBody.modifyOne(ind, modifier);
        if (ind == index) {
            myPivot = myBody.doubleValue(ind);
        }
    }

    public void set( long ind,  Comparable<?> value) {
        this.set(ind, NumberDefinition.doubleValue(value));
    }

    public void set( long ind,  double value) {
        myBody.set(ind, value);
        if (ind == index) {
            myPivot = value;
        }
    }

    public void setRHS( double rhs) {
        myRHS = rhs;
    }

    @Override
    public String toString() {
        return index + ": " + myBody.toString() + " = " + myRHS;
    }

    private <T extends Access1D<Double> & Mutate1D.Modifiable<Double>> double calculate( T x,  double rhs,  double relaxation) {

        @Var double increment = rhs;

        double error = increment -= myBody.dot(x);

        increment *= relaxation;

        increment /= myPivot;

        x.add(index, increment);

        return error;
    }

}

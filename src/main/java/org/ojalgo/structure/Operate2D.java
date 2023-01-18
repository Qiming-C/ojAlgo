package org.ojalgo.structure;

import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;

public interface Operate2D<N extends Comparable<N>, P extends Operate2D<N, P>> {

    default P onAll( BinaryFunction<N> operator,  double right) {
        return this.onAll(operator.second(right));
    }

    default P onAll( BinaryFunction<N> operator,  N right) {
        return this.onAll(operator.second(right));
    }

    default P onAll( double left,  BinaryFunction<N> operator) {
        return this.onAll(operator.first(left));
    }

    default P onAll( N left,  BinaryFunction<N> operator) {
        return this.onAll(operator.first(left));
    }

    default P onAll( ParameterFunction<N> operator,  int parameter) {
        return this.onAll(operator.parameter(parameter));
    }

    P onAll(UnaryFunction<N> operator);

    P onAny(Transformation2D<N> operator);

    P onColumns(BinaryFunction<N> operator, Access1D<N> right);

    P onMatching(Access2D<N> left, BinaryFunction<N> operator);

    P onMatching(BinaryFunction<N> operator, Access2D<N> right);

    P onRows(BinaryFunction<N> operator, Access1D<N> right);

}

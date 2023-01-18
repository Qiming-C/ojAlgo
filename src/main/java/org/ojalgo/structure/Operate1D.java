package org.ojalgo.structure;

import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;

public interface Operate1D<N extends Comparable<N>, P extends Operate1D<N, P>> {

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

    P onAny(Transformation1D<N> operator);

    P onMatching(Access1D<N> left, BinaryFunction<N> operator);

    P onMatching(BinaryFunction<N> operator, Access1D<N> right);

}

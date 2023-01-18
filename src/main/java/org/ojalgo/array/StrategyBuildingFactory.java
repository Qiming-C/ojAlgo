package org.ojalgo.array;

import org.ojalgo.structure.Access1D;

public abstract class StrategyBuildingFactory<N extends Comparable<N>, I extends Access1D<N>, SB extends StrategyBuildingFactory<N, I, SB>> {

    private final DenseCapacityStrategy<N> myStrategy;

    public StrategyBuildingFactory( DenseArray.Factory<N> denseFactory) {

        super();

        myStrategy = new DenseCapacityStrategy<>(denseFactory);
    }

    /**
     *Returns this.
 @param chunk Defines the capacity break point. Below this point the capacity is doubled when needed.
     *        Above it, it is grown by adding one "chunk" at the time.
     * 
     */
    public SB chunk( long chunk) {
        myStrategy.chunk(chunk);
        return (SB) this;
    }

    public SB fixed( long fixed) {
        return this.initial(fixed).limit(fixed);
    }

    /**
     *Returns this.
 @param initial Sets the initial capacity of the "arrays" to be created using this factory.
     * 
     */
    public SB initial( long initial) {
        myStrategy.initial(initial);
        return (SB) this;
    }

    /**
     *Returns this.
 @param limit Defines a maximum size. Only set this if you know the precise max size, and it should be
     *        something relatively small. Setting the max size is meant as an alternative to setting any/all
     *        of the other paramaters, and will switch to a tighter capacity strategy. The only other
     *        configuration you may want to set in combination with this one is the initial capacity (set that
     *        first in that case).
     * 
     */
    public SB limit( long limit) {
        myStrategy.limit(limit);
        return (SB) this;
    }

    public abstract I make();

    DenseCapacityStrategy<N> getStrategy() {
        return myStrategy;
    }

}

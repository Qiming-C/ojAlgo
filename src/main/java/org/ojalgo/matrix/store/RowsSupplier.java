package org.ojalgo.matrix.store;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.ojalgo.array.SparseArray;
import org.ojalgo.array.SparseArray.SparseFactory;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.ElementView1D;

public final class RowsSupplier<N extends Comparable<N>> implements Access2D<N>, ElementsSupplier<N>, Supplier<PhysicalStore<N>> {

    static final class ItemView<N extends Comparable<N>> extends RowView<N> {

        private final RowsSupplier<N> mySupplier;

        ItemView( RowsSupplier<N> access) {
            super(access);
            mySupplier = access;
        }

        @Override public ElementView1D<N, ?> elements() {
            return this.getCurrent().elements();
        }

        @Override public ElementView1D<N, ?> nonzeros() {
            return this.getCurrent().nonzeros();
        }

        private SparseArray<N> getCurrent() {
            return mySupplier.getRow(Math.toIntExact(this.row()));
        }

    }

    private final int myColumnsCount;
    private final PhysicalStore.Factory<N, ?> myFactory;
    private final List<SparseArray<N>> myRows = new ArrayList<>();

    RowsSupplier( PhysicalStore.Factory<N, ?> factory,  int numberOfColumns) {
        super();
        myColumnsCount = numberOfColumns;
        myFactory = factory;
    }

    public SparseArray<N> addRow() {
        return this.addRow(SparseArray.factory(myFactory.array()).limit(myColumnsCount).make());
    }

    public void addRows( int numberToAdd) {
        SparseFactory<N> factory = SparseArray.factory(myFactory.array()).limit(myColumnsCount);
        for (int i = 0; i < numberToAdd; i++) {
            myRows.add(factory.make());
        }
    }

    @Override public long countColumns() {
        return myColumnsCount;
    }

    @Override public long countRows() {
        return myRows.size();
    }

    @Override public double doubleValue( long row,  long col) {
        return myRows.get((int) row).doubleValue(col);
    }

    @Override public PhysicalStore<N> get() {
        return this.collect(myFactory);
    }

    @Override public N get( long row,  long col) {
        return myRows.get((int) row).get(col);
    }

    public SparseArray<N> getRow( int index) {
        return myRows.get(index);
    }

    public PhysicalStore.Factory<N, ?> physical() {
        return myFactory;
    }

    public SparseArray<N> removeRow( int index) {
        return myRows.remove(index);
    }

    @Override public RowView<N> rows() {
        return new ItemView<>(this);
    }

    public RowsSupplier<N> selectRows( int[] indices) {
        RowsSupplier<N> retVal = new RowsSupplier<>(myFactory, myColumnsCount);
        for (int i = 0; i < indices.length; i++) {
            retVal.addRow(this.getRow(indices[i]));
        }
        return retVal;
    }

    @Override public void supplyTo( TransformableRegion<N> receiver) {

        receiver.reset();

        for (int i = 0, limit = myRows.size(); i < limit; i++) {
            myRows.get(i).supplyNonZerosTo(receiver.regionByRows(i));
        }
    }

    @Override
    public String toString() {
        return Access2D.toString(this);
    }

    SparseArray<N> addRow( SparseArray<N> rowToAdd) {
        if (myRows.add(rowToAdd)) {
            return rowToAdd;
        } else {
            return null;
        }
    }

}

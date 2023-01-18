/*
 * Copyright 1997-2022 Optimatika (www.optimatika.se)
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
package org.ojalgo.netio;

import com.google.errorprone.annotations.Var;
import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.ojalgo.ProgrammingError;
import org.ojalgo.netio.EnumeratedColumnsParser.LineView;
import org.ojalgo.type.context.TypeContext;

public final class EnumeratedColumnsParser implements BasicParser<LineView> {

    public static class Builder implements Supplier<EnumeratedColumnsParser> {

        private char myDelimiter = ',';
        private final int myNumberOfColumns;
        private ParseStrategy myStrategy = ParseStrategy.RFC4180;

        Builder( int numberOfColumns) {

            super();

            myNumberOfColumns = numberOfColumns;
        }

        public Builder delimiter( char delimiter) {
            myDelimiter = delimiter;
            return this;
        }

        @Override public EnumeratedColumnsParser get() {
            return new EnumeratedColumnsParser(myNumberOfColumns, myDelimiter, myStrategy);
        }

        public Builder strategy( ParseStrategy quoted) {
            myStrategy = quoted;
            return this;
        }

    }

    public static abstract class LineView {

        final char delimiter;
        transient String line = null;
        final int numberOfColumns;

        @SuppressWarnings("unused")
        private LineView() {
            this(0, ',');
        }

        LineView( int numberOfColumns,  char delimiter) {

            super();

            this.numberOfColumns = numberOfColumns;
            this.delimiter = delimiter;
        }

        public final double doubleValue( Enum<?> column) {
             String tmpStringValue = this.get(column.ordinal());
            if ((tmpStringValue != null) && (tmpStringValue.length() > 0)) {
                return Double.parseDouble(tmpStringValue);
            } else {
                return Double.NaN;
            }
        }

        public final double floatValue( Enum<?> column) {
             String tmpStringValue = this.get(column.ordinal());
            if ((tmpStringValue != null) && (tmpStringValue.length() > 0)) {
                return Float.parseFloat(tmpStringValue);
            } else {
                return Float.NaN;
            }
        }

        public final String get( Enum<?> column) {
            return this.get(column.ordinal());
        }

        public final <P> P get( Enum<?> column,  TypeContext<P> typeContext) {
             String tmpStringValue = this.get(column.ordinal());
            if ((tmpStringValue != null) && (tmpStringValue.length() > 0)) {
                return typeContext.parse(tmpStringValue);
            } else {
                return null;
            }
        }

        public abstract String get( int column);

        public final long intValue( Enum<?> column) {
             String tmpStringValue = this.get(column.ordinal());
            if ((tmpStringValue != null) && (tmpStringValue.length() > 0)) {
                return Integer.parseInt(tmpStringValue);
            } else {
                return 0;
            }
        }

        public boolean isLineOK() {
            return (line != null) && (line.length() > 0);
        }

        public final long longValue( Enum<?> column) {
             String tmpStringValue = this.get(column.ordinal());
            if ((tmpStringValue != null) && (tmpStringValue.length() > 0)) {
                return Long.parseLong(tmpStringValue);
            } else {
                return 0L;
            }
        }

        public final BigDecimal toBigDecimal( Enum<?> column) {
             String tmpStringValue = this.get(column.ordinal());
            if ((tmpStringValue != null) && (tmpStringValue.length() > 0)) {
                return new BigDecimal(tmpStringValue);
            } else {
                return null;
            }
        }

        abstract boolean index( String line, Supplier<String> lineSupplier);

    }

    public enum ParseStrategy {

        /**
         * Simplest possible. Just delimited data. No quoting or any special characters...
         */
        FAST {

            @Override
            public LineView make( int numberOfColumns,  char delimiter) {
                return new FastViewStrategy(numberOfColumns, delimiter);
            }
        },

        /**
         * Same as {@link #FAST} but assumes values are quoted (they have to be, ALL of them).
         */
        QUOTED() {

            @Override
            public LineView make( int numberOfColumns,  char delimiter) {
                return new QuotedViewStrategy(numberOfColumns, delimiter);
            }
        },

        /**
         * As specified by the RFC4180 standard.
         */
        RFC4180 {

            @Override
            public LineView make( int numberOfColumns,  char delimiter) {
                return new RFC4180(numberOfColumns, delimiter);
            }
        };

        public abstract LineView make(int numberOfColumns, char delimiter);

    }

    static class FastViewStrategy extends LineView {

        private final int[] myIndices;

        FastViewStrategy( int numberOfColumns,  char delimiter) {

            super(numberOfColumns, delimiter);

            myIndices = new int[numberOfColumns + 1];
        }

        @Override
        public String get( int column) {
            return line.substring(myIndices[column] + 1, myIndices[column + 1]);
        }

        @Override
        boolean index( String line,  Supplier<String> lineSupplier) {

            @Var int tmpIndex = 0;
            @Var int tmpPosition = -1;
            myIndices[tmpIndex] = tmpPosition;

            while ((tmpPosition = line.indexOf(delimiter, ++tmpPosition)) >= 0) {
                myIndices[++tmpIndex] = tmpPosition;
            }

            tmpPosition = line.length();
            myIndices[++tmpIndex] = tmpPosition;

            this.line = line;

            return tmpIndex == numberOfColumns;
        }
    }

    static class QuotedViewStrategy extends LineView {

        private final int[] myIndices;
        private final String mySplitter;

        QuotedViewStrategy( int numberOfColumns,  char delimiter) {

            super(numberOfColumns, delimiter);

            myIndices = new int[numberOfColumns + 1];
            mySplitter = String.valueOf(new char[] { '"', delimiter, '"' });
        }

        @Override
        public String get( int column) {
            return line.substring(myIndices[column] + 3, myIndices[column + 1]);
        }

        @Override
        boolean index( String line,  Supplier<String> lineSupplier) {

            @Var int tmpIndex = 0;
            @Var int tmpPosition = -2;
            myIndices[tmpIndex] = tmpPosition;

            while ((tmpPosition = line.indexOf(mySplitter, ++tmpPosition)) >= 0) {
                myIndices[++tmpIndex] = tmpPosition;
            }

            tmpPosition = line.length() - 1;
            myIndices[++tmpIndex] = tmpPosition;

            this.line = line;

            return tmpIndex == numberOfColumns;
        }

    }

    static class RFC4180 extends LineView {

        private static final char QUOTE = '"';

        private final int[] myBegin;
        private final int[] myEnd;
        private boolean myEscaped;

        RFC4180( int numberOfColumns,  char delimiter) {

            super(numberOfColumns, delimiter);

            myBegin = new int[numberOfColumns];
            myEnd = new int[numberOfColumns];
        }

        @Override
        public String get( int column) {
            if (myEscaped) {
                return line.substring(myBegin[column], myEnd[column]).replace("\"\"", "\"");
            } else {
                return line.substring(myBegin[column], myEnd[column]);
            }
        }

        @Override
        boolean index( String line,  Supplier<String> lineSupplier) {

            myEscaped = false;

            @Var String tmpLine = line;

            @Var int c = 0;
            myBegin[0] = 0;
            @Var int tmpMode = 0;
            @Var int tmpNumberOfQuotes = 0;

            @Var char tmpCurChar;
            @Var int tmpNextInd;
            for (int i = 0; i < tmpLine.length(); i++) {
                tmpCurChar = tmpLine.charAt(i);
                tmpNextInd = i + 1;

                switch (tmpMode) {

                case 1: // Within quotes - look for the end of the quote

                    if (tmpCurChar == QUOTE) {
                        tmpNumberOfQuotes++;
                        if (((tmpNumberOfQuotes % 2) == 0) && ((tmpNextInd == tmpLine.length()) || (tmpLine.charAt(tmpNextInd) != QUOTE))) {
                            myEnd[c++] = i;
                            tmpMode = 2;
                        } else {
                            myEscaped = true;
                        }
                    } else if (tmpNextInd == tmpLine.length()) {
                        if (lineSupplier == null) {
                            throw new ProgrammingError("Cant't handle line breaks within quotes when used this way!");
                        }
                        String nextPart = lineSupplier.get();
                        if (nextPart != null) {
                            tmpLine = tmpLine + '\n' + nextPart;
                        } else {
                            return false;
                        }
                    }

                    break;

                case 2: // Quote ended but not yet found next delimiter

                    if (tmpCurChar == delimiter) {
                        myBegin[c] = tmpNextInd;
                        if (tmpNextInd == tmpLine.length()) {
                            myEnd[c++] = tmpNextInd;
                        }
                        tmpMode = 0;
                    } else if (tmpNextInd == tmpLine.length()) {
                        myEnd[c++] = tmpNextInd;
                    }

                    break;

                default: // Not quoted

                    if (tmpCurChar == QUOTE) {
                        tmpNumberOfQuotes++;
                        myBegin[c] = tmpNextInd;
                        tmpMode = 1;
                    } else if (tmpCurChar == delimiter) {
                        myEnd[c++] = i;
                        myBegin[c] = tmpNextInd;
                        if (tmpNextInd == tmpLine.length()) {
                            myEnd[c++] = tmpNextInd;
                        }
                    } else if (tmpNextInd == tmpLine.length()) {
                        myEnd[c++] = tmpNextInd;
                    }

                    break;
                }
            }

            this.line = tmpLine;

            return numberOfColumns == c;
        }

    }

    public static EnumeratedColumnsParser.Builder make( Class<? extends Enum<?>> columns) {
        return new EnumeratedColumnsParser.Builder(columns.getFields().length);
    }

    public static EnumeratedColumnsParser.Builder make( int numberOfColumns) {
        return new EnumeratedColumnsParser.Builder(numberOfColumns);
    }

    private final LineView myLineView;

    EnumeratedColumnsParser( int columns,  char delimiter,  ParseStrategy strategy) {

        super();

        myLineView = strategy.make(columns, delimiter);
    }

    @Override public LineView parse( String line) {
        return this.parseLine(line, null);
    }

    @Override public void parse( Supplier<String> reader,  boolean skipHeader,  Consumer<LineView> consumer) {

        // A reimplementation of the default method from the BasicParser interface.
        // The only difference is that it keeps a reference to the BufferedReader to enable passing it to other methods.

        @Var String line = null;
        @Var LineView item = null;

        if (skipHeader) {
            line = reader.get();
            line = null;
        }

        while ((line = reader.get()) != null) {
            if ((line.length() > 0) && !line.startsWith("#") && ((item = this.parseLine(line, reader)) != null)) {
                consumer.accept(item);
            }
        }

    }

    LineView parseLine( String line,  Supplier<String> reader) {

        if (myLineView.index(line, reader)) {
            return myLineView;
        } else {
            return null;
        }
    }

}

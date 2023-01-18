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
package org.ojalgo.data.domain.finance.series;

import com.google.common.base.Splitter;
import com.google.errorprone.annotations.Var;
import java.time.LocalDate;
import java.util.List;
import org.ojalgo.netio.ASCII;
import org.ojalgo.netio.BasicParser;

/**
 * @author apete
 */
public class YahooParser implements BasicParser<YahooParser.Data> {

    public static final class Data extends DatePrice {

        public final double adjustedClose;
        public final double close;
        public final double high;
        public final double low;
        public final double open;
        public final double volume;

        public Data( LocalDate date,  double open,  double high,  double low,  double close,  double adjustedClose,
                 double volume) {

            super(date);

            this.adjustedClose = adjustedClose;
            this.close = close;
            this.high = high;
            this.low = low;
            this.open = open;
            this.volume = volume;
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj) || !(obj instanceof Data)) {
                return false;
            }
            var other = (Data) obj;
            if ((Double.doubleToLongBits(adjustedClose) != Double.doubleToLongBits(other.adjustedClose))
                    || (Double.doubleToLongBits(close) != Double.doubleToLongBits(other.close))
                    || (Double.doubleToLongBits(high) != Double.doubleToLongBits(other.high))
                    || (Double.doubleToLongBits(low) != Double.doubleToLongBits(other.low))) {
                return false;
            }
            if ((Double.doubleToLongBits(open) != Double.doubleToLongBits(other.open))
                    || (Double.doubleToLongBits(volume) != Double.doubleToLongBits(other.volume))) {
                return false;
            }
            return true;
        }

        @Override
        public double getPrice() {
            return adjustedClose;
        }

        @Override
        public int hashCode() {
             int prime = 31;
            @Var int result = super.hashCode();
            @Var long temp;
            temp = Double.doubleToLongBits(adjustedClose);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(close);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(high);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(low);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(open);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(volume);
            return prime * result + (int) (temp ^ (temp >>> 32));
        }

    }

    public static final YahooParser INSTANCE = new YahooParser();

    /**
     * Checks if the header matches what this parser can handle.
     */
    public static boolean testHeader( String header) {

        List<String> columns = Splitter.onPattern("" + ASCII.COMMA).splitToList(header);

        int length = columns.size();
        if (length != 7) {
            return false;
        }

        String date = columns.get(0).trim();
        if (!"Date".equalsIgnoreCase(date)) {
            return false;
        }

        String price = columns.get(5).trim();
        if (!"Adj Close".equalsIgnoreCase(price)) {
            return false;
        }

        return true;
    }

    public YahooParser() {
        super();
    }

    @Override
    public YahooParser.Data parse( String line) {

        // Date,Open,High,Low,Close,Adj Close,Volume

        @Var LocalDate date = null;
        @Var double open = Double.NaN;
        @Var double high = Double.NaN;
        @Var double low = Double.NaN;
        @Var double close = Double.NaN;
        @Var double adjustedClose = Double.NaN;
        @Var double volume = Double.NaN;

        try {

            @Var int inclBegin = 0;
            @Var int exclEnd = line.indexOf(ASCII.COMMA, inclBegin);
            @Var String part = line.substring(inclBegin, exclEnd);
            date = LocalDate.parse(part);

            inclBegin = exclEnd + 1;
            exclEnd = line.indexOf(ASCII.COMMA, inclBegin);
            part = line.substring(inclBegin, exclEnd);
            try {
                open = Double.parseDouble(part);
            } catch ( NumberFormatException ex) {
                open = Double.NaN;
            }

            inclBegin = exclEnd + 1;
            exclEnd = line.indexOf(ASCII.COMMA, inclBegin);
            part = line.substring(inclBegin, exclEnd);
            try {
                high = Double.parseDouble(part);
            } catch ( NumberFormatException ex) {
                high = Double.NaN;
            }

            inclBegin = exclEnd + 1;
            exclEnd = line.indexOf(ASCII.COMMA, inclBegin);
            part = line.substring(inclBegin, exclEnd);
            try {
                low = Double.parseDouble(part);
            } catch ( NumberFormatException ex) {
                low = Double.NaN;
            }

            inclBegin = exclEnd + 1;
            exclEnd = line.indexOf(ASCII.COMMA, inclBegin);
            part = line.substring(inclBegin, exclEnd);
            try {
                close = Double.parseDouble(part);
            } catch ( NumberFormatException ex) {
                close = Double.NaN;
            }

            inclBegin = exclEnd + 1;
            exclEnd = line.indexOf(ASCII.COMMA, inclBegin);
            part = line.substring(inclBegin, exclEnd);
            try {
                adjustedClose = Double.parseDouble(part);
            } catch ( NumberFormatException ex) {
                adjustedClose = Double.NaN;
            }

            inclBegin = exclEnd + 1;
            part = line.substring(inclBegin);
            try {
                volume = Double.parseDouble(part);
            } catch ( NumberFormatException ex) {
                volume = Double.NaN;
            }

        } catch (Exception cause) {

            date = null;
            adjustedClose = Double.NaN;
        }

        if (date != null && Double.isFinite(adjustedClose)) {
            // Date,Open,High,Low,Close,Adj Close,Volume
            return new Data(date, open, high, low, close, adjustedClose, volume);
        } else {
            return null;
        }
    }

}

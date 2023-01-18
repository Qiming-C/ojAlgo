package org.ojalgo.data.domain.finance.series;

import com.google.common.base.Splitter;
import com.google.errorprone.annotations.Var;
import java.time.LocalDate;
import java.util.List;
import org.ojalgo.netio.ASCII;
import org.ojalgo.netio.BasicParser;

/**
 * https://iextrading.com/developer/docs/#chart
 *
 * @author stefanvanegmond
 */
public class IEXTradingParser implements BasicParser<IEXTradingParser.Data> {

    public static final class Data extends DatePrice {

        public final double close;
        public final double high;
        public final double low;
        public final double open;
        public final double unadjustedVolume;
        public final double volume;

        public Data( LocalDate date,  double open,  double high,  double low,  double close,  double volume,
                 double unadjustedVolume) {
            super(date);
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
            this.unadjustedVolume = unadjustedVolume;
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
            if ((Double.doubleToLongBits(close) != Double.doubleToLongBits(other.close))
                    || (Double.doubleToLongBits(high) != Double.doubleToLongBits(other.high))
                    || (Double.doubleToLongBits(low) != Double.doubleToLongBits(other.low))
                    || (Double.doubleToLongBits(open) != Double.doubleToLongBits(other.open))) {
                return false;
            }
            if ((Double.doubleToLongBits(unadjustedVolume) != Double.doubleToLongBits(other.unadjustedVolume))
                    || (Double.doubleToLongBits(volume) != Double.doubleToLongBits(other.volume))) {
                return false;
            }
            return true;
        }

        @Override
        public double getPrice() {
            return close;
        }

        @Override
        public int hashCode() {
             int prime = 31;
            @Var int result = super.hashCode();
            @Var long temp;
            temp = Double.doubleToLongBits(close);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(high);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(low);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(open);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(unadjustedVolume);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(volume);
            return prime * result + (int) (temp ^ (temp >>> 32));
        }

    }

    public static final IEXTradingParser INSTANCE = new IEXTradingParser();

    /**
     * Checks if the header matches what this parser can handle.
     */
    public static boolean testHeader( String header) {

        List<String> columns = Splitter.onPattern("" + ASCII.COMMA).splitToList(header);

        int length = columns.size();
        if (length != 12) {
            return false;
        }

        String date = columns.get(0).trim();
        if (!"date".equalsIgnoreCase(date)) {
            return false;
        }

        String price = columns.get(4).trim();
        if (!"close".equalsIgnoreCase(price)) {
            return false;
        }

        return true;
    }

    public IEXTradingParser() {
        super();
    }

    @Override
    public IEXTradingParser.Data parse( String line) {

        // date,open,high,low,close,volume,unadjustedVolume,change,changePercent,vwap,label,changeOverTime

        @Var LocalDate date = null;
        @Var double open = Double.NaN;
        @Var double high = Double.NaN;
        @Var double low = Double.NaN;
        @Var double close = Double.NaN;
        @Var double volume = Double.NaN;
        @Var double unadjustedVolume = Double.NaN;

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
                volume = Double.parseDouble(part);
            } catch ( NumberFormatException ex) {
                volume = Double.NaN;
            }

            inclBegin = exclEnd + 1;
            exclEnd = line.indexOf(ASCII.COMMA, inclBegin);
            part = line.substring(inclBegin, exclEnd);
            try {
                unadjustedVolume = Double.parseDouble(part);
            } catch ( NumberFormatException ex) {
                unadjustedVolume = Double.NaN;
            }

        } catch (Exception cause) {

            date = null;
            close = Double.NaN;
        }

        if (date != null && Double.isFinite(close)) {
            // date,open,high,low,close,volume,unadjustedVolume,change,changePercent,vwap,label,changeOverTime
            return new Data(date, open, high, low, close, volume, unadjustedVolume);
        } else {
            return null;
        }
    }

}

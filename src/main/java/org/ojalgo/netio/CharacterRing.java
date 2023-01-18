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
package org.ojalgo.netio;

import com.google.errorprone.annotations.Var;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A circular char buffer - an {@linkplain Appendable} {@linkplain CharSequence} that always hold exactly
 * 65536 characters. Whenever you append something the oldest entry gets overwritten.
 *
 * @author apete
 */
public final class CharacterRing implements CharSequence, Appendable, BasicLogger.Buffer {

    public static final class RingLogger implements BasicLogger, BasicLogger.Buffer {

        private transient Formatter myFormatter;
        private final CharacterRing myRing;

        RingLogger() {

            super();

            myRing = new CharacterRing();
        }

        @Override public Optional<Writer> asWriter() {
            return Optional.empty();
        }

        @Override public void clear() {
            myRing.clear();
        }

        @Override public void flush( Appendable receiver) {
            myRing.flush(receiver);
        }

        @Override public void flush( BasicLogger receiver) {
            myRing.flush(receiver);
        }

        @Override public void print( boolean value) {
            try {
                myRing.append(String.valueOf(value));
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        @Override public void print( byte value) {
            try {
                myRing.append(String.valueOf(value));
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        @Override public void print( char value) {
            try {
                myRing.append(String.valueOf(value));
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        @Override public void print( double value) {
            try {
                myRing.append(String.valueOf(value));
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        @Override public void print( float value) {
            try {
                myRing.append(String.valueOf(value));
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        @Override public void print( int value) {
            try {
                myRing.append(String.valueOf(value));
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        @Override public void print( long value) {
            try {
                myRing.append(String.valueOf(value));
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        @Override public void print( Object object) {
            try {
                myRing.append(String.valueOf(object));
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        @Override public void print( short value) {
            try {
                myRing.append(String.valueOf(value));
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        @Override public void print( Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override public void printf( String format,  Object... args) {
            if (myFormatter == null || !Objects.equals(myFormatter.locale(), Locale.getDefault())) {
                myFormatter = new Formatter(myRing);
            }
            myFormatter.format(Locale.getDefault(), format, args);
        }

        @Override public void println() {
            try {
                myRing.append(ASCII.LF);
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

    }

    public static int length = Character.MAX_VALUE + 1;

    public static RingLogger newRingLogger() {
        return new RingLogger();
    }

    private final char[] myCharacters;
    private char myCursor = 0;

    public CharacterRing() {

        super();

        myCharacters = new char[length];
        myCursor = 0;
    }

    @Override
    public CharacterRing append( char c) throws IOException {
        myCharacters[myCursor++] = c;
        return this;
    }

    @Override
    public CharacterRing append( CharSequence csq) throws IOException {
        return this.append(csq, 0, csq.length());
    }

    @Override
    public CharacterRing append( CharSequence csq,  int start,  int end) throws IOException {
        for (int i = start; i < end; i++) {
            this.append(csq.charAt(i));
        }
        return this;
    }

    @Override
    public char charAt( int index) {
        return myCharacters[(myCursor + index) % length];
    }

    @Override public void clear() {
        Arrays.fill(myCharacters, ASCII.NULL);
        myCursor = 0;
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof CharacterRing)) {
            return false;
        }
        var other = (CharacterRing) obj;
        if (!Arrays.equals(myCharacters, other.myCharacters) || (myCursor != other.myCursor)) {
            return false;
        }
        return true;
    }

    @Override public void flush( Appendable receiver) {
        try {
            synchronized (receiver) {
                int cursor = myCursor;
                @Var char tmpChar;
                for (int i = cursor; i < length; i++) {
                    tmpChar = myCharacters[i];
                    if (tmpChar != ASCII.NULL) {
                        receiver.append(tmpChar);
                    }
                }
                for (int i = 0; i < cursor; i++) {
                    tmpChar = myCharacters[i];
                    if (tmpChar != ASCII.NULL) {
                        receiver.append(tmpChar);
                    }
                }
                this.clear();
            }
        } catch (IOException cause) {
            throw new RuntimeException(cause);
        }
    }

    @Override public void flush( BasicLogger receiver) {
        synchronized (receiver) {
            int cursor = myCursor;
            @Var char tmpChar;
            for (int i = cursor; i < length; i++) {
                tmpChar = myCharacters[i];
                if (tmpChar != ASCII.NULL) {
                    receiver.print(tmpChar);
                }
            }
            for (int i = 0; i < cursor; i++) {
                tmpChar = myCharacters[i];
                if (tmpChar != ASCII.NULL) {
                    receiver.print(tmpChar);
                }
            }
            this.clear();
        }
    }

    @Override
    public int hashCode() {
        int prime = 31;
        @Var int result = 1;
        result = (prime * result) + Arrays.hashCode(myCharacters);
        return (prime * result) + myCursor;
    }

    public int indexOfFirst( char c) {

        @Var int retVal = -1;

        char cursor = myCursor;
        for (int i = cursor; (retVal < 0) && (i < length); i++) {
            if (myCharacters[i] == c) {
                retVal = i - cursor;
            }
        }
        for (int i = 0; (retVal < 0) && (i < cursor); i++) {
            if (myCharacters[i] == c) {
                retVal = i + cursor;
            }
        }

        return retVal;
    }

    public int indexOfLast( char c) {

        @Var int retVal = -1;

        char cursor = myCursor;
        for (int i = cursor - 1; (retVal < 0) && (i >= 0); i--) {
            if (myCharacters[i] == c) {
                retVal = i + cursor;
            }
        }
        for (int i = length - 1; (retVal < 0) && (i >= cursor); i--) {
            if (myCharacters[i] == c) {
                retVal = i - cursor;
            }
        }

        return retVal;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public CharSequence subSequence( int start,  int end) {
        return CharBuffer.wrap(this, start, end);
    }

    @Override
    public String toString() {

        char cursor = myCursor;

        String firstPart = String.valueOf(myCharacters, cursor, length - cursor);
        String secondPart = String.valueOf(myCharacters, 0, cursor);

        return firstPart + secondPart;
    }

    char getCursor() {
        return myCursor;
    }

}

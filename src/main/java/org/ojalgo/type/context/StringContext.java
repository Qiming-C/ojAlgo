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
package org.ojalgo.type.context;

import com.google.errorprone.annotations.Var;
import java.text.Format;
import org.ojalgo.ProgrammingError;
import org.ojalgo.type.format.StringFormat;

/**
 * StringContext
 *
 * @author apete
 */
public final class StringContext extends FormatContext<String> {

    private static final Format DEFAULT_FORMAT = new StringFormat();

    private final int myLength;

    public StringContext() {

        super(DEFAULT_FORMAT);

        myLength = 0;
    }

    public StringContext( Format format,  int length) {

        super(format);

        myLength = length;
    }

    public StringContext( int length) {

        super(DEFAULT_FORMAT);

        myLength = length;
    }

    

    @Override
    public String enforce( String object) {

        @Var String retVal = object.trim();
         int tmpLength = retVal.length();

        if ((myLength > 1) && (tmpLength > myLength)) {
            retVal = retVal.substring(0, myLength - 1).trim() + "…";
        }

        return retVal;
    }

    @Override
    protected void configureFormat( Format format,  Object object) {

    }

    @Override
    protected String handleFormatException( Format format,  Object object) {
        return "";
    }

    @Override
    protected String handleParseException( Format format,  String string) {
        return "";
    }

}

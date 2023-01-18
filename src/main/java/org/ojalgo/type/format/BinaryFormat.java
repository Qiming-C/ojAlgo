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
package org.ojalgo.type.format;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class BinaryFormat extends Format {

    private static final long serialVersionUID = 1L;

    public BinaryFormat() {
        super();
    }

    @Override
    public StringBuffer format( Object someObj,  StringBuffer aBufferToAppendTo,  FieldPosition aPosition) {
        return aBufferToAppendTo.append(new String((byte[]) someObj, UTF_8));
    }

    @Override
    public byte[] parseObject( String someSource,  ParsePosition somePos) {
        return someSource.getBytes(UTF_8);
    }

}

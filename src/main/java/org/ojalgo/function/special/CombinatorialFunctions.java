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
package org.ojalgo.function.special;

import com.google.errorprone.annotations.Var;

/**
 * https://reference.wolfram.com/language/tutorial/CombinatorialFunctions.html
 *
 * @author apete
 */
public abstract class CombinatorialFunctions {

    /**
     *Returns the number of ways the set can be partitioned in to subsets of the given sizes.
 @param n The number of elements in the set
     * @param k A vector of subset sizes the sum of which must equal the size of the full set
     * 
     */
    public static long partitions( int n,  int[] k) {
        @Var double retVal = MissingMath.factorial(n);
        for (int i = 0, limit = k.length; i < limit; i++) {
            retVal /= MissingMath.factorial(k[i]);
        }
        return Math.round(retVal);
    }

    /**
     *Returns the number of permutations of the set.
 @param n The number of elements in the set
     * 
     */
    public static long permutations( int n) {
        return Math.round(MissingMath.factorial(n));
    }

    /**
     *Returns the number of subsets to the set.
 @param n The number of elements in the set
     * @param k The number of elements in the subset
     * 
     */
    public static long subsets( int n,  int k) {
        return Math.round(MissingMath.factorial(n) / (MissingMath.factorial(k) * MissingMath.factorial(n - k)));
    }

    /**
     *Returns the number of ordered k-tuples (variations) of the set.
 @param n The number of elements in the set
     * @param k The size of the tuple
     * 
     */
    public static long variations( int n,  int k) {
        return Math.round(MissingMath.factorial(n) / MissingMath.factorial(n - k));
    }

}

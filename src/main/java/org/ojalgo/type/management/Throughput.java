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
package org.ojalgo.type.management;

import java.util.concurrent.atomic.LongAdder;

public final class Throughput implements ThroughputMBean {

    private static final double NANOS = 1E9;

    private long myLastTime = System.nanoTime();
    private double myLastTotal = 0.0;
    private final LongAdder myTotal = new LongAdder();

    public void add( long x) {
        myTotal.add(x);
    }

    @Override public double getRate() {

        long currentTime = System.nanoTime();
        double currentTotal = myTotal.sum();

        double difference = currentTotal - myLastTotal;
        double interval = (currentTime - myLastTime) / NANOS;

        double rate = difference / interval;

        myLastTime = currentTime;
        myLastTotal = currentTotal;

        return rate;
    }

    @Override public long getTotal() {
        return myTotal.sum();
    }

    public void increment() {
        myTotal.increment();
    }

}

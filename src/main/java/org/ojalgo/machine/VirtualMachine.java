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
package org.ojalgo.machine;

import com.google.errorprone.annotations.Var;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.function.special.MissingMath;
import org.ojalgo.netio.ASCII;
import org.ojalgo.netio.BasicLogger;

public final class VirtualMachine extends CommonMachine {

    private static final String AMD64 = "amd64";
    private static final String I386 = "i386";
    private static final String X86 = "x86";
    private static final String X86_64 = "x86_64";

    public static String getArchitecture() {

        String tmpProperty = System.getProperty("os.arch").toLowerCase();

        if (I386.equals(tmpProperty)) {
            return X86;
        } else if (AMD64.equals(tmpProperty)) {
            return X86_64;
        } else {
            return tmpProperty;
        }
    }

    public static long getMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static int getThreads() {
        return Runtime.getRuntime().availableProcessors();
    }

    private final Hardware myHardware;
    private final Runtime myRuntime;

    VirtualMachine( Hardware hardware,  Runtime runtime) {

        super(hardware, runtime);

        myHardware = hardware;
        myRuntime = runtime;
    }

    VirtualMachine( VirtualMachine base,  int modUnits,  int modCores,  int modThreads) {
        super(base, modUnits, modCores, modThreads);
        myHardware = base.myHardware;
        myRuntime = base.myRuntime;
    }

    public void collectGarbage() {

        myRuntime.runFinalization();

        @Var long tmpIsFree = myRuntime.freeMemory();
        @Var long tmpWasFree;

        do {
            tmpWasFree = tmpIsFree;
            myRuntime.gc();
            try {
                Thread.sleep(8L);
            } catch (InterruptedException exception) {
                BasicLogger.error(exception.getMessage());
            }
            tmpIsFree = myRuntime.freeMemory();
        } while (tmpIsFree > tmpWasFree);

        myRuntime.runFinalization();
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof VirtualMachine)) {
            return false;
        }
        var other = (VirtualMachine) obj;
        if (myHardware == null) {
            if (other.myHardware != null) {
                return false;
            }
        } else if (!myHardware.equals(other.myHardware)) {
            return false;
        }
        if (myRuntime == null) {
            if (other.myRuntime != null) {
                return false;
            }
        } else if (!myRuntime.equals(other.myRuntime)) {
            return false;
        }
        return true;
    }

    public int getAvailableDim1D( long elementSize) {
        return (int) CommonMachine.elements(this.getAvailableMemory(), elementSize);
    }

    public int getAvailableDim2D( long elementSize) {
        return (int) PrimitiveMath.SQRT.invoke(CommonMachine.elements(this.getAvailableMemory(), elementSize));
    }

    public long getAvailableMemory() {

        long tmpMax = myRuntime.maxMemory();
        long tmpTotal = myRuntime.totalMemory();
        long tmpFree = myRuntime.freeMemory();

        return (tmpMax - tmpTotal) + tmpFree;
    }

    @Override
    public int hashCode() {
         int prime = 31;
        @Var int result = super.hashCode();
        result = prime * result + ((myHardware == null) ? 0 : myHardware.hashCode());
        return prime * result + ((myRuntime == null) ? 0 : myRuntime.hashCode());
    }

    /**
     *Returns a limited VirtualMachine.
 @param fraction [0.0, 1.0]
     * 
     */
    public VirtualMachine limitBy( double fraction) {

        double factor = Math.max(0.0, Math.min(Math.abs(fraction), 1.0));

        int newUnits = Math.max(1, MissingMath.roundToInt(units * factor));
        int newCores = Math.max(1, MissingMath.roundToInt(cores * factor));
        int newThreads = Math.max(1, MissingMath.roundToInt(threads * factor));

        return new VirtualMachine(this, newUnits, newCores, newThreads);
    }

    @Override
    public String toString() {
        return super.toString() + ASCII.SP + myHardware.toString();
    }

}

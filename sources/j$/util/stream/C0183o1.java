package j$.util.stream;

import java.util.Collections;
import java.util.EnumSet;

/* renamed from: j$.util.stream.o1  reason: case insensitive filesystem */
public final class CLASSNAMEo1 {
    static {
        CLASSNAMEm1 m1Var = CLASSNAMEm1.CONCURRENT;
        CLASSNAMEm1 m1Var2 = CLASSNAMEm1.UNORDERED;
        CLASSNAMEm1 m1Var3 = CLASSNAMEm1.IDENTITY_FINISH;
        Collections.unmodifiableSet(EnumSet.of(m1Var, m1Var2, m1Var3));
        Collections.unmodifiableSet(EnumSet.of(m1Var, m1Var2));
        Collections.unmodifiableSet(EnumSet.of(m1Var3));
        Collections.unmodifiableSet(EnumSet.of(m1Var2, m1Var3));
        Collections.emptySet();
    }

    static double a(double[] dArr) {
        double d = dArr[0] + dArr[1];
        double d2 = dArr[dArr.length - 1];
        return (!Double.isNaN(d) || !Double.isInfinite(d2)) ? d : d2;
    }

    static double[] b(double[] dArr, double d) {
        double d2 = d - dArr[1];
        double d3 = dArr[0];
        double d4 = d3 + d2;
        dArr[1] = (d4 - d3) - d2;
        dArr[0] = d4;
        return dArr;
    }
}

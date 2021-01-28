package j$.util.stream;

import j$.util.stream.CLASSNAMEm1;
import java.util.Collections;
import java.util.EnumSet;

/* renamed from: j$.util.stream.n1  reason: case insensitive filesystem */
public final class CLASSNAMEn1 {
    static {
        CLASSNAMEm1.a aVar = CLASSNAMEm1.a.CONCURRENT;
        CLASSNAMEm1.a aVar2 = CLASSNAMEm1.a.UNORDERED;
        CLASSNAMEm1.a aVar3 = CLASSNAMEm1.a.IDENTITY_FINISH;
        Collections.unmodifiableSet(EnumSet.of(aVar, aVar2, aVar3));
        Collections.unmodifiableSet(EnumSet.of(aVar, aVar2));
        Collections.unmodifiableSet(EnumSet.of(aVar3));
        Collections.unmodifiableSet(EnumSet.of(aVar2, aVar3));
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

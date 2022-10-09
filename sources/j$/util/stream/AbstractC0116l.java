package j$.util.stream;

import java.util.Collections;
import java.util.EnumSet;
/* renamed from: j$.util.stream.l  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public abstract class AbstractCLASSNAMEl {
    static {
        EnumCLASSNAMEh enumCLASSNAMEh = EnumCLASSNAMEh.CONCURRENT;
        EnumCLASSNAMEh enumCLASSNAMEh2 = EnumCLASSNAMEh.UNORDERED;
        EnumCLASSNAMEh enumCLASSNAMEh3 = EnumCLASSNAMEh.IDENTITY_FINISH;
        Collections.unmodifiableSet(EnumSet.of(enumCLASSNAMEh, enumCLASSNAMEh2, enumCLASSNAMEh3));
        Collections.unmodifiableSet(EnumSet.of(enumCLASSNAMEh, enumCLASSNAMEh2));
        Collections.unmodifiableSet(EnumSet.of(enumCLASSNAMEh3));
        Collections.unmodifiableSet(EnumSet.of(enumCLASSNAMEh2, enumCLASSNAMEh3));
        Collections.emptySet();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static double a(double[] dArr) {
        double d = dArr[0] + dArr[1];
        double d2 = dArr[dArr.length - 1];
        return (!Double.isNaN(d) || !Double.isInfinite(d2)) ? d : d2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static double[] b(double[] dArr, double d) {
        double d2 = d - dArr[1];
        double d3 = dArr[0];
        double d4 = d3 + d2;
        dArr[1] = (d4 - d3) - d2;
        dArr[0] = d4;
        return dArr;
    }
}

package j$.wrappers;

import java.util.function.LongToDoubleFunction;
/* renamed from: j$.wrappers.l0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEl0 {
    final /* synthetic */ LongToDoubleFunction a;

    private /* synthetic */ CLASSNAMEl0(LongToDoubleFunction longToDoubleFunction) {
        this.a = longToDoubleFunction;
    }

    public static /* synthetic */ CLASSNAMEl0 b(LongToDoubleFunction longToDoubleFunction) {
        if (longToDoubleFunction == null) {
            return null;
        }
        return longToDoubleFunction instanceof AbstractCLASSNAMEm0 ? ((AbstractCLASSNAMEm0) longToDoubleFunction).a : new CLASSNAMEl0(longToDoubleFunction);
    }

    public double a(long j) {
        return this.a.applyAsDouble(j);
    }
}

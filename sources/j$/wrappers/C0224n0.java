package j$.wrappers;

import java.util.function.LongToIntFunction;
/* renamed from: j$.wrappers.n0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEn0 {
    final /* synthetic */ LongToIntFunction a;

    private /* synthetic */ CLASSNAMEn0(LongToIntFunction longToIntFunction) {
        this.a = longToIntFunction;
    }

    public static /* synthetic */ CLASSNAMEn0 b(LongToIntFunction longToIntFunction) {
        if (longToIntFunction == null) {
            return null;
        }
        return longToIntFunction instanceof AbstractCLASSNAMEo0 ? ((AbstractCLASSNAMEo0) longToIntFunction).a : new CLASSNAMEn0(longToIntFunction);
    }

    public int a(long j) {
        return this.a.applyAsInt(j);
    }
}

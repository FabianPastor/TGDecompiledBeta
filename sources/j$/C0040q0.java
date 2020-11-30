package j$;

import java.util.function.LongToIntFunction;

/* renamed from: j$.q0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEq0 {
    final /* synthetic */ LongToIntFunction a;

    private /* synthetic */ CLASSNAMEq0(LongToIntFunction longToIntFunction) {
        this.a = longToIntFunction;
    }

    public static /* synthetic */ CLASSNAMEq0 b(LongToIntFunction longToIntFunction) {
        if (longToIntFunction == null) {
            return null;
        }
        return longToIntFunction instanceof CLASSNAMEr0 ? ((CLASSNAMEr0) longToIntFunction).a : new CLASSNAMEq0(longToIntFunction);
    }

    public int a(long j) {
        return this.a.applyAsInt(j);
    }
}

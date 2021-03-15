package a;

import java.util.function.LongToIntFunction;

/* renamed from: a.q0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEq0 {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LongToIntFunction var_a;

    private /* synthetic */ CLASSNAMEq0(LongToIntFunction longToIntFunction) {
        this.var_a = longToIntFunction;
    }

    public static /* synthetic */ CLASSNAMEq0 b(LongToIntFunction longToIntFunction) {
        if (longToIntFunction == null) {
            return null;
        }
        return longToIntFunction instanceof CLASSNAMEr0 ? ((CLASSNAMEr0) longToIntFunction).var_a : new CLASSNAMEq0(longToIntFunction);
    }

    public int a(long j) {
        return this.var_a.applyAsInt(j);
    }
}

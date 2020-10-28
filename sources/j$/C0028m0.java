package j$;

import java.util.function.LongPredicate;

/* renamed from: j$.m0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm0 {
    final /* synthetic */ LongPredicate a;

    private /* synthetic */ CLASSNAMEm0(LongPredicate longPredicate) {
        this.a = longPredicate;
    }

    public static /* synthetic */ CLASSNAMEm0 a(LongPredicate longPredicate) {
        if (longPredicate == null) {
            return null;
        }
        return longPredicate instanceof CLASSNAMEn0 ? ((CLASSNAMEn0) longPredicate).a : new CLASSNAMEm0(longPredicate);
    }

    public boolean b(long j) {
        return this.a.test(j);
    }
}

package j$;

import j$.util.function.E;
import java.util.function.LongPredicate;

/* renamed from: j$.m0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm0 implements E {
    final /* synthetic */ LongPredicate a;

    private /* synthetic */ CLASSNAMEm0(LongPredicate longPredicate) {
        this.a = longPredicate;
    }

    public static /* synthetic */ E a(LongPredicate longPredicate) {
        if (longPredicate == null) {
            return null;
        }
        return longPredicate instanceof CLASSNAMEn0 ? ((CLASSNAMEn0) longPredicate).a : new CLASSNAMEm0(longPredicate);
    }

    public /* synthetic */ boolean b(long j) {
        return this.a.test(j);
    }
}

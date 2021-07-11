package j$;

import j$.util.function.E;
import java.util.function.LongPredicate;

/* renamed from: j$.j0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj0 implements E {
    final /* synthetic */ LongPredicate a;

    private /* synthetic */ CLASSNAMEj0(LongPredicate longPredicate) {
        this.a = longPredicate;
    }

    public static /* synthetic */ E a(LongPredicate longPredicate) {
        if (longPredicate == null) {
            return null;
        }
        return longPredicate instanceof CLASSNAMEk0 ? ((CLASSNAMEk0) longPredicate).a : new CLASSNAMEj0(longPredicate);
    }

    public /* synthetic */ boolean b(long j) {
        return this.a.test(j);
    }
}

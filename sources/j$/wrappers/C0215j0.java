package j$.wrappers;

import java.util.function.LongPredicate;
/* renamed from: j$.wrappers.j0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEj0 {
    final /* synthetic */ LongPredicate a;

    private /* synthetic */ CLASSNAMEj0(LongPredicate longPredicate) {
        this.a = longPredicate;
    }

    public static /* synthetic */ CLASSNAMEj0 a(LongPredicate longPredicate) {
        if (longPredicate == null) {
            return null;
        }
        return longPredicate instanceof AbstractCLASSNAMEk0 ? ((AbstractCLASSNAMEk0) longPredicate).a : new CLASSNAMEj0(longPredicate);
    }

    public boolean b(long j) {
        return this.a.test(j);
    }
}

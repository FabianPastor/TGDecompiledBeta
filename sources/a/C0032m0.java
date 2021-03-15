package a;

import j$.util.function.E;
import java.util.function.LongPredicate;

/* renamed from: a.m0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm0 implements E {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LongPredicate var_a;

    private /* synthetic */ CLASSNAMEm0(LongPredicate longPredicate) {
        this.var_a = longPredicate;
    }

    public static /* synthetic */ E a(LongPredicate longPredicate) {
        if (longPredicate == null) {
            return null;
        }
        return longPredicate instanceof CLASSNAMEn0 ? ((CLASSNAMEn0) longPredicate).var_a : new CLASSNAMEm0(longPredicate);
    }

    public /* synthetic */ boolean b(long j) {
        return this.var_a.test(j);
    }
}

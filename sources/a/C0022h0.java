package a;

import j$.util.function.B;
import java.util.function.LongBinaryOperator;

/* renamed from: a.h0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh0 implements LongBinaryOperator {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ B var_a;

    private /* synthetic */ CLASSNAMEh0(B b) {
        this.var_a = b;
    }

    public static /* synthetic */ LongBinaryOperator a(B b) {
        if (b == null) {
            return null;
        }
        return b instanceof CLASSNAMEg0 ? ((CLASSNAMEg0) b).var_a : new CLASSNAMEh0(b);
    }

    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.var_a.applyAsLong(j, j2);
    }
}

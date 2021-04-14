package a;

import j$.util.function.F;
import java.util.function.LongUnaryOperator;

/* renamed from: a.t0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEt0 implements LongUnaryOperator {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ F var_a;

    private /* synthetic */ CLASSNAMEt0(F f) {
        this.var_a = f;
    }

    public static /* synthetic */ LongUnaryOperator a(F f) {
        if (f == null) {
            return null;
        }
        return f instanceof CLASSNAMEs0 ? ((CLASSNAMEs0) f).var_a : new CLASSNAMEt0(f);
    }

    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return a(this.var_a.a(CLASSNAMEs0.c(longUnaryOperator)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.var_a.applyAsLong(j);
    }

    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return a(this.var_a.b(CLASSNAMEs0.c(longUnaryOperator)));
    }
}

package a;

import j$.util.function.F;
import java.util.function.LongUnaryOperator;

/* renamed from: a.s0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs0 implements F {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LongUnaryOperator var_a;

    private /* synthetic */ CLASSNAMEs0(LongUnaryOperator longUnaryOperator) {
        this.var_a = longUnaryOperator;
    }

    public static /* synthetic */ F c(LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof CLASSNAMEt0 ? ((CLASSNAMEt0) longUnaryOperator).var_a : new CLASSNAMEs0(longUnaryOperator);
    }

    public /* synthetic */ F a(F f) {
        return c(this.var_a.andThen(CLASSNAMEt0.a(f)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.var_a.applyAsLong(j);
    }

    public /* synthetic */ F b(F f) {
        return c(this.var_a.compose(CLASSNAMEt0.a(f)));
    }
}

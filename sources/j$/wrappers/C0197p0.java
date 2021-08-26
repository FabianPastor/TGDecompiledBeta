package j$.wrappers;

import j$.util.function.s;
import java.util.function.LongUnaryOperator;

/* renamed from: j$.wrappers.p0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp0 implements s {
    final /* synthetic */ LongUnaryOperator a;

    private /* synthetic */ CLASSNAMEp0(LongUnaryOperator longUnaryOperator) {
        this.a = longUnaryOperator;
    }

    public static /* synthetic */ s c(LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof CLASSNAMEq0 ? ((CLASSNAMEq0) longUnaryOperator).a : new CLASSNAMEp0(longUnaryOperator);
    }

    public /* synthetic */ s a(s sVar) {
        return c(this.a.andThen(CLASSNAMEq0.a(sVar)));
    }

    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    public /* synthetic */ s b(s sVar) {
        return c(this.a.compose(CLASSNAMEq0.a(sVar)));
    }
}

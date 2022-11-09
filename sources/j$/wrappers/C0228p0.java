package j$.wrappers;

import java.util.function.LongUnaryOperator;
/* renamed from: j$.wrappers.p0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEp0 implements j$.util.function.t {
    final /* synthetic */ LongUnaryOperator a;

    private /* synthetic */ CLASSNAMEp0(LongUnaryOperator longUnaryOperator) {
        this.a = longUnaryOperator;
    }

    public static /* synthetic */ j$.util.function.t c(LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof q0 ? ((q0) longUnaryOperator).a : new CLASSNAMEp0(longUnaryOperator);
    }

    @Override // j$.util.function.t
    public /* synthetic */ j$.util.function.t a(j$.util.function.t tVar) {
        return c(this.a.andThen(q0.a(tVar)));
    }

    @Override // j$.util.function.t
    public /* synthetic */ long applyAsLong(long j) {
        return this.a.applyAsLong(j);
    }

    @Override // j$.util.function.t
    public /* synthetic */ j$.util.function.t b(j$.util.function.t tVar) {
        return c(this.a.compose(q0.a(tVar)));
    }
}

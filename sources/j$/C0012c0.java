package j$;

import j$.util.function.A;
import java.util.function.IntUnaryOperator;

/* renamed from: j$.c0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEc0 implements IntUnaryOperator {
    final /* synthetic */ A a;

    private /* synthetic */ CLASSNAMEc0(A a2) {
        this.a = a2;
    }

    public static /* synthetic */ IntUnaryOperator a(A a2) {
        if (a2 == null) {
            return null;
        }
        return a2 instanceof CLASSNAMEb0 ? ((CLASSNAMEb0) a2).a : new CLASSNAMEc0(a2);
    }

    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return a(CLASSNAMEb0.b(((CLASSNAMEb0) this.a).a.andThen(a(CLASSNAMEb0.b(intUnaryOperator)))));
    }

    public /* synthetic */ int applyAsInt(int i) {
        return ((CLASSNAMEb0) this.a).a.applyAsInt(i);
    }

    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return a(CLASSNAMEb0.b(((CLASSNAMEb0) this.a).a.compose(a(CLASSNAMEb0.b(intUnaryOperator)))));
    }
}

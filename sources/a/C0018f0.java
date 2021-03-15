package a;

import j$.util.function.A;
import java.util.function.IntUnaryOperator;

/* renamed from: a.f0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf0 implements IntUnaryOperator {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ A var_a;

    private /* synthetic */ CLASSNAMEf0(A a2) {
        this.var_a = a2;
    }

    public static /* synthetic */ IntUnaryOperator a(A a2) {
        if (a2 == null) {
            return null;
        }
        return a2 instanceof CLASSNAMEe0 ? ((CLASSNAMEe0) a2).var_a : new CLASSNAMEf0(a2);
    }

    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return a(CLASSNAMEe0.b(((CLASSNAMEe0) this.var_a).var_a.andThen(a(CLASSNAMEe0.b(intUnaryOperator)))));
    }

    public /* synthetic */ int applyAsInt(int i) {
        return ((CLASSNAMEe0) this.var_a).var_a.applyAsInt(i);
    }

    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return a(CLASSNAMEe0.b(((CLASSNAMEe0) this.var_a).var_a.compose(a(CLASSNAMEe0.b(intUnaryOperator)))));
    }
}

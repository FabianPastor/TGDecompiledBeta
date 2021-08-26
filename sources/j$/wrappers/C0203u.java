package j$.wrappers;

import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Function;
import java.util.function.BinaryOperator;

/* renamed from: j$.wrappers.u  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu implements CLASSNAMEb {
    final /* synthetic */ BinaryOperator a;

    private /* synthetic */ CLASSNAMEu(BinaryOperator binaryOperator) {
        this.a = binaryOperator;
    }

    public static /* synthetic */ CLASSNAMEb a(BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return binaryOperator instanceof CLASSNAMEv ? ((CLASSNAMEv) binaryOperator).a : new CLASSNAMEu(binaryOperator);
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }

    public /* synthetic */ BiFunction b(Function function) {
        return CLASSNAMEs.a(this.a.andThen(N.a(function)));
    }
}

package j$.wrappers;

import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.b;
import java.util.function.BinaryOperator;

/* renamed from: j$.wrappers.u  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu implements b {
    final /* synthetic */ BinaryOperator a;

    private /* synthetic */ CLASSNAMEu(BinaryOperator binaryOperator) {
        this.a = binaryOperator;
    }

    public static /* synthetic */ b a(BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return binaryOperator instanceof CLASSNAMEv ? ((CLASSNAMEv) binaryOperator).a : new CLASSNAMEu(binaryOperator);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return CLASSNAMEs.a(this.a.andThen(N.a(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

package j$;

import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.n;
import java.util.function.BinaryOperator;

/* renamed from: j$.u  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu implements n {
    final /* synthetic */ BinaryOperator a;

    private /* synthetic */ CLASSNAMEu(BinaryOperator binaryOperator) {
        this.a = binaryOperator;
    }

    public static /* synthetic */ n b(BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return binaryOperator instanceof CLASSNAMEv ? ((CLASSNAMEv) binaryOperator).a : new CLASSNAMEu(binaryOperator);
    }

    public /* synthetic */ BiFunction a(Function function) {
        return CLASSNAMEs.b(this.a.andThen(N.a(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

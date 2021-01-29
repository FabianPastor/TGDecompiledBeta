package j$;

import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.n;
import java.util.function.BinaryOperator;

/* renamed from: j$.x  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEx implements n {
    final /* synthetic */ BinaryOperator a;

    private /* synthetic */ CLASSNAMEx(BinaryOperator binaryOperator) {
        this.a = binaryOperator;
    }

    public static /* synthetic */ n b(BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return binaryOperator instanceof CLASSNAMEy ? ((CLASSNAMEy) binaryOperator).a : new CLASSNAMEx(binaryOperator);
    }

    public /* synthetic */ BiFunction a(Function function) {
        return CLASSNAMEv.b(this.a.andThen(Q.a(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

package a;

import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.n;
import java.util.function.BinaryOperator;

/* renamed from: a.x  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEx implements n {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BinaryOperator var_a;

    private /* synthetic */ CLASSNAMEx(BinaryOperator binaryOperator) {
        this.var_a = binaryOperator;
    }

    public static /* synthetic */ n b(BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return binaryOperator instanceof CLASSNAMEy ? ((CLASSNAMEy) binaryOperator).var_a : new CLASSNAMEx(binaryOperator);
    }

    public /* synthetic */ BiFunction a(Function function) {
        return CLASSNAMEv.b(this.var_a.andThen(Q.a(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.var_a.apply(obj, obj2);
    }
}

package j$;

import j$.util.function.n;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/* renamed from: j$.y  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEy implements BinaryOperator {
    final /* synthetic */ n a;

    private /* synthetic */ CLASSNAMEy(n nVar) {
        this.a = nVar;
    }

    public static /* synthetic */ BinaryOperator a(n nVar) {
        if (nVar == null) {
            return null;
        }
        return nVar instanceof CLASSNAMEx ? ((CLASSNAMEx) nVar).a : new CLASSNAMEy(nVar);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return CLASSNAMEw.a(this.a.a(P.c(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

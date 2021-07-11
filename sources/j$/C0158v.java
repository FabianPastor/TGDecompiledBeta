package j$;

import j$.util.function.n;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/* renamed from: j$.v  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv implements BinaryOperator {
    final /* synthetic */ n a;

    private /* synthetic */ CLASSNAMEv(n nVar) {
        this.a = nVar;
    }

    public static /* synthetic */ BinaryOperator a(n nVar) {
        if (nVar == null) {
            return null;
        }
        return nVar instanceof CLASSNAMEu ? ((CLASSNAMEu) nVar).a : new CLASSNAMEv(nVar);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return CLASSNAMEt.a(this.a.a(M.c(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

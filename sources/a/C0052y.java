package a;

import j$.util.function.n;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/* renamed from: a.y  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEy implements BinaryOperator {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n var_a;

    private /* synthetic */ CLASSNAMEy(n nVar) {
        this.var_a = nVar;
    }

    public static /* synthetic */ BinaryOperator a(n nVar) {
        if (nVar == null) {
            return null;
        }
        return nVar instanceof CLASSNAMEx ? ((CLASSNAMEx) nVar).var_a : new CLASSNAMEy(nVar);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return CLASSNAMEw.a(this.var_a.a(P.c(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.var_a.apply(obj, obj2);
    }
}

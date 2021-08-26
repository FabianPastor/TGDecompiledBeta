package j$.wrappers;

import j$.util.function.CLASSNAMEb;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/* renamed from: j$.wrappers.v  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv implements BinaryOperator {
    final /* synthetic */ CLASSNAMEb a;

    private /* synthetic */ CLASSNAMEv(CLASSNAMEb bVar) {
        this.a = bVar;
    }

    public static /* synthetic */ BinaryOperator a(CLASSNAMEb bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar instanceof CLASSNAMEu ? ((CLASSNAMEu) bVar).a : new CLASSNAMEv(bVar);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return CLASSNAMEt.a(this.a.b(M.c(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

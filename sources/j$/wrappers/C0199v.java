package j$.wrappers;

import j$.util.function.b;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/* renamed from: j$.wrappers.v  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv implements BinaryOperator {
    final /* synthetic */ b a;

    private /* synthetic */ CLASSNAMEv(b bVar) {
        this.a = bVar;
    }

    public static /* synthetic */ BinaryOperator a(b bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar instanceof CLASSNAMEu ? ((CLASSNAMEu) bVar).a : new CLASSNAMEv(bVar);
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return CLASSNAMEt.a(this.a.andThen(M.a(function)));
    }

    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

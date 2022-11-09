package j$.wrappers;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
/* renamed from: j$.wrappers.v  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEv implements BinaryOperator {
    final /* synthetic */ j$.util.function.b a;

    private /* synthetic */ CLASSNAMEv(j$.util.function.b bVar) {
        this.a = bVar;
    }

    public static /* synthetic */ BinaryOperator a(j$.util.function.b bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar instanceof CLASSNAMEu ? ((CLASSNAMEu) bVar).a : new CLASSNAMEv(bVar);
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return CLASSNAMEt.a(this.a.andThen(M.a(function)));
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

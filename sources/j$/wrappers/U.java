package j$.wrappers;

import j$.util.function.l;
import java.util.function.IntFunction;

public final /* synthetic */ class U implements IntFunction {
    final /* synthetic */ l a;

    private /* synthetic */ U(l lVar) {
        this.a = lVar;
    }

    public static /* synthetic */ IntFunction a(l lVar) {
        if (lVar == null) {
            return null;
        }
        return lVar instanceof T ? ((T) lVar).a : new U(lVar);
    }

    public /* synthetic */ Object apply(int i) {
        return this.a.apply(i);
    }
}

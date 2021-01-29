package j$;

import j$.util.function.x;
import java.util.function.IntFunction;

public final /* synthetic */ class X implements IntFunction {
    final /* synthetic */ x a;

    private /* synthetic */ X(x xVar) {
        this.a = xVar;
    }

    public static /* synthetic */ IntFunction a(x xVar) {
        if (xVar == null) {
            return null;
        }
        return xVar instanceof W ? ((W) xVar).a : new X(xVar);
    }

    public /* synthetic */ Object apply(int i) {
        return this.a.apply(i);
    }
}

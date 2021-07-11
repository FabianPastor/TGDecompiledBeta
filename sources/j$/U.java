package j$;

import j$.util.function.x;
import java.util.function.IntFunction;

public final /* synthetic */ class U implements IntFunction {
    final /* synthetic */ x a;

    private /* synthetic */ U(x xVar) {
        this.a = xVar;
    }

    public static /* synthetic */ IntFunction a(x xVar) {
        if (xVar == null) {
            return null;
        }
        return xVar instanceof T ? ((T) xVar).a : new U(xVar);
    }

    public /* synthetic */ Object apply(int i) {
        return this.a.apply(i);
    }
}

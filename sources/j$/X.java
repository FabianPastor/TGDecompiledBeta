package j$;

import j$.util.function.v;
import java.util.function.IntFunction;

public final /* synthetic */ class X implements IntFunction {
    final /* synthetic */ v a;

    private /* synthetic */ X(v vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ IntFunction a(v vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof W ? ((W) vVar).a : new X(vVar);
    }

    public /* synthetic */ Object apply(int i) {
        return this.a.apply(i);
    }
}

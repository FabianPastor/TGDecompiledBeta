package j$.wrappers;

import j$.util.function.x;
import java.util.function.Predicate;

public final /* synthetic */ class y0 implements Predicate {
    final /* synthetic */ x a;

    private /* synthetic */ y0(x xVar) {
        this.a = xVar;
    }

    public static /* synthetic */ Predicate a(x xVar) {
        if (xVar == null) {
            return null;
        }
        return xVar instanceof x0 ? ((x0) xVar).a : new y0(xVar);
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        return a(this.a.c(x0.b(predicate)));
    }

    public /* synthetic */ Predicate negate() {
        return a(this.a.negate());
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        return a(this.a.a(x0.b(predicate)));
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.a.test(obj);
    }
}

package j$.wrappers;

import j$.util.function.y;
import java.util.function.Predicate;

public final /* synthetic */ class y0 implements Predicate {
    final /* synthetic */ y a;

    private /* synthetic */ y0(y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ Predicate a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof x0 ? ((x0) yVar).a : new y0(yVar);
    }

    public /* synthetic */ Predicate and(Predicate predicate) {
        return a(this.a.b(x0.c(predicate)));
    }

    public /* synthetic */ Predicate negate() {
        return a(this.a.negate());
    }

    public /* synthetic */ Predicate or(Predicate predicate) {
        return a(this.a.a(x0.c(predicate)));
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.a.test(obj);
    }
}

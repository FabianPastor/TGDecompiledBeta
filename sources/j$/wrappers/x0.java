package j$.wrappers;

import j$.util.function.x;
import java.util.function.Predicate;

public final /* synthetic */ class x0 implements x {
    final /* synthetic */ Predicate a;

    private /* synthetic */ x0(Predicate predicate) {
        this.a = predicate;
    }

    public static /* synthetic */ x b(Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate instanceof y0 ? ((y0) predicate).a : new x0(predicate);
    }

    public /* synthetic */ x a(x xVar) {
        return b(this.a.or(y0.a(xVar)));
    }

    public /* synthetic */ x c(x xVar) {
        return b(this.a.and(y0.a(xVar)));
    }

    public /* synthetic */ x negate() {
        return b(this.a.negate());
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.a.test(obj);
    }
}

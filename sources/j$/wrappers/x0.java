package j$.wrappers;

import j$.util.function.y;
import java.util.function.Predicate;

public final /* synthetic */ class x0 implements y {
    final /* synthetic */ Predicate a;

    private /* synthetic */ x0(Predicate predicate) {
        this.a = predicate;
    }

    public static /* synthetic */ y c(Predicate predicate) {
        if (predicate == null) {
            return null;
        }
        return predicate instanceof y0 ? ((y0) predicate).a : new x0(predicate);
    }

    public /* synthetic */ y a(y yVar) {
        return c(this.a.or(y0.a(yVar)));
    }

    public /* synthetic */ y b(y yVar) {
        return c(this.a.and(y0.a(yVar)));
    }

    public /* synthetic */ y negate() {
        return c(this.a.negate());
    }

    public /* synthetic */ boolean test(Object obj) {
        return this.a.test(obj);
    }
}

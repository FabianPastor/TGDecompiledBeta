package j$;

import j$.util.function.E;
import java.util.function.Supplier;

public final /* synthetic */ class D0 implements Supplier {
    final /* synthetic */ E a;

    private /* synthetic */ D0(E e) {
        this.a = e;
    }

    public static /* synthetic */ Supplier a(E e) {
        if (e == null) {
            return null;
        }
        return e instanceof C0 ? ((C0) e).a : new D0(e);
    }

    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

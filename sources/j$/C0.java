package j$;

import j$.util.function.J;
import java.util.function.Supplier;

public final /* synthetic */ class C0 implements J {
    final /* synthetic */ Supplier a;

    private /* synthetic */ C0(Supplier supplier) {
        this.a = supplier;
    }

    public static /* synthetic */ J a(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier instanceof D0 ? ((D0) supplier).a : new C0(supplier);
    }

    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

package j$;

import j$.util.function.J;
import java.util.function.Supplier;

public final /* synthetic */ class z0 implements J {
    final /* synthetic */ Supplier a;

    private /* synthetic */ z0(Supplier supplier) {
        this.a = supplier;
    }

    public static /* synthetic */ J a(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier instanceof A0 ? ((A0) supplier).a : new z0(supplier);
    }

    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

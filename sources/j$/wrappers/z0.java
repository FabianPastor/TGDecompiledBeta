package j$.wrappers;

import j$.util.function.y;
import java.util.function.Supplier;

public final /* synthetic */ class z0 implements y {
    final /* synthetic */ Supplier a;

    private /* synthetic */ z0(Supplier supplier) {
        this.a = supplier;
    }

    public static /* synthetic */ y a(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier instanceof A0 ? ((A0) supplier).a : new z0(supplier);
    }

    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

package j$;

import j$.util.function.V;
import java.util.function.Supplier;

public final /* synthetic */ class f0 implements V {
    final /* synthetic */ Supplier a;

    private /* synthetic */ f0(Supplier supplier) {
        this.a = supplier;
    }

    public static /* synthetic */ V a(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return new f0(supplier);
    }

    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

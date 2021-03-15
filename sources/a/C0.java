package a;

import j$.util.function.J;
import java.util.function.Supplier;

public final /* synthetic */ class C0 implements J {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Supplier var_a;

    private /* synthetic */ C0(Supplier supplier) {
        this.var_a = supplier;
    }

    public static /* synthetic */ J a(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier instanceof D0 ? ((D0) supplier).var_a : new C0(supplier);
    }

    public /* synthetic */ Object get() {
        return this.var_a.get();
    }
}

package j$.wrappers;

import java.util.function.Supplier;
/* loaded from: classes2.dex */
public final /* synthetic */ class z0 implements j$.util.function.y {
    final /* synthetic */ Supplier a;

    private /* synthetic */ z0(Supplier supplier) {
        this.a = supplier;
    }

    public static /* synthetic */ j$.util.function.y a(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier instanceof A0 ? ((A0) supplier).a : new z0(supplier);
    }

    @Override // j$.util.function.y
    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

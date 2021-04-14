package a;

import j$.util.function.J;
import java.util.function.Supplier;

public final /* synthetic */ class D0 implements Supplier {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ J var_a;

    private /* synthetic */ D0(J j) {
        this.var_a = j;
    }

    public static /* synthetic */ Supplier a(J j) {
        if (j == null) {
            return null;
        }
        return j instanceof C0 ? ((C0) j).var_a : new D0(j);
    }

    public /* synthetic */ Object get() {
        return this.var_a.get();
    }
}

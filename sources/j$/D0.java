package j$;

import j$.util.function.J;
import java.util.function.Supplier;

public final /* synthetic */ class D0 implements Supplier {
    final /* synthetic */ J a;

    private /* synthetic */ D0(J j) {
        this.a = j;
    }

    public static /* synthetic */ Supplier a(J j) {
        if (j == null) {
            return null;
        }
        return j instanceof C0 ? ((C0) j).a : new D0(j);
    }

    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

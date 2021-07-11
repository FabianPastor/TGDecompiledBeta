package j$;

import j$.util.function.J;
import java.util.function.Supplier;

public final /* synthetic */ class A0 implements Supplier {
    final /* synthetic */ J a;

    private /* synthetic */ A0(J j) {
        this.a = j;
    }

    public static /* synthetic */ Supplier a(J j) {
        if (j == null) {
            return null;
        }
        return j instanceof z0 ? ((z0) j).a : new A0(j);
    }

    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

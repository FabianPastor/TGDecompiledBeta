package j$.wrappers;

import j$.util.function.y;
import java.util.function.Supplier;

public final /* synthetic */ class A0 implements Supplier {
    final /* synthetic */ y a;

    private /* synthetic */ A0(y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ Supplier a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof z0 ? ((z0) yVar).a : new A0(yVar);
    }

    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

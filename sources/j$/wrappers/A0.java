package j$.wrappers;

import j$.util.function.z;
import java.util.function.Supplier;

public final /* synthetic */ class A0 implements Supplier {
    final /* synthetic */ z a;

    private /* synthetic */ A0(z zVar) {
        this.a = zVar;
    }

    public static /* synthetic */ Supplier a(z zVar) {
        if (zVar == null) {
            return null;
        }
        return zVar instanceof z0 ? ((z0) zVar).a : new A0(zVar);
    }

    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

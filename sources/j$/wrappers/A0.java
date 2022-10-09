package j$.wrappers;

import java.util.function.Supplier;
/* loaded from: classes2.dex */
public final /* synthetic */ class A0 implements Supplier {
    final /* synthetic */ j$.util.function.y a;

    private /* synthetic */ A0(j$.util.function.y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ Supplier a(j$.util.function.y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof z0 ? ((z0) yVar).a : new A0(yVar);
    }

    @Override // java.util.function.Supplier
    public /* synthetic */ Object get() {
        return this.a.get();
    }
}

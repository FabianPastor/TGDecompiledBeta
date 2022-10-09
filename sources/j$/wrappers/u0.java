package j$.wrappers;

import java.util.function.ObjIntConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class u0 implements ObjIntConsumer {
    final /* synthetic */ j$.util.function.v a;

    private /* synthetic */ u0(j$.util.function.v vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ ObjIntConsumer a(j$.util.function.v vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof t0 ? ((t0) vVar).a : new u0(vVar);
    }

    @Override // java.util.function.ObjIntConsumer
    public /* synthetic */ void accept(Object obj, int i) {
        this.a.accept(obj, i);
    }
}

package j$.wrappers;

import java.util.function.ObjDoubleConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class s0 implements ObjDoubleConsumer {
    final /* synthetic */ j$.util.function.u a;

    private /* synthetic */ s0(j$.util.function.u uVar) {
        this.a = uVar;
    }

    public static /* synthetic */ ObjDoubleConsumer a(j$.util.function.u uVar) {
        if (uVar == null) {
            return null;
        }
        return uVar instanceof r0 ? ((r0) uVar).a : new s0(uVar);
    }

    @Override // java.util.function.ObjDoubleConsumer
    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}

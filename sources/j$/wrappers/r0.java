package j$.wrappers;

import java.util.function.ObjDoubleConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class r0 implements j$.util.function.u {
    final /* synthetic */ ObjDoubleConsumer a;

    private /* synthetic */ r0(ObjDoubleConsumer objDoubleConsumer) {
        this.a = objDoubleConsumer;
    }

    public static /* synthetic */ j$.util.function.u a(ObjDoubleConsumer objDoubleConsumer) {
        if (objDoubleConsumer == null) {
            return null;
        }
        return objDoubleConsumer instanceof s0 ? ((s0) objDoubleConsumer).a : new r0(objDoubleConsumer);
    }

    @Override // j$.util.function.u
    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}

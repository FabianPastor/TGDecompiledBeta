package j$.wrappers;

import j$.util.function.u;
import java.util.function.ObjDoubleConsumer;

/* renamed from: j$.wrappers.r0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEr0 implements u {
    final /* synthetic */ ObjDoubleConsumer a;

    private /* synthetic */ CLASSNAMEr0(ObjDoubleConsumer objDoubleConsumer) {
        this.a = objDoubleConsumer;
    }

    public static /* synthetic */ u a(ObjDoubleConsumer objDoubleConsumer) {
        if (objDoubleConsumer == null) {
            return null;
        }
        return objDoubleConsumer instanceof s0 ? ((s0) objDoubleConsumer).a : new CLASSNAMEr0(objDoubleConsumer);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}

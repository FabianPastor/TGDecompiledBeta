package j$;

import j$.util.function.G;
import java.util.function.ObjDoubleConsumer;

/* renamed from: j$.r0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEr0 implements G {
    final /* synthetic */ ObjDoubleConsumer a;

    private /* synthetic */ CLASSNAMEr0(ObjDoubleConsumer objDoubleConsumer) {
        this.a = objDoubleConsumer;
    }

    public static /* synthetic */ G a(ObjDoubleConsumer objDoubleConsumer) {
        if (objDoubleConsumer == null) {
            return null;
        }
        return objDoubleConsumer instanceof s0 ? ((s0) objDoubleConsumer).a : new CLASSNAMEr0(objDoubleConsumer);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}

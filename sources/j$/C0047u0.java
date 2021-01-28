package j$;

import j$.util.function.G;
import java.util.function.ObjDoubleConsumer;

/* renamed from: j$.u0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu0 implements G {
    final /* synthetic */ ObjDoubleConsumer a;

    private /* synthetic */ CLASSNAMEu0(ObjDoubleConsumer objDoubleConsumer) {
        this.a = objDoubleConsumer;
    }

    public static /* synthetic */ G a(ObjDoubleConsumer objDoubleConsumer) {
        if (objDoubleConsumer == null) {
            return null;
        }
        return objDoubleConsumer instanceof CLASSNAMEv0 ? ((CLASSNAMEv0) objDoubleConsumer).a : new CLASSNAMEu0(objDoubleConsumer);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}

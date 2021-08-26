package j$.wrappers;

import j$.util.function.u;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class t0 implements u {
    final /* synthetic */ ObjIntConsumer a;

    private /* synthetic */ t0(ObjIntConsumer objIntConsumer) {
        this.a = objIntConsumer;
    }

    public static /* synthetic */ u a(ObjIntConsumer objIntConsumer) {
        if (objIntConsumer == null) {
            return null;
        }
        return objIntConsumer instanceof u0 ? ((u0) objIntConsumer).a : new t0(objIntConsumer);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.a.accept(obj, i);
    }
}

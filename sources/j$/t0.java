package j$;

import j$.util.function.H;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class t0 implements H {
    final /* synthetic */ ObjIntConsumer a;

    private /* synthetic */ t0(ObjIntConsumer objIntConsumer) {
        this.a = objIntConsumer;
    }

    public static /* synthetic */ H a(ObjIntConsumer objIntConsumer) {
        if (objIntConsumer == null) {
            return null;
        }
        return objIntConsumer instanceof u0 ? ((u0) objIntConsumer).a : new t0(objIntConsumer);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.a.accept(obj, i);
    }
}

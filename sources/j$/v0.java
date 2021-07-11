package j$;

import j$.util.function.I;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class v0 implements I {
    final /* synthetic */ ObjLongConsumer a;

    private /* synthetic */ v0(ObjLongConsumer objLongConsumer) {
        this.a = objLongConsumer;
    }

    public static /* synthetic */ I a(ObjLongConsumer objLongConsumer) {
        if (objLongConsumer == null) {
            return null;
        }
        return objLongConsumer instanceof w0 ? ((w0) objLongConsumer).a : new v0(objLongConsumer);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.a.accept(obj, j);
    }
}

package j$.wrappers;

import j$.util.function.w;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class v0 implements w {
    final /* synthetic */ ObjLongConsumer a;

    private /* synthetic */ v0(ObjLongConsumer objLongConsumer) {
        this.a = objLongConsumer;
    }

    public static /* synthetic */ w a(ObjLongConsumer objLongConsumer) {
        if (objLongConsumer == null) {
            return null;
        }
        return objLongConsumer instanceof w0 ? ((w0) objLongConsumer).a : new v0(objLongConsumer);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.a.accept(obj, j);
    }
}

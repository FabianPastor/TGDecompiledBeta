package j$;

import j$.util.function.D;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class y0 implements D {
    final /* synthetic */ ObjLongConsumer a;

    private /* synthetic */ y0(ObjLongConsumer objLongConsumer) {
        this.a = objLongConsumer;
    }

    public static /* synthetic */ D a(ObjLongConsumer objLongConsumer) {
        if (objLongConsumer == null) {
            return null;
        }
        return objLongConsumer instanceof z0 ? ((z0) objLongConsumer).a : new y0(objLongConsumer);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.a.accept(obj, j);
    }
}

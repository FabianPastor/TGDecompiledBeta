package j$;

import j$.util.function.T;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class c0 implements T {
    final /* synthetic */ ObjLongConsumer a;

    private /* synthetic */ c0(ObjLongConsumer objLongConsumer) {
        this.a = objLongConsumer;
    }

    public static /* synthetic */ T b(ObjLongConsumer objLongConsumer) {
        if (objLongConsumer == null) {
            return null;
        }
        return new c0(objLongConsumer);
    }

    public /* synthetic */ void a(Object obj, long j) {
        this.a.accept(obj, j);
    }
}

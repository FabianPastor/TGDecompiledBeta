package a;

import j$.util.function.I;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class y0 implements I {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ObjLongConsumer var_a;

    private /* synthetic */ y0(ObjLongConsumer objLongConsumer) {
        this.var_a = objLongConsumer;
    }

    public static /* synthetic */ I a(ObjLongConsumer objLongConsumer) {
        if (objLongConsumer == null) {
            return null;
        }
        return objLongConsumer instanceof z0 ? ((z0) objLongConsumer).var_a : new y0(objLongConsumer);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.var_a.accept(obj, j);
    }
}

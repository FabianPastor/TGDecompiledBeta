package j$;

import j$.util.function.D;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class z0 implements ObjLongConsumer {
    final /* synthetic */ D a;

    private /* synthetic */ z0(D d) {
        this.a = d;
    }

    public static /* synthetic */ ObjLongConsumer a(D d) {
        if (d == null) {
            return null;
        }
        return d instanceof y0 ? ((y0) d).a : new z0(d);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.a.accept(obj, j);
    }
}

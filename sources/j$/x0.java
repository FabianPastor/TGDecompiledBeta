package j$;

import j$.util.function.H;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class x0 implements ObjIntConsumer {
    final /* synthetic */ H a;

    private /* synthetic */ x0(H h) {
        this.a = h;
    }

    public static /* synthetic */ ObjIntConsumer a(H h) {
        if (h == null) {
            return null;
        }
        return h instanceof w0 ? ((w0) h).a : new x0(h);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.a.accept(obj, i);
    }
}

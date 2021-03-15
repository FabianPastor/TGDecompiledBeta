package a;

import j$.util.function.H;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class x0 implements ObjIntConsumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ H var_a;

    private /* synthetic */ x0(H h) {
        this.var_a = h;
    }

    public static /* synthetic */ ObjIntConsumer a(H h) {
        if (h == null) {
            return null;
        }
        return h instanceof w0 ? ((w0) h).var_a : new x0(h);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.var_a.accept(obj, i);
    }
}

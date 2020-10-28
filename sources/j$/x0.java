package j$;

import j$.util.function.C;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class x0 implements ObjIntConsumer {
    final /* synthetic */ C a;

    private /* synthetic */ x0(C c) {
        this.a = c;
    }

    public static /* synthetic */ ObjIntConsumer a(C c) {
        if (c == null) {
            return null;
        }
        return c instanceof w0 ? ((w0) c).a : new x0(c);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.a.accept(obj, i);
    }
}

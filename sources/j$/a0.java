package j$;

import j$.util.function.Q;
import java.util.function.ObjDoubleConsumer;

public final /* synthetic */ class a0 implements Q {
    final /* synthetic */ ObjDoubleConsumer a;

    private /* synthetic */ a0(ObjDoubleConsumer objDoubleConsumer) {
        this.a = objDoubleConsumer;
    }

    public static /* synthetic */ Q b(ObjDoubleConsumer objDoubleConsumer) {
        if (objDoubleConsumer == null) {
            return null;
        }
        return new a0(objDoubleConsumer);
    }

    public /* synthetic */ void a(Object obj, double d) {
        this.a.accept(obj, d);
    }
}

package j$;

import j$.util.function.C;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class w0 implements C {
    final /* synthetic */ ObjIntConsumer a;

    private /* synthetic */ w0(ObjIntConsumer objIntConsumer) {
        this.a = objIntConsumer;
    }

    public static /* synthetic */ C a(ObjIntConsumer objIntConsumer) {
        if (objIntConsumer == null) {
            return null;
        }
        return objIntConsumer instanceof x0 ? ((x0) objIntConsumer).a : new w0(objIntConsumer);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.a.accept(obj, i);
    }
}

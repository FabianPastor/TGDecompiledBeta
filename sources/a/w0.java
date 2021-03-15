package a;

import j$.util.function.H;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class w0 implements H {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ObjIntConsumer var_a;

    private /* synthetic */ w0(ObjIntConsumer objIntConsumer) {
        this.var_a = objIntConsumer;
    }

    public static /* synthetic */ H a(ObjIntConsumer objIntConsumer) {
        if (objIntConsumer == null) {
            return null;
        }
        return objIntConsumer instanceof x0 ? ((x0) objIntConsumer).var_a : new w0(objIntConsumer);
    }

    public /* synthetic */ void accept(Object obj, int i) {
        this.var_a.accept(obj, i);
    }
}

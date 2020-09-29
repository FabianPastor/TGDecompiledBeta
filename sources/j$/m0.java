package j$;

import j$.util.function.S;
import java.util.function.ObjIntConsumer;

public final /* synthetic */ class m0 implements S {
    final /* synthetic */ ObjIntConsumer a;

    private /* synthetic */ m0(ObjIntConsumer objIntConsumer) {
        this.a = objIntConsumer;
    }

    public static /* synthetic */ S b(ObjIntConsumer objIntConsumer) {
        if (objIntConsumer == null) {
            return null;
        }
        return new m0(objIntConsumer);
    }

    public /* synthetic */ void a(Object obj, int i) {
        this.a.accept(obj, i);
    }
}

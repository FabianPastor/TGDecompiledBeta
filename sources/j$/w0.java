package j$;

import j$.util.function.I;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class w0 implements ObjLongConsumer {
    final /* synthetic */ I a;

    private /* synthetic */ w0(I i) {
        this.a = i;
    }

    public static /* synthetic */ ObjLongConsumer a(I i) {
        if (i == null) {
            return null;
        }
        return i instanceof v0 ? ((v0) i).a : new w0(i);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.a.accept(obj, j);
    }
}

package j$.wrappers;

import j$.util.function.w;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class w0 implements ObjLongConsumer {
    final /* synthetic */ w a;

    private /* synthetic */ w0(w wVar) {
        this.a = wVar;
    }

    public static /* synthetic */ ObjLongConsumer a(w wVar) {
        if (wVar == null) {
            return null;
        }
        return wVar instanceof v0 ? ((v0) wVar).a : new w0(wVar);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.a.accept(obj, j);
    }
}

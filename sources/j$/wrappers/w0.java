package j$.wrappers;

import j$.util.function.v;
import java.util.function.ObjLongConsumer;

public final /* synthetic */ class w0 implements ObjLongConsumer {
    final /* synthetic */ v a;

    private /* synthetic */ w0(v vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ ObjLongConsumer a(v vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof v0 ? ((v0) vVar).a : new w0(vVar);
    }

    public /* synthetic */ void accept(Object obj, long j) {
        this.a.accept(obj, j);
    }
}

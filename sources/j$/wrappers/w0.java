package j$.wrappers;

import java.util.function.ObjLongConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class w0 implements ObjLongConsumer {
    final /* synthetic */ j$.util.function.w a;

    private /* synthetic */ w0(j$.util.function.w wVar) {
        this.a = wVar;
    }

    public static /* synthetic */ ObjLongConsumer a(j$.util.function.w wVar) {
        if (wVar == null) {
            return null;
        }
        return wVar instanceof v0 ? ((v0) wVar).a : new w0(wVar);
    }

    @Override // java.util.function.ObjLongConsumer
    public /* synthetic */ void accept(Object obj, long j) {
        this.a.accept(obj, j);
    }
}

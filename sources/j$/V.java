package j$;

import j$.util.function.w;
import java.util.function.IntConsumer;

public final /* synthetic */ class V implements IntConsumer {
    final /* synthetic */ w a;

    private /* synthetic */ V(w wVar) {
        this.a = wVar;
    }

    public static /* synthetic */ IntConsumer a(w wVar) {
        if (wVar == null) {
            return null;
        }
        return wVar instanceof U ? ((U) wVar).a : new V(wVar);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return a(this.a.l(U.b(intConsumer)));
    }
}

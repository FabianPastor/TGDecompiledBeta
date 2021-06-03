package j$;

import j$.util.function.w;
import java.util.function.IntConsumer;

public final /* synthetic */ class S implements IntConsumer {
    final /* synthetic */ w a;

    private /* synthetic */ S(w wVar) {
        this.a = wVar;
    }

    public static /* synthetic */ IntConsumer a(w wVar) {
        if (wVar == null) {
            return null;
        }
        return wVar instanceof Q ? ((Q) wVar).a : new S(wVar);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return a(this.a.k(Q.b(intConsumer)));
    }
}

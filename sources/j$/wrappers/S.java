package j$.wrappers;

import j$.util.function.l;
import java.util.function.IntConsumer;

public final /* synthetic */ class S implements IntConsumer {
    final /* synthetic */ l a;

    private /* synthetic */ S(l lVar) {
        this.a = lVar;
    }

    public static /* synthetic */ IntConsumer a(l lVar) {
        if (lVar == null) {
            return null;
        }
        return lVar instanceof Q ? ((Q) lVar).a : new S(lVar);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return a(this.a.l(Q.b(intConsumer)));
    }
}

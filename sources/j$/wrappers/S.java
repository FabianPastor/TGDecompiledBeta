package j$.wrappers;

import j$.util.function.k;
import java.util.function.IntConsumer;

public final /* synthetic */ class S implements IntConsumer {
    final /* synthetic */ k a;

    private /* synthetic */ S(k kVar) {
        this.a = kVar;
    }

    public static /* synthetic */ IntConsumer a(k kVar) {
        if (kVar == null) {
            return null;
        }
        return kVar instanceof Q ? ((Q) kVar).a : new S(kVar);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return a(this.a.l(Q.b(intConsumer)));
    }
}

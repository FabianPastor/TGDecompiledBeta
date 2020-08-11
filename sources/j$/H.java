package j$;

import j$.util.function.B;
import java.util.function.IntConsumer;

public final /* synthetic */ class H implements IntConsumer {
    final /* synthetic */ B a;

    private /* synthetic */ H(B b) {
        this.a = b;
    }

    public static /* synthetic */ IntConsumer a(B b) {
        if (b == null) {
            return null;
        }
        return b instanceof G ? ((G) b).a : new H(b);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return a(this.a.q(G.a(intConsumer)));
    }
}

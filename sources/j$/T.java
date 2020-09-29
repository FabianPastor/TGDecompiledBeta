package j$;

import j$.util.function.B;
import java.util.function.IntConsumer;

public final /* synthetic */ class T implements IntConsumer {
    final /* synthetic */ B a;

    private /* synthetic */ T(B b) {
        this.a = b;
    }

    public static /* synthetic */ IntConsumer a(B b) {
        if (b == null) {
            return null;
        }
        return b instanceof S ? ((S) b).a : new T(b);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return a(this.a.q(S.a(intConsumer)));
    }
}

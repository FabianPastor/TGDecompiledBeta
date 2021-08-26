package j$.wrappers;

import j$.util.function.k;
import java.util.function.IntConsumer;

public final /* synthetic */ class Q implements k {
    final /* synthetic */ IntConsumer a;

    private /* synthetic */ Q(IntConsumer intConsumer) {
        this.a = intConsumer;
    }

    public static /* synthetic */ k b(IntConsumer intConsumer) {
        if (intConsumer == null) {
            return null;
        }
        return intConsumer instanceof S ? ((S) intConsumer).a : new Q(intConsumer);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ k l(k kVar) {
        return b(this.a.andThen(S.a(kVar)));
    }
}

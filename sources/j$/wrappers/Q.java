package j$.wrappers;

import j$.util.function.l;
import java.util.function.IntConsumer;

public final /* synthetic */ class Q implements l {
    final /* synthetic */ IntConsumer a;

    private /* synthetic */ Q(IntConsumer intConsumer) {
        this.a = intConsumer;
    }

    public static /* synthetic */ l b(IntConsumer intConsumer) {
        if (intConsumer == null) {
            return null;
        }
        return intConsumer instanceof S ? ((S) intConsumer).a : new Q(intConsumer);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ l l(l lVar) {
        return b(this.a.andThen(S.a(lVar)));
    }
}

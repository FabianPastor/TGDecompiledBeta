package j$;

import j$.util.function.w;
import java.util.function.IntConsumer;

public final /* synthetic */ class Q implements w {
    final /* synthetic */ IntConsumer a;

    private /* synthetic */ Q(IntConsumer intConsumer) {
        this.a = intConsumer;
    }

    public static /* synthetic */ w b(IntConsumer intConsumer) {
        if (intConsumer == null) {
            return null;
        }
        return intConsumer instanceof S ? ((S) intConsumer).a : new Q(intConsumer);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ w k(w wVar) {
        return b(this.a.andThen(S.a(wVar)));
    }
}

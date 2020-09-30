package j$;

import j$.util.function.J;
import java.util.function.LongConsumer;

public final /* synthetic */ class Q implements J {
    final /* synthetic */ LongConsumer a;

    private /* synthetic */ Q(LongConsumer longConsumer) {
        this.a = longConsumer;
    }

    public static /* synthetic */ J a(LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof S ? ((S) longConsumer).a : new Q(longConsumer);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ J h(J j) {
        return a(this.a.andThen(S.a(j)));
    }
}

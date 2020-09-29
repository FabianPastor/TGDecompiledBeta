package j$;

import j$.util.function.J;
import java.util.function.LongConsumer;

public final /* synthetic */ class c0 implements J {
    final /* synthetic */ LongConsumer a;

    private /* synthetic */ c0(LongConsumer longConsumer) {
        this.a = longConsumer;
    }

    public static /* synthetic */ J a(LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof d0 ? ((d0) longConsumer).a : new c0(longConsumer);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ J h(J j) {
        return a(this.a.andThen(d0.a(j)));
    }
}

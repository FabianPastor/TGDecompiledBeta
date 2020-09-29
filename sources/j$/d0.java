package j$;

import j$.util.function.J;
import java.util.function.LongConsumer;

public final /* synthetic */ class d0 implements LongConsumer {
    final /* synthetic */ J a;

    private /* synthetic */ d0(J j) {
        this.a = j;
    }

    public static /* synthetic */ LongConsumer a(J j) {
        if (j == null) {
            return null;
        }
        return j instanceof c0 ? ((c0) j).a : new d0(j);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return a(this.a.h(c0.a(longConsumer)));
    }
}

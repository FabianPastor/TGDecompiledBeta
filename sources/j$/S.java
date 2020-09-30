package j$;

import j$.util.function.J;
import java.util.function.LongConsumer;

public final /* synthetic */ class S implements LongConsumer {
    final /* synthetic */ J a;

    private /* synthetic */ S(J j) {
        this.a = j;
    }

    public static /* synthetic */ LongConsumer a(J j) {
        if (j == null) {
            return null;
        }
        return j instanceof Q ? ((Q) j).a : new S(j);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return a(this.a.h(Q.a(longConsumer)));
    }
}

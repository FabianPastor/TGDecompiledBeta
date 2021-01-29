package j$;

import j$.util.function.C;
import java.util.function.LongConsumer;

/* renamed from: j$.j0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj0 implements LongConsumer {
    final /* synthetic */ C a;

    private /* synthetic */ CLASSNAMEj0(C c) {
        this.a = c;
    }

    public static /* synthetic */ LongConsumer a(C c) {
        if (c == null) {
            return null;
        }
        return c instanceof CLASSNAMEi0 ? ((CLASSNAMEi0) c).a : new CLASSNAMEj0(c);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return a(this.a.g(CLASSNAMEi0.b(longConsumer)));
    }
}

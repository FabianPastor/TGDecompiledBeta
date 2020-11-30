package j$;

import j$.util.function.C;
import java.util.function.LongConsumer;

/* renamed from: j$.i0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi0 implements C {
    final /* synthetic */ LongConsumer a;

    private /* synthetic */ CLASSNAMEi0(LongConsumer longConsumer) {
        this.a = longConsumer;
    }

    public static /* synthetic */ C b(LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof CLASSNAMEj0 ? ((CLASSNAMEj0) longConsumer).a : new CLASSNAMEi0(longConsumer);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ C g(C c) {
        return b(this.a.andThen(CLASSNAMEj0.a(c)));
    }
}

package j$;

import j$.util.function.C;
import java.util.function.LongConsumer;

/* renamed from: j$.g0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg0 implements LongConsumer {
    final /* synthetic */ C a;

    private /* synthetic */ CLASSNAMEg0(C c) {
        this.a = c;
    }

    public static /* synthetic */ LongConsumer a(C c) {
        if (c == null) {
            return null;
        }
        return c instanceof CLASSNAMEf0 ? ((CLASSNAMEf0) c).a : new CLASSNAMEg0(c);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return a(this.a.f(CLASSNAMEf0.b(longConsumer)));
    }
}

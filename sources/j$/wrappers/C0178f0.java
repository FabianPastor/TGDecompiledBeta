package j$.wrappers;

import j$.util.function.q;
import java.util.function.LongConsumer;

/* renamed from: j$.wrappers.f0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf0 implements q {
    final /* synthetic */ LongConsumer a;

    private /* synthetic */ CLASSNAMEf0(LongConsumer longConsumer) {
        this.a = longConsumer;
    }

    public static /* synthetic */ q b(LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof CLASSNAMEg0 ? ((CLASSNAMEg0) longConsumer).a : new CLASSNAMEf0(longConsumer);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ q f(q qVar) {
        return b(this.a.andThen(CLASSNAMEg0.a(qVar)));
    }
}
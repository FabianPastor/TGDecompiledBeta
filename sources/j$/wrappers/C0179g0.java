package j$.wrappers;

import j$.util.function.p;
import java.util.function.LongConsumer;

/* renamed from: j$.wrappers.g0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg0 implements LongConsumer {
    final /* synthetic */ p a;

    private /* synthetic */ CLASSNAMEg0(p pVar) {
        this.a = pVar;
    }

    public static /* synthetic */ LongConsumer a(p pVar) {
        if (pVar == null) {
            return null;
        }
        return pVar instanceof CLASSNAMEf0 ? ((CLASSNAMEf0) pVar).a : new CLASSNAMEg0(pVar);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return a(this.a.f(CLASSNAMEf0.b(longConsumer)));
    }
}

package j$.wrappers;

import j$.util.function.q;
import java.util.function.LongConsumer;

/* renamed from: j$.wrappers.g0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg0 implements LongConsumer {
    final /* synthetic */ q a;

    private /* synthetic */ CLASSNAMEg0(q qVar) {
        this.a = qVar;
    }

    public static /* synthetic */ LongConsumer a(q qVar) {
        if (qVar == null) {
            return null;
        }
        return qVar instanceof CLASSNAMEf0 ? ((CLASSNAMEf0) qVar).a : new CLASSNAMEg0(qVar);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return a(this.a.f(CLASSNAMEf0.b(longConsumer)));
    }
}

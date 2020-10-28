package j$;

import j$.util.function.y;
import java.util.function.LongConsumer;

/* renamed from: j$.j0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj0 implements LongConsumer {
    final /* synthetic */ y a;

    private /* synthetic */ CLASSNAMEj0(y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ LongConsumer a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof CLASSNAMEi0 ? ((CLASSNAMEi0) yVar).a : new CLASSNAMEj0(yVar);
    }

    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return a(this.a.g(CLASSNAMEi0.b(longConsumer)));
    }
}

package j$;

import j$.util.function.CLASSNAMEt;
import java.util.function.DoubleConsumer;

/* renamed from: j$.v  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv implements DoubleConsumer {
    final /* synthetic */ CLASSNAMEt a;

    private /* synthetic */ CLASSNAMEv(CLASSNAMEt tVar) {
        this.a = tVar;
    }

    public static /* synthetic */ DoubleConsumer a(CLASSNAMEt tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof CLASSNAMEu ? ((CLASSNAMEu) tVar).a : new CLASSNAMEv(tVar);
    }

    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return a(this.a.p(CLASSNAMEu.a(doubleConsumer)));
    }
}

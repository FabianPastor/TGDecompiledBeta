package j$;

import j$.util.function.CLASSNAMEt;
import java.util.function.DoubleConsumer;

/* renamed from: j$.u  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu implements CLASSNAMEt {
    final /* synthetic */ DoubleConsumer a;

    private /* synthetic */ CLASSNAMEu(DoubleConsumer doubleConsumer) {
        this.a = doubleConsumer;
    }

    public static /* synthetic */ CLASSNAMEt a(DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof CLASSNAMEv ? ((CLASSNAMEv) doubleConsumer).a : new CLASSNAMEu(doubleConsumer);
    }

    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return a(this.a.andThen(CLASSNAMEv.a(tVar)));
    }
}

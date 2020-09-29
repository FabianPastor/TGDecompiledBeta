package j$;

import j$.util.function.CLASSNAMEt;
import java.util.function.DoubleConsumer;

public final /* synthetic */ class F implements CLASSNAMEt {
    final /* synthetic */ DoubleConsumer a;

    private /* synthetic */ F(DoubleConsumer doubleConsumer) {
        this.a = doubleConsumer;
    }

    public static /* synthetic */ CLASSNAMEt a(DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof G ? ((G) doubleConsumer).a : new F(doubleConsumer);
    }

    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return a(this.a.andThen(G.a(tVar)));
    }
}

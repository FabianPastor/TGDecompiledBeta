package j$;

import j$.util.function.CLASSNAMEt;
import java.util.function.DoubleConsumer;

public final /* synthetic */ class G implements DoubleConsumer {
    final /* synthetic */ CLASSNAMEt a;

    private /* synthetic */ G(CLASSNAMEt tVar) {
        this.a = tVar;
    }

    public static /* synthetic */ DoubleConsumer a(CLASSNAMEt tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof F ? ((F) tVar).a : new G(tVar);
    }

    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return a(this.a.p(F.a(doubleConsumer)));
    }
}

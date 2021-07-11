package j$;

import j$.util.function.q;
import java.util.function.DoubleConsumer;

public final /* synthetic */ class B implements DoubleConsumer {
    final /* synthetic */ q a;

    private /* synthetic */ B(q qVar) {
        this.a = qVar;
    }

    public static /* synthetic */ DoubleConsumer a(q qVar) {
        if (qVar == null) {
            return null;
        }
        return qVar instanceof A ? ((A) qVar).a : new B(qVar);
    }

    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return a(this.a.j(A.b(doubleConsumer)));
    }
}

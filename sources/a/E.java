package a;

import j$.util.function.q;
import java.util.function.DoubleConsumer;

public final /* synthetic */ class E implements DoubleConsumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ q var_a;

    private /* synthetic */ E(q qVar) {
        this.var_a = qVar;
    }

    public static /* synthetic */ DoubleConsumer a(q qVar) {
        if (qVar == null) {
            return null;
        }
        return qVar instanceof D ? ((D) qVar).var_a : new E(qVar);
    }

    public /* synthetic */ void accept(double d) {
        this.var_a.accept(d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return a(this.var_a.k(D.b(doubleConsumer)));
    }
}

package a;

import j$.util.function.q;
import java.util.function.DoubleConsumer;

public final /* synthetic */ class D implements q {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DoubleConsumer var_a;

    private /* synthetic */ D(DoubleConsumer doubleConsumer) {
        this.var_a = doubleConsumer;
    }

    public static /* synthetic */ q b(DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof E ? ((E) doubleConsumer).var_a : new D(doubleConsumer);
    }

    public /* synthetic */ void accept(double d) {
        this.var_a.accept(d);
    }

    public /* synthetic */ q k(q qVar) {
        return b(this.var_a.andThen(E.a(qVar)));
    }
}

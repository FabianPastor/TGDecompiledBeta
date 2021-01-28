package j$;

import j$.util.function.q;
import java.util.function.DoubleConsumer;

public final /* synthetic */ class D implements q {
    final /* synthetic */ DoubleConsumer a;

    private /* synthetic */ D(DoubleConsumer doubleConsumer) {
        this.a = doubleConsumer;
    }

    public static /* synthetic */ q b(DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof E ? ((E) doubleConsumer).a : new D(doubleConsumer);
    }

    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ q k(q qVar) {
        return b(this.a.andThen(E.a(qVar)));
    }
}

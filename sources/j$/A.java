package j$;

import j$.util.function.q;
import java.util.function.DoubleConsumer;

public final /* synthetic */ class A implements q {
    final /* synthetic */ DoubleConsumer a;

    private /* synthetic */ A(DoubleConsumer doubleConsumer) {
        this.a = doubleConsumer;
    }

    public static /* synthetic */ q b(DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof B ? ((B) doubleConsumer).a : new A(doubleConsumer);
    }

    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ q j(q qVar) {
        return b(this.a.andThen(B.a(qVar)));
    }
}

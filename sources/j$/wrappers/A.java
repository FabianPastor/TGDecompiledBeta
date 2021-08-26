package j$.wrappers;

import j$.util.function.f;
import java.util.function.DoubleConsumer;

public final /* synthetic */ class A implements f {
    final /* synthetic */ DoubleConsumer a;

    private /* synthetic */ A(DoubleConsumer doubleConsumer) {
        this.a = doubleConsumer;
    }

    public static /* synthetic */ f b(DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof B ? ((B) doubleConsumer).a : new A(doubleConsumer);
    }

    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ f j(f fVar) {
        return b(this.a.andThen(B.a(fVar)));
    }
}

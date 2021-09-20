package j$.wrappers;

import j$.util.function.f;
import java.util.function.DoubleConsumer;

public final /* synthetic */ class B implements DoubleConsumer {
    final /* synthetic */ f a;

    private /* synthetic */ B(f fVar) {
        this.a = fVar;
    }

    public static /* synthetic */ DoubleConsumer a(f fVar) {
        if (fVar == null) {
            return null;
        }
        return fVar instanceof A ? ((A) fVar).a : new B(fVar);
    }

    public /* synthetic */ void accept(double d) {
        this.a.accept(d);
    }

    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return a(this.a.j(A.b(doubleConsumer)));
    }
}

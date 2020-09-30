package j$;

import j$.util.function.B;
import java.util.function.IntConsumer;

public final /* synthetic */ class G implements B {
    final /* synthetic */ IntConsumer a;

    private /* synthetic */ G(IntConsumer intConsumer) {
        this.a = intConsumer;
    }

    public static /* synthetic */ B a(IntConsumer intConsumer) {
        if (intConsumer == null) {
            return null;
        }
        return intConsumer instanceof H ? ((H) intConsumer).a : new G(intConsumer);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ B q(B b) {
        return a(this.a.andThen(H.a(b)));
    }
}

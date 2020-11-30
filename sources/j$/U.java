package j$;

import j$.util.function.w;
import java.util.function.IntConsumer;

public final /* synthetic */ class U implements w {
    final /* synthetic */ IntConsumer a;

    private /* synthetic */ U(IntConsumer intConsumer) {
        this.a = intConsumer;
    }

    public static /* synthetic */ w b(IntConsumer intConsumer) {
        if (intConsumer == null) {
            return null;
        }
        return intConsumer instanceof V ? ((V) intConsumer).a : new U(intConsumer);
    }

    public /* synthetic */ void accept(int i) {
        this.a.accept(i);
    }

    public /* synthetic */ w l(w wVar) {
        return b(this.a.andThen(V.a(wVar)));
    }
}

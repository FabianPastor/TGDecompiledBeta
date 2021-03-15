package a;

import j$.util.function.w;
import java.util.function.IntConsumer;

public final /* synthetic */ class U implements w {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IntConsumer var_a;

    private /* synthetic */ U(IntConsumer intConsumer) {
        this.var_a = intConsumer;
    }

    public static /* synthetic */ w b(IntConsumer intConsumer) {
        if (intConsumer == null) {
            return null;
        }
        return intConsumer instanceof V ? ((V) intConsumer).var_a : new U(intConsumer);
    }

    public /* synthetic */ void accept(int i) {
        this.var_a.accept(i);
    }

    public /* synthetic */ w l(w wVar) {
        return b(this.var_a.andThen(V.a(wVar)));
    }
}

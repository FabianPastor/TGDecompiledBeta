package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class M1 implements j$.util.function.b {
    public static final /* synthetic */ M1 a = new M1();

    private /* synthetic */ M1() {
    }

    @Override // j$.util.function.BiFunction
    public BiFunction andThen(Function function) {
        function.getClass();
        return new j$.util.concurrent.a(this, function);
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return new S1((A1) obj, (A1) obj2);
    }
}

package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class I1 implements j$.util.function.b {
    public static final /* synthetic */ I1 a = new I1();

    private /* synthetic */ I1() {
    }

    @Override // j$.util.function.BiFunction
    public BiFunction andThen(Function function) {
        function.getClass();
        return new j$.util.concurrent.a(this, function);
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return new P1((InterfaceCLASSNAMEw1) obj, (InterfaceCLASSNAMEw1) obj2);
    }
}

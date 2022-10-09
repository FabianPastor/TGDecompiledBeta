package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class K1 implements j$.util.function.b {
    public static final /* synthetic */ K1 a = new K1();

    private /* synthetic */ K1() {
    }

    @Override // j$.util.function.BiFunction
    public BiFunction andThen(Function function) {
        function.getClass();
        return new j$.util.concurrent.a(this, function);
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return new Q1((InterfaceCLASSNAMEy1) obj, (InterfaceCLASSNAMEy1) obj2);
    }
}

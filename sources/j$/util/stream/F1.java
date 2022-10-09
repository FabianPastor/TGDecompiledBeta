package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class F1 implements j$.util.function.b {
    public static final /* synthetic */ F1 a = new F1();

    private /* synthetic */ F1() {
    }

    @Override // j$.util.function.BiFunction
    public BiFunction andThen(Function function) {
        function.getClass();
        return new j$.util.concurrent.a(this, function);
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return new O1((InterfaceCLASSNAMEu1) obj, (InterfaceCLASSNAMEu1) obj2);
    }
}

package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Function;
import j$.util.function.n;
import j$.util.stream.R1;
import j$.util.stream.S1;

/* renamed from: j$.util.stream.b0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb0 implements n {
    public static final /* synthetic */ CLASSNAMEb0 a = new CLASSNAMEb0();

    private /* synthetic */ CLASSNAMEb0() {
    }

    public BiFunction a(Function function) {
        function.getClass();
        return new CLASSNAMEb(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new S1.f.a((R1.b) obj, (R1.b) obj2);
    }
}

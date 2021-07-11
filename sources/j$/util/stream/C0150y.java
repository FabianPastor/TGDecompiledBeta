package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Function;
import j$.util.function.n;
import j$.util.stream.R1;
import j$.util.stream.S1;

/* renamed from: j$.util.stream.y  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEy implements n {
    public static final /* synthetic */ CLASSNAMEy a = new CLASSNAMEy();

    private /* synthetic */ CLASSNAMEy() {
    }

    public BiFunction a(Function function) {
        function.getClass();
        return new CLASSNAMEb(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new S1.f.c((R1.d) obj, (R1.d) obj2);
    }
}

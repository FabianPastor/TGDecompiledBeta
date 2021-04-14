package a;

import j$.util.function.x;
import java.util.function.IntFunction;

public final /* synthetic */ class W implements x {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IntFunction var_a;

    private /* synthetic */ W(IntFunction intFunction) {
        this.var_a = intFunction;
    }

    public static /* synthetic */ x a(IntFunction intFunction) {
        if (intFunction == null) {
            return null;
        }
        return intFunction instanceof X ? ((X) intFunction).var_a : new W(intFunction);
    }

    public /* synthetic */ Object apply(int i) {
        return this.var_a.apply(i);
    }
}

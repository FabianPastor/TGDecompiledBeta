package j$.wrappers;

import j$.util.function.n;
import java.util.function.IntToLongFunction;

public final /* synthetic */ class Z implements n {
    final /* synthetic */ IntToLongFunction a;

    private /* synthetic */ Z(IntToLongFunction intToLongFunction) {
        this.a = intToLongFunction;
    }

    public static /* synthetic */ n a(IntToLongFunction intToLongFunction) {
        if (intToLongFunction == null) {
            return null;
        }
        return intToLongFunction instanceof CLASSNAMEa0 ? ((CLASSNAMEa0) intToLongFunction).a : new Z(intToLongFunction);
    }

    public /* synthetic */ long applyAsLong(int i) {
        return this.a.applyAsLong(i);
    }
}

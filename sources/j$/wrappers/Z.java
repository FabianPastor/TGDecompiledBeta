package j$.wrappers;

import java.util.function.IntToLongFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class Z implements j$.util.function.n {
    final /* synthetic */ IntToLongFunction a;

    private /* synthetic */ Z(IntToLongFunction intToLongFunction) {
        this.a = intToLongFunction;
    }

    public static /* synthetic */ j$.util.function.n a(IntToLongFunction intToLongFunction) {
        if (intToLongFunction == null) {
            return null;
        }
        return intToLongFunction instanceof CLASSNAMEa0 ? ((CLASSNAMEa0) intToLongFunction).a : new Z(intToLongFunction);
    }

    @Override // j$.util.function.n
    public /* synthetic */ long applyAsLong(int i) {
        return this.a.applyAsLong(i);
    }
}

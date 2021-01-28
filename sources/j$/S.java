package j$;

import j$.util.function.v;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class S implements v {
    final /* synthetic */ IntBinaryOperator a;

    private /* synthetic */ S(IntBinaryOperator intBinaryOperator) {
        this.a = intBinaryOperator;
    }

    public static /* synthetic */ v a(IntBinaryOperator intBinaryOperator) {
        if (intBinaryOperator == null) {
            return null;
        }
        return intBinaryOperator instanceof T ? ((T) intBinaryOperator).a : new S(intBinaryOperator);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.a.applyAsInt(i, i2);
    }
}

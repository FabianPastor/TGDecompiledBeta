package j$;

import j$.util.function.v;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class O implements v {
    final /* synthetic */ IntBinaryOperator a;

    private /* synthetic */ O(IntBinaryOperator intBinaryOperator) {
        this.a = intBinaryOperator;
    }

    public static /* synthetic */ v a(IntBinaryOperator intBinaryOperator) {
        if (intBinaryOperator == null) {
            return null;
        }
        return intBinaryOperator instanceof P ? ((P) intBinaryOperator).a : new O(intBinaryOperator);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.a.applyAsInt(i, i2);
    }
}

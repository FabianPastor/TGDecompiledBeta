package j$.wrappers;

import j$.util.function.i;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class O implements i {
    final /* synthetic */ IntBinaryOperator a;

    private /* synthetic */ O(IntBinaryOperator intBinaryOperator) {
        this.a = intBinaryOperator;
    }

    public static /* synthetic */ i a(IntBinaryOperator intBinaryOperator) {
        if (intBinaryOperator == null) {
            return null;
        }
        return intBinaryOperator instanceof P ? ((P) intBinaryOperator).a : new O(intBinaryOperator);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.a.applyAsInt(i, i2);
    }
}

package j$;

import j$.util.function.z;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class F implements z {
    final /* synthetic */ IntBinaryOperator a;

    private /* synthetic */ F(IntBinaryOperator intBinaryOperator) {
        this.a = intBinaryOperator;
    }

    public static /* synthetic */ z b(IntBinaryOperator intBinaryOperator) {
        if (intBinaryOperator == null) {
            return null;
        }
        return new F(intBinaryOperator);
    }

    public /* synthetic */ int a(int i, int i2) {
        return this.a.applyAsInt(i, i2);
    }
}

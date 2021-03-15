package a;

import j$.util.function.v;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class S implements v {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IntBinaryOperator var_a;

    private /* synthetic */ S(IntBinaryOperator intBinaryOperator) {
        this.var_a = intBinaryOperator;
    }

    public static /* synthetic */ v a(IntBinaryOperator intBinaryOperator) {
        if (intBinaryOperator == null) {
            return null;
        }
        return intBinaryOperator instanceof T ? ((T) intBinaryOperator).var_a : new S(intBinaryOperator);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.var_a.applyAsInt(i, i2);
    }
}

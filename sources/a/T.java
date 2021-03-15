package a;

import j$.util.function.v;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class T implements IntBinaryOperator {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v var_a;

    private /* synthetic */ T(v vVar) {
        this.var_a = vVar;
    }

    public static /* synthetic */ IntBinaryOperator a(v vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof S ? ((S) vVar).var_a : new T(vVar);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.var_a.applyAsInt(i, i2);
    }
}

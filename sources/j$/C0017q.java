package j$;

import j$.util.function.CLASSNAMEo;
import java.util.function.BinaryOperator;

/* renamed from: j$.q  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEq implements CLASSNAMEo {
    final /* synthetic */ BinaryOperator a;

    private /* synthetic */ CLASSNAMEq(BinaryOperator binaryOperator) {
        this.a = binaryOperator;
    }

    public static /* synthetic */ CLASSNAMEo b(BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return new CLASSNAMEq(binaryOperator);
    }

    public /* synthetic */ Object a(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

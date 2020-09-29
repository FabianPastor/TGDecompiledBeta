package j$;

import j$.util.function.CLASSNAMEo;
import java.util.function.BinaryOperator;

public final /* synthetic */ class B implements CLASSNAMEo {
    final /* synthetic */ BinaryOperator a;

    private /* synthetic */ B(BinaryOperator binaryOperator) {
        this.a = binaryOperator;
    }

    public static /* synthetic */ CLASSNAMEo b(BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return new B(binaryOperator);
    }

    public /* synthetic */ Object a(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}

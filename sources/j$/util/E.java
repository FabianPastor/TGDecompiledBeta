package j$.util;

import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

public final /* synthetic */ class E {
    public static void b(F _this, CLASSNAMEt action) {
        action.getClass();
        while (_this.hasNext()) {
            action.accept(((Z) _this).nextDouble());
        }
    }

    public static Double d(F _this) {
        if (!l0.a) {
            return Double.valueOf(((Z) _this).nextDouble());
        }
        l0.b(_this.getClass(), "{0} calling PrimitiveIterator.OfDouble.nextLong()");
        throw null;
    }

    public static void a(F _this, Consumer consumer) {
        if (consumer instanceof CLASSNAMEt) {
            ((Z) _this).e((CLASSNAMEt) consumer);
            return;
        }
        consumer.getClass();
        if (!l0.a) {
            consumer.getClass();
            ((Z) _this).e(new CLASSNAMEa(consumer));
            return;
        }
        l0.b(_this.getClass(), "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
        throw null;
    }
}

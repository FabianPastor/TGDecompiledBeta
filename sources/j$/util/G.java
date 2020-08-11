package j$.util;

import j$.util.function.B;
import j$.util.function.Consumer;

public final /* synthetic */ class G {
    public static void b(H _this, B action) {
        action.getClass();
        while (_this.hasNext()) {
            action.accept(((X) _this).nextInt());
        }
    }

    public static Integer d(H _this) {
        if (!l0.a) {
            return Integer.valueOf(((X) _this).nextInt());
        }
        l0.b(_this.getClass(), "{0} calling PrimitiveIterator.OfInt.nextInt()");
        throw null;
    }

    public static void a(H _this, Consumer consumer) {
        if (consumer instanceof B) {
            ((X) _this).c((B) consumer);
            return;
        }
        consumer.getClass();
        if (!l0.a) {
            consumer.getClass();
            ((X) _this).c(new CLASSNAMEi(consumer));
            return;
        }
        l0.b(_this.getClass(), "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
        throw null;
    }
}

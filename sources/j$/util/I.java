package j$.util;

import j$.util.function.Consumer;
import j$.util.function.J;

public final /* synthetic */ class I {
    public static void b(J _this, J action) {
        action.getClass();
        while (_this.hasNext()) {
            action.accept(((Y) _this).nextLong());
        }
    }

    public static Long d(J _this) {
        if (!l0.a) {
            return Long.valueOf(((Y) _this).nextLong());
        }
        l0.b(_this.getClass(), "{0} calling PrimitiveIterator.OfLong.nextLong()");
        throw null;
    }

    public static void a(J _this, Consumer consumer) {
        if (consumer instanceof J) {
            ((Y) _this).d((J) consumer);
            return;
        }
        consumer.getClass();
        if (!l0.a) {
            consumer.getClass();
            ((Y) _this).d(new CLASSNAMEb(consumer));
            return;
        }
        l0.b(_this.getClass(), "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
        throw null;
    }
}

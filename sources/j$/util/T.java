package j$.util;

import j$.util.function.Consumer;
import j$.util.function.J;

public final /* synthetic */ class T {
    public static boolean b(U _this, Consumer consumer) {
        if (consumer instanceof J) {
            return _this.i((J) consumer);
        }
        if (!l0.a) {
            consumer.getClass();
            return _this.i(new CLASSNAMEb(consumer));
        }
        l0.b(_this.getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
        throw null;
    }

    public static void a(U _this, Consumer consumer) {
        if (consumer instanceof J) {
            _this.d((J) consumer);
        } else if (!l0.a) {
            consumer.getClass();
            _this.d(new CLASSNAMEb(consumer));
        } else {
            l0.b(_this.getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
            throw null;
        }
    }
}

package j$.util;

import j$.util.function.B;
import j$.util.function.Consumer;

public final /* synthetic */ class Q {
    public static boolean b(S _this, Consumer consumer) {
        if (consumer instanceof B) {
            return _this.f((B) consumer);
        }
        if (!l0.a) {
            consumer.getClass();
            return _this.f(new CLASSNAMEi(consumer));
        }
        l0.b(_this.getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
        throw null;
    }

    public static void a(S _this, Consumer consumer) {
        if (consumer instanceof B) {
            _this.c((B) consumer);
        } else if (!l0.a) {
            consumer.getClass();
            _this.c(new CLASSNAMEi(consumer));
        } else {
            l0.b(_this.getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
            throw null;
        }
    }
}

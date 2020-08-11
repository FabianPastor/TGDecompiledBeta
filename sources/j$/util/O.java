package j$.util;

import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

public final /* synthetic */ class O {
    public static boolean b(P _this, Consumer consumer) {
        if (consumer instanceof CLASSNAMEt) {
            return _this.j((CLASSNAMEt) consumer);
        }
        if (!l0.a) {
            consumer.getClass();
            return _this.j(new CLASSNAMEa(consumer));
        }
        l0.b(_this.getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
        throw null;
    }

    public static void a(P _this, Consumer consumer) {
        if (consumer instanceof CLASSNAMEt) {
            _this.e((CLASSNAMEt) consumer);
        } else if (!l0.a) {
            consumer.getClass();
            _this.e(new CLASSNAMEa(consumer));
        } else {
            l0.b(_this.getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
            throw null;
        }
    }
}

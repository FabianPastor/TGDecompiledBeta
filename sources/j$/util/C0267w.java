package j$.util;

import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.u;
import j$.util.function.y;

/* renamed from: j$.util.w  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEw {
    public static void a(C c, Consumer consumer) {
        if (consumer instanceof q) {
            c.e((q) consumer);
        } else if (!W.a) {
            consumer.getClass();
            c.e(new CLASSNAMEj(consumer));
        } else {
            W.a(c.getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
            throw null;
        }
    }

    public static void b(D d, Consumer consumer) {
        if (consumer instanceof u) {
            d.c((u) consumer);
        } else if (!W.a) {
            consumer.getClass();
            d.c(new CLASSNAMEh(consumer));
        } else {
            W.a(d.getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
            throw null;
        }
    }

    public static void c(E e, Consumer consumer) {
        if (consumer instanceof y) {
            e.d((y) consumer);
        } else if (!W.a) {
            consumer.getClass();
            e.d(new CLASSNAMEg(consumer));
        } else {
            W.a(e.getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
            throw null;
        }
    }

    public static boolean d(C c, Consumer consumer) {
        if (consumer instanceof q) {
            return c.o((q) consumer);
        }
        if (!W.a) {
            consumer.getClass();
            return c.o(new CLASSNAMEj(consumer));
        }
        W.a(c.getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
        throw null;
    }

    public static boolean e(D d, Consumer consumer) {
        if (consumer instanceof u) {
            return d.h((u) consumer);
        }
        if (!W.a) {
            consumer.getClass();
            return d.h(new CLASSNAMEh(consumer));
        }
        W.a(d.getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
        throw null;
    }

    public static boolean f(E e, Consumer consumer) {
        if (consumer instanceof y) {
            return e.j((y) consumer);
        }
        if (!W.a) {
            consumer.getClass();
            return e.j(new CLASSNAMEg(consumer));
        }
        W.a(e.getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
        throw null;
    }
}

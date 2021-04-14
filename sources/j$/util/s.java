package j$.util;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;

public final /* synthetic */ class s {
    public static void a(Spliterator.a aVar, Consumer consumer) {
        if (consumer instanceof q) {
            aVar.e((q) consumer);
        } else if (!w.var_a) {
            consumer.getClass();
            aVar.e(new j(consumer));
        } else {
            w.a(aVar.getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
            throw null;
        }
    }

    public static void b(Spliterator.b bVar, Consumer consumer) {
        if (consumer instanceof w) {
            bVar.c((w) consumer);
        } else if (!w.var_a) {
            consumer.getClass();
            bVar.c(new h(consumer));
        } else {
            w.a(bVar.getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
            throw null;
        }
    }

    public static void c(Spliterator.c cVar, Consumer consumer) {
        if (consumer instanceof C) {
            cVar.d((C) consumer);
        } else if (!w.var_a) {
            consumer.getClass();
            cVar.d(new g(consumer));
        } else {
            w.a(cVar.getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
            throw null;
        }
    }

    public static boolean d(Spliterator.a aVar, Consumer consumer) {
        if (consumer instanceof q) {
            return aVar.o((q) consumer);
        }
        if (!w.var_a) {
            consumer.getClass();
            return aVar.o(new j(consumer));
        }
        w.a(aVar.getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
        throw null;
    }

    public static boolean e(Spliterator.b bVar, Consumer consumer) {
        if (consumer instanceof w) {
            return bVar.h((w) consumer);
        }
        if (!w.var_a) {
            consumer.getClass();
            return bVar.h(new h(consumer));
        }
        w.a(bVar.getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
        throw null;
    }

    public static boolean f(Spliterator.c cVar, Consumer consumer) {
        if (consumer instanceof C) {
            return cVar.j((C) consumer);
        }
        if (!w.var_a) {
            consumer.getClass();
            return cVar.j(new g(consumer));
        }
        w.a(cVar.getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
        throw null;
    }
}

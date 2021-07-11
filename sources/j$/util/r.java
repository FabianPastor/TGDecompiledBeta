package j$.util;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import java.util.SortedSet;

public final /* synthetic */ class r {
    public static void a(Spliterator.a aVar, Consumer consumer) {
        if (consumer instanceof q) {
            aVar.e((q) consumer);
        } else if (!v.a) {
            consumer.getClass();
            aVar.e(new j(consumer));
        } else {
            v.a(aVar.getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
            throw null;
        }
    }

    public static void b(Spliterator.b bVar, Consumer consumer) {
        if (consumer instanceof w) {
            bVar.c((w) consumer);
        } else if (!v.a) {
            consumer.getClass();
            bVar.c(new h(consumer));
        } else {
            v.a(bVar.getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
            throw null;
        }
    }

    public static void c(Spliterator.c cVar, Consumer consumer) {
        if (consumer instanceof C) {
            cVar.d((C) consumer);
        } else if (!v.a) {
            consumer.getClass();
            cVar.d(new g(consumer));
        } else {
            v.a(cVar.getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
            throw null;
        }
    }

    public static Spliterator d(SortedSet sortedSet) {
        return new t(sortedSet, sortedSet, 21);
    }

    public static boolean e(Spliterator.a aVar, Consumer consumer) {
        if (consumer instanceof q) {
            return aVar.n((q) consumer);
        }
        if (!v.a) {
            consumer.getClass();
            return aVar.n(new j(consumer));
        }
        v.a(aVar.getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
        throw null;
    }

    public static boolean f(Spliterator.b bVar, Consumer consumer) {
        if (consumer instanceof w) {
            return bVar.g((w) consumer);
        }
        if (!v.a) {
            consumer.getClass();
            return bVar.g(new h(consumer));
        }
        v.a(bVar.getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
        throw null;
    }

    public static boolean g(Spliterator.c cVar, Consumer consumer) {
        if (consumer instanceof C) {
            return cVar.i((C) consumer);
        }
        if (!v.a) {
            consumer.getClass();
            return cVar.i(new g(consumer));
        }
        v.a(cVar.getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
        throw null;
    }
}

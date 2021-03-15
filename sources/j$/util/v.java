package j$.util;

import a.CLASSNAMEz;
import j$.util.Iterator;
import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.t;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class v {

    /* renamed from: a  reason: collision with root package name */
    private static final Spliterator var_a = new g.d();
    private static final Spliterator.b b = new g.b();
    private static final Spliterator.c c = new g.c();
    private static final Spliterator.a d = new g.a();

    class a implements Iterator<T>, Consumer<T>, Iterator {

        /* renamed from: a  reason: collision with root package name */
        boolean var_a = false;
        Object b;
        final /* synthetic */ Spliterator c;

        a(Spliterator spliterator) {
            this.c = spliterator;
        }

        public void accept(Object obj) {
            this.var_a = true;
            this.b = obj;
        }

        public Consumer f(Consumer consumer) {
            consumer.getClass();
            return new CLASSNAMEe(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, CLASSNAMEz.b(consumer));
        }

        public boolean hasNext() {
            if (!this.var_a) {
                this.c.b(this);
            }
            return this.var_a;
        }

        public Object next() {
            if (this.var_a || hasNext()) {
                this.var_a = false;
                return this.b;
            }
            throw new NoSuchElementException();
        }

        public /* synthetic */ void remove() {
            Iterator.CC.a(this);
            throw null;
        }
    }

    class b implements t.b, w {

        /* renamed from: a  reason: collision with root package name */
        boolean var_a = false;
        int b;
        final /* synthetic */ Spliterator.b c;

        b(Spliterator.b bVar) {
            this.c = bVar;
        }

        public void accept(int i) {
            this.var_a = true;
            this.b = i;
        }

        /* renamed from: c */
        public void forEachRemaining(w wVar) {
            wVar.getClass();
            while (hasNext()) {
                wVar.accept(nextInt());
            }
        }

        public void forEachRemaining(Consumer consumer) {
            if (consumer instanceof w) {
                forEachRemaining((w) consumer);
                return;
            }
            consumer.getClass();
            if (!w.var_a) {
                forEachRemaining(new h(consumer));
            } else {
                w.a(b.class, "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
                throw null;
            }
        }

        public boolean hasNext() {
            if (!this.var_a) {
                this.c.h(this);
            }
            return this.var_a;
        }

        public w l(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }

        public Integer next() {
            if (!w.var_a) {
                return Integer.valueOf(nextInt());
            }
            w.a(b.class, "{0} calling PrimitiveIterator.OfInt.nextInt()");
            throw null;
        }

        public int nextInt() {
            if (this.var_a || hasNext()) {
                this.var_a = false;
                return this.b;
            }
            throw new NoSuchElementException();
        }

        public /* synthetic */ void remove() {
            Iterator.CC.a(this);
            throw null;
        }
    }

    class c implements t.c, C {

        /* renamed from: a  reason: collision with root package name */
        boolean var_a = false;
        long b;
        final /* synthetic */ Spliterator.c c;

        c(Spliterator.c cVar) {
            this.c = cVar;
        }

        public void accept(long j) {
            this.var_a = true;
            this.b = j;
        }

        /* renamed from: d */
        public void forEachRemaining(C c2) {
            c2.getClass();
            while (hasNext()) {
                c2.accept(nextLong());
            }
        }

        public void forEachRemaining(Consumer consumer) {
            if (consumer instanceof C) {
                forEachRemaining((C) consumer);
                return;
            }
            consumer.getClass();
            if (!w.var_a) {
                forEachRemaining(new g(consumer));
            } else {
                w.a(c.class, "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
                throw null;
            }
        }

        public C g(C c2) {
            c2.getClass();
            return new CLASSNAMEh(this, c2);
        }

        public boolean hasNext() {
            if (!this.var_a) {
                this.c.j(this);
            }
            return this.var_a;
        }

        public Long next() {
            if (!w.var_a) {
                return Long.valueOf(nextLong());
            }
            w.a(c.class, "{0} calling PrimitiveIterator.OfLong.nextLong()");
            throw null;
        }

        public long nextLong() {
            if (this.var_a || hasNext()) {
                this.var_a = false;
                return this.b;
            }
            throw new NoSuchElementException();
        }

        public /* synthetic */ void remove() {
            Iterator.CC.a(this);
            throw null;
        }
    }

    class d implements t.a, q {

        /* renamed from: a  reason: collision with root package name */
        boolean var_a = false;
        double b;
        final /* synthetic */ Spliterator.a c;

        d(Spliterator.a aVar) {
            this.c = aVar;
        }

        public void accept(double d) {
            this.var_a = true;
            this.b = d;
        }

        /* renamed from: e */
        public void forEachRemaining(q qVar) {
            qVar.getClass();
            while (hasNext()) {
                qVar.accept(nextDouble());
            }
        }

        public void forEachRemaining(Consumer consumer) {
            if (consumer instanceof q) {
                forEachRemaining((q) consumer);
                return;
            }
            consumer.getClass();
            if (!w.var_a) {
                forEachRemaining(new j(consumer));
            } else {
                w.a(d.class, "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
                throw null;
            }
        }

        public boolean hasNext() {
            if (!this.var_a) {
                this.c.o(this);
            }
            return this.var_a;
        }

        public q k(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }

        public Double next() {
            if (!w.var_a) {
                return Double.valueOf(nextDouble());
            }
            w.a(d.class, "{0} calling PrimitiveIterator.OfDouble.nextLong()");
            throw null;
        }

        public double nextDouble() {
            if (this.var_a || hasNext()) {
                this.var_a = false;
                return this.b;
            }
            throw new NoSuchElementException();
        }

        public /* synthetic */ void remove() {
            Iterator.CC.a(this);
            throw null;
        }
    }

    static final class e<T> implements Spliterator<T> {

        /* renamed from: a  reason: collision with root package name */
        private final Object[] var_a;
        private int b;
        private final int c;
        private final int d;

        public e(Object[] objArr, int i, int i2, int i3) {
            this.var_a = objArr;
            this.b = i;
            this.c = i2;
            this.d = i3 | 64 | 16384;
        }

        public boolean b(Consumer consumer) {
            consumer.getClass();
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            Object[] objArr = this.var_a;
            this.b = i + 1;
            consumer.accept(objArr[i]);
            return true;
        }

        public int characteristics() {
            return this.d;
        }

        public long estimateSize() {
            return (long) (this.c - this.b);
        }

        public void forEachRemaining(Consumer consumer) {
            int i;
            consumer.getClass();
            Object[] objArr = this.var_a;
            int length = objArr.length;
            int i2 = this.c;
            if (length >= i2 && (i = this.b) >= 0) {
                this.b = i2;
                if (i < i2) {
                    do {
                        consumer.accept(objArr[i]);
                        i++;
                    } while (i < i2);
                }
            }
        }

        public Comparator getComparator() {
            if (k.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return k.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return k.f(this, i);
        }

        public Spliterator trySplit() {
            int i = this.b;
            int i2 = (this.c + i) >>> 1;
            if (i >= i2) {
                return null;
            }
            Object[] objArr = this.var_a;
            this.b = i2;
            return new e(objArr, i, i2, this.d);
        }
    }

    static final class f implements Spliterator.a {

        /* renamed from: a  reason: collision with root package name */
        private final double[] var_a;
        private int b;
        private final int c;
        private final int d;

        public f(double[] dArr, int i, int i2, int i3) {
            this.var_a = dArr;
            this.b = i;
            this.c = i2;
            this.d = i3 | 64 | 16384;
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return s.d(this, consumer);
        }

        public int characteristics() {
            return this.d;
        }

        /* renamed from: e */
        public void forEachRemaining(q qVar) {
            int i;
            qVar.getClass();
            double[] dArr = this.var_a;
            int length = dArr.length;
            int i2 = this.c;
            if (length >= i2 && (i = this.b) >= 0) {
                this.b = i2;
                if (i < i2) {
                    do {
                        qVar.accept(dArr[i]);
                        i++;
                    } while (i < i2);
                }
            }
        }

        public long estimateSize() {
            return (long) (this.c - this.b);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            s.a(this, consumer);
        }

        public Comparator getComparator() {
            if (k.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return k.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return k.f(this, i);
        }

        /* renamed from: o */
        public boolean tryAdvance(q qVar) {
            qVar.getClass();
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            double[] dArr = this.var_a;
            this.b = i + 1;
            qVar.accept(dArr[i]);
            return true;
        }

        public Spliterator.a trySplit() {
            int i = this.b;
            int i2 = (this.c + i) >>> 1;
            if (i >= i2) {
                return null;
            }
            double[] dArr = this.var_a;
            this.b = i2;
            return new f(dArr, i, i2, this.d);
        }
    }

    private static abstract class g<T, S extends Spliterator<T>, C> {

        private static final class a extends g<Double, Spliterator.a, q> implements Spliterator.a {
            a() {
            }

            public /* synthetic */ boolean b(Consumer consumer) {
                return s.d(this, consumer);
            }

            public void e(q qVar) {
                qVar.getClass();
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                s.a(this, consumer);
            }

            public Comparator getComparator() {
                throw new IllegalStateException();
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return k.e(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return k.f(this, i);
            }

            public boolean o(q qVar) {
                qVar.getClass();
                return false;
            }
        }

        private static final class b extends g<Integer, Spliterator.b, w> implements Spliterator.b {
            b() {
            }

            public /* synthetic */ boolean b(Consumer consumer) {
                return s.e(this, consumer);
            }

            public void c(w wVar) {
                wVar.getClass();
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                s.b(this, consumer);
            }

            public Comparator getComparator() {
                throw new IllegalStateException();
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return k.e(this);
            }

            public boolean h(w wVar) {
                wVar.getClass();
                return false;
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return k.f(this, i);
            }
        }

        private static final class c extends g<Long, Spliterator.c, C> implements Spliterator.c {
            c() {
            }

            public /* synthetic */ boolean b(Consumer consumer) {
                return s.f(this, consumer);
            }

            public void d(C c) {
                c.getClass();
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                s.c(this, consumer);
            }

            public Comparator getComparator() {
                throw new IllegalStateException();
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return k.e(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return k.f(this, i);
            }

            public boolean j(C c) {
                c.getClass();
                return false;
            }
        }

        private static final class d<T> extends g<T, Spliterator<T>, Consumer<? super T>> implements Spliterator<T> {
            d() {
            }

            public boolean b(Consumer consumer) {
                consumer.getClass();
                return false;
            }

            public void forEachRemaining(Consumer consumer) {
                consumer.getClass();
            }

            public Comparator getComparator() {
                throw new IllegalStateException();
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return k.e(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return k.f(this, i);
            }
        }

        g() {
        }

        public int characteristics() {
            return 16448;
        }

        public long estimateSize() {
            return 0;
        }

        public void forEachRemaining(Object obj) {
            obj.getClass();
        }

        public boolean tryAdvance(Object obj) {
            obj.getClass();
            return false;
        }

        public Spliterator trySplit() {
            return null;
        }
    }

    static final class h implements Spliterator.b {

        /* renamed from: a  reason: collision with root package name */
        private final int[] var_a;
        private int b;
        private final int c;
        private final int d;

        public h(int[] iArr, int i, int i2, int i3) {
            this.var_a = iArr;
            this.b = i;
            this.c = i2;
            this.d = i3 | 64 | 16384;
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return s.e(this, consumer);
        }

        /* renamed from: c */
        public void forEachRemaining(w wVar) {
            int i;
            wVar.getClass();
            int[] iArr = this.var_a;
            int length = iArr.length;
            int i2 = this.c;
            if (length >= i2 && (i = this.b) >= 0) {
                this.b = i2;
                if (i < i2) {
                    do {
                        wVar.accept(iArr[i]);
                        i++;
                    } while (i < i2);
                }
            }
        }

        public int characteristics() {
            return this.d;
        }

        public long estimateSize() {
            return (long) (this.c - this.b);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            s.b(this, consumer);
        }

        public Comparator getComparator() {
            if (k.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return k.e(this);
        }

        /* renamed from: h */
        public boolean tryAdvance(w wVar) {
            wVar.getClass();
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            int[] iArr = this.var_a;
            this.b = i + 1;
            wVar.accept(iArr[i]);
            return true;
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return k.f(this, i);
        }

        public Spliterator.b trySplit() {
            int i = this.b;
            int i2 = (this.c + i) >>> 1;
            if (i >= i2) {
                return null;
            }
            int[] iArr = this.var_a;
            this.b = i2;
            return new h(iArr, i, i2, this.d);
        }
    }

    static class i<T> implements Spliterator<T> {

        /* renamed from: a  reason: collision with root package name */
        private final Collection var_a;
        private java.util.Iterator b;
        private final int c;
        private long d;
        private int e;

        public i(Collection collection, int i) {
            this.var_a = collection;
            this.b = null;
            this.c = (i & 4096) == 0 ? i | 64 | 16384 : i;
        }

        public i(java.util.Iterator it, int i) {
            this.var_a = null;
            this.b = it;
            this.d = Long.MAX_VALUE;
            this.c = i & -16449;
        }

        public boolean b(Consumer consumer) {
            consumer.getClass();
            if (this.b == null) {
                this.b = this.var_a.iterator();
                this.d = (long) this.var_a.size();
            }
            if (!this.b.hasNext()) {
                return false;
            }
            consumer.accept(this.b.next());
            return true;
        }

        public int characteristics() {
            return this.c;
        }

        public long estimateSize() {
            if (this.b != null) {
                return this.d;
            }
            this.b = this.var_a.iterator();
            long size = (long) this.var_a.size();
            this.d = size;
            return size;
        }

        public void forEachRemaining(Consumer consumer) {
            consumer.getClass();
            java.util.Iterator it = this.b;
            if (it == null) {
                it = this.var_a.iterator();
                this.b = it;
                this.d = (long) this.var_a.size();
            }
            if (it instanceof Iterator) {
                ((Iterator) it).forEachRemaining(consumer);
            } else {
                Iterator.CC.$default$forEachRemaining(it, consumer);
            }
        }

        public Comparator getComparator() {
            if (k.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return k.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return k.f(this, i);
        }

        public Spliterator trySplit() {
            long j;
            java.util.Iterator it = this.b;
            if (it == null) {
                it = this.var_a.iterator();
                this.b = it;
                j = (long) this.var_a.size();
                this.d = j;
            } else {
                j = this.d;
            }
            if (j <= 1 || !it.hasNext()) {
                return null;
            }
            int i = this.e + 1024;
            if (((long) i) > j) {
                i = (int) j;
            }
            if (i > 33554432) {
                i = 33554432;
            }
            Object[] objArr = new Object[i];
            int i2 = 0;
            do {
                objArr[i2] = it.next();
                i2++;
                if (i2 >= i || !it.hasNext()) {
                    this.e = i2;
                    long j2 = this.d;
                }
                objArr[i2] = it.next();
                i2++;
                break;
            } while (!it.hasNext());
            this.e = i2;
            long j22 = this.d;
            if (j22 != Long.MAX_VALUE) {
                this.d = j22 - ((long) i2);
            }
            return new e(objArr, 0, i2, this.c);
        }
    }

    static final class j implements Spliterator.c {

        /* renamed from: a  reason: collision with root package name */
        private final long[] var_a;
        private int b;
        private final int c;
        private final int d;

        public j(long[] jArr, int i, int i2, int i3) {
            this.var_a = jArr;
            this.b = i;
            this.c = i2;
            this.d = i3 | 64 | 16384;
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return s.f(this, consumer);
        }

        public int characteristics() {
            return this.d;
        }

        /* renamed from: d */
        public void forEachRemaining(C c2) {
            int i;
            c2.getClass();
            long[] jArr = this.var_a;
            int length = jArr.length;
            int i2 = this.c;
            if (length >= i2 && (i = this.b) >= 0) {
                this.b = i2;
                if (i < i2) {
                    do {
                        c2.accept(jArr[i]);
                        i++;
                    } while (i < i2);
                }
            }
        }

        public long estimateSize() {
            return (long) (this.c - this.b);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            s.c(this, consumer);
        }

        public Comparator getComparator() {
            if (k.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return k.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return k.f(this, i);
        }

        /* renamed from: j */
        public boolean tryAdvance(C c2) {
            c2.getClass();
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            long[] jArr = this.var_a;
            this.b = i + 1;
            c2.accept(jArr[i]);
            return true;
        }

        public Spliterator.c trySplit() {
            int i = this.b;
            int i2 = (this.c + i) >>> 1;
            if (i >= i2) {
                return null;
            }
            long[] jArr = this.var_a;
            this.b = i2;
            return new j(jArr, i, i2, this.d);
        }
    }

    private static void a(int i2, int i3, int i4) {
        if (i3 > i4) {
            throw new ArrayIndexOutOfBoundsException("origin(" + i3 + ") > fence(" + i4 + ")");
        } else if (i3 < 0) {
            throw new ArrayIndexOutOfBoundsException(i3);
        } else if (i4 > i2) {
            throw new ArrayIndexOutOfBoundsException(i4);
        }
    }

    public static Spliterator.a b() {
        return d;
    }

    public static Spliterator.b c() {
        return b;
    }

    public static Spliterator.c d() {
        return c;
    }

    public static Spliterator e() {
        return var_a;
    }

    public static t.a f(Spliterator.a aVar) {
        aVar.getClass();
        return new d(aVar);
    }

    public static t.b g(Spliterator.b bVar) {
        bVar.getClass();
        return new b(bVar);
    }

    public static t.c h(Spliterator.c cVar) {
        cVar.getClass();
        return new c(cVar);
    }

    public static java.util.Iterator i(Spliterator spliterator) {
        spliterator.getClass();
        return new a(spliterator);
    }

    public static Spliterator.a j(double[] dArr, int i2, int i3, int i4) {
        dArr.getClass();
        a(dArr.length, i2, i3);
        return new f(dArr, i2, i3, i4);
    }

    public static Spliterator.b k(int[] iArr, int i2, int i3, int i4) {
        iArr.getClass();
        a(iArr.length, i2, i3);
        return new h(iArr, i2, i3, i4);
    }

    public static Spliterator.c l(long[] jArr, int i2, int i3, int i4) {
        jArr.getClass();
        a(jArr.length, i2, i3);
        return new j(jArr, i2, i3, i4);
    }

    public static Spliterator m(Object[] objArr, int i2, int i3, int i4) {
        objArr.getClass();
        a(objArr.length, i2, i3);
        return new e(objArr, i2, i3, i4);
    }

    public static Spliterator n(java.util.Iterator it, int i2) {
        it.getClass();
        return new i(it, i2);
    }
}

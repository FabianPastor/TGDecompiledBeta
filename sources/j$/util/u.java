package j$.util;

import j$.CLASSNAMEw;
import j$.util.Iterator;
import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.CLASSNAMEf;
import j$.util.function.CLASSNAMEg;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.s;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class u {
    private static final Spliterator a = new g.d();
    private static final Spliterator.b b = new g.b();
    private static final Spliterator.c c = new g.c();
    private static final Spliterator.a d = new g.a();

    class a implements Iterator<T>, Consumer<T>, Iterator {
        boolean a = false;
        Object b;
        final /* synthetic */ Spliterator c;

        a(Spliterator spliterator) {
            this.c = spliterator;
        }

        public void accept(Object obj) {
            this.a = true;
            this.b = obj;
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, CLASSNAMEw.b(consumer));
        }

        public boolean hasNext() {
            if (!this.a) {
                this.c.b(this);
            }
            return this.a;
        }

        public Object next() {
            if (this.a || hasNext()) {
                this.a = false;
                return this.b;
            }
            throw new NoSuchElementException();
        }

        public /* synthetic */ void remove() {
            Iterator.CC.a(this);
            throw null;
        }
    }

    class b implements s.b, w {
        boolean a = false;
        int b;
        final /* synthetic */ Spliterator.b c;

        b(Spliterator.b bVar) {
            this.c = bVar;
        }

        public void accept(int i) {
            this.a = true;
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
            if (!v.a) {
                forEachRemaining(new h(consumer));
            } else {
                v.a(b.class, "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
                throw null;
            }
        }

        public boolean hasNext() {
            if (!this.a) {
                this.c.g(this);
            }
            return this.a;
        }

        public w k(w wVar) {
            wVar.getClass();
            return new CLASSNAMEg(this, wVar);
        }

        public Integer next() {
            if (!v.a) {
                return Integer.valueOf(nextInt());
            }
            v.a(b.class, "{0} calling PrimitiveIterator.OfInt.nextInt()");
            throw null;
        }

        public int nextInt() {
            if (this.a || hasNext()) {
                this.a = false;
                return this.b;
            }
            throw new NoSuchElementException();
        }

        public /* synthetic */ void remove() {
            Iterator.CC.a(this);
            throw null;
        }
    }

    class c implements s.c, C {
        boolean a = false;
        long b;
        final /* synthetic */ Spliterator.c c;

        c(Spliterator.c cVar) {
            this.c = cVar;
        }

        public void accept(long j) {
            this.a = true;
            this.b = j;
        }

        /* renamed from: d */
        public void forEachRemaining(C c2) {
            c2.getClass();
            while (hasNext()) {
                c2.accept(nextLong());
            }
        }

        public C f(C c2) {
            c2.getClass();
            return new CLASSNAMEh(this, c2);
        }

        public void forEachRemaining(Consumer consumer) {
            if (consumer instanceof C) {
                forEachRemaining((C) consumer);
                return;
            }
            consumer.getClass();
            if (!v.a) {
                forEachRemaining(new g(consumer));
            } else {
                v.a(c.class, "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
                throw null;
            }
        }

        public boolean hasNext() {
            if (!this.a) {
                this.c.i(this);
            }
            return this.a;
        }

        public Long next() {
            if (!v.a) {
                return Long.valueOf(nextLong());
            }
            v.a(c.class, "{0} calling PrimitiveIterator.OfLong.nextLong()");
            throw null;
        }

        public long nextLong() {
            if (this.a || hasNext()) {
                this.a = false;
                return this.b;
            }
            throw new NoSuchElementException();
        }

        public /* synthetic */ void remove() {
            Iterator.CC.a(this);
            throw null;
        }
    }

    class d implements s.a, q {
        boolean a = false;
        double b;
        final /* synthetic */ Spliterator.a c;

        d(Spliterator.a aVar) {
            this.c = aVar;
        }

        public void accept(double d) {
            this.a = true;
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
            if (!v.a) {
                forEachRemaining(new j(consumer));
            } else {
                v.a(d.class, "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
                throw null;
            }
        }

        public boolean hasNext() {
            if (!this.a) {
                this.c.n(this);
            }
            return this.a;
        }

        public q j(q qVar) {
            qVar.getClass();
            return new CLASSNAMEf(this, qVar);
        }

        public Double next() {
            if (!v.a) {
                return Double.valueOf(nextDouble());
            }
            v.a(d.class, "{0} calling PrimitiveIterator.OfDouble.nextLong()");
            throw null;
        }

        public double nextDouble() {
            if (this.a || hasNext()) {
                this.a = false;
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
        private final Object[] a;
        private int b;
        private final int c;
        private final int d;

        public e(Object[] objArr, int i, int i2, int i3) {
            this.a = objArr;
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
            Object[] objArr = this.a;
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
            Object[] objArr = this.a;
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
            if (j$.time.a.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return j$.time.a.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return j$.time.a.f(this, i);
        }

        public Spliterator trySplit() {
            int i = this.b;
            int i2 = (this.c + i) >>> 1;
            if (i >= i2) {
                return null;
            }
            Object[] objArr = this.a;
            this.b = i2;
            return new e(objArr, i, i2, this.d);
        }
    }

    static final class f implements Spliterator.a {
        private final double[] a;
        private int b;
        private final int c;
        private final int d;

        public f(double[] dArr, int i, int i2, int i3) {
            this.a = dArr;
            this.b = i;
            this.c = i2;
            this.d = i3 | 64 | 16384;
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return r.e(this, consumer);
        }

        public int characteristics() {
            return this.d;
        }

        /* renamed from: e */
        public void forEachRemaining(q qVar) {
            int i;
            qVar.getClass();
            double[] dArr = this.a;
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
            r.a(this, consumer);
        }

        public Comparator getComparator() {
            if (j$.time.a.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return j$.time.a.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return j$.time.a.f(this, i);
        }

        /* renamed from: n */
        public boolean tryAdvance(q qVar) {
            qVar.getClass();
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            double[] dArr = this.a;
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
            double[] dArr = this.a;
            this.b = i2;
            return new f(dArr, i, i2, this.d);
        }
    }

    private static abstract class g<T, S extends Spliterator<T>, C> {

        private static final class a extends g<Double, Spliterator.a, q> implements Spliterator.a {
            a() {
            }

            public /* synthetic */ boolean b(Consumer consumer) {
                return r.e(this, consumer);
            }

            public void e(q qVar) {
                qVar.getClass();
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                r.a(this, consumer);
            }

            public Comparator getComparator() {
                throw new IllegalStateException();
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return j$.time.a.e(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return j$.time.a.f(this, i);
            }

            public boolean n(q qVar) {
                qVar.getClass();
                return false;
            }
        }

        private static final class b extends g<Integer, Spliterator.b, w> implements Spliterator.b {
            b() {
            }

            public /* synthetic */ boolean b(Consumer consumer) {
                return r.f(this, consumer);
            }

            public void c(w wVar) {
                wVar.getClass();
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                r.b(this, consumer);
            }

            public boolean g(w wVar) {
                wVar.getClass();
                return false;
            }

            public Comparator getComparator() {
                throw new IllegalStateException();
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return j$.time.a.e(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return j$.time.a.f(this, i);
            }
        }

        private static final class c extends g<Long, Spliterator.c, C> implements Spliterator.c {
            c() {
            }

            public /* synthetic */ boolean b(Consumer consumer) {
                return r.g(this, consumer);
            }

            public void d(C c) {
                c.getClass();
            }

            public /* synthetic */ void forEachRemaining(Consumer consumer) {
                r.c(this, consumer);
            }

            public Comparator getComparator() {
                throw new IllegalStateException();
            }

            public /* synthetic */ long getExactSizeIfKnown() {
                return j$.time.a.e(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return j$.time.a.f(this, i);
            }

            public boolean i(C c) {
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
                return j$.time.a.e(this);
            }

            public /* synthetic */ boolean hasCharacteristics(int i) {
                return j$.time.a.f(this, i);
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
        private final int[] a;
        private int b;
        private final int c;
        private final int d;

        public h(int[] iArr, int i, int i2, int i3) {
            this.a = iArr;
            this.b = i;
            this.c = i2;
            this.d = i3 | 64 | 16384;
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return r.f(this, consumer);
        }

        /* renamed from: c */
        public void forEachRemaining(w wVar) {
            int i;
            wVar.getClass();
            int[] iArr = this.a;
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
            r.b(this, consumer);
        }

        /* renamed from: g */
        public boolean tryAdvance(w wVar) {
            wVar.getClass();
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            int[] iArr = this.a;
            this.b = i + 1;
            wVar.accept(iArr[i]);
            return true;
        }

        public Comparator getComparator() {
            if (j$.time.a.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return j$.time.a.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return j$.time.a.f(this, i);
        }

        public Spliterator.b trySplit() {
            int i = this.b;
            int i2 = (this.c + i) >>> 1;
            if (i >= i2) {
                return null;
            }
            int[] iArr = this.a;
            this.b = i2;
            return new h(iArr, i, i2, this.d);
        }
    }

    static class i<T> implements Spliterator<T> {
        private final Collection a;
        private java.util.Iterator b;
        private final int c;
        private long d;
        private int e;

        public i(Collection collection, int i) {
            this.a = collection;
            this.b = null;
            this.c = (i & 4096) == 0 ? i | 64 | 16384 : i;
        }

        public i(java.util.Iterator it, int i) {
            this.a = null;
            this.b = it;
            this.d = Long.MAX_VALUE;
            this.c = i & -16449;
        }

        public boolean b(Consumer consumer) {
            consumer.getClass();
            if (this.b == null) {
                this.b = this.a.iterator();
                this.d = (long) this.a.size();
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
            this.b = this.a.iterator();
            long size = (long) this.a.size();
            this.d = size;
            return size;
        }

        public void forEachRemaining(Consumer consumer) {
            consumer.getClass();
            java.util.Iterator it = this.b;
            if (it == null) {
                it = this.a.iterator();
                this.b = it;
                this.d = (long) this.a.size();
            }
            if (it instanceof Iterator) {
                ((Iterator) it).forEachRemaining(consumer);
            } else {
                Iterator.CC.$default$forEachRemaining(it, consumer);
            }
        }

        public Comparator getComparator() {
            if (j$.time.a.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return j$.time.a.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return j$.time.a.f(this, i);
        }

        public Spliterator trySplit() {
            long j;
            java.util.Iterator it = this.b;
            if (it == null) {
                it = this.a.iterator();
                this.b = it;
                j = (long) this.a.size();
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
        private final long[] a;
        private int b;
        private final int c;
        private final int d;

        public j(long[] jArr, int i, int i2, int i3) {
            this.a = jArr;
            this.b = i;
            this.c = i2;
            this.d = i3 | 64 | 16384;
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return r.g(this, consumer);
        }

        public int characteristics() {
            return this.d;
        }

        /* renamed from: d */
        public void forEachRemaining(C c2) {
            int i;
            c2.getClass();
            long[] jArr = this.a;
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
            r.c(this, consumer);
        }

        public Comparator getComparator() {
            if (j$.time.a.f(this, 4)) {
                return null;
            }
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return j$.time.a.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return j$.time.a.f(this, i);
        }

        /* renamed from: i */
        public boolean tryAdvance(C c2) {
            c2.getClass();
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            long[] jArr = this.a;
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
            long[] jArr = this.a;
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
        return a;
    }

    public static s.a f(Spliterator.a aVar) {
        aVar.getClass();
        return new d(aVar);
    }

    public static s.b g(Spliterator.b bVar) {
        bVar.getClass();
        return new b(bVar);
    }

    public static s.c h(Spliterator.c cVar) {
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

    public static Spliterator m(Collection collection, int i2) {
        collection.getClass();
        return new i(collection, i2);
    }

    public static Spliterator n(Object[] objArr, int i2, int i3, int i4) {
        objArr.getClass();
        a(objArr.length, i2, i3);
        return new e(objArr, i2, i3, i4);
    }

    public static Spliterator o(java.util.Iterator it, int i2) {
        it.getClass();
        return new i(it, i2);
    }
}

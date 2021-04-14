package j$.util.concurrent;

import a.P0;
import a.R0;
import a.T0;
import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.k;
import j$.util.s;
import j$.util.stream.Q1;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class e extends Random {
    private static final AtomicInteger d = new AtomicInteger();
    private static final AtomicLong e;
    private static final ThreadLocal f = new ThreadLocal();
    private static final ThreadLocal g = new a();

    /* renamed from: a  reason: collision with root package name */
    long var_a;
    int b;
    boolean c = true;

    class a extends ThreadLocal<e> {
        a() {
        }

        /* access modifiers changed from: protected */
        public Object initialValue() {
            return new e((d) null);
        }
    }

    static final class b implements Spliterator.a {

        /* renamed from: a  reason: collision with root package name */
        long var_a;
        final long b;
        final double c;
        final double d;

        b(long j, long j2, double d2, double d3) {
            this.var_a = j;
            this.b = j2;
            this.c = d2;
            this.d = d3;
        }

        /* renamed from: a */
        public b trySplit() {
            long j = this.var_a;
            long j2 = (this.b + j) >>> 1;
            if (j2 <= j) {
                return null;
            }
            this.var_a = j2;
            return new b(j, j2, this.c, this.d);
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return s.d(this, consumer);
        }

        public int characteristics() {
            return 17728;
        }

        /* renamed from: e */
        public void forEachRemaining(q qVar) {
            qVar.getClass();
            long j = this.var_a;
            long j2 = this.b;
            if (j < j2) {
                this.var_a = j2;
                double d2 = this.c;
                double d3 = this.d;
                e b2 = e.b();
                do {
                    qVar.accept(b2.d(d2, d3));
                    j++;
                } while (j < j2);
            }
        }

        public long estimateSize() {
            return this.b - this.var_a;
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

        /* renamed from: o */
        public boolean tryAdvance(q qVar) {
            qVar.getClass();
            long j = this.var_a;
            if (j >= this.b) {
                return false;
            }
            qVar.accept(e.b().d(this.c, this.d));
            this.var_a = j + 1;
            return true;
        }
    }

    static final class c implements Spliterator.b {

        /* renamed from: a  reason: collision with root package name */
        long var_a;
        final long b;
        final int c;
        final int d;

        c(long j, long j2, int i, int i2) {
            this.var_a = j;
            this.b = j2;
            this.c = i;
            this.d = i2;
        }

        /* renamed from: a */
        public c trySplit() {
            long j = this.var_a;
            long j2 = (this.b + j) >>> 1;
            if (j2 <= j) {
                return null;
            }
            this.var_a = j2;
            return new c(j, j2, this.c, this.d);
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return s.e(this, consumer);
        }

        /* renamed from: c */
        public void forEachRemaining(w wVar) {
            wVar.getClass();
            long j = this.var_a;
            long j2 = this.b;
            if (j < j2) {
                this.var_a = j2;
                int i = this.c;
                int i2 = this.d;
                e b2 = e.b();
                do {
                    wVar.accept(b2.e(i, i2));
                    j++;
                } while (j < j2);
            }
        }

        public int characteristics() {
            return 17728;
        }

        public long estimateSize() {
            return this.b - this.var_a;
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

        /* renamed from: h */
        public boolean tryAdvance(w wVar) {
            wVar.getClass();
            long j = this.var_a;
            if (j >= this.b) {
                return false;
            }
            wVar.accept(e.b().e(this.c, this.d));
            this.var_a = j + 1;
            return true;
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return k.f(this, i);
        }
    }

    static final class d implements Spliterator.c {

        /* renamed from: a  reason: collision with root package name */
        long var_a;
        final long b;
        final long c;
        final long d;

        d(long j, long j2, long j3, long j4) {
            this.var_a = j;
            this.b = j2;
            this.c = j3;
            this.d = j4;
        }

        /* renamed from: a */
        public d trySplit() {
            long j = this.var_a;
            long j2 = (this.b + j) >>> 1;
            if (j2 <= j) {
                return null;
            }
            this.var_a = j2;
            return new d(j, j2, this.c, this.d);
        }

        public /* synthetic */ boolean b(Consumer consumer) {
            return s.f(this, consumer);
        }

        public int characteristics() {
            return 17728;
        }

        /* renamed from: d */
        public void forEachRemaining(C c2) {
            c2.getClass();
            long j = this.var_a;
            long j2 = this.b;
            if (j < j2) {
                this.var_a = j2;
                long j3 = this.c;
                long j4 = this.d;
                e b2 = e.b();
                do {
                    c2.accept(b2.f(j3, j4));
                    j++;
                } while (j < j2);
            }
        }

        public long estimateSize() {
            return this.b - this.var_a;
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

        /* renamed from: j */
        public boolean tryAdvance(C c2) {
            c2.getClass();
            long j = this.var_a;
            if (j >= this.b) {
                return false;
            }
            c2.accept(e.b().f(this.c, this.d));
            this.var_a = j + 1;
            return true;
        }
    }

    static {
        long j;
        if (((Boolean) AccessController.doPrivileged(new d())).booleanValue()) {
            byte[] seed = SecureRandom.getSeed(8);
            j = ((long) seed[0]) & 255;
            for (int i = 1; i < 8; i++) {
                j = (j << 8) | (((long) seed[i]) & 255);
            }
        } else {
            j = i(System.nanoTime()) ^ i(System.currentTimeMillis());
        }
        e = new AtomicLong(j);
        new ObjectStreamField("rnd", Long.TYPE);
        new ObjectStreamField("initialized", Boolean.TYPE);
    }

    private e() {
    }

    e(d dVar) {
    }

    static final int a(int i) {
        int i2 = i ^ (i << 13);
        int i3 = i2 ^ (i2 >>> 17);
        int i4 = i3 ^ (i3 << 5);
        ((e) g.get()).b = i4;
        return i4;
    }

    public static e b() {
        e eVar = (e) g.get();
        if (eVar.b == 0) {
            g();
        }
        return eVar;
    }

    static final int c() {
        return ((e) g.get()).b;
    }

    static final void g() {
        int addAndGet = d.addAndGet(-NUM);
        if (addAndGet == 0) {
            addAndGet = 1;
        }
        long i = i(e.getAndAdd(-4942790177534073029L));
        e eVar = (e) g.get();
        eVar.var_a = i;
        eVar.b = addAndGet;
    }

    private static int h(long j) {
        long j2 = (j ^ (j >>> 33)) * -49064778989728563L;
        return (int) (((j2 ^ (j2 >>> 33)) * -4265267296055464877L) >>> 32);
    }

    private static long i(long j) {
        long j2 = (j ^ (j >>> 33)) * -49064778989728563L;
        long j3 = (j2 ^ (j2 >>> 33)) * -4265267296055464877L;
        return j3 ^ (j3 >>> 33);
    }

    /* access modifiers changed from: package-private */
    public final double d(double d2, double d3) {
        double nextLong = (double) (nextLong() >>> 11);
        Double.isNaN(nextLong);
        double d4 = nextLong * 1.1102230246251565E-16d;
        if (d2 >= d3) {
            return d4;
        }
        double d5 = ((d3 - d2) * d4) + d2;
        return d5 >= d3 ? Double.longBitsToDouble(Double.doubleToLongBits(d3) - 1) : d5;
    }

    public DoubleStream doubles() {
        return P0.m0(Q1.o(new b(0, Long.MAX_VALUE, Double.MAX_VALUE, 0.0d), false));
    }

    /* access modifiers changed from: package-private */
    public final int e(int i, int i2) {
        int i3;
        int h = h(j());
        if (i >= i2) {
            return h;
        }
        int i4 = i2 - i;
        int i5 = i4 - 1;
        if ((i4 & i5) == 0) {
            i3 = h & i5;
        } else if (i4 > 0) {
            int i6 = h >>> 1;
            while (true) {
                int i7 = i6 + i5;
                i3 = i6 % i4;
                if (i7 - i3 >= 0) {
                    break;
                }
                i6 = h(j()) >>> 1;
            }
        } else {
            while (true) {
                if (h >= i && h < i2) {
                    return h;
                }
                h = h(j());
            }
        }
        return i3 + i;
    }

    /* access modifiers changed from: package-private */
    public final long f(long j, long j2) {
        long i = i(j());
        if (j >= j2) {
            return i;
        }
        long j3 = j2 - j;
        long j4 = j3 - 1;
        if ((j3 & j4) == 0) {
            return (i & j4) + j;
        }
        if (j3 > 0) {
            while (true) {
                long j5 = i >>> 1;
                long j6 = j5 + j4;
                long j7 = j5 % j3;
                if (j6 - j7 >= 0) {
                    return j7 + j;
                }
                i = i(j());
            }
        } else {
            while (true) {
                if (i >= j && i < j2) {
                    return i;
                }
                i = i(j());
            }
        }
    }

    public IntStream ints() {
        return R0.m0(Q1.p(new c(0, Long.MAX_VALUE, Integer.MAX_VALUE, 0), false));
    }

    /* access modifiers changed from: package-private */
    public final long j() {
        long j = this.var_a - 7046029254386353131L;
        this.var_a = j;
        return j;
    }

    public LongStream longs() {
        return T0.m0(Q1.q(new d(0, Long.MAX_VALUE, Long.MAX_VALUE, 0), false));
    }

    /* access modifiers changed from: protected */
    public int next(int i) {
        return (int) (i(j()) >>> (64 - i));
    }

    public boolean nextBoolean() {
        return h(j()) < 0;
    }

    public double nextDouble() {
        double i = (double) (i(j()) >>> 11);
        Double.isNaN(i);
        return i * 1.1102230246251565E-16d;
    }

    public float nextFloat() {
        return ((float) (h(j()) >>> 8)) * 5.9604645E-8f;
    }

    public double nextGaussian() {
        ThreadLocal threadLocal = f;
        Double d2 = (Double) threadLocal.get();
        if (d2 != null) {
            threadLocal.set((Object) null);
            return d2.doubleValue();
        }
        while (true) {
            double nextDouble = (nextDouble() * 2.0d) - 1.0d;
            double nextDouble2 = (nextDouble() * 2.0d) - 1.0d;
            double d3 = (nextDouble2 * nextDouble2) + (nextDouble * nextDouble);
            if (d3 < 1.0d && d3 != 0.0d) {
                double sqrt = StrictMath.sqrt((StrictMath.log(d3) * -2.0d) / d3);
                f.set(new Double(nextDouble2 * sqrt));
                return nextDouble * sqrt;
            }
        }
    }

    public int nextInt() {
        return h(j());
    }

    public int nextInt(int i) {
        if (i > 0) {
            int h = h(j());
            int i2 = i - 1;
            if ((i & i2) == 0) {
                return h & i2;
            }
            while (true) {
                int i3 = h >>> 1;
                int i4 = i3 + i2;
                int i5 = i3 % i;
                if (i4 - i5 >= 0) {
                    return i5;
                }
                h = h(j());
            }
        } else {
            throw new IllegalArgumentException("bound must be positive");
        }
    }

    public long nextLong() {
        return i(j());
    }

    public void setSeed(long j) {
        if (this.c) {
            throw new UnsupportedOperationException();
        }
    }

    public DoubleStream doubles(double d2, double d3) {
        if (d2 < d3) {
            return P0.m0(Q1.o(new b(0, Long.MAX_VALUE, d2, d3), false));
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    public IntStream ints(int i, int i2) {
        if (i < i2) {
            return R0.m0(Q1.p(new c(0, Long.MAX_VALUE, i, i2), false));
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    public LongStream longs(long j) {
        if (j >= 0) {
            return T0.m0(Q1.q(new d(0, j, Long.MAX_VALUE, 0), false));
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    public DoubleStream doubles(long j) {
        if (j >= 0) {
            return P0.m0(Q1.o(new b(0, j, Double.MAX_VALUE, 0.0d), false));
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    public IntStream ints(long j) {
        if (j >= 0) {
            return R0.m0(Q1.p(new c(0, j, Integer.MAX_VALUE, 0), false));
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    public LongStream longs(long j, long j2) {
        if (j < j2) {
            return T0.m0(Q1.q(new d(0, Long.MAX_VALUE, j, j2), false));
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    public DoubleStream doubles(long j, double d2, double d3) {
        if (j < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        } else if (d2 < d3) {
            return P0.m0(Q1.o(new b(0, j, d2, d3), false));
        } else {
            throw new IllegalArgumentException("bound must be greater than origin");
        }
    }

    public IntStream ints(long j, int i, int i2) {
        if (j < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        } else if (i < i2) {
            return R0.m0(Q1.p(new c(0, j, i, i2), false));
        } else {
            throw new IllegalArgumentException("bound must be greater than origin");
        }
    }

    public LongStream longs(long j, long j2, long j3) {
        if (j < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        } else if (j2 < j3) {
            return T0.m0(Q1.q(new d(0, j, j2, j3), false));
        } else {
            throw new IllegalArgumentException("bound must be greater than origin");
        }
    }
}

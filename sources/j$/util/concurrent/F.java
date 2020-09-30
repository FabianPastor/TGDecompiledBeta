package j$.util.concurrent;

import j$.l0;
import j$.m0;
import j$.n0;
import j$.util.stream.A2;
import j$.util.stream.M1;
import j$.util.stream.W2;
import j$.util.stream.b7;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class F extends Random {
    private static final AtomicInteger d = new AtomicInteger();
    private static final AtomicLong e = new AtomicLong(h());
    private static final ThreadLocal f = new ThreadLocal();
    private static final ThreadLocal g = new B();
    long a;
    int b;
    boolean c;

    public /* synthetic */ DoubleStream doubles() {
        return l0.c(c());
    }

    public /* synthetic */ DoubleStream doubles(double d2, double d3) {
        return l0.c(d(d2, d3));
    }

    public /* synthetic */ DoubleStream doubles(long j) {
        return l0.c(e(j));
    }

    public /* synthetic */ DoubleStream doubles(long j, double d2, double d3) {
        return l0.c(f(j, d2, d3));
    }

    public /* synthetic */ IntStream ints() {
        return m0.c(l());
    }

    public /* synthetic */ IntStream ints(int i, int i2) {
        return m0.c(m(i, i2));
    }

    public /* synthetic */ IntStream ints(long j) {
        return m0.c(n(j));
    }

    public /* synthetic */ IntStream ints(long j, int i, int i2) {
        return m0.c(o(j, i, i2));
    }

    public /* synthetic */ LongStream longs() {
        return n0.c(q());
    }

    public /* synthetic */ LongStream longs(long j) {
        return n0.c(r(j));
    }

    public /* synthetic */ LongStream longs(long j, long j2) {
        return n0.c(s(j, j2));
    }

    public /* synthetic */ LongStream longs(long j, long j2, long j3) {
        return n0.c(t(j, j2, j3));
    }

    /* synthetic */ F(A x0) {
        this();
    }

    static {
        ObjectStreamField[] objectStreamFieldArr = {new ObjectStreamField("rnd", Long.TYPE), new ObjectStreamField("initialized", Boolean.TYPE)};
    }

    private static long h() {
        if (!((Boolean) AccessController.doPrivileged(new A())).booleanValue()) {
            return v(System.currentTimeMillis()) ^ v(System.nanoTime());
        }
        byte[] seedBytes = SecureRandom.getSeed(8);
        long s = ((long) seedBytes[0]) & 255;
        for (int i = 1; i < 8; i++) {
            s = (s << 8) | (((long) seedBytes[i]) & 255);
        }
        return s;
    }

    private static long v(long z) {
        long z2 = ((z >>> 33) ^ z) * -49064778989728563L;
        long z3 = ((z2 >>> 33) ^ z2) * -4265267296055464877L;
        return (z3 >>> 33) ^ z3;
    }

    private static int u(long z) {
        long z2 = ((z >>> 33) ^ z) * -49064778989728563L;
        return (int) ((((z2 >>> 33) ^ z2) * -4265267296055464877L) >>> 32);
    }

    private F() {
        this.c = true;
    }

    static final void p() {
        int p = d.addAndGet(-NUM);
        int probe = p == 0 ? 1 : p;
        long seed = v(e.getAndAdd(-4942790177534073029L));
        F t = (F) g.get();
        t.a = seed;
        t.b = probe;
    }

    public static F b() {
        F t = (F) g.get();
        if (t.b == 0) {
            p();
        }
        return t;
    }

    public void setSeed(long seed) {
        if (this.c) {
            throw new UnsupportedOperationException();
        }
    }

    /* access modifiers changed from: package-private */
    public final long w() {
        long j = this.a - 7046029254386353131L;
        long r = j;
        this.a = j;
        return r;
    }

    /* access modifiers changed from: protected */
    public int next(int bits) {
        return (int) (v(w()) >>> (64 - bits));
    }

    /* access modifiers changed from: package-private */
    public final long k(long origin, long bound) {
        long r = v(w());
        if (origin >= bound) {
            return r;
        }
        long n = bound - origin;
        long m = n - 1;
        if ((n & m) == 0) {
            return (r & m) + origin;
        }
        if (n > 0) {
            long u = r >>> 1;
            while (true) {
                long j = u % n;
                long r2 = j;
                if ((u + m) - j >= 0) {
                    return r2 + origin;
                }
                u = v(w()) >>> 1;
            }
        } else {
            while (true) {
                if (r >= origin && r < bound) {
                    return r;
                }
                r = v(w());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final int j(int origin, int bound) {
        int r = u(w());
        if (origin >= bound) {
            return r;
        }
        int n = bound - origin;
        int m = n - 1;
        if ((n & m) == 0) {
            return (r & m) + origin;
        }
        if (n > 0) {
            int u = r >>> 1;
            while (true) {
                int i = u % n;
                int r2 = i;
                if ((u + m) - i >= 0) {
                    return r2 + origin;
                }
                u = u(w()) >>> 1;
            }
        } else {
            while (true) {
                if (r >= origin && r < bound) {
                    return r;
                }
                r = u(w());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final double i(double origin, double bound) {
        double nextLong = (double) (nextLong() >>> 11);
        Double.isNaN(nextLong);
        double r = nextLong * 1.1102230246251565E-16d;
        if (origin >= bound) {
            return r;
        }
        double r2 = ((bound - origin) * r) + origin;
        if (r2 >= bound) {
            return Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1);
        }
        return r2;
    }

    public int nextInt() {
        return u(w());
    }

    public int nextInt(int bound) {
        if (bound > 0) {
            int r = u(w());
            int m = bound - 1;
            if ((bound & m) == 0) {
                return r & m;
            }
            int u = r >>> 1;
            while (true) {
                int i = u % bound;
                int r2 = i;
                if ((u + m) - i >= 0) {
                    return r2;
                }
                u = u(w()) >>> 1;
            }
        } else {
            throw new IllegalArgumentException("bound must be positive");
        }
    }

    public long nextLong() {
        return v(w());
    }

    public double nextDouble() {
        double v = (double) (v(w()) >>> 11);
        Double.isNaN(v);
        return v * 1.1102230246251565E-16d;
    }

    public boolean nextBoolean() {
        return u(w()) < 0;
    }

    public float nextFloat() {
        return ((float) (u(w()) >>> 8)) * 5.9604645E-8f;
    }

    public double nextGaussian() {
        Double d2 = (Double) f.get();
        if (d2 != null) {
            f.set((Object) null);
            return d2.doubleValue();
        }
        while (true) {
            double v1 = (nextDouble() * 2.0d) - 1.0d;
            double v2 = (nextDouble() * 2.0d) - 1.0d;
            double s = (v1 * v1) + (v2 * v2);
            if (s < 1.0d && s != 0.0d) {
                double multiplier = StrictMath.sqrt((StrictMath.log(s) * -2.0d) / s);
                f.set(new Double(v2 * multiplier));
                return v1 * multiplier;
            }
        }
    }

    public A2 n(long streamSize) {
        if (streamSize >= 0) {
            return b7.b(new D(0, streamSize, Integer.MAX_VALUE, 0), false);
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    public A2 l() {
        return b7.b(new D(0, Long.MAX_VALUE, Integer.MAX_VALUE, 0), false);
    }

    public A2 o(long streamSize, int randomNumberOrigin, int randomNumberBound) {
        if (streamSize < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        } else if (randomNumberOrigin < randomNumberBound) {
            return b7.b(new D(0, streamSize, randomNumberOrigin, randomNumberBound), false);
        } else {
            throw new IllegalArgumentException("bound must be greater than origin");
        }
    }

    public A2 m(int randomNumberOrigin, int randomNumberBound) {
        if (randomNumberOrigin < randomNumberBound) {
            return b7.b(new D(0, Long.MAX_VALUE, randomNumberOrigin, randomNumberBound), false);
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    public W2 r(long streamSize) {
        if (streamSize >= 0) {
            return b7.c(new E(0, streamSize, Long.MAX_VALUE, 0), false);
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    public W2 q() {
        return b7.c(new E(0, Long.MAX_VALUE, Long.MAX_VALUE, 0), false);
    }

    public W2 t(long streamSize, long randomNumberOrigin, long randomNumberBound) {
        if (streamSize < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        } else if (randomNumberOrigin < randomNumberBound) {
            return b7.c(new E(0, streamSize, randomNumberOrigin, randomNumberBound), false);
        } else {
            throw new IllegalArgumentException("bound must be greater than origin");
        }
    }

    public W2 s(long randomNumberOrigin, long randomNumberBound) {
        if (randomNumberOrigin < randomNumberBound) {
            return b7.c(new E(0, Long.MAX_VALUE, randomNumberOrigin, randomNumberBound), false);
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    public M1 e(long streamSize) {
        if (streamSize >= 0) {
            return b7.a(new C(0, streamSize, Double.MAX_VALUE, 0.0d), false);
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    public M1 c() {
        return b7.a(new C(0, Long.MAX_VALUE, Double.MAX_VALUE, 0.0d), false);
    }

    public M1 f(long streamSize, double randomNumberOrigin, double randomNumberBound) {
        if (streamSize < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        } else if (randomNumberOrigin < randomNumberBound) {
            return b7.a(new C(0, streamSize, randomNumberOrigin, randomNumberBound), false);
        } else {
            throw new IllegalArgumentException("bound must be greater than origin");
        }
    }

    public M1 d(double randomNumberOrigin, double randomNumberBound) {
        if (randomNumberOrigin < randomNumberBound) {
            return b7.a(new C(0, Long.MAX_VALUE, randomNumberOrigin, randomNumberBound), false);
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    static final int g() {
        return ((F) g.get()).b;
    }

    static final int a(int probe) {
        int probe2 = probe ^ (probe << 13);
        int probe3 = probe2 ^ (probe2 >>> 17);
        int probe4 = probe3 ^ (probe3 << 5);
        ((F) g.get()).b = probe4;
        return probe4;
    }
}

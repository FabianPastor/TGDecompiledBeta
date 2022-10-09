package j$.util.concurrent;

import j$.util.stream.AbstractCLASSNAMEo1;
import j$.wrappers.C$r8$wrapper$java$util$stream$IntStream$WRP;
import j$.wrappers.M0;
import j$.wrappers.O0;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
/* loaded from: classes2.dex */
public class i extends Random {
    private static final AtomicInteger d = new AtomicInteger();
    private static final AtomicLong e;
    private static final ThreadLocal f;
    private static final ThreadLocal g;
    long a;
    int b;
    boolean c = true;

    static {
        long i;
        if (((Boolean) AccessController.doPrivileged(new d())).booleanValue()) {
            byte[] seed = SecureRandom.getSeed(8);
            i = seed[0] & 255;
            for (int i2 = 1; i2 < 8; i2++) {
                i = (i << 8) | (seed[i2] & 255);
            }
        } else {
            i = i(System.nanoTime()) ^ i(System.currentTimeMillis());
        }
        e = new AtomicLong(i);
        f = new ThreadLocal();
        g = new e();
        new ObjectStreamField("rnd", Long.TYPE);
        new ObjectStreamField("initialized", Boolean.TYPE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public i(d dVar) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int a(int i) {
        int i2 = i ^ (i << 13);
        int i3 = i2 ^ (i2 >>> 17);
        int i4 = i3 ^ (i3 << 5);
        ((i) g.get()).b = i4;
        return i4;
    }

    public static i b() {
        i iVar = (i) g.get();
        if (iVar.b == 0) {
            g();
        }
        return iVar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int c() {
        return ((i) g.get()).b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void g() {
        int addAndGet = d.addAndGet(-NUM);
        if (addAndGet == 0) {
            addAndGet = 1;
        }
        long i = i(e.getAndAdd(-4942790177534073029L));
        i iVar = (i) g.get();
        iVar.a = i;
        iVar.b = addAndGet;
    }

    private static int h(long j) {
        long j2 = (j ^ (j >>> 33)) * (-49064778989728563L);
        return (int) (((j2 ^ (j2 >>> 33)) * (-4265267296055464877L)) >>> 32);
    }

    private static long i(long j) {
        long j2 = (j ^ (j >>> 33)) * (-49064778989728563L);
        long j3 = (j2 ^ (j2 >>> 33)) * (-4265267296055464877L);
        return j3 ^ (j3 >>> 33);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final double d(double d2, double d3) {
        double nextLong = nextLong() >>> 11;
        Double.isNaN(nextLong);
        double d4 = nextLong * 1.1102230246251565E-16d;
        if (d2 < d3) {
            double d5 = ((d3 - d2) * d4) + d2;
            return d5 >= d3 ? Double.longBitsToDouble(Double.doubleToLongBits(d3) - 1) : d5;
        }
        return d4;
    }

    @Override // java.util.Random
    public DoubleStream doubles() {
        return M0.n0(AbstractCLASSNAMEo1.r(new f(0L, Long.MAX_VALUE, Double.MAX_VALUE, 0.0d), false));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int e(int i, int i2) {
        int i3;
        int h = h(j());
        if (i < i2) {
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
        return h;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long f(long j, long j2) {
        long i = i(j());
        if (j < j2) {
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
        } else {
            return i;
        }
    }

    @Override // java.util.Random
    public IntStream ints() {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(AbstractCLASSNAMEo1.s(new g(0L, Long.MAX_VALUE, Integer.MAX_VALUE, 0), false));
    }

    final long j() {
        long j = this.a - 7046029254386353131L;
        this.a = j;
        return j;
    }

    @Override // java.util.Random
    public LongStream longs() {
        return O0.n0(AbstractCLASSNAMEo1.t(new h(0L, Long.MAX_VALUE, Long.MAX_VALUE, 0L), false));
    }

    @Override // java.util.Random
    protected int next(int i) {
        return (int) (i(j()) >>> (64 - i));
    }

    @Override // java.util.Random
    public boolean nextBoolean() {
        return h(j()) < 0;
    }

    @Override // java.util.Random
    public double nextDouble() {
        double i = i(j()) >>> 11;
        Double.isNaN(i);
        return i * 1.1102230246251565E-16d;
    }

    @Override // java.util.Random
    public float nextFloat() {
        return (h(j()) >>> 8) * 5.9604645E-8f;
    }

    @Override // java.util.Random
    public double nextGaussian() {
        ThreadLocal threadLocal = f;
        Double d2 = (Double) threadLocal.get();
        if (d2 != null) {
            threadLocal.set(null);
            return d2.doubleValue();
        }
        while (true) {
            double nextDouble = (nextDouble() * 2.0d) - 1.0d;
            double nextDouble2 = (nextDouble() * 2.0d) - 1.0d;
            double d3 = (nextDouble2 * nextDouble2) + (nextDouble * nextDouble);
            if (d3 < 1.0d && d3 != 0.0d) {
                double sqrt = StrictMath.sqrt((StrictMath.log(d3) * (-2.0d)) / d3);
                f.set(new Double(nextDouble2 * sqrt));
                return nextDouble * sqrt;
            }
        }
    }

    @Override // java.util.Random
    public int nextInt() {
        return h(j());
    }

    @Override // java.util.Random
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

    @Override // java.util.Random
    public long nextLong() {
        return i(j());
    }

    @Override // java.util.Random
    public void setSeed(long j) {
        if (!this.c) {
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Random
    public DoubleStream doubles(double d2, double d3) {
        if (d2 < d3) {
            return M0.n0(AbstractCLASSNAMEo1.r(new f(0L, Long.MAX_VALUE, d2, d3), false));
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    @Override // java.util.Random
    public IntStream ints(int i, int i2) {
        if (i < i2) {
            return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(AbstractCLASSNAMEo1.s(new g(0L, Long.MAX_VALUE, i, i2), false));
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    @Override // java.util.Random
    public LongStream longs(long j) {
        if (j >= 0) {
            return O0.n0(AbstractCLASSNAMEo1.t(new h(0L, j, Long.MAX_VALUE, 0L), false));
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    public DoubleStream doubles(long j) {
        if (j >= 0) {
            return M0.n0(AbstractCLASSNAMEo1.r(new f(0L, j, Double.MAX_VALUE, 0.0d), false));
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    public IntStream ints(long j) {
        if (j >= 0) {
            return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(AbstractCLASSNAMEo1.s(new g(0L, j, Integer.MAX_VALUE, 0), false));
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    public LongStream longs(long j, long j2) {
        if (j < j2) {
            return O0.n0(AbstractCLASSNAMEo1.t(new h(0L, Long.MAX_VALUE, j, j2), false));
        }
        throw new IllegalArgumentException("bound must be greater than origin");
    }

    @Override // java.util.Random
    public DoubleStream doubles(long j, double d2, double d3) {
        if (j >= 0) {
            if (d2 < d3) {
                return M0.n0(AbstractCLASSNAMEo1.r(new f(0L, j, d2, d3), false));
            }
            throw new IllegalArgumentException("bound must be greater than origin");
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    public IntStream ints(long j, int i, int i2) {
        if (j >= 0) {
            if (i < i2) {
                return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(AbstractCLASSNAMEo1.s(new g(0L, j, i, i2), false));
            }
            throw new IllegalArgumentException("bound must be greater than origin");
        }
        throw new IllegalArgumentException("size must be non-negative");
    }

    @Override // java.util.Random
    public LongStream longs(long j, long j2, long j3) {
        if (j >= 0) {
            if (j2 < j3) {
                return O0.n0(AbstractCLASSNAMEo1.t(new h(0L, j, j2, j3), false));
            }
            throw new IllegalArgumentException("bound must be greater than origin");
        }
        throw new IllegalArgumentException("size must be non-negative");
    }
}

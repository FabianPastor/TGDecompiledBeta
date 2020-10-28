package j$.util.concurrent;

import j$.CLASSNAMEt;
import j$.CLASSNAMEv;
import j$.P;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import sun.misc.Unsafe;

public class ConcurrentHashMap extends AbstractMap implements ConcurrentMap, Serializable, w {
    static final int g = Runtime.getRuntime().availableProcessors();
    private static final Unsafe h;
    private static final long i;
    private static final long j;
    private static final long k;
    private static final long l;
    private static final long m;
    private static final long n;
    private static final int o;
    volatile transient m[] a;
    private volatile transient m[] b;
    private volatile transient long baseCount;
    private volatile transient CLASSNAMEd[] c;
    private volatile transient int cellsBusy;
    private transient j d;
    private transient v e;
    private transient f f;
    private volatile transient int sizeCtl;
    private volatile transient int transferIndex;

    static {
        new ObjectStreamField("segments", o[].class);
        Class cls = Integer.TYPE;
        new ObjectStreamField("segmentMask", cls);
        new ObjectStreamField("segmentShift", cls);
        try {
            Unsafe c2 = x.c();
            h = c2;
            Class<ConcurrentHashMap> cls2 = ConcurrentHashMap.class;
            i = c2.objectFieldOffset(cls2.getDeclaredField("sizeCtl"));
            j = c2.objectFieldOffset(cls2.getDeclaredField("transferIndex"));
            k = c2.objectFieldOffset(cls2.getDeclaredField("baseCount"));
            l = c2.objectFieldOffset(cls2.getDeclaredField("cellsBusy"));
            m = c2.objectFieldOffset(CLASSNAMEd.class.getDeclaredField("value"));
            Class<m[]> cls3 = m[].class;
            n = (long) c2.arrayBaseOffset(cls3);
            int arrayIndexScale = c2.arrayIndexScale(cls3);
            if (((arrayIndexScale - 1) & arrayIndexScale) == 0) {
                o = 31 - Integer.numberOfLeadingZeros(arrayIndexScale);
                return;
            }
            throw new Error("data type scale not a power of two");
        } catch (Exception e2) {
            throw new Error(e2);
        }
    }

    public ConcurrentHashMap() {
    }

    public ConcurrentHashMap(int i2) {
        if (i2 >= 0) {
            this.sizeCtl = i2 >= NUM ? NUM : o(i2 + (i2 >>> 1) + 1);
            return;
        }
        throw new IllegalArgumentException();
    }

    public ConcurrentHashMap(int i2, float f2, int i3) {
        if (f2 <= 0.0f || i2 < 0 || i3 <= 0) {
            throw new IllegalArgumentException();
        }
        double d2 = (double) (((float) ((long) (i2 < i3 ? i3 : i2))) / f2);
        Double.isNaN(d2);
        long j2 = (long) (d2 + 1.0d);
        this.sizeCtl = j2 >= NUM ? NUM : o((int) j2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0012, code lost:
        if (r1.compareAndSwapLong(r11, r3, r5, r9) == false) goto L_0x0014;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void a(long r12, int r14) {
        /*
            r11 = this;
            j$.util.concurrent.d[] r0 = r11.c
            if (r0 != 0) goto L_0x0014
            sun.misc.Unsafe r1 = h
            long r3 = k
            long r5 = r11.baseCount
            long r9 = r5 + r12
            r2 = r11
            r7 = r9
            boolean r1 = r1.compareAndSwapLong(r2, r3, r5, r7)
            if (r1 != 0) goto L_0x003b
        L_0x0014:
            r1 = 1
            if (r0 == 0) goto L_0x0094
            int r2 = r0.length
            int r2 = r2 - r1
            if (r2 < 0) goto L_0x0094
            int r3 = j$.util.concurrent.D.c()
            r2 = r2 & r3
            r4 = r0[r2]
            if (r4 == 0) goto L_0x0094
            sun.misc.Unsafe r3 = h
            long r5 = m
            long r7 = r4.value
            long r9 = r7 + r12
            boolean r0 = r3.compareAndSwapLong(r4, r5, r7, r9)
            if (r0 != 0) goto L_0x0034
            r1 = r0
            goto L_0x0094
        L_0x0034:
            if (r14 > r1) goto L_0x0037
            return
        L_0x0037:
            long r9 = r11.m()
        L_0x003b:
            if (r14 < 0) goto L_0x0093
        L_0x003d:
            int r4 = r11.sizeCtl
            long r12 = (long) r4
            int r14 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r14 < 0) goto L_0x0093
            j$.util.concurrent.m[] r12 = r11.a
            if (r12 == 0) goto L_0x0093
            int r13 = r12.length
            r14 = 1073741824(0x40000000, float:2.0)
            if (r13 >= r14) goto L_0x0093
            int r13 = j(r13)
            if (r4 >= 0) goto L_0x007b
            int r14 = r4 >>> 16
            if (r14 != r13) goto L_0x0093
            int r14 = r13 + 1
            if (r4 == r14) goto L_0x0093
            r14 = 65535(0xffff, float:9.1834E-41)
            int r13 = r13 + r14
            if (r4 == r13) goto L_0x0093
            j$.util.concurrent.m[] r13 = r11.b
            if (r13 == 0) goto L_0x0093
            int r14 = r11.transferIndex
            if (r14 > 0) goto L_0x006a
            goto L_0x0093
        L_0x006a:
            sun.misc.Unsafe r0 = h
            long r2 = i
            int r5 = r4 + 1
            r1 = r11
            boolean r14 = r0.compareAndSwapInt(r1, r2, r4, r5)
            if (r14 == 0) goto L_0x008e
            r11.p(r12, r13)
            goto L_0x008e
        L_0x007b:
            sun.misc.Unsafe r0 = h
            long r2 = i
            int r13 = r13 << 16
            int r5 = r13 + 2
            r1 = r11
            boolean r13 = r0.compareAndSwapInt(r1, r2, r4, r5)
            if (r13 == 0) goto L_0x008e
            r13 = 0
            r11.p(r12, r13)
        L_0x008e:
            long r9 = r11.m()
            goto L_0x003d
        L_0x0093:
            return
        L_0x0094:
            r11.e(r12, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.a(long, int):void");
    }

    static final boolean b(m[] mVarArr, int i2, m mVar, m mVar2) {
        return h.compareAndSwapObject(mVarArr, (((long) i2) << o) + n, (Object) null, mVar2);
    }

    static Class c(Object obj) {
        Type[] actualTypeArguments;
        if (!(obj instanceof Comparable)) {
            return null;
        }
        Class<?> cls = obj.getClass();
        if (cls == String.class) {
            return cls;
        }
        Type[] genericInterfaces = cls.getGenericInterfaces();
        if (genericInterfaces == null) {
            return null;
        }
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType() == Comparable.class && (actualTypeArguments = parameterizedType.getActualTypeArguments()) != null && actualTypeArguments.length == 1 && actualTypeArguments[0] == cls) {
                    return cls;
                }
            }
        }
        return null;
    }

    static int d(Class cls, Object obj, Object obj2) {
        if (obj2 == null || obj2.getClass() != cls) {
            return 0;
        }
        return ((Comparable) obj).compareTo(obj2);
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x009b, code lost:
        if (r9.c != r7) goto L_0x00ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x009d, code lost:
        r1 = new j$.util.concurrent.CLASSNAMEd[(r8 << 1)];
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00a2, code lost:
        if (r2 >= r8) goto L_0x00ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00a4, code lost:
        r1[r2] = r7[r2];
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00ab, code lost:
        r9.c = r1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0101 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x001b A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void e(long r25, boolean r27) {
        /*
            r24 = this;
            r9 = r24
            r10 = r25
            int r0 = j$.util.concurrent.D.c()
            r12 = 1
            if (r0 != 0) goto L_0x0015
            j$.util.concurrent.D.g()
            int r0 = j$.util.concurrent.D.c()
            r1 = r0
            r0 = 1
            goto L_0x0018
        L_0x0015:
            r1 = r0
            r0 = r27
        L_0x0018:
            r13 = 0
            r14 = r1
            r15 = 0
        L_0x001b:
            j$.util.concurrent.d[] r7 = r9.c
            if (r7 == 0) goto L_0x00bd
            int r8 = r7.length
            if (r8 <= 0) goto L_0x00bd
            int r1 = r8 + -1
            r1 = r1 & r14
            r1 = r7[r1]
            if (r1 != 0) goto L_0x0061
            int r1 = r9.cellsBusy
            if (r1 != 0) goto L_0x00b6
            j$.util.concurrent.d r7 = new j$.util.concurrent.d
            r7.<init>(r10)
            int r1 = r9.cellsBusy
            if (r1 != 0) goto L_0x00b6
            sun.misc.Unsafe r1 = h
            long r3 = l
            r5 = 0
            r6 = 1
            r2 = r24
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x00b6
            j$.util.concurrent.d[] r1 = r9.c     // Catch:{ all -> 0x005d }
            if (r1 == 0) goto L_0x0056
            int r2 = r1.length     // Catch:{ all -> 0x005d }
            if (r2 <= 0) goto L_0x0056
            int r2 = r2 + -1
            r2 = r2 & r14
            r3 = r1[r2]     // Catch:{ all -> 0x005d }
            if (r3 != 0) goto L_0x0056
            r1[r2] = r7     // Catch:{ all -> 0x005d }
            r1 = 1
            goto L_0x0057
        L_0x0056:
            r1 = 0
        L_0x0057:
            r9.cellsBusy = r13
            if (r1 == 0) goto L_0x001b
            goto L_0x0101
        L_0x005d:
            r0 = move-exception
            r9.cellsBusy = r13
            throw r0
        L_0x0061:
            if (r0 != 0) goto L_0x0065
            r0 = 1
            goto L_0x00b7
        L_0x0065:
            sun.misc.Unsafe r2 = h
            long r18 = m
            long r3 = r1.value
            long r22 = r3 + r10
            r16 = r2
            r17 = r1
            r20 = r3
            boolean r1 = r16.compareAndSwapLong(r17, r18, r20, r22)
            if (r1 == 0) goto L_0x007b
            goto L_0x0101
        L_0x007b:
            j$.util.concurrent.d[] r1 = r9.c
            if (r1 != r7) goto L_0x00b6
            int r1 = g
            if (r8 < r1) goto L_0x0084
            goto L_0x00b6
        L_0x0084:
            if (r15 != 0) goto L_0x0088
            r15 = 1
            goto L_0x00b7
        L_0x0088:
            int r1 = r9.cellsBusy
            if (r1 != 0) goto L_0x00b7
            long r3 = l
            r5 = 0
            r6 = 1
            r1 = r2
            r2 = r24
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x00b7
            j$.util.concurrent.d[] r1 = r9.c     // Catch:{ all -> 0x00b2 }
            if (r1 != r7) goto L_0x00ad
            int r1 = r8 << 1
            j$.util.concurrent.d[] r1 = new j$.util.concurrent.CLASSNAMEd[r1]     // Catch:{ all -> 0x00b2 }
            r2 = 0
        L_0x00a2:
            if (r2 >= r8) goto L_0x00ab
            r3 = r7[r2]     // Catch:{ all -> 0x00b2 }
            r1[r2] = r3     // Catch:{ all -> 0x00b2 }
            int r2 = r2 + 1
            goto L_0x00a2
        L_0x00ab:
            r9.c = r1     // Catch:{ all -> 0x00b2 }
        L_0x00ad:
            r9.cellsBusy = r13
            r1 = r14
            goto L_0x0018
        L_0x00b2:
            r0 = move-exception
            r9.cellsBusy = r13
            throw r0
        L_0x00b6:
            r15 = 0
        L_0x00b7:
            int r14 = j$.util.concurrent.D.a(r14)
            goto L_0x001b
        L_0x00bd:
            int r1 = r9.cellsBusy
            if (r1 != 0) goto L_0x00f1
            j$.util.concurrent.d[] r1 = r9.c
            if (r1 != r7) goto L_0x00f1
            sun.misc.Unsafe r1 = h
            long r3 = l
            r5 = 0
            r6 = 1
            r2 = r24
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x00f1
            j$.util.concurrent.d[] r1 = r9.c     // Catch:{ all -> 0x00ed }
            if (r1 != r7) goto L_0x00e7
            r1 = 2
            j$.util.concurrent.d[] r1 = new j$.util.concurrent.CLASSNAMEd[r1]     // Catch:{ all -> 0x00ed }
            r2 = r14 & 1
            j$.util.concurrent.d r3 = new j$.util.concurrent.d     // Catch:{ all -> 0x00ed }
            r3.<init>(r10)     // Catch:{ all -> 0x00ed }
            r1[r2] = r3     // Catch:{ all -> 0x00ed }
            r9.c = r1     // Catch:{ all -> 0x00ed }
            r1 = 1
            goto L_0x00e8
        L_0x00e7:
            r1 = 0
        L_0x00e8:
            r9.cellsBusy = r13
            if (r1 == 0) goto L_0x001b
            goto L_0x0101
        L_0x00ed:
            r0 = move-exception
            r9.cellsBusy = r13
            throw r0
        L_0x00f1:
            sun.misc.Unsafe r1 = h
            long r3 = k
            long r5 = r9.baseCount
            long r7 = r5 + r10
            r2 = r24
            boolean r1 = r1.compareAndSwapLong(r2, r3, r5, r7)
            if (r1 == 0) goto L_0x001b
        L_0x0101:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.e(long, boolean):void");
    }

    /* JADX INFO: finally extract failed */
    private final m[] g() {
        while (true) {
            m[] mVarArr = this.a;
            if (mVarArr != null && mVarArr.length != 0) {
                return mVarArr;
            }
            int i2 = this.sizeCtl;
            if (i2 < 0) {
                Thread.yield();
            } else {
                if (h.compareAndSwapInt(this, i, i2, -1)) {
                    try {
                        m[] mVarArr2 = this.a;
                        if (mVarArr2 == null || mVarArr2.length == 0) {
                            int i3 = i2 > 0 ? i2 : 16;
                            m[] mVarArr3 = new m[i3];
                            this.a = mVarArr3;
                            i2 = i3 - (i3 >>> 2);
                            mVarArr2 = mVarArr3;
                        }
                        this.sizeCtl = i2;
                        return mVarArr2;
                    } catch (Throwable th) {
                        this.sizeCtl = i2;
                        throw th;
                    }
                }
            }
        }
    }

    static final int j(int i2) {
        return Integer.numberOfLeadingZeros(i2) | 32768;
    }

    static final void k(m[] mVarArr, int i2, m mVar) {
        h.putObjectVolatile(mVarArr, (((long) i2) << o) + n, mVar);
    }

    static final int l(int i2) {
        return (i2 ^ (i2 >>> 16)) & Integer.MAX_VALUE;
    }

    static final m n(m[] mVarArr, int i2) {
        return (m) h.getObjectVolatile(mVarArr, (((long) i2) << o) + n);
    }

    private static final int o(int i2) {
        int i3 = i2 - 1;
        int i4 = i3 | (i3 >>> 1);
        int i5 = i4 | (i4 >>> 2);
        int i6 = i5 | (i5 >>> 4);
        int i7 = i6 | (i6 >>> 8);
        int i8 = i7 | (i7 >>> 16);
        if (i8 < 0) {
            return 1;
        }
        if (i8 >= NUM) {
            return NUM;
        }
        return 1 + i8;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: j$.util.concurrent.m} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v10, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v10, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v13, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v14, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v14, resolved type: j$.util.concurrent.s} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: j$.util.concurrent.s} */
    /* JADX WARNING: type inference failed for: r6v12, types: [j$.util.concurrent.m] */
    /* JADX WARNING: type inference failed for: r13v13, types: [j$.util.concurrent.m] */
    /* JADX WARNING: type inference failed for: r6v17, types: [j$.util.concurrent.m] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void p(j$.util.concurrent.m[] r31, j$.util.concurrent.m[] r32) {
        /*
            r30 = this;
            r7 = r30
            r0 = r31
            int r8 = r0.length
            int r1 = g
            r9 = 1
            if (r1 <= r9) goto L_0x000e
            int r2 = r8 >>> 3
            int r2 = r2 / r1
            goto L_0x000f
        L_0x000e:
            r2 = r8
        L_0x000f:
            r10 = 16
            if (r2 >= r10) goto L_0x0016
            r11 = 16
            goto L_0x0017
        L_0x0016:
            r11 = r2
        L_0x0017:
            if (r32 != 0) goto L_0x0029
            int r1 = r8 << 1
            j$.util.concurrent.m[] r1 = new j$.util.concurrent.m[r1]     // Catch:{ all -> 0x0023 }
            r7.b = r1
            r7.transferIndex = r8
            r12 = r1
            goto L_0x002b
        L_0x0023:
            r0 = 2147483647(0x7fffffff, float:NaN)
            r7.sizeCtl = r0
            return
        L_0x0029:
            r12 = r32
        L_0x002b:
            int r13 = r12.length
            j$.util.concurrent.h r14 = new j$.util.concurrent.h
            r14.<init>(r12)
            r4 = r0
            r3 = r7
            r5 = 0
            r6 = 0
            r16 = 1
            r17 = 0
        L_0x0039:
            r1 = -1
            if (r16 == 0) goto L_0x008d
            int r5 = r5 + -1
            if (r5 >= r6) goto L_0x007c
            if (r17 == 0) goto L_0x0043
            goto L_0x007c
        L_0x0043:
            int r2 = r3.transferIndex
            if (r2 > 0) goto L_0x004c
            r22 = r3
            r15 = r4
            r5 = -1
            goto L_0x0087
        L_0x004c:
            sun.misc.Unsafe r1 = h
            long r18 = j
            if (r2 <= r11) goto L_0x0055
            int r20 = r2 - r11
            goto L_0x0057
        L_0x0055:
            r20 = 0
        L_0x0057:
            r21 = r2
            r2 = r30
            r22 = r3
            r15 = r4
            r3 = r18
            r18 = r5
            r5 = r21
            r19 = r6
            r6 = r20
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x0074
            int r2 = r21 + -1
            r5 = r2
            r6 = r20
            goto L_0x0087
        L_0x0074:
            r4 = r15
            r5 = r18
            r6 = r19
            r3 = r22
            goto L_0x0039
        L_0x007c:
            r22 = r3
            r15 = r4
            r18 = r5
            r19 = r6
            r5 = r18
            r6 = r19
        L_0x0087:
            r4 = r15
            r3 = r22
            r16 = 0
            goto L_0x0039
        L_0x008d:
            r22 = r3
            r15 = r4
            r19 = r6
            r2 = 0
            if (r5 < 0) goto L_0x01c3
            if (r5 >= r8) goto L_0x01c3
            int r3 = r5 + r8
            if (r3 < r13) goto L_0x009d
            goto L_0x01c3
        L_0x009d:
            j$.util.concurrent.m r4 = n(r15, r5)
            if (r4 != 0) goto L_0x00b7
            boolean r1 = b(r15, r5, r2, r14)
            r16 = r1
            r9 = r7
            r21 = r11
            r7 = r14
            r4 = r15
            r3 = r22
            r2 = 16
            r10 = 1
        L_0x00b3:
            r22 = r13
            goto L_0x0207
        L_0x00b7:
            int r6 = r4.a
            if (r6 != r1) goto L_0x00c8
            r9 = r7
            r21 = r11
            r7 = r14
            r4 = r15
            r3 = r22
            r2 = 16
            r10 = 1
            r16 = 1
            goto L_0x00b3
        L_0x00c8:
            monitor-enter(r4)
            j$.util.concurrent.m r1 = n(r15, r5)     // Catch:{ all -> 0x01c0 }
            if (r1 != r4) goto L_0x01b2
            if (r6 < 0) goto L_0x0124
            r1 = r6 & r8
            j$.util.concurrent.m r6 = r4.d     // Catch:{ all -> 0x01c0 }
            r10 = r4
        L_0x00d6:
            if (r6 == 0) goto L_0x00e3
            int r9 = r6.a     // Catch:{ all -> 0x01c0 }
            r9 = r9 & r8
            if (r9 == r1) goto L_0x00df
            r10 = r6
            r1 = r9
        L_0x00df:
            j$.util.concurrent.m r6 = r6.d     // Catch:{ all -> 0x01c0 }
            r9 = 1
            goto L_0x00d6
        L_0x00e3:
            if (r1 != 0) goto L_0x00e7
            r1 = r10
            goto L_0x00e9
        L_0x00e7:
            r1 = r2
            r2 = r10
        L_0x00e9:
            r6 = r4
        L_0x00ea:
            if (r6 == r10) goto L_0x0114
            int r9 = r6.a     // Catch:{ all -> 0x01c0 }
            r16 = r10
            java.lang.Object r10 = r6.b     // Catch:{ all -> 0x01c0 }
            r21 = r11
            java.lang.Object r11 = r6.c     // Catch:{ all -> 0x01c0 }
            r22 = r9 & r8
            if (r22 != 0) goto L_0x0103
            r22 = r13
            j$.util.concurrent.m r13 = new j$.util.concurrent.m     // Catch:{ all -> 0x01c0 }
            r13.<init>(r9, r10, r11, r1)     // Catch:{ all -> 0x01c0 }
            r1 = r13
            goto L_0x010b
        L_0x0103:
            r22 = r13
            j$.util.concurrent.m r13 = new j$.util.concurrent.m     // Catch:{ all -> 0x01c0 }
            r13.<init>(r9, r10, r11, r2)     // Catch:{ all -> 0x01c0 }
            r2 = r13
        L_0x010b:
            j$.util.concurrent.m r6 = r6.d     // Catch:{ all -> 0x01c0 }
            r10 = r16
            r11 = r21
            r13 = r22
            goto L_0x00ea
        L_0x0114:
            r21 = r11
            r22 = r13
            k(r12, r5, r1)     // Catch:{ all -> 0x01c0 }
            k(r12, r3, r2)     // Catch:{ all -> 0x01c0 }
            k(r15, r5, r14)     // Catch:{ all -> 0x01c0 }
            r7 = r14
            goto L_0x01af
        L_0x0124:
            r21 = r11
            r22 = r13
            boolean r1 = r4 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x01c0 }
            if (r1 == 0) goto L_0x01b6
            r1 = r4
            j$.util.concurrent.r r1 = (j$.util.concurrent.r) r1     // Catch:{ all -> 0x01c0 }
            j$.util.concurrent.s r6 = r1.f     // Catch:{ all -> 0x01c0 }
            r9 = r2
            r10 = r9
            r11 = r6
            r13 = 0
            r15 = 0
            r6 = r10
        L_0x0137:
            if (r11 == 0) goto L_0x017a
            r16 = r1
            int r1 = r11.a     // Catch:{ all -> 0x01c0 }
            j$.util.concurrent.s r7 = new j$.util.concurrent.s     // Catch:{ all -> 0x01c0 }
            java.lang.Object r0 = r11.b     // Catch:{ all -> 0x01c0 }
            r29 = r14
            java.lang.Object r14 = r11.c     // Catch:{ all -> 0x01c0 }
            r27 = 0
            r28 = 0
            r23 = r7
            r24 = r1
            r25 = r0
            r26 = r14
            r23.<init>(r24, r25, r26, r27, r28)     // Catch:{ all -> 0x01c0 }
            r0 = r1 & r8
            if (r0 != 0) goto L_0x0164
            r7.h = r10     // Catch:{ all -> 0x01c0 }
            if (r10 != 0) goto L_0x015e
            r2 = r7
            goto L_0x0160
        L_0x015e:
            r10.d = r7     // Catch:{ all -> 0x01c0 }
        L_0x0160:
            int r13 = r13 + 1
            r10 = r7
            goto L_0x016f
        L_0x0164:
            r7.h = r9     // Catch:{ all -> 0x01c0 }
            if (r9 != 0) goto L_0x016a
            r6 = r7
            goto L_0x016c
        L_0x016a:
            r9.d = r7     // Catch:{ all -> 0x01c0 }
        L_0x016c:
            int r15 = r15 + 1
            r9 = r7
        L_0x016f:
            j$.util.concurrent.m r11 = r11.d     // Catch:{ all -> 0x01c0 }
            r7 = r30
            r0 = r31
            r1 = r16
            r14 = r29
            goto L_0x0137
        L_0x017a:
            r16 = r1
            r29 = r14
            r0 = 6
            if (r13 > r0) goto L_0x0186
            j$.util.concurrent.m r1 = s(r2)     // Catch:{ all -> 0x01c0 }
            goto L_0x0190
        L_0x0186:
            if (r15 == 0) goto L_0x018e
            j$.util.concurrent.r r1 = new j$.util.concurrent.r     // Catch:{ all -> 0x01c0 }
            r1.<init>(r2)     // Catch:{ all -> 0x01c0 }
            goto L_0x0190
        L_0x018e:
            r1 = r16
        L_0x0190:
            if (r15 > r0) goto L_0x0197
            j$.util.concurrent.m r0 = s(r6)     // Catch:{ all -> 0x01c0 }
            goto L_0x01a1
        L_0x0197:
            if (r13 == 0) goto L_0x019f
            j$.util.concurrent.r r0 = new j$.util.concurrent.r     // Catch:{ all -> 0x01c0 }
            r0.<init>(r6)     // Catch:{ all -> 0x01c0 }
            goto L_0x01a1
        L_0x019f:
            r0 = r16
        L_0x01a1:
            k(r12, r5, r1)     // Catch:{ all -> 0x01c0 }
            k(r12, r3, r0)     // Catch:{ all -> 0x01c0 }
            r0 = r31
            r7 = r29
            k(r0, r5, r7)     // Catch:{ all -> 0x01c0 }
            r15 = r0
        L_0x01af:
            r16 = 1
            goto L_0x01b7
        L_0x01b2:
            r21 = r11
            r22 = r13
        L_0x01b6:
            r7 = r14
        L_0x01b7:
            monitor-exit(r4)     // Catch:{ all -> 0x01c0 }
            r13 = r5
            r4 = r15
            r2 = 16
            r10 = 1
            r9 = r30
            goto L_0x0205
        L_0x01c0:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x01c0 }
            throw r0
        L_0x01c3:
            r21 = r11
            r22 = r13
            r7 = r14
            if (r17 == 0) goto L_0x01d9
            r9 = r30
            r9.b = r2
            r9.a = r12
            int r0 = r8 << 1
            r10 = 1
            int r1 = r8 >>> 1
            int r0 = r0 - r1
            r9.sizeCtl = r0
            return
        L_0x01d9:
            r10 = 1
            r9 = r30
            sun.misc.Unsafe r1 = h
            long r3 = i
            int r11 = r9.sizeCtl
            int r6 = r11 + -1
            r2 = r30
            r13 = r5
            r5 = r11
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x0202
            int r11 = r11 + -2
            int r1 = j(r8)
            r2 = 16
            int r1 = r1 << r2
            if (r11 == r1) goto L_0x01fa
            return
        L_0x01fa:
            r5 = r8
            r3 = r9
            r4 = r15
            r16 = 1
            r17 = 1
            goto L_0x0207
        L_0x0202:
            r2 = 16
            r4 = r15
        L_0x0205:
            r3 = r9
            r5 = r13
        L_0x0207:
            r14 = r7
            r7 = r9
            r6 = r19
            r11 = r21
            r13 = r22
            r9 = 1
            r10 = 16
            goto L_0x0039
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.p(j$.util.concurrent.m[], j$.util.concurrent.m[]):void");
    }

    private final void q(m[] mVarArr, int i2) {
        if (mVarArr != null) {
            int length = mVarArr.length;
            if (length < 64) {
                r(length << 1);
                return;
            }
            m n2 = n(mVarArr, i2);
            if (n2 != null && n2.a >= 0) {
                synchronized (n2) {
                    if (n(mVarArr, i2) == n2) {
                        s sVar = null;
                        m mVar = n2;
                        s sVar2 = null;
                        while (mVar != null) {
                            s sVar3 = new s(mVar.a, mVar.b, mVar.c, (m) null, (s) null);
                            sVar3.h = sVar2;
                            if (sVar2 == null) {
                                sVar = sVar3;
                            } else {
                                sVar2.d = sVar3;
                            }
                            mVar = mVar.d;
                            sVar2 = sVar3;
                        }
                        k(mVarArr, i2, new r(sVar));
                    }
                }
            }
        }
    }

    private final void r(int i2) {
        int length;
        m[] mVarArr;
        int o2 = i2 >= NUM ? NUM : o(i2 + (i2 >>> 1) + 1);
        while (true) {
            int i3 = this.sizeCtl;
            if (i3 >= 0) {
                m[] mVarArr2 = this.a;
                if (mVarArr2 == null || (length = mVarArr2.length) == 0) {
                    int i4 = i3 > o2 ? i3 : o2;
                    if (h.compareAndSwapInt(this, i, i3, -1)) {
                        try {
                            if (this.a == mVarArr2) {
                                this.a = new m[i4];
                                i3 = i4 - (i4 >>> 2);
                            }
                        } finally {
                            this.sizeCtl = i3;
                        }
                    }
                } else if (o2 > i3 && length < NUM) {
                    if (mVarArr2 == this.a) {
                        int j2 = j(length);
                        if (i3 >= 0) {
                            if (h.compareAndSwapInt(this, i, i3, (j2 << 16) + 2)) {
                                p(mVarArr2, (m[]) null);
                            }
                        } else if ((i3 >>> 16) == j2 && i3 != j2 + 1 && i3 != j2 + 65535 && (mVarArr = this.b) != null && this.transferIndex > 0) {
                            if (h.compareAndSwapInt(this, i, i3, i3 + 1)) {
                                p(mVarArr2, mVarArr);
                            }
                        } else {
                            return;
                        }
                    } else {
                        continue;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    static m s(m mVar) {
        m mVar2 = null;
        m mVar3 = null;
        while (mVar != null) {
            m mVar4 = new m(mVar.a, mVar.b, mVar.c, (m) null);
            if (mVar3 == null) {
                mVar2 = mVar4;
            } else {
                mVar3.d = mVar4;
            }
            mVar = mVar.d;
            mVar3 = mVar4;
        }
        return mVar2;
    }

    public void clear() {
        m[] mVarArr = this.a;
        long j2 = 0;
        loop0:
        while (true) {
            int i2 = 0;
            while (mVarArr != null && i2 < mVarArr.length) {
                m n2 = n(mVarArr, i2);
                if (n2 == null) {
                    i2++;
                } else {
                    int i3 = n2.a;
                    if (i3 == -1) {
                        mVarArr = f(mVarArr, n2);
                    } else {
                        synchronized (n2) {
                            if (n(mVarArr, i2) == n2) {
                                for (m mVar = i3 >= 0 ? n2 : n2 instanceof r ? ((r) n2).f : null; mVar != null; mVar = mVar.d) {
                                    j2--;
                                }
                                k(mVarArr, i2, (m) null);
                                i2++;
                            }
                        }
                    }
                }
            }
        }
        if (j2 != 0) {
            a(j2, -1);
        }
    }

    /* JADX INFO: finally extract failed */
    public Object compute(Object obj, BiFunction biFunction) {
        Object obj2;
        int i2;
        Object obj3;
        int i3;
        m mVar;
        if (obj == null || biFunction == null) {
            throw null;
        }
        int l2 = l(obj.hashCode());
        m[] mVarArr = this.a;
        int i4 = 0;
        Object obj4 = null;
        int i5 = 0;
        while (true) {
            if (mVarArr != null) {
                int length = mVarArr.length;
                if (length != 0) {
                    int i6 = (length - 1) & l2;
                    m n2 = n(mVarArr, i6);
                    int i7 = 1;
                    if (n2 == null) {
                        n nVar = new n();
                        synchronized (nVar) {
                            if (b(mVarArr, i6, (m) null, nVar)) {
                                try {
                                    Object apply = biFunction.apply(obj, (Object) null);
                                    if (apply != null) {
                                        mVar = new m(l2, obj, apply, (m) null);
                                        i3 = 1;
                                    } else {
                                        i3 = i4;
                                        mVar = null;
                                    }
                                    k(mVarArr, i6, mVar);
                                    i4 = i3;
                                    obj4 = apply;
                                    i5 = 1;
                                } catch (Throwable th) {
                                    k(mVarArr, i6, (m) null);
                                    throw th;
                                }
                            }
                        }
                        if (i5 != 0) {
                            break;
                        }
                    } else {
                        int i8 = n2.a;
                        if (i8 == -1) {
                            mVarArr = f(mVarArr, n2);
                        } else {
                            synchronized (n2) {
                                if (n(mVarArr, i6) == n2) {
                                    if (i8 >= 0) {
                                        m mVar2 = null;
                                        m mVar3 = n2;
                                        i2 = 1;
                                        while (true) {
                                            if (mVar3.a != l2 || ((obj3 = mVar3.b) != obj && (obj3 == null || !obj.equals(obj3)))) {
                                                m mVar4 = mVar3.d;
                                                if (mVar4 == null) {
                                                    obj2 = biFunction.apply(obj, (Object) null);
                                                    if (obj2 != null) {
                                                        mVar3.d = new m(l2, obj, obj2, (m) null);
                                                    } else {
                                                        i5 = i2;
                                                        obj4 = obj2;
                                                    }
                                                } else {
                                                    i2++;
                                                    m mVar5 = mVar4;
                                                    mVar2 = mVar3;
                                                    mVar3 = mVar5;
                                                }
                                            }
                                        }
                                        Object apply2 = biFunction.apply(obj, mVar3.c);
                                        if (apply2 != null) {
                                            mVar3.c = apply2;
                                            i5 = i2;
                                            obj4 = apply2;
                                        } else {
                                            m mVar6 = mVar3.d;
                                            if (mVar2 != null) {
                                                mVar2.d = mVar6;
                                            } else {
                                                k(mVarArr, i6, mVar6);
                                            }
                                            obj2 = apply2;
                                            i7 = i2;
                                            i5 = i7;
                                            obj4 = obj2;
                                            i4 = -1;
                                        }
                                    } else if (n2 instanceof r) {
                                        r rVar = (r) n2;
                                        s sVar = rVar.e;
                                        s b2 = sVar != null ? sVar.b(l2, obj, (Class) null) : null;
                                        obj2 = biFunction.apply(obj, b2 == null ? null : b2.c);
                                        if (obj2 != null) {
                                            if (b2 != null) {
                                                b2.c = obj2;
                                            } else {
                                                rVar.f(l2, obj, obj2);
                                                i2 = 1;
                                            }
                                        } else if (b2 != null) {
                                            if (rVar.g(b2)) {
                                                k(mVarArr, i6, s(rVar.f));
                                            }
                                            i5 = i7;
                                            obj4 = obj2;
                                            i4 = -1;
                                        }
                                        obj4 = obj2;
                                        i5 = 1;
                                    }
                                    i5 = i2;
                                    obj4 = obj2;
                                    i4 = 1;
                                }
                            }
                            if (i5 != 0) {
                                if (i5 >= 8) {
                                    q(mVarArr, i6);
                                }
                            }
                        }
                    }
                }
            }
            mVarArr = g();
        }
        if (i4 != 0) {
            a((long) i4, i5);
        }
        return obj4;
    }

    public /* synthetic */ Object compute(Object obj, java.util.function.BiFunction biFunction) {
        return compute(obj, CLASSNAMEv.b(biFunction));
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x00c0, code lost:
        if (r5 == null) goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x00c2, code lost:
        a(1, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x00c7, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object computeIfAbsent(java.lang.Object r13, j$.util.function.Function r14) {
        /*
            r12 = this;
            r0 = 0
            if (r13 == 0) goto L_0x00d1
            if (r14 == 0) goto L_0x00d1
            int r1 = r13.hashCode()
            int r1 = l(r1)
            j$.util.concurrent.m[] r2 = r12.a
            r3 = 0
            r5 = r0
            r4 = 0
        L_0x0012:
            if (r2 == 0) goto L_0x00cb
            int r6 = r2.length
            if (r6 != 0) goto L_0x0019
            goto L_0x00cb
        L_0x0019:
            int r6 = r6 + -1
            r6 = r6 & r1
            j$.util.concurrent.m r7 = n(r2, r6)
            r8 = 1
            if (r7 != 0) goto L_0x004f
            j$.util.concurrent.n r9 = new j$.util.concurrent.n
            r9.<init>()
            monitor-enter(r9)
            boolean r7 = b(r2, r6, r0, r9)     // Catch:{ all -> 0x004c }
            if (r7 == 0) goto L_0x0047
            java.lang.Object r4 = r14.apply(r13)     // Catch:{ all -> 0x0042 }
            if (r4 == 0) goto L_0x003b
            j$.util.concurrent.m r5 = new j$.util.concurrent.m     // Catch:{ all -> 0x0042 }
            r5.<init>(r1, r13, r4, r0)     // Catch:{ all -> 0x0042 }
            goto L_0x003c
        L_0x003b:
            r5 = r0
        L_0x003c:
            k(r2, r6, r5)     // Catch:{ all -> 0x004c }
            r5 = r4
            r4 = 1
            goto L_0x0047
        L_0x0042:
            r13 = move-exception
            k(r2, r6, r0)     // Catch:{ all -> 0x004c }
            throw r13     // Catch:{ all -> 0x004c }
        L_0x0047:
            monitor-exit(r9)     // Catch:{ all -> 0x004c }
            if (r4 == 0) goto L_0x0012
            goto L_0x00c0
        L_0x004c:
            r13 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x004c }
            throw r13
        L_0x004f:
            int r9 = r7.a
            r10 = -1
            if (r9 != r10) goto L_0x0059
            j$.util.concurrent.m[] r2 = r12.f(r2, r7)
            goto L_0x0012
        L_0x0059:
            monitor-enter(r7)
            j$.util.concurrent.m r10 = n(r2, r6)     // Catch:{ all -> 0x00c8 }
            r11 = 2
            if (r10 != r7) goto L_0x00b2
            if (r9 < 0) goto L_0x0091
            r5 = r7
            r4 = 1
        L_0x0065:
            int r9 = r5.a     // Catch:{ all -> 0x00c8 }
            if (r9 != r1) goto L_0x0078
            java.lang.Object r9 = r5.b     // Catch:{ all -> 0x00c8 }
            if (r9 == r13) goto L_0x0075
            if (r9 == 0) goto L_0x0078
            boolean r9 = r13.equals(r9)     // Catch:{ all -> 0x00c8 }
            if (r9 == 0) goto L_0x0078
        L_0x0075:
            java.lang.Object r5 = r5.c     // Catch:{ all -> 0x00c8 }
            goto L_0x00b2
        L_0x0078:
            j$.util.concurrent.m r9 = r5.d     // Catch:{ all -> 0x00c8 }
            if (r9 != 0) goto L_0x008d
            java.lang.Object r9 = r14.apply(r13)     // Catch:{ all -> 0x00c8 }
            if (r9 == 0) goto L_0x008b
            j$.util.concurrent.m r10 = new j$.util.concurrent.m     // Catch:{ all -> 0x00c8 }
            r10.<init>(r1, r13, r9, r0)     // Catch:{ all -> 0x00c8 }
            r5.d = r10     // Catch:{ all -> 0x00c8 }
            r5 = r9
            goto L_0x00b3
        L_0x008b:
            r5 = r9
            goto L_0x00b2
        L_0x008d:
            int r4 = r4 + 1
            r5 = r9
            goto L_0x0065
        L_0x0091:
            boolean r9 = r7 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x00c8 }
            if (r9 == 0) goto L_0x00b2
            r4 = r7
            j$.util.concurrent.r r4 = (j$.util.concurrent.r) r4     // Catch:{ all -> 0x00c8 }
            j$.util.concurrent.s r5 = r4.e     // Catch:{ all -> 0x00c8 }
            if (r5 == 0) goto L_0x00a6
            j$.util.concurrent.s r5 = r5.b(r1, r13, r0)     // Catch:{ all -> 0x00c8 }
            if (r5 == 0) goto L_0x00a6
            java.lang.Object r4 = r5.c     // Catch:{ all -> 0x00c8 }
            r5 = r4
            goto L_0x00b1
        L_0x00a6:
            java.lang.Object r5 = r14.apply(r13)     // Catch:{ all -> 0x00c8 }
            if (r5 == 0) goto L_0x00b1
            r4.f(r1, r13, r5)     // Catch:{ all -> 0x00c8 }
            r4 = 2
            goto L_0x00b3
        L_0x00b1:
            r4 = 2
        L_0x00b2:
            r8 = 0
        L_0x00b3:
            monitor-exit(r7)     // Catch:{ all -> 0x00c8 }
            if (r4 == 0) goto L_0x0012
            r13 = 8
            if (r4 < r13) goto L_0x00bd
            r12.q(r2, r6)
        L_0x00bd:
            if (r8 != 0) goto L_0x00c0
            return r5
        L_0x00c0:
            if (r5 == 0) goto L_0x00c7
            r13 = 1
            r12.a(r13, r4)
        L_0x00c7:
            return r5
        L_0x00c8:
            r13 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00c8 }
            throw r13
        L_0x00cb:
            j$.util.concurrent.m[] r2 = r12.g()
            goto L_0x0012
        L_0x00d1:
            goto L_0x00d3
        L_0x00d2:
            throw r0
        L_0x00d3:
            goto L_0x00d2
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.computeIfAbsent(java.lang.Object, j$.util.function.Function):java.lang.Object");
    }

    public /* synthetic */ Object computeIfAbsent(Object obj, Function function) {
        return computeIfAbsent(obj, P.c(function));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0099, code lost:
        if (r3 == 0) goto L_0x009f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x009b, code lost:
        a((long) r3, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x009f, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object computeIfPresent(java.lang.Object r14, j$.util.function.BiFunction r15) {
        /*
            r13 = this;
            r0 = 0
            if (r14 == 0) goto L_0x00a9
            if (r15 == 0) goto L_0x00a9
            int r1 = r14.hashCode()
            int r1 = l(r1)
            j$.util.concurrent.m[] r2 = r13.a
            r3 = 0
            r5 = r0
            r4 = 0
        L_0x0012:
            if (r2 == 0) goto L_0x00a3
            int r6 = r2.length
            if (r6 != 0) goto L_0x0019
            goto L_0x00a3
        L_0x0019:
            int r6 = r6 + -1
            r6 = r6 & r1
            j$.util.concurrent.m r7 = n(r2, r6)
            if (r7 != 0) goto L_0x0024
            goto L_0x0099
        L_0x0024:
            int r8 = r7.a
            r9 = -1
            if (r8 != r9) goto L_0x002e
            j$.util.concurrent.m[] r2 = r13.f(r2, r7)
            goto L_0x0012
        L_0x002e:
            monitor-enter(r7)
            j$.util.concurrent.m r10 = n(r2, r6)     // Catch:{ all -> 0x00a0 }
            if (r10 != r7) goto L_0x0096
            if (r8 < 0) goto L_0x006c
            r4 = 1
            r10 = r0
            r8 = r7
        L_0x003a:
            int r11 = r8.a     // Catch:{ all -> 0x00a0 }
            if (r11 != r1) goto L_0x0061
            java.lang.Object r11 = r8.b     // Catch:{ all -> 0x00a0 }
            if (r11 == r14) goto L_0x004a
            if (r11 == 0) goto L_0x0061
            boolean r11 = r14.equals(r11)     // Catch:{ all -> 0x00a0 }
            if (r11 == 0) goto L_0x0061
        L_0x004a:
            java.lang.Object r5 = r8.c     // Catch:{ all -> 0x00a0 }
            java.lang.Object r5 = r15.apply(r14, r5)     // Catch:{ all -> 0x00a0 }
            if (r5 == 0) goto L_0x0055
            r8.c = r5     // Catch:{ all -> 0x00a0 }
            goto L_0x0096
        L_0x0055:
            j$.util.concurrent.m r3 = r8.d     // Catch:{ all -> 0x00a0 }
            if (r10 == 0) goto L_0x005c
            r10.d = r3     // Catch:{ all -> 0x00a0 }
            goto L_0x005f
        L_0x005c:
            k(r2, r6, r3)     // Catch:{ all -> 0x00a0 }
        L_0x005f:
            r3 = -1
            goto L_0x0096
        L_0x0061:
            j$.util.concurrent.m r10 = r8.d     // Catch:{ all -> 0x00a0 }
            if (r10 != 0) goto L_0x0066
            goto L_0x0096
        L_0x0066:
            int r4 = r4 + 1
            r12 = r10
            r10 = r8
            r8 = r12
            goto L_0x003a
        L_0x006c:
            boolean r8 = r7 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x00a0 }
            if (r8 == 0) goto L_0x0096
            r4 = 2
            r8 = r7
            j$.util.concurrent.r r8 = (j$.util.concurrent.r) r8     // Catch:{ all -> 0x00a0 }
            j$.util.concurrent.s r10 = r8.e     // Catch:{ all -> 0x00a0 }
            if (r10 == 0) goto L_0x0096
            j$.util.concurrent.s r10 = r10.b(r1, r14, r0)     // Catch:{ all -> 0x00a0 }
            if (r10 == 0) goto L_0x0096
            java.lang.Object r5 = r10.c     // Catch:{ all -> 0x00a0 }
            java.lang.Object r5 = r15.apply(r14, r5)     // Catch:{ all -> 0x00a0 }
            if (r5 == 0) goto L_0x0089
            r10.c = r5     // Catch:{ all -> 0x00a0 }
            goto L_0x0096
        L_0x0089:
            boolean r3 = r8.g(r10)     // Catch:{ all -> 0x00a0 }
            if (r3 == 0) goto L_0x005f
            j$.util.concurrent.s r3 = r8.f     // Catch:{ all -> 0x00a0 }
            j$.util.concurrent.m r3 = s(r3)     // Catch:{ all -> 0x00a0 }
            goto L_0x005c
        L_0x0096:
            monitor-exit(r7)     // Catch:{ all -> 0x00a0 }
            if (r4 == 0) goto L_0x0012
        L_0x0099:
            if (r3 == 0) goto L_0x009f
            long r14 = (long) r3
            r13.a(r14, r4)
        L_0x009f:
            return r5
        L_0x00a0:
            r14 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00a0 }
            throw r14
        L_0x00a3:
            j$.util.concurrent.m[] r2 = r13.g()
            goto L_0x0012
        L_0x00a9:
            goto L_0x00ab
        L_0x00aa:
            throw r0
        L_0x00ab:
            goto L_0x00aa
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.computeIfPresent(java.lang.Object, j$.util.function.BiFunction):java.lang.Object");
    }

    public /* synthetic */ Object computeIfPresent(Object obj, java.util.function.BiFunction biFunction) {
        return computeIfPresent(obj, CLASSNAMEv.b(biFunction));
    }

    public boolean containsKey(Object obj) {
        return get(obj) != null;
    }

    public boolean containsValue(Object obj) {
        obj.getClass();
        m[] mVarArr = this.a;
        if (mVarArr != null) {
            q qVar = new q(mVarArr, mVarArr.length, 0, mVarArr.length);
            while (true) {
                m a2 = qVar.a();
                if (a2 == null) {
                    break;
                }
                Object obj2 = a2.c;
                if (obj2 == obj) {
                    return true;
                }
                if (obj2 != null && obj.equals(obj2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set entrySet() {
        f fVar = this.f;
        if (fVar != null) {
            return fVar;
        }
        f fVar2 = new f(this);
        this.f = fVar2;
        return fVar2;
    }

    public boolean equals(Object obj) {
        Object value;
        Object obj2;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map map = (Map) obj;
        m[] mVarArr = this.a;
        int length = mVarArr == null ? 0 : mVarArr.length;
        q qVar = new q(mVarArr, length, 0, length);
        while (true) {
            m a2 = qVar.a();
            if (a2 != null) {
                Object obj3 = a2.c;
                Object obj4 = map.get(a2.b);
                if (obj4 == null || (obj4 != obj3 && !obj4.equals(obj3))) {
                    return false;
                }
            } else {
                for (Map.Entry entry : map.entrySet()) {
                    Object key = entry.getKey();
                    if (key == null || (value = entry.getValue()) == null || (obj2 = get(key)) == null || (value != obj2 && !value.equals(obj2))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public final m[] f(m[] mVarArr, m mVar) {
        m[] mVarArr2;
        int i2;
        if (mVarArr == null || !(mVar instanceof h) || (mVarArr2 = ((h) mVar).e) == null) {
            return this.a;
        }
        int j2 = j(mVarArr.length);
        while (true) {
            if (mVarArr2 != this.b || this.a != mVarArr || (i2 = this.sizeCtl) >= 0 || (i2 >>> 16) != j2 || i2 == j2 + 1 || i2 == 65535 + j2 || this.transferIndex <= 0) {
                break;
            }
            if (h.compareAndSwapInt(this, i, i2, i2 + 1)) {
                p(mVarArr, mVarArr2);
                break;
            }
        }
        return mVarArr2;
    }

    public void forEach(BiConsumer biConsumer) {
        biConsumer.getClass();
        m[] mVarArr = this.a;
        if (mVarArr != null) {
            q qVar = new q(mVarArr, mVarArr.length, 0, mVarArr.length);
            while (true) {
                m a2 = qVar.a();
                if (a2 != null) {
                    biConsumer.accept(a2.b, a2.c);
                } else {
                    return;
                }
            }
        }
    }

    public /* synthetic */ void forEach(java.util.function.BiConsumer biConsumer) {
        forEach(CLASSNAMEt.b(biConsumer));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x004d, code lost:
        return r1.c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object get(java.lang.Object r5) {
        /*
            r4 = this;
            int r0 = r5.hashCode()
            int r0 = l(r0)
            j$.util.concurrent.m[] r1 = r4.a
            r2 = 0
            if (r1 == 0) goto L_0x004e
            int r3 = r1.length
            if (r3 <= 0) goto L_0x004e
            int r3 = r3 + -1
            r3 = r3 & r0
            j$.util.concurrent.m r1 = n(r1, r3)
            if (r1 == 0) goto L_0x004e
            int r3 = r1.a
            if (r3 != r0) goto L_0x002c
            java.lang.Object r3 = r1.b
            if (r3 == r5) goto L_0x0029
            if (r3 == 0) goto L_0x0037
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x0037
        L_0x0029:
            java.lang.Object r5 = r1.c
            return r5
        L_0x002c:
            if (r3 >= 0) goto L_0x0037
            j$.util.concurrent.m r5 = r1.a(r0, r5)
            if (r5 == 0) goto L_0x0036
            java.lang.Object r2 = r5.c
        L_0x0036:
            return r2
        L_0x0037:
            j$.util.concurrent.m r1 = r1.d
            if (r1 == 0) goto L_0x004e
            int r3 = r1.a
            if (r3 != r0) goto L_0x0037
            java.lang.Object r3 = r1.b
            if (r3 == r5) goto L_0x004b
            if (r3 == 0) goto L_0x0037
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x0037
        L_0x004b:
            java.lang.Object r5 = r1.c
            return r5
        L_0x004e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.get(java.lang.Object):java.lang.Object");
    }

    public Object getOrDefault(Object obj, Object obj2) {
        Object obj3 = get(obj);
        return obj3 == null ? obj2 : obj3;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0053, code lost:
        if (r11 == false) goto L_0x0055;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object h(java.lang.Object r9, java.lang.Object r10, boolean r11) {
        /*
            r8 = this;
            r0 = 0
            if (r9 == 0) goto L_0x0098
            if (r10 == 0) goto L_0x0098
            int r1 = r9.hashCode()
            int r1 = l(r1)
            r2 = 0
            j$.util.concurrent.m[] r3 = r8.a
        L_0x0010:
            if (r3 == 0) goto L_0x0092
            int r4 = r3.length
            if (r4 != 0) goto L_0x0017
            goto L_0x0092
        L_0x0017:
            int r4 = r4 + -1
            r4 = r4 & r1
            j$.util.concurrent.m r5 = n(r3, r4)
            if (r5 != 0) goto L_0x002c
            j$.util.concurrent.m r5 = new j$.util.concurrent.m
            r5.<init>(r1, r9, r10, r0)
            boolean r4 = b(r3, r4, r0, r5)
            if (r4 == 0) goto L_0x0010
            goto L_0x0089
        L_0x002c:
            int r6 = r5.a
            r7 = -1
            if (r6 != r7) goto L_0x0036
            j$.util.concurrent.m[] r3 = r8.f(r3, r5)
            goto L_0x0010
        L_0x0036:
            monitor-enter(r5)
            j$.util.concurrent.m r7 = n(r3, r4)     // Catch:{ all -> 0x008f }
            if (r7 != r5) goto L_0x007b
            if (r6 < 0) goto L_0x0068
            r2 = 1
            r6 = r5
        L_0x0041:
            int r7 = r6.a     // Catch:{ all -> 0x008f }
            if (r7 != r1) goto L_0x0058
            java.lang.Object r7 = r6.b     // Catch:{ all -> 0x008f }
            if (r7 == r9) goto L_0x0051
            if (r7 == 0) goto L_0x0058
            boolean r7 = r9.equals(r7)     // Catch:{ all -> 0x008f }
            if (r7 == 0) goto L_0x0058
        L_0x0051:
            java.lang.Object r7 = r6.c     // Catch:{ all -> 0x008f }
            if (r11 != 0) goto L_0x007c
        L_0x0055:
            r6.c = r10     // Catch:{ all -> 0x008f }
            goto L_0x007c
        L_0x0058:
            j$.util.concurrent.m r7 = r6.d     // Catch:{ all -> 0x008f }
            if (r7 != 0) goto L_0x0064
            j$.util.concurrent.m r7 = new j$.util.concurrent.m     // Catch:{ all -> 0x008f }
            r7.<init>(r1, r9, r10, r0)     // Catch:{ all -> 0x008f }
            r6.d = r7     // Catch:{ all -> 0x008f }
            goto L_0x007b
        L_0x0064:
            int r2 = r2 + 1
            r6 = r7
            goto L_0x0041
        L_0x0068:
            boolean r6 = r5 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x008f }
            if (r6 == 0) goto L_0x007b
            r2 = 2
            r6 = r5
            j$.util.concurrent.r r6 = (j$.util.concurrent.r) r6     // Catch:{ all -> 0x008f }
            j$.util.concurrent.s r6 = r6.f(r1, r9, r10)     // Catch:{ all -> 0x008f }
            if (r6 == 0) goto L_0x007b
            java.lang.Object r7 = r6.c     // Catch:{ all -> 0x008f }
            if (r11 != 0) goto L_0x007c
            goto L_0x0055
        L_0x007b:
            r7 = r0
        L_0x007c:
            monitor-exit(r5)     // Catch:{ all -> 0x008f }
            if (r2 == 0) goto L_0x0010
            r9 = 8
            if (r2 < r9) goto L_0x0086
            r8.q(r3, r4)
        L_0x0086:
            if (r7 == 0) goto L_0x0089
            return r7
        L_0x0089:
            r9 = 1
            r8.a(r9, r2)
            return r0
        L_0x008f:
            r9 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x008f }
            throw r9
        L_0x0092:
            j$.util.concurrent.m[] r3 = r8.g()
            goto L_0x0010
        L_0x0098:
            goto L_0x009a
        L_0x0099:
            throw r0
        L_0x009a:
            goto L_0x0099
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.h(java.lang.Object, java.lang.Object, boolean):java.lang.Object");
    }

    public int hashCode() {
        m[] mVarArr = this.a;
        int i2 = 0;
        if (mVarArr != null) {
            q qVar = new q(mVarArr, mVarArr.length, 0, mVarArr.length);
            while (true) {
                m a2 = qVar.a();
                if (a2 == null) {
                    break;
                }
                i2 += a2.c.hashCode() ^ a2.b.hashCode();
            }
        }
        return i2;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x00af, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object i(java.lang.Object r13, java.lang.Object r14, java.lang.Object r15) {
        /*
            r12 = this;
            int r0 = r13.hashCode()
            int r0 = l(r0)
            j$.util.concurrent.m[] r1 = r12.a
        L_0x000a:
            r2 = 0
            if (r1 == 0) goto L_0x00af
            int r3 = r1.length
            if (r3 == 0) goto L_0x00af
            int r3 = r3 + -1
            r3 = r3 & r0
            j$.util.concurrent.m r4 = n(r1, r3)
            if (r4 != 0) goto L_0x001b
            goto L_0x00af
        L_0x001b:
            int r5 = r4.a
            r6 = -1
            if (r5 != r6) goto L_0x0025
            j$.util.concurrent.m[] r1 = r12.f(r1, r4)
            goto L_0x000a
        L_0x0025:
            r7 = 0
            monitor-enter(r4)
            j$.util.concurrent.m r8 = n(r1, r3)     // Catch:{ all -> 0x00ac }
            r9 = 1
            if (r8 != r4) goto L_0x009d
            if (r5 < 0) goto L_0x006c
            r7 = r2
            r5 = r4
        L_0x0032:
            int r8 = r5.a     // Catch:{ all -> 0x00ac }
            if (r8 != r0) goto L_0x0062
            java.lang.Object r8 = r5.b     // Catch:{ all -> 0x00ac }
            if (r8 == r13) goto L_0x0042
            if (r8 == 0) goto L_0x0062
            boolean r8 = r13.equals(r8)     // Catch:{ all -> 0x00ac }
            if (r8 == 0) goto L_0x0062
        L_0x0042:
            java.lang.Object r8 = r5.c     // Catch:{ all -> 0x00ac }
            if (r15 == 0) goto L_0x0050
            if (r15 == r8) goto L_0x0050
            if (r8 == 0) goto L_0x0066
            boolean r10 = r15.equals(r8)     // Catch:{ all -> 0x00ac }
            if (r10 == 0) goto L_0x0066
        L_0x0050:
            if (r14 == 0) goto L_0x0055
            r5.c = r14     // Catch:{ all -> 0x00ac }
            goto L_0x009f
        L_0x0055:
            if (r7 == 0) goto L_0x005c
            j$.util.concurrent.m r3 = r5.d     // Catch:{ all -> 0x00ac }
            r7.d = r3     // Catch:{ all -> 0x00ac }
            goto L_0x009f
        L_0x005c:
            j$.util.concurrent.m r5 = r5.d     // Catch:{ all -> 0x00ac }
        L_0x005e:
            k(r1, r3, r5)     // Catch:{ all -> 0x00ac }
            goto L_0x009f
        L_0x0062:
            j$.util.concurrent.m r7 = r5.d     // Catch:{ all -> 0x00ac }
            if (r7 != 0) goto L_0x0068
        L_0x0066:
            r7 = 1
            goto L_0x009d
        L_0x0068:
            r11 = r7
            r7 = r5
            r5 = r11
            goto L_0x0032
        L_0x006c:
            boolean r5 = r4 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x00ac }
            if (r5 == 0) goto L_0x009d
            r5 = r4
            j$.util.concurrent.r r5 = (j$.util.concurrent.r) r5     // Catch:{ all -> 0x00ac }
            j$.util.concurrent.s r7 = r5.e     // Catch:{ all -> 0x00ac }
            if (r7 == 0) goto L_0x0066
            j$.util.concurrent.s r7 = r7.b(r0, r13, r2)     // Catch:{ all -> 0x00ac }
            if (r7 == 0) goto L_0x0066
            java.lang.Object r8 = r7.c     // Catch:{ all -> 0x00ac }
            if (r15 == 0) goto L_0x008b
            if (r15 == r8) goto L_0x008b
            if (r8 == 0) goto L_0x0066
            boolean r10 = r15.equals(r8)     // Catch:{ all -> 0x00ac }
            if (r10 == 0) goto L_0x0066
        L_0x008b:
            if (r14 == 0) goto L_0x0090
            r7.c = r14     // Catch:{ all -> 0x00ac }
            goto L_0x009f
        L_0x0090:
            boolean r7 = r5.g(r7)     // Catch:{ all -> 0x00ac }
            if (r7 == 0) goto L_0x009f
            j$.util.concurrent.s r5 = r5.f     // Catch:{ all -> 0x00ac }
            j$.util.concurrent.m r5 = s(r5)     // Catch:{ all -> 0x00ac }
            goto L_0x005e
        L_0x009d:
            r8 = r2
            r9 = r7
        L_0x009f:
            monitor-exit(r4)     // Catch:{ all -> 0x00ac }
            if (r9 == 0) goto L_0x000a
            if (r8 == 0) goto L_0x00af
            if (r14 != 0) goto L_0x00ab
            r13 = -1
            r12.a(r13, r6)
        L_0x00ab:
            return r8
        L_0x00ac:
            r13 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x00ac }
            throw r13
        L_0x00af:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.i(java.lang.Object, java.lang.Object, java.lang.Object):java.lang.Object");
    }

    public boolean isEmpty() {
        return m() <= 0;
    }

    public Set keySet() {
        j jVar = this.d;
        if (jVar != null) {
            return jVar;
        }
        j jVar2 = new j(this, (Object) null);
        this.d = jVar2;
        return jVar2;
    }

    /* access modifiers changed from: package-private */
    public final long m() {
        CLASSNAMEd[] dVarArr = this.c;
        long j2 = this.baseCount;
        if (dVarArr != null) {
            for (CLASSNAMEd dVar : dVarArr) {
                if (dVar != null) {
                    j2 += dVar.value;
                }
            }
        }
        return j2;
    }

    public Object merge(Object obj, Object obj2, BiFunction biFunction) {
        int i2;
        Object obj3;
        Object obj4 = obj;
        Object obj5 = obj2;
        BiFunction biFunction2 = biFunction;
        if (obj4 == null || obj5 == null || biFunction2 == null) {
            throw null;
        }
        int l2 = l(obj.hashCode());
        m[] mVarArr = this.a;
        int i3 = 0;
        Object obj6 = null;
        int i4 = 0;
        while (true) {
            if (mVarArr != null) {
                int length = mVarArr.length;
                if (length != 0) {
                    int i5 = (length - 1) & l2;
                    m n2 = n(mVarArr, i5);
                    i2 = 1;
                    if (n2 != null) {
                        int i6 = n2.a;
                        if (i6 == -1) {
                            mVarArr = f(mVarArr, n2);
                        } else {
                            synchronized (n2) {
                                if (n(mVarArr, i5) == n2) {
                                    if (i6 >= 0) {
                                        m mVar = null;
                                        m mVar2 = n2;
                                        int i7 = 1;
                                        while (true) {
                                            if (mVar2.a != l2 || ((obj3 = mVar2.b) != obj4 && (obj3 == null || !obj4.equals(obj3)))) {
                                                m mVar3 = mVar2.d;
                                                if (mVar3 == null) {
                                                    mVar2.d = new m(l2, obj4, obj5, (m) null);
                                                    i3 = i7;
                                                    i4 = 1;
                                                    obj6 = obj5;
                                                    break;
                                                }
                                                i7++;
                                                m mVar4 = mVar3;
                                                mVar = mVar2;
                                                mVar2 = mVar4;
                                            }
                                        }
                                        Object apply = biFunction2.apply(mVar2.c, obj5);
                                        if (apply != null) {
                                            mVar2.c = apply;
                                            i3 = i7;
                                            obj6 = apply;
                                        } else {
                                            m mVar5 = mVar2.d;
                                            if (mVar != null) {
                                                mVar.d = mVar5;
                                            } else {
                                                k(mVarArr, i5, mVar5);
                                            }
                                            i3 = i7;
                                            obj6 = apply;
                                        }
                                    } else if (n2 instanceof r) {
                                        i3 = 2;
                                        r rVar = (r) n2;
                                        s sVar = rVar.e;
                                        s b2 = sVar == null ? null : sVar.b(l2, obj4, (Class) null);
                                        Object apply2 = b2 == null ? obj5 : biFunction2.apply(b2.c, obj5);
                                        if (apply2 != null) {
                                            if (b2 != null) {
                                                b2.c = apply2;
                                            } else {
                                                rVar.f(l2, obj4, apply2);
                                                obj6 = apply2;
                                                i4 = 1;
                                            }
                                        } else if (b2 != null) {
                                            if (rVar.g(b2)) {
                                                k(mVarArr, i5, s(rVar.f));
                                            }
                                            obj6 = apply2;
                                        }
                                        obj6 = apply2;
                                    }
                                    i4 = -1;
                                }
                            }
                            if (i3 != 0) {
                                if (i3 >= 8) {
                                    q(mVarArr, i5);
                                }
                                i2 = i4;
                                obj5 = obj6;
                            }
                        }
                    } else if (b(mVarArr, i5, (m) null, new m(l2, obj4, obj5, (m) null))) {
                        break;
                    }
                }
            }
            mVarArr = g();
        }
        if (i2 != 0) {
            a((long) i2, i3);
        }
        return obj5;
    }

    public /* synthetic */ Object merge(Object obj, Object obj2, java.util.function.BiFunction biFunction) {
        return merge(obj, obj2, CLASSNAMEv.b(biFunction));
    }

    public Object put(Object obj, Object obj2) {
        return h(obj, obj2, false);
    }

    public void putAll(Map map) {
        r(map.size());
        for (Map.Entry entry : map.entrySet()) {
            h(entry.getKey(), entry.getValue(), false);
        }
    }

    public Object putIfAbsent(Object obj, Object obj2) {
        return h(obj, obj2, true);
    }

    public Object remove(Object obj) {
        return i(obj, (Object) null, (Object) null);
    }

    public boolean remove(Object obj, Object obj2) {
        obj.getClass();
        return (obj2 == null || i(obj, (Object) null, obj2) == null) ? false : true;
    }

    public Object replace(Object obj, Object obj2) {
        if (obj != null && obj2 != null) {
            return i(obj, obj2, (Object) null);
        }
        throw null;
    }

    public boolean replace(Object obj, Object obj2, Object obj3) {
        if (obj != null && obj2 != null && obj3 != null) {
            return i(obj, obj3, obj2) != null;
        }
        throw null;
    }

    public void replaceAll(BiFunction biFunction) {
        biFunction.getClass();
        m[] mVarArr = this.a;
        if (mVarArr != null) {
            q qVar = new q(mVarArr, mVarArr.length, 0, mVarArr.length);
            while (true) {
                m a2 = qVar.a();
                if (a2 != null) {
                    Object obj = a2.c;
                    Object obj2 = a2.b;
                    do {
                        Object apply = biFunction.apply(obj2, obj);
                        apply.getClass();
                        if (i(obj2, apply, obj) != null || (obj = get(obj2)) == null) {
                        }
                        Object apply2 = biFunction.apply(obj2, obj);
                        apply2.getClass();
                        break;
                    } while ((obj = get(obj2)) == null);
                } else {
                    return;
                }
            }
        }
    }

    public /* synthetic */ void replaceAll(java.util.function.BiFunction biFunction) {
        replaceAll(CLASSNAMEv.b(biFunction));
    }

    public int size() {
        long m2 = m();
        if (m2 < 0) {
            return 0;
        }
        if (m2 > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) m2;
    }

    public String toString() {
        m[] mVarArr = this.a;
        int length = mVarArr == null ? 0 : mVarArr.length;
        q qVar = new q(mVarArr, length, 0, length);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        m a2 = qVar.a();
        if (a2 != null) {
            while (true) {
                Object obj = a2.b;
                Object obj2 = a2.c;
                if (obj == this) {
                    obj = "(this Map)";
                }
                sb.append(obj);
                sb.append('=');
                if (obj2 == this) {
                    obj2 = "(this Map)";
                }
                sb.append(obj2);
                a2 = qVar.a();
                if (a2 == null) {
                    break;
                }
                sb.append(',');
                sb.append(' ');
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public Collection values() {
        v vVar = this.e;
        if (vVar != null) {
            return vVar;
        }
        v vVar2 = new v(this);
        this.e = vVar2;
        return vVar2;
    }
}

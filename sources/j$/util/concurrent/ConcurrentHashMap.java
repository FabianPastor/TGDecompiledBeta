package j$.util.concurrent;

import j$.A;
import j$.CLASSNAMEy;
import j$.O;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import sun.misc.Unsafe;

public class ConcurrentHashMap extends AbstractMap implements ConcurrentMap, Serializable, y {
    private static int g = 16;
    private static final int h = 65535;
    private static final int i = 16;
    static final int j = Runtime.getRuntime().availableProcessors();
    private static final Unsafe k;
    private static final long l;
    private static final long m;
    private static final long n;
    private static final long o;
    private static final long p;
    private static final long q;
    private static final int r;
    volatile transient m[] a;
    private volatile transient m[] b;
    private volatile transient long baseCount;
    private volatile transient CLASSNAMEd[] c;
    private volatile transient int cellsBusy;
    private transient j d;
    private transient v e;
    private transient CLASSNAMEf f;
    private volatile transient int sizeCtl;
    private volatile transient int transferIndex;

    public /* synthetic */ Object compute(Object obj, BiFunction biFunction) {
        return compute(obj, A.b(biFunction));
    }

    public /* synthetic */ Object computeIfAbsent(Object obj, Function function) {
        return computeIfAbsent(obj, O.c(function));
    }

    public /* synthetic */ Object computeIfPresent(Object obj, BiFunction biFunction) {
        return computeIfPresent(obj, A.b(biFunction));
    }

    public /* synthetic */ void forEach(BiConsumer biConsumer) {
        forEach(CLASSNAMEy.b(biConsumer));
    }

    public /* synthetic */ Object merge(Object obj, Object obj2, BiFunction biFunction) {
        return merge(obj, obj2, A.b(biFunction));
    }

    public /* synthetic */ void replaceAll(BiFunction biFunction) {
        replaceAll(A.b(biFunction));
    }

    static {
        ObjectStreamField[] objectStreamFieldArr = {new ObjectStreamField("segments", o[].class), new ObjectStreamField("segmentMask", Integer.TYPE), new ObjectStreamField("segmentShift", Integer.TYPE)};
        try {
            Unsafe c2 = z.c();
            k = c2;
            Class<ConcurrentHashMap> cls = ConcurrentHashMap.class;
            l = c2.objectFieldOffset(cls.getDeclaredField("sizeCtl"));
            m = k.objectFieldOffset(cls.getDeclaredField("transferIndex"));
            n = k.objectFieldOffset(cls.getDeclaredField("baseCount"));
            o = k.objectFieldOffset(cls.getDeclaredField("cellsBusy"));
            p = k.objectFieldOffset(CLASSNAMEd.class.getDeclaredField("value"));
            Class<m[]> cls2 = m[].class;
            q = (long) k.arrayBaseOffset(cls2);
            int scale = k.arrayIndexScale(cls2);
            if (((scale - 1) & scale) == 0) {
                r = 31 - Integer.numberOfLeadingZeros(scale);
                return;
            }
            throw new Error("data type scale not a power of two");
        } catch (Exception e2) {
            throw new Error(e2);
        }
    }

    static final int m(int h2) {
        return ((h2 >>> 16) ^ h2) & Integer.MAX_VALUE;
    }

    private static final int p(int c2) {
        int n2 = c2 - 1;
        int n3 = n2 | (n2 >>> 1);
        int n4 = n3 | (n3 >>> 2);
        int n5 = n4 | (n4 >>> 4);
        int n6 = n5 | (n5 >>> 8);
        int n7 = n6 | (n6 >>> 16);
        if (n7 < 0) {
            return 1;
        }
        if (n7 >= NUM) {
            return NUM;
        }
        return n7 + 1;
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.Class c(java.lang.Object r8) {
        /*
            boolean r0 = r8 instanceof java.lang.Comparable
            if (r0 == 0) goto L_0x0040
            java.lang.Class r0 = r8.getClass()
            r1 = r0
            java.lang.Class<java.lang.String> r2 = java.lang.String.class
            if (r0 != r2) goto L_0x000e
            return r1
        L_0x000e:
            java.lang.reflect.Type[] r0 = r1.getGenericInterfaces()
            r2 = r0
            if (r0 == 0) goto L_0x0040
            r0 = 0
        L_0x0016:
            int r3 = r2.length
            if (r0 >= r3) goto L_0x0040
            r3 = r2[r0]
            r4 = r3
            boolean r3 = r3 instanceof java.lang.reflect.ParameterizedType
            if (r3 == 0) goto L_0x003d
            r3 = r4
            java.lang.reflect.ParameterizedType r3 = (java.lang.reflect.ParameterizedType) r3
            r5 = r3
            java.lang.reflect.Type r3 = r3.getRawType()
            java.lang.Class<java.lang.Comparable> r6 = java.lang.Comparable.class
            if (r3 != r6) goto L_0x003d
            java.lang.reflect.Type[] r3 = r5.getActualTypeArguments()
            r6 = r3
            if (r3 == 0) goto L_0x003d
            int r3 = r6.length
            r7 = 1
            if (r3 != r7) goto L_0x003d
            r3 = 0
            r3 = r6[r3]
            if (r3 != r1) goto L_0x003d
            return r1
        L_0x003d:
            int r0 = r0 + 1
            goto L_0x0016
        L_0x0040:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.c(java.lang.Object):java.lang.Class");
    }

    static int d(Class kc, Object k2, Object x) {
        if (x == null || x.getClass() != kc) {
            return 0;
        }
        return ((Comparable) k2).compareTo(x);
    }

    static final m o(m[] tab, int i2) {
        return (m) k.getObjectVolatile(tab, (((long) i2) << r) + q);
    }

    static final boolean b(m[] tab, int i2, m c2, m v) {
        return k.compareAndSwapObject(tab, q + (((long) i2) << r), c2, v);
    }

    static final void l(m[] tab, int i2, m v) {
        k.putObjectVolatile(tab, (((long) i2) << r) + q, v);
    }

    public ConcurrentHashMap() {
    }

    public ConcurrentHashMap(int initialCapacity) {
        int cap;
        if (initialCapacity >= 0) {
            if (initialCapacity >= NUM) {
                cap = NUM;
            } else {
                cap = p((initialCapacity >>> 1) + initialCapacity + 1);
            }
            this.sizeCtl = cap;
            return;
        }
        throw new IllegalArgumentException();
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        if (loadFactor <= 0.0f || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        double d2 = (double) (((float) ((long) (initialCapacity < concurrencyLevel ? concurrencyLevel : initialCapacity))) / loadFactor);
        Double.isNaN(d2);
        long size = (long) (d2 + 1.0d);
        this.sizeCtl = size >= NUM ? NUM : p((int) size);
    }

    public int size() {
        long n2 = n();
        if (n2 < 0) {
            return 0;
        }
        if (n2 > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) n2;
    }

    public boolean isEmpty() {
        return n() <= 0;
    }

    public Object get(Object key) {
        int h2 = m(key.hashCode());
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.a;
        ConcurrentHashMap.Node<K, V>[] tab = nodeArr;
        if (nodeArr != null) {
            int length = tab.length;
            int n2 = length;
            if (length > 0) {
                ConcurrentHashMap.Node<K, V> o2 = o(tab, (n2 - 1) & h2);
                ConcurrentHashMap.Node<K, V> e2 = o2;
                if (o2 != null) {
                    int i2 = e2.a;
                    int eh = i2;
                    if (i2 == h2) {
                        K k2 = e2.b;
                        K ek = k2;
                        if (k2 == key || (ek != null && key.equals(ek))) {
                            return e2.c;
                        }
                    } else if (eh < 0) {
                        ConcurrentHashMap.Node<K, V> a2 = e2.a(h2, key);
                        ConcurrentHashMap.Node<K, V> p2 = a2;
                        if (a2 != null) {
                            return p2.c;
                        }
                        return null;
                    }
                    while (true) {
                        ConcurrentHashMap.Node<K, V> node = e2.d;
                        e2 = node;
                        if (node == null) {
                            break;
                        } else if (e2.a == h2) {
                            K k3 = e2.b;
                            K ek2 = k3;
                            if (k3 == key || (ek2 != null && key.equals(ek2))) {
                            }
                        }
                    }
                    return e2.c;
                }
            }
        }
        return null;
    }

    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    public boolean containsValue(Object value) {
        if (value != null) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.a;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            if (nodeArr != null) {
                ConcurrentHashMap.Traverser<K, V> it = new q(t, t.length, 0, t.length);
                while (true) {
                    ConcurrentHashMap.Node<K, V> b2 = it.b();
                    ConcurrentHashMap.Node<K, V> p2 = b2;
                    if (b2 == null) {
                        break;
                    }
                    V v = p2.c;
                    V v2 = v;
                    if (v == value) {
                        return true;
                    }
                    if (v2 != null && value.equals(v2)) {
                        return true;
                    }
                }
            }
            return false;
        }
        throw null;
    }

    public Object put(Object key, Object value) {
        return i(key, value, false);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0096, code lost:
        a(1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x009b, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object i(java.lang.Object r13, java.lang.Object r14, boolean r15) {
        /*
            r12 = this;
            r0 = 0
            if (r13 == 0) goto L_0x00a5
            if (r14 == 0) goto L_0x00a5
            int r1 = r13.hashCode()
            int r1 = m(r1)
            r2 = 0
            j$.util.concurrent.m[] r3 = r12.a
        L_0x0010:
            if (r3 == 0) goto L_0x009f
            int r4 = r3.length
            r5 = r4
            if (r4 != 0) goto L_0x0018
            goto L_0x009f
        L_0x0018:
            int r4 = r5 + -1
            r4 = r4 & r1
            r6 = r4
            j$.util.concurrent.m r4 = o(r3, r4)
            r7 = r4
            if (r4 != 0) goto L_0x0030
            j$.util.concurrent.m r4 = new j$.util.concurrent.m
            r4.<init>(r1, r13, r14, r0)
            boolean r4 = b(r3, r6, r0, r4)
            if (r4 == 0) goto L_0x00a3
            goto L_0x0096
        L_0x0030:
            int r4 = r7.a
            r8 = r4
            r9 = -1
            if (r4 != r9) goto L_0x003c
            j$.util.concurrent.m[] r3 = r12.f(r3, r7)
            goto L_0x00a3
        L_0x003c:
            r4 = 0
            monitor-enter(r7)
            j$.util.concurrent.m r9 = o(r3, r6)     // Catch:{ all -> 0x009c }
            if (r9 != r7) goto L_0x0089
            if (r8 < 0) goto L_0x0073
            r2 = 1
            r9 = r7
        L_0x0048:
            int r10 = r9.a     // Catch:{ all -> 0x009c }
            if (r10 != r1) goto L_0x0061
            java.lang.Object r10 = r9.b     // Catch:{ all -> 0x009c }
            r11 = r10
            if (r10 == r13) goto L_0x0059
            if (r11 == 0) goto L_0x0061
            boolean r10 = r13.equals(r11)     // Catch:{ all -> 0x009c }
            if (r10 == 0) goto L_0x0061
        L_0x0059:
            java.lang.Object r10 = r9.c     // Catch:{ all -> 0x009c }
            r4 = r10
            if (r15 != 0) goto L_0x006f
            r9.c = r14     // Catch:{ all -> 0x009c }
            goto L_0x006f
        L_0x0061:
            r10 = r9
            j$.util.concurrent.m r11 = r9.d     // Catch:{ all -> 0x009c }
            r9 = r11
            if (r11 != 0) goto L_0x0070
            j$.util.concurrent.m r11 = new j$.util.concurrent.m     // Catch:{ all -> 0x009c }
            r11.<init>(r1, r13, r14, r0)     // Catch:{ all -> 0x009c }
            r10.d = r11     // Catch:{ all -> 0x009c }
        L_0x006f:
            goto L_0x0089
        L_0x0070:
            int r2 = r2 + 1
            goto L_0x0048
        L_0x0073:
            boolean r9 = r7 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x009c }
            if (r9 == 0) goto L_0x0089
            r2 = 2
            r9 = r7
            j$.util.concurrent.r r9 = (j$.util.concurrent.r) r9     // Catch:{ all -> 0x009c }
            j$.util.concurrent.s r9 = r9.f(r1, r13, r14)     // Catch:{ all -> 0x009c }
            r10 = r9
            if (r9 == 0) goto L_0x0089
            java.lang.Object r9 = r10.c     // Catch:{ all -> 0x009c }
            r4 = r9
            if (r15 != 0) goto L_0x0089
            r10.c = r14     // Catch:{ all -> 0x009c }
        L_0x0089:
            monitor-exit(r7)     // Catch:{ all -> 0x009c }
            if (r2 == 0) goto L_0x00a3
            r9 = 8
            if (r2 < r9) goto L_0x0093
            r12.r(r3, r6)
        L_0x0093:
            if (r4 == 0) goto L_0x0096
            return r4
        L_0x0096:
            r3 = 1
            r12.a(r3, r2)
            return r0
        L_0x009c:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x009c }
            throw r0
        L_0x009f:
            j$.util.concurrent.m[] r3 = r12.g()
        L_0x00a3:
            goto L_0x0010
        L_0x00a5:
            goto L_0x00a7
        L_0x00a6:
            throw r0
        L_0x00a7:
            goto L_0x00a6
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.i(java.lang.Object, java.lang.Object, boolean):java.lang.Object");
    }

    public void putAll(Map m2) {
        s(m2.size());
        for (Map.Entry<? extends K, ? extends V> e2 : m2.entrySet()) {
            i(e2.getKey(), e2.getValue(), false);
        }
    }

    public Object remove(Object key) {
        return j(key, (Object) null, (Object) null);
    }

    /* access modifiers changed from: package-private */
    public final Object j(Object key, Object value, Object cv) {
        Object obj = key;
        Object obj2 = value;
        Object obj3 = cv;
        int hash = m(key.hashCode());
        ConcurrentHashMap.Node<K, V>[] tab = this.a;
        while (tab != null) {
            int length = tab.length;
            int n2 = length;
            if (length == 0) {
                return null;
            }
            int i2 = (n2 - 1) & hash;
            int i3 = i2;
            ConcurrentHashMap.Node<K, V> o2 = o(tab, i2);
            ConcurrentHashMap.Node<K, V> f2 = o2;
            if (o2 == null) {
                return null;
            }
            int i4 = f2.a;
            int fh = i4;
            if (i4 == -1) {
                tab = f(tab, f2);
            } else {
                V oldVal = null;
                boolean validated = false;
                synchronized (f2) {
                    if (o(tab, i3) == f2) {
                        if (fh >= 0) {
                            validated = true;
                            ConcurrentHashMap.Node<K, V> e2 = f2;
                            ConcurrentHashMap.Node<K, V> pred = null;
                            while (true) {
                                if (e2.a == hash) {
                                    K k2 = e2.b;
                                    K ek = k2;
                                    if (k2 == obj) {
                                        break;
                                    }
                                    K ek2 = ek;
                                    if (ek2 != null && obj.equals(ek2)) {
                                        break;
                                    }
                                }
                                pred = e2;
                                ConcurrentHashMap.Node<K, V> node = e2.d;
                                e2 = node;
                                if (node == null) {
                                    break;
                                }
                            }
                            V ev = e2.c;
                            if (obj3 == null || obj3 == ev || (ev != null && obj3.equals(ev))) {
                                oldVal = ev;
                                if (obj2 != null) {
                                    e2.c = obj2;
                                } else if (pred != null) {
                                    V v = ev;
                                    pred.d = e2.d;
                                } else {
                                    l(tab, i3, e2.d);
                                }
                            }
                        } else if (f2 instanceof r) {
                            validated = true;
                            ConcurrentHashMap.TreeBin<K, V> t = (r) f2;
                            s sVar = t.e;
                            s sVar2 = sVar;
                            if (sVar != null) {
                                ConcurrentHashMap.TreeNode<K, V> b2 = sVar2.b(hash, obj, (Class) null);
                                ConcurrentHashMap.TreeNode<K, V> p2 = b2;
                                if (b2 != null) {
                                    V pv = p2.c;
                                    if (obj3 == null || obj3 == pv || (pv != null && obj3.equals(pv))) {
                                        oldVal = pv;
                                        if (obj2 != null) {
                                            p2.c = obj2;
                                        } else if (t.g(p2)) {
                                            l(tab, i3, t(t.f));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (validated) {
                    if (oldVal == null) {
                        return null;
                    }
                    if (obj2 == null) {
                        a(-1, -1);
                    }
                    return oldVal;
                }
            }
            obj = key;
        }
        return null;
    }

    public void clear() {
        ConcurrentHashMap.Node<K, V> p2;
        long delta = 0;
        int i2 = 0;
        ConcurrentHashMap.Node<K, V>[] tab = this.a;
        while (tab != null && i2 < tab.length) {
            ConcurrentHashMap.Node<K, V> f2 = o(tab, i2);
            if (f2 == null) {
                i2++;
            } else {
                int i3 = f2.a;
                int fh = i3;
                if (i3 == -1) {
                    tab = f(tab, f2);
                    i2 = 0;
                } else {
                    synchronized (f2) {
                        try {
                            if (o(tab, i2) == f2) {
                                if (fh >= 0) {
                                    p2 = f2;
                                } else {
                                    p2 = f2 instanceof r ? ((r) f2).f : null;
                                }
                                while (p2 != null) {
                                    delta--;
                                    p2 = p2.d;
                                }
                                int i4 = i2 + 1;
                                try {
                                    l(tab, i2, (m) null);
                                    i2 = i4;
                                } catch (Throwable th) {
                                    th = th;
                                    while (true) {
                                        try {
                                            break;
                                        } catch (Throwable th2) {
                                            th = th2;
                                        }
                                    }
                                    throw th;
                                }
                            }
                            try {
                            } catch (Throwable th3) {
                                th = th3;
                                int i5 = i2;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            int i6 = i2;
                            while (true) {
                                break;
                            }
                            throw th;
                        }
                    }
                }
            }
        }
        if (delta != 0) {
            a(delta, -1);
        }
    }

    public Set keySet() {
        j jVar = this.d;
        j jVar2 = jVar;
        if (jVar != null) {
            return jVar2;
        }
        j jVar3 = new j(this, (Object) null);
        this.d = jVar3;
        return jVar3;
    }

    public Collection values() {
        ConcurrentHashMap.ValuesView<K, V> valuesView = this.e;
        ConcurrentHashMap.ValuesView<K, V> vs = valuesView;
        if (valuesView != null) {
            return vs;
        }
        v vVar = new v(this);
        this.e = vVar;
        return vVar;
    }

    public Set entrySet() {
        ConcurrentHashMap.EntrySetView<K, V> entrySetView = this.f;
        ConcurrentHashMap.EntrySetView<K, V> es = entrySetView;
        if (entrySetView != null) {
            return es;
        }
        CLASSNAMEf fVar = new CLASSNAMEf(this);
        this.f = fVar;
        return fVar;
    }

    public int hashCode() {
        int h2 = 0;
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.a;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        if (nodeArr != null) {
            ConcurrentHashMap.Traverser<K, V> it = new q(t, t.length, 0, t.length);
            while (true) {
                ConcurrentHashMap.Node<K, V> b2 = it.b();
                ConcurrentHashMap.Node<K, V> p2 = b2;
                if (b2 == null) {
                    break;
                }
                h2 += p2.b.hashCode() ^ p2.c.hashCode();
            }
        }
        return h2;
    }

    public String toString() {
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.a;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f2 = nodeArr == null ? 0 : t.length;
        q qVar = new q(t, f2, 0, f2);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        ConcurrentHashMap.Node<K, V> b2 = qVar.b();
        ConcurrentHashMap.Node<K, V> p2 = b2;
        if (b2 != null) {
            while (true) {
                K k2 = p2.b;
                K k3 = p2.c;
                K k4 = "(this Map)";
                sb.append(k2 == this ? k4 : k2);
                sb.append('=');
                if (k3 != this) {
                    k4 = k3;
                }
                sb.append(k4);
                ConcurrentHashMap.Node<K, V> b3 = qVar.b();
                p2 = b3;
                if (b3 == null) {
                    break;
                }
                sb.append(',');
                sb.append(' ');
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object o2) {
        if (o2 == this) {
            return true;
        }
        if (!(o2 instanceof Map)) {
            return false;
        }
        Map<?, ?> m2 = (Map) o2;
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.a;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f2 = nodeArr == null ? 0 : t.length;
        ConcurrentHashMap.Traverser<K, V> it = new q(t, f2, 0, f2);
        while (true) {
            ConcurrentHashMap.Node<K, V> b2 = it.b();
            ConcurrentHashMap.Node<K, V> p2 = b2;
            if (b2 != null) {
                Object val = p2.c;
                Object v = m2.get(p2.b);
                if (v == null || (v != val && !v.equals(val))) {
                    return false;
                }
            } else {
                for (Map.Entry<?, ?> e2 : m2.entrySet()) {
                    Object key = e2.getKey();
                    Object mk = key;
                    if (key != null) {
                        Object value = e2.getValue();
                        Object mv = value;
                        if (value != null) {
                            Object obj = get(mk);
                            Object v2 = obj;
                            if (obj != null && (mv == v2 || mv.equals(v2))) {
                            }
                        }
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public Object putIfAbsent(Object key, Object value) {
        return i(key, value, true);
    }

    public boolean remove(Object key, Object value) {
        if (key != null) {
            return (value == null || j(key, (Object) null, value) == null) ? false : true;
        }
        throw null;
    }

    public boolean replace(Object key, Object oldValue, Object newValue) {
        if (key != null && oldValue != null && newValue != null) {
            return j(key, newValue, oldValue) != null;
        }
        throw null;
    }

    public Object replace(Object key, Object value) {
        if (key != null && value != null) {
            return j(key, value, (Object) null);
        }
        throw null;
    }

    public Object getOrDefault(Object key, Object defaultValue) {
        V v = get(key);
        return v == null ? defaultValue : v;
    }

    public void forEach(j$.util.function.BiConsumer biConsumer) {
        if (biConsumer != null) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.a;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            if (nodeArr != null) {
                ConcurrentHashMap.Traverser<K, V> it = new q(t, t.length, 0, t.length);
                while (true) {
                    ConcurrentHashMap.Node<K, V> b2 = it.b();
                    ConcurrentHashMap.Node<K, V> p2 = b2;
                    if (b2 != null) {
                        biConsumer.accept(p2.b, p2.c);
                    } else {
                        return;
                    }
                }
            }
        } else {
            throw null;
        }
    }

    public void replaceAll(j$.util.function.BiFunction biFunction) {
        V v;
        if (biFunction != null) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.a;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            if (nodeArr != null) {
                ConcurrentHashMap.Traverser<K, V> it = new q(t, t.length, 0, t.length);
                while (true) {
                    ConcurrentHashMap.Node<K, V> b2 = it.b();
                    ConcurrentHashMap.Node<K, V> p2 = b2;
                    if (b2 != null) {
                        V oldValue = p2.c;
                        K key = p2.b;
                        do {
                            V newValue = biFunction.a(key, oldValue);
                            if (newValue != null) {
                                if (j(key, newValue, oldValue) != null) {
                                    break;
                                }
                                v = get(key);
                                oldValue = v;
                            } else {
                                throw null;
                            }
                        } while (v != null);
                    } else {
                        return;
                    }
                }
            }
        } else {
            throw null;
        }
    }

    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v2, types: [java.lang.Class, j$.util.concurrent.m] */
    /* JADX WARNING: type inference failed for: r0v3 */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x005b, code lost:
        if (r6 == 0) goto L_0x00f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x00e2, code lost:
        if (r5 == null) goto L_0x00e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x00e4, code lost:
        a(1, r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x00e9, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object computeIfAbsent(java.lang.Object r18, j$.util.function.Function r19) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = r19
            r0 = 0
            if (r2 == 0) goto L_0x00f5
            if (r3 == 0) goto L_0x00f5
            int r4 = r18.hashCode()
            int r4 = m(r4)
            r5 = 0
            r6 = 0
            j$.util.concurrent.m[] r7 = r1.a
        L_0x0017:
            if (r7 == 0) goto L_0x00ed
            int r8 = r7.length
            r9 = r8
            if (r8 != 0) goto L_0x001f
            goto L_0x00ed
        L_0x001f:
            int r8 = r9 + -1
            r8 = r8 & r4
            r10 = r8
            j$.util.concurrent.m r8 = o(r7, r8)
            r11 = r8
            if (r8 != 0) goto L_0x0067
            j$.util.concurrent.n r8 = new j$.util.concurrent.n
            r8.<init>()
            monitor-enter(r8)
            boolean r12 = b(r7, r10, r0, r8)     // Catch:{ all -> 0x0061 }
            if (r12 == 0) goto L_0x005a
            r6 = 1
            r12 = 0
            java.lang.Object r13 = r3.apply(r2)     // Catch:{ all -> 0x004c }
            r5 = r13
            if (r13 == 0) goto L_0x0045
            j$.util.concurrent.m r13 = new j$.util.concurrent.m     // Catch:{ all -> 0x004c }
            r13.<init>(r4, r2, r5, r0)     // Catch:{ all -> 0x004c }
            r12 = r13
        L_0x0045:
            l(r7, r10, r12)     // Catch:{ all -> 0x0049 }
            goto L_0x005a
        L_0x0049:
            r0 = move-exception
            r12 = r1
            goto L_0x0063
        L_0x004c:
            r0 = move-exception
            l(r7, r10, r12)     // Catch:{ all -> 0x0061 }
            throw r0     // Catch:{ all -> 0x0052 }
        L_0x0052:
            r0 = move-exception
            r12 = r17
            r3 = r19
            r2 = r18
            goto L_0x0063
        L_0x005a:
            monitor-exit(r8)     // Catch:{ all -> 0x0061 }
            if (r6 == 0) goto L_0x005f
            goto L_0x00e2
        L_0x005f:
            goto L_0x00f2
        L_0x0061:
            r0 = move-exception
            r12 = r1
        L_0x0063:
            monitor-exit(r8)     // Catch:{ all -> 0x0065 }
            throw r0
        L_0x0065:
            r0 = move-exception
            goto L_0x0063
        L_0x0067:
            int r8 = r11.a
            r12 = r8
            r13 = -1
            if (r8 != r13) goto L_0x0073
            j$.util.concurrent.m[] r7 = r1.f(r7, r11)
            goto L_0x00f2
        L_0x0073:
            r8 = 0
            monitor-enter(r11)
            j$.util.concurrent.m r13 = o(r7, r10)     // Catch:{ all -> 0x00ea }
            if (r13 != r11) goto L_0x00d5
            if (r12 < 0) goto L_0x00ad
            r6 = 1
            r13 = r11
        L_0x007f:
            int r14 = r13.a     // Catch:{ all -> 0x00ea }
            if (r14 != r4) goto L_0x0094
            java.lang.Object r14 = r13.b     // Catch:{ all -> 0x00ea }
            r15 = r14
            if (r14 == r2) goto L_0x0090
            if (r15 == 0) goto L_0x0094
            boolean r14 = r2.equals(r15)     // Catch:{ all -> 0x00ea }
            if (r14 == 0) goto L_0x0094
        L_0x0090:
            java.lang.Object r14 = r13.c     // Catch:{ all -> 0x00ea }
            r5 = r14
            goto L_0x00a9
        L_0x0094:
            r14 = r13
            j$.util.concurrent.m r15 = r13.d     // Catch:{ all -> 0x00ea }
            r13 = r15
            if (r15 != 0) goto L_0x00aa
            java.lang.Object r15 = r3.apply(r2)     // Catch:{ all -> 0x00ea }
            r5 = r15
            if (r15 == 0) goto L_0x00a9
            r8 = 1
            j$.util.concurrent.m r15 = new j$.util.concurrent.m     // Catch:{ all -> 0x00ea }
            r15.<init>(r4, r2, r5, r0)     // Catch:{ all -> 0x00ea }
            r14.d = r15     // Catch:{ all -> 0x00ea }
        L_0x00a9:
            goto L_0x00d5
        L_0x00aa:
            int r6 = r6 + 1
            goto L_0x007f
        L_0x00ad:
            boolean r13 = r11 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x00ea }
            if (r13 == 0) goto L_0x00d5
            r6 = 2
            r13 = r11
            j$.util.concurrent.r r13 = (j$.util.concurrent.r) r13     // Catch:{ all -> 0x00ea }
            j$.util.concurrent.s r14 = r13.e     // Catch:{ all -> 0x00ea }
            r15 = r14
            if (r14 == 0) goto L_0x00ca
            j$.util.concurrent.s r14 = r15.b(r4, r2, r0)     // Catch:{ all -> 0x00ea }
            r16 = r14
            if (r14 == 0) goto L_0x00c8
            r14 = r16
            java.lang.Object r0 = r14.c     // Catch:{ all -> 0x00ea }
            r5 = r0
            goto L_0x00d5
        L_0x00c8:
            r14 = r16
        L_0x00ca:
            java.lang.Object r0 = r3.apply(r2)     // Catch:{ all -> 0x00ea }
            r5 = r0
            if (r0 == 0) goto L_0x00d5
            r8 = 1
            r13.f(r4, r2, r5)     // Catch:{ all -> 0x00ea }
        L_0x00d5:
            monitor-exit(r11)     // Catch:{ all -> 0x00ea }
            if (r6 == 0) goto L_0x00f2
            r0 = 8
            if (r6 < r0) goto L_0x00df
            r1.r(r7, r10)
        L_0x00df:
            if (r8 != 0) goto L_0x00e2
            return r5
        L_0x00e2:
            if (r5 == 0) goto L_0x00e9
            r7 = 1
            r1.a(r7, r6)
        L_0x00e9:
            return r5
        L_0x00ea:
            r0 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x00ea }
            throw r0
        L_0x00ed:
            j$.util.concurrent.m[] r0 = r17.g()
            r7 = r0
        L_0x00f2:
            r0 = 0
            goto L_0x0017
        L_0x00f5:
            r0 = 0
            goto L_0x00f8
        L_0x00f7:
            throw r0
        L_0x00f8:
            goto L_0x00f7
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.computeIfAbsent(java.lang.Object, j$.util.function.Function):java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00b3, code lost:
        if (r6 == 0) goto L_0x00b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00b5, code lost:
        a((long) r6, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00b9, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object computeIfPresent(java.lang.Object r18, j$.util.function.BiFunction r19) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = r19
            if (r2 == 0) goto L_0x00c4
            if (r3 == 0) goto L_0x00c4
            int r4 = r18.hashCode()
            int r4 = m(r4)
            r5 = 0
            r6 = 0
            r7 = 0
            j$.util.concurrent.m[] r8 = r1.a
        L_0x0017:
            if (r8 == 0) goto L_0x00bd
            int r9 = r8.length
            r10 = r9
            if (r9 != 0) goto L_0x001f
            goto L_0x00bd
        L_0x001f:
            int r9 = r10 + -1
            r9 = r9 & r4
            r11 = r9
            j$.util.concurrent.m r9 = o(r8, r9)
            r12 = r9
            if (r9 != 0) goto L_0x002c
            goto L_0x00b3
        L_0x002c:
            int r9 = r12.a
            r13 = r9
            r14 = -1
            if (r9 != r14) goto L_0x0038
            j$.util.concurrent.m[] r8 = r1.f(r8, r12)
            goto L_0x00c2
        L_0x0038:
            monitor-enter(r12)
            j$.util.concurrent.m r9 = o(r8, r11)     // Catch:{ all -> 0x00ba }
            if (r9 != r12) goto L_0x00af
            if (r13 < 0) goto L_0x007e
            r7 = 1
            r9 = r12
            r14 = 0
        L_0x0044:
            int r15 = r9.a     // Catch:{ all -> 0x00ba }
            if (r15 != r4) goto L_0x0073
            java.lang.Object r15 = r9.b     // Catch:{ all -> 0x00ba }
            r16 = r15
            if (r15 == r2) goto L_0x0059
            r15 = r16
            if (r15 == 0) goto L_0x0073
            boolean r16 = r2.equals(r15)     // Catch:{ all -> 0x00ba }
            if (r16 == 0) goto L_0x0073
            goto L_0x005b
        L_0x0059:
            r15 = r16
        L_0x005b:
            java.lang.Object r0 = r9.c     // Catch:{ all -> 0x00ba }
            java.lang.Object r0 = r3.a(r2, r0)     // Catch:{ all -> 0x00ba }
            r5 = r0
            if (r5 == 0) goto L_0x0067
            r9.c = r5     // Catch:{ all -> 0x00ba }
            goto L_0x007a
        L_0x0067:
            r6 = -1
            j$.util.concurrent.m r0 = r9.d     // Catch:{ all -> 0x00ba }
            if (r14 == 0) goto L_0x006f
            r14.d = r0     // Catch:{ all -> 0x00ba }
            goto L_0x0072
        L_0x006f:
            l(r8, r11, r0)     // Catch:{ all -> 0x00ba }
        L_0x0072:
            goto L_0x007a
        L_0x0073:
            r14 = r9
            j$.util.concurrent.m r0 = r9.d     // Catch:{ all -> 0x00ba }
            r9 = r0
            if (r0 != 0) goto L_0x007b
        L_0x007a:
            goto L_0x00af
        L_0x007b:
            int r7 = r7 + 1
            goto L_0x0044
        L_0x007e:
            boolean r0 = r12 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x00ba }
            if (r0 == 0) goto L_0x00af
            r7 = 2
            r0 = r12
            j$.util.concurrent.r r0 = (j$.util.concurrent.r) r0     // Catch:{ all -> 0x00ba }
            j$.util.concurrent.s r9 = r0.e     // Catch:{ all -> 0x00ba }
            r14 = r9
            if (r9 == 0) goto L_0x00af
            r9 = 0
            j$.util.concurrent.s r15 = r14.b(r4, r2, r9)     // Catch:{ all -> 0x00ba }
            r9 = r15
            if (r15 == 0) goto L_0x00af
            java.lang.Object r15 = r9.c     // Catch:{ all -> 0x00ba }
            java.lang.Object r15 = r3.a(r2, r15)     // Catch:{ all -> 0x00ba }
            r5 = r15
            if (r5 == 0) goto L_0x009f
            r9.c = r5     // Catch:{ all -> 0x00ba }
            goto L_0x00af
        L_0x009f:
            r6 = -1
            boolean r15 = r0.g(r9)     // Catch:{ all -> 0x00ba }
            if (r15 == 0) goto L_0x00af
            j$.util.concurrent.s r15 = r0.f     // Catch:{ all -> 0x00ba }
            j$.util.concurrent.m r15 = t(r15)     // Catch:{ all -> 0x00ba }
            l(r8, r11, r15)     // Catch:{ all -> 0x00ba }
        L_0x00af:
            monitor-exit(r12)     // Catch:{ all -> 0x00ba }
            if (r7 == 0) goto L_0x00c2
        L_0x00b3:
            if (r6 == 0) goto L_0x00b9
            long r8 = (long) r6
            r1.a(r8, r7)
        L_0x00b9:
            return r5
        L_0x00ba:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x00ba }
            throw r0
        L_0x00bd:
            j$.util.concurrent.m[] r0 = r17.g()
            r8 = r0
        L_0x00c2:
            goto L_0x0017
        L_0x00c4:
            r0 = 0
            goto L_0x00c7
        L_0x00c6:
            throw r0
        L_0x00c7:
            goto L_0x00c6
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.computeIfPresent(java.lang.Object, j$.util.function.BiFunction):java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x005d, code lost:
        if (r7 == 0) goto L_0x0124;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0115, code lost:
        if (r6 == 0) goto L_0x011b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x0117, code lost:
        a((long) r6, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x011b, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object compute(java.lang.Object r19, j$.util.function.BiFunction r20) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r20
            r0 = 0
            if (r2 == 0) goto L_0x0129
            if (r3 == 0) goto L_0x0129
            int r4 = r19.hashCode()
            int r4 = m(r4)
            r5 = 0
            r6 = 0
            r7 = 0
            j$.util.concurrent.m[] r8 = r1.a
        L_0x0018:
            if (r8 == 0) goto L_0x011f
            int r9 = r8.length
            r10 = r9
            if (r9 != 0) goto L_0x0020
            goto L_0x011f
        L_0x0020:
            int r9 = r10 + -1
            r9 = r9 & r4
            r11 = r9
            j$.util.concurrent.m r9 = o(r8, r9)
            r12 = r9
            if (r9 != 0) goto L_0x0069
            j$.util.concurrent.n r9 = new j$.util.concurrent.n
            r9.<init>()
            monitor-enter(r9)
            boolean r13 = b(r8, r11, r0, r9)     // Catch:{ all -> 0x0063 }
            if (r13 == 0) goto L_0x005c
            r7 = 1
            r13 = 0
            java.lang.Object r14 = r3.a(r2, r0)     // Catch:{ all -> 0x004e }
            r5 = r14
            if (r14 == 0) goto L_0x0047
            r6 = 1
            j$.util.concurrent.m r14 = new j$.util.concurrent.m     // Catch:{ all -> 0x004e }
            r14.<init>(r4, r2, r5, r0)     // Catch:{ all -> 0x004e }
            r13 = r14
        L_0x0047:
            l(r8, r11, r13)     // Catch:{ all -> 0x004b }
            goto L_0x005c
        L_0x004b:
            r0 = move-exception
            r13 = r1
            goto L_0x0065
        L_0x004e:
            r0 = move-exception
            l(r8, r11, r13)     // Catch:{ all -> 0x0063 }
            throw r0     // Catch:{ all -> 0x0054 }
        L_0x0054:
            r0 = move-exception
            r13 = r18
            r3 = r20
            r2 = r19
            goto L_0x0065
        L_0x005c:
            monitor-exit(r9)     // Catch:{ all -> 0x0063 }
            if (r7 == 0) goto L_0x0061
            goto L_0x0115
        L_0x0061:
            goto L_0x0124
        L_0x0063:
            r0 = move-exception
            r13 = r1
        L_0x0065:
            monitor-exit(r9)     // Catch:{ all -> 0x0067 }
            throw r0
        L_0x0067:
            r0 = move-exception
            goto L_0x0065
        L_0x0069:
            int r9 = r12.a
            r13 = r9
            r14 = -1
            if (r9 != r14) goto L_0x0075
            j$.util.concurrent.m[] r8 = r1.f(r8, r12)
            goto L_0x0124
        L_0x0075:
            monitor-enter(r12)
            j$.util.concurrent.m r9 = o(r8, r11)     // Catch:{ all -> 0x011c }
            if (r9 != r12) goto L_0x010b
            if (r13 < 0) goto L_0x00cc
            r7 = 1
            r9 = r12
            r14 = 0
        L_0x0081:
            int r15 = r9.a     // Catch:{ all -> 0x011c }
            if (r15 != r4) goto L_0x00b0
            java.lang.Object r15 = r9.b     // Catch:{ all -> 0x011c }
            r16 = r15
            if (r15 == r2) goto L_0x0096
            r15 = r16
            if (r15 == 0) goto L_0x00b0
            boolean r16 = r2.equals(r15)     // Catch:{ all -> 0x011c }
            if (r16 == 0) goto L_0x00b0
            goto L_0x0098
        L_0x0096:
            r15 = r16
        L_0x0098:
            java.lang.Object r0 = r9.c     // Catch:{ all -> 0x011c }
            java.lang.Object r0 = r3.a(r2, r0)     // Catch:{ all -> 0x011c }
            r5 = r0
            if (r5 == 0) goto L_0x00a4
            r9.c = r5     // Catch:{ all -> 0x011c }
            goto L_0x00c7
        L_0x00a4:
            r6 = -1
            j$.util.concurrent.m r0 = r9.d     // Catch:{ all -> 0x011c }
            if (r14 == 0) goto L_0x00ac
            r14.d = r0     // Catch:{ all -> 0x011c }
            goto L_0x00af
        L_0x00ac:
            l(r8, r11, r0)     // Catch:{ all -> 0x011c }
        L_0x00af:
            goto L_0x00c7
        L_0x00b0:
            r14 = r9
            j$.util.concurrent.m r0 = r9.d     // Catch:{ all -> 0x011c }
            r9 = r0
            if (r0 != 0) goto L_0x00c8
            r0 = 0
            java.lang.Object r15 = r3.a(r2, r0)     // Catch:{ all -> 0x011c }
            r5 = r15
            if (r5 == 0) goto L_0x00c7
            r6 = 1
            j$.util.concurrent.m r0 = new j$.util.concurrent.m     // Catch:{ all -> 0x011c }
            r15 = 0
            r0.<init>(r4, r2, r5, r15)     // Catch:{ all -> 0x011c }
            r14.d = r0     // Catch:{ all -> 0x011c }
        L_0x00c7:
            goto L_0x010b
        L_0x00c8:
            int r7 = r7 + 1
            r0 = 0
            goto L_0x0081
        L_0x00cc:
            boolean r0 = r12 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x011c }
            if (r0 == 0) goto L_0x010b
            r7 = 1
            r0 = r12
            j$.util.concurrent.r r0 = (j$.util.concurrent.r) r0     // Catch:{ all -> 0x011c }
            j$.util.concurrent.s r9 = r0.e     // Catch:{ all -> 0x011c }
            r14 = r9
            if (r9 == 0) goto L_0x00e0
            r9 = 0
            j$.util.concurrent.s r15 = r14.b(r4, r2, r9)     // Catch:{ all -> 0x011c }
            r9 = r15
            goto L_0x00e1
        L_0x00e0:
            r9 = 0
        L_0x00e1:
            if (r9 != 0) goto L_0x00e5
            r15 = 0
            goto L_0x00e7
        L_0x00e5:
            java.lang.Object r15 = r9.c     // Catch:{ all -> 0x011c }
        L_0x00e7:
            java.lang.Object r17 = r3.a(r2, r15)     // Catch:{ all -> 0x011c }
            r5 = r17
            if (r5 == 0) goto L_0x00f9
            if (r9 == 0) goto L_0x00f4
            r9.c = r5     // Catch:{ all -> 0x011c }
            goto L_0x010b
        L_0x00f4:
            r6 = 1
            r0.f(r4, r2, r5)     // Catch:{ all -> 0x011c }
            goto L_0x010b
        L_0x00f9:
            if (r9 == 0) goto L_0x010b
            r6 = -1
            boolean r17 = r0.g(r9)     // Catch:{ all -> 0x011c }
            if (r17 == 0) goto L_0x010b
            j$.util.concurrent.s r2 = r0.f     // Catch:{ all -> 0x011c }
            j$.util.concurrent.m r2 = t(r2)     // Catch:{ all -> 0x011c }
            l(r8, r11, r2)     // Catch:{ all -> 0x011c }
        L_0x010b:
            monitor-exit(r12)     // Catch:{ all -> 0x011c }
            if (r7 == 0) goto L_0x0124
            r0 = 8
            if (r7 < r0) goto L_0x0115
            r1.r(r8, r11)
        L_0x0115:
            if (r6 == 0) goto L_0x011b
            long r8 = (long) r6
            r1.a(r8, r7)
        L_0x011b:
            return r5
        L_0x011c:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x011c }
            throw r0
        L_0x011f:
            j$.util.concurrent.m[] r0 = r18.g()
            r8 = r0
        L_0x0124:
            r2 = r19
            r0 = 0
            goto L_0x0018
        L_0x0129:
            r0 = 0
            goto L_0x012c
        L_0x012b:
            throw r0
        L_0x012c:
            goto L_0x012b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.compute(java.lang.Object, j$.util.function.BiFunction):java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0070, code lost:
        r16 = r0;
        r6 = r4.a(r10.c, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0079, code lost:
        if (r6 == null) goto L_0x007e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x007b, code lost:
        r10.c = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007e, code lost:
        r7 = -1;
        r0 = r10.d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0081, code lost:
        if (r15 == null) goto L_0x0086;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0083, code lost:
        r15.d = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0086, code lost:
        l(r9, r12, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0090, code lost:
        r6 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
        r15.d = new j$.util.concurrent.m(r5, r2, r6, (j$.util.concurrent.m) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x009d, code lost:
        r7 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00a1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00a6, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x00ff, code lost:
        if (r8 == 0) goto L_0x0120;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0103, code lost:
        if (r8 < 8) goto L_0x0108;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0105, code lost:
        r(r9, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0108, code lost:
        r0 = r7;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object merge(java.lang.Object r18, java.lang.Object r19, j$.util.function.BiFunction r20) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = r19
            r4 = r20
            r0 = 0
            if (r2 == 0) goto L_0x0125
            if (r3 == 0) goto L_0x0125
            if (r4 == 0) goto L_0x0125
            int r5 = r18.hashCode()
            int r5 = m(r5)
            r6 = 0
            r7 = 0
            r8 = 0
            j$.util.concurrent.m[] r9 = r1.a
        L_0x001c:
            if (r9 == 0) goto L_0x0117
            int r10 = r9.length
            r11 = r10
            if (r10 != 0) goto L_0x0026
            r16 = r6
            goto L_0x0119
        L_0x0026:
            int r10 = r11 + -1
            r10 = r10 & r5
            r12 = r10
            j$.util.concurrent.m r10 = o(r9, r10)
            r13 = r10
            if (r10 != 0) goto L_0x0041
            j$.util.concurrent.m r10 = new j$.util.concurrent.m
            r10.<init>(r5, r2, r3, r0)
            boolean r10 = b(r9, r12, r0, r10)
            if (r10 == 0) goto L_0x0120
            r0 = 1
            r6 = r19
            goto L_0x0109
        L_0x0041:
            int r10 = r13.a
            r14 = r10
            r15 = -1
            if (r10 != r15) goto L_0x004d
            j$.util.concurrent.m[] r9 = r1.f(r9, r13)
            goto L_0x0120
        L_0x004d:
            monitor-enter(r13)
            j$.util.concurrent.m r10 = o(r9, r12)     // Catch:{ all -> 0x0112 }
            if (r10 != r13) goto L_0x00fa
            if (r14 < 0) goto L_0x00af
            r8 = 1
            r10 = r13
            r15 = 0
        L_0x0059:
            int r0 = r10.a     // Catch:{ all -> 0x0110 }
            if (r0 != r5) goto L_0x008a
            java.lang.Object r0 = r10.b     // Catch:{ all -> 0x0110 }
            r16 = r0
            if (r0 == r2) goto L_0x006e
            r0 = r16
            if (r0 == 0) goto L_0x008a
            boolean r16 = r2.equals(r0)     // Catch:{ all -> 0x0110 }
            if (r16 == 0) goto L_0x008a
            goto L_0x0070
        L_0x006e:
            r0 = r16
        L_0x0070:
            r16 = r0
            java.lang.Object r0 = r10.c     // Catch:{ all -> 0x0110 }
            java.lang.Object r0 = r4.a(r0, r3)     // Catch:{ all -> 0x0110 }
            r6 = r0
            if (r6 == 0) goto L_0x007e
            r10.c = r6     // Catch:{ all -> 0x0110 }
            goto L_0x009f
        L_0x007e:
            r7 = -1
            j$.util.concurrent.m r0 = r10.d     // Catch:{ all -> 0x0110 }
            if (r15 == 0) goto L_0x0086
            r15.d = r0     // Catch:{ all -> 0x0110 }
            goto L_0x0089
        L_0x0086:
            l(r9, r12, r0)     // Catch:{ all -> 0x0110 }
        L_0x0089:
            goto L_0x009f
        L_0x008a:
            r15 = r10
            j$.util.concurrent.m r0 = r10.d     // Catch:{ all -> 0x0110 }
            r10 = r0
            if (r0 != 0) goto L_0x00ab
            r7 = 1
            r6 = r19
            j$.util.concurrent.m r0 = new j$.util.concurrent.m     // Catch:{ all -> 0x00a6 }
            r16 = r7
            r7 = 0
            r0.<init>(r5, r2, r6, r7)     // Catch:{ all -> 0x00a1 }
            r15.d = r0     // Catch:{ all -> 0x00a1 }
            r7 = r16
        L_0x009f:
            goto L_0x00fe
        L_0x00a1:
            r0 = move-exception
            r7 = r16
            goto L_0x0115
        L_0x00a6:
            r0 = move-exception
            r16 = r7
            goto L_0x0115
        L_0x00ab:
            int r8 = r8 + 1
            r0 = 0
            goto L_0x0059
        L_0x00af:
            boolean r0 = r13 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x0112 }
            if (r0 == 0) goto L_0x00f7
            r8 = 2
            r0 = r13
            j$.util.concurrent.r r0 = (j$.util.concurrent.r) r0     // Catch:{ all -> 0x0112 }
            j$.util.concurrent.s r10 = r0.e     // Catch:{ all -> 0x0112 }
            if (r10 != 0) goto L_0x00be
            r16 = 0
            goto L_0x00c3
        L_0x00be:
            r15 = 0
            j$.util.concurrent.s r16 = r10.b(r5, r2, r15)     // Catch:{ all -> 0x0112 }
        L_0x00c3:
            r15 = r16
            if (r15 != 0) goto L_0x00cb
            r16 = r6
            r6 = r3
            goto L_0x00d3
        L_0x00cb:
            r16 = r6
            java.lang.Object r6 = r15.c     // Catch:{ all -> 0x00f3 }
            java.lang.Object r6 = r4.a(r6, r3)     // Catch:{ all -> 0x00f3 }
        L_0x00d3:
            if (r6 == 0) goto L_0x00e0
            if (r15 == 0) goto L_0x00db
            r15.c = r6     // Catch:{ all -> 0x0110 }
            goto L_0x00fe
        L_0x00db:
            r7 = 1
            r0.f(r5, r2, r6)     // Catch:{ all -> 0x0110 }
            goto L_0x00fe
        L_0x00e0:
            if (r15 == 0) goto L_0x00fe
            r7 = -1
            boolean r16 = r0.g(r15)     // Catch:{ all -> 0x0110 }
            if (r16 == 0) goto L_0x00fe
            j$.util.concurrent.s r2 = r0.f     // Catch:{ all -> 0x0110 }
            j$.util.concurrent.m r2 = t(r2)     // Catch:{ all -> 0x0110 }
            l(r9, r12, r2)     // Catch:{ all -> 0x0110 }
            goto L_0x00fe
        L_0x00f3:
            r0 = move-exception
            r6 = r16
            goto L_0x0115
        L_0x00f7:
            r16 = r6
            goto L_0x00fc
        L_0x00fa:
            r16 = r6
        L_0x00fc:
            r6 = r16
        L_0x00fe:
            monitor-exit(r13)     // Catch:{ all -> 0x0110 }
            if (r8 == 0) goto L_0x0120
            r0 = 8
            if (r8 < r0) goto L_0x0108
            r1.r(r9, r12)
        L_0x0108:
            r0 = r7
        L_0x0109:
            if (r0 == 0) goto L_0x010f
            long r9 = (long) r0
            r1.a(r9, r8)
        L_0x010f:
            return r6
        L_0x0110:
            r0 = move-exception
            goto L_0x0115
        L_0x0112:
            r0 = move-exception
            r16 = r6
        L_0x0115:
            monitor-exit(r13)     // Catch:{ all -> 0x0110 }
            throw r0
        L_0x0117:
            r16 = r6
        L_0x0119:
            j$.util.concurrent.m[] r0 = r17.g()
            r9 = r0
            r6 = r16
        L_0x0120:
            r2 = r18
            r0 = 0
            goto L_0x001c
        L_0x0125:
            r0 = 0
            goto L_0x0128
        L_0x0127:
            throw r0
        L_0x0128:
            goto L_0x0127
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.merge(java.lang.Object, java.lang.Object, j$.util.function.BiFunction):java.lang.Object");
    }

    public long h() {
        long n2 = n();
        if (n2 < 0) {
            return 0;
        }
        return n2;
    }

    static final int k(int n2) {
        return Integer.numberOfLeadingZeros(n2) | 32768;
    }

    private final m[] g() {
        ConcurrentHashMap.Node<K, V>[] tab;
        while (true) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.a;
            tab = nodeArr;
            if (nodeArr != null && tab.length != 0) {
                break;
            }
            int i2 = this.sizeCtl;
            int sc = i2;
            if (i2 < 0) {
                Thread.yield();
            } else {
                if (k.compareAndSwapInt(this, l, sc, -1)) {
                    try {
                        ConcurrentHashMap.Node<K, V>[] nodeArr2 = this.a;
                        tab = nodeArr2;
                        if (nodeArr2 == null || tab.length == 0) {
                            int n2 = sc > 0 ? sc : 16;
                            ConcurrentHashMap.Node<K, V>[] nt = new m[n2];
                            tab = nt;
                            this.a = nt;
                            sc = n2 - (n2 >>> 2);
                        }
                    } finally {
                        this.sizeCtl = sc;
                    }
                }
            }
        }
        return tab;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void a(long r24, int r26) {
        /*
            r23 = this;
            r8 = r23
            r9 = r24
            r11 = r26
            j$.util.concurrent.d[] r0 = r8.c
            r12 = r0
            if (r0 != 0) goto L_0x0020
            sun.misc.Unsafe r0 = k
            long r2 = n
            long r4 = r8.baseCount
            r13 = r4
            long r6 = r13 + r9
            r15 = r6
            r1 = r23
            boolean r0 = r0.compareAndSwapLong(r1, r2, r4, r6)
            if (r0 != 0) goto L_0x001e
            goto L_0x0020
        L_0x001e:
            r0 = r15
            goto L_0x0051
        L_0x0020:
            r0 = 1
            if (r12 == 0) goto L_0x00d1
            int r1 = r12.length
            r2 = 1
            int r1 = r1 - r2
            r3 = r1
            if (r1 < 0) goto L_0x00d1
            int r1 = j$.util.concurrent.F.g()
            r1 = r1 & r3
            r1 = r12[r1]
            r4 = r1
            if (r1 == 0) goto L_0x00d1
            sun.misc.Unsafe r13 = k
            long r15 = p
            long r5 = r4.value
            r21 = r5
            long r19 = r21 + r9
            r14 = r4
            r17 = r5
            boolean r1 = r13.compareAndSwapLong(r14, r15, r17, r19)
            r0 = r1
            if (r1 != 0) goto L_0x0049
            goto L_0x00d1
        L_0x0049:
            if (r11 > r2) goto L_0x004c
            return
        L_0x004c:
            long r1 = r23.n()
            r0 = r1
        L_0x0051:
            if (r11 < 0) goto L_0x00d0
            r6 = r0
        L_0x0054:
            int r0 = r8.sizeCtl
            r13 = r0
            long r0 = (long) r0
            int r2 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r2 < 0) goto L_0x00cc
            j$.util.concurrent.m[] r0 = r8.a
            r14 = r0
            if (r0 == 0) goto L_0x00c9
            int r0 = r14.length
            r15 = r0
            r1 = 1073741824(0x40000000, float:2.0)
            if (r0 >= r1) goto L_0x00c6
            int r5 = k(r15)
            if (r13 >= 0) goto L_0x00a9
            int r0 = r13 >>> 16
            if (r0 != r5) goto L_0x00a5
            int r0 = r5 + 1
            if (r13 == r0) goto L_0x00a5
            r0 = 65535(0xffff, float:9.1834E-41)
            int r0 = r0 + r5
            if (r13 == r0) goto L_0x00a5
            j$.util.concurrent.m[] r0 = r8.b
            r4 = r0
            if (r0 == 0) goto L_0x00a0
            int r0 = r8.transferIndex
            if (r0 > 0) goto L_0x0087
            r17 = r6
            goto L_0x00ce
        L_0x0087:
            sun.misc.Unsafe r0 = k
            long r2 = l
            int r16 = r13 + 1
            r1 = r23
            r17 = r6
            r6 = r4
            r4 = r13
            r7 = r5
            r5 = r16
            boolean r0 = r0.compareAndSwapInt(r1, r2, r4, r5)
            if (r0 == 0) goto L_0x00c1
            r8.q(r14, r6)
            goto L_0x00c1
        L_0x00a0:
            r17 = r6
            r6 = r4
            r7 = r5
            goto L_0x00ce
        L_0x00a5:
            r17 = r6
            r7 = r5
            goto L_0x00ce
        L_0x00a9:
            r17 = r6
            r7 = r5
            sun.misc.Unsafe r0 = k
            long r2 = l
            int r1 = r7 << 16
            int r5 = r1 + 2
            r1 = r23
            r4 = r13
            boolean r0 = r0.compareAndSwapInt(r1, r2, r4, r5)
            if (r0 == 0) goto L_0x00c1
            r0 = 0
            r8.q(r14, r0)
        L_0x00c1:
            long r6 = r23.n()
            goto L_0x0054
        L_0x00c6:
            r17 = r6
            goto L_0x00ce
        L_0x00c9:
            r17 = r6
            goto L_0x00ce
        L_0x00cc:
            r17 = r6
        L_0x00ce:
            r0 = r17
        L_0x00d0:
            return
        L_0x00d1:
            r8.e(r9, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.a(long, int):void");
    }

    /* access modifiers changed from: package-private */
    public final m[] f(m[] tab, m f2) {
        if (tab != null && (f2 instanceof h)) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = ((h) f2).e;
            ConcurrentHashMap.Node<K, V>[] nextTab = nodeArr;
            if (nodeArr != null) {
                int rs = k(tab.length);
                while (true) {
                    if (nextTab != this.b || this.a != tab) {
                        break;
                    }
                    int i2 = this.sizeCtl;
                    int sc = i2;
                    if (i2 >= 0 || (sc >>> 16) != rs || sc == rs + 1 || sc == 65535 + rs || this.transferIndex <= 0) {
                        break;
                    }
                    if (k.compareAndSwapInt(this, l, sc, sc + 1)) {
                        q(tab, nextTab);
                        break;
                    }
                }
                return nextTab;
            }
        }
        return this.a;
    }

    private final void s(int size) {
        int c2;
        if (size >= NUM) {
            c2 = NUM;
        } else {
            c2 = p((size >>> 1) + size + 1);
        }
        while (true) {
            int i2 = this.sizeCtl;
            int sc = i2;
            if (i2 >= 0) {
                ConcurrentHashMap.Node<K, V>[] tab = this.a;
                if (tab != null) {
                    int length = tab.length;
                    int n2 = length;
                    if (length != 0) {
                        if (c2 > sc && n2 < NUM) {
                            if (tab == this.a) {
                                int rs = k(n2);
                                if (sc >= 0) {
                                    if (k.compareAndSwapInt(this, l, sc, (rs << 16) + 2)) {
                                        q(tab, (m[]) null);
                                    }
                                } else if ((sc >>> 16) == rs && sc != rs + 1 && sc != 65535 + rs) {
                                    m[] mVarArr = this.b;
                                    m[] mVarArr2 = mVarArr;
                                    if (mVarArr != null && this.transferIndex > 0) {
                                        if (k.compareAndSwapInt(this, l, sc, sc + 1)) {
                                            q(tab, mVarArr2);
                                        }
                                    } else {
                                        return;
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
                    }
                }
                int n3 = sc > c2 ? sc : c2;
                if (k.compareAndSwapInt(this, l, sc, -1)) {
                    try {
                        if (this.a == tab) {
                            this.a = new m[n3];
                            sc = n3 - (n3 >>> 2);
                        }
                    } finally {
                        this.sizeCtl = sc;
                    }
                }
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0205, code lost:
        r7 = r31;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void q(j$.util.concurrent.m[] r32, j$.util.concurrent.m[] r33) {
        /*
            r31 = this;
            r7 = r31
            r8 = r32
            int r9 = r8.length
            int r0 = j
            r1 = 1
            if (r0 <= r1) goto L_0x000e
            int r1 = r9 >>> 3
            int r1 = r1 / r0
            goto L_0x000f
        L_0x000e:
            r1 = r9
        L_0x000f:
            r0 = r1
            r10 = 16
            if (r1 >= r10) goto L_0x0018
            r0 = 16
            r11 = r0
            goto L_0x0019
        L_0x0018:
            r11 = r0
        L_0x0019:
            if (r33 != 0) goto L_0x0030
            int r0 = r9 << 1
            j$.util.concurrent.m[] r0 = new j$.util.concurrent.m[r0]     // Catch:{ all -> 0x0027 }
            r7.b = r0
            r7.transferIndex = r9
            r12 = r0
            goto L_0x0032
        L_0x0027:
            r0 = move-exception
            r1 = r0
            r0 = r1
            r1 = 2147483647(0x7fffffff, float:NaN)
            r7.sizeCtl = r1
            return
        L_0x0030:
            r12 = r33
        L_0x0032:
            int r13 = r12.length
            j$.util.concurrent.h r0 = new j$.util.concurrent.h
            r0.<init>(r12)
            r14 = r0
            r0 = 1
            r1 = 0
            r2 = 0
            r3 = 0
            r15 = r0
            r16 = r1
            r6 = r2
            r5 = r3
        L_0x0042:
            if (r15 == 0) goto L_0x008a
            int r0 = r6 + -1
            if (r0 >= r5) goto L_0x0082
            if (r16 == 0) goto L_0x004d
            r19 = r5
            goto L_0x0084
        L_0x004d:
            int r1 = r7.transferIndex
            r6 = r1
            if (r1 > 0) goto L_0x0057
            r0 = -1
            r1 = 0
            r6 = r0
            r15 = r1
            goto L_0x0089
        L_0x0057:
            sun.misc.Unsafe r1 = k
            long r3 = m
            if (r6 <= r11) goto L_0x0062
            int r2 = r6 - r11
            r17 = r2
            goto L_0x0065
        L_0x0062:
            r2 = 0
            r17 = 0
        L_0x0065:
            r18 = r17
            r2 = r31
            r19 = r5
            r5 = r6
            r20 = r6
            r6 = r17
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x007e
            r1 = r18
            int r6 = r20 + -1
            r0 = 0
            r15 = r0
            r5 = r1
            goto L_0x0089
        L_0x007e:
            r6 = r0
            r5 = r19
            goto L_0x0089
        L_0x0082:
            r19 = r5
        L_0x0084:
            r1 = 0
            r6 = r0
            r15 = r1
            r5 = r19
        L_0x0089:
            goto L_0x0042
        L_0x008a:
            r19 = r5
            r0 = 0
            if (r6 < 0) goto L_0x0217
            if (r6 >= r9) goto L_0x0217
            int r1 = r6 + r9
            if (r1 < r13) goto L_0x009d
            r21 = r11
            r20 = r13
            r29 = r15
            goto L_0x021d
        L_0x009d:
            j$.util.concurrent.m r1 = o(r8, r6)
            r2 = r1
            if (r1 != 0) goto L_0x00b1
            boolean r0 = b(r8, r6, r0, r14)
            r15 = r0
            r21 = r11
            r20 = r13
            r3 = 16
            goto L_0x0259
        L_0x00b1:
            int r0 = r2.a
            r1 = r0
            r3 = -1
            if (r0 != r3) goto L_0x00c1
            r0 = 1
            r15 = r0
            r21 = r11
            r20 = r13
            r3 = 16
            goto L_0x0259
        L_0x00c1:
            monitor-enter(r2)
            j$.util.concurrent.m r0 = o(r8, r6)     // Catch:{ all -> 0x020c }
            if (r0 != r2) goto L_0x01fa
            if (r1 < 0) goto L_0x014c
            r0 = r1 & r9
            r3 = r2
            j$.util.concurrent.m r4 = r2.d     // Catch:{ all -> 0x0143 }
        L_0x00cf:
            if (r4 == 0) goto L_0x00e5
            int r5 = r4.a     // Catch:{ all -> 0x00dc }
            r5 = r5 & r9
            if (r5 == r0) goto L_0x00d8
            r0 = r5
            r3 = r4
        L_0x00d8:
            j$.util.concurrent.m r5 = r4.d     // Catch:{ all -> 0x00dc }
            r4 = r5
            goto L_0x00cf
        L_0x00dc:
            r0 = move-exception
            r33 = r1
            r21 = r11
            r20 = r13
            goto L_0x0215
        L_0x00e5:
            if (r0 != 0) goto L_0x00ea
            r4 = r3
            r5 = 0
            goto L_0x00ec
        L_0x00ea:
            r5 = r3
            r4 = 0
        L_0x00ec:
            r17 = r2
            r10 = r17
        L_0x00f0:
            if (r10 == r3) goto L_0x0126
            r18 = r0
            int r0 = r10.a     // Catch:{ all -> 0x0143 }
            r33 = r1
            java.lang.Object r1 = r10.b     // Catch:{ all -> 0x011f }
            r20 = r3
            java.lang.Object r3 = r10.c     // Catch:{ all -> 0x011f }
            r21 = r0 & r9
            if (r21 != 0) goto L_0x010b
            r21 = r11
            j$.util.concurrent.m r11 = new j$.util.concurrent.m     // Catch:{ all -> 0x013e }
            r11.<init>(r0, r1, r3, r4)     // Catch:{ all -> 0x013e }
            r4 = r11
            goto L_0x0113
        L_0x010b:
            r21 = r11
            j$.util.concurrent.m r11 = new j$.util.concurrent.m     // Catch:{ all -> 0x013e }
            r11.<init>(r0, r1, r3, r5)     // Catch:{ all -> 0x013e }
            r5 = r11
        L_0x0113:
            j$.util.concurrent.m r0 = r10.d     // Catch:{ all -> 0x013e }
            r10 = r0
            r1 = r33
            r0 = r18
            r3 = r20
            r11 = r21
            goto L_0x00f0
        L_0x011f:
            r0 = move-exception
            r21 = r11
            r20 = r13
            goto L_0x0215
        L_0x0126:
            r18 = r0
            r33 = r1
            r20 = r3
            r21 = r11
            l(r12, r6, r4)     // Catch:{ all -> 0x013e }
            int r0 = r6 + r9
            l(r12, r0, r5)     // Catch:{ all -> 0x013e }
            l(r8, r6, r14)     // Catch:{ all -> 0x013e }
            r15 = 1
            r20 = r13
            goto L_0x0204
        L_0x013e:
            r0 = move-exception
            r20 = r13
            goto L_0x0215
        L_0x0143:
            r0 = move-exception
            r33 = r1
            r21 = r11
            r20 = r13
            goto L_0x0215
        L_0x014c:
            r33 = r1
            r21 = r11
            boolean r0 = r2 instanceof j$.util.concurrent.r     // Catch:{ all -> 0x01f4 }
            if (r0 == 0) goto L_0x01ef
            r0 = r2
            j$.util.concurrent.r r0 = (j$.util.concurrent.r) r0     // Catch:{ all -> 0x01f4 }
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r10 = 0
            r11 = 0
            r18 = r1
            j$.util.concurrent.s r1 = r0.f     // Catch:{ all -> 0x01f4 }
            r30 = r18
            r18 = r0
            r0 = r11
            r11 = r10
            r10 = r5
            r5 = r4
            r4 = r3
            r3 = r30
        L_0x016c:
            if (r1 == 0) goto L_0x01b5
            r20 = r13
            int r13 = r1.a     // Catch:{ all -> 0x01b0 }
            j$.util.concurrent.s r28 = new j$.util.concurrent.s     // Catch:{ all -> 0x01b0 }
            r29 = r15
            java.lang.Object r15 = r1.b     // Catch:{ all -> 0x01c1 }
            java.lang.Object r7 = r1.c     // Catch:{ all -> 0x01c1 }
            r26 = 0
            r27 = 0
            r22 = r28
            r23 = r13
            r24 = r15
            r25 = r7
            r22.<init>(r23, r24, r25, r26, r27)     // Catch:{ all -> 0x01c1 }
            r7 = r28
            r15 = r13 & r9
            if (r15 != 0) goto L_0x019b
            r7.h = r4     // Catch:{ all -> 0x01c1 }
            if (r4 != 0) goto L_0x0195
            r3 = r7
            goto L_0x0197
        L_0x0195:
            r4.d = r7     // Catch:{ all -> 0x01c1 }
        L_0x0197:
            r4 = r7
            int r11 = r11 + 1
            goto L_0x01a6
        L_0x019b:
            r7.h = r10     // Catch:{ all -> 0x01c1 }
            if (r10 != 0) goto L_0x01a1
            r5 = r7
            goto L_0x01a3
        L_0x01a1:
            r10.d = r7     // Catch:{ all -> 0x01c1 }
        L_0x01a3:
            r10 = r7
            int r0 = r0 + 1
        L_0x01a6:
            j$.util.concurrent.m r7 = r1.d     // Catch:{ all -> 0x01c1 }
            r1 = r7
            r7 = r31
            r13 = r20
            r15 = r29
            goto L_0x016c
        L_0x01b0:
            r0 = move-exception
            r29 = r15
            goto L_0x0215
        L_0x01b5:
            r20 = r13
            r29 = r15
            r1 = 6
            if (r11 > r1) goto L_0x01c5
            j$.util.concurrent.m r7 = t(r3)     // Catch:{ all -> 0x01c1 }
            goto L_0x01cf
        L_0x01c1:
            r0 = move-exception
            r15 = r29
            goto L_0x0215
        L_0x01c5:
            if (r0 == 0) goto L_0x01cd
            j$.util.concurrent.r r7 = new j$.util.concurrent.r     // Catch:{ all -> 0x01c1 }
            r7.<init>(r3)     // Catch:{ all -> 0x01c1 }
            goto L_0x01cf
        L_0x01cd:
            r7 = r18
        L_0x01cf:
            if (r0 > r1) goto L_0x01d7
            j$.util.concurrent.m r1 = t(r5)     // Catch:{ all -> 0x01c1 }
            goto L_0x01e1
        L_0x01d7:
            if (r11 == 0) goto L_0x01df
            j$.util.concurrent.r r1 = new j$.util.concurrent.r     // Catch:{ all -> 0x01c1 }
            r1.<init>(r5)     // Catch:{ all -> 0x01c1 }
            goto L_0x01e1
        L_0x01df:
            r1 = r18
        L_0x01e1:
            l(r12, r6, r7)     // Catch:{ all -> 0x01c1 }
            int r13 = r6 + r9
            l(r12, r13, r1)     // Catch:{ all -> 0x01c1 }
            l(r8, r6, r14)     // Catch:{ all -> 0x01c1 }
            r15 = 1
            goto L_0x0204
        L_0x01ef:
            r20 = r13
            r29 = r15
            goto L_0x0202
        L_0x01f4:
            r0 = move-exception
            r20 = r13
            r29 = r15
            goto L_0x0215
        L_0x01fa:
            r33 = r1
            r21 = r11
            r20 = r13
            r29 = r15
        L_0x0202:
            r15 = r29
        L_0x0204:
            monitor-exit(r2)     // Catch:{ all -> 0x020a }
            r3 = 16
            r7 = r31
            goto L_0x0259
        L_0x020a:
            r0 = move-exception
            goto L_0x0215
        L_0x020c:
            r0 = move-exception
            r33 = r1
            r21 = r11
            r20 = r13
            r29 = r15
        L_0x0215:
            monitor-exit(r2)     // Catch:{ all -> 0x020a }
            throw r0
        L_0x0217:
            r21 = r11
            r20 = r13
            r29 = r15
        L_0x021d:
            if (r16 == 0) goto L_0x022d
            r7 = r31
            r7.b = r0
            r7.a = r12
            int r0 = r9 << 1
            int r1 = r9 >>> 1
            int r0 = r0 - r1
            r7.sizeCtl = r0
            return
        L_0x022d:
            r7 = r31
            sun.misc.Unsafe r1 = k
            long r3 = l
            int r5 = r7.sizeCtl
            r0 = r5
            int r10 = r0 + -1
            r2 = r31
            r11 = r6
            r6 = r10
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x0253
            int r1 = r0 + -2
            int r2 = k(r9)
            r3 = 16
            int r2 = r2 << r3
            if (r1 == r2) goto L_0x024e
            return
        L_0x024e:
            r15 = 1
            r16 = 1
            r6 = r9
            goto L_0x0258
        L_0x0253:
            r3 = 16
            r6 = r11
            r15 = r29
        L_0x0258:
        L_0x0259:
            r5 = r19
            r13 = r20
            r11 = r21
            r10 = 16
            goto L_0x0042
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.q(j$.util.concurrent.m[], j$.util.concurrent.m[]):void");
    }

    /* access modifiers changed from: package-private */
    public final long n() {
        CLASSNAMEd[] as = this.c;
        long sum = this.baseCount;
        if (as != null) {
            for (CLASSNAMEd dVar : as) {
                CLASSNAMEd a2 = dVar;
                if (dVar != null) {
                    sum += a2.value;
                }
            }
        }
        return sum;
    }

    /* JADX INFO: finally extract failed */
    private final void e(long x, boolean wasUncontended) {
        boolean wasUncontended2;
        CLASSNAMEd a2;
        long j2 = x;
        int g2 = F.g();
        int h2 = g2;
        if (g2 == 0) {
            F.p();
            h2 = F.g();
            wasUncontended2 = true;
        } else {
            wasUncontended2 = wasUncontended;
        }
        boolean wasUncontended3 = wasUncontended2;
        int h3 = h2;
        boolean collide = false;
        while (true) {
            CLASSNAMEd[] dVarArr = this.c;
            CLASSNAMEd[] as = dVarArr;
            if (dVarArr != null) {
                int length = as.length;
                int n2 = length;
                if (length > 0) {
                    CLASSNAMEd dVar = as[(n2 - 1) & h3];
                    CLASSNAMEd a3 = dVar;
                    if (dVar == null) {
                        if (this.cellsBusy == 0) {
                            CLASSNAMEd r2 = new CLASSNAMEd(j2);
                            if (this.cellsBusy == 0) {
                                a2 = a3;
                                if (k.compareAndSwapInt(this, o, 0, 1)) {
                                    boolean created = false;
                                    try {
                                        CLASSNAMEd[] dVarArr2 = this.c;
                                        CLASSNAMEd[] rs = dVarArr2;
                                        if (dVarArr2 != null) {
                                            int length2 = rs.length;
                                            int m2 = length2;
                                            if (length2 > 0) {
                                                int i2 = (m2 - 1) & h3;
                                                int j3 = i2;
                                                if (rs[i2] == null) {
                                                    rs[j3] = r2;
                                                    created = true;
                                                }
                                            }
                                        }
                                        if (created) {
                                            return;
                                        }
                                    } finally {
                                        this.cellsBusy = 0;
                                    }
                                }
                            } else {
                                a2 = a3;
                            }
                        } else {
                            a2 = a3;
                        }
                        collide = false;
                        CLASSNAMEd dVar2 = a2;
                    } else {
                        CLASSNAMEd a4 = a3;
                        if (!wasUncontended3) {
                            wasUncontended3 = true;
                            CLASSNAMEd dVar3 = a4;
                        } else {
                            Unsafe unsafe = k;
                            long j4 = p;
                            CLASSNAMEd a5 = a4;
                            long v = a5.value;
                            if (!unsafe.compareAndSwapLong(a5, j4, v, v + j2)) {
                                if (this.c != as) {
                                } else if (n2 >= j) {
                                    CLASSNAMEd dVar4 = a5;
                                } else if (!collide) {
                                    collide = true;
                                    CLASSNAMEd dVar5 = a5;
                                } else if (this.cellsBusy == 0) {
                                    CLASSNAMEd dVar6 = a5;
                                    if (k.compareAndSwapInt(this, o, 0, 1)) {
                                        try {
                                            if (this.c == as) {
                                                CLASSNAMEd[] rs2 = new CLASSNAMEd[(n2 << 1)];
                                                for (int i3 = 0; i3 < n2; i3++) {
                                                    rs2[i3] = as[i3];
                                                }
                                                this.c = rs2;
                                            }
                                            this.cellsBusy = 0;
                                            collide = false;
                                        } catch (Throwable th) {
                                            this.cellsBusy = 0;
                                            throw th;
                                        }
                                    }
                                }
                                collide = false;
                            } else {
                                return;
                            }
                        }
                    }
                    h3 = F.a(h3);
                }
            }
            if (this.cellsBusy == 0 && this.c == as) {
                if (k.compareAndSwapInt(this, o, 0, 1)) {
                    boolean init = false;
                    try {
                        if (this.c == as) {
                            CLASSNAMEd[] rs3 = new CLASSNAMEd[2];
                            rs3[h3 & 1] = new CLASSNAMEd(j2);
                            this.c = rs3;
                            init = true;
                        }
                        if (init) {
                            return;
                        }
                    } finally {
                        this.cellsBusy = 0;
                    }
                }
            }
            Unsafe unsafe2 = k;
            long j5 = n;
            long v2 = this.baseCount;
            if (unsafe2.compareAndSwapLong(this, j5, v2, v2 + j2)) {
                return;
            }
        }
    }

    private final void r(m[] tab, int index) {
        if (tab != null) {
            int length = tab.length;
            int n2 = length;
            if (length < 64) {
                s(n2 << 1);
                return;
            }
            ConcurrentHashMap.Node<K, V> o2 = o(tab, index);
            ConcurrentHashMap.Node<K, V> b2 = o2;
            if (o2 != null && b2.a >= 0) {
                synchronized (b2) {
                    if (o(tab, index) == b2) {
                        ConcurrentHashMap.TreeNode<K, V> hd = null;
                        ConcurrentHashMap.TreeNode<K, V> tl = null;
                        for (ConcurrentHashMap.Node<K, V> e2 = b2; e2 != null; e2 = e2.d) {
                            ConcurrentHashMap.TreeNode<K, V> p2 = new s(e2.a, e2.b, e2.c, (m) null, (s) null);
                            p2.h = tl;
                            if (tl == null) {
                                hd = p2;
                            } else {
                                tl.d = p2;
                            }
                            tl = p2;
                        }
                        l(tab, index, new r(hd));
                    }
                }
            }
        }
    }

    static m t(m b2) {
        ConcurrentHashMap.Node<K, V> hd = null;
        ConcurrentHashMap.Node<K, V> tl = null;
        for (m mVar = b2; mVar != null; mVar = mVar.d) {
            ConcurrentHashMap.Node<K, V> p2 = new m(mVar.a, mVar.b, mVar.c, (m) null);
            if (tl == null) {
                hd = p2;
            } else {
                tl.d = p2;
            }
            tl = p2;
        }
        return hd;
    }
}

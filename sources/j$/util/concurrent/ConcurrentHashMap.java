package j$.util.concurrent;

import j$.util.Collection;
import j$.util.Iterator;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.IntFunction;
import j$.util.function.Predicate;
import j$.wrappers.C$r8$wrapper$java$util$function$BiConsumer$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$BiFunction$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$Consumer$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$Function$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$IntFunction$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$Predicate$VWRP;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import sun.misc.Unsafe;

public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable, ConcurrentMap<K, V> {
    private static final long ABASE;
    private static final int ASHIFT;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final int DEFAULT_CAPACITY = 16;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int HASH_BITS = Integer.MAX_VALUE;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int MAXIMUM_CAPACITY = NUM;
    static final int MAX_ARRAY_SIZE = NUM;
    private static final int MAX_RESIZERS = 65535;
    private static final int MIN_TRANSFER_STRIDE = 16;
    static final int MIN_TREEIFY_CAPACITY = 64;
    static final int MOVED = -1;
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    static final int RESERVED = -3;
    private static int RESIZE_STAMP_BITS = 16;
    private static final int RESIZE_STAMP_SHIFT = 16;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    static final int TREEBIN = -2;
    static final int TREEIFY_THRESHOLD = 8;
    private static final Unsafe U;
    static final int UNTREEIFY_THRESHOLD = 6;
    private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("segments", Segment[].class), new ObjectStreamField("segmentMask", Integer.TYPE), new ObjectStreamField("segmentShift", Integer.TYPE)};
    private static final long serialVersionUID = 7249069246763182397L;
    private volatile transient long baseCount;
    private volatile transient int cellsBusy;
    private volatile transient CounterCell[] counterCells;
    private transient EntrySetView<K, V> entrySet;
    private transient KeySetView<K, V> keySet;
    private volatile transient Node<K, V>[] nextTable;
    private volatile transient int sizeCtl;
    volatile transient Node<K, V>[] table;
    private volatile transient int transferIndex;
    private transient ValuesView<K, V> values;

    public /* synthetic */ Object compute(Object obj, BiFunction biFunction) {
        return compute(obj, C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
    }

    public /* synthetic */ Object computeIfAbsent(Object obj, Function function) {
        return computeIfAbsent(obj, C$r8$wrapper$java$util$function$Function$VWRP.convert(function));
    }

    public /* synthetic */ Object computeIfPresent(Object obj, BiFunction biFunction) {
        return computeIfPresent(obj, C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
    }

    public /* synthetic */ void forEach(BiConsumer biConsumer) {
        forEach(C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(biConsumer));
    }

    public /* synthetic */ Object merge(Object obj, Object obj2, BiFunction biFunction) {
        return merge(obj, obj2, C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
    }

    public /* synthetic */ void replaceAll(BiFunction biFunction) {
        replaceAll(C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
    }

    static {
        try {
            Unsafe unsafe = DesugarUnsafe.getUnsafe();
            U = unsafe;
            Class<ConcurrentHashMap> cls = ConcurrentHashMap.class;
            SIZECTL = unsafe.objectFieldOffset(cls.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = unsafe.objectFieldOffset(cls.getDeclaredField("transferIndex"));
            BASECOUNT = unsafe.objectFieldOffset(cls.getDeclaredField("baseCount"));
            CELLSBUSY = unsafe.objectFieldOffset(cls.getDeclaredField("cellsBusy"));
            CELLVALUE = unsafe.objectFieldOffset(CounterCell.class.getDeclaredField("value"));
            Class<Node[]> cls2 = Node[].class;
            ABASE = (long) unsafe.arrayBaseOffset(cls2);
            int scale = unsafe.arrayIndexScale(cls2);
            if (((scale - 1) & scale) == 0) {
                ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
                return;
            }
            throw new Error("data type scale not a power of two");
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        volatile Node<K, V> next;
        volatile V val;

        Node(int hash2, K key2, V val2, Node<K, V> next2) {
            this.hash = hash2;
            this.key = key2;
            this.val = val2;
            this.next = next2;
        }

        public final K getKey() {
            return this.key;
        }

        public final V getValue() {
            return this.val;
        }

        public final int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }

        public final String toString() {
            return this.key + "=" + this.val;
        }

        public final V setValue(V v) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object o) {
            Object obj;
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry) o;
                Map.Entry<?, ?> e = entry;
                Object key2 = entry.getKey();
                Object k = key2;
                if (key2 != null) {
                    Object value = e.getValue();
                    Object v = value;
                    if (value != null && (k == (obj = this.key) || k.equals(obj))) {
                        Object obj2 = this.val;
                        Object u = obj2;
                        if (v == obj2 || v.equals(u)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public Node<K, V> find(int h, Object k) {
            Node<K, V> node;
            Node<K, V> node2 = this;
            if (k == null) {
                return null;
            }
            do {
                if (node2.hash == h) {
                    K k2 = node2.key;
                    K ek = k2;
                    if (k2 == k || (ek != null && k.equals(ek))) {
                        return node2;
                    }
                }
                node = node2.next;
                node2 = node;
            } while (node != null);
            return null;
        }
    }

    static final int spread(int h) {
        return ((h >>> 16) ^ h) & Integer.MAX_VALUE;
    }

    private static final int tableSizeFor(int c) {
        int n = c - 1;
        int n2 = n | (n >>> 1);
        int n3 = n2 | (n2 >>> 2);
        int n4 = n3 | (n3 >>> 4);
        int n5 = n4 | (n4 >>> 8);
        int n6 = n5 | (n5 >>> 16);
        if (n6 < 0) {
            return 1;
        }
        if (n6 >= NUM) {
            return NUM;
        }
        return n6 + 1;
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.Class<?> comparableClassFor(java.lang.Object r8) {
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.comparableClassFor(java.lang.Object):java.lang.Class");
    }

    static int compareComparables(Class<?> kc, Object k, Object x) {
        if (x == null || x.getClass() != kc) {
            return 0;
        }
        return ((Comparable) k).compareTo(x);
    }

    static final <K, V> Node<K, V> tabAt(Node<K, V>[] tab, int i) {
        return (Node) U.getObjectVolatile(tab, (((long) i) << ASHIFT) + ABASE);
    }

    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i, Node<K, V> c, Node<K, V> v) {
        return U.compareAndSwapObject(tab, ABASE + (((long) i) << ASHIFT), c, v);
    }

    static final <K, V> void setTabAt(Node<K, V>[] tab, int i, Node<K, V> v) {
        U.putObjectVolatile(tab, (((long) i) << ASHIFT) + ABASE, v);
    }

    public ConcurrentHashMap() {
    }

    public ConcurrentHashMap(int initialCapacity) {
        int cap;
        if (initialCapacity >= 0) {
            if (initialCapacity >= NUM) {
                cap = NUM;
            } else {
                cap = tableSizeFor((initialCapacity >>> 1) + initialCapacity + 1);
            }
            this.sizeCtl = cap;
            return;
        }
        throw new IllegalArgumentException();
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        this.sizeCtl = 16;
        putAll(m);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        if (loadFactor <= 0.0f || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        double d = (double) (((float) ((long) (initialCapacity < concurrencyLevel ? concurrencyLevel : initialCapacity))) / loadFactor);
        Double.isNaN(d);
        long size = (long) (d + 1.0d);
        this.sizeCtl = size >= NUM ? NUM : tableSizeFor((int) size);
    }

    public int size() {
        long n = sumCount();
        if (n < 0) {
            return 0;
        }
        if (n > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) n;
    }

    public boolean isEmpty() {
        return sumCount() <= 0;
    }

    public V get(Object key) {
        int h = spread(key.hashCode());
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
        ConcurrentHashMap.Node<K, V>[] tab = nodeArr;
        if (nodeArr != null) {
            int length = tab.length;
            int n = length;
            if (length > 0) {
                ConcurrentHashMap.Node<K, V> tabAt = tabAt(tab, (n - 1) & h);
                ConcurrentHashMap.Node<K, V> e = tabAt;
                if (tabAt != null) {
                    int i = e.hash;
                    int eh = i;
                    if (i == h) {
                        K k = e.key;
                        K ek = k;
                        if (k == key || (ek != null && key.equals(ek))) {
                            return e.val;
                        }
                    } else if (eh < 0) {
                        ConcurrentHashMap.Node<K, V> find = e.find(h, key);
                        ConcurrentHashMap.Node<K, V> p = find;
                        if (find != null) {
                            return p.val;
                        }
                        return null;
                    }
                    while (true) {
                        ConcurrentHashMap.Node<K, V> node = e.next;
                        e = node;
                        if (node == null) {
                            break;
                        } else if (e.hash == h) {
                            K k2 = e.key;
                            K ek2 = k2;
                            if (k2 == key || (ek2 != null && key.equals(ek2))) {
                            }
                        }
                    }
                    return e.val;
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
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            if (nodeArr != null) {
                ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, t.length, 0, t.length);
                while (true) {
                    ConcurrentHashMap.Node<K, V> advance = it.advance();
                    ConcurrentHashMap.Node<K, V> p = advance;
                    if (advance == null) {
                        break;
                    }
                    V v = p.val;
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
        throw new NullPointerException();
    }

    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0096, code lost:
        addCount(1, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x009b, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final V putVal(K r13, V r14, boolean r15) {
        /*
            r12 = this;
            if (r13 == 0) goto L_0x00a5
            if (r14 == 0) goto L_0x00a5
            int r0 = r13.hashCode()
            int r0 = spread(r0)
            r1 = 0
            j$.util.concurrent.ConcurrentHashMap$Node<K, V>[] r2 = r12.table
        L_0x000f:
            if (r2 == 0) goto L_0x009f
            int r3 = r2.length
            r4 = r3
            if (r3 != 0) goto L_0x0017
            goto L_0x009f
        L_0x0017:
            int r3 = r4 + -1
            r3 = r3 & r0
            r5 = r3
            j$.util.concurrent.ConcurrentHashMap$Node r3 = tabAt(r2, r3)
            r6 = r3
            r7 = 0
            if (r3 != 0) goto L_0x0030
            j$.util.concurrent.ConcurrentHashMap$Node r3 = new j$.util.concurrent.ConcurrentHashMap$Node
            r3.<init>(r0, r13, r14, r7)
            boolean r3 = casTabAt(r2, r5, r7, r3)
            if (r3 == 0) goto L_0x00a3
            goto L_0x0096
        L_0x0030:
            int r3 = r6.hash
            r8 = r3
            r9 = -1
            if (r3 != r9) goto L_0x003c
            j$.util.concurrent.ConcurrentHashMap$Node[] r2 = r12.helpTransfer(r2, r6)
            goto L_0x00a3
        L_0x003c:
            r3 = 0
            monitor-enter(r6)
            j$.util.concurrent.ConcurrentHashMap$Node r9 = tabAt(r2, r5)     // Catch:{ all -> 0x009c }
            if (r9 != r6) goto L_0x0089
            if (r8 < 0) goto L_0x0073
            r1 = 1
            r9 = r6
        L_0x0048:
            int r10 = r9.hash     // Catch:{ all -> 0x009c }
            if (r10 != r0) goto L_0x0061
            K r10 = r9.key     // Catch:{ all -> 0x009c }
            r11 = r10
            if (r10 == r13) goto L_0x0059
            if (r11 == 0) goto L_0x0061
            boolean r10 = r13.equals(r11)     // Catch:{ all -> 0x009c }
            if (r10 == 0) goto L_0x0061
        L_0x0059:
            V r10 = r9.val     // Catch:{ all -> 0x009c }
            r3 = r10
            if (r15 != 0) goto L_0x006f
            r9.val = r14     // Catch:{ all -> 0x009c }
            goto L_0x006f
        L_0x0061:
            r10 = r9
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r11 = r9.next     // Catch:{ all -> 0x009c }
            r9 = r11
            if (r11 != 0) goto L_0x0070
            j$.util.concurrent.ConcurrentHashMap$Node r11 = new j$.util.concurrent.ConcurrentHashMap$Node     // Catch:{ all -> 0x009c }
            r11.<init>(r0, r13, r14, r7)     // Catch:{ all -> 0x009c }
            r10.next = r11     // Catch:{ all -> 0x009c }
        L_0x006f:
            goto L_0x0089
        L_0x0070:
            int r1 = r1 + 1
            goto L_0x0048
        L_0x0073:
            boolean r9 = r6 instanceof j$.util.concurrent.ConcurrentHashMap.TreeBin     // Catch:{ all -> 0x009c }
            if (r9 == 0) goto L_0x0089
            r1 = 2
            r9 = r6
            j$.util.concurrent.ConcurrentHashMap$TreeBin r9 = (j$.util.concurrent.ConcurrentHashMap.TreeBin) r9     // Catch:{ all -> 0x009c }
            j$.util.concurrent.ConcurrentHashMap$TreeNode r9 = r9.putTreeVal(r0, r13, r14)     // Catch:{ all -> 0x009c }
            r10 = r9
            if (r9 == 0) goto L_0x0089
            V r9 = r10.val     // Catch:{ all -> 0x009c }
            r3 = r9
            if (r15 != 0) goto L_0x0089
            r10.val = r14     // Catch:{ all -> 0x009c }
        L_0x0089:
            monitor-exit(r6)     // Catch:{ all -> 0x009c }
            if (r1 == 0) goto L_0x00a3
            r9 = 8
            if (r1 < r9) goto L_0x0093
            r12.treeifyBin(r2, r5)
        L_0x0093:
            if (r3 == 0) goto L_0x0096
            return r3
        L_0x0096:
            r2 = 1
            r12.addCount(r2, r1)
            return r7
        L_0x009c:
            r7 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x009c }
            throw r7
        L_0x009f:
            j$.util.concurrent.ConcurrentHashMap$Node[] r2 = r12.initTable()
        L_0x00a3:
            goto L_0x000f
        L_0x00a5:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            r0.<init>()
            goto L_0x00ac
        L_0x00ab:
            throw r0
        L_0x00ac:
            goto L_0x00ab
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.putVal(java.lang.Object, java.lang.Object, boolean):java.lang.Object");
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        tryPresize(m.size());
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            putVal(e.getKey(), e.getValue(), false);
        }
    }

    public V remove(Object key) {
        return replaceNode(key, (Object) null, (Object) null);
    }

    /* access modifiers changed from: package-private */
    public final V replaceNode(Object key, V value, Object cv) {
        Object obj = key;
        V v = value;
        Object obj2 = cv;
        int hash = spread(key.hashCode());
        ConcurrentHashMap.Node<K, V>[] tab = this.table;
        while (tab != null) {
            int length = tab.length;
            int n = length;
            if (length == 0) {
                return null;
            }
            int i = (n - 1) & hash;
            int i2 = i;
            ConcurrentHashMap.Node<K, V> tabAt = tabAt(tab, i);
            ConcurrentHashMap.Node<K, V> f = tabAt;
            if (tabAt == null) {
                return null;
            }
            int i3 = f.hash;
            int fh = i3;
            if (i3 == -1) {
                tab = helpTransfer(tab, f);
            } else {
                V oldVal = null;
                boolean validated = false;
                synchronized (f) {
                    if (tabAt(tab, i2) == f) {
                        if (fh >= 0) {
                            validated = true;
                            ConcurrentHashMap.Node<K, V> e = f;
                            ConcurrentHashMap.Node<K, V> pred = null;
                            while (true) {
                                if (e.hash == hash) {
                                    K k = e.key;
                                    K ek = k;
                                    if (k == obj) {
                                        break;
                                    }
                                    K ek2 = ek;
                                    if (ek2 != null && obj.equals(ek2)) {
                                        break;
                                    }
                                }
                                pred = e;
                                ConcurrentHashMap.Node<K, V> node = e.next;
                                e = node;
                                if (node == null) {
                                    break;
                                }
                            }
                            V ev = e.val;
                            if (obj2 == null || obj2 == ev || (ev != null && obj2.equals(ev))) {
                                oldVal = ev;
                                if (v != null) {
                                    e.val = v;
                                } else if (pred != null) {
                                    V v2 = ev;
                                    pred.next = e.next;
                                } else {
                                    setTabAt(tab, i2, e.next);
                                }
                            }
                        } else if (f instanceof TreeBin) {
                            validated = true;
                            ConcurrentHashMap.TreeBin<K, V> t = (TreeBin) f;
                            TreeNode<K, V> treeNode = t.root;
                            TreeNode<K, V> treeNode2 = treeNode;
                            if (treeNode != null) {
                                ConcurrentHashMap.TreeNode<K, V> findTreeNode = treeNode2.findTreeNode(hash, obj, (Class<?>) null);
                                ConcurrentHashMap.TreeNode<K, V> p = findTreeNode;
                                if (findTreeNode != null) {
                                    V pv = p.val;
                                    if (obj2 == null || obj2 == pv || (pv != null && obj2.equals(pv))) {
                                        oldVal = pv;
                                        if (v != null) {
                                            p.val = v;
                                        } else if (t.removeTreeNode(p)) {
                                            setTabAt(tab, i2, untreeify(t.first));
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
                    if (v == null) {
                        addCount(-1, -1);
                    }
                    return oldVal;
                }
            }
            obj = key;
        }
        return null;
    }

    public void clear() {
        Throwable th;
        ConcurrentHashMap.Node<K, V> p;
        long delta = 0;
        int i = 0;
        ConcurrentHashMap.Node<K, V>[] tab = this.table;
        while (tab != null && i < tab.length) {
            ConcurrentHashMap.Node<K, V> f = tabAt(tab, i);
            if (f == null) {
                i++;
            } else {
                int i2 = f.hash;
                int fh = i2;
                if (i2 == -1) {
                    tab = helpTransfer(tab, f);
                    i = 0;
                } else {
                    synchronized (f) {
                        try {
                            if (tabAt(tab, i) == f) {
                                if (fh >= 0) {
                                    p = f;
                                } else {
                                    p = f instanceof TreeBin ? ((TreeBin) f).first : null;
                                }
                                while (p != null) {
                                    delta--;
                                    p = p.next;
                                }
                                int i3 = i + 1;
                                try {
                                    setTabAt(tab, i, (Node) null);
                                    i = i3;
                                } catch (Throwable th2) {
                                    Throwable th3 = th2;
                                    int i4 = i3;
                                    try {
                                    } catch (Throwable th4) {
                                        int i5 = i4;
                                        th = th4;
                                        i = i5;
                                        Throwable th5 = th;
                                        i4 = i;
                                        th3 = th5;
                                        throw th3;
                                    }
                                    throw th3;
                                }
                            }
                        } catch (Throwable th6) {
                            th = th6;
                        }
                    }
                }
            }
        }
        if (delta != 0) {
            addCount(delta, -1);
        }
    }

    public Set<K> keySet() {
        KeySetView<K, V> keySetView = this.keySet;
        KeySetView<K, V> keySetView2 = keySetView;
        if (keySetView != null) {
            return keySetView2;
        }
        KeySetView<K, V> keySetView3 = new KeySetView<>(this, null);
        this.keySet = keySetView3;
        return keySetView3;
    }

    public Collection<V> values() {
        ConcurrentHashMap.ValuesView<K, V> valuesView = this.values;
        ConcurrentHashMap.ValuesView<K, V> vs = valuesView;
        if (valuesView != null) {
            return vs;
        }
        ValuesView<K, V> valuesView2 = new ValuesView<>(this);
        this.values = valuesView2;
        return valuesView2;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        ConcurrentHashMap.EntrySetView<K, V> entrySetView = this.entrySet;
        ConcurrentHashMap.EntrySetView<K, V> es = entrySetView;
        if (entrySetView != null) {
            return es;
        }
        EntrySetView<K, V> entrySetView2 = new EntrySetView<>(this);
        this.entrySet = entrySetView2;
        return entrySetView2;
    }

    public int hashCode() {
        int h = 0;
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        if (nodeArr != null) {
            ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, t.length, 0, t.length);
            while (true) {
                ConcurrentHashMap.Node<K, V> advance = it.advance();
                ConcurrentHashMap.Node<K, V> p = advance;
                if (advance == null) {
                    break;
                }
                h += p.key.hashCode() ^ p.val.hashCode();
            }
        }
        return h;
    }

    public String toString() {
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f = nodeArr == null ? 0 : t.length;
        Traverser traverser = new Traverser(t, f, 0, f);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        ConcurrentHashMap.Node<K, V> advance = traverser.advance();
        ConcurrentHashMap.Node<K, V> p = advance;
        if (advance != null) {
            while (true) {
                K k = p.key;
                V v = p.val;
                V v2 = "(this Map)";
                sb.append(k == this ? v2 : k);
                sb.append('=');
                if (v != this) {
                    v2 = v;
                }
                sb.append(v2);
                ConcurrentHashMap.Node<K, V> advance2 = traverser.advance();
                p = advance2;
                if (advance2 == null) {
                    break;
                }
                sb.append(',');
                sb.append(' ');
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        Map<?, ?> m = (Map) o;
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f = nodeArr == null ? 0 : t.length;
        ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, f, 0, f);
        while (true) {
            ConcurrentHashMap.Node<K, V> advance = it.advance();
            ConcurrentHashMap.Node<K, V> p = advance;
            if (advance != null) {
                Object val = p.val;
                Object v = m.get(p.key);
                if (v == null || (v != val && !v.equals(val))) {
                    return false;
                }
            } else {
                for (Map.Entry<?, ?> e : m.entrySet()) {
                    Object key = e.getKey();
                    Object mk = key;
                    if (key != null) {
                        Object value = e.getValue();
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

    static class Segment<K, V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;

        Segment(float lf) {
            this.loadFactor = lf;
        }
    }

    private void writeObject(ObjectOutputStream s) {
        int sshift = 0;
        int ssize = 1;
        while (ssize < 16) {
            sshift++;
            ssize <<= 1;
        }
        int segmentShift = 32 - sshift;
        int segmentMask = ssize - 1;
        ConcurrentHashMap.Segment<K, V>[] segments = new Segment[16];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = new Segment<>(0.75f);
        }
        s.putFields().put("segments", segments);
        s.putFields().put("segmentShift", segmentShift);
        s.putFields().put("segmentMask", segmentMask);
        s.writeFields();
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        if (nodeArr != null) {
            ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, t.length, 0, t.length);
            while (true) {
                ConcurrentHashMap.Node<K, V> advance = it.advance();
                ConcurrentHashMap.Node<K, V> p = advance;
                if (advance == null) {
                    break;
                }
                s.writeObject(p.key);
                s.writeObject(p.val);
            }
        }
        s.writeObject((Object) null);
        s.writeObject((Object) null);
    }

    private void readObject(ObjectInputStream s) {
        int n;
        int mask;
        long size;
        boolean insertAtFront;
        long j;
        this.sizeCtl = -1;
        s.defaultReadObject();
        long size2 = 0;
        ConcurrentHashMap.Node<K, V> p = null;
        while (true) {
            K k = s.readObject();
            V v = s.readObject();
            if (k != null && v != null) {
                p = new Node<>(spread(k.hashCode()), k, v, p);
                size2++;
            }
        }
        if (size2 == 0) {
            this.sizeCtl = 0;
            long j2 = size2;
            return;
        }
        if (size2 >= NUM) {
            n = NUM;
        } else {
            int sz = (int) size2;
            n = tableSizeFor(sz + (sz >>> 1) + 1);
        }
        ConcurrentHashMap.Node<K, V>[] tab = new Node[n];
        int mask2 = n - 1;
        long added = 0;
        while (p != null) {
            ConcurrentHashMap.Node<K, V> next = p.next;
            int h = p.hash;
            int j3 = h & mask2;
            ConcurrentHashMap.TreeBin<K, V> tabAt = tabAt(tab, j3);
            ConcurrentHashMap.TreeBin<K, V> treeBin = tabAt;
            if (tabAt == null) {
                insertAtFront = true;
                size = size2;
                mask = mask2;
            } else {
                K k2 = p.key;
                if (treeBin.hash < 0) {
                    if (((TreeBin) treeBin).putTreeVal(h, k2, p.val) == null) {
                        added++;
                    }
                    insertAtFront = false;
                    size = size2;
                    mask = mask2;
                } else {
                    int binCount = 0;
                    boolean insertAtFront2 = true;
                    long j4 = size2;
                    Node<K, V> node = treeBin;
                    size = j4;
                    while (true) {
                        if (node == null) {
                            break;
                        }
                        if (node.hash == h) {
                            K k3 = node.key;
                            K qk = k3;
                            if (k3 == k2) {
                                break;
                            }
                            K qk2 = qk;
                            if (qk2 != null && k2.equals(qk2)) {
                                break;
                            }
                        }
                        binCount++;
                        node = node.next;
                    }
                    insertAtFront2 = false;
                    if (!insertAtFront2 || binCount < 8) {
                        int i = binCount;
                        mask = mask2;
                        insertAtFront = insertAtFront2;
                    } else {
                        added++;
                        p.next = treeBin;
                        ConcurrentHashMap.TreeNode<K, V> hd = null;
                        ConcurrentHashMap.Node<K, V> q = p;
                        int i2 = binCount;
                        ConcurrentHashMap.TreeNode<K, V> tl = null;
                        while (q != null) {
                            int mask3 = mask2;
                            long added2 = added;
                            ConcurrentHashMap.TreeNode<K, V> t = new TreeNode<>(q.hash, q.key, q.val, (Node) null, (TreeNode) null);
                            t.prev = tl;
                            if (tl == null) {
                                hd = t;
                            } else {
                                tl.next = t;
                            }
                            tl = t;
                            q = q.next;
                            mask2 = mask3;
                            added = added2;
                        }
                        mask = mask2;
                        long j5 = added;
                        setTabAt(tab, j3, new TreeBin(hd));
                        insertAtFront = false;
                    }
                }
            }
            if (insertAtFront) {
                j = 1;
                added++;
                p.next = treeBin;
                setTabAt(tab, j3, p);
            } else {
                j = 1;
            }
            p = next;
            long j6 = j;
            size2 = size;
            mask2 = mask;
        }
        int i3 = mask2;
        this.table = tab;
        this.sizeCtl = n - (n >>> 2);
        this.baseCount = added;
    }

    public V putIfAbsent(K key, V value) {
        return putVal(key, value, true);
    }

    public boolean remove(Object key, Object value) {
        if (key != null) {
            return (value == null || replaceNode(key, (Object) null, value) == null) ? false : true;
        }
        throw new NullPointerException();
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (key != null && oldValue != null && newValue != null) {
            return replaceNode(key, newValue, oldValue) != null;
        }
        throw new NullPointerException();
    }

    public V replace(K key, V value) {
        if (key != null && value != null) {
            return replaceNode(key, value, (Object) null);
        }
        throw new NullPointerException();
    }

    public V getOrDefault(Object key, V defaultValue) {
        V v = get(key);
        return v == null ? defaultValue : v;
    }

    public void forEach(j$.util.function.BiConsumer<? super K, ? super V> biConsumer) {
        if (biConsumer != null) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            if (nodeArr != null) {
                ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, t.length, 0, t.length);
                while (true) {
                    ConcurrentHashMap.Node<K, V> advance = it.advance();
                    ConcurrentHashMap.Node<K, V> p = advance;
                    if (advance != null) {
                        biConsumer.accept(p.key, p.val);
                    } else {
                        return;
                    }
                }
            }
        } else {
            throw new NullPointerException();
        }
    }

    public void replaceAll(j$.util.function.BiFunction<? super K, ? super V, ? extends V> biFunction) {
        V v;
        if (biFunction != null) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            if (nodeArr != null) {
                ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, t.length, 0, t.length);
                while (true) {
                    ConcurrentHashMap.Node<K, V> advance = it.advance();
                    ConcurrentHashMap.Node<K, V> p = advance;
                    if (advance != null) {
                        V oldValue = p.val;
                        K key = p.key;
                        do {
                            V newValue = biFunction.apply(key, oldValue);
                            if (newValue != null) {
                                if (replaceNode(key, newValue, oldValue) != null) {
                                    break;
                                }
                                v = get(key);
                                oldValue = v;
                            } else {
                                throw new NullPointerException();
                            }
                        } while (v != null);
                    } else {
                        return;
                    }
                }
            }
        } else {
            throw new NullPointerException();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0050, code lost:
        if (r2 == 0) goto L_0x0010;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x00d2, code lost:
        if (r1 == null) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x00d4, code lost:
        addCount(1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x00d9, code lost:
        return r1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0058 A[LOOP:1: B:35:0x0058->B:38:0x005a, LOOP_START, PHI: r8 
      PHI: (r8v8 'th' java.lang.Throwable) = (r8v7 'th' java.lang.Throwable), (r8v9 'th' java.lang.Throwable) binds: [B:100:0x0058, B:38:0x005a] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC, Splitter:B:35:0x0058] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public V computeIfAbsent(K r14, j$.util.function.Function<? super K, ? extends V> r15) {
        /*
            r13 = this;
            if (r14 == 0) goto L_0x00e3
            if (r15 == 0) goto L_0x00e3
            int r0 = r14.hashCode()
            int r0 = spread(r0)
            r1 = 0
            r2 = 0
            j$.util.concurrent.ConcurrentHashMap$Node<K, V>[] r3 = r13.table
        L_0x0010:
            if (r3 == 0) goto L_0x00dd
            int r4 = r3.length
            r5 = r4
            if (r4 != 0) goto L_0x0018
            goto L_0x00dd
        L_0x0018:
            int r4 = r5 + -1
            r4 = r4 & r0
            r6 = r4
            j$.util.concurrent.ConcurrentHashMap$Node r4 = tabAt(r3, r4)
            r7 = r4
            r8 = 0
            if (r4 != 0) goto L_0x005c
            j$.util.concurrent.ConcurrentHashMap$ReservationNode r4 = new j$.util.concurrent.ConcurrentHashMap$ReservationNode
            r4.<init>()
            monitor-enter(r4)
            boolean r9 = casTabAt(r3, r6, r8, r4)     // Catch:{ all -> 0x0056 }
            if (r9 == 0) goto L_0x004f
            r2 = 1
            r9 = 0
            java.lang.Object r10 = r15.apply(r14)     // Catch:{ all -> 0x0046 }
            r1 = r10
            if (r10 == 0) goto L_0x003f
            j$.util.concurrent.ConcurrentHashMap$Node r10 = new j$.util.concurrent.ConcurrentHashMap$Node     // Catch:{ all -> 0x0046 }
            r10.<init>(r0, r14, r1, r8)     // Catch:{ all -> 0x0046 }
            r9 = r10
        L_0x003f:
            setTabAt(r3, r6, r9)     // Catch:{ all -> 0x0043 }
            goto L_0x004f
        L_0x0043:
            r8 = move-exception
            r9 = r13
            goto L_0x0058
        L_0x0046:
            r8 = move-exception
            setTabAt(r3, r6, r9)     // Catch:{ all -> 0x0056 }
            throw r8     // Catch:{ all -> 0x004c }
        L_0x004c:
            r8 = move-exception
            r9 = r13
            goto L_0x0058
        L_0x004f:
            monitor-exit(r4)     // Catch:{ all -> 0x0056 }
            if (r2 == 0) goto L_0x0054
            goto L_0x00d2
        L_0x0054:
            goto L_0x00e1
        L_0x0056:
            r8 = move-exception
            r9 = r13
        L_0x0058:
            monitor-exit(r4)     // Catch:{ all -> 0x005a }
            throw r8
        L_0x005a:
            r8 = move-exception
            goto L_0x0058
        L_0x005c:
            int r4 = r7.hash
            r9 = r4
            r10 = -1
            if (r4 != r10) goto L_0x0068
            j$.util.concurrent.ConcurrentHashMap$Node[] r3 = r13.helpTransfer(r3, r7)
            goto L_0x00e1
        L_0x0068:
            r4 = 0
            monitor-enter(r7)
            j$.util.concurrent.ConcurrentHashMap$Node r10 = tabAt(r3, r6)     // Catch:{ all -> 0x00da }
            if (r10 != r7) goto L_0x00c5
            if (r9 < 0) goto L_0x00a2
            r2 = 1
            r10 = r7
        L_0x0074:
            int r11 = r10.hash     // Catch:{ all -> 0x00da }
            if (r11 != r0) goto L_0x0089
            K r11 = r10.key     // Catch:{ all -> 0x00da }
            r12 = r11
            if (r11 == r14) goto L_0x0085
            if (r12 == 0) goto L_0x0089
            boolean r11 = r14.equals(r12)     // Catch:{ all -> 0x00da }
            if (r11 == 0) goto L_0x0089
        L_0x0085:
            V r8 = r10.val     // Catch:{ all -> 0x00da }
            r1 = r8
            goto L_0x009e
        L_0x0089:
            r11 = r10
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r12 = r10.next     // Catch:{ all -> 0x00da }
            r10 = r12
            if (r12 != 0) goto L_0x009f
            java.lang.Object r12 = r15.apply(r14)     // Catch:{ all -> 0x00da }
            r1 = r12
            if (r12 == 0) goto L_0x009e
            r4 = 1
            j$.util.concurrent.ConcurrentHashMap$Node r12 = new j$.util.concurrent.ConcurrentHashMap$Node     // Catch:{ all -> 0x00da }
            r12.<init>(r0, r14, r1, r8)     // Catch:{ all -> 0x00da }
            r11.next = r12     // Catch:{ all -> 0x00da }
        L_0x009e:
            goto L_0x00c5
        L_0x009f:
            int r2 = r2 + 1
            goto L_0x0074
        L_0x00a2:
            boolean r10 = r7 instanceof j$.util.concurrent.ConcurrentHashMap.TreeBin     // Catch:{ all -> 0x00da }
            if (r10 == 0) goto L_0x00c5
            r2 = 2
            r10 = r7
            j$.util.concurrent.ConcurrentHashMap$TreeBin r10 = (j$.util.concurrent.ConcurrentHashMap.TreeBin) r10     // Catch:{ all -> 0x00da }
            j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r11 = r10.root     // Catch:{ all -> 0x00da }
            r12 = r11
            if (r11 == 0) goto L_0x00ba
            j$.util.concurrent.ConcurrentHashMap$TreeNode r8 = r12.findTreeNode(r0, r14, r8)     // Catch:{ all -> 0x00da }
            r11 = r8
            if (r8 == 0) goto L_0x00ba
            java.lang.Object r8 = r11.val     // Catch:{ all -> 0x00da }
            r1 = r8
            goto L_0x00c5
        L_0x00ba:
            java.lang.Object r8 = r15.apply(r14)     // Catch:{ all -> 0x00da }
            r1 = r8
            if (r8 == 0) goto L_0x00c5
            r4 = 1
            r10.putTreeVal(r0, r14, r1)     // Catch:{ all -> 0x00da }
        L_0x00c5:
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            if (r2 == 0) goto L_0x00e1
            r8 = 8
            if (r2 < r8) goto L_0x00cf
            r13.treeifyBin(r3, r6)
        L_0x00cf:
            if (r4 != 0) goto L_0x00d2
            return r1
        L_0x00d2:
            if (r1 == 0) goto L_0x00d9
            r3 = 1
            r13.addCount(r3, r2)
        L_0x00d9:
            return r1
        L_0x00da:
            r8 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00da }
            throw r8
        L_0x00dd:
            j$.util.concurrent.ConcurrentHashMap$Node[] r3 = r13.initTable()
        L_0x00e1:
            goto L_0x0010
        L_0x00e3:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            r0.<init>()
            goto L_0x00ea
        L_0x00e9:
            throw r0
        L_0x00ea:
            goto L_0x00e9
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.computeIfAbsent(java.lang.Object, j$.util.function.Function):java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00a7, code lost:
        if (r2 == 0) goto L_0x00ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00a9, code lost:
        addCount((long) r2, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00ad, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public V computeIfPresent(K r14, j$.util.function.BiFunction<? super K, ? super V, ? extends V> r15) {
        /*
            r13 = this;
            if (r14 == 0) goto L_0x00b7
            if (r15 == 0) goto L_0x00b7
            int r0 = r14.hashCode()
            int r0 = spread(r0)
            r1 = 0
            r2 = 0
            r3 = 0
            j$.util.concurrent.ConcurrentHashMap$Node<K, V>[] r4 = r13.table
        L_0x0011:
            if (r4 == 0) goto L_0x00b1
            int r5 = r4.length
            r6 = r5
            if (r5 != 0) goto L_0x0019
            goto L_0x00b1
        L_0x0019:
            int r5 = r6 + -1
            r5 = r5 & r0
            r7 = r5
            j$.util.concurrent.ConcurrentHashMap$Node r5 = tabAt(r4, r5)
            r8 = r5
            if (r5 != 0) goto L_0x0026
            goto L_0x00a7
        L_0x0026:
            int r5 = r8.hash
            r9 = r5
            r10 = -1
            if (r5 != r10) goto L_0x0032
            j$.util.concurrent.ConcurrentHashMap$Node[] r4 = r13.helpTransfer(r4, r8)
            goto L_0x00b5
        L_0x0032:
            monitor-enter(r8)
            j$.util.concurrent.ConcurrentHashMap$Node r5 = tabAt(r4, r7)     // Catch:{ all -> 0x00ae }
            if (r5 != r8) goto L_0x00a3
            if (r9 < 0) goto L_0x0072
            r3 = 1
            r5 = r8
            r10 = 0
        L_0x003e:
            int r11 = r5.hash     // Catch:{ all -> 0x00ae }
            if (r11 != r0) goto L_0x0067
            K r11 = r5.key     // Catch:{ all -> 0x00ae }
            r12 = r11
            if (r11 == r14) goto L_0x004f
            if (r12 == 0) goto L_0x0067
            boolean r11 = r14.equals(r12)     // Catch:{ all -> 0x00ae }
            if (r11 == 0) goto L_0x0067
        L_0x004f:
            V r11 = r5.val     // Catch:{ all -> 0x00ae }
            java.lang.Object r11 = r15.apply(r14, r11)     // Catch:{ all -> 0x00ae }
            r1 = r11
            if (r1 == 0) goto L_0x005b
            r5.val = r1     // Catch:{ all -> 0x00ae }
            goto L_0x006e
        L_0x005b:
            r2 = -1
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r11 = r5.next     // Catch:{ all -> 0x00ae }
            if (r10 == 0) goto L_0x0063
            r10.next = r11     // Catch:{ all -> 0x00ae }
            goto L_0x0066
        L_0x0063:
            setTabAt(r4, r7, r11)     // Catch:{ all -> 0x00ae }
        L_0x0066:
            goto L_0x006e
        L_0x0067:
            r10 = r5
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r11 = r5.next     // Catch:{ all -> 0x00ae }
            r5 = r11
            if (r11 != 0) goto L_0x006f
        L_0x006e:
            goto L_0x00a3
        L_0x006f:
            int r3 = r3 + 1
            goto L_0x003e
        L_0x0072:
            boolean r5 = r8 instanceof j$.util.concurrent.ConcurrentHashMap.TreeBin     // Catch:{ all -> 0x00ae }
            if (r5 == 0) goto L_0x00a3
            r3 = 2
            r5 = r8
            j$.util.concurrent.ConcurrentHashMap$TreeBin r5 = (j$.util.concurrent.ConcurrentHashMap.TreeBin) r5     // Catch:{ all -> 0x00ae }
            j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r10 = r5.root     // Catch:{ all -> 0x00ae }
            r11 = r10
            if (r10 == 0) goto L_0x00a3
            r10 = 0
            j$.util.concurrent.ConcurrentHashMap$TreeNode r10 = r11.findTreeNode(r0, r14, r10)     // Catch:{ all -> 0x00ae }
            r12 = r10
            if (r10 == 0) goto L_0x00a3
            java.lang.Object r10 = r12.val     // Catch:{ all -> 0x00ae }
            java.lang.Object r10 = r15.apply(r14, r10)     // Catch:{ all -> 0x00ae }
            r1 = r10
            if (r1 == 0) goto L_0x0093
            r12.val = r1     // Catch:{ all -> 0x00ae }
            goto L_0x00a3
        L_0x0093:
            r2 = -1
            boolean r10 = r5.removeTreeNode(r12)     // Catch:{ all -> 0x00ae }
            if (r10 == 0) goto L_0x00a3
            j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r10 = r5.first     // Catch:{ all -> 0x00ae }
            j$.util.concurrent.ConcurrentHashMap$Node r10 = untreeify(r10)     // Catch:{ all -> 0x00ae }
            setTabAt(r4, r7, r10)     // Catch:{ all -> 0x00ae }
        L_0x00a3:
            monitor-exit(r8)     // Catch:{ all -> 0x00ae }
            if (r3 == 0) goto L_0x00b5
        L_0x00a7:
            if (r2 == 0) goto L_0x00ad
            long r4 = (long) r2
            r13.addCount(r4, r3)
        L_0x00ad:
            return r1
        L_0x00ae:
            r5 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x00ae }
            throw r5
        L_0x00b1:
            j$.util.concurrent.ConcurrentHashMap$Node[] r4 = r13.initTable()
        L_0x00b5:
            goto L_0x0011
        L_0x00b7:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            r0.<init>()
            goto L_0x00be
        L_0x00bd:
            throw r0
        L_0x00be:
            goto L_0x00bd
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.computeIfPresent(java.lang.Object, j$.util.function.BiFunction):java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:0x011c, code lost:
        treeifyBin(r8, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0062, code lost:
        if (r7 == 0) goto L_0x012e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00a3, code lost:
        r5 = r3.apply(r2, r7.val);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00aa, code lost:
        if (r5 == null) goto L_0x00af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00ac, code lost:
        r7.val = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00af, code lost:
        r6 = -1;
        r12 = r7.next;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00b2, code lost:
        if (r0 == null) goto L_0x00b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00b4, code lost:
        r0.next = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00b7, code lost:
        setTabAt(r8, r10, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0116, code lost:
        if (r7 == 0) goto L_0x012e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x011a, code lost:
        if (r7 < 8) goto L_0x011f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x006a A[LOOP:1: B:36:0x006a->B:39:0x006c, LOOP_START, PHI: r0 
      PHI: (r0v24 'th' java.lang.Throwable) = (r0v23 'th' java.lang.Throwable), (r0v25 'th' java.lang.Throwable) binds: [B:120:0x006a, B:39:0x006c] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC, Splitter:B:36:0x006a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public V compute(K r19, j$.util.function.BiFunction<? super K, ? super V, ? extends V> r20) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            r3 = r20
            if (r2 == 0) goto L_0x0132
            if (r3 == 0) goto L_0x0132
            int r0 = r19.hashCode()
            int r4 = spread(r0)
            r0 = 0
            r5 = 0
            r6 = 0
            j$.util.concurrent.ConcurrentHashMap$Node<K, V>[] r7 = r1.table
            r8 = r7
            r7 = r6
            r6 = r5
            r5 = r0
        L_0x001b:
            if (r8 == 0) goto L_0x0129
            int r0 = r8.length
            r9 = r0
            if (r0 != 0) goto L_0x0023
            goto L_0x0129
        L_0x0023:
            int r0 = r9 + -1
            r0 = r0 & r4
            r10 = r0
            j$.util.concurrent.ConcurrentHashMap$Node r0 = tabAt(r8, r0)
            r11 = r0
            r12 = 0
            if (r0 != 0) goto L_0x006e
            j$.util.concurrent.ConcurrentHashMap$ReservationNode r0 = new j$.util.concurrent.ConcurrentHashMap$ReservationNode
            r0.<init>()
            r13 = r0
            monitor-enter(r13)
            boolean r0 = casTabAt(r8, r10, r12, r13)     // Catch:{ all -> 0x0068 }
            if (r0 == 0) goto L_0x0061
            r7 = 1
            r14 = 0
            java.lang.Object r0 = r3.apply(r2, r12)     // Catch:{ all -> 0x0053 }
            r5 = r0
            if (r0 == 0) goto L_0x004c
            r6 = 1
            j$.util.concurrent.ConcurrentHashMap$Node r0 = new j$.util.concurrent.ConcurrentHashMap$Node     // Catch:{ all -> 0x0053 }
            r0.<init>(r4, r2, r5, r12)     // Catch:{ all -> 0x0053 }
            r14 = r0
        L_0x004c:
            setTabAt(r8, r10, r14)     // Catch:{ all -> 0x0050 }
            goto L_0x0061
        L_0x0050:
            r0 = move-exception
            r12 = r1
            goto L_0x006a
        L_0x0053:
            r0 = move-exception
            setTabAt(r8, r10, r14)     // Catch:{ all -> 0x0068 }
            throw r0     // Catch:{ all -> 0x0059 }
        L_0x0059:
            r0 = move-exception
            r12 = r18
            r3 = r20
            r2 = r19
            goto L_0x006a
        L_0x0061:
            monitor-exit(r13)     // Catch:{ all -> 0x0068 }
            if (r7 == 0) goto L_0x0066
            goto L_0x011f
        L_0x0066:
            goto L_0x012e
        L_0x0068:
            r0 = move-exception
            r12 = r1
        L_0x006a:
            monitor-exit(r13)     // Catch:{ all -> 0x006c }
            throw r0
        L_0x006c:
            r0 = move-exception
            goto L_0x006a
        L_0x006e:
            int r0 = r11.hash
            r13 = r0
            r14 = -1
            if (r0 != r14) goto L_0x007b
            j$.util.concurrent.ConcurrentHashMap$Node[] r0 = r1.helpTransfer(r8, r11)
            r8 = r0
            goto L_0x012e
        L_0x007b:
            monitor-enter(r11)
            j$.util.concurrent.ConcurrentHashMap$Node r0 = tabAt(r8, r10)     // Catch:{ all -> 0x0126 }
            if (r0 != r11) goto L_0x0115
            if (r13 < 0) goto L_0x00d8
            r0 = 1
            r7 = r11
            r14 = 0
            r17 = r14
            r14 = r0
            r0 = r17
        L_0x008c:
            int r15 = r7.hash     // Catch:{ all -> 0x00d5 }
            if (r15 != r4) goto L_0x00bb
            K r15 = r7.key     // Catch:{ all -> 0x00d5 }
            r16 = r15
            if (r15 == r2) goto L_0x00a1
            r15 = r16
            if (r15 == 0) goto L_0x00bb
            boolean r16 = r2.equals(r15)     // Catch:{ all -> 0x00d5 }
            if (r16 == 0) goto L_0x00bb
            goto L_0x00a3
        L_0x00a1:
            r15 = r16
        L_0x00a3:
            V r12 = r7.val     // Catch:{ all -> 0x00d5 }
            java.lang.Object r12 = r3.apply(r2, r12)     // Catch:{ all -> 0x00d5 }
            r5 = r12
            if (r5 == 0) goto L_0x00af
            r7.val = r5     // Catch:{ all -> 0x00d5 }
            goto L_0x00d0
        L_0x00af:
            r6 = -1
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r12 = r7.next     // Catch:{ all -> 0x00d5 }
            if (r0 == 0) goto L_0x00b7
            r0.next = r12     // Catch:{ all -> 0x00d5 }
            goto L_0x00ba
        L_0x00b7:
            setTabAt(r8, r10, r12)     // Catch:{ all -> 0x00d5 }
        L_0x00ba:
            goto L_0x00d0
        L_0x00bb:
            r0 = r7
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r15 = r7.next     // Catch:{ all -> 0x00d5 }
            r7 = r15
            if (r15 != 0) goto L_0x00d2
            java.lang.Object r15 = r3.apply(r2, r12)     // Catch:{ all -> 0x00d5 }
            r5 = r15
            if (r5 == 0) goto L_0x00d0
            r6 = 1
            j$.util.concurrent.ConcurrentHashMap$Node r15 = new j$.util.concurrent.ConcurrentHashMap$Node     // Catch:{ all -> 0x00d5 }
            r15.<init>(r4, r2, r5, r12)     // Catch:{ all -> 0x00d5 }
            r0.next = r15     // Catch:{ all -> 0x00d5 }
        L_0x00d0:
            r7 = r14
            goto L_0x0115
        L_0x00d2:
            int r14 = r14 + 1
            goto L_0x008c
        L_0x00d5:
            r0 = move-exception
            r7 = r14
            goto L_0x0127
        L_0x00d8:
            boolean r0 = r11 instanceof j$.util.concurrent.ConcurrentHashMap.TreeBin     // Catch:{ all -> 0x0126 }
            if (r0 == 0) goto L_0x0115
            r7 = 1
            r0 = r11
            j$.util.concurrent.ConcurrentHashMap$TreeBin r0 = (j$.util.concurrent.ConcurrentHashMap.TreeBin) r0     // Catch:{ all -> 0x0126 }
            j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r14 = r0.root     // Catch:{ all -> 0x0126 }
            r15 = r14
            if (r14 == 0) goto L_0x00ea
            j$.util.concurrent.ConcurrentHashMap$TreeNode r14 = r15.findTreeNode(r4, r2, r12)     // Catch:{ all -> 0x0126 }
            goto L_0x00eb
        L_0x00ea:
            r14 = 0
        L_0x00eb:
            if (r14 != 0) goto L_0x00ee
        L_0x00ed:
            goto L_0x00f1
        L_0x00ee:
            java.lang.Object r12 = r14.val     // Catch:{ all -> 0x0126 }
            goto L_0x00ed
        L_0x00f1:
            java.lang.Object r16 = r3.apply(r2, r12)     // Catch:{ all -> 0x0126 }
            r5 = r16
            if (r5 == 0) goto L_0x0103
            if (r14 == 0) goto L_0x00fe
            r14.val = r5     // Catch:{ all -> 0x0126 }
            goto L_0x0115
        L_0x00fe:
            r6 = 1
            r0.putTreeVal(r4, r2, r5)     // Catch:{ all -> 0x0126 }
            goto L_0x0115
        L_0x0103:
            if (r14 == 0) goto L_0x0115
            r6 = -1
            boolean r16 = r0.removeTreeNode(r14)     // Catch:{ all -> 0x0126 }
            if (r16 == 0) goto L_0x0115
            j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r2 = r0.first     // Catch:{ all -> 0x0126 }
            j$.util.concurrent.ConcurrentHashMap$Node r2 = untreeify(r2)     // Catch:{ all -> 0x0126 }
            setTabAt(r8, r10, r2)     // Catch:{ all -> 0x0126 }
        L_0x0115:
            monitor-exit(r11)     // Catch:{ all -> 0x0126 }
            if (r7 == 0) goto L_0x012e
            r0 = 8
            if (r7 < r0) goto L_0x011f
            r1.treeifyBin(r8, r10)
        L_0x011f:
            if (r6 == 0) goto L_0x0125
            long r8 = (long) r6
            r1.addCount(r8, r7)
        L_0x0125:
            return r5
        L_0x0126:
            r0 = move-exception
        L_0x0127:
            monitor-exit(r11)     // Catch:{ all -> 0x0126 }
            throw r0
        L_0x0129:
            j$.util.concurrent.ConcurrentHashMap$Node[] r0 = r18.initTable()
            r8 = r0
        L_0x012e:
            r2 = r19
            goto L_0x001b
        L_0x0132:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            r0.<init>()
            goto L_0x0139
        L_0x0138:
            throw r0
        L_0x0139:
            goto L_0x0138
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.compute(java.lang.Object, j$.util.function.BiFunction):java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:106:0x012d, code lost:
        if (r8 == 0) goto L_0x014e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x0131, code lost:
        if (r8 < 8) goto L_0x0136;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x0133, code lost:
        treeifyBin(r9, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x0136, code lost:
        r0 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00af, code lost:
        r6 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        r0.next = new j$.util.concurrent.ConcurrentHashMap.Node<>(r5, r2, r6, (j$.util.concurrent.ConcurrentHashMap.Node) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00bc, code lost:
        r7 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x00c1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00c2, code lost:
        r8 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x00c7, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x00c8, code lost:
        r8 = r15;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public V merge(K r21, V r22, j$.util.function.BiFunction<? super V, ? super V, ? extends V> r23) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            if (r2 == 0) goto L_0x0152
            if (r3 == 0) goto L_0x0152
            if (r4 == 0) goto L_0x0152
            int r0 = r21.hashCode()
            int r5 = spread(r0)
            r0 = 0
            r6 = 0
            r7 = 0
            j$.util.concurrent.ConcurrentHashMap$Node<K, V>[] r8 = r1.table
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r0
        L_0x001f:
            if (r9 == 0) goto L_0x0145
            int r0 = r9.length
            r10 = r0
            if (r0 != 0) goto L_0x0029
            r17 = r6
            goto L_0x0147
        L_0x0029:
            int r0 = r10 + -1
            r0 = r0 & r5
            r11 = r0
            j$.util.concurrent.ConcurrentHashMap$Node r0 = tabAt(r9, r0)
            r12 = r0
            r13 = 0
            if (r0 != 0) goto L_0x0045
            j$.util.concurrent.ConcurrentHashMap$Node r0 = new j$.util.concurrent.ConcurrentHashMap$Node
            r0.<init>(r5, r2, r3, r13)
            boolean r0 = casTabAt(r9, r11, r13, r0)
            if (r0 == 0) goto L_0x014e
            r0 = 1
            r6 = r22
            goto L_0x0137
        L_0x0045:
            int r0 = r12.hash
            r14 = r0
            r15 = -1
            if (r0 != r15) goto L_0x0052
            j$.util.concurrent.ConcurrentHashMap$Node[] r0 = r1.helpTransfer(r9, r12)
            r9 = r0
            goto L_0x014e
        L_0x0052:
            monitor-enter(r12)
            j$.util.concurrent.ConcurrentHashMap$Node r0 = tabAt(r9, r11)     // Catch:{ all -> 0x0140 }
            if (r0 != r12) goto L_0x0128
            if (r14 < 0) goto L_0x00df
            r0 = 1
            r8 = r12
            r15 = 0
            r19 = r15
            r15 = r0
            r0 = r19
        L_0x0063:
            int r13 = r8.hash     // Catch:{ all -> 0x00d9 }
            if (r13 != r5) goto L_0x00a7
            K r13 = r8.key     // Catch:{ all -> 0x00d9 }
            r17 = r13
            if (r13 == r2) goto L_0x0082
            r13 = r17
            if (r13 == 0) goto L_0x007f
            boolean r17 = r2.equals(r13)     // Catch:{ all -> 0x007b }
            if (r17 == 0) goto L_0x0078
            goto L_0x0084
        L_0x0078:
            r17 = r6
            goto L_0x00a9
        L_0x007b:
            r0 = move-exception
            r8 = r15
            goto L_0x0143
        L_0x007f:
            r17 = r6
            goto L_0x00a9
        L_0x0082:
            r13 = r17
        L_0x0084:
            r17 = r6
            V r6 = r8.val     // Catch:{ all -> 0x00d3 }
            java.lang.Object r6 = r4.apply(r6, r3)     // Catch:{ all -> 0x00d3 }
            if (r6 == 0) goto L_0x0091
            r8.val = r6     // Catch:{ all -> 0x007b }
            goto L_0x00be
        L_0x0091:
            r7 = -1
            r16 = r6
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r6 = r8.next     // Catch:{ all -> 0x00a1 }
            if (r0 == 0) goto L_0x009b
            r0.next = r6     // Catch:{ all -> 0x00a1 }
            goto L_0x009e
        L_0x009b:
            setTabAt(r9, r11, r6)     // Catch:{ all -> 0x00a1 }
        L_0x009e:
            r6 = r16
            goto L_0x00be
        L_0x00a1:
            r0 = move-exception
            r8 = r15
            r6 = r16
            goto L_0x0143
        L_0x00a7:
            r17 = r6
        L_0x00a9:
            r0 = r8
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r6 = r8.next     // Catch:{ all -> 0x00d3 }
            r8 = r6
            if (r6 != 0) goto L_0x00cd
            r7 = 1
            r6 = r22
            j$.util.concurrent.ConcurrentHashMap$Node r13 = new j$.util.concurrent.ConcurrentHashMap$Node     // Catch:{ all -> 0x00c7 }
            r18 = r7
            r7 = 0
            r13.<init>(r5, r2, r6, r7)     // Catch:{ all -> 0x00c1 }
            r0.next = r13     // Catch:{ all -> 0x00c1 }
            r7 = r18
        L_0x00be:
            r8 = r15
            goto L_0x012c
        L_0x00c1:
            r0 = move-exception
            r8 = r15
            r7 = r18
            goto L_0x0143
        L_0x00c7:
            r0 = move-exception
            r18 = r7
            r8 = r15
            goto L_0x0143
        L_0x00cd:
            int r15 = r15 + 1
            r6 = r17
            r13 = 0
            goto L_0x0063
        L_0x00d3:
            r0 = move-exception
            r8 = r15
            r6 = r17
            goto L_0x0143
        L_0x00d9:
            r0 = move-exception
            r17 = r6
            r8 = r15
            goto L_0x0143
        L_0x00df:
            r17 = r6
            boolean r0 = r12 instanceof j$.util.concurrent.ConcurrentHashMap.TreeBin     // Catch:{ all -> 0x0124 }
            if (r0 == 0) goto L_0x012a
            r8 = 2
            r0 = r12
            j$.util.concurrent.ConcurrentHashMap$TreeBin r0 = (j$.util.concurrent.ConcurrentHashMap.TreeBin) r0     // Catch:{ all -> 0x0124 }
            j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r6 = r0.root     // Catch:{ all -> 0x0124 }
            if (r6 != 0) goto L_0x00ef
            r13 = 0
            goto L_0x00f4
        L_0x00ef:
            r13 = 0
            j$.util.concurrent.ConcurrentHashMap$TreeNode r13 = r6.findTreeNode(r5, r2, r13)     // Catch:{ all -> 0x0124 }
        L_0x00f4:
            if (r13 != 0) goto L_0x00f9
            r15 = r3
            goto L_0x00ff
        L_0x00f9:
            java.lang.Object r15 = r13.val     // Catch:{ all -> 0x0124 }
            java.lang.Object r15 = r4.apply(r15, r3)     // Catch:{ all -> 0x0124 }
        L_0x00ff:
            if (r15 == 0) goto L_0x0110
            if (r13 == 0) goto L_0x010a
            r13.val = r15     // Catch:{ all -> 0x0107 }
            goto L_0x0122
        L_0x0107:
            r0 = move-exception
            r6 = r15
            goto L_0x0143
        L_0x010a:
            r7 = 1
            r0.putTreeVal(r5, r2, r15)     // Catch:{ all -> 0x0107 }
            r6 = r15
            goto L_0x012c
        L_0x0110:
            if (r13 == 0) goto L_0x0122
            r7 = -1
            boolean r16 = r0.removeTreeNode(r13)     // Catch:{ all -> 0x0107 }
            if (r16 == 0) goto L_0x0122
            j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r2 = r0.first     // Catch:{ all -> 0x0107 }
            j$.util.concurrent.ConcurrentHashMap$Node r2 = untreeify(r2)     // Catch:{ all -> 0x0107 }
            setTabAt(r9, r11, r2)     // Catch:{ all -> 0x0107 }
        L_0x0122:
            r6 = r15
            goto L_0x012c
        L_0x0124:
            r0 = move-exception
            r6 = r17
            goto L_0x0143
        L_0x0128:
            r17 = r6
        L_0x012a:
            r6 = r17
        L_0x012c:
            monitor-exit(r12)     // Catch:{ all -> 0x013e }
            if (r8 == 0) goto L_0x014e
            r0 = 8
            if (r8 < r0) goto L_0x0136
            r1.treeifyBin(r9, r11)
        L_0x0136:
            r0 = r7
        L_0x0137:
            if (r0 == 0) goto L_0x013d
            long r9 = (long) r0
            r1.addCount(r9, r8)
        L_0x013d:
            return r6
        L_0x013e:
            r0 = move-exception
            goto L_0x0143
        L_0x0140:
            r0 = move-exception
            r17 = r6
        L_0x0143:
            monitor-exit(r12)     // Catch:{ all -> 0x013e }
            throw r0
        L_0x0145:
            r17 = r6
        L_0x0147:
            j$.util.concurrent.ConcurrentHashMap$Node[] r0 = r20.initTable()
            r9 = r0
            r6 = r17
        L_0x014e:
            r2 = r21
            goto L_0x001f
        L_0x0152:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            r0.<init>()
            goto L_0x0159
        L_0x0158:
            throw r0
        L_0x0159:
            goto L_0x0158
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.merge(java.lang.Object, java.lang.Object, j$.util.function.BiFunction):java.lang.Object");
    }

    public boolean contains(Object value) {
        return containsValue(value);
    }

    public Enumeration<K> keys() {
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f = nodeArr == null ? 0 : t.length;
        return new KeyIterator(t, f, 0, f, this);
    }

    public Enumeration<V> elements() {
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f = nodeArr == null ? 0 : t.length;
        return new ValueIterator(t, f, 0, f, this);
    }

    public long mappingCount() {
        long n = sumCount();
        if (n < 0) {
            return 0;
        }
        return n;
    }

    static final class ForwardingNode<K, V> extends Node<K, V> {
        final Node<K, V>[] nextTable;

        ForwardingNode(Node<K, V>[] tab) {
            super(-1, null, null, (Node) null);
            this.nextTable = tab;
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x002d, code lost:
            if ((r4 instanceof j$.util.concurrent.ConcurrentHashMap.ForwardingNode) == false) goto L_0x0035;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x002f, code lost:
            r0 = ((j$.util.concurrent.ConcurrentHashMap.ForwardingNode) r4).nextTable;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0039, code lost:
            return r4.find(r8, r9);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public j$.util.concurrent.ConcurrentHashMap.Node<K, V> find(int r8, java.lang.Object r9) {
            /*
                r7 = this;
                j$.util.concurrent.ConcurrentHashMap$Node<K, V>[] r0 = r7.nextTable
            L_0x0002:
                r1 = 0
                if (r9 == 0) goto L_0x0041
                if (r0 == 0) goto L_0x0041
                int r2 = r0.length
                r3 = r2
                if (r2 == 0) goto L_0x0041
                int r2 = r3 + -1
                r2 = r2 & r8
                j$.util.concurrent.ConcurrentHashMap$Node r2 = j$.util.concurrent.ConcurrentHashMap.tabAt(r0, r2)
                r4 = r2
                if (r2 != 0) goto L_0x0016
                goto L_0x0041
            L_0x0016:
                int r2 = r4.hash
                r5 = r2
                if (r2 != r8) goto L_0x0029
                K r2 = r4.key
                r6 = r2
                if (r2 == r9) goto L_0x0028
                if (r6 == 0) goto L_0x0029
                boolean r2 = r9.equals(r6)
                if (r2 == 0) goto L_0x0029
            L_0x0028:
                return r4
            L_0x0029:
                if (r5 >= 0) goto L_0x003a
                boolean r1 = r4 instanceof j$.util.concurrent.ConcurrentHashMap.ForwardingNode
                if (r1 == 0) goto L_0x0035
                r1 = r4
                j$.util.concurrent.ConcurrentHashMap$ForwardingNode r1 = (j$.util.concurrent.ConcurrentHashMap.ForwardingNode) r1
                j$.util.concurrent.ConcurrentHashMap$Node<K, V>[] r0 = r1.nextTable
                goto L_0x0002
            L_0x0035:
                j$.util.concurrent.ConcurrentHashMap$Node r1 = r4.find(r8, r9)
                return r1
            L_0x003a:
                j$.util.concurrent.ConcurrentHashMap$Node<K, V> r2 = r4.next
                r4 = r2
                if (r2 != 0) goto L_0x0040
                return r1
            L_0x0040:
                goto L_0x0016
            L_0x0041:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.ForwardingNode.find(int, java.lang.Object):j$.util.concurrent.ConcurrentHashMap$Node");
        }
    }

    static final class ReservationNode<K, V> extends Node<K, V> {
        ReservationNode() {
            super(-3, null, null, (Node) null);
        }

        /* access modifiers changed from: package-private */
        public Node<K, V> find(int h, Object k) {
            return null;
        }
    }

    static final int resizeStamp(int n) {
        return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
    }

    private final Node<K, V>[] initTable() {
        ConcurrentHashMap.Node<K, V>[] tab;
        while (true) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.table;
            tab = nodeArr;
            if (nodeArr != null && tab.length != 0) {
                break;
            }
            int i = this.sizeCtl;
            int sc = i;
            if (i < 0) {
                Thread.yield();
            } else {
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                    try {
                        ConcurrentHashMap.Node<K, V>[] nodeArr2 = this.table;
                        tab = nodeArr2;
                        if (nodeArr2 == null || tab.length == 0) {
                            int n = sc > 0 ? sc : 16;
                            ConcurrentHashMap.Node<K, V>[] nt = new Node[n];
                            tab = nt;
                            this.table = nt;
                            sc = n - (n >>> 2);
                        }
                    } finally {
                        this.sizeCtl = sc;
                    }
                }
            }
        }
        return tab;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x001b, code lost:
        if (r0.compareAndSwapLong(r23, r2, r4, r6) == false) goto L_0x001d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void addCount(long r24, int r26) {
        /*
            r23 = this;
            r8 = r23
            r9 = r24
            r11 = r26
            j$.util.concurrent.ConcurrentHashMap$CounterCell[] r0 = r8.counterCells
            r12 = r0
            if (r0 != 0) goto L_0x001d
            sun.misc.Unsafe r0 = U
            long r2 = BASECOUNT
            long r4 = r8.baseCount
            r13 = r4
            long r6 = r13 + r9
            r15 = r6
            r1 = r23
            boolean r0 = r0.compareAndSwapLong(r1, r2, r4, r6)
            if (r0 != 0) goto L_0x004d
        L_0x001d:
            r0 = 1
            if (r12 == 0) goto L_0x00b8
            int r1 = r12.length
            r2 = 1
            int r1 = r1 - r2
            r3 = r1
            if (r1 < 0) goto L_0x00b8
            int r1 = j$.util.concurrent.ThreadLocalRandom.getProbe()
            r1 = r1 & r3
            r1 = r12[r1]
            r4 = r1
            if (r1 == 0) goto L_0x00b8
            sun.misc.Unsafe r13 = U
            long r15 = CELLVALUE
            long r5 = r4.value
            r21 = r5
            long r19 = r21 + r9
            r14 = r4
            r17 = r5
            boolean r1 = r13.compareAndSwapLong(r14, r15, r17, r19)
            r0 = r1
            if (r1 != 0) goto L_0x0046
            goto L_0x00b8
        L_0x0046:
            if (r11 > r2) goto L_0x0049
            return
        L_0x0049:
            long r15 = r23.sumCount()
        L_0x004d:
            if (r11 < 0) goto L_0x00b7
        L_0x004f:
            int r0 = r8.sizeCtl
            r6 = r0
            long r0 = (long) r0
            int r2 = (r15 > r0 ? 1 : (r15 == r0 ? 0 : -1))
            if (r2 < 0) goto L_0x00b7
            j$.util.concurrent.ConcurrentHashMap$Node<K, V>[] r0 = r8.table
            r7 = r0
            if (r0 == 0) goto L_0x00b7
            int r0 = r7.length
            r13 = r0
            r1 = 1073741824(0x40000000, float:2.0)
            if (r0 >= r1) goto L_0x00b7
            int r14 = resizeStamp(r13)
            if (r6 >= 0) goto L_0x0099
            int r0 = RESIZE_STAMP_SHIFT
            int r0 = r6 >>> r0
            if (r0 != r14) goto L_0x00b7
            int r0 = r14 + 1
            if (r6 == r0) goto L_0x00b7
            int r0 = MAX_RESIZERS
            int r0 = r0 + r14
            if (r6 == r0) goto L_0x00b7
            j$.util.concurrent.ConcurrentHashMap$Node<K, V>[] r0 = r8.nextTable
            r5 = r0
            if (r0 == 0) goto L_0x0097
            int r0 = r8.transferIndex
            if (r0 > 0) goto L_0x0081
            goto L_0x00b7
        L_0x0081:
            sun.misc.Unsafe r0 = U
            long r2 = SIZECTL
            int r17 = r6 + 1
            r1 = r23
            r4 = r6
            r11 = r5
            r5 = r17
            boolean r0 = r0.compareAndSwapInt(r1, r2, r4, r5)
            if (r0 == 0) goto L_0x00b0
            r8.transfer(r7, r11)
            goto L_0x00b0
        L_0x0097:
            r11 = r5
            goto L_0x00b7
        L_0x0099:
            sun.misc.Unsafe r0 = U
            long r2 = SIZECTL
            int r1 = RESIZE_STAMP_SHIFT
            int r1 = r14 << r1
            int r5 = r1 + 2
            r1 = r23
            r4 = r6
            boolean r0 = r0.compareAndSwapInt(r1, r2, r4, r5)
            if (r0 == 0) goto L_0x00b0
            r0 = 0
            r8.transfer(r7, r0)
        L_0x00b0:
            long r15 = r23.sumCount()
            r11 = r26
            goto L_0x004f
        L_0x00b7:
            return
        L_0x00b8:
            r8.fullAddCount(r9, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.addCount(long, int):void");
    }

    /* access modifiers changed from: package-private */
    public final Node<K, V>[] helpTransfer(Node<K, V>[] tab, Node<K, V> f) {
        if (tab != null && (f instanceof ForwardingNode)) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = ((ForwardingNode) f).nextTable;
            ConcurrentHashMap.Node<K, V>[] nextTab = nodeArr;
            if (nodeArr != null) {
                int rs = resizeStamp(tab.length);
                while (true) {
                    if (nextTab != this.nextTable || this.table != tab) {
                        break;
                    }
                    int i = this.sizeCtl;
                    int sc = i;
                    if (i >= 0 || (sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 || sc == MAX_RESIZERS + rs || this.transferIndex <= 0) {
                        break;
                    }
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                        transfer(tab, nextTab);
                        break;
                    }
                }
                return nextTab;
            }
        }
        return this.table;
    }

    private final void tryPresize(int size) {
        int c;
        if (size >= NUM) {
            c = NUM;
        } else {
            c = tableSizeFor((size >>> 1) + size + 1);
        }
        while (true) {
            int i = this.sizeCtl;
            int sc = i;
            if (i >= 0) {
                ConcurrentHashMap.Node<K, V>[] tab = this.table;
                if (tab != null) {
                    int length = tab.length;
                    int n = length;
                    if (length != 0) {
                        if (c > sc && n < NUM) {
                            if (tab == this.table) {
                                int rs = resizeStamp(n);
                                if (sc >= 0) {
                                    if (U.compareAndSwapInt(this, SIZECTL, sc, (rs << RESIZE_STAMP_SHIFT) + 2)) {
                                        transfer(tab, (Node<K, V>[]) null);
                                    }
                                } else if ((sc >>> RESIZE_STAMP_SHIFT) == rs && sc != rs + 1 && sc != MAX_RESIZERS + rs) {
                                    Node<K, V>[] nodeArr = this.nextTable;
                                    Node<K, V>[] nodeArr2 = nodeArr;
                                    if (nodeArr != null && this.transferIndex > 0) {
                                        if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                                            transfer(tab, nodeArr2);
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
                int n2 = sc > c ? sc : c;
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                    try {
                        if (this.table == tab) {
                            this.table = new Node[n2];
                            sc = n2 - (n2 >>> 2);
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

    /* JADX WARNING: Code restructure failed: missing block: B:140:0x0219, code lost:
        r7 = r30;
        r15 = r28;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void transfer(j$.util.concurrent.ConcurrentHashMap.Node<K, V>[] r31, j$.util.concurrent.ConcurrentHashMap.Node<K, V>[] r32) {
        /*
            r30 = this;
            r7 = r30
            r8 = r31
            int r9 = r8.length
            int r0 = NCPU
            r1 = 1
            if (r0 <= r1) goto L_0x000e
            int r1 = r9 >>> 3
            int r1 = r1 / r0
            goto L_0x000f
        L_0x000e:
            r1 = r9
        L_0x000f:
            r0 = r1
            r2 = 16
            if (r1 >= r2) goto L_0x0018
            r0 = 16
            r10 = r0
            goto L_0x0019
        L_0x0018:
            r10 = r0
        L_0x0019:
            if (r32 != 0) goto L_0x0030
            int r0 = r9 << 1
            j$.util.concurrent.ConcurrentHashMap$Node[] r0 = new j$.util.concurrent.ConcurrentHashMap.Node[r0]     // Catch:{ all -> 0x0027 }
            r7.nextTable = r0
            r7.transferIndex = r9
            r11 = r0
            goto L_0x0032
        L_0x0027:
            r0 = move-exception
            r1 = r0
            r0 = r1
            r1 = 2147483647(0x7fffffff, float:NaN)
            r7.sizeCtl = r1
            return
        L_0x0030:
            r11 = r32
        L_0x0032:
            int r12 = r11.length
            j$.util.concurrent.ConcurrentHashMap$ForwardingNode r0 = new j$.util.concurrent.ConcurrentHashMap$ForwardingNode
            r0.<init>(r11)
            r13 = r0
            r0 = 1
            r1 = 0
            r2 = 0
            r3 = 0
            r14 = r0
            r15 = r1
            r6 = r2
            r5 = r3
        L_0x0041:
            if (r14 == 0) goto L_0x0089
            int r0 = r6 + -1
            if (r0 >= r5) goto L_0x0081
            if (r15 == 0) goto L_0x004c
            r18 = r5
            goto L_0x0083
        L_0x004c:
            int r1 = r7.transferIndex
            r6 = r1
            if (r1 > 0) goto L_0x0056
            r0 = -1
            r1 = 0
            r6 = r0
            r14 = r1
            goto L_0x0088
        L_0x0056:
            sun.misc.Unsafe r1 = U
            long r3 = TRANSFERINDEX
            if (r6 <= r10) goto L_0x0061
            int r2 = r6 - r10
            r16 = r2
            goto L_0x0064
        L_0x0061:
            r2 = 0
            r16 = 0
        L_0x0064:
            r17 = r16
            r2 = r30
            r18 = r5
            r5 = r6
            r19 = r6
            r6 = r16
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x007d
            r1 = r17
            int r6 = r19 + -1
            r0 = 0
            r14 = r0
            r5 = r1
            goto L_0x0088
        L_0x007d:
            r6 = r0
            r5 = r18
            goto L_0x0088
        L_0x0081:
            r18 = r5
        L_0x0083:
            r1 = 0
            r6 = r0
            r14 = r1
            r5 = r18
        L_0x0088:
            goto L_0x0041
        L_0x0089:
            r18 = r5
            r0 = 0
            if (r6 < 0) goto L_0x022d
            if (r6 >= r9) goto L_0x022d
            int r1 = r6 + r9
            if (r1 < r12) goto L_0x009e
            r19 = r10
            r20 = r12
            r17 = r14
            r28 = r15
            goto L_0x0235
        L_0x009e:
            j$.util.concurrent.ConcurrentHashMap$Node r1 = tabAt(r8, r6)
            r2 = r1
            if (r1 != 0) goto L_0x00b0
            boolean r0 = casTabAt(r8, r6, r0, r13)
            r14 = r0
            r19 = r10
            r20 = r12
            goto L_0x0270
        L_0x00b0:
            int r0 = r2.hash
            r1 = r0
            r3 = -1
            if (r0 != r3) goto L_0x00be
            r0 = 1
            r14 = r0
            r19 = r10
            r20 = r12
            goto L_0x0270
        L_0x00be:
            monitor-enter(r2)
            j$.util.concurrent.ConcurrentHashMap$Node r0 = tabAt(r8, r6)     // Catch:{ all -> 0x0220 }
            if (r0 != r2) goto L_0x020c
            if (r1 < 0) goto L_0x015a
            r0 = r1 & r9
            r3 = r2
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r4 = r2.next     // Catch:{ all -> 0x014f }
        L_0x00cc:
            if (r4 == 0) goto L_0x00e4
            int r5 = r4.hash     // Catch:{ all -> 0x00d9 }
            r5 = r5 & r9
            if (r5 == r0) goto L_0x00d5
            r0 = r5
            r3 = r4
        L_0x00d5:
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r5 = r4.next     // Catch:{ all -> 0x00d9 }
            r4 = r5
            goto L_0x00cc
        L_0x00d9:
            r0 = move-exception
            r32 = r1
            r19 = r10
            r20 = r12
            r28 = r15
            goto L_0x022b
        L_0x00e4:
            if (r0 != 0) goto L_0x00e9
            r4 = r3
            r5 = 0
            goto L_0x00eb
        L_0x00e9:
            r5 = r3
            r4 = 0
        L_0x00eb:
            r16 = r2
            r29 = r16
            r16 = r0
            r0 = r29
        L_0x00f3:
            if (r0 == r3) goto L_0x0132
            r32 = r1
            int r1 = r0.hash     // Catch:{ all -> 0x0129 }
            r17 = r3
            K r3 = r0.key     // Catch:{ all -> 0x0129 }
            r19 = r10
            V r10 = r0.val     // Catch:{ all -> 0x0122 }
            r20 = r1 & r9
            if (r20 != 0) goto L_0x010e
            r20 = r12
            j$.util.concurrent.ConcurrentHashMap$Node r12 = new j$.util.concurrent.ConcurrentHashMap$Node     // Catch:{ all -> 0x014a }
            r12.<init>(r1, r3, r10, r4)     // Catch:{ all -> 0x014a }
            r4 = r12
            goto L_0x0116
        L_0x010e:
            r20 = r12
            j$.util.concurrent.ConcurrentHashMap$Node r12 = new j$.util.concurrent.ConcurrentHashMap$Node     // Catch:{ all -> 0x014a }
            r12.<init>(r1, r3, r10, r5)     // Catch:{ all -> 0x014a }
            r5 = r12
        L_0x0116:
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r1 = r0.next     // Catch:{ all -> 0x014a }
            r0 = r1
            r1 = r32
            r3 = r17
            r10 = r19
            r12 = r20
            goto L_0x00f3
        L_0x0122:
            r0 = move-exception
            r20 = r12
            r28 = r15
            goto L_0x022b
        L_0x0129:
            r0 = move-exception
            r19 = r10
            r20 = r12
            r28 = r15
            goto L_0x022b
        L_0x0132:
            r32 = r1
            r17 = r3
            r19 = r10
            r20 = r12
            setTabAt(r11, r6, r4)     // Catch:{ all -> 0x014a }
            int r0 = r6 + r9
            setTabAt(r11, r0, r5)     // Catch:{ all -> 0x014a }
            setTabAt(r8, r6, r13)     // Catch:{ all -> 0x014a }
            r14 = 1
            r28 = r15
            goto L_0x0218
        L_0x014a:
            r0 = move-exception
            r28 = r15
            goto L_0x022b
        L_0x014f:
            r0 = move-exception
            r32 = r1
            r19 = r10
            r20 = r12
            r28 = r15
            goto L_0x022b
        L_0x015a:
            r32 = r1
            r19 = r10
            r20 = r12
            boolean r0 = r2 instanceof j$.util.concurrent.ConcurrentHashMap.TreeBin     // Catch:{ all -> 0x0206 }
            if (r0 == 0) goto L_0x0201
            r0 = r2
            j$.util.concurrent.ConcurrentHashMap$TreeBin r0 = (j$.util.concurrent.ConcurrentHashMap.TreeBin) r0     // Catch:{ all -> 0x0206 }
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r10 = 0
            r12 = 0
            r16 = r1
            j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r1 = r0.first     // Catch:{ all -> 0x0206 }
            r29 = r16
            r16 = r0
            r0 = r12
            r12 = r10
            r10 = r5
            r5 = r4
            r4 = r3
            r3 = r29
        L_0x017c:
            if (r1 == 0) goto L_0x01c7
            r17 = r14
            int r14 = r1.hash     // Catch:{ all -> 0x01c0 }
            j$.util.concurrent.ConcurrentHashMap$TreeNode r27 = new j$.util.concurrent.ConcurrentHashMap$TreeNode     // Catch:{ all -> 0x01c0 }
            K r7 = r1.key     // Catch:{ all -> 0x01c0 }
            r28 = r15
            V r15 = r1.val     // Catch:{ all -> 0x01d3 }
            r25 = 0
            r26 = 0
            r21 = r27
            r22 = r14
            r23 = r7
            r24 = r15
            r21.<init>(r22, r23, r24, r25, r26)     // Catch:{ all -> 0x01d3 }
            r7 = r27
            r15 = r14 & r9
            if (r15 != 0) goto L_0x01ab
            r7.prev = r4     // Catch:{ all -> 0x01d3 }
            if (r4 != 0) goto L_0x01a5
            r3 = r7
            goto L_0x01a7
        L_0x01a5:
            r4.next = r7     // Catch:{ all -> 0x01d3 }
        L_0x01a7:
            r4 = r7
            int r12 = r12 + 1
            goto L_0x01b6
        L_0x01ab:
            r7.prev = r10     // Catch:{ all -> 0x01d3 }
            if (r10 != 0) goto L_0x01b1
            r5 = r7
            goto L_0x01b3
        L_0x01b1:
            r10.next = r7     // Catch:{ all -> 0x01d3 }
        L_0x01b3:
            r10 = r7
            int r0 = r0 + 1
        L_0x01b6:
            j$.util.concurrent.ConcurrentHashMap$Node<K, V> r7 = r1.next     // Catch:{ all -> 0x01d3 }
            r1 = r7
            r7 = r30
            r14 = r17
            r15 = r28
            goto L_0x017c
        L_0x01c0:
            r0 = move-exception
            r28 = r15
            r14 = r17
            goto L_0x022b
        L_0x01c7:
            r17 = r14
            r28 = r15
            r1 = 6
            if (r12 > r1) goto L_0x01d7
            j$.util.concurrent.ConcurrentHashMap$Node r7 = untreeify(r3)     // Catch:{ all -> 0x01d3 }
            goto L_0x01e1
        L_0x01d3:
            r0 = move-exception
            r14 = r17
            goto L_0x022b
        L_0x01d7:
            if (r0 == 0) goto L_0x01df
            j$.util.concurrent.ConcurrentHashMap$TreeBin r7 = new j$.util.concurrent.ConcurrentHashMap$TreeBin     // Catch:{ all -> 0x01d3 }
            r7.<init>(r3)     // Catch:{ all -> 0x01d3 }
            goto L_0x01e1
        L_0x01df:
            r7 = r16
        L_0x01e1:
            if (r0 > r1) goto L_0x01e9
            j$.util.concurrent.ConcurrentHashMap$Node r1 = untreeify(r5)     // Catch:{ all -> 0x01d3 }
            goto L_0x01f3
        L_0x01e9:
            if (r12 == 0) goto L_0x01f1
            j$.util.concurrent.ConcurrentHashMap$TreeBin r1 = new j$.util.concurrent.ConcurrentHashMap$TreeBin     // Catch:{ all -> 0x01d3 }
            r1.<init>(r5)     // Catch:{ all -> 0x01d3 }
            goto L_0x01f3
        L_0x01f1:
            r1 = r16
        L_0x01f3:
            setTabAt(r11, r6, r7)     // Catch:{ all -> 0x01d3 }
            int r14 = r6 + r9
            setTabAt(r11, r14, r1)     // Catch:{ all -> 0x01d3 }
            setTabAt(r8, r6, r13)     // Catch:{ all -> 0x01d3 }
            r14 = 1
            goto L_0x0218
        L_0x0201:
            r17 = r14
            r28 = r15
            goto L_0x0216
        L_0x0206:
            r0 = move-exception
            r17 = r14
            r28 = r15
            goto L_0x022b
        L_0x020c:
            r32 = r1
            r19 = r10
            r20 = r12
            r17 = r14
            r28 = r15
        L_0x0216:
            r14 = r17
        L_0x0218:
            monitor-exit(r2)     // Catch:{ all -> 0x021e }
            r7 = r30
            r15 = r28
            goto L_0x0270
        L_0x021e:
            r0 = move-exception
            goto L_0x022b
        L_0x0220:
            r0 = move-exception
            r32 = r1
            r19 = r10
            r20 = r12
            r17 = r14
            r28 = r15
        L_0x022b:
            monitor-exit(r2)     // Catch:{ all -> 0x021e }
            throw r0
        L_0x022d:
            r19 = r10
            r20 = r12
            r17 = r14
            r28 = r15
        L_0x0235:
            if (r28 == 0) goto L_0x0245
            r7 = r30
            r7.nextTable = r0
            r7.table = r11
            int r0 = r9 << 1
            int r1 = r9 >>> 1
            int r0 = r0 - r1
            r7.sizeCtl = r0
            return
        L_0x0245:
            r7 = r30
            sun.misc.Unsafe r1 = U
            long r3 = SIZECTL
            int r5 = r7.sizeCtl
            r0 = r5
            int r10 = r0 + -1
            r2 = r30
            r12 = r6
            r6 = r10
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x026a
            int r1 = r0 + -2
            int r2 = resizeStamp(r9)
            int r3 = RESIZE_STAMP_SHIFT
            int r2 = r2 << r3
            if (r1 == r2) goto L_0x0266
            return
        L_0x0266:
            r14 = 1
            r15 = 1
            r6 = r9
            goto L_0x026f
        L_0x026a:
            r6 = r12
            r14 = r17
            r15 = r28
        L_0x026f:
        L_0x0270:
            r5 = r18
            r10 = r19
            r12 = r20
            goto L_0x0041
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.transfer(j$.util.concurrent.ConcurrentHashMap$Node[], j$.util.concurrent.ConcurrentHashMap$Node[]):void");
    }

    static final class CounterCell {
        volatile long value;

        CounterCell(long x) {
            this.value = x;
        }
    }

    /* access modifiers changed from: package-private */
    public final long sumCount() {
        CounterCell[] as = this.counterCells;
        long sum = this.baseCount;
        if (as != null) {
            for (CounterCell counterCell : as) {
                CounterCell a = counterCell;
                if (counterCell != null) {
                    sum += a.value;
                }
            }
        }
        return sum;
    }

    /* JADX INFO: finally extract failed */
    private final void fullAddCount(long x, boolean wasUncontended) {
        boolean wasUncontended2;
        CounterCell a;
        long j = x;
        int probe = ThreadLocalRandom.getProbe();
        int h = probe;
        if (probe == 0) {
            ThreadLocalRandom.localInit();
            h = ThreadLocalRandom.getProbe();
            wasUncontended2 = true;
        } else {
            wasUncontended2 = wasUncontended;
        }
        boolean wasUncontended3 = wasUncontended2;
        int h2 = h;
        boolean collide = false;
        while (true) {
            CounterCell[] counterCellArr = this.counterCells;
            CounterCell[] as = counterCellArr;
            if (counterCellArr != null) {
                int length = as.length;
                int n = length;
                if (length > 0) {
                    CounterCell counterCell = as[(n - 1) & h2];
                    CounterCell a2 = counterCell;
                    if (counterCell == null) {
                        if (this.cellsBusy == 0) {
                            CounterCell r = new CounterCell(j);
                            if (this.cellsBusy == 0) {
                                a = a2;
                                if (U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                                    boolean created = false;
                                    try {
                                        CounterCell[] counterCellArr2 = this.counterCells;
                                        CounterCell[] rs = counterCellArr2;
                                        if (counterCellArr2 != null) {
                                            int length2 = rs.length;
                                            int m = length2;
                                            if (length2 > 0) {
                                                int i = (m - 1) & h2;
                                                int j2 = i;
                                                if (rs[i] == null) {
                                                    rs[j2] = r;
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
                                a = a2;
                            }
                        } else {
                            a = a2;
                        }
                        collide = false;
                        CounterCell counterCell2 = a;
                    } else {
                        CounterCell a3 = a2;
                        if (!wasUncontended3) {
                            wasUncontended3 = true;
                            CounterCell counterCell3 = a3;
                        } else {
                            Unsafe unsafe = U;
                            long j3 = CELLVALUE;
                            CounterCell a4 = a3;
                            long v = a4.value;
                            if (!unsafe.compareAndSwapLong(a4, j3, v, v + j)) {
                                if (this.counterCells != as) {
                                } else if (n >= NCPU) {
                                    CounterCell counterCell4 = a4;
                                } else if (!collide) {
                                    collide = true;
                                    CounterCell counterCell5 = a4;
                                } else if (this.cellsBusy == 0) {
                                    CounterCell counterCell6 = a4;
                                    if (unsafe.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                                        try {
                                            if (this.counterCells == as) {
                                                CounterCell[] rs2 = new CounterCell[(n << 1)];
                                                for (int i2 = 0; i2 < n; i2++) {
                                                    rs2[i2] = as[i2];
                                                }
                                                this.counterCells = rs2;
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
                    h2 = ThreadLocalRandom.advanceProbe(h2);
                }
            }
            if (this.cellsBusy == 0 && this.counterCells == as) {
                if (U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                    boolean init = false;
                    try {
                        if (this.counterCells == as) {
                            CounterCell[] rs3 = new CounterCell[2];
                            rs3[h2 & 1] = new CounterCell(j);
                            this.counterCells = rs3;
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
            Unsafe unsafe2 = U;
            long j4 = BASECOUNT;
            long v2 = this.baseCount;
            if (unsafe2.compareAndSwapLong(this, j4, v2, v2 + j)) {
                return;
            }
        }
    }

    private final void treeifyBin(Node<K, V>[] tab, int index) {
        if (tab != null) {
            int length = tab.length;
            int n = length;
            if (length < 64) {
                tryPresize(n << 1);
                return;
            }
            ConcurrentHashMap.Node<K, V> tabAt = tabAt(tab, index);
            ConcurrentHashMap.Node<K, V> b = tabAt;
            if (tabAt != null && b.hash >= 0) {
                synchronized (b) {
                    if (tabAt(tab, index) == b) {
                        ConcurrentHashMap.TreeNode<K, V> hd = null;
                        ConcurrentHashMap.TreeNode<K, V> tl = null;
                        for (ConcurrentHashMap.Node<K, V> e = b; e != null; e = e.next) {
                            ConcurrentHashMap.TreeNode<K, V> p = new TreeNode<>(e.hash, e.key, e.val, (Node) null, (TreeNode) null);
                            p.prev = tl;
                            if (tl == null) {
                                hd = p;
                            } else {
                                tl.next = p;
                            }
                            tl = p;
                        }
                        setTabAt(tab, index, new TreeBin(hd));
                    }
                }
            }
        }
    }

    static <K, V> Node<K, V> untreeify(Node<K, V> b) {
        ConcurrentHashMap.Node<K, V> hd = null;
        ConcurrentHashMap.Node<K, V> tl = null;
        for (Node<K, V> node = b; node != null; node = node.next) {
            ConcurrentHashMap.Node<K, V> p = new Node<>(node.hash, node.key, node.val, (Node) null);
            if (tl == null) {
                hd = p;
            } else {
                tl.next = p;
            }
            tl = p;
        }
        return hd;
    }

    static final class TreeNode<K, V> extends Node<K, V> {
        TreeNode<K, V> left;
        TreeNode<K, V> parent;
        TreeNode<K, V> prev;
        boolean red;
        TreeNode<K, V> right;

        TreeNode(int hash, K key, V val, Node<K, V> next, TreeNode<K, V> parent2) {
            super(hash, key, val, next);
            this.parent = parent2;
        }

        /* access modifiers changed from: package-private */
        public Node<K, V> find(int h, Object k) {
            return findTreeNode(h, k, (Class<?>) null);
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x002f, code lost:
            if (r3 != null) goto L_0x0031;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final j$.util.concurrent.ConcurrentHashMap.TreeNode<K, V> findTreeNode(int r8, java.lang.Object r9, java.lang.Class<?> r10) {
            /*
                r7 = this;
                if (r9 == 0) goto L_0x004c
                r0 = r7
            L_0x0003:
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r1 = r0.left
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r2 = r0.right
                int r3 = r0.hash
                r4 = r3
                if (r3 <= r8) goto L_0x000e
                r0 = r1
                goto L_0x0048
            L_0x000e:
                if (r4 >= r8) goto L_0x0012
                r0 = r2
                goto L_0x0048
            L_0x0012:
                java.lang.Object r3 = r0.key
                r5 = r3
                if (r3 == r9) goto L_0x004b
                if (r5 == 0) goto L_0x0020
                boolean r3 = r9.equals(r5)
                if (r3 == 0) goto L_0x0020
                goto L_0x004b
            L_0x0020:
                if (r1 != 0) goto L_0x0024
                r0 = r2
                goto L_0x0048
            L_0x0024:
                if (r2 != 0) goto L_0x0028
                r0 = r1
                goto L_0x0048
            L_0x0028:
                if (r10 != 0) goto L_0x0031
                java.lang.Class r3 = j$.util.concurrent.ConcurrentHashMap.comparableClassFor(r9)
                r10 = r3
                if (r3 == 0) goto L_0x003f
            L_0x0031:
                int r3 = j$.util.concurrent.ConcurrentHashMap.compareComparables(r10, r9, r5)
                r6 = r3
                if (r3 == 0) goto L_0x003f
                if (r6 >= 0) goto L_0x003c
                r3 = r1
                goto L_0x003d
            L_0x003c:
                r3 = r2
            L_0x003d:
                r0 = r3
                goto L_0x0048
            L_0x003f:
                j$.util.concurrent.ConcurrentHashMap$TreeNode r3 = r2.findTreeNode(r8, r9, r10)
                r6 = r3
                if (r3 == 0) goto L_0x0047
                return r6
            L_0x0047:
                r0 = r1
            L_0x0048:
                if (r0 != 0) goto L_0x0003
                goto L_0x004c
            L_0x004b:
                return r0
            L_0x004c:
                r0 = 0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.TreeNode.findTreeNode(int, java.lang.Object, java.lang.Class):j$.util.concurrent.ConcurrentHashMap$TreeNode");
        }
    }

    static final class TreeBin<K, V> extends Node<K, V> {
        static final /* synthetic */ boolean $assertionsDisabled = true;
        private static final long LOCKSTATE;
        static final int READER = 4;
        private static final Unsafe U;
        static final int WAITER = 2;
        static final int WRITER = 1;
        volatile TreeNode<K, V> first;
        volatile int lockState;
        TreeNode<K, V> root;
        volatile Thread waiter;

        static {
            try {
                Unsafe unsafe = DesugarUnsafe.getUnsafe();
                U = unsafe;
                LOCKSTATE = unsafe.objectFieldOffset(TreeBin.class.getDeclaredField("lockState"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        static int tieBreakOrder(Object a, Object b) {
            if (!(a == null || b == null)) {
                int compareTo = a.getClass().getName().compareTo(b.getClass().getName());
                int d = compareTo;
                if (compareTo != 0) {
                    return d;
                }
            }
            return System.identityHashCode(a) <= System.identityHashCode(b) ? -1 : 1;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0036, code lost:
            if (r9 != null) goto L_0x0038;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        TreeBin(j$.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r14) {
            /*
                r13 = this;
                r0 = -2
                r1 = 0
                r13.<init>(r0, r1, r1, r1)
                r13.first = r14
                r0 = 0
                r2 = r14
            L_0x0009:
                if (r2 == 0) goto L_0x0061
                j$.util.concurrent.ConcurrentHashMap$Node r3 = r2.next
                j$.util.concurrent.ConcurrentHashMap$TreeNode r3 = (j$.util.concurrent.ConcurrentHashMap.TreeNode) r3
                r2.right = r1
                r2.left = r1
                if (r0 != 0) goto L_0x001c
                r2.parent = r1
                r4 = 0
                r2.red = r4
                r0 = r2
                goto L_0x005e
            L_0x001c:
                java.lang.Object r4 = r2.key
                int r5 = r2.hash
                r6 = 0
                r7 = r0
            L_0x0022:
                java.lang.Object r8 = r7.key
                int r9 = r7.hash
                r10 = r9
                if (r9 <= r5) goto L_0x002b
                r9 = -1
                goto L_0x0045
            L_0x002b:
                if (r10 >= r5) goto L_0x002f
                r9 = 1
                goto L_0x0045
            L_0x002f:
                if (r6 != 0) goto L_0x0038
                java.lang.Class r9 = j$.util.concurrent.ConcurrentHashMap.comparableClassFor(r4)
                r6 = r9
                if (r9 == 0) goto L_0x003f
            L_0x0038:
                int r9 = j$.util.concurrent.ConcurrentHashMap.compareComparables(r6, r4, r8)
                r11 = r9
                if (r9 != 0) goto L_0x0044
            L_0x003f:
                int r9 = tieBreakOrder(r4, r8)
                goto L_0x0045
            L_0x0044:
                r9 = r11
            L_0x0045:
                r11 = r7
                if (r9 > 0) goto L_0x004b
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r12 = r7.left
                goto L_0x004d
            L_0x004b:
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r12 = r7.right
            L_0x004d:
                r7 = r12
                if (r12 != 0) goto L_0x0060
                r2.parent = r11
                if (r9 > 0) goto L_0x0057
                r11.left = r2
                goto L_0x0059
            L_0x0057:
                r11.right = r2
            L_0x0059:
                j$.util.concurrent.ConcurrentHashMap$TreeNode r0 = balanceInsertion(r0, r2)
            L_0x005e:
                r2 = r3
                goto L_0x0009
            L_0x0060:
                goto L_0x0022
            L_0x0061:
                r13.root = r0
                boolean r1 = $assertionsDisabled
                if (r1 != 0) goto L_0x0074
                boolean r1 = checkInvariants(r0)
                if (r1 == 0) goto L_0x006e
                goto L_0x0074
            L_0x006e:
                java.lang.AssertionError r1 = new java.lang.AssertionError
                r1.<init>()
                throw r1
            L_0x0074:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.TreeBin.<init>(j$.util.concurrent.ConcurrentHashMap$TreeNode):void");
        }

        private final void lockRoot() {
            if (!U.compareAndSwapInt(this, LOCKSTATE, 0, 1)) {
                contendedLock();
            }
        }

        private final void unlockRoot() {
            this.lockState = 0;
        }

        private final void contendedLock() {
            boolean waiting = false;
            while (true) {
                int i = this.lockState;
                int s = i;
                if ((i & -3) == 0) {
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, 1)) {
                        break;
                    }
                } else if ((s & 2) == 0) {
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, s | 2)) {
                        waiting = true;
                        this.waiter = Thread.currentThread();
                    }
                } else if (waiting) {
                    LockSupport.park(this);
                }
            }
            if (waiting) {
                this.waiter = null;
            }
        }

        /* access modifiers changed from: package-private */
        public final Node<K, V> find(int h, Object k) {
            ConcurrentHashMap.TreeNode<K, V> p = null;
            if (k != null) {
                ConcurrentHashMap.Node<K, V> e = this.first;
                while (e != null) {
                    int i = this.lockState;
                    int s = i;
                    if ((i & 3) != 0) {
                        if (e.hash == h) {
                            K k2 = e.key;
                            K ek = k2;
                            if (k2 == k || (ek != null && k.equals(ek))) {
                                return e;
                            }
                        }
                        e = e.next;
                    } else {
                        Unsafe unsafe = U;
                        long j = LOCKSTATE;
                        if (unsafe.compareAndSwapInt(this, j, s, s + 4)) {
                            try {
                                TreeNode<K, V> treeNode = this.root;
                                TreeNode<K, V> treeNode2 = treeNode;
                                if (treeNode != null) {
                                    p = treeNode2.findTreeNode(h, k, (Class<?>) null);
                                }
                                if (DesugarUnsafe.getAndAddInt(unsafe, this, j, -4) == 6) {
                                    Thread thread = this.waiter;
                                    Thread w = thread;
                                    if (thread != null) {
                                        LockSupport.unpark(w);
                                    }
                                }
                                return p;
                            } catch (Throwable p2) {
                                if (DesugarUnsafe.getAndAddInt(U, this, LOCKSTATE, -4) == 6) {
                                    Thread thread2 = this.waiter;
                                    Thread w2 = thread2;
                                    if (thread2 != null) {
                                        LockSupport.unpark(w2);
                                    }
                                }
                                throw p2;
                            }
                        }
                    }
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x004a, code lost:
            if (r2 != null) goto L_0x004c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x006e, code lost:
            return r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:51:0x00c0, code lost:
            if ($assertionsDisabled != false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x00c8, code lost:
            if (checkInvariants(r1.root) == false) goto L_0x00cb;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x00d0, code lost:
            throw new java.lang.AssertionError();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:?, code lost:
            return null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:?, code lost:
            return null;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final j$.util.concurrent.ConcurrentHashMap.TreeNode<K, V> putTreeVal(int r17, K r18, V r19) {
            /*
                r16 = this;
                r1 = r16
                r8 = r17
                r9 = r18
                r0 = 0
                r2 = 0
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r3 = r1.root
                r10 = r2
                r11 = r3
            L_0x000c:
                if (r11 != 0) goto L_0x0022
                j$.util.concurrent.ConcurrentHashMap$TreeNode r12 = new j$.util.concurrent.ConcurrentHashMap$TreeNode
                r6 = 0
                r7 = 0
                r2 = r12
                r3 = r17
                r4 = r18
                r5 = r19
                r2.<init>(r3, r4, r5, r6, r7)
                r1.root = r12
                r1.first = r12
                goto L_0x00be
            L_0x0022:
                int r2 = r11.hash
                r12 = r2
                if (r2 <= r8) goto L_0x002d
                r2 = -1
                r13 = r2
                r14 = r10
                r10 = r0
                goto L_0x007b
            L_0x002d:
                if (r12 >= r8) goto L_0x0034
                r2 = 1
                r13 = r2
                r14 = r10
                r10 = r0
                goto L_0x007b
            L_0x0034:
                java.lang.Object r2 = r11.key
                r3 = r2
                if (r2 == r9) goto L_0x00de
                if (r3 == 0) goto L_0x0043
                boolean r2 = r9.equals(r3)
                if (r2 == 0) goto L_0x0043
                goto L_0x00de
            L_0x0043:
                if (r0 != 0) goto L_0x004c
                java.lang.Class r2 = j$.util.concurrent.ConcurrentHashMap.comparableClassFor(r18)
                r0 = r2
                if (r2 == 0) goto L_0x0053
            L_0x004c:
                int r2 = j$.util.concurrent.ConcurrentHashMap.compareComparables(r0, r9, r3)
                r4 = r2
                if (r2 != 0) goto L_0x0077
            L_0x0053:
                if (r10 != 0) goto L_0x006f
                r10 = 1
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r2 = r11.left
                r4 = r2
                if (r2 == 0) goto L_0x0062
                j$.util.concurrent.ConcurrentHashMap$TreeNode r2 = r4.findTreeNode(r8, r9, r0)
                r5 = r2
                if (r2 != 0) goto L_0x006e
            L_0x0062:
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r2 = r11.right
                r4 = r2
                if (r2 == 0) goto L_0x006f
                j$.util.concurrent.ConcurrentHashMap$TreeNode r2 = r4.findTreeNode(r8, r9, r0)
                r5 = r2
                if (r2 == 0) goto L_0x006f
            L_0x006e:
                return r5
            L_0x006f:
                int r2 = tieBreakOrder(r9, r3)
                r13 = r2
                r14 = r10
                r10 = r0
                goto L_0x007b
            L_0x0077:
                r2 = r4
                r13 = r2
                r14 = r10
                r10 = r0
            L_0x007b:
                r15 = r11
                if (r13 > 0) goto L_0x0081
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r0 = r11.left
                goto L_0x0083
            L_0x0081:
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r0 = r11.right
            L_0x0083:
                r11 = r0
                if (r0 != 0) goto L_0x00d8
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r7 = r1.first
                j$.util.concurrent.ConcurrentHashMap$TreeNode r0 = new j$.util.concurrent.ConcurrentHashMap$TreeNode
                r2 = r0
                r3 = r17
                r4 = r18
                r5 = r19
                r6 = r7
                r8 = r7
                r7 = r15
                r2.<init>(r3, r4, r5, r6, r7)
                r1.first = r0
                if (r8 == 0) goto L_0x009d
                r8.prev = r2
            L_0x009d:
                if (r13 > 0) goto L_0x00a2
                r15.left = r2
                goto L_0x00a4
            L_0x00a2:
                r15.right = r2
            L_0x00a4:
                boolean r0 = r15.red
                if (r0 != 0) goto L_0x00ac
                r0 = 1
                r2.red = r0
                goto L_0x00bc
            L_0x00ac:
                r16.lockRoot()
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r0 = r1.root     // Catch:{ all -> 0x00d3 }
                j$.util.concurrent.ConcurrentHashMap$TreeNode r0 = balanceInsertion(r0, r2)     // Catch:{ all -> 0x00d3 }
                r1.root = r0     // Catch:{ all -> 0x00d3 }
                r16.unlockRoot()
            L_0x00bc:
                r0 = r10
                r10 = r14
            L_0x00be:
                boolean r2 = $assertionsDisabled
                if (r2 != 0) goto L_0x00d1
                j$.util.concurrent.ConcurrentHashMap$TreeNode<K, V> r2 = r1.root
                boolean r2 = checkInvariants(r2)
                if (r2 == 0) goto L_0x00cb
                goto L_0x00d1
            L_0x00cb:
                java.lang.AssertionError r2 = new java.lang.AssertionError
                r2.<init>()
                throw r2
            L_0x00d1:
                r2 = 0
                return r2
            L_0x00d3:
                r0 = move-exception
                r16.unlockRoot()
                throw r0
            L_0x00d8:
                r8 = r17
                r0 = r10
                r10 = r14
                goto L_0x000c
            L_0x00de:
                return r11
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.TreeBin.putTreeVal(int, java.lang.Object, java.lang.Object):j$.util.concurrent.ConcurrentHashMap$TreeNode");
        }

        /* JADX INFO: finally extract failed */
        /* access modifiers changed from: package-private */
        public final boolean removeTreeNode(TreeNode<K, V> p) {
            TreeNode<K, V> treeNode;
            ConcurrentHashMap.TreeNode<K, V> next = (TreeNode) p.next;
            ConcurrentHashMap.TreeNode<K, V> pred = p.prev;
            if (pred == null) {
                this.first = next;
            } else {
                pred.next = next;
            }
            if (next != null) {
                next.prev = pred;
            }
            if (this.first == null) {
                this.root = null;
                return true;
            }
            TreeNode<K, V> treeNode2 = this.root;
            TreeNode<K, V> treeNode3 = treeNode2;
            if (!(treeNode2 == null || treeNode3.right == null)) {
                ConcurrentHashMap.TreeNode<K, V> treeNode4 = treeNode3.left;
                ConcurrentHashMap.TreeNode<K, V> rl = treeNode4;
                if (!(treeNode4 == null || rl.left == null)) {
                    lockRoot();
                    try {
                        TreeNode<K, V> treeNode5 = p.left;
                        TreeNode<K, V> treeNode6 = p.right;
                        if (treeNode5 != null && treeNode6 != null) {
                            TreeNode<K, V> treeNode7 = treeNode6;
                            while (true) {
                                TreeNode<K, V> treeNode8 = treeNode7.left;
                                TreeNode<K, V> treeNode9 = treeNode8;
                                if (treeNode8 == null) {
                                    break;
                                }
                                treeNode7 = treeNode9;
                            }
                            boolean c = treeNode7.red;
                            treeNode7.red = p.red;
                            p.red = c;
                            TreeNode<K, V> treeNode10 = treeNode7.right;
                            ConcurrentHashMap.TreeNode<K, V> pp = p.parent;
                            if (treeNode7 == treeNode6) {
                                p.parent = treeNode7;
                                treeNode7.right = p;
                            } else {
                                ConcurrentHashMap.TreeNode<K, V> sp = treeNode7.parent;
                                p.parent = sp;
                                if (sp != null) {
                                    if (treeNode7 == sp.left) {
                                        sp.left = p;
                                    } else {
                                        sp.right = p;
                                    }
                                }
                                treeNode7.right = treeNode6;
                                if (treeNode6 != null) {
                                    treeNode6.parent = treeNode7;
                                }
                            }
                            p.left = null;
                            p.right = treeNode10;
                            if (treeNode10 != null) {
                                treeNode10.parent = p;
                            }
                            treeNode7.left = treeNode5;
                            if (treeNode5 != null) {
                                treeNode5.parent = treeNode7;
                            }
                            treeNode7.parent = pp;
                            if (pp == null) {
                                treeNode3 = treeNode7;
                            } else if (p == pp.left) {
                                pp.left = treeNode7;
                            } else {
                                pp.right = treeNode7;
                            }
                            if (treeNode10 != null) {
                                treeNode = treeNode10;
                            } else {
                                treeNode = p;
                            }
                        } else if (treeNode5 != null) {
                            treeNode = treeNode5;
                        } else if (treeNode6 != null) {
                            treeNode = treeNode6;
                        } else {
                            treeNode = p;
                        }
                        if (treeNode != p) {
                            ConcurrentHashMap.TreeNode<K, V> pp2 = p.parent;
                            treeNode.parent = pp2;
                            if (pp2 == null) {
                                treeNode3 = treeNode;
                            } else if (p == pp2.left) {
                                pp2.left = treeNode;
                            } else {
                                pp2.right = treeNode;
                            }
                            p.parent = null;
                            p.right = null;
                            p.left = null;
                        }
                        this.root = p.red ? treeNode3 : balanceDeletion(treeNode3, treeNode);
                        if (p == treeNode) {
                            ConcurrentHashMap.TreeNode<K, V> treeNode11 = p.parent;
                            ConcurrentHashMap.TreeNode<K, V> pp3 = treeNode11;
                            if (treeNode11 != null) {
                                if (p == pp3.left) {
                                    pp3.left = null;
                                } else if (p == pp3.right) {
                                    pp3.right = null;
                                }
                                p.parent = null;
                            }
                        }
                        unlockRoot();
                        if ($assertionsDisabled || checkInvariants(this.root)) {
                            return false;
                        }
                        throw new AssertionError();
                    } catch (Throwable th) {
                        unlockRoot();
                        throw th;
                    }
                }
            }
            return true;
        }

        static <K, V> TreeNode<K, V> rotateLeft(ConcurrentHashMap.TreeNode<K, V> root2, TreeNode<K, V> p) {
            if (p != null) {
                ConcurrentHashMap.TreeNode<K, V> treeNode = p.right;
                ConcurrentHashMap.TreeNode<K, V> r = treeNode;
                if (treeNode != null) {
                    ConcurrentHashMap.TreeNode<K, V> treeNode2 = r.left;
                    p.right = treeNode2;
                    ConcurrentHashMap.TreeNode<K, V> rl = treeNode2;
                    if (treeNode2 != null) {
                        rl.parent = p;
                    }
                    ConcurrentHashMap.TreeNode<K, V> treeNode3 = p.parent;
                    r.parent = treeNode3;
                    ConcurrentHashMap.TreeNode<K, V> pp = treeNode3;
                    if (treeNode3 == null) {
                        root2 = r;
                        r.red = false;
                    } else if (pp.left == p) {
                        pp.left = r;
                    } else {
                        pp.right = r;
                    }
                    r.left = p;
                    p.parent = r;
                }
            }
            return root2;
        }

        static <K, V> TreeNode<K, V> rotateRight(ConcurrentHashMap.TreeNode<K, V> root2, TreeNode<K, V> p) {
            if (p != null) {
                ConcurrentHashMap.TreeNode<K, V> treeNode = p.left;
                ConcurrentHashMap.TreeNode<K, V> l = treeNode;
                if (treeNode != null) {
                    ConcurrentHashMap.TreeNode<K, V> treeNode2 = l.right;
                    p.left = treeNode2;
                    ConcurrentHashMap.TreeNode<K, V> lr = treeNode2;
                    if (treeNode2 != null) {
                        lr.parent = p;
                    }
                    ConcurrentHashMap.TreeNode<K, V> treeNode3 = p.parent;
                    l.parent = treeNode3;
                    ConcurrentHashMap.TreeNode<K, V> pp = treeNode3;
                    if (treeNode3 == null) {
                        root2 = l;
                        l.red = false;
                    } else if (pp.right == p) {
                        pp.right = l;
                    } else {
                        pp.left = l;
                    }
                    l.right = p;
                    p.parent = l;
                }
            }
            return root2;
        }

        static <K, V> TreeNode<K, V> balanceInsertion(ConcurrentHashMap.TreeNode<K, V> root2, ConcurrentHashMap.TreeNode<K, V> x) {
            x.red = true;
            while (true) {
                ConcurrentHashMap.TreeNode<K, V> treeNode = x.parent;
                ConcurrentHashMap.TreeNode<K, V> xp = treeNode;
                if (treeNode != null) {
                    if (!xp.red) {
                        break;
                    }
                    ConcurrentHashMap.TreeNode<K, V> treeNode2 = xp.parent;
                    ConcurrentHashMap.TreeNode<K, V> xpp = treeNode2;
                    if (treeNode2 == null) {
                        break;
                    }
                    ConcurrentHashMap.TreeNode<K, V> treeNode3 = xpp.left;
                    ConcurrentHashMap.TreeNode<K, V> xppl = treeNode3;
                    ConcurrentHashMap.TreeNode<K, V> treeNode4 = null;
                    if (xp == treeNode3) {
                        ConcurrentHashMap.TreeNode<K, V> treeNode5 = xpp.right;
                        ConcurrentHashMap.TreeNode<K, V> xppr = treeNode5;
                        if (treeNode5 == null || !xppr.red) {
                            if (x == xp.right) {
                                x = xp;
                                root2 = rotateLeft(root2, xp);
                                ConcurrentHashMap.TreeNode<K, V> treeNode6 = x.parent;
                                xp = treeNode6;
                                if (treeNode6 != null) {
                                    treeNode4 = xp.parent;
                                }
                                xpp = treeNode4;
                            }
                            if (xp != null) {
                                xp.red = false;
                                if (xpp != null) {
                                    xpp.red = true;
                                    root2 = rotateRight(root2, xpp);
                                }
                            }
                        } else {
                            xppr.red = false;
                            xp.red = false;
                            xpp.red = true;
                            x = xpp;
                        }
                    } else if (xppl == null || !xppl.red) {
                        if (x == xp.left) {
                            x = xp;
                            root2 = rotateRight(root2, xp);
                            ConcurrentHashMap.TreeNode<K, V> treeNode7 = x.parent;
                            xp = treeNode7;
                            if (treeNode7 != null) {
                                treeNode4 = xp.parent;
                            }
                            xpp = treeNode4;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root2 = rotateLeft(root2, xpp);
                            }
                        }
                    } else {
                        xppl.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                } else {
                    x.red = false;
                    return x;
                }
            }
            return root2;
        }

        static <K, V> TreeNode<K, V> balanceDeletion(ConcurrentHashMap.TreeNode<K, V> root2, ConcurrentHashMap.TreeNode<K, V> x) {
            while (x != null && x != root2) {
                ConcurrentHashMap.TreeNode<K, V> treeNode = x.parent;
                ConcurrentHashMap.TreeNode<K, V> xp = treeNode;
                if (treeNode == null) {
                    x.red = false;
                    return x;
                } else if (x.red) {
                    x.red = false;
                    return root2;
                } else {
                    ConcurrentHashMap.TreeNode<K, V> treeNode2 = xp.left;
                    ConcurrentHashMap.TreeNode<K, V> xpl = treeNode2;
                    ConcurrentHashMap.TreeNode<K, V> treeNode3 = null;
                    if (treeNode2 == x) {
                        ConcurrentHashMap.TreeNode<K, V> treeNode4 = xp.right;
                        ConcurrentHashMap.TreeNode<K, V> xpr = treeNode4;
                        if (treeNode4 != null && xpr.red) {
                            xpr.red = false;
                            xp.red = true;
                            root2 = rotateLeft(root2, xp);
                            ConcurrentHashMap.TreeNode<K, V> treeNode5 = x.parent;
                            xp = treeNode5;
                            xpr = treeNode5 == null ? null : xp.right;
                        }
                        if (xpr == null) {
                            x = xp;
                        } else {
                            ConcurrentHashMap.TreeNode<K, V> sl = xpr.left;
                            ConcurrentHashMap.TreeNode<K, V> sr = xpr.right;
                            if ((sr == null || !sr.red) && (sl == null || !sl.red)) {
                                xpr.red = true;
                                x = xp;
                            } else {
                                if (sr == null || !sr.red) {
                                    if (sl != null) {
                                        sl.red = false;
                                    }
                                    xpr.red = true;
                                    root2 = rotateRight(root2, xpr);
                                    ConcurrentHashMap.TreeNode<K, V> treeNode6 = x.parent;
                                    xp = treeNode6;
                                    if (treeNode6 != null) {
                                        treeNode3 = xp.right;
                                    }
                                    xpr = treeNode3;
                                }
                                if (xpr != null) {
                                    xpr.red = xp == null ? false : xp.red;
                                    ConcurrentHashMap.TreeNode<K, V> treeNode7 = xpr.right;
                                    ConcurrentHashMap.TreeNode<K, V> sr2 = treeNode7;
                                    if (treeNode7 != null) {
                                        sr2.red = false;
                                    }
                                }
                                if (xp != null) {
                                    xp.red = false;
                                    root2 = rotateLeft(root2, xp);
                                }
                                x = root2;
                            }
                        }
                    } else {
                        if (xpl != null && xpl.red) {
                            xpl.red = false;
                            xp.red = true;
                            root2 = rotateRight(root2, xp);
                            ConcurrentHashMap.TreeNode<K, V> treeNode8 = x.parent;
                            xp = treeNode8;
                            xpl = treeNode8 == null ? null : xp.left;
                        }
                        if (xpl == null) {
                            x = xp;
                        } else {
                            ConcurrentHashMap.TreeNode<K, V> sl2 = xpl.left;
                            ConcurrentHashMap.TreeNode<K, V> sr3 = xpl.right;
                            if ((sl2 == null || !sl2.red) && (sr3 == null || !sr3.red)) {
                                xpl.red = true;
                                x = xp;
                            } else {
                                if (sl2 == null || !sl2.red) {
                                    if (sr3 != null) {
                                        sr3.red = false;
                                    }
                                    xpl.red = true;
                                    root2 = rotateLeft(root2, xpl);
                                    ConcurrentHashMap.TreeNode<K, V> treeNode9 = x.parent;
                                    xp = treeNode9;
                                    if (treeNode9 != null) {
                                        treeNode3 = xp.left;
                                    }
                                    xpl = treeNode3;
                                }
                                if (xpl != null) {
                                    xpl.red = xp == null ? false : xp.red;
                                    ConcurrentHashMap.TreeNode<K, V> treeNode10 = xpl.left;
                                    ConcurrentHashMap.TreeNode<K, V> sl3 = treeNode10;
                                    if (treeNode10 != null) {
                                        sl3.red = false;
                                    }
                                }
                                if (xp != null) {
                                    xp.red = false;
                                    root2 = rotateRight(root2, xp);
                                }
                                x = root2;
                            }
                        }
                    }
                }
            }
            return root2;
        }

        static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
            ConcurrentHashMap.TreeNode<K, V> tp = t.parent;
            ConcurrentHashMap.TreeNode<K, V> tl = t.left;
            ConcurrentHashMap.TreeNode<K, V> tr = t.right;
            ConcurrentHashMap.TreeNode<K, V> tb = t.prev;
            ConcurrentHashMap.TreeNode<K, V> tn = (TreeNode) t.next;
            if (tb != null && tb.next != t) {
                return false;
            }
            if (tn != null && tn.prev != t) {
                return false;
            }
            if (tp != null && t != tp.left && t != tp.right) {
                return false;
            }
            if (tl != null && (tl.parent != t || tl.hash > t.hash)) {
                return false;
            }
            if (tr != null && (tr.parent != t || tr.hash < t.hash)) {
                return false;
            }
            if (t.red && tl != null && tl.red && tr != null && tr.red) {
                return false;
            }
            if (tl != null && !checkInvariants(tl)) {
                return false;
            }
            if (tr == null || checkInvariants(tr)) {
                return true;
            }
            return false;
        }
    }

    static final class TableStack<K, V> {
        int index;
        int length;
        TableStack<K, V> next;
        Node<K, V>[] tab;

        TableStack() {
        }
    }

    static class Traverser<K, V> {
        int baseIndex;
        int baseLimit;
        final int baseSize;
        int index;
        Node<K, V> next = null;
        TableStack<K, V> spare;
        TableStack<K, V> stack;
        Node<K, V>[] tab;

        Traverser(Node<K, V>[] tab2, int size, int index2, int limit) {
            this.tab = tab2;
            this.baseSize = size;
            this.index = index2;
            this.baseIndex = index2;
            this.baseLimit = limit;
        }

        /* access modifiers changed from: package-private */
        public final Node<K, V> advance() {
            TreeNode<K, V> treeNode;
            Node<K, V> node = this.next;
            Node<K, V> node2 = node;
            if (node != null) {
                node2 = node2.next;
            }
            while (treeNode == null) {
                if (this.baseIndex < this.baseLimit) {
                    ConcurrentHashMap.Node<K, V>[] nodeArr = this.tab;
                    ConcurrentHashMap.Node<K, V>[] t = nodeArr;
                    if (nodeArr != null) {
                        int length = t.length;
                        int n = length;
                        int i = this.index;
                        int i2 = i;
                        if (length > i && i2 >= 0) {
                            Node<K, V> tabAt = ConcurrentHashMap.tabAt(t, i2);
                            treeNode = tabAt;
                            if (tabAt != null && treeNode.hash < 0) {
                                if (treeNode instanceof ForwardingNode) {
                                    this.tab = ((ForwardingNode) treeNode).nextTable;
                                    treeNode = null;
                                    pushState(t, i2, n);
                                } else {
                                    treeNode = treeNode instanceof TreeBin ? ((TreeBin) treeNode).first : null;
                                }
                            }
                            if (this.stack != null) {
                                recoverState(n);
                            } else {
                                int i3 = this.baseSize + i2;
                                this.index = i3;
                                if (i3 >= n) {
                                    int i4 = this.baseIndex + 1;
                                    this.baseIndex = i4;
                                    this.index = i4;
                                }
                            }
                        }
                    }
                }
                this.next = null;
                return null;
            }
            this.next = treeNode;
            return treeNode;
        }

        private void pushState(Node<K, V>[] t, int i, int n) {
            ConcurrentHashMap.TableStack<K, V> s = this.spare;
            if (s != null) {
                this.spare = s.next;
            } else {
                s = new TableStack<>();
            }
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = this.stack;
            this.stack = s;
        }

        private void recoverState(int n) {
            ConcurrentHashMap.TableStack<K, V> s;
            while (true) {
                ConcurrentHashMap.TableStack<K, V> tableStack = this.stack;
                s = tableStack;
                if (tableStack == null) {
                    break;
                }
                int i = this.index;
                int i2 = s.length;
                int len = i2;
                int i3 = i + i2;
                this.index = i3;
                if (i3 < n) {
                    break;
                }
                n = len;
                this.index = s.index;
                this.tab = s.tab;
                s.tab = null;
                ConcurrentHashMap.TableStack<K, V> next2 = s.next;
                s.next = this.spare;
                this.stack = next2;
                this.spare = s;
            }
            if (s == null) {
                int i4 = this.index + this.baseSize;
                this.index = i4;
                if (i4 >= n) {
                    int i5 = this.baseIndex + 1;
                    this.baseIndex = i5;
                    this.index = i5;
                }
            }
        }
    }

    static class BaseIterator<K, V> extends Traverser<K, V> {
        Node<K, V> lastReturned;
        final ConcurrentHashMap<K, V> map;

        BaseIterator(Node<K, V>[] tab, int size, int index, int limit, ConcurrentHashMap<K, V> concurrentHashMap) {
            super(tab, size, index, limit);
            this.map = concurrentHashMap;
            advance();
        }

        public final boolean hasNext() {
            return this.next != null;
        }

        public final boolean hasMoreElements() {
            return this.next != null;
        }

        public final void remove() {
            ConcurrentHashMap.Node<K, V> node = this.lastReturned;
            ConcurrentHashMap.Node<K, V> p = node;
            if (node != null) {
                this.lastReturned = null;
                this.map.replaceNode(p.key, null, (Object) null);
                return;
            }
            throw new IllegalStateException();
        }
    }

    static final class KeyIterator<K, V> extends BaseIterator<K, V> implements Iterator<K>, Enumeration<K>, j$.util.Iterator<K> {
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            forEachRemaining(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
        }

        KeyIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMap<K, V> concurrentHashMap) {
            super(tab, index, size, limit, concurrentHashMap);
        }

        public final K next() {
            ConcurrentHashMap.Node<K, V> node = this.next;
            ConcurrentHashMap.Node<K, V> p = node;
            if (node != null) {
                K k = p.key;
                this.lastReturned = p;
                advance();
                return k;
            }
            throw new NoSuchElementException();
        }

        public final K nextElement() {
            return next();
        }
    }

    static final class ValueIterator<K, V> extends BaseIterator<K, V> implements java.util.Iterator<V>, Enumeration<V>, j$.util.Iterator<V> {
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            forEachRemaining(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
        }

        ValueIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMap<K, V> concurrentHashMap) {
            super(tab, index, size, limit, concurrentHashMap);
        }

        public final V next() {
            ConcurrentHashMap.Node<K, V> node = this.next;
            ConcurrentHashMap.Node<K, V> p = node;
            if (node != null) {
                V v = p.val;
                this.lastReturned = p;
                advance();
                return v;
            }
            throw new NoSuchElementException();
        }

        public final V nextElement() {
            return next();
        }
    }

    static final class EntryIterator<K, V> extends BaseIterator<K, V> implements java.util.Iterator<Map.Entry<K, V>>, j$.util.Iterator<Map.Entry<K, V>> {
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            forEachRemaining(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
        }

        EntryIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMap<K, V> concurrentHashMap) {
            super(tab, index, size, limit, concurrentHashMap);
        }

        public final Map.Entry<K, V> next() {
            ConcurrentHashMap.Node<K, V> node = this.next;
            ConcurrentHashMap.Node<K, V> p = node;
            if (node != null) {
                K k = p.key;
                V v = p.val;
                this.lastReturned = p;
                advance();
                return new MapEntry(k, v, this.map);
            }
            throw new NoSuchElementException();
        }
    }

    static final class MapEntry<K, V> implements Map.Entry<K, V> {
        final K key;
        final ConcurrentHashMap<K, V> map;
        V val;

        MapEntry(K key2, V val2, ConcurrentHashMap<K, V> concurrentHashMap) {
            this.key = key2;
            this.val = val2;
            this.map = concurrentHashMap;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.val;
        }

        public int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }

        public String toString() {
            return this.key + "=" + this.val;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0020, code lost:
            r0 = r4.val;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0016, code lost:
            r0 = r4.key;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r5) {
            /*
                r4 = this;
                boolean r0 = r5 instanceof java.util.Map.Entry
                if (r0 == 0) goto L_0x002c
                r0 = r5
                java.util.Map$Entry r0 = (java.util.Map.Entry) r0
                r1 = r0
                java.lang.Object r0 = r0.getKey()
                r2 = r0
                if (r0 == 0) goto L_0x002c
                java.lang.Object r0 = r1.getValue()
                r3 = r0
                if (r0 == 0) goto L_0x002c
                K r0 = r4.key
                if (r2 == r0) goto L_0x0020
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x002c
            L_0x0020:
                V r0 = r4.val
                if (r3 == r0) goto L_0x002a
                boolean r0 = r3.equals(r0)
                if (r0 == 0) goto L_0x002c
            L_0x002a:
                r0 = 1
                goto L_0x002d
            L_0x002c:
                r0 = 0
            L_0x002d:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.MapEntry.equals(java.lang.Object):boolean");
        }

        public V setValue(V value) {
            if (value != null) {
                V v = this.val;
                this.val = value;
                this.map.put(this.key, value);
                return v;
            }
            throw new NullPointerException();
        }
    }

    static final class KeySpliterator<K, V> extends Traverser<K, V> implements Spliterator<K> {
        long est;

        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        KeySpliterator(Node<K, V>[] tab, int size, int index, int limit, long est2) {
            super(tab, size, index, limit);
            this.est = est2;
        }

        public Spliterator<K> trySplit() {
            int i = this.baseIndex;
            int i2 = i;
            int i3 = this.baseLimit;
            int f = i3;
            int i4 = (i + i3) >>> 1;
            int h = i4;
            if (i4 <= i2) {
                return null;
            }
            Node[] nodeArr = this.tab;
            int i5 = this.baseSize;
            this.baseLimit = h;
            long j = this.est >>> 1;
            this.est = j;
            return new KeySpliterator(nodeArr, i5, h, f, j);
        }

        public void forEachRemaining(Consumer<? super K> consumer) {
            if (consumer != null) {
                while (true) {
                    ConcurrentHashMap.Node<K, V> advance = advance();
                    ConcurrentHashMap.Node<K, V> p = advance;
                    if (advance != null) {
                        consumer.accept(p.key);
                    } else {
                        return;
                    }
                }
            } else {
                throw new NullPointerException();
            }
        }

        public boolean tryAdvance(Consumer<? super K> consumer) {
            if (consumer != null) {
                ConcurrentHashMap.Node<K, V> advance = advance();
                ConcurrentHashMap.Node<K, V> p = advance;
                if (advance == null) {
                    return false;
                }
                consumer.accept(p.key);
                return true;
            }
            throw new NullPointerException();
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return 4353;
        }
    }

    static final class ValueSpliterator<K, V> extends Traverser<K, V> implements Spliterator<V> {
        long est;

        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        ValueSpliterator(Node<K, V>[] tab, int size, int index, int limit, long est2) {
            super(tab, size, index, limit);
            this.est = est2;
        }

        public Spliterator<V> trySplit() {
            int i = this.baseIndex;
            int i2 = i;
            int i3 = this.baseLimit;
            int f = i3;
            int i4 = (i + i3) >>> 1;
            int h = i4;
            if (i4 <= i2) {
                return null;
            }
            Node[] nodeArr = this.tab;
            int i5 = this.baseSize;
            this.baseLimit = h;
            long j = this.est >>> 1;
            this.est = j;
            return new ValueSpliterator(nodeArr, i5, h, f, j);
        }

        public void forEachRemaining(Consumer<? super V> consumer) {
            if (consumer != null) {
                while (true) {
                    ConcurrentHashMap.Node<K, V> advance = advance();
                    ConcurrentHashMap.Node<K, V> p = advance;
                    if (advance != null) {
                        consumer.accept(p.val);
                    } else {
                        return;
                    }
                }
            } else {
                throw new NullPointerException();
            }
        }

        public boolean tryAdvance(Consumer<? super V> consumer) {
            if (consumer != null) {
                ConcurrentHashMap.Node<K, V> advance = advance();
                ConcurrentHashMap.Node<K, V> p = advance;
                if (advance == null) {
                    return false;
                }
                consumer.accept(p.val);
                return true;
            }
            throw new NullPointerException();
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return 4352;
        }
    }

    static final class EntrySpliterator<K, V> extends Traverser<K, V> implements Spliterator<Map.Entry<K, V>> {
        long est;
        final ConcurrentHashMap<K, V> map;

        public /* synthetic */ Comparator getComparator() {
            return Spliterator.CC.$default$getComparator(this);
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return Spliterator.CC.$default$getExactSizeIfKnown(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i) {
            return Spliterator.CC.$default$hasCharacteristics(this, i);
        }

        EntrySpliterator(Node<K, V>[] tab, int size, int index, int limit, long est2, ConcurrentHashMap<K, V> concurrentHashMap) {
            super(tab, size, index, limit);
            this.map = concurrentHashMap;
            this.est = est2;
        }

        public Spliterator<Map.Entry<K, V>> trySplit() {
            int i = this.baseIndex;
            int i2 = i;
            int i3 = this.baseLimit;
            int f = i3;
            int i4 = (i + i3) >>> 1;
            int h = i4;
            if (i4 <= i2) {
                return null;
            }
            Node[] nodeArr = this.tab;
            int i5 = this.baseSize;
            this.baseLimit = h;
            long j = this.est >>> 1;
            this.est = j;
            return new EntrySpliterator(nodeArr, i5, h, f, j, this.map);
        }

        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> consumer) {
            if (consumer != null) {
                while (true) {
                    ConcurrentHashMap.Node<K, V> advance = advance();
                    ConcurrentHashMap.Node<K, V> p = advance;
                    if (advance != null) {
                        consumer.accept(new MapEntry(p.key, p.val, this.map));
                    } else {
                        return;
                    }
                }
            } else {
                throw new NullPointerException();
            }
        }

        public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> consumer) {
            if (consumer != null) {
                ConcurrentHashMap.Node<K, V> advance = advance();
                ConcurrentHashMap.Node<K, V> p = advance;
                if (advance == null) {
                    return false;
                }
                consumer.accept(new MapEntry(p.key, p.val, this.map));
                return true;
            }
            throw new NullPointerException();
        }

        public long estimateSize() {
            return this.est;
        }

        public int characteristics() {
            return 4353;
        }
    }

    static abstract class CollectionView<K, V, E> implements Collection<E>, Serializable {
        private static final String oomeMsg = "Required array size too large";
        private static final long serialVersionUID = 7249069246763182397L;
        final ConcurrentHashMap<K, V> map;

        public abstract boolean contains(Object obj);

        public abstract java.util.Iterator<E> iterator();

        public abstract boolean remove(Object obj);

        CollectionView(ConcurrentHashMap<K, V> concurrentHashMap) {
            this.map = concurrentHashMap;
        }

        public ConcurrentHashMap<K, V> getMap() {
            return this.map;
        }

        public final void clear() {
            this.map.clear();
        }

        public final int size() {
            return this.map.size();
        }

        public final boolean isEmpty() {
            return this.map.isEmpty();
        }

        public final Object[] toArray() {
            long sz = this.map.mappingCount();
            if (sz <= NUM) {
                int n = (int) sz;
                Object[] r = new Object[n];
                int i = 0;
                java.util.Iterator it = iterator();
                while (it.hasNext()) {
                    E e = it.next();
                    if (i == n) {
                        if (n < NUM) {
                            if (n >= NUM) {
                                n = NUM;
                            } else {
                                n += (n >>> 1) + 1;
                            }
                            r = Arrays.copyOf(r, n);
                        } else {
                            throw new OutOfMemoryError("Required array size too large");
                        }
                    }
                    r[i] = e;
                    i++;
                }
                return i == n ? r : Arrays.copyOf(r, i);
            }
            throw new OutOfMemoryError("Required array size too large");
        }

        public final <T> T[] toArray(T[] a) {
            T[] r;
            long sz = this.map.mappingCount();
            if (sz <= NUM) {
                int m = (int) sz;
                if (a.length >= m) {
                    r = a;
                } else {
                    r = (Object[]) Array.newInstance(a.getClass().getComponentType(), m);
                }
                int n = r.length;
                int i = 0;
                java.util.Iterator it = iterator();
                while (it.hasNext()) {
                    E e = it.next();
                    if (i == n) {
                        if (n < NUM) {
                            if (n >= NUM) {
                                n = NUM;
                            } else {
                                n += (n >>> 1) + 1;
                            }
                            r = Arrays.copyOf(r, n);
                        } else {
                            throw new OutOfMemoryError("Required array size too large");
                        }
                    }
                    r[i] = e;
                    i++;
                }
                if (a != r || i >= n) {
                    return i == n ? r : Arrays.copyOf(r, i);
                }
                r[i] = null;
                return r;
            }
            throw new OutOfMemoryError("Required array size too large");
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            java.util.Iterator<E> it = iterator();
            if (it.hasNext()) {
                while (true) {
                    Object e = it.next();
                    sb.append(e == this ? "(this Collection)" : e);
                    if (!it.hasNext()) {
                        break;
                    }
                    sb.append(',');
                    sb.append(' ');
                }
            }
            sb.append(']');
            return sb.toString();
        }

        /* JADX WARNING: Removed duplicated region for block: B:4:0x000c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean containsAll(java.util.Collection<?> r4) {
            /*
                r3 = this;
                if (r4 == r3) goto L_0x001c
                java.util.Iterator r0 = r4.iterator()
            L_0x0006:
                boolean r1 = r0.hasNext()
                if (r1 == 0) goto L_0x001c
                java.lang.Object r1 = r0.next()
                if (r1 == 0) goto L_0x001a
                boolean r2 = r3.contains(r1)
                if (r2 != 0) goto L_0x0019
                goto L_0x001a
            L_0x0019:
                goto L_0x0006
            L_0x001a:
                r0 = 0
                return r0
            L_0x001c:
                r0 = 1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.CollectionView.containsAll(java.util.Collection):boolean");
        }

        public final boolean removeAll(Collection<?> c) {
            if (c != null) {
                boolean modified = false;
                java.util.Iterator<E> it = iterator();
                while (it.hasNext()) {
                    if (c.contains(it.next())) {
                        it.remove();
                        modified = true;
                    }
                }
                return modified;
            }
            throw new NullPointerException();
        }

        public final boolean retainAll(Collection<?> c) {
            if (c != null) {
                boolean modified = false;
                java.util.Iterator<E> it = iterator();
                while (it.hasNext()) {
                    if (!c.contains(it.next())) {
                        it.remove();
                        modified = true;
                    }
                }
                return modified;
            }
            throw new NullPointerException();
        }
    }

    public static class KeySetView<K, V> extends CollectionView<K, V, K> implements Set<K>, Serializable, j$.util.Set<K> {
        private static final long serialVersionUID = 7249069246763182397L;
        private final V value;

        public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
            forEach(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
        }

        public /* synthetic */ boolean removeIf(Predicate predicate) {
            return Collection.CC.$default$removeIf(this, predicate);
        }

        public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
            return removeIf(C$r8$wrapper$java$util$function$Predicate$VWRP.convert(predicate));
        }

        public /* synthetic */ Object[] toArray(IntFunction intFunction) {
            return Collection.CC.$default$toArray(this, intFunction);
        }

        public /* synthetic */ Object[] toArray(java.util.function.IntFunction intFunction) {
            return toArray(C$r8$wrapper$java$util$function$IntFunction$VWRP.convert(intFunction));
        }

        public /* bridge */ /* synthetic */ ConcurrentHashMap getMap() {
            return super.getMap();
        }

        KeySetView(ConcurrentHashMap<K, V> concurrentHashMap, V value2) {
            super(concurrentHashMap);
            this.value = value2;
        }

        public V getMappedValue() {
            return this.value;
        }

        public boolean contains(Object o) {
            return this.map.containsKey(o);
        }

        public boolean remove(Object o) {
            return this.map.remove(o) != null;
        }

        public java.util.Iterator<K> iterator() {
            ConcurrentHashMap concurrentHashMap = this.map;
            ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            int f = nodeArr == null ? 0 : t.length;
            return new KeyIterator(t, f, 0, f, concurrentHashMap);
        }

        public boolean add(K e) {
            V v = this.value;
            V v2 = v;
            if (v != null) {
                return this.map.putVal(e, v2, true) == null;
            }
            throw new UnsupportedOperationException();
        }

        public boolean addAll(java.util.Collection<? extends K> c) {
            boolean added = false;
            V v = this.value;
            V v2 = v;
            if (v != null) {
                for (K e : c) {
                    if (this.map.putVal(e, v2, true) == null) {
                        added = true;
                    }
                }
                return added;
            }
            throw new UnsupportedOperationException();
        }

        public int hashCode() {
            int h = 0;
            java.util.Iterator it = iterator();
            while (it.hasNext()) {
                h += it.next().hashCode();
            }
            return h;
        }

        public boolean equals(Object o) {
            if (o instanceof Set) {
                Set set = (Set) o;
                Set set2 = set;
                if (set == this || (containsAll(set2) && set2.containsAll(this))) {
                    return true;
                }
            }
            return false;
        }

        public Spliterator<K> spliterator() {
            ConcurrentHashMap concurrentHashMap = this.map;
            long n = concurrentHashMap.sumCount();
            ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            int f = nodeArr == null ? 0 : t.length;
            return new KeySpliterator(t, f, 0, f, n < 0 ? 0 : n);
        }

        public void forEach(Consumer<? super K> consumer) {
            if (consumer != null) {
                ConcurrentHashMap.Node<K, V>[] nodeArr = this.map.table;
                ConcurrentHashMap.Node<K, V>[] t = nodeArr;
                if (nodeArr != null) {
                    ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, t.length, 0, t.length);
                    while (true) {
                        ConcurrentHashMap.Node<K, V> advance = it.advance();
                        ConcurrentHashMap.Node<K, V> p = advance;
                        if (advance != null) {
                            consumer.accept(p.key);
                        } else {
                            return;
                        }
                    }
                }
            } else {
                throw new NullPointerException();
            }
        }
    }

    static final class ValuesView<K, V> extends CollectionView<K, V, V> implements java.util.Collection<V>, Serializable, j$.util.Collection<V> {
        private static final long serialVersionUID = 2249069246763182397L;

        public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
            forEach(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
        }

        public /* synthetic */ boolean removeIf(Predicate predicate) {
            return Collection.CC.$default$removeIf(this, predicate);
        }

        public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
            return removeIf(C$r8$wrapper$java$util$function$Predicate$VWRP.convert(predicate));
        }

        public /* synthetic */ Object[] toArray(IntFunction intFunction) {
            return Collection.CC.$default$toArray(this, intFunction);
        }

        public /* synthetic */ Object[] toArray(java.util.function.IntFunction intFunction) {
            return toArray(C$r8$wrapper$java$util$function$IntFunction$VWRP.convert(intFunction));
        }

        ValuesView(ConcurrentHashMap<K, V> concurrentHashMap) {
            super(concurrentHashMap);
        }

        public final boolean contains(Object o) {
            return this.map.containsValue(o);
        }

        public final boolean remove(Object o) {
            if (o == null) {
                return false;
            }
            java.util.Iterator<V> it = iterator();
            while (it.hasNext()) {
                if (o.equals(it.next())) {
                    it.remove();
                    return true;
                }
            }
            return false;
        }

        public final java.util.Iterator<V> iterator() {
            ConcurrentHashMap concurrentHashMap = this.map;
            ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            int f = nodeArr == null ? 0 : t.length;
            return new ValueIterator(t, f, 0, f, concurrentHashMap);
        }

        public final boolean add(V v) {
            throw new UnsupportedOperationException();
        }

        public final boolean addAll(java.util.Collection<? extends V> collection) {
            throw new UnsupportedOperationException();
        }

        public Spliterator<V> spliterator() {
            ConcurrentHashMap concurrentHashMap = this.map;
            long n = concurrentHashMap.sumCount();
            ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            int f = nodeArr == null ? 0 : t.length;
            return new ValueSpliterator(t, f, 0, f, n < 0 ? 0 : n);
        }

        public void forEach(Consumer<? super V> consumer) {
            if (consumer != null) {
                ConcurrentHashMap.Node<K, V>[] nodeArr = this.map.table;
                ConcurrentHashMap.Node<K, V>[] t = nodeArr;
                if (nodeArr != null) {
                    ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, t.length, 0, t.length);
                    while (true) {
                        ConcurrentHashMap.Node<K, V> advance = it.advance();
                        ConcurrentHashMap.Node<K, V> p = advance;
                        if (advance != null) {
                            consumer.accept(p.val);
                        } else {
                            return;
                        }
                    }
                }
            } else {
                throw new NullPointerException();
            }
        }
    }

    static final class EntrySetView<K, V> extends CollectionView<K, V, Map.Entry<K, V>> implements Set<Map.Entry<K, V>>, Serializable, j$.util.Set<Map.Entry<K, V>> {
        private static final long serialVersionUID = 2249069246763182397L;

        public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
            forEach(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
        }

        public /* synthetic */ boolean removeIf(Predicate predicate) {
            return Collection.CC.$default$removeIf(this, predicate);
        }

        public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
            return removeIf(C$r8$wrapper$java$util$function$Predicate$VWRP.convert(predicate));
        }

        public /* synthetic */ Object[] toArray(IntFunction intFunction) {
            return Collection.CC.$default$toArray(this, intFunction);
        }

        public /* synthetic */ Object[] toArray(java.util.function.IntFunction intFunction) {
            return toArray(C$r8$wrapper$java$util$function$IntFunction$VWRP.convert(intFunction));
        }

        EntrySetView(ConcurrentHashMap<K, V> concurrentHashMap) {
            super(concurrentHashMap);
        }

        public boolean contains(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry) o;
                Map.Entry<?, ?> e = entry;
                Object key = entry.getKey();
                Object k = key;
                if (key != null) {
                    Object obj = this.map.get(k);
                    Object r = obj;
                    if (obj != null) {
                        Object value = e.getValue();
                        Object v = value;
                        return value != null && (v == r || v.equals(r));
                    }
                }
            }
        }

        public boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry) o;
                Map.Entry<?, ?> e = entry;
                Object key = entry.getKey();
                Object k = key;
                if (key != null) {
                    Object v = e.getValue();
                    return v != null && this.map.remove(k, v);
                }
            }
        }

        public java.util.Iterator<Map.Entry<K, V>> iterator() {
            ConcurrentHashMap concurrentHashMap = this.map;
            ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            int f = nodeArr == null ? 0 : t.length;
            return new EntryIterator(t, f, 0, f, concurrentHashMap);
        }

        public boolean add(Map.Entry<K, V> e) {
            return this.map.putVal(e.getKey(), e.getValue(), false) == null;
        }

        public boolean addAll(java.util.Collection<? extends Map.Entry<K, V>> c) {
            boolean added = false;
            for (Map.Entry<K, V> e : c) {
                if (add(e)) {
                    added = true;
                }
            }
            return added;
        }

        public final int hashCode() {
            int h = 0;
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.map.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            if (nodeArr != null) {
                ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, t.length, 0, t.length);
                while (true) {
                    Node advance = it.advance();
                    Node node = advance;
                    if (advance == null) {
                        break;
                    }
                    h += node.hashCode();
                }
            }
            return h;
        }

        public final boolean equals(Object o) {
            if (o instanceof Set) {
                Set set = (Set) o;
                Set set2 = set;
                if (set == this || (containsAll(set2) && set2.containsAll(this))) {
                    return true;
                }
            }
            return false;
        }

        public Spliterator<Map.Entry<K, V>> spliterator() {
            ConcurrentHashMap concurrentHashMap = this.map;
            long n = concurrentHashMap.sumCount();
            ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.table;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            int f = nodeArr == null ? 0 : t.length;
            return new EntrySpliterator(t, f, 0, f, n < 0 ? 0 : n, concurrentHashMap);
        }

        public void forEach(Consumer<? super Map.Entry<K, V>> consumer) {
            if (consumer != null) {
                ConcurrentHashMap.Node<K, V>[] nodeArr = this.map.table;
                ConcurrentHashMap.Node<K, V>[] t = nodeArr;
                if (nodeArr != null) {
                    ConcurrentHashMap.Traverser<K, V> it = new Traverser<>(t, t.length, 0, t.length);
                    while (true) {
                        ConcurrentHashMap.Node<K, V> advance = it.advance();
                        ConcurrentHashMap.Node<K, V> p = advance;
                        if (advance != null) {
                            consumer.accept(new MapEntry(p.key, p.val, this.map));
                        } else {
                            return;
                        }
                    }
                }
            } else {
                throw new NullPointerException();
            }
        }
    }
}

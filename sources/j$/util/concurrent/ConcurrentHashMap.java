package j$.util.concurrent;

import j$.util.AbstractCLASSNAMEa;
import j$.util.InterfaceCLASSNAMEb;
import j$.util.Iterator;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.wrappers.CLASSNAMEh;
import j$.wrappers.CLASSNAMEq;
import j$.wrappers.CLASSNAMEs;
import j$.wrappers.CLASSNAMEw;
import j$.wrappers.M;
import j$.wrappers.P0;
import j$.wrappers.x0;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import sun.misc.Unsafe;
/* loaded from: classes2.dex */
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable, j$.util.concurrent.b {
    static final int g = Runtime.getRuntime().availableProcessors();
    private static final Unsafe h;
    private static final long i;
    private static final long j;
    private static final long k;
    private static final long l;
    private static final long m;
    private static final long n;
    private static final int o;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = 7249069246763182397L;
    volatile transient l[] a;
    private volatile transient l[] b;
    private volatile transient long baseCount;
    private volatile transient c[] c;
    private volatile transient int cellsBusy;
    private transient i d;
    private transient u e;
    private transient e f;
    private volatile transient int sizeCtl;
    private volatile transient int transferIndex;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class a extends p {
        final ConcurrentHashMap i;
        l j;

        a(l[] lVarArr, int i, int i2, int i3, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i, i2, i3);
            this.i = concurrentHashMap;
            a();
        }

        public final boolean hasMoreElements() {
            return this.b != null;
        }

        public final boolean hasNext() {
            return this.b != null;
        }

        public final void remove() {
            l lVar = this.j;
            if (lVar != null) {
                this.j = null;
                this.i.i(lVar.b, null, null);
                return;
            }
            throw new IllegalStateException();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class c {
        volatile long value;

        c(long j) {
            this.value = j;
        }
    }

    /* loaded from: classes2.dex */
    static final class d extends a implements Iterator, j$.util.Iterator {
        d(l[] lVarArr, int i, int i2, int i3, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i, i2, i3, concurrentHashMap);
        }

        @Override // j$.util.Iterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        @Override // java.util.Iterator
        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, CLASSNAMEw.b(consumer));
        }

        @Override // java.util.Iterator, j$.util.Iterator
        /* renamed from: next */
        public Object mo307next() {
            l lVar = this.b;
            if (lVar != null) {
                Object obj = lVar.b;
                Object obj2 = lVar.c;
                this.j = lVar;
                a();
                return new k(obj, obj2, this.i);
            }
            throw new NoSuchElementException();
        }
    }

    /* loaded from: classes2.dex */
    static final class e extends b implements Set, InterfaceCLASSNAMEb {
        e(ConcurrentHashMap concurrentHashMap) {
            super(concurrentHashMap);
        }

        @Override // java.util.Collection, java.util.Set
        /* renamed from: a */
        public boolean add(Map.Entry entry) {
            return this.a.h(entry.getKey(), entry.getValue(), false) == null;
        }

        @Override // java.util.Collection, java.util.Set
        public boolean addAll(Collection collection) {
            java.util.Iterator it = collection.iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (add((Map.Entry) it.next())) {
                    z = true;
                }
            }
            return z;
        }

        @Override // j$.util.concurrent.ConcurrentHashMap.b, java.util.Collection
        public boolean contains(Object obj) {
            Map.Entry entry;
            Object key;
            Object obj2;
            Object value;
            return (!(obj instanceof Map.Entry) || (key = (entry = (Map.Entry) obj).getKey()) == null || (obj2 = this.a.get(key)) == null || (value = entry.getValue()) == null || (value != obj2 && !value.equals(obj2))) ? false : true;
        }

        @Override // java.util.Collection, java.util.Set
        public final boolean equals(Object obj) {
            Set set;
            return (obj instanceof Set) && ((set = (Set) obj) == this || (containsAll(set) && set.containsAll(this)));
        }

        @Override // j$.util.InterfaceCLASSNAMEb, j$.lang.e
        public void forEach(Consumer consumer) {
            consumer.getClass();
            l[] lVarArr = this.a.a;
            if (lVarArr != null) {
                p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
                while (true) {
                    l a = pVar.a();
                    if (a == null) {
                        return;
                    }
                    consumer.accept(new k(a.b, a.c, this.a));
                }
            }
        }

        @Override // java.lang.Iterable
        public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
            forEach(CLASSNAMEw.b(consumer));
        }

        @Override // java.util.Collection, java.util.Set
        public final int hashCode() {
            l[] lVarArr = this.a.a;
            int i = 0;
            if (lVarArr != null) {
                p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
                while (true) {
                    l a = pVar.a();
                    if (a == null) {
                        break;
                    }
                    i += a.hashCode();
                }
            }
            return i;
        }

        @Override // j$.util.concurrent.ConcurrentHashMap.b, java.util.Collection, java.lang.Iterable
        public java.util.Iterator iterator() {
            ConcurrentHashMap concurrentHashMap = this.a;
            l[] lVarArr = concurrentHashMap.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            return new d(lVarArr, length, 0, length, concurrentHashMap);
        }

        @Override // j$.util.InterfaceCLASSNAMEb
        public /* synthetic */ boolean k(Predicate predicate) {
            return AbstractCLASSNAMEa.h(this, predicate);
        }

        @Override // java.util.Collection
        public /* synthetic */ Stream parallelStream() {
            return P0.n0(AbstractCLASSNAMEa.g(this));
        }

        @Override // java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            Map.Entry entry;
            Object key;
            Object value;
            return (obj instanceof Map.Entry) && (key = (entry = (Map.Entry) obj).getKey()) != null && (value = entry.getValue()) != null && this.a.remove(key, value);
        }

        @Override // java.util.Collection
        public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
            return AbstractCLASSNAMEa.h(this, x0.a(predicate));
        }

        @Override // java.util.Collection, java.lang.Iterable, java.util.Set, j$.util.InterfaceCLASSNAMEb, j$.lang.e
        /* renamed from: spliterator */
        public j$.util.u mo289spliterator() {
            ConcurrentHashMap concurrentHashMap = this.a;
            long m = concurrentHashMap.m();
            l[] lVarArr = concurrentHashMap.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            long j = 0;
            if (m >= 0) {
                j = m;
            }
            return new f(lVarArr, length, 0, length, j, concurrentHashMap);
        }

        @Override // java.util.Collection, java.lang.Iterable, java.util.Set, j$.util.InterfaceCLASSNAMEb, j$.lang.e
        /* renamed from: spliterator  reason: collision with other method in class */
        public /* synthetic */ Spliterator mo289spliterator() {
            return CLASSNAMEh.a(mo289spliterator());
        }

        @Override // java.util.Collection, j$.util.InterfaceCLASSNAMEb
        /* renamed from: stream */
        public /* synthetic */ j$.util.stream.Stream mo238stream() {
            return AbstractCLASSNAMEa.i(this);
        }

        @Override // java.util.Collection, j$.util.InterfaceCLASSNAMEb
        /* renamed from: stream  reason: collision with other method in class */
        public /* synthetic */ Stream mo238stream() {
            return P0.n0(AbstractCLASSNAMEa.i(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class f extends p implements j$.util.u {
        final ConcurrentHashMap i;
        long j;

        f(l[] lVarArr, int i, int i2, int i3, long j, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i, i2, i3);
            this.i = concurrentHashMap;
            this.j = j;
        }

        @Override // j$.util.u
        public boolean b(Consumer consumer) {
            consumer.getClass();
            l a = a();
            if (a == null) {
                return false;
            }
            consumer.accept(new k(a.b, a.c, this.i));
            return true;
        }

        @Override // j$.util.u
        public int characteristics() {
            return 4353;
        }

        @Override // j$.util.u
        public long estimateSize() {
            return this.j;
        }

        @Override // j$.util.u
        public void forEachRemaining(Consumer consumer) {
            consumer.getClass();
            while (true) {
                l a = a();
                if (a != null) {
                    consumer.accept(new k(a.b, a.c, this.i));
                } else {
                    return;
                }
            }
        }

        @Override // j$.util.u
        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        @Override // j$.util.u
        public /* synthetic */ long getExactSizeIfKnown() {
            return AbstractCLASSNAMEa.e(this);
        }

        @Override // j$.util.u
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return AbstractCLASSNAMEa.f(this, i);
        }

        @Override // j$.util.u
        /* renamed from: trySplit */
        public j$.util.u mo322trySplit() {
            int i = this.f;
            int i2 = this.g;
            int i3 = (i + i2) >>> 1;
            if (i3 <= i) {
                return null;
            }
            l[] lVarArr = this.a;
            int i4 = this.h;
            this.g = i3;
            long j = this.j >>> 1;
            this.j = j;
            return new f(lVarArr, i4, i3, i2, j, this.i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class g extends l {
        final l[] e;

        g(l[] lVarArr) {
            super(-1, null, null, null);
            this.e = lVarArr;
        }

        /* JADX WARN: Code restructure failed: missing block: B:20:0x0027, code lost:
            if ((r0 instanceof j$.util.concurrent.ConcurrentHashMap.g) == false) goto L28;
         */
        /* JADX WARN: Code restructure failed: missing block: B:21:0x0029, code lost:
            r0 = ((j$.util.concurrent.ConcurrentHashMap.g) r0).e;
         */
        /* JADX WARN: Code restructure failed: missing block: B:23:0x0032, code lost:
            return r0.a(r5, r6);
         */
        @Override // j$.util.concurrent.ConcurrentHashMap.l
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        j$.util.concurrent.ConcurrentHashMap.l a(int r5, java.lang.Object r6) {
            /*
                r4 = this;
                j$.util.concurrent.ConcurrentHashMap$l[] r0 = r4.e
            L2:
                r1 = 0
                if (r0 == 0) goto L37
                int r2 = r0.length
                if (r2 == 0) goto L37
                int r2 = r2 + (-1)
                r2 = r2 & r5
                j$.util.concurrent.ConcurrentHashMap$l r0 = j$.util.concurrent.ConcurrentHashMap.n(r0, r2)
                if (r0 != 0) goto L12
                goto L37
            L12:
                int r2 = r0.a
                if (r2 != r5) goto L23
                java.lang.Object r3 = r0.b
                if (r3 == r6) goto L22
                if (r3 == 0) goto L23
                boolean r3 = r6.equals(r3)
                if (r3 == 0) goto L23
            L22:
                return r0
            L23:
                if (r2 >= 0) goto L33
                boolean r1 = r0 instanceof j$.util.concurrent.ConcurrentHashMap.g
                if (r1 == 0) goto L2e
                j$.util.concurrent.ConcurrentHashMap$g r0 = (j$.util.concurrent.ConcurrentHashMap.g) r0
                j$.util.concurrent.ConcurrentHashMap$l[] r0 = r0.e
                goto L2
            L2e:
                j$.util.concurrent.ConcurrentHashMap$l r5 = r0.a(r5, r6)
                return r5
            L33:
                j$.util.concurrent.ConcurrentHashMap$l r0 = r0.d
                if (r0 != 0) goto L12
            L37:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.g.a(int, java.lang.Object):j$.util.concurrent.ConcurrentHashMap$l");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class h extends a implements java.util.Iterator, Enumeration, j$.util.Iterator {
        h(l[] lVarArr, int i, int i2, int i3, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i, i2, i3, concurrentHashMap);
        }

        @Override // j$.util.Iterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        @Override // java.util.Iterator
        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, CLASSNAMEw.b(consumer));
        }

        @Override // java.util.Iterator, j$.util.Iterator
        /* renamed from: next */
        public final Object mo307next() {
            l lVar = this.b;
            if (lVar != null) {
                Object obj = lVar.b;
                this.j = lVar;
                a();
                return obj;
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Enumeration
        public final Object nextElement() {
            return mo307next();
        }
    }

    /* loaded from: classes2.dex */
    public static class i extends b implements Set, InterfaceCLASSNAMEb {
        i(ConcurrentHashMap concurrentHashMap, Object obj) {
            super(concurrentHashMap);
        }

        @Override // java.util.Collection, java.util.Set
        public boolean add(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection, java.util.Set
        public boolean addAll(Collection collection) {
            throw new UnsupportedOperationException();
        }

        @Override // j$.util.concurrent.ConcurrentHashMap.b, java.util.Collection
        public boolean contains(Object obj) {
            return this.a.containsKey(obj);
        }

        @Override // java.util.Collection, java.util.Set
        public boolean equals(Object obj) {
            Set set;
            return (obj instanceof Set) && ((set = (Set) obj) == this || (containsAll(set) && set.containsAll(this)));
        }

        @Override // j$.util.InterfaceCLASSNAMEb, j$.lang.e
        public void forEach(Consumer consumer) {
            consumer.getClass();
            l[] lVarArr = this.a.a;
            if (lVarArr != null) {
                p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
                while (true) {
                    l a = pVar.a();
                    if (a == null) {
                        return;
                    }
                    consumer.accept(a.b);
                }
            }
        }

        @Override // java.lang.Iterable
        public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
            forEach(CLASSNAMEw.b(consumer));
        }

        @Override // java.util.Collection, java.util.Set
        public int hashCode() {
            java.util.Iterator it = iterator();
            int i = 0;
            while (((a) it).hasNext()) {
                i += ((h) it).mo307next().hashCode();
            }
            return i;
        }

        @Override // j$.util.concurrent.ConcurrentHashMap.b, java.util.Collection, java.lang.Iterable
        public java.util.Iterator iterator() {
            ConcurrentHashMap concurrentHashMap = this.a;
            l[] lVarArr = concurrentHashMap.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            return new h(lVarArr, length, 0, length, concurrentHashMap);
        }

        @Override // j$.util.InterfaceCLASSNAMEb
        public /* synthetic */ boolean k(Predicate predicate) {
            return AbstractCLASSNAMEa.h(this, predicate);
        }

        @Override // java.util.Collection
        public /* synthetic */ Stream parallelStream() {
            return P0.n0(AbstractCLASSNAMEa.g(this));
        }

        @Override // java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            return this.a.remove(obj) != null;
        }

        @Override // java.util.Collection
        public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
            return AbstractCLASSNAMEa.h(this, x0.a(predicate));
        }

        @Override // java.util.Collection, java.lang.Iterable, java.util.Set, j$.util.InterfaceCLASSNAMEb, j$.lang.e
        /* renamed from: spliterator */
        public j$.util.u mo289spliterator() {
            ConcurrentHashMap concurrentHashMap = this.a;
            long m = concurrentHashMap.m();
            l[] lVarArr = concurrentHashMap.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            long j = 0;
            if (m >= 0) {
                j = m;
            }
            return new j(lVarArr, length, 0, length, j);
        }

        @Override // java.util.Collection, java.lang.Iterable, java.util.Set, j$.util.InterfaceCLASSNAMEb, j$.lang.e
        /* renamed from: spliterator  reason: collision with other method in class */
        public /* synthetic */ Spliterator mo289spliterator() {
            return CLASSNAMEh.a(mo289spliterator());
        }

        @Override // java.util.Collection, j$.util.InterfaceCLASSNAMEb
        /* renamed from: stream */
        public /* synthetic */ j$.util.stream.Stream mo238stream() {
            return AbstractCLASSNAMEa.i(this);
        }

        @Override // java.util.Collection, j$.util.InterfaceCLASSNAMEb
        /* renamed from: stream  reason: collision with other method in class */
        public /* synthetic */ Stream mo238stream() {
            return P0.n0(AbstractCLASSNAMEa.i(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class j extends p implements j$.util.u {
        long i;

        j(l[] lVarArr, int i, int i2, int i3, long j) {
            super(lVarArr, i, i2, i3);
            this.i = j;
        }

        @Override // j$.util.u
        public boolean b(Consumer consumer) {
            consumer.getClass();
            l a = a();
            if (a == null) {
                return false;
            }
            consumer.accept(a.b);
            return true;
        }

        @Override // j$.util.u
        public int characteristics() {
            return 4353;
        }

        @Override // j$.util.u
        public long estimateSize() {
            return this.i;
        }

        @Override // j$.util.u
        public void forEachRemaining(Consumer consumer) {
            consumer.getClass();
            while (true) {
                l a = a();
                if (a != null) {
                    consumer.accept(a.b);
                } else {
                    return;
                }
            }
        }

        @Override // j$.util.u
        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        @Override // j$.util.u
        public /* synthetic */ long getExactSizeIfKnown() {
            return AbstractCLASSNAMEa.e(this);
        }

        @Override // j$.util.u
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return AbstractCLASSNAMEa.f(this, i);
        }

        @Override // j$.util.u
        /* renamed from: trySplit */
        public j$.util.u mo322trySplit() {
            int i = this.f;
            int i2 = this.g;
            int i3 = (i + i2) >>> 1;
            if (i3 <= i) {
                return null;
            }
            l[] lVarArr = this.a;
            int i4 = this.h;
            this.g = i3;
            long j = this.i >>> 1;
            this.i = j;
            return new j(lVarArr, i4, i3, i2, j);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class k implements Map.Entry {
        final Object a;
        Object b;
        final ConcurrentHashMap c;

        k(Object obj, Object obj2, ConcurrentHashMap concurrentHashMap) {
            this.a = obj;
            this.b = obj2;
            this.c = concurrentHashMap;
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object obj) {
            Map.Entry entry;
            Object key;
            Object value;
            Object obj2;
            Object obj3;
            return (obj instanceof Map.Entry) && (key = (entry = (Map.Entry) obj).getKey()) != null && (value = entry.getValue()) != null && (key == (obj2 = this.a) || key.equals(obj2)) && (value == (obj3 = this.b) || value.equals(obj3));
        }

        @Override // java.util.Map.Entry
        public Object getKey() {
            return this.a;
        }

        @Override // java.util.Map.Entry
        public Object getValue() {
            return this.b;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return this.a.hashCode() ^ this.b.hashCode();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Map.Entry
        public Object setValue(Object obj) {
            obj.getClass();
            Object obj2 = this.b;
            this.b = obj;
            this.c.put(this.a, obj);
            return obj2;
        }

        public String toString() {
            return this.a + "=" + this.b;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class l implements Map.Entry {
        final int a;
        final Object b;
        volatile Object c;
        volatile l d;

        l(int i, Object obj, Object obj2, l lVar) {
            this.a = i;
            this.b = obj;
            this.c = obj2;
            this.d = lVar;
        }

        l a(int i, Object obj) {
            Object obj2;
            l lVar = this;
            do {
                if (lVar.a == i && ((obj2 = lVar.b) == obj || (obj2 != null && obj.equals(obj2)))) {
                    return lVar;
                }
                lVar = lVar.d;
            } while (lVar != null);
            return null;
        }

        @Override // java.util.Map.Entry
        public final boolean equals(Object obj) {
            Map.Entry entry;
            Object key;
            Object value;
            Object obj2;
            Object obj3;
            return (obj instanceof Map.Entry) && (key = (entry = (Map.Entry) obj).getKey()) != null && (value = entry.getValue()) != null && (key == (obj2 = this.b) || key.equals(obj2)) && (value == (obj3 = this.c) || value.equals(obj3));
        }

        @Override // java.util.Map.Entry
        public final Object getKey() {
            return this.b;
        }

        @Override // java.util.Map.Entry
        public final Object getValue() {
            return this.c;
        }

        @Override // java.util.Map.Entry
        public final int hashCode() {
            return this.b.hashCode() ^ this.c.hashCode();
        }

        @Override // java.util.Map.Entry
        public final Object setValue(Object obj) {
            throw new UnsupportedOperationException();
        }

        public final String toString() {
            return this.b + "=" + this.c;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class m extends l {
        m() {
            super(-3, null, null, null);
        }

        @Override // j$.util.concurrent.ConcurrentHashMap.l
        l a(int i, Object obj) {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    static class n extends ReentrantLock {
        n(float f) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class o {
        int a;
        int b;
        l[] c;
        o d;

        o() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class p {
        l[] a;
        l b = null;
        o c;
        o d;
        int e;
        int f;
        int g;
        final int h;

        p(l[] lVarArr, int i, int i2, int i3) {
            this.a = lVarArr;
            this.h = i;
            this.e = i2;
            this.f = i2;
            this.g = i3;
        }

        final l a() {
            l[] lVarArr;
            int length;
            int i;
            o oVar;
            l lVar = this.b;
            if (lVar != null) {
                lVar = lVar.d;
            }
            while (lVar == null) {
                if (this.f >= this.g || (lVarArr = this.a) == null || (length = lVarArr.length) <= (i = this.e) || i < 0) {
                    this.b = null;
                    return null;
                }
                l n = ConcurrentHashMap.n(lVarArr, i);
                if (n == null || n.a >= 0) {
                    lVar = n;
                } else if (n instanceof g) {
                    this.a = ((g) n).e;
                    o oVar2 = this.d;
                    if (oVar2 != null) {
                        this.d = oVar2.d;
                    } else {
                        oVar2 = new o();
                    }
                    oVar2.c = lVarArr;
                    oVar2.a = length;
                    oVar2.b = i;
                    oVar2.d = this.c;
                    this.c = oVar2;
                    lVar = null;
                } else {
                    lVar = n instanceof q ? ((q) n).f : null;
                }
                if (this.c != null) {
                    while (true) {
                        oVar = this.c;
                        if (oVar == null) {
                            break;
                        }
                        int i2 = this.e;
                        int i3 = oVar.a;
                        int i4 = i2 + i3;
                        this.e = i4;
                        if (i4 < length) {
                            break;
                        }
                        this.e = oVar.b;
                        this.a = oVar.c;
                        oVar.c = null;
                        o oVar3 = oVar.d;
                        oVar.d = this.d;
                        this.c = oVar3;
                        this.d = oVar;
                        length = i3;
                    }
                    if (oVar == null) {
                        int i5 = this.e + this.h;
                        this.e = i5;
                        if (i5 >= length) {
                            int i6 = this.f + 1;
                            this.f = i6;
                            this.e = i6;
                        }
                    }
                } else {
                    int i7 = i + this.h;
                    this.e = i7;
                    if (i7 >= length) {
                        int i8 = this.f + 1;
                        this.f = i8;
                        this.e = i8;
                    }
                }
            }
            this.b = lVar;
            return lVar;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class q extends l {
        private static final Unsafe h;
        private static final long i;
        r e;
        volatile r f;
        volatile Thread g;
        volatile int lockState;

        static {
            try {
                Unsafe c = j$.util.concurrent.c.c();
                h = c;
                i = c.objectFieldOffset(q.class.getDeclaredField("lockState"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        q(r rVar) {
            super(-2, null, null, null);
            int d;
            int j;
            this.f = rVar;
            r rVar2 = null;
            while (rVar != null) {
                r rVar3 = (r) rVar.d;
                rVar.g = null;
                rVar.f = null;
                if (rVar2 == null) {
                    rVar.e = null;
                    rVar.i = false;
                } else {
                    Object obj = rVar.b;
                    int i2 = rVar.a;
                    r rVar4 = rVar2;
                    Class cls = null;
                    while (true) {
                        Object obj2 = rVar4.b;
                        int i3 = rVar4.a;
                        j = i3 > i2 ? -1 : i3 < i2 ? 1 : ((cls == null && (cls = ConcurrentHashMap.c(obj)) == null) || (d = ConcurrentHashMap.d(cls, obj, obj2)) == 0) ? j(obj, obj2) : d;
                        r rVar5 = j <= 0 ? rVar4.f : rVar4.g;
                        if (rVar5 == null) {
                            break;
                        }
                        rVar4 = rVar5;
                    }
                    rVar.e = rVar4;
                    if (j <= 0) {
                        rVar4.f = rVar;
                    } else {
                        rVar4.g = rVar;
                    }
                    rVar = c(rVar2, rVar);
                }
                rVar2 = rVar;
                rVar = rVar3;
            }
            this.e = rVar2;
        }

        static r b(r rVar, r rVar2) {
            while (rVar2 != null && rVar2 != rVar) {
                r rVar3 = rVar2.e;
                if (rVar3 == null) {
                    rVar2.i = false;
                    return rVar2;
                } else if (rVar2.i) {
                    rVar2.i = false;
                    return rVar;
                } else {
                    r rVar4 = rVar3.f;
                    r rVar5 = null;
                    if (rVar4 == rVar2) {
                        rVar4 = rVar3.g;
                        if (rVar4 != null && rVar4.i) {
                            rVar4.i = false;
                            rVar3.i = true;
                            rVar = h(rVar, rVar3);
                            rVar3 = rVar2.e;
                            rVar4 = rVar3 == null ? null : rVar3.g;
                        }
                        if (rVar4 == null) {
                            rVar2 = rVar3;
                        } else {
                            r rVar6 = rVar4.f;
                            r rVar7 = rVar4.g;
                            if ((rVar7 != null && rVar7.i) || (rVar6 != null && rVar6.i)) {
                                if (rVar7 == null || !rVar7.i) {
                                    if (rVar6 != null) {
                                        rVar6.i = false;
                                    }
                                    rVar4.i = true;
                                    rVar = i(rVar, rVar4);
                                    rVar3 = rVar2.e;
                                    if (rVar3 != null) {
                                        rVar5 = rVar3.g;
                                    }
                                } else {
                                    rVar5 = rVar4;
                                }
                                if (rVar5 != null) {
                                    rVar5.i = rVar3 == null ? false : rVar3.i;
                                    r rVar8 = rVar5.g;
                                    if (rVar8 != null) {
                                        rVar8.i = false;
                                    }
                                }
                                if (rVar3 != null) {
                                    rVar3.i = false;
                                    rVar = h(rVar, rVar3);
                                }
                                rVar2 = rVar;
                                rVar = rVar2;
                            }
                            rVar4.i = true;
                            rVar2 = rVar3;
                        }
                    } else {
                        if (rVar4 != null && rVar4.i) {
                            rVar4.i = false;
                            rVar3.i = true;
                            rVar = i(rVar, rVar3);
                            rVar3 = rVar2.e;
                            rVar4 = rVar3 == null ? null : rVar3.f;
                        }
                        if (rVar4 == null) {
                            rVar2 = rVar3;
                        } else {
                            r rVar9 = rVar4.f;
                            r rVar10 = rVar4.g;
                            if ((rVar9 != null && rVar9.i) || (rVar10 != null && rVar10.i)) {
                                if (rVar9 == null || !rVar9.i) {
                                    if (rVar10 != null) {
                                        rVar10.i = false;
                                    }
                                    rVar4.i = true;
                                    rVar = h(rVar, rVar4);
                                    rVar3 = rVar2.e;
                                    if (rVar3 != null) {
                                        rVar5 = rVar3.f;
                                    }
                                } else {
                                    rVar5 = rVar4;
                                }
                                if (rVar5 != null) {
                                    rVar5.i = rVar3 == null ? false : rVar3.i;
                                    r rVar11 = rVar5.f;
                                    if (rVar11 != null) {
                                        rVar11.i = false;
                                    }
                                }
                                if (rVar3 != null) {
                                    rVar3.i = false;
                                    rVar = i(rVar, rVar3);
                                }
                                rVar2 = rVar;
                                rVar = rVar2;
                            }
                            rVar4.i = true;
                            rVar2 = rVar3;
                        }
                    }
                }
            }
            return rVar;
        }

        static r c(r rVar, r rVar2) {
            r rVar3;
            rVar2.i = true;
            while (true) {
                r rVar4 = rVar2.e;
                if (rVar4 == null) {
                    rVar2.i = false;
                    return rVar2;
                } else if (!rVar4.i || (rVar3 = rVar4.e) == null) {
                    break;
                } else {
                    r rVar5 = rVar3.f;
                    if (rVar4 == rVar5) {
                        rVar5 = rVar3.g;
                        if (rVar5 == null || !rVar5.i) {
                            if (rVar2 == rVar4.g) {
                                rVar = h(rVar, rVar4);
                                r rVar6 = rVar4.e;
                                rVar3 = rVar6 == null ? null : rVar6.e;
                                rVar4 = rVar6;
                                rVar2 = rVar4;
                            }
                            if (rVar4 != null) {
                                rVar4.i = false;
                                if (rVar3 != null) {
                                    rVar3.i = true;
                                    rVar = i(rVar, rVar3);
                                }
                            }
                        } else {
                            rVar5.i = false;
                            rVar4.i = false;
                            rVar3.i = true;
                            rVar2 = rVar3;
                        }
                    } else if (rVar5 == null || !rVar5.i) {
                        if (rVar2 == rVar4.f) {
                            rVar = i(rVar, rVar4);
                            r rVar7 = rVar4.e;
                            rVar3 = rVar7 == null ? null : rVar7.e;
                            rVar4 = rVar7;
                            rVar2 = rVar4;
                        }
                        if (rVar4 != null) {
                            rVar4.i = false;
                            if (rVar3 != null) {
                                rVar3.i = true;
                                rVar = h(rVar, rVar3);
                            }
                        }
                    } else {
                        rVar5.i = false;
                        rVar4.i = false;
                        rVar3.i = true;
                        rVar2 = rVar3;
                    }
                }
            }
            return rVar;
        }

        private final void d() {
            boolean z = false;
            while (true) {
                int i2 = this.lockState;
                if ((i2 & (-3)) == 0) {
                    if (h.compareAndSwapInt(this, i, i2, 1)) {
                        break;
                    }
                } else if ((i2 & 2) == 0) {
                    if (h.compareAndSwapInt(this, i, i2, i2 | 2)) {
                        z = true;
                        this.g = Thread.currentThread();
                    }
                } else if (z) {
                    LockSupport.park(this);
                }
            }
            if (z) {
                this.g = null;
            }
        }

        private final void e() {
            if (!h.compareAndSwapInt(this, i, 0, 1)) {
                d();
            }
        }

        static r h(r rVar, r rVar2) {
            r rVar3 = rVar2.g;
            if (rVar3 != null) {
                r rVar4 = rVar3.f;
                rVar2.g = rVar4;
                if (rVar4 != null) {
                    rVar4.e = rVar2;
                }
                r rVar5 = rVar2.e;
                rVar3.e = rVar5;
                if (rVar5 == null) {
                    rVar3.i = false;
                    rVar = rVar3;
                } else if (rVar5.f == rVar2) {
                    rVar5.f = rVar3;
                } else {
                    rVar5.g = rVar3;
                }
                rVar3.f = rVar2;
                rVar2.e = rVar3;
            }
            return rVar;
        }

        static r i(r rVar, r rVar2) {
            r rVar3 = rVar2.f;
            if (rVar3 != null) {
                r rVar4 = rVar3.g;
                rVar2.f = rVar4;
                if (rVar4 != null) {
                    rVar4.e = rVar2;
                }
                r rVar5 = rVar2.e;
                rVar3.e = rVar5;
                if (rVar5 == null) {
                    rVar3.i = false;
                    rVar = rVar3;
                } else if (rVar5.g == rVar2) {
                    rVar5.g = rVar3;
                } else {
                    rVar5.f = rVar3;
                }
                rVar3.g = rVar2;
                rVar2.e = rVar3;
            }
            return rVar;
        }

        static int j(Object obj, Object obj2) {
            int compareTo;
            return (obj == null || obj2 == null || (compareTo = obj.getClass().getName().compareTo(obj2.getClass().getName())) == 0) ? System.identityHashCode(obj) <= System.identityHashCode(obj2) ? -1 : 1 : compareTo;
        }

        @Override // j$.util.concurrent.ConcurrentHashMap.l
        final l a(int i2, Object obj) {
            Object obj2;
            Thread thread;
            Thread thread2;
            l lVar = this.f;
            while (true) {
                r rVar = null;
                if (lVar == null) {
                    return null;
                }
                int i3 = this.lockState;
                if ((i3 & 3) == 0) {
                    Unsafe unsafe = h;
                    long j = i;
                    if (unsafe.compareAndSwapInt(this, j, i3, i3 + 4)) {
                        try {
                            r rVar2 = this.e;
                            if (rVar2 != null) {
                                rVar = rVar2.b(i2, obj, null);
                            }
                            if (j$.util.concurrent.c.a(unsafe, this, j, -4) == 6 && (thread2 = this.g) != null) {
                                LockSupport.unpark(thread2);
                            }
                            return rVar;
                        } catch (Throwable th) {
                            if (j$.util.concurrent.c.a(h, this, i, -4) == 6 && (thread = this.g) != null) {
                                LockSupport.unpark(thread);
                            }
                            throw th;
                        }
                    }
                } else if (lVar.a != i2 || ((obj2 = lVar.b) != obj && (obj2 == null || !obj.equals(obj2)))) {
                    lVar = lVar.d;
                }
            }
            return lVar;
        }

        /* JADX WARN: Code restructure failed: missing block: B:30:0x0060, code lost:
            return r3;
         */
        /* JADX WARN: Code restructure failed: missing block: B:50:0x00a3, code lost:
            return null;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        final j$.util.concurrent.ConcurrentHashMap.r f(int r16, java.lang.Object r17, java.lang.Object r18) {
            /*
                r15 = this;
                r1 = r15
                r0 = r16
                r4 = r17
                j$.util.concurrent.ConcurrentHashMap$r r2 = r1.e
                r8 = 0
                r9 = 0
                r10 = r2
                r2 = r8
                r3 = 0
            Lc:
                if (r10 != 0) goto L22
                j$.util.concurrent.ConcurrentHashMap$r r9 = new j$.util.concurrent.ConcurrentHashMap$r
                r6 = 0
                r7 = 0
                r2 = r9
                r3 = r16
                r4 = r17
                r5 = r18
                r2.<init>(r3, r4, r5, r6, r7)
                r1.e = r9
                r1.f = r9
                goto La3
            L22:
                int r5 = r10.a
                r11 = 1
                if (r5 <= r0) goto L2a
                r5 = -1
                r12 = -1
                goto L69
            L2a:
                if (r5 >= r0) goto L2e
                r12 = 1
                goto L69
            L2e:
                java.lang.Object r5 = r10.b
                if (r5 == r4) goto Lab
                if (r5 == 0) goto L3c
                boolean r6 = r4.equals(r5)
                if (r6 == 0) goto L3c
                goto Lab
            L3c:
                if (r2 != 0) goto L44
                java.lang.Class r2 = j$.util.concurrent.ConcurrentHashMap.c(r17)
                if (r2 == 0) goto L4a
            L44:
                int r6 = j$.util.concurrent.ConcurrentHashMap.d(r2, r4, r5)
                if (r6 != 0) goto L68
            L4a:
                if (r3 != 0) goto L62
                j$.util.concurrent.ConcurrentHashMap$r r3 = r10.f
                if (r3 == 0) goto L56
                j$.util.concurrent.ConcurrentHashMap$r r3 = r3.b(r0, r4, r2)
                if (r3 != 0) goto L60
            L56:
                j$.util.concurrent.ConcurrentHashMap$r r3 = r10.g
                if (r3 == 0) goto L61
                j$.util.concurrent.ConcurrentHashMap$r r3 = r3.b(r0, r4, r2)
                if (r3 == 0) goto L61
            L60:
                return r3
            L61:
                r3 = 1
            L62:
                int r5 = j(r4, r5)
                r12 = r5
                goto L69
            L68:
                r12 = r6
            L69:
                if (r12 > 0) goto L6e
                j$.util.concurrent.ConcurrentHashMap$r r5 = r10.f
                goto L70
            L6e:
                j$.util.concurrent.ConcurrentHashMap$r r5 = r10.g
            L70:
                if (r5 != 0) goto La8
                j$.util.concurrent.ConcurrentHashMap$r r13 = r1.f
                j$.util.concurrent.ConcurrentHashMap$r r14 = new j$.util.concurrent.ConcurrentHashMap$r
                r2 = r14
                r3 = r16
                r4 = r17
                r5 = r18
                r6 = r13
                r7 = r10
                r2.<init>(r3, r4, r5, r6, r7)
                r1.f = r14
                if (r13 == 0) goto L88
                r13.h = r14
            L88:
                if (r12 > 0) goto L8d
                r10.f = r14
                goto L8f
            L8d:
                r10.g = r14
            L8f:
                boolean r0 = r10.i
                if (r0 != 0) goto L96
                r14.i = r11
                goto La3
            L96:
                r15.e()
                j$.util.concurrent.ConcurrentHashMap$r r0 = r1.e     // Catch: java.lang.Throwable -> La4
                j$.util.concurrent.ConcurrentHashMap$r r0 = c(r0, r14)     // Catch: java.lang.Throwable -> La4
                r1.e = r0     // Catch: java.lang.Throwable -> La4
                r1.lockState = r9
            La3:
                return r8
            La4:
                r0 = move-exception
                r1.lockState = r9
                throw r0
            La8:
                r10 = r5
                goto Lc
            Lab:
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.q.f(int, java.lang.Object, java.lang.Object):j$.util.concurrent.ConcurrentHashMap$r");
        }

        /* JADX WARN: Removed duplicated region for block: B:57:0x008e A[Catch: all -> 0x00c8, TryCatch #0 {all -> 0x00c8, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:39:0x0065, B:41:0x006b, B:42:0x006d, B:57:0x008e, B:64:0x009f, B:60:0x0096, B:62:0x009a, B:63:0x009d, B:65:0x00a5, B:69:0x00ae, B:71:0x00b2, B:73:0x00b6, B:75:0x00ba, B:79:0x00c3, B:76:0x00bd, B:78:0x00c1, B:68:0x00aa, B:45:0x0077, B:47:0x007b, B:48:0x007e, B:32:0x0052, B:34:0x0058, B:36:0x005c, B:37:0x005f, B:38:0x0061), top: B:86:0x0030 }] */
        /* JADX WARN: Removed duplicated region for block: B:67:0x00a9  */
        /* JADX WARN: Removed duplicated region for block: B:68:0x00aa A[Catch: all -> 0x00c8, TryCatch #0 {all -> 0x00c8, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:39:0x0065, B:41:0x006b, B:42:0x006d, B:57:0x008e, B:64:0x009f, B:60:0x0096, B:62:0x009a, B:63:0x009d, B:65:0x00a5, B:69:0x00ae, B:71:0x00b2, B:73:0x00b6, B:75:0x00ba, B:79:0x00c3, B:76:0x00bd, B:78:0x00c1, B:68:0x00aa, B:45:0x0077, B:47:0x007b, B:48:0x007e, B:32:0x0052, B:34:0x0058, B:36:0x005c, B:37:0x005f, B:38:0x0061), top: B:86:0x0030 }] */
        /* JADX WARN: Removed duplicated region for block: B:71:0x00b2 A[Catch: all -> 0x00c8, TryCatch #0 {all -> 0x00c8, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:39:0x0065, B:41:0x006b, B:42:0x006d, B:57:0x008e, B:64:0x009f, B:60:0x0096, B:62:0x009a, B:63:0x009d, B:65:0x00a5, B:69:0x00ae, B:71:0x00b2, B:73:0x00b6, B:75:0x00ba, B:79:0x00c3, B:76:0x00bd, B:78:0x00c1, B:68:0x00aa, B:45:0x0077, B:47:0x007b, B:48:0x007e, B:32:0x0052, B:34:0x0058, B:36:0x005c, B:37:0x005f, B:38:0x0061), top: B:86:0x0030 }] */
        /* JADX WARN: Removed duplicated region for block: B:75:0x00ba A[Catch: all -> 0x00c8, TryCatch #0 {all -> 0x00c8, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:39:0x0065, B:41:0x006b, B:42:0x006d, B:57:0x008e, B:64:0x009f, B:60:0x0096, B:62:0x009a, B:63:0x009d, B:65:0x00a5, B:69:0x00ae, B:71:0x00b2, B:73:0x00b6, B:75:0x00ba, B:79:0x00c3, B:76:0x00bd, B:78:0x00c1, B:68:0x00aa, B:45:0x0077, B:47:0x007b, B:48:0x007e, B:32:0x0052, B:34:0x0058, B:36:0x005c, B:37:0x005f, B:38:0x0061), top: B:86:0x0030 }] */
        /* JADX WARN: Removed duplicated region for block: B:76:0x00bd A[Catch: all -> 0x00c8, TryCatch #0 {all -> 0x00c8, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:39:0x0065, B:41:0x006b, B:42:0x006d, B:57:0x008e, B:64:0x009f, B:60:0x0096, B:62:0x009a, B:63:0x009d, B:65:0x00a5, B:69:0x00ae, B:71:0x00b2, B:73:0x00b6, B:75:0x00ba, B:79:0x00c3, B:76:0x00bd, B:78:0x00c1, B:68:0x00aa, B:45:0x0077, B:47:0x007b, B:48:0x007e, B:32:0x0052, B:34:0x0058, B:36:0x005c, B:37:0x005f, B:38:0x0061), top: B:86:0x0030 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        final boolean g(j$.util.concurrent.ConcurrentHashMap.r r11) {
            /*
                Method dump skipped, instructions count: 205
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.q.g(j$.util.concurrent.ConcurrentHashMap$r):boolean");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class r extends l {
        r e;
        r f;
        r g;
        r h;
        boolean i;

        r(int i, Object obj, Object obj2, l lVar, r rVar) {
            super(i, obj, obj2, lVar);
            this.e = rVar;
        }

        @Override // j$.util.concurrent.ConcurrentHashMap.l
        l a(int i, Object obj) {
            return b(i, obj, null);
        }

        final r b(int i, Object obj, Class cls) {
            int d;
            if (obj != null) {
                r rVar = this;
                do {
                    r rVar2 = rVar.f;
                    r rVar3 = rVar.g;
                    int i2 = rVar.a;
                    if (i2 <= i) {
                        if (i2 >= i) {
                            Object obj2 = rVar.b;
                            if (obj2 == obj || (obj2 != null && obj.equals(obj2))) {
                                return rVar;
                            }
                            if (rVar2 != null) {
                                if (rVar3 != null) {
                                    if ((cls == null && (cls = ConcurrentHashMap.c(obj)) == null) || (d = ConcurrentHashMap.d(cls, obj, obj2)) == 0) {
                                        r b = rVar3.b(i, obj, cls);
                                        if (b != null) {
                                            return b;
                                        }
                                    } else if (d >= 0) {
                                        rVar2 = rVar3;
                                    }
                                }
                            }
                        }
                        rVar = rVar3;
                        continue;
                    }
                    rVar = rVar2;
                    continue;
                } while (rVar != null);
                return null;
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class s extends a implements java.util.Iterator, Enumeration, j$.util.Iterator {
        s(l[] lVarArr, int i, int i2, int i3, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i, i2, i3, concurrentHashMap);
        }

        @Override // j$.util.Iterator
        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        @Override // java.util.Iterator
        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, CLASSNAMEw.b(consumer));
        }

        @Override // java.util.Iterator, j$.util.Iterator
        /* renamed from: next */
        public final Object mo307next() {
            l lVar = this.b;
            if (lVar != null) {
                Object obj = lVar.c;
                this.j = lVar;
                a();
                return obj;
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Enumeration
        public final Object nextElement() {
            return mo307next();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class t extends p implements j$.util.u {
        long i;

        t(l[] lVarArr, int i, int i2, int i3, long j) {
            super(lVarArr, i, i2, i3);
            this.i = j;
        }

        @Override // j$.util.u
        public boolean b(Consumer consumer) {
            consumer.getClass();
            l a = a();
            if (a == null) {
                return false;
            }
            consumer.accept(a.c);
            return true;
        }

        @Override // j$.util.u
        public int characteristics() {
            return 4352;
        }

        @Override // j$.util.u
        public long estimateSize() {
            return this.i;
        }

        @Override // j$.util.u
        public void forEachRemaining(Consumer consumer) {
            consumer.getClass();
            while (true) {
                l a = a();
                if (a != null) {
                    consumer.accept(a.c);
                } else {
                    return;
                }
            }
        }

        @Override // j$.util.u
        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        @Override // j$.util.u
        public /* synthetic */ long getExactSizeIfKnown() {
            return AbstractCLASSNAMEa.e(this);
        }

        @Override // j$.util.u
        public /* synthetic */ boolean hasCharacteristics(int i) {
            return AbstractCLASSNAMEa.f(this, i);
        }

        @Override // j$.util.u
        /* renamed from: trySplit */
        public j$.util.u mo322trySplit() {
            int i = this.f;
            int i2 = this.g;
            int i3 = (i + i2) >>> 1;
            if (i3 <= i) {
                return null;
            }
            l[] lVarArr = this.a;
            int i4 = this.h;
            this.g = i3;
            long j = this.i >>> 1;
            this.i = j;
            return new t(lVarArr, i4, i3, i2, j);
        }
    }

    /* loaded from: classes2.dex */
    static final class u extends b implements InterfaceCLASSNAMEb {
        u(ConcurrentHashMap concurrentHashMap) {
            super(concurrentHashMap);
        }

        @Override // java.util.Collection
        public final boolean add(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public final boolean addAll(Collection collection) {
            throw new UnsupportedOperationException();
        }

        @Override // j$.util.concurrent.ConcurrentHashMap.b, java.util.Collection
        public final boolean contains(Object obj) {
            return this.a.containsValue(obj);
        }

        @Override // j$.util.InterfaceCLASSNAMEb, j$.lang.e
        public void forEach(Consumer consumer) {
            consumer.getClass();
            l[] lVarArr = this.a.a;
            if (lVarArr != null) {
                p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
                while (true) {
                    l a = pVar.a();
                    if (a == null) {
                        return;
                    }
                    consumer.accept(a.c);
                }
            }
        }

        @Override // java.lang.Iterable
        public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
            forEach(CLASSNAMEw.b(consumer));
        }

        @Override // j$.util.concurrent.ConcurrentHashMap.b, java.util.Collection, java.lang.Iterable
        public final java.util.Iterator iterator() {
            ConcurrentHashMap concurrentHashMap = this.a;
            l[] lVarArr = concurrentHashMap.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            return new s(lVarArr, length, 0, length, concurrentHashMap);
        }

        @Override // j$.util.InterfaceCLASSNAMEb
        public /* synthetic */ boolean k(Predicate predicate) {
            return AbstractCLASSNAMEa.h(this, predicate);
        }

        @Override // java.util.Collection
        public /* synthetic */ Stream parallelStream() {
            return P0.n0(AbstractCLASSNAMEa.g(this));
        }

        @Override // java.util.Collection
        public final boolean remove(Object obj) {
            a aVar;
            if (obj != null) {
                java.util.Iterator it = iterator();
                do {
                    aVar = (a) it;
                    if (!aVar.hasNext()) {
                        return false;
                    }
                } while (!obj.equals(((s) it).mo307next()));
                aVar.remove();
                return true;
            }
            return false;
        }

        @Override // java.util.Collection
        public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
            return AbstractCLASSNAMEa.h(this, x0.a(predicate));
        }

        @Override // java.util.Collection, java.lang.Iterable, j$.util.InterfaceCLASSNAMEb, j$.lang.e
        /* renamed from: spliterator */
        public j$.util.u mo289spliterator() {
            ConcurrentHashMap concurrentHashMap = this.a;
            long m = concurrentHashMap.m();
            l[] lVarArr = concurrentHashMap.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            long j = 0;
            if (m >= 0) {
                j = m;
            }
            return new t(lVarArr, length, 0, length, j);
        }

        @Override // java.util.Collection, java.lang.Iterable, j$.util.InterfaceCLASSNAMEb, j$.lang.e
        /* renamed from: spliterator  reason: collision with other method in class */
        public /* synthetic */ Spliterator mo289spliterator() {
            return CLASSNAMEh.a(mo289spliterator());
        }

        @Override // java.util.Collection, j$.util.InterfaceCLASSNAMEb
        /* renamed from: stream */
        public /* synthetic */ j$.util.stream.Stream mo238stream() {
            return AbstractCLASSNAMEa.i(this);
        }

        @Override // java.util.Collection, j$.util.InterfaceCLASSNAMEb
        /* renamed from: stream  reason: collision with other method in class */
        public /* synthetic */ Stream mo238stream() {
            return P0.n0(AbstractCLASSNAMEa.i(this));
        }
    }

    static {
        Class cls = Integer.TYPE;
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("segments", n[].class), new ObjectStreamField("segmentMask", cls), new ObjectStreamField("segmentShift", cls)};
        try {
            Unsafe c2 = j$.util.concurrent.c.c();
            h = c2;
            i = c2.objectFieldOffset(ConcurrentHashMap.class.getDeclaredField("sizeCtl"));
            j = c2.objectFieldOffset(ConcurrentHashMap.class.getDeclaredField("transferIndex"));
            k = c2.objectFieldOffset(ConcurrentHashMap.class.getDeclaredField("baseCount"));
            l = c2.objectFieldOffset(ConcurrentHashMap.class.getDeclaredField("cellsBusy"));
            m = c2.objectFieldOffset(c.class.getDeclaredField("value"));
            n = c2.arrayBaseOffset(l[].class);
            int arrayIndexScale = c2.arrayIndexScale(l[].class);
            if (((arrayIndexScale - 1) & arrayIndexScale) != 0) {
                throw new Error("data type scale not a power of two");
            }
            o = 31 - Integer.numberOfLeadingZeros(arrayIndexScale);
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
        double d2 = (i2 < i3 ? i3 : i2) / f2;
        Double.isNaN(d2);
        long j2 = (long) (d2 + 1.0d);
        this.sizeCtl = j2 >= NUM ? NUM : o((int) j2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0012, code lost:
        if (r1.compareAndSwapLong(r11, r3, r5, r9) == false) goto L53;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final void a(long r12, int r14) {
        /*
            r11 = this;
            j$.util.concurrent.ConcurrentHashMap$c[] r0 = r11.c
            if (r0 != 0) goto L14
            sun.misc.Unsafe r1 = j$.util.concurrent.ConcurrentHashMap.h
            long r3 = j$.util.concurrent.ConcurrentHashMap.k
            long r5 = r11.baseCount
            long r9 = r5 + r12
            r2 = r11
            r7 = r9
            boolean r1 = r1.compareAndSwapLong(r2, r3, r5, r7)
            if (r1 != 0) goto L3b
        L14:
            r1 = 1
            if (r0 == 0) goto L94
            int r2 = r0.length
            int r2 = r2 - r1
            if (r2 < 0) goto L94
            int r3 = j$.util.concurrent.i.c()
            r2 = r2 & r3
            r4 = r0[r2]
            if (r4 == 0) goto L94
            sun.misc.Unsafe r3 = j$.util.concurrent.ConcurrentHashMap.h
            long r5 = j$.util.concurrent.ConcurrentHashMap.m
            long r7 = r4.value
            long r9 = r7 + r12
            boolean r0 = r3.compareAndSwapLong(r4, r5, r7, r9)
            if (r0 != 0) goto L34
            r1 = r0
            goto L94
        L34:
            if (r14 > r1) goto L37
            return
        L37:
            long r9 = r11.m()
        L3b:
            if (r14 < 0) goto L93
        L3d:
            int r4 = r11.sizeCtl
            long r12 = (long) r4
            int r14 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r14 < 0) goto L93
            j$.util.concurrent.ConcurrentHashMap$l[] r12 = r11.a
            if (r12 == 0) goto L93
            int r13 = r12.length
            r14 = 1073741824(0x40000000, float:2.0)
            if (r13 >= r14) goto L93
            int r13 = j(r13)
            if (r4 >= 0) goto L7b
            int r14 = r4 >>> 16
            if (r14 != r13) goto L93
            int r14 = r13 + 1
            if (r4 == r14) goto L93
            r14 = 65535(0xffff, float:9.1834E-41)
            int r13 = r13 + r14
            if (r4 == r13) goto L93
            j$.util.concurrent.ConcurrentHashMap$l[] r13 = r11.b
            if (r13 == 0) goto L93
            int r14 = r11.transferIndex
            if (r14 > 0) goto L6a
            goto L93
        L6a:
            sun.misc.Unsafe r0 = j$.util.concurrent.ConcurrentHashMap.h
            long r2 = j$.util.concurrent.ConcurrentHashMap.i
            int r5 = r4 + 1
            r1 = r11
            boolean r14 = r0.compareAndSwapInt(r1, r2, r4, r5)
            if (r14 == 0) goto L8e
            r11.p(r12, r13)
            goto L8e
        L7b:
            sun.misc.Unsafe r0 = j$.util.concurrent.ConcurrentHashMap.h
            long r2 = j$.util.concurrent.ConcurrentHashMap.i
            int r13 = r13 << 16
            int r5 = r13 + 2
            r1 = r11
            boolean r13 = r0.compareAndSwapInt(r1, r2, r4, r5)
            if (r13 == 0) goto L8e
            r13 = 0
            r11.p(r12, r13)
        L8e:
            long r9 = r11.m()
            goto L3d
        L93:
            return
        L94:
            r11.e(r12, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.a(long, int):void");
    }

    static final boolean b(l[] lVarArr, int i2, l lVar, l lVar2) {
        return h.compareAndSwapObject(lVarArr, (i2 << o) + n, (Object) null, lVar2);
    }

    static Class c(Object obj) {
        Type[] actualTypeArguments;
        if (obj instanceof Comparable) {
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
        return null;
    }

    static int d(Class cls, Object obj, Object obj2) {
        if (obj2 == null || obj2.getClass() != cls) {
            return 0;
        }
        return ((Comparable) obj).compareTo(obj2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x009b, code lost:
        if (r24.c != r7) goto L101;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x009d, code lost:
        r1 = new j$.util.concurrent.ConcurrentHashMap.c[r8 << 1];
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00a2, code lost:
        if (r2 >= r8) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00a4, code lost:
        r1[r2] = r7[r2];
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00ab, code lost:
        r24.c = r1;
     */
    /* JADX WARN: Removed duplicated region for block: B:108:0x001b A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0101 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final void e(long r25, boolean r27) {
        /*
            Method dump skipped, instructions count: 258
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.e(long, boolean):void");
    }

    private final l[] g() {
        while (true) {
            l[] lVarArr = this.a;
            if (lVarArr == null || lVarArr.length == 0) {
                int i2 = this.sizeCtl;
                if (i2 < 0) {
                    Thread.yield();
                } else if (h.compareAndSwapInt(this, i, i2, -1)) {
                    try {
                        l[] lVarArr2 = this.a;
                        if (lVarArr2 == null || lVarArr2.length == 0) {
                            int i3 = i2 > 0 ? i2 : 16;
                            l[] lVarArr3 = new l[i3];
                            this.a = lVarArr3;
                            i2 = i3 - (i3 >>> 2);
                            lVarArr2 = lVarArr3;
                        }
                        this.sizeCtl = i2;
                        return lVarArr2;
                    } catch (Throwable th) {
                        this.sizeCtl = i2;
                        throw th;
                    }
                }
            } else {
                return lVarArr;
            }
        }
    }

    static final int j(int i2) {
        return Integer.numberOfLeadingZeros(i2) | 32768;
    }

    static final void k(l[] lVarArr, int i2, l lVar) {
        h.putObjectVolatile(lVarArr, (i2 << o) + n, lVar);
    }

    static final int l(int i2) {
        return (i2 ^ (i2 >>> 16)) & Integer.MAX_VALUE;
    }

    static final l n(l[] lVarArr, int i2) {
        return (l) h.getObjectVolatile(lVarArr, (i2 << o) + n);
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
        if (i8 < NUM) {
            return 1 + i8;
        }
        return NUM;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v12, types: [j$.util.concurrent.ConcurrentHashMap$l] */
    /* JADX WARN: Type inference failed for: r13v14, types: [j$.util.concurrent.ConcurrentHashMap$l] */
    /* JADX WARN: Type inference failed for: r6v20, types: [j$.util.concurrent.ConcurrentHashMap$l] */
    /* JADX WARN: Type inference failed for: r6v25, types: [j$.util.concurrent.ConcurrentHashMap$l] */
    private final void p(l[] lVarArr, l[] lVarArr2) {
        l[] lVarArr3;
        l[] lVarArr4;
        int i2;
        int i3;
        int i4;
        int i5;
        g gVar;
        r rVar;
        int i6;
        ConcurrentHashMap<K, V> concurrentHashMap = this;
        int length = lVarArr.length;
        int i7 = g;
        int i8 = i7 > 1 ? (length >>> 3) / i7 : length;
        int i9 = i8 < 16 ? 16 : i8;
        if (lVarArr2 == null) {
            try {
                l[] lVarArr5 = new l[length << 1];
                concurrentHashMap.b = lVarArr5;
                concurrentHashMap.transferIndex = length;
                lVarArr3 = lVarArr5;
            } catch (Throwable unused) {
                concurrentHashMap.sizeCtl = Integer.MAX_VALUE;
                return;
            }
        } else {
            lVarArr3 = lVarArr2;
        }
        int length2 = lVarArr3.length;
        g gVar2 = new g(lVarArr3);
        l[] lVarArr6 = lVarArr;
        int i10 = 0;
        int i11 = 0;
        boolean z = true;
        boolean z2 = false;
        while (true) {
            if (z) {
                int i12 = i10 - 1;
                if (i12 >= i11 || z2) {
                    lVarArr4 = lVarArr6;
                    i10 = i12;
                    i11 = i11;
                } else {
                    int i13 = concurrentHashMap.transferIndex;
                    if (i13 <= 0) {
                        lVarArr4 = lVarArr6;
                        i10 = -1;
                    } else {
                        Unsafe unsafe = h;
                        long j2 = j;
                        int i14 = i13 > i9 ? i13 - i9 : 0;
                        lVarArr4 = lVarArr6;
                        i2 = i11;
                        if (unsafe.compareAndSwapInt(this, j2, i13, i14)) {
                            i10 = i13 - 1;
                            i11 = i14;
                        } else {
                            lVarArr6 = lVarArr4;
                            i10 = i12;
                            i11 = i2;
                        }
                    }
                }
                lVarArr6 = lVarArr4;
                z = false;
            } else {
                l[] lVarArr7 = lVarArr6;
                i2 = i11;
                r rVar2 = null;
                if (i10 < 0 || i10 >= length || (i3 = i10 + length) >= length2) {
                    int i15 = i9;
                    int i16 = length2;
                    g gVar3 = gVar2;
                    if (z2) {
                        this.b = null;
                        this.a = lVarArr3;
                        this.sizeCtl = (length << 1) - (length >>> 1);
                        return;
                    }
                    Unsafe unsafe2 = h;
                    long j3 = i;
                    int i17 = this.sizeCtl;
                    int i18 = i10;
                    if (!unsafe2.compareAndSwapInt(this, j3, i17, i17 - 1)) {
                        gVar2 = gVar3;
                        concurrentHashMap = this;
                        i10 = i18;
                        lVarArr6 = lVarArr7;
                        i11 = i2;
                        i9 = i15;
                        length2 = i16;
                    } else if (i17 - 2 != (j(length) << 16)) {
                        return;
                    } else {
                        gVar2 = gVar3;
                        i10 = length;
                        concurrentHashMap = this;
                        lVarArr6 = lVarArr7;
                        i11 = i2;
                        i9 = i15;
                        length2 = i16;
                        z = true;
                        z2 = true;
                    }
                } else {
                    l n2 = n(lVarArr7, i10);
                    if (n2 == null) {
                        z = b(lVarArr7, i10, null, gVar2);
                        lVarArr6 = lVarArr7;
                        i11 = i2;
                    } else {
                        int i19 = n2.a;
                        if (i19 == -1) {
                            lVarArr6 = lVarArr7;
                            i11 = i2;
                            z = true;
                        } else {
                            synchronized (n2) {
                                if (n(lVarArr7, i10) == n2) {
                                    if (i19 >= 0) {
                                        int i20 = i19 & length;
                                        r rVar3 = n2;
                                        for (r rVar4 = n2.d; rVar4 != null; rVar4 = rVar4.d) {
                                            int i21 = rVar4.a & length;
                                            if (i21 != i20) {
                                                rVar3 = rVar4;
                                                i20 = i21;
                                            }
                                        }
                                        if (i20 == 0) {
                                            rVar = null;
                                            rVar2 = rVar3;
                                        } else {
                                            rVar = rVar3;
                                        }
                                        l lVar = n2;
                                        while (lVar != rVar3) {
                                            int i22 = lVar.a;
                                            r rVar5 = rVar3;
                                            Object obj = lVar.b;
                                            int i23 = i9;
                                            Object obj2 = lVar.c;
                                            if ((i22 & length) == 0) {
                                                i6 = length2;
                                                rVar2 = new l(i22, obj, obj2, rVar2);
                                            } else {
                                                i6 = length2;
                                                rVar = new l(i22, obj, obj2, rVar);
                                            }
                                            lVar = lVar.d;
                                            rVar3 = rVar5;
                                            i9 = i23;
                                            length2 = i6;
                                        }
                                        i4 = i9;
                                        i5 = length2;
                                        k(lVarArr3, i10, rVar2);
                                        k(lVarArr3, i3, rVar);
                                        k(lVarArr7, i10, gVar2);
                                        gVar = gVar2;
                                        lVarArr6 = lVarArr7;
                                    } else {
                                        i4 = i9;
                                        i5 = length2;
                                        if (n2 instanceof q) {
                                            q qVar = (q) n2;
                                            r rVar6 = null;
                                            r rVar7 = null;
                                            l lVar2 = qVar.f;
                                            int i24 = 0;
                                            int i25 = 0;
                                            r rVar8 = null;
                                            while (lVar2 != null) {
                                                q qVar2 = qVar;
                                                int i26 = lVar2.a;
                                                g gVar4 = gVar2;
                                                r rVar9 = new r(i26, lVar2.b, lVar2.c, null, null);
                                                if ((i26 & length) == 0) {
                                                    rVar9.h = rVar7;
                                                    if (rVar7 == null) {
                                                        rVar2 = rVar9;
                                                    } else {
                                                        rVar7.d = rVar9;
                                                    }
                                                    i24++;
                                                    rVar7 = rVar9;
                                                } else {
                                                    rVar9.h = rVar6;
                                                    if (rVar6 == null) {
                                                        rVar8 = rVar9;
                                                    } else {
                                                        rVar6.d = rVar9;
                                                    }
                                                    i25++;
                                                    rVar6 = rVar9;
                                                }
                                                lVar2 = lVar2.d;
                                                qVar = qVar2;
                                                gVar2 = gVar4;
                                            }
                                            q qVar3 = qVar;
                                            g gVar5 = gVar2;
                                            l s2 = i24 <= 6 ? s(rVar2) : i25 != 0 ? new q(rVar2) : qVar3;
                                            l s3 = i25 <= 6 ? s(rVar8) : i24 != 0 ? new q(rVar8) : qVar3;
                                            k(lVarArr3, i10, s2);
                                            k(lVarArr3, i3, s3);
                                            gVar = gVar5;
                                            k(lVarArr, i10, gVar);
                                            lVarArr6 = lVarArr;
                                        }
                                    }
                                    z = true;
                                } else {
                                    i4 = i9;
                                    i5 = length2;
                                }
                                gVar = gVar2;
                                lVarArr6 = lVarArr7;
                            }
                            gVar2 = gVar;
                            i11 = i2;
                            i9 = i4;
                            length2 = i5;
                            concurrentHashMap = this;
                        }
                    }
                }
            }
        }
    }

    private final void q(l[] lVarArr, int i2) {
        int length = lVarArr.length;
        if (length < 64) {
            r(length << 1);
            return;
        }
        l n2 = n(lVarArr, i2);
        if (n2 == null || n2.a < 0) {
            return;
        }
        synchronized (n2) {
            if (n(lVarArr, i2) == n2) {
                r rVar = null;
                l lVar = n2;
                r rVar2 = null;
                while (lVar != null) {
                    r rVar3 = new r(lVar.a, lVar.b, lVar.c, null, null);
                    rVar3.h = rVar2;
                    if (rVar2 == null) {
                        rVar = rVar3;
                    } else {
                        rVar2.d = rVar3;
                    }
                    lVar = lVar.d;
                    rVar2 = rVar3;
                }
                k(lVarArr, i2, new q(rVar));
            }
        }
    }

    private final void r(int i2) {
        int length;
        l[] lVarArr;
        int o2 = i2 >= NUM ? NUM : o(i2 + (i2 >>> 1) + 1);
        while (true) {
            int i3 = this.sizeCtl;
            if (i3 >= 0) {
                l[] lVarArr2 = this.a;
                if (lVarArr2 == null || (length = lVarArr2.length) == 0) {
                    int i4 = i3 > o2 ? i3 : o2;
                    if (h.compareAndSwapInt(this, i, i3, -1)) {
                        try {
                            if (this.a == lVarArr2) {
                                this.a = new l[i4];
                                i3 = i4 - (i4 >>> 2);
                            }
                        } finally {
                            this.sizeCtl = i3;
                        }
                    } else {
                        continue;
                    }
                } else if (o2 <= i3 || length >= NUM) {
                    return;
                } else {
                    if (lVarArr2 == this.a) {
                        int j2 = j(length);
                        if (i3 < 0) {
                            if ((i3 >>> 16) != j2 || i3 == j2 + 1 || i3 == j2 + 65535 || (lVarArr = this.b) == null || this.transferIndex <= 0) {
                                return;
                            }
                            if (h.compareAndSwapInt(this, i, i3, i3 + 1)) {
                                p(lVarArr2, lVarArr);
                            }
                        } else if (h.compareAndSwapInt(this, i, i3, (j2 << 16) + 2)) {
                            p(lVarArr2, null);
                        }
                    } else {
                        continue;
                    }
                }
            } else {
                return;
            }
        }
    }

    private void readObject(ObjectInputStream objectInputStream) {
        long j2;
        int o2;
        boolean z;
        Object obj;
        this.sizeCtl = -1;
        objectInputStream.defaultReadObject();
        long j3 = 0;
        long j4 = 0;
        l lVar = null;
        while (true) {
            Object readObject = objectInputStream.readObject();
            Object readObject2 = objectInputStream.readObject();
            j2 = 1;
            if (readObject == null || readObject2 == null) {
                break;
            }
            j4++;
            lVar = new l(l(readObject.hashCode()), readObject, readObject2, lVar);
        }
        if (j4 == 0) {
            this.sizeCtl = 0;
            return;
        }
        if (j4 >= NUM) {
            o2 = NUM;
        } else {
            int i2 = (int) j4;
            o2 = o(i2 + (i2 >>> 1) + 1);
        }
        l[] lVarArr = new l[o2];
        int i3 = o2 - 1;
        while (lVar != null) {
            l lVar2 = lVar.d;
            int i4 = lVar.a;
            int i5 = i4 & i3;
            l n2 = n(lVarArr, i5);
            if (n2 == null) {
                z = true;
            } else {
                Object obj2 = lVar.b;
                if (n2.a >= 0) {
                    int i6 = 0;
                    for (l lVar3 = n2; lVar3 != null; lVar3 = lVar3.d) {
                        if (lVar3.a == i4 && ((obj = lVar3.b) == obj2 || (obj != null && obj2.equals(obj)))) {
                            z = false;
                            break;
                        }
                        i6++;
                    }
                    z = true;
                    if (z && i6 >= 8) {
                        j3++;
                        lVar.d = n2;
                        l lVar4 = lVar;
                        r rVar = null;
                        r rVar2 = null;
                        while (lVar4 != null) {
                            long j5 = j3;
                            r rVar3 = new r(lVar4.a, lVar4.b, lVar4.c, null, null);
                            rVar3.h = rVar2;
                            if (rVar2 == null) {
                                rVar = rVar3;
                            } else {
                                rVar2.d = rVar3;
                            }
                            lVar4 = lVar4.d;
                            rVar2 = rVar3;
                            j3 = j5;
                        }
                        k(lVarArr, i5, new q(rVar));
                    }
                } else if (((q) n2).f(i4, obj2, lVar.c) == null) {
                    j3 += j2;
                }
                z = false;
            }
            if (z) {
                j3++;
                lVar.d = n2;
                k(lVarArr, i5, lVar);
            }
            j2 = 1;
            lVar = lVar2;
        }
        this.a = lVarArr;
        this.sizeCtl = o2 - (o2 >>> 2);
        this.baseCount = j3;
    }

    static l s(l lVar) {
        l lVar2 = null;
        l lVar3 = null;
        while (lVar != null) {
            l lVar4 = new l(lVar.a, lVar.b, lVar.c, null);
            if (lVar3 == null) {
                lVar2 = lVar4;
            } else {
                lVar3.d = lVar4;
            }
            lVar = lVar.d;
            lVar3 = lVar4;
        }
        return lVar2;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) {
        int i2 = 1;
        int i3 = 0;
        while (i2 < 16) {
            i3++;
            i2 <<= 1;
        }
        int i4 = 32 - i3;
        int i5 = i2 - 1;
        n[] nVarArr = new n[16];
        for (int i6 = 0; i6 < 16; i6++) {
            nVarArr[i6] = new n(0.75f);
        }
        objectOutputStream.putFields().put("segments", nVarArr);
        objectOutputStream.putFields().put("segmentShift", i4);
        objectOutputStream.putFields().put("segmentMask", i5);
        objectOutputStream.writeFields();
        l[] lVarArr = this.a;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a2 = pVar.a();
                if (a2 == null) {
                    break;
                }
                objectOutputStream.writeObject(a2.b);
                objectOutputStream.writeObject(a2.c);
            }
        }
        objectOutputStream.writeObject(null);
        objectOutputStream.writeObject(null);
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public void clear() {
        l n2;
        l[] lVarArr = this.a;
        long j2 = 0;
        loop0: while (true) {
            int i2 = 0;
            while (lVarArr != null && i2 < lVarArr.length) {
                n2 = n(lVarArr, i2);
                if (n2 == null) {
                    i2++;
                } else {
                    int i3 = n2.a;
                    if (i3 == -1) {
                        break;
                    }
                    synchronized (n2) {
                        if (n(lVarArr, i2) == n2) {
                            for (l lVar = i3 >= 0 ? n2 : n2 instanceof q ? ((q) n2).f : null; lVar != null; lVar = lVar.d) {
                                j2--;
                            }
                            k(lVarArr, i2, null);
                            i2++;
                        }
                    }
                }
            }
            lVarArr = f(lVarArr, n2);
        }
        if (j2 != 0) {
            a(j2, -1);
        }
    }

    @Override // j$.util.Map
    public Object compute(Object obj, BiFunction biFunction) {
        int i2;
        l lVar;
        Object obj2;
        Object obj3;
        if (obj == null || biFunction == null) {
            throw null;
        }
        int l2 = l(obj.hashCode());
        l[] lVarArr = this.a;
        int i3 = 0;
        Object obj4 = null;
        int i4 = 0;
        while (true) {
            if (lVarArr != null) {
                int length = lVarArr.length;
                if (length != 0) {
                    int i5 = (length - 1) & l2;
                    l n2 = n(lVarArr, i5);
                    if (n2 == null) {
                        m mVar = new m();
                        synchronized (mVar) {
                            if (b(lVarArr, i5, null, mVar)) {
                                Object apply = biFunction.apply(obj, null);
                                if (apply != null) {
                                    lVar = new l(l2, obj, apply, null);
                                    i2 = 1;
                                } else {
                                    i2 = i3;
                                    lVar = null;
                                }
                                k(lVarArr, i5, lVar);
                                i3 = i2;
                                obj4 = apply;
                                i4 = 1;
                            }
                        }
                        if (i4 != 0) {
                            break;
                        }
                    } else {
                        int i6 = n2.a;
                        if (i6 == -1) {
                            lVarArr = f(lVarArr, n2);
                        } else {
                            synchronized (n2) {
                                if (n(lVarArr, i5) == n2) {
                                    if (i6 >= 0) {
                                        l lVar2 = null;
                                        l lVar3 = n2;
                                        int i7 = 1;
                                        while (true) {
                                            if (lVar3.a != l2 || ((obj3 = lVar3.b) != obj && (obj3 == null || !obj.equals(obj3)))) {
                                                l lVar4 = lVar3.d;
                                                if (lVar4 == null) {
                                                    Object apply2 = biFunction.apply(obj, null);
                                                    if (apply2 != null) {
                                                        lVar3.d = new l(l2, obj, apply2, null);
                                                        i3 = 1;
                                                    }
                                                    obj2 = apply2;
                                                } else {
                                                    i7++;
                                                    lVar2 = lVar3;
                                                    lVar3 = lVar4;
                                                }
                                            }
                                        }
                                        obj2 = biFunction.apply(obj, lVar3.c);
                                        if (obj2 != null) {
                                            lVar3.c = obj2;
                                        } else {
                                            l lVar5 = lVar3.d;
                                            if (lVar2 != null) {
                                                lVar2.d = lVar5;
                                            } else {
                                                k(lVarArr, i5, lVar5);
                                            }
                                            i3 = -1;
                                        }
                                        i4 = i7;
                                        obj4 = obj2;
                                    } else if (n2 instanceof q) {
                                        q qVar = (q) n2;
                                        r rVar = qVar.e;
                                        r b2 = rVar != null ? rVar.b(l2, obj, null) : null;
                                        Object apply3 = biFunction.apply(obj, b2 == null ? null : b2.c);
                                        if (apply3 != null) {
                                            if (b2 != null) {
                                                b2.c = apply3;
                                            } else {
                                                qVar.f(l2, obj, apply3);
                                                i3 = 1;
                                            }
                                        } else if (b2 != null) {
                                            if (qVar.g(b2)) {
                                                k(lVarArr, i5, s(qVar.f));
                                            }
                                            i3 = -1;
                                        }
                                        obj4 = apply3;
                                        i4 = 1;
                                    }
                                }
                            }
                            if (i4 != 0) {
                                if (i4 >= 8) {
                                    q(lVarArr, i5);
                                }
                            }
                        }
                    }
                }
            }
            lVarArr = g();
        }
        if (i3 != 0) {
            a(i3, i4);
        }
        return obj4;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public /* synthetic */ Object compute(Object obj, java.util.function.BiFunction biFunction) {
        return compute(obj, CLASSNAMEs.a(biFunction));
    }

    @Override // j$.util.Map
    public Object computeIfAbsent(Object obj, Function function) {
        int i2;
        Object apply;
        r b2;
        Object obj2;
        Object obj3;
        if (obj == null || function == null) {
            throw null;
        }
        int l2 = l(obj.hashCode());
        l[] lVarArr = this.a;
        Object obj4 = null;
        int i3 = 0;
        while (true) {
            if (lVarArr != null) {
                int length = lVarArr.length;
                if (length != 0) {
                    int i4 = (length - 1) & l2;
                    l n2 = n(lVarArr, i4);
                    boolean z = true;
                    if (n2 == null) {
                        m mVar = new m();
                        synchronized (mVar) {
                            if (b(lVarArr, i4, null, mVar)) {
                                Object apply2 = function.apply(obj);
                                k(lVarArr, i4, apply2 != null ? new l(l2, obj, apply2, null) : null);
                                obj4 = apply2;
                                i3 = 1;
                            }
                        }
                        if (i3 != 0) {
                            break;
                        }
                    } else {
                        int i5 = n2.a;
                        if (i5 == -1) {
                            lVarArr = f(lVarArr, n2);
                        } else {
                            synchronized (n2) {
                                if (n(lVarArr, i4) == n2) {
                                    if (i5 >= 0) {
                                        l lVar = n2;
                                        i2 = 1;
                                        while (true) {
                                            if (lVar.a != l2 || ((obj3 = lVar.b) != obj && (obj3 == null || !obj.equals(obj3)))) {
                                                l lVar2 = lVar.d;
                                                if (lVar2 == null) {
                                                    apply = function.apply(obj);
                                                    if (apply != null) {
                                                        lVar.d = new l(l2, obj, apply, null);
                                                    }
                                                } else {
                                                    i2++;
                                                    lVar = lVar2;
                                                }
                                            }
                                        }
                                        obj2 = lVar.c;
                                        z = false;
                                        int i6 = i2;
                                        obj4 = obj2;
                                        i3 = i6;
                                    } else if (n2 instanceof q) {
                                        i2 = 2;
                                        q qVar = (q) n2;
                                        r rVar = qVar.e;
                                        if (rVar == null || (b2 = rVar.b(l2, obj, null)) == null) {
                                            apply = function.apply(obj);
                                            if (apply != null) {
                                                qVar.f(l2, obj, apply);
                                                i3 = i2;
                                                obj4 = apply;
                                            }
                                            z = false;
                                            i3 = i2;
                                            obj4 = apply;
                                        } else {
                                            obj2 = b2.c;
                                            z = false;
                                            int i62 = i2;
                                            obj4 = obj2;
                                            i3 = i62;
                                        }
                                    }
                                }
                                z = false;
                            }
                            if (i3 != 0) {
                                if (i3 >= 8) {
                                    q(lVarArr, i4);
                                }
                                if (!z) {
                                    return obj4;
                                }
                            }
                        }
                    }
                }
            }
            lVarArr = g();
        }
        if (obj4 != null) {
            a(1L, i3);
        }
        return obj4;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public /* synthetic */ Object computeIfAbsent(Object obj, java.util.function.Function function) {
        return computeIfAbsent(obj, M.a(function));
    }

    @Override // j$.util.Map
    public Object computeIfPresent(Object obj, BiFunction biFunction) {
        r b2;
        l s2;
        Object obj2;
        if (obj == null || biFunction == null) {
            throw null;
        }
        int l2 = l(obj.hashCode());
        l[] lVarArr = this.a;
        int i2 = 0;
        Object obj3 = null;
        int i3 = 0;
        while (true) {
            if (lVarArr != null) {
                int length = lVarArr.length;
                if (length != 0) {
                    int i4 = (length - 1) & l2;
                    l n2 = n(lVarArr, i4);
                    if (n2 == null) {
                        break;
                    }
                    int i5 = n2.a;
                    if (i5 == -1) {
                        lVarArr = f(lVarArr, n2);
                    } else {
                        synchronized (n2) {
                            if (n(lVarArr, i4) == n2) {
                                if (i5 >= 0) {
                                    i3 = 1;
                                    l lVar = null;
                                    l lVar2 = n2;
                                    while (true) {
                                        if (lVar2.a != l2 || ((obj2 = lVar2.b) != obj && (obj2 == null || !obj.equals(obj2)))) {
                                            l lVar3 = lVar2.d;
                                            if (lVar3 == null) {
                                                break;
                                            }
                                            i3++;
                                            lVar = lVar2;
                                            lVar2 = lVar3;
                                        }
                                    }
                                    obj3 = biFunction.apply(obj, lVar2.c);
                                    if (obj3 != null) {
                                        lVar2.c = obj3;
                                    } else {
                                        s2 = lVar2.d;
                                        if (lVar != null) {
                                            lVar.d = s2;
                                            i2 = -1;
                                        }
                                        k(lVarArr, i4, s2);
                                        i2 = -1;
                                    }
                                } else if (n2 instanceof q) {
                                    i3 = 2;
                                    q qVar = (q) n2;
                                    r rVar = qVar.e;
                                    if (rVar != null && (b2 = rVar.b(l2, obj, null)) != null) {
                                        obj3 = biFunction.apply(obj, b2.c);
                                        if (obj3 != null) {
                                            b2.c = obj3;
                                        } else {
                                            if (qVar.g(b2)) {
                                                s2 = s(qVar.f);
                                                k(lVarArr, i4, s2);
                                            }
                                            i2 = -1;
                                        }
                                    }
                                }
                            }
                        }
                        if (i3 != 0) {
                            break;
                        }
                    }
                }
            }
            lVarArr = g();
        }
        if (i2 != 0) {
            a(i2, i3);
        }
        return obj3;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public /* synthetic */ Object computeIfPresent(Object obj, java.util.function.BiFunction biFunction) {
        return computeIfPresent(obj, CLASSNAMEs.a(biFunction));
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public boolean containsKey(Object obj) {
        return get(obj) != null;
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public boolean containsValue(Object obj) {
        obj.getClass();
        l[] lVarArr = this.a;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a2 = pVar.a();
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

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        e eVar = this.f;
        if (eVar != null) {
            return eVar;
        }
        e eVar2 = new e(this);
        this.f = eVar2;
        return eVar2;
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public boolean equals(Object obj) {
        V value;
        V v;
        if (obj != this) {
            if (!(obj instanceof Map)) {
                return false;
            }
            Map map = (Map) obj;
            l[] lVarArr = this.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            p pVar = new p(lVarArr, length, 0, length);
            while (true) {
                l a2 = pVar.a();
                if (a2 == null) {
                    for (Map.Entry<K, V> entry : map.entrySet()) {
                        K key = entry.getKey();
                        if (key == null || (value = entry.getValue()) == null || (v = get(key)) == null || (value != v && !value.equals(v))) {
                            return false;
                        }
                    }
                    return true;
                }
                Object obj2 = a2.c;
                Object obj3 = map.get(a2.b);
                if (obj3 == null || (obj3 != obj2 && !obj3.equals(obj2))) {
                    break;
                }
            }
            return false;
        }
        return true;
    }

    final l[] f(l[] lVarArr, l lVar) {
        l[] lVarArr2;
        int i2;
        if (!(lVar instanceof g) || (lVarArr2 = ((g) lVar).e) == null) {
            return this.a;
        }
        int j2 = j(lVarArr.length);
        while (true) {
            if (lVarArr2 != this.b || this.a != lVarArr || (i2 = this.sizeCtl) >= 0 || (i2 >>> 16) != j2 || i2 == j2 + 1 || i2 == 65535 + j2 || this.transferIndex <= 0) {
                break;
            } else if (h.compareAndSwapInt(this, i, i2, i2 + 1)) {
                p(lVarArr, lVarArr2);
                break;
            }
        }
        return lVarArr2;
    }

    @Override // j$.util.concurrent.b, j$.util.Map
    public void forEach(BiConsumer biConsumer) {
        biConsumer.getClass();
        l[] lVarArr = this.a;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a2 = pVar.a();
                if (a2 == null) {
                    return;
                }
                biConsumer.accept(a2.b, a2.c);
            }
        }
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public /* synthetic */ void forEach(java.util.function.BiConsumer biConsumer) {
        forEach(CLASSNAMEq.a(biConsumer));
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x004d, code lost:
        return (V) r1.c;
     */
    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public V get(java.lang.Object r5) {
        /*
            r4 = this;
            int r0 = r5.hashCode()
            int r0 = l(r0)
            j$.util.concurrent.ConcurrentHashMap$l[] r1 = r4.a
            r2 = 0
            if (r1 == 0) goto L4e
            int r3 = r1.length
            if (r3 <= 0) goto L4e
            int r3 = r3 + (-1)
            r3 = r3 & r0
            j$.util.concurrent.ConcurrentHashMap$l r1 = n(r1, r3)
            if (r1 == 0) goto L4e
            int r3 = r1.a
            if (r3 != r0) goto L2c
            java.lang.Object r3 = r1.b
            if (r3 == r5) goto L29
            if (r3 == 0) goto L37
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L37
        L29:
            java.lang.Object r5 = r1.c
            return r5
        L2c:
            if (r3 >= 0) goto L37
            j$.util.concurrent.ConcurrentHashMap$l r5 = r1.a(r0, r5)
            if (r5 == 0) goto L36
            java.lang.Object r2 = r5.c
        L36:
            return r2
        L37:
            j$.util.concurrent.ConcurrentHashMap$l r1 = r1.d
            if (r1 == 0) goto L4e
            int r3 = r1.a
            if (r3 != r0) goto L37
            java.lang.Object r3 = r1.b
            if (r3 == r5) goto L4b
            if (r3 == 0) goto L37
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L37
        L4b:
            java.lang.Object r5 = r1.c
            return r5
        L4e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.get(java.lang.Object):java.lang.Object");
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public Object getOrDefault(Object obj, Object obj2) {
        V v = get(obj);
        return v == null ? obj2 : v;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x0053, code lost:
        if (r11 == false) goto L35;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    final java.lang.Object h(java.lang.Object r9, java.lang.Object r10, boolean r11) {
        /*
            r8 = this;
            r0 = 0
            if (r9 == 0) goto L98
            if (r10 == 0) goto L98
            int r1 = r9.hashCode()
            int r1 = l(r1)
            r2 = 0
            j$.util.concurrent.ConcurrentHashMap$l[] r3 = r8.a
        L10:
            if (r3 == 0) goto L92
            int r4 = r3.length
            if (r4 != 0) goto L17
            goto L92
        L17:
            int r4 = r4 + (-1)
            r4 = r4 & r1
            j$.util.concurrent.ConcurrentHashMap$l r5 = n(r3, r4)
            if (r5 != 0) goto L2c
            j$.util.concurrent.ConcurrentHashMap$l r5 = new j$.util.concurrent.ConcurrentHashMap$l
            r5.<init>(r1, r9, r10, r0)
            boolean r4 = b(r3, r4, r0, r5)
            if (r4 == 0) goto L10
            goto L89
        L2c:
            int r6 = r5.a
            r7 = -1
            if (r6 != r7) goto L36
            j$.util.concurrent.ConcurrentHashMap$l[] r3 = r8.f(r3, r5)
            goto L10
        L36:
            monitor-enter(r5)
            j$.util.concurrent.ConcurrentHashMap$l r7 = n(r3, r4)     // Catch: java.lang.Throwable -> L8f
            if (r7 != r5) goto L7b
            if (r6 < 0) goto L68
            r2 = 1
            r6 = r5
        L41:
            int r7 = r6.a     // Catch: java.lang.Throwable -> L8f
            if (r7 != r1) goto L58
            java.lang.Object r7 = r6.b     // Catch: java.lang.Throwable -> L8f
            if (r7 == r9) goto L51
            if (r7 == 0) goto L58
            boolean r7 = r9.equals(r7)     // Catch: java.lang.Throwable -> L8f
            if (r7 == 0) goto L58
        L51:
            java.lang.Object r7 = r6.c     // Catch: java.lang.Throwable -> L8f
            if (r11 != 0) goto L7c
        L55:
            r6.c = r10     // Catch: java.lang.Throwable -> L8f
            goto L7c
        L58:
            j$.util.concurrent.ConcurrentHashMap$l r7 = r6.d     // Catch: java.lang.Throwable -> L8f
            if (r7 != 0) goto L64
            j$.util.concurrent.ConcurrentHashMap$l r7 = new j$.util.concurrent.ConcurrentHashMap$l     // Catch: java.lang.Throwable -> L8f
            r7.<init>(r1, r9, r10, r0)     // Catch: java.lang.Throwable -> L8f
            r6.d = r7     // Catch: java.lang.Throwable -> L8f
            goto L7b
        L64:
            int r2 = r2 + 1
            r6 = r7
            goto L41
        L68:
            boolean r6 = r5 instanceof j$.util.concurrent.ConcurrentHashMap.q     // Catch: java.lang.Throwable -> L8f
            if (r6 == 0) goto L7b
            r2 = 2
            r6 = r5
            j$.util.concurrent.ConcurrentHashMap$q r6 = (j$.util.concurrent.ConcurrentHashMap.q) r6     // Catch: java.lang.Throwable -> L8f
            j$.util.concurrent.ConcurrentHashMap$r r6 = r6.f(r1, r9, r10)     // Catch: java.lang.Throwable -> L8f
            if (r6 == 0) goto L7b
            java.lang.Object r7 = r6.c     // Catch: java.lang.Throwable -> L8f
            if (r11 != 0) goto L7c
            goto L55
        L7b:
            r7 = r0
        L7c:
            monitor-exit(r5)     // Catch: java.lang.Throwable -> L8f
            if (r2 == 0) goto L10
            r9 = 8
            if (r2 < r9) goto L86
            r8.q(r3, r4)
        L86:
            if (r7 == 0) goto L89
            return r7
        L89:
            r9 = 1
            r8.a(r9, r2)
            return r0
        L8f:
            r9 = move-exception
            monitor-exit(r5)     // Catch: java.lang.Throwable -> L8f
            throw r9
        L92:
            j$.util.concurrent.ConcurrentHashMap$l[] r3 = r8.g()
            goto L10
        L98:
            goto L9a
        L99:
            throw r0
        L9a:
            goto L99
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.h(java.lang.Object, java.lang.Object, boolean):java.lang.Object");
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public int hashCode() {
        l[] lVarArr = this.a;
        int i2 = 0;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a2 = pVar.a();
                if (a2 == null) {
                    break;
                }
                i2 += a2.c.hashCode() ^ a2.b.hashCode();
            }
        }
        return i2;
    }

    final Object i(Object obj, Object obj2, Object obj3) {
        int length;
        int i2;
        l n2;
        Object obj4;
        r b2;
        l s2;
        Object obj5;
        int l2 = l(obj.hashCode());
        l[] lVarArr = this.a;
        while (true) {
            if (lVarArr == null || (length = lVarArr.length) == 0 || (n2 = n(lVarArr, (i2 = (length - 1) & l2))) == null) {
                break;
            }
            int i3 = n2.a;
            if (i3 == -1) {
                lVarArr = f(lVarArr, n2);
            } else {
                boolean z = false;
                synchronized (n2) {
                    if (n(lVarArr, i2) == n2) {
                        if (i3 >= 0) {
                            l lVar = null;
                            l lVar2 = n2;
                            while (true) {
                                if (lVar2.a != l2 || ((obj5 = lVar2.b) != obj && (obj5 == null || !obj.equals(obj5)))) {
                                    l lVar3 = lVar2.d;
                                    if (lVar3 == null) {
                                        break;
                                    }
                                    lVar = lVar2;
                                    lVar2 = lVar3;
                                }
                            }
                            obj4 = lVar2.c;
                            if (obj3 == null || obj3 == obj4 || (obj4 != null && obj3.equals(obj4))) {
                                if (obj2 != null) {
                                    lVar2.c = obj2;
                                } else if (lVar != null) {
                                    lVar.d = lVar2.d;
                                } else {
                                    s2 = lVar2.d;
                                    k(lVarArr, i2, s2);
                                }
                                z = true;
                            }
                            obj4 = null;
                            z = true;
                        } else if (n2 instanceof q) {
                            q qVar = (q) n2;
                            r rVar = qVar.e;
                            if (rVar != null && (b2 = rVar.b(l2, obj, null)) != null) {
                                obj4 = b2.c;
                                if (obj3 == null || obj3 == obj4 || (obj4 != null && obj3.equals(obj4))) {
                                    if (obj2 != null) {
                                        b2.c = obj2;
                                    } else if (qVar.g(b2)) {
                                        s2 = s(qVar.f);
                                        k(lVarArr, i2, s2);
                                    }
                                    z = true;
                                }
                            }
                            obj4 = null;
                            z = true;
                        }
                    }
                    obj4 = null;
                }
                if (z) {
                    if (obj4 != null) {
                        if (obj2 == null) {
                            a(-1L, -1);
                        }
                        return obj4;
                    }
                }
            }
        }
        return null;
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public boolean isEmpty() {
        return m() <= 0;
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public Set<K> keySet() {
        i iVar = this.d;
        if (iVar != null) {
            return iVar;
        }
        i iVar2 = new i(this, null);
        this.d = iVar2;
        return iVar2;
    }

    final long m() {
        c[] cVarArr = this.c;
        long j2 = this.baseCount;
        if (cVarArr != null) {
            for (c cVar : cVarArr) {
                if (cVar != null) {
                    j2 += cVar.value;
                }
            }
        }
        return j2;
    }

    @Override // j$.util.Map
    public Object merge(Object obj, Object obj2, BiFunction biFunction) {
        int i2;
        Object obj3;
        Object obj4;
        Object obj5 = obj2;
        if (obj == null || obj5 == null || biFunction == null) {
            throw null;
        }
        int l2 = l(obj.hashCode());
        l[] lVarArr = this.a;
        int i3 = 0;
        Object obj6 = null;
        int i4 = 0;
        while (true) {
            if (lVarArr != null) {
                int length = lVarArr.length;
                if (length != 0) {
                    int i5 = (length - 1) & l2;
                    l n2 = n(lVarArr, i5);
                    i2 = 1;
                    if (n2 != null) {
                        int i6 = n2.a;
                        if (i6 == -1) {
                            lVarArr = f(lVarArr, n2);
                        } else {
                            synchronized (n2) {
                                if (n(lVarArr, i5) == n2) {
                                    if (i6 >= 0) {
                                        l lVar = null;
                                        l lVar2 = n2;
                                        int i7 = 1;
                                        while (true) {
                                            if (lVar2.a != l2 || ((obj4 = lVar2.b) != obj && (obj4 == null || !obj.equals(obj4)))) {
                                                l lVar3 = lVar2.d;
                                                if (lVar3 == null) {
                                                    lVar2.d = new l(l2, obj, obj5, null);
                                                    obj3 = obj5;
                                                    break;
                                                }
                                                i7++;
                                                lVar = lVar2;
                                                lVar2 = lVar3;
                                            }
                                        }
                                        Object apply = biFunction.apply(lVar2.c, obj5);
                                        if (apply != null) {
                                            lVar2.c = apply;
                                        } else {
                                            l lVar4 = lVar2.d;
                                            if (lVar != null) {
                                                lVar.d = lVar4;
                                            } else {
                                                k(lVarArr, i5, lVar4);
                                            }
                                            i3 = -1;
                                        }
                                        i2 = i3;
                                        obj3 = apply;
                                        i4 = i7;
                                        obj6 = obj3;
                                        i3 = i2;
                                    } else if (n2 instanceof q) {
                                        i4 = 2;
                                        q qVar = (q) n2;
                                        r rVar = qVar.e;
                                        r b2 = rVar == null ? null : rVar.b(l2, obj, null);
                                        Object apply2 = b2 == null ? obj5 : biFunction.apply(b2.c, obj5);
                                        if (apply2 != null) {
                                            if (b2 != null) {
                                                b2.c = apply2;
                                            } else {
                                                qVar.f(l2, obj, apply2);
                                                i3 = 1;
                                            }
                                        } else if (b2 != null) {
                                            if (qVar.g(b2)) {
                                                k(lVarArr, i5, s(qVar.f));
                                            }
                                            i3 = -1;
                                        }
                                        obj6 = apply2;
                                    }
                                }
                            }
                            if (i4 != 0) {
                                if (i4 >= 8) {
                                    q(lVarArr, i5);
                                }
                                i2 = i3;
                                obj5 = obj6;
                            }
                        }
                    } else if (b(lVarArr, i5, null, new l(l2, obj, obj5, null))) {
                        break;
                    }
                }
            }
            lVarArr = g();
        }
        if (i2 != 0) {
            a(i2, i4);
        }
        return obj5;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public /* synthetic */ Object merge(Object obj, Object obj2, java.util.function.BiFunction biFunction) {
        return merge(obj, obj2, CLASSNAMEs.a(biFunction));
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public V put(K k2, V v) {
        return (V) h(k2, v, false);
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        r(map.size());
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            h(entry.getKey(), entry.getValue(), false);
        }
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public V putIfAbsent(K k2, V v) {
        return (V) h(k2, v, true);
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public V remove(Object obj) {
        return (V) i(obj, null, null);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public boolean remove(Object obj, Object obj2) {
        obj.getClass();
        return (obj2 == null || i(obj, null, obj2) == null) ? false : true;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public Object replace(Object obj, Object obj2) {
        if (obj == null || obj2 == null) {
            throw null;
        }
        return i(obj, obj2, null);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public boolean replace(Object obj, Object obj2, Object obj3) {
        if (obj == null || obj2 == null || obj3 == null) {
            throw null;
        }
        return i(obj, obj3, obj2) != null;
    }

    @Override // j$.util.Map
    public void replaceAll(BiFunction biFunction) {
        biFunction.getClass();
        l[] lVarArr = this.a;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a2 = pVar.a();
                if (a2 == null) {
                    return;
                }
                Object obj = a2.c;
                Object obj2 = a2.b;
                do {
                    Object apply = biFunction.apply(obj2, obj);
                    apply.getClass();
                    if (i(obj2, apply, obj) == null) {
                        obj = get(obj2);
                    }
                } while (obj != null);
            }
        }
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public /* synthetic */ void replaceAll(java.util.function.BiFunction biFunction) {
        replaceAll(CLASSNAMEs.a(biFunction));
    }

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public int size() {
        long m2 = m();
        if (m2 < 0) {
            return 0;
        }
        if (m2 <= 2147483647L) {
            return (int) m2;
        }
        return Integer.MAX_VALUE;
    }

    @Override // java.util.AbstractMap
    public String toString() {
        l[] lVarArr = this.a;
        int length = lVarArr == null ? 0 : lVarArr.length;
        p pVar = new p(lVarArr, length, 0, length);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        l a2 = pVar.a();
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
                a2 = pVar.a();
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

    @Override // java.util.AbstractMap, java.util.Map, j$.util.Map
    public Collection values() {
        u uVar = this.e;
        if (uVar != null) {
            return uVar;
        }
        u uVar2 = new u(this);
        this.e = uVar2;
        return uVar2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static abstract class b implements Collection, Serializable {
        final ConcurrentHashMap a;

        b(ConcurrentHashMap concurrentHashMap) {
            this.a = concurrentHashMap;
        }

        @Override // java.util.Collection
        public final void clear() {
            this.a.clear();
        }

        @Override // java.util.Collection
        public abstract boolean contains(Object obj);

        /* JADX WARN: Removed duplicated region for block: B:6:0x000c  */
        @Override // java.util.Collection
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final boolean containsAll(java.util.Collection r2) {
            /*
                r1 = this;
                if (r2 == r1) goto L1a
                java.util.Iterator r2 = r2.iterator()
            L6:
                boolean r0 = r2.hasNext()
                if (r0 == 0) goto L1a
                java.lang.Object r0 = r2.next()
                if (r0 == 0) goto L18
                boolean r0 = r1.contains(r0)
                if (r0 != 0) goto L6
            L18:
                r2 = 0
                return r2
            L1a:
                r2 = 1
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.b.containsAll(java.util.Collection):boolean");
        }

        @Override // java.util.Collection
        public final boolean isEmpty() {
            return this.a.isEmpty();
        }

        @Override // java.util.Collection, java.lang.Iterable
        public abstract java.util.Iterator iterator();

        @Override // java.util.Collection
        public final boolean removeAll(Collection collection) {
            collection.getClass();
            java.util.Iterator it = iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (collection.contains(it.next())) {
                    it.remove();
                    z = true;
                }
            }
            return z;
        }

        @Override // java.util.Collection
        public final boolean retainAll(Collection collection) {
            collection.getClass();
            java.util.Iterator it = iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (!collection.contains(it.next())) {
                    it.remove();
                    z = true;
                }
            }
            return z;
        }

        @Override // java.util.Collection
        public final int size() {
            return this.a.size();
        }

        @Override // java.util.Collection
        public final Object[] toArray() {
            long m = this.a.m();
            if (m < 0) {
                m = 0;
            }
            if (m <= NUM) {
                int i = (int) m;
                Object[] objArr = new Object[i];
                int i2 = 0;
                java.util.Iterator it = iterator();
                while (it.hasNext()) {
                    Object next = it.next();
                    if (i2 == i) {
                        int i3 = NUM;
                        if (i >= NUM) {
                            throw new OutOfMemoryError("Required array size too large");
                        }
                        if (i < NUM) {
                            i3 = (i >>> 1) + 1 + i;
                        }
                        objArr = Arrays.copyOf(objArr, i3);
                        i = i3;
                    }
                    objArr[i2] = next;
                    i2++;
                }
                return i2 == i ? objArr : Arrays.copyOf(objArr, i2);
            }
            throw new OutOfMemoryError("Required array size too large");
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            java.util.Iterator it = iterator();
            if (it.hasNext()) {
                while (true) {
                    Object next = it.next();
                    if (next == this) {
                        next = "(this Collection)";
                    }
                    sb.append(next);
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

        @Override // java.util.Collection
        public final Object[] toArray(Object[] objArr) {
            long m = this.a.m();
            if (m < 0) {
                m = 0;
            }
            if (m <= NUM) {
                int i = (int) m;
                Object[] objArr2 = objArr.length >= i ? objArr : (Object[]) Array.newInstance(objArr.getClass().getComponentType(), i);
                int length = objArr2.length;
                int i2 = 0;
                java.util.Iterator it = iterator();
                while (it.hasNext()) {
                    Object next = it.next();
                    if (i2 == length) {
                        int i3 = NUM;
                        if (length >= NUM) {
                            throw new OutOfMemoryError("Required array size too large");
                        }
                        if (length < NUM) {
                            i3 = (length >>> 1) + 1 + length;
                        }
                        objArr2 = Arrays.copyOf(objArr2, i3);
                        length = i3;
                    }
                    objArr2[i2] = next;
                    i2++;
                }
                if (objArr != objArr2 || i2 >= length) {
                    return i2 == length ? objArr2 : Arrays.copyOf(objArr2, i2);
                }
                objArr2[i2] = null;
                return objArr2;
            }
            throw new OutOfMemoryError("Required array size too large");
        }
    }
}

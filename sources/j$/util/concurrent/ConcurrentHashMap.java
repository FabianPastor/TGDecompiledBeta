package j$.util.concurrent;

import j$.util.CLASSNAMEa;
import j$.util.CLASSNAMEb;
import j$.util.Iterator;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Stream;
import sun.misc.Unsafe;

public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable, b {
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

    static class a extends p {
        final ConcurrentHashMap i;
        l j;

        a(l[] lVarArr, int i2, int i3, int i4, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i2, i3, i4);
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
                this.i.i(lVar.b, (Object) null, (Object) null);
                return;
            }
            throw new IllegalStateException();
        }
    }

    static final class c {
        volatile long value;

        c(long j) {
            this.value = j;
        }
    }

    static final class d extends a implements Iterator, j$.util.Iterator {
        d(l[] lVarArr, int i, int i2, int i3, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i, i2, i3, concurrentHashMap);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, CLASSNAMEw.b(consumer));
        }

        public Object next() {
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

    static final class e extends b implements Set, CLASSNAMEb {
        e(ConcurrentHashMap concurrentHashMap) {
            super(concurrentHashMap);
        }

        /* renamed from: a */
        public boolean add(Map.Entry entry) {
            return this.a.h(entry.getKey(), entry.getValue(), false) == null;
        }

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

        /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
            r0 = r2.a.get((r0 = r3.getKey()));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0014, code lost:
            r3 = (r3 = (java.util.Map.Entry) r3).getValue();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean contains(java.lang.Object r3) {
            /*
                r2 = this;
                boolean r0 = r3 instanceof java.util.Map.Entry
                if (r0 == 0) goto L_0x0024
                java.util.Map$Entry r3 = (java.util.Map.Entry) r3
                java.lang.Object r0 = r3.getKey()
                if (r0 == 0) goto L_0x0024
                j$.util.concurrent.ConcurrentHashMap r1 = r2.a
                java.lang.Object r0 = r1.get(r0)
                if (r0 == 0) goto L_0x0024
                java.lang.Object r3 = r3.getValue()
                if (r3 == 0) goto L_0x0024
                if (r3 == r0) goto L_0x0022
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0024
            L_0x0022:
                r3 = 1
                goto L_0x0025
            L_0x0024:
                r3 = 0
            L_0x0025:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.e.contains(java.lang.Object):boolean");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
            r2 = (java.util.Set) r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean equals(java.lang.Object r2) {
            /*
                r1 = this;
                boolean r0 = r2 instanceof java.util.Set
                if (r0 == 0) goto L_0x0016
                java.util.Set r2 = (java.util.Set) r2
                if (r2 == r1) goto L_0x0014
                boolean r0 = r1.containsAll(r2)
                if (r0 == 0) goto L_0x0016
                boolean r2 = r2.containsAll(r1)
                if (r2 == 0) goto L_0x0016
            L_0x0014:
                r2 = 1
                goto L_0x0017
            L_0x0016:
                r2 = 0
            L_0x0017:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.e.equals(java.lang.Object):boolean");
        }

        public void forEach(Consumer consumer) {
            consumer.getClass();
            l[] lVarArr = this.a.a;
            if (lVarArr != null) {
                p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
                while (true) {
                    l a = pVar.a();
                    if (a != null) {
                        consumer.accept(new k(a.b, a.c, this.a));
                    } else {
                        return;
                    }
                }
            }
        }

        public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
            forEach(CLASSNAMEw.b(consumer));
        }

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

        public java.util.Iterator iterator() {
            ConcurrentHashMap concurrentHashMap = this.a;
            l[] lVarArr = concurrentHashMap.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            return new d(lVarArr, length, 0, length, concurrentHashMap);
        }

        public /* synthetic */ boolean k(Predicate predicate) {
            return CLASSNAMEa.h(this, predicate);
        }

        public /* synthetic */ Stream parallelStream() {
            return P0.n0(CLASSNAMEa.g(this));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
            r3 = (java.util.Map.Entry) r3;
            r0 = r3.getKey();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
            r3 = r3.getValue();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean remove(java.lang.Object r3) {
            /*
                r2 = this;
                boolean r0 = r3 instanceof java.util.Map.Entry
                if (r0 == 0) goto L_0x001c
                java.util.Map$Entry r3 = (java.util.Map.Entry) r3
                java.lang.Object r0 = r3.getKey()
                if (r0 == 0) goto L_0x001c
                java.lang.Object r3 = r3.getValue()
                if (r3 == 0) goto L_0x001c
                j$.util.concurrent.ConcurrentHashMap r1 = r2.a
                boolean r3 = r1.remove(r0, r3)
                if (r3 == 0) goto L_0x001c
                r3 = 1
                goto L_0x001d
            L_0x001c:
                r3 = 0
            L_0x001d:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.e.remove(java.lang.Object):boolean");
        }

        public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
            return CLASSNAMEa.h(this, x0.a(predicate));
        }

        public j$.util.u spliterator() {
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
    }

    static final class f extends p implements j$.util.u {
        final ConcurrentHashMap i;
        long j;

        f(l[] lVarArr, int i2, int i3, int i4, long j2, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i2, i3, i4);
            this.i = concurrentHashMap;
            this.j = j2;
        }

        public boolean b(Consumer consumer) {
            consumer.getClass();
            l a = a();
            if (a == null) {
                return false;
            }
            consumer.accept(new k(a.b, a.c, this.i));
            return true;
        }

        public int characteristics() {
            return 4353;
        }

        public long estimateSize() {
            return this.j;
        }

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

        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return CLASSNAMEa.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i2) {
            return CLASSNAMEa.f(this, i2);
        }

        public j$.util.u trySplit() {
            int i2 = this.f;
            int i3 = this.g;
            int i4 = (i2 + i3) >>> 1;
            if (i4 <= i2) {
                return null;
            }
            l[] lVarArr = this.a;
            int i5 = this.h;
            this.g = i4;
            long j2 = this.j >>> 1;
            this.j = j2;
            return new f(lVarArr, i5, i4, i3, j2, this.i);
        }
    }

    static final class g extends l {
        final l[] e;

        g(l[] lVarArr) {
            super(-1, (Object) null, (Object) null, (l) null);
            this.e = lVarArr;
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0027, code lost:
            if ((r0 instanceof j$.util.concurrent.ConcurrentHashMap.g) == false) goto L_0x002e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0029, code lost:
            r0 = ((j$.util.concurrent.ConcurrentHashMap.g) r0).e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0032, code lost:
            return r0.a(r5, r6);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public j$.util.concurrent.ConcurrentHashMap.l a(int r5, java.lang.Object r6) {
            /*
                r4 = this;
                j$.util.concurrent.ConcurrentHashMap$l[] r0 = r4.e
            L_0x0002:
                r1 = 0
                if (r0 == 0) goto L_0x0037
                int r2 = r0.length
                if (r2 == 0) goto L_0x0037
                int r2 = r2 + -1
                r2 = r2 & r5
                j$.util.concurrent.ConcurrentHashMap$l r0 = j$.util.concurrent.ConcurrentHashMap.n(r0, r2)
                if (r0 != 0) goto L_0x0012
                goto L_0x0037
            L_0x0012:
                int r2 = r0.a
                if (r2 != r5) goto L_0x0023
                java.lang.Object r3 = r0.b
                if (r3 == r6) goto L_0x0022
                if (r3 == 0) goto L_0x0023
                boolean r3 = r6.equals(r3)
                if (r3 == 0) goto L_0x0023
            L_0x0022:
                return r0
            L_0x0023:
                if (r2 >= 0) goto L_0x0033
                boolean r1 = r0 instanceof j$.util.concurrent.ConcurrentHashMap.g
                if (r1 == 0) goto L_0x002e
                j$.util.concurrent.ConcurrentHashMap$g r0 = (j$.util.concurrent.ConcurrentHashMap.g) r0
                j$.util.concurrent.ConcurrentHashMap$l[] r0 = r0.e
                goto L_0x0002
            L_0x002e:
                j$.util.concurrent.ConcurrentHashMap$l r5 = r0.a(r5, r6)
                return r5
            L_0x0033:
                j$.util.concurrent.ConcurrentHashMap$l r0 = r0.d
                if (r0 != 0) goto L_0x0012
            L_0x0037:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.g.a(int, java.lang.Object):j$.util.concurrent.ConcurrentHashMap$l");
        }
    }

    static final class h extends a implements java.util.Iterator, Enumeration, j$.util.Iterator {
        h(l[] lVarArr, int i, int i2, int i3, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i, i2, i3, concurrentHashMap);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, CLASSNAMEw.b(consumer));
        }

        public final Object next() {
            l lVar = this.b;
            if (lVar != null) {
                Object obj = lVar.b;
                this.j = lVar;
                a();
                return obj;
            }
            throw new NoSuchElementException();
        }

        public final Object nextElement() {
            return next();
        }
    }

    public static class i extends b implements Set, CLASSNAMEb {
        i(ConcurrentHashMap concurrentHashMap, Object obj) {
            super(concurrentHashMap);
        }

        public boolean add(Object obj) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection collection) {
            throw new UnsupportedOperationException();
        }

        public boolean contains(Object obj) {
            return this.a.containsKey(obj);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
            r2 = (java.util.Set) r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r2) {
            /*
                r1 = this;
                boolean r0 = r2 instanceof java.util.Set
                if (r0 == 0) goto L_0x0016
                java.util.Set r2 = (java.util.Set) r2
                if (r2 == r1) goto L_0x0014
                boolean r0 = r1.containsAll(r2)
                if (r0 == 0) goto L_0x0016
                boolean r2 = r2.containsAll(r1)
                if (r2 == 0) goto L_0x0016
            L_0x0014:
                r2 = 1
                goto L_0x0017
            L_0x0016:
                r2 = 0
            L_0x0017:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.i.equals(java.lang.Object):boolean");
        }

        public void forEach(Consumer consumer) {
            consumer.getClass();
            l[] lVarArr = this.a.a;
            if (lVarArr != null) {
                p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
                while (true) {
                    l a = pVar.a();
                    if (a != null) {
                        consumer.accept(a.b);
                    } else {
                        return;
                    }
                }
            }
        }

        public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
            forEach(CLASSNAMEw.b(consumer));
        }

        public int hashCode() {
            java.util.Iterator it = iterator();
            int i = 0;
            while (((a) it).hasNext()) {
                i += ((h) it).next().hashCode();
            }
            return i;
        }

        public java.util.Iterator iterator() {
            ConcurrentHashMap concurrentHashMap = this.a;
            l[] lVarArr = concurrentHashMap.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            return new h(lVarArr, length, 0, length, concurrentHashMap);
        }

        public /* synthetic */ boolean k(Predicate predicate) {
            return CLASSNAMEa.h(this, predicate);
        }

        public /* synthetic */ Stream parallelStream() {
            return P0.n0(CLASSNAMEa.g(this));
        }

        public boolean remove(Object obj) {
            return this.a.remove(obj) != null;
        }

        public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
            return CLASSNAMEa.h(this, x0.a(predicate));
        }

        public j$.util.u spliterator() {
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
    }

    static final class j extends p implements j$.util.u {
        long i;

        j(l[] lVarArr, int i2, int i3, int i4, long j) {
            super(lVarArr, i2, i3, i4);
            this.i = j;
        }

        public boolean b(Consumer consumer) {
            consumer.getClass();
            l a = a();
            if (a == null) {
                return false;
            }
            consumer.accept(a.b);
            return true;
        }

        public int characteristics() {
            return 4353;
        }

        public long estimateSize() {
            return this.i;
        }

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

        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return CLASSNAMEa.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i2) {
            return CLASSNAMEa.f(this, i2);
        }

        public j$.util.u trySplit() {
            int i2 = this.f;
            int i3 = this.g;
            int i4 = (i2 + i3) >>> 1;
            if (i4 <= i2) {
                return null;
            }
            l[] lVarArr = this.a;
            int i5 = this.h;
            this.g = i4;
            long j = this.i >>> 1;
            this.i = j;
            return new j(lVarArr, i5, i4, i3, j);
        }
    }

    static final class k implements Map.Entry {
        final Object a;
        Object b;
        final ConcurrentHashMap c;

        k(Object obj, Object obj2, ConcurrentHashMap concurrentHashMap) {
            this.a = obj;
            this.b = obj2;
            this.c = concurrentHashMap;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
            r0 = r2.b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
            r3 = (java.util.Map.Entry) r3;
            r0 = r3.getKey();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
            r3 = r3.getValue();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0012, code lost:
            r1 = r2.a;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r3) {
            /*
                r2 = this;
                boolean r0 = r3 instanceof java.util.Map.Entry
                if (r0 == 0) goto L_0x0028
                java.util.Map$Entry r3 = (java.util.Map.Entry) r3
                java.lang.Object r0 = r3.getKey()
                if (r0 == 0) goto L_0x0028
                java.lang.Object r3 = r3.getValue()
                if (r3 == 0) goto L_0x0028
                java.lang.Object r1 = r2.a
                if (r0 == r1) goto L_0x001c
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0028
            L_0x001c:
                java.lang.Object r0 = r2.b
                if (r3 == r0) goto L_0x0026
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0028
            L_0x0026:
                r3 = 1
                goto L_0x0029
            L_0x0028:
                r3 = 0
            L_0x0029:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.k.equals(java.lang.Object):boolean");
        }

        public Object getKey() {
            return this.a;
        }

        public Object getValue() {
            return this.b;
        }

        public int hashCode() {
            return this.a.hashCode() ^ this.b.hashCode();
        }

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

    static class l implements Map.Entry {
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

        /* access modifiers changed from: package-private */
        public l a(int i, Object obj) {
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

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
            r0 = r2.c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
            r3 = (java.util.Map.Entry) r3;
            r0 = r3.getKey();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
            r3 = r3.getValue();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0012, code lost:
            r1 = r2.b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean equals(java.lang.Object r3) {
            /*
                r2 = this;
                boolean r0 = r3 instanceof java.util.Map.Entry
                if (r0 == 0) goto L_0x0028
                java.util.Map$Entry r3 = (java.util.Map.Entry) r3
                java.lang.Object r0 = r3.getKey()
                if (r0 == 0) goto L_0x0028
                java.lang.Object r3 = r3.getValue()
                if (r3 == 0) goto L_0x0028
                java.lang.Object r1 = r2.b
                if (r0 == r1) goto L_0x001c
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0028
            L_0x001c:
                java.lang.Object r0 = r2.c
                if (r3 == r0) goto L_0x0026
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x0028
            L_0x0026:
                r3 = 1
                goto L_0x0029
            L_0x0028:
                r3 = 0
            L_0x0029:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.l.equals(java.lang.Object):boolean");
        }

        public final Object getKey() {
            return this.b;
        }

        public final Object getValue() {
            return this.c;
        }

        public final int hashCode() {
            return this.b.hashCode() ^ this.c.hashCode();
        }

        public final Object setValue(Object obj) {
            throw new UnsupportedOperationException();
        }

        public final String toString() {
            return this.b + "=" + this.c;
        }
    }

    static final class m extends l {
        m() {
            super(-3, (Object) null, (Object) null, (l) null);
        }

        /* access modifiers changed from: package-private */
        public l a(int i, Object obj) {
            return null;
        }
    }

    static class n extends ReentrantLock {
        n(float f) {
        }
    }

    static final class o {
        int a;
        int b;
        l[] c;
        o d;

        o() {
        }
    }

    static class p {
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

        /* access modifiers changed from: package-private */
        public final l a() {
            l lVar;
            l[] lVarArr;
            int length;
            int i;
            o oVar;
            l lVar2 = this.b;
            if (lVar2 != null) {
                lVar2 = lVar2.d;
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

    static final class q extends l {
        private static final Unsafe h;
        private static final long i;
        r e;
        volatile r f;
        volatile Thread g;
        volatile int lockState;

        static {
            try {
                Unsafe c = c.c();
                h = c;
                i = c.objectFieldOffset(q.class.getDeclaredField("lockState"));
            } catch (Exception e2) {
                throw new Error(e2);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:13:0x002f, code lost:
            r6 = j$.util.concurrent.ConcurrentHashMap.c(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0035, code lost:
            r8 = j$.util.concurrent.ConcurrentHashMap.d(r6, r3, r7);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        q(j$.util.concurrent.ConcurrentHashMap.r r10) {
            /*
                r9 = this;
                r0 = -2
                r1 = 0
                r9.<init>(r0, r1, r1, r1)
                r9.f = r10
                r0 = r1
            L_0x0008:
                if (r10 == 0) goto L_0x005c
                j$.util.concurrent.ConcurrentHashMap$l r2 = r10.d
                j$.util.concurrent.ConcurrentHashMap$r r2 = (j$.util.concurrent.ConcurrentHashMap.r) r2
                r10.g = r1
                r10.f = r1
                if (r0 != 0) goto L_0x001b
                r10.e = r1
                r0 = 0
                r10.i = r0
            L_0x0019:
                r0 = r10
                goto L_0x0058
            L_0x001b:
                java.lang.Object r3 = r10.b
                int r4 = r10.a
                r5 = r0
                r6 = r1
            L_0x0021:
                java.lang.Object r7 = r5.b
                int r8 = r5.a
                if (r8 <= r4) goto L_0x0029
                r7 = -1
                goto L_0x0041
            L_0x0029:
                if (r8 >= r4) goto L_0x002d
                r7 = 1
                goto L_0x0041
            L_0x002d:
                if (r6 != 0) goto L_0x0035
                java.lang.Class r6 = j$.util.concurrent.ConcurrentHashMap.c(r3)
                if (r6 == 0) goto L_0x003b
            L_0x0035:
                int r8 = j$.util.concurrent.ConcurrentHashMap.d(r6, r3, r7)
                if (r8 != 0) goto L_0x0040
            L_0x003b:
                int r7 = j(r3, r7)
                goto L_0x0041
            L_0x0040:
                r7 = r8
            L_0x0041:
                if (r7 > 0) goto L_0x0046
                j$.util.concurrent.ConcurrentHashMap$r r8 = r5.f
                goto L_0x0048
            L_0x0046:
                j$.util.concurrent.ConcurrentHashMap$r r8 = r5.g
            L_0x0048:
                if (r8 != 0) goto L_0x005a
                r10.e = r5
                if (r7 > 0) goto L_0x0051
                r5.f = r10
                goto L_0x0053
            L_0x0051:
                r5.g = r10
            L_0x0053:
                j$.util.concurrent.ConcurrentHashMap$r r10 = c(r0, r10)
                goto L_0x0019
            L_0x0058:
                r10 = r2
                goto L_0x0008
            L_0x005a:
                r5 = r8
                goto L_0x0021
            L_0x005c:
                r9.e = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.q.<init>(j$.util.concurrent.ConcurrentHashMap$r):void");
        }

        static r b(r rVar, r rVar2) {
            r rVar3;
            while (rVar2 != null && rVar2 != rVar) {
                r rVar4 = rVar2.e;
                if (rVar4 == null) {
                    rVar2.i = false;
                    return rVar2;
                } else if (rVar2.i) {
                    rVar2.i = false;
                    return rVar;
                } else {
                    r rVar5 = rVar4.f;
                    r rVar6 = null;
                    if (rVar5 == rVar2) {
                        rVar3 = rVar4.g;
                        if (rVar3 != null && rVar3.i) {
                            rVar3.i = false;
                            rVar4.i = true;
                            rVar = h(rVar, rVar4);
                            rVar4 = rVar2.e;
                            rVar3 = rVar4 == null ? null : rVar4.g;
                        }
                        if (rVar3 != null) {
                            r rVar7 = rVar3.f;
                            r rVar8 = rVar3.g;
                            if ((rVar8 != null && rVar8.i) || (rVar7 != null && rVar7.i)) {
                                if (rVar8 == null || !rVar8.i) {
                                    if (rVar7 != null) {
                                        rVar7.i = false;
                                    }
                                    rVar3.i = true;
                                    rVar = i(rVar, rVar3);
                                    rVar4 = rVar2.e;
                                    if (rVar4 != null) {
                                        rVar6 = rVar4.g;
                                    }
                                } else {
                                    rVar6 = rVar3;
                                }
                                if (rVar6 != null) {
                                    rVar6.i = rVar4 == null ? false : rVar4.i;
                                    r rVar9 = rVar6.g;
                                    if (rVar9 != null) {
                                        rVar9.i = false;
                                    }
                                }
                                if (rVar4 != null) {
                                    rVar4.i = false;
                                    rVar = h(rVar, rVar4);
                                }
                                rVar2 = rVar;
                                rVar = rVar2;
                            }
                            rVar3.i = true;
                        }
                    } else {
                        if (rVar5 != null && rVar5.i) {
                            rVar5.i = false;
                            rVar4.i = true;
                            rVar = i(rVar, rVar4);
                            rVar4 = rVar2.e;
                            rVar5 = rVar4 == null ? null : rVar4.f;
                        }
                        if (rVar3 != null) {
                            r rVar10 = rVar3.f;
                            r rVar11 = rVar3.g;
                            if ((rVar10 != null && rVar10.i) || (rVar11 != null && rVar11.i)) {
                                if (rVar10 == null || !rVar10.i) {
                                    if (rVar11 != null) {
                                        rVar11.i = false;
                                    }
                                    rVar3.i = true;
                                    rVar = h(rVar, rVar3);
                                    rVar4 = rVar2.e;
                                    if (rVar4 != null) {
                                        rVar6 = rVar4.f;
                                    }
                                } else {
                                    rVar6 = rVar3;
                                }
                                if (rVar6 != null) {
                                    rVar6.i = rVar4 == null ? false : rVar4.i;
                                    r rVar12 = rVar6.f;
                                    if (rVar12 != null) {
                                        rVar12.i = false;
                                    }
                                }
                                if (rVar4 != null) {
                                    rVar4.i = false;
                                    rVar = i(rVar, rVar4);
                                }
                                rVar2 = rVar;
                                rVar = rVar2;
                            }
                            rVar3.i = true;
                        }
                    }
                    rVar2 = rVar4;
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
                    return rVar;
                } else {
                    r rVar5 = rVar3.f;
                    if (rVar4 == rVar5) {
                        rVar5 = rVar3.g;
                        if (rVar5 == null || !rVar5.i) {
                            if (rVar2 == rVar4.g) {
                                rVar = h(rVar, rVar4);
                                r rVar6 = rVar4.e;
                                rVar3 = rVar6 == null ? null : rVar6.e;
                                r rVar7 = rVar4;
                                rVar4 = rVar6;
                                rVar2 = rVar7;
                            }
                            if (rVar4 != null) {
                                rVar4.i = false;
                                if (rVar3 != null) {
                                    rVar3.i = true;
                                    rVar = i(rVar, rVar3);
                                }
                            }
                        }
                    } else if (rVar5 == null || !rVar5.i) {
                        if (rVar2 == rVar4.f) {
                            rVar = i(rVar, rVar4);
                            r rVar8 = rVar4.e;
                            rVar3 = rVar8 == null ? null : rVar8.e;
                            r rVar9 = rVar4;
                            rVar4 = rVar8;
                            rVar2 = rVar9;
                        }
                        if (rVar4 != null) {
                            rVar4.i = false;
                            if (rVar3 != null) {
                                rVar3.i = true;
                                rVar = h(rVar, rVar3);
                            }
                        }
                    }
                    rVar5.i = false;
                    rVar4.i = false;
                    rVar3.i = true;
                    rVar2 = rVar3;
                }
            }
            return rVar;
        }

        private final void d() {
            boolean z = false;
            while (true) {
                int i2 = this.lockState;
                if ((i2 & -3) == 0) {
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

        /* access modifiers changed from: package-private */
        public final l a(int i2, Object obj) {
            Thread thread;
            Thread thread2;
            Object obj2;
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
                                rVar = rVar2.b(i2, obj, (Class) null);
                            }
                            if (c.a(unsafe, this, j, -4) == 6 && (thread2 = this.g) != null) {
                                LockSupport.unpark(thread2);
                            }
                            return rVar;
                        } catch (Throwable th) {
                            if (c.a(h, this, i, -4) == 6 && (thread = this.g) != null) {
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

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0060, code lost:
            return r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x00a3, code lost:
            return null;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final j$.util.concurrent.ConcurrentHashMap.r f(int r16, java.lang.Object r17, java.lang.Object r18) {
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
            L_0x000c:
                if (r10 != 0) goto L_0x0022
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
                goto L_0x00a3
            L_0x0022:
                int r5 = r10.a
                r11 = 1
                if (r5 <= r0) goto L_0x002a
                r5 = -1
                r12 = -1
                goto L_0x0069
            L_0x002a:
                if (r5 >= r0) goto L_0x002e
                r12 = 1
                goto L_0x0069
            L_0x002e:
                java.lang.Object r5 = r10.b
                if (r5 == r4) goto L_0x00ab
                if (r5 == 0) goto L_0x003c
                boolean r6 = r4.equals(r5)
                if (r6 == 0) goto L_0x003c
                goto L_0x00ab
            L_0x003c:
                if (r2 != 0) goto L_0x0044
                java.lang.Class r2 = j$.util.concurrent.ConcurrentHashMap.c(r17)
                if (r2 == 0) goto L_0x004a
            L_0x0044:
                int r6 = j$.util.concurrent.ConcurrentHashMap.d(r2, r4, r5)
                if (r6 != 0) goto L_0x0068
            L_0x004a:
                if (r3 != 0) goto L_0x0062
                j$.util.concurrent.ConcurrentHashMap$r r3 = r10.f
                if (r3 == 0) goto L_0x0056
                j$.util.concurrent.ConcurrentHashMap$r r3 = r3.b(r0, r4, r2)
                if (r3 != 0) goto L_0x0060
            L_0x0056:
                j$.util.concurrent.ConcurrentHashMap$r r3 = r10.g
                if (r3 == 0) goto L_0x0061
                j$.util.concurrent.ConcurrentHashMap$r r3 = r3.b(r0, r4, r2)
                if (r3 == 0) goto L_0x0061
            L_0x0060:
                return r3
            L_0x0061:
                r3 = 1
            L_0x0062:
                int r5 = j(r4, r5)
                r12 = r5
                goto L_0x0069
            L_0x0068:
                r12 = r6
            L_0x0069:
                if (r12 > 0) goto L_0x006e
                j$.util.concurrent.ConcurrentHashMap$r r5 = r10.f
                goto L_0x0070
            L_0x006e:
                j$.util.concurrent.ConcurrentHashMap$r r5 = r10.g
            L_0x0070:
                if (r5 != 0) goto L_0x00a8
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
                if (r13 == 0) goto L_0x0088
                r13.h = r14
            L_0x0088:
                if (r12 > 0) goto L_0x008d
                r10.f = r14
                goto L_0x008f
            L_0x008d:
                r10.g = r14
            L_0x008f:
                boolean r0 = r10.i
                if (r0 != 0) goto L_0x0096
                r14.i = r11
                goto L_0x00a3
            L_0x0096:
                r15.e()
                j$.util.concurrent.ConcurrentHashMap$r r0 = r1.e     // Catch:{ all -> 0x00a4 }
                j$.util.concurrent.ConcurrentHashMap$r r0 = c(r0, r14)     // Catch:{ all -> 0x00a4 }
                r1.e = r0     // Catch:{ all -> 0x00a4 }
                r1.lockState = r9
            L_0x00a3:
                return r8
            L_0x00a4:
                r0 = move-exception
                r1.lockState = r9
                throw r0
            L_0x00a8:
                r10 = r5
                goto L_0x000c
            L_0x00ab:
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.q.f(int, java.lang.Object, java.lang.Object):j$.util.concurrent.ConcurrentHashMap$r");
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x008e A[Catch:{ all -> 0x00c8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x00a9 A[Catch:{ all -> 0x00c8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:66:0x00aa A[Catch:{ all -> 0x00c8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x00ba A[Catch:{ all -> 0x00c8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:74:0x00bd A[Catch:{ all -> 0x00c8 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean g(j$.util.concurrent.ConcurrentHashMap.r r11) {
            /*
                r10 = this;
                j$.util.concurrent.ConcurrentHashMap$l r0 = r11.d
                j$.util.concurrent.ConcurrentHashMap$r r0 = (j$.util.concurrent.ConcurrentHashMap.r) r0
                j$.util.concurrent.ConcurrentHashMap$r r1 = r11.h
                if (r1 != 0) goto L_0x000b
                r10.f = r0
                goto L_0x000d
            L_0x000b:
                r1.d = r0
            L_0x000d:
                if (r0 == 0) goto L_0x0011
                r0.h = r1
            L_0x0011:
                j$.util.concurrent.ConcurrentHashMap$r r0 = r10.f
                r1 = 1
                r2 = 0
                if (r0 != 0) goto L_0x001a
                r10.e = r2
                return r1
            L_0x001a:
                j$.util.concurrent.ConcurrentHashMap$r r0 = r10.e
                if (r0 == 0) goto L_0x00cc
                j$.util.concurrent.ConcurrentHashMap$r r3 = r0.g
                if (r3 == 0) goto L_0x00cc
                j$.util.concurrent.ConcurrentHashMap$r r3 = r0.f
                if (r3 == 0) goto L_0x00cc
                j$.util.concurrent.ConcurrentHashMap$r r3 = r3.f
                if (r3 != 0) goto L_0x002c
                goto L_0x00cc
            L_0x002c:
                r10.e()
                r1 = 0
                j$.util.concurrent.ConcurrentHashMap$r r3 = r11.f     // Catch:{ all -> 0x00c8 }
                j$.util.concurrent.ConcurrentHashMap$r r4 = r11.g     // Catch:{ all -> 0x00c8 }
                if (r3 == 0) goto L_0x0084
                if (r4 == 0) goto L_0x0084
                r5 = r4
            L_0x0039:
                j$.util.concurrent.ConcurrentHashMap$r r6 = r5.f     // Catch:{ all -> 0x00c8 }
                if (r6 == 0) goto L_0x003f
                r5 = r6
                goto L_0x0039
            L_0x003f:
                boolean r6 = r5.i     // Catch:{ all -> 0x00c8 }
                boolean r7 = r11.i     // Catch:{ all -> 0x00c8 }
                r5.i = r7     // Catch:{ all -> 0x00c8 }
                r11.i = r6     // Catch:{ all -> 0x00c8 }
                j$.util.concurrent.ConcurrentHashMap$r r6 = r5.g     // Catch:{ all -> 0x00c8 }
                j$.util.concurrent.ConcurrentHashMap$r r7 = r11.e     // Catch:{ all -> 0x00c8 }
                if (r5 != r4) goto L_0x0052
                r11.e = r5     // Catch:{ all -> 0x00c8 }
                r5.g = r11     // Catch:{ all -> 0x00c8 }
                goto L_0x0065
            L_0x0052:
                j$.util.concurrent.ConcurrentHashMap$r r8 = r5.e     // Catch:{ all -> 0x00c8 }
                r11.e = r8     // Catch:{ all -> 0x00c8 }
                if (r8 == 0) goto L_0x0061
                j$.util.concurrent.ConcurrentHashMap$r r9 = r8.f     // Catch:{ all -> 0x00c8 }
                if (r5 != r9) goto L_0x005f
                r8.f = r11     // Catch:{ all -> 0x00c8 }
                goto L_0x0061
            L_0x005f:
                r8.g = r11     // Catch:{ all -> 0x00c8 }
            L_0x0061:
                r5.g = r4     // Catch:{ all -> 0x00c8 }
                r4.e = r5     // Catch:{ all -> 0x00c8 }
            L_0x0065:
                r11.f = r2     // Catch:{ all -> 0x00c8 }
                r11.g = r6     // Catch:{ all -> 0x00c8 }
                if (r6 == 0) goto L_0x006d
                r6.e = r11     // Catch:{ all -> 0x00c8 }
            L_0x006d:
                r5.f = r3     // Catch:{ all -> 0x00c8 }
                r3.e = r5     // Catch:{ all -> 0x00c8 }
                r5.e = r7     // Catch:{ all -> 0x00c8 }
                if (r7 != 0) goto L_0x0077
                r0 = r5
                goto L_0x0080
            L_0x0077:
                j$.util.concurrent.ConcurrentHashMap$r r3 = r7.f     // Catch:{ all -> 0x00c8 }
                if (r11 != r3) goto L_0x007e
                r7.f = r5     // Catch:{ all -> 0x00c8 }
                goto L_0x0080
            L_0x007e:
                r7.g = r5     // Catch:{ all -> 0x00c8 }
            L_0x0080:
                if (r6 == 0) goto L_0x008b
                r3 = r6
                goto L_0x008c
            L_0x0084:
                if (r3 == 0) goto L_0x0087
                goto L_0x008c
            L_0x0087:
                if (r4 == 0) goto L_0x008b
                r3 = r4
                goto L_0x008c
            L_0x008b:
                r3 = r11
            L_0x008c:
                if (r3 == r11) goto L_0x00a5
                j$.util.concurrent.ConcurrentHashMap$r r4 = r11.e     // Catch:{ all -> 0x00c8 }
                r3.e = r4     // Catch:{ all -> 0x00c8 }
                if (r4 != 0) goto L_0x0096
                r0 = r3
                goto L_0x009f
            L_0x0096:
                j$.util.concurrent.ConcurrentHashMap$r r5 = r4.f     // Catch:{ all -> 0x00c8 }
                if (r11 != r5) goto L_0x009d
                r4.f = r3     // Catch:{ all -> 0x00c8 }
                goto L_0x009f
            L_0x009d:
                r4.g = r3     // Catch:{ all -> 0x00c8 }
            L_0x009f:
                r11.e = r2     // Catch:{ all -> 0x00c8 }
                r11.g = r2     // Catch:{ all -> 0x00c8 }
                r11.f = r2     // Catch:{ all -> 0x00c8 }
            L_0x00a5:
                boolean r4 = r11.i     // Catch:{ all -> 0x00c8 }
                if (r4 == 0) goto L_0x00aa
                goto L_0x00ae
            L_0x00aa:
                j$.util.concurrent.ConcurrentHashMap$r r0 = b(r0, r3)     // Catch:{ all -> 0x00c8 }
            L_0x00ae:
                r10.e = r0     // Catch:{ all -> 0x00c8 }
                if (r11 != r3) goto L_0x00c5
                j$.util.concurrent.ConcurrentHashMap$r r0 = r11.e     // Catch:{ all -> 0x00c8 }
                if (r0 == 0) goto L_0x00c5
                j$.util.concurrent.ConcurrentHashMap$r r3 = r0.f     // Catch:{ all -> 0x00c8 }
                if (r11 != r3) goto L_0x00bd
                r0.f = r2     // Catch:{ all -> 0x00c8 }
                goto L_0x00c3
            L_0x00bd:
                j$.util.concurrent.ConcurrentHashMap$r r3 = r0.g     // Catch:{ all -> 0x00c8 }
                if (r11 != r3) goto L_0x00c3
                r0.g = r2     // Catch:{ all -> 0x00c8 }
            L_0x00c3:
                r11.e = r2     // Catch:{ all -> 0x00c8 }
            L_0x00c5:
                r10.lockState = r1
                return r1
            L_0x00c8:
                r11 = move-exception
                r10.lockState = r1
                throw r11
            L_0x00cc:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.q.g(j$.util.concurrent.ConcurrentHashMap$r):boolean");
        }
    }

    static final class r extends l {
        r e;
        r f;
        r g;
        r h;
        boolean i;

        r(int i2, Object obj, Object obj2, l lVar, r rVar) {
            super(i2, obj, obj2, lVar);
            this.e = rVar;
        }

        /* access modifiers changed from: package-private */
        public l a(int i2, Object obj) {
            return b(i2, obj, (Class) null);
        }

        /* access modifiers changed from: package-private */
        public final r b(int i2, Object obj, Class cls) {
            int d;
            if (obj == null) {
                return null;
            }
            r rVar = this;
            do {
                r rVar2 = rVar.f;
                r rVar3 = rVar.g;
                int i3 = rVar.a;
                if (i3 <= i2) {
                    if (i3 >= i2) {
                        Object obj2 = rVar.b;
                        if (obj2 == obj || (obj2 != null && obj.equals(obj2))) {
                            return rVar;
                        }
                        if (rVar2 != null) {
                            if (rVar3 != null) {
                                if ((cls == null && (cls = ConcurrentHashMap.c(obj)) == null) || (d = ConcurrentHashMap.d(cls, obj, obj2)) == 0) {
                                    r b = rVar3.b(i2, obj, cls);
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
    }

    static final class s extends a implements java.util.Iterator, Enumeration, j$.util.Iterator {
        s(l[] lVarArr, int i, int i2, int i3, ConcurrentHashMap concurrentHashMap) {
            super(lVarArr, i, i2, i3, concurrentHashMap);
        }

        public /* synthetic */ void forEachRemaining(Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, consumer);
        }

        public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
            Iterator.CC.$default$forEachRemaining(this, CLASSNAMEw.b(consumer));
        }

        public final Object next() {
            l lVar = this.b;
            if (lVar != null) {
                Object obj = lVar.c;
                this.j = lVar;
                a();
                return obj;
            }
            throw new NoSuchElementException();
        }

        public final Object nextElement() {
            return next();
        }
    }

    static final class t extends p implements j$.util.u {
        long i;

        t(l[] lVarArr, int i2, int i3, int i4, long j) {
            super(lVarArr, i2, i3, i4);
            this.i = j;
        }

        public boolean b(Consumer consumer) {
            consumer.getClass();
            l a = a();
            if (a == null) {
                return false;
            }
            consumer.accept(a.c);
            return true;
        }

        public int characteristics() {
            return 4352;
        }

        public long estimateSize() {
            return this.i;
        }

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

        public Comparator getComparator() {
            throw new IllegalStateException();
        }

        public /* synthetic */ long getExactSizeIfKnown() {
            return CLASSNAMEa.e(this);
        }

        public /* synthetic */ boolean hasCharacteristics(int i2) {
            return CLASSNAMEa.f(this, i2);
        }

        public j$.util.u trySplit() {
            int i2 = this.f;
            int i3 = this.g;
            int i4 = (i2 + i3) >>> 1;
            if (i4 <= i2) {
                return null;
            }
            l[] lVarArr = this.a;
            int i5 = this.h;
            this.g = i4;
            long j = this.i >>> 1;
            this.i = j;
            return new t(lVarArr, i5, i4, i3, j);
        }
    }

    static final class u extends b implements CLASSNAMEb {
        u(ConcurrentHashMap concurrentHashMap) {
            super(concurrentHashMap);
        }

        public final boolean add(Object obj) {
            throw new UnsupportedOperationException();
        }

        public final boolean addAll(Collection collection) {
            throw new UnsupportedOperationException();
        }

        public final boolean contains(Object obj) {
            return this.a.containsValue(obj);
        }

        public void forEach(Consumer consumer) {
            consumer.getClass();
            l[] lVarArr = this.a.a;
            if (lVarArr != null) {
                p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
                while (true) {
                    l a = pVar.a();
                    if (a != null) {
                        consumer.accept(a.c);
                    } else {
                        return;
                    }
                }
            }
        }

        public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
            forEach(CLASSNAMEw.b(consumer));
        }

        public final java.util.Iterator iterator() {
            ConcurrentHashMap concurrentHashMap = this.a;
            l[] lVarArr = concurrentHashMap.a;
            int length = lVarArr == null ? 0 : lVarArr.length;
            return new s(lVarArr, length, 0, length, concurrentHashMap);
        }

        public /* synthetic */ boolean k(Predicate predicate) {
            return CLASSNAMEa.h(this, predicate);
        }

        public /* synthetic */ Stream parallelStream() {
            return P0.n0(CLASSNAMEa.g(this));
        }

        public final boolean remove(Object obj) {
            a aVar;
            if (obj == null) {
                return false;
            }
            java.util.Iterator it = iterator();
            do {
                aVar = (a) it;
                if (!aVar.hasNext()) {
                    return false;
                }
            } while (!obj.equals(((s) it).next()));
            aVar.remove();
            return true;
        }

        public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
            return CLASSNAMEa.h(this, x0.a(predicate));
        }

        public j$.util.u spliterator() {
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
    }

    static {
        Class cls = Integer.TYPE;
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("segments", n[].class), new ObjectStreamField("segmentMask", cls), new ObjectStreamField("segmentShift", cls)};
        try {
            Unsafe c2 = c.c();
            h = c2;
            Class<ConcurrentHashMap> cls2 = ConcurrentHashMap.class;
            i = c2.objectFieldOffset(cls2.getDeclaredField("sizeCtl"));
            j = c2.objectFieldOffset(cls2.getDeclaredField("transferIndex"));
            k = c2.objectFieldOffset(cls2.getDeclaredField("baseCount"));
            l = c2.objectFieldOffset(cls2.getDeclaredField("cellsBusy"));
            m = c2.objectFieldOffset(c.class.getDeclaredField("value"));
            Class<l[]> cls3 = l[].class;
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
            j$.util.concurrent.ConcurrentHashMap$c[] r0 = r11.c
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
            int r3 = j$.util.concurrent.i.c()
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
            j$.util.concurrent.ConcurrentHashMap$l[] r12 = r11.a
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
            j$.util.concurrent.ConcurrentHashMap$l[] r13 = r11.b
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

    static final boolean b(l[] lVarArr, int i2, l lVar, l lVar2) {
        return h.compareAndSwapObject(lVarArr, (((long) i2) << o) + n, (Object) null, lVar2);
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
        r1 = new j$.util.concurrent.ConcurrentHashMap.c[(r8 << 1)];
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
            int r0 = j$.util.concurrent.i.c()
            r12 = 1
            if (r0 != 0) goto L_0x0015
            j$.util.concurrent.i.g()
            int r0 = j$.util.concurrent.i.c()
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
            j$.util.concurrent.ConcurrentHashMap$c[] r7 = r9.c
            if (r7 == 0) goto L_0x00bd
            int r8 = r7.length
            if (r8 <= 0) goto L_0x00bd
            int r1 = r8 + -1
            r1 = r1 & r14
            r1 = r7[r1]
            if (r1 != 0) goto L_0x0061
            int r1 = r9.cellsBusy
            if (r1 != 0) goto L_0x00b6
            j$.util.concurrent.ConcurrentHashMap$c r7 = new j$.util.concurrent.ConcurrentHashMap$c
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
            j$.util.concurrent.ConcurrentHashMap$c[] r1 = r9.c     // Catch:{ all -> 0x005d }
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
            j$.util.concurrent.ConcurrentHashMap$c[] r1 = r9.c
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
            j$.util.concurrent.ConcurrentHashMap$c[] r1 = r9.c     // Catch:{ all -> 0x00b2 }
            if (r1 != r7) goto L_0x00ad
            int r1 = r8 << 1
            j$.util.concurrent.ConcurrentHashMap$c[] r1 = new j$.util.concurrent.ConcurrentHashMap.c[r1]     // Catch:{ all -> 0x00b2 }
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
            int r14 = j$.util.concurrent.i.a(r14)
            goto L_0x001b
        L_0x00bd:
            int r1 = r9.cellsBusy
            if (r1 != 0) goto L_0x00f1
            j$.util.concurrent.ConcurrentHashMap$c[] r1 = r9.c
            if (r1 != r7) goto L_0x00f1
            sun.misc.Unsafe r1 = h
            long r3 = l
            r5 = 0
            r6 = 1
            r2 = r24
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x00f1
            j$.util.concurrent.ConcurrentHashMap$c[] r1 = r9.c     // Catch:{ all -> 0x00ed }
            if (r1 != r7) goto L_0x00e7
            r1 = 2
            j$.util.concurrent.ConcurrentHashMap$c[] r1 = new j$.util.concurrent.ConcurrentHashMap.c[r1]     // Catch:{ all -> 0x00ed }
            r2 = r14 & 1
            j$.util.concurrent.ConcurrentHashMap$c r3 = new j$.util.concurrent.ConcurrentHashMap$c     // Catch:{ all -> 0x00ed }
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
    private final l[] g() {
        while (true) {
            l[] lVarArr = this.a;
            if (lVarArr != null && lVarArr.length != 0) {
                return lVarArr;
            }
            int i2 = this.sizeCtl;
            if (i2 < 0) {
                Thread.yield();
            } else {
                if (h.compareAndSwapInt(this, i, i2, -1)) {
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
            }
        }
    }

    static final int j(int i2) {
        return Integer.numberOfLeadingZeros(i2) | 32768;
    }

    static final void k(l[] lVarArr, int i2, l lVar) {
        h.putObjectVolatile(lVarArr, (((long) i2) << o) + n, lVar);
    }

    static final int l(int i2) {
        return (i2 ^ (i2 >>> 16)) & Integer.MAX_VALUE;
    }

    static final l n(l[] lVarArr, int i2) {
        return (l) h.getObjectVolatile(lVarArr, (((long) i2) << o) + n);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: j$.util.concurrent.ConcurrentHashMap$l} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: j$.util.concurrent.ConcurrentHashMap$l} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v9, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v10, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v14, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: j$.util.concurrent.ConcurrentHashMap$r} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: j$.util.concurrent.ConcurrentHashMap$l} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: j$.util.concurrent.ConcurrentHashMap$l} */
    /* JADX WARNING: type inference failed for: r13v14, types: [j$.util.concurrent.ConcurrentHashMap$l] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void p(j$.util.concurrent.ConcurrentHashMap.l[] r31, j$.util.concurrent.ConcurrentHashMap.l[] r32) {
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
            j$.util.concurrent.ConcurrentHashMap$l[] r1 = new j$.util.concurrent.ConcurrentHashMap.l[r1]     // Catch:{ all -> 0x0023 }
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
            j$.util.concurrent.ConcurrentHashMap$g r14 = new j$.util.concurrent.ConcurrentHashMap$g
            r14.<init>(r12)
            r3 = r0
            r5 = 0
            r6 = 0
            r16 = 1
            r17 = 0
        L_0x0038:
            r1 = -1
            if (r16 == 0) goto L_0x0082
            int r5 = r5 + -1
            if (r5 >= r6) goto L_0x0075
            if (r17 == 0) goto L_0x0042
            goto L_0x0075
        L_0x0042:
            int r4 = r7.transferIndex
            if (r4 > 0) goto L_0x0049
            r15 = r3
            r5 = -1
            goto L_0x007e
        L_0x0049:
            sun.misc.Unsafe r1 = h
            long r18 = j
            if (r4 <= r11) goto L_0x0054
            int r2 = r4 - r11
            r20 = r2
            goto L_0x0056
        L_0x0054:
            r20 = 0
        L_0x0056:
            r2 = r30
            r15 = r3
            r21 = r4
            r3 = r18
            r18 = r5
            r5 = r21
            r19 = r6
            r6 = r20
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x0071
            int r4 = r21 + -1
            r5 = r4
            r6 = r20
            goto L_0x007e
        L_0x0071:
            r3 = r15
            r5 = r18
            goto L_0x009b
        L_0x0075:
            r15 = r3
            r18 = r5
            r19 = r6
            r5 = r18
            r6 = r19
        L_0x007e:
            r3 = r15
            r16 = 0
            goto L_0x0038
        L_0x0082:
            r15 = r3
            r19 = r6
            r2 = 0
            if (r5 < 0) goto L_0x01ab
            if (r5 >= r8) goto L_0x01ab
            int r3 = r5 + r8
            if (r3 < r13) goto L_0x0090
            goto L_0x01ab
        L_0x0090:
            j$.util.concurrent.ConcurrentHashMap$l r4 = n(r15, r5)
            if (r4 != 0) goto L_0x009e
            boolean r16 = b(r15, r5, r2, r14)
            r3 = r15
        L_0x009b:
            r6 = r19
            goto L_0x0038
        L_0x009e:
            int r6 = r4.a
            if (r6 != r1) goto L_0x00a8
            r3 = r15
            r6 = r19
            r16 = 1
            goto L_0x0038
        L_0x00a8:
            monitor-enter(r4)
            j$.util.concurrent.ConcurrentHashMap$l r1 = n(r15, r5)     // Catch:{ all -> 0x01a8 }
            if (r1 != r4) goto L_0x0193
            if (r6 < 0) goto L_0x0105
            r1 = r6 & r8
            j$.util.concurrent.ConcurrentHashMap$l r6 = r4.d     // Catch:{ all -> 0x01a8 }
            r10 = r4
        L_0x00b6:
            if (r6 == 0) goto L_0x00c3
            int r9 = r6.a     // Catch:{ all -> 0x01a8 }
            r9 = r9 & r8
            if (r9 == r1) goto L_0x00bf
            r10 = r6
            r1 = r9
        L_0x00bf:
            j$.util.concurrent.ConcurrentHashMap$l r6 = r6.d     // Catch:{ all -> 0x01a8 }
            r9 = 1
            goto L_0x00b6
        L_0x00c3:
            if (r1 != 0) goto L_0x00c8
            r1 = r2
            r2 = r10
            goto L_0x00c9
        L_0x00c8:
            r1 = r10
        L_0x00c9:
            r6 = r4
        L_0x00ca:
            if (r6 == r10) goto L_0x00f4
            int r9 = r6.a     // Catch:{ all -> 0x01a8 }
            r16 = r10
            java.lang.Object r10 = r6.b     // Catch:{ all -> 0x01a8 }
            r21 = r11
            java.lang.Object r11 = r6.c     // Catch:{ all -> 0x01a8 }
            r22 = r9 & r8
            if (r22 != 0) goto L_0x00e3
            r22 = r13
            j$.util.concurrent.ConcurrentHashMap$l r13 = new j$.util.concurrent.ConcurrentHashMap$l     // Catch:{ all -> 0x01a8 }
            r13.<init>(r9, r10, r11, r2)     // Catch:{ all -> 0x01a8 }
            r2 = r13
            goto L_0x00eb
        L_0x00e3:
            r22 = r13
            j$.util.concurrent.ConcurrentHashMap$l r13 = new j$.util.concurrent.ConcurrentHashMap$l     // Catch:{ all -> 0x01a8 }
            r13.<init>(r9, r10, r11, r1)     // Catch:{ all -> 0x01a8 }
            r1 = r13
        L_0x00eb:
            j$.util.concurrent.ConcurrentHashMap$l r6 = r6.d     // Catch:{ all -> 0x01a8 }
            r10 = r16
            r11 = r21
            r13 = r22
            goto L_0x00ca
        L_0x00f4:
            r21 = r11
            r22 = r13
            k(r12, r5, r2)     // Catch:{ all -> 0x01a8 }
            k(r12, r3, r1)     // Catch:{ all -> 0x01a8 }
            k(r15, r5, r14)     // Catch:{ all -> 0x01a8 }
            r7 = r14
            r3 = r15
            goto L_0x0190
        L_0x0105:
            r21 = r11
            r22 = r13
            boolean r1 = r4 instanceof j$.util.concurrent.ConcurrentHashMap.q     // Catch:{ all -> 0x01a8 }
            if (r1 == 0) goto L_0x0197
            r1 = r4
            j$.util.concurrent.ConcurrentHashMap$q r1 = (j$.util.concurrent.ConcurrentHashMap.q) r1     // Catch:{ all -> 0x01a8 }
            j$.util.concurrent.ConcurrentHashMap$r r6 = r1.f     // Catch:{ all -> 0x01a8 }
            r9 = r2
            r10 = r9
            r11 = r6
            r13 = 0
            r15 = 0
            r6 = r10
        L_0x0118:
            if (r11 == 0) goto L_0x015b
            r16 = r1
            int r1 = r11.a     // Catch:{ all -> 0x01a8 }
            j$.util.concurrent.ConcurrentHashMap$r r7 = new j$.util.concurrent.ConcurrentHashMap$r     // Catch:{ all -> 0x01a8 }
            java.lang.Object r0 = r11.b     // Catch:{ all -> 0x01a8 }
            r29 = r14
            java.lang.Object r14 = r11.c     // Catch:{ all -> 0x01a8 }
            r27 = 0
            r28 = 0
            r23 = r7
            r24 = r1
            r25 = r0
            r26 = r14
            r23.<init>(r24, r25, r26, r27, r28)     // Catch:{ all -> 0x01a8 }
            r0 = r1 & r8
            if (r0 != 0) goto L_0x0145
            r7.h = r10     // Catch:{ all -> 0x01a8 }
            if (r10 != 0) goto L_0x013f
            r2 = r7
            goto L_0x0141
        L_0x013f:
            r10.d = r7     // Catch:{ all -> 0x01a8 }
        L_0x0141:
            int r13 = r13 + 1
            r10 = r7
            goto L_0x0150
        L_0x0145:
            r7.h = r9     // Catch:{ all -> 0x01a8 }
            if (r9 != 0) goto L_0x014b
            r6 = r7
            goto L_0x014d
        L_0x014b:
            r9.d = r7     // Catch:{ all -> 0x01a8 }
        L_0x014d:
            int r15 = r15 + 1
            r9 = r7
        L_0x0150:
            j$.util.concurrent.ConcurrentHashMap$l r11 = r11.d     // Catch:{ all -> 0x01a8 }
            r7 = r30
            r0 = r31
            r1 = r16
            r14 = r29
            goto L_0x0118
        L_0x015b:
            r16 = r1
            r29 = r14
            r0 = 6
            if (r13 > r0) goto L_0x0167
            j$.util.concurrent.ConcurrentHashMap$l r1 = s(r2)     // Catch:{ all -> 0x01a8 }
            goto L_0x0171
        L_0x0167:
            if (r15 == 0) goto L_0x016f
            j$.util.concurrent.ConcurrentHashMap$q r1 = new j$.util.concurrent.ConcurrentHashMap$q     // Catch:{ all -> 0x01a8 }
            r1.<init>(r2)     // Catch:{ all -> 0x01a8 }
            goto L_0x0171
        L_0x016f:
            r1 = r16
        L_0x0171:
            if (r15 > r0) goto L_0x0178
            j$.util.concurrent.ConcurrentHashMap$l r0 = s(r6)     // Catch:{ all -> 0x01a8 }
            goto L_0x0182
        L_0x0178:
            if (r13 == 0) goto L_0x0180
            j$.util.concurrent.ConcurrentHashMap$q r0 = new j$.util.concurrent.ConcurrentHashMap$q     // Catch:{ all -> 0x01a8 }
            r0.<init>(r6)     // Catch:{ all -> 0x01a8 }
            goto L_0x0182
        L_0x0180:
            r0 = r16
        L_0x0182:
            k(r12, r5, r1)     // Catch:{ all -> 0x01a8 }
            k(r12, r3, r0)     // Catch:{ all -> 0x01a8 }
            r0 = r31
            r7 = r29
            k(r0, r5, r7)     // Catch:{ all -> 0x01a8 }
            r3 = r0
        L_0x0190:
            r16 = 1
            goto L_0x0199
        L_0x0193:
            r21 = r11
            r22 = r13
        L_0x0197:
            r7 = r14
            r3 = r15
        L_0x0199:
            monitor-exit(r4)     // Catch:{ all -> 0x01a8 }
            r14 = r7
            r6 = r19
            r11 = r21
            r13 = r22
            r9 = 1
            r10 = 16
            r7 = r30
            goto L_0x0038
        L_0x01a8:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x01a8 }
            throw r0
        L_0x01ab:
            r21 = r11
            r22 = r13
            r7 = r14
            r9 = r30
            if (r17 == 0) goto L_0x01c1
            r9.b = r2
            r9.a = r12
            int r0 = r8 << 1
            r10 = 1
            int r1 = r8 >>> 1
            int r0 = r0 - r1
            r9.sizeCtl = r0
            return
        L_0x01c1:
            r10 = 1
            sun.misc.Unsafe r1 = h
            long r3 = i
            int r11 = r9.sizeCtl
            int r6 = r11 + -1
            r2 = r30
            r13 = r5
            r5 = r11
            boolean r1 = r1.compareAndSwapInt(r2, r3, r5, r6)
            if (r1 == 0) goto L_0x01f3
            int r11 = r11 + -2
            int r1 = j(r8)
            r2 = 16
            int r1 = r1 << r2
            if (r11 == r1) goto L_0x01e0
            return
        L_0x01e0:
            r14 = r7
            r5 = r8
            r7 = r9
            r3 = r15
            r6 = r19
            r11 = r21
            r13 = r22
            r9 = 1
            r10 = 16
            r16 = 1
            r17 = 1
            goto L_0x0038
        L_0x01f3:
            r14 = r7
            r7 = r9
            r5 = r13
            r3 = r15
            r6 = r19
            r11 = r21
            r13 = r22
            r9 = 1
            r10 = 16
            goto L_0x0038
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.p(j$.util.concurrent.ConcurrentHashMap$l[], j$.util.concurrent.ConcurrentHashMap$l[]):void");
    }

    private final void q(l[] lVarArr, int i2) {
        int length = lVarArr.length;
        if (length < 64) {
            r(length << 1);
            return;
        }
        l n2 = n(lVarArr, i2);
        if (n2 != null && n2.a >= 0) {
            synchronized (n2) {
                if (n(lVarArr, i2) == n2) {
                    r rVar = null;
                    l lVar = n2;
                    r rVar2 = null;
                    while (lVar != null) {
                        r rVar3 = new r(lVar.a, lVar.b, lVar.c, (l) null, (r) null);
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
                    }
                } else if (o2 > i3 && length < NUM) {
                    if (lVarArr2 == this.a) {
                        int j2 = j(length);
                        if (i3 >= 0) {
                            if (h.compareAndSwapInt(this, i, i3, (j2 << 16) + 2)) {
                                p(lVarArr2, (l[]) null);
                            }
                        } else if ((i3 >>> 16) == j2 && i3 != j2 + 1 && i3 != j2 + 65535 && (lVarArr = this.b) != null && this.transferIndex > 0) {
                            if (h.compareAndSwapInt(this, i, i3, i3 + 1)) {
                                p(lVarArr2, lVarArr);
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

    private void readObject(ObjectInputStream objectInputStream) {
        long j2;
        int i2;
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
            if (readObject != null && readObject2 != null) {
                j4++;
                lVar = new l(l(readObject.hashCode()), readObject, readObject2, lVar);
            }
        }
        if (j4 == 0) {
            this.sizeCtl = 0;
            return;
        }
        if (j4 >= NUM) {
            i2 = NUM;
        } else {
            int i3 = (int) j4;
            i2 = o(i3 + (i3 >>> 1) + 1);
        }
        l[] lVarArr = new l[i2];
        int i4 = i2 - 1;
        while (lVar != null) {
            l lVar2 = lVar.d;
            int i5 = lVar.a;
            int i6 = i5 & i4;
            l n2 = n(lVarArr, i6);
            if (n2 == null) {
                z = true;
            } else {
                Object obj2 = lVar.b;
                if (n2.a >= 0) {
                    l lVar3 = n2;
                    int i7 = 0;
                    while (true) {
                        if (lVar3 == null) {
                            z = true;
                            break;
                        } else if (lVar3.a != i5 || ((obj = lVar3.b) != obj2 && (obj == null || !obj2.equals(obj)))) {
                            i7++;
                            lVar3 = lVar3.d;
                        }
                    }
                    z = false;
                    if (z && i7 >= 8) {
                        j3++;
                        lVar.d = n2;
                        l lVar4 = lVar;
                        r rVar = null;
                        r rVar2 = null;
                        while (lVar4 != null) {
                            long j5 = j3;
                            r rVar3 = new r(lVar4.a, lVar4.b, lVar4.c, (l) null, (r) null);
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
                        long j6 = j3;
                        k(lVarArr, i6, new q(rVar));
                    }
                } else if (((q) n2).f(i5, obj2, lVar.c) == null) {
                    j3 += j2;
                }
                z = false;
            }
            if (z) {
                j3++;
                lVar.d = n2;
                k(lVarArr, i6, lVar);
            }
            j2 = 1;
            lVar = lVar2;
        }
        this.a = lVarArr;
        this.sizeCtl = i2 - (i2 >>> 2);
        this.baseCount = j3;
    }

    static l s(l lVar) {
        l lVar2 = null;
        l lVar3 = null;
        while (lVar != null) {
            l lVar4 = new l(lVar.a, lVar.b, lVar.c, (l) null);
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
        objectOutputStream.writeObject((Object) null);
        objectOutputStream.writeObject((Object) null);
    }

    public void clear() {
        l[] lVarArr = this.a;
        long j2 = 0;
        loop0:
        while (true) {
            int i2 = 0;
            while (lVarArr != null && i2 < lVarArr.length) {
                l n2 = n(lVarArr, i2);
                if (n2 == null) {
                    i2++;
                } else {
                    int i3 = n2.a;
                    if (i3 == -1) {
                        lVarArr = f(lVarArr, n2);
                    } else {
                        synchronized (n2) {
                            if (n(lVarArr, i2) == n2) {
                                for (l lVar = i3 >= 0 ? n2 : n2 instanceof q ? ((q) n2).f : null; lVar != null; lVar = lVar.d) {
                                    j2--;
                                }
                                k(lVarArr, i2, (l) null);
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
        Object obj3;
        int i2;
        l lVar;
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
                            if (b(lVarArr, i5, (l) null, mVar)) {
                                try {
                                    Object apply = biFunction.apply(obj, (Object) null);
                                    if (apply != null) {
                                        lVar = new l(l2, obj, apply, (l) null);
                                        i2 = 1;
                                    } else {
                                        i2 = i3;
                                        lVar = null;
                                    }
                                    k(lVarArr, i5, lVar);
                                    i3 = i2;
                                    obj4 = apply;
                                    i4 = 1;
                                } catch (Throwable th) {
                                    k(lVarArr, i5, (l) null);
                                    throw th;
                                }
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
                                                    Object apply2 = biFunction.apply(obj, (Object) null);
                                                    if (apply2 != null) {
                                                        lVar3.d = new l(l2, obj, apply2, (l) null);
                                                        i3 = 1;
                                                    }
                                                    obj2 = apply2;
                                                } else {
                                                    i7++;
                                                    l lVar5 = lVar4;
                                                    lVar2 = lVar3;
                                                    lVar3 = lVar5;
                                                }
                                            }
                                        }
                                        obj2 = biFunction.apply(obj, lVar3.c);
                                        if (obj2 != null) {
                                            lVar3.c = obj2;
                                        } else {
                                            l lVar6 = lVar3.d;
                                            if (lVar2 != null) {
                                                lVar2.d = lVar6;
                                            } else {
                                                k(lVarArr, i5, lVar6);
                                            }
                                            i3 = -1;
                                        }
                                        i4 = i7;
                                        obj4 = obj2;
                                    } else if (n2 instanceof q) {
                                        q qVar = (q) n2;
                                        r rVar = qVar.e;
                                        r b2 = rVar != null ? rVar.b(l2, obj, (Class) null) : null;
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
            a((long) i3, i4);
        }
        return obj4;
    }

    public /* synthetic */ Object compute(Object obj, java.util.function.BiFunction biFunction) {
        return compute(obj, CLASSNAMEs.a(biFunction));
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x00c2, code lost:
        if (r5 == null) goto L_0x00c9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x00c4, code lost:
        a(1, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x00c9, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object computeIfAbsent(java.lang.Object r13, j$.util.function.Function r14) {
        /*
            r12 = this;
            r0 = 0
            if (r13 == 0) goto L_0x00d3
            if (r14 == 0) goto L_0x00d3
            int r1 = r13.hashCode()
            int r1 = l(r1)
            j$.util.concurrent.ConcurrentHashMap$l[] r2 = r12.a
            r3 = 0
            r5 = r0
            r4 = 0
        L_0x0012:
            if (r2 == 0) goto L_0x00cd
            int r6 = r2.length
            if (r6 != 0) goto L_0x0019
            goto L_0x00cd
        L_0x0019:
            int r6 = r6 + -1
            r6 = r6 & r1
            j$.util.concurrent.ConcurrentHashMap$l r7 = n(r2, r6)
            r8 = 1
            if (r7 != 0) goto L_0x004f
            j$.util.concurrent.ConcurrentHashMap$m r9 = new j$.util.concurrent.ConcurrentHashMap$m
            r9.<init>()
            monitor-enter(r9)
            boolean r7 = b(r2, r6, r0, r9)     // Catch:{ all -> 0x004c }
            if (r7 == 0) goto L_0x0047
            java.lang.Object r4 = r14.apply(r13)     // Catch:{ all -> 0x0042 }
            if (r4 == 0) goto L_0x003b
            j$.util.concurrent.ConcurrentHashMap$l r5 = new j$.util.concurrent.ConcurrentHashMap$l     // Catch:{ all -> 0x0042 }
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
            goto L_0x00c2
        L_0x004c:
            r13 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x004c }
            throw r13
        L_0x004f:
            int r9 = r7.a
            r10 = -1
            if (r9 != r10) goto L_0x0059
            j$.util.concurrent.ConcurrentHashMap$l[] r2 = r12.f(r2, r7)
            goto L_0x0012
        L_0x0059:
            monitor-enter(r7)
            j$.util.concurrent.ConcurrentHashMap$l r10 = n(r2, r6)     // Catch:{ all -> 0x00ca }
            if (r10 != r7) goto L_0x00b4
            if (r9 < 0) goto L_0x008d
            r4 = r7
            r5 = 1
        L_0x0064:
            int r9 = r4.a     // Catch:{ all -> 0x00ca }
            if (r9 != r1) goto L_0x0077
            java.lang.Object r9 = r4.b     // Catch:{ all -> 0x00ca }
            if (r9 == r13) goto L_0x0074
            if (r9 == 0) goto L_0x0077
            boolean r9 = r13.equals(r9)     // Catch:{ all -> 0x00ca }
            if (r9 == 0) goto L_0x0077
        L_0x0074:
            java.lang.Object r4 = r4.c     // Catch:{ all -> 0x00ca }
            goto L_0x00a1
        L_0x0077:
            j$.util.concurrent.ConcurrentHashMap$l r9 = r4.d     // Catch:{ all -> 0x00ca }
            if (r9 != 0) goto L_0x0089
            java.lang.Object r9 = r14.apply(r13)     // Catch:{ all -> 0x00ca }
            if (r9 == 0) goto L_0x00b0
            j$.util.concurrent.ConcurrentHashMap$l r10 = new j$.util.concurrent.ConcurrentHashMap$l     // Catch:{ all -> 0x00ca }
            r10.<init>(r1, r13, r9, r0)     // Catch:{ all -> 0x00ca }
            r4.d = r10     // Catch:{ all -> 0x00ca }
            goto L_0x00b1
        L_0x0089:
            int r5 = r5 + 1
            r4 = r9
            goto L_0x0064
        L_0x008d:
            boolean r9 = r7 instanceof j$.util.concurrent.ConcurrentHashMap.q     // Catch:{ all -> 0x00ca }
            if (r9 == 0) goto L_0x00b4
            r5 = 2
            r4 = r7
            j$.util.concurrent.ConcurrentHashMap$q r4 = (j$.util.concurrent.ConcurrentHashMap.q) r4     // Catch:{ all -> 0x00ca }
            j$.util.concurrent.ConcurrentHashMap$r r9 = r4.e     // Catch:{ all -> 0x00ca }
            if (r9 == 0) goto L_0x00a6
            j$.util.concurrent.ConcurrentHashMap$r r9 = r9.b(r1, r13, r0)     // Catch:{ all -> 0x00ca }
            if (r9 == 0) goto L_0x00a6
            java.lang.Object r4 = r9.c     // Catch:{ all -> 0x00ca }
        L_0x00a1:
            r8 = 0
            r11 = r5
            r5 = r4
            r4 = r11
            goto L_0x00b5
        L_0x00a6:
            java.lang.Object r9 = r14.apply(r13)     // Catch:{ all -> 0x00ca }
            if (r9 == 0) goto L_0x00b0
            r4.f(r1, r13, r9)     // Catch:{ all -> 0x00ca }
            goto L_0x00b1
        L_0x00b0:
            r8 = 0
        L_0x00b1:
            r4 = r5
            r5 = r9
            goto L_0x00b5
        L_0x00b4:
            r8 = 0
        L_0x00b5:
            monitor-exit(r7)     // Catch:{ all -> 0x00ca }
            if (r4 == 0) goto L_0x0012
            r13 = 8
            if (r4 < r13) goto L_0x00bf
            r12.q(r2, r6)
        L_0x00bf:
            if (r8 != 0) goto L_0x00c2
            return r5
        L_0x00c2:
            if (r5 == 0) goto L_0x00c9
            r13 = 1
            r12.a(r13, r4)
        L_0x00c9:
            return r5
        L_0x00ca:
            r13 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00ca }
            throw r13
        L_0x00cd:
            j$.util.concurrent.ConcurrentHashMap$l[] r2 = r12.g()
            goto L_0x0012
        L_0x00d3:
            goto L_0x00d5
        L_0x00d4:
            throw r0
        L_0x00d5:
            goto L_0x00d4
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.computeIfAbsent(java.lang.Object, j$.util.function.Function):java.lang.Object");
    }

    public /* synthetic */ Object computeIfAbsent(Object obj, Function function) {
        return computeIfAbsent(obj, M.a(function));
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
            j$.util.concurrent.ConcurrentHashMap$l[] r2 = r13.a
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
            j$.util.concurrent.ConcurrentHashMap$l r7 = n(r2, r6)
            if (r7 != 0) goto L_0x0024
            goto L_0x0099
        L_0x0024:
            int r8 = r7.a
            r9 = -1
            if (r8 != r9) goto L_0x002e
            j$.util.concurrent.ConcurrentHashMap$l[] r2 = r13.f(r2, r7)
            goto L_0x0012
        L_0x002e:
            monitor-enter(r7)
            j$.util.concurrent.ConcurrentHashMap$l r10 = n(r2, r6)     // Catch:{ all -> 0x00a0 }
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
            j$.util.concurrent.ConcurrentHashMap$l r3 = r8.d     // Catch:{ all -> 0x00a0 }
            if (r10 == 0) goto L_0x005c
            r10.d = r3     // Catch:{ all -> 0x00a0 }
            goto L_0x005f
        L_0x005c:
            k(r2, r6, r3)     // Catch:{ all -> 0x00a0 }
        L_0x005f:
            r3 = -1
            goto L_0x0096
        L_0x0061:
            j$.util.concurrent.ConcurrentHashMap$l r10 = r8.d     // Catch:{ all -> 0x00a0 }
            if (r10 != 0) goto L_0x0066
            goto L_0x0096
        L_0x0066:
            int r4 = r4 + 1
            r12 = r10
            r10 = r8
            r8 = r12
            goto L_0x003a
        L_0x006c:
            boolean r8 = r7 instanceof j$.util.concurrent.ConcurrentHashMap.q     // Catch:{ all -> 0x00a0 }
            if (r8 == 0) goto L_0x0096
            r4 = 2
            r8 = r7
            j$.util.concurrent.ConcurrentHashMap$q r8 = (j$.util.concurrent.ConcurrentHashMap.q) r8     // Catch:{ all -> 0x00a0 }
            j$.util.concurrent.ConcurrentHashMap$r r10 = r8.e     // Catch:{ all -> 0x00a0 }
            if (r10 == 0) goto L_0x0096
            j$.util.concurrent.ConcurrentHashMap$r r10 = r10.b(r1, r14, r0)     // Catch:{ all -> 0x00a0 }
            if (r10 == 0) goto L_0x0096
            java.lang.Object r5 = r10.c     // Catch:{ all -> 0x00a0 }
            java.lang.Object r5 = r15.apply(r14, r5)     // Catch:{ all -> 0x00a0 }
            if (r5 == 0) goto L_0x0089
            r10.c = r5     // Catch:{ all -> 0x00a0 }
            goto L_0x0096
        L_0x0089:
            boolean r3 = r8.g(r10)     // Catch:{ all -> 0x00a0 }
            if (r3 == 0) goto L_0x005f
            j$.util.concurrent.ConcurrentHashMap$r r3 = r8.f     // Catch:{ all -> 0x00a0 }
            j$.util.concurrent.ConcurrentHashMap$l r3 = s(r3)     // Catch:{ all -> 0x00a0 }
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
            j$.util.concurrent.ConcurrentHashMap$l[] r2 = r13.g()
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
        return computeIfPresent(obj, CLASSNAMEs.a(biFunction));
    }

    public boolean containsKey(Object obj) {
        return get(obj) != null;
    }

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

    public Set<Map.Entry<K, V>> entrySet() {
        e eVar = this.f;
        if (eVar != null) {
            return eVar;
        }
        e eVar2 = new e(this);
        this.f = eVar2;
        return eVar2;
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
        l[] lVarArr = this.a;
        int length = lVarArr == null ? 0 : lVarArr.length;
        p pVar = new p(lVarArr, length, 0, length);
        while (true) {
            l a2 = pVar.a();
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
    public final l[] f(l[] lVarArr, l lVar) {
        l[] lVarArr2;
        int i2;
        if (!(lVar instanceof g) || (lVarArr2 = ((g) lVar).e) == null) {
            return this.a;
        }
        int j2 = j(lVarArr.length);
        while (true) {
            if (lVarArr2 != this.b || this.a != lVarArr || (i2 = this.sizeCtl) >= 0 || (i2 >>> 16) != j2 || i2 == j2 + 1 || i2 == 65535 + j2 || this.transferIndex <= 0) {
                break;
            }
            if (h.compareAndSwapInt(this, i, i2, i2 + 1)) {
                p(lVarArr, lVarArr2);
                break;
            }
        }
        return lVarArr2;
    }

    public void forEach(BiConsumer biConsumer) {
        biConsumer.getClass();
        l[] lVarArr = this.a;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a2 = pVar.a();
                if (a2 != null) {
                    biConsumer.accept(a2.b, a2.c);
                } else {
                    return;
                }
            }
        }
    }

    public /* synthetic */ void forEach(java.util.function.BiConsumer biConsumer) {
        forEach(CLASSNAMEq.a(biConsumer));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x004d, code lost:
        return r1.c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public V get(java.lang.Object r5) {
        /*
            r4 = this;
            int r0 = r5.hashCode()
            int r0 = l(r0)
            j$.util.concurrent.ConcurrentHashMap$l[] r1 = r4.a
            r2 = 0
            if (r1 == 0) goto L_0x004e
            int r3 = r1.length
            if (r3 <= 0) goto L_0x004e
            int r3 = r3 + -1
            r3 = r3 & r0
            j$.util.concurrent.ConcurrentHashMap$l r1 = n(r1, r3)
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
            j$.util.concurrent.ConcurrentHashMap$l r5 = r1.a(r0, r5)
            if (r5 == 0) goto L_0x0036
            java.lang.Object r2 = r5.c
        L_0x0036:
            return r2
        L_0x0037:
            j$.util.concurrent.ConcurrentHashMap$l r1 = r1.d
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
            j$.util.concurrent.ConcurrentHashMap$l[] r3 = r8.a
        L_0x0010:
            if (r3 == 0) goto L_0x0092
            int r4 = r3.length
            if (r4 != 0) goto L_0x0017
            goto L_0x0092
        L_0x0017:
            int r4 = r4 + -1
            r4 = r4 & r1
            j$.util.concurrent.ConcurrentHashMap$l r5 = n(r3, r4)
            if (r5 != 0) goto L_0x002c
            j$.util.concurrent.ConcurrentHashMap$l r5 = new j$.util.concurrent.ConcurrentHashMap$l
            r5.<init>(r1, r9, r10, r0)
            boolean r4 = b(r3, r4, r0, r5)
            if (r4 == 0) goto L_0x0010
            goto L_0x0089
        L_0x002c:
            int r6 = r5.a
            r7 = -1
            if (r6 != r7) goto L_0x0036
            j$.util.concurrent.ConcurrentHashMap$l[] r3 = r8.f(r3, r5)
            goto L_0x0010
        L_0x0036:
            monitor-enter(r5)
            j$.util.concurrent.ConcurrentHashMap$l r7 = n(r3, r4)     // Catch:{ all -> 0x008f }
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
            j$.util.concurrent.ConcurrentHashMap$l r7 = r6.d     // Catch:{ all -> 0x008f }
            if (r7 != 0) goto L_0x0064
            j$.util.concurrent.ConcurrentHashMap$l r7 = new j$.util.concurrent.ConcurrentHashMap$l     // Catch:{ all -> 0x008f }
            r7.<init>(r1, r9, r10, r0)     // Catch:{ all -> 0x008f }
            r6.d = r7     // Catch:{ all -> 0x008f }
            goto L_0x007b
        L_0x0064:
            int r2 = r2 + 1
            r6 = r7
            goto L_0x0041
        L_0x0068:
            boolean r6 = r5 instanceof j$.util.concurrent.ConcurrentHashMap.q     // Catch:{ all -> 0x008f }
            if (r6 == 0) goto L_0x007b
            r2 = 2
            r6 = r5
            j$.util.concurrent.ConcurrentHashMap$q r6 = (j$.util.concurrent.ConcurrentHashMap.q) r6     // Catch:{ all -> 0x008f }
            j$.util.concurrent.ConcurrentHashMap$r r6 = r6.f(r1, r9, r10)     // Catch:{ all -> 0x008f }
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
            j$.util.concurrent.ConcurrentHashMap$l[] r3 = r8.g()
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

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00af, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object i(java.lang.Object r13, java.lang.Object r14, java.lang.Object r15) {
        /*
            r12 = this;
            int r0 = r13.hashCode()
            int r0 = l(r0)
            j$.util.concurrent.ConcurrentHashMap$l[] r1 = r12.a
        L_0x000a:
            r2 = 0
            if (r1 == 0) goto L_0x00af
            int r3 = r1.length
            if (r3 == 0) goto L_0x00af
            int r3 = r3 + -1
            r3 = r3 & r0
            j$.util.concurrent.ConcurrentHashMap$l r4 = n(r1, r3)
            if (r4 != 0) goto L_0x001b
            goto L_0x00af
        L_0x001b:
            int r5 = r4.a
            r6 = -1
            if (r5 != r6) goto L_0x0025
            j$.util.concurrent.ConcurrentHashMap$l[] r1 = r12.f(r1, r4)
            goto L_0x000a
        L_0x0025:
            r7 = 0
            monitor-enter(r4)
            j$.util.concurrent.ConcurrentHashMap$l r8 = n(r1, r3)     // Catch:{ all -> 0x00ac }
            r9 = 1
            if (r8 != r4) goto L_0x009e
            if (r5 < 0) goto L_0x006d
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
            goto L_0x0067
        L_0x0055:
            if (r7 == 0) goto L_0x005c
            j$.util.concurrent.ConcurrentHashMap$l r3 = r5.d     // Catch:{ all -> 0x00ac }
            r7.d = r3     // Catch:{ all -> 0x00ac }
            goto L_0x0067
        L_0x005c:
            j$.util.concurrent.ConcurrentHashMap$l r5 = r5.d     // Catch:{ all -> 0x00ac }
        L_0x005e:
            k(r1, r3, r5)     // Catch:{ all -> 0x00ac }
            goto L_0x0067
        L_0x0062:
            j$.util.concurrent.ConcurrentHashMap$l r7 = r5.d     // Catch:{ all -> 0x00ac }
            if (r7 != 0) goto L_0x0069
        L_0x0066:
            r8 = r2
        L_0x0067:
            r7 = 1
            goto L_0x009f
        L_0x0069:
            r11 = r7
            r7 = r5
            r5 = r11
            goto L_0x0032
        L_0x006d:
            boolean r5 = r4 instanceof j$.util.concurrent.ConcurrentHashMap.q     // Catch:{ all -> 0x00ac }
            if (r5 == 0) goto L_0x009e
            r5 = r4
            j$.util.concurrent.ConcurrentHashMap$q r5 = (j$.util.concurrent.ConcurrentHashMap.q) r5     // Catch:{ all -> 0x00ac }
            j$.util.concurrent.ConcurrentHashMap$r r7 = r5.e     // Catch:{ all -> 0x00ac }
            if (r7 == 0) goto L_0x0066
            j$.util.concurrent.ConcurrentHashMap$r r7 = r7.b(r0, r13, r2)     // Catch:{ all -> 0x00ac }
            if (r7 == 0) goto L_0x0066
            java.lang.Object r8 = r7.c     // Catch:{ all -> 0x00ac }
            if (r15 == 0) goto L_0x008c
            if (r15 == r8) goto L_0x008c
            if (r8 == 0) goto L_0x0066
            boolean r10 = r15.equals(r8)     // Catch:{ all -> 0x00ac }
            if (r10 == 0) goto L_0x0066
        L_0x008c:
            if (r14 == 0) goto L_0x0091
            r7.c = r14     // Catch:{ all -> 0x00ac }
            goto L_0x0067
        L_0x0091:
            boolean r7 = r5.g(r7)     // Catch:{ all -> 0x00ac }
            if (r7 == 0) goto L_0x0067
            j$.util.concurrent.ConcurrentHashMap$r r5 = r5.f     // Catch:{ all -> 0x00ac }
            j$.util.concurrent.ConcurrentHashMap$l r5 = s(r5)     // Catch:{ all -> 0x00ac }
            goto L_0x005e
        L_0x009e:
            r8 = r2
        L_0x009f:
            monitor-exit(r4)     // Catch:{ all -> 0x00ac }
            if (r7 == 0) goto L_0x000a
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

    public Set<K> keySet() {
        i iVar = this.d;
        if (iVar != null) {
            return iVar;
        }
        i iVar2 = new i(this, (Object) null);
        this.d = iVar2;
        return iVar2;
    }

    /* access modifiers changed from: package-private */
    public final long m() {
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

    public Object merge(Object obj, Object obj2, BiFunction biFunction) {
        int i2;
        Object obj3;
        Object obj4;
        Object obj5 = obj;
        Object obj6 = obj2;
        BiFunction biFunction2 = biFunction;
        if (obj5 == null || obj6 == null || biFunction2 == null) {
            throw null;
        }
        int l2 = l(obj.hashCode());
        l[] lVarArr = this.a;
        int i3 = 0;
        Object obj7 = null;
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
                                            if (lVar2.a != l2 || ((obj4 = lVar2.b) != obj5 && (obj4 == null || !obj5.equals(obj4)))) {
                                                l lVar3 = lVar2.d;
                                                if (lVar3 == null) {
                                                    lVar2.d = new l(l2, obj5, obj6, (l) null);
                                                    obj3 = obj6;
                                                    break;
                                                }
                                                i7++;
                                                l lVar4 = lVar3;
                                                lVar = lVar2;
                                                lVar2 = lVar4;
                                            }
                                        }
                                        Object apply = biFunction2.apply(lVar2.c, obj6);
                                        if (apply != null) {
                                            lVar2.c = apply;
                                        } else {
                                            l lVar5 = lVar2.d;
                                            if (lVar != null) {
                                                lVar.d = lVar5;
                                            } else {
                                                k(lVarArr, i5, lVar5);
                                            }
                                            i3 = -1;
                                        }
                                        Object obj8 = apply;
                                        i2 = i3;
                                        obj3 = obj8;
                                        i4 = i7;
                                        obj7 = obj3;
                                        i3 = i2;
                                    } else if (n2 instanceof q) {
                                        i4 = 2;
                                        q qVar = (q) n2;
                                        r rVar = qVar.e;
                                        r b2 = rVar == null ? null : rVar.b(l2, obj5, (Class) null);
                                        Object apply2 = b2 == null ? obj6 : biFunction2.apply(b2.c, obj6);
                                        if (apply2 != null) {
                                            if (b2 != null) {
                                                b2.c = apply2;
                                            } else {
                                                qVar.f(l2, obj5, apply2);
                                                i3 = 1;
                                            }
                                        } else if (b2 != null) {
                                            if (qVar.g(b2)) {
                                                k(lVarArr, i5, s(qVar.f));
                                            }
                                            i3 = -1;
                                        }
                                        obj7 = apply2;
                                    }
                                }
                            }
                            if (i4 != 0) {
                                if (i4 >= 8) {
                                    q(lVarArr, i5);
                                }
                                i2 = i3;
                                obj6 = obj7;
                            }
                        }
                    } else if (b(lVarArr, i5, (l) null, new l(l2, obj5, obj6, (l) null))) {
                        break;
                    }
                }
            }
            lVarArr = g();
        }
        if (i2 != 0) {
            a((long) i2, i4);
        }
        return obj6;
    }

    public /* synthetic */ Object merge(Object obj, Object obj2, java.util.function.BiFunction biFunction) {
        return merge(obj, obj2, CLASSNAMEs.a(biFunction));
    }

    public V put(K k2, V v) {
        return h(k2, v, false);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        r(map.size());
        for (Map.Entry next : map.entrySet()) {
            h(next.getKey(), next.getValue(), false);
        }
    }

    public V putIfAbsent(K k2, V v) {
        return h(k2, v, true);
    }

    public V remove(Object obj) {
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
        l[] lVarArr = this.a;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a2 = pVar.a();
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
        replaceAll(CLASSNAMEs.a(biFunction));
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

    public Collection values() {
        u uVar = this.e;
        if (uVar != null) {
            return uVar;
        }
        u uVar2 = new u(this);
        this.e = uVar2;
        return uVar2;
    }

    static abstract class b implements Collection, Serializable {
        final ConcurrentHashMap a;

        b(ConcurrentHashMap concurrentHashMap) {
            this.a = concurrentHashMap;
        }

        public final void clear() {
            this.a.clear();
        }

        public abstract boolean contains(Object obj);

        /* JADX WARNING: Removed duplicated region for block: B:4:0x000c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean containsAll(java.util.Collection r2) {
            /*
                r1 = this;
                if (r2 == r1) goto L_0x001a
                java.util.Iterator r2 = r2.iterator()
            L_0x0006:
                boolean r0 = r2.hasNext()
                if (r0 == 0) goto L_0x001a
                java.lang.Object r0 = r2.next()
                if (r0 == 0) goto L_0x0018
                boolean r0 = r1.contains(r0)
                if (r0 != 0) goto L_0x0006
            L_0x0018:
                r2 = 0
                return r2
            L_0x001a:
                r2 = 1
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.b.containsAll(java.util.Collection):boolean");
        }

        public final boolean isEmpty() {
            return this.a.isEmpty();
        }

        public abstract java.util.Iterator iterator();

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

        public final int size() {
            return this.a.size();
        }

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
                        if (i < NUM) {
                            if (i < NUM) {
                                i3 = (i >>> 1) + 1 + i;
                            }
                            objArr = Arrays.copyOf(objArr, i3);
                            i = i3;
                        } else {
                            throw new OutOfMemoryError("Required array size too large");
                        }
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
                        if (length < NUM) {
                            if (length < NUM) {
                                i3 = (length >>> 1) + 1 + length;
                            }
                            objArr2 = Arrays.copyOf(objArr2, i3);
                            length = i3;
                        } else {
                            throw new OutOfMemoryError("Required array size too large");
                        }
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

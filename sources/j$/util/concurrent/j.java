package j$.util.concurrent;

import j$.CLASSNAMEz;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class j extends CLASSNAMEc implements Set, Serializable, j$.util.Set {
    j(ConcurrentHashMap concurrentHashMap, Object obj) {
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.j.equals(java.lang.Object):boolean");
    }

    public void forEach(Consumer consumer) {
        consumer.getClass();
        m[] mVarArr = this.a.a;
        if (mVarArr != null) {
            q qVar = new q(mVarArr, mVarArr.length, 0, mVarArr.length);
            while (true) {
                m a = qVar.a();
                if (a != null) {
                    consumer.accept(a.b);
                } else {
                    return;
                }
            }
        }
    }

    public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
        forEach(CLASSNAMEz.b(consumer));
    }

    public int hashCode() {
        Iterator it = iterator();
        int i = 0;
        while (((CLASSNAMEb) it).hasNext()) {
            i += ((i) it).next().hashCode();
        }
        return i;
    }

    public Iterator iterator() {
        ConcurrentHashMap concurrentHashMap = this.a;
        m[] mVarArr = concurrentHashMap.a;
        int length = mVarArr == null ? 0 : mVarArr.length;
        return new i(mVarArr, length, 0, length, concurrentHashMap);
    }

    public boolean remove(Object obj) {
        return this.a.remove(obj) != null;
    }

    public Spliterator spliterator() {
        ConcurrentHashMap concurrentHashMap = this.a;
        long m = concurrentHashMap.m();
        m[] mVarArr = concurrentHashMap.a;
        int length = mVarArr == null ? 0 : mVarArr.length;
        long j = 0;
        if (m >= 0) {
            j = m;
        }
        return new k(mVarArr, length, 0, length, j);
    }
}

package j$.util.concurrent;

import j$.CLASSNAMEz;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

final class v extends CLASSNAMEc implements Collection, Serializable, j$.util.Collection {
    v(ConcurrentHashMap concurrentHashMap) {
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
        m[] mVarArr = this.a.a;
        if (mVarArr != null) {
            q qVar = new q(mVarArr, mVarArr.length, 0, mVarArr.length);
            while (true) {
                m a = qVar.a();
                if (a != null) {
                    consumer.accept(a.c);
                } else {
                    return;
                }
            }
        }
    }

    public /* synthetic */ void forEach(java.util.function.Consumer consumer) {
        forEach(CLASSNAMEz.b(consumer));
    }

    public final Iterator iterator() {
        ConcurrentHashMap concurrentHashMap = this.a;
        m[] mVarArr = concurrentHashMap.a;
        int length = mVarArr == null ? 0 : mVarArr.length;
        return new t(mVarArr, length, 0, length, concurrentHashMap);
    }

    public final boolean remove(Object obj) {
        CLASSNAMEb bVar;
        if (obj == null) {
            return false;
        }
        Iterator it = iterator();
        do {
            bVar = (CLASSNAMEb) it;
            if (!bVar.hasNext()) {
                return false;
            }
        } while (!obj.equals(((t) it).next()));
        bVar.remove();
        return true;
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
        return new u(mVarArr, length, 0, length, j);
    }
}

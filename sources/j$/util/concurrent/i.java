package j$.util.concurrent;

import j$.CLASSNAMEz;
import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class i extends CLASSNAMEb implements Iterator, Enumeration, j$.util.Iterator {
    i(m[] mVarArr, int i, int i2, int i3, ConcurrentHashMap concurrentHashMap) {
        super(mVarArr, i, i2, i3, concurrentHashMap);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, CLASSNAMEz.b(consumer));
    }

    public final Object next() {
        m mVar = this.b;
        if (mVar != null) {
            Object obj = mVar.b;
            this.j = mVar;
            a();
            return obj;
        }
        throw new NoSuchElementException();
    }

    public final Object nextElement() {
        return next();
    }
}

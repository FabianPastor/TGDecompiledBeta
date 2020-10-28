package j$.util.concurrent;

import j$.CLASSNAMEz;
import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class e extends CLASSNAMEb implements Iterator, j$.util.Iterator {
    e(m[] mVarArr, int i, int i2, int i3, ConcurrentHashMap concurrentHashMap) {
        super(mVarArr, i, i2, i3, concurrentHashMap);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, CLASSNAMEz.b(consumer));
    }

    public Object next() {
        m mVar = this.b;
        if (mVar != null) {
            Object obj = mVar.b;
            Object obj2 = mVar.c;
            this.j = mVar;
            a();
            return new l(obj, obj2, this.i);
        }
        throw new NoSuchElementException();
    }
}

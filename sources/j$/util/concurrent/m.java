package j$.util.concurrent;

import j$.util.Map;
import java.util.Map;

class m implements Map.Entry, Map.Entry {
    final int a;
    final Object b;
    volatile Object c;
    volatile m d;

    m(int hash, Object key, Object val, m next) {
        this.a = hash;
        this.b = key;
        this.c = val;
        this.d = next;
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

    public final String toString() {
        return this.b + "=" + this.c;
    }

    public final Object setValue(Object obj) {
        throw new UnsupportedOperationException();
    }

    public final boolean equals(Object o) {
        Object obj;
        if (o instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry) o;
            Map.Entry<?, ?> e = entry;
            Object key = entry.getKey();
            Object k = key;
            if (key != null) {
                Object value = e.getValue();
                Object v = value;
                if (value != null && (k == (obj = this.b) || k.equals(obj))) {
                    Object obj2 = this.c;
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
    public m a(int h, Object k) {
        m mVar;
        m mVar2 = this;
        if (k == null) {
            return null;
        }
        do {
            if (mVar2.a == h) {
                K k2 = mVar2.b;
                K ek = k2;
                if (k2 == k || (ek != null && k.equals(ek))) {
                    return mVar2;
                }
            }
            mVar = mVar2.d;
            mVar2 = mVar;
        } while (mVar != null);
        return null;
    }
}

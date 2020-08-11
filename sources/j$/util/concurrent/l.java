package j$.util.concurrent;

import j$.util.Map;
import java.util.Map;

final class l implements Map.Entry, Map.Entry {
    final Object a;
    Object b;
    final ConcurrentHashMap c;

    l(Object key, Object val, ConcurrentHashMap concurrentHashMap) {
        this.a = key;
        this.b = val;
        this.c = concurrentHashMap;
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

    public String toString() {
        return this.a + "=" + this.b;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0020, code lost:
        r0 = r4.b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0016, code lost:
        r0 = r4.a;
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
            java.lang.Object r0 = r4.a
            if (r2 == r0) goto L_0x0020
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x002c
        L_0x0020:
            java.lang.Object r0 = r4.b
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.l.equals(java.lang.Object):boolean");
    }

    public Object setValue(Object value) {
        if (value != null) {
            V v = this.b;
            this.b = value;
            this.c.put(this.a, value);
            return v;
        }
        throw null;
    }
}

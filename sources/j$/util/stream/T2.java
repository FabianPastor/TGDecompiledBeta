package j$.util.stream;

import j$.util.Map;
import j$.util.Spliterator;
import java.util.EnumMap;
import java.util.Map;

enum T2 {
    DISTINCT(0, r2),
    SORTED(1, r5),
    ORDERED(2, r7),
    SIZED(3, r11),
    SHORT_CIRCUIT(12, r13);
    
    static final int a = 0;
    static final int b = 0;
    static final int c = 0;
    private static final int d = 0;
    private static final int e = 0;
    private static final int f = 0;
    static final int g = 0;
    static final int h = 0;
    static final int i = 0;
    static final int j = 0;
    static final int k = 0;
    static final int l = 0;
    static final int m = 0;
    static final int n = 0;
    static final int o = 0;
    static final int p = 0;
    private final Map r;
    private final int s;
    private final int t;
    private final int u;
    private final int v;

    private static class a {
        final Map a;

        a(Map map) {
            this.a = map;
        }

        /* access modifiers changed from: package-private */
        public a a(b bVar) {
            this.a.put(bVar, 2);
            return this;
        }

        /* access modifiers changed from: package-private */
        public a b(b bVar) {
            this.a.put(bVar, 1);
            return this;
        }

        /* access modifiers changed from: package-private */
        public a c(b bVar) {
            this.a.put(bVar, 3);
            return this;
        }
    }

    enum b {
        SPLITERATOR,
        STREAM,
        OP,
        TERMINAL_OP,
        UPSTREAM_TERMINAL_OP
    }

    static {
        T2 t2;
        b bVar;
        b bVar2;
        b bVar3;
        T2 t22;
        T2 t23;
        b bVar4;
        b bVar5;
        T2 t24;
        T2 t25;
        a = b(bVar);
        int b2 = b(bVar2);
        b = b2;
        c = b(bVar3);
        b(bVar4);
        b(bVar5);
        T2[] values = values();
        int i2 = 0;
        for (int i3 = 0; i3 < 5; i3++) {
            i2 |= values[i3].v;
        }
        d = i2;
        e = b2;
        int i4 = b2 << 1;
        f = i4;
        g = b2 | i4;
        h = t2.t;
        i = t2.u;
        j = t22.t;
        k = t22.u;
        l = t23.t;
        m = t23.u;
        n = t24.t;
        o = t24.u;
        p = t25.t;
    }

    private T2(int i2, a aVar) {
        b[] values = b.values();
        for (int i3 = 0; i3 < 5; i3++) {
            b bVar = values[i3];
            Map map = aVar.a;
            if (map instanceof j$.util.Map) {
                ((j$.util.Map) map).putIfAbsent(bVar, 0);
            } else {
                Map.CC.$default$putIfAbsent(map, bVar, 0);
            }
        }
        this.r = aVar.a;
        int i4 = i2 * 2;
        this.s = i4;
        this.t = 1 << i4;
        this.u = 2 << i4;
        this.v = 3 << i4;
    }

    static int a(int i2, int i3) {
        return i2 | (i3 & (i2 == 0 ? d : ((((e & i2) << 1) | i2) | ((f & i2) >> 1)) ^ -1));
    }

    private static int b(b bVar) {
        T2[] values = values();
        int i2 = 0;
        for (int i3 = 0; i3 < 5; i3++) {
            T2 t2 = values[i3];
            i2 |= ((Integer) t2.r.get(bVar)).intValue() << t2.s;
        }
        return i2;
    }

    static int c(Spliterator spliterator) {
        int characteristics = spliterator.characteristics();
        return ((characteristics & 4) == 0 || spliterator.getComparator() == null) ? a & characteristics : a & characteristics & -5;
    }

    private static a f(b bVar) {
        EnumMap enumMap = new EnumMap(b.class);
        a aVar = new a(enumMap);
        enumMap.put(bVar, 1);
        return aVar;
    }

    static int g(int i2) {
        return i2 & ((i2 ^ -1) >> 1) & e;
    }

    /* access modifiers changed from: package-private */
    public boolean d(int i2) {
        return (i2 & this.v) == this.t;
    }

    /* access modifiers changed from: package-private */
    public boolean e(int i2) {
        int i3 = this.v;
        return (i2 & i3) == i3;
    }
}

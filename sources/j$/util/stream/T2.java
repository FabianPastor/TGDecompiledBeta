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
    
    static final int f = 0;
    static final int g = 0;
    static final int h = 0;
    private static final int i = 0;
    private static final int j = 0;
    private static final int k = 0;
    static final int l = 0;
    static final int m = 0;
    static final int n = 0;
    static final int o = 0;
    static final int p = 0;
    static final int q = 0;
    static final int r = 0;
    static final int s = 0;
    static final int t = 0;
    static final int u = 0;

    /* renamed from: a  reason: collision with root package name */
    private final Map var_a;
    private final int b;
    private final int c;
    private final int d;
    private final int e;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        final Map var_a;

        a(Map map) {
            this.var_a = map;
        }

        /* access modifiers changed from: package-private */
        public a a(b bVar) {
            this.var_a.put(bVar, 2);
            return this;
        }

        /* access modifiers changed from: package-private */
        public a b(b bVar) {
            this.var_a.put(bVar, 1);
            return this;
        }

        /* access modifiers changed from: package-private */
        public a c(b bVar) {
            this.var_a.put(bVar, 3);
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
        f = b(bVar);
        int b2 = b(bVar2);
        g = b2;
        h = b(bVar3);
        b(bVar4);
        b(bVar5);
        T2[] values = values();
        int i2 = 0;
        for (int i3 = 0; i3 < 5; i3++) {
            i2 |= values[i3].e;
        }
        i = i2;
        j = b2;
        int i4 = b2 << 1;
        k = i4;
        l = b2 | i4;
        m = t2.c;
        n = t2.d;
        o = t22.c;
        p = t22.d;
        q = t23.c;
        r = t23.d;
        s = t24.c;
        t = t24.d;
        u = t25.c;
    }

    private T2(int i2, a aVar) {
        b[] values = b.values();
        for (int i3 = 0; i3 < 5; i3++) {
            b bVar = values[i3];
            Map map = aVar.var_a;
            if (map instanceof j$.util.Map) {
                ((j$.util.Map) map).putIfAbsent(bVar, 0);
            } else {
                Map.CC.$default$putIfAbsent(map, bVar, 0);
            }
        }
        this.var_a = aVar.var_a;
        int i4 = i2 * 2;
        this.b = i4;
        this.c = 1 << i4;
        this.d = 2 << i4;
        this.e = 3 << i4;
    }

    static int a(int i2, int i3) {
        return i2 | (i3 & (i2 == 0 ? i : ((((j & i2) << 1) | i2) | ((k & i2) >> 1)) ^ -1));
    }

    private static int b(b bVar) {
        T2[] values = values();
        int i2 = 0;
        for (int i3 = 0; i3 < 5; i3++) {
            T2 t2 = values[i3];
            i2 |= ((Integer) t2.var_a.get(bVar)).intValue() << t2.b;
        }
        return i2;
    }

    static int c(Spliterator spliterator) {
        int characteristics = spliterator.characteristics();
        return ((characteristics & 4) == 0 || spliterator.getComparator() == null) ? f & characteristics : f & characteristics & -5;
    }

    private static a f(b bVar) {
        a aVar = new a(new EnumMap(b.class));
        aVar.var_a.put(bVar, 1);
        return aVar;
    }

    static int g(int i2) {
        return i2 & ((i2 ^ -1) >> 1) & j;
    }

    /* access modifiers changed from: package-private */
    public boolean d(int i2) {
        return (i2 & this.e) == this.c;
    }

    /* access modifiers changed from: package-private */
    public boolean e(int i2) {
        int i3 = this.e;
        return (i2 & i3) == i3;
    }
}

package j$.util.stream;

import j$.util.Map;
import j$.util.Spliterator;
import java.util.EnumMap;
import java.util.Map;

/* renamed from: j$.util.stream.g6  reason: case insensitive filesystem */
enum CLASSNAMEg6 {
    DISTINCT(0, r2),
    SORTED(1, r7),
    ORDERED(2, r9),
    SIZED(3, r13),
    SHORT_CIRCUIT(12, r14);
    
    static final int k = 0;
    static final int l = 0;
    static final int m = 0;
    private static final int n = 0;
    private static final int o = 0;
    private static final int p = 0;
    static final int q = 0;
    static final int r = 0;
    static final int s = 0;
    static final int t = 0;
    static final int u = 0;
    static final int v = 0;
    static final int w = 0;
    static final int x = 0;
    static final int y = 0;
    static final int z = 0;
    private final Map a;
    private final int b;
    private final int c;
    private final int d;
    private final int e;

    static {
        CLASSNAMEg6 g6Var;
        CLASSNAMEf6 f6Var;
        CLASSNAMEf6 f6Var2;
        CLASSNAMEf6 f6Var3;
        CLASSNAMEg6 g6Var2;
        CLASSNAMEg6 g6Var3;
        CLASSNAMEf6 f6Var4;
        CLASSNAMEf6 f6Var5;
        CLASSNAMEg6 g6Var4;
        CLASSNAMEg6 g6Var5;
        k = b(f6Var);
        int b2 = b(f6Var2);
        l = b2;
        m = b(f6Var3);
        b(f6Var4);
        b(f6Var5);
        CLASSNAMEg6[] values = values();
        int i = 0;
        for (int i2 = 0; i2 < 5; i2++) {
            i |= values[i2].e;
        }
        n = i;
        o = b2;
        int i3 = b2 << 1;
        p = i3;
        q = b2 | i3;
        r = g6Var.c;
        s = g6Var.d;
        t = g6Var2.c;
        u = g6Var2.d;
        v = g6Var3.c;
        w = g6Var3.d;
        x = g6Var4.c;
        y = g6Var4.d;
        z = g6Var5.c;
    }

    private CLASSNAMEg6(int i, CLASSNAMEe6 e6Var) {
        CLASSNAMEf6[] values = CLASSNAMEf6.values();
        for (int i2 = 0; i2 < 5; i2++) {
            CLASSNAMEf6 f6Var = values[i2];
            Map map = e6Var.a;
            if (map instanceof j$.util.Map) {
                ((j$.util.Map) map).putIfAbsent(f6Var, 0);
            } else {
                Map.CC.$default$putIfAbsent(map, f6Var, 0);
            }
        }
        this.a = e6Var.a;
        int i3 = i * 2;
        this.b = i3;
        this.c = 1 << i3;
        this.d = 2 << i3;
        this.e = 3 << i3;
    }

    static int a(int i, int i2) {
        return i | (i2 & (i == 0 ? n : ((((o & i) << 1) | i) | ((p & i) >> 1)) ^ -1));
    }

    private static int b(CLASSNAMEf6 f6Var) {
        CLASSNAMEg6[] values = values();
        int i = 0;
        for (int i2 = 0; i2 < 5; i2++) {
            CLASSNAMEg6 g6Var = values[i2];
            i |= ((Integer) g6Var.a.get(f6Var)).intValue() << g6Var.b;
        }
        return i;
    }

    static int c(Spliterator spliterator) {
        int characteristics = spliterator.characteristics();
        return ((characteristics & 4) == 0 || spliterator.getComparator() == null) ? k & characteristics : k & characteristics & -5;
    }

    private static CLASSNAMEe6 f(CLASSNAMEf6 f6Var) {
        CLASSNAMEe6 e6Var = new CLASSNAMEe6(new EnumMap(CLASSNAMEf6.class));
        e6Var.a.put(f6Var, 1);
        return e6Var;
    }

    static int g(int i) {
        return i & ((i ^ -1) >> 1) & o;
    }

    /* access modifiers changed from: package-private */
    public boolean d(int i) {
        return (i & this.e) == this.c;
    }

    /* access modifiers changed from: package-private */
    public boolean e(int i) {
        int i2 = this.e;
        return (i & i2) == i2;
    }
}

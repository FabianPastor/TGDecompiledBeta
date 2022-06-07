package j$.util.stream;

import j$.util.Map;
import j$.util.u;
import java.util.EnumMap;
import java.util.Map;

/* renamed from: j$.util.stream.d4  reason: case insensitive filesystem */
enum CLASSNAMEd4 {
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
    private final Map a;
    private final int b;
    private final int c;
    private final int d;
    private final int e;

    static {
        CLASSNAMEd4 d4Var;
        CLASSNAMEc4 c4Var;
        CLASSNAMEc4 c4Var2;
        CLASSNAMEc4 c4Var3;
        int i2;
        CLASSNAMEd4 d4Var2;
        CLASSNAMEd4 d4Var3;
        CLASSNAMEc4 c4Var4;
        CLASSNAMEc4 c4Var5;
        CLASSNAMEd4 d4Var4;
        CLASSNAMEd4 d4Var5;
        f = b(c4Var);
        int b2 = b(c4Var2);
        g = b2;
        h = b(c4Var3);
        b(c4Var4);
        b(c4Var5);
        int i3 = 0;
        for (CLASSNAMEd4 d4Var6 : values()) {
            i3 |= d4Var6.e;
        }
        i = i3;
        j = b2;
        int i4 = b2 << 1;
        k = i4;
        l = b2 | i4;
        m = d4Var.c;
        n = d4Var.d;
        o = d4Var2.c;
        p = d4Var2.d;
        q = d4Var3.c;
        r = d4Var3.d;
        s = d4Var4.c;
        t = d4Var4.d;
        u = d4Var5.c;
    }

    private CLASSNAMEd4(int i2, CLASSNAMEb4 b4Var) {
        for (CLASSNAMEc4 c4Var : CLASSNAMEc4.values()) {
            Map map = b4Var.a;
            if (map instanceof j$.util.Map) {
                ((j$.util.Map) map).putIfAbsent(c4Var, 0);
            } else {
                Map.CC.$default$putIfAbsent(map, c4Var, 0);
            }
        }
        this.a = b4Var.a;
        int i3 = i2 * 2;
        this.b = i3;
        this.c = 1 << i3;
        this.d = 2 << i3;
        this.e = 3 << i3;
    }

    static int a(int i2, int i3) {
        return i2 | (i3 & (i2 == 0 ? i : ((((j & i2) << 1) | i2) | ((k & i2) >> 1)) ^ -1));
    }

    private static int b(CLASSNAMEc4 c4Var) {
        int i2 = 0;
        for (CLASSNAMEd4 d4Var : values()) {
            i2 |= ((Integer) d4Var.a.get(c4Var)).intValue() << d4Var.b;
        }
        return i2;
    }

    static int c(u uVar) {
        int characteristics = uVar.characteristics();
        return ((characteristics & 4) == 0 || uVar.getComparator() == null) ? f & characteristics : f & characteristics & -5;
    }

    private static CLASSNAMEb4 f(CLASSNAMEc4 c4Var) {
        EnumMap enumMap = new EnumMap(CLASSNAMEc4.class);
        CLASSNAMEb4 b4Var = new CLASSNAMEb4(enumMap);
        enumMap.put(c4Var, 1);
        return b4Var;
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

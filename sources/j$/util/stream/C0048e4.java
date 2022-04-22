package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.y;
import java.util.EnumMap;
import java.util.Map;

/* renamed from: j$.util.stream.e4  reason: case insensitive filesystem */
enum CLASSNAMEe4 {
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
        CLASSNAMEe4 e4Var;
        CLASSNAMEd4 d4Var;
        CLASSNAMEd4 d4Var2;
        CLASSNAMEd4 d4Var3;
        int i2;
        CLASSNAMEe4 e4Var2;
        CLASSNAMEe4 e4Var3;
        CLASSNAMEd4 d4Var4;
        CLASSNAMEd4 d4Var5;
        CLASSNAMEe4 e4Var4;
        CLASSNAMEe4 e4Var5;
        f = b(d4Var);
        int b2 = b(d4Var2);
        g = b2;
        h = b(d4Var3);
        b(d4Var4);
        b(d4Var5);
        int i3 = 0;
        for (CLASSNAMEe4 e4Var6 : values()) {
            i3 |= e4Var6.e;
        }
        i = i3;
        j = b2;
        int i4 = b2 << 1;
        k = i4;
        l = b2 | i4;
        m = e4Var.c;
        n = e4Var.d;
        o = e4Var2.c;
        p = e4Var2.d;
        q = e4Var3.c;
        r = e4Var3.d;
        s = e4Var4.c;
        t = e4Var4.d;
        u = e4Var5.c;
    }

    private CLASSNAMEe4(int i2, CLASSNAMEc4 c4Var) {
        for (CLASSNAMEd4 B : CLASSNAMEd4.values()) {
            CLASSNAMEa.B(c4Var.a, B, 0);
        }
        this.a = c4Var.a;
        int i3 = i2 * 2;
        this.b = i3;
        this.c = 1 << i3;
        this.d = 2 << i3;
        this.e = 3 << i3;
    }

    static int a(int i2, int i3) {
        return i2 | (i3 & (i2 == 0 ? i : ((((j & i2) << 1) | i2) | ((k & i2) >> 1)) ^ -1));
    }

    private static int b(CLASSNAMEd4 d4Var) {
        int i2 = 0;
        for (CLASSNAMEe4 e4Var : values()) {
            i2 |= ((Integer) e4Var.a.get(d4Var)).intValue() << e4Var.b;
        }
        return i2;
    }

    static int c(y yVar) {
        int characteristics = yVar.characteristics();
        return ((characteristics & 4) == 0 || yVar.getComparator() == null) ? f & characteristics : f & characteristics & -5;
    }

    private static CLASSNAMEc4 f(CLASSNAMEd4 d4Var) {
        EnumMap enumMap = new EnumMap(CLASSNAMEd4.class);
        CLASSNAMEc4 c4Var = new CLASSNAMEc4(enumMap);
        enumMap.put(d4Var, 1);
        return c4Var;
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

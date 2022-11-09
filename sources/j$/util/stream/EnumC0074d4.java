package j$.util.stream;

import java.util.EnumMap;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* JADX WARN: Init of enum DISTINCT can be incorrect */
/* JADX WARN: Init of enum ORDERED can be incorrect */
/* JADX WARN: Init of enum SHORT_CIRCUIT can be incorrect */
/* JADX WARN: Init of enum SIZED can be incorrect */
/* JADX WARN: Init of enum SORTED can be incorrect */
/* renamed from: j$.util.stream.d4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public enum EnumCLASSNAMEd4 {
    DISTINCT(0, r2),
    SORTED(1, r5),
    ORDERED(2, r7),
    SIZED(3, r11),
    SHORT_CIRCUIT(12, r13);
    
    static final int f;
    static final int g;
    static final int h;
    private static final int i;
    private static final int j;
    private static final int k;
    static final int l;
    static final int m;
    static final int n;
    static final int o;
    static final int p;
    static final int q;
    static final int r;
    static final int s;
    static final int t;
    static final int u;
    private final Map a;
    private final int b;
    private final int c;
    private final int d;
    private final int e;

    static {
        EnumCLASSNAMEc4 enumCLASSNAMEc4 = EnumCLASSNAMEc4.SPLITERATOR;
        CLASSNAMEb4 f2 = f(enumCLASSNAMEc4);
        EnumCLASSNAMEc4 enumCLASSNAMECLASSNAME = EnumCLASSNAMEc4.STREAM;
        f2.b(enumCLASSNAMECLASSNAME);
        EnumCLASSNAMEc4 enumCLASSNAMECLASSNAME = EnumCLASSNAMEc4.OP;
        f2.c(enumCLASSNAMECLASSNAME);
        EnumCLASSNAMEd4 enumCLASSNAMEd4 = DISTINCT;
        CLASSNAMEb4 f3 = f(enumCLASSNAMEc4);
        f3.b(enumCLASSNAMECLASSNAME);
        f3.c(enumCLASSNAMECLASSNAME);
        EnumCLASSNAMEd4 enumCLASSNAMEd42 = SORTED;
        CLASSNAMEb4 f4 = f(enumCLASSNAMEc4);
        f4.b(enumCLASSNAMECLASSNAME);
        f4.c(enumCLASSNAMECLASSNAME);
        EnumCLASSNAMEc4 enumCLASSNAMECLASSNAME = EnumCLASSNAMEc4.TERMINAL_OP;
        f4.a(enumCLASSNAMECLASSNAME);
        EnumCLASSNAMEc4 enumCLASSNAMECLASSNAME = EnumCLASSNAMEc4.UPSTREAM_TERMINAL_OP;
        f4.a(enumCLASSNAMECLASSNAME);
        EnumCLASSNAMEd4 enumCLASSNAMEd43 = ORDERED;
        CLASSNAMEb4 f5 = f(enumCLASSNAMEc4);
        f5.b(enumCLASSNAMECLASSNAME);
        f5.a(enumCLASSNAMECLASSNAME);
        EnumCLASSNAMEd4 enumCLASSNAMEd44 = SIZED;
        f(enumCLASSNAMECLASSNAME).b(enumCLASSNAMECLASSNAME);
        EnumCLASSNAMEd4 enumCLASSNAMEd45 = SHORT_CIRCUIT;
        f = b(enumCLASSNAMEc4);
        int b = b(enumCLASSNAMECLASSNAME);
        g = b;
        h = b(enumCLASSNAMECLASSNAME);
        b(enumCLASSNAMECLASSNAME);
        b(enumCLASSNAMECLASSNAME);
        int i2 = 0;
        for (EnumCLASSNAMEd4 enumCLASSNAMEd46 : values()) {
            i2 |= enumCLASSNAMEd46.e;
        }
        i = i2;
        j = b;
        int i3 = b << 1;
        k = i3;
        l = b | i3;
        m = enumCLASSNAMEd4.c;
        n = enumCLASSNAMEd4.d;
        o = enumCLASSNAMEd42.c;
        p = enumCLASSNAMEd42.d;
        q = enumCLASSNAMEd43.c;
        r = enumCLASSNAMEd43.d;
        s = enumCLASSNAMEd44.c;
        t = enumCLASSNAMEd44.d;
        u = enumCLASSNAMEd45.c;
    }

    EnumCLASSNAMEd4(int i2, CLASSNAMEb4 CLASSNAMEb4) {
        EnumCLASSNAMEc4[] values;
        for (EnumCLASSNAMEc4 enumCLASSNAMEc4 : EnumCLASSNAMEc4.values()) {
            Map map = CLASSNAMEb4.a;
            if (map instanceof j$.util.Map) {
                ((j$.util.Map) map).putIfAbsent(enumCLASSNAMEc4, 0);
            } else {
                map.get(enumCLASSNAMEc4);
            }
        }
        this.a = CLASSNAMEb4.a;
        int i3 = i2 * 2;
        this.b = i3;
        this.c = 1 << i3;
        this.d = 2 << i3;
        this.e = 3 << i3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int a(int i2, int i3) {
        return i2 | (i3 & (i2 == 0 ? i : ((((j & i2) << 1) | i2) | ((k & i2) >> 1)) ^ (-1)));
    }

    private static int b(EnumCLASSNAMEc4 enumCLASSNAMEc4) {
        EnumCLASSNAMEd4[] values;
        int i2 = 0;
        for (EnumCLASSNAMEd4 enumCLASSNAMEd4 : values()) {
            i2 |= ((Integer) enumCLASSNAMEd4.a.get(enumCLASSNAMEc4)).intValue() << enumCLASSNAMEd4.b;
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int c(j$.util.u uVar) {
        int characteristics = uVar.characteristics();
        return ((characteristics & 4) == 0 || uVar.getComparator() == null) ? f & characteristics : f & characteristics & (-5);
    }

    private static CLASSNAMEb4 f(EnumCLASSNAMEc4 enumCLASSNAMEc4) {
        EnumMap enumMap = new EnumMap(EnumCLASSNAMEc4.class);
        CLASSNAMEb4 CLASSNAMEb4 = new CLASSNAMEb4(enumMap);
        enumMap.put((EnumMap) enumCLASSNAMEc4, (EnumCLASSNAMEc4) 1);
        return CLASSNAMEb4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int g(int i2) {
        return i2 & ((i2 ^ (-1)) >> 1) & j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean d(int i2) {
        return (i2 & this.e) == this.c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean e(int i2) {
        int i3 = this.e;
        return (i2 & i3) == i3;
    }
}

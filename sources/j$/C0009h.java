package j$;

/* renamed from: j$.h  reason: case insensitive filesystem */
public /* synthetic */ class CLASSNAMEh {
    public static /* synthetic */ int a(int i, int i2) {
        int i3 = i % i2;
        if (i3 == 0) {
            return 0;
        }
        return (((i ^ i2) >> 31) | 1) > 0 ? i3 : i3 + i2;
    }
}

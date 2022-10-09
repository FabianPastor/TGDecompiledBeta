package j$.time;
/* loaded from: classes2.dex */
public enum d {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;
    
    private static final d[] a = values();

    public static d a(int i) {
        if (i < 1 || i > 12) {
            throw new a("Invalid value for MonthOfYear: " + i);
        }
        return a[i - 1];
    }
}

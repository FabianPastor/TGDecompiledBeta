package j$.time;

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
    
    private static final d[] a = null;

    static {
        a = values();
    }

    public static d a(int i) {
        if (i >= 1 && i <= 12) {
            return a[i - 1];
        }
        throw new a("Invalid value for MonthOfYear: " + i);
    }
}

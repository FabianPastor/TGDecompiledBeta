package j$.time.t;

import j$.time.f;

public enum u implements s {
    BCE,
    CE;

    public static u A(int isoEra) {
        if (isoEra == 0) {
            return BCE;
        }
        if (isoEra == 1) {
            return CE;
        }
        throw new f("Invalid era: " + isoEra);
    }

    public int getValue() {
        return ordinal();
    }
}

package j$.time.format;

public enum K {
    FULL(2, 0),
    FULL_STANDALONE(32770, 0),
    SHORT(1, 1),
    SHORT_STANDALONE(32769, 1),
    NARROW(4, 1),
    NARROW_STANDALONE(32772, 1);
    
    private final int a;

    private K(int calendarStyle, int zoneNameStyleIndex) {
        this.a = zoneNameStyleIndex;
    }

    /* access modifiers changed from: package-private */
    public int i() {
        return this.a;
    }
}

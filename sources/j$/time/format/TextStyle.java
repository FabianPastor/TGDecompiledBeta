package j$.time.format;

public enum TextStyle {
    FULL(2, 0),
    FULL_STANDALONE(32770, 0),
    SHORT(1, 1),
    SHORT_STANDALONE(32769, 1),
    NARROW(4, 1),
    NARROW_STANDALONE(32772, 1);
    
    private final int calendarStyle;
    private final int zoneNameStyleIndex;

    private TextStyle(int calendarStyle2, int zoneNameStyleIndex2) {
        this.calendarStyle = calendarStyle2;
        this.zoneNameStyleIndex = zoneNameStyleIndex2;
    }

    public boolean isStandalone() {
        return (ordinal() & 1) == 1;
    }

    public TextStyle asStandalone() {
        return values()[ordinal() | 1];
    }

    public TextStyle asNormal() {
        return values()[ordinal() & -2];
    }

    /* access modifiers changed from: package-private */
    public int toCalendarStyle() {
        return this.calendarStyle;
    }

    /* access modifiers changed from: package-private */
    public int zoneNameStyleIndex() {
        return this.zoneNameStyleIndex;
    }
}

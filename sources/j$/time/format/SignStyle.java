package j$.time.format;

public enum SignStyle {
    NORMAL,
    ALWAYS,
    NEVER,
    NOT_NEGATIVE,
    EXCEEDS_PAD;

    /* access modifiers changed from: package-private */
    public boolean parse(boolean positive, boolean strict, boolean fixedWidth) {
        switch (ordinal()) {
            case 0:
                if (!positive || !strict) {
                    return true;
                }
                return false;
            case 1:
            case 4:
                return true;
            default:
                if (strict || fixedWidth) {
                    return false;
                }
                return true;
        }
    }
}

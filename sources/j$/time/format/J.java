package j$.time.format;

public enum J {
    NORMAL,
    ALWAYS,
    NEVER,
    NOT_NEGATIVE,
    EXCEEDS_PAD;

    /* access modifiers changed from: package-private */
    public boolean i(boolean positive, boolean strict, boolean fixedWidth) {
        int ordinal = ordinal();
        if (ordinal != 0) {
            if (ordinal == 1 || ordinal == 4) {
                return true;
            }
            if (strict || fixedWidth) {
                return false;
            }
            return true;
        } else if (!positive || !strict) {
            return true;
        } else {
            return false;
        }
    }
}

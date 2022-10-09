package j$.time.format;
/* loaded from: classes2.dex */
enum l implements g {
    SENSITIVE,
    INSENSITIVE,
    STRICT,
    LENIENT;

    @Override // java.lang.Enum
    public String toString() {
        int ordinal = ordinal();
        if (ordinal != 0) {
            if (ordinal == 1) {
                return "ParseCaseSensitive(false)";
            }
            if (ordinal == 2) {
                return "ParseStrict(true)";
            }
            if (ordinal != 3) {
                throw new IllegalStateException("Unreachable");
            }
            return "ParseStrict(false)";
        }
        return "ParseCaseSensitive(true)";
    }
}

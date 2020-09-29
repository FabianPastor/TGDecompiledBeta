package j$.time.format;

enum t implements CLASSNAMEj {
    SENSITIVE,
    INSENSITIVE,
    STRICT,
    LENIENT;

    public boolean i(C context, StringBuilder buf) {
        return true;
    }

    public int p(A context, CharSequence text, int position) {
        int ordinal = ordinal();
        if (ordinal == 0) {
            context.m(true);
        } else if (ordinal == 1) {
            context.m(false);
        } else if (ordinal == 2) {
            context.q(true);
        } else if (ordinal == 3) {
            context.q(false);
        }
        return position;
    }

    public String toString() {
        int ordinal = ordinal();
        if (ordinal == 0) {
            return "ParseCaseSensitive(true)";
        }
        if (ordinal == 1) {
            return "ParseCaseSensitive(false)";
        }
        if (ordinal == 2) {
            return "ParseStrict(true)";
        }
        if (ordinal == 3) {
            return "ParseStrict(false)";
        }
        throw new IllegalStateException("Unreachable");
    }
}

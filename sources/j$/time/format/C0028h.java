package j$.time.format;

/* renamed from: j$.time.format.h  reason: case insensitive filesystem */
final class CLASSNAMEh implements CLASSNAMEj {
    private final char a;

    CLASSNAMEh(char literal) {
        this.a = literal;
    }

    public boolean i(C context, StringBuilder buf) {
        buf.append(this.a);
        return true;
    }

    public int p(A context, CharSequence text, int position) {
        if (position == text.length()) {
            return position ^ -1;
        }
        char ch = text.charAt(position);
        if (ch == this.a || (!context.k() && (Character.toUpperCase(ch) == Character.toUpperCase(this.a) || Character.toLowerCase(ch) == Character.toLowerCase(this.a)))) {
            return position + 1;
        }
        return position ^ -1;
    }

    public String toString() {
        if (this.a == '\'') {
            return "''";
        }
        return "'" + this.a + "'";
    }
}

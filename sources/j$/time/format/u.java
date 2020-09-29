package j$.time.format;

final class u implements CLASSNAMEj {
    private final String a;

    u(String literal) {
        this.a = literal;
    }

    public boolean i(C context, StringBuilder buf) {
        buf.append(this.a);
        return true;
    }

    public int p(A context, CharSequence text, int position) {
        if (position > text.length() || position < 0) {
            throw new IndexOutOfBoundsException();
        }
        String str = this.a;
        if (!context.s(text, position, str, 0, str.length())) {
            return position ^ -1;
        }
        return this.a.length() + position;
    }

    public String toString() {
        String converted = this.a.replace("'", "''");
        return "'" + converted + "'";
    }
}

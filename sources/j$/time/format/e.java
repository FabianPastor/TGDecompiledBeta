package j$.time.format;

final class e implements g {
    private final char a;

    e(char c) {
        this.a = c;
    }

    public String toString() {
        if (this.a == '\'') {
            return "''";
        }
        return "'" + this.a + "'";
    }
}

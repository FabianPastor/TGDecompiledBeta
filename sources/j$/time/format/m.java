package j$.time.format;

final class m implements g {
    private final String a;

    m(String str) {
        this.a = str;
    }

    public String toString() {
        String replace = this.a.replace("'", "''");
        return "'" + replace + "'";
    }
}

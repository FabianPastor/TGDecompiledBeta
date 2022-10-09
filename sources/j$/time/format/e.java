package j$.time.format;
/* loaded from: classes2.dex */
final class e implements g {
    private final char a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public e(char c) {
        this.a = c;
    }

    public String toString() {
        if (this.a == '\'') {
            return "''";
        }
        return "'" + this.a + "'";
    }
}

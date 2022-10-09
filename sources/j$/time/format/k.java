package j$.time.format;
/* loaded from: classes2.dex */
final class k implements g {
    static final String[] c = {"+HH", "+HHmm", "+HH:mm", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS"};
    static final k d = new k("+HH:MM:ss", "Z");
    private final String a;
    private final int b;

    static {
        new k("+HH:MM:ss", "0");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public k(String str, String str2) {
        if (str != null) {
            if (str2 == null) {
                throw new NullPointerException("noOffsetText");
            }
            int i = 0;
            while (true) {
                String[] strArr = c;
                if (i >= strArr.length) {
                    throw new IllegalArgumentException("Invalid zone offset pattern: " + str);
                } else if (strArr[i].equals(str)) {
                    this.b = i;
                    this.a = str2;
                    return;
                } else {
                    i++;
                }
            }
        } else {
            throw new NullPointerException("pattern");
        }
    }

    public String toString() {
        String replace = this.a.replace("'", "''");
        return "Offset(" + c[this.b] + ",'" + replace + "')";
    }
}

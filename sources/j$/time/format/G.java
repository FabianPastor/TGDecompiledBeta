package j$.time.format;

import j$.util.concurrent.ConcurrentHashMap;

public final class G {
    public static final G e = new G('0', '+', '-', '.');
    private final char a;
    private final char b;
    private final char c;
    private final char d;

    static {
        new ConcurrentHashMap(16, 0.75f, 2);
    }

    private G(char zeroChar, char positiveSignChar, char negativeSignChar, char decimalPointChar) {
        this.a = zeroChar;
        this.b = positiveSignChar;
        this.c = negativeSignChar;
        this.d = decimalPointChar;
    }

    public char f() {
        return this.a;
    }

    public char e() {
        return this.b;
    }

    public char d() {
        return this.c;
    }

    public char c() {
        return this.d;
    }

    /* access modifiers changed from: package-private */
    public int b(char ch) {
        int val = ch - this.a;
        if (val < 0 || val > 9) {
            return -1;
        }
        return val;
    }

    /* access modifiers changed from: package-private */
    public String a(String numericText) {
        char c2 = this.a;
        if (c2 == '0') {
            return numericText;
        }
        int diff = c2 - '0';
        char[] array = numericText.toCharArray();
        for (int i = 0; i < array.length; i++) {
            array[i] = (char) (array[i] + diff);
        }
        return new String(array);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof G)) {
            return false;
        }
        G other = (G) obj;
        if (this.a == other.a && this.b == other.b && this.c == other.c && this.d == other.d) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.a + this.b + this.c + this.d;
    }

    public String toString() {
        return "DecimalStyle[" + this.a + this.b + this.c + this.d + "]";
    }
}

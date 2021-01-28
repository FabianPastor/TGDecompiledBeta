package j$.util;

import java.util.NoSuchElementException;

public final class q {
    private static final q c = new q();
    private final boolean a;
    private final int b;

    private q() {
        this.a = false;
        this.b = 0;
    }

    private q(int i) {
        this.a = true;
        this.b = i;
    }

    public static q a() {
        return c;
    }

    public static q d(int i) {
        return new q(i);
    }

    public int b() {
        if (this.a) {
            return this.b;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean c() {
        return this.a;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof q)) {
            return false;
        }
        q qVar = (q) obj;
        boolean z = this.a;
        if (!z || !qVar.a) {
            if (z == qVar.a) {
                return true;
            }
        } else if (this.b == qVar.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.a) {
            return this.b;
        }
        return 0;
    }

    public String toString() {
        if (!this.a) {
            return "OptionalInt.empty";
        }
        return String.format("OptionalInt[%s]", new Object[]{Integer.valueOf(this.b)});
    }
}

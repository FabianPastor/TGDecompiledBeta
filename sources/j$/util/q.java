package j$.util;

import java.util.NoSuchElementException;

public final class q {
    private static final q c = new q();

    /* renamed from: a  reason: collision with root package name */
    private final boolean var_a;
    private final int b;

    private q() {
        this.var_a = false;
        this.b = 0;
    }

    private q(int i) {
        this.var_a = true;
        this.b = i;
    }

    public static q a() {
        return c;
    }

    public static q d(int i) {
        return new q(i);
    }

    public int b() {
        if (this.var_a) {
            return this.b;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean c() {
        return this.var_a;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof q)) {
            return false;
        }
        q qVar = (q) obj;
        boolean z = this.var_a;
        if (!z || !qVar.var_a) {
            if (z == qVar.var_a) {
                return true;
            }
        } else if (this.b == qVar.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.var_a) {
            return this.b;
        }
        return 0;
    }

    public String toString() {
        if (!this.var_a) {
            return "OptionalInt.empty";
        }
        return String.format("OptionalInt[%s]", new Object[]{Integer.valueOf(this.b)});
    }
}

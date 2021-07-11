package j$.util;

import java.util.NoSuchElementException;

public final class p {
    private static final p a = new p();
    private final boolean b;
    private final int c;

    private p() {
        this.b = false;
        this.c = 0;
    }

    private p(int i) {
        this.b = true;
        this.c = i;
    }

    public static p a() {
        return a;
    }

    public static p d(int i) {
        return new p(i);
    }

    public int b() {
        if (this.b) {
            return this.c;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean c() {
        return this.b;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof p)) {
            return false;
        }
        p pVar = (p) obj;
        boolean z = this.b;
        if (!z || !pVar.b) {
            if (z == pVar.b) {
                return true;
            }
        } else if (this.c == pVar.c) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.b) {
            return this.c;
        }
        return 0;
    }

    public String toString() {
        if (!this.b) {
            return "OptionalInt.empty";
        }
        return String.format("OptionalInt[%s]", new Object[]{Integer.valueOf(this.c)});
    }
}

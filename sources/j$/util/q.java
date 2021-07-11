package j$.util;

import java.util.NoSuchElementException;

public final class q {
    private static final q a = new q();
    private final boolean b;
    private final long c;

    private q() {
        this.b = false;
        this.c = 0;
    }

    private q(long j) {
        this.b = true;
        this.c = j;
    }

    public static q a() {
        return a;
    }

    public static q d(long j) {
        return new q(j);
    }

    public long b() {
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
        if (!(obj instanceof q)) {
            return false;
        }
        q qVar = (q) obj;
        boolean z = this.b;
        if (!z || !qVar.b) {
            if (z == qVar.b) {
                return true;
            }
        } else if (this.c == qVar.c) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (!this.b) {
            return 0;
        }
        long j = this.c;
        return (int) (j ^ (j >>> 32));
    }

    public String toString() {
        if (!this.b) {
            return "OptionalLong.empty";
        }
        return String.format("OptionalLong[%s]", new Object[]{Long.valueOf(this.c)});
    }
}

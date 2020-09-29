package j$.time;

import java.io.Serializable;

final class d extends e implements Serializable {
    private final o a;

    d(o zone) {
        this.a = zone;
    }

    public o a() {
        return this.a;
    }

    public long c() {
        return System.currentTimeMillis();
    }

    public i b() {
        return i.Q(c());
    }

    public boolean equals(Object obj) {
        if (obj instanceof d) {
            return this.a.equals(((d) obj).a);
        }
        return false;
    }

    public int hashCode() {
        return this.a.hashCode() + 1;
    }

    public String toString() {
        return "SystemClock[" + this.a + "]";
    }
}

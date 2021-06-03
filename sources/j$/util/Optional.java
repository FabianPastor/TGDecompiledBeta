package j$.util;

import j$.time.a;
import java.util.NoSuchElementException;

public final class Optional<T> {
    private static final Optional a = new Optional();
    private final Object b;

    private Optional() {
        this.b = null;
    }

    private Optional(Object obj) {
        obj.getClass();
        this.b = obj;
    }

    public static Optional empty() {
        return a;
    }

    public static Optional of(Object obj) {
        return new Optional(obj);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Optional)) {
            return false;
        }
        return a.u(this.b, ((Optional) obj).b);
    }

    public Object get() {
        Object obj = this.b;
        if (obj != null) {
            return obj;
        }
        throw new NoSuchElementException("No value present");
    }

    public int hashCode() {
        Object obj = this.b;
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    public boolean isPresent() {
        return this.b != null;
    }

    public String toString() {
        Object obj = this.b;
        if (obj == null) {
            return "Optional.empty";
        }
        return String.format("Optional[%s]", new Object[]{obj});
    }
}

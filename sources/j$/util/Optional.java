package j$.util;

import java.util.NoSuchElementException;

public final class Optional<T> {
    private static final Optional b = new Optional();
    private final Object a;

    private Optional() {
        this.a = null;
    }

    private Optional(Object obj) {
        obj.getClass();
        this.a = obj;
    }

    public static Optional empty() {
        return b;
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
        return k.r(this.a, ((Optional) obj).a);
    }

    public Object get() {
        Object obj = this.a;
        if (obj != null) {
            return obj;
        }
        throw new NoSuchElementException("No value present");
    }

    public int hashCode() {
        Object obj = this.a;
        if (obj != null) {
            return obj.hashCode();
        }
        return 0;
    }

    public boolean isPresent() {
        return this.a != null;
    }

    public String toString() {
        Object obj = this.a;
        if (obj == null) {
            return "Optional.empty";
        }
        return String.format("Optional[%s]", new Object[]{obj});
    }
}

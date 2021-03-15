package j$.util;

import java.util.NoSuchElementException;

public final class Optional<T> {
    private static final Optional b = new Optional();

    /* renamed from: a  reason: collision with root package name */
    private final Object var_a;

    private Optional() {
        this.var_a = null;
    }

    private Optional(Object obj) {
        obj.getClass();
        this.var_a = obj;
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
        return k.r(this.var_a, ((Optional) obj).var_a);
    }

    public Object get() {
        Object obj = this.var_a;
        if (obj != null) {
            return obj;
        }
        throw new NoSuchElementException("No value present");
    }

    public int hashCode() {
        Object obj = this.var_a;
        if (obj != null) {
            return obj.hashCode();
        }
        return 0;
    }

    public boolean isPresent() {
        return this.var_a != null;
    }

    public String toString() {
        Object obj = this.var_a;
        if (obj == null) {
            return "Optional.empty";
        }
        return String.format("Optional[%s]", new Object[]{obj});
    }
}

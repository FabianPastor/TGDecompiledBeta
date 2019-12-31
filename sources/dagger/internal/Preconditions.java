package dagger.internal;

public final class Preconditions {
    public static <T> T checkNotNull(T t) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException();
    }

    public static <T> T checkNotNull(T t, String str) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(str);
    }

    public static <T> void checkBuilderRequirement(T t, Class<T> cls) {
        if (t == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(cls.getCanonicalName());
            stringBuilder.append(" must be set");
            throw new IllegalStateException(stringBuilder.toString());
        }
    }
}

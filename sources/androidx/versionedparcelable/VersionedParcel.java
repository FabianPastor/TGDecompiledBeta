package androidx.versionedparcelable;

import android.os.Parcelable;
import java.lang.reflect.InvocationTargetException;

public abstract class VersionedParcel {
    protected abstract void closeField();

    protected abstract VersionedParcel createSubParcel();

    protected abstract byte[] readByteArray();

    protected abstract boolean readField(int i);

    protected abstract int readInt();

    protected abstract <T extends Parcelable> T readParcelable();

    protected abstract String readString();

    protected abstract void setOutputField(int i);

    protected abstract void writeByteArray(byte[] bArr);

    protected abstract void writeInt(int i);

    protected abstract void writeParcelable(Parcelable parcelable);

    protected abstract void writeString(String str);

    public boolean isStream() {
        return false;
    }

    public void setSerializationFlags(boolean allowSerialization, boolean ignoreParcelables) {
    }

    public void writeByteArray(byte[] b, int fieldId) {
        setOutputField(fieldId);
        writeByteArray(b);
    }

    public void writeInt(int val, int fieldId) {
        setOutputField(fieldId);
        writeInt(val);
    }

    public void writeString(String val, int fieldId) {
        setOutputField(fieldId);
        writeString(val);
    }

    public void writeParcelable(Parcelable p, int fieldId) {
        setOutputField(fieldId);
        writeParcelable(p);
    }

    public int readInt(int def, int fieldId) {
        return !readField(fieldId) ? def : readInt();
    }

    public String readString(String def, int fieldId) {
        return !readField(fieldId) ? def : readString();
    }

    public byte[] readByteArray(byte[] def, int fieldId) {
        return !readField(fieldId) ? def : readByteArray();
    }

    public <T extends Parcelable> T readParcelable(T def, int fieldId) {
        return !readField(fieldId) ? def : readParcelable();
    }

    protected void writeVersionedParcelable(VersionedParcelable p) {
        if (p == null) {
            writeString(null);
            return;
        }
        writeVersionedParcelableCreator(p);
        VersionedParcel subParcel = createSubParcel();
        writeToParcel(p, subParcel);
        subParcel.closeField();
    }

    private void writeVersionedParcelableCreator(VersionedParcelable p) {
        try {
            writeString(findParcelClass(p.getClass()).getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(p.getClass().getSimpleName() + " does not have a Parcelizer", e);
        }
    }

    protected <T extends VersionedParcelable> T readVersionedParcelable() {
        String name = readString();
        if (name == null) {
            return null;
        }
        return readFromParcel(name, createSubParcel());
    }

    protected static <T extends VersionedParcelable> T readFromParcel(String parcelCls, VersionedParcel versionedParcel) {
        try {
            return (VersionedParcelable) Class.forName(parcelCls, true, VersionedParcel.class.getClassLoader()).getDeclaredMethod("read", new Class[]{VersionedParcel.class}).invoke(null, new Object[]{versionedParcel});
        } catch (IllegalAccessException e) {
            throw new RuntimeException("VersionedParcel encountered IllegalAccessException", e);
        } catch (InvocationTargetException e2) {
            if (e2.getCause() instanceof RuntimeException) {
                throw ((RuntimeException) e2.getCause());
            }
            throw new RuntimeException("VersionedParcel encountered InvocationTargetException", e2);
        } catch (NoSuchMethodException e3) {
            throw new RuntimeException("VersionedParcel encountered NoSuchMethodException", e3);
        } catch (ClassNotFoundException e4) {
            throw new RuntimeException("VersionedParcel encountered ClassNotFoundException", e4);
        }
    }

    protected static <T extends VersionedParcelable> void writeToParcel(T val, VersionedParcel versionedParcel) {
        try {
            findParcelClass((VersionedParcelable) val).getDeclaredMethod("write", new Class[]{val.getClass(), VersionedParcel.class}).invoke(null, new Object[]{val, versionedParcel});
        } catch (IllegalAccessException e) {
            throw new RuntimeException("VersionedParcel encountered IllegalAccessException", e);
        } catch (InvocationTargetException e2) {
            if (e2.getCause() instanceof RuntimeException) {
                throw ((RuntimeException) e2.getCause());
            }
            throw new RuntimeException("VersionedParcel encountered InvocationTargetException", e2);
        } catch (NoSuchMethodException e3) {
            throw new RuntimeException("VersionedParcel encountered NoSuchMethodException", e3);
        } catch (ClassNotFoundException e4) {
            throw new RuntimeException("VersionedParcel encountered ClassNotFoundException", e4);
        }
    }

    private static <T extends VersionedParcelable> Class findParcelClass(T val) throws ClassNotFoundException {
        return findParcelClass(val.getClass());
    }

    private static Class findParcelClass(Class<? extends VersionedParcelable> cls) throws ClassNotFoundException {
        String pkg = cls.getPackage().getName();
        return Class.forName(String.format("%s.%sParcelizer", new Object[]{pkg, cls.getSimpleName()}), false, cls.getClassLoader());
    }
}

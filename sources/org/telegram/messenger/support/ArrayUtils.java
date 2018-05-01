package org.telegram.messenger.support;

import java.lang.reflect.Array;
import org.telegram.tgnet.ConnectionsManager;

public class ArrayUtils {
    private static final int CACHE_SIZE = 73;
    private static Object[] EMPTY = new Object[0];
    private static Object[] sCache = new Object[73];

    public static int idealByteArraySize(int i) {
        for (int i2 = 4; i2 < 32; i2++) {
            int i3 = (1 << i2) - 12;
            if (i <= i3) {
                return i3;
            }
        }
        return i;
    }

    private ArrayUtils() {
    }

    public static int idealBooleanArraySize(int i) {
        return idealByteArraySize(i);
    }

    public static int idealShortArraySize(int i) {
        return idealByteArraySize(i * 2) / 2;
    }

    public static int idealCharArraySize(int i) {
        return idealByteArraySize(i * 2) / 2;
    }

    public static int idealIntArraySize(int i) {
        return idealByteArraySize(i * 4) / 4;
    }

    public static int idealFloatArraySize(int i) {
        return idealByteArraySize(i * 4) / 4;
    }

    public static int idealObjectArraySize(int i) {
        return idealByteArraySize(i * 4) / 4;
    }

    public static int idealLongArraySize(int i) {
        return idealByteArraySize(i * 8) / 8;
    }

    public static boolean equals(byte[] bArr, byte[] bArr2, int i) {
        if (bArr == bArr2) {
            return true;
        }
        if (!(bArr == null || bArr2 == null || bArr.length < i)) {
            if (bArr2.length >= i) {
                for (int i2 = 0; i2 < i; i2++) {
                    if (bArr[i2] != bArr2[i2]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static <T> T[] emptyArray(Class<T> cls) {
        if (cls == Object.class) {
            return (Object[]) EMPTY;
        }
        int identityHashCode = ((System.identityHashCode(cls) / 8) & ConnectionsManager.DEFAULT_DATACENTER_ID) % 73;
        Object obj = sCache[identityHashCode];
        if (obj == null || obj.getClass().getComponentType() != cls) {
            obj = Array.newInstance(cls, 0);
            sCache[identityHashCode] = obj;
        }
        return (Object[]) obj;
    }

    public static <T> boolean contains(T[] tArr, T t) {
        for (Object obj : tArr) {
            if (obj == null) {
                if (t == null) {
                    return true;
                }
            } else if (t != null && obj.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return 1;
            }
        }
        return false;
    }

    public static long total(long[] jArr) {
        long j = 0;
        int i = 0;
        while (i < jArr.length) {
            i++;
            j += jArr[i];
        }
        return j;
    }

    public static <T> T[] appendElement(Class<T> cls, T[] tArr, T t) {
        int i = 0;
        if (tArr != null) {
            int length = tArr.length;
            cls = (Object[]) Array.newInstance(cls, length + 1);
            System.arraycopy(tArr, 0, cls, 0, length);
            i = length;
        } else {
            cls = (Object[]) Array.newInstance(cls, 1);
        }
        cls[i] = t;
        return cls;
    }

    public static <T> T[] removeElement(Class<T> cls, T[] tArr, T t) {
        if (tArr != null) {
            int length = tArr.length;
            int i = 0;
            while (i < length) {
                if (tArr[i] != t) {
                    i++;
                } else if (length == 1) {
                    return null;
                } else {
                    Object[] objArr = (Object[]) Array.newInstance(cls, length - 1);
                    System.arraycopy(tArr, 0, objArr, 0, i);
                    System.arraycopy(tArr, i + 1, objArr, i, (length - i) - 1);
                    return objArr;
                }
            }
        }
        return tArr;
    }

    public static int[] appendInt(int[] iArr, int i) {
        if (iArr == null) {
            return new int[]{i};
        }
        for (int i2 : iArr) {
            if (i2 == i) {
                return iArr;
            }
        }
        Object obj = new int[(r1 + 1)];
        System.arraycopy(iArr, 0, obj, 0, r1);
        obj[r1] = i;
        return obj;
    }

    public static int[] removeInt(int[] iArr, int i) {
        if (iArr == null) {
            return null;
        }
        int length = iArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (iArr[i2] == i) {
                i = length - 1;
                Object obj = new int[i];
                if (i2 > 0) {
                    System.arraycopy(iArr, 0, obj, 0, i2);
                }
                if (i2 < i) {
                    System.arraycopy(iArr, i2 + 1, obj, i2, (length - i2) - 1);
                }
                return obj;
            }
        }
        return iArr;
    }
}

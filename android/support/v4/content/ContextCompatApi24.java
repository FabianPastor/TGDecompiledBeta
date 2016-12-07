package android.support.v4.content;

import android.content.Context;
import java.io.File;

class ContextCompatApi24 {
    ContextCompatApi24() {
    }

    public static File getDataDir(Context context) {
        return context.getDataDir();
    }

    public static Context createDeviceProtectedStorageContext(Context context) {
        return context.createDeviceProtectedStorageContext();
    }

    public static boolean isDeviceProtectedStorage(Context context) {
        return context.isDeviceProtectedStorage();
    }
}

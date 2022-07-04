package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import org.telegram.messenger.ApplicationLoader;

public class LauncherIconController {
    public static void tryFixLauncherIconIfNeeded() {
        LauncherIcon[] values = LauncherIcon.values();
        int length = values.length;
        int i = 0;
        while (i < length) {
            if (!isEnabled(values[i])) {
                i++;
            } else {
                return;
            }
        }
        setIcon(LauncherIcon.DEFAULT);
    }

    public static boolean isEnabled(LauncherIcon launcherIcon) {
        Context context = ApplicationLoader.applicationContext;
        int componentEnabledSetting = context.getPackageManager().getComponentEnabledSetting(launcherIcon.getComponentName(context));
        if (componentEnabledSetting == 1) {
            return true;
        }
        if (componentEnabledSetting == 0 && launcherIcon == LauncherIcon.DEFAULT) {
            return true;
        }
        return false;
    }

    public static void setIcon(LauncherIcon launcherIcon) {
        Context context = ApplicationLoader.applicationContext;
        PackageManager packageManager = context.getPackageManager();
        LauncherIcon[] values = LauncherIcon.values();
        int length = values.length;
        for (int i = 0; i < length; i++) {
            LauncherIcon launcherIcon2 = values[i];
            packageManager.setComponentEnabledSetting(launcherIcon2.getComponentName(context), launcherIcon2 == launcherIcon ? 1 : 2, 1);
        }
    }

    public enum LauncherIcon {
        DEFAULT("DefaultIcon", NUM, NUM, NUM),
        VINTAGE("VintageIcon", NUM, NUM, NUM),
        AQUA("AquaIcon", NUM, NUM, NUM),
        PREMIUM("PremiumIcon", NUM, NUM, NUM, true),
        TURBO("TurboIcon", NUM, NUM, NUM, true),
        NOX("NoxIcon", NUM, NUM, NUM, true);
        
        public final int background;
        private ComponentName componentName;
        public final int foreground;
        public final String key;
        public final boolean premium;
        public final int title;

        public ComponentName getComponentName(Context context) {
            if (this.componentName == null) {
                String packageName = context.getPackageName();
                this.componentName = new ComponentName(packageName, "org.telegram.messenger." + this.key);
            }
            return this.componentName;
        }

        private LauncherIcon(String str, int i, int i2, int i3) {
            this(r9, r10, str, i, i2, i3, false);
        }

        private LauncherIcon(String str, int i, int i2, int i3, boolean z) {
            this.key = str;
            this.background = i;
            this.foreground = i2;
            this.title = i3;
            this.premium = z;
        }
    }
}

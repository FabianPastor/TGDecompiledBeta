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

    public static boolean isEnabled(LauncherIcon icon) {
        Context ctx = ApplicationLoader.applicationContext;
        int i = ctx.getPackageManager().getComponentEnabledSetting(icon.getComponentName(ctx));
        if (i != 1) {
            return i == 0 && icon == LauncherIcon.DEFAULT;
        }
        return true;
    }

    public static void setIcon(LauncherIcon icon) {
        int i;
        Context ctx = ApplicationLoader.applicationContext;
        PackageManager pm = ctx.getPackageManager();
        for (LauncherIcon i2 : LauncherIcon.values()) {
            ComponentName componentName = i2.getComponentName(ctx);
            if (i2 == icon) {
                i = 1;
            } else {
                i = 2;
            }
            pm.setComponentEnabledSetting(componentName, i, 1);
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

        public ComponentName getComponentName(Context ctx) {
            if (this.componentName == null) {
                String packageName = ctx.getPackageName();
                this.componentName = new ComponentName(packageName, "org.telegram.messenger." + this.key);
            }
            return this.componentName;
        }

        private LauncherIcon(String key2, int background2, int foreground2, int title2) {
            this(r9, r10, key2, background2, foreground2, title2, false);
        }

        private LauncherIcon(String key2, int background2, int foreground2, int title2, boolean premium2) {
            this.key = key2;
            this.background = background2;
            this.foreground = foreground2;
            this.title = title2;
            this.premium = premium2;
        }
    }
}

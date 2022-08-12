package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;

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
        DEFAULT("DefaultIcon", r4, r15, R.string.AppIconDefault),
        VINTAGE("VintageIcon", R.drawable.icon_6_background_sa, R.mipmap.icon_6_foreground_sa, R.string.AppIconVintage),
        AQUA("AquaIcon", R.drawable.icon_4_background_sa, r13, R.string.AppIconAqua),
        PREMIUM("PremiumIcon", R.drawable.icon_3_background_sa, R.mipmap.icon_3_foreground_sa, R.string.AppIconPremium, true),
        TURBO("TurboIcon", R.drawable.icon_5_background_sa, R.mipmap.icon_5_foreground_sa, R.string.AppIconTurbo, true),
        NOX("NoxIcon", R.drawable.icon_2_background_sa, r13, R.string.AppIconNox, true);
        
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

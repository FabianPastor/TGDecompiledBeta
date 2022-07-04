package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.ArrayList;
import org.telegram.ui.ActionBar.ThemeDescription;

public class SimpleThemeDescription {
    private SimpleThemeDescription() {
    }

    public static ThemeDescription createThemeDescription(ThemeDescription.ThemeDescriptionDelegate del, String key) {
        return new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, del, key);
    }

    public static ArrayList<ThemeDescription> createThemeDescriptions(ThemeDescription.ThemeDescriptionDelegate del, String... keys) {
        ArrayList<ThemeDescription> l = new ArrayList<>(keys.length);
        for (String k : keys) {
            l.add(createThemeDescription(del, k));
        }
        return l;
    }
}

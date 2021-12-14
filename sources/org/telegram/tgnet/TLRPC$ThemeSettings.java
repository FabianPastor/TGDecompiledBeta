package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$ThemeSettings extends TLObject {
    public int accent_color;
    public TLRPC$BaseTheme base_theme;
    public int flags;
    public ArrayList<Integer> message_colors = new ArrayList<>();
    public boolean message_colors_animated;
    public int outbox_accent_color;
    public TLRPC$WallPaper wallpaper;

    public static TLRPC$ThemeSettings TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ThemeSettings tLRPC$ThemeSettings;
        if (i == -NUM) {
            tLRPC$ThemeSettings = new TLRPC$TL_themeSettings_layer132();
        } else if (i != -NUM) {
            tLRPC$ThemeSettings = i != -94849324 ? null : new TLRPC$TL_themeSettings();
        } else {
            tLRPC$ThemeSettings = new TLRPC$TL_themeSettings_layer131();
        }
        if (tLRPC$ThemeSettings != null || !z) {
            if (tLRPC$ThemeSettings != null) {
                tLRPC$ThemeSettings.readParams(abstractSerializedData, z);
            }
            return tLRPC$ThemeSettings;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ThemeSettings", new Object[]{Integer.valueOf(i)}));
    }
}

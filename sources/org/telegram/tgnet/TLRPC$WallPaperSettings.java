package org.telegram.tgnet;

public abstract class TLRPC$WallPaperSettings extends TLObject {
    public int background_color;
    public boolean blur;
    public int flags;
    public int fourth_background_color;
    public int intensity;
    public boolean motion;
    public int rotation;
    public int second_background_color;
    public int third_background_color;

    public static TLRPC$WallPaperSettings TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
        if (i == -NUM) {
            tLRPC$WallPaperSettings = new TLRPC$TL_wallPaperSettings_layer106();
        } else if (i != 84438264) {
            tLRPC$WallPaperSettings = i != NUM ? null : new TLRPC$TL_wallPaperSettings();
        } else {
            tLRPC$WallPaperSettings = new TLRPC$TL_wallPaperSettings_layer128();
        }
        if (tLRPC$WallPaperSettings != null || !z) {
            if (tLRPC$WallPaperSettings != null) {
                tLRPC$WallPaperSettings.readParams(abstractSerializedData, z);
            }
            return tLRPC$WallPaperSettings;
        }
        throw new RuntimeException(String.format("can't parse magic %x in WallPaperSettings", new Object[]{Integer.valueOf(i)}));
    }
}

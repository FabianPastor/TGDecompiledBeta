package org.telegram.tgnet;

public abstract class TLRPC$WallPaperSettings extends TLObject {
    public int background_color;
    public boolean blur;
    public int flags;
    public int intensity;
    public boolean motion;
    public int rotation;
    public int second_background_color;

    public static TLRPC$WallPaperSettings TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_wallPaperSettings tLRPC$TL_wallPaperSettings;
        if (i != -NUM) {
            tLRPC$TL_wallPaperSettings = i != 84438264 ? null : new TLRPC$TL_wallPaperSettings();
        } else {
            tLRPC$TL_wallPaperSettings = new TLRPC$TL_wallPaperSettings_layer106();
        }
        if (tLRPC$TL_wallPaperSettings != null || !z) {
            if (tLRPC$TL_wallPaperSettings != null) {
                tLRPC$TL_wallPaperSettings.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_wallPaperSettings;
        }
        throw new RuntimeException(String.format("can't parse magic %x in WallPaperSettings", new Object[]{Integer.valueOf(i)}));
    }
}

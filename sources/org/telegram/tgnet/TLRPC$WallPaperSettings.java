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
        TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
        if (i != -NUM) {
            tLRPC$WallPaperSettings = i != 84438264 ? null : new TLRPC$TL_wallPaperSettings();
        } else {
            tLRPC$WallPaperSettings = new TLRPC$TL_wallPaperSettings_layer106();
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

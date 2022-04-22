package org.telegram.tgnet;

public class TLRPC$TL_themeSettings_layer131 extends TLRPC$ThemeSettings {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32;
        int readInt322;
        this.flags = abstractSerializedData.readInt32(z);
        this.base_theme = TLRPC$BaseTheme.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.accent_color = abstractSerializedData.readInt32(z);
        if (!((this.flags & 1) == 0 || (readInt322 = abstractSerializedData.readInt32(z)) == 0)) {
            this.message_colors.add(Integer.valueOf(readInt322));
        }
        if (!((this.flags & 1) == 0 || (readInt32 = abstractSerializedData.readInt32(z)) == 0)) {
            this.message_colors.add(0, Integer.valueOf(readInt32));
        }
        if ((this.flags & 2) != 0) {
            this.wallpaper = TLRPC$WallPaper.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.base_theme.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.accent_color);
        int i = 0;
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.message_colors.size() > 1 ? this.message_colors.get(1).intValue() : 0);
        }
        if ((this.flags & 1) != 0) {
            if (this.message_colors.size() > 0) {
                i = this.message_colors.get(0).intValue();
            }
            abstractSerializedData.writeInt32(i);
        }
        if ((this.flags & 2) != 0) {
            this.wallpaper.serializeToStream(abstractSerializedData);
        }
    }
}

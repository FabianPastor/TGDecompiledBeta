package org.telegram.tgnet;

public class TLRPC$TL_inputThemeSettings extends TLObject {
    public static int constructor = -NUM;
    public int accent_color;
    public TLRPC$BaseTheme base_theme;
    public int flags;
    public int message_bottom_color;
    public int message_top_color;
    public TLRPC$InputWallPaper wallpaper;
    public TLRPC$WallPaperSettings wallpaper_settings;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.base_theme = TLRPC$BaseTheme.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.accent_color = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.message_top_color = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 1) != 0) {
            this.message_bottom_color = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 2) != 0) {
            this.wallpaper = TLRPC$InputWallPaper.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 2) != 0) {
            this.wallpaper_settings = TLRPC$WallPaperSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.base_theme.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.accent_color);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.message_top_color);
        }
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32(this.message_bottom_color);
        }
        if ((this.flags & 2) != 0) {
            this.wallpaper.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            this.wallpaper_settings.serializeToStream(abstractSerializedData);
        }
    }
}

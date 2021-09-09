package org.telegram.tgnet;

public class TLRPC$TL_chatTheme extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$Theme dark_theme;
    public String emoticon;
    public TLRPC$Theme theme;

    public static TLRPC$TL_chatTheme TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_chatTheme tLRPC$TL_chatTheme = new TLRPC$TL_chatTheme();
            tLRPC$TL_chatTheme.readParams(abstractSerializedData, z);
            return tLRPC$TL_chatTheme;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_chatTheme", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.emoticon = abstractSerializedData.readString(z);
        this.theme = TLRPC$Theme.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.dark_theme = TLRPC$Theme.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.emoticon);
        this.theme.serializeToStream(abstractSerializedData);
        this.dark_theme.serializeToStream(abstractSerializedData);
    }
}

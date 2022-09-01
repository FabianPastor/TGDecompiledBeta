package org.telegram.tgnet;

public class TLRPC$TL_messageExtendedMedia extends TLRPC$MessageExtendedMedia {
    public static int constructor = -NUM;
    public TLRPC$MessageMedia media;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.media = TLRPC$MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.media.serializeToStream(abstractSerializedData);
    }
}

package org.telegram.tgnet;

public class TLRPC$TL_inputMediaGame extends TLRPC$InputMedia {
    public static int constructor = -NUM;
    public TLRPC$InputGame id;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = TLRPC$InputGame.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.id.serializeToStream(abstractSerializedData);
    }
}

package org.telegram.tgnet;

public class TLRPC$TL_messageMediaGame extends TLRPC$MessageMedia {
    public static int constructor = -38694904;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.game = TLRPC$TL_game.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.game.serializeToStream(abstractSerializedData);
    }
}

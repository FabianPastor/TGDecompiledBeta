package org.telegram.tgnet;

public class TLRPC$TL_messageActionGameScore extends TLRPC$MessageAction {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.game_id = abstractSerializedData.readInt64(z);
        this.score = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.game_id);
        abstractSerializedData.writeInt32(this.score);
    }
}

package org.telegram.tgnet;

public class TLRPC$TL_sendMessageHistoryImportAction extends TLRPC$SendMessageAction {
    public static int constructor = -NUM;
    public int progress;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.progress = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.progress);
    }
}

package org.telegram.tgnet;

public class TLRPC$TL_messages_checkHistoryImport extends TLObject {
    public static int constructor = NUM;
    public String import_head;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_messages_historyImportParsed.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.import_head);
    }
}

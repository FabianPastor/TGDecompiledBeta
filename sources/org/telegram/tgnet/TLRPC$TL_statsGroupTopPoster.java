package org.telegram.tgnet;

public class TLRPC$TL_statsGroupTopPoster extends TLObject {
    public static int constructor = -NUM;
    public int avg_chars;
    public int messages;
    public long user_id;

    public static TLRPC$TL_statsGroupTopPoster TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_statsGroupTopPoster tLRPC$TL_statsGroupTopPoster = new TLRPC$TL_statsGroupTopPoster();
            tLRPC$TL_statsGroupTopPoster.readParams(abstractSerializedData, z);
            return tLRPC$TL_statsGroupTopPoster;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_statsGroupTopPoster", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        this.messages = abstractSerializedData.readInt32(z);
        this.avg_chars = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        abstractSerializedData.writeInt32(this.messages);
        abstractSerializedData.writeInt32(this.avg_chars);
    }
}

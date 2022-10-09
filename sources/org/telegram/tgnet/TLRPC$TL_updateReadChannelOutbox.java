package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updateReadChannelOutbox extends TLRPC$Update {
    public static int constructor = -NUM;
    public long channel_id;
    public int max_id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.channel_id = abstractSerializedData.readInt64(z);
        this.max_id = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.channel_id);
        abstractSerializedData.writeInt32(this.max_id);
    }
}

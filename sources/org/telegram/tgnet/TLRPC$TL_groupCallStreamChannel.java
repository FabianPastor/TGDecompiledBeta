package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_groupCallStreamChannel extends TLObject {
    public static int constructor = -NUM;
    public int channel;
    public long last_timestamp_ms;
    public int scale;

    public static TLRPC$TL_groupCallStreamChannel TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_groupCallStreamChannel", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_groupCallStreamChannel tLRPC$TL_groupCallStreamChannel = new TLRPC$TL_groupCallStreamChannel();
        tLRPC$TL_groupCallStreamChannel.readParams(abstractSerializedData, z);
        return tLRPC$TL_groupCallStreamChannel;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.channel = abstractSerializedData.readInt32(z);
        this.scale = abstractSerializedData.readInt32(z);
        this.last_timestamp_ms = abstractSerializedData.readInt64(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.channel);
        abstractSerializedData.writeInt32(this.scale);
        abstractSerializedData.writeInt64(this.last_timestamp_ms);
    }
}

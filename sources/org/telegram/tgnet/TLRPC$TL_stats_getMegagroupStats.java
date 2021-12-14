package org.telegram.tgnet;

public class TLRPC$TL_stats_getMegagroupStats extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$InputChannel channel;
    public boolean dark;
    public int flags;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_stats_megagroupStats.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.dark ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        this.channel.serializeToStream(abstractSerializedData);
    }
}

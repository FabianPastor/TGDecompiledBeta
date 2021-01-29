package org.telegram.tgnet;

public class TLRPC$TL_stats_loadAsyncGraph extends TLObject {
    public static int constructor = NUM;
    public int flags;
    public String token;
    public long x;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$StatsGraph.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeString(this.token);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt64(this.x);
        }
    }
}

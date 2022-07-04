package org.telegram.tgnet;

public class TLRPC$TL_stats_messageStats extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$StatsGraph views_graph;

    public static TLRPC$TL_stats_messageStats TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_stats_messageStats tLRPC$TL_stats_messageStats = new TLRPC$TL_stats_messageStats();
            tLRPC$TL_stats_messageStats.readParams(abstractSerializedData, z);
            return tLRPC$TL_stats_messageStats;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_stats_messageStats", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.views_graph = TLRPC$StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.views_graph.serializeToStream(abstractSerializedData);
    }
}

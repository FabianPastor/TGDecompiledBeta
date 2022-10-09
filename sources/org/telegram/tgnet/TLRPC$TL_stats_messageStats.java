package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_stats_messageStats extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$StatsGraph views_graph;

    public static TLRPC$TL_stats_messageStats TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_stats_messageStats", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_stats_messageStats tLRPC$TL_stats_messageStats = new TLRPC$TL_stats_messageStats();
        tLRPC$TL_stats_messageStats.readParams(abstractSerializedData, z);
        return tLRPC$TL_stats_messageStats;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.views_graph = TLRPC$StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.views_graph.serializeToStream(abstractSerializedData);
    }
}

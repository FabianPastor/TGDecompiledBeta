package org.telegram.tgnet;

public abstract class TLRPC$StatsGraph extends TLObject {
    public static TLRPC$StatsGraph TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$StatsGraph tLRPC$StatsGraph;
        if (i != -NUM) {
            tLRPC$StatsGraph = i != -NUM ? i != NUM ? null : new TLRPC$TL_statsGraphAsync() : new TLRPC$TL_statsGraphError();
        } else {
            tLRPC$StatsGraph = new TLRPC$TL_statsGraph();
        }
        if (tLRPC$StatsGraph != null || !z) {
            if (tLRPC$StatsGraph != null) {
                tLRPC$StatsGraph.readParams(abstractSerializedData, z);
            }
            return tLRPC$StatsGraph;
        }
        throw new RuntimeException(String.format("can't parse magic %x in StatsGraph", new Object[]{Integer.valueOf(i)}));
    }
}

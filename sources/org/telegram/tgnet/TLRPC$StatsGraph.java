package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$StatsGraph extends TLObject {
    public static TLRPC$StatsGraph TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$StatsGraph tLRPC$TL_statsGraph;
        if (i == -NUM) {
            tLRPC$TL_statsGraph = new TLRPC$TL_statsGraph();
        } else if (i != -NUM) {
            tLRPC$TL_statsGraph = i != NUM ? null : new TLRPC$TL_statsGraphAsync();
        } else {
            tLRPC$TL_statsGraph = new TLRPC$TL_statsGraphError();
        }
        if (tLRPC$TL_statsGraph != null || !z) {
            if (tLRPC$TL_statsGraph != null) {
                tLRPC$TL_statsGraph.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_statsGraph;
        }
        throw new RuntimeException(String.format("can't parse magic %x in StatsGraph", Integer.valueOf(i)));
    }
}

package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$PollResults extends TLObject {
    public int flags;
    public boolean min;
    public ArrayList<Integer> recent_voters = new ArrayList<>();
    public ArrayList<TLRPC$TL_pollAnswerVoters> results = new ArrayList<>();
    public int total_voters;

    public static TLRPC$PollResults TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_pollResults tLRPC$TL_pollResults;
        if (i != -NUM) {
            tLRPC$TL_pollResults = i != NUM ? null : new TLRPC$TL_pollResults_layer108();
        } else {
            tLRPC$TL_pollResults = new TLRPC$TL_pollResults();
        }
        if (tLRPC$TL_pollResults != null || !z) {
            if (tLRPC$TL_pollResults != null) {
                tLRPC$TL_pollResults.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_pollResults;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PollResults", new Object[]{Integer.valueOf(i)}));
    }
}

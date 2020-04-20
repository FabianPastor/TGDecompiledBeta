package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$PollResults extends TLObject {
    public int flags;
    public boolean min;
    public ArrayList<Integer> recent_voters = new ArrayList<>();
    public ArrayList<TLRPC$TL_pollAnswerVoters> results = new ArrayList<>();
    public String solution;
    public ArrayList<TLRPC$MessageEntity> solution_entities = new ArrayList<>();
    public int total_voters;

    public static TLRPC$PollResults TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PollResults tLRPC$PollResults;
        if (i == -NUM) {
            tLRPC$PollResults = new TLRPC$TL_pollResults();
        } else if (i != -NUM) {
            tLRPC$PollResults = i != NUM ? null : new TLRPC$TL_pollResults_layer108();
        } else {
            tLRPC$PollResults = new TLRPC$TL_pollResults_layer111();
        }
        if (tLRPC$PollResults != null || !z) {
            if (tLRPC$PollResults != null) {
                tLRPC$PollResults.readParams(abstractSerializedData, z);
            }
            return tLRPC$PollResults;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PollResults", new Object[]{Integer.valueOf(i)}));
    }
}

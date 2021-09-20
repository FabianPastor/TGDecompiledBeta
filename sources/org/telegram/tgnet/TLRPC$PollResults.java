package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$PollResults extends TLObject {
    public int flags;
    public boolean min;
    public ArrayList<Long> recent_voters = new ArrayList<>();
    public ArrayList<TLRPC$TL_pollAnswerVoters> results = new ArrayList<>();
    public String solution;
    public ArrayList<TLRPC$MessageEntity> solution_entities = new ArrayList<>();
    public int total_voters;

    public static TLRPC$PollResults TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PollResults tLRPC$PollResults;
        switch (i) {
            case -1159937629:
                tLRPC$PollResults = new TLRPC$TL_pollResults_layer131();
                break;
            case -932174686:
                tLRPC$PollResults = new TLRPC$TL_pollResults_layer111();
                break;
            case -591909213:
                tLRPC$PollResults = new TLRPC$TL_pollResults();
                break;
            case 1465219162:
                tLRPC$PollResults = new TLRPC$TL_pollResults_layer108();
                break;
            default:
                tLRPC$PollResults = null;
                break;
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

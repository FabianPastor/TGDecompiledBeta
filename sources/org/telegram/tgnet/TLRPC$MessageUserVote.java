package org.telegram.tgnet;

public abstract class TLRPC$MessageUserVote extends TLObject {
    public int date;
    public int user_id;

    public static TLRPC$MessageUserVote TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageUserVote tLRPC$MessageUserVote;
        if (i != -NUM) {
            tLRPC$MessageUserVote = i != NUM ? i != NUM ? null : new TLRPC$TL_messageUserVoteInputOption() : new TLRPC$TL_messageUserVoteMultiple();
        } else {
            tLRPC$MessageUserVote = new TLRPC$TL_messageUserVote();
        }
        if (tLRPC$MessageUserVote != null || !z) {
            if (tLRPC$MessageUserVote != null) {
                tLRPC$MessageUserVote.readParams(abstractSerializedData, z);
            }
            return tLRPC$MessageUserVote;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageUserVote", new Object[]{Integer.valueOf(i)}));
    }
}

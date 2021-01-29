package org.telegram.tgnet;

public abstract class TLRPC$ChatParticipant extends TLObject {
    public int date;
    public int inviter_id;
    public int user_id;

    public static TLRPC$ChatParticipant TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        if (i == -NUM) {
            tLRPC$ChatParticipant = new TLRPC$TL_chatParticipant();
        } else if (i != -NUM) {
            tLRPC$ChatParticipant = i != -NUM ? null : new TLRPC$TL_chatParticipantAdmin();
        } else {
            tLRPC$ChatParticipant = new TLRPC$TL_chatParticipantCreator();
        }
        if (tLRPC$ChatParticipant != null || !z) {
            if (tLRPC$ChatParticipant != null) {
                tLRPC$ChatParticipant.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChatParticipant;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChatParticipant", new Object[]{Integer.valueOf(i)}));
    }
}

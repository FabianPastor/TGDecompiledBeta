package org.telegram.tgnet;

public abstract class TLRPC$ChatParticipant extends TLObject {
    public int date;
    public long inviter_id;
    public long user_id;

    public static TLRPC$ChatParticipant TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        switch (i) {
            case -1600962725:
                tLRPC$ChatParticipant = new TLRPC$TL_chatParticipantAdmin();
                break;
            case -1070776313:
                tLRPC$ChatParticipant = new TLRPC$TL_chatParticipant();
                break;
            case -925415106:
                tLRPC$ChatParticipant = new TLRPC$TL_chatParticipant_layer131();
                break;
            case -636267638:
                tLRPC$ChatParticipant = new TLRPC$TL_chatParticipantCreator_layer131();
                break;
            case -489233354:
                tLRPC$ChatParticipant = new TLRPC$TL_chatParticipantAdmin_layer131();
                break;
            case -462696732:
                tLRPC$ChatParticipant = new TLRPC$TL_chatParticipantCreator();
                break;
            default:
                tLRPC$ChatParticipant = null;
                break;
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

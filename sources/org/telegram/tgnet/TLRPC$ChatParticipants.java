package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$ChatParticipants extends TLObject {
    public long admin_id;
    public long chat_id;
    public int flags;
    public ArrayList<TLRPC$ChatParticipant> participants = new ArrayList<>();
    public TLRPC$ChatParticipant self_participant;
    public int version;

    public static TLRPC$ChatParticipants TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChatParticipants tLRPC$ChatParticipants;
        switch (i) {
            case -2023500831:
                tLRPC$ChatParticipants = new TLRPC$TL_chatParticipantsForbidden();
                break;
            case -57668565:
                tLRPC$ChatParticipants = new TLRPC$TL_chatParticipantsForbidden_layer131();
                break;
            case 265468810:
                tLRPC$ChatParticipants = new TLRPC$TL_chatParticipantsForbidden_old();
                break;
            case 1018991608:
                tLRPC$ChatParticipants = new TLRPC$TL_chatParticipants();
                break;
            case 1061556205:
                tLRPC$ChatParticipants = new TLRPC$TL_chatParticipants_layer131();
                break;
            case 2017571861:
                tLRPC$ChatParticipants = new TLRPC$TL_chatParticipants_old();
                break;
            default:
                tLRPC$ChatParticipants = null;
                break;
        }
        if (tLRPC$ChatParticipants != null || !z) {
            if (tLRPC$ChatParticipants != null) {
                tLRPC$ChatParticipants.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChatParticipants;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChatParticipants", new Object[]{Integer.valueOf(i)}));
    }
}

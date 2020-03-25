package org.telegram.tgnet;

public abstract class TLRPC$GroupCallParticipant extends TLObject {
    public int date;
    public int flags;
    public int inviter_id;
    public byte[] member_tag_hash;
    public TLRPC$TL_inputPhoneCall phone_call;
    public boolean readonly;
    public byte[] streams;
    public int user_id;

    public static TLRPC$GroupCallParticipant TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$GroupCallParticipant tLRPC$GroupCallParticipant;
        switch (i) {
            case 930387696:
                tLRPC$GroupCallParticipant = new TLRPC$TL_groupCallParticipantInvited();
                break;
            case 1100680690:
                tLRPC$GroupCallParticipant = new TLRPC$TL_groupCallParticipantLeft();
                break;
            case 1326135736:
                tLRPC$GroupCallParticipant = new TLRPC$TL_groupCallParticipantAdmin();
                break;
            case 1486730135:
                tLRPC$GroupCallParticipant = new TLRPC$TL_groupCallParticipant();
                break;
            default:
                tLRPC$GroupCallParticipant = null;
                break;
        }
        if (tLRPC$GroupCallParticipant != null || !z) {
            if (tLRPC$GroupCallParticipant != null) {
                tLRPC$GroupCallParticipant.readParams(abstractSerializedData, z);
            }
            return tLRPC$GroupCallParticipant;
        }
        throw new RuntimeException(String.format("can't parse magic %x in GroupCallParticipant", new Object[]{Integer.valueOf(i)}));
    }
}

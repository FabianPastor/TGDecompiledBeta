package org.telegram.tgnet;

public abstract class TLRPC$ChannelParticipant extends TLObject {
    public TLRPC$TL_chatAdminRights admin_rights;
    public TLRPC$TL_channelAdminRights_layer92 admin_rights_layer92;
    public TLRPC$TL_chatBannedRights banned_rights;
    public TLRPC$TL_channelBannedRights_layer92 banned_rights_layer92;
    public boolean can_edit;
    public int date;
    public int flags;
    public int inviter_id;
    public int kicked_by;
    public boolean left;
    public int promoted_by;
    public String rank;
    public boolean self;
    public int user_id;

    public static TLRPC$ChannelParticipant TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant;
        switch (i) {
            case -2138237532:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantCreator();
                break;
            case -1933187430:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantKicked_layer67();
                break;
            case -1861910545:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantModerator_layer67();
                break;
            case -1743180447:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantEditor_layer67();
                break;
            case -1557620115:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantSelf();
                break;
            case -1473271656:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin_layer92();
                break;
            case -859915345:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin();
                break;
            case -471670279:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantCreator_layer103();
                break;
            case 367766557:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipant();
                break;
            case 470789295:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantBanned();
                break;
            case 573315206:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantBanned_layer92();
                break;
            case 1571450403:
                tLRPC$ChannelParticipant = new TLRPC$TL_channelParticipantAdmin_layer103();
                break;
            default:
                tLRPC$ChannelParticipant = null;
                break;
        }
        if (tLRPC$ChannelParticipant != null || !z) {
            if (tLRPC$ChannelParticipant != null) {
                tLRPC$ChannelParticipant.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChannelParticipant;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChannelParticipant", new Object[]{Integer.valueOf(i)}));
    }
}

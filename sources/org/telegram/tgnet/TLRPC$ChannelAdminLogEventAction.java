package org.telegram.tgnet;

public abstract class TLRPC$ChannelAdminLogEventAction extends TLObject {
    public TLRPC$Message message;
    public TLRPC$Message new_message;
    public TLRPC$ChannelParticipant new_participant;
    public TLRPC$Photo new_photo;
    public TLRPC$InputStickerSet new_stickerset;
    public TLRPC$ChannelParticipant participant;
    public TLRPC$Message prev_message;
    public TLRPC$ChannelParticipant prev_participant;
    public TLRPC$Photo prev_photo;
    public TLRPC$InputStickerSet prev_stickerset;
    public String prev_value;

    public static TLRPC$ChannelAdminLogEventAction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ChannelAdminLogEventAction tLRPC$ChannelAdminLogEventAction;
        switch (i) {
            case -1895328189:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionStopPoll();
                break;
            case -1569748965:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionChangeLinkedChat();
                break;
            case -1312568665:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionChangeStickerSet();
                break;
            case -714643696:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin();
                break;
            case -484690728:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionParticipantInvite();
                break;
            case -422036098:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionParticipantToggleBan();
                break;
            case -421545947:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionChangeTitle();
                break;
            case -370660328:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionUpdatePinned();
                break;
            case -124291086:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionParticipantLeave();
                break;
            case 241923758:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionChangeLocation();
                break;
            case 405815507:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionParticipantJoin();
                break;
            case 460916654:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionToggleInvites();
                break;
            case 648939889:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionToggleSignatures();
                break;
            case 771095562:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionDefaultBannedRights();
                break;
            case 1121994683:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionDeleteMessage();
                break;
            case 1129042607:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionChangePhoto();
                break;
            case 1401984889:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionToggleSlowMode();
                break;
            case 1427671598:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionChangeAbout();
                break;
            case 1599903217:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden();
                break;
            case 1783299128:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionChangeUsername();
                break;
            case 1889215493:
                tLRPC$ChannelAdminLogEventAction = new TLRPC$TL_channelAdminLogEventActionEditMessage();
                break;
            default:
                tLRPC$ChannelAdminLogEventAction = null;
                break;
        }
        if (tLRPC$ChannelAdminLogEventAction != null || !z) {
            if (tLRPC$ChannelAdminLogEventAction != null) {
                tLRPC$ChannelAdminLogEventAction.readParams(abstractSerializedData, z);
            }
            return tLRPC$ChannelAdminLogEventAction;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ChannelAdminLogEventAction", new Object[]{Integer.valueOf(i)}));
    }
}

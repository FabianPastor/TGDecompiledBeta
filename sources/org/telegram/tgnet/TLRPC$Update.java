package org.telegram.tgnet;

public abstract class TLRPC$Update extends TLObject {
    public static TLRPC$Update TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Update tLRPC$Update;
        switch (i) {
            case -2112423005:
                tLRPC$Update = new TLRPC$TL_updateTheme();
                break;
            case -2046916883:
                tLRPC$Update = new TLRPC$TL_updateGroupCall();
                break;
            case -2027964103:
                tLRPC$Update = new TLRPC$TL_updateGeoLiveViewed();
                break;
            case -1987495099:
                tLRPC$Update = new TLRPC$TL_updateChannelReadMessagesContents();
                break;
            case -1906403213:
                tLRPC$Update = new TLRPC$TL_updateDcOptions();
                break;
            case -1870238482:
                tLRPC$Update = new TLRPC$TL_updateDeleteScheduledMessages();
                break;
            case -1821035490:
                tLRPC$Update = new TLRPC$TL_updateSavedGifs();
                break;
            case -1791935732:
                tLRPC$Update = new TLRPC$TL_updateUserPhoto();
                break;
            case -1738988427:
                tLRPC$Update = new TLRPC$TL_updateChannelPinnedMessage();
                break;
            case -1734268085:
                tLRPC$Update = new TLRPC$TL_updateChannelMessageViews();
                break;
            case -1706939360:
                tLRPC$Update = new TLRPC$TL_updateRecentStickers();
                break;
            case -1704596961:
                tLRPC$Update = new TLRPC$TL_updateChatUserTyping();
                break;
            case -1667805217:
                tLRPC$Update = new TLRPC$TL_updateReadHistoryInbox();
                break;
            case -1576161051:
                tLRPC$Update = new TLRPC$TL_updateDeleteMessages();
                break;
            case -1574314746:
                tLRPC$Update = new TLRPC$TL_updateConfig();
                break;
            case -1512627963:
                tLRPC$Update = new TLRPC$TL_updateDialogFilterOrder();
                break;
            case -1489818765:
                tLRPC$Update = new TLRPC$TL_updateUserName();
                break;
            case -1425052898:
                tLRPC$Update = new TLRPC$TL_updatePhoneCall();
                break;
            case -1398708869:
                tLRPC$Update = new TLRPC$TL_updateMessagePoll();
                break;
            case -1264392051:
                tLRPC$Update = new TLRPC$TL_updateEncryption();
                break;
            case -1263546448:
                tLRPC$Update = new TLRPC$TL_updatePeerLocated();
                break;
            case -1232070311:
                tLRPC$Update = new TLRPC$TL_updateChatParticipantAdmin();
                break;
            case -1227598250:
                tLRPC$Update = new TLRPC$TL_updateChannel();
                break;
            case -1094555409:
                tLRPC$Update = new TLRPC$TL_updateNotifySettings();
                break;
            case -1015733815:
                tLRPC$Update = new TLRPC$TL_updateDeleteChannelMessages();
                break;
            case -519195831:
                tLRPC$Update = new TLRPC$TL_updateChatPinnedMessage();
                break;
            case -513517117:
                tLRPC$Update = new TLRPC$TL_updateDialogUnreadMark();
                break;
            case -469536605:
                tLRPC$Update = new TLRPC$TL_updateEditMessage();
                break;
            case -451831443:
                tLRPC$Update = new TLRPC$TL_updateFavedStickers();
                break;
            case -364179876:
                tLRPC$Update = new TLRPC$TL_updateChatParticipantAdd();
                break;
            case -352032773:
                tLRPC$Update = new TLRPC$TL_updateChannelTooLong();
                break;
            case -337352679:
                tLRPC$Update = new TLRPC$TL_updateServiceNotification();
                break;
            case -299124375:
                tLRPC$Update = new TLRPC$TL_updateDraftMessage();
                break;
            case -298113238:
                tLRPC$Update = new TLRPC$TL_updatePrivacy();
                break;
            case -99664734:
                tLRPC$Update = new TLRPC$TL_updatePinnedDialogs();
                break;
            case -13975905:
                tLRPC$Update = new TLRPC$TL_updateChannelUserTyping();
                break;
            case 92188360:
                tLRPC$Update = new TLRPC$TL_updateGroupCallParticipant();
                break;
            case 125178264:
                tLRPC$Update = new TLRPC$TL_updateChatParticipants();
                break;
            case 196268545:
                tLRPC$Update = new TLRPC$TL_updateStickerSetsOrder();
                break;
            case 314130811:
                tLRPC$Update = new TLRPC$TL_updateUserPhone();
                break;
            case 314359194:
                tLRPC$Update = new TLRPC$TL_updateNewEncryptedMessage();
                break;
            case 357013699:
                tLRPC$Update = new TLRPC$TL_updateMessageReactions();
                break;
            case 386986326:
                tLRPC$Update = new TLRPC$TL_updateEncryptedChatTyping();
                break;
            case 422972864:
                tLRPC$Update = new TLRPC$TL_updateFolderPeers();
                break;
            case 457133559:
                tLRPC$Update = new TLRPC$TL_updateEditChannelMessage();
                break;
            case 469489699:
                tLRPC$Update = new TLRPC$TL_updateUserStatus();
                break;
            case 482860628:
                tLRPC$Update = new TLRPC$TL_updateReadChannelDiscussionInbox();
                break;
            case 522914557:
                tLRPC$Update = new TLRPC$TL_updateNewMessage();
                break;
            case 610945826:
                tLRPC$Update = new TLRPC$TL_updatePeerBlocked();
                break;
            case 634833351:
                tLRPC$Update = new TLRPC$TL_updateReadChannelOutbox();
                break;
            case 643940105:
                tLRPC$Update = new TLRPC$TL_updatePhoneCallSignalingData();
                break;
            case 654302845:
                tLRPC$Update = new TLRPC$TL_updateDialogFilter();
                break;
            case 791617983:
                tLRPC$Update = new TLRPC$TL_updateReadHistoryOutbox();
                break;
            case 856380452:
                tLRPC$Update = new TLRPC$TL_updateReadChannelInbox();
                break;
            case 889491791:
                tLRPC$Update = new TLRPC$TL_updateDialogFilters();
                break;
            case 956179895:
                tLRPC$Update = new TLRPC$TL_updateEncryptedMessagesRead();
                break;
            case 967122427:
                tLRPC$Update = new TLRPC$TL_updateNewScheduledMessage();
                break;
            case 1081547008:
                tLRPC$Update = new TLRPC$TL_updateChannelWebPage();
                break;
            case 1135492588:
                tLRPC$Update = new TLRPC$TL_updateStickerSets();
                break;
            case 1178116716:
                tLRPC$Update = new TLRPC$TL_updateReadChannelDiscussionOutbox();
                break;
            case 1180041828:
                tLRPC$Update = new TLRPC$TL_updateLangPackTooLong();
                break;
            case 1279515160:
                tLRPC$Update = new TLRPC$TL_updateUserPinnedMessage();
                break;
            case 1318109142:
                tLRPC$Update = new TLRPC$TL_updateMessageID();
                break;
            case 1421875280:
                tLRPC$Update = new TLRPC$TL_updateChatDefaultBannedRights();
                break;
            case 1442983757:
                tLRPC$Update = new TLRPC$TL_updateLangPack();
                break;
            case 1448076945:
                tLRPC$Update = new TLRPC$TL_updateLoginToken();
                break;
            case 1461528386:
                tLRPC$Update = new TLRPC$TL_updateReadFeaturedStickers();
                break;
            case 1548249383:
                tLRPC$Update = new TLRPC$TL_updateUserTyping();
                break;
            case 1656358105:
                tLRPC$Update = new TLRPC$TL_updateNewChannelMessage();
                break;
            case 1708307556:
                tLRPC$Update = new TLRPC$TL_updateChannelParticipant();
                break;
            case 1753886890:
                tLRPC$Update = new TLRPC$TL_updateNewStickerSet();
                break;
            case 1757493555:
                tLRPC$Update = new TLRPC$TL_updateReadMessagesContents();
                break;
            case 1786671974:
                tLRPC$Update = new TLRPC$TL_updatePeerSettings();
                break;
            case 1851755554:
                tLRPC$Update = new TLRPC$TL_updateChatParticipantDelete();
                break;
            case 1852826908:
                tLRPC$Update = new TLRPC$TL_updateDialogPinned();
                break;
            case 1854571743:
                tLRPC$Update = new TLRPC$TL_updateChannelMessageForwards();
                break;
            case 1887741886:
                tLRPC$Update = new TLRPC$TL_updateContactsReset();
                break;
            case 1893427255:
                tLRPC$Update = new TLRPC$TL_updateChannelAvailableMessages();
                break;
            case 2139689491:
                tLRPC$Update = new TLRPC$TL_updateWebPage();
                break;
            default:
                tLRPC$Update = null;
                break;
        }
        if (tLRPC$Update != null || !z) {
            if (tLRPC$Update != null) {
                tLRPC$Update.readParams(abstractSerializedData, z);
            }
            return tLRPC$Update;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Update", new Object[]{Integer.valueOf(i)}));
    }
}

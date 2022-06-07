package org.telegram.tgnet;

public abstract class TLRPC$Update extends TLObject {
    public static TLRPC$Update TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Update tLRPC$Update;
        switch (i) {
            case -2112423005:
                tLRPC$Update = new TLRPC$TL_updateTheme();
                break;
            case -2092401936:
                tLRPC$Update = new TLRPC$TL_updateChatUserTyping();
                break;
            case -2027964103:
                tLRPC$Update = new TLRPC$TL_updateGeoLiveViewed();
                break;
            case -2006880112:
                tLRPC$Update = new TLRPC$TL_updateTranscribeAudio();
                break;
            case -1937192669:
                tLRPC$Update = new TLRPC$TL_updateChannelUserTyping();
                break;
            case -1906403213:
                tLRPC$Update = new TLRPC$TL_updateDcOptions();
                break;
            case -1870238482:
                tLRPC$Update = new TLRPC$TL_updateDeleteScheduledMessages();
                break;
            case -1842450928:
                tLRPC$Update = new TLRPC$TL_updateReadChannelInbox();
                break;
            case -1821035490:
                tLRPC$Update = new TLRPC$TL_updateSavedGifs();
                break;
            case -1738720581:
                tLRPC$Update = new TLRPC$TL_updateChannelParticipant();
                break;
            case -1706939360:
                tLRPC$Update = new TLRPC$TL_updateRecentStickers();
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
            case -1425052898:
                tLRPC$Update = new TLRPC$TL_updatePhoneCall();
                break;
            case -1398708869:
                tLRPC$Update = new TLRPC$TL_updateMessagePoll();
                break;
            case -1304443240:
                tLRPC$Update = new TLRPC$TL_updateChannelAvailableMessages();
                break;
            case -1264392051:
                tLRPC$Update = new TLRPC$TL_updateEncryption();
                break;
            case -1263546448:
                tLRPC$Update = new TLRPC$TL_updatePeerLocated();
                break;
            case -1218471511:
                tLRPC$Update = new TLRPC$TL_updateReadChannelOutbox();
                break;
            case -1147422299:
                tLRPC$Update = new TLRPC$TL_updatePeerHistoryTTL();
                break;
            case -1094555409:
                tLRPC$Update = new TLRPC$TL_updateNotifySettings();
                break;
            case -1071741569:
                tLRPC$Update = new TLRPC$TL_updateUserTyping();
                break;
            case -1020437742:
                tLRPC$Update = new TLRPC$TL_updateDeleteChannelMessages();
                break;
            case -1007549728:
                tLRPC$Update = new TLRPC$TL_updateUserName();
                break;
            case -761649164:
                tLRPC$Update = new TLRPC$TL_updateChannelMessageForwards();
                break;
            case -693004986:
                tLRPC$Update = new TLRPC$TL_updateReadChannelDiscussionInbox();
                break;
            case -674602590:
                tLRPC$Update = new TLRPC$TL_updateChatParticipantAdmin();
                break;
            case -513517117:
                tLRPC$Update = new TLRPC$TL_updateDialogUnreadMark();
                break;
            case -483443337:
                tLRPC$Update = new TLRPC$TL_updateChatParticipantDelete();
                break;
            case -469536605:
                tLRPC$Update = new TLRPC$TL_updateEditMessage();
                break;
            case -451831443:
                tLRPC$Update = new TLRPC$TL_updateFavedStickers();
                break;
            case -440534818:
                tLRPC$Update = new TLRPC$TL_updateUserStatus();
                break;
            case -337352679:
                tLRPC$Update = new TLRPC$TL_updateServiceNotification();
                break;
            case -309990731:
                tLRPC$Update = new TLRPC$TL_updatePinnedMessages();
                break;
            case -299124375:
                tLRPC$Update = new TLRPC$TL_updateDraftMessage();
                break;
            case -298113238:
                tLRPC$Update = new TLRPC$TL_updatePrivacy();
                break;
            case -232346616:
                tLRPC$Update = new TLRPC$TL_updateChannelMessageViews();
                break;
            case -232290676:
                tLRPC$Update = new TLRPC$TL_updateUserPhoto();
                break;
            case -219423922:
                tLRPC$Update = new TLRPC$TL_updateGroupCallParticipants();
                break;
            case -124097970:
                tLRPC$Update = new TLRPC$TL_updateChat();
                break;
            case -99664734:
                tLRPC$Update = new TLRPC$TL_updatePinnedDialogs();
                break;
            case 8703322:
                tLRPC$Update = new TLRPC$TL_updateTranscribedAudio();
                break;
            case 88680979:
                tLRPC$Update = new TLRPC$TL_updateUserPhone();
                break;
            case 125178264:
                tLRPC$Update = new TLRPC$TL_updateChatParticipants();
                break;
            case 192428418:
                tLRPC$Update = new TLRPC$TL_updateGroupCallConnection();
                break;
            case 196268545:
                tLRPC$Update = new TLRPC$TL_updateStickerSetsOrder();
                break;
            case 277713951:
                tLRPC$Update = new TLRPC$TL_updateChannelTooLong();
                break;
            case 314359194:
                tLRPC$Update = new TLRPC$TL_updateNewEncryptedMessage();
                break;
            case 347227392:
                tLRPC$Update = new TLRPC$TL_updateGroupCall();
                break;
            case 347625491:
                tLRPC$Update = new TLRPC$TL_updateBotMenuButton();
                break;
            case 357013699:
                tLRPC$Update = new TLRPC$TL_updateMessageReactions();
                break;
            case 361936797:
                tLRPC$Update = new TLRPC$TL_updateWebViewResultSent();
                break;
            case 386986326:
                tLRPC$Update = new TLRPC$TL_updateEncryptedChatTyping();
                break;
            case 397910539:
                tLRPC$Update = new TLRPC$TL_updateAttachMenuBots();
                break;
            case 422972864:
                tLRPC$Update = new TLRPC$TL_updateFolderPeers();
                break;
            case 457133559:
                tLRPC$Update = new TLRPC$TL_updateEditChannelMessage();
                break;
            case 522914557:
                tLRPC$Update = new TLRPC$TL_updateNewMessage();
                break;
            case 610945826:
                tLRPC$Update = new TLRPC$TL_updatePeerBlocked();
                break;
            case 643940105:
                tLRPC$Update = new TLRPC$TL_updatePhoneCallSignalingData();
                break;
            case 654302845:
                tLRPC$Update = new TLRPC$TL_updateDialogFilter();
                break;
            case 791390623:
                tLRPC$Update = new TLRPC$TL_updateChannelWebPage();
                break;
            case 791617983:
                tLRPC$Update = new TLRPC$TL_updateReadHistoryOutbox();
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
            case 1037718609:
                tLRPC$Update = new TLRPC$TL_updateChatParticipantAdd();
                break;
            case 1135492588:
                tLRPC$Update = new TLRPC$TL_updateStickerSets();
                break;
            case 1153291573:
                tLRPC$Update = new TLRPC$TL_updateChannelReadMessagesContents();
                break;
            case 1180041828:
                tLRPC$Update = new TLRPC$TL_updateLangPackTooLong();
                break;
            case 1299263278:
                tLRPC$Update = new TLRPC$TL_updateBotCommands();
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
            case 1538885128:
                tLRPC$Update = new TLRPC$TL_updatePinnedChannelMessages();
                break;
            case 1656358105:
                tLRPC$Update = new TLRPC$TL_updateNewChannelMessage();
                break;
            case 1666927625:
                tLRPC$Update = new TLRPC$TL_updateChannel();
                break;
            case 1753886890:
                tLRPC$Update = new TLRPC$TL_updateNewStickerSet();
                break;
            case 1757493555:
                tLRPC$Update = new TLRPC$TL_updateReadMessagesContents();
                break;
            case 1767677564:
                tLRPC$Update = new TLRPC$TL_updateReadChannelDiscussionOutbox();
                break;
            case 1786671974:
                tLRPC$Update = new TLRPC$TL_updatePeerSettings();
                break;
            case 1852826908:
                tLRPC$Update = new TLRPC$TL_updateDialogPinned();
                break;
            case 1885586395:
                tLRPC$Update = new TLRPC$TL_updatePendingJoinRequests();
                break;
            case 1887741886:
                tLRPC$Update = new TLRPC$TL_updateContactsReset();
                break;
            case 1960361625:
                tLRPC$Update = new TLRPC$TL_updateSavedRingtones();
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

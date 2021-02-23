package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$MessageAction extends TLObject {
    public String address;
    public TLRPC$TL_inputGroupCall call;
    public long call_id;
    public int channel_id;
    public int chat_id;
    public String currency;
    public int duration;
    public TLRPC$DecryptedMessageAction encryptedAction;
    public int flags;
    public long game_id;
    public int inviter_id;
    public String message;
    public TLRPC$UserProfilePhoto newUserPhoto;
    public TLRPC$Photo photo;
    public TLRPC$PhoneCallDiscardReason reason;
    public int score;
    public String title;
    public long total_amount;
    public int ttl;
    public int user_id;
    public ArrayList<Integer> users = new ArrayList<>();
    public boolean video;

    public static TLRPC$MessageAction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageAction tLRPC$MessageAction;
        switch (i) {
            case -2132731265:
                tLRPC$MessageAction = new TLRPC$TL_messageActionPhoneCall();
                break;
            case -1834538890:
                tLRPC$MessageAction = new TLRPC$TL_messageActionGameScore();
                break;
            case -1799538451:
                tLRPC$MessageAction = new TLRPC$TL_messageActionPinMessage();
                break;
            case -1781355374:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChannelCreate();
                break;
            case -1780220945:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatDeletePhoto();
                break;
            case -1730095465:
                tLRPC$MessageAction = new TLRPC$TL_messageActionGeoProximityReached();
                break;
            case -1615153660:
                tLRPC$MessageAction = new TLRPC$TL_messageActionHistoryClear();
                break;
            case -1503425638:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatCreate();
                break;
            case -1441072131:
                tLRPC$MessageAction = new TLRPC$TL_messageActionSetMessagesTTL();
                break;
            case -1410748418:
                tLRPC$MessageAction = new TLRPC$TL_messageActionBotAllowed();
                break;
            case -1336546578:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChannelMigrateFrom();
                break;
            case -1297179892:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatDeleteUser();
                break;
            case -1247687078:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatEditTitle();
                break;
            case -1230047312:
                tLRPC$MessageAction = new TLRPC$TL_messageActionEmpty();
                break;
            case -648257196:
                tLRPC$MessageAction = new TLRPC$TL_messageActionSecureValuesSent();
                break;
            case -202219658:
                tLRPC$MessageAction = new TLRPC$TL_messageActionContactSignUp();
                break;
            case -123931160:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatJoinedByLink();
                break;
            case -85549226:
                tLRPC$MessageAction = new TLRPC$TL_messageActionCustomAction();
                break;
            case 1080663248:
                tLRPC$MessageAction = new TLRPC$TL_messageActionPaymentSent();
                break;
            case 1200788123:
                tLRPC$MessageAction = new TLRPC$TL_messageActionScreenshotTaken();
                break;
            case 1217033015:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatAddUser();
                break;
            case 1371385889:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatMigrateTo();
                break;
            case 1431655760:
                tLRPC$MessageAction = new TLRPC$TL_messageActionUserJoined();
                break;
            case 1431655761:
                tLRPC$MessageAction = new TLRPC$TL_messageActionUserUpdatedPhoto();
                break;
            case 1431655762:
                tLRPC$MessageAction = new TLRPC$TL_messageActionTTLChange();
                break;
            case 1431655767:
                tLRPC$MessageAction = new TLRPC$TL_messageActionCreatedBroadcastList();
                break;
            case 1431655925:
                tLRPC$MessageAction = new TLRPC$TL_messageActionLoginUnknownLocation();
                break;
            case 1431655927:
                tLRPC$MessageAction = new TLRPC$TL_messageEncryptedAction();
                break;
            case 1581055051:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatAddUser_old();
                break;
            case 1991897370:
                tLRPC$MessageAction = new TLRPC$TL_messageActionInviteToGroupCall();
                break;
            case 2047704898:
                tLRPC$MessageAction = new TLRPC$TL_messageActionGroupCall();
                break;
            case 2144015272:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatEditPhoto();
                break;
            default:
                tLRPC$MessageAction = null;
                break;
        }
        if (tLRPC$MessageAction != null || !z) {
            if (tLRPC$MessageAction != null) {
                tLRPC$MessageAction.readParams(abstractSerializedData, z);
            }
            return tLRPC$MessageAction;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageAction", new Object[]{Integer.valueOf(i)}));
    }
}

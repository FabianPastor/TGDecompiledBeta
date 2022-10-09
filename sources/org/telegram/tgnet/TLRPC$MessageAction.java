package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$MessageAction extends TLObject {
    public String address;
    public long amount;
    public TLRPC$TL_inputGroupCall call;
    public long call_id;
    public long channel_id;
    public long chat_id;
    public String currency;
    public int duration;
    public TLRPC$DecryptedMessageAction encryptedAction;
    public int flags;
    public long game_id;
    public long inviter_id;
    public String invoice_slug;
    public String message;
    public int months;
    public TLRPC$UserProfilePhoto newUserPhoto;
    public TLRPC$Photo photo;
    public TLRPC$PhoneCallDiscardReason reason;
    public boolean recurring_init;
    public boolean recurring_used;
    public int score;
    public String title;
    public long total_amount;
    public int ttl;
    public long user_id;
    public ArrayList<Long> users = new ArrayList<>();
    public boolean video;

    public static TLRPC$MessageAction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageAction tLRPC$MessageAction;
        switch (i) {
            case -2132731265:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.video = (readInt32 & 4) != 0;
                        this.call_id = abstractSerializedData2.readInt64(z2);
                        if ((this.flags & 1) != 0) {
                            this.reason = TLRPC$PhoneCallDiscardReason.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 2) != 0) {
                            this.duration = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.video ? this.flags | 4 : this.flags & (-5);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeInt64(this.call_id);
                        if ((this.flags & 1) != 0) {
                            this.reason.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeInt32(this.duration);
                        }
                    }
                };
                break;
            case -1892568281:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionPaymentSentMe
                    public static int constructor = -NUM;
                    public int flags;
                    public TLRPC$TL_paymentRequestedInfo info;
                    public byte[] payload;
                    public String shipping_option_id;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.recurring_init = (readInt32 & 4) != 0;
                        if ((readInt32 & 8) != 0) {
                            z3 = true;
                        }
                        this.recurring_used = z3;
                        this.currency = abstractSerializedData2.readString(z2);
                        this.total_amount = abstractSerializedData2.readInt64(z2);
                        this.payload = abstractSerializedData2.readByteArray(z2);
                        if ((this.flags & 1) != 0) {
                            this.info = TLRPC$TL_paymentRequestedInfo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                        if ((this.flags & 2) != 0) {
                            this.shipping_option_id = abstractSerializedData2.readString(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.recurring_init ? this.flags | 4 : this.flags & (-5);
                        this.flags = i2;
                        int i3 = this.recurring_used ? i2 | 8 : i2 & (-9);
                        this.flags = i3;
                        abstractSerializedData2.writeInt32(i3);
                        abstractSerializedData2.writeString(this.currency);
                        abstractSerializedData2.writeInt64(this.total_amount);
                        abstractSerializedData2.writeByteArray(this.payload);
                        if ((this.flags & 1) != 0) {
                            this.info.serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeString(this.shipping_option_id);
                        }
                    }
                };
                break;
            case -1834538890:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionGameScore
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.game_id = abstractSerializedData2.readInt64(z2);
                        this.score = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt64(this.game_id);
                        abstractSerializedData2.writeInt32(this.score);
                    }
                };
                break;
            case -1799538451:
                tLRPC$MessageAction = new TLRPC$TL_messageActionPinMessage();
                break;
            case -1781355374:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.title = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.title);
                    }
                };
                break;
            case -1780220945:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatDeletePhoto();
                break;
            case -1776926890:
                tLRPC$MessageAction = new TLRPC$TL_messageActionPaymentSent();
                break;
            case -1730095465:
                tLRPC$MessageAction = new TLRPC$TL_messageActionGeoProximityReached();
                break;
            case -1615153660:
                tLRPC$MessageAction = new TLRPC$TL_messageActionHistoryClear();
                break;
            case -1539362612:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatDeleteUser();
                break;
            case -1503425638:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatCreate() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionChatCreate_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatCreate, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.title = abstractSerializedData2.readString(z2);
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        if (readInt32 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                            }
                            return;
                        }
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            this.users.add(Long.valueOf(abstractSerializedData2.readInt32(z2)));
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatCreate, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.users.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            abstractSerializedData2.writeInt32((int) this.users.get(i2).longValue());
                        }
                    }
                };
                break;
            case -1441072131:
                tLRPC$MessageAction = new TLRPC$TL_messageActionSetMessagesTTL();
                break;
            case -1434950843:
                tLRPC$MessageAction = new TLRPC$TL_messageActionSetChatTheme();
                break;
            case -1415514682:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionGiftPremium
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.currency = abstractSerializedData2.readString(z2);
                        this.amount = abstractSerializedData2.readInt64(z2);
                        this.months = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.currency);
                        abstractSerializedData2.writeInt64(this.amount);
                        abstractSerializedData2.writeInt32(this.months);
                    }
                };
                break;
            case -1410748418:
                tLRPC$MessageAction = new TLRPC$TL_messageActionBotAllowed();
                break;
            case -1336546578:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChannelMigrateFrom() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.title = abstractSerializedData2.readString(z2);
                        this.chat_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeInt32((int) this.chat_id);
                    }
                };
                break;
            case -1297179892:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatDeleteUser() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.user_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.user_id);
                    }
                };
                break;
            case -1281329567:
                tLRPC$MessageAction = new TLRPC$TL_messageActionGroupCallScheduled();
                break;
            case -1262252875:
                tLRPC$MessageAction = new TLRPC$TL_messageActionWebViewDataSent();
                break;
            case -1247687078:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.title = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.title);
                    }
                };
                break;
            case -1230047312:
                tLRPC$MessageAction = new TLRPC$TL_messageActionEmpty();
                break;
            case -1119368275:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatCreate();
                break;
            case -648257196:
                tLRPC$MessageAction = new TLRPC$TL_messageActionSecureValuesSent();
                break;
            case -519864430:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatMigrateTo();
                break;
            case -365344535:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChannelMigrateFrom();
                break;
            case -339958837:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatJoinedByRequest();
                break;
            case -202219658:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -123931160:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatJoinedByLink() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink_layer131
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.inviter_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.inviter_id);
                    }
                };
                break;
            case -85549226:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionCustomAction
                    public static int constructor = -85549226;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.message = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.message);
                    }
                };
                break;
            case 29007925:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionPhoneNumberRequest
                    public static int constructor = 29007925;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 51520707:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatJoinedByLink();
                break;
            case 365886720:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatAddUser();
                break;
            case 1080663248:
                tLRPC$MessageAction = new TLRPC$TL_messageActionPaymentSent() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent_layer140
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.currency = abstractSerializedData2.readString(z2);
                        this.total_amount = abstractSerializedData2.readInt64(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.currency);
                        abstractSerializedData2.writeInt64(this.total_amount);
                    }
                };
                break;
            case 1200788123:
                tLRPC$MessageAction = new TLRPC$TL_messageActionScreenshotTaken();
                break;
            case 1205698681:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionWebViewDataSentMe
                    public static int constructor = NUM;
                    public String data;
                    public String text;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                        this.data = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                        abstractSerializedData2.writeString(this.data);
                    }
                };
                break;
            case 1217033015:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatAddUser() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        if (readInt32 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                            }
                            return;
                        }
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            this.users.add(Long.valueOf(abstractSerializedData2.readInt32(z2)));
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.users.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            abstractSerializedData2.writeInt32((int) this.users.get(i2).longValue());
                        }
                    }
                };
                break;
            case 1345295095:
                tLRPC$MessageAction = new TLRPC$TL_messageActionInviteToGroupCall();
                break;
            case 1371385889:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatMigrateTo() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.channel_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.channel_id);
                    }
                };
                break;
            case 1431655760:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1431655761:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.newUserPhoto = TLRPC$UserProfilePhoto.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        this.newUserPhoto.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case 1431655762:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionTTLChange
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.ttl = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.ttl);
                    }
                };
                break;
            case 1431655767:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionCreatedBroadcastList
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1431655925:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.title = abstractSerializedData2.readString(z2);
                        this.address = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeString(this.address);
                    }
                };
                break;
            case 1431655927:
                tLRPC$MessageAction = new TLRPC$TL_messageEncryptedAction();
                break;
            case 1581055051:
                tLRPC$MessageAction = new TLRPC$TL_messageActionChatAddUser() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser_old
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.user_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32((int) this.user_id);
                    }
                };
                break;
            case 1991897370:
                tLRPC$MessageAction = new TLRPC$TL_messageActionInviteToGroupCall() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall_layer131
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.call = TLRPC$TL_inputGroupCall.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        if (readInt32 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                            }
                            return;
                        }
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            this.users.add(Long.valueOf(abstractSerializedData2.readInt32(z2)));
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        this.call.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.users.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            abstractSerializedData2.writeInt32((int) this.users.get(i2).longValue());
                        }
                    }
                };
                break;
            case 2047704898:
                tLRPC$MessageAction = new TLRPC$MessageAction() { // from class: org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.flags = abstractSerializedData2.readInt32(z2);
                        this.call = TLRPC$TL_inputGroupCall.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 1) != 0) {
                            this.duration = abstractSerializedData2.readInt32(z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        this.call.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeInt32(this.duration);
                        }
                    }
                };
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
        throw new RuntimeException(String.format("can't parse magic %x in MessageAction", Integer.valueOf(i)));
    }
}

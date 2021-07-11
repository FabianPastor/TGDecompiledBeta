package org.telegram.tgnet;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class TLRPC$Message extends TLObject {
    public TLRPC$MessageAction action;
    public String attachPath = "";
    public int date;
    public int destroyTime;
    public long dialog_id;
    public int edit_date;
    public boolean edit_hide;
    public ArrayList<TLRPC$MessageEntity> entities = new ArrayList<>();
    public int flags;
    public int forwards;
    public TLRPC$Peer from_id;
    public boolean from_scheduled;
    public TLRPC$MessageFwdHeader fwd_from;
    public int fwd_msg_id = 0;
    public long grouped_id;
    public int id;
    public boolean isThreadMessage;
    public int layer;
    public boolean legacy;
    public int local_id = 0;
    public TLRPC$MessageMedia media;
    public boolean media_unread;
    public boolean mentioned;
    public String message;
    public boolean out;
    public HashMap<String, String> params;
    public TLRPC$Peer peer_id;
    public boolean pinned;
    public boolean post;
    public String post_author;
    public long random_id;
    public TLRPC$TL_messageReactions reactions;
    public int realId;
    public TLRPC$TL_messageReplies replies;
    public TLRPC$Message replyMessage;
    public TLRPC$ReplyMarkup reply_markup;
    public TLRPC$TL_messageReplyHeader reply_to;
    public int reqId;
    public ArrayList<TLRPC$TL_restrictionReason> restriction_reason = new ArrayList<>();
    public int send_state = 0;
    public int seq_in;
    public int seq_out;
    public boolean silent;
    public int stickerVerified = 1;
    public int ttl;
    public int ttl_period;
    public boolean unread;
    public int via_bot_id;
    public String via_bot_name;
    public int views;
    public boolean with_my_score;

    public static TLRPC$Message TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Message tLRPC$Message;
        switch (i) {
            case -2082087340:
                tLRPC$Message = new TLRPC$TL_messageEmpty_layer122();
                break;
            case -1868117372:
                tLRPC$Message = new TLRPC$TL_messageEmpty();
                break;
            case -1864508399:
                tLRPC$Message = new TLRPC$TL_message_layer72();
                break;
            case -1752573244:
                tLRPC$Message = new TLRPC$TL_message_layer104_3();
                break;
            case -1642487306:
                tLRPC$Message = new TLRPC$TL_messageService_layer118();
                break;
            case -1618124613:
                tLRPC$Message = new TLRPC$TL_messageService_old();
                break;
            case -1553471722:
                tLRPC$Message = new TLRPC$TL_messageForwarded_old2();
                break;
            case -1481959023:
                tLRPC$Message = new TLRPC$TL_message_old3();
                break;
            case -1125940270:
                tLRPC$Message = new TLRPC$TL_message();
                break;
            case -1066691065:
                tLRPC$Message = new TLRPC$TL_messageService_layer48();
                break;
            case -1063525281:
                tLRPC$Message = new TLRPC$TL_message_layer68();
                break;
            case -1023016155:
                tLRPC$Message = new TLRPC$TL_message_old4();
                break;
            case -913120932:
                tLRPC$Message = new TLRPC$TL_message_layer47();
                break;
            case -260565816:
                tLRPC$Message = new TLRPC$TL_message_old5();
                break;
            case -181507201:
                tLRPC$Message = new TLRPC$TL_message_layer118();
                break;
            case 99903492:
                tLRPC$Message = new TLRPC$TL_messageForwarded_old();
                break;
            case 479924263:
                tLRPC$Message = new TLRPC$TL_message_layer104_2();
                break;
            case 495384334:
                tLRPC$Message = new TLRPC$TL_messageService_old2();
                break;
            case 585853626:
                tLRPC$Message = new TLRPC$TL_message_old();
                break;
            case 678405636:
                tLRPC$Message = new TLRPC$TL_messageService_layer123();
                break;
            case 721967202:
                tLRPC$Message = new TLRPC$TL_messageService();
                break;
            case 736885382:
                tLRPC$Message = new TLRPC$TL_message_old6();
                break;
            case 1157215293:
                tLRPC$Message = new TLRPC$TL_message_layer104();
                break;
            case 1160515173:
                tLRPC$Message = new TLRPC$TL_message_layer117();
                break;
            case 1431655928:
                tLRPC$Message = new TLRPC$TL_message_secret_old();
                break;
            case 1431655929:
                tLRPC$Message = new TLRPC$TL_message_secret_layer72();
                break;
            case 1431655930:
                tLRPC$Message = new TLRPC$TL_message_secret();
                break;
            case 1450613171:
                tLRPC$Message = new TLRPC$TL_message_old2();
                break;
            case 1487813065:
                tLRPC$Message = new TLRPC$TL_message_layer123();
                break;
            case 1537633299:
                tLRPC$Message = new TLRPC$TL_message_old7();
                break;
            default:
                tLRPC$Message = null;
                break;
        }
        if (tLRPC$Message != null || !z) {
            if (tLRPC$Message != null) {
                tLRPC$Message.readParams(abstractSerializedData, z);
                if (tLRPC$Message.from_id == null) {
                    tLRPC$Message.from_id = tLRPC$Message.peer_id;
                }
            }
            return tLRPC$Message;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Message", new Object[]{Integer.valueOf(i)}));
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:95:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readAttachPath(org.telegram.tgnet.AbstractSerializedData r8, int r9) {
        /*
            r7 = this;
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r7.media
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0010
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            if (r3 != 0) goto L_0x0010
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r0 != 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            java.lang.String r3 = r7.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x003f
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_old
            if (r4 != 0) goto L_0x0033
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_layer68
            if (r4 != 0) goto L_0x0033
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_layer74
            if (r4 != 0) goto L_0x0033
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument_old
            if (r4 != 0) goto L_0x0033
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument_layer68
            if (r4 != 0) goto L_0x0033
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument_layer74
            if (r3 == 0) goto L_0x003f
        L_0x0033:
            java.lang.String r3 = r7.message
            java.lang.String r4 = "-1"
            boolean r3 = r3.startsWith(r4)
            if (r3 == 0) goto L_0x003f
            r3 = 1
            goto L_0x0040
        L_0x003f:
            r3 = 0
        L_0x0040:
            boolean r4 = r7.out
            r5 = 3
            if (r4 != 0) goto L_0x0057
            org.telegram.tgnet.TLRPC$Peer r4 = r7.peer_id
            if (r4 == 0) goto L_0x0061
            org.telegram.tgnet.TLRPC$Peer r6 = r7.from_id
            if (r6 == 0) goto L_0x0061
            int r4 = r4.user_id
            if (r4 == 0) goto L_0x0061
            int r6 = r6.user_id
            if (r4 != r6) goto L_0x0061
            if (r6 != r9) goto L_0x0061
        L_0x0057:
            int r9 = r7.id
            if (r9 < 0) goto L_0x0065
            if (r0 != 0) goto L_0x0065
            int r9 = r7.send_state
            if (r9 == r5) goto L_0x0065
        L_0x0061:
            boolean r9 = r7.legacy
            if (r9 == 0) goto L_0x011c
        L_0x0065:
            r9 = 2
            if (r0 == 0) goto L_0x009c
            if (r3 == 0) goto L_0x009c
            java.lang.String r0 = r7.message
            int r0 = r0.length()
            r3 = 6
            if (r0 <= r3) goto L_0x008c
            java.lang.String r0 = r7.message
            char r0 = r0.charAt(r9)
            r3 = 95
            if (r0 != r3) goto L_0x008c
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7.params = r0
            java.lang.String r3 = r7.message
            java.lang.String r4 = "ve"
            r0.put(r4, r3)
        L_0x008c:
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r7.params
            if (r0 != 0) goto L_0x0098
            java.lang.String r0 = r7.message
            int r0 = r0.length()
            if (r0 != r9) goto L_0x009c
        L_0x0098:
            java.lang.String r0 = ""
            r7.message = r0
        L_0x009c:
            int r0 = r8.remaining()
            if (r0 <= 0) goto L_0x011c
            java.lang.String r0 = r8.readString(r1)
            r7.attachPath = r0
            if (r0 == 0) goto L_0x011c
            int r3 = r7.id
            if (r3 < 0) goto L_0x00b6
            int r3 = r7.send_state
            if (r3 == r5) goto L_0x00b6
            boolean r3 = r7.legacy
            if (r3 == 0) goto L_0x0114
        L_0x00b6:
            java.lang.String r3 = "||"
            boolean r0 = r0.startsWith(r3)
            if (r0 == 0) goto L_0x0114
            java.lang.String r0 = r7.attachPath
            java.lang.String r3 = "\\|\\|"
            java.lang.String[] r0 = r0.split(r3)
            int r3 = r0.length
            if (r3 <= 0) goto L_0x011c
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r7.params
            if (r3 != 0) goto L_0x00d5
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            r7.params = r3
        L_0x00d5:
            r3 = 1
        L_0x00d6:
            int r4 = r0.length
            int r4 = r4 - r2
            if (r3 >= r4) goto L_0x00f1
            r4 = r0[r3]
            java.lang.String r5 = "\\|=\\|"
            java.lang.String[] r4 = r4.split(r5)
            int r5 = r4.length
            if (r5 != r9) goto L_0x00ee
            java.util.HashMap<java.lang.String, java.lang.String> r5 = r7.params
            r6 = r4[r1]
            r4 = r4[r2]
            r5.put(r6, r4)
        L_0x00ee:
            int r3 = r3 + 1
            goto L_0x00d6
        L_0x00f1:
            int r9 = r0.length
            int r9 = r9 - r2
            r9 = r0[r9]
            java.lang.String r9 = r9.trim()
            r7.attachPath = r9
            boolean r9 = r7.legacy
            if (r9 == 0) goto L_0x011c
            java.util.HashMap<java.lang.String, java.lang.String> r9 = r7.params
            java.lang.String r0 = "legacy_layer"
            java.lang.Object r9 = r9.get(r0)
            java.lang.CharSequence r9 = (java.lang.CharSequence) r9
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r9)
            int r9 = r9.intValue()
            r7.layer = r9
            goto L_0x011c
        L_0x0114:
            java.lang.String r9 = r7.attachPath
            java.lang.String r9 = r9.trim()
            r7.attachPath = r9
        L_0x011c:
            int r9 = r7.flags
            r9 = r9 & 4
            if (r9 == 0) goto L_0x012c
            int r9 = r7.id
            if (r9 >= 0) goto L_0x012c
            int r8 = r8.readInt32(r1)
            r7.fwd_msg_id = r8
        L_0x012c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.TLRPC$Message.readAttachPath(org.telegram.tgnet.AbstractSerializedData, int):void");
    }

    /* access modifiers changed from: protected */
    public void writeAttachPath(AbstractSerializedData abstractSerializedData) {
        HashMap<String, String> hashMap;
        HashMap<String, String> hashMap2;
        if ((this instanceof TLRPC$TL_message_secret) || (this instanceof TLRPC$TL_message_secret_layer72)) {
            String str = this.attachPath;
            if (str == null) {
                str = "";
            }
            if (this.send_state == 1 && (hashMap = this.params) != null && hashMap.size() > 0) {
                for (Map.Entry next : this.params.entrySet()) {
                    str = ((String) next.getKey()) + "|=|" + ((String) next.getValue()) + "||" + str;
                }
                str = "||" + str;
            }
            abstractSerializedData.writeString(str);
            return;
        }
        String str2 = !TextUtils.isEmpty(this.attachPath) ? this.attachPath : " ";
        if (this.legacy) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            this.layer = 130;
            this.params.put("legacy_layer", "130");
        }
        if ((this.id < 0 || this.send_state == 3 || this.legacy) && (hashMap2 = this.params) != null && hashMap2.size() > 0) {
            for (Map.Entry next2 : this.params.entrySet()) {
                str2 = ((String) next2.getKey()) + "|=|" + ((String) next2.getValue()) + "||" + str2;
            }
            str2 = "||" + str2;
        }
        abstractSerializedData.writeString(str2);
        if ((this.flags & 4) != 0 && this.id < 0) {
            abstractSerializedData.writeInt32(this.fwd_msg_id);
        }
    }
}

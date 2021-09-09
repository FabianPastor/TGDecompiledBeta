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
    public TLRPC$MessageReplies replies;
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
    public long via_bot_id;
    public String via_bot_name;
    public int views;
    public boolean with_my_score;

    public static TLRPC$Message TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Message tLRPC$Message;
        switch (i) {
            case -2082087340:
                tLRPC$Message = new TLRPC$TL_messageEmpty_layer122();
                break;
            case -2049520670:
                tLRPC$Message = new TLRPC$TL_message();
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
                tLRPC$Message = new TLRPC$TL_message_layer131();
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

    /* JADX WARNING: Code restructure failed: missing block: B:37:0x005d, code lost:
        if (r9 == r13) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0067, code lost:
        if (r11.send_state != 3) goto L_0x0069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x006b, code lost:
        if (r11.legacy != false) goto L_0x006d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:96:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readAttachPath(org.telegram.tgnet.AbstractSerializedData r12, long r13) {
        /*
            r11 = this;
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r11.media
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
            java.lang.String r3 = r11.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x003f
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r11.media
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
            java.lang.String r3 = r11.message
            java.lang.String r4 = "-1"
            boolean r3 = r3.startsWith(r4)
            if (r3 == 0) goto L_0x003f
            r3 = 1
            goto L_0x0040
        L_0x003f:
            r3 = 0
        L_0x0040:
            boolean r4 = r11.out
            r5 = 3
            if (r4 != 0) goto L_0x005f
            org.telegram.tgnet.TLRPC$Peer r4 = r11.peer_id
            if (r4 == 0) goto L_0x0069
            org.telegram.tgnet.TLRPC$Peer r6 = r11.from_id
            if (r6 == 0) goto L_0x0069
            long r7 = r4.user_id
            r9 = 0
            int r4 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r4 == 0) goto L_0x0069
            long r9 = r6.user_id
            int r4 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r4 != 0) goto L_0x0069
            int r4 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r4 != 0) goto L_0x0069
        L_0x005f:
            int r13 = r11.id
            if (r13 < 0) goto L_0x006d
            if (r0 != 0) goto L_0x006d
            int r13 = r11.send_state
            if (r13 == r5) goto L_0x006d
        L_0x0069:
            boolean r13 = r11.legacy
            if (r13 == 0) goto L_0x0122
        L_0x006d:
            r13 = 2
            if (r0 == 0) goto L_0x00a3
            if (r3 == 0) goto L_0x00a3
            java.lang.String r14 = r11.message
            int r14 = r14.length()
            r0 = 6
            if (r14 <= r0) goto L_0x0093
            java.lang.String r14 = r11.message
            char r14 = r14.charAt(r13)
            r0 = 95
            if (r14 != r0) goto L_0x0093
            java.util.HashMap r14 = new java.util.HashMap
            r14.<init>()
            r11.params = r14
            java.lang.String r0 = r11.message
            java.lang.String r3 = "ve"
            r14.put(r3, r0)
        L_0x0093:
            java.util.HashMap<java.lang.String, java.lang.String> r14 = r11.params
            if (r14 != 0) goto L_0x009f
            java.lang.String r14 = r11.message
            int r14 = r14.length()
            if (r14 != r13) goto L_0x00a3
        L_0x009f:
            java.lang.String r14 = ""
            r11.message = r14
        L_0x00a3:
            int r14 = r12.remaining()
            if (r14 <= 0) goto L_0x0122
            java.lang.String r14 = r12.readString(r1)
            r11.attachPath = r14
            if (r14 == 0) goto L_0x0122
            int r0 = r11.id
            if (r0 < 0) goto L_0x00bd
            int r0 = r11.send_state
            if (r0 == r5) goto L_0x00bd
            boolean r0 = r11.legacy
            if (r0 == 0) goto L_0x011a
        L_0x00bd:
            java.lang.String r0 = "||"
            boolean r14 = r14.startsWith(r0)
            if (r14 == 0) goto L_0x011a
            java.lang.String r14 = r11.attachPath
            java.lang.String r0 = "\\|\\|"
            java.lang.String[] r14 = r14.split(r0)
            int r0 = r14.length
            if (r0 <= 0) goto L_0x0122
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r11.params
            if (r0 != 0) goto L_0x00db
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r11.params = r0
        L_0x00db:
            r0 = 1
        L_0x00dc:
            int r3 = r14.length
            int r3 = r3 - r2
            if (r0 >= r3) goto L_0x00f7
            r3 = r14[r0]
            java.lang.String r4 = "\\|=\\|"
            java.lang.String[] r3 = r3.split(r4)
            int r4 = r3.length
            if (r4 != r13) goto L_0x00f4
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r11.params
            r5 = r3[r1]
            r3 = r3[r2]
            r4.put(r5, r3)
        L_0x00f4:
            int r0 = r0 + 1
            goto L_0x00dc
        L_0x00f7:
            int r13 = r14.length
            int r13 = r13 - r2
            r13 = r14[r13]
            java.lang.String r13 = r13.trim()
            r11.attachPath = r13
            boolean r13 = r11.legacy
            if (r13 == 0) goto L_0x0122
            java.util.HashMap<java.lang.String, java.lang.String> r13 = r11.params
            java.lang.String r14 = "legacy_layer"
            java.lang.Object r13 = r13.get(r14)
            java.lang.CharSequence r13 = (java.lang.CharSequence) r13
            java.lang.Integer r13 = org.telegram.messenger.Utilities.parseInt(r13)
            int r13 = r13.intValue()
            r11.layer = r13
            goto L_0x0122
        L_0x011a:
            java.lang.String r13 = r11.attachPath
            java.lang.String r13 = r13.trim()
            r11.attachPath = r13
        L_0x0122:
            int r13 = r11.flags
            r13 = r13 & 4
            if (r13 == 0) goto L_0x0132
            int r13 = r11.id
            if (r13 >= 0) goto L_0x0132
            int r12 = r12.readInt32(r1)
            r11.fwd_msg_id = r12
        L_0x0132:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.TLRPC$Message.readAttachPath(org.telegram.tgnet.AbstractSerializedData, long):void");
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
            this.layer = 133;
            this.params.put("legacy_layer", "133");
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

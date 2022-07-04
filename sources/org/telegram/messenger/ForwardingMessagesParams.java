package org.telegram.messenger;

import android.util.LongSparseArray;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_pollAnswerVoters;

public class ForwardingMessagesParams {
    public LongSparseArray<MessageObject.GroupedMessages> groupedMessagesMap = new LongSparseArray<>();
    public boolean hasCaption;
    public boolean hasSenders;
    public boolean hasSpoilers;
    public boolean hideCaption;
    public boolean hideForwardSendersName;
    public boolean isSecret;
    public ArrayList<MessageObject> messages;
    public boolean multiplyUsers;
    public ArrayList<TLRPC$TL_pollAnswerVoters> pollChoosenAnswers = new ArrayList<>();
    public ArrayList<MessageObject> previewMessages = new ArrayList<>();
    public SparseBooleanArray selectedIds = new SparseBooleanArray();
    public boolean willSeeSenders;

    /* JADX WARNING: Removed duplicated region for block: B:43:0x0124  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x014a  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0178  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01ed A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ForwardingMessagesParams(java.util.ArrayList<org.telegram.messenger.MessageObject> r20, long r21) {
        /*
            r19 = this;
            r6 = r19
            r7 = r20
            r19.<init>()
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            r6.groupedMessagesMap = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6.previewMessages = r0
            android.util.SparseBooleanArray r0 = new android.util.SparseBooleanArray
            r0.<init>()
            r6.selectedIds = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6.pollChoosenAnswers = r0
            r6.messages = r7
            r8 = 0
            r6.hasCaption = r8
            r6.hasSenders = r8
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r21)
            r6.isSecret = r0
            r6.hasSpoilers = r8
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            r10 = 0
        L_0x0038:
            int r0 = r20.size()
            r11 = 1
            if (r10 >= r0) goto L_0x01f4
            java.lang.Object r0 = r7.get(r10)
            r12 = r0
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            java.lang.CharSequence r0 = r12.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0050
            r6.hasCaption = r11
        L_0x0050:
            android.util.SparseBooleanArray r0 = r6.selectedIds
            int r1 = r12.getId()
            r0.put(r1, r11)
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message
            r3.<init>()
            org.telegram.tgnet.TLRPC$Message r0 = r12.messageOwner
            int r1 = r0.id
            r3.id = r1
            long r1 = r0.grouped_id
            r3.grouped_id = r1
            org.telegram.tgnet.TLRPC$Peer r1 = r0.peer_id
            r3.peer_id = r1
            org.telegram.tgnet.TLRPC$Peer r1 = r0.from_id
            r3.from_id = r1
            java.lang.String r1 = r0.message
            r3.message = r1
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            r3.media = r1
            org.telegram.tgnet.TLRPC$MessageAction r1 = r0.action
            r3.action = r1
            r3.edit_date = r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            if (r0 == 0) goto L_0x00a3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r3.entities
            r1.addAll(r0)
            boolean r0 = r6.hasSpoilers
            if (r0 != 0) goto L_0x00a3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r3.entities
            java.util.Iterator r0 = r0.iterator()
        L_0x0091:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x00a3
            java.lang.Object r1 = r0.next()
            org.telegram.tgnet.TLRPC$MessageEntity r1 = (org.telegram.tgnet.TLRPC$MessageEntity) r1
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler
            if (r1 == 0) goto L_0x0091
            r6.hasSpoilers = r11
        L_0x00a3:
            r3.out = r11
            r3.unread = r8
            org.telegram.tgnet.TLRPC$Message r0 = r12.messageOwner
            long r1 = r0.via_bot_id
            r3.via_bot_id = r1
            org.telegram.tgnet.TLRPC$ReplyMarkup r1 = r0.reply_markup
            r3.reply_markup = r1
            boolean r1 = r0.post
            r3.post = r1
            boolean r1 = r0.legacy
            r3.legacy = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r1 = r0.restriction_reason
            r3.restriction_reason = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.replyMessage
            r3.replyMessage = r0
            int r0 = r12.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r0 = r0.clientUserId
            boolean r2 = r6.isSecret
            r13 = 0
            if (r2 != 0) goto L_0x011f
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r2.fwd_from
            if (r4 == 0) goto L_0x00f4
            boolean r0 = r12.isDice()
            if (r0 != 0) goto L_0x00de
            r6.hasSenders = r11
            goto L_0x00e0
        L_0x00de:
            r6.willSeeSenders = r11
        L_0x00e0:
            org.telegram.tgnet.TLRPC$Peer r0 = r4.from_id
            if (r0 != 0) goto L_0x00f1
            java.lang.String r0 = r4.from_name
            boolean r0 = r9.contains(r0)
            if (r0 != 0) goto L_0x00f1
            java.lang.String r0 = r4.from_name
            r9.add(r0)
        L_0x00f1:
            r17 = r9
            goto L_0x0122
        L_0x00f4:
            org.telegram.tgnet.TLRPC$Peer r4 = r2.from_id
            long r4 = r4.user_id
            int r16 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            r17 = r9
            if (r16 == 0) goto L_0x0108
            long r8 = r2.dialog_id
            int r2 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x0108
            int r2 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r2 == 0) goto L_0x0121
        L_0x0108:
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r4 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r4.<init>()
            org.telegram.tgnet.TLRPC$Message r0 = r12.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.from_id
            r4.from_id = r0
            boolean r0 = r12.isDice()
            if (r0 != 0) goto L_0x011c
            r6.hasSenders = r11
            goto L_0x0122
        L_0x011c:
            r6.willSeeSenders = r11
            goto L_0x0122
        L_0x011f:
            r17 = r9
        L_0x0121:
            r4 = 0
        L_0x0122:
            if (r4 == 0) goto L_0x012c
            r3.fwd_from = r4
            int r0 = r3.flags
            r0 = r0 | 4
            r3.flags = r0
        L_0x012c:
            r8 = r21
            r3.dialog_id = r8
            org.telegram.messenger.ForwardingMessagesParams$1 r5 = new org.telegram.messenger.ForwardingMessagesParams$1
            int r2 = r12.currentAccount
            r4 = 1
            r18 = 0
            r0 = r5
            r1 = r19
            r15 = r5
            r5 = r18
            r0.<init>(r2, r3, r4, r5)
            r15.preview = r11
            long r0 = r15.getGroupId()
            int r2 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x016c
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r0 = r6.groupedMessagesMap
            long r1 = r15.getGroupId()
            r3 = 0
            java.lang.Object r0 = r0.get(r1, r3)
            org.telegram.messenger.MessageObject$GroupedMessages r0 = (org.telegram.messenger.MessageObject.GroupedMessages) r0
            if (r0 != 0) goto L_0x0167
            org.telegram.messenger.MessageObject$GroupedMessages r0 = new org.telegram.messenger.MessageObject$GroupedMessages
            r0.<init>()
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r1 = r6.groupedMessagesMap
            long r2 = r15.getGroupId()
            r1.put(r2, r0)
        L_0x0167:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r0.messages
            r0.add(r15)
        L_0x016c:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r6.previewMessages
            r1 = 0
            r0.add(r1, r15)
            boolean r0 = r12.isPoll()
            if (r0 == 0) goto L_0x01ed
            org.telegram.tgnet.TLRPC$Message r0 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            org.telegram.messenger.ForwardingMessagesParams$PreviewMediaPoll r2 = new org.telegram.messenger.ForwardingMessagesParams$PreviewMediaPoll
            r2.<init>()
            org.telegram.tgnet.TLRPC$Poll r3 = r0.poll
            r2.poll = r3
            java.lang.String r3 = r0.provider
            r2.provider = r3
            org.telegram.tgnet.TLRPC$TL_pollResults r3 = new org.telegram.tgnet.TLRPC$TL_pollResults
            r3.<init>()
            r2.results = r3
            org.telegram.tgnet.TLRPC$PollResults r4 = r0.results
            int r4 = r4.total_voters
            r3.total_voters = r4
            r2.totalVotersCached = r4
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner
            r3.media = r2
            boolean r3 = r12.canUnvote()
            if (r3 == 0) goto L_0x01ed
            org.telegram.tgnet.TLRPC$PollResults r3 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r3 = r3.results
            int r3 = r3.size()
            r4 = 0
        L_0x01ad:
            if (r4 >= r3) goto L_0x01ed
            org.telegram.tgnet.TLRPC$PollResults r5 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r5 = r5.results
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r5 = (org.telegram.tgnet.TLRPC$TL_pollAnswerVoters) r5
            boolean r11 = r5.chosen
            if (r11 == 0) goto L_0x01e3
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r11 = new org.telegram.tgnet.TLRPC$TL_pollAnswerVoters
            r11.<init>()
            boolean r12 = r5.chosen
            r11.chosen = r12
            boolean r12 = r5.correct
            r11.correct = r12
            int r12 = r5.flags
            r11.flags = r12
            byte[] r12 = r5.option
            r11.option = r12
            int r5 = r5.voters
            r11.voters = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r5 = r6.pollChoosenAnswers
            r5.add(r11)
            org.telegram.tgnet.TLRPC$PollResults r5 = r2.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r5 = r5.results
            r5.add(r11)
            goto L_0x01ea
        L_0x01e3:
            org.telegram.tgnet.TLRPC$PollResults r11 = r2.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r11 = r11.results
            r11.add(r5)
        L_0x01ea:
            int r4 = r4 + 1
            goto L_0x01ad
        L_0x01ed:
            int r10 = r10 + 1
            r9 = r17
            r8 = 0
            goto L_0x0038
        L_0x01f4:
            r17 = r9
            r1 = 0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r2 = 0
        L_0x01fd:
            int r3 = r20.size()
            if (r2 >= r3) goto L_0x025e
            java.lang.Object r3 = r7.get(r2)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            boolean r4 = r3.isFromUser()
            if (r4 == 0) goto L_0x0216
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id
            long r3 = r3.user_id
            goto L_0x024a
        L_0x0216:
            int r4 = r3.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            long r8 = r5.channel_id
            java.lang.Long r5 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r5 == 0) goto L_0x0243
            boolean r4 = r4.megagroup
            if (r4 == 0) goto L_0x0243
            boolean r4 = r3.isForwardedChannelPost()
            if (r4 == 0) goto L_0x0243
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id
            long r3 = r3.channel_id
            goto L_0x0249
        L_0x0243:
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            long r3 = r3.channel_id
        L_0x0249:
            long r3 = -r3
        L_0x024a:
            java.lang.Long r5 = java.lang.Long.valueOf(r3)
            boolean r5 = r0.contains(r5)
            if (r5 != 0) goto L_0x025b
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r0.add(r3)
        L_0x025b:
            int r2 = r2 + 1
            goto L_0x01fd
        L_0x025e:
            int r0 = r0.size()
            int r2 = r17.size()
            int r0 = r0 + r2
            if (r0 <= r11) goto L_0x026b
            r6.multiplyUsers = r11
        L_0x026b:
            r8 = 0
        L_0x026c:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r0 = r6.groupedMessagesMap
            int r0 = r0.size()
            if (r8 >= r0) goto L_0x0282
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r0 = r6.groupedMessagesMap
            java.lang.Object r0 = r0.valueAt(r8)
            org.telegram.messenger.MessageObject$GroupedMessages r0 = (org.telegram.messenger.MessageObject.GroupedMessages) r0
            r0.calculate()
            int r8 = r8 + 1
            goto L_0x026c
        L_0x0282:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ForwardingMessagesParams.<init>(java.util.ArrayList, long):void");
    }

    public void getSelectedMessages(ArrayList<MessageObject> arrayList) {
        arrayList.clear();
        for (int i = 0; i < this.messages.size(); i++) {
            MessageObject messageObject = this.messages.get(i);
            if (this.selectedIds.get(messageObject.getId(), false)) {
                arrayList.add(messageObject);
            }
        }
    }

    public class PreviewMediaPoll extends TLRPC$TL_messageMediaPoll {
        public int totalVotersCached;

        public PreviewMediaPoll() {
        }
    }
}

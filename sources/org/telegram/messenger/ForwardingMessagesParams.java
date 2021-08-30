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
    public boolean hideCaption;
    public boolean hideForwardSendersName;
    public boolean isSecret;
    public ArrayList<MessageObject> messages;
    public boolean multiplyUsers;
    public ArrayList<TLRPC$TL_pollAnswerVoters> pollChoosenAnswers = new ArrayList<>();
    public ArrayList<MessageObject> previewMessages = new ArrayList<>();
    public SparseBooleanArray selectedIds = new SparseBooleanArray();
    public boolean willSeeSenders;

    /* JADX WARNING: Removed duplicated region for block: B:30:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x013e  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01b3 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ForwardingMessagesParams(java.util.ArrayList<org.telegram.messenger.MessageObject> r18, long r19) {
        /*
            r17 = this;
            r6 = r17
            r7 = r18
            r17.<init>()
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
            boolean r0 = org.telegram.messenger.DialogObject.isSecretDialogId(r19)
            r6.isSecret = r0
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            r10 = 0
        L_0x0036:
            int r0 = r18.size()
            r11 = 1
            if (r10 >= r0) goto L_0x01b8
            java.lang.Object r0 = r7.get(r10)
            r12 = r0
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            java.lang.CharSequence r0 = r12.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x004e
            r6.hasCaption = r11
        L_0x004e:
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
            r3.out = r11
            r3.unread = r8
            int r1 = r0.via_bot_id
            r3.via_bot_id = r1
            org.telegram.tgnet.TLRPC$ReplyMarkup r1 = r0.reply_markup
            r3.reply_markup = r1
            boolean r1 = r0.post
            r3.post = r1
            boolean r0 = r0.legacy
            r3.legacy = r0
            int r0 = r12.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            boolean r1 = r6.isSecret
            r13 = 0
            if (r1 != 0) goto L_0x00e6
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r1.fwd_from
            if (r2 == 0) goto L_0x00c0
            boolean r0 = r12.isDice()
            if (r0 != 0) goto L_0x00ac
            r6.hasSenders = r11
            goto L_0x00ae
        L_0x00ac:
            r6.willSeeSenders = r11
        L_0x00ae:
            org.telegram.tgnet.TLRPC$Peer r0 = r2.from_id
            if (r0 != 0) goto L_0x00e7
            java.lang.String r0 = r2.from_name
            boolean r0 = r9.contains(r0)
            if (r0 != 0) goto L_0x00e7
            java.lang.String r0 = r2.from_name
            r9.add(r0)
            goto L_0x00e7
        L_0x00c0:
            org.telegram.tgnet.TLRPC$Peer r2 = r1.from_id
            int r2 = r2.user_id
            if (r2 == 0) goto L_0x00cf
            long r4 = r1.dialog_id
            long r14 = (long) r0
            int r1 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r1 != 0) goto L_0x00cf
            if (r2 == r0) goto L_0x00e6
        L_0x00cf:
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r2 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r2.<init>()
            org.telegram.tgnet.TLRPC$Message r0 = r12.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.from_id
            r2.from_id = r0
            boolean r0 = r12.isDice()
            if (r0 != 0) goto L_0x00e3
            r6.hasSenders = r11
            goto L_0x00e7
        L_0x00e3:
            r6.willSeeSenders = r11
            goto L_0x00e7
        L_0x00e6:
            r2 = r13
        L_0x00e7:
            if (r2 == 0) goto L_0x00f1
            r3.fwd_from = r2
            int r0 = r3.flags
            r0 = r0 | 4
            r3.flags = r0
        L_0x00f1:
            r14 = r19
            r3.dialog_id = r14
            org.telegram.messenger.ForwardingMessagesParams$1 r5 = new org.telegram.messenger.ForwardingMessagesParams$1
            int r2 = r12.currentAccount
            r4 = 1
            r16 = 0
            r0 = r5
            r1 = r17
            r8 = r5
            r5 = r16
            r0.<init>(r2, r3, r4, r5)
            r8.preview = r11
            long r0 = r8.getGroupId()
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0132
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r0 = r6.groupedMessagesMap
            long r1 = r8.getGroupId()
            java.lang.Object r0 = r0.get(r1, r13)
            org.telegram.messenger.MessageObject$GroupedMessages r0 = (org.telegram.messenger.MessageObject.GroupedMessages) r0
            if (r0 != 0) goto L_0x012d
            org.telegram.messenger.MessageObject$GroupedMessages r0 = new org.telegram.messenger.MessageObject$GroupedMessages
            r0.<init>()
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r1 = r6.groupedMessagesMap
            long r2 = r8.getGroupId()
            r1.put(r2, r0)
        L_0x012d:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r0.messages
            r0.add(r8)
        L_0x0132:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r6.previewMessages
            r1 = 0
            r0.add(r1, r8)
            boolean r0 = r12.isPoll()
            if (r0 == 0) goto L_0x01b3
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
            org.telegram.tgnet.TLRPC$Message r3 = r8.messageOwner
            r3.media = r2
            boolean r3 = r12.canUnvote()
            if (r3 == 0) goto L_0x01b3
            org.telegram.tgnet.TLRPC$PollResults r3 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r3 = r3.results
            int r3 = r3.size()
            r4 = 0
        L_0x0173:
            if (r4 >= r3) goto L_0x01b3
            org.telegram.tgnet.TLRPC$PollResults r5 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r5 = r5.results
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r5 = (org.telegram.tgnet.TLRPC$TL_pollAnswerVoters) r5
            boolean r8 = r5.chosen
            if (r8 == 0) goto L_0x01a9
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r8 = new org.telegram.tgnet.TLRPC$TL_pollAnswerVoters
            r8.<init>()
            boolean r11 = r5.chosen
            r8.chosen = r11
            boolean r11 = r5.correct
            r8.correct = r11
            int r11 = r5.flags
            r8.flags = r11
            byte[] r11 = r5.option
            r8.option = r11
            int r5 = r5.voters
            r8.voters = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r5 = r6.pollChoosenAnswers
            r5.add(r8)
            org.telegram.tgnet.TLRPC$PollResults r5 = r2.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r5 = r5.results
            r5.add(r8)
            goto L_0x01b0
        L_0x01a9:
            org.telegram.tgnet.TLRPC$PollResults r8 = r2.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r8 = r8.results
            r8.add(r5)
        L_0x01b0:
            int r4 = r4 + 1
            goto L_0x0173
        L_0x01b3:
            int r10 = r10 + 1
            r8 = 0
            goto L_0x0036
        L_0x01b8:
            r1 = 0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r2 = 1
        L_0x01bf:
            int r3 = r18.size()
            if (r2 >= r3) goto L_0x0220
            java.lang.Object r3 = r7.get(r2)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            boolean r4 = r3.isFromUser()
            if (r4 == 0) goto L_0x01d8
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id
            int r3 = r3.user_id
            goto L_0x020c
        L_0x01d8:
            int r4 = r3.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            int r5 = r5.channel_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r5 == 0) goto L_0x0205
            boolean r4 = r4.megagroup
            if (r4 == 0) goto L_0x0205
            boolean r4 = r3.isForwardedChannelPost()
            if (r4 == 0) goto L_0x0205
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id
            int r3 = r3.channel_id
            goto L_0x020b
        L_0x0205:
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            int r3 = r3.channel_id
        L_0x020b:
            int r3 = -r3
        L_0x020c:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            boolean r4 = r0.contains(r4)
            if (r4 != 0) goto L_0x021d
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r0.add(r3)
        L_0x021d:
            int r2 = r2 + 1
            goto L_0x01bf
        L_0x0220:
            int r0 = r0.size()
            int r2 = r9.size()
            int r0 = r0 + r2
            if (r0 <= r11) goto L_0x022d
            r6.multiplyUsers = r11
        L_0x022d:
            r8 = 0
        L_0x022e:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r0 = r6.groupedMessagesMap
            int r0 = r0.size()
            if (r8 >= r0) goto L_0x0244
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r0 = r6.groupedMessagesMap
            java.lang.Object r0 = r0.valueAt(r8)
            org.telegram.messenger.MessageObject$GroupedMessages r0 = (org.telegram.messenger.MessageObject.GroupedMessages) r0
            r0.calculate()
            int r8 = r8 + 1
            goto L_0x022e
        L_0x0244:
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

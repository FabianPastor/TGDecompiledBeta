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
    public ArrayList<TLRPC$TL_pollAnswerVoters> pollChoosenAnswers = new ArrayList<>();
    public ArrayList<MessageObject> previewMessages = new ArrayList<>();
    public SparseBooleanArray selectedIds = new SparseBooleanArray();

    /* JADX WARNING: Removed duplicated region for block: B:23:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0192 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ForwardingMessagesParams(java.util.ArrayList<org.telegram.messenger.MessageObject> r17, long r18) {
        /*
            r16 = this;
            r6 = r16
            r7 = r17
            r16.<init>()
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
            boolean r0 = org.telegram.messenger.DialogObject.isSecretDialogId(r18)
            r6.isSecret = r0
            r9 = 0
        L_0x0031:
            int r0 = r17.size()
            if (r9 >= r0) goto L_0x0196
            java.lang.Object r0 = r7.get(r9)
            r10 = r0
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            java.lang.CharSequence r0 = r10.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r11 = 1
            if (r0 != 0) goto L_0x0049
            r6.hasCaption = r11
        L_0x0049:
            android.util.SparseBooleanArray r0 = r6.selectedIds
            int r1 = r10.getId()
            r0.put(r1, r11)
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message
            r3.<init>()
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
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
            int r0 = r10.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            boolean r1 = r6.isSecret
            r12 = 0
            if (r1 != 0) goto L_0x00ca
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r1.fwd_from
            if (r2 == 0) goto L_0x00a7
            boolean r0 = r10.isDice()
            if (r0 != 0) goto L_0x00cb
            r6.hasSenders = r11
            goto L_0x00cb
        L_0x00a7:
            org.telegram.tgnet.TLRPC$Peer r2 = r1.from_id
            int r2 = r2.user_id
            if (r2 == 0) goto L_0x00b6
            long r4 = r1.dialog_id
            long r13 = (long) r0
            int r1 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r1 != 0) goto L_0x00b6
            if (r2 == r0) goto L_0x00ca
        L_0x00b6:
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r2 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r2.<init>()
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.from_id
            r2.from_id = r0
            boolean r0 = r10.isDice()
            if (r0 != 0) goto L_0x00cb
            r6.hasSenders = r11
            goto L_0x00cb
        L_0x00ca:
            r2 = r12
        L_0x00cb:
            if (r2 == 0) goto L_0x00d5
            r3.fwd_from = r2
            int r0 = r3.flags
            r0 = r0 | 4
            r3.flags = r0
        L_0x00d5:
            r13 = r18
            r3.dialog_id = r13
            org.telegram.messenger.ForwardingMessagesParams$1 r15 = new org.telegram.messenger.ForwardingMessagesParams$1
            int r2 = r10.currentAccount
            r4 = 1
            r5 = 0
            r0 = r15
            r1 = r16
            r0.<init>(r2, r3, r4, r5)
            r15.preview = r11
            long r0 = r15.getGroupId()
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0112
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r0 = r6.groupedMessagesMap
            long r1 = r15.getGroupId()
            java.lang.Object r0 = r0.get(r1, r12)
            org.telegram.messenger.MessageObject$GroupedMessages r0 = (org.telegram.messenger.MessageObject.GroupedMessages) r0
            if (r0 != 0) goto L_0x010d
            org.telegram.messenger.MessageObject$GroupedMessages r0 = new org.telegram.messenger.MessageObject$GroupedMessages
            r0.<init>()
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r1 = r6.groupedMessagesMap
            long r2 = r15.getGroupId()
            r1.put(r2, r0)
        L_0x010d:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r0.messages
            r0.add(r15)
        L_0x0112:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r6.previewMessages
            r0.add(r8, r15)
            boolean r0 = r10.isPoll()
            if (r0 == 0) goto L_0x0192
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            org.telegram.messenger.ForwardingMessagesParams$PreviewMediaPoll r1 = new org.telegram.messenger.ForwardingMessagesParams$PreviewMediaPoll
            r1.<init>()
            org.telegram.tgnet.TLRPC$Poll r2 = r0.poll
            r1.poll = r2
            java.lang.String r2 = r0.provider
            r1.provider = r2
            org.telegram.tgnet.TLRPC$TL_pollResults r2 = new org.telegram.tgnet.TLRPC$TL_pollResults
            r2.<init>()
            r1.results = r2
            org.telegram.tgnet.TLRPC$PollResults r3 = r0.results
            int r3 = r3.total_voters
            r2.total_voters = r3
            r1.totalVotersCached = r3
            org.telegram.tgnet.TLRPC$Message r2 = r15.messageOwner
            r2.media = r1
            boolean r2 = r10.canUnvote()
            if (r2 == 0) goto L_0x0192
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            int r2 = r2.size()
            r3 = 0
        L_0x0152:
            if (r3 >= r2) goto L_0x0192
            org.telegram.tgnet.TLRPC$PollResults r4 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r4.results
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r4 = (org.telegram.tgnet.TLRPC$TL_pollAnswerVoters) r4
            boolean r5 = r4.chosen
            if (r5 == 0) goto L_0x0188
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r5 = new org.telegram.tgnet.TLRPC$TL_pollAnswerVoters
            r5.<init>()
            boolean r10 = r4.chosen
            r5.chosen = r10
            boolean r10 = r4.correct
            r5.correct = r10
            int r10 = r4.flags
            r5.flags = r10
            byte[] r10 = r4.option
            r5.option = r10
            int r4 = r4.voters
            r5.voters = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r6.pollChoosenAnswers
            r4.add(r5)
            org.telegram.tgnet.TLRPC$PollResults r4 = r1.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r4.results
            r4.add(r5)
            goto L_0x018f
        L_0x0188:
            org.telegram.tgnet.TLRPC$PollResults r5 = r1.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r5 = r5.results
            r5.add(r4)
        L_0x018f:
            int r3 = r3 + 1
            goto L_0x0152
        L_0x0192:
            int r9 = r9 + 1
            goto L_0x0031
        L_0x0196:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r0 = r6.groupedMessagesMap
            int r0 = r0.size()
            if (r8 >= r0) goto L_0x01ac
            android.util.LongSparseArray<org.telegram.messenger.MessageObject$GroupedMessages> r0 = r6.groupedMessagesMap
            java.lang.Object r0 = r0.valueAt(r8)
            org.telegram.messenger.MessageObject$GroupedMessages r0 = (org.telegram.messenger.MessageObject.GroupedMessages) r0
            r0.calculate()
            int r8 = r8 + 1
            goto L_0x0196
        L_0x01ac:
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

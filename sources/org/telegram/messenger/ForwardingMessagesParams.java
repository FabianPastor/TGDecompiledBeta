package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

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
    public ArrayList<TLRPC.TL_pollAnswerVoters> pollChoosenAnswers = new ArrayList<>();
    public ArrayList<MessageObject> previewMessages = new ArrayList<>();
    public SparseBooleanArray selectedIds = new SparseBooleanArray();
    public boolean willSeeSenders;

    public ForwardingMessagesParams(ArrayList<MessageObject> messages2, long newDialogId) {
        long uid;
        ArrayList<MessageObject> arrayList = messages2;
        this.messages = arrayList;
        boolean z = false;
        this.hasCaption = false;
        this.hasSenders = false;
        this.isSecret = DialogObject.isEncryptedDialog(newDialogId);
        this.hasSpoilers = false;
        ArrayList arrayList2 = new ArrayList();
        int i = 0;
        while (i < messages2.size()) {
            MessageObject messageObject = arrayList.get(i);
            if (!TextUtils.isEmpty(messageObject.caption)) {
                this.hasCaption = true;
            }
            this.selectedIds.put(messageObject.getId(), true);
            TLRPC.Message message = new TLRPC.TL_message();
            message.id = messageObject.messageOwner.id;
            message.grouped_id = messageObject.messageOwner.grouped_id;
            message.peer_id = messageObject.messageOwner.peer_id;
            message.from_id = messageObject.messageOwner.from_id;
            message.message = messageObject.messageOwner.message;
            message.media = messageObject.messageOwner.media;
            message.action = messageObject.messageOwner.action;
            message.edit_date = z ? 1 : 0;
            if (messageObject.messageOwner.entities != null) {
                message.entities.addAll(messageObject.messageOwner.entities);
                if (!this.hasSpoilers) {
                    Iterator<TLRPC.MessageEntity> it = message.entities.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (it.next() instanceof TLRPC.TL_messageEntitySpoiler) {
                                this.hasSpoilers = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            message.out = true;
            message.unread = z;
            message.via_bot_id = messageObject.messageOwner.via_bot_id;
            message.reply_markup = messageObject.messageOwner.reply_markup;
            message.post = messageObject.messageOwner.post;
            message.legacy = messageObject.messageOwner.legacy;
            message.restriction_reason = messageObject.messageOwner.restriction_reason;
            message.replyMessage = messageObject.messageOwner.replyMessage;
            TLRPC.MessageFwdHeader header = null;
            long clientUserId = UserConfig.getInstance(messageObject.currentAccount).clientUserId;
            if (!this.isSecret) {
                if (messageObject.messageOwner.fwd_from != null) {
                    header = messageObject.messageOwner.fwd_from;
                    if (!messageObject.isDice()) {
                        this.hasSenders = true;
                    } else {
                        this.willSeeSenders = true;
                    }
                    if (header.from_id == null && !arrayList2.contains(header.from_name)) {
                        arrayList2.add(header.from_name);
                    }
                } else if (!(messageObject.messageOwner.from_id.user_id != 0 && messageObject.messageOwner.dialog_id == clientUserId && messageObject.messageOwner.from_id.user_id == clientUserId)) {
                    header = new TLRPC.TL_messageFwdHeader();
                    header.from_id = messageObject.messageOwner.from_id;
                    if (!messageObject.isDice()) {
                        this.hasSenders = true;
                    } else {
                        this.willSeeSenders = true;
                    }
                }
            }
            TLRPC.MessageFwdHeader header2 = header;
            if (header2 != null) {
                message.fwd_from = header2;
                message.flags |= 4;
            }
            message.dialog_id = newDialogId;
            TLRPC.MessageFwdHeader messageFwdHeader = header2;
            MessageObject previewMessage = new MessageObject(messageObject.currentAccount, message, true, false) {
                public boolean needDrawForwarded() {
                    if (ForwardingMessagesParams.this.hideForwardSendersName) {
                        return false;
                    }
                    return super.needDrawForwarded();
                }
            };
            previewMessage.preview = true;
            if (previewMessage.getGroupId() != 0) {
                MessageObject.GroupedMessages groupedMessages = this.groupedMessagesMap.get(previewMessage.getGroupId(), (Object) null);
                if (groupedMessages == null) {
                    groupedMessages = new MessageObject.GroupedMessages();
                    this.groupedMessagesMap.put(previewMessage.getGroupId(), groupedMessages);
                }
                groupedMessages.messages.add(previewMessage);
            }
            this.previewMessages.add(z, previewMessage);
            if (messageObject.isPoll()) {
                TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) messageObject.messageOwner.media;
                PreviewMediaPoll newMediaPoll = new PreviewMediaPoll();
                newMediaPoll.poll = mediaPoll.poll;
                newMediaPoll.provider = mediaPoll.provider;
                newMediaPoll.results = new TLRPC.TL_pollResults();
                TLRPC.PollResults pollResults = newMediaPoll.results;
                int i2 = mediaPoll.results.total_voters;
                pollResults.total_voters = i2;
                newMediaPoll.totalVotersCached = i2;
                previewMessage.messageOwner.media = newMediaPoll;
                if (messageObject.canUnvote()) {
                    int N = mediaPoll.results.results.size();
                    for (int a = 0; a < N; a++) {
                        TLRPC.TL_pollAnswerVoters answer = mediaPoll.results.results.get(a);
                        if (answer.chosen) {
                            TLRPC.TL_pollAnswerVoters newAnswer = new TLRPC.TL_pollAnswerVoters();
                            newAnswer.chosen = answer.chosen;
                            newAnswer.correct = answer.correct;
                            newAnswer.flags = answer.flags;
                            newAnswer.option = answer.option;
                            newAnswer.voters = answer.voters;
                            this.pollChoosenAnswers.add(newAnswer);
                            newMediaPoll.results.results.add(newAnswer);
                        } else {
                            newMediaPoll.results.results.add(answer);
                        }
                    }
                }
            }
            i++;
            z = false;
        }
        ArrayList<Long> uids = new ArrayList<>();
        for (int a2 = 0; a2 < messages2.size(); a2++) {
            MessageObject object = arrayList.get(a2);
            if (object.isFromUser()) {
                uid = object.messageOwner.from_id.user_id;
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(object.currentAccount).getChat(Long.valueOf(object.messageOwner.peer_id.channel_id));
                if (!ChatObject.isChannel(chat) || !chat.megagroup || !object.isForwardedChannelPost()) {
                    uid = -object.messageOwner.peer_id.channel_id;
                } else {
                    uid = -object.messageOwner.fwd_from.from_id.channel_id;
                }
            }
            if (!uids.contains(Long.valueOf(uid))) {
                uids.add(Long.valueOf(uid));
            }
        }
        if (uids.size() + arrayList2.size() > 1) {
            this.multiplyUsers = true;
        }
        for (int i3 = 0; i3 < this.groupedMessagesMap.size(); i3++) {
            this.groupedMessagesMap.valueAt(i3).calculate();
        }
    }

    public void getSelectedMessages(ArrayList<MessageObject> messagesToForward) {
        messagesToForward.clear();
        for (int i = 0; i < this.messages.size(); i++) {
            MessageObject messageObject = this.messages.get(i);
            if (this.selectedIds.get(messageObject.getId(), false)) {
                messagesToForward.add(messageObject);
            }
        }
    }

    public class PreviewMediaPoll extends TLRPC.TL_messageMediaPoll {
        public int totalVotersCached;

        public PreviewMediaPoll() {
        }
    }
}

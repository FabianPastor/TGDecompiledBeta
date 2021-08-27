package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageFwdHeader;

public class ForwardingMessagesParams {
    public LongSparseArray<MessageObject.GroupedMessages> groupedMessagesMap = new LongSparseArray<>();
    public boolean hasCaption;
    public boolean hideCaption;
    public boolean hideForwardSendersName;
    public ArrayList<MessageObject> messages;
    public ArrayList<MessageObject> previewMessages = new ArrayList<>();
    public SparseBooleanArray selectedIds = new SparseBooleanArray();

    public ForwardingMessagesParams(ArrayList<MessageObject> arrayList) {
        TLRPC$Peer tLRPC$Peer;
        this.messages = arrayList;
        this.hasCaption = false;
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject messageObject = arrayList.get(i);
            if (!TextUtils.isEmpty(messageObject.caption)) {
                this.hasCaption = true;
            }
            this.selectedIds.put(messageObject.getId(), true);
            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            tLRPC$TL_message.id = tLRPC$Message.id;
            tLRPC$TL_message.grouped_id = tLRPC$Message.grouped_id;
            tLRPC$TL_message.peer_id = tLRPC$Message.peer_id;
            tLRPC$TL_message.from_id = tLRPC$Message.from_id;
            tLRPC$TL_message.message = tLRPC$Message.message;
            tLRPC$TL_message.media = tLRPC$Message.media;
            tLRPC$TL_message.action = tLRPC$Message.action;
            tLRPC$TL_message.flags = tLRPC$Message.flags;
            tLRPC$TL_message.out = true;
            tLRPC$TL_message.unread = false;
            tLRPC$TL_message.via_bot_id = tLRPC$Message.via_bot_id;
            tLRPC$TL_message.reply_markup = tLRPC$Message.reply_markup;
            tLRPC$TL_message.post = tLRPC$Message.post;
            tLRPC$TL_message.legacy = tLRPC$Message.legacy;
            TLRPC$TL_messageFwdHeader tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader();
            TLRPC$Message tLRPC$Message2 = messageObject.messageOwner;
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = tLRPC$Message2.fwd_from;
            if (tLRPC$MessageFwdHeader == null || (tLRPC$Peer = tLRPC$MessageFwdHeader.from_id) == null) {
                tLRPC$TL_messageFwdHeader.from_id = tLRPC$Message2.from_id;
            } else {
                tLRPC$TL_messageFwdHeader.from_id = tLRPC$Peer;
            }
            tLRPC$TL_message.fwd_from = tLRPC$TL_messageFwdHeader;
            tLRPC$TL_message.flags |= 4;
            AnonymousClass1 r6 = new MessageObject(messageObject.currentAccount, tLRPC$TL_message, true, false) {
                public boolean needDrawForwarded() {
                    if (ForwardingMessagesParams.this.hideForwardSendersName) {
                        return false;
                    }
                    return super.needDrawForwarded();
                }
            };
            r6.preview = true;
            if (r6.getGroupId() != 0) {
                MessageObject.GroupedMessages groupedMessages = this.groupedMessagesMap.get(r6.getGroupId(), (Object) null);
                if (groupedMessages == null) {
                    groupedMessages = new MessageObject.GroupedMessages();
                    this.groupedMessagesMap.put(r6.getGroupId(), groupedMessages);
                }
                groupedMessages.messages.add(r6);
            }
            this.previewMessages.add(0, r6);
        }
        for (int i2 = 0; i2 < this.groupedMessagesMap.size(); i2++) {
            this.groupedMessagesMap.valueAt(i2).calculate();
        }
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
}

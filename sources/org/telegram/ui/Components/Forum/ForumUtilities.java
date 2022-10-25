package org.telegram.ui.Components.Forum;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import java.util.ArrayList;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ForumTopic;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LetterDrawable;
/* loaded from: classes3.dex */
public class ForumUtilities {
    public static void setTopicIcon(BackupImageView backupImageView, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        setTopicIcon(backupImageView, tLRPC$TL_forumTopic, false);
    }

    public static void setTopicIcon(BackupImageView backupImageView, TLRPC$TL_forumTopic tLRPC$TL_forumTopic, boolean z) {
        if (tLRPC$TL_forumTopic == null || backupImageView == null) {
            return;
        }
        if (tLRPC$TL_forumTopic.icon_emoji_id != 0) {
            backupImageView.setImageDrawable(null);
            backupImageView.setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(10, UserConfig.selectedAccount, tLRPC$TL_forumTopic.icon_emoji_id));
            return;
        }
        backupImageView.setAnimatedEmojiDrawable(null);
        backupImageView.setImageDrawable(createTopicDrawable(tLRPC$TL_forumTopic));
    }

    public static Drawable createTopicDrawable(TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        if (tLRPC$TL_forumTopic == null) {
            return null;
        }
        return createTopicDrawable(tLRPC$TL_forumTopic.title, tLRPC$TL_forumTopic.icon_color);
    }

    public static Drawable createTopicDrawable(String str, int i) {
        ForumBubbleDrawable forumBubbleDrawable = new ForumBubbleDrawable(i);
        LetterDrawable letterDrawable = new LetterDrawable(null, 1);
        String upperCase = str.trim().toUpperCase();
        letterDrawable.setTitle(upperCase.length() >= 1 ? upperCase.substring(0, 1) : "");
        CombinedDrawable combinedDrawable = new CombinedDrawable(forumBubbleDrawable, letterDrawable, 0, 0);
        combinedDrawable.setFullsize(true);
        return combinedDrawable;
    }

    public static void openTopic(BaseFragment baseFragment, long j, TLRPC$TL_forumTopic tLRPC$TL_forumTopic, int i) {
        TLRPC$TL_forumTopic tLRPC$TL_forumTopic2;
        TLRPC$TL_forumTopic findTopic;
        if (baseFragment == null) {
            return;
        }
        TLRPC$Chat chat = baseFragment.getMessagesController().getChat(Long.valueOf(j));
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", j);
        if (i != 0) {
            bundle.putInt("message_id", i);
        }
        bundle.putInt("unread_count", tLRPC$TL_forumTopic.unread_count);
        bundle.putBoolean("historyPreloaded", false);
        ChatActivity chatActivity = new ChatActivity(bundle);
        TLRPC$Message tLRPC$Message = tLRPC$TL_forumTopic.topicStartMessage;
        if (tLRPC$Message != null || (findTopic = baseFragment.getMessagesController().getTopicsController().findTopic(j, tLRPC$TL_forumTopic.id)) == null) {
            tLRPC$TL_forumTopic2 = tLRPC$TL_forumTopic;
        } else {
            tLRPC$Message = findTopic.topicStartMessage;
            tLRPC$TL_forumTopic2 = findTopic;
        }
        if (tLRPC$Message == null) {
            return;
        }
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(new MessageObject(baseFragment.getCurrentAccount(), tLRPC$Message, false, false));
        chatActivity.setThreadMessages(arrayList, chat, tLRPC$TL_forumTopic2.id, tLRPC$TL_forumTopic2.read_inbox_max_id, tLRPC$TL_forumTopic2.read_outbox_max_id, tLRPC$TL_forumTopic2);
        if (i != 0) {
            chatActivity.highlightMessageId = i;
        }
        baseFragment.presentFragment(chatActivity);
    }

    public static CharSequence getForumSpannedName(TLRPC$ForumTopic tLRPC$ForumTopic, TextPaint textPaint) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (tLRPC$ForumTopic instanceof TLRPC$TL_forumTopic) {
            TLRPC$TL_forumTopic tLRPC$TL_forumTopic = (TLRPC$TL_forumTopic) tLRPC$ForumTopic;
            if (tLRPC$TL_forumTopic.icon_emoji_id != 0) {
                spannableStringBuilder.append((CharSequence) " ");
                AnimatedEmojiSpan animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$TL_forumTopic.icon_emoji_id, 0.95f, textPaint == null ? null : textPaint.getFontMetricsInt());
                spannableStringBuilder.setSpan(animatedEmojiSpan, 0, 1, 33);
                animatedEmojiSpan.cacheType = 7;
            } else {
                spannableStringBuilder.append((CharSequence) " ");
                Drawable createTopicDrawable = createTopicDrawable(tLRPC$TL_forumTopic);
                createTopicDrawable.setBounds(0, 0, (int) (createTopicDrawable.getIntrinsicWidth() * 0.65f), (int) (createTopicDrawable.getIntrinsicHeight() * 0.65f));
                if (createTopicDrawable instanceof CombinedDrawable) {
                    CombinedDrawable combinedDrawable = (CombinedDrawable) createTopicDrawable;
                    if (combinedDrawable.getIcon() instanceof LetterDrawable) {
                        ((LetterDrawable) combinedDrawable.getIcon()).scale = 0.7f;
                    }
                }
                spannableStringBuilder.setSpan(new ImageSpan(createTopicDrawable, 2), 0, 1, 33);
            }
            if (!TextUtils.isEmpty(tLRPC$TL_forumTopic.title)) {
                spannableStringBuilder.append((CharSequence) " ");
                spannableStringBuilder.append((CharSequence) tLRPC$TL_forumTopic.title);
            }
            return spannableStringBuilder;
        }
        return "DELETED";
    }

    public static void applyTopic(ChatActivity chatActivity, MessagesStorage.TopicKey topicKey) {
        TLRPC$TL_forumTopic findTopic = chatActivity.getMessagesController().getTopicsController().findTopic(-topicKey.dialogId, topicKey.topicId);
        if (findTopic == null) {
            return;
        }
        TLRPC$Chat chat = chatActivity.getMessagesController().getChat(Long.valueOf(-topicKey.dialogId));
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(new MessageObject(chatActivity.getCurrentAccount(), findTopic.topicStartMessage, false, false));
        chatActivity.setThreadMessages(arrayList, chat, findTopic.id, findTopic.read_inbox_max_id, findTopic.read_outbox_max_id, findTopic);
    }
}

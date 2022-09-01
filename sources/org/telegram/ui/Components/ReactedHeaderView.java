package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messages_chatFull;
import org.telegram.tgnet.TLRPC$TL_messages_getFullChat;
import org.telegram.tgnet.TLRPC$TL_messages_getMessageReactionsList;
import org.telegram.tgnet.TLRPC$TL_messages_getMessageReadParticipants;
import org.telegram.tgnet.TLRPC$TL_messages_messageReactionsList;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.Theme;

public class ReactedHeaderView extends FrameLayout {
    private AvatarsImageView avatarsImageView;
    private int currentAccount;
    private int fixedWidth;
    private FlickerLoadingView flickerLoadingView;
    private ImageView iconView;
    private boolean ignoreLayout;
    private boolean isLoaded;
    private MessageObject message;
    private BackupImageView reactView;
    private Consumer<List<TLRPC$User>> seenCallback;
    private List<TLRPC$User> seenUsers = new ArrayList();
    private TextView titleView;
    private List<TLRPC$User> users = new ArrayList();

    public ReactedHeaderView(Context context, int i, MessageObject messageObject, long j) {
        super(context);
        this.currentAccount = i;
        this.message = messageObject;
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setColors("actionBarDefaultSubmenuBackground", "listSelectorSDK21", (String) null);
        this.flickerLoadingView.setViewType(13);
        this.flickerLoadingView.setIsSingleCell(false);
        addView(this.flickerLoadingView, LayoutHelper.createFrame(-2, -1.0f));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        this.titleView.setTextSize(1, 16.0f);
        this.titleView.setLines(1);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.titleView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 40.0f, 0.0f, 62.0f, 0.0f));
        AvatarsImageView avatarsImageView2 = new AvatarsImageView(context, false);
        this.avatarsImageView = avatarsImageView2;
        avatarsImageView2.setStyle(11);
        addView(this.avatarsImageView, LayoutHelper.createFrameRelatively(56.0f, -1.0f, 8388629, 0.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        addView(imageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 11.0f, 0.0f, 0.0f, 0.0f));
        Drawable mutate = ContextCompat.getDrawable(context, R.drawable.msg_reactions).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultSubmenuItemIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconView.setImageDrawable(mutate);
        this.iconView.setVisibility(8);
        BackupImageView backupImageView = new BackupImageView(context);
        this.reactView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 11.0f, 0.0f, 0.0f, 0.0f));
        this.titleView.setAlpha(0.0f);
        this.avatarsImageView.setAlpha(0.0f);
        setBackground(Theme.getSelectorDrawable(false));
    }

    public void setSeenCallback(Consumer<List<TLRPC$User>> consumer) {
        this.seenCallback = consumer;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.isLoaded) {
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            TLRPC$Chat chat = instance.getChat(Long.valueOf(this.message.getChatId()));
            TLRPC$ChatFull chatFull = instance.getChatFull(this.message.getChatId());
            if (chat != null && this.message.isOutOwner() && this.message.isSent() && !this.message.isEditing() && !this.message.isSending() && !this.message.isSendError() && !this.message.isContentUnread() && !this.message.isUnread() && ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.message.messageOwner.date < 604800 && (ChatObject.isMegagroup(chat) || !ChatObject.isChannel(chat)) && chatFull != null && chatFull.participants_count <= MessagesController.getInstance(this.currentAccount).chatReadMarkSizeThreshold && !(this.message.messageOwner.action instanceof TLRPC$TL_messageActionChatJoinedByRequest)) {
                TLRPC$TL_messages_getMessageReadParticipants tLRPC$TL_messages_getMessageReadParticipants = new TLRPC$TL_messages_getMessageReadParticipants();
                tLRPC$TL_messages_getMessageReadParticipants.msg_id = this.message.getId();
                tLRPC$TL_messages_getMessageReadParticipants.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.message.getDialogId());
                TLRPC$Peer tLRPC$Peer = this.message.messageOwner.from_id;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getMessageReadParticipants, new ReactedHeaderView$$ExternalSyntheticLambda5(this, tLRPC$Peer != null ? tLRPC$Peer.user_id : 0, chat), 64);
                return;
            }
            loadReactions();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachedToWindow$5(long j, TLRPC$Chat tLRPC$Chat, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$Vector) {
            ArrayList arrayList = new ArrayList();
            Iterator<Object> it = ((TLRPC$Vector) tLObject).objects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (next instanceof Long) {
                    long longValue = ((Long) next).longValue();
                    if (j != longValue) {
                        arrayList.add(Long.valueOf(longValue));
                    }
                }
            }
            arrayList.add(Long.valueOf(j));
            ArrayList arrayList2 = new ArrayList();
            ReactedHeaderView$$ExternalSyntheticLambda1 reactedHeaderView$$ExternalSyntheticLambda1 = new ReactedHeaderView$$ExternalSyntheticLambda1(this, arrayList2);
            if (ChatObject.isChannel(tLRPC$Chat)) {
                TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
                tLRPC$TL_channels_getParticipants.limit = MessagesController.getInstance(this.currentAccount).chatReadMarkSizeThreshold;
                tLRPC$TL_channels_getParticipants.offset = 0;
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
                tLRPC$TL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(tLRPC$Chat.id);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getParticipants, new ReactedHeaderView$$ExternalSyntheticLambda7(this, arrayList, arrayList2, reactedHeaderView$$ExternalSyntheticLambda1));
                return;
            }
            TLRPC$TL_messages_getFullChat tLRPC$TL_messages_getFullChat = new TLRPC$TL_messages_getFullChat();
            tLRPC$TL_messages_getFullChat.chat_id = tLRPC$Chat.id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getFullChat, new ReactedHeaderView$$ExternalSyntheticLambda6(this, arrayList, arrayList2, reactedHeaderView$$ExternalSyntheticLambda1));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachedToWindow$0(List list) {
        this.seenUsers.addAll(list);
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TLRPC$User tLRPC$User = (TLRPC$User) it.next();
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= this.users.size()) {
                    break;
                } else if (this.users.get(i).id == tLRPC$User.id) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                this.users.add(tLRPC$User);
            }
        }
        Consumer<List<TLRPC$User>> consumer = this.seenCallback;
        if (consumer != null) {
            consumer.accept(list);
        }
        loadReactions();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachedToWindow$2(List list, List list2, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ReactedHeaderView$$ExternalSyntheticLambda2(this, tLObject, list, list2, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachedToWindow$1(TLObject tLObject, List list, List list2, Runnable runnable) {
        if (tLObject != null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            for (int i = 0; i < tLRPC$TL_channels_channelParticipants.users.size(); i++) {
                TLRPC$User tLRPC$User = tLRPC$TL_channels_channelParticipants.users.get(i);
                MessagesController.getInstance(this.currentAccount).putUser(tLRPC$User, false);
                if (!tLRPC$User.self && list.contains(Long.valueOf(tLRPC$User.id))) {
                    list2.add(tLRPC$User);
                }
            }
        }
        runnable.run();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachedToWindow$4(List list, List list2, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ReactedHeaderView$$ExternalSyntheticLambda3(this, tLObject, list, list2, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachedToWindow$3(TLObject tLObject, List list, List list2, Runnable runnable) {
        if (tLObject != null) {
            TLRPC$TL_messages_chatFull tLRPC$TL_messages_chatFull = (TLRPC$TL_messages_chatFull) tLObject;
            for (int i = 0; i < tLRPC$TL_messages_chatFull.users.size(); i++) {
                TLRPC$User tLRPC$User = tLRPC$TL_messages_chatFull.users.get(i);
                MessagesController.getInstance(this.currentAccount).putUser(tLRPC$User, false);
                if (!tLRPC$User.self && list.contains(Long.valueOf(tLRPC$User.id))) {
                    list2.add(tLRPC$User);
                }
            }
        }
        runnable.run();
    }

    private void loadReactions() {
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        TLRPC$TL_messages_getMessageReactionsList tLRPC$TL_messages_getMessageReactionsList = new TLRPC$TL_messages_getMessageReactionsList();
        tLRPC$TL_messages_getMessageReactionsList.peer = instance.getInputPeer(this.message.getDialogId());
        tLRPC$TL_messages_getMessageReactionsList.id = this.message.getId();
        tLRPC$TL_messages_getMessageReactionsList.limit = 3;
        tLRPC$TL_messages_getMessageReactionsList.reaction = null;
        tLRPC$TL_messages_getMessageReactionsList.offset = null;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getMessageReactionsList, new ReactedHeaderView$$ExternalSyntheticLambda4(this), 64);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReactions$7(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_messages_messageReactionsList) {
            TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList = (TLRPC$TL_messages_messageReactionsList) tLObject;
            post(new ReactedHeaderView$$ExternalSyntheticLambda0(this, tLRPC$TL_messages_messageReactionsList.count, tLRPC$TL_messages_messageReactionsList));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReactions$6(int i, TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList) {
        String str;
        boolean z;
        boolean z2;
        String str2;
        if (this.seenUsers.isEmpty() || this.seenUsers.size() < i) {
            str = LocaleController.formatPluralString("ReactionsCount", i, new Object[0]);
        } else {
            if (i == this.seenUsers.size()) {
                str2 = String.valueOf(i);
            } else {
                str2 = i + "/" + this.seenUsers.size();
            }
            str = String.format(LocaleController.getPluralString("Reacted", i), new Object[]{str2});
        }
        if (getMeasuredWidth() > 0) {
            this.fixedWidth = getMeasuredWidth();
        }
        this.titleView.setText(str);
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions = this.message.messageOwner.reactions;
        if (tLRPC$TL_messageReactions != null && tLRPC$TL_messageReactions.results.size() == 1 && !tLRPC$TL_messages_messageReactionsList.reactions.isEmpty()) {
            Iterator<TLRPC$TL_availableReaction> it = MediaDataController.getInstance(this.currentAccount).getReactionsList().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TLRPC$TL_availableReaction next = it.next();
                if (next.reaction.equals(tLRPC$TL_messages_messageReactionsList.reactions.get(0).reaction)) {
                    this.reactView.setImage(ImageLocation.getForDocument(next.center_icon), "40_40_lastframe", "webp", (Drawable) null, (Object) next);
                    this.reactView.setVisibility(0);
                    this.reactView.setAlpha(0.0f);
                    this.reactView.animate().alpha(1.0f).start();
                    this.iconView.setVisibility(8);
                    z = false;
                    break;
                }
            }
        }
        z = true;
        if (z) {
            this.iconView.setVisibility(0);
            this.iconView.setAlpha(0.0f);
            this.iconView.animate().alpha(1.0f).start();
        }
        Iterator<TLRPC$User> it2 = tLRPC$TL_messages_messageReactionsList.users.iterator();
        while (it2.hasNext()) {
            TLRPC$User next2 = it2.next();
            TLRPC$Peer tLRPC$Peer = this.message.messageOwner.from_id;
            if (!(tLRPC$Peer == null || next2.id == tLRPC$Peer.user_id)) {
                int i2 = 0;
                while (true) {
                    if (i2 >= this.users.size()) {
                        z2 = false;
                        break;
                    } else if (this.users.get(i2).id == next2.id) {
                        z2 = true;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (!z2) {
                    this.users.add(next2);
                }
            }
        }
        updateView();
    }

    public List<TLRPC$User> getSeenUsers() {
        return this.seenUsers;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0059  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateView() {
        /*
            r6 = this;
            java.util.List<org.telegram.tgnet.TLRPC$User> r0 = r6.users
            int r0 = r0.size()
            r1 = 1
            r2 = 0
            if (r0 <= 0) goto L_0x000c
            r0 = 1
            goto L_0x000d
        L_0x000c:
            r0 = 0
        L_0x000d:
            r6.setEnabled(r0)
            r0 = 0
        L_0x0011:
            r3 = 3
            if (r0 >= r3) goto L_0x0037
            java.util.List<org.telegram.tgnet.TLRPC$User> r3 = r6.users
            int r3 = r3.size()
            if (r0 >= r3) goto L_0x002c
            org.telegram.ui.Components.AvatarsImageView r3 = r6.avatarsImageView
            int r4 = r6.currentAccount
            java.util.List<org.telegram.tgnet.TLRPC$User> r5 = r6.users
            java.lang.Object r5 = r5.get(r0)
            org.telegram.tgnet.TLObject r5 = (org.telegram.tgnet.TLObject) r5
            r3.setObject(r0, r4, r5)
            goto L_0x0034
        L_0x002c:
            org.telegram.ui.Components.AvatarsImageView r3 = r6.avatarsImageView
            int r4 = r6.currentAccount
            r5 = 0
            r3.setObject(r0, r4, r5)
        L_0x0034:
            int r0 = r0 + 1
            goto L_0x0011
        L_0x0037:
            java.util.List<org.telegram.tgnet.TLRPC$User> r0 = r6.users
            int r0 = r0.size()
            r3 = 0
            r4 = 1094713344(0x41400000, float:12.0)
            if (r0 == r1) goto L_0x004c
            r1 = 2
            if (r0 == r1) goto L_0x0047
            r0 = 0
            goto L_0x0053
        L_0x0047:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
            goto L_0x0052
        L_0x004c:
            r0 = 1103101952(0x41CLASSNAME, float:24.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
        L_0x0052:
            float r0 = (float) r0
        L_0x0053:
            org.telegram.ui.Components.AvatarsImageView r1 = r6.avatarsImageView
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x005e
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r0 = (float) r0
        L_0x005e:
            r1.setTranslationX(r0)
            org.telegram.ui.Components.AvatarsImageView r0 = r6.avatarsImageView
            r0.commitTransition(r2)
            android.widget.TextView r0 = r6.titleView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            r1 = 1065353216(0x3var_, float:1.0)
            android.view.ViewPropertyAnimator r0 = r0.alpha(r1)
            r4 = 220(0xdc, double:1.087E-321)
            android.view.ViewPropertyAnimator r0 = r0.setDuration(r4)
            r0.start()
            org.telegram.ui.Components.AvatarsImageView r0 = r6.avatarsImageView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            android.view.ViewPropertyAnimator r0 = r0.alpha(r1)
            android.view.ViewPropertyAnimator r0 = r0.setDuration(r4)
            r0.start()
            org.telegram.ui.Components.FlickerLoadingView r0 = r6.flickerLoadingView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            android.view.ViewPropertyAnimator r0 = r0.alpha(r3)
            android.view.ViewPropertyAnimator r0 = r0.setDuration(r4)
            org.telegram.ui.Components.HideViewAfterAnimation r1 = new org.telegram.ui.Components.HideViewAfterAnimation
            org.telegram.ui.Components.FlickerLoadingView r2 = r6.flickerLoadingView
            r1.<init>(r2)
            android.view.ViewPropertyAnimator r0 = r0.setListener(r1)
            r0.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReactedHeaderView.updateView():void");
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3 = this.fixedWidth;
        if (i3 > 0) {
            i = View.MeasureSpec.makeMeasureSpec(i3, NUM);
        }
        if (this.flickerLoadingView.getVisibility() == 0) {
            this.ignoreLayout = true;
            this.flickerLoadingView.setVisibility(8);
            super.onMeasure(i, i2);
            this.flickerLoadingView.getLayoutParams().width = getMeasuredWidth();
            this.flickerLoadingView.setVisibility(0);
            this.ignoreLayout = false;
            super.onMeasure(i, i2);
            return;
        }
        super.onMeasure(i, i2);
    }
}

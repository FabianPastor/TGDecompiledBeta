package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest;
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
    private long dialogId;
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
        this.dialogId = j;
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
        Drawable mutate = ContextCompat.getDrawable(context, NUM).mutate();
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
            if (chat != null && this.message.isOutOwner() && this.message.isSent() && !this.message.isEditing() && !this.message.isSending() && !this.message.isSendError() && !this.message.isContentUnread() && !this.message.isUnread() && ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.message.messageOwner.date < 604800 && (ChatObject.isMegagroup(chat) || !ChatObject.isChannel(chat)) && chatFull != null && chatFull.participants_count < MessagesController.getInstance(this.currentAccount).chatReadMarkSizeThreshold && !(this.message.messageOwner.action instanceof TLRPC$TL_messageActionChatJoinedByRequest)) {
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
        tLRPC$TL_messages_getMessageReactionsList.peer = instance.getInputPeer(this.dialogId);
        tLRPC$TL_messages_getMessageReactionsList.id = this.message.getId();
        tLRPC$TL_messages_getMessageReactionsList.limit = 3;
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
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00f2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadReactions$6(int r12, org.telegram.tgnet.TLRPC$TL_messages_messageReactionsList r13) {
        /*
            r11 = this;
            java.util.List<org.telegram.tgnet.TLRPC$User> r0 = r11.seenUsers
            boolean r0 = r0.isEmpty()
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x0049
            java.util.List<org.telegram.tgnet.TLRPC$User> r0 = r11.seenUsers
            int r0 = r0.size()
            if (r0 >= r12) goto L_0x0013
            goto L_0x0049
        L_0x0013:
            java.util.List<org.telegram.tgnet.TLRPC$User> r0 = r11.seenUsers
            int r0 = r0.size()
            if (r12 != r0) goto L_0x0020
            java.lang.String r0 = java.lang.String.valueOf(r12)
            goto L_0x003a
        L_0x0020:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r12)
            java.lang.String r3 = "/"
            r0.append(r3)
            java.util.List<org.telegram.tgnet.TLRPC$User> r3 = r11.seenUsers
            int r3 = r3.size()
            r0.append(r3)
            java.lang.String r0 = r0.toString()
        L_0x003a:
            java.lang.String r3 = "Reacted"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getPluralString(r3, r12)
            java.lang.Object[] r3 = new java.lang.Object[r1]
            r3[r2] = r0
            java.lang.String r12 = java.lang.String.format(r12, r3)
            goto L_0x004f
        L_0x0049:
            java.lang.String r0 = "ReactionsCount"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r0, r12)
        L_0x004f:
            android.widget.TextView r0 = r11.titleView
            r0.setText(r12)
            org.telegram.messenger.MessageObject r12 = r11.message
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r12 = r12.reactions
            r0 = 1065353216(0x3var_, float:1.0)
            r3 = 0
            if (r12 == 0) goto L_0x00cc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_reactionCount> r12 = r12.results
            int r12 = r12.size()
            if (r12 != r1) goto L_0x00cc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messagePeerReaction> r12 = r13.reactions
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x00cc
            int r12 = r11.currentAccount
            org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
            java.util.List r12 = r12.getReactionsList()
            java.util.Iterator r12 = r12.iterator()
        L_0x007d:
            boolean r4 = r12.hasNext()
            if (r4 == 0) goto L_0x00cc
            java.lang.Object r4 = r12.next()
            r10 = r4
            org.telegram.tgnet.TLRPC$TL_availableReaction r10 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r10
            java.lang.String r4 = r10.reaction
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messagePeerReaction> r5 = r13.reactions
            java.lang.Object r5 = r5.get(r2)
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r5 = (org.telegram.tgnet.TLRPC$TL_messagePeerReaction) r5
            java.lang.String r5 = r5.reaction
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x007d
            org.telegram.ui.Components.BackupImageView r5 = r11.reactView
            org.telegram.tgnet.TLRPC$Document r12 = r10.static_icon
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForDocument(r12)
            r9 = 0
            java.lang.String r7 = "50_50"
            java.lang.String r8 = "webp"
            r5.setImage((org.telegram.messenger.ImageLocation) r6, (java.lang.String) r7, (java.lang.String) r8, (android.graphics.drawable.Drawable) r9, (java.lang.Object) r10)
            org.telegram.ui.Components.BackupImageView r12 = r11.reactView
            r12.setVisibility(r2)
            org.telegram.ui.Components.BackupImageView r12 = r11.reactView
            r12.setAlpha(r3)
            org.telegram.ui.Components.BackupImageView r12 = r11.reactView
            android.view.ViewPropertyAnimator r12 = r12.animate()
            android.view.ViewPropertyAnimator r12 = r12.alpha(r0)
            r12.start()
            android.widget.ImageView r12 = r11.iconView
            r4 = 8
            r12.setVisibility(r4)
            r12 = 0
            goto L_0x00cd
        L_0x00cc:
            r12 = 1
        L_0x00cd:
            if (r12 == 0) goto L_0x00e6
            android.widget.ImageView r12 = r11.iconView
            r12.setVisibility(r2)
            android.widget.ImageView r12 = r11.iconView
            r12.setAlpha(r3)
            android.widget.ImageView r12 = r11.iconView
            android.view.ViewPropertyAnimator r12 = r12.animate()
            android.view.ViewPropertyAnimator r12 = r12.alpha(r0)
            r12.start()
        L_0x00e6:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r12 = r13.users
            java.util.Iterator r12 = r12.iterator()
        L_0x00ec:
            boolean r13 = r12.hasNext()
            if (r13 == 0) goto L_0x012f
            java.lang.Object r13 = r12.next()
            org.telegram.tgnet.TLRPC$User r13 = (org.telegram.tgnet.TLRPC$User) r13
            org.telegram.messenger.MessageObject r0 = r11.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.from_id
            if (r0 == 0) goto L_0x00ec
            long r3 = r13.id
            long r5 = r0.user_id
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x00ec
            r0 = 0
        L_0x0109:
            java.util.List<org.telegram.tgnet.TLRPC$User> r3 = r11.users
            int r3 = r3.size()
            if (r0 >= r3) goto L_0x0126
            java.util.List<org.telegram.tgnet.TLRPC$User> r3 = r11.users
            java.lang.Object r3 = r3.get(r0)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            long r3 = r3.id
            long r5 = r13.id
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x0123
            r0 = 1
            goto L_0x0127
        L_0x0123:
            int r0 = r0 + 1
            goto L_0x0109
        L_0x0126:
            r0 = 0
        L_0x0127:
            if (r0 != 0) goto L_0x00ec
            java.util.List<org.telegram.tgnet.TLRPC$User> r0 = r11.users
            r0.add(r13)
            goto L_0x00ec
        L_0x012f:
            r11.updateView()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReactedHeaderView.lambda$loadReactions$6(int, org.telegram.tgnet.TLRPC$TL_messages_messageReactionsList):void");
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

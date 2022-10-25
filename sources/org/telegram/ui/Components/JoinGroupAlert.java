package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.JoinSheetUserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class JoinGroupAlert extends BottomSheet {
    private TLRPC$ChatInvite chatInvite;
    private TLRPC$Chat currentChat;
    private BaseFragment fragment;
    private String hash;
    private RadialProgressView requestProgressView;
    private TextView requestTextView;

    public JoinGroupAlert(Context context, TLObject tLObject, String str, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        String str2;
        int i;
        int i2;
        String str3;
        int i3;
        String str4;
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        fixNavigationBar(getThemedColor("windowBackgroundWhite"));
        this.fragment = baseFragment;
        if (tLObject instanceof TLRPC$ChatInvite) {
            this.chatInvite = (TLRPC$ChatInvite) tLObject;
        } else if (tLObject instanceof TLRPC$Chat) {
            this.currentChat = (TLRPC$Chat) tLObject;
        }
        this.hash = str;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setClickable(true);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(linearLayout);
        NestedScrollView nestedScrollView = new NestedScrollView(context);
        nestedScrollView.addView(frameLayout);
        setCustomView(nestedScrollView);
        ImageView imageView = new ImageView(context);
        imageView.setBackground(Theme.createSelectorDrawable(getThemedColor("listSelectorSDK21")));
        imageView.setColorFilter(getThemedColor("key_sheet_other"));
        imageView.setImageResource(R.drawable.ic_layer_close);
        imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                JoinGroupAlert.this.lambda$new$0(view);
            }
        });
        int dp = AndroidUtilities.dp(8.0f);
        imageView.setPadding(dp, dp, dp, dp);
        frameLayout.addView(imageView, LayoutHelper.createFrame(36, 36.0f, 8388661, 6.0f, 8.0f, 6.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setRoundRadius(AndroidUtilities.dp(35.0f));
        linearLayout.addView(backupImageView, LayoutHelper.createLinear(70, 70, 49, 0, 29, 0, 0));
        TLRPC$ChatInvite tLRPC$ChatInvite = this.chatInvite;
        String str5 = null;
        if (tLRPC$ChatInvite != null) {
            if (tLRPC$ChatInvite.chat != null) {
                AvatarDrawable avatarDrawable = new AvatarDrawable(this.chatInvite.chat);
                TLRPC$ChatInvite tLRPC$ChatInvite2 = this.chatInvite;
                TLRPC$Chat tLRPC$Chat = tLRPC$ChatInvite2.chat;
                String str6 = tLRPC$Chat.title;
                i = tLRPC$Chat.participants_count;
                backupImageView.setForUserOrChat(tLRPC$Chat, avatarDrawable, tLRPC$ChatInvite2);
                str5 = str6;
            } else {
                AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                avatarDrawable2.setInfo(0L, this.chatInvite.title, null);
                TLRPC$ChatInvite tLRPC$ChatInvite3 = this.chatInvite;
                String str7 = tLRPC$ChatInvite3.title;
                i = tLRPC$ChatInvite3.participants_count;
                backupImageView.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(tLRPC$ChatInvite3.photo.sizes, 50), this.chatInvite.photo), "50_50", avatarDrawable2, this.chatInvite);
                str5 = str7;
            }
            str2 = this.chatInvite.about;
        } else if (this.currentChat != null) {
            AvatarDrawable avatarDrawable3 = new AvatarDrawable(this.currentChat);
            String str8 = this.currentChat.title;
            TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.currentChat.id);
            str5 = chatFull != null ? chatFull.about : str5;
            i = Math.max(this.currentChat.participants_count, chatFull != null ? chatFull.participants_count : 0);
            TLRPC$Chat tLRPC$Chat2 = this.currentChat;
            backupImageView.setForUserOrChat(tLRPC$Chat2, avatarDrawable3, tLRPC$Chat2);
            str2 = str5;
            str5 = str8;
        } else {
            str2 = null;
            i = 0;
        }
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 17.0f);
        textView.setTextColor(getThemedColor("dialogTextBlack"));
        textView.setText(str5);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 49, 10, 9, 10, i > 0 ? 0 : 20));
        TLRPC$ChatInvite tLRPC$ChatInvite4 = this.chatInvite;
        final boolean z = (tLRPC$ChatInvite4 != null && ((tLRPC$ChatInvite4.channel && !tLRPC$ChatInvite4.megagroup) || ChatObject.isChannelAndNotMegaGroup(tLRPC$ChatInvite4.chat))) || (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup);
        boolean z2 = !TextUtils.isEmpty(str2);
        if (i > 0) {
            TextView textView2 = new TextView(context);
            textView2.setTextSize(1, 14.0f);
            textView2.setTextColor(getThemedColor("dialogTextGray3"));
            textView2.setSingleLine(true);
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            if (z) {
                textView2.setText(LocaleController.formatPluralString("Subscribers", i, new Object[0]));
            } else {
                textView2.setText(LocaleController.formatPluralString("Members", i, new Object[0]));
            }
            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 49, 10, 3, 10, z2 ? 0 : 20));
        }
        if (z2) {
            TextView textView3 = new TextView(context);
            textView3.setGravity(17);
            textView3.setText(str2);
            textView3.setTextColor(getThemedColor("dialogTextBlack"));
            textView3.setTextSize(1, 15.0f);
            linearLayout.addView(textView3, LayoutHelper.createLinear(-1, -2, 48, 24, 10, 24, 20));
        }
        TLRPC$ChatInvite tLRPC$ChatInvite5 = this.chatInvite;
        if (tLRPC$ChatInvite5 != null && !tLRPC$ChatInvite5.request_needed) {
            if (tLRPC$ChatInvite5 == null) {
                return;
            }
            if (!tLRPC$ChatInvite5.participants.isEmpty()) {
                RecyclerListView recyclerListView = new RecyclerListView(context);
                recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                recyclerListView.setNestedScrollingEnabled(false);
                recyclerListView.setClipToPadding(false);
                recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
                recyclerListView.setHorizontalScrollBarEnabled(false);
                recyclerListView.setVerticalScrollBarEnabled(false);
                recyclerListView.setAdapter(new UsersAdapter(context));
                recyclerListView.setGlowColor(getThemedColor("dialogScrollGlow"));
                linearLayout.addView(recyclerListView, LayoutHelper.createLinear(-2, 90, 49, 0, 0, 0, 7));
            }
            View view = new View(context);
            view.setBackgroundColor(getThemedColor("dialogShadowLine"));
            linearLayout.addView(view, new LinearLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight()));
            PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(context, false, resourcesProvider);
            linearLayout.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            pickerBottomLayout.cancelButton.setTextColor(getThemedColor("dialogTextBlue2"));
            pickerBottomLayout.cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
            pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    JoinGroupAlert.this.lambda$new$8(view2);
                }
            });
            pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            pickerBottomLayout.doneButton.setVisibility(0);
            pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
            pickerBottomLayout.doneButtonTextView.setTextColor(getThemedColor("dialogTextBlue2"));
            TLRPC$ChatInvite tLRPC$ChatInvite6 = this.chatInvite;
            if ((tLRPC$ChatInvite6.channel && !tLRPC$ChatInvite6.megagroup) || (ChatObject.isChannel(tLRPC$ChatInvite6.chat) && !this.chatInvite.chat.megagroup)) {
                pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("ProfileJoinChannel", R.string.ProfileJoinChannel).toUpperCase());
            } else {
                pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("JoinGroup", R.string.JoinGroup));
            }
            pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    JoinGroupAlert.this.lambda$new$11(view2);
                }
            });
            return;
        }
        FrameLayout frameLayout2 = new FrameLayout(getContext());
        linearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, -2));
        RadialProgressView radialProgressView = new RadialProgressView(getContext(), resourcesProvider);
        this.requestProgressView = radialProgressView;
        radialProgressView.setProgressColor(getThemedColor("featuredStickers_addButton"));
        this.requestProgressView.setSize(AndroidUtilities.dp(32.0f));
        this.requestProgressView.setVisibility(4);
        frameLayout2.addView(this.requestProgressView, LayoutHelper.createFrame(48, 48, 17));
        TextView textView4 = new TextView(getContext());
        this.requestTextView = textView4;
        textView4.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor("featuredStickers_addButton"), getThemedColor("featuredStickers_addButtonPressed")));
        this.requestTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.requestTextView.setGravity(17);
        this.requestTextView.setSingleLine(true);
        TextView textView5 = this.requestTextView;
        if (z) {
            i2 = R.string.RequestToJoinChannel;
            str3 = "RequestToJoinChannel";
        } else {
            i2 = R.string.RequestToJoinGroup;
            str3 = "RequestToJoinGroup";
        }
        textView5.setText(LocaleController.getString(str3, i2));
        this.requestTextView.setTextColor(getThemedColor("featuredStickers_buttonText"));
        this.requestTextView.setTextSize(1, 15.0f);
        this.requestTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.requestTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                JoinGroupAlert.this.lambda$new$7(z, view2);
            }
        });
        frameLayout2.addView(this.requestTextView, LayoutHelper.createLinear(-1, 48, 8388611, 16, 0, 16, 0));
        TextView textView6 = new TextView(getContext());
        textView6.setGravity(17);
        textView6.setTextSize(1, 14.0f);
        if (z) {
            i3 = R.string.RequestToJoinChannelDescription;
            str4 = "RequestToJoinChannelDescription";
        } else {
            i3 = R.string.RequestToJoinGroupDescription;
            str4 = "RequestToJoinGroupDescription";
        }
        textView6.setText(LocaleController.getString(str4, i3));
        textView6.setTextColor(getThemedColor("dialogTextGray3"));
        linearLayout.addView(textView6, LayoutHelper.createLinear(-1, -2, 48, 24, 17, 24, 15));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(final boolean z, View view) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                JoinGroupAlert.this.lambda$new$1();
            }
        }, 400L);
        if (this.chatInvite == null && this.currentChat != null) {
            MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), 0, null, null, true, new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    JoinGroupAlert.this.dismiss();
                }
            }, new MessagesController.ErrorDelegate() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda10
                @Override // org.telegram.messenger.MessagesController.ErrorDelegate
                public final boolean run(TLRPC$TL_error tLRPC$TL_error) {
                    boolean lambda$new$3;
                    lambda$new$3 = JoinGroupAlert.this.lambda$new$3(z, tLRPC$TL_error);
                    return lambda$new$3;
                }
            });
            return;
        }
        final TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite = new TLRPC$TL_messages_importChatInvite();
        tLRPC$TL_messages_importChatInvite.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_importChatInvite, new RequestDelegate() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda12
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                JoinGroupAlert.this.lambda$new$6(z, tLRPC$TL_messages_importChatInvite, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        if (!isDismissed()) {
            this.requestTextView.setVisibility(4);
            this.requestProgressView.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$3(final boolean z, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null && "INVITE_REQUEST_SENT".equals(tLRPC$TL_error.text)) {
            setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    JoinGroupAlert.this.lambda$new$2(z, dialogInterface);
                }
            });
        }
        dismiss();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(boolean z, DialogInterface dialogInterface) {
        showBulletin(getContext(), this.fragment, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(final boolean z, final TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                JoinGroupAlert.this.lambda$new$5(tLRPC$TL_error, z, tLRPC$TL_messages_importChatInvite);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(TLRPC$TL_error tLRPC$TL_error, final boolean z, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (tLRPC$TL_error != null) {
            if ("INVITE_REQUEST_SENT".equals(tLRPC$TL_error.text)) {
                setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        JoinGroupAlert.this.lambda$new$4(z, dialogInterface);
                    }
                });
            } else {
                AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this.fragment, tLRPC$TL_messages_importChatInvite, new Object[0]);
            }
        }
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(boolean z, DialogInterface dialogInterface) {
        showBulletin(getContext(), this.fragment, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8(View view) {
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$11(View view) {
        dismiss();
        final TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite = new TLRPC$TL_messages_importChatInvite();
        tLRPC$TL_messages_importChatInvite.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_importChatInvite, new RequestDelegate() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda11
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                JoinGroupAlert.this.lambda$new$10(tLRPC$TL_messages_importChatInvite, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10(final TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                JoinGroupAlert.this.lambda$new$9(tLRPC$TL_error, tLObject, tLRPC$TL_messages_importChatInvite);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (tLRPC$TL_error == null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            if (tLRPC$Updates.chats.isEmpty()) {
                return;
            }
            TLRPC$Chat tLRPC$Chat = tLRPC$Updates.chats.get(0);
            tLRPC$Chat.left = false;
            tLRPC$Chat.kicked = false;
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$Updates.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tLRPC$Updates.chats, false);
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", tLRPC$Chat.id);
            if (!MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this.fragment)) {
                return;
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            BaseFragment baseFragment2 = this.fragment;
            baseFragment2.presentFragment(chatActivity, baseFragment2 instanceof ChatActivity);
            return;
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this.fragment, tLRPC$TL_messages_importChatInvite, new Object[0]);
    }

    public static void showBulletin(Context context, BaseFragment baseFragment, boolean z) {
        String string;
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(context, baseFragment.getResourceProvider());
        twoLineLottieLayout.imageView.setAnimation(R.raw.timer_3, 28, 28);
        twoLineLottieLayout.titleTextView.setText(LocaleController.getString("RequestToJoinSent", R.string.RequestToJoinSent));
        if (z) {
            string = LocaleController.getString("RequestToJoinChannelSentDescription", R.string.RequestToJoinChannelSentDescription);
        } else {
            string = LocaleController.getString("RequestToJoinGroupSentDescription", R.string.RequestToJoinGroupSentDescription);
        }
        twoLineLottieLayout.subtitleTextView.setText(string);
        Bulletin.make(baseFragment, twoLineLottieLayout, 2750).show();
    }

    /* loaded from: classes3.dex */
    private class UsersAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public UsersAdapter(Context context) {
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int size = JoinGroupAlert.this.chatInvite.participants.size();
            return size != (JoinGroupAlert.this.chatInvite.chat != null ? JoinGroupAlert.this.chatInvite.chat.participants_count : JoinGroupAlert.this.chatInvite.participants_count) ? size + 1 : size;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1788onCreateViewHolder(ViewGroup viewGroup, int i) {
            JoinSheetUserCell joinSheetUserCell = new JoinSheetUserCell(this.context);
            joinSheetUserCell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(90.0f)));
            return new RecyclerListView.Holder(joinSheetUserCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            JoinSheetUserCell joinSheetUserCell = (JoinSheetUserCell) viewHolder.itemView;
            if (i < JoinGroupAlert.this.chatInvite.participants.size()) {
                joinSheetUserCell.setUser(JoinGroupAlert.this.chatInvite.participants.get(i));
            } else {
                joinSheetUserCell.setCount((JoinGroupAlert.this.chatInvite.chat != null ? JoinGroupAlert.this.chatInvite.chat.participants_count : JoinGroupAlert.this.chatInvite.participants_count) - JoinGroupAlert.this.chatInvite.participants.size());
            }
        }
    }
}

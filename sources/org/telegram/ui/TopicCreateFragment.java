package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_messageActionTopicCreate;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_updateMessageID;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.Forum.ForumBubbleDrawable;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.ReplaceableIconDrawable;
import org.telegram.ui.TopicCreateFragment;
/* loaded from: classes3.dex */
public class TopicCreateFragment extends BaseFragment {
    int animationIndex;
    BackupImageView[] backupImageView;
    long chatId;
    boolean created;
    Drawable defaultIconDrawable;
    EditTextBoldCursor editTextBoldCursor;
    String firstSymbol;
    ForumBubbleDrawable forumBubbleDrawable;
    int iconColor;
    ReplaceableIconDrawable replaceableIconDrawable;
    SelectAnimatedEmojiDialog selectAnimatedEmojiDialog;
    long selectedEmojiDocumentId;
    TLRPC$TL_forumTopic topicForEdit;
    int topicId;

    public static TopicCreateFragment create(long j, int i) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", j);
        bundle.putInt("topic_id", i);
        return new TopicCreateFragment(bundle);
    }

    private TopicCreateFragment(Bundle bundle) {
        super(bundle);
        this.backupImageView = new BackupImageView[2];
        this.firstSymbol = "";
        this.animationIndex = 0;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.chatId = this.arguments.getLong("chat_id");
        int i = this.arguments.getInt("topic_id", 0);
        this.topicId = i;
        if (i != 0) {
            TLRPC$TL_forumTopic findTopic = getMessagesController().getTopicsController().findTopic(this.chatId, this.topicId);
            this.topicForEdit = findTopic;
            if (findTopic == null) {
                return false;
            }
            this.iconColor = findTopic.icon_color;
        } else {
            int[] iArr = ForumBubbleDrawable.serverSupportedColor;
            this.iconColor = iArr[Math.abs(Utilities.random.nextInt() % iArr.length)];
        }
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        if (this.topicForEdit != null) {
            this.actionBar.setTitle(LocaleController.getString("NewTopic", R.string.EditTopic));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewTopic", R.string.NewTopic));
        }
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        if (this.topicForEdit == null) {
            this.actionBar.createMenu().addItem(1, LocaleController.getString("Create", R.string.Create).toUpperCase());
        } else {
            this.actionBar.createMenu().addItem(2, R.drawable.ic_ab_done);
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        frameLayout.addView(linearLayout);
        HeaderCell headerCell = new HeaderCell(context);
        headerCell.setText(LocaleController.getString("CreateTopicTitle", R.string.CreateTopicTitle));
        FrameLayout frameLayout2 = new FrameLayout(context);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.editTextBoldCursor = editTextBoldCursor;
        editTextBoldCursor.setHintText(LocaleController.getString("EnterTopicName", R.string.EnterTopicName));
        this.editTextBoldCursor.setHintColor(getThemedColor("chat_messagePanelHint"));
        this.editTextBoldCursor.setTextColor(getThemedColor("chat_messagePanelText"));
        this.editTextBoldCursor.setPadding(AndroidUtilities.dp(0.0f), this.editTextBoldCursor.getPaddingTop(), AndroidUtilities.dp(0.0f), this.editTextBoldCursor.getPaddingBottom());
        this.editTextBoldCursor.setBackgroundDrawable(null);
        this.editTextBoldCursor.setSingleLine(true);
        EditTextBoldCursor editTextBoldCursor2 = this.editTextBoldCursor;
        editTextBoldCursor2.setInputType(editTextBoldCursor2.getInputType() | 16384);
        frameLayout2.addView(this.editTextBoldCursor, LayoutHelper.createFrame(-1, -1.0f, 0, 51.0f, 4.0f, 21.0f, 4.0f));
        this.editTextBoldCursor.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.TopicCreateFragment.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                String trim = editable.toString().trim();
                String str = TopicCreateFragment.this.firstSymbol;
                if (trim.length() > 0) {
                    TopicCreateFragment.this.firstSymbol = trim.substring(0, 1).toUpperCase();
                } else {
                    TopicCreateFragment.this.firstSymbol = "";
                }
                if (!str.equals(TopicCreateFragment.this.firstSymbol)) {
                    LetterDrawable letterDrawable = new LetterDrawable(null, 1);
                    letterDrawable.setTitle(TopicCreateFragment.this.firstSymbol);
                    TopicCreateFragment.this.replaceableIconDrawable.setIcon((Drawable) letterDrawable, true);
                }
            }
        });
        AnonymousClass3 anonymousClass3 = new AnonymousClass3(this, context);
        anonymousClass3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TopicCreateFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TopicCreateFragment.this.lambda$createView$0(view);
            }
        });
        for (int i = 0; i < 2; i++) {
            this.backupImageView[i] = new BackupImageView(context);
            anonymousClass3.addView(this.backupImageView[i], LayoutHelper.createFrame(28, 28, 17));
        }
        frameLayout2.addView(anonymousClass3, LayoutHelper.createFrame(40, 40.0f, 16, 10.0f, 0.0f, 0.0f, 0.0f));
        linearLayout.addView(headerCell);
        linearLayout.addView(frameLayout2);
        FrameLayout frameLayout3 = new FrameLayout(context);
        CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, R.drawable.greydivider_top, Theme.getColor("windowBackgroundGrayShadow")), 0, 0);
        combinedDrawable.setFullsize(true);
        frameLayout3.setBackgroundDrawable(combinedDrawable);
        frameLayout3.setClipChildren(false);
        SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = new SelectAnimatedEmojiDialog(this, getContext(), false, null, 3, null) { // from class: org.telegram.ui.TopicCreateFragment.4
            private boolean firstLayout = true;

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                super.onLayout(z, i2, i3, i4, i5);
                if (this.firstLayout) {
                    this.firstLayout = false;
                    TopicCreateFragment.this.selectAnimatedEmojiDialog.onShow(null);
                }
            }

            @Override // org.telegram.ui.SelectAnimatedEmojiDialog
            protected void onEmojiSelected(View view, Long l, TLRPC$Document tLRPC$Document, Integer num) {
                boolean z = false;
                if (!TextUtils.isEmpty(UserConfig.getInstance(((BaseFragment) TopicCreateFragment.this).currentAccount).defaultTopicIcons)) {
                    TLRPC$TL_messages_stickerSet stickerSetByEmojiOrName = TopicCreateFragment.this.getMediaDataController().getStickerSetByEmojiOrName(UserConfig.getInstance(((BaseFragment) TopicCreateFragment.this).currentAccount).defaultTopicIcons);
                    if ((stickerSetByEmojiOrName == null ? 0L : stickerSetByEmojiOrName.set.id) == MediaDataController.getStickerSetId(tLRPC$Document)) {
                        z = true;
                    }
                }
                TopicCreateFragment.this.selectEmoji(l, z);
            }
        };
        this.selectAnimatedEmojiDialog = selectAnimatedEmojiDialog;
        selectAnimatedEmojiDialog.setAnimationsEnabled(this.fragmentBeginToShow);
        this.selectAnimatedEmojiDialog.setClipChildren(false);
        frameLayout3.addView(this.selectAnimatedEmojiDialog, LayoutHelper.createFrame(-1, -1.0f, 0, 12.0f, 12.0f, 12.0f, 12.0f));
        linearLayout.addView(frameLayout3, LayoutHelper.createFrame(-1, -1.0f));
        Drawable createTopicDrawable = ForumUtilities.createTopicDrawable("", this.iconColor);
        this.forumBubbleDrawable = (ForumBubbleDrawable) ((CombinedDrawable) createTopicDrawable).getBackgroundDrawable();
        this.replaceableIconDrawable = new ReplaceableIconDrawable(context);
        CombinedDrawable combinedDrawable2 = new CombinedDrawable(createTopicDrawable, this.replaceableIconDrawable, 0, 0);
        combinedDrawable2.setFullsize(true);
        this.selectAnimatedEmojiDialog.setForumIconDrawable(combinedDrawable2);
        this.defaultIconDrawable = combinedDrawable2;
        this.replaceableIconDrawable.addView(this.backupImageView[0]);
        this.replaceableIconDrawable.addView(this.backupImageView[1]);
        this.backupImageView[0].setImageDrawable(this.defaultIconDrawable);
        AndroidUtilities.updateViewVisibilityAnimated(this.backupImageView[0], true, 1.0f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.backupImageView[1], false, 1.0f, false);
        this.forumBubbleDrawable.addParent(this.backupImageView[0]);
        this.forumBubbleDrawable.addParent(this.backupImageView[1]);
        TLRPC$TL_forumTopic tLRPC$TL_forumTopic = this.topicForEdit;
        if (tLRPC$TL_forumTopic != null) {
            this.editTextBoldCursor.setText(tLRPC$TL_forumTopic.title);
            selectEmoji(Long.valueOf(this.topicForEdit.icon_emoji_id), true);
        }
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.TopicCreateFragment$1  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onItemClick$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        AnonymousClass1() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:42:0x0100, code lost:
            if (r13.topicForEdit.icon_emoji_id != r13.selectedEmojiDocumentId) goto L48;
         */
        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onItemClick(int r13) {
            /*
                Method dump skipped, instructions count: 386
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TopicCreateFragment.AnonymousClass1.onItemClick(int):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$1(final String str, final AlertDialog alertDialog, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TopicCreateFragment$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TopicCreateFragment.AnonymousClass1.this.lambda$onItemClick$0(tLObject, str, alertDialog);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$0(TLObject tLObject, String str, AlertDialog alertDialog) {
            if (tLObject != null) {
                TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
                for (int i = 0; i < tLRPC$Updates.updates.size(); i++) {
                    if (tLRPC$Updates.updates.get(i) instanceof TLRPC$TL_updateMessageID) {
                        TLRPC$TL_updateMessageID tLRPC$TL_updateMessageID = (TLRPC$TL_updateMessageID) tLRPC$Updates.updates.get(i);
                        Bundle bundle = new Bundle();
                        bundle.putLong("chat_id", TopicCreateFragment.this.chatId);
                        bundle.putInt("message_id", 1);
                        bundle.putInt("unread_count", 0);
                        bundle.putBoolean("historyPreloaded", false);
                        ChatActivity chatActivity = new ChatActivity(bundle);
                        TLRPC$TL_messageActionTopicCreate tLRPC$TL_messageActionTopicCreate = new TLRPC$TL_messageActionTopicCreate();
                        tLRPC$TL_messageActionTopicCreate.title = str;
                        TLRPC$TL_messageService tLRPC$TL_messageService = new TLRPC$TL_messageService();
                        tLRPC$TL_messageService.action = tLRPC$TL_messageActionTopicCreate;
                        tLRPC$TL_messageService.peer_id = TopicCreateFragment.this.getMessagesController().getPeer(-TopicCreateFragment.this.chatId);
                        tLRPC$TL_messageService.dialog_id = -TopicCreateFragment.this.chatId;
                        tLRPC$TL_messageService.id = tLRPC$TL_updateMessageID.id;
                        tLRPC$TL_messageService.date = (int) (System.currentTimeMillis() / 1000);
                        ArrayList<MessageObject> arrayList = new ArrayList<>();
                        arrayList.add(new MessageObject(((BaseFragment) TopicCreateFragment.this).currentAccount, tLRPC$TL_messageService, false, false));
                        TLRPC$Chat chat = TopicCreateFragment.this.getMessagesController().getChat(Long.valueOf(TopicCreateFragment.this.chatId));
                        TLRPC$TL_forumTopic tLRPC$TL_forumTopic = new TLRPC$TL_forumTopic();
                        tLRPC$TL_forumTopic.id = tLRPC$TL_updateMessageID.id;
                        TopicCreateFragment topicCreateFragment = TopicCreateFragment.this;
                        long j = topicCreateFragment.selectedEmojiDocumentId;
                        if (j != 0) {
                            tLRPC$TL_forumTopic.icon_emoji_id = j;
                            tLRPC$TL_forumTopic.flags |= 1;
                        }
                        tLRPC$TL_forumTopic.topicStartMessage = tLRPC$TL_messageService;
                        tLRPC$TL_forumTopic.title = str;
                        tLRPC$TL_forumTopic.top_message = tLRPC$TL_messageService.id;
                        tLRPC$TL_forumTopic.topMessage = tLRPC$TL_messageService;
                        tLRPC$TL_forumTopic.from_id = topicCreateFragment.getMessagesController().getPeer(TopicCreateFragment.this.getUserConfig().clientUserId);
                        tLRPC$TL_forumTopic.notify_settings = new TLRPC$TL_peerNotifySettings();
                        tLRPC$TL_forumTopic.icon_color = TopicCreateFragment.this.iconColor;
                        chatActivity.setThreadMessages(arrayList, chat, tLRPC$TL_messageService.id, 1, 1, tLRPC$TL_forumTopic);
                        chatActivity.justCreatedTopic = true;
                        TopicCreateFragment.this.getMessagesController().getTopicsController().onTopicCreated(-TopicCreateFragment.this.chatId, tLRPC$TL_forumTopic, true);
                        TopicCreateFragment.this.presentFragment(chatActivity);
                    }
                }
            }
            alertDialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.TopicCreateFragment$3  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass3 extends FrameLayout {
        ValueAnimator backAnimator;
        boolean pressed;
        float pressedProgress;

        AnonymousClass3(TopicCreateFragment topicCreateFragment, Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            float f = ((1.0f - this.pressedProgress) * 0.2f) + 0.8f;
            canvas.save();
            canvas.scale(f, f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f);
            super.dispatchDraw(canvas);
            canvas.restore();
            updatePressedProgress();
        }

        @Override // android.view.View
        public void setPressed(boolean z) {
            ValueAnimator valueAnimator;
            super.setPressed(z);
            if (this.pressed != z) {
                this.pressed = z;
                invalidate();
                if (z && (valueAnimator = this.backAnimator) != null) {
                    valueAnimator.removeAllListeners();
                    this.backAnimator.cancel();
                }
                if (z) {
                    return;
                }
                float f = this.pressedProgress;
                if (f == 0.0f) {
                    return;
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(f, 0.0f);
                this.backAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.TopicCreateFragment$3$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        TopicCreateFragment.AnonymousClass3.this.lambda$setPressed$0(valueAnimator2);
                    }
                });
                this.backAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.TopicCreateFragment.3.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        AnonymousClass3.this.backAnimator = null;
                    }
                });
                this.backAnimator.setInterpolator(new OvershootInterpolator(5.0f));
                this.backAnimator.setDuration(350L);
                this.backAnimator.start();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setPressed$0(ValueAnimator valueAnimator) {
            this.pressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        public void updatePressedProgress() {
            if (isPressed()) {
                float f = this.pressedProgress;
                if (f == 1.0f) {
                    return;
                }
                this.pressedProgress = Utilities.clamp(f + 0.16f, 1.0f, 0.0f);
                invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        if (this.selectedEmojiDocumentId == 0 && this.topicForEdit == null) {
            this.iconColor = this.forumBubbleDrawable.moveNexColor();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectEmoji(Long l, boolean z) {
        long longValue = l == null ? 0L : l.longValue();
        if (this.selectedEmojiDocumentId == longValue) {
            return;
        }
        if (!z && longValue != 0 && !getUserConfig().isPremium()) {
            TLRPC$Document findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, l.longValue());
            if (findDocument == null) {
                return;
            }
            BulletinFactory.of(this).createEmojiBulletin(findDocument, AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiHint", R.string.UnlockPremiumEmojiHint)), LocaleController.getString("PremiumMore", R.string.PremiumMore), new Runnable() { // from class: org.telegram.ui.TopicCreateFragment$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TopicCreateFragment.this.lambda$selectEmoji$1();
                }
            }).show();
            return;
        }
        this.selectedEmojiDocumentId = longValue;
        this.selectAnimatedEmojiDialog.setSelected(Long.valueOf(longValue));
        if (longValue != 0) {
            this.backupImageView[1].setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(10, this.currentAccount, longValue));
            this.backupImageView[1].setImageDrawable(null);
        } else {
            LetterDrawable letterDrawable = new LetterDrawable(null, 1);
            letterDrawable.setTitle(this.firstSymbol);
            this.replaceableIconDrawable.setIcon((Drawable) letterDrawable, false);
            this.backupImageView[1].setImageDrawable(this.defaultIconDrawable);
            this.backupImageView[1].setAnimatedEmojiDrawable(null);
        }
        BackupImageView[] backupImageViewArr = this.backupImageView;
        BackupImageView backupImageView = backupImageViewArr[0];
        backupImageViewArr[0] = backupImageViewArr[1];
        backupImageViewArr[1] = backupImageView;
        AndroidUtilities.updateViewVisibilityAnimated(backupImageViewArr[0], true, 0.5f, true);
        AndroidUtilities.updateViewVisibilityAnimated(this.backupImageView[1], false, 0.5f, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$selectEmoji$1() {
        new PremiumFeatureBottomSheet(this, 11, false).show();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        if (z) {
            this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, null);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        super.onTransitionAnimationEnd(z, z2);
        if (!z && this.created) {
            removeSelfFromStack();
        }
        getNotificationCenter().onAnimationFinish(this.animationIndex);
        this.selectAnimatedEmojiDialog.setAnimationsEnabled(this.fragmentBeginToShow);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        this.editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(this.editTextBoldCursor);
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void showKeyboard() {
        this.editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(this.editTextBoldCursor);
    }
}

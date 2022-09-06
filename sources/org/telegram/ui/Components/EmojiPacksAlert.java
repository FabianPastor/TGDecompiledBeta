package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;

public class EmojiPacksAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static Pattern urlPattern;
    private TextView addButtonView;
    /* access modifiers changed from: private */
    public LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private FrameLayout buttonsView;
    /* access modifiers changed from: private */
    public EmojiPacksLoader customEmojiPacks;
    /* access modifiers changed from: private */
    public BaseFragment fragment;
    /* access modifiers changed from: private */
    public Float fromY;
    /* access modifiers changed from: private */
    public GridLayoutManager gridLayoutManager;
    /* access modifiers changed from: private */
    public boolean hasDescription;
    /* access modifiers changed from: private */
    public float lastY;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ValueAnimator loadAnimator;
    /* access modifiers changed from: private */
    public float loadT;
    boolean loaded = true;
    /* access modifiers changed from: private */
    public View paddingView;
    private ActionBarPopupWindow popupWindow;
    /* access modifiers changed from: private */
    public long premiumButtonClicked;
    private PremiumButtonView premiumButtonView;
    /* access modifiers changed from: private */
    public CircularProgressDrawable progressDrawable;
    /* access modifiers changed from: private */
    public TextView removeButtonView;
    /* access modifiers changed from: private */
    public View shadowView;
    private boolean shown = false;

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onButtonClicked(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onCloseByLink() {
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public EmojiPacksAlert(org.telegram.ui.ActionBar.BaseFragment r20, android.content.Context r21, org.telegram.ui.ActionBar.Theme.ResourcesProvider r22, java.util.ArrayList<org.telegram.tgnet.TLRPC$InputStickerSet> r23) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = 0
            r0.<init>(r2, r5, r3)
            r0.shown = r5
            r6 = 1
            r0.loaded = r6
            r23.size()
            r0.fragment = r1
            r19.fixNavigationBar()
            org.telegram.ui.Components.EmojiPacksAlert$1 r7 = new org.telegram.ui.Components.EmojiPacksAlert$1
            int r8 = r0.currentAccount
            r7.<init>(r8, r4)
            r0.customEmojiPacks = r7
            org.telegram.ui.Components.CircularProgressDrawable r7 = new org.telegram.ui.Components.CircularProgressDrawable
            r8 = 1107296256(0x42000000, float:32.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r9 = 1080033280(0x40600000, float:3.5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            java.lang.String r10 = "featuredStickers_addButton"
            int r11 = r0.getThemedColor(r10)
            r7.<init>(r8, r9, r11)
            r0.progressDrawable = r7
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "windowBackgroundWhiteLinkText"
            int r8 = r0.getThemedColor(r8)
            r9 = 178(0xb2, float:2.5E-43)
            int r8 = androidx.core.graphics.ColorUtils.setAlphaComponent(r8, r9)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            org.telegram.ui.Components.EmojiPacksAlert$2 r8 = new org.telegram.ui.Components.EmojiPacksAlert$2
            r8.<init>(r2, r7)
            r0.containerView = r8
            org.telegram.ui.Components.EmojiPacksAlert$3 r7 = new org.telegram.ui.Components.EmojiPacksAlert$3
            r7.<init>(r0, r2)
            r0.paddingView = r7
            org.telegram.ui.Components.EmojiPacksAlert$4 r7 = new org.telegram.ui.Components.EmojiPacksAlert$4
            r7.<init>(r2)
            r0.listView = r7
            android.view.ViewGroup r7 = r0.containerView
            int r8 = r0.backgroundPaddingLeft
            int r9 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            r7.setPadding(r8, r9, r8, r5)
            android.view.ViewGroup r7 = r0.containerView
            r7.setClipChildren(r5)
            android.view.ViewGroup r7 = r0.containerView
            r7.setClipToPadding(r5)
            android.view.ViewGroup r7 = r0.containerView
            r7.setWillNotDraw(r5)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            r8 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r7.setSelectorRadius(r9)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            java.lang.String r9 = "listSelectorSDK21"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r9, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
            r7.setSelectorDrawableColor(r9)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            r9 = 1090519040(0x41000000, float:8.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r12 = 1116209152(0x42880000, float:68.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r7.setPadding(r11, r5, r9, r13)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            org.telegram.ui.Components.EmojiPacksAlert$Adapter r9 = new org.telegram.ui.Components.EmojiPacksAlert$Adapter
            r11 = 0
            r9.<init>()
            r7.setAdapter(r9)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            androidx.recyclerview.widget.GridLayoutManager r9 = new androidx.recyclerview.widget.GridLayoutManager
            r11 = 8
            r9.<init>(r2, r11)
            r0.gridLayoutManager = r9
            r7.setLayoutManager(r9)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            org.telegram.ui.Components.EmojiPacksAlert$5 r9 = new org.telegram.ui.Components.EmojiPacksAlert$5
            r9.<init>()
            r7.addItemDecoration(r9)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda9 r9 = new org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda9
            r9.<init>(r0, r4, r1, r3)
            r7.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r9)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda10 r4 = new org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda10
            r4.<init>(r0, r2)
            r3.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r4)
            androidx.recyclerview.widget.GridLayoutManager r3 = r0.gridLayoutManager
            r3.setReverseLayout(r5)
            androidx.recyclerview.widget.GridLayoutManager r3 = r0.gridLayoutManager
            org.telegram.ui.Components.EmojiPacksAlert$7 r4 = new org.telegram.ui.Components.EmojiPacksAlert$7
            r4.<init>()
            r3.setSpanSizeLookup(r4)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            r7 = 51
            r9 = -1
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r7)
            r3.addView(r4, r7)
            android.view.View r3 = new android.view.View
            r3.<init>(r2)
            r0.shadowView = r3
            java.lang.String r4 = "dialogShadowLine"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setBackgroundColor(r4)
            android.view.ViewGroup r3 = r0.containerView
            android.view.View r4 = r0.shadowView
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = 1065353216(0x3var_, float:1.0)
            float r14 = org.telegram.messenger.AndroidUtilities.density
            float r13 = r13 / r14
            r14 = 80
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((float) r7, (float) r13, (int) r14)
            r3.addView(r4, r7)
            android.view.View r3 = r0.shadowView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r4 = -r4
            float r4 = (float) r4
            r3.setTranslationY(r4)
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r2)
            r0.buttonsView = r3
            java.lang.String r4 = "dialogBackground"
            int r4 = r0.getThemedColor(r4)
            r3.setBackgroundColor(r4)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r4 = r0.buttonsView
            r7 = 68
            r12 = 87
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r7, (int) r12)
            r3.addView(r4, r7)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r2)
            r0.addButtonView = r3
            r3.setVisibility(r11)
            android.widget.TextView r3 = r0.addButtonView
            int r4 = r0.getThemedColor(r10)
            float[] r7 = new float[r6]
            r7[r5] = r8
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.filledRect((int) r4, (float[]) r7)
            r3.setBackground(r4)
            android.widget.TextView r3 = r0.addButtonView
            java.lang.String r4 = "featuredStickers_buttonText"
            int r4 = r0.getThemedColor(r4)
            r3.setTextColor(r4)
            android.widget.TextView r3 = r0.addButtonView
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r3.setTypeface(r7)
            android.widget.TextView r3 = r0.addButtonView
            r7 = 17
            r3.setGravity(r7)
            android.widget.FrameLayout r3 = r0.buttonsView
            android.widget.TextView r8 = r0.addButtonView
            r12 = -1
            r13 = 1111490560(0x42400000, float:48.0)
            r15 = 1094713344(0x41400000, float:12.0)
            r16 = 1092616192(0x41200000, float:10.0)
            r17 = 1094713344(0x41400000, float:12.0)
            r18 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r3.addView(r8, r9)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r2)
            r0.removeButtonView = r3
            r3.setVisibility(r11)
            android.widget.TextView r3 = r0.removeButtonView
            r8 = 268435455(0xfffffff, float:2.5243547E-29)
            java.lang.String r9 = "dialogTextRed"
            int r10 = r0.getThemedColor(r9)
            r8 = r8 & r10
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createRadSelectorDrawable(r8, r5, r5)
            r3.setBackground(r8)
            android.widget.TextView r3 = r0.removeButtonView
            int r8 = r0.getThemedColor(r9)
            r3.setTextColor(r8)
            android.widget.TextView r3 = r0.removeButtonView
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r3.setTypeface(r4)
            android.widget.TextView r3 = r0.removeButtonView
            r3.setGravity(r7)
            android.widget.TextView r3 = r0.removeButtonView
            r3.setClickable(r6)
            android.widget.FrameLayout r3 = r0.buttonsView
            android.widget.TextView r4 = r0.removeButtonView
            r7 = -1
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            r9 = 80
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 1100480512(0x41980000, float:19.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r3.addView(r4, r7)
            org.telegram.ui.Components.Premium.PremiumButtonView r3 = new org.telegram.ui.Components.Premium.PremiumButtonView
            r3.<init>(r2, r5)
            r0.premiumButtonView = r3
            int r2 = org.telegram.messenger.R.string.UnlockPremiumEmoji
            java.lang.String r4 = "UnlockPremiumEmoji"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda1
            r4.<init>(r0)
            r3.setButton(r2, r4)
            org.telegram.ui.Components.Premium.PremiumButtonView r2 = r0.premiumButtonView
            int r3 = org.telegram.messenger.R.raw.unlock_icon
            r2.setIcon(r3)
            org.telegram.ui.Components.Premium.PremiumButtonView r2 = r0.premiumButtonView
            android.widget.FrameLayout r2 = r2.buttonLayout
            r2.setClickable(r6)
            android.widget.FrameLayout r2 = r0.buttonsView
            org.telegram.ui.Components.Premium.PremiumButtonView r3 = r0.premiumButtonView
            r4 = -1
            r5 = 1111490560(0x42400000, float:48.0)
            r6 = 80
            r7 = 1094713344(0x41400000, float:12.0)
            r8 = 1092616192(0x41200000, float:10.0)
            r9 = 1094713344(0x41400000, float:12.0)
            r10 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10)
            r2.addView(r3, r4)
            r19.updateButton()
            int r1 = r20.getCurrentAccount()
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r2 = 5
            r1.checkStickers(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, android.content.Context, org.telegram.ui.ActionBar.Theme$ResourcesProvider, java.util.ArrayList):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ArrayList arrayList, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, View view, int i) {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
        int i2 = 0;
        if (arrayList == null || arrayList.size() <= 1) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
                this.popupWindow = null;
            } else if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getChatActivityEnterView().getVisibility() == 0 && (view instanceof EmojiImageView)) {
                AnimatedEmojiSpan animatedEmojiSpan = ((EmojiImageView) view).span;
                try {
                    TLRPC$Document tLRPC$Document = animatedEmojiSpan.document;
                    if (tLRPC$Document == null) {
                        tLRPC$Document = AnimatedEmojiDrawable.findDocument(this.currentAccount, animatedEmojiSpan.getDocumentId());
                    }
                    SpannableString spannableString = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document));
                    spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
                    ((ChatActivity) baseFragment).getChatActivityEnterView().messageEditText.getText().append(spannableString);
                    ((ChatActivity) baseFragment).showEmojiHint();
                    onCloseByLink();
                    dismiss();
                } catch (Exception unused) {
                }
                try {
                    view.performHapticFeedback(3, 1);
                } catch (Exception unused2) {
                }
            }
        } else if (SystemClock.elapsedRealtime() - this.premiumButtonClicked >= 250) {
            int i3 = 0;
            while (true) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.customEmojiPacks.data;
                if (i2 >= arrayListArr.length) {
                    break;
                }
                int size = arrayListArr[i2].size();
                if (this.customEmojiPacks.data.length > 1) {
                    size = Math.min(this.gridLayoutManager.getSpanCount() * 2, size);
                }
                i3 += size + 1 + 1;
                if (i < i3) {
                    break;
                }
                i2++;
            }
            ArrayList<TLRPC$TL_messages_stickerSet> arrayList2 = this.customEmojiPacks.stickerSets;
            if (arrayList2 != null && i2 < arrayList2.size()) {
                tLRPC$TL_messages_stickerSet = this.customEmojiPacks.stickerSets.get(i2);
            }
            if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.set != null) {
                ArrayList arrayList3 = new ArrayList();
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                arrayList3.add(tLRPC$TL_inputStickerSetID);
                new EmojiPacksAlert(baseFragment, getContext(), resourcesProvider, arrayList3) {
                    /* access modifiers changed from: protected */
                    public void onCloseByLink() {
                        EmojiPacksAlert.this.dismiss();
                    }
                }.show();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$2(Context context, View view, int i) {
        AnimatedEmojiSpan animatedEmojiSpan;
        if (!(view instanceof EmojiImageView) || (animatedEmojiSpan = ((EmojiImageView) view).span) == null) {
            return false;
        }
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), true, true);
        actionBarMenuSubItem.setItemHeight(48);
        actionBarMenuSubItem.setPadding(AndroidUtilities.dp(26.0f), 0, AndroidUtilities.dp(26.0f), 0);
        actionBarMenuSubItem.setText(LocaleController.getString("Copy", R.string.Copy));
        actionBarMenuSubItem.getTextView().setTextSize(1, 14.4f);
        actionBarMenuSubItem.getTextView().setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        actionBarMenuSubItem.setOnClickListener(new EmojiPacksAlert$$ExternalSyntheticLambda4(this, animatedEmojiSpan));
        LinearLayout linearLayout = new LinearLayout(context);
        Drawable mutate = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("actionBarDefaultSubmenuBackground"), PorterDuff.Mode.MULTIPLY));
        linearLayout.setBackground(mutate);
        linearLayout.addView(actionBarMenuSubItem);
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(linearLayout, -2, -2);
        this.popupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setClippingEnabled(true);
        this.popupWindow.setLayoutInScreen(true);
        this.popupWindow.setInputMethodMode(2);
        this.popupWindow.setSoftInputMode(0);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        this.popupWindow.showAtLocation(view, 51, (iArr[0] - AndroidUtilities.dp(49.0f)) + (view.getMeasuredWidth() / 2), iArr[1] - AndroidUtilities.dp(52.0f));
        try {
            view.performHapticFeedback(0, 1);
        } catch (Exception unused) {
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(AnimatedEmojiSpan animatedEmojiSpan, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
            this.popupWindow = null;
            SpannableString spannableString = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(AnimatedEmojiDrawable.findDocument(this.currentAccount, animatedEmojiSpan.getDocumentId())));
            spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
            if (AndroidUtilities.addToClipboard(spannableString)) {
                BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createCopyBulletin(LocaleController.getString("EmojiCopied", R.string.EmojiCopied)).show();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        showPremiumAlert();
    }

    private void updateShowButton(boolean z) {
        int i = 0;
        boolean z2 = !this.shown && z;
        if (this.removeButtonView.getVisibility() == 0) {
            i = AndroidUtilities.dp(19.0f);
        }
        float f = (float) i;
        float f2 = 1.0f;
        float f3 = 0.0f;
        if (z2) {
            ViewPropertyAnimator duration = this.buttonsView.animate().translationY(z ? f : (float) AndroidUtilities.dp(16.0f)).alpha(z ? 1.0f : 0.0f).setDuration(250);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            ViewPropertyAnimator translationY = this.shadowView.animate().translationY(z ? -(((float) AndroidUtilities.dp(68.0f)) - f) : 0.0f);
            if (!z) {
                f2 = 0.0f;
            }
            translationY.alpha(f2).setDuration(250).setInterpolator(cubicBezierInterpolator).start();
            ViewPropertyAnimator animate = this.listView.animate();
            if (!z) {
                f3 = ((float) AndroidUtilities.dp(68.0f)) - f;
            }
            animate.translationY(f3).setDuration(250).setInterpolator(cubicBezierInterpolator).start();
        } else {
            this.buttonsView.setAlpha(z ? 1.0f : 0.0f);
            this.buttonsView.setTranslationY(z ? f : (float) AndroidUtilities.dp(16.0f));
            View view = this.shadowView;
            if (!z) {
                f2 = 0.0f;
            }
            view.setAlpha(f2);
            this.shadowView.setTranslationY(z ? -(((float) AndroidUtilities.dp(68.0f)) - f) : 0.0f);
            RecyclerListView recyclerListView = this.listView;
            if (!z) {
                f3 = ((float) AndroidUtilities.dp(68.0f)) - f;
            }
            recyclerListView.setTranslationY(f3);
        }
        this.shown = z;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            updateInstallment();
        }
    }

    public void showPremiumAlert() {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            new PremiumFeatureBottomSheet(baseFragment, 11, false).show();
        } else if (getContext() instanceof LaunchActivity) {
            ((LaunchActivity) getContext()).lambda$runLinkRequest$62(new PremiumPreviewFragment((String) null));
        }
    }

    /* access modifiers changed from: private */
    public void updateLightStatusBar(boolean z) {
        boolean z2 = true;
        boolean z3 = AndroidUtilities.computePerceivedBrightness(getThemedColor("dialogBackground")) > 0.721f;
        if (AndroidUtilities.computePerceivedBrightness(Theme.blendOver(getThemedColor("actionBarDefault"), NUM)) <= 0.721f) {
            z2 = false;
        }
        if (!z) {
            z3 = z2;
        }
        AndroidUtilities.setLightStatusBar(getWindow(), z3);
    }

    public void updateInstallment() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof EmojiPackHeader) {
                EmojiPackHeader emojiPackHeader = (EmojiPackHeader) childAt;
                if (!(emojiPackHeader.set == null || emojiPackHeader.set.set == null)) {
                    emojiPackHeader.toggle(MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(emojiPackHeader.set.set.id), true);
                }
            }
        }
        updateButton();
    }

    public static void installSet(BaseFragment baseFragment, TLObject tLObject, boolean z) {
        installSet(baseFragment, tLObject, z, (Utilities.Callback<Boolean>) null, (Runnable) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0031 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0032  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void installSet(org.telegram.ui.ActionBar.BaseFragment r11, org.telegram.tgnet.TLObject r12, boolean r13, org.telegram.messenger.Utilities.Callback<java.lang.Boolean> r14, java.lang.Runnable r15) {
        /*
            if (r11 != 0) goto L_0x0005
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            goto L_0x0009
        L_0x0005:
            int r0 = r11.getCurrentAccount()
        L_0x0009:
            r7 = r0
            r0 = 0
            if (r11 != 0) goto L_0x000f
            r4 = r0
            goto L_0x0014
        L_0x000f:
            android.view.View r1 = r11.getFragmentView()
            r4 = r1
        L_0x0014:
            if (r12 != 0) goto L_0x0017
            return
        L_0x0017:
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            if (r1 == 0) goto L_0x0020
            r1 = r12
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r1
            r6 = r1
            goto L_0x0021
        L_0x0020:
            r6 = r0
        L_0x0021:
            if (r6 == 0) goto L_0x0027
            org.telegram.tgnet.TLRPC$StickerSet r12 = r6.set
        L_0x0025:
            r2 = r12
            goto L_0x002f
        L_0x0027:
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$StickerSet
            if (r1 == 0) goto L_0x002e
            org.telegram.tgnet.TLRPC$StickerSet r12 = (org.telegram.tgnet.TLRPC$StickerSet) r12
            goto L_0x0025
        L_0x002e:
            r2 = r0
        L_0x002f:
            if (r2 != 0) goto L_0x0032
            return
        L_0x0032:
            org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r7)
            long r0 = r2.id
            boolean r12 = r12.cancelRemovingStickerSet(r0)
            if (r12 == 0) goto L_0x0046
            if (r14 == 0) goto L_0x0045
            java.lang.Boolean r11 = java.lang.Boolean.TRUE
            r14.run(r11)
        L_0x0045:
            return
        L_0x0046:
            org.telegram.tgnet.TLRPC$TL_messages_installStickerSet r12 = new org.telegram.tgnet.TLRPC$TL_messages_installStickerSet
            r12.<init>()
            org.telegram.tgnet.TLRPC$TL_inputStickerSetID r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID
            r0.<init>()
            r12.stickerset = r0
            long r8 = r2.id
            r0.id = r8
            long r8 = r2.access_hash
            r0.access_hash = r8
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)
            org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda8 r10 = new org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda8
            r1 = r10
            r3 = r13
            r5 = r11
            r8 = r14
            r9 = r15
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
            r0.sendRequest(r12, r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.installSet(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLObject, boolean, org.telegram.messenger.Utilities$Callback, java.lang.Runnable):void");
    }

    /* JADX WARNING: type inference failed for: r18v0, types: [org.telegram.tgnet.TLRPC$TL_messages_stickerSet] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$installSet$5(org.telegram.tgnet.TLRPC$StickerSet r13, org.telegram.tgnet.TLRPC$TL_error r14, boolean r15, android.view.View r16, org.telegram.ui.ActionBar.BaseFragment r17, org.telegram.tgnet.TLRPC$TL_messages_stickerSet r18, org.telegram.tgnet.TLObject r19, int r20, org.telegram.messenger.Utilities.Callback r21, java.lang.Runnable r22) {
        /*
            r0 = r13
            r1 = r17
            r2 = r19
            r3 = r21
            boolean r4 = r0.masks
            r5 = 1
            r6 = 0
            if (r4 == 0) goto L_0x000f
            r4 = 1
            goto L_0x0016
        L_0x000f:
            boolean r4 = r0.emojis
            if (r4 == 0) goto L_0x0015
            r4 = 5
            goto L_0x0016
        L_0x0015:
            r4 = 0
        L_0x0016:
            if (r14 != 0) goto L_0x0054
            if (r15 == 0) goto L_0x003f
            if (r16 == 0) goto L_0x003f
            org.telegram.ui.Components.StickerSetBulletinLayout r6 = new org.telegram.ui.Components.StickerSetBulletinLayout     // Catch:{ Exception -> 0x0075 }
            android.view.View r7 = r17.getFragmentView()     // Catch:{ Exception -> 0x0075 }
            android.content.Context r8 = r7.getContext()     // Catch:{ Exception -> 0x0075 }
            if (r18 != 0) goto L_0x002a
            r9 = r0
            goto L_0x002c
        L_0x002a:
            r9 = r18
        L_0x002c:
            r10 = 2
            r11 = 0
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r17.getResourceProvider()     // Catch:{ Exception -> 0x0075 }
            r7 = r6
            r7.<init>(r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x0075 }
            r0 = 1500(0x5dc, float:2.102E-42)
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.Bulletin.make((org.telegram.ui.ActionBar.BaseFragment) r1, (org.telegram.ui.Components.Bulletin.Layout) r6, (int) r0)     // Catch:{ Exception -> 0x0075 }
            r0.show()     // Catch:{ Exception -> 0x0075 }
        L_0x003f:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive     // Catch:{ Exception -> 0x0075 }
            if (r0 == 0) goto L_0x004c
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r20)     // Catch:{ Exception -> 0x0075 }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive r2 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive) r2     // Catch:{ Exception -> 0x0075 }
            r0.processStickerSetInstallResultArchive(r1, r5, r4, r2)     // Catch:{ Exception -> 0x0075 }
        L_0x004c:
            if (r3 == 0) goto L_0x0082
            java.lang.Boolean r0 = java.lang.Boolean.TRUE     // Catch:{ Exception -> 0x0075 }
            r3.run(r0)     // Catch:{ Exception -> 0x0075 }
            goto L_0x0082
        L_0x0054:
            if (r16 == 0) goto L_0x0077
            android.view.View r0 = r17.getFragmentView()     // Catch:{ Exception -> 0x0075 }
            android.content.Context r0 = r0.getContext()     // Catch:{ Exception -> 0x0075 }
            java.lang.String r1 = "ErrorOccurred"
            int r2 = org.telegram.messenger.R.string.ErrorOccurred     // Catch:{ Exception -> 0x0075 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0075 }
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r1, r6)     // Catch:{ Exception -> 0x0075 }
            r0.show()     // Catch:{ Exception -> 0x0075 }
            if (r3 == 0) goto L_0x0082
            java.lang.Boolean r0 = java.lang.Boolean.FALSE     // Catch:{ Exception -> 0x0075 }
            r3.run(r0)     // Catch:{ Exception -> 0x0075 }
            goto L_0x0082
        L_0x0075:
            r0 = move-exception
            goto L_0x007f
        L_0x0077:
            if (r3 == 0) goto L_0x0082
            java.lang.Boolean r0 = java.lang.Boolean.FALSE     // Catch:{ Exception -> 0x0075 }
            r3.run(r0)     // Catch:{ Exception -> 0x0075 }
            goto L_0x0082
        L_0x007f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0082:
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r20)
            r1 = 0
            r2 = 1
            r3 = 0
            org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda6 r5 = new org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda6
            r6 = r22
            r5.<init>(r6)
            r13 = r0
            r14 = r4
            r15 = r1
            r16 = r2
            r17 = r3
            r18 = r5
            r13.loadStickers(r14, r15, r16, r17, r18)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.lambda$installSet$5(org.telegram.tgnet.TLRPC$StickerSet, org.telegram.tgnet.TLRPC$TL_error, boolean, android.view.View, org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$TL_messages_stickerSet, org.telegram.tgnet.TLObject, int, org.telegram.messenger.Utilities$Callback, java.lang.Runnable):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$installSet$4(Runnable runnable, ArrayList arrayList) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void uninstallSet(BaseFragment baseFragment, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z, Runnable runnable) {
        if (baseFragment != null && tLRPC$TL_messages_stickerSet != null && baseFragment.getFragmentView() != null) {
            MediaDataController.getInstance(baseFragment.getCurrentAccount()).toggleStickerSet(baseFragment.getFragmentView().getContext(), tLRPC$TL_messages_stickerSet, 0, baseFragment, true, z, runnable);
        }
    }

    private void loadAnimation() {
        if (this.loadAnimator == null) {
            this.loadAnimator = ValueAnimator.ofFloat(new float[]{this.loadT, 1.0f});
            this.fromY = Float.valueOf(this.lastY + this.containerView.getY());
            this.loadAnimator.addUpdateListener(new EmojiPacksAlert$$ExternalSyntheticLambda0(this));
            this.loadAnimator.setDuration(250);
            this.loadAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.loadAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAnimation$7(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.loadT = floatValue;
        this.listView.setAlpha(floatValue);
        this.addButtonView.setAlpha(this.loadT);
        this.removeButtonView.setAlpha(this.loadT);
        this.containerView.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateButton() {
        TLRPC$StickerSet tLRPC$StickerSet;
        if (this.buttonsView != null) {
            ArrayList arrayList = this.customEmojiPacks.stickerSets == null ? new ArrayList() : new ArrayList(this.customEmojiPacks.stickerSets);
            int i = 0;
            while (i < arrayList.size()) {
                if (arrayList.get(i) == null) {
                    arrayList.remove(i);
                    i--;
                }
                i++;
            }
            MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) arrayList.get(i2);
                if (!(tLRPC$TL_messages_stickerSet == null || (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) == null)) {
                    if (!instance.isStickerPackInstalled(tLRPC$StickerSet.id)) {
                        arrayList3.add(tLRPC$TL_messages_stickerSet);
                    } else {
                        arrayList2.add(tLRPC$TL_messages_stickerSet);
                    }
                }
            }
            boolean isPremium = UserConfig.getInstance(this.currentAccount).isPremium();
            ArrayList arrayList4 = new ArrayList(arrayList3);
            int i3 = 0;
            while (i3 < arrayList4.size()) {
                if (MessageObject.isPremiumEmojiPack((TLRPC$TL_messages_stickerSet) arrayList4.get(i3)) && !isPremium) {
                    arrayList4.remove(i3);
                    i3--;
                }
                i3++;
            }
            boolean z = this.customEmojiPacks.inputStickerSets != null && arrayList.size() == this.customEmojiPacks.inputStickerSets.size();
            if (!this.loaded && z) {
                loadAnimation();
            }
            this.loaded = z;
            if (!z) {
                this.listView.setAlpha(0.0f);
            }
            if (!this.loaded) {
                this.premiumButtonView.setVisibility(8);
                this.addButtonView.setVisibility(8);
                this.removeButtonView.setVisibility(8);
                updateShowButton(false);
            } else if ((arrayList4.size() > 0 || arrayList3.size() < 0 || isPremium) && this.loaded) {
                this.premiumButtonView.setVisibility(4);
                if (arrayList4.size() > 0) {
                    this.addButtonView.setVisibility(0);
                    this.removeButtonView.setVisibility(8);
                    if (arrayList4.size() == 1) {
                        this.addButtonView.setText(LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList4.get(0)).documents.size(), new Object[0])));
                    } else {
                        this.addButtonView.setText(LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("EmojiPackCount", arrayList4.size(), new Object[0])));
                    }
                    this.addButtonView.setOnClickListener(new EmojiPacksAlert$$ExternalSyntheticLambda3(this, arrayList4));
                    updateShowButton(true);
                } else if (arrayList2.size() > 0) {
                    this.addButtonView.setVisibility(8);
                    this.removeButtonView.setVisibility(0);
                    if (arrayList2.size() == 1) {
                        this.removeButtonView.setText(LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList2.get(0)).documents.size(), new Object[0])));
                    } else {
                        this.removeButtonView.setText(LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("EmojiPackCount", arrayList2.size(), new Object[0])));
                    }
                    this.removeButtonView.setOnClickListener(new EmojiPacksAlert$$ExternalSyntheticLambda2(this, arrayList2));
                    updateShowButton(true);
                } else {
                    this.addButtonView.setVisibility(8);
                    this.removeButtonView.setVisibility(8);
                    updateShowButton(false);
                }
            } else {
                this.premiumButtonView.setVisibility(0);
                this.addButtonView.setVisibility(8);
                this.removeButtonView.setVisibility(8);
                updateShowButton(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$9(ArrayList arrayList, View view) {
        int size = arrayList.size();
        int[] iArr = new int[2];
        for (int i = 0; i < arrayList.size(); i++) {
            installSet(this.fragment, (TLObject) arrayList.get(i), size == 1, size > 1 ? new EmojiPacksAlert$$ExternalSyntheticLambda7(this, iArr, size, arrayList) : null, (Runnable) null);
        }
        onButtonClicked(true);
        if (size <= 1) {
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$8(int[] iArr, int i, ArrayList arrayList, Boolean bool) {
        iArr[0] = iArr[0] + 1;
        if (bool.booleanValue()) {
            iArr[1] = iArr[1] + 1;
        }
        if (iArr[0] == i && iArr[1] > 0) {
            dismiss();
            Bulletin.make(this.fragment, (Bulletin.Layout) new StickerSetBulletinLayout(this.fragment.getFragmentView().getContext(), (TLObject) arrayList.get(0), iArr[1], 2, (TLRPC$Document) null, this.fragment.getResourceProvider()), 1500).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$10(ArrayList arrayList, View view) {
        dismiss();
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            MediaDataController.getInstance(baseFragment.getCurrentAccount()).removeMultipleStickerSets(this.fragment.getFragmentView().getContext(), this.fragment, arrayList);
        } else {
            int i = 0;
            while (i < arrayList.size()) {
                uninstallSet(this.fragment, (TLRPC$TL_messages_stickerSet) arrayList.get(i), i == 0, (Runnable) null);
                i++;
            }
        }
        onButtonClicked(false);
    }

    /* access modifiers changed from: private */
    public int getListTop() {
        if (this.containerView == null) {
            return 0;
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || recyclerListView.getChildCount() < 1) {
            return this.containerView.getPaddingTop();
        }
        View childAt = this.listView.getChildAt(0);
        View view = this.paddingView;
        if (childAt != view) {
            return this.containerView.getPaddingTop();
        }
        return view.getBottom() + this.containerView.getPaddingTop();
    }

    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
    }

    public int getContainerViewHeight() {
        RecyclerListView recyclerListView = this.listView;
        int i = 0;
        int measuredHeight = (recyclerListView == null ? 0 : recyclerListView.getMeasuredHeight()) - getListTop();
        ViewGroup viewGroup = this.containerView;
        if (viewGroup != null) {
            i = viewGroup.getPaddingTop();
        }
        return measuredHeight + i + AndroidUtilities.navigationBarHeight + AndroidUtilities.dp(8.0f);
    }

    class SeparatorView extends View {
        public SeparatorView(EmojiPacksAlert emojiPacksAlert, Context context) {
            super(context);
            setBackgroundColor(emojiPacksAlert.getThemedColor("chat_emojiPanelShadowLine"));
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.getShadowHeight());
            layoutParams.topMargin = AndroidUtilities.dp(14.0f);
            setLayoutParams(layoutParams);
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = EmojiPacksAlert.this.paddingView;
            } else {
                boolean z = true;
                if (i == 1) {
                    EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
                    view = new EmojiImageView(emojiPacksAlert.getContext());
                } else if (i == 2) {
                    EmojiPacksAlert emojiPacksAlert2 = EmojiPacksAlert.this;
                    Context context = emojiPacksAlert2.getContext();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        z = false;
                    }
                    view = new EmojiPackHeader(emojiPacksAlert2, context, z);
                } else if (i == 3) {
                    view = new TextView(EmojiPacksAlert.this.getContext());
                } else if (i == 4) {
                    EmojiPacksAlert emojiPacksAlert3 = EmojiPacksAlert.this;
                    view = new SeparatorView(emojiPacksAlert3, emojiPacksAlert3.getContext());
                } else {
                    view = null;
                }
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            EmojiView.CustomEmoji customEmoji;
            ArrayList<TLRPC$Document> arrayList;
            int i2 = i - 1;
            int itemViewType = viewHolder.getItemViewType();
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
            int i3 = 0;
            boolean z = true;
            if (itemViewType == 1) {
                if (EmojiPacksAlert.this.hasDescription) {
                    i2--;
                }
                EmojiImageView emojiImageView = (EmojiImageView) viewHolder.itemView;
                int i4 = 0;
                while (true) {
                    if (i3 >= EmojiPacksAlert.this.customEmojiPacks.data.length) {
                        customEmoji = null;
                        break;
                    }
                    int size = EmojiPacksAlert.this.customEmojiPacks.data[i3].size();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        size = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, size);
                    }
                    if (i2 > i4 && i2 <= i4 + size) {
                        customEmoji = EmojiPacksAlert.this.customEmojiPacks.data[i3].get((i2 - i4) - 1);
                        break;
                    } else {
                        i4 += size + 1 + 1;
                        i3++;
                    }
                }
                AnimatedEmojiSpan animatedEmojiSpan = emojiImageView.span;
                if ((animatedEmojiSpan == null && customEmoji != null) || ((customEmoji == null && animatedEmojiSpan != null) || (customEmoji != null && animatedEmojiSpan.documentId != customEmoji.documentId))) {
                    if (customEmoji == null) {
                        emojiImageView.span = null;
                        return;
                    }
                    TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                    TLRPC$StickerSet tLRPC$StickerSet = customEmoji.stickerSet.set;
                    tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                    tLRPC$TL_inputStickerSetID.short_name = tLRPC$StickerSet.short_name;
                    tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                    emojiImageView.span = new AnimatedEmojiSpan(customEmoji.documentId, (Paint.FontMetricsInt) null);
                }
            } else if (itemViewType == 2) {
                if (EmojiPacksAlert.this.hasDescription && i2 > 0) {
                    i2--;
                }
                int i5 = 0;
                int i6 = 0;
                while (i5 < EmojiPacksAlert.this.customEmojiPacks.data.length) {
                    int size2 = EmojiPacksAlert.this.customEmojiPacks.data[i5].size();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        size2 = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, size2);
                    }
                    if (i2 == i6) {
                        break;
                    }
                    i6 += size2 + 1 + 1;
                    i5++;
                }
                if (EmojiPacksAlert.this.customEmojiPacks.stickerSets != null && i5 < EmojiPacksAlert.this.customEmojiPacks.stickerSets.size()) {
                    tLRPC$TL_messages_stickerSet = EmojiPacksAlert.this.customEmojiPacks.stickerSets.get(i5);
                }
                if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.documents != null) {
                    int i7 = 0;
                    while (true) {
                        if (i7 >= tLRPC$TL_messages_stickerSet.documents.size()) {
                            break;
                        } else if (!MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet.documents.get(i7))) {
                            break;
                        } else {
                            i7++;
                        }
                    }
                }
                z = false;
                if (i5 < EmojiPacksAlert.this.customEmojiPacks.data.length) {
                    EmojiPackHeader emojiPackHeader = (EmojiPackHeader) viewHolder.itemView;
                    if (!(tLRPC$TL_messages_stickerSet == null || (arrayList = tLRPC$TL_messages_stickerSet.documents) == null)) {
                        i3 = arrayList.size();
                    }
                    emojiPackHeader.set(tLRPC$TL_messages_stickerSet, i3, z);
                }
            } else if (itemViewType == 3) {
                TextView textView = (TextView) viewHolder.itemView;
                textView.setTextSize(1, 13.0f);
                textView.setTextColor(EmojiPacksAlert.this.getThemedColor("chat_emojiPanelTrendingDescription"));
                textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("PremiumPreviewEmojiPack", R.string.PremiumPreviewEmojiPack)));
                textView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(14.0f));
            }
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            int i2 = i - 1;
            if (EmojiPacksAlert.this.hasDescription) {
                if (i2 == 1) {
                    return 3;
                }
                if (i2 > 0) {
                    i2--;
                }
            }
            int i3 = 0;
            for (ArrayList<EmojiView.CustomEmoji> size : EmojiPacksAlert.this.customEmojiPacks.data) {
                if (i2 == i3) {
                    return 2;
                }
                int size2 = size.size();
                if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                    size2 = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, size2);
                }
                int i4 = i3 + size2 + 1;
                if (i2 == i4) {
                    return 4;
                }
                i3 = i4 + 1;
            }
            return 1;
        }

        public int getItemCount() {
            EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
            boolean unused = emojiPacksAlert.hasDescription = !UserConfig.getInstance(emojiPacksAlert.currentAccount).isPremium() && EmojiPacksAlert.this.customEmojiPacks.stickerSets != null && EmojiPacksAlert.this.customEmojiPacks.stickerSets.size() == 1 && MessageObject.isPremiumEmojiPack(EmojiPacksAlert.this.customEmojiPacks.stickerSets.get(0));
            return (EmojiPacksAlert.this.hasDescription ? 1 : 0) + true + EmojiPacksAlert.this.customEmojiPacks.getItemsCount() + Math.max(0, EmojiPacksAlert.this.customEmojiPacks.data.length - 1);
        }
    }

    /* access modifiers changed from: private */
    public void onSubItemClick(int i) {
        ArrayList<TLRPC$TL_messages_stickerSet> arrayList;
        String str;
        EmojiPacksLoader emojiPacksLoader = this.customEmojiPacks;
        if (emojiPacksLoader != null && (arrayList = emojiPacksLoader.stickerSets) != null && !arrayList.isEmpty()) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.customEmojiPacks.stickerSets.get(0);
            TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
            if (tLRPC$StickerSet == null || !tLRPC$StickerSet.emojis) {
                str = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/" + tLRPC$TL_messages_stickerSet.set.short_name;
            } else {
                str = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addemoji/" + tLRPC$TL_messages_stickerSet.set.short_name;
            }
            String str2 = str;
            if (i == 1) {
                Context context = null;
                BaseFragment baseFragment = this.fragment;
                if (baseFragment != null) {
                    context = baseFragment.getParentActivity();
                }
                if (context == null) {
                    context = getContext();
                }
                AnonymousClass8 r1 = new ShareAlert(context, (ArrayList) null, str2, false, str2, false, this.resourcesProvider) {
                    /* access modifiers changed from: protected */
                    public void onSend(androidx.collection.LongSparseArray<TLRPC$Dialog> longSparseArray, int i) {
                        AndroidUtilities.runOnUIThread(new EmojiPacksAlert$8$$ExternalSyntheticLambda0(this, longSparseArray, i), 100);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onSend$0(androidx.collection.LongSparseArray longSparseArray, int i) {
                        UndoView undoView;
                        if (EmojiPacksAlert.this.fragment instanceof ChatActivity) {
                            undoView = ((ChatActivity) EmojiPacksAlert.this.fragment).getUndoView();
                        } else {
                            undoView = EmojiPacksAlert.this.fragment instanceof ProfileActivity ? ((ProfileActivity) EmojiPacksAlert.this.fragment).getUndoView() : null;
                        }
                        UndoView undoView2 = undoView;
                        if (undoView2 == null) {
                            return;
                        }
                        if (longSparseArray.size() == 1) {
                            undoView2.showWithAction(((TLRPC$Dialog) longSparseArray.valueAt(0)).id, 53, (Object) Integer.valueOf(i));
                        } else {
                            undoView2.showWithAction(0, 53, (Object) Integer.valueOf(i), (Object) Integer.valueOf(longSparseArray.size()), (Runnable) null, (Runnable) null);
                        }
                    }
                };
                BaseFragment baseFragment2 = this.fragment;
                if (baseFragment2 != null) {
                    baseFragment2.showDialog(r1);
                } else {
                    r1.show();
                }
            } else if (i == 2) {
                try {
                    AndroidUtilities.addToClipboard(str2);
                    BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createCopyLinkBulletin().show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    private class EmojiImageView extends View {
        ValueAnimator backAnimator;
        public ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public ImageReceiver imageReceiver;
        /* access modifiers changed from: private */
        public float pressedProgress;
        public AnimatedEmojiSpan span;

        public EmojiImageView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM));
        }

        public void setPressed(boolean z) {
            ValueAnimator valueAnimator;
            if (isPressed() != z) {
                super.setPressed(z);
                invalidate();
                if (z && (valueAnimator = this.backAnimator) != null) {
                    valueAnimator.removeAllListeners();
                    this.backAnimator.cancel();
                }
                if (!z) {
                    float f = this.pressedProgress;
                    if (f != 0.0f) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f, 0.0f});
                        this.backAnimator = ofFloat;
                        ofFloat.addUpdateListener(new EmojiPacksAlert$EmojiImageView$$ExternalSyntheticLambda0(this));
                        this.backAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                super.onAnimationEnd(animator);
                                EmojiImageView.this.backAnimator = null;
                            }
                        });
                        this.backAnimator.setInterpolator(new OvershootInterpolator(5.0f));
                        this.backAnimator.setDuration(350);
                        this.backAnimator.start();
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setPressed$0(ValueAnimator valueAnimator) {
            this.pressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            EmojiPacksAlert.this.containerView.invalidate();
        }

        public void updatePressedProgress() {
            if (isPressed()) {
                float f = this.pressedProgress;
                if (f != 1.0f) {
                    this.pressedProgress = Utilities.clamp(f + 0.16f, 1.0f, 0.0f);
                    invalidate();
                }
            }
        }
    }

    private class EmojiPackHeader extends FrameLayout {
        public TextView addButtonView;
        private ValueAnimator animator;
        public BaseFragment dummyFragment = new BaseFragment() {
            public int getCurrentAccount() {
                return this.currentAccount;
            }

            public View getFragmentView() {
                return EmojiPackHeader.this.this$0.containerView;
            }

            public FrameLayout getLayoutContainer() {
                return (FrameLayout) EmojiPackHeader.this.this$0.containerView;
            }

            public Theme.ResourcesProvider getResourceProvider() {
                return EmojiPackHeader.this.this$0.resourcesProvider;
            }
        };
        public ActionBarMenuItem optionsButton;
        public TextView removeButtonView;
        /* access modifiers changed from: private */
        public TLRPC$TL_messages_stickerSet set;
        private boolean single;
        public TextView subtitleView;
        final /* synthetic */ EmojiPacksAlert this$0;
        public LinkSpanDrawable.LinksTextView titleView;
        private float toggleT = 0.0f;
        private boolean toggled = false;
        public PremiumButtonView unlockButtonView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public EmojiPackHeader(org.telegram.ui.Components.EmojiPacksAlert r27, android.content.Context r28, boolean r29) {
            /*
                r26 = this;
                r0 = r26
                r1 = r27
                r3 = r28
                r2 = r29
                r0.this$0 = r1
                r0.<init>(r3)
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$1 r4 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$1
                r4.<init>()
                r0.dummyFragment = r4
                r8 = 0
                r0.toggled = r8
                r4 = 0
                r0.toggleT = r4
                r0.single = r2
                r5 = 1101004800(0x41a00000, float:20.0)
                java.lang.String r6 = "fonts/rmedium.ttf"
                r9 = 1
                if (r2 != 0) goto L_0x021d
                int r7 = r27.currentAccount
                org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
                boolean r7 = r7.isPremium()
                r10 = 1082130432(0x40800000, float:4.0)
                r11 = 1098907648(0x41800000, float:16.0)
                r12 = 1073741824(0x40000000, float:2.0)
                r13 = 1105199104(0x41e00000, float:28.0)
                r14 = -2147483648(0xfffffffvar_, float:-0.0)
                r15 = 99999(0x1869f, float:1.40128E-40)
                r16 = 1090519040(0x41000000, float:8.0)
                if (r7 != 0) goto L_0x00e7
                org.telegram.ui.Components.Premium.PremiumButtonView r7 = new org.telegram.ui.Components.Premium.PremiumButtonView
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r7.<init>(r3, r4, r8)
                r0.unlockButtonView = r7
                int r4 = org.telegram.messenger.R.string.Unlock
                java.lang.String r10 = "Unlock"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda2 r10 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda2
                r10.<init>(r0)
                r7.setButton(r4, r10)
                org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.unlockButtonView
                int r7 = org.telegram.messenger.R.raw.unlock_icon
                r4.setIcon(r7)
                org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.unlockButtonView
                org.telegram.ui.Components.RLottieImageView r4 = r4.getIconView()
                android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
                android.view.ViewGroup$MarginLayoutParams r4 = (android.view.ViewGroup.MarginLayoutParams) r4
                r7 = 1065353216(0x3var_, float:1.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r4.leftMargin = r10
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r4.topMargin = r7
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r4.height = r7
                r4.width = r7
                org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.unlockButtonView
                org.telegram.ui.Components.AnimatedTextView r4 = r4.getTextView()
                android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
                android.view.ViewGroup$MarginLayoutParams r4 = (android.view.ViewGroup.MarginLayoutParams) r4
                r7 = 1077936128(0x40400000, float:3.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r4.leftMargin = r7
                org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.unlockButtonView
                android.view.View r4 = r4.getChildAt(r8)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r16)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
                r4.setPadding(r7, r8, r10, r8)
                org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.unlockButtonView
                r18 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r19 = 1105199104(0x41e00000, float:28.0)
                r20 = 8388661(0x800035, float:1.1755018E-38)
                r21 = 0
                r22 = 1098551132(0x417a8f5c, float:15.66)
                r23 = 1085611704(0x40b51eb8, float:5.66)
                r24 = 0
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r18, r19, r20, r21, r22, r23, r24)
                r0.addView(r4, r7)
                org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.unlockButtonView
                int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r14)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r12)
                r4.measure(r7, r10)
                org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.unlockButtonView
                int r4 = r4.getMeasuredWidth()
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
                int r4 = r4 + r7
                float r4 = (float) r4
                float r7 = org.telegram.messenger.AndroidUtilities.density
                float r16 = r4 / r7
                r4 = r16
                goto L_0x00e9
            L_0x00e7:
                r4 = 1090519040(0x41000000, float:8.0)
            L_0x00e9:
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r3)
                r0.addButtonView = r7
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r7.setTypeface(r10)
                android.widget.TextView r7 = r0.addButtonView
                java.lang.String r10 = "featuredStickers_buttonText"
                int r10 = r1.getThemedColor(r10)
                r7.setTextColor(r10)
                android.widget.TextView r7 = r0.addButtonView
                java.lang.String r10 = "featuredStickers_addButton"
                int r5 = r1.getThemedColor(r10)
                float[] r11 = new float[r9]
                r17 = 1082130432(0x40800000, float:4.0)
                r11[r8] = r17
                android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.filledRect((int) r5, (float[]) r11)
                r7.setBackground(r5)
                android.widget.TextView r5 = r0.addButtonView
                int r7 = org.telegram.messenger.R.string.Add
                java.lang.String r11 = "Add"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
                r5.setText(r7)
                android.widget.TextView r5 = r0.addButtonView
                r7 = 1099956224(0x41900000, float:18.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r5.setPadding(r11, r8, r7, r8)
                android.widget.TextView r5 = r0.addButtonView
                r7 = 17
                r5.setGravity(r7)
                android.widget.TextView r5 = r0.addButtonView
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda4 r11 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda4
                r11.<init>(r0)
                r5.setOnClickListener(r11)
                android.widget.TextView r5 = r0.addButtonView
                r19 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r20 = 1105199104(0x41e00000, float:28.0)
                r21 = 8388661(0x800035, float:1.1755018E-38)
                r22 = 0
                r23 = 1098551132(0x417a8f5c, float:15.66)
                r24 = 1085611704(0x40b51eb8, float:5.66)
                r25 = 0
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r19, r20, r21, r22, r23, r24, r25)
                r0.addView(r5, r11)
                android.widget.TextView r5 = r0.addButtonView
                int r11 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r14)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r12)
                r5.measure(r11, r9)
                android.widget.TextView r5 = r0.addButtonView
                int r5 = r5.getMeasuredWidth()
                r9 = 1098907648(0x41800000, float:16.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r5 = r5 + r11
                float r5 = (float) r5
                float r9 = org.telegram.messenger.AndroidUtilities.density
                float r5 = r5 / r9
                float r4 = java.lang.Math.max(r4, r5)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r3)
                r0.removeButtonView = r5
                android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r5.setTypeface(r9)
                android.widget.TextView r5 = r0.removeButtonView
                int r9 = r1.getThemedColor(r10)
                r5.setTextColor(r9)
                android.widget.TextView r5 = r0.removeButtonView
                r9 = 268435455(0xfffffff, float:2.5243547E-29)
                int r10 = r1.getThemedColor(r10)
                r9 = r9 & r10
                r10 = 4
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createRadSelectorDrawable(r9, r10, r10)
                r5.setBackground(r9)
                android.widget.TextView r5 = r0.removeButtonView
                int r9 = org.telegram.messenger.R.string.StickersRemove
                java.lang.String r10 = "StickersRemove"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
                r5.setText(r9)
                android.widget.TextView r5 = r0.removeButtonView
                r9 = 1094713344(0x41400000, float:12.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r10, r8, r9, r8)
                android.widget.TextView r5 = r0.removeButtonView
                r5.setGravity(r7)
                android.widget.TextView r5 = r0.removeButtonView
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda1 r7 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda1
                r7.<init>(r0)
                r5.setOnClickListener(r7)
                android.widget.TextView r5 = r0.removeButtonView
                r5.setClickable(r8)
                android.widget.TextView r5 = r0.removeButtonView
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r19, r20, r21, r22, r23, r24, r25)
                r0.addView(r5, r7)
                android.widget.TextView r5 = r0.removeButtonView
                r7 = 0
                r5.setScaleX(r7)
                android.widget.TextView r5 = r0.removeButtonView
                r5.setScaleY(r7)
                android.widget.TextView r5 = r0.removeButtonView
                r5.setAlpha(r7)
                android.widget.TextView r5 = r0.removeButtonView
                int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r14)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r12)
                r5.measure(r7, r9)
                android.widget.TextView r5 = r0.removeButtonView
                int r5 = r5.getMeasuredWidth()
                r7 = 1098907648(0x41800000, float:16.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r5 = r5 + r7
                float r5 = (float) r5
                float r7 = org.telegram.messenger.AndroidUtilities.density
                float r5 = r5 / r7
                float r4 = java.lang.Math.max(r4, r5)
                goto L_0x021f
            L_0x021d:
                r4 = 1107296256(0x42000000, float:32.0)
            L_0x021f:
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = new org.telegram.ui.Components.LinkSpanDrawable$LinksTextView
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r27.resourcesProvider
                r5.<init>(r3, r7)
                r0.titleView = r5
                r7 = 1073741824(0x40000000, float:2.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r5.setPadding(r9, r8, r7, r8)
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r5.setTypeface(r6)
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
                r5.setEllipsize(r6)
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                r6 = 1
                r5.setSingleLine(r6)
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                r5.setLines(r6)
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r27.resourcesProvider
                java.lang.String r7 = "windowBackgroundWhiteLinkText"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r7, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
                r5.setLinkTextColor(r6)
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                java.lang.String r6 = "dialogTextBlack"
                int r6 = r1.getThemedColor(r6)
                r5.setTextColor(r6)
                if (r2 == 0) goto L_0x028d
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                r6 = 1101004800(0x41a00000, float:20.0)
                r7 = 1
                r5.setTextSize(r7, r6)
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 8388659(0x800033, float:1.1755015E-38)
                r12 = 1094713344(0x41400000, float:12.0)
                r13 = 1093664768(0x41300000, float:11.0)
                r15 = 0
                r14 = r4
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r5, r6)
                goto L_0x02ab
            L_0x028d:
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                r6 = 1099431936(0x41880000, float:17.0)
                r7 = 1
                r5.setTextSize(r7, r6)
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r5 = r0.titleView
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 8388659(0x800033, float:1.1755015E-38)
                r12 = 1086324736(0x40CLASSNAME, float:6.0)
                r13 = 1092616192(0x41200000, float:10.0)
                r15 = 0
                r14 = r4
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r5, r6)
            L_0x02ab:
                if (r2 != 0) goto L_0x02ed
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r3)
                r0.subtitleView = r5
                r6 = 1095761920(0x41500000, float:13.0)
                r7 = 1
                r5.setTextSize(r7, r6)
                android.widget.TextView r5 = r0.subtitleView
                java.lang.String r6 = "dialogTextGray2"
                int r6 = r1.getThemedColor(r6)
                r5.setTextColor(r6)
                android.widget.TextView r5 = r0.subtitleView
                android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
                r5.setEllipsize(r6)
                android.widget.TextView r5 = r0.subtitleView
                r5.setSingleLine(r7)
                android.widget.TextView r5 = r0.subtitleView
                r5.setLines(r7)
                android.widget.TextView r5 = r0.subtitleView
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 8388659(0x800033, float:1.1755015E-38)
                r12 = 1090519040(0x41000000, float:8.0)
                r13 = 1107117998(0x41fd47ae, float:31.66)
                r15 = 0
                r14 = r4
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r5, r4)
            L_0x02ed:
                if (r2 == 0) goto L_0x0383
                org.telegram.ui.ActionBar.ActionBarMenuItem r9 = new org.telegram.ui.ActionBar.ActionBarMenuItem
                r4 = 0
                r5 = 0
                java.lang.String r2 = "key_sheet_other"
                int r6 = r1.getThemedColor(r2)
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r27.resourcesProvider
                r2 = r9
                r3 = r28
                r2.<init>((android.content.Context) r3, (org.telegram.ui.ActionBar.ActionBarMenu) r4, (int) r5, (int) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
                r0.optionsButton = r9
                r9.setLongClickEnabled(r8)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                r3 = 2
                r2.setSubMenuOpenSide(r3)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                int r4 = org.telegram.messenger.R.drawable.ic_ab_other
                r2.setIcon((int) r4)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                java.lang.String r4 = "player_actionBarSelector"
                int r4 = r1.getThemedColor(r4)
                r5 = 1
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4, r5)
                r2.setBackgroundDrawable(r4)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                r4 = 40
                r5 = 1109393408(0x42200000, float:40.0)
                r6 = 53
                r7 = 0
                r8 = 1084227584(0x40a00000, float:5.0)
                r9 = 1084227584(0x40a00000, float:5.0)
                int r10 = r27.backgroundPaddingLeft
                float r10 = (float) r10
                float r11 = org.telegram.messenger.AndroidUtilities.density
                float r10 = r10 / r11
                float r9 = r9 - r10
                r10 = 0
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10)
                r0.addView(r2, r4)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                int r4 = org.telegram.messenger.R.drawable.msg_share
                int r5 = org.telegram.messenger.R.string.StickersShare
                java.lang.String r6 = "StickersShare"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r6 = 1
                r2.addSubItem(r6, r4, r5)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                int r4 = org.telegram.messenger.R.drawable.msg_link
                int r5 = org.telegram.messenger.R.string.CopyLink
                java.lang.String r6 = "CopyLink"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r2.addSubItem(r3, r4, r5)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda3 r3 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda3
                r3.<init>(r0)
                r2.setOnClickListener(r3)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda6 r3 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda6
                r3.<init>(r1)
                r2.setDelegate(r3)
                org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
                int r2 = org.telegram.messenger.R.string.AccDescrMoreOptions
                java.lang.String r3 = "AccDescrMoreOptions"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setContentDescription(r2)
            L_0x0383:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.EmojiPackHeader.<init>(org.telegram.ui.Components.EmojiPacksAlert, android.content.Context, boolean):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            long unused = this.this$0.premiumButtonClicked = SystemClock.elapsedRealtime();
            this.this$0.showPremiumAlert();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            EmojiPacksAlert.installSet(this.dummyFragment, this.set, true);
            toggle(true, true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            EmojiPacksAlert.uninstallSet(this.dummyFragment, this.set, true, new EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda5(this));
            toggle(false, true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2() {
            toggle(true, true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            this.optionsButton.toggleSubMenu();
        }

        /* access modifiers changed from: private */
        public void toggle(boolean z, boolean z2) {
            if (this.toggled != z) {
                this.toggled = z;
                ValueAnimator valueAnimator = this.animator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.animator = null;
                }
                TextView textView = this.addButtonView;
                if (textView != null && this.removeButtonView != null) {
                    textView.setClickable(!z);
                    this.removeButtonView.setClickable(z);
                    float f = 1.0f;
                    if (z2) {
                        float[] fArr = new float[2];
                        fArr[0] = this.toggleT;
                        if (!z) {
                            f = 0.0f;
                        }
                        fArr[1] = f;
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                        this.animator = ofFloat;
                        ofFloat.addUpdateListener(new EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda0(this));
                        this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        this.animator.setDuration(250);
                        this.animator.start();
                        return;
                    }
                    this.toggleT = z ? 1.0f : 0.0f;
                    this.addButtonView.setScaleX(z ? 0.0f : 1.0f);
                    this.addButtonView.setScaleY(z ? 0.0f : 1.0f);
                    this.addButtonView.setAlpha(z ? 0.0f : 1.0f);
                    this.removeButtonView.setScaleX(z ? 1.0f : 0.0f);
                    this.removeButtonView.setScaleY(z ? 1.0f : 0.0f);
                    TextView textView2 = this.removeButtonView;
                    if (!z) {
                        f = 0.0f;
                    }
                    textView2.setAlpha(f);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$toggle$6(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.toggleT = floatValue;
            this.addButtonView.setScaleX(1.0f - floatValue);
            this.addButtonView.setScaleY(1.0f - this.toggleT);
            this.addButtonView.setAlpha(1.0f - this.toggleT);
            this.removeButtonView.setScaleX(this.toggleT);
            this.removeButtonView.setScaleY(this.toggleT);
            this.removeButtonView.setAlpha(this.toggleT);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:64:0x007b, code lost:
            r3 = r3;
         */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0080  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x0091  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00b4  */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x00c8  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void set(org.telegram.tgnet.TLRPC$TL_messages_stickerSet r10, int r11, boolean r12) {
            /*
                r9 = this;
                r9.set = r10
                r0 = 0
                r1 = 0
                if (r10 == 0) goto L_0x0088
                org.telegram.tgnet.TLRPC$StickerSet r2 = r10.set
                if (r2 == 0) goto L_0x0088
                java.util.regex.Pattern r2 = org.telegram.ui.Components.EmojiPacksAlert.urlPattern     // Catch:{ Exception -> 0x0076 }
                if (r2 != 0) goto L_0x0019
                java.lang.String r2 = "@[a-zA-Z\\d_]{1,32}"
                java.util.regex.Pattern r2 = java.util.regex.Pattern.compile(r2)     // Catch:{ Exception -> 0x0076 }
                java.util.regex.Pattern unused = org.telegram.ui.Components.EmojiPacksAlert.urlPattern = r2     // Catch:{ Exception -> 0x0076 }
            L_0x0019:
                java.util.regex.Pattern r2 = org.telegram.ui.Components.EmojiPacksAlert.urlPattern     // Catch:{ Exception -> 0x0076 }
                org.telegram.tgnet.TLRPC$StickerSet r3 = r10.set     // Catch:{ Exception -> 0x0076 }
                java.lang.String r3 = r3.title     // Catch:{ Exception -> 0x0076 }
                java.util.regex.Matcher r2 = r2.matcher(r3)     // Catch:{ Exception -> 0x0076 }
                r3 = r0
            L_0x0026:
                boolean r4 = r2.find()     // Catch:{ Exception -> 0x0073 }
                if (r4 == 0) goto L_0x007b
                if (r3 != 0) goto L_0x0046
                android.text.SpannableStringBuilder r4 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0073 }
                org.telegram.tgnet.TLRPC$StickerSet r5 = r10.set     // Catch:{ Exception -> 0x0073 }
                java.lang.String r5 = r5.title     // Catch:{ Exception -> 0x0073 }
                r4.<init>(r5)     // Catch:{ Exception -> 0x0073 }
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r3 = r9.titleView     // Catch:{ Exception -> 0x0043 }
                org.telegram.ui.Components.EmojiPacksAlert$LinkMovementMethodMy r5 = new org.telegram.ui.Components.EmojiPacksAlert$LinkMovementMethodMy     // Catch:{ Exception -> 0x0043 }
                r5.<init>()     // Catch:{ Exception -> 0x0043 }
                r3.setMovementMethod(r5)     // Catch:{ Exception -> 0x0043 }
                r3 = r4
                goto L_0x0046
            L_0x0043:
                r2 = move-exception
                r0 = r4
                goto L_0x0077
            L_0x0046:
                int r4 = r2.start()     // Catch:{ Exception -> 0x0073 }
                int r5 = r2.end()     // Catch:{ Exception -> 0x0073 }
                org.telegram.tgnet.TLRPC$StickerSet r6 = r10.set     // Catch:{ Exception -> 0x0073 }
                java.lang.String r6 = r6.title     // Catch:{ Exception -> 0x0073 }
                char r6 = r6.charAt(r4)     // Catch:{ Exception -> 0x0073 }
                r7 = 64
                if (r6 == r7) goto L_0x005c
                int r4 = r4 + 1
            L_0x005c:
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$2 r6 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$2     // Catch:{ Exception -> 0x0073 }
                org.telegram.tgnet.TLRPC$StickerSet r7 = r10.set     // Catch:{ Exception -> 0x0073 }
                java.lang.String r7 = r7.title     // Catch:{ Exception -> 0x0073 }
                int r8 = r4 + 1
                java.lang.CharSequence r7 = r7.subSequence(r8, r5)     // Catch:{ Exception -> 0x0073 }
                java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0073 }
                r6.<init>(r7)     // Catch:{ Exception -> 0x0073 }
                r3.setSpan(r6, r4, r5, r1)     // Catch:{ Exception -> 0x0073 }
                goto L_0x0026
            L_0x0073:
                r2 = move-exception
                r0 = r3
                goto L_0x0077
            L_0x0076:
                r2 = move-exception
            L_0x0077:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
                r3 = r0
            L_0x007b:
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r0 = r9.titleView
                if (r3 == 0) goto L_0x0080
                goto L_0x0084
            L_0x0080:
                org.telegram.tgnet.TLRPC$StickerSet r2 = r10.set
                java.lang.String r3 = r2.title
            L_0x0084:
                r0.setText(r3)
                goto L_0x008d
            L_0x0088:
                org.telegram.ui.Components.LinkSpanDrawable$LinksTextView r2 = r9.titleView
                r2.setText(r0)
            L_0x008d:
                android.widget.TextView r0 = r9.subtitleView
                if (r0 == 0) goto L_0x009c
                java.lang.Object[] r2 = new java.lang.Object[r1]
                java.lang.String r3 = "EmojiCount"
                java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r3, r11, r2)
                r0.setText(r11)
            L_0x009c:
                r11 = 8
                if (r12 == 0) goto L_0x00c8
                org.telegram.ui.Components.Premium.PremiumButtonView r12 = r9.unlockButtonView
                if (r12 == 0) goto L_0x00c8
                org.telegram.ui.Components.EmojiPacksAlert r12 = r9.this$0
                int r12 = r12.currentAccount
                org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)
                boolean r12 = r12.isPremium()
                if (r12 != 0) goto L_0x00c8
                org.telegram.ui.Components.Premium.PremiumButtonView r10 = r9.unlockButtonView
                r10.setVisibility(r1)
                android.widget.TextView r10 = r9.addButtonView
                if (r10 == 0) goto L_0x00c0
                r10.setVisibility(r11)
            L_0x00c0:
                android.widget.TextView r10 = r9.removeButtonView
                if (r10 == 0) goto L_0x00f9
                r10.setVisibility(r11)
                goto L_0x00f9
            L_0x00c8:
                org.telegram.ui.Components.Premium.PremiumButtonView r12 = r9.unlockButtonView
                if (r12 == 0) goto L_0x00cf
                r12.setVisibility(r11)
            L_0x00cf:
                android.widget.TextView r11 = r9.addButtonView
                if (r11 == 0) goto L_0x00d6
                r11.setVisibility(r1)
            L_0x00d6:
                android.widget.TextView r11 = r9.removeButtonView
                if (r11 == 0) goto L_0x00dd
                r11.setVisibility(r1)
            L_0x00dd:
                if (r10 == 0) goto L_0x00f5
                org.telegram.ui.Components.EmojiPacksAlert r11 = r9.this$0
                int r11 = r11.currentAccount
                org.telegram.messenger.MediaDataController r11 = org.telegram.messenger.MediaDataController.getInstance(r11)
                org.telegram.tgnet.TLRPC$StickerSet r10 = r10.set
                long r2 = r10.id
                boolean r10 = r11.isStickerPackInstalled((long) r2)
                if (r10 == 0) goto L_0x00f5
                r10 = 1
                goto L_0x00f6
            L_0x00f5:
                r10 = 0
            L_0x00f6:
                r9.toggle(r10, r1)
            L_0x00f9:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.EmojiPackHeader.set(org.telegram.tgnet.TLRPC$TL_messages_stickerSet, int, boolean):void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.single ? 42.0f : 56.0f), NUM));
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    class EmojiPacksLoader implements NotificationCenter.NotificationCenterDelegate {
        private int currentAccount;
        public ArrayList<EmojiView.CustomEmoji>[] data;
        public ArrayList<TLRPC$InputStickerSet> inputStickerSets;
        public ArrayList<TLRPC$TL_messages_stickerSet> stickerSets;

        /* access modifiers changed from: protected */
        public void onUpdate() {
            throw null;
        }

        public EmojiPacksLoader(int i, ArrayList<TLRPC$InputStickerSet> arrayList) {
            this.currentAccount = i;
            this.inputStickerSets = arrayList == null ? new ArrayList<>() : arrayList;
            init();
        }

        private void init() {
            TLRPC$StickerSet tLRPC$StickerSet;
            this.stickerSets = new ArrayList<>(this.inputStickerSets.size());
            this.data = new ArrayList[this.inputStickerSets.size()];
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            boolean[] zArr = new boolean[1];
            int i = 0;
            while (i < this.data.length) {
                TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSets.get(i), false, new EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda2(this, zArr));
                if (this.data.length != 1 || stickerSet == null || (tLRPC$StickerSet = stickerSet.set) == null || tLRPC$StickerSet.emojis) {
                    this.stickerSets.add(stickerSet);
                    putStickerSet(i, stickerSet);
                    i++;
                } else {
                    AndroidUtilities.runOnUIThread(new EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda1(this));
                    new StickersAlert(EmojiPacksAlert.this.getContext(), EmojiPacksAlert.this.fragment, this.inputStickerSets.get(i), (TLRPC$TL_messages_stickerSet) null, EmojiPacksAlert.this.fragment instanceof ChatActivity ? ((ChatActivity) EmojiPacksAlert.this.fragment).getChatActivityEnterView() : null, EmojiPacksAlert.this.resourcesProvider).show();
                    return;
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$init$1(boolean[] zArr) {
            if (!zArr[0]) {
                zArr[0] = true;
                AndroidUtilities.runOnUIThread(new EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$init$0() {
            EmojiPacksAlert.this.dismiss();
            if (EmojiPacksAlert.this.fragment != null && EmojiPacksAlert.this.fragment.getParentActivity() != null) {
                BulletinFactory.of(EmojiPacksAlert.this.fragment).createErrorBulletin(LocaleController.getString("AddEmojiNotFound", R.string.AddEmojiNotFound)).show();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$init$2() {
            EmojiPacksAlert.this.dismiss();
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            TLRPC$StickerSet tLRPC$StickerSet;
            if (i == NotificationCenter.groupStickersDidLoad) {
                for (int i3 = 0; i3 < this.stickerSets.size(); i3++) {
                    if (this.stickerSets.get(i3) == null) {
                        TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSets.get(i3), true);
                        if (this.stickerSets.size() != 1 || stickerSet == null || (tLRPC$StickerSet = stickerSet.set) == null || tLRPC$StickerSet.emojis) {
                            this.stickerSets.set(i3, stickerSet);
                            if (stickerSet != null) {
                                putStickerSet(i3, stickerSet);
                            }
                        } else {
                            EmojiPacksAlert.this.dismiss();
                            new StickersAlert(EmojiPacksAlert.this.getContext(), EmojiPacksAlert.this.fragment, this.inputStickerSets.get(i3), (TLRPC$TL_messages_stickerSet) null, EmojiPacksAlert.this.fragment instanceof ChatActivity ? ((ChatActivity) EmojiPacksAlert.this.fragment).getChatActivityEnterView() : null, EmojiPacksAlert.this.resourcesProvider).show();
                            return;
                        }
                    }
                }
                onUpdate();
            }
        }

        private void putStickerSet(int i, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            if (i >= 0) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
                if (i < arrayListArr.length) {
                    int i2 = 0;
                    if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents == null) {
                        arrayListArr[i] = new ArrayList<>(12);
                        while (i2 < 12) {
                            this.data[i].add((Object) null);
                            i2++;
                        }
                        return;
                    }
                    arrayListArr[i] = new ArrayList<>();
                    while (i2 < tLRPC$TL_messages_stickerSet.documents.size()) {
                        TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i2);
                        if (tLRPC$Document == null) {
                            this.data[i].add((Object) null);
                        } else {
                            EmojiView.CustomEmoji customEmoji = new EmojiView.CustomEmoji();
                            findEmoticon(tLRPC$TL_messages_stickerSet, tLRPC$Document.id);
                            customEmoji.stickerSet = tLRPC$TL_messages_stickerSet;
                            customEmoji.documentId = tLRPC$Document.id;
                            this.data[i].add(customEmoji);
                        }
                        i2++;
                    }
                }
            }
        }

        public int getItemsCount() {
            int i;
            int i2 = 0;
            if (this.data == null) {
                return 0;
            }
            int i3 = 0;
            while (true) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
                if (i2 >= arrayListArr.length) {
                    return i3;
                }
                if (arrayListArr[i2] != null) {
                    if (arrayListArr.length == 1) {
                        i = arrayListArr[i2].size();
                    } else {
                        i = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, this.data[i2].size());
                    }
                    i3 = i3 + i + 1;
                }
                i2++;
            }
        }

        public String findEmoticon(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, long j) {
            if (tLRPC$TL_messages_stickerSet == null) {
                return null;
            }
            for (int i = 0; i < tLRPC$TL_messages_stickerSet.packs.size(); i++) {
                TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i);
                ArrayList<Long> arrayList = tLRPC$TL_stickerPack.documents;
                if (arrayList != null && arrayList.contains(Long.valueOf(j))) {
                    return tLRPC$TL_stickerPack.emoticon;
                }
            }
            return null;
        }
    }
}

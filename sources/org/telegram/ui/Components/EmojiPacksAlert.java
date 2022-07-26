package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;

public class EmojiPacksAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private TextView addButtonView;
    /* access modifiers changed from: private */
    public LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private FrameLayout buttonsView;
    /* access modifiers changed from: private */
    public EmojiPacksLoader customEmojiPacks;
    boolean first = false;
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
    /* access modifiers changed from: private */
    public long premiumButtonClicked;
    private PremiumButtonView premiumButtonView;
    /* access modifiers changed from: private */
    public CircularProgressDrawable progressDrawable;
    /* access modifiers changed from: private */
    public TextView removeButtonView;
    /* access modifiers changed from: private */
    public View shadowView;

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onButtonClicked(boolean z) {
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public EmojiPacksAlert(org.telegram.ui.ActionBar.BaseFragment r18, android.content.Context r19, org.telegram.ui.ActionBar.Theme.ResourcesProvider r20, java.util.ArrayList<org.telegram.tgnet.TLRPC$InputStickerSet> r21) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = r20
            r4 = r21
            r5 = 0
            r0.<init>(r2, r5, r3)
            r0.first = r5
            r6 = 1
            r0.loaded = r6
            r21.size()
            r0.fragment = r1
            r17.fixNavigationBar()
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
            org.telegram.ui.Components.EmojiPacksAlert$2 r7 = new org.telegram.ui.Components.EmojiPacksAlert$2
            r7.<init>(r2)
            r0.containerView = r7
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
            r8 = 1090519040(0x41000000, float:8.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r11 = 1116209152(0x42880000, float:68.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r7.setPadding(r9, r5, r8, r12)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            org.telegram.ui.Components.EmojiPacksAlert$Adapter r8 = new org.telegram.ui.Components.EmojiPacksAlert$Adapter
            r9 = 0
            r8.<init>()
            r7.setAdapter(r8)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            androidx.recyclerview.widget.GridLayoutManager r8 = new androidx.recyclerview.widget.GridLayoutManager
            r9 = 8
            r8.<init>(r2, r9)
            r0.gridLayoutManager = r8
            r7.setLayoutManager(r8)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            org.telegram.ui.Components.EmojiPacksAlert$5 r8 = new org.telegram.ui.Components.EmojiPacksAlert$5
            r8.<init>()
            r7.addItemDecoration(r8)
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda8 r8 = new org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda8
            r8.<init>(r0, r4, r1, r3)
            r7.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r8)
            androidx.recyclerview.widget.GridLayoutManager r3 = r0.gridLayoutManager
            r3.setReverseLayout(r5)
            androidx.recyclerview.widget.GridLayoutManager r3 = r0.gridLayoutManager
            org.telegram.ui.Components.EmojiPacksAlert$6 r4 = new org.telegram.ui.Components.EmojiPacksAlert$6
            r4.<init>()
            r3.setSpanSizeLookup(r4)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            r7 = 51
            r8 = -1
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r8, (int) r7)
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
            r12 = 1065353216(0x3var_, float:1.0)
            float r13 = org.telegram.messenger.AndroidUtilities.density
            float r12 = r12 / r13
            r13 = 80
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((float) r7, (float) r12, (int) r13)
            r3.addView(r4, r7)
            android.view.View r3 = r0.shadowView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
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
            r11 = 87
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r7, (int) r11)
            r3.addView(r4, r7)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r2)
            r0.addButtonView = r3
            r3.setVisibility(r9)
            android.widget.TextView r3 = r0.addButtonView
            int r4 = r0.getThemedColor(r10)
            float[] r7 = new float[r6]
            r8 = 1086324736(0x40CLASSNAME, float:6.0)
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
            r10 = -1
            r11 = 1111490560(0x42400000, float:48.0)
            r12 = 80
            r13 = 1094713344(0x41400000, float:12.0)
            r14 = 1092616192(0x41200000, float:10.0)
            r15 = 1094713344(0x41400000, float:12.0)
            r16 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r3.addView(r8, r10)
            org.telegram.ui.Components.EmojiPacksAlert$7 r3 = new org.telegram.ui.Components.EmojiPacksAlert$7
            r3.<init>(r0, r2)
            r0.removeButtonView = r3
            r3.setVisibility(r9)
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
            r2 = 2131558588(0x7f0d00bc, float:1.8742496E38)
            r3.setIcon(r2)
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
            r17.updateButton()
            int r1 = r18.getCurrentAccount()
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r2 = 5
            r1.checkStickers(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, android.content.Context, org.telegram.ui.ActionBar.Theme$ResourcesProvider, java.util.ArrayList):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ArrayList arrayList, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, View view, int i) {
        if (arrayList != null && arrayList.size() > 1 && SystemClock.elapsedRealtime() - this.premiumButtonClicked >= 250) {
            int i2 = 0;
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
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (arrayList2 == null || i2 >= arrayList2.size()) ? null : this.customEmojiPacks.stickerSets.get(i2);
            if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.set != null) {
                ArrayList arrayList3 = new ArrayList();
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                arrayList3.add(tLRPC$TL_inputStickerSetID);
                new EmojiPacksAlert(baseFragment, getContext(), resourcesProvider, arrayList3).show();
            }
        }
    }

    private void updateShowButton(boolean z, boolean z2) {
        float dp = (float) (this.removeButtonView.getVisibility() == 0 ? AndroidUtilities.dp(19.0f) : 0);
        float f = 1.0f;
        float f2 = 0.0f;
        if (z2) {
            ViewPropertyAnimator duration = this.buttonsView.animate().translationY(z ? dp : (float) AndroidUtilities.dp(16.0f)).alpha(z ? 1.0f : 0.0f).setDuration(250);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            ViewPropertyAnimator translationY = this.shadowView.animate().translationY(z ? -(((float) AndroidUtilities.dp(68.0f)) - dp) : 0.0f);
            if (!z) {
                f = 0.0f;
            }
            translationY.alpha(f).setDuration(250).setInterpolator(cubicBezierInterpolator).start();
            ViewPropertyAnimator animate = this.listView.animate();
            if (!z) {
                f2 = ((float) AndroidUtilities.dp(68.0f)) - dp;
            }
            animate.translationY(f2).setDuration(250).setInterpolator(cubicBezierInterpolator).start();
            return;
        }
        this.buttonsView.setAlpha(z ? 1.0f : 0.0f);
        this.buttonsView.setTranslationY(z ? dp : (float) AndroidUtilities.dp(16.0f));
        View view = this.shadowView;
        if (!z) {
            f = 0.0f;
        }
        view.setAlpha(f);
        this.shadowView.setTranslationY(z ? -(((float) AndroidUtilities.dp(68.0f)) - dp) : 0.0f);
        RecyclerListView recyclerListView = this.listView;
        if (!z) {
            f2 = ((float) AndroidUtilities.dp(68.0f)) - dp;
        }
        recyclerListView.setTranslationY(f2);
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
            ((LaunchActivity) getContext()).lambda$runLinkRequest$60(new PremiumPreviewFragment((String) null));
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

    public static void installSet(BaseFragment baseFragment, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z) {
        installSet(baseFragment, tLRPC$TL_messages_stickerSet, z, (Utilities.Callback<Boolean>) null, (Runnable) null);
    }

    public static void installSet(BaseFragment baseFragment, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z, Utilities.Callback<Boolean> callback, Runnable runnable) {
        if (tLRPC$TL_messages_stickerSet != null && baseFragment != null) {
            if (!MediaDataController.getInstance(baseFragment.getCurrentAccount()).cancelRemovingStickerSet(tLRPC$TL_messages_stickerSet.set.id)) {
                TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                tLRPC$TL_messages_installStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
                TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                ConnectionsManager.getInstance(baseFragment.getCurrentAccount()).sendRequest(tLRPC$TL_messages_installStickerSet, new EmojiPacksAlert$$ExternalSyntheticLambda7(tLRPC$TL_messages_stickerSet, z, baseFragment, callback, runnable));
            } else if (callback != null) {
                callback.run(Boolean.TRUE);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$installSet$2(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_error tLRPC$TL_error, boolean z, BaseFragment baseFragment, TLObject tLObject, Utilities.Callback callback, Runnable runnable) {
        int i;
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        if (tLRPC$StickerSet.masks) {
            i = 1;
        } else {
            i = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        if (tLRPC$TL_error == null) {
            if (z) {
                try {
                    if (baseFragment.getFragmentView() != null) {
                        Bulletin.make(baseFragment, (Bulletin.Layout) new StickerSetBulletinLayout(baseFragment.getFragmentView().getContext(), tLRPC$TL_messages_stickerSet, 2, (TLRPC$Document) null, baseFragment.getResourceProvider()), 1500).show();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                MediaDataController.getInstance(baseFragment.getCurrentAccount()).processStickerSetInstallResultArchive(baseFragment, true, i, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
            }
            if (callback != null) {
                callback.run(Boolean.TRUE);
            }
        } else if (baseFragment.getFragmentView() != null) {
            Toast.makeText(baseFragment.getFragmentView().getContext(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
            if (callback != null) {
                callback.run(Boolean.FALSE);
            }
        } else if (callback != null) {
            callback.run(Boolean.FALSE);
        }
        MediaDataController.getInstance(baseFragment.getCurrentAccount()).loadStickers(i, false, true, false, new EmojiPacksAlert$$ExternalSyntheticLambda5(runnable));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$installSet$1(Runnable runnable, ArrayList arrayList) {
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
            this.loadT = 0.0f;
            this.loadAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.fromY = Float.valueOf(this.lastY + this.containerView.getY());
            this.loadAnimator.addUpdateListener(new EmojiPacksAlert$$ExternalSyntheticLambda0(this));
            this.loadAnimator.setDuration(250);
            this.loadAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.loadAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAnimation$4(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.loadT = floatValue;
        this.listView.setAlpha(floatValue);
        this.addButtonView.setAlpha(this.loadT);
        this.removeButtonView.setAlpha(this.loadT);
        this.premiumButtonView.setAlpha(this.loadT);
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
            if (!this.loaded && arrayList.size() > 0) {
                loadAnimation();
            }
            boolean z = arrayList.size() > 0;
            this.loaded = z;
            if (!z) {
                this.listView.setAlpha(0.0f);
            }
            if (arrayList4.size() > 0 || arrayList3.size() <= 0 || isPremium) {
                this.premiumButtonView.setVisibility(4);
                if (arrayList4.size() > 0) {
                    this.addButtonView.setVisibility(0);
                    this.removeButtonView.setVisibility(8);
                    if (arrayList4.size() == 1) {
                        this.addButtonView.setText(LocaleController.formatString("AddStickersCount", NUM, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList4.get(0)).documents.size(), new Object[0])));
                    } else {
                        this.addButtonView.setText(LocaleController.formatString("AddStickersCount", NUM, LocaleController.formatPluralString("EmojiPackCount", arrayList4.size(), new Object[0])));
                    }
                    this.addButtonView.setOnClickListener(new EmojiPacksAlert$$ExternalSyntheticLambda2(this, arrayList4));
                    updateShowButton(true, !this.first);
                } else if (arrayList2.size() > 0) {
                    this.addButtonView.setVisibility(8);
                    this.removeButtonView.setVisibility(0);
                    if (arrayList2.size() == 1) {
                        this.removeButtonView.setText(LocaleController.formatString("RemoveStickersCount", NUM, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList2.get(0)).documents.size(), new Object[0])));
                    } else {
                        this.removeButtonView.setText(LocaleController.formatString("RemoveStickersCount", NUM, LocaleController.formatPluralString("EmojiPackCount", arrayList2.size(), new Object[0])));
                    }
                    this.removeButtonView.setOnClickListener(new EmojiPacksAlert$$ExternalSyntheticLambda3(this, arrayList2));
                    updateShowButton(true, !this.first);
                } else {
                    this.addButtonView.setVisibility(8);
                    this.removeButtonView.setVisibility(8);
                    updateShowButton(false, !this.first);
                }
            } else {
                this.premiumButtonView.setVisibility(0);
                this.addButtonView.setVisibility(8);
                this.removeButtonView.setVisibility(8);
                this.premiumButtonView.setButton(LocaleController.getString("UnlockPremiumEmoji", NUM), new EmojiPacksAlert$$ExternalSyntheticLambda1(this));
                updateShowButton(true, !this.first);
            }
            this.first = false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$5(View view) {
        showPremiumAlert();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$7(ArrayList arrayList, View view) {
        int size = arrayList.size();
        int[] iArr = new int[2];
        for (int i = 0; i < arrayList.size(); i++) {
            installSet(this.fragment, (TLRPC$TL_messages_stickerSet) arrayList.get(i), size == 1, size > 1 ? new EmojiPacksAlert$$ExternalSyntheticLambda6(this, iArr, size, arrayList) : null, (Runnable) null);
        }
        onButtonClicked(true);
        if (size <= 1) {
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$6(int[] iArr, int i, ArrayList arrayList, Boolean bool) {
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
    public /* synthetic */ void lambda$updateButton$8(ArrayList arrayList, View view) {
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

    private class Adapter extends RecyclerView.Adapter {
        private Adapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = EmojiPacksAlert.this.paddingView;
            } else {
                boolean z = true;
                if (i == 1) {
                    EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
                    view = new EmojiImageView(emojiPacksAlert, emojiPacksAlert.getContext());
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
                textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("PremiumPreviewEmojiPack", NUM)));
                textView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f));
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
                ShareAlert shareAlert = new ShareAlert(context, (ArrayList<MessageObject>) null, str2, false, str2, false, this.resourcesProvider);
                BaseFragment baseFragment2 = this.fragment;
                if (baseFragment2 != null) {
                    baseFragment2.showDialog(shareAlert);
                } else {
                    shareAlert.show();
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
        public ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public ImageReceiver imageReceiver;
        public AnimatedEmojiSpan span;

        public EmojiImageView(EmojiPacksAlert emojiPacksAlert, Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM));
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
        public TextView titleView;
        private float toggleT = 0.0f;
        private boolean toggled = false;
        public PremiumButtonView unlockButtonView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public EmojiPackHeader(org.telegram.ui.Components.EmojiPacksAlert r26, android.content.Context r27, boolean r28) {
            /*
                r25 = this;
                r0 = r25
                r1 = r26
                r3 = r27
                r2 = r28
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
                r7 = 1090519040(0x41000000, float:8.0)
                if (r2 != 0) goto L_0x021a
                int r10 = r26.currentAccount
                org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)
                boolean r10 = r10.isPremium()
                r11 = 1082130432(0x40800000, float:4.0)
                r12 = 1098907648(0x41800000, float:16.0)
                r13 = 1073741824(0x40000000, float:2.0)
                r14 = 1105199104(0x41e00000, float:28.0)
                r15 = -2147483648(0xfffffffvar_, float:-0.0)
                r4 = 99999(0x1869f, float:1.40128E-40)
                if (r10 != 0) goto L_0x00e4
                org.telegram.ui.Components.Premium.PremiumButtonView r10 = new org.telegram.ui.Components.Premium.PremiumButtonView
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r10.<init>(r3, r9, r8)
                r0.unlockButtonView = r10
                r9 = 2131628809(0x7f0e1309, float:1.8884921E38)
                java.lang.String r11 = "Unlock"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda2 r11 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda2
                r11.<init>(r0)
                r10.setButton(r9, r11)
                org.telegram.ui.Components.Premium.PremiumButtonView r9 = r0.unlockButtonView
                r10 = 2131558588(0x7f0d00bc, float:1.8742496E38)
                r9.setIcon(r10)
                org.telegram.ui.Components.Premium.PremiumButtonView r9 = r0.unlockButtonView
                org.telegram.ui.Components.RLottieImageView r9 = r9.getIconView()
                android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
                android.view.ViewGroup$MarginLayoutParams r9 = (android.view.ViewGroup.MarginLayoutParams) r9
                r10 = 1065353216(0x3var_, float:1.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r9.leftMargin = r11
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r9.topMargin = r10
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r9.height = r10
                r9.width = r10
                org.telegram.ui.Components.Premium.PremiumButtonView r9 = r0.unlockButtonView
                org.telegram.ui.Components.AnimatedTextView r9 = r9.getTextView()
                android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
                android.view.ViewGroup$MarginLayoutParams r9 = (android.view.ViewGroup.MarginLayoutParams) r9
                r10 = 1077936128(0x40400000, float:3.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r9.leftMargin = r10
                org.telegram.ui.Components.Premium.PremiumButtonView r9 = r0.unlockButtonView
                android.view.View r9 = r9.getChildAt(r8)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r9.setPadding(r10, r8, r7, r8)
                org.telegram.ui.Components.Premium.PremiumButtonView r7 = r0.unlockButtonView
                r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r17 = 1105199104(0x41e00000, float:28.0)
                r18 = 8388661(0x800035, float:1.1755018E-38)
                r19 = 0
                r20 = 1098551132(0x417a8f5c, float:15.66)
                r21 = 1085611704(0x40b51eb8, float:5.66)
                r22 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r16, r17, r18, r19, r20, r21, r22)
                r0.addView(r7, r9)
                org.telegram.ui.Components.Premium.PremiumButtonView r7 = r0.unlockButtonView
                int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r15)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r13)
                r7.measure(r9, r10)
                org.telegram.ui.Components.Premium.PremiumButtonView r7 = r0.unlockButtonView
                int r7 = r7.getMeasuredWidth()
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r7 = r7 + r9
                float r7 = (float) r7
                float r9 = org.telegram.messenger.AndroidUtilities.density
                float r7 = r7 / r9
            L_0x00e4:
                android.widget.TextView r9 = new android.widget.TextView
                r9.<init>(r3)
                r0.addButtonView = r9
                android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r9.setTypeface(r10)
                android.widget.TextView r9 = r0.addButtonView
                java.lang.String r10 = "featuredStickers_buttonText"
                int r10 = r1.getThemedColor(r10)
                r9.setTextColor(r10)
                android.widget.TextView r9 = r0.addButtonView
                java.lang.String r10 = "featuredStickers_addButton"
                int r11 = r1.getThemedColor(r10)
                r5 = 1
                float[] r12 = new float[r5]
                r5 = 1082130432(0x40800000, float:4.0)
                r12[r8] = r5
                android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.filledRect((int) r11, (float[]) r12)
                r9.setBackground(r5)
                android.widget.TextView r5 = r0.addButtonView
                r9 = 2131624242(0x7f0e0132, float:1.8875658E38)
                java.lang.String r11 = "Add"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r5.setText(r9)
                android.widget.TextView r5 = r0.addButtonView
                r9 = 1099956224(0x41900000, float:18.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r11, r8, r9, r8)
                android.widget.TextView r5 = r0.addButtonView
                r9 = 17
                r5.setGravity(r9)
                android.widget.TextView r5 = r0.addButtonView
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda4 r11 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda4
                r11.<init>(r0)
                r5.setOnClickListener(r11)
                android.widget.TextView r5 = r0.addButtonView
                r18 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r19 = 1105199104(0x41e00000, float:28.0)
                r20 = 8388661(0x800035, float:1.1755018E-38)
                r21 = 0
                r22 = 1098551132(0x417a8f5c, float:15.66)
                r23 = 1085611704(0x40b51eb8, float:5.66)
                r24 = 0
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r18, r19, r20, r21, r22, r23, r24)
                r0.addView(r5, r11)
                android.widget.TextView r5 = r0.addButtonView
                int r11 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r15)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r12 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r13)
                r5.measure(r11, r12)
                android.widget.TextView r5 = r0.addButtonView
                int r5 = r5.getMeasuredWidth()
                r11 = 1098907648(0x41800000, float:16.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
                int r5 = r5 + r12
                float r5 = (float) r5
                float r11 = org.telegram.messenger.AndroidUtilities.density
                float r5 = r5 / r11
                float r5 = java.lang.Math.max(r7, r5)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r3)
                r0.removeButtonView = r7
                android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r7.setTypeface(r11)
                android.widget.TextView r7 = r0.removeButtonView
                int r11 = r1.getThemedColor(r10)
                r7.setTextColor(r11)
                android.widget.TextView r7 = r0.removeButtonView
                r11 = 268435455(0xfffffff, float:2.5243547E-29)
                int r10 = r1.getThemedColor(r10)
                r10 = r10 & r11
                r11 = 4
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createRadSelectorDrawable(r10, r11, r11)
                r7.setBackground(r10)
                android.widget.TextView r7 = r0.removeButtonView
                r10 = 2131628523(0x7f0e11eb, float:1.8884341E38)
                java.lang.String r11 = "StickersRemove"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
                r7.setText(r10)
                android.widget.TextView r7 = r0.removeButtonView
                r10 = 1094713344(0x41400000, float:12.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r7.setPadding(r11, r8, r10, r8)
                android.widget.TextView r7 = r0.removeButtonView
                r7.setGravity(r9)
                android.widget.TextView r7 = r0.removeButtonView
                org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda1 r9 = new org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda1
                r9.<init>(r0)
                r7.setOnClickListener(r9)
                android.widget.TextView r7 = r0.removeButtonView
                r7.setClickable(r8)
                android.widget.TextView r7 = r0.removeButtonView
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r18, r19, r20, r21, r22, r23, r24)
                r0.addView(r7, r9)
                android.widget.TextView r7 = r0.removeButtonView
                r9 = 0
                r7.setScaleX(r9)
                android.widget.TextView r7 = r0.removeButtonView
                r7.setScaleY(r9)
                android.widget.TextView r7 = r0.removeButtonView
                r7.setAlpha(r9)
                android.widget.TextView r7 = r0.removeButtonView
                int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r15)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r13)
                r7.measure(r4, r9)
                android.widget.TextView r4 = r0.removeButtonView
                int r4 = r4.getMeasuredWidth()
                r7 = 1098907648(0x41800000, float:16.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r4 = r4 + r7
                float r4 = (float) r4
                float r7 = org.telegram.messenger.AndroidUtilities.density
                float r4 = r4 / r7
                float r7 = java.lang.Math.max(r5, r4)
            L_0x021a:
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r3)
                r0.titleView = r4
                android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r4.setTypeface(r5)
                android.widget.TextView r4 = r0.titleView
                android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
                r4.setEllipsize(r5)
                android.widget.TextView r4 = r0.titleView
                r5 = 1
                r4.setSingleLine(r5)
                android.widget.TextView r4 = r0.titleView
                r4.setLines(r5)
                android.widget.TextView r4 = r0.titleView
                java.lang.String r6 = "dialogTextBlack"
                int r6 = r1.getThemedColor(r6)
                r4.setTextColor(r6)
                if (r2 == 0) goto L_0x0265
                android.widget.TextView r4 = r0.titleView
                r6 = 1101004800(0x41a00000, float:20.0)
                r4.setTextSize(r5, r6)
                android.widget.TextView r4 = r0.titleView
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 8388659(0x800033, float:1.1755015E-38)
                r12 = 1096810496(0x41600000, float:14.0)
                r13 = 1093664768(0x41300000, float:11.0)
                r15 = 0
                r14 = r7
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r4, r5)
                goto L_0x0283
            L_0x0265:
                android.widget.TextView r4 = r0.titleView
                r5 = 1099431936(0x41880000, float:17.0)
                r6 = 1
                r4.setTextSize(r6, r5)
                android.widget.TextView r4 = r0.titleView
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 8388659(0x800033, float:1.1755015E-38)
                r12 = 1090519040(0x41000000, float:8.0)
                r13 = 1092616192(0x41200000, float:10.0)
                r15 = 0
                r14 = r7
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r4, r5)
            L_0x0283:
                if (r2 != 0) goto L_0x02c5
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r3)
                r0.subtitleView = r4
                r5 = 1095761920(0x41500000, float:13.0)
                r6 = 1
                r4.setTextSize(r6, r5)
                android.widget.TextView r4 = r0.subtitleView
                java.lang.String r5 = "dialogTextGray2"
                int r5 = r1.getThemedColor(r5)
                r4.setTextColor(r5)
                android.widget.TextView r4 = r0.subtitleView
                android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
                r4.setEllipsize(r5)
                android.widget.TextView r4 = r0.subtitleView
                r4.setSingleLine(r6)
                android.widget.TextView r4 = r0.subtitleView
                r4.setLines(r6)
                android.widget.TextView r4 = r0.subtitleView
                r9 = -1082130432(0xffffffffbvar_, float:-1.0)
                r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r11 = 8388659(0x800033, float:1.1755015E-38)
                r12 = 1090519040(0x41000000, float:8.0)
                r13 = 1107117998(0x41fd47ae, float:31.66)
                r15 = 0
                r14 = r7
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r4, r5)
            L_0x02c5:
                if (r2 == 0) goto L_0x0361
                org.telegram.ui.ActionBar.ActionBarMenuItem r9 = new org.telegram.ui.ActionBar.ActionBarMenuItem
                r4 = 0
                r5 = 0
                java.lang.String r2 = "key_sheet_other"
                int r6 = r1.getThemedColor(r2)
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r26.resourcesProvider
                r2 = r9
                r3 = r27
                r2.<init>((android.content.Context) r3, (org.telegram.ui.ActionBar.ActionBarMenu) r4, (int) r5, (int) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
                r0.optionsButton = r9
                r9.setLongClickEnabled(r8)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                r3 = 2
                r2.setSubMenuOpenSide(r3)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                r4 = 2131165453(0x7var_d, float:1.7945124E38)
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
                int r10 = r26.backgroundPaddingLeft
                float r10 = (float) r10
                float r11 = org.telegram.messenger.AndroidUtilities.density
                float r10 = r10 / r11
                float r9 = r9 - r10
                r10 = 0
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10)
                r0.addView(r2, r4)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                r4 = 2131165942(0x7var_f6, float:1.7946115E38)
                r5 = 2131628528(0x7f0e11f0, float:1.8884351E38)
                java.lang.String r6 = "StickersShare"
                java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r6 = 1
                r2.addSubItem(r6, r4, r5)
                org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
                r4 = 2131165783(0x7var_, float:1.7945793E38)
                r5 = 2131625278(0x7f0e053e, float:1.887776E38)
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
                r2 = 2131624003(0x7f0e0043, float:1.8875173E38)
                java.lang.String r3 = "AccDescrMoreOptions"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setContentDescription(r2)
            L_0x0361:
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

        public void set(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i, boolean z) {
            TLRPC$StickerSet tLRPC$StickerSet;
            this.set = tLRPC$TL_messages_stickerSet;
            if (tLRPC$TL_messages_stickerSet == null || (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) == null) {
                this.titleView.setText((CharSequence) null);
            } else {
                this.titleView.setText(tLRPC$StickerSet.title);
            }
            TextView textView = this.subtitleView;
            if (textView != null) {
                textView.setText(LocaleController.formatPluralString("EmojiCount", i, new Object[0]));
            }
            if (!z || this.unlockButtonView == null || UserConfig.getInstance(this.this$0.currentAccount).isPremium()) {
                PremiumButtonView premiumButtonView = this.unlockButtonView;
                if (premiumButtonView != null) {
                    premiumButtonView.setVisibility(8);
                }
                TextView textView2 = this.addButtonView;
                if (textView2 != null) {
                    textView2.setVisibility(0);
                }
                TextView textView3 = this.removeButtonView;
                if (textView3 != null) {
                    textView3.setVisibility(0);
                }
                toggle(tLRPC$TL_messages_stickerSet != null && MediaDataController.getInstance(this.this$0.currentAccount).isStickerPackInstalled(tLRPC$TL_messages_stickerSet.set.id), false);
                return;
            }
            this.unlockButtonView.setVisibility(0);
            TextView textView4 = this.addButtonView;
            if (textView4 != null) {
                textView4.setVisibility(8);
            }
            TextView textView5 = this.removeButtonView;
            if (textView5 != null) {
                textView5.setVisibility(8);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.single ? 42.0f : 56.0f), NUM));
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
            BulletinFactory.of(EmojiPacksAlert.this.fragment).createErrorBulletin(LocaleController.getString("AddEmojiNotFound", NUM)).show();
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
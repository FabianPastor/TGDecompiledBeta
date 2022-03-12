package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;

public class ChatThemeBottomSheet extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public final Adapter adapter;
    private final View applyButton;
    private TextView applyTextView;
    /* access modifiers changed from: private */
    public View changeDayNightView;
    /* access modifiers changed from: private */
    public ValueAnimator changeDayNightViewAnimator;
    /* access modifiers changed from: private */
    public float changeDayNightViewProgress;
    private final ChatActivity chatActivity;
    private final RLottieDrawable darkThemeDrawable;
    /* access modifiers changed from: private */
    public final RLottieImageView darkThemeView;
    private boolean forceDark;
    HintView hintView;
    private boolean isApplyClicked;
    /* access modifiers changed from: private */
    public boolean isLightDarkChangeAnimation;
    private final LinearLayoutManager layoutManager;
    private final boolean originalIsDark;
    private final EmojiThemes originalTheme;
    /* access modifiers changed from: private */
    public int prevSelectedPosition = -1;
    private final FlickerLoadingView progressView;
    /* access modifiers changed from: private */
    public final RecyclerListView recyclerView;
    private TextView resetTextView;
    /* access modifiers changed from: private */
    public final LinearSmoothScroller scroller;
    private ChatThemeItem selectedItem;
    /* access modifiers changed from: private */
    public final ChatActivity.ThemeDelegate themeDelegate;
    private final TextView titleView;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatThemeBottomSheet(org.telegram.ui.ChatActivity r21, org.telegram.ui.ChatActivity.ThemeDelegate r22) {
        /*
            r20 = this;
            r0 = r20
            r1 = r22
            android.app.Activity r2 = r21.getParentActivity()
            r3 = 1
            r0.<init>(r2, r3, r1)
            r2 = -1
            r0.prevSelectedPosition = r2
            r2 = r21
            r0.chatActivity = r2
            r0.themeDelegate = r1
            org.telegram.ui.ActionBar.EmojiThemes r2 = r22.getCurrentTheme()
            r0.originalTheme = r2
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r2 = r2.isDark()
            r0.originalIsDark = r2
            org.telegram.ui.Components.ChatThemeBottomSheet$Adapter r2 = new org.telegram.ui.Components.ChatThemeBottomSheet$Adapter
            int r4 = r0.currentAccount
            r5 = 0
            r2.<init>(r4, r1, r5)
            r0.adapter = r2
            r0.setDimBehind(r5)
            r0.setCanDismissWithSwipe(r5)
            r0.setApplyBottomPadding(r5)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            android.content.Context r6 = r20.getContext()
            r4.<init>(r6)
            r0.setCustomView(r4)
            android.widget.TextView r6 = new android.widget.TextView
            android.content.Context r7 = r20.getContext()
            r6.<init>(r7)
            r0.titleView = r6
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.MIDDLE
            r6.setEllipsize(r7)
            r6.setLines(r3)
            r6.setSingleLine(r3)
            java.lang.String r7 = "SelectTheme"
            r8 = 2131627867(0x7f0e0f5b, float:1.888301E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)
            r6.setText(r7)
            java.lang.String r7 = "dialogTextBlack"
            int r7 = r0.getThemedColor(r7)
            r6.setTextColor(r7)
            r7 = 1101004800(0x41a00000, float:20.0)
            r6.setTextSize(r3, r7)
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r8)
            r8 = 1101529088(0x41a80000, float:21.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r12 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r6.setPadding(r9, r11, r8, r12)
            r13 = -1
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r15 = 8388659(0x800033, float:1.1755015E-38)
            r16 = 0
            r17 = 0
            r18 = 1115160576(0x42780000, float:62.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r4.addView(r6, r8)
            java.lang.String r6 = "featuredStickers_addButton"
            int r8 = r0.getThemedColor(r6)
            r9 = 1105199104(0x41e00000, float:28.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r9)
            org.telegram.ui.Components.RLottieDrawable r9 = new org.telegram.ui.Components.RLottieDrawable
            r12 = 2131558518(0x7f0d0076, float:1.8742354E38)
            java.lang.String r13 = "NUM"
            r16 = 1
            r17 = 0
            r11 = r9
            r14 = r15
            r11.<init>(r12, r13, r14, r15, r16, r17)
            r0.darkThemeDrawable = r9
            r9.setPlayInDirectionOfCustomEndFrame(r3)
            r9.beginApplyLayerColors()
            r0.setDarkButtonColor(r8)
            r9.commitApplyLayerColors()
            org.telegram.ui.Components.RLottieImageView r8 = new org.telegram.ui.Components.RLottieImageView
            android.content.Context r11 = r20.getContext()
            r8.<init>(r11)
            r0.darkThemeView = r8
            r8.setAnimation(r9)
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.CENTER
            r8.setScaleType(r9)
            org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda2 r9 = new org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda2
            r9.<init>(r0)
            r8.setOnClickListener(r9)
            r11 = 44
            r12 = 1110441984(0x42300000, float:44.0)
            r13 = 8388661(0x800035, float:1.1755018E-38)
            r14 = 0
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 1088421888(0x40e00000, float:7.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r4.addView(r8, r9)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r8 = r8.isDark()
            r8 = r8 ^ r3
            r0.forceDark = r8
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r8 = r8.isDark()
            r0.setForceDark(r8, r5)
            org.telegram.ui.Components.ChatThemeBottomSheet$1 r8 = new org.telegram.ui.Components.ChatThemeBottomSheet$1
            android.content.Context r9 = r20.getContext()
            r8.<init>(r0, r9)
            r0.scroller = r8
            org.telegram.ui.Components.RecyclerListView r8 = new org.telegram.ui.Components.RecyclerListView
            android.content.Context r9 = r20.getContext()
            r8.<init>(r9)
            r0.recyclerView = r8
            r8.setAdapter(r2)
            r8.setClipChildren(r5)
            r8.setClipToPadding(r5)
            r8.setHasFixedSize(r3)
            r2 = 0
            r8.setItemAnimator(r2)
            r8.setNestedScrollingEnabled(r5)
            androidx.recyclerview.widget.LinearLayoutManager r2 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r9 = r20.getContext()
            r2.<init>(r9, r5, r5)
            r0.layoutManager = r2
            r8.setLayoutManager(r2)
            r2 = 1094713344(0x41400000, float:12.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r8.setPadding(r9, r5, r2, r5)
            org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda6 r2 = new org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda6
            r2.<init>(r0, r1)
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            org.telegram.ui.Components.FlickerLoadingView r2 = new org.telegram.ui.Components.FlickerLoadingView
            android.content.Context r9 = r20.getContext()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r11 = r0.resourcesProvider
            r2.<init>(r9, r11)
            r0.progressView = r2
            r9 = 14
            r2.setViewType(r9)
            r2.setVisibility(r5)
            r11 = -1
            r12 = 1120927744(0x42d00000, float:104.0)
            r13 = 8388611(0x800003, float:1.1754948E-38)
            r14 = 0
            r15 = 1110441984(0x42300000, float:44.0)
            r16 = 0
            r17 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r4.addView(r2, r9)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r4.addView(r8, r2)
            android.view.View r2 = new android.view.View
            android.content.Context r8 = r20.getContext()
            r2.<init>(r8)
            r0.applyButton = r2
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r6 = r0.getThemedColor(r6)
            java.lang.String r9 = "featuredStickers_addButtonPressed"
            int r9 = r0.getThemedColor(r9)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r8, r6, r9)
            r2.setBackground(r6)
            r2.setEnabled(r5)
            org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda3
            r5.<init>(r0)
            r2.setOnClickListener(r5)
            r8 = -1
            r9 = 1111490560(0x42400000, float:48.0)
            r10 = 8388611(0x800003, float:1.1754948E-38)
            r11 = 1098907648(0x41800000, float:16.0)
            r12 = 1126301696(0x43220000, float:162.0)
            r13 = 1098907648(0x41800000, float:16.0)
            r14 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r4.addView(r2, r5)
            android.widget.TextView r2 = new android.widget.TextView
            android.content.Context r5 = r20.getContext()
            r2.<init>(r5)
            r0.resetTextView = r2
            r5 = 0
            r2.setAlpha(r5)
            android.widget.TextView r2 = r0.resetTextView
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r5)
            android.widget.TextView r2 = r0.resetTextView
            r5 = 17
            r2.setGravity(r5)
            android.widget.TextView r2 = r0.resetTextView
            r2.setLines(r3)
            android.widget.TextView r2 = r0.resetTextView
            r2.setSingleLine(r3)
            android.widget.TextView r2 = r0.resetTextView
            org.telegram.ui.ActionBar.EmojiThemes r1 = r22.getCurrentTheme()
            if (r1 != 0) goto L_0x020c
            r1 = 2131625355(0x7f0e058b, float:1.8877916E38)
            java.lang.String r6 = "DoNoSetTheme"
            goto L_0x0211
        L_0x020c:
            r1 = 2131624899(0x7f0e03c3, float:1.887699E38)
            java.lang.String r6 = "ChatResetTheme"
        L_0x0211:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
            r2.setText(r1)
            android.widget.TextView r1 = r0.resetTextView
            java.lang.String r2 = "featuredStickers_buttonText"
            int r6 = r0.getThemedColor(r2)
            r1.setTextColor(r6)
            android.widget.TextView r1 = r0.resetTextView
            r6 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r3, r6)
            android.widget.TextView r1 = r0.resetTextView
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r1.setTypeface(r8)
            android.widget.TextView r1 = r0.resetTextView
            r8 = 4
            r1.setVisibility(r8)
            android.widget.TextView r1 = r0.resetTextView
            r9 = -1
            r10 = 1111490560(0x42400000, float:48.0)
            r11 = 8388611(0x800003, float:1.1754948E-38)
            r12 = 1098907648(0x41800000, float:16.0)
            r13 = 1126301696(0x43220000, float:162.0)
            r14 = 1098907648(0x41800000, float:16.0)
            r15 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r4.addView(r1, r9)
            android.widget.TextView r1 = new android.widget.TextView
            android.content.Context r9 = r20.getContext()
            r1.<init>(r9)
            r0.applyTextView = r1
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r1.setEllipsize(r9)
            android.widget.TextView r1 = r0.applyTextView
            r1.setGravity(r5)
            android.widget.TextView r1 = r0.applyTextView
            r1.setLines(r3)
            android.widget.TextView r1 = r0.applyTextView
            r1.setSingleLine(r3)
            android.widget.TextView r1 = r0.applyTextView
            r5 = 2131624874(0x7f0e03aa, float:1.887694E38)
            java.lang.String r9 = "ChatApplyTheme"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r1.setText(r5)
            android.widget.TextView r1 = r0.applyTextView
            int r2 = r0.getThemedColor(r2)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r0.applyTextView
            r1.setTextSize(r3, r6)
            android.widget.TextView r1 = r0.applyTextView
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r0.applyTextView
            r1.setVisibility(r8)
            android.widget.TextView r1 = r0.applyTextView
            r5 = -1
            r6 = 1111490560(0x42400000, float:48.0)
            r7 = 8388611(0x800003, float:1.1754948E-38)
            r8 = 1098907648(0x41800000, float:16.0)
            r9 = 1126301696(0x43220000, float:162.0)
            r10 = 1098907648(0x41800000, float:16.0)
            r11 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
            r4.addView(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatThemeBottomSheet.<init>(org.telegram.ui.ChatActivity, org.telegram.ui.ChatActivity$ThemeDelegate):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        if (this.changeDayNightViewAnimator == null) {
            setupLightDarkTheme(!this.forceDark);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(ChatActivity.ThemeDelegate themeDelegate2, View view, final int i) {
        if (this.adapter.items.get(i) != this.selectedItem && this.changeDayNightView == null) {
            ChatThemeItem chatThemeItem = this.adapter.items.get(i);
            this.selectedItem = chatThemeItem;
            this.isLightDarkChangeAnimation = false;
            EmojiThemes emojiThemes = chatThemeItem.chatTheme;
            if (emojiThemes == null || emojiThemes.showAsDefaultStub) {
                this.applyTextView.animate().alpha(0.0f).setDuration(300).start();
                this.resetTextView.animate().alpha(1.0f).setDuration(300).start();
            } else {
                this.resetTextView.animate().alpha(0.0f).setDuration(300).start();
                this.applyTextView.animate().alpha(1.0f).setDuration(300).start();
            }
            EmojiThemes emojiThemes2 = this.selectedItem.chatTheme;
            if (emojiThemes2.showAsDefaultStub) {
                themeDelegate2.setCurrentTheme((EmojiThemes) null, true, Boolean.valueOf(this.forceDark));
            } else {
                themeDelegate2.setCurrentTheme(emojiThemes2, true, Boolean.valueOf(this.forceDark));
            }
            this.adapter.setSelectedItem(i);
            this.containerView.postDelayed(new Runnable() {
                public void run() {
                    int i;
                    RecyclerView.LayoutManager layoutManager = ChatThemeBottomSheet.this.recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        if (i > ChatThemeBottomSheet.this.prevSelectedPosition) {
                            i = Math.min(i + 1, ChatThemeBottomSheet.this.adapter.items.size() - 1);
                        } else {
                            i = Math.max(i - 1, 0);
                        }
                        ChatThemeBottomSheet.this.scroller.setTargetPosition(i);
                        layoutManager.startSmoothScroll(ChatThemeBottomSheet.this.scroller);
                    }
                    int unused = ChatThemeBottomSheet.this.prevSelectedPosition = i;
                }
            }, 100);
            for (int i2 = 0; i2 < this.recyclerView.getChildCount(); i2++) {
                ThemeSmallPreviewView themeSmallPreviewView = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i2);
                if (themeSmallPreviewView != view) {
                    themeSmallPreviewView.cancelAnimation();
                }
            }
            if (!this.adapter.items.get(i).chatTheme.showAsDefaultStub) {
                ((ThemeSmallPreviewView) view).playEmojiAnimation();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        applySelectedTheme();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ChatThemeController.preloadAllWallpaperThumbs(true);
        ChatThemeController.preloadAllWallpaperThumbs(false);
        ChatThemeController.preloadAllWallpaperImages(true);
        ChatThemeController.preloadAllWallpaperImages(false);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.isApplyClicked = false;
        List<EmojiThemes> cachedThemes = this.themeDelegate.getCachedThemes();
        if (cachedThemes == null || cachedThemes.isEmpty()) {
            ChatThemeController.requestAllChatThemes(new ResultCallback<List<EmojiThemes>>() {
                public void onComplete(List<EmojiThemes> list) {
                    if (list != null && !list.isEmpty()) {
                        ChatThemeBottomSheet.this.themeDelegate.setCachedThemes(list);
                    }
                    ChatThemeBottomSheet.this.onDataLoaded(list);
                }

                public void onError(TLRPC$TL_error tLRPC$TL_error) {
                    Toast.makeText(ChatThemeBottomSheet.this.getContext(), tLRPC$TL_error.text, 0).show();
                }
            }, true);
        } else {
            onDataLoaded(cachedThemes);
        }
        if (this.chatActivity.getCurrentUser() != null && SharedConfig.dayNightThemeSwitchHintCount > 0 && !this.chatActivity.getCurrentUser().self) {
            SharedConfig.updateDayNightThemeSwitchHintCount(SharedConfig.dayNightThemeSwitchHintCount - 1);
            HintView hintView2 = new HintView(getContext(), 9, this.chatActivity.getResourceProvider());
            this.hintView = hintView2;
            hintView2.setVisibility(4);
            this.hintView.setShowingDuration(5000);
            this.hintView.setBottomOffset(-AndroidUtilities.dp(8.0f));
            this.hintView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChatThemeDayNightSwitchTooltip", NUM, this.chatActivity.getCurrentUser().first_name)));
            AndroidUtilities.runOnUIThread(new ChatThemeBottomSheet$$ExternalSyntheticLambda4(this), 1500);
            this.container.addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$3() {
        this.hintView.showForView(this.darkThemeView, true);
    }

    public void onContainerTranslationYChanged(float f) {
        HintView hintView2 = this.hintView;
        if (hintView2 != null) {
            hintView2.hide();
        }
    }

    public void onBackPressed() {
        close();
    }

    public void dismiss() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        super.dismiss();
        if (!this.isApplyClicked) {
            this.themeDelegate.setCurrentTheme(this.originalTheme, true, Boolean.valueOf(this.originalIsDark));
        }
    }

    public void close() {
        if (hasChanges()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
            builder.setTitle(LocaleController.getString("ChatThemeSaveDialogTitle", NUM));
            builder.setSubtitle(LocaleController.getString("ChatThemeSaveDialogText", NUM));
            builder.setPositiveButton(LocaleController.getString("ChatThemeSaveDialogApply", NUM), new ChatThemeBottomSheet$$ExternalSyntheticLambda0(this));
            builder.setNegativeButton(LocaleController.getString("ChatThemeSaveDialogDiscard", NUM), new ChatThemeBottomSheet$$ExternalSyntheticLambda1(this));
            builder.show();
            return;
        }
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$close$4(DialogInterface dialogInterface, int i) {
        applySelectedTheme();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$close$5(DialogInterface dialogInterface, int i) {
        dismiss();
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        AnonymousClass4 r7 = new ThemeDescription.ThemeDescriptionDelegate() {
            private boolean isAnimationStarted = false;

            public void didSetColor() {
            }

            public void onAnimationProgress(float f) {
                if (f == 0.0f && !this.isAnimationStarted) {
                    ChatThemeBottomSheet.this.onAnimationStart();
                    this.isAnimationStarted = true;
                }
                ChatThemeBottomSheet chatThemeBottomSheet = ChatThemeBottomSheet.this;
                chatThemeBottomSheet.setDarkButtonColor(chatThemeBottomSheet.getThemedColor("featuredStickers_addButton"));
                ChatThemeBottomSheet chatThemeBottomSheet2 = ChatThemeBottomSheet.this;
                chatThemeBottomSheet2.setOverlayNavBarColor(chatThemeBottomSheet2.getThemedColor("dialogBackground"));
                if (ChatThemeBottomSheet.this.isLightDarkChangeAnimation) {
                    ChatThemeBottomSheet.this.setItemsAnimationProgress(f);
                }
                if (f == 1.0f && this.isAnimationStarted) {
                    boolean unused = ChatThemeBottomSheet.this.isLightDarkChangeAnimation = false;
                    ChatThemeBottomSheet.this.onAnimationEnd();
                    this.isAnimationStarted = false;
                }
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, r7, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.recyclerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ThemeSmallPreviewView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackgroundGray"));
        arrayList.add(new ThemeDescription(this.applyButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription(this.applyButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        Iterator<ThemeDescription> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().resourcesProvider = this.themeDelegate;
        }
        return arrayList;
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void setupLightDarkTheme(boolean z) {
        ValueAnimator valueAnimator = this.changeDayNightViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        FrameLayout frameLayout = (FrameLayout) getWindow().getDecorView();
        final Bitmap createBitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(createBitmap);
        this.darkThemeView.setAlpha(0.0f);
        ((FrameLayout) this.chatActivity.getParentActivity().getWindow().getDecorView()).draw(canvas);
        frameLayout.draw(canvas);
        this.darkThemeView.setAlpha(1.0f);
        final Paint paint = new Paint(1);
        paint.setColor(-16777216);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        final Paint paint2 = new Paint(1);
        paint2.setFilterBitmap(true);
        int[] iArr = new int[2];
        this.darkThemeView.getLocationInWindow(iArr);
        final float f = (float) iArr[0];
        float f2 = (float) iArr[1];
        final float measuredWidth = f + (((float) this.darkThemeView.getMeasuredWidth()) / 2.0f);
        final float measuredHeight = f2 + (((float) this.darkThemeView.getMeasuredHeight()) / 2.0f);
        float max = ((float) Math.max(createBitmap.getHeight(), createBitmap.getWidth())) * 0.9f;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        paint2.setShader(new BitmapShader(createBitmap, tileMode, tileMode));
        AnonymousClass5 r15 = r0;
        final boolean z2 = z;
        float f3 = f2;
        final float f4 = max;
        final float f5 = f3;
        AnonymousClass5 r0 = new View(getContext()) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (z2) {
                    if (ChatThemeBottomSheet.this.changeDayNightViewProgress > 0.0f) {
                        canvas.drawCircle(measuredWidth, measuredHeight, f4 * ChatThemeBottomSheet.this.changeDayNightViewProgress, paint);
                    }
                    canvas.drawBitmap(createBitmap, 0.0f, 0.0f, paint2);
                } else {
                    canvas.drawCircle(measuredWidth, measuredHeight, f4 * (1.0f - ChatThemeBottomSheet.this.changeDayNightViewProgress), paint2);
                }
                canvas.save();
                canvas.translate(f, f5);
                ChatThemeBottomSheet.this.darkThemeView.draw(canvas);
                canvas.restore();
            }
        };
        this.changeDayNightView = r15;
        this.changeDayNightViewProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.changeDayNightViewAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float unused = ChatThemeBottomSheet.this.changeDayNightViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                ChatThemeBottomSheet.this.changeDayNightView.invalidate();
            }
        });
        this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ChatThemeBottomSheet.this.changeDayNightView != null) {
                    if (ChatThemeBottomSheet.this.changeDayNightView.getParent() != null) {
                        ((ViewGroup) ChatThemeBottomSheet.this.changeDayNightView.getParent()).removeView(ChatThemeBottomSheet.this.changeDayNightView);
                    }
                    View unused = ChatThemeBottomSheet.this.changeDayNightView = null;
                }
                ValueAnimator unused2 = ChatThemeBottomSheet.this.changeDayNightViewAnimator = null;
                super.onAnimationEnd(animator);
            }
        });
        this.changeDayNightViewAnimator.setDuration(400);
        this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
        this.changeDayNightViewAnimator.start();
        frameLayout.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
        AndroidUtilities.runOnUIThread(new ChatThemeBottomSheet$$ExternalSyntheticLambda5(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupLightDarkTheme$6(boolean z) {
        Adapter adapter2 = this.adapter;
        if (adapter2 != null && adapter2.items != null) {
            setForceDark(z, true);
            ChatThemeItem chatThemeItem = this.selectedItem;
            if (chatThemeItem != null) {
                this.isLightDarkChangeAnimation = true;
                EmojiThemes emojiThemes = chatThemeItem.chatTheme;
                if (emojiThemes.showAsDefaultStub) {
                    this.themeDelegate.setCurrentTheme((EmojiThemes) null, false, Boolean.valueOf(z));
                } else {
                    this.themeDelegate.setCurrentTheme(emojiThemes, false, Boolean.valueOf(z));
                }
            }
            Adapter adapter3 = this.adapter;
            if (adapter3 != null && adapter3.items != null) {
                for (int i = 0; i < this.adapter.items.size(); i++) {
                    this.adapter.items.get(i).themeIndex = z ? 1 : 0;
                }
                this.adapter.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null || !hasChanges()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        if (((int) motionEvent.getY()) >= this.containerView.getTop() && x >= this.containerView.getLeft() && x <= this.containerView.getRight()) {
            return false;
        }
        this.chatActivity.getFragmentView().dispatchTouchEvent(motionEvent);
        return true;
    }

    /* access modifiers changed from: private */
    public void onDataLoaded(List<EmojiThemes> list) {
        if (list != null && !list.isEmpty()) {
            boolean z = false;
            ChatThemeItem chatThemeItem = new ChatThemeItem(list.get(0));
            ArrayList arrayList = new ArrayList(list.size());
            EmojiThemes currentTheme = this.themeDelegate.getCurrentTheme();
            arrayList.add(0, chatThemeItem);
            this.selectedItem = chatThemeItem;
            for (int i = 1; i < list.size(); i++) {
                EmojiThemes emojiThemes = list.get(i);
                ChatThemeItem chatThemeItem2 = new ChatThemeItem(emojiThemes);
                emojiThemes.loadPreviewColors(this.currentAccount);
                chatThemeItem2.themeIndex = this.forceDark ? 1 : 0;
                arrayList.add(chatThemeItem2);
            }
            this.adapter.setItems(arrayList);
            this.applyButton.setEnabled(true);
            this.applyTextView.setAlpha(0.0f);
            this.resetTextView.setAlpha(0.0f);
            this.recyclerView.setAlpha(0.0f);
            this.applyTextView.setVisibility(0);
            this.resetTextView.setVisibility(0);
            this.darkThemeView.setVisibility(0);
            if (currentTheme != null) {
                int i2 = 0;
                while (true) {
                    if (i2 == arrayList.size()) {
                        i2 = -1;
                        break;
                    } else if (((ChatThemeItem) arrayList.get(i2)).chatTheme.getEmoticon().equals(currentTheme.getEmoticon())) {
                        this.selectedItem = (ChatThemeItem) arrayList.get(i2);
                        break;
                    } else {
                        i2++;
                    }
                }
                if (i2 != -1) {
                    this.prevSelectedPosition = i2;
                    this.adapter.setSelectedItem(i2);
                    if (i2 > 0 && i2 < arrayList.size() / 2) {
                        i2--;
                    }
                    this.layoutManager.scrollToPositionWithOffset(Math.min(i2, this.adapter.items.size() - 1), 0);
                }
            } else {
                this.adapter.setSelectedItem(0);
                this.layoutManager.scrollToPositionWithOffset(0, 0);
                z = true;
            }
            float f = 1.0f;
            this.recyclerView.animate().alpha(1.0f).setDuration(150).start();
            this.resetTextView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150).start();
            ViewPropertyAnimator animate = this.applyTextView.animate();
            if (z) {
                f = 0.0f;
            }
            animate.alpha(f).setDuration(150).start();
            this.progressView.animate().alpha(0.0f).setListener(new HideViewAfterAnimation(this.progressView)).setDuration(150).start();
        }
    }

    /* access modifiers changed from: private */
    public void onAnimationStart() {
        List<ChatThemeItem> list;
        Adapter adapter2 = this.adapter;
        if (!(adapter2 == null || (list = adapter2.items) == null)) {
            for (ChatThemeItem chatThemeItem : list) {
                chatThemeItem.themeIndex = this.forceDark ? 1 : 0;
            }
        }
        if (!this.isLightDarkChangeAnimation) {
            setItemsAnimationProgress(1.0f);
        }
    }

    /* access modifiers changed from: private */
    public void onAnimationEnd() {
        this.isLightDarkChangeAnimation = false;
    }

    /* access modifiers changed from: private */
    public void setDarkButtonColor(int i) {
        this.darkThemeDrawable.setLayerColor("Sunny.**", i);
        this.darkThemeDrawable.setLayerColor("Path.**", i);
        this.darkThemeDrawable.setLayerColor("Path 10.**", i);
        this.darkThemeDrawable.setLayerColor("Path 11.**", i);
    }

    private void setForceDark(boolean z, boolean z2) {
        this.useLightNavBar = z;
        this.useLightStatusBar = z;
        if (this.forceDark != z) {
            this.forceDark = z;
            int i = 0;
            if (z2) {
                RLottieDrawable rLottieDrawable = this.darkThemeDrawable;
                if (z) {
                    i = rLottieDrawable.getFramesCount();
                }
                rLottieDrawable.setCustomEndFrame(i);
                this.darkThemeView.playAnimation();
                return;
            }
            RLottieDrawable rLottieDrawable2 = this.darkThemeDrawable;
            rLottieDrawable2.setCurrentFrame(z ? rLottieDrawable2.getFramesCount() - 1 : 0, false, true);
            this.darkThemeView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void setItemsAnimationProgress(float f) {
        for (int i = 0; i < this.adapter.getItemCount(); i++) {
            this.adapter.items.get(i).animationProgress = f;
        }
    }

    private void applySelectedTheme() {
        boolean z;
        ChatThemeItem chatThemeItem = this.selectedItem;
        EmojiThemes emojiThemes = chatThemeItem.chatTheme;
        boolean z2 = emojiThemes.showAsDefaultStub;
        Bulletin bulletin = null;
        EmojiThemes emojiThemes2 = z2 ? null : emojiThemes;
        if (!(chatThemeItem == null || emojiThemes2 == this.originalTheme)) {
            String emoticon = (emojiThemes == null || z2) ? null : emojiThemes.getEmoticon();
            ChatThemeController.getInstance(this.currentAccount).setDialogTheme(this.chatActivity.getDialogId(), emoticon, true);
            if (emojiThemes == null || emojiThemes.showAsDefaultStub) {
                this.themeDelegate.setCurrentTheme((EmojiThemes) null, true, Boolean.valueOf(this.originalIsDark));
            } else {
                this.themeDelegate.setCurrentTheme(emojiThemes, true, Boolean.valueOf(this.originalIsDark));
            }
            this.isApplyClicked = true;
            TLRPC$User currentUser = this.chatActivity.getCurrentUser();
            if (currentUser != null && !currentUser.self) {
                if (TextUtils.isEmpty(emoticon)) {
                    emoticon = "❌";
                    z = true;
                } else {
                    z = false;
                }
                StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(getContext(), (TLObject) null, -1, emoticon != null ? MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoticon) : null, this.chatActivity.getResourceProvider());
                stickerSetBulletinLayout.subtitleTextView.setVisibility(8);
                if (z) {
                    stickerSetBulletinLayout.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ThemeAlsoDisabledForHint", NUM, currentUser.first_name)));
                } else {
                    stickerSetBulletinLayout.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ThemeAlsoAppliedForHint", NUM, currentUser.first_name)));
                }
                stickerSetBulletinLayout.titleTextView.setTypeface((Typeface) null);
                bulletin = Bulletin.make((BaseFragment) this.chatActivity, (Bulletin.Layout) stickerSetBulletinLayout, 2750);
            }
        }
        dismiss();
        if (bulletin != null) {
            bulletin.show();
        }
    }

    private boolean hasChanges() {
        if (this.selectedItem == null) {
            return false;
        }
        EmojiThemes emojiThemes = this.originalTheme;
        String str = null;
        String emoticon = emojiThemes != null ? emojiThemes.getEmoticon() : null;
        String str2 = "❌";
        if (TextUtils.isEmpty(emoticon)) {
            emoticon = str2;
        }
        EmojiThemes emojiThemes2 = this.selectedItem.chatTheme;
        if (emojiThemes2 != null) {
            str = emojiThemes2.getEmoticon();
        }
        if (!TextUtils.isEmpty(str)) {
            str2 = str;
        }
        return !ObjectsCompat$$ExternalSyntheticBackport0.m(emoticon, str2);
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int currentAccount;
        private final int currentViewType;
        public List<ChatThemeItem> items;
        private HashMap<String, Theme.ThemeInfo> loadingThemes = new HashMap<>();
        private HashMap<Theme.ThemeInfo, String> loadingWallpapers = new HashMap<>();
        private final Theme.ResourcesProvider resourcesProvider;
        private int selectedItemPosition = -1;
        private WeakReference<ThemeSmallPreviewView> selectedViewRef;

        public Adapter(int i, Theme.ResourcesProvider resourcesProvider2, int i2) {
            this.currentViewType = i2;
            this.resourcesProvider = resourcesProvider2;
            this.currentAccount = i;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new ThemeSmallPreviewView(viewGroup.getContext(), this.currentAccount, this.resourcesProvider, this.currentViewType));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ThemeSmallPreviewView themeSmallPreviewView = (ThemeSmallPreviewView) viewHolder.itemView;
            Theme.ThemeInfo themeInfo = this.items.get(i).chatTheme.getThemeInfo(this.items.get(i).themeIndex);
            if (themeInfo != null && themeInfo.pathToFile != null && !themeInfo.previewParsed && new File(themeInfo.pathToFile).exists()) {
                parseTheme(themeInfo);
            }
            ChatThemeItem chatThemeItem = this.items.get(i);
            ChatThemeItem chatThemeItem2 = themeSmallPreviewView.chatThemeItem;
            boolean z = false;
            boolean z2 = chatThemeItem2 != null && chatThemeItem2.chatTheme.getEmoticon().equals(chatThemeItem.chatTheme.getEmoticon()) && !DrawerProfileCell.switchingTheme && themeSmallPreviewView.lastThemeIndex == chatThemeItem.themeIndex;
            themeSmallPreviewView.setItem(chatThemeItem, z2);
            if (i == this.selectedItemPosition) {
                z = true;
            }
            themeSmallPreviewView.setSelected(z, z2);
            if (i == this.selectedItemPosition) {
                this.selectedViewRef = new WeakReference<>(themeSmallPreviewView);
            }
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Can't wrap try/catch for region: R(2:92|93) */
        /* JADX WARNING: Can't wrap try/catch for region: R(5:144|150|151|152|153) */
        /* JADX WARNING: Code restructure failed: missing block: B:116:0x0223, code lost:
            r13 = 65535;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:117:0x0224, code lost:
            if (r13 == 0) goto L_0x0246;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:119:0x0227, code lost:
            if (r13 == 1) goto L_0x0242;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:120:0x0229, code lost:
            if (r13 == 2) goto L_0x023e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:122:0x022c, code lost:
            if (r13 == 3) goto L_0x023b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:124:0x022f, code lost:
            if (r13 == 4) goto L_0x0238;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:126:0x0232, code lost:
            if (r13 == 5) goto L_0x0235;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:128:0x0235, code lost:
            r2.previewBackgroundGradientColor3 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:129:0x0238, code lost:
            r2.previewBackgroundGradientColor2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:130:0x023b, code lost:
            r2.previewBackgroundGradientColor1 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:131:0x023e, code lost:
            r2.setPreviewBackgroundColor(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:132:0x0242, code lost:
            r2.setPreviewOutColor(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:133:0x0246, code lost:
            r2.setPreviewInColor(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:84:0x01bd, code lost:
            if (r13.equals("key_chat_wallpaper_gradient_to3") == false) goto L_0x0250;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
            r3 = org.telegram.messenger.Utilities.parseInt(r3).intValue();
         */
        /* JADX WARNING: Missing exception handler attribute for start block: B:152:0x028e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:92:0x01d9 */
        /* JADX WARNING: Unknown top exception splitter block from list: {B:146:0x0284=Splitter:B:146:0x0284, B:152:0x028e=Splitter:B:152:0x028e} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean parseTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r21) {
            /*
                r20 = this;
                r1 = r20
                r2 = r21
                java.lang.String r0 = "chat_inBubble"
                if (r2 == 0) goto L_0x02df
                java.lang.String r4 = r2.pathToFile
                if (r4 != 0) goto L_0x000e
                goto L_0x02df
            L_0x000e:
                java.io.File r4 = new java.io.File
                java.lang.String r5 = r2.pathToFile
                r4.<init>(r5)
                r5 = 1
                java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ all -> 0x028f }
                r6.<init>(r4)     // Catch:{ all -> 0x028f }
                r4 = 0
                r7 = 0
            L_0x001d:
                byte[] r8 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x0288 }
                int r8 = r6.read(r8)     // Catch:{ all -> 0x0288 }
                r9 = -1
                if (r8 == r9) goto L_0x0282
                r12 = r4
                r10 = 0
                r11 = 0
            L_0x0029:
                if (r10 >= r8) goto L_0x0267
                byte[] r13 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x0288 }
                byte r14 = r13[r10]     // Catch:{ all -> 0x0288 }
                r15 = 10
                if (r14 != r15) goto L_0x0253
                int r14 = r10 - r11
                int r14 = r14 + r5
                java.lang.String r15 = new java.lang.String     // Catch:{ all -> 0x0288 }
                int r9 = r14 + -1
                java.lang.String r3 = "UTF-8"
                r15.<init>(r13, r11, r9, r3)     // Catch:{ all -> 0x0288 }
                java.lang.String r3 = "WLS="
                boolean r3 = r15.startsWith(r3)     // Catch:{ all -> 0x0288 }
                r9 = 4
                if (r3 == 0) goto L_0x016d
                java.lang.String r3 = r15.substring(r9)     // Catch:{ all -> 0x0288 }
                android.net.Uri r9 = android.net.Uri.parse(r3)     // Catch:{ all -> 0x0288 }
                java.lang.String r13 = "slug"
                java.lang.String r13 = r9.getQueryParameter(r13)     // Catch:{ all -> 0x0288 }
                r2.slug = r13     // Catch:{ all -> 0x0288 }
                java.io.File r13 = new java.io.File     // Catch:{ all -> 0x0288 }
                java.io.File r15 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x0288 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0288 }
                r5.<init>()     // Catch:{ all -> 0x0288 }
                java.lang.String r3 = org.telegram.messenger.Utilities.MD5(r3)     // Catch:{ all -> 0x0288 }
                r5.append(r3)     // Catch:{ all -> 0x0288 }
                java.lang.String r3 = ".wp"
                r5.append(r3)     // Catch:{ all -> 0x0288 }
                java.lang.String r3 = r5.toString()     // Catch:{ all -> 0x0288 }
                r13.<init>(r15, r3)     // Catch:{ all -> 0x0288 }
                java.lang.String r3 = r13.getAbsolutePath()     // Catch:{ all -> 0x0288 }
                r2.pathToWallpaper = r3     // Catch:{ all -> 0x0288 }
                java.lang.String r3 = "mode"
                java.lang.String r3 = r9.getQueryParameter(r3)     // Catch:{ all -> 0x0288 }
                if (r3 == 0) goto L_0x00a8
                java.lang.String r3 = r3.toLowerCase()     // Catch:{ all -> 0x0288 }
                java.lang.String r5 = " "
                java.lang.String[] r3 = r3.split(r5)     // Catch:{ all -> 0x0288 }
                if (r3 == 0) goto L_0x00a8
                int r5 = r3.length     // Catch:{ all -> 0x0288 }
                if (r5 <= 0) goto L_0x00a8
                r5 = 0
            L_0x0094:
                int r13 = r3.length     // Catch:{ all -> 0x0288 }
                if (r5 >= r13) goto L_0x00a8
                java.lang.String r13 = "blur"
                r15 = r3[r5]     // Catch:{ all -> 0x0288 }
                boolean r13 = r13.equals(r15)     // Catch:{ all -> 0x0288 }
                if (r13 == 0) goto L_0x00a5
                r13 = 1
                r2.isBlured = r13     // Catch:{ all -> 0x0288 }
                goto L_0x00a8
            L_0x00a5:
                int r5 = r5 + 1
                goto L_0x0094
            L_0x00a8:
                java.lang.String r3 = "pattern"
                java.lang.String r3 = r9.getQueryParameter(r3)     // Catch:{ all -> 0x0288 }
                boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0288 }
                if (r3 != 0) goto L_0x024a
                java.lang.String r3 = "bg_color"
                java.lang.String r3 = r9.getQueryParameter(r3)     // Catch:{ Exception -> 0x0137 }
                boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0137 }
                if (r5 != 0) goto L_0x0137
                r5 = 6
                r13 = 0
                java.lang.String r15 = r3.substring(r13, r5)     // Catch:{ Exception -> 0x0137 }
                r13 = 16
                int r15 = java.lang.Integer.parseInt(r15, r13)     // Catch:{ Exception -> 0x0137 }
                r16 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r15 = r15 | r16
                r2.patternBgColor = r15     // Catch:{ Exception -> 0x0137 }
                int r15 = r3.length()     // Catch:{ Exception -> 0x0137 }
                r13 = 13
                if (r15 < r13) goto L_0x00f3
                char r5 = r3.charAt(r5)     // Catch:{ Exception -> 0x0137 }
                boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x0137 }
                if (r5 == 0) goto L_0x00f3
                r5 = 7
                java.lang.String r5 = r3.substring(r5, r13)     // Catch:{ Exception -> 0x0137 }
                r15 = 16
                int r5 = java.lang.Integer.parseInt(r5, r15)     // Catch:{ Exception -> 0x0137 }
                r5 = r5 | r16
                r2.patternBgGradientColor1 = r5     // Catch:{ Exception -> 0x0137 }
            L_0x00f3:
                int r5 = r3.length()     // Catch:{ Exception -> 0x0137 }
                r15 = 20
                if (r5 < r15) goto L_0x0115
                char r5 = r3.charAt(r13)     // Catch:{ Exception -> 0x0137 }
                boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x0137 }
                if (r5 == 0) goto L_0x0115
                r5 = 14
                java.lang.String r5 = r3.substring(r5, r15)     // Catch:{ Exception -> 0x0137 }
                r13 = 16
                int r5 = java.lang.Integer.parseInt(r5, r13)     // Catch:{ Exception -> 0x0137 }
                r5 = r5 | r16
                r2.patternBgGradientColor2 = r5     // Catch:{ Exception -> 0x0137 }
            L_0x0115:
                int r5 = r3.length()     // Catch:{ Exception -> 0x0137 }
                r13 = 27
                if (r5 != r13) goto L_0x0137
                char r5 = r3.charAt(r15)     // Catch:{ Exception -> 0x0137 }
                boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x0137 }
                if (r5 == 0) goto L_0x0137
                r5 = 21
                java.lang.String r3 = r3.substring(r5)     // Catch:{ Exception -> 0x0137 }
                r5 = 16
                int r3 = java.lang.Integer.parseInt(r3, r5)     // Catch:{ Exception -> 0x0137 }
                r3 = r3 | r16
                r2.patternBgGradientColor3 = r3     // Catch:{ Exception -> 0x0137 }
            L_0x0137:
                java.lang.String r3 = "rotation"
                java.lang.String r3 = r9.getQueryParameter(r3)     // Catch:{ Exception -> 0x014d }
                boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x014d }
                if (r5 != 0) goto L_0x014d
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x014d }
                int r3 = r3.intValue()     // Catch:{ Exception -> 0x014d }
                r2.patternBgGradientRotation = r3     // Catch:{ Exception -> 0x014d }
            L_0x014d:
                java.lang.String r3 = "intensity"
                java.lang.String r3 = r9.getQueryParameter(r3)     // Catch:{ all -> 0x0288 }
                boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0288 }
                if (r5 != 0) goto L_0x0163
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x0288 }
                int r3 = r3.intValue()     // Catch:{ all -> 0x0288 }
                r2.patternIntensity = r3     // Catch:{ all -> 0x0288 }
            L_0x0163:
                int r3 = r2.patternIntensity     // Catch:{ all -> 0x0288 }
                if (r3 != 0) goto L_0x024a
                r3 = 50
                r2.patternIntensity = r3     // Catch:{ all -> 0x0288 }
                goto L_0x024a
            L_0x016d:
                java.lang.String r3 = "WPS"
                boolean r3 = r15.startsWith(r3)     // Catch:{ all -> 0x0288 }
                if (r3 == 0) goto L_0x017d
                int r14 = r14 + r12
                r2.previewWallpaperOffset = r14     // Catch:{ all -> 0x0288 }
                r19 = r6
                r7 = 1
                goto L_0x026b
            L_0x017d:
                r3 = 61
                int r3 = r15.indexOf(r3)     // Catch:{ all -> 0x0288 }
                r13 = -1
                if (r3 == r13) goto L_0x024a
                r5 = 0
                java.lang.String r13 = r15.substring(r5, r3)     // Catch:{ all -> 0x0288 }
                boolean r5 = r13.equals(r0)     // Catch:{ all -> 0x0288 }
                java.lang.String r9 = "key_chat_wallpaper_gradient_to3"
                r17 = r7
                java.lang.String r7 = "key_chat_wallpaper_gradient_to2"
                r18 = r8
                java.lang.String r8 = "chat_wallpaper_gradient_to"
                java.lang.String r1 = "chat_wallpaper"
                r19 = r6
                java.lang.String r6 = "chat_outBubble"
                if (r5 != 0) goto L_0x01bf
                boolean r5 = r13.equals(r6)     // Catch:{ all -> 0x0280 }
                if (r5 != 0) goto L_0x01bf
                boolean r5 = r13.equals(r1)     // Catch:{ all -> 0x0280 }
                if (r5 != 0) goto L_0x01bf
                boolean r5 = r13.equals(r8)     // Catch:{ all -> 0x0280 }
                if (r5 != 0) goto L_0x01bf
                boolean r5 = r13.equals(r7)     // Catch:{ all -> 0x0280 }
                if (r5 != 0) goto L_0x01bf
                boolean r5 = r13.equals(r9)     // Catch:{ all -> 0x0280 }
                if (r5 == 0) goto L_0x0250
            L_0x01bf:
                int r3 = r3 + 1
                java.lang.String r3 = r15.substring(r3)     // Catch:{ all -> 0x0280 }
                int r5 = r3.length()     // Catch:{ all -> 0x0280 }
                if (r5 <= 0) goto L_0x01e2
                r5 = 0
                char r15 = r3.charAt(r5)     // Catch:{ all -> 0x0280 }
                r5 = 35
                if (r15 != r5) goto L_0x01e2
                int r3 = android.graphics.Color.parseColor(r3)     // Catch:{ Exception -> 0x01d9 }
                goto L_0x01ea
            L_0x01d9:
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x0280 }
                int r3 = r3.intValue()     // Catch:{ all -> 0x0280 }
                goto L_0x01ea
            L_0x01e2:
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x0280 }
                int r3 = r3.intValue()     // Catch:{ all -> 0x0280 }
            L_0x01ea:
                int r5 = r13.hashCode()     // Catch:{ all -> 0x0280 }
                r15 = 2
                switch(r5) {
                    case -1625862693: goto L_0x021b;
                    case -633951866: goto L_0x0213;
                    case 1269980952: goto L_0x020b;
                    case 1381936524: goto L_0x0203;
                    case 1381936525: goto L_0x01fb;
                    case 2052611411: goto L_0x01f3;
                    default: goto L_0x01f2;
                }     // Catch:{ all -> 0x0280 }
            L_0x01f2:
                goto L_0x0223
            L_0x01f3:
                boolean r1 = r13.equals(r6)     // Catch:{ all -> 0x0280 }
                if (r1 == 0) goto L_0x0223
                r13 = 1
                goto L_0x0224
            L_0x01fb:
                boolean r1 = r13.equals(r9)     // Catch:{ all -> 0x0280 }
                if (r1 == 0) goto L_0x0223
                r13 = 5
                goto L_0x0224
            L_0x0203:
                boolean r1 = r13.equals(r7)     // Catch:{ all -> 0x0280 }
                if (r1 == 0) goto L_0x0223
                r13 = 4
                goto L_0x0224
            L_0x020b:
                boolean r1 = r13.equals(r0)     // Catch:{ all -> 0x0280 }
                if (r1 == 0) goto L_0x0223
                r13 = 0
                goto L_0x0224
            L_0x0213:
                boolean r1 = r13.equals(r8)     // Catch:{ all -> 0x0280 }
                if (r1 == 0) goto L_0x0223
                r13 = 3
                goto L_0x0224
            L_0x021b:
                boolean r1 = r13.equals(r1)     // Catch:{ all -> 0x0280 }
                if (r1 == 0) goto L_0x0223
                r13 = 2
                goto L_0x0224
            L_0x0223:
                r13 = -1
            L_0x0224:
                if (r13 == 0) goto L_0x0246
                r1 = 1
                if (r13 == r1) goto L_0x0242
                if (r13 == r15) goto L_0x023e
                r1 = 3
                if (r13 == r1) goto L_0x023b
                r1 = 4
                if (r13 == r1) goto L_0x0238
                r1 = 5
                if (r13 == r1) goto L_0x0235
                goto L_0x0250
            L_0x0235:
                r2.previewBackgroundGradientColor3 = r3     // Catch:{ all -> 0x0280 }
                goto L_0x0250
            L_0x0238:
                r2.previewBackgroundGradientColor2 = r3     // Catch:{ all -> 0x0280 }
                goto L_0x0250
            L_0x023b:
                r2.previewBackgroundGradientColor1 = r3     // Catch:{ all -> 0x0280 }
                goto L_0x0250
            L_0x023e:
                r2.setPreviewBackgroundColor(r3)     // Catch:{ all -> 0x0280 }
                goto L_0x0250
            L_0x0242:
                r2.setPreviewOutColor(r3)     // Catch:{ all -> 0x0280 }
                goto L_0x0250
            L_0x0246:
                r2.setPreviewInColor(r3)     // Catch:{ all -> 0x0280 }
                goto L_0x0250
            L_0x024a:
                r19 = r6
                r17 = r7
                r18 = r8
            L_0x0250:
                int r11 = r11 + r14
                int r12 = r12 + r14
                goto L_0x0259
            L_0x0253:
                r19 = r6
                r17 = r7
                r18 = r8
            L_0x0259:
                int r10 = r10 + 1
                r1 = r20
                r7 = r17
                r8 = r18
                r6 = r19
                r5 = 1
                r9 = -1
                goto L_0x0029
            L_0x0267:
                r19 = r6
                r17 = r7
            L_0x026b:
                if (r7 != 0) goto L_0x0284
                if (r4 != r12) goto L_0x0270
                goto L_0x0284
            L_0x0270:
                java.nio.channels.FileChannel r1 = r19.getChannel()     // Catch:{ all -> 0x0280 }
                long r3 = (long) r12     // Catch:{ all -> 0x0280 }
                r1.position(r3)     // Catch:{ all -> 0x0280 }
                r1 = r20
                r4 = r12
                r6 = r19
                r5 = 1
                goto L_0x001d
            L_0x0280:
                r0 = move-exception
                goto L_0x028b
            L_0x0282:
                r19 = r6
            L_0x0284:
                r19.close()     // Catch:{ all -> 0x028f }
                goto L_0x0293
            L_0x0288:
                r0 = move-exception
                r19 = r6
            L_0x028b:
                r19.close()     // Catch:{ all -> 0x028e }
            L_0x028e:
                throw r0     // Catch:{ all -> 0x028f }
            L_0x028f:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0293:
                java.lang.String r0 = r2.pathToWallpaper
                if (r0 == 0) goto L_0x02d9
                boolean r0 = r2.badWallpaper
                if (r0 != 0) goto L_0x02d9
                java.io.File r0 = new java.io.File
                java.lang.String r1 = r2.pathToWallpaper
                r0.<init>(r1)
                boolean r0 = r0.exists()
                if (r0 != 0) goto L_0x02d9
                r1 = r20
                java.util.HashMap<org.telegram.ui.ActionBar.Theme$ThemeInfo, java.lang.String> r0 = r1.loadingWallpapers
                boolean r0 = r0.containsKey(r2)
                if (r0 != 0) goto L_0x02d7
                java.util.HashMap<org.telegram.ui.ActionBar.Theme$ThemeInfo, java.lang.String> r0 = r1.loadingWallpapers
                java.lang.String r3 = r2.slug
                r0.put(r2, r3)
                org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
                r0.<init>()
                org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r3 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
                r3.<init>()
                java.lang.String r4 = r2.slug
                r3.slug = r4
                r0.wallpaper = r3
                int r3 = r2.account
                org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
                org.telegram.ui.Components.ChatThemeBottomSheet$Adapter$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.Components.ChatThemeBottomSheet$Adapter$$ExternalSyntheticLambda1
                r4.<init>(r1, r2)
                r3.sendRequest(r0, r4)
            L_0x02d7:
                r2 = 0
                return r2
            L_0x02d9:
                r1 = r20
                r3 = 1
                r2.previewParsed = r3
                return r3
            L_0x02df:
                r2 = 0
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatThemeBottomSheet.Adapter.parseTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo):boolean");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$parseTheme$1(Theme.ThemeInfo themeInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new ChatThemeBottomSheet$Adapter$$ExternalSyntheticLambda0(this, tLObject, themeInfo));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$parseTheme$0(TLObject tLObject, Theme.ThemeInfo themeInfo) {
            if (tLObject instanceof TLRPC$TL_wallPaper) {
                TLRPC$WallPaper tLRPC$WallPaper = (TLRPC$WallPaper) tLObject;
                String attachFileName = FileLoader.getAttachFileName(tLRPC$WallPaper.document);
                if (!this.loadingThemes.containsKey(attachFileName)) {
                    this.loadingThemes.put(attachFileName, themeInfo);
                    FileLoader.getInstance(themeInfo.account).loadFile(tLRPC$WallPaper.document, tLRPC$WallPaper, 1, 1);
                    return;
                }
                return;
            }
            themeInfo.badWallpaper = true;
        }

        public int getItemCount() {
            List<ChatThemeItem> list = this.items;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        public void setItems(List<ChatThemeItem> list) {
            this.items = list;
            notifyDataSetChanged();
        }

        public void setSelectedItem(int i) {
            int i2 = this.selectedItemPosition;
            if (i2 != i) {
                if (i2 >= 0) {
                    notifyItemChanged(i2);
                    WeakReference<ThemeSmallPreviewView> weakReference = this.selectedViewRef;
                    ThemeSmallPreviewView themeSmallPreviewView = weakReference == null ? null : (ThemeSmallPreviewView) weakReference.get();
                    if (themeSmallPreviewView != null) {
                        themeSmallPreviewView.setSelected(false);
                    }
                }
                this.selectedItemPosition = i;
                notifyItemChanged(i);
            }
        }
    }

    public static class ChatThemeItem {
        public float animationProgress;
        public final EmojiThemes chatTheme;
        public Bitmap icon;
        public boolean isSelected;
        public Drawable previewDrawable;
        public int themeIndex;

        public ChatThemeItem(EmojiThemes emojiThemes) {
            this.chatTheme = emojiThemes;
        }
    }

    public void show() {
        String str;
        int i;
        super.show();
        TextView textView = this.resetTextView;
        if (this.themeDelegate.getCurrentTheme() == null) {
            i = NUM;
            str = "DoNoSetTheme";
        } else {
            i = NUM;
            str = "ChatResetTheme";
        }
        textView.setText(LocaleController.getString(str, i));
    }
}

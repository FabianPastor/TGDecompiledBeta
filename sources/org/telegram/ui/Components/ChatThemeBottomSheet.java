package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
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
import org.telegram.tgnet.TLRPC;
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
    /* access modifiers changed from: private */
    public final RLottieDrawable darkThemeDrawable;
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
    private FrameLayout rootLayout;
    /* access modifiers changed from: private */
    public final LinearSmoothScroller scroller;
    private ChatThemeItem selectedItem;
    /* access modifiers changed from: private */
    public final ChatActivity.ThemeDelegate themeDelegate;
    private final TextView titleView;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatThemeBottomSheet(org.telegram.ui.ChatActivity r27, org.telegram.ui.ChatActivity.ThemeDelegate r28) {
        /*
            r26 = this;
            r0 = r26
            r1 = r28
            android.app.Activity r2 = r27.getParentActivity()
            r3 = 1
            r0.<init>(r2, r3, r1)
            r2 = -1
            r0.prevSelectedPosition = r2
            r2 = r27
            r0.chatActivity = r2
            r0.themeDelegate = r1
            org.telegram.ui.ActionBar.EmojiThemes r4 = r28.getCurrentTheme()
            r0.originalTheme = r4
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r4 = r4.isDark()
            r0.originalIsDark = r4
            org.telegram.ui.Components.ChatThemeBottomSheet$Adapter r4 = new org.telegram.ui.Components.ChatThemeBottomSheet$Adapter
            int r5 = r0.currentAccount
            r6 = 0
            r4.<init>(r5, r1, r6)
            r0.adapter = r4
            r0.setDimBehind(r6)
            r0.setCanDismissWithSwipe(r6)
            r0.setApplyBottomPadding(r6)
            r0.drawNavigationBar = r3
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            android.content.Context r7 = r26.getContext()
            r5.<init>(r7)
            r0.rootLayout = r5
            r0.setCustomView(r5)
            android.widget.TextView r5 = new android.widget.TextView
            android.content.Context r7 = r26.getContext()
            r5.<init>(r7)
            r0.titleView = r5
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.MIDDLE
            r5.setEllipsize(r7)
            r5.setLines(r3)
            r5.setSingleLine(r3)
            java.lang.String r7 = "SelectTheme"
            r8 = 2131627959(0x7f0e0fb7, float:1.8883197E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)
            r5.setText(r7)
            java.lang.String r7 = "dialogTextBlack"
            int r7 = r0.getThemedColor(r7)
            r5.setTextColor(r7)
            r7 = 1101004800(0x41a00000, float:20.0)
            r5.setTextSize(r3, r7)
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r5.setTypeface(r8)
            r8 = 1101529088(0x41a80000, float:21.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r12 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r5.setPadding(r9, r11, r8, r12)
            android.widget.FrameLayout r8 = r0.rootLayout
            r11 = -1
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 8388659(0x800033, float:1.1755015E-38)
            r14 = 0
            r15 = 0
            r16 = 1115160576(0x42780000, float:62.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r8.addView(r5, r9)
            java.lang.String r5 = "featuredStickers_addButton"
            int r8 = r0.getThemedColor(r5)
            r9 = 1105199104(0x41e00000, float:28.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            org.telegram.ui.Components.RLottieDrawable r15 = new org.telegram.ui.Components.RLottieDrawable
            r12 = 2131558525(0x7f0d007d, float:1.8742368E38)
            java.lang.String r13 = "NUM"
            r16 = 0
            r17 = 0
            r11 = r15
            r14 = r9
            r10 = r15
            r15 = r9
            r11.<init>(r12, r13, r14, r15, r16, r17)
            r0.darkThemeDrawable = r10
            org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r11 = r11.isDark()
            r11 = r11 ^ r3
            r0.forceDark = r11
            org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r11 = r11.isDark()
            r0.setForceDark(r11, r6)
            r10.setAllowDecodeSingleFrame(r3)
            r10.setPlayInDirectionOfCustomEndFrame(r3)
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r8, r12)
            r10.setColorFilter(r11)
            org.telegram.ui.Components.RLottieImageView r11 = new org.telegram.ui.Components.RLottieImageView
            android.content.Context r12 = r26.getContext()
            r11.<init>(r12)
            r0.darkThemeView = r11
            r11.setAnimation(r10)
            android.widget.ImageView$ScaleType r10 = android.widget.ImageView.ScaleType.CENTER
            r11.setScaleType(r10)
            org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda2 r10 = new org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda2
            r10.<init>(r0)
            r11.setOnClickListener(r10)
            android.widget.FrameLayout r10 = r0.rootLayout
            r19 = 44
            r20 = 1110441984(0x42300000, float:44.0)
            r21 = 8388661(0x800035, float:1.1755018E-38)
            r22 = 0
            r23 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r24 = 1088421888(0x40e00000, float:7.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r10.addView(r11, r12)
            org.telegram.ui.Components.ChatThemeBottomSheet$1 r10 = new org.telegram.ui.Components.ChatThemeBottomSheet$1
            android.content.Context r11 = r26.getContext()
            r10.<init>(r11)
            r0.scroller = r10
            org.telegram.ui.Components.RecyclerListView r10 = new org.telegram.ui.Components.RecyclerListView
            android.content.Context r11 = r26.getContext()
            r10.<init>(r11)
            r0.recyclerView = r10
            r10.setAdapter(r4)
            r10.setClipChildren(r6)
            r10.setClipToPadding(r6)
            r10.setHasFixedSize(r3)
            r4 = 0
            r10.setItemAnimator(r4)
            r10.setNestedScrollingEnabled(r6)
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r11 = r26.getContext()
            r4.<init>(r11, r6, r6)
            r0.layoutManager = r4
            r10.setLayoutManager(r4)
            r4 = 1094713344(0x41400000, float:12.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r10.setPadding(r11, r6, r4, r6)
            org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda6 r4 = new org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda6
            r4.<init>(r0, r1)
            r10.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            org.telegram.ui.Components.FlickerLoadingView r4 = new org.telegram.ui.Components.FlickerLoadingView
            android.content.Context r11 = r26.getContext()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r12 = r0.resourcesProvider
            r4.<init>(r11, r12)
            r0.progressView = r4
            r11 = 14
            r4.setViewType(r11)
            r4.setVisibility(r6)
            android.widget.FrameLayout r11 = r0.rootLayout
            r19 = -1
            r20 = 1120927744(0x42d00000, float:104.0)
            r21 = 8388611(0x800003, float:1.1754948E-38)
            r23 = 1110441984(0x42300000, float:44.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r11.addView(r4, r12)
            android.widget.FrameLayout r4 = r0.rootLayout
            r11 = -1
            r12 = 1120927744(0x42d00000, float:104.0)
            r13 = 8388611(0x800003, float:1.1754948E-38)
            r14 = 0
            r15 = 1110441984(0x42300000, float:44.0)
            r16 = 0
            r17 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r4.addView(r10, r11)
            android.view.View r4 = new android.view.View
            android.content.Context r10 = r26.getContext()
            r4.<init>(r10)
            r0.applyButton = r4
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r5 = r0.getThemedColor(r5)
            java.lang.String r11 = "featuredStickers_addButtonPressed"
            int r11 = r0.getThemedColor(r11)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r10, r5, r11)
            r4.setBackground(r5)
            r4.setEnabled(r6)
            org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda3
            r5.<init>(r0)
            r4.setOnClickListener(r5)
            android.widget.FrameLayout r5 = r0.rootLayout
            r10 = -1
            r11 = 1111490560(0x42400000, float:48.0)
            r12 = 8388611(0x800003, float:1.1754948E-38)
            r13 = 1098907648(0x41800000, float:16.0)
            r14 = 1126301696(0x43220000, float:162.0)
            r15 = 1098907648(0x41800000, float:16.0)
            r16 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r4, r6)
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r5 = r26.getContext()
            r4.<init>(r5)
            r0.resetTextView = r4
            r5 = 0
            r4.setAlpha(r5)
            android.widget.TextView r4 = r0.resetTextView
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r5)
            android.widget.TextView r4 = r0.resetTextView
            r5 = 17
            r4.setGravity(r5)
            android.widget.TextView r4 = r0.resetTextView
            r4.setLines(r3)
            android.widget.TextView r4 = r0.resetTextView
            r4.setSingleLine(r3)
            android.widget.TextView r4 = r0.resetTextView
            org.telegram.ui.ActionBar.EmojiThemes r6 = r28.getCurrentTheme()
            if (r6 != 0) goto L_0x022c
            r6 = 2131625427(0x7f0e05d3, float:1.8878062E38)
            java.lang.String r10 = "DoNoSetTheme"
            goto L_0x0231
        L_0x022c:
            r6 = 2131624947(0x7f0e03f3, float:1.8877088E38)
            java.lang.String r10 = "ChatResetTheme"
        L_0x0231:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            r4.setText(r6)
            android.widget.TextView r4 = r0.resetTextView
            java.lang.String r6 = "featuredStickers_buttonText"
            int r10 = r0.getThemedColor(r6)
            r4.setTextColor(r10)
            android.widget.TextView r4 = r0.resetTextView
            r10 = 1097859072(0x41700000, float:15.0)
            r4.setTextSize(r3, r10)
            android.widget.TextView r4 = r0.resetTextView
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r4.setTypeface(r11)
            android.widget.TextView r4 = r0.resetTextView
            r11 = 4
            r4.setVisibility(r11)
            android.widget.FrameLayout r4 = r0.rootLayout
            android.widget.TextView r12 = r0.resetTextView
            r13 = -1
            r14 = 1111490560(0x42400000, float:48.0)
            r15 = 8388611(0x800003, float:1.1754948E-38)
            r16 = 1098907648(0x41800000, float:16.0)
            r17 = 1126301696(0x43220000, float:162.0)
            r18 = 1098907648(0x41800000, float:16.0)
            r19 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r4.addView(r12, r13)
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r12 = r26.getContext()
            r4.<init>(r12)
            r0.applyTextView = r4
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r12)
            android.widget.TextView r4 = r0.applyTextView
            r4.setGravity(r5)
            android.widget.TextView r4 = r0.applyTextView
            r4.setLines(r3)
            android.widget.TextView r4 = r0.applyTextView
            r4.setSingleLine(r3)
            android.widget.TextView r4 = r0.applyTextView
            r5 = 2131624922(0x7f0e03da, float:1.8877037E38)
            java.lang.String r12 = "ChatApplyTheme"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            r4.setText(r5)
            android.widget.TextView r4 = r0.applyTextView
            int r5 = r0.getThemedColor(r6)
            r4.setTextColor(r5)
            android.widget.TextView r4 = r0.applyTextView
            r4.setTextSize(r3, r10)
            android.widget.TextView r3 = r0.applyTextView
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r3.setTypeface(r4)
            android.widget.TextView r3 = r0.applyTextView
            r3.setVisibility(r11)
            android.widget.FrameLayout r3 = r0.rootLayout
            android.widget.TextView r4 = r0.applyTextView
            r10 = -1
            r11 = 1111490560(0x42400000, float:48.0)
            r12 = 8388611(0x800003, float:1.1754948E-38)
            r13 = 1098907648(0x41800000, float:16.0)
            r14 = 1126301696(0x43220000, float:162.0)
            r15 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r3.addView(r4, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatThemeBottomSheet.<init>(org.telegram.ui.ChatActivity, org.telegram.ui.ChatActivity$ThemeDelegate):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatThemeBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3913lambda$new$0$orgtelegramuiComponentsChatThemeBottomSheet(View view) {
        if (this.changeDayNightViewAnimator == null) {
            setupLightDarkTheme(!this.forceDark);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatThemeBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3914lambda$new$1$orgtelegramuiComponentsChatThemeBottomSheet(ChatActivity.ThemeDelegate themeDelegate2, View view, final int position) {
        if (this.adapter.items.get(position) != this.selectedItem && this.changeDayNightView == null) {
            ChatThemeItem chatThemeItem = this.adapter.items.get(position);
            this.selectedItem = chatThemeItem;
            this.isLightDarkChangeAnimation = false;
            if (chatThemeItem.chatTheme == null || this.selectedItem.chatTheme.showAsDefaultStub) {
                this.applyTextView.animate().alpha(0.0f).setDuration(300).start();
                this.resetTextView.animate().alpha(1.0f).setDuration(300).start();
            } else {
                this.resetTextView.animate().alpha(0.0f).setDuration(300).start();
                this.applyTextView.animate().alpha(1.0f).setDuration(300).start();
            }
            if (this.selectedItem.chatTheme.showAsDefaultStub) {
                themeDelegate2.setCurrentTheme((EmojiThemes) null, true, Boolean.valueOf(this.forceDark));
            } else {
                themeDelegate2.setCurrentTheme(this.selectedItem.chatTheme, true, Boolean.valueOf(this.forceDark));
            }
            this.adapter.setSelectedItem(position);
            this.containerView.postDelayed(new Runnable() {
                public void run() {
                    int targetPosition;
                    RecyclerView.LayoutManager layoutManager = ChatThemeBottomSheet.this.recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        if (position > ChatThemeBottomSheet.this.prevSelectedPosition) {
                            targetPosition = Math.min(position + 1, ChatThemeBottomSheet.this.adapter.items.size() - 1);
                        } else {
                            targetPosition = Math.max(position - 1, 0);
                        }
                        ChatThemeBottomSheet.this.scroller.setTargetPosition(targetPosition);
                        layoutManager.startSmoothScroll(ChatThemeBottomSheet.this.scroller);
                    }
                    int unused = ChatThemeBottomSheet.this.prevSelectedPosition = position;
                }
            }, 100);
            for (int i = 0; i < this.recyclerView.getChildCount(); i++) {
                ThemeSmallPreviewView child = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i);
                if (child != view) {
                    child.cancelAnimation();
                }
            }
            if (!this.adapter.items.get(position).chatTheme.showAsDefaultStub) {
                ((ThemeSmallPreviewView) view).playEmojiAnimation();
            }
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatThemeBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3915lambda$new$2$orgtelegramuiComponentsChatThemeBottomSheet(View view) {
        applySelectedTheme();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatThemeController.preloadAllWallpaperThumbs(true);
        ChatThemeController.preloadAllWallpaperThumbs(false);
        ChatThemeController.preloadAllWallpaperImages(true);
        ChatThemeController.preloadAllWallpaperImages(false);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.isApplyClicked = false;
        List<EmojiThemes> cachedThemes = this.themeDelegate.getCachedThemes();
        if (cachedThemes == null || cachedThemes.isEmpty()) {
            ChatThemeController.requestAllChatThemes(new ResultCallback<List<EmojiThemes>>() {
                public /* synthetic */ void onError(Throwable th) {
                    ResultCallback.CC.$default$onError((ResultCallback) this, th);
                }

                public void onComplete(List<EmojiThemes> result) {
                    if (result != null && !result.isEmpty()) {
                        ChatThemeBottomSheet.this.themeDelegate.setCachedThemes(result);
                    }
                    ChatThemeBottomSheet.this.onDataLoaded(result);
                }

                public void onError(TLRPC.TL_error error) {
                    Toast.makeText(ChatThemeBottomSheet.this.getContext(), error.text, 0).show();
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

    /* renamed from: lambda$onCreate$3$org-telegram-ui-Components-ChatThemeBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3916xc8d0fdc7() {
        this.hintView.showForView(this.darkThemeView, true);
    }

    public void onContainerTranslationYChanged(float y) {
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

    /* renamed from: lambda$close$4$org-telegram-ui-Components-ChatThemeBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3911lambda$close$4$orgtelegramuiComponentsChatThemeBottomSheet(DialogInterface dialogInterface, int i) {
        applySelectedTheme();
    }

    /* renamed from: lambda$close$5$org-telegram-ui-Components-ChatThemeBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3912lambda$close$5$orgtelegramuiComponentsChatThemeBottomSheet(DialogInterface dialogInterface, int i) {
        dismiss();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            private boolean isAnimationStarted = false;

            public void onAnimationProgress(float progress) {
                if (progress == 0.0f && !this.isAnimationStarted) {
                    ChatThemeBottomSheet.this.onAnimationStart();
                    this.isAnimationStarted = true;
                }
                ChatThemeBottomSheet.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(ChatThemeBottomSheet.this.getThemedColor("featuredStickers_addButton"), PorterDuff.Mode.MULTIPLY));
                ChatThemeBottomSheet chatThemeBottomSheet = ChatThemeBottomSheet.this;
                chatThemeBottomSheet.setOverlayNavBarColor(chatThemeBottomSheet.getThemedColor("windowBackgroundGray"));
                if (ChatThemeBottomSheet.this.isLightDarkChangeAnimation) {
                    ChatThemeBottomSheet.this.setItemsAnimationProgress(progress);
                }
                if (progress == 1.0f && this.isAnimationStarted) {
                    boolean unused = ChatThemeBottomSheet.this.isLightDarkChangeAnimation = false;
                    ChatThemeBottomSheet.this.onAnimationEnd();
                    this.isAnimationStarted = false;
                }
            }

            public void didSetColor() {
            }
        };
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, descriptionDelegate, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.recyclerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ThemeSmallPreviewView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.applyButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        themeDescriptions.add(new ThemeDescription(this.applyButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        Iterator<ThemeDescription> it = themeDescriptions.iterator();
        while (it.hasNext()) {
            it.next().resourcesProvider = this.themeDelegate;
        }
        return themeDescriptions;
    }

    public void setupLightDarkTheme(boolean isDark) {
        boolean z = isDark;
        ValueAnimator valueAnimator = this.changeDayNightViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        FrameLayout decorView1 = (FrameLayout) this.chatActivity.getParentActivity().getWindow().getDecorView();
        FrameLayout decorView2 = (FrameLayout) getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(decorView2.getWidth(), decorView2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(bitmap);
        this.darkThemeView.setAlpha(0.0f);
        decorView1.draw(bitmapCanvas);
        decorView2.draw(bitmapCanvas);
        this.darkThemeView.setAlpha(1.0f);
        final Paint xRefPaint = new Paint(1);
        xRefPaint.setColor(-16777216);
        xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint bitmapPaint = new Paint(1);
        bitmapPaint.setFilterBitmap(true);
        int[] position = new int[2];
        this.darkThemeView.getLocationInWindow(position);
        float x = (float) position[0];
        float y = (float) position[1];
        float cx = x + (((float) this.darkThemeView.getMeasuredWidth()) / 2.0f);
        float cy = y + (((float) this.darkThemeView.getMeasuredHeight()) / 2.0f);
        float r = ((float) Math.max(bitmap.getHeight(), bitmap.getWidth())) * 0.9f;
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapPaint.setShader(bitmapShader);
        FrameLayout frameLayout = decorView1;
        AnonymousClass5 r15 = r0;
        BitmapShader bitmapShader2 = bitmapShader;
        float y2 = y;
        final boolean z2 = isDark;
        float x2 = x;
        final Canvas canvas = bitmapCanvas;
        int[] iArr = position;
        final float f = cx;
        final float f2 = cy;
        Paint bitmapPaint2 = bitmapPaint;
        final float f3 = r;
        Paint paint = xRefPaint;
        final Bitmap bitmap2 = bitmap;
        Canvas canvas2 = bitmapCanvas;
        final Paint paint2 = bitmapPaint2;
        Bitmap bitmap3 = bitmap;
        final float f4 = x2;
        FrameLayout decorView22 = decorView2;
        final float f5 = y2;
        AnonymousClass5 r0 = new View(getContext()) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (z2) {
                    if (ChatThemeBottomSheet.this.changeDayNightViewProgress > 0.0f) {
                        canvas.drawCircle(f, f2, f3 * ChatThemeBottomSheet.this.changeDayNightViewProgress, xRefPaint);
                    }
                    canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint2);
                } else {
                    canvas.drawCircle(f, f2, f3 * (1.0f - ChatThemeBottomSheet.this.changeDayNightViewProgress), paint2);
                }
                canvas.save();
                canvas.translate(f4, f5);
                ChatThemeBottomSheet.this.darkThemeView.draw(canvas);
                canvas.restore();
            }
        };
        this.changeDayNightView = r15;
        this.changeDayNightViewProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.changeDayNightViewAnimator = ofFloat;
        final boolean z3 = isDark;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean changedNavigationBarColor = false;

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float unused = ChatThemeBottomSheet.this.changeDayNightViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                ChatThemeBottomSheet.this.changeDayNightView.invalidate();
                if (!this.changedNavigationBarColor && ChatThemeBottomSheet.this.changeDayNightViewProgress > 0.5f) {
                    this.changedNavigationBarColor = true;
                    AndroidUtilities.setLightNavigationBar(ChatThemeBottomSheet.this.getWindow(), true ^ z3);
                    AndroidUtilities.setNavigationBarColor(ChatThemeBottomSheet.this.getWindow(), ChatThemeBottomSheet.this.getThemedColor("windowBackgroundGray"));
                }
            }
        });
        this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (ChatThemeBottomSheet.this.changeDayNightView != null) {
                    if (ChatThemeBottomSheet.this.changeDayNightView.getParent() != null) {
                        ((ViewGroup) ChatThemeBottomSheet.this.changeDayNightView.getParent()).removeView(ChatThemeBottomSheet.this.changeDayNightView);
                    }
                    View unused = ChatThemeBottomSheet.this.changeDayNightView = null;
                }
                ValueAnimator unused2 = ChatThemeBottomSheet.this.changeDayNightViewAnimator = null;
                super.onAnimationEnd(animation);
            }
        });
        this.changeDayNightViewAnimator.setDuration(400);
        this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
        this.changeDayNightViewAnimator.start();
        decorView22.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
        AndroidUtilities.runOnUIThread(new ChatThemeBottomSheet$$ExternalSyntheticLambda5(this, z3));
    }

    /* renamed from: lambda$setupLightDarkTheme$6$org-telegram-ui-Components-ChatThemeBottomSheet  reason: not valid java name */
    public /* synthetic */ void m3917xc4b3db8f(boolean isDark) {
        Adapter adapter2 = this.adapter;
        if (adapter2 != null && adapter2.items != null) {
            setForceDark(isDark, true);
            ChatThemeItem chatThemeItem = this.selectedItem;
            if (chatThemeItem != null) {
                this.isLightDarkChangeAnimation = true;
                if (chatThemeItem.chatTheme.showAsDefaultStub) {
                    this.themeDelegate.setCurrentTheme((EmojiThemes) null, false, Boolean.valueOf(isDark));
                } else {
                    this.themeDelegate.setCurrentTheme(this.selectedItem.chatTheme, false, Boolean.valueOf(isDark));
                }
            }
            Adapter adapter3 = this.adapter;
            if (adapter3 != null && adapter3.items != null) {
                for (int i = 0; i < this.adapter.items.size(); i++) {
                    this.adapter.items.get(i).themeIndex = isDark;
                }
                this.adapter.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent event) {
        if (event == null || !hasChanges()) {
            return false;
        }
        int x = (int) event.getX();
        if (((int) event.getY()) >= this.containerView.getTop() && x >= this.containerView.getLeft() && x <= this.containerView.getRight()) {
            return false;
        }
        this.chatActivity.getFragmentView().dispatchTouchEvent(event);
        return true;
    }

    /* access modifiers changed from: private */
    public void onDataLoaded(List<EmojiThemes> result) {
        if (result != null && !result.isEmpty()) {
            ChatThemeItem noThemeItem = new ChatThemeItem(result.get(0));
            List<ChatThemeItem> items = new ArrayList<>(result.size());
            EmojiThemes currentTheme = this.themeDelegate.getCurrentTheme();
            items.add(0, noThemeItem);
            this.selectedItem = noThemeItem;
            for (int i = 1; i < result.size(); i++) {
                EmojiThemes chatTheme = result.get(i);
                ChatThemeItem item = new ChatThemeItem(chatTheme);
                chatTheme.loadPreviewColors(this.currentAccount);
                item.themeIndex = this.forceDark ? 1 : 0;
                items.add(item);
            }
            this.adapter.setItems(items);
            this.applyButton.setEnabled(true);
            this.applyTextView.setAlpha(0.0f);
            this.resetTextView.setAlpha(0.0f);
            this.recyclerView.setAlpha(0.0f);
            this.applyTextView.setVisibility(0);
            this.resetTextView.setVisibility(0);
            this.darkThemeView.setVisibility(0);
            boolean showRestText = false;
            if (currentTheme != null) {
                int selectedPosition = -1;
                int i2 = 0;
                while (true) {
                    if (i2 == items.size()) {
                        break;
                    } else if (items.get(i2).chatTheme.getEmoticon().equals(currentTheme.getEmoticon())) {
                        this.selectedItem = items.get(i2);
                        selectedPosition = i2;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (selectedPosition != -1) {
                    this.prevSelectedPosition = selectedPosition;
                    this.adapter.setSelectedItem(selectedPosition);
                    if (selectedPosition > 0 && selectedPosition < items.size() / 2) {
                        selectedPosition--;
                    }
                    this.layoutManager.scrollToPositionWithOffset(Math.min(selectedPosition, this.adapter.items.size() - 1), 0);
                }
            } else {
                showRestText = true;
                this.adapter.setSelectedItem(0);
                this.layoutManager.scrollToPositionWithOffset(0, 0);
            }
            float f = 1.0f;
            this.recyclerView.animate().alpha(1.0f).setDuration(150).start();
            this.resetTextView.animate().alpha(showRestText ? 1.0f : 0.0f).setDuration(150).start();
            ViewPropertyAnimator animate = this.applyTextView.animate();
            if (showRestText) {
                f = 0.0f;
            }
            animate.alpha(f).setDuration(150).start();
            this.progressView.animate().alpha(0.0f).setListener(new HideViewAfterAnimation(this.progressView)).setDuration(150).start();
        }
    }

    /* access modifiers changed from: private */
    public void onAnimationStart() {
        Adapter adapter2 = this.adapter;
        if (!(adapter2 == null || adapter2.items == null)) {
            for (ChatThemeItem item : this.adapter.items) {
                item.themeIndex = this.forceDark ? 1 : 0;
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

    private void setDarkButtonColor(int color) {
        this.darkThemeDrawable.setLayerColor("Sunny.**", color);
        this.darkThemeDrawable.setLayerColor("Path.**", color);
        this.darkThemeDrawable.setLayerColor("Path 10.**", color);
        this.darkThemeDrawable.setLayerColor("Path 11.**", color);
    }

    private void setForceDark(boolean isDark, boolean playAnimation) {
        if (this.forceDark != isDark) {
            this.forceDark = isDark;
            int i = 0;
            if (playAnimation) {
                RLottieDrawable rLottieDrawable = this.darkThemeDrawable;
                if (isDark) {
                    i = rLottieDrawable.getFramesCount();
                }
                rLottieDrawable.setCustomEndFrame(i);
                RLottieImageView rLottieImageView = this.darkThemeView;
                if (rLottieImageView != null) {
                    rLottieImageView.playAnimation();
                    return;
                }
                return;
            }
            int frame = isDark ? this.darkThemeDrawable.getFramesCount() - 1 : 0;
            this.darkThemeDrawable.setCurrentFrame(frame, false, true);
            this.darkThemeDrawable.setCustomEndFrame(frame);
            RLottieImageView rLottieImageView2 = this.darkThemeView;
            if (rLottieImageView2 != null) {
                rLottieImageView2.invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setItemsAnimationProgress(float progress) {
        for (int i = 0; i < this.adapter.getItemCount(); i++) {
            this.adapter.items.get(i).animationProgress = progress;
        }
    }

    private void applySelectedTheme() {
        Bulletin bulletin = null;
        EmojiThemes newTheme = this.selectedItem.chatTheme;
        if (newTheme.showAsDefaultStub) {
            newTheme = null;
        }
        ChatThemeItem chatThemeItem = this.selectedItem;
        if (!(chatThemeItem == null || newTheme == this.originalTheme)) {
            EmojiThemes chatTheme = chatThemeItem.chatTheme;
            String emoticon = (chatTheme == null || chatTheme.showAsDefaultStub) ? null : chatTheme.getEmoticon();
            ChatThemeController.getInstance(this.currentAccount).setDialogTheme(this.chatActivity.getDialogId(), emoticon, true);
            if (chatTheme == null || chatTheme.showAsDefaultStub) {
                this.themeDelegate.setCurrentTheme((EmojiThemes) null, true, Boolean.valueOf(this.originalIsDark));
            } else {
                this.themeDelegate.setCurrentTheme(chatTheme, true, Boolean.valueOf(this.originalIsDark));
            }
            this.isApplyClicked = true;
            TLRPC.User user = this.chatActivity.getCurrentUser();
            if (user != null && !user.self) {
                boolean themeDisabled = false;
                if (TextUtils.isEmpty(emoticon)) {
                    themeDisabled = true;
                    emoticon = "❌";
                }
                StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(getContext(), (TLObject) null, -1, emoticon != null ? MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoticon) : null, this.chatActivity.getResourceProvider());
                stickerSetBulletinLayout.subtitleTextView.setVisibility(8);
                if (themeDisabled) {
                    stickerSetBulletinLayout.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ThemeAlsoDisabledForHint", NUM, user.first_name)));
                } else {
                    stickerSetBulletinLayout.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ThemeAlsoAppliedForHint", NUM, user.first_name)));
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
        String newEmoticon = null;
        String oldEmoticon = emojiThemes != null ? emojiThemes.getEmoticon() : null;
        if (TextUtils.isEmpty(oldEmoticon)) {
            oldEmoticon = "❌";
        }
        if (this.selectedItem.chatTheme != null) {
            newEmoticon = this.selectedItem.chatTheme.getEmoticon();
        }
        if (TextUtils.isEmpty(newEmoticon)) {
            newEmoticon = "❌";
        }
        return !ColorUtils$$ExternalSyntheticBackport0.m(oldEmoticon, newEmoticon);
    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int currentAccount;
        private final int currentViewType;
        public List<ChatThemeItem> items;
        private HashMap<String, Theme.ThemeInfo> loadingThemes = new HashMap<>();
        private HashMap<Theme.ThemeInfo, String> loadingWallpapers = new HashMap<>();
        private final Theme.ResourcesProvider resourcesProvider;
        private int selectedItemPosition = -1;
        private WeakReference<ThemeSmallPreviewView> selectedViewRef;

        public Adapter(int currentAccount2, Theme.ResourcesProvider resourcesProvider2, int type) {
            this.currentViewType = type;
            this.resourcesProvider = resourcesProvider2;
            this.currentAccount = currentAccount2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new ThemeSmallPreviewView(parent.getContext(), this.currentAccount, this.resourcesProvider, this.currentViewType));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ThemeSmallPreviewView view = (ThemeSmallPreviewView) holder.itemView;
            Theme.ThemeInfo themeInfo = this.items.get(position).chatTheme.getThemeInfo(this.items.get(position).themeIndex);
            if (themeInfo != null && themeInfo.pathToFile != null && !themeInfo.previewParsed && new File(themeInfo.pathToFile).exists()) {
                parseTheme(themeInfo);
            }
            boolean animated = true;
            ChatThemeItem newItem = this.items.get(position);
            if (view.chatThemeItem == null || !view.chatThemeItem.chatTheme.getEmoticon().equals(newItem.chatTheme.getEmoticon()) || DrawerProfileCell.switchingTheme || view.lastThemeIndex != newItem.themeIndex) {
                animated = false;
            }
            view.setItem(newItem, animated);
            view.setSelected(position == this.selectedItemPosition, animated);
            if (position == this.selectedItemPosition) {
                this.selectedViewRef = new WeakReference<>(view);
            }
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:179:?, code lost:
            r21.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:180:0x0315, code lost:
            r0 = th;
         */
        /* JADX WARNING: Removed duplicated region for block: B:131:0x025d A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }, FALL_THROUGH] */
        /* JADX WARNING: Removed duplicated region for block: B:132:0x025e A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:135:0x0266 A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:138:0x026e A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:141:0x0276 A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:144:0x027e A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:147:0x0286 A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:152:0x0292 A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:153:0x0293 A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:154:0x0296 A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:155:0x0299 A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:156:0x029c A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:157:0x02a0 A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:158:0x02a4 A[Catch:{ Exception -> 0x023b, all -> 0x02a8 }] */
        /* JADX WARNING: Removed duplicated region for block: B:195:0x0333  */
        /* JADX WARNING: Removed duplicated region for block: B:201:0x0375  */
        /* JADX WARNING: Unknown top exception splitter block from list: {B:178:0x0311=Splitter:B:178:0x0311, B:186:0x0324=Splitter:B:186:0x0324} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean parseTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r26) {
            /*
                r25 = this;
                r1 = r25
                r2 = r26
                java.lang.String r3 = "chat_inBubble"
                if (r2 == 0) goto L_0x037d
                java.lang.String r0 = r2.pathToFile
                if (r0 != 0) goto L_0x000e
                goto L_0x037d
            L_0x000e:
                r5 = 0
                java.io.File r0 = new java.io.File
                java.lang.String r6 = r2.pathToFile
                r0.<init>(r6)
                r6 = r0
                r7 = 1
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0325 }
                r0.<init>(r6)     // Catch:{ all -> 0x0325 }
                r8 = r0
                r0 = 0
                r9 = 0
            L_0x0020:
                byte[] r10 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x0317 }
                int r10 = r8.read(r10)     // Catch:{ all -> 0x0317 }
                r11 = r10
                r12 = -1
                if (r10 == r12) goto L_0x0309
                r10 = r0
                r13 = 0
                r14 = 0
                r24 = r9
                r9 = r0
                r0 = r24
            L_0x0032:
                if (r14 >= r11) goto L_0x02e3
                byte[] r15 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x0317 }
                byte r15 = r15[r14]     // Catch:{ all -> 0x0317 }
                r12 = 10
                if (r15 != r12) goto L_0x02cb
                int r12 = r0 + 1
                int r0 = r14 - r13
                int r15 = r0 + 1
                java.lang.String r0 = new java.lang.String     // Catch:{ all -> 0x0317 }
                byte[] r4 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x0317 }
                int r7 = r15 + -1
                r16 = r5
                java.lang.String r5 = "UTF-8"
                r0.<init>(r4, r13, r7, r5)     // Catch:{ all -> 0x02c1 }
                r4 = r0
                java.lang.String r0 = "WLS="
                boolean r0 = r4.startsWith(r0)     // Catch:{ all -> 0x02c1 }
                r5 = 4
                if (r0 == 0) goto L_0x01b2
                java.lang.String r0 = r4.substring(r5)     // Catch:{ all -> 0x01a8 }
                r5 = r0
                android.net.Uri r0 = android.net.Uri.parse(r5)     // Catch:{ all -> 0x01a8 }
                r7 = r0
                java.lang.String r0 = "slug"
                java.lang.String r0 = r7.getQueryParameter(r0)     // Catch:{ all -> 0x01a8 }
                r2.slug = r0     // Catch:{ all -> 0x01a8 }
                java.io.File r0 = new java.io.File     // Catch:{ all -> 0x01a8 }
                r17 = r6
                java.io.File r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x01cc }
                r18 = r11
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x01cc }
                r11.<init>()     // Catch:{ all -> 0x01cc }
                r19 = r12
                java.lang.String r12 = org.telegram.messenger.Utilities.MD5(r5)     // Catch:{ all -> 0x01cc }
                r11.append(r12)     // Catch:{ all -> 0x01cc }
                java.lang.String r12 = ".wp"
                r11.append(r12)     // Catch:{ all -> 0x01cc }
                java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x01cc }
                r0.<init>(r6, r11)     // Catch:{ all -> 0x01cc }
                java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x01cc }
                r2.pathToWallpaper = r0     // Catch:{ all -> 0x01cc }
                java.lang.String r0 = "mode"
                java.lang.String r0 = r7.getQueryParameter(r0)     // Catch:{ all -> 0x01cc }
                if (r0 == 0) goto L_0x00cc
                java.lang.String r6 = r0.toLowerCase()     // Catch:{ all -> 0x01cc }
                r0 = r6
                java.lang.String r6 = " "
                java.lang.String[] r6 = r0.split(r6)     // Catch:{ all -> 0x01cc }
                if (r6 == 0) goto L_0x00c9
                int r11 = r6.length     // Catch:{ all -> 0x01cc }
                if (r11 <= 0) goto L_0x00c9
                r11 = 0
            L_0x00ae:
                int r12 = r6.length     // Catch:{ all -> 0x01cc }
                if (r11 >= r12) goto L_0x00c6
                java.lang.String r12 = "blur"
                r20 = r0
                r0 = r6[r11]     // Catch:{ all -> 0x01cc }
                boolean r0 = r12.equals(r0)     // Catch:{ all -> 0x01cc }
                if (r0 == 0) goto L_0x00c1
                r12 = 1
                r2.isBlured = r12     // Catch:{ all -> 0x01cc }
                goto L_0x00ce
            L_0x00c1:
                int r11 = r11 + 1
                r0 = r20
                goto L_0x00ae
            L_0x00c6:
                r20 = r0
                goto L_0x00ce
            L_0x00c9:
                r20 = r0
                goto L_0x00ce
            L_0x00cc:
                r20 = r0
            L_0x00ce:
                java.lang.String r0 = "pattern"
                java.lang.String r0 = r7.getQueryParameter(r0)     // Catch:{ all -> 0x01cc }
                r6 = r0
                boolean r0 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x01cc }
                if (r0 != 0) goto L_0x01a0
                java.lang.String r0 = "bg_color"
                java.lang.String r0 = r7.getQueryParameter(r0)     // Catch:{ Exception -> 0x0166 }
                boolean r11 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0166 }
                if (r11 != 0) goto L_0x0163
                r11 = 6
                r21 = r5
                r12 = 0
                java.lang.String r5 = r0.substring(r12, r11)     // Catch:{ Exception -> 0x0161 }
                r12 = 16
                int r5 = java.lang.Integer.parseInt(r5, r12)     // Catch:{ Exception -> 0x0161 }
                r22 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r5 = r5 | r22
                r2.patternBgColor = r5     // Catch:{ Exception -> 0x0161 }
                int r5 = r0.length()     // Catch:{ Exception -> 0x0161 }
                r12 = 13
                if (r5 < r12) goto L_0x011c
                char r5 = r0.charAt(r11)     // Catch:{ Exception -> 0x0161 }
                boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x0161 }
                if (r5 == 0) goto L_0x011c
                r5 = 7
                java.lang.String r5 = r0.substring(r5, r12)     // Catch:{ Exception -> 0x0161 }
                r11 = 16
                int r5 = java.lang.Integer.parseInt(r5, r11)     // Catch:{ Exception -> 0x0161 }
                r5 = r5 | r22
                r2.patternBgGradientColor1 = r5     // Catch:{ Exception -> 0x0161 }
            L_0x011c:
                int r5 = r0.length()     // Catch:{ Exception -> 0x0161 }
                r11 = 20
                if (r5 < r11) goto L_0x013e
                char r5 = r0.charAt(r12)     // Catch:{ Exception -> 0x0161 }
                boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x0161 }
                if (r5 == 0) goto L_0x013e
                r5 = 14
                java.lang.String r5 = r0.substring(r5, r11)     // Catch:{ Exception -> 0x0161 }
                r12 = 16
                int r5 = java.lang.Integer.parseInt(r5, r12)     // Catch:{ Exception -> 0x0161 }
                r5 = r5 | r22
                r2.patternBgGradientColor2 = r5     // Catch:{ Exception -> 0x0161 }
            L_0x013e:
                int r5 = r0.length()     // Catch:{ Exception -> 0x0161 }
                r12 = 27
                if (r5 != r12) goto L_0x0165
                char r5 = r0.charAt(r11)     // Catch:{ Exception -> 0x0161 }
                boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x0161 }
                if (r5 == 0) goto L_0x0165
                r5 = 21
                java.lang.String r5 = r0.substring(r5)     // Catch:{ Exception -> 0x0161 }
                r11 = 16
                int r5 = java.lang.Integer.parseInt(r5, r11)     // Catch:{ Exception -> 0x0161 }
                r5 = r5 | r22
                r2.patternBgGradientColor3 = r5     // Catch:{ Exception -> 0x0161 }
                goto L_0x0165
            L_0x0161:
                r0 = move-exception
                goto L_0x0169
            L_0x0163:
                r21 = r5
            L_0x0165:
                goto L_0x0169
            L_0x0166:
                r0 = move-exception
                r21 = r5
            L_0x0169:
                java.lang.String r0 = "rotation"
                java.lang.String r0 = r7.getQueryParameter(r0)     // Catch:{ Exception -> 0x0180 }
                boolean r5 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0180 }
                if (r5 != 0) goto L_0x017f
                java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0180 }
                int r5 = r5.intValue()     // Catch:{ Exception -> 0x0180 }
                r2.patternBgGradientRotation = r5     // Catch:{ Exception -> 0x0180 }
            L_0x017f:
                goto L_0x0181
            L_0x0180:
                r0 = move-exception
            L_0x0181:
                java.lang.String r0 = "intensity"
                java.lang.String r0 = r7.getQueryParameter(r0)     // Catch:{ all -> 0x01cc }
                boolean r5 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x01cc }
                if (r5 != 0) goto L_0x0197
                java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x01cc }
                int r5 = r5.intValue()     // Catch:{ all -> 0x01cc }
                r2.patternIntensity = r5     // Catch:{ all -> 0x01cc }
            L_0x0197:
                int r5 = r2.patternIntensity     // Catch:{ all -> 0x01cc }
                if (r5 != 0) goto L_0x01a2
                r5 = 50
                r2.patternIntensity = r5     // Catch:{ all -> 0x01cc }
                goto L_0x01a2
            L_0x01a0:
                r21 = r5
            L_0x01a2:
                r23 = r4
                r21 = r8
                goto L_0x02b4
            L_0x01a8:
                r0 = move-exception
                r17 = r6
                r1 = r0
                r21 = r8
                r5 = r16
                goto L_0x031f
            L_0x01b2:
                r17 = r6
                r18 = r11
                r19 = r12
                java.lang.String r0 = "WPS"
                boolean r0 = r4.startsWith(r0)     // Catch:{ all -> 0x02b9 }
                if (r0 == 0) goto L_0x01d4
                int r0 = r9 + r15
                r2.previewWallpaperOffset = r0     // Catch:{ all -> 0x01cc }
                r0 = 1
                r5 = r0
                r21 = r8
                r0 = r19
                goto L_0x02eb
            L_0x01cc:
                r0 = move-exception
                r1 = r0
                r21 = r8
                r5 = r16
                goto L_0x031f
            L_0x01d4:
                r0 = 61
                int r0 = r4.indexOf(r0)     // Catch:{ all -> 0x02b9 }
                r6 = r0
                r7 = -1
                if (r0 == r7) goto L_0x02ae
                r11 = 0
                java.lang.String r0 = r4.substring(r11, r6)     // Catch:{ all -> 0x02b9 }
                r11 = r0
                boolean r0 = r11.equals(r3)     // Catch:{ all -> 0x02b9 }
                java.lang.String r12 = "key_chat_wallpaper_gradient_to3"
                java.lang.String r5 = "key_chat_wallpaper_gradient_to2"
                java.lang.String r7 = "chat_wallpaper_gradient_to"
                java.lang.String r1 = "chat_wallpaper"
                r21 = r8
                java.lang.String r8 = "chat_outBubble"
                if (r0 != 0) goto L_0x0219
                boolean r0 = r11.equals(r8)     // Catch:{ all -> 0x02a8 }
                if (r0 != 0) goto L_0x0219
                boolean r0 = r11.equals(r1)     // Catch:{ all -> 0x02a8 }
                if (r0 != 0) goto L_0x0219
                boolean r0 = r11.equals(r7)     // Catch:{ all -> 0x02a8 }
                if (r0 != 0) goto L_0x0219
                boolean r0 = r11.equals(r5)     // Catch:{ all -> 0x02a8 }
                if (r0 != 0) goto L_0x0219
                boolean r0 = r11.equals(r12)     // Catch:{ all -> 0x02a8 }
                if (r0 == 0) goto L_0x0215
                goto L_0x0219
            L_0x0215:
                r23 = r4
                goto L_0x02b4
            L_0x0219:
                int r0 = r6 + 1
                java.lang.String r0 = r4.substring(r0)     // Catch:{ all -> 0x02a8 }
                r22 = r0
                int r0 = r22.length()     // Catch:{ all -> 0x02a8 }
                if (r0 <= 0) goto L_0x0248
                r23 = r4
                r4 = r22
                r22 = r6
                r6 = 0
                char r0 = r4.charAt(r6)     // Catch:{ all -> 0x02a8 }
                r6 = 35
                if (r0 != r6) goto L_0x024e
                int r0 = android.graphics.Color.parseColor(r4)     // Catch:{ Exception -> 0x023b }
            L_0x023a:
                goto L_0x0256
            L_0x023b:
                r0 = move-exception
                r6 = r0
                r0 = r6
                java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x02a8 }
                int r6 = r6.intValue()     // Catch:{ all -> 0x02a8 }
                r0 = r6
                goto L_0x023a
            L_0x0248:
                r23 = r4
                r4 = r22
                r22 = r6
            L_0x024e:
                java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ all -> 0x02a8 }
                int r0 = r0.intValue()     // Catch:{ all -> 0x02a8 }
            L_0x0256:
                int r6 = r11.hashCode()     // Catch:{ all -> 0x02a8 }
                switch(r6) {
                    case -1625862693: goto L_0x0286;
                    case -633951866: goto L_0x027e;
                    case 1269980952: goto L_0x0276;
                    case 1381936524: goto L_0x026e;
                    case 1381936525: goto L_0x0266;
                    case 2052611411: goto L_0x025e;
                    default: goto L_0x025d;
                }     // Catch:{ all -> 0x02a8 }
            L_0x025d:
                goto L_0x028e
            L_0x025e:
                boolean r1 = r11.equals(r8)     // Catch:{ all -> 0x02a8 }
                if (r1 == 0) goto L_0x025d
                r1 = 1
                goto L_0x028f
            L_0x0266:
                boolean r1 = r11.equals(r12)     // Catch:{ all -> 0x02a8 }
                if (r1 == 0) goto L_0x025d
                r1 = 5
                goto L_0x028f
            L_0x026e:
                boolean r1 = r11.equals(r5)     // Catch:{ all -> 0x02a8 }
                if (r1 == 0) goto L_0x025d
                r1 = 4
                goto L_0x028f
            L_0x0276:
                boolean r1 = r11.equals(r3)     // Catch:{ all -> 0x02a8 }
                if (r1 == 0) goto L_0x025d
                r1 = 0
                goto L_0x028f
            L_0x027e:
                boolean r1 = r11.equals(r7)     // Catch:{ all -> 0x02a8 }
                if (r1 == 0) goto L_0x025d
                r1 = 3
                goto L_0x028f
            L_0x0286:
                boolean r1 = r11.equals(r1)     // Catch:{ all -> 0x02a8 }
                if (r1 == 0) goto L_0x025d
                r1 = 2
                goto L_0x028f
            L_0x028e:
                r1 = -1
            L_0x028f:
                switch(r1) {
                    case 0: goto L_0x02a4;
                    case 1: goto L_0x02a0;
                    case 2: goto L_0x029c;
                    case 3: goto L_0x0299;
                    case 4: goto L_0x0296;
                    case 5: goto L_0x0293;
                    default: goto L_0x0292;
                }     // Catch:{ all -> 0x02a8 }
            L_0x0292:
                goto L_0x02b4
            L_0x0293:
                r2.previewBackgroundGradientColor3 = r0     // Catch:{ all -> 0x02a8 }
                goto L_0x02b4
            L_0x0296:
                r2.previewBackgroundGradientColor2 = r0     // Catch:{ all -> 0x02a8 }
                goto L_0x02b4
            L_0x0299:
                r2.previewBackgroundGradientColor1 = r0     // Catch:{ all -> 0x02a8 }
                goto L_0x02b4
            L_0x029c:
                r2.setPreviewBackgroundColor(r0)     // Catch:{ all -> 0x02a8 }
                goto L_0x02b4
            L_0x02a0:
                r2.setPreviewOutColor(r0)     // Catch:{ all -> 0x02a8 }
                goto L_0x02b4
            L_0x02a4:
                r2.setPreviewInColor(r0)     // Catch:{ all -> 0x02a8 }
                goto L_0x02b4
            L_0x02a8:
                r0 = move-exception
                r1 = r0
                r5 = r16
                goto L_0x031f
            L_0x02ae:
                r23 = r4
                r22 = r6
                r21 = r8
            L_0x02b4:
                int r13 = r13 + r15
                int r9 = r9 + r15
                r0 = r19
                goto L_0x02d3
            L_0x02b9:
                r0 = move-exception
                r21 = r8
                r1 = r0
                r5 = r16
                goto L_0x031f
            L_0x02c1:
                r0 = move-exception
                r17 = r6
                r21 = r8
                r1 = r0
                r5 = r16
                goto L_0x031f
            L_0x02cb:
                r16 = r5
                r17 = r6
                r21 = r8
                r18 = r11
            L_0x02d3:
                int r14 = r14 + 1
                r1 = r25
                r5 = r16
                r6 = r17
                r11 = r18
                r8 = r21
                r7 = 1
                r12 = -1
                goto L_0x0032
            L_0x02e3:
                r16 = r5
                r17 = r6
                r21 = r8
                r18 = r11
            L_0x02eb:
                if (r5 != 0) goto L_0x0311
                if (r10 != r9) goto L_0x02f0
                goto L_0x0311
            L_0x02f0:
                java.nio.channels.FileChannel r1 = r21.getChannel()     // Catch:{ all -> 0x0306 }
                long r6 = (long) r9     // Catch:{ all -> 0x0306 }
                r1.position(r6)     // Catch:{ all -> 0x0306 }
                r1 = r25
                r6 = r17
                r8 = r21
                r7 = 1
                r24 = r9
                r9 = r0
                r0 = r24
                goto L_0x0020
            L_0x0306:
                r0 = move-exception
                r1 = r0
                goto L_0x031f
            L_0x0309:
                r16 = r5
                r17 = r6
                r21 = r8
                r18 = r11
            L_0x0311:
                r21.close()     // Catch:{ all -> 0x0315 }
                goto L_0x032b
            L_0x0315:
                r0 = move-exception
                goto L_0x0328
            L_0x0317:
                r0 = move-exception
                r16 = r5
                r17 = r6
                r21 = r8
                r1 = r0
            L_0x031f:
                r21.close()     // Catch:{ all -> 0x0323 }
                goto L_0x0324
            L_0x0323:
                r0 = move-exception
            L_0x0324:
                throw r1     // Catch:{ all -> 0x0315 }
            L_0x0325:
                r0 = move-exception
                r17 = r6
            L_0x0328:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x032b:
                java.lang.String r0 = r2.pathToWallpaper
                if (r0 == 0) goto L_0x0375
                boolean r0 = r2.badWallpaper
                if (r0 != 0) goto L_0x0375
                java.io.File r0 = new java.io.File
                java.lang.String r1 = r2.pathToWallpaper
                r0.<init>(r1)
                r6 = r0
                boolean r0 = r6.exists()
                if (r0 != 0) goto L_0x0372
                r1 = r25
                java.util.HashMap<org.telegram.ui.ActionBar.Theme$ThemeInfo, java.lang.String> r0 = r1.loadingWallpapers
                boolean r0 = r0.containsKey(r2)
                if (r0 != 0) goto L_0x0370
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
                int r4 = r2.account
                org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
                org.telegram.ui.Components.ChatThemeBottomSheet$Adapter$$ExternalSyntheticLambda1 r7 = new org.telegram.ui.Components.ChatThemeBottomSheet$Adapter$$ExternalSyntheticLambda1
                r7.<init>(r1, r2)
                r4.sendRequest(r0, r7)
            L_0x0370:
                r3 = 0
                return r3
            L_0x0372:
                r1 = r25
                goto L_0x0379
            L_0x0375:
                r1 = r25
                r6 = r17
            L_0x0379:
                r3 = 1
                r2.previewParsed = r3
                return r3
            L_0x037d:
                r3 = 0
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatThemeBottomSheet.Adapter.parseTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo):boolean");
        }

        /* renamed from: lambda$parseTheme$1$org-telegram-ui-Components-ChatThemeBottomSheet$Adapter  reason: not valid java name */
        public /* synthetic */ void m3919xe20fb76b(Theme.ThemeInfo themeInfo, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new ChatThemeBottomSheet$Adapter$$ExternalSyntheticLambda0(this, response, themeInfo));
        }

        /* renamed from: lambda$parseTheme$0$org-telegram-ui-Components-ChatThemeBottomSheet$Adapter  reason: not valid java name */
        public /* synthetic */ void m3918xbc7bae6a(TLObject response, Theme.ThemeInfo themeInfo) {
            if (response instanceof TLRPC.TL_wallPaper) {
                TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) response;
                String name = FileLoader.getAttachFileName(wallPaper.document);
                if (!this.loadingThemes.containsKey(name)) {
                    this.loadingThemes.put(name, themeInfo);
                    FileLoader.getInstance(themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
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

        public void setItems(List<ChatThemeItem> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        public void setSelectedItem(int position) {
            int i = this.selectedItemPosition;
            if (i != position) {
                if (i >= 0) {
                    notifyItemChanged(i);
                    WeakReference<ThemeSmallPreviewView> weakReference = this.selectedViewRef;
                    ThemeSmallPreviewView view = weakReference == null ? null : (ThemeSmallPreviewView) weakReference.get();
                    if (view != null) {
                        view.setSelected(false);
                    }
                }
                this.selectedItemPosition = position;
                notifyItemChanged(position);
            }
        }
    }

    public static class ChatThemeItem {
        public float animationProgress = 1.0f;
        public final EmojiThemes chatTheme;
        public Bitmap icon;
        public boolean isSelected;
        public Drawable previewDrawable;
        public int themeIndex;

        public ChatThemeItem(EmojiThemes chatTheme2) {
            this.chatTheme = chatTheme2;
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

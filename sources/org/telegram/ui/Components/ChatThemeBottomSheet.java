package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WallPaperSettings;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.ChatTheme;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
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
    private final ChatTheme originalTheme;
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
            org.telegram.ui.ActionBar.ChatTheme r2 = r22.getCurrentTheme()
            r0.originalTheme = r2
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r2 = r2.isDark()
            r0.originalIsDark = r2
            org.telegram.ui.Components.ChatThemeBottomSheet$Adapter r2 = new org.telegram.ui.Components.ChatThemeBottomSheet$Adapter
            r2.<init>(r1)
            r0.adapter = r2
            r4 = 0
            r0.setDimBehind(r4)
            r0.setCanDismissWithSwipe(r4)
            r0.setApplyBottomPadding(r4)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            android.content.Context r6 = r20.getContext()
            r5.<init>(r6)
            r0.setCustomView(r5)
            android.widget.TextView r6 = new android.widget.TextView
            android.content.Context r7 = r20.getContext()
            r6.<init>(r7)
            r0.titleView = r6
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.MIDDLE
            r6.setEllipsize(r7)
            r6.setLines(r3)
            r6.setSingleLine(r3)
            java.lang.String r7 = "SelectTheme"
            r8 = 2131627564(0x7f0e0e2c, float:1.8882396E38)
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
            r5.addView(r6, r8)
            java.lang.String r6 = "featuredStickers_addButton"
            int r8 = r0.getThemedColor(r6)
            r9 = 1105199104(0x41e00000, float:28.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r9)
            org.telegram.ui.Components.RLottieDrawable r9 = new org.telegram.ui.Components.RLottieDrawable
            r12 = 2131558489(0x7f0d0059, float:1.8742295E38)
            java.lang.String r13 = "NUM"
            r16 = 1
            r17 = 0
            r11 = r9
            r14 = r15
            r11.<init>((int) r12, (java.lang.String) r13, (int) r14, (int) r15, (boolean) r16, (int[]) r17)
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
            r15 = 0
            r16 = 1088421888(0x40e00000, float:7.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r5.addView(r8, r9)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r8 = r8.isDark()
            r8 = r8 ^ r3
            r0.forceDark = r8
            org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r8 = r8.isDark()
            r0.setForceDark(r8, r4)
            org.telegram.ui.Components.ChatThemeBottomSheet$1 r8 = new org.telegram.ui.Components.ChatThemeBottomSheet$1
            android.content.Context r9 = r20.getContext()
            r8.<init>(r0, r9)
            r0.scroller = r8
            org.telegram.ui.Components.RecyclerListView r8 = new org.telegram.ui.Components.RecyclerListView
            android.content.Context r9 = r20.getContext()
            r8.<init>(r9)
            r0.recyclerView = r8
            r8.setAdapter(r2)
            r8.setClipChildren(r4)
            r8.setClipToPadding(r4)
            r8.setHasFixedSize(r3)
            r2 = 0
            r8.setItemAnimator(r2)
            r8.setNestedScrollingEnabled(r4)
            androidx.recyclerview.widget.LinearLayoutManager r2 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r9 = r20.getContext()
            r2.<init>(r9, r4, r4)
            r0.layoutManager = r2
            r8.setLayoutManager(r2)
            r2 = 1094713344(0x41400000, float:12.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r8.setPadding(r9, r4, r2, r4)
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
            r2.setVisibility(r4)
            r11 = -1
            r12 = 1120927744(0x42d00000, float:104.0)
            r13 = 8388611(0x800003, float:1.1754948E-38)
            r14 = 0
            r15 = 1110441984(0x42300000, float:44.0)
            r16 = 0
            r17 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r5.addView(r2, r9)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r5.addView(r8, r2)
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
            r2.setEnabled(r4)
            org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda3 r4 = new org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda3
            r4.<init>(r0)
            r2.setOnClickListener(r4)
            r8 = -1
            r9 = 1111490560(0x42400000, float:48.0)
            r10 = 8388611(0x800003, float:1.1754948E-38)
            r11 = 1098907648(0x41800000, float:16.0)
            r12 = 1126301696(0x43220000, float:162.0)
            r13 = 1098907648(0x41800000, float:16.0)
            r14 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r5.addView(r2, r4)
            android.widget.TextView r2 = new android.widget.TextView
            android.content.Context r4 = r20.getContext()
            r2.<init>(r4)
            r0.resetTextView = r2
            r4 = 0
            r2.setAlpha(r4)
            android.widget.TextView r2 = r0.resetTextView
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r4)
            android.widget.TextView r2 = r0.resetTextView
            r4 = 17
            r2.setGravity(r4)
            android.widget.TextView r2 = r0.resetTextView
            r2.setLines(r3)
            android.widget.TextView r2 = r0.resetTextView
            r2.setSingleLine(r3)
            android.widget.TextView r2 = r0.resetTextView
            org.telegram.ui.ActionBar.ChatTheme r1 = r22.getCurrentTheme()
            if (r1 != 0) goto L_0x0209
            r1 = 2131625271(0x7f0e0537, float:1.8877745E38)
            java.lang.String r6 = "DoNoSetTheme"
            goto L_0x020e
        L_0x0209:
            r1 = 2131624855(0x7f0e0397, float:1.8876901E38)
            java.lang.String r6 = "ChatResetTheme"
        L_0x020e:
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
            r5.addView(r1, r9)
            android.widget.TextView r1 = new android.widget.TextView
            android.content.Context r9 = r20.getContext()
            r1.<init>(r9)
            r0.applyTextView = r1
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r1.setEllipsize(r9)
            android.widget.TextView r1 = r0.applyTextView
            r1.setGravity(r4)
            android.widget.TextView r1 = r0.applyTextView
            r1.setLines(r3)
            android.widget.TextView r1 = r0.applyTextView
            r1.setSingleLine(r3)
            android.widget.TextView r1 = r0.applyTextView
            r4 = 2131624830(0x7f0e037e, float:1.887685E38)
            java.lang.String r9 = "ChatApplyTheme"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            r1.setText(r4)
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
            r6 = -1
            r7 = 1111490560(0x42400000, float:48.0)
            r8 = 8388611(0x800003, float:1.1754948E-38)
            r9 = 1098907648(0x41800000, float:16.0)
            r10 = 1126301696(0x43220000, float:162.0)
            r11 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r5.addView(r1, r2)
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
        if (this.adapter.items.get(i) != this.selectedItem) {
            ChatThemeItem chatThemeItem = (ChatThemeItem) this.adapter.items.get(i);
            this.selectedItem = chatThemeItem;
            this.isLightDarkChangeAnimation = false;
            ChatTheme chatTheme = chatThemeItem.chatTheme;
            if (chatTheme == null || chatTheme.isDefault) {
                this.applyTextView.animate().alpha(0.0f).setDuration(300).start();
                this.resetTextView.animate().alpha(1.0f).setDuration(300).start();
            } else {
                this.resetTextView.animate().alpha(0.0f).setDuration(300).start();
                this.applyTextView.animate().alpha(1.0f).setDuration(300).start();
            }
            ChatTheme chatTheme2 = this.selectedItem.chatTheme;
            if (chatTheme2.isDefault) {
                themeDelegate2.setCurrentTheme((ChatTheme) null, true, Boolean.valueOf(this.forceDark));
            } else {
                themeDelegate2.setCurrentTheme(chatTheme2, true, Boolean.valueOf(this.forceDark));
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
                Adapter.ChatThemeView chatThemeView = (Adapter.ChatThemeView) this.recyclerView.getChildAt(i2);
                if (chatThemeView != view) {
                    chatThemeView.cancelAnimation();
                }
            }
            if (!((ChatThemeItem) this.adapter.items.get(i)).chatTheme.isDefault) {
                ((Adapter.ChatThemeView) view).playEmojiAnimation();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        applySelectedTheme();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        ChatThemeController.preloadAllWallpaperThumbs(true);
        ChatThemeController.preloadAllWallpaperThumbs(false);
        ChatThemeController.preloadAllWallpaperImages(true);
        ChatThemeController.preloadAllWallpaperImages(false);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.isApplyClicked = false;
        List<ChatTheme> cachedThemes = this.themeDelegate.getCachedThemes();
        if (cachedThemes == null || cachedThemes.isEmpty()) {
            ChatThemeController.requestAllChatThemes(new ResultCallback<List<ChatTheme>>() {
                public void onComplete(List<ChatTheme> list) {
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
        if (this.chatActivity.getCurrentUser() != null && (i = SharedConfig.dayNightThemeSwitchHintCount) > 0) {
            SharedConfig.updateDayNightThemeSwitchHintCount(i - 1);
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
        arrayList.add(new ThemeDescription(this.recyclerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{Adapter.ChatThemeView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackgroundGray"));
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
                ChatTheme chatTheme = chatThemeItem.chatTheme;
                if (chatTheme.isDefault) {
                    this.themeDelegate.setCurrentTheme((ChatTheme) null, false, Boolean.valueOf(z));
                } else {
                    this.themeDelegate.setCurrentTheme(chatTheme, false, Boolean.valueOf(z));
                }
            }
            Adapter adapter3 = this.adapter;
            if (adapter3 != null && adapter3.items != null) {
                for (int i = 0; i < this.adapter.items.size(); i++) {
                    ((ChatThemeItem) this.adapter.items.get(i)).isDark = z;
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
    public void onDataLoaded(List<ChatTheme> list) {
        if (list != null && !list.isEmpty()) {
            boolean z = false;
            ChatThemeItem chatThemeItem = new ChatThemeItem(list.get(0));
            ArrayList arrayList = new ArrayList(list.size());
            ChatTheme currentTheme = this.themeDelegate.getCurrentTheme();
            arrayList.add(0, chatThemeItem);
            this.selectedItem = chatThemeItem;
            for (int i = 1; i < list.size(); i++) {
                ChatTheme chatTheme = list.get(i);
                ChatThemeItem chatThemeItem2 = new ChatThemeItem(chatTheme);
                HashMap<String, Integer> currentColors = chatTheme.getCurrentColors(this.chatActivity.getCurrentAccount(), true);
                Integer num = currentColors.get("chat_inBubble");
                if (num == null) {
                    num = Integer.valueOf(getThemedColor("chat_inBubble"));
                }
                chatThemeItem2.inBubbleColorDark = num.intValue();
                Integer num2 = currentColors.get("chat_outBubble");
                if (num2 == null) {
                    num2 = Integer.valueOf(getThemedColor("chat_outBubble"));
                }
                chatThemeItem2.outBubbleColorDark = num2.intValue();
                Integer num3 = currentColors.get("featuredStickers_addButton");
                chatThemeItem2.strokeColorLight = num3 != null ? num3.intValue() : 0;
                HashMap<String, Integer> currentColors2 = chatTheme.getCurrentColors(this.chatActivity.getCurrentAccount(), false);
                Integer num4 = currentColors2.get("chat_inBubble");
                if (num4 == null) {
                    num4 = Integer.valueOf(getThemedColor("chat_inBubble"));
                }
                chatThemeItem2.inBubbleColorLight = num4.intValue();
                Integer num5 = currentColors2.get("chat_outBubble");
                if (num5 == null) {
                    num5 = Integer.valueOf(getThemedColor("chat_outBubble"));
                }
                chatThemeItem2.outBubbleColorLight = num5.intValue();
                Integer num6 = currentColors2.get("featuredStickers_addButton");
                chatThemeItem2.strokeColorDark = num6 != null ? num6.intValue() : 0;
                chatThemeItem2.isDark = this.forceDark;
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
        Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            for (ChatThemeItem chatThemeItem : adapter2.items) {
                chatThemeItem.isDark = this.forceDark;
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
            ((ChatThemeItem) this.adapter.items.get(i)).animationProgress = f;
        }
    }

    private void applySelectedTheme() {
        boolean z;
        ChatThemeItem chatThemeItem = this.selectedItem;
        ChatTheme chatTheme = chatThemeItem.chatTheme;
        boolean z2 = chatTheme.isDefault;
        Bulletin bulletin = null;
        ChatTheme chatTheme2 = z2 ? null : chatTheme;
        if (!(chatThemeItem == null || chatTheme2 == this.originalTheme)) {
            String emoticon = (chatTheme == null || z2) ? null : chatTheme.getEmoticon();
            ChatThemeController.getInstance(this.currentAccount).setDialogTheme(this.chatActivity.getDialogId(), emoticon, true);
            if (chatTheme == null || chatTheme.isDefault) {
                this.themeDelegate.setCurrentTheme((ChatTheme) null, true, Boolean.valueOf(this.originalIsDark));
            } else {
                this.themeDelegate.setCurrentTheme(chatTheme, true, Boolean.valueOf(this.originalIsDark));
            }
            this.isApplyClicked = true;
            TLRPC$User currentUser = this.chatActivity.getCurrentUser();
            if (currentUser != null) {
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
        ChatTheme chatTheme = this.originalTheme;
        String str = null;
        String emoticon = chatTheme != null ? chatTheme.getEmoticon() : null;
        String str2 = "❌";
        if (TextUtils.isEmpty(emoticon)) {
            emoticon = str2;
        }
        ChatTheme chatTheme2 = this.selectedItem.chatTheme;
        if (chatTheme2 != null) {
            str = chatTheme2.getEmoticon();
        }
        if (!TextUtils.isEmpty(str)) {
            str2 = str;
        }
        return !ObjectsCompat$$ExternalSyntheticBackport0.m(emoticon, str2);
    }

    @SuppressLint({"NotifyDataSetChanged"})
    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        /* access modifiers changed from: private */
        public List<ChatThemeItem> items;
        private final Theme.ResourcesProvider resourcesProvider;
        private int selectedItemPosition = -1;
        private WeakReference<ChatThemeView> selectedViewRef;

        public Adapter(Theme.ResourcesProvider resourcesProvider2) {
            this.resourcesProvider = resourcesProvider2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new ChatThemeView(viewGroup.getContext(), this.resourcesProvider));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ChatThemeView chatThemeView = (ChatThemeView) viewHolder.itemView;
            chatThemeView.setItem(this.items.get(i));
            chatThemeView.setSelected(i == this.selectedItemPosition);
            if (i == this.selectedItemPosition) {
                this.selectedViewRef = new WeakReference<>(chatThemeView);
            }
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
                    ChatThemeView chatThemeView = (ChatThemeView) this.selectedViewRef.get();
                    if (chatThemeView != null) {
                        chatThemeView.setSelected(false);
                    }
                }
                this.selectedItemPosition = i;
                notifyItemChanged(i);
            }
        }

        private class ChatThemeView extends FrameLayout implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
            private final float BUBBLE_HEIGHT = ((float) AndroidUtilities.dp(21.0f));
            private final float BUBBLE_WIDTH = ((float) AndroidUtilities.dp(41.0f));
            private final float INNER_RADIUS = ((float) AndroidUtilities.dp(6.0f));
            private final float INNER_RECT_SPACE = ((float) AndroidUtilities.dp(4.0f));
            private final float STROKE_RADIUS = ((float) AndroidUtilities.dp(8.0f));
            Runnable animationCancelRunnable;
            private final Paint backgroundFillPaint = new Paint(1);
            private BackupImageView backupImageView;
            private ChatThemeItem chatThemeItem;
            private final Path clipPath;
            private final Paint inBubblePaint;
            private boolean isDark;
            private TextPaint noThemeTextPaint;
            private final Paint outBubblePaintFirst;
            private final Paint outBubblePaintSecond;
            private final RectF rectF;
            private final Theme.ResourcesProvider resourcesProvider;
            private ValueAnimator strokeAlphaAnimator;
            private final Paint strokePaint;
            private StaticLayout textLayout;

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public ChatThemeView(Context context, Theme.ResourcesProvider resourcesProvider2) {
                super(context);
                Paint paint = new Paint(1);
                this.strokePaint = paint;
                this.outBubblePaintFirst = new Paint(1);
                this.outBubblePaintSecond = new Paint(1);
                this.inBubblePaint = new Paint(1);
                this.rectF = new RectF();
                this.clipPath = new Path();
                this.resourcesProvider = resourcesProvider2;
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
                setBackgroundColor(getThemedColor("dialogBackgroundGray"));
                BackupImageView backupImageView2 = new BackupImageView(context);
                this.backupImageView = backupImageView2;
                backupImageView2.getImageReceiver().setCrossfadeWithOldImage(true);
                this.backupImageView.getImageReceiver().setAllowStartLottieAnimation(false);
                this.backupImageView.getImageReceiver().setAutoRepeat(0);
                addView(this.backupImageView, LayoutHelper.createFrame(28, 28.0f, 81, 0.0f, 0.0f, 0.0f, 12.0f));
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(77.0f), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), NUM));
            }

            /* access modifiers changed from: protected */
            public void onSizeChanged(int i, int i2, int i3, int i4) {
                super.onSizeChanged(i, i2, i3, i4);
                if (i != i3 || i2 != i4) {
                    RectF rectF2 = this.rectF;
                    float f = this.INNER_RECT_SPACE;
                    rectF2.set(f, f, ((float) i) - f, ((float) i2) - f);
                    this.clipPath.reset();
                    Path path = this.clipPath;
                    RectF rectF3 = this.rectF;
                    float f2 = this.INNER_RADIUS;
                    path.addRoundRect(rectF3, f2, f2, Path.Direction.CW);
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                ChatThemeItem chatThemeItem2 = this.chatThemeItem;
                if (chatThemeItem2 == null) {
                    super.dispatchDraw(canvas);
                    return;
                }
                if (chatThemeItem2.isSelected || this.strokeAlphaAnimator != null) {
                    float strokeWidth = this.strokePaint.getStrokeWidth() * 0.5f;
                    this.rectF.set(strokeWidth, strokeWidth, ((float) getWidth()) - strokeWidth, ((float) getHeight()) - strokeWidth);
                    RectF rectF2 = this.rectF;
                    float f = this.STROKE_RADIUS;
                    canvas.drawRoundRect(rectF2, f, f, this.strokePaint);
                }
                RectF rectF3 = this.rectF;
                float f2 = this.INNER_RECT_SPACE;
                rectF3.set(f2, f2, ((float) getWidth()) - this.INNER_RECT_SPACE, ((float) getHeight()) - this.INNER_RECT_SPACE);
                ChatThemeItem chatThemeItem3 = this.chatThemeItem;
                ChatTheme chatTheme = chatThemeItem3.chatTheme;
                if (chatTheme == null || chatTheme.isDefault) {
                    RectF rectF4 = this.rectF;
                    float f3 = this.INNER_RADIUS;
                    canvas.drawRoundRect(rectF4, f3, f3, this.backgroundFillPaint);
                    canvas.save();
                    StaticLayout noThemeStaticLayout = getNoThemeStaticLayout();
                    canvas.translate(((float) (getWidth() - noThemeStaticLayout.getWidth())) * 0.5f, (float) AndroidUtilities.dp(18.0f));
                    noThemeStaticLayout.draw(canvas);
                    canvas.restore();
                } else {
                    if (chatThemeItem3.previewDrawable != null) {
                        canvas.save();
                        canvas.clipPath(this.clipPath);
                        this.chatThemeItem.previewDrawable.setBounds(0, 0, getWidth(), getHeight());
                        this.chatThemeItem.previewDrawable.draw(canvas);
                        canvas.restore();
                    } else {
                        RectF rectF5 = this.rectF;
                        float f4 = this.INNER_RADIUS;
                        canvas.drawRoundRect(rectF5, f4, f4, this.backgroundFillPaint);
                    }
                    float dp = this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(8.0f));
                    float dp2 = this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(22.0f));
                    this.rectF.set(dp2, dp, this.BUBBLE_WIDTH + dp2, this.BUBBLE_HEIGHT + dp);
                    RectF rectF6 = this.rectF;
                    canvas.drawRoundRect(rectF6, rectF6.height() * 0.5f, this.rectF.height() * 0.5f, this.outBubblePaintFirst);
                    RectF rectF7 = this.rectF;
                    canvas.drawRoundRect(rectF7, rectF7.height() * 0.5f, this.rectF.height() * 0.5f, this.outBubblePaintSecond);
                    float dp3 = this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(5.0f));
                    float dp4 = dp + this.BUBBLE_HEIGHT + ((float) AndroidUtilities.dp(4.0f));
                    this.rectF.set(dp3, dp4, this.BUBBLE_WIDTH + dp3, this.BUBBLE_HEIGHT + dp4);
                    RectF rectF8 = this.rectF;
                    canvas.drawRoundRect(rectF8, rectF8.height() * 0.5f, this.rectF.height() * 0.5f, this.inBubblePaint);
                }
                super.dispatchDraw(canvas);
            }

            public void setItem(ChatThemeItem chatThemeItem2) {
                boolean z = true;
                boolean z2 = this.chatThemeItem != chatThemeItem2;
                boolean z3 = this.isDark;
                boolean z4 = chatThemeItem2.isDark;
                if (z3 == z4) {
                    z = false;
                }
                this.isDark = z4;
                this.chatThemeItem = chatThemeItem2;
                TLRPC$Document emojiAnimatedSticker = chatThemeItem2.chatTheme.getEmoticon() != null ? MediaDataController.getInstance(ChatThemeBottomSheet.this.currentAccount).getEmojiAnimatedSticker(chatThemeItem2.chatTheme.getEmoticon()) : null;
                if (z2) {
                    Runnable runnable = this.animationCancelRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                        this.animationCancelRunnable = null;
                    }
                    this.backupImageView.animate().cancel();
                    this.backupImageView.setScaleX(1.0f);
                    this.backupImageView.setScaleY(1.0f);
                }
                BackupImageView backupImageView2 = this.backupImageView;
                ImageLocation forDocument = ImageLocation.getForDocument(emojiAnimatedSticker);
                ChatTheme chatTheme = chatThemeItem2.chatTheme;
                backupImageView2.setImage(forDocument, "50_50", (Drawable) Emoji.getEmojiDrawable(chatTheme == null ? "❌" : chatTheme.getEmoticon()), (Object) null);
                ChatTheme chatTheme2 = chatThemeItem2.chatTheme;
                if (chatTheme2 != null && !chatTheme2.isDefault) {
                    updatePreviewBackground();
                    if (z2 || z) {
                        chatThemeItem2.chatTheme.loadWallpaperThumb(this.isDark, new ChatThemeBottomSheet$Adapter$ChatThemeView$$ExternalSyntheticLambda1(this, chatThemeItem2.chatTheme.getTlTheme(this.isDark).id, chatThemeItem2.chatTheme.getWallpaper(this.isDark).settings.intensity));
                    }
                }
                setBackgroundColor(0);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$setItem$0(long j, int i, Pair pair) {
                MotionBackgroundDrawable previewDrawable;
                if (pair != null && ((Long) pair.first).longValue() == j && (previewDrawable = getPreviewDrawable()) != null) {
                    previewDrawable.setPatternBitmap(i >= 0 ? 100 : -100, (Bitmap) pair.second);
                    previewDrawable.setPatternColorFilter(previewDrawable.getPatternColor());
                }
            }

            public void setSelected(boolean z) {
                super.setSelected(z);
                if (this.chatThemeItem.isSelected != z) {
                    ValueAnimator valueAnimator = this.strokeAlphaAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.cancel();
                    }
                    int i = 0;
                    if (z) {
                        this.strokePaint.setAlpha(0);
                    }
                    int[] iArr = new int[2];
                    iArr[0] = z ? 0 : 255;
                    if (z) {
                        i = 255;
                    }
                    iArr[1] = i;
                    ValueAnimator ofInt = ValueAnimator.ofInt(iArr);
                    this.strokeAlphaAnimator = ofInt;
                    ofInt.addUpdateListener(this);
                    this.strokeAlphaAnimator.addListener(this);
                    this.strokeAlphaAnimator.setDuration(350);
                    this.strokeAlphaAnimator.start();
                }
                this.chatThemeItem.isSelected = z;
            }

            public void setBackgroundColor(int i) {
                this.backgroundFillPaint.setColor(getThemedColor("dialogBackgroundGray"));
                TextPaint textPaint = this.noThemeTextPaint;
                if (textPaint != null) {
                    textPaint.setColor(getThemedColor("chat_emojiPanelTrendingDescription"));
                }
                invalidate();
            }

            private void fillOutBubblePaint(Paint paint, List<Integer> list) {
                if (list.size() > 1) {
                    int[] iArr = new int[list.size()];
                    for (int i = 0; i != list.size(); i++) {
                        iArr[i] = list.get(i).intValue();
                    }
                    float dp = this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(8.0f));
                    paint.setShader(new LinearGradient(0.0f, dp, 0.0f, dp + this.BUBBLE_HEIGHT, iArr, (float[]) null, Shader.TileMode.CLAMP));
                    return;
                }
                paint.setShader((Shader) null);
            }

            public void updatePreviewBackground() {
                ChatTheme chatTheme;
                ChatThemeItem chatThemeItem2 = this.chatThemeItem;
                if (chatThemeItem2 != null && (chatTheme = chatThemeItem2.chatTheme) != null && !chatTheme.isDefault) {
                    this.inBubblePaint.setColor(chatThemeItem2.isDark ? chatThemeItem2.inBubbleColorDark : chatThemeItem2.inBubbleColorLight);
                    ChatThemeItem chatThemeItem3 = this.chatThemeItem;
                    this.outBubblePaintSecond.setColor(chatThemeItem3.isDark ? chatThemeItem3.outBubbleColorDark : chatThemeItem3.outBubbleColorLight);
                    ChatThemeItem chatThemeItem4 = this.chatThemeItem;
                    fillOutBubblePaint(this.outBubblePaintFirst, chatThemeItem4.chatTheme.getTlTheme(!chatThemeItem4.isDark).settings.message_colors);
                    ChatThemeItem chatThemeItem5 = this.chatThemeItem;
                    TLRPC$TL_theme tlTheme = chatThemeItem5.chatTheme.getTlTheme(chatThemeItem5.isDark);
                    fillOutBubblePaint(this.outBubblePaintSecond, tlTheme.settings.message_colors);
                    this.outBubblePaintSecond.setAlpha(255);
                    MotionBackgroundDrawable previewDrawable = getPreviewDrawable();
                    if (previewDrawable != null) {
                        TLRPC$WallPaperSettings tLRPC$WallPaperSettings = tlTheme.settings.wallpaper.settings;
                        int i = tLRPC$WallPaperSettings.background_color | -16777216;
                        if (i == -16777216) {
                            i = 0;
                        }
                        int i2 = tLRPC$WallPaperSettings.second_background_color | -16777216;
                        if (i2 == -16777216) {
                            i2 = 0;
                        }
                        int i3 = tLRPC$WallPaperSettings.third_background_color | -16777216;
                        if (i3 == -16777216) {
                            i3 = 0;
                        }
                        int i4 = tLRPC$WallPaperSettings.fourth_background_color | -16777216;
                        if (i4 == -16777216) {
                            i4 = 0;
                        }
                        previewDrawable.setPatternBitmap(tLRPC$WallPaperSettings.intensity >= 0 ? 100 : -100);
                        previewDrawable.setColors(i, i2, i3, i4, false);
                        previewDrawable.setPatternColorFilter(previewDrawable.getPatternColor());
                    }
                    invalidate();
                }
            }

            private MotionBackgroundDrawable getPreviewDrawable() {
                ChatThemeItem chatThemeItem2 = this.chatThemeItem;
                if (chatThemeItem2 == null) {
                    return null;
                }
                MotionBackgroundDrawable motionBackgroundDrawable = chatThemeItem2.previewDrawable;
                if (motionBackgroundDrawable != null) {
                    return motionBackgroundDrawable;
                }
                MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable();
                this.chatThemeItem.previewDrawable = motionBackgroundDrawable2;
                return motionBackgroundDrawable2;
            }

            private StaticLayout getNoThemeStaticLayout() {
                StaticLayout staticLayout = this.textLayout;
                if (staticLayout != null) {
                    return staticLayout;
                }
                TextPaint textPaint = new TextPaint(129);
                this.noThemeTextPaint = textPaint;
                textPaint.setColor(getThemedColor("chat_emojiPanelTrendingDescription"));
                this.noThemeTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
                this.noThemeTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                StaticLayout createStaticLayout2 = StaticLayoutEx.createStaticLayout2(LocaleController.getString("NoTheme", NUM), this.noThemeTextPaint, AndroidUtilities.dp(52.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true, TextUtils.TruncateAt.END, AndroidUtilities.dp(52.0f), 3);
                this.textLayout = createStaticLayout2;
                return createStaticLayout2;
            }

            private int getThemedColor(String str) {
                Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
                Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
                return color != null ? color.intValue() : Theme.getColor(str);
            }

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int i;
                ChatThemeItem chatThemeItem2 = this.chatThemeItem;
                if (chatThemeItem2.chatTheme.isDefault) {
                    i = getThemedColor("featuredStickers_addButton");
                } else {
                    i = chatThemeItem2.isDark ? chatThemeItem2.strokeColorDark : chatThemeItem2.strokeColorLight;
                }
                this.strokePaint.setColor(i);
                this.strokePaint.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
                invalidate();
            }

            public void onAnimationEnd(Animator animator) {
                this.strokeAlphaAnimator = null;
                invalidate();
            }

            public void onAnimationCancel(Animator animator) {
                this.strokeAlphaAnimator = null;
                invalidate();
            }

            public void playEmojiAnimation() {
                if (this.backupImageView.getImageReceiver().getLottieAnimation() != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.animationCancelRunnable);
                    this.backupImageView.setVisibility(0);
                    this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    this.backupImageView.getImageReceiver().getLottieAnimation().start();
                    this.backupImageView.setPivotY((float) AndroidUtilities.dp(24.0f));
                    this.backupImageView.setPivotX((float) AndroidUtilities.dp(12.0f));
                    this.backupImageView.animate().scaleX(2.0f).scaleY(2.0f).setDuration(300).setInterpolator(AndroidUtilities.overshootInterpolator).start();
                    ChatThemeBottomSheet$Adapter$ChatThemeView$$ExternalSyntheticLambda0 chatThemeBottomSheet$Adapter$ChatThemeView$$ExternalSyntheticLambda0 = new ChatThemeBottomSheet$Adapter$ChatThemeView$$ExternalSyntheticLambda0(this);
                    this.animationCancelRunnable = chatThemeBottomSheet$Adapter$ChatThemeView$$ExternalSyntheticLambda0;
                    AndroidUtilities.runOnUIThread(chatThemeBottomSheet$Adapter$ChatThemeView$$ExternalSyntheticLambda0, 2500);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$playEmojiAnimation$1() {
                this.animationCancelRunnable = null;
                this.backupImageView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }

            public void cancelAnimation() {
                Runnable runnable = this.animationCancelRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.animationCancelRunnable.run();
                }
            }
        }
    }

    private static class ChatThemeItem {
        public float animationProgress;
        public final ChatTheme chatTheme;
        public int inBubbleColorDark;
        public int inBubbleColorLight;
        public boolean isDark;
        public boolean isSelected;
        public int outBubbleColorDark;
        public int outBubbleColorLight;
        public MotionBackgroundDrawable previewDrawable;
        public int strokeColorDark;
        public int strokeColorLight;

        public ChatThemeItem(ChatTheme chatTheme2) {
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

package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ThemeSmallPreviewView;

public class DefaultThemesPreviewCell extends LinearLayout {
    private final ChatThemeBottomSheet.Adapter adapter;
    TextCell browseThemesCell;
    int currentType;
    RLottieDrawable darkThemeDrawable;
    TextCell dayNightCell;
    private LinearLayoutManager layoutManager = null;
    /* access modifiers changed from: private */
    public ValueAnimator navBarAnimator;
    /* access modifiers changed from: private */
    public int navBarColor;
    BaseFragment parentFragment;
    private final FlickerLoadingView progressView;
    private final RecyclerListView recyclerView;
    private int selectedPosition = -1;
    int themeIndex;
    private Boolean wasPortrait = null;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DefaultThemesPreviewCell(Context context, BaseFragment parentFragment2, int type) {
        super(context);
        LinearLayoutManager linearLayoutManager;
        final Context context2 = context;
        BaseFragment baseFragment = parentFragment2;
        this.currentType = type;
        this.parentFragment = baseFragment;
        setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
        int i = 0;
        ChatThemeBottomSheet.Adapter adapter2 = new ChatThemeBottomSheet.Adapter(parentFragment2.getCurrentAccount(), (Theme.ResourcesProvider) null, this.currentType == 0 ? 0 : 1);
        this.adapter = adapter2;
        RecyclerListView recyclerListView = new RecyclerListView(getContext());
        this.recyclerView = recyclerListView;
        recyclerListView.setAdapter(adapter2);
        recyclerListView.setSelectorDrawableColor(0);
        recyclerListView.setClipChildren(false);
        recyclerListView.setClipToPadding(false);
        recyclerListView.setHasFixedSize(true);
        recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        recyclerListView.setNestedScrollingEnabled(false);
        updateLayoutManager();
        recyclerListView.setFocusable(false);
        recyclerListView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new DefaultThemesPreviewCell$$ExternalSyntheticLambda1(this, baseFragment));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(getContext(), (Theme.ResourcesProvider) null);
        this.progressView = flickerLoadingView;
        flickerLoadingView.setViewType(14);
        flickerLoadingView.setVisibility(0);
        if (this.currentType == 0) {
            frameLayout.addView(flickerLoadingView, LayoutHelper.createFrame(-1, 104.0f, 8388611, 0.0f, 8.0f, 0.0f, 8.0f));
            frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, 104.0f, 8388611, 0.0f, 8.0f, 0.0f, 8.0f));
        } else {
            frameLayout.addView(flickerLoadingView, LayoutHelper.createFrame(-1, 104.0f, 8388611, 0.0f, 8.0f, 0.0f, 8.0f));
            frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, -2.0f, 8388611, 0.0f, 8.0f, 0.0f, 8.0f));
        }
        recyclerListView.setEmptyView(flickerLoadingView);
        recyclerListView.setAnimateEmptyView(true, 0);
        if (this.currentType == 0) {
            RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
            this.darkThemeDrawable = rLottieDrawable;
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.darkThemeDrawable.beginApplyLayerColors();
            this.darkThemeDrawable.commitApplyLayerColors();
            TextCell textCell = new TextCell(context2);
            this.dayNightCell = textCell;
            textCell.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            this.dayNightCell.imageLeft = 21;
            addView(this.dayNightCell, LayoutHelper.createFrame(-1, -2.0f));
            TextCell textCell2 = new TextCell(context2);
            this.browseThemesCell = textCell2;
            textCell2.setTextAndIcon(LocaleController.getString("SettingsBrowseThemes", NUM), NUM, false);
            addView(this.browseThemesCell, LayoutHelper.createFrame(-1, -2.0f));
            this.dayNightCell.setOnClickListener(new View.OnClickListener() {
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: java.lang.Object[]} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onClick(android.view.View r26) {
                    /*
                        r25 = this;
                        r6 = r25
                        boolean r0 = org.telegram.ui.Cells.DrawerProfileCell.switchingTheme
                        if (r0 == 0) goto L_0x0007
                        return
                    L_0x0007:
                        java.lang.String r0 = "windowBackgroundWhiteBlueText4"
                        int r7 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                        java.lang.String r1 = "windowBackgroundGray"
                        int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                        r8 = 1
                        org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r8
                        android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
                        java.lang.String r4 = "themeconfig"
                        r5 = 0
                        android.content.SharedPreferences r9 = r3.getSharedPreferences(r4, r5)
                        java.lang.String r3 = "lastDayTheme"
                        java.lang.String r4 = "Blue"
                        java.lang.String r3 = r9.getString(r3, r4)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = org.telegram.ui.ActionBar.Theme.getTheme(r3)
                        if (r4 == 0) goto L_0x0037
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = org.telegram.ui.ActionBar.Theme.getTheme(r3)
                        boolean r4 = r4.isDark()
                        if (r4 == 0) goto L_0x0039
                    L_0x0037:
                        java.lang.String r3 = "Blue"
                    L_0x0039:
                        java.lang.String r4 = "lastDarkTheme"
                        java.lang.String r10 = "Dark Blue"
                        java.lang.String r4 = r9.getString(r4, r10)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
                        if (r11 == 0) goto L_0x0051
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
                        boolean r11 = r11.isDark()
                        if (r11 != 0) goto L_0x0053
                    L_0x0051:
                        java.lang.String r4 = "Dark Blue"
                    L_0x0053:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
                        boolean r12 = r3.equals(r4)
                        if (r12 == 0) goto L_0x007c
                        boolean r12 = r11.isDark()
                        if (r12 != 0) goto L_0x0077
                        boolean r10 = r3.equals(r10)
                        if (r10 != 0) goto L_0x0077
                        java.lang.String r10 = "Night"
                        boolean r10 = r3.equals(r10)
                        if (r10 == 0) goto L_0x0072
                        goto L_0x0077
                    L_0x0072:
                        java.lang.String r4 = "Dark Blue"
                        r10 = r3
                        r12 = r4
                        goto L_0x007e
                    L_0x0077:
                        java.lang.String r3 = "Blue"
                        r10 = r3
                        r12 = r4
                        goto L_0x007e
                    L_0x007c:
                        r10 = r3
                        r12 = r4
                    L_0x007e:
                        boolean r3 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDark()
                        r3 = r3 ^ r8
                        r13 = r3
                        if (r3 == 0) goto L_0x008c
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r12)
                        r11 = r3
                        goto L_0x0091
                    L_0x008c:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r10)
                        r11 = r3
                    L_0x0091:
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r3 = r3.darkThemeDrawable
                        if (r13 == 0) goto L_0x00a1
                        org.telegram.ui.DefaultThemesPreviewCell r4 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r4 = r4.darkThemeDrawable
                        int r4 = r4.getFramesCount()
                        int r4 = r4 - r8
                        goto L_0x00a2
                    L_0x00a1:
                        r4 = 0
                    L_0x00a2:
                        r3.setCustomEndFrame(r4)
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r3 = r3.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r3 = r3.getImageView()
                        r3.playAnimation()
                        r4 = 2
                        int[] r14 = new int[r4]
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r3 = r3.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r3 = r3.getImageView()
                        r3.getLocationInWindow(r14)
                        r3 = r14[r5]
                        org.telegram.ui.DefaultThemesPreviewCell r15 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r15 = r15.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r15 = r15.getImageView()
                        int r15 = r15.getMeasuredWidth()
                        int r15 = r15 / r4
                        int r3 = r3 + r15
                        r14[r5] = r3
                        r3 = r14[r8]
                        org.telegram.ui.DefaultThemesPreviewCell r15 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r15 = r15.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r15 = r15.getImageView()
                        int r15 = r15.getMeasuredHeight()
                        int r15 = r15 / r4
                        r16 = 1077936128(0x40400000, float:3.0)
                        int r16 = org.telegram.messenger.AndroidUtilities.dp(r16)
                        int r15 = r15 + r16
                        int r3 = r3 + r15
                        r14[r8] = r3
                        org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                        int r15 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
                        r4 = 7
                        java.lang.Object[] r4 = new java.lang.Object[r4]
                        r4[r5] = r11
                        java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
                        r4[r8] = r5
                        r5 = 2
                        r4[r5] = r14
                        r5 = 3
                        r17 = -1
                        java.lang.Integer r17 = java.lang.Integer.valueOf(r17)
                        r4[r5] = r17
                        r5 = 4
                        java.lang.Boolean r17 = java.lang.Boolean.valueOf(r13)
                        r4[r5] = r17
                        r5 = 5
                        org.telegram.ui.DefaultThemesPreviewCell r8 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r8 = r8.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r8 = r8.getImageView()
                        r4[r5] = r8
                        r5 = 6
                        org.telegram.ui.DefaultThemesPreviewCell r8 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r8 = r8.dayNightCell
                        r4[r5] = r8
                        r3.postNotificationName(r15, r4)
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r3.updateDayNightMode()
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r3.updateSelectedPosition()
                        int r8 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r0 = r0.darkThemeDrawable
                        android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
                        android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.SRC_IN
                        r3.<init>(r8, r4)
                        r0.setColorFilter(r3)
                        r0 = 2
                        float[] r3 = new float[r0]
                        r3 = {0, NUM} // fill-array
                        android.animation.ValueAnimator r15 = android.animation.ValueAnimator.ofFloat(r3)
                        org.telegram.ui.DefaultThemesPreviewCell$1$1 r0 = new org.telegram.ui.DefaultThemesPreviewCell$1$1
                        r0.<init>(r7, r8)
                        r15.addUpdateListener(r0)
                        org.telegram.ui.DefaultThemesPreviewCell$1$2 r0 = new org.telegram.ui.DefaultThemesPreviewCell$1$2
                        r0.<init>(r8)
                        r15.addListener(r0)
                        r4 = 350(0x15e, double:1.73E-321)
                        r15.setDuration(r4)
                        r15.start()
                        int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                        android.content.Context r0 = r1
                        boolean r3 = r0 instanceof android.app.Activity
                        if (r3 == 0) goto L_0x0172
                        android.app.Activity r0 = (android.app.Activity) r0
                        android.view.Window r0 = r0.getWindow()
                        goto L_0x0173
                    L_0x0172:
                        r0 = 0
                    L_0x0173:
                        if (r0 == 0) goto L_0x0201
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r3 = r3.navBarAnimator
                        if (r3 == 0) goto L_0x019b
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r3 = r3.navBarAnimator
                        boolean r3 = r3.isRunning()
                        if (r3 == 0) goto L_0x019b
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        int r2 = r3.navBarColor
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r3 = r3.navBarAnimator
                        r3.cancel()
                        r18 = r2
                        goto L_0x019d
                    L_0x019b:
                        r18 = r2
                    L_0x019d:
                        r3 = r18
                        org.telegram.ui.DefaultThemesPreviewCell r2 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r4 = 2
                        float[] r4 = new float[r4]
                        r4 = {0, NUM} // fill-array
                        android.animation.ValueAnimator r4 = android.animation.ValueAnimator.ofFloat(r4)
                        android.animation.ValueAnimator unused = r2.navBarAnimator = r4
                        if (r13 == 0) goto L_0x01b3
                        r2 = 1112014848(0x42480000, float:50.0)
                        goto L_0x01b5
                    L_0x01b3:
                        r2 = 1128792064(0x43480000, float:200.0)
                    L_0x01b5:
                        r16 = 1125515264(0x43160000, float:150.0)
                        r21 = 1135542272(0x43avar_, float:350.0)
                        org.telegram.ui.DefaultThemesPreviewCell r4 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r5 = r4.navBarAnimator
                        org.telegram.ui.DefaultThemesPreviewCell$1$3 r4 = new org.telegram.ui.DefaultThemesPreviewCell$1$3
                        r22 = r0
                        r0 = r4
                        r23 = r1
                        r1 = r25
                        r24 = r7
                        r20 = r8
                        r19 = r9
                        r8 = 350(0x15e, double:1.73E-321)
                        r7 = r4
                        r4 = r23
                        r8 = r5
                        r5 = r22
                        r0.<init>(r2, r3, r4, r5)
                        r8.addUpdateListener(r7)
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r0 = r0.navBarAnimator
                        org.telegram.ui.DefaultThemesPreviewCell$1$4 r1 = new org.telegram.ui.DefaultThemesPreviewCell$1$4
                        r1.<init>(r5, r4)
                        r0.addListener(r1)
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r0 = r0.navBarAnimator
                        r7 = 350(0x15e, double:1.73E-321)
                        r0.setDuration(r7)
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r0 = r0.navBarAnimator
                        r0.start()
                        r2 = r18
                        goto L_0x0209
                    L_0x0201:
                        r5 = r0
                        r4 = r1
                        r24 = r7
                        r20 = r8
                        r19 = r9
                    L_0x0209:
                        boolean r0 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDay()
                        if (r0 == 0) goto L_0x0225
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r0 = r0.dayNightCell
                        r1 = 2131628269(0x7f0e10ed, float:1.8883826E38)
                        java.lang.String r3 = "SettingsSwitchToNightMode"
                        java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r3 = r3.darkThemeDrawable
                        r7 = 1
                        r0.setTextAndIcon((java.lang.String) r1, (android.graphics.drawable.Drawable) r3, (boolean) r7)
                        goto L_0x023a
                    L_0x0225:
                        r7 = 1
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r0 = r0.dayNightCell
                        r1 = 2131628268(0x7f0e10ec, float:1.8883824E38)
                        java.lang.String r3 = "SettingsSwitchToDayMode"
                        java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r3 = r3.darkThemeDrawable
                        r0.setTextAndIcon((java.lang.String) r1, (android.graphics.drawable.Drawable) r3, (boolean) r7)
                    L_0x023a:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DefaultThemesPreviewCell.AnonymousClass1.onClick(android.view.View):void");
                }
            });
            this.darkThemeDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.browseThemesCell.setOnClickListener(new DefaultThemesPreviewCell$$ExternalSyntheticLambda0(baseFragment));
            if (!Theme.isCurrentThemeDay()) {
                RLottieDrawable rLottieDrawable2 = this.darkThemeDrawable;
                rLottieDrawable2.setCurrentFrame(rLottieDrawable2.getFramesCount() - 1);
                this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToDayMode", NUM), (Drawable) this.darkThemeDrawable, true);
            } else {
                this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToNightMode", NUM), (Drawable) this.darkThemeDrawable, true);
            }
        }
        if (!MediaDataController.getInstance(parentFragment2.getCurrentAccount()).defaultEmojiThemes.isEmpty()) {
            ArrayList<ChatThemeBottomSheet.ChatThemeItem> themes = new ArrayList<>(MediaDataController.getInstance(parentFragment2.getCurrentAccount()).defaultEmojiThemes);
            if (this.currentType == 0) {
                EmojiThemes chatTheme = EmojiThemes.createPreviewCustom();
                chatTheme.loadPreviewColors(parentFragment2.getCurrentAccount());
                ChatThemeBottomSheet.ChatThemeItem item = new ChatThemeBottomSheet.ChatThemeItem(chatTheme);
                item.themeIndex = !Theme.isCurrentThemeDay() ? 2 : i;
                themes.add(item);
            }
            adapter2.setItems(themes);
        }
        updateDayNightMode();
        updateSelectedPosition();
        updateColors();
        int i2 = this.selectedPosition;
        if (i2 >= 0 && (linearLayoutManager = this.layoutManager) != null) {
            linearLayoutManager.scrollToPositionWithOffset(i2, AndroidUtilities.dp(16.0f));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-DefaultThemesPreviewCell  reason: not valid java name */
    public /* synthetic */ void m3366lambda$new$0$orgtelegramuiDefaultThemesPreviewCell(BaseFragment parentFragment2, View view, int position) {
        ChatThemeBottomSheet.ChatThemeItem chatTheme = this.adapter.items.get(position);
        Theme.ThemeInfo info = chatTheme.chatTheme.getThemeInfo(this.themeIndex);
        int accentId = -1;
        if (chatTheme.chatTheme.getEmoticon().equals("🏠") || chatTheme.chatTheme.getEmoticon().equals("🎨")) {
            accentId = chatTheme.chatTheme.getAccentId(this.themeIndex);
        }
        if (info == null) {
            TLRPC.TL_theme theme = chatTheme.chatTheme.getTlTheme(this.themeIndex);
            info = Theme.getTheme(Theme.getBaseThemeKey(theme.settings.get(chatTheme.chatTheme.getSettingsIndex(this.themeIndex))));
            if (info != null) {
                Theme.ThemeAccent accent = info.accentsByThemeId.get(theme.id);
                if (accent == null) {
                    accent = info.createNewAccent(theme, parentFragment2.getCurrentAccount());
                }
                accentId = accent.id;
                info.setCurrentAccentId(accentId);
            }
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, info, false, null, Integer.valueOf(accentId));
        this.selectedPosition = position;
        int i = 0;
        while (i < this.adapter.items.size()) {
            this.adapter.items.get(i).isSelected = i == this.selectedPosition;
            i++;
        }
        this.adapter.setSelectedItem(this.selectedPosition);
        for (int i2 = 0; i2 < this.recyclerView.getChildCount(); i2++) {
            ThemeSmallPreviewView child = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i2);
            if (child != view) {
                child.cancelAnimation();
            }
        }
        ((ThemeSmallPreviewView) view).playEmojiAnimation();
        if (info != null) {
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            editor.putString((this.currentType == 1 || info.isDark()) ? "lastDarkTheme" : "lastDayTheme", info.getKey());
            editor.commit();
        }
    }

    public void updateLayoutManager() {
        boolean isPortrait = AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x;
        Boolean bool = this.wasPortrait;
        if (bool == null || bool.booleanValue() != isPortrait) {
            if (this.currentType != 0) {
                int spanCount = isPortrait ? 3 : 9;
                LinearLayoutManager linearLayoutManager = this.layoutManager;
                if (linearLayoutManager instanceof GridLayoutManager) {
                    ((GridLayoutManager) linearLayoutManager).setSpanCount(spanCount);
                } else {
                    this.recyclerView.setHasFixedSize(false);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        public int getSpanSize(int position) {
                            return 1;
                        }
                    });
                    RecyclerListView recyclerListView = this.recyclerView;
                    this.layoutManager = gridLayoutManager;
                    recyclerListView.setLayoutManager(gridLayoutManager);
                }
            } else if (this.layoutManager == null) {
                RecyclerListView recyclerListView2 = this.recyclerView;
                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), 0, false);
                this.layoutManager = linearLayoutManager2;
                recyclerListView2.setLayoutManager(linearLayoutManager2);
            }
            this.wasPortrait = Boolean.valueOf(isPortrait);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        updateLayoutManager();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void updateDayNightMode() {
        int i;
        int i2;
        int i3 = 2;
        if (this.currentType == 0) {
            if (Theme.isCurrentThemeDay()) {
                i3 = 0;
            }
            this.themeIndex = i3;
        } else if (Theme.getActiveTheme().getKey().equals("Blue")) {
            this.themeIndex = 0;
        } else if (Theme.getActiveTheme().getKey().equals("Day")) {
            this.themeIndex = 1;
        } else if (Theme.getActiveTheme().getKey().equals("Night")) {
            this.themeIndex = 2;
        } else if (Theme.getActiveTheme().getKey().equals("Dark Blue")) {
            this.themeIndex = 3;
        } else {
            if (Theme.isCurrentThemeDay() && ((i2 = this.themeIndex) == 2 || i2 == 3)) {
                this.themeIndex = 0;
            }
            if (!Theme.isCurrentThemeDay() && ((i = this.themeIndex) == 0 || i == 1)) {
                this.themeIndex = 2;
            }
        }
        if (this.adapter.items != null) {
            for (int i4 = 0; i4 < this.adapter.items.size(); i4++) {
                this.adapter.items.get(i4).themeIndex = this.themeIndex;
            }
            ChatThemeBottomSheet.Adapter adapter2 = this.adapter;
            adapter2.notifyItemRangeChanged(0, adapter2.items.size());
        }
        updateSelectedPosition();
    }

    /* access modifiers changed from: private */
    public void updateSelectedPosition() {
        if (this.adapter.items != null) {
            this.selectedPosition = -1;
            int i = 0;
            while (true) {
                if (i >= this.adapter.items.size()) {
                    break;
                }
                TLRPC.TL_theme theme = this.adapter.items.get(i).chatTheme.getTlTheme(this.themeIndex);
                Theme.ThemeInfo themeInfo = this.adapter.items.get(i).chatTheme.getThemeInfo(this.themeIndex);
                if (theme != null) {
                    if (Theme.getActiveTheme().name.equals(Theme.getBaseThemeKey(theme.settings.get(this.adapter.items.get(i).chatTheme.getSettingsIndex(this.themeIndex))))) {
                        if (Theme.getActiveTheme().accentsByThemeId != null) {
                            Theme.ThemeAccent accent = Theme.getActiveTheme().accentsByThemeId.get(theme.id);
                            if (accent != null && accent.id == Theme.getActiveTheme().currentAccentId) {
                                this.selectedPosition = i;
                                break;
                            }
                        } else {
                            this.selectedPosition = i;
                            break;
                        }
                    } else {
                        continue;
                    }
                } else if (themeInfo != null) {
                    if (Theme.getActiveTheme().name.equals(themeInfo.getKey()) && this.adapter.items.get(i).chatTheme.getAccentId(this.themeIndex) == Theme.getActiveTheme().currentAccentId) {
                        this.selectedPosition = i;
                        break;
                    }
                } else {
                    continue;
                }
                i++;
            }
            if (this.selectedPosition == -1 && this.currentType != 3) {
                this.selectedPosition = this.adapter.items.size() - 1;
            }
            int i2 = 0;
            while (i2 < this.adapter.items.size()) {
                this.adapter.items.get(i2).isSelected = i2 == this.selectedPosition;
                i2++;
            }
            this.adapter.setSelectedItem(this.selectedPosition);
        }
    }

    public void selectTheme(Theme.ThemeInfo themeInfo) {
        if (themeInfo.info == null || themeInfo.themeLoaded) {
            if (!TextUtils.isEmpty(themeInfo.assetName)) {
                Theme.PatternsLoader.createLoader(false);
            }
            if (this.currentType != 2) {
                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
                editor.putString((this.currentType == 1 || themeInfo.isDark()) ? "lastDarkTheme" : "lastDayTheme", themeInfo.getKey());
                editor.commit();
            }
            if (this.currentType == 1) {
                if (themeInfo != Theme.getCurrentNightTheme()) {
                    Theme.setCurrentNightTheme(themeInfo);
                } else {
                    return;
                }
            } else if (themeInfo != Theme.getActiveTheme()) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, null, -1);
            } else {
                return;
            }
            int childCount = getChildCount();
        }
    }

    public void updateColors() {
        if (this.currentType == 0) {
            this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlueText4"), PorterDuff.Mode.SRC_IN));
            Theme.setSelectorDrawableColor(this.dayNightCell.getBackground(), Theme.getColor("listSelectorSDK21"), true);
            this.browseThemesCell.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("windowBackgroundWhite"), Theme.getColor("listSelectorSDK21")));
            this.dayNightCell.setColors((String) null, "windowBackgroundWhiteBlueText4");
            this.browseThemesCell.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
        }
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        updateColors();
    }
}

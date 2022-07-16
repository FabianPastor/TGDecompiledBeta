package org.telegram.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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
import org.telegram.tgnet.TLRPC$TL_theme;
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

@SuppressLint({"ViewConstructor"})
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
    private final FlickerLoadingView progressView;
    private final RecyclerListView recyclerView;
    private int selectedPosition = -1;
    int themeIndex;
    private Boolean wasPortrait = null;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DefaultThemesPreviewCell(Context context, BaseFragment baseFragment, int i) {
        super(context);
        LinearLayoutManager linearLayoutManager;
        final Context context2 = context;
        BaseFragment baseFragment2 = baseFragment;
        this.currentType = i;
        setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
        int i2 = 0;
        ChatThemeBottomSheet.Adapter adapter2 = new ChatThemeBottomSheet.Adapter(baseFragment.getCurrentAccount(), (Theme.ResourcesProvider) null, this.currentType == 0 ? 0 : 1);
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
        recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new DefaultThemesPreviewCell$$ExternalSyntheticLambda1(this, baseFragment2));
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
            TextCell textCell2 = this.dayNightCell;
            textCell2.imageLeft = 21;
            addView(textCell2, LayoutHelper.createFrame(-1, -2.0f));
            TextCell textCell3 = new TextCell(context2);
            this.browseThemesCell = textCell3;
            textCell3.setTextAndIcon(LocaleController.getString("SettingsBrowseThemes", NUM), NUM, false);
            addView(this.browseThemesCell, LayoutHelper.createFrame(-1, -2.0f));
            this.dayNightCell.setOnClickListener(new View.OnClickListener() {
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: java.lang.Object[]} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* JADX WARNING: Removed duplicated region for block: B:26:0x0078  */
                /* JADX WARNING: Removed duplicated region for block: B:27:0x007d  */
                /* JADX WARNING: Removed duplicated region for block: B:30:0x0087  */
                /* JADX WARNING: Removed duplicated region for block: B:31:0x008d  */
                /* JADX WARNING: Removed duplicated region for block: B:34:0x0151  */
                /* JADX WARNING: Removed duplicated region for block: B:35:0x0158  */
                /* JADX WARNING: Removed duplicated region for block: B:38:0x015c  */
                /* JADX WARNING: Removed duplicated region for block: B:50:0x01d1  */
                /* JADX WARNING: Removed duplicated region for block: B:51:0x01e6  */
                @android.annotation.SuppressLint({"NotifyDataSetChanged"})
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onClick(android.view.View r14) {
                    /*
                        r13 = this;
                        boolean r0 = org.telegram.ui.Cells.DrawerProfileCell.switchingTheme
                        if (r0 == 0) goto L_0x0005
                        return
                    L_0x0005:
                        java.lang.String r0 = "windowBackgroundWhiteBlueText4"
                        int r1 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                        java.lang.String r2 = "windowBackgroundGray"
                        int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                        r6 = 1
                        org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r6
                        android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
                        java.lang.String r5 = "themeconfig"
                        r7 = 0
                        android.content.SharedPreferences r4 = r4.getSharedPreferences(r5, r7)
                        java.lang.String r5 = "lastDayTheme"
                        java.lang.String r8 = "Blue"
                        java.lang.String r5 = r4.getString(r5, r8)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r5)
                        if (r9 == 0) goto L_0x0035
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r5)
                        boolean r9 = r9.isDark()
                        if (r9 == 0) goto L_0x0036
                    L_0x0035:
                        r5 = r8
                    L_0x0036:
                        java.lang.String r9 = "lastDarkTheme"
                        java.lang.String r10 = "Dark Blue"
                        java.lang.String r4 = r4.getString(r9, r10)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
                        if (r9 == 0) goto L_0x004e
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
                        boolean r9 = r9.isDark()
                        if (r9 != 0) goto L_0x004f
                    L_0x004e:
                        r4 = r10
                    L_0x004f:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
                        boolean r11 = r5.equals(r4)
                        if (r11 == 0) goto L_0x006f
                        boolean r9 = r9.isDark()
                        if (r9 != 0) goto L_0x006d
                        boolean r9 = r5.equals(r10)
                        if (r9 != 0) goto L_0x006d
                        java.lang.String r9 = "Night"
                        boolean r9 = r5.equals(r9)
                        if (r9 == 0) goto L_0x0070
                    L_0x006d:
                        r10 = r4
                        goto L_0x0071
                    L_0x006f:
                        r10 = r4
                    L_0x0070:
                        r8 = r5
                    L_0x0071:
                        boolean r4 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDark()
                        r4 = r4 ^ r6
                        if (r4 == 0) goto L_0x007d
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.getTheme(r10)
                        goto L_0x0081
                    L_0x007d:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.getTheme(r8)
                    L_0x0081:
                        org.telegram.ui.DefaultThemesPreviewCell r8 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r8 = r8.darkThemeDrawable
                        if (r4 == 0) goto L_0x008d
                        int r9 = r8.getFramesCount()
                        int r9 = r9 - r6
                        goto L_0x008e
                    L_0x008d:
                        r9 = 0
                    L_0x008e:
                        r8.setCustomEndFrame(r9)
                        org.telegram.ui.DefaultThemesPreviewCell r8 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r8 = r8.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r8 = r8.getImageView()
                        r8.playAnimation()
                        r8 = 2
                        int[] r9 = new int[r8]
                        org.telegram.ui.DefaultThemesPreviewCell r10 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r10 = r10.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r10 = r10.getImageView()
                        r10.getLocationInWindow(r9)
                        r10 = r9[r7]
                        org.telegram.ui.DefaultThemesPreviewCell r11 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r11 = r11.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r11 = r11.getImageView()
                        int r11 = r11.getMeasuredWidth()
                        int r11 = r11 / r8
                        int r10 = r10 + r11
                        r9[r7] = r10
                        r10 = r9[r6]
                        org.telegram.ui.DefaultThemesPreviewCell r11 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r11 = r11.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r11 = r11.getImageView()
                        int r11 = r11.getMeasuredHeight()
                        int r11 = r11 / r8
                        r12 = 1077936128(0x40400000, float:3.0)
                        int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                        int r11 = r11 + r12
                        int r10 = r10 + r11
                        r9[r6] = r10
                        org.telegram.messenger.NotificationCenter r10 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                        int r11 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
                        r12 = 7
                        java.lang.Object[] r12 = new java.lang.Object[r12]
                        r12[r7] = r5
                        java.lang.Boolean r5 = java.lang.Boolean.FALSE
                        r12[r6] = r5
                        r12[r8] = r9
                        r5 = 3
                        r7 = -1
                        java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                        r12[r5] = r7
                        r5 = 4
                        java.lang.Boolean r7 = java.lang.Boolean.valueOf(r4)
                        r12[r5] = r7
                        r5 = 5
                        org.telegram.ui.DefaultThemesPreviewCell r7 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r7 = r7.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r7 = r7.getImageView()
                        r12[r5] = r7
                        r5 = 6
                        org.telegram.ui.DefaultThemesPreviewCell r7 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r7 = r7.dayNightCell
                        r12[r5] = r7
                        r10.postNotificationName(r11, r12)
                        org.telegram.ui.DefaultThemesPreviewCell r5 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r5.updateDayNightMode()
                        org.telegram.ui.DefaultThemesPreviewCell r5 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r5.updateSelectedPosition()
                        int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                        org.telegram.ui.DefaultThemesPreviewCell r5 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r5 = r5.darkThemeDrawable
                        android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
                        android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.SRC_IN
                        r7.<init>(r0, r9)
                        r5.setColorFilter(r7)
                        float[] r5 = new float[r8]
                        r5 = {0, NUM} // fill-array
                        android.animation.ValueAnimator r5 = android.animation.ValueAnimator.ofFloat(r5)
                        org.telegram.ui.DefaultThemesPreviewCell$1$1 r7 = new org.telegram.ui.DefaultThemesPreviewCell$1$1
                        r7.<init>(r1, r0)
                        r5.addUpdateListener(r7)
                        org.telegram.ui.DefaultThemesPreviewCell$1$2 r1 = new org.telegram.ui.DefaultThemesPreviewCell$1$2
                        r1.<init>(r0)
                        r5.addListener(r1)
                        r9 = 350(0x15e, double:1.73E-321)
                        r5.setDuration(r9)
                        r5.start()
                        int r7 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                        android.content.Context r0 = r1
                        boolean r1 = r0 instanceof android.app.Activity
                        if (r1 == 0) goto L_0x0158
                        android.app.Activity r0 = (android.app.Activity) r0
                        android.view.Window r0 = r0.getWindow()
                        goto L_0x0159
                    L_0x0158:
                        r0 = 0
                    L_0x0159:
                        r11 = r0
                        if (r11 == 0) goto L_0x01cb
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r0 = r0.navBarAnimator
                        if (r0 == 0) goto L_0x0180
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r0 = r0.navBarAnimator
                        boolean r0 = r0.isRunning()
                        if (r0 == 0) goto L_0x0180
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        int r0 = r0.navBarColor
                        org.telegram.ui.DefaultThemesPreviewCell r1 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r1 = r1.navBarAnimator
                        r1.cancel()
                        r3 = r0
                    L_0x0180:
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        float[] r1 = new float[r8]
                        r1 = {0, NUM} // fill-array
                        android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
                        android.animation.ValueAnimator unused = r0.navBarAnimator = r1
                        if (r4 == 0) goto L_0x0195
                        r0 = 1112014848(0x42480000, float:50.0)
                        r2 = 1112014848(0x42480000, float:50.0)
                        goto L_0x0199
                    L_0x0195:
                        r0 = 1128792064(0x43480000, float:200.0)
                        r2 = 1128792064(0x43480000, float:200.0)
                    L_0x0199:
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r8 = r0.navBarAnimator
                        org.telegram.ui.DefaultThemesPreviewCell$1$3 r12 = new org.telegram.ui.DefaultThemesPreviewCell$1$3
                        r0 = r12
                        r1 = r13
                        r4 = r7
                        r5 = r11
                        r0.<init>(r2, r3, r4, r5)
                        r8.addUpdateListener(r12)
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r0 = r0.navBarAnimator
                        org.telegram.ui.DefaultThemesPreviewCell$1$4 r1 = new org.telegram.ui.DefaultThemesPreviewCell$1$4
                        r1.<init>(r13, r11, r7)
                        r0.addListener(r1)
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r0 = r0.navBarAnimator
                        r0.setDuration(r9)
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        android.animation.ValueAnimator r0 = r0.navBarAnimator
                        r0.start()
                    L_0x01cb:
                        boolean r0 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDay()
                        if (r0 == 0) goto L_0x01e6
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r0 = r0.dayNightCell
                        r1 = 2131628319(0x7f0e111f, float:1.8883927E38)
                        java.lang.String r2 = "SettingsSwitchToNightMode"
                        java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                        org.telegram.ui.DefaultThemesPreviewCell r2 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r2 = r2.darkThemeDrawable
                        r0.setTextAndIcon((java.lang.String) r1, (android.graphics.drawable.Drawable) r2, (boolean) r6)
                        goto L_0x01fa
                    L_0x01e6:
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r0 = r0.dayNightCell
                        r1 = 2131628318(0x7f0e111e, float:1.8883925E38)
                        java.lang.String r2 = "SettingsSwitchToDayMode"
                        java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                        org.telegram.ui.DefaultThemesPreviewCell r2 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r2 = r2.darkThemeDrawable
                        r0.setTextAndIcon((java.lang.String) r1, (android.graphics.drawable.Drawable) r2, (boolean) r6)
                    L_0x01fa:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DefaultThemesPreviewCell.AnonymousClass1.onClick(android.view.View):void");
                }
            });
            this.darkThemeDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.browseThemesCell.setOnClickListener(new DefaultThemesPreviewCell$$ExternalSyntheticLambda0(baseFragment2));
            if (!Theme.isCurrentThemeDay()) {
                RLottieDrawable rLottieDrawable2 = this.darkThemeDrawable;
                rLottieDrawable2.setCurrentFrame(rLottieDrawable2.getFramesCount() - 1);
                this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToDayMode", NUM), (Drawable) this.darkThemeDrawable, true);
            } else {
                this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToNightMode", NUM), (Drawable) this.darkThemeDrawable, true);
            }
        }
        if (!MediaDataController.getInstance(baseFragment.getCurrentAccount()).defaultEmojiThemes.isEmpty()) {
            ArrayList arrayList = new ArrayList(MediaDataController.getInstance(baseFragment.getCurrentAccount()).defaultEmojiThemes);
            if (this.currentType == 0) {
                EmojiThemes createPreviewCustom = EmojiThemes.createPreviewCustom();
                createPreviewCustom.loadPreviewColors(baseFragment.getCurrentAccount());
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem = new ChatThemeBottomSheet.ChatThemeItem(createPreviewCustom);
                chatThemeItem.themeIndex = !Theme.isCurrentThemeDay() ? 2 : i2;
                arrayList.add(chatThemeItem);
            }
            adapter2.setItems(arrayList);
        }
        updateDayNightMode();
        updateSelectedPosition();
        updateColors();
        int i3 = this.selectedPosition;
        if (i3 >= 0 && (linearLayoutManager = this.layoutManager) != null) {
            linearLayoutManager.scrollToPositionWithOffset(i3, AndroidUtilities.dp(16.0f));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(BaseFragment baseFragment, View view, int i) {
        int i2;
        ChatThemeBottomSheet.ChatThemeItem chatThemeItem = this.adapter.items.get(i);
        Theme.ThemeInfo themeInfo = chatThemeItem.chatTheme.getThemeInfo(this.themeIndex);
        if (chatThemeItem.chatTheme.getEmoticon().equals("🏠") || chatThemeItem.chatTheme.getEmoticon().equals("🎨")) {
            i2 = chatThemeItem.chatTheme.getAccentId(this.themeIndex);
        } else {
            i2 = -1;
        }
        if (themeInfo == null) {
            TLRPC$TL_theme tlTheme = chatThemeItem.chatTheme.getTlTheme(this.themeIndex);
            Theme.ThemeInfo theme = Theme.getTheme(Theme.getBaseThemeKey(tlTheme.settings.get(chatThemeItem.chatTheme.getSettingsIndex(this.themeIndex))));
            if (theme != null) {
                Theme.ThemeAccent themeAccent = theme.accentsByThemeId.get(tlTheme.id);
                if (themeAccent == null) {
                    themeAccent = theme.createNewAccent(tlTheme, baseFragment.getCurrentAccount());
                }
                i2 = themeAccent.id;
                theme.setCurrentAccentId(i2);
            }
            themeInfo = theme;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.FALSE, null, Integer.valueOf(i2));
        this.selectedPosition = i;
        int i3 = 0;
        while (i3 < this.adapter.items.size()) {
            this.adapter.items.get(i3).isSelected = i3 == this.selectedPosition;
            i3++;
        }
        this.adapter.setSelectedItem(this.selectedPosition);
        for (int i4 = 0; i4 < this.recyclerView.getChildCount(); i4++) {
            ThemeSmallPreviewView themeSmallPreviewView = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i4);
            if (themeSmallPreviewView != view) {
                themeSmallPreviewView.cancelAnimation();
            }
        }
        ((ThemeSmallPreviewView) view).playEmojiAnimation();
        if (themeInfo != null) {
            SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            edit.putString((this.currentType == 1 || themeInfo.isDark()) ? "lastDarkTheme" : "lastDayTheme", themeInfo.getKey());
            edit.commit();
        }
    }

    public void updateLayoutManager() {
        Point point = AndroidUtilities.displaySize;
        boolean z = point.y > point.x;
        Boolean bool = this.wasPortrait;
        if (bool == null || bool.booleanValue() != z) {
            if (this.currentType != 0) {
                int i = z ? 3 : 9;
                LinearLayoutManager linearLayoutManager = this.layoutManager;
                if (linearLayoutManager instanceof GridLayoutManager) {
                    ((GridLayoutManager) linearLayoutManager).setSpanCount(i);
                } else {
                    this.recyclerView.setHasFixedSize(false);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), i);
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(this) {
                        public int getSpanSize(int i) {
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
            this.wasPortrait = Boolean.valueOf(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        updateLayoutManager();
        super.onMeasure(i, i2);
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
                TLRPC$TL_theme tlTheme = this.adapter.items.get(i).chatTheme.getTlTheme(this.themeIndex);
                Theme.ThemeInfo themeInfo = this.adapter.items.get(i).chatTheme.getThemeInfo(this.themeIndex);
                if (tlTheme != null) {
                    if (Theme.getActiveTheme().name.equals(Theme.getBaseThemeKey(tlTheme.settings.get(this.adapter.items.get(i).chatTheme.getSettingsIndex(this.themeIndex))))) {
                        if (Theme.getActiveTheme().accentsByThemeId != null) {
                            Theme.ThemeAccent themeAccent = Theme.getActiveTheme().accentsByThemeId.get(tlTheme.id);
                            if (themeAccent != null && themeAccent.id == Theme.getActiveTheme().currentAccentId) {
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

    public void updateColors() {
        if (this.currentType == 0) {
            this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlueText4"), PorterDuff.Mode.SRC_IN));
            Theme.setSelectorDrawableColor(this.dayNightCell.getBackground(), Theme.getColor("listSelectorSDK21"), true);
            this.browseThemesCell.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("windowBackgroundWhite"), Theme.getColor("listSelectorSDK21")));
            this.dayNightCell.setColors((String) null, "windowBackgroundWhiteBlueText4");
            this.browseThemesCell.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
        }
    }

    public void setBackgroundColor(int i) {
        super.setBackgroundColor(i);
        updateColors();
    }
}

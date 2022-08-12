package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
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
            int i3 = R.raw.sun_outline;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(i3, "" + i3, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
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
            textCell3.setTextAndIcon(LocaleController.getString("SettingsBrowseThemes", R.string.SettingsBrowseThemes), R.drawable.msg_colors, false);
            addView(this.browseThemesCell, LayoutHelper.createFrame(-1, -2.0f));
            this.dayNightCell.setOnClickListener(new View.OnClickListener() {
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.lang.Object[]} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* JADX WARNING: Removed duplicated region for block: B:26:0x0079  */
                /* JADX WARNING: Removed duplicated region for block: B:27:0x007e  */
                /* JADX WARNING: Removed duplicated region for block: B:30:0x0089  */
                /* JADX WARNING: Removed duplicated region for block: B:31:0x008f  */
                @android.annotation.SuppressLint({"NotifyDataSetChanged"})
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onClick(android.view.View r13) {
                    /*
                        r12 = this;
                        boolean r13 = org.telegram.ui.Cells.DrawerProfileCell.switchingTheme
                        if (r13 == 0) goto L_0x0005
                        return
                    L_0x0005:
                        java.lang.String r13 = "windowBackgroundWhiteBlueText4"
                        int r2 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                        java.lang.String r13 = "windowBackgroundGray"
                        int r4 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                        r13 = 1
                        org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r13
                        android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
                        java.lang.String r1 = "themeconfig"
                        r6 = 0
                        android.content.SharedPreferences r0 = r0.getSharedPreferences(r1, r6)
                        java.lang.String r1 = "lastDayTheme"
                        java.lang.String r3 = "Blue"
                        java.lang.String r1 = r0.getString(r1, r3)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.getTheme(r1)
                        if (r5 == 0) goto L_0x0035
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.getTheme(r1)
                        boolean r5 = r5.isDark()
                        if (r5 == 0) goto L_0x0036
                    L_0x0035:
                        r1 = r3
                    L_0x0036:
                        java.lang.String r5 = "lastDarkTheme"
                        java.lang.String r7 = "Dark Blue"
                        java.lang.String r0 = r0.getString(r5, r7)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
                        if (r5 == 0) goto L_0x004e
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
                        boolean r5 = r5.isDark()
                        if (r5 != 0) goto L_0x004f
                    L_0x004e:
                        r0 = r7
                    L_0x004f:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
                        boolean r8 = r1.equals(r0)
                        if (r8 == 0) goto L_0x006f
                        boolean r5 = r5.isDark()
                        if (r5 != 0) goto L_0x006d
                        boolean r5 = r1.equals(r7)
                        if (r5 != 0) goto L_0x006d
                        java.lang.String r5 = "Night"
                        boolean r5 = r1.equals(r5)
                        if (r5 == 0) goto L_0x0070
                    L_0x006d:
                        r7 = r0
                        goto L_0x0071
                    L_0x006f:
                        r7 = r0
                    L_0x0070:
                        r3 = r1
                    L_0x0071:
                        boolean r0 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDark()
                        r8 = r0 ^ 1
                        if (r8 == 0) goto L_0x007e
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
                        goto L_0x0082
                    L_0x007e:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r3)
                    L_0x0082:
                        r7 = r0
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r0 = r0.darkThemeDrawable
                        if (r8 == 0) goto L_0x008f
                        int r1 = r0.getFramesCount()
                        int r1 = r1 - r13
                        goto L_0x0090
                    L_0x008f:
                        r1 = 0
                    L_0x0090:
                        r0.setCustomEndFrame(r1)
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r0 = r0.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r0 = r0.getImageView()
                        r0.playAnimation()
                        r9 = 2
                        int[] r10 = new int[r9]
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r0 = r0.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r0 = r0.getImageView()
                        r0.getLocationInWindow(r10)
                        r0 = r10[r6]
                        org.telegram.ui.DefaultThemesPreviewCell r1 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r1 = r1.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r1 = r1.getImageView()
                        int r1 = r1.getMeasuredWidth()
                        int r1 = r1 / r9
                        int r0 = r0 + r1
                        r10[r6] = r0
                        r0 = r10[r13]
                        org.telegram.ui.DefaultThemesPreviewCell r1 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r1 = r1.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r1 = r1.getImageView()
                        int r1 = r1.getMeasuredHeight()
                        int r1 = r1 / r9
                        r3 = 1077936128(0x40400000, float:3.0)
                        int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                        int r1 = r1 + r3
                        int r0 = r0 + r1
                        r10[r13] = r0
                        android.content.Context r3 = r1
                        org.telegram.ui.DefaultThemesPreviewCell$1$$ExternalSyntheticLambda1 r11 = new org.telegram.ui.DefaultThemesPreviewCell$1$$ExternalSyntheticLambda1
                        r0 = r11
                        r1 = r12
                        r5 = r8
                        r0.<init>(r1, r2, r3, r4, r5)
                        org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                        int r1 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
                        r2 = 8
                        java.lang.Object[] r2 = new java.lang.Object[r2]
                        r2[r6] = r7
                        java.lang.Boolean r3 = java.lang.Boolean.FALSE
                        r2[r13] = r3
                        r2[r9] = r10
                        r13 = 3
                        r3 = -1
                        java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                        r2[r13] = r3
                        r13 = 4
                        java.lang.Boolean r3 = java.lang.Boolean.valueOf(r8)
                        r2[r13] = r3
                        r13 = 5
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r3 = r3.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r3 = r3.getImageView()
                        r2[r13] = r3
                        r13 = 6
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r3 = r3.dayNightCell
                        r2[r13] = r3
                        r13 = 7
                        r2[r13] = r11
                        r0.postNotificationName(r1, r2)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DefaultThemesPreviewCell.AnonymousClass1.onClick(android.view.View):void");
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onClick$1(int i, Context context, int i2, boolean z) {
                    AndroidUtilities.runOnUIThread(new DefaultThemesPreviewCell$1$$ExternalSyntheticLambda0(this, i, context, i2, z));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onClick$0(int i, Context context, int i2, boolean z) {
                    Context context2 = context;
                    DefaultThemesPreviewCell.this.updateDayNightMode();
                    DefaultThemesPreviewCell.this.updateSelectedPosition();
                    final int color = Theme.getColor("windowBackgroundWhiteBlueText4");
                    DefaultThemesPreviewCell.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    final int i3 = i;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            DefaultThemesPreviewCell.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(i3, color, ((Float) valueAnimator.getAnimatedValue()).floatValue()), PorterDuff.Mode.SRC_IN));
                        }
                    });
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            DefaultThemesPreviewCell.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                            super.onAnimationEnd(animator);
                        }
                    });
                    ofFloat.setDuration(350);
                    ofFloat.start();
                    final int color2 = Theme.getColor("windowBackgroundGray");
                    final Window window = context2 instanceof Activity ? ((Activity) context2).getWindow() : null;
                    if (window != null) {
                        if (DefaultThemesPreviewCell.this.navBarAnimator != null && DefaultThemesPreviewCell.this.navBarAnimator.isRunning()) {
                            DefaultThemesPreviewCell.this.navBarAnimator.cancel();
                        }
                        final int access$200 = (DefaultThemesPreviewCell.this.navBarAnimator == null || !DefaultThemesPreviewCell.this.navBarAnimator.isRunning()) ? i2 : DefaultThemesPreviewCell.this.navBarColor;
                        ValueAnimator unused = DefaultThemesPreviewCell.this.navBarAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        final float f = z ? 50.0f : 200.0f;
                        final int i4 = color2;
                        final Window window2 = window;
                        DefaultThemesPreviewCell.this.navBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(350.0f, 150.0f) {
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int unused = DefaultThemesPreviewCell.this.navBarColor = ColorUtils.blendARGB(access$200, i4, Math.max(0.0f, Math.min(1.0f, ((((Float) valueAnimator.getAnimatedValue()).floatValue() * 350.0f) - f) / 150.0f)));
                                boolean z = false;
                                AndroidUtilities.setNavigationBarColor(window2, DefaultThemesPreviewCell.this.navBarColor, false);
                                Window window = window2;
                                if (AndroidUtilities.computePerceivedBrightness(DefaultThemesPreviewCell.this.navBarColor) >= 0.721f) {
                                    z = true;
                                }
                                AndroidUtilities.setLightNavigationBar(window, z);
                            }
                        });
                        DefaultThemesPreviewCell.this.navBarAnimator.addListener(new AnimatorListenerAdapter(this) {
                            public void onAnimationEnd(Animator animator) {
                                boolean z = false;
                                AndroidUtilities.setNavigationBarColor(window, color2, false);
                                Window window = window;
                                if (AndroidUtilities.computePerceivedBrightness(color2) >= 0.721f) {
                                    z = true;
                                }
                                AndroidUtilities.setLightNavigationBar(window, z);
                            }
                        });
                        DefaultThemesPreviewCell.this.navBarAnimator.setDuration(350);
                        DefaultThemesPreviewCell.this.navBarAnimator.start();
                    }
                    if (Theme.isCurrentThemeDay()) {
                        DefaultThemesPreviewCell.this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToNightMode", R.string.SettingsSwitchToNightMode), (Drawable) DefaultThemesPreviewCell.this.darkThemeDrawable, true);
                    } else {
                        DefaultThemesPreviewCell.this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToDayMode", R.string.SettingsSwitchToDayMode), (Drawable) DefaultThemesPreviewCell.this.darkThemeDrawable, true);
                    }
                }
            });
            this.darkThemeDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.browseThemesCell.setOnClickListener(new DefaultThemesPreviewCell$$ExternalSyntheticLambda0(baseFragment2));
            if (!Theme.isCurrentThemeDay()) {
                RLottieDrawable rLottieDrawable2 = this.darkThemeDrawable;
                rLottieDrawable2.setCurrentFrame(rLottieDrawable2.getFramesCount() - 1);
                this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToDayMode", R.string.SettingsSwitchToDayMode), (Drawable) this.darkThemeDrawable, true);
            } else {
                this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToNightMode", R.string.SettingsSwitchToNightMode), (Drawable) this.darkThemeDrawable, true);
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
        int i4 = this.selectedPosition;
        if (i4 >= 0 && (linearLayoutManager = this.layoutManager) != null) {
            linearLayoutManager.scrollToPositionWithOffset(i4, AndroidUtilities.dp(16.0f));
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

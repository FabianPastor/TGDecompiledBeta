package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
    private final LinearLayoutManager layoutManager;
    private final FlickerLoadingView progressView;
    private final RecyclerListView recyclerView;
    private int selectedPosition = -1;
    int themeIndex;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DefaultThemesPreviewCell(Context context, BaseFragment baseFragment, int i) {
        super(context);
        Context context2 = context;
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
        recyclerListView.setClipChildren(false);
        recyclerListView.setClipToPadding(false);
        recyclerListView.setHasFixedSize(true);
        recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        recyclerListView.setNestedScrollingEnabled(false);
        if (this.currentType == 0) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 0, false);
            this.layoutManager = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
        } else {
            recyclerListView.setHasFixedSize(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(this) {
                public int getSpanSize(int i) {
                    return 1;
                }
            });
            this.layoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
        }
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
            textCell.imageLeft = 21;
            addView(textCell, LayoutHelper.createFrame(-1, -2.0f));
            TextCell textCell2 = new TextCell(context2);
            this.browseThemesCell = textCell2;
            textCell2.setTextAndIcon(LocaleController.getString("SettingsBrowseThemes", NUM), NUM, false);
            addView(this.browseThemesCell, LayoutHelper.createFrame(-1, -2.0f));
            this.dayNightCell.setOnClickListener(new View.OnClickListener() {
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: java.lang.Object[]} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* JADX WARNING: Removed duplicated region for block: B:26:0x0072  */
                /* JADX WARNING: Removed duplicated region for block: B:27:0x0077  */
                /* JADX WARNING: Removed duplicated region for block: B:30:0x0081  */
                /* JADX WARNING: Removed duplicated region for block: B:31:0x0087  */
                /* JADX WARNING: Removed duplicated region for block: B:34:0x0140  */
                /* JADX WARNING: Removed duplicated region for block: B:35:0x0155  */
                @android.annotation.SuppressLint({"NotifyDataSetChanged"})
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onClick(android.view.View r11) {
                    /*
                        r10 = this;
                        boolean r11 = org.telegram.ui.Cells.DrawerProfileCell.switchingTheme
                        if (r11 == 0) goto L_0x0005
                        return
                    L_0x0005:
                        java.lang.String r11 = "windowBackgroundWhiteBlueText4"
                        int r0 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                        r1 = 1
                        org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r1
                        android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
                        java.lang.String r3 = "themeconfig"
                        r4 = 0
                        android.content.SharedPreferences r2 = r2.getSharedPreferences(r3, r4)
                        java.lang.String r3 = "lastDayTheme"
                        java.lang.String r5 = "Blue"
                        java.lang.String r3 = r2.getString(r3, r5)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = org.telegram.ui.ActionBar.Theme.getTheme(r3)
                        if (r6 == 0) goto L_0x002f
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = org.telegram.ui.ActionBar.Theme.getTheme(r3)
                        boolean r6 = r6.isDark()
                        if (r6 == 0) goto L_0x0030
                    L_0x002f:
                        r3 = r5
                    L_0x0030:
                        java.lang.String r6 = "lastDarkTheme"
                        java.lang.String r7 = "Dark Blue"
                        java.lang.String r2 = r2.getString(r6, r7)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = org.telegram.ui.ActionBar.Theme.getTheme(r2)
                        if (r6 == 0) goto L_0x0048
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = org.telegram.ui.ActionBar.Theme.getTheme(r2)
                        boolean r6 = r6.isDark()
                        if (r6 != 0) goto L_0x0049
                    L_0x0048:
                        r2 = r7
                    L_0x0049:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
                        boolean r8 = r3.equals(r2)
                        if (r8 == 0) goto L_0x0069
                        boolean r6 = r6.isDark()
                        if (r6 != 0) goto L_0x0067
                        boolean r6 = r3.equals(r7)
                        if (r6 != 0) goto L_0x0067
                        java.lang.String r6 = "Night"
                        boolean r6 = r3.equals(r6)
                        if (r6 == 0) goto L_0x006a
                    L_0x0067:
                        r7 = r2
                        goto L_0x006b
                    L_0x0069:
                        r7 = r2
                    L_0x006a:
                        r5 = r3
                    L_0x006b:
                        boolean r2 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDark()
                        r2 = r2 ^ r1
                        if (r2 == 0) goto L_0x0077
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
                        goto L_0x007b
                    L_0x0077:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r5)
                    L_0x007b:
                        org.telegram.ui.DefaultThemesPreviewCell r5 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r5 = r5.darkThemeDrawable
                        if (r2 == 0) goto L_0x0087
                        int r6 = r5.getFramesCount()
                        int r6 = r6 - r1
                        goto L_0x0088
                    L_0x0087:
                        r6 = 0
                    L_0x0088:
                        r5.setCustomEndFrame(r6)
                        org.telegram.ui.DefaultThemesPreviewCell r5 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r5 = r5.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r5 = r5.getImageView()
                        r5.playAnimation()
                        r5 = 2
                        int[] r6 = new int[r5]
                        org.telegram.ui.DefaultThemesPreviewCell r7 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r7 = r7.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r7 = r7.getImageView()
                        r7.getLocationInWindow(r6)
                        r7 = r6[r4]
                        org.telegram.ui.DefaultThemesPreviewCell r8 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r8 = r8.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r8 = r8.getImageView()
                        int r8 = r8.getMeasuredWidth()
                        int r8 = r8 / r5
                        int r7 = r7 + r8
                        r6[r4] = r7
                        r7 = r6[r1]
                        org.telegram.ui.DefaultThemesPreviewCell r8 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r8 = r8.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r8 = r8.getImageView()
                        int r8 = r8.getMeasuredHeight()
                        int r8 = r8 / r5
                        r9 = 1077936128(0x40400000, float:3.0)
                        int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                        int r8 = r8 + r9
                        int r7 = r7 + r8
                        r6[r1] = r7
                        org.telegram.messenger.NotificationCenter r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                        int r8 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
                        r9 = 6
                        java.lang.Object[] r9 = new java.lang.Object[r9]
                        r9[r4] = r3
                        java.lang.Boolean r3 = java.lang.Boolean.FALSE
                        r9[r1] = r3
                        r9[r5] = r6
                        r3 = 3
                        r4 = -1
                        java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                        r9[r3] = r4
                        r3 = 4
                        java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)
                        r9[r3] = r2
                        r2 = 5
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r3 = r3.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r3 = r3.getImageView()
                        r9[r2] = r3
                        r7.postNotificationName(r8, r9)
                        org.telegram.ui.DefaultThemesPreviewCell r2 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r2.updateDayNightMode()
                        org.telegram.ui.DefaultThemesPreviewCell r2 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r2.updateSelectedPosition()
                        int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                        org.telegram.ui.DefaultThemesPreviewCell r2 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r2 = r2.darkThemeDrawable
                        android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
                        android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.SRC_IN
                        r3.<init>(r11, r4)
                        r2.setColorFilter(r3)
                        float[] r2 = new float[r5]
                        r2 = {0, NUM} // fill-array
                        android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
                        org.telegram.ui.DefaultThemesPreviewCell$2$1 r3 = new org.telegram.ui.DefaultThemesPreviewCell$2$1
                        r3.<init>(r0, r11)
                        r2.addUpdateListener(r3)
                        org.telegram.ui.DefaultThemesPreviewCell$2$2 r0 = new org.telegram.ui.DefaultThemesPreviewCell$2$2
                        r0.<init>(r11)
                        r2.addListener(r0)
                        r3 = 350(0x15e, double:1.73E-321)
                        r2.setDuration(r3)
                        r2.start()
                        boolean r11 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDay()
                        if (r11 == 0) goto L_0x0155
                        org.telegram.ui.DefaultThemesPreviewCell r11 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r11 = r11.dayNightCell
                        r0 = 2131628056(0x7f0e1018, float:1.8883394E38)
                        java.lang.String r2 = "SettingsSwitchToNightMode"
                        java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                        org.telegram.ui.DefaultThemesPreviewCell r2 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r2 = r2.darkThemeDrawable
                        r11.setTextAndIcon((java.lang.String) r0, (android.graphics.drawable.Drawable) r2, (boolean) r1)
                        goto L_0x0169
                    L_0x0155:
                        org.telegram.ui.DefaultThemesPreviewCell r11 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r11 = r11.dayNightCell
                        r0 = 2131628055(0x7f0e1017, float:1.8883392E38)
                        java.lang.String r2 = "SettingsSwitchToDayMode"
                        java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                        org.telegram.ui.DefaultThemesPreviewCell r2 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r2 = r2.darkThemeDrawable
                        r11.setTextAndIcon((java.lang.String) r0, (android.graphics.drawable.Drawable) r2, (boolean) r1)
                    L_0x0169:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DefaultThemesPreviewCell.AnonymousClass2.onClick(android.view.View):void");
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
        ArrayList<ChatThemeBottomSheet.ChatThemeItem> arrayList = Theme.defaultEmojiThemes;
        if (!arrayList.isEmpty()) {
            ArrayList arrayList2 = new ArrayList(arrayList);
            if (this.currentType == 0) {
                EmojiThemes createPreviewCustom = EmojiThemes.createPreviewCustom();
                createPreviewCustom.loadPreviewColors(baseFragment.getCurrentAccount());
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem = new ChatThemeBottomSheet.ChatThemeItem(createPreviewCustom);
                chatThemeItem.themeIndex = !Theme.isCurrentThemeDay() ? 2 : i2;
                arrayList2.add(chatThemeItem);
            }
            adapter2.setItems(arrayList2);
        }
        updateDayNightMode();
        updateSelectedPosition();
        updateColors();
        int i3 = this.selectedPosition;
        if (i3 >= 0) {
            this.layoutManager.scrollToPositionWithOffset(i3, AndroidUtilities.dp(16.0f));
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
            this.dayNightCell.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("windowBackgroundWhite"), Theme.getColor("listSelectorSDK21")));
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

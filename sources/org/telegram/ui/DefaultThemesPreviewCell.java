package org.telegram.ui;

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
    private final LinearLayoutManager layoutManager;
    BaseFragment parentFragment;
    private final FlickerLoadingView progressView;
    private final RecyclerListView recyclerView;
    private int selectedPosition = -1;
    int themeIndex;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public DefaultThemesPreviewCell(Context context, BaseFragment parentFragment2, int type) {
        super(context);
        Context context2 = context;
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
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                public int getSpanSize(int position) {
                    return 1;
                }
            });
            this.layoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
        }
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
            textCell.imageLeft = 21;
            addView(this.dayNightCell, LayoutHelper.createFrame(-1, -2.0f));
            TextCell textCell2 = new TextCell(context2);
            this.browseThemesCell = textCell2;
            textCell2.setTextAndIcon(LocaleController.getString("SettingsBrowseThemes", NUM), NUM, false);
            addView(this.browseThemesCell, LayoutHelper.createFrame(-1, -2.0f));
            this.dayNightCell.setOnClickListener(new View.OnClickListener() {
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: java.lang.Object[]} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onClick(android.view.View r17) {
                    /*
                        r16 = this;
                        r0 = r16
                        boolean r1 = org.telegram.ui.Cells.DrawerProfileCell.switchingTheme
                        if (r1 == 0) goto L_0x0007
                        return
                    L_0x0007:
                        java.lang.String r1 = "windowBackgroundWhiteBlueText4"
                        int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                        r3 = 1
                        org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r3
                        android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
                        java.lang.String r5 = "themeconfig"
                        r6 = 0
                        android.content.SharedPreferences r4 = r4.getSharedPreferences(r5, r6)
                        java.lang.String r5 = "lastDayTheme"
                        java.lang.String r7 = "Blue"
                        java.lang.String r5 = r4.getString(r5, r7)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = org.telegram.ui.ActionBar.Theme.getTheme(r5)
                        if (r7 == 0) goto L_0x0031
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = org.telegram.ui.ActionBar.Theme.getTheme(r5)
                        boolean r7 = r7.isDark()
                        if (r7 == 0) goto L_0x0033
                    L_0x0031:
                        java.lang.String r5 = "Blue"
                    L_0x0033:
                        java.lang.String r7 = "lastDarkTheme"
                        java.lang.String r8 = "Dark Blue"
                        java.lang.String r7 = r4.getString(r7, r8)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
                        if (r9 == 0) goto L_0x004b
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
                        boolean r9 = r9.isDark()
                        if (r9 != 0) goto L_0x004d
                    L_0x004b:
                        java.lang.String r7 = "Dark Blue"
                    L_0x004d:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
                        boolean r10 = r5.equals(r7)
                        if (r10 == 0) goto L_0x0071
                        boolean r10 = r9.isDark()
                        if (r10 != 0) goto L_0x006f
                        boolean r8 = r5.equals(r8)
                        if (r8 != 0) goto L_0x006f
                        java.lang.String r8 = "Night"
                        boolean r8 = r5.equals(r8)
                        if (r8 == 0) goto L_0x006c
                        goto L_0x006f
                    L_0x006c:
                        java.lang.String r7 = "Dark Blue"
                        goto L_0x0071
                    L_0x006f:
                        java.lang.String r5 = "Blue"
                    L_0x0071:
                        boolean r8 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDark()
                        r8 = r8 ^ r3
                        r10 = r8
                        if (r8 == 0) goto L_0x007e
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
                        goto L_0x0082
                    L_0x007e:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = org.telegram.ui.ActionBar.Theme.getTheme(r5)
                    L_0x0082:
                        org.telegram.ui.DefaultThemesPreviewCell r9 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r9 = r9.darkThemeDrawable
                        if (r10 == 0) goto L_0x0092
                        org.telegram.ui.DefaultThemesPreviewCell r11 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r11 = r11.darkThemeDrawable
                        int r11 = r11.getFramesCount()
                        int r11 = r11 - r3
                        goto L_0x0093
                    L_0x0092:
                        r11 = 0
                    L_0x0093:
                        r9.setCustomEndFrame(r11)
                        org.telegram.ui.DefaultThemesPreviewCell r9 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r9 = r9.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r9 = r9.getImageView()
                        r9.playAnimation()
                        r9 = 2
                        int[] r11 = new int[r9]
                        org.telegram.ui.DefaultThemesPreviewCell r12 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r12 = r12.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r12 = r12.getImageView()
                        r12.getLocationInWindow(r11)
                        r12 = r11[r6]
                        org.telegram.ui.DefaultThemesPreviewCell r13 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r13 = r13.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r13 = r13.getImageView()
                        int r13 = r13.getMeasuredWidth()
                        int r13 = r13 / r9
                        int r12 = r12 + r13
                        r11[r6] = r12
                        r12 = r11[r3]
                        org.telegram.ui.DefaultThemesPreviewCell r13 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r13 = r13.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r13 = r13.getImageView()
                        int r13 = r13.getMeasuredHeight()
                        int r13 = r13 / r9
                        r14 = 1077936128(0x40400000, float:3.0)
                        int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                        int r13 = r13 + r14
                        int r12 = r12 + r13
                        r11[r3] = r12
                        org.telegram.messenger.NotificationCenter r12 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                        int r13 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
                        r14 = 6
                        java.lang.Object[] r14 = new java.lang.Object[r14]
                        r14[r6] = r8
                        java.lang.Boolean r6 = java.lang.Boolean.valueOf(r6)
                        r14[r3] = r6
                        r14[r9] = r11
                        r6 = 3
                        r15 = -1
                        java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
                        r14[r6] = r15
                        r6 = 4
                        java.lang.Boolean r15 = java.lang.Boolean.valueOf(r10)
                        r14[r6] = r15
                        r6 = 5
                        org.telegram.ui.DefaultThemesPreviewCell r15 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r15 = r15.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r15 = r15.getImageView()
                        r14[r6] = r15
                        r12.postNotificationName(r13, r14)
                        org.telegram.ui.DefaultThemesPreviewCell r6 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r6.updateDayNightMode()
                        org.telegram.ui.DefaultThemesPreviewCell r6 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r6.updateSelectedPosition()
                        int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                        org.telegram.ui.DefaultThemesPreviewCell r6 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r6 = r6.darkThemeDrawable
                        android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
                        android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.SRC_IN
                        r12.<init>(r1, r13)
                        r6.setColorFilter(r12)
                        float[] r6 = new float[r9]
                        r6 = {0, NUM} // fill-array
                        android.animation.ValueAnimator r6 = android.animation.ValueAnimator.ofFloat(r6)
                        org.telegram.ui.DefaultThemesPreviewCell$2$1 r9 = new org.telegram.ui.DefaultThemesPreviewCell$2$1
                        r9.<init>(r2, r1)
                        r6.addUpdateListener(r9)
                        org.telegram.ui.DefaultThemesPreviewCell$2$2 r9 = new org.telegram.ui.DefaultThemesPreviewCell$2$2
                        r9.<init>(r1)
                        r6.addListener(r9)
                        r12 = 350(0x15e, double:1.73E-321)
                        r6.setDuration(r12)
                        r6.start()
                        boolean r9 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDay()
                        if (r9 == 0) goto L_0x0162
                        org.telegram.ui.DefaultThemesPreviewCell r9 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r9 = r9.dayNightCell
                        r12 = 2131627788(0x7f0e0f0c, float:1.888285E38)
                        java.lang.String r13 = "SettingsSwitchToNightMode"
                        java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                        org.telegram.ui.DefaultThemesPreviewCell r13 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r13 = r13.darkThemeDrawable
                        r9.setTextAndIcon((java.lang.String) r12, (android.graphics.drawable.Drawable) r13, (boolean) r3)
                        goto L_0x0176
                    L_0x0162:
                        org.telegram.ui.DefaultThemesPreviewCell r9 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r9 = r9.dayNightCell
                        r12 = 2131627787(0x7f0e0f0b, float:1.8882848E38)
                        java.lang.String r13 = "SettingsSwitchToDayMode"
                        java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                        org.telegram.ui.DefaultThemesPreviewCell r13 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r13 = r13.darkThemeDrawable
                        r9.setTextAndIcon((java.lang.String) r12, (android.graphics.drawable.Drawable) r13, (boolean) r3)
                    L_0x0176:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DefaultThemesPreviewCell.AnonymousClass2.onClick(android.view.View):void");
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
        if (!Theme.defaultEmojiThemes.isEmpty()) {
            ArrayList<ChatThemeBottomSheet.ChatThemeItem> themes = new ArrayList<>(Theme.defaultEmojiThemes);
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
        if (i2 >= 0) {
            this.layoutManager.scrollToPositionWithOffset(i2, AndroidUtilities.dp(16.0f));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-DefaultThemesPreviewCell  reason: not valid java name */
    public /* synthetic */ void m2816lambda$new$0$orgtelegramuiDefaultThemesPreviewCell(BaseFragment parentFragment2, View view, int position) {
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
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, info, true, null, Integer.valueOf(accentId));
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
            this.dayNightCell.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("windowBackgroundWhite"), Theme.getColor("listSelectorSDK21")));
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

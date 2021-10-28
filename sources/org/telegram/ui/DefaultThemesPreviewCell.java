package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
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
            textCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToNightMode", NUM), (Drawable) this.darkThemeDrawable, true);
            TextCell textCell2 = this.dayNightCell;
            textCell2.imageLeft = 21;
            addView(textCell2, LayoutHelper.createFrame(-1, -2.0f));
            TextCell textCell3 = new TextCell(context2);
            this.browseThemesCell = textCell3;
            textCell3.setTextAndIcon(LocaleController.getString("SettingsBrowseThemes", NUM), NUM, false);
            addView(this.browseThemesCell, LayoutHelper.createFrame(-1, -2.0f));
            this.dayNightCell.setOnClickListener(new View.OnClickListener() {
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: java.lang.Object[]} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* JADX WARNING: Removed duplicated region for block: B:23:0x0065  */
                /* JADX WARNING: Removed duplicated region for block: B:24:0x006a  */
                /* JADX WARNING: Removed duplicated region for block: B:27:0x0074  */
                /* JADX WARNING: Removed duplicated region for block: B:28:0x007a  */
                @android.annotation.SuppressLint({"NotifyDataSetChanged"})
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onClick(android.view.View r9) {
                    /*
                        r8 = this;
                        android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext
                        java.lang.String r0 = "themeconfig"
                        r1 = 0
                        android.content.SharedPreferences r9 = r9.getSharedPreferences(r0, r1)
                        java.lang.String r0 = "lastDayTheme"
                        java.lang.String r2 = "Blue"
                        java.lang.String r0 = r9.getString(r0, r2)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
                        if (r3 == 0) goto L_0x0021
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
                        boolean r3 = r3.isDark()
                        if (r3 == 0) goto L_0x0022
                    L_0x0021:
                        r0 = r2
                    L_0x0022:
                        java.lang.String r3 = "lastDarkTheme"
                        java.lang.String r4 = "Dark Blue"
                        java.lang.String r9 = r9.getString(r3, r4)
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r9)
                        if (r3 == 0) goto L_0x003a
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r9)
                        boolean r3 = r3.isDark()
                        if (r3 != 0) goto L_0x003b
                    L_0x003a:
                        r9 = r4
                    L_0x003b:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
                        boolean r5 = r0.equals(r9)
                        if (r5 == 0) goto L_0x005b
                        boolean r3 = r3.isDark()
                        if (r3 != 0) goto L_0x0059
                        boolean r3 = r0.equals(r4)
                        if (r3 != 0) goto L_0x0059
                        java.lang.String r3 = "Night"
                        boolean r3 = r0.equals(r3)
                        if (r3 == 0) goto L_0x005c
                    L_0x0059:
                        r4 = r9
                        goto L_0x005d
                    L_0x005b:
                        r4 = r9
                    L_0x005c:
                        r2 = r0
                    L_0x005d:
                        boolean r9 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDark()
                        r0 = 1
                        r9 = r9 ^ r0
                        if (r9 == 0) goto L_0x006a
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
                        goto L_0x006e
                    L_0x006a:
                        org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = org.telegram.ui.ActionBar.Theme.getTheme(r2)
                    L_0x006e:
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Components.RLottieDrawable r3 = r3.darkThemeDrawable
                        if (r9 == 0) goto L_0x007a
                        int r4 = r3.getFramesCount()
                        int r4 = r4 - r0
                        goto L_0x007b
                    L_0x007a:
                        r4 = 0
                    L_0x007b:
                        r3.setCustomEndFrame(r4)
                        org.telegram.ui.DefaultThemesPreviewCell r3 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r3 = r3.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r3 = r3.getImageView()
                        r3.playAnimation()
                        r3 = 2
                        int[] r4 = new int[r3]
                        org.telegram.ui.DefaultThemesPreviewCell r5 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r5 = r5.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r5 = r5.getImageView()
                        r5.getLocationInWindow(r4)
                        r5 = r4[r1]
                        org.telegram.ui.DefaultThemesPreviewCell r6 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r6 = r6.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r6 = r6.getImageView()
                        int r6 = r6.getMeasuredWidth()
                        int r6 = r6 / r3
                        int r5 = r5 + r6
                        r4[r1] = r5
                        r5 = r4[r0]
                        org.telegram.ui.DefaultThemesPreviewCell r6 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r6 = r6.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r6 = r6.getImageView()
                        int r6 = r6.getMeasuredHeight()
                        int r6 = r6 / r3
                        r7 = 1077936128(0x40400000, float:3.0)
                        int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                        int r6 = r6 + r7
                        int r5 = r5 + r6
                        r4[r0] = r5
                        org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                        int r6 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
                        r7 = 6
                        java.lang.Object[] r7 = new java.lang.Object[r7]
                        r7[r1] = r2
                        java.lang.Boolean r1 = java.lang.Boolean.FALSE
                        r7[r0] = r1
                        r7[r3] = r4
                        r0 = 3
                        r1 = -1
                        java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                        r7[r0] = r1
                        r0 = 4
                        java.lang.Boolean r9 = java.lang.Boolean.valueOf(r9)
                        r7[r0] = r9
                        r9 = 5
                        org.telegram.ui.DefaultThemesPreviewCell r0 = org.telegram.ui.DefaultThemesPreviewCell.this
                        org.telegram.ui.Cells.TextCell r0 = r0.dayNightCell
                        org.telegram.ui.Components.RLottieImageView r0 = r0.getImageView()
                        r7[r9] = r0
                        r5.postNotificationName(r6, r7)
                        org.telegram.ui.DefaultThemesPreviewCell r9 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r9.updateDayNightMode()
                        org.telegram.ui.DefaultThemesPreviewCell r9 = org.telegram.ui.DefaultThemesPreviewCell.this
                        r9.updateSelectedPosition()
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
            }
        }
        ArrayList<ChatThemeBottomSheet.ChatThemeItem> arrayList = Theme.defaultEmojiThemes;
        if (!arrayList.isEmpty()) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.addAll(arrayList);
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
        ChatThemeBottomSheet.ChatThemeItem chatThemeItem = this.adapter.items.get(i);
        Theme.ThemeInfo themeInfo = chatThemeItem.chatTheme.getThemeInfo(this.themeIndex);
        int accentId = chatThemeItem.chatTheme.getEmoticon().equals("🏠") ? chatThemeItem.chatTheme.getAccentId(this.themeIndex) : -1;
        if (themeInfo == null) {
            TLRPC$TL_theme tlTheme = chatThemeItem.chatTheme.getTlTheme(this.themeIndex);
            Theme.ThemeInfo theme = Theme.getTheme(Theme.getBaseThemeKey(tlTheme.settings.get(chatThemeItem.chatTheme.getSettingsIndex(this.themeIndex))));
            if (theme != null) {
                Theme.ThemeAccent themeAccent = theme.accentsByThemeId.get(tlTheme.id);
                if (themeAccent == null) {
                    themeAccent = theme.createNewAccent(tlTheme, baseFragment.getCurrentAccount());
                }
                accentId = themeAccent.id;
                theme.setCurrentAccentId(accentId);
            }
            themeInfo = theme;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.FALSE, null, Integer.valueOf(accentId));
        this.selectedPosition = i;
        this.adapter.setSelectedItem(i);
        for (int i2 = 0; i2 < this.recyclerView.getChildCount(); i2++) {
            ChatThemeBottomSheet.Adapter.ChatThemeView chatThemeView = (ChatThemeBottomSheet.Adapter.ChatThemeView) this.recyclerView.getChildAt(i2);
            if (chatThemeView != view) {
                chatThemeView.cancelAnimation();
            }
        }
        ((ChatThemeBottomSheet.Adapter.ChatThemeView) view).playEmojiAnimation();
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
        Theme.ThemeAccent themeAccent;
        if (this.adapter.items != null) {
            this.selectedPosition = -1;
            for (int i = 0; i < this.adapter.items.size(); i++) {
                TLRPC$TL_theme tlTheme = this.adapter.items.get(i).chatTheme.getTlTheme(this.themeIndex);
                if (tlTheme != null) {
                    if (Theme.getCurrentTheme().name.equals(Theme.getBaseThemeKey(tlTheme.settings.get(this.adapter.items.get(i).chatTheme.getSettingsIndex(this.themeIndex)))) && (themeAccent = Theme.getCurrentTheme().accentsByThemeId.get(tlTheme.id)) != null && themeAccent.id == Theme.getCurrentTheme().currentAccentId) {
                        this.selectedPosition = i;
                    }
                }
            }
            if (this.selectedPosition == -1) {
                this.selectedPosition = this.adapter.items.size() - 1;
            }
            this.adapter.setSelectedItem(this.selectedPosition);
        }
    }

    public void updateColors() {
        if (this.currentType == 0) {
            this.darkThemeDrawable.setLayerColor("Sunny.**", Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.darkThemeDrawable.setLayerColor("Path.**", Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.darkThemeDrawable.setLayerColor("Path 10.**", Theme.getColor("windowBackgroundWhiteBlueText4"));
            this.darkThemeDrawable.setLayerColor("Path 11.**", Theme.getColor("windowBackgroundWhiteBlueText4"));
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

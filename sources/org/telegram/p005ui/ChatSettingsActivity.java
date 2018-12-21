package org.telegram.p005ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.CheckBoxCell;
import org.telegram.p005ui.Cells.EmptyCell;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.NotificationsCheckCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextCheckCell;
import org.telegram.p005ui.Cells.TextDetailSettingsCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.NumberPicker;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;

/* renamed from: org.telegram.ui.ChatSettingsActivity */
public class ChatSettingsActivity extends BaseFragment {
    private int appearance2Row;
    private int appearanceRow;
    private int autoplayGifsRow;
    private int backgroundRow;
    private int contactsReimportRow;
    private int contactsSortRow;
    private int customTabsRow;
    private int directShareRow;
    private int emojiRow;
    private int enableAnimationsRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int nightModeRow;
    private int raiseToSpeakRow;
    private int rowCount;
    private int saveToGalleryRow;
    private int sendByEnterRow;
    private int settings2Row;
    private int settingsRow;
    private int stickersRow;
    private int stickersSection2Row;
    private int textSizeRow;
    private int themeRow;

    /* renamed from: org.telegram.ui.ChatSettingsActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChatSettingsActivity.this.lambda$checkDiscard$2$PollCreateActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatSettingsActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ChatSettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 2:
                    TextSettingsCell textCell = holder.itemView;
                    if (position == ChatSettingsActivity.this.textSizeRow) {
                        int size = MessagesController.getGlobalMainSettings().getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
                        textCell.setTextAndValue(LocaleController.getString("TextSize", R.string.TextSize), String.format("%d", new Object[]{Integer.valueOf(size)}), false);
                        return;
                    } else if (position == ChatSettingsActivity.this.contactsSortRow) {
                        String value;
                        int sort = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                        if (sort == 0) {
                            value = LocaleController.getString("Default", R.string.Default);
                        } else if (sort == 1) {
                            value = LocaleController.getString("FirstName", R.string.SortFirstName);
                        } else {
                            value = LocaleController.getString("LastName", R.string.SortLastName);
                        }
                        textCell.setTextAndValue(LocaleController.getString("SortBy", R.string.SortBy), value, true);
                        return;
                    } else if (position == ChatSettingsActivity.this.backgroundRow) {
                        textCell.setText(LocaleController.getString("ChatBackground", R.string.ChatBackground), true);
                        return;
                    } else if (position == ChatSettingsActivity.this.contactsReimportRow) {
                        textCell.setText(LocaleController.getString("ImportContacts", R.string.ImportContacts), true);
                        return;
                    } else if (position == ChatSettingsActivity.this.stickersRow) {
                        textCell.setText(LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), false);
                        return;
                    } else if (position == ChatSettingsActivity.this.emojiRow) {
                        textCell.setText(LocaleController.getString("Emoji", R.string.Emoji), true);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCheckCell textCell2 = holder.itemView;
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (position == ChatSettingsActivity.this.enableAnimationsRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("EnableAnimations", R.string.EnableAnimations), preferences.getBoolean("view_animations", true), true);
                        return;
                    } else if (position == ChatSettingsActivity.this.sendByEnterRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("SendByEnter", R.string.SendByEnter), preferences.getBoolean("send_by_enter", false), true);
                        return;
                    } else if (position == ChatSettingsActivity.this.saveToGalleryRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), SharedConfig.saveToGallery, false);
                        return;
                    } else if (position == ChatSettingsActivity.this.autoplayGifsRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("AutoplayGifs", R.string.AutoplayGifs), SharedConfig.autoplayGifs, true);
                        return;
                    } else if (position == ChatSettingsActivity.this.raiseToSpeakRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), SharedConfig.raiseToSpeak, true);
                        return;
                    } else if (position == ChatSettingsActivity.this.customTabsRow) {
                        textCell2.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", R.string.ChromeCustomTabsInfo), SharedConfig.customTabs, false, true);
                        return;
                    } else if (position == ChatSettingsActivity.this.directShareRow) {
                        textCell2.setTextAndValueAndCheck(LocaleController.getString("DirectShare", R.string.DirectShare), LocaleController.getString("DirectShareInfo", R.string.DirectShareInfo), SharedConfig.directShare, false, true);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    HeaderCell headerCell = holder.itemView;
                    if (position == ChatSettingsActivity.this.settingsRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        return;
                    } else if (position == ChatSettingsActivity.this.appearanceRow) {
                        headerCell.setText(LocaleController.getString("Appearance", R.string.Appearance));
                        return;
                    } else {
                        return;
                    }
                case 5:
                    NotificationsCheckCell checkCell = holder.itemView;
                    if (position == ChatSettingsActivity.this.nightModeRow) {
                        boolean enabled;
                        if (Theme.selectedAutoNightType != 0) {
                            enabled = true;
                        } else {
                            enabled = false;
                        }
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), enabled ? Theme.getCurrentNightThemeName() : LocaleController.getString("NotificationsOff", R.string.NotificationsOff), enabled, true);
                        return;
                    }
                    return;
                case 6:
                    TextDetailSettingsCell textCell3 = holder.itemView;
                    if (position == ChatSettingsActivity.this.themeRow) {
                        textCell3.setTextAndValue(LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("ThemeInfo", R.string.ThemeInfo), true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == ChatSettingsActivity.this.textSizeRow || position == ChatSettingsActivity.this.enableAnimationsRow || position == ChatSettingsActivity.this.backgroundRow || position == ChatSettingsActivity.this.sendByEnterRow || position == ChatSettingsActivity.this.autoplayGifsRow || position == ChatSettingsActivity.this.contactsSortRow || position == ChatSettingsActivity.this.contactsReimportRow || position == ChatSettingsActivity.this.saveToGalleryRow || position == ChatSettingsActivity.this.stickersRow || position == ChatSettingsActivity.this.raiseToSpeakRow || position == ChatSettingsActivity.this.customTabsRow || position == ChatSettingsActivity.this.directShareRow || position == ChatSettingsActivity.this.emojiRow || position == ChatSettingsActivity.this.themeRow || position == ChatSettingsActivity.this.nightModeRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new TextDetailSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == ChatSettingsActivity.this.stickersSection2Row || position == ChatSettingsActivity.this.settings2Row || position == ChatSettingsActivity.this.appearance2Row) {
                return 1;
            }
            if (position == ChatSettingsActivity.this.backgroundRow || position == ChatSettingsActivity.this.contactsReimportRow || position == ChatSettingsActivity.this.textSizeRow || position == ChatSettingsActivity.this.contactsSortRow || position == ChatSettingsActivity.this.stickersRow || position == ChatSettingsActivity.this.emojiRow) {
                return 2;
            }
            if (position == ChatSettingsActivity.this.enableAnimationsRow || position == ChatSettingsActivity.this.sendByEnterRow || position == ChatSettingsActivity.this.saveToGalleryRow || position == ChatSettingsActivity.this.autoplayGifsRow || position == ChatSettingsActivity.this.raiseToSpeakRow || position == ChatSettingsActivity.this.customTabsRow || position == ChatSettingsActivity.this.directShareRow) {
                return 3;
            }
            if (position == ChatSettingsActivity.this.appearanceRow || position == ChatSettingsActivity.this.settingsRow) {
                return 4;
            }
            if (position == ChatSettingsActivity.this.nightModeRow) {
                return 5;
            }
            return 6;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.emojiRow = -1;
        this.contactsReimportRow = -1;
        this.contactsSortRow = -1;
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.appearanceRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.nightModeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.themeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.backgroundRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.textSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.appearance2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.customTabsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.directShareRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.enableAnimationsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.raiseToSpeakRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sendByEnterRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.autoplayGifsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.saveToGalleryRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settings2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.stickersRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.stickersSection2Row = i;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("ChatSettings", R.string.ChatSettings));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnItemClickListener(new ChatSettingsActivity$$Lambda$0(this));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$4$ChatSettingsActivity(View view, int position, float x, float y) {
        Builder builder;
        if (position != this.textSizeRow) {
            SharedPreferences preferences;
            Editor editor;
            if (position == this.enableAnimationsRow) {
                preferences = MessagesController.getGlobalMainSettings();
                boolean animations = preferences.getBoolean("view_animations", true);
                editor = preferences.edit();
                editor.putBoolean("view_animations", !animations);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!animations);
                    return;
                }
                return;
            }
            if (position == this.backgroundRow) {
                presentFragment(new WallpapersActivity());
                return;
            }
            if (position == this.sendByEnterRow) {
                preferences = MessagesController.getGlobalMainSettings();
                boolean send = preferences.getBoolean("send_by_enter", false);
                editor = preferences.edit();
                editor.putBoolean("send_by_enter", !send);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!send);
                    return;
                }
                return;
            }
            if (position == this.raiseToSpeakRow) {
                SharedConfig.toogleRaiseToSpeak();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
                    return;
                }
                return;
            }
            if (position == this.autoplayGifsRow) {
                SharedConfig.toggleAutoplayGifs();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.autoplayGifs);
                    return;
                }
                return;
            }
            if (position == this.saveToGalleryRow) {
                SharedConfig.toggleSaveToGallery();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.saveToGallery);
                    return;
                }
                return;
            }
            if (position == this.customTabsRow) {
                SharedConfig.toggleCustomTabs();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
                    return;
                }
                return;
            }
            if (position == this.directShareRow) {
                SharedConfig.toggleDirectShare();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.directShare);
                    return;
                }
                return;
            }
            if (position == this.themeRow) {
                presentFragment(new ThemeActivity(0));
                return;
            }
            if (position != this.contactsReimportRow) {
                if (position != this.contactsSortRow) {
                    if (position == this.stickersRow) {
                        presentFragment(new StickersActivity(0));
                        return;
                    }
                    if (position != this.emojiRow) {
                        if (position != this.nightModeRow) {
                            return;
                        }
                        if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.m9dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.m9dp(76.0f))))) {
                            presentFragment(new ThemeActivity(1));
                            return;
                        }
                        NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                        if (Theme.selectedAutoNightType == 0) {
                            Theme.selectedAutoNightType = 2;
                            checkCell.setChecked(true);
                        } else {
                            Theme.selectedAutoNightType = 0;
                            checkCell.setChecked(false);
                        }
                        Theme.saveAutoNightThemeConfig();
                        Theme.checkAutoNightThemeConditions();
                        boolean enabled = Theme.selectedAutoNightType != 0;
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), enabled ? Theme.getCurrentNightThemeName() : LocaleController.getString("NotificationsOff", R.string.NotificationsOff), enabled, true);
                    } else if (getParentActivity() != null) {
                        boolean[] maskValues = new boolean[2];
                        BottomSheet.Builder builder2 = new BottomSheet.Builder(getParentActivity());
                        builder2.setApplyTopPadding(false);
                        builder2.setApplyBottomPadding(false);
                        LinearLayout linearLayout = new LinearLayout(getParentActivity());
                        linearLayout.setOrientation(1);
                        int a = 0;
                        while (true) {
                            if (a < (VERSION.SDK_INT >= 19 ? 2 : 1)) {
                                String name = null;
                                if (a == 0) {
                                    maskValues[a] = SharedConfig.allowBigEmoji;
                                    name = LocaleController.getString("EmojiBigSize", R.string.EmojiBigSize);
                                } else if (a == 1) {
                                    maskValues[a] = SharedConfig.useSystemEmoji;
                                    name = LocaleController.getString("EmojiUseDefault", R.string.EmojiUseDefault);
                                }
                                CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21);
                                checkBoxCell.setTag(Integer.valueOf(a));
                                checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                                checkBoxCell.setText(name, TtmlNode.ANONYMOUS_REGION_ID, maskValues[a], true);
                                checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                                checkBoxCell.setOnClickListener(new ChatSettingsActivity$$Lambda$3(maskValues));
                                a++;
                            } else {
                                BottomSheetCell cell = new BottomSheetCell(getParentActivity(), 1);
                                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                cell.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
                                cell.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                                cell.setOnClickListener(new ChatSettingsActivity$$Lambda$4(this, maskValues, position));
                                linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
                                builder2.setCustomView(linearLayout);
                                showDialog(builder2.create());
                                return;
                            }
                        }
                    }
                } else if (getParentActivity() != null) {
                    builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("SortBy", R.string.SortBy));
                    builder.setItems(new CharSequence[]{LocaleController.getString("Default", R.string.Default), LocaleController.getString("SortFirstName", R.string.SortFirstName), LocaleController.getString("SortLastName", R.string.SortLastName)}, new ChatSettingsActivity$$Lambda$2(this, position));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                }
            }
        } else if (getParentActivity() != null) {
            builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("TextSize", R.string.TextSize));
            NumberPicker numberPicker = new NumberPicker(getParentActivity());
            numberPicker.setMinValue(12);
            numberPicker.setMaxValue(30);
            numberPicker.setValue(SharedConfig.fontSize);
            builder.setView(numberPicker);
            builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new ChatSettingsActivity$$Lambda$1(this, numberPicker, position));
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$null$0$ChatSettingsActivity(NumberPicker numberPicker, int position, DialogInterface dialog, int which) {
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("fons_size", numberPicker.getValue());
        SharedConfig.fontSize = numberPicker.getValue();
        editor.commit();
        if (this.listAdapter != null) {
            this.listAdapter.notifyItemChanged(position);
        }
    }

    final /* synthetic */ void lambda$null$1$ChatSettingsActivity(int position, DialogInterface dialog, int which) {
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("sortContactsBy", which);
        editor.commit();
        if (this.listAdapter != null) {
            this.listAdapter.notifyItemChanged(position);
        }
    }

    static final /* synthetic */ void lambda$null$2$ChatSettingsActivity(boolean[] maskValues, View v) {
        CheckBoxCell cell = (CheckBoxCell) v;
        int num = ((Integer) cell.getTag()).intValue();
        maskValues[num] = !maskValues[num];
        cell.setChecked(maskValues[num], true);
    }

    final /* synthetic */ void lambda$null$3$ChatSettingsActivity(boolean[] maskValues, int position, View v) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        boolean z = maskValues[0];
        SharedConfig.allowBigEmoji = z;
        editor.putBoolean("allowBigEmoji", z);
        z = maskValues[1];
        SharedConfig.useSystemEmoji = z;
        editor.putBoolean("useSystemEmoji", z);
        editor.commit();
        if (this.listAdapter != null) {
            this.listAdapter.notifyItemChanged(position);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[25];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground);
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        return themeDescriptionArr;
    }
}

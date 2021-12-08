package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.NotificationsSettingsActivity;

public class NotificationsCustomSettingsActivity extends BaseFragment {
    private static final int search_button = 0;
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public int alertRow;
    /* access modifiers changed from: private */
    public int alertSection2Row;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int deleteAllRow;
    /* access modifiers changed from: private */
    public int deleteAllSectionRow;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public ArrayList<NotificationsSettingsActivity.NotificationException> exceptions;
    /* access modifiers changed from: private */
    public int exceptionsAddRow;
    private HashMap<Long, NotificationsSettingsActivity.NotificationException> exceptionsDict;
    /* access modifiers changed from: private */
    public int exceptionsEndRow;
    /* access modifiers changed from: private */
    public int exceptionsSection2Row;
    /* access modifiers changed from: private */
    public int exceptionsStartRow;
    /* access modifiers changed from: private */
    public int groupSection2Row;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int messageLedRow;
    /* access modifiers changed from: private */
    public int messagePopupNotificationRow;
    /* access modifiers changed from: private */
    public int messagePriorityRow;
    /* access modifiers changed from: private */
    public int messageSectionRow;
    /* access modifiers changed from: private */
    public int messageSoundRow;
    /* access modifiers changed from: private */
    public int messageVibrateRow;
    /* access modifiers changed from: private */
    public int previewRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;

    public NotificationsCustomSettingsActivity(int type, ArrayList<NotificationsSettingsActivity.NotificationException> notificationExceptions) {
        this(type, notificationExceptions, false);
    }

    public NotificationsCustomSettingsActivity(int type, ArrayList<NotificationsSettingsActivity.NotificationException> notificationExceptions, boolean load) {
        this.rowCount = 0;
        this.exceptionsDict = new HashMap<>();
        this.currentType = type;
        this.exceptions = notificationExceptions;
        int N = notificationExceptions.size();
        for (int a = 0; a < N; a++) {
            NotificationsSettingsActivity.NotificationException exception = this.exceptions.get(a);
            this.exceptionsDict.put(Long.valueOf(exception.did), exception);
        }
        if (load) {
            loadExceptions();
        }
    }

    public boolean onFragmentCreate() {
        updateRows(true);
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == -1) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsExceptions", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Notifications", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NotificationsCustomSettingsActivity.this.finishFragment();
                }
            }
        });
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList = this.exceptions;
        if (arrayList != null && !arrayList.isEmpty()) {
            this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    boolean unused = NotificationsCustomSettingsActivity.this.searching = true;
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(true);
                }

                public void onSearchCollapse() {
                    NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs((String) null);
                    boolean unused = NotificationsCustomSettingsActivity.this.searching = false;
                    boolean unused2 = NotificationsCustomSettingsActivity.this.searchWas = false;
                    NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoExceptions", NUM));
                    NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.adapter);
                    NotificationsCustomSettingsActivity.this.adapter.notifyDataSetChanged();
                    NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(true);
                    NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(false);
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(false);
                }

                public void onTextChanged(EditText editText) {
                    if (NotificationsCustomSettingsActivity.this.searchAdapter != null) {
                        String text = editText.getText().toString();
                        if (text.length() != 0) {
                            boolean unused = NotificationsCustomSettingsActivity.this.searchWas = true;
                            if (NotificationsCustomSettingsActivity.this.listView != null) {
                                NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                                NotificationsCustomSettingsActivity.this.emptyView.showProgress();
                                NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.searchAdapter);
                                NotificationsCustomSettingsActivity.this.searchAdapter.notifyDataSetChanged();
                                NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(false);
                                NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(true);
                            }
                        }
                        NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs(text);
                    }
                }
            }).setSearchFieldHint(LocaleController.getString("Search", NUM));
        }
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", NUM));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda1(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(NotificationsCustomSettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: org.telegram.ui.NotificationsSettingsActivity$NotificationException} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$createView$9$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3332x1ee9dd24(android.view.View r22, int r23, float r24, float r25) {
        /*
            r21 = this;
            r9 = r21
            r10 = r22
            r11 = r23
            r12 = 0
            android.app.Activity r0 = r21.getParentActivity()
            if (r0 != 0) goto L_0x000e
            return
        L_0x000e:
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter r1 = r9.searchAdapter
            if (r0 == r1) goto L_0x025c
            int r0 = r9.exceptionsStartRow
            if (r11 < r0) goto L_0x0022
            int r0 = r9.exceptionsEndRow
            if (r11 >= r0) goto L_0x0022
            goto L_0x025c
        L_0x0022:
            int r0 = r9.exceptionsAddRow
            r1 = 0
            r2 = 2
            r3 = 1
            if (r11 != r0) goto L_0x0060
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r4 = "onlySelect"
            r0.putBoolean(r4, r3)
            java.lang.String r3 = "checkCanWrite"
            r0.putBoolean(r3, r1)
            int r1 = r9.currentType
            java.lang.String r3 = "dialogsType"
            if (r1 != 0) goto L_0x0043
            r1 = 6
            r0.putInt(r3, r1)
            goto L_0x004e
        L_0x0043:
            if (r1 != r2) goto L_0x004a
            r1 = 5
            r0.putInt(r3, r1)
            goto L_0x004e
        L_0x004a:
            r1 = 4
            r0.putInt(r3, r1)
        L_0x004e:
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda2
            r2.<init>(r9)
            r1.setDelegate(r2)
            r9.presentFragment(r1)
            goto L_0x024f
        L_0x0060:
            int r0 = r9.deleteAllRow
            if (r11 != r0) goto L_0x00be
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r21.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131626710(0x7f0e0ad6, float:1.8880664E38)
            java.lang.String r2 = "NotificationsDeleteAllExceptionTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131626709(0x7f0e0ad5, float:1.8880662E38)
            java.lang.String r2 = "NotificationsDeleteAllExceptionAlert"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131625188(0x7f0e04e4, float:1.8877577E38)
            java.lang.String r2 = "Delete"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda0
            r2.<init>(r9)
            r0.setPositiveButton(r1, r2)
            r1 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r0.create()
            r9.showDialog(r1)
            r2 = -1
            android.view.View r2 = r1.getButton(r2)
            android.widget.TextView r2 = (android.widget.TextView) r2
            if (r2 == 0) goto L_0x00bc
            java.lang.String r3 = "dialogTextRed2"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
        L_0x00bc:
            goto L_0x024f
        L_0x00be:
            int r0 = r9.alertRow
            if (r11 != r0) goto L_0x0102
            org.telegram.messenger.NotificationsController r0 = r21.getNotificationsController()
            int r2 = r9.currentType
            boolean r12 = r0.isGlobalNotificationsEnabled((int) r2)
            r0 = r10
            org.telegram.ui.Cells.NotificationsCheckCell r0 = (org.telegram.ui.Cells.NotificationsCheckCell) r0
            org.telegram.ui.Components.RecyclerListView r2 = r9.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r8 = r2.findViewHolderForAdapterPosition(r11)
            if (r12 != 0) goto L_0x00ee
            org.telegram.messenger.NotificationsController r2 = r21.getNotificationsController()
            int r4 = r9.currentType
            r2.setGlobalNotificationsEnabled(r4, r1)
            r0.setChecked(r3)
            if (r8 == 0) goto L_0x00ea
            org.telegram.ui.NotificationsCustomSettingsActivity$ListAdapter r1 = r9.adapter
            r1.onBindViewHolder(r8, r11)
        L_0x00ea:
            r21.checkRowsEnabled()
            goto L_0x0100
        L_0x00ee:
            r2 = 0
            int r4 = r9.currentType
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r5 = r9.exceptions
            int r6 = r9.currentAccount
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda10 r7 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda10
            r7.<init>(r9, r0, r8, r11)
            r1 = r21
            org.telegram.ui.Components.AlertsCreator.showCustomNotificationsDialog(r1, r2, r4, r5, r6, r7)
        L_0x0100:
            goto L_0x024f
        L_0x0102:
            int r0 = r9.previewRow
            if (r11 != r0) goto L_0x014f
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x010d
            return
        L_0x010d:
            android.content.SharedPreferences r0 = r21.getNotificationsSettings()
            android.content.SharedPreferences$Editor r1 = r0.edit()
            int r2 = r9.currentType
            if (r2 != r3) goto L_0x0126
            java.lang.String r2 = "EnablePreviewAll"
            boolean r3 = r0.getBoolean(r2, r3)
            r4 = r3 ^ 1
            r1.putBoolean(r2, r4)
            r12 = r3
            goto L_0x0141
        L_0x0126:
            if (r2 != 0) goto L_0x0135
            java.lang.String r2 = "EnablePreviewGroup"
            boolean r3 = r0.getBoolean(r2, r3)
            r4 = r3 ^ 1
            r1.putBoolean(r2, r4)
            r12 = r3
            goto L_0x0141
        L_0x0135:
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r3 = r0.getBoolean(r2, r3)
            r4 = r3 ^ 1
            r1.putBoolean(r2, r4)
            r12 = r3
        L_0x0141:
            r1.commit()
            org.telegram.messenger.NotificationsController r2 = r21.getNotificationsController()
            int r3 = r9.currentType
            r2.updateServerNotificationsSettings((int) r3)
            goto L_0x024f
        L_0x014f:
            int r0 = r9.messageSoundRow
            if (r11 != r0) goto L_0x01c8
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x015a
            return
        L_0x015a:
            android.content.SharedPreferences r0 = r21.getNotificationsSettings()     // Catch:{ Exception -> 0x01c2 }
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x01c2 }
            java.lang.String r4 = "android.intent.action.RINGTONE_PICKER"
            r1.<init>(r4)     // Catch:{ Exception -> 0x01c2 }
            java.lang.String r4 = "android.intent.extra.ringtone.TYPE"
            r1.putExtra(r4, r2)     // Catch:{ Exception -> 0x01c2 }
            java.lang.String r4 = "android.intent.extra.ringtone.SHOW_DEFAULT"
            r1.putExtra(r4, r3)     // Catch:{ Exception -> 0x01c2 }
            java.lang.String r4 = "android.intent.extra.ringtone.SHOW_SILENT"
            r1.putExtra(r4, r3)     // Catch:{ Exception -> 0x01c2 }
            java.lang.String r4 = "android.intent.extra.ringtone.DEFAULT_URI"
            android.net.Uri r2 = android.media.RingtoneManager.getDefaultUri(r2)     // Catch:{ Exception -> 0x01c2 }
            r1.putExtra(r4, r2)     // Catch:{ Exception -> 0x01c2 }
            r2 = 0
            r4 = 0
            android.net.Uri r5 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x01c2 }
            if (r5 == 0) goto L_0x0188
            java.lang.String r6 = r5.getPath()     // Catch:{ Exception -> 0x01c2 }
            r4 = r6
        L_0x0188:
            int r6 = r9.currentType     // Catch:{ Exception -> 0x01c2 }
            if (r6 != r3) goto L_0x0193
            java.lang.String r3 = "GlobalSoundPath"
            java.lang.String r3 = r0.getString(r3, r4)     // Catch:{ Exception -> 0x01c2 }
            goto L_0x01a2
        L_0x0193:
            if (r6 != 0) goto L_0x019c
            java.lang.String r3 = "GroupSoundPath"
            java.lang.String r3 = r0.getString(r3, r4)     // Catch:{ Exception -> 0x01c2 }
            goto L_0x01a2
        L_0x019c:
            java.lang.String r3 = "ChannelSoundPath"
            java.lang.String r3 = r0.getString(r3, r4)     // Catch:{ Exception -> 0x01c2 }
        L_0x01a2:
            if (r3 == 0) goto L_0x01b9
            java.lang.String r6 = "NoSound"
            boolean r6 = r3.equals(r6)     // Catch:{ Exception -> 0x01c2 }
            if (r6 != 0) goto L_0x01b9
            boolean r6 = r3.equals(r4)     // Catch:{ Exception -> 0x01c2 }
            if (r6 == 0) goto L_0x01b4
            r2 = r5
            goto L_0x01b9
        L_0x01b4:
            android.net.Uri r6 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x01c2 }
            r2 = r6
        L_0x01b9:
            java.lang.String r6 = "android.intent.extra.ringtone.EXISTING_URI"
            r1.putExtra(r6, r2)     // Catch:{ Exception -> 0x01c2 }
            r9.startActivityForResult(r1, r11)     // Catch:{ Exception -> 0x01c2 }
            goto L_0x01c6
        L_0x01c2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01c6:
            goto L_0x024f
        L_0x01c8:
            int r0 = r9.messageLedRow
            r1 = 0
            if (r11 != r0) goto L_0x01e8
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x01d5
            return
        L_0x01d5:
            android.app.Activity r0 = r21.getParentActivity()
            int r3 = r9.currentType
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda5
            r4.<init>(r9, r11)
            android.app.Dialog r0 = org.telegram.ui.Components.AlertsCreator.createColorSelectDialog(r0, r1, r3, r4)
            r9.showDialog(r0)
            goto L_0x024f
        L_0x01e8:
            int r0 = r9.messagePopupNotificationRow
            if (r11 != r0) goto L_0x0206
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x01f3
            return
        L_0x01f3:
            android.app.Activity r0 = r21.getParentActivity()
            int r1 = r9.currentType
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda6 r2 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda6
            r2.<init>(r9, r11)
            android.app.Dialog r0 = org.telegram.ui.Components.AlertsCreator.createPopupSelectDialog(r0, r1, r2)
            r9.showDialog(r0)
            goto L_0x024f
        L_0x0206:
            int r0 = r9.messageVibrateRow
            if (r11 != r0) goto L_0x0230
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x0211
            return
        L_0x0211:
            int r0 = r9.currentType
            if (r0 != r3) goto L_0x0218
            java.lang.String r0 = "vibrate_messages"
            goto L_0x021f
        L_0x0218:
            if (r0 != 0) goto L_0x021d
            java.lang.String r0 = "vibrate_group"
            goto L_0x021f
        L_0x021d:
            java.lang.String r0 = "vibrate_channel"
        L_0x021f:
            android.app.Activity r3 = r21.getParentActivity()
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda7 r4 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda7
            r4.<init>(r9, r11)
            android.app.Dialog r1 = org.telegram.ui.Components.AlertsCreator.createVibrationSelectDialog(r3, r1, r0, r4)
            r9.showDialog(r1)
            goto L_0x024e
        L_0x0230:
            int r0 = r9.messagePriorityRow
            if (r11 != r0) goto L_0x024e
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x023b
            return
        L_0x023b:
            android.app.Activity r0 = r21.getParentActivity()
            int r3 = r9.currentType
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda8 r4 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda8
            r4.<init>(r9, r11)
            android.app.Dialog r0 = org.telegram.ui.Components.AlertsCreator.createPrioritySelectDialog(r0, r1, r3, r4)
            r9.showDialog(r0)
            goto L_0x024f
        L_0x024e:
        L_0x024f:
            boolean r0 = r10 instanceof org.telegram.ui.Cells.TextCheckCell
            if (r0 == 0) goto L_0x025b
            r0 = r10
            org.telegram.ui.Cells.TextCheckCell r0 = (org.telegram.ui.Cells.TextCheckCell) r0
            r1 = r12 ^ 1
            r0.setChecked(r1)
        L_0x025b:
            return
        L_0x025c:
            org.telegram.ui.Components.RecyclerListView r0 = r9.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter r1 = r9.searchAdapter
            if (r0 != r1) goto L_0x02ce
            java.lang.Object r0 = r1.getObject(r11)
            boolean r1 = r0 instanceof org.telegram.ui.NotificationsSettingsActivity.NotificationException
            if (r1 == 0) goto L_0x0279
            org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter r1 = r9.searchAdapter
            java.util.ArrayList r1 = r1.searchResult
            r2 = r0
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r2 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r2
            r3 = 0
            goto L_0x02ca
        L_0x0279:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.User
            if (r1 == 0) goto L_0x0283
            r1 = r0
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC.User) r1
            long r1 = r1.id
            goto L_0x028a
        L_0x0283:
            r1 = r0
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC.Chat) r1
            long r2 = r1.id
            long r2 = -r2
            r1 = r2
        L_0x028a:
            java.util.HashMap<java.lang.Long, org.telegram.ui.NotificationsSettingsActivity$NotificationException> r3 = r9.exceptionsDict
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            boolean r3 = r3.containsKey(r4)
            if (r3 == 0) goto L_0x02a4
            java.util.HashMap<java.lang.Long, org.telegram.ui.NotificationsSettingsActivity$NotificationException> r3 = r9.exceptionsDict
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            java.lang.Object r3 = r3.get(r4)
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r3 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r3
            r4 = 0
            goto L_0x02c5
        L_0x02a4:
            r3 = 1
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r4 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException
            r4.<init>()
            r4.did = r1
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.User
            if (r5 == 0) goto L_0x02b8
            r5 = r0
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC.User) r5
            long r6 = r5.id
            r4.did = r6
            goto L_0x02c0
        L_0x02b8:
            r5 = r0
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC.Chat) r5
            long r6 = r5.id
            long r6 = -r6
            r4.did = r6
        L_0x02c0:
            r20 = r4
            r4 = r3
            r3 = r20
        L_0x02c5:
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r5 = r9.exceptions
            r2 = r3
            r3 = r4
            r1 = r5
        L_0x02ca:
            r0 = r1
            r13 = r2
            r14 = r3
            goto L_0x02e7
        L_0x02ce:
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r1 = r9.exceptions
            int r0 = r9.exceptionsStartRow
            int r0 = r11 - r0
            if (r0 < 0) goto L_0x0313
            int r2 = r1.size()
            if (r0 < r2) goto L_0x02dd
            goto L_0x0313
        L_0x02dd:
            java.lang.Object r2 = r1.get(r0)
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r2 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r2
            r3 = 0
            r0 = r1
            r13 = r2
            r14 = r3
        L_0x02e7:
            if (r13 != 0) goto L_0x02ea
            return
        L_0x02ea:
            long r7 = r13.did
            r15 = -1
            r16 = 0
            int r6 = r9.currentAccount
            r17 = 0
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda11 r18 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda11
            r1 = r18
            r2 = r21
            r3 = r14
            r4 = r0
            r5 = r13
            r19 = r6
            r6 = r23
            r1.<init>(r2, r3, r4, r5, r6)
            r1 = r21
            r2 = r7
            r4 = r15
            r5 = r16
            r6 = r19
            r7 = r17
            r8 = r18
            org.telegram.ui.Components.AlertsCreator.showCustomNotificationsDialog(r1, r2, r4, r5, r6, r7, r8)
            return
        L_0x0313:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.m3332x1ee9dd24(android.view.View, int, float, float):void");
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3323x27d99f9b(boolean newException, ArrayList arrayList, NotificationsSettingsActivity.NotificationException exception, int position, int param) {
        int idx;
        if (param != 0) {
            SharedPreferences preferences = getNotificationsSettings();
            exception.hasCustom = preferences.getBoolean("custom_" + exception.did, false);
            exception.notify = preferences.getInt("notify2_" + exception.did, 0);
            if (exception.notify != 0) {
                int time = preferences.getInt("notifyuntil_" + exception.did, -1);
                if (time != -1) {
                    exception.muteUntil = time;
                }
            }
            if (newException) {
                this.exceptions.add(exception);
                this.exceptionsDict.put(Long.valueOf(exception.did), exception);
                updateRows(true);
            } else {
                this.listView.getAdapter().notifyItemChanged(position);
            }
            this.actionBar.closeSearchField();
        } else if (!newException) {
            ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2 = this.exceptions;
            if (arrayList != arrayList2 && (idx = arrayList2.indexOf(exception)) >= 0) {
                this.exceptions.remove(idx);
                this.exceptionsDict.remove(Long.valueOf(exception.did));
            }
            arrayList.remove(exception);
            if (arrayList == this.exceptions) {
                if (this.exceptionsAddRow != -1 && arrayList.isEmpty()) {
                    this.listView.getAdapter().notifyItemChanged(this.exceptionsAddRow);
                    this.listView.getAdapter().notifyItemRemoved(this.deleteAllRow);
                    this.listView.getAdapter().notifyItemRemoved(this.deleteAllSectionRow);
                }
                this.listView.getAdapter().notifyItemRemoved(position);
                updateRows(false);
                checkRowsEnabled();
            } else {
                updateRows(true);
                this.searchAdapter.notifyDataSetChanged();
            }
            this.actionBar.closeSearchField();
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3325x424var_d(DialogsActivity fragment, ArrayList dids, CharSequence message, boolean param) {
        Bundle args2 = new Bundle();
        args2.putLong("dialog_id", ((Long) dids.get(0)).longValue());
        args2.putBoolean("exception", true);
        ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(args2);
        profileNotificationsActivity.setDelegate(new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda3(this));
        presentFragment(profileNotificationsActivity, true);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3324xb514511c(NotificationsSettingsActivity.NotificationException exception) {
        this.exceptions.add(0, exception);
        updateRows(true);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3326xcvar_b41e(DialogInterface dialogInterface, int i) {
        SharedPreferences.Editor editor = getNotificationsSettings().edit();
        int N = this.exceptions.size();
        for (int a = 0; a < N; a++) {
            NotificationsSettingsActivity.NotificationException exception = this.exceptions.get(a);
            SharedPreferences.Editor remove = editor.remove("notify2_" + exception.did);
            remove.remove("custom_" + exception.did);
            getMessagesStorage().setDialogFlags(exception.did, 0);
            TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(exception.did);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
        }
        editor.commit();
        int N2 = this.exceptions.size();
        for (int a2 = 0; a2 < N2; a2++) {
            getNotificationsController().updateServerNotificationsSettings(this.exceptions.get(a2).did, false);
        }
        this.exceptions.clear();
        this.exceptionsDict.clear();
        updateRows(true);
        getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3327x5cCLASSNAMEf(NotificationsCheckCell checkCell, RecyclerView.ViewHolder holder, int position, int param) {
        int offUntil;
        int iconType;
        SharedPreferences preferences = getNotificationsSettings();
        int offUntil2 = this.currentType;
        if (offUntil2 == 1) {
            offUntil = preferences.getInt("EnableAll2", 0);
        } else if (offUntil2 == 0) {
            offUntil = preferences.getInt("EnableGroup2", 0);
        } else {
            offUntil = preferences.getInt("EnableChannel2", 0);
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        if (offUntil < currentTime) {
            iconType = 0;
        } else if (offUntil - 31536000 >= currentTime) {
            iconType = 0;
        } else {
            iconType = 2;
        }
        checkCell.setChecked(getNotificationsController().isGlobalNotificationsEnabled(this.currentType), iconType);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
        checkRowsEnabled();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3328xe9fvar_(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3329x7739c8a1(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3330x4747a22(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3331x91af2ba3(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    private void checkRowsEnabled() {
        if (this.exceptions.isEmpty()) {
            int count = this.listView.getChildCount();
            ArrayList<Animator> animators = new ArrayList<>();
            boolean enabled = getNotificationsController().isGlobalNotificationsEnabled(this.currentType);
            for (int a = 0; a < count; a++) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(this.listView.getChildAt(a));
                switch (holder.getItemViewType()) {
                    case 0:
                        HeaderCell headerCell = (HeaderCell) holder.itemView;
                        if (holder.getAdapterPosition() != this.messageSectionRow) {
                            break;
                        } else {
                            headerCell.setEnabled(enabled, animators);
                            break;
                        }
                    case 1:
                        ((TextCheckCell) holder.itemView).setEnabled(enabled, animators);
                        break;
                    case 3:
                        ((TextColorCell) holder.itemView).setEnabled(enabled, animators);
                        break;
                    case 5:
                        ((TextSettingsCell) holder.itemView).setEnabled(enabled, animators);
                        break;
                }
            }
            if (animators.isEmpty() == 0) {
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(animators);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(NotificationsCustomSettingsActivity.this.animatorSet)) {
                            AnimatorSet unused = NotificationsCustomSettingsActivity.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.setDuration(150);
                this.animatorSet.start();
            }
        }
    }

    private void loadExceptions() {
        getMessagesStorage().getStorageQueue().postRunnable(new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda4(this));
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x029d  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x02f1  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x030b A[LOOP:3: B:126:0x0309->B:127:0x030b, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0325  */
    /* renamed from: lambda$loadExceptions$11$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3335xabefe22e() {
        /*
            r25 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r9 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r10 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r11 = r0
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            r12 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r13 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6 = r0
            org.telegram.messenger.UserConfig r0 = r25.getUserConfig()
            long r4 = r0.clientUserId
            android.content.SharedPreferences r3 = r25.getNotificationsSettings()
            java.util.Map r2 = r3.getAll()
            java.util.Set r0 = r2.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x0052:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0212
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r16 = r1.getKey()
            r17 = r0
            r0 = r16
            java.lang.String r0 = (java.lang.String) r0
            r16 = r7
            java.lang.String r7 = "notify2_"
            boolean r18 = r0.startsWith(r7)
            if (r18 == 0) goto L_0x01f8
            r18 = r8
            java.lang.String r8 = ""
            java.lang.String r0 = r0.replace(r7, r8)
            java.lang.Long r7 = org.telegram.messenger.Utilities.parseLong(r0)
            long r7 = r7.longValue()
            r19 = 0
            int r21 = (r7 > r19 ? 1 : (r7 == r19 ? 0 : -1))
            if (r21 == 0) goto L_0x01ec
            int r19 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r19 == 0) goto L_0x01ec
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r19 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException
            r19.<init>()
            r20 = r19
            r21 = r4
            r4 = r20
            r4.did = r7
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r19 = r6
            java.lang.String r6 = "custom_"
            r5.append(r6)
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            r6 = 0
            boolean r5 = r3.getBoolean(r5, r6)
            r4.hasCustom = r5
            java.lang.Object r5 = r1.getValue()
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            r4.notify = r5
            int r5 = r4.notify
            if (r5 == 0) goto L_0x00e2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "notifyuntil_"
            r5.append(r6)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            java.lang.Object r5 = r2.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 == 0) goto L_0x00e2
            int r6 = r5.intValue()
            r4.muteUntil = r6
        L_0x00e2:
            boolean r5 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)
            if (r5 == 0) goto L_0x0149
            int r5 = org.telegram.messenger.DialogObject.getEncryptedChatId(r7)
            org.telegram.messenger.MessagesController r6 = r25.getMessagesController()
            r20 = r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r6.getEncryptedChat(r0)
            if (r0 != 0) goto L_0x010c
            java.lang.Integer r6 = java.lang.Integer.valueOf(r5)
            r15.add(r6)
            r12.put(r7, r4)
            r24 = r1
            r23 = r2
            r6 = r3
            goto L_0x0144
        L_0x010c:
            org.telegram.messenger.MessagesController r6 = r25.getMessagesController()
            r24 = r1
            r23 = r2
            long r1 = r0.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r6.getUser(r1)
            if (r1 != 0) goto L_0x0130
            r6 = r3
            long r2 = r0.user_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r13.add(r2)
            long r2 = r0.user_id
            r12.put(r2, r4)
            goto L_0x0144
        L_0x0130:
            r6 = r3
            boolean r2 = r1.deleted
            if (r2 == 0) goto L_0x0144
            r3 = r6
            r7 = r16
            r0 = r17
            r8 = r18
            r6 = r19
            r4 = r21
            r2 = r23
            goto L_0x0052
        L_0x0144:
            r9.add(r4)
            goto L_0x0203
        L_0x0149:
            r20 = r0
            r24 = r1
            r23 = r2
            r6 = r3
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r7)
            if (r0 == 0) goto L_0x0187
            org.telegram.messenger.MessagesController r0 = r25.getMessagesController()
            java.lang.Long r1 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x016f
            java.lang.Long r1 = java.lang.Long.valueOf(r7)
            r13.add(r1)
            r12.put(r7, r4)
            goto L_0x0182
        L_0x016f:
            boolean r1 = r0.deleted
            if (r1 == 0) goto L_0x0182
            r3 = r6
            r7 = r16
            r0 = r17
            r8 = r18
            r6 = r19
            r4 = r21
            r2 = r23
            goto L_0x0052
        L_0x0182:
            r9.add(r4)
            goto L_0x0203
        L_0x0187:
            org.telegram.messenger.MessagesController r0 = r25.getMessagesController()
            long r1 = -r7
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            if (r0 != 0) goto L_0x01b0
            long r1 = -r7
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r14.add(r1)
            r12.put(r7, r4)
            r3 = r6
            r7 = r16
            r0 = r17
            r8 = r18
            r6 = r19
            r4 = r21
            r2 = r23
            goto L_0x0052
        L_0x01b0:
            boolean r1 = r0.left
            if (r1 != 0) goto L_0x01dd
            boolean r1 = r0.kicked
            if (r1 != 0) goto L_0x01dd
            org.telegram.tgnet.TLRPC$InputChannel r1 = r0.migrated_to
            if (r1 == 0) goto L_0x01cb
            r3 = r6
            r7 = r16
            r0 = r17
            r8 = r18
            r6 = r19
            r4 = r21
            r2 = r23
            goto L_0x0052
        L_0x01cb:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x01d9
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x01d9
            r11.add(r4)
            goto L_0x0203
        L_0x01d9:
            r10.add(r4)
            goto L_0x0203
        L_0x01dd:
            r3 = r6
            r7 = r16
            r0 = r17
            r8 = r18
            r6 = r19
            r4 = r21
            r2 = r23
            goto L_0x0052
        L_0x01ec:
            r20 = r0
            r24 = r1
            r23 = r2
            r21 = r4
            r19 = r6
            r6 = r3
            goto L_0x0203
        L_0x01f8:
            r24 = r1
            r23 = r2
            r21 = r4
            r19 = r6
            r18 = r8
            r6 = r3
        L_0x0203:
            r3 = r6
            r7 = r16
            r0 = r17
            r8 = r18
            r6 = r19
            r4 = r21
            r2 = r23
            goto L_0x0052
        L_0x0212:
            r23 = r2
            r21 = r4
            r19 = r6
            r16 = r7
            r18 = r8
            r6 = r3
            int r0 = r12.size()
            if (r0 == 0) goto L_0x0348
            boolean r0 = r15.isEmpty()     // Catch:{ Exception -> 0x028c }
            java.lang.String r1 = ","
            if (r0 != 0) goto L_0x0247
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()     // Catch:{ Exception -> 0x023f }
            java.lang.String r2 = android.text.TextUtils.join(r1, r15)     // Catch:{ Exception -> 0x023f }
            r7 = r19
            r0.getEncryptedChatsInternal(r2, r7, r13)     // Catch:{ Exception -> 0x0239 }
            goto L_0x0249
        L_0x0239:
            r0 = move-exception
            r5 = r16
            r8 = r18
            goto L_0x0293
        L_0x023f:
            r0 = move-exception
            r7 = r19
            r5 = r16
            r8 = r18
            goto L_0x0293
        L_0x0247:
            r7 = r19
        L_0x0249:
            boolean r0 = r13.isEmpty()     // Catch:{ Exception -> 0x0286 }
            if (r0 != 0) goto L_0x0267
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()     // Catch:{ Exception -> 0x0261 }
            java.lang.String r2 = android.text.TextUtils.join(r1, r13)     // Catch:{ Exception -> 0x0261 }
            r8 = r18
            r0.getUsersInternal(r2, r8)     // Catch:{ Exception -> 0x025d }
            goto L_0x0269
        L_0x025d:
            r0 = move-exception
            r5 = r16
            goto L_0x0293
        L_0x0261:
            r0 = move-exception
            r8 = r18
            r5 = r16
            goto L_0x0293
        L_0x0267:
            r8 = r18
        L_0x0269:
            boolean r0 = r14.isEmpty()     // Catch:{ Exception -> 0x0282 }
            if (r0 != 0) goto L_0x027f
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()     // Catch:{ Exception -> 0x0282 }
            java.lang.String r1 = android.text.TextUtils.join(r1, r14)     // Catch:{ Exception -> 0x0282 }
            r5 = r16
            r0.getChatsInternal(r1, r5)     // Catch:{ Exception -> 0x027d }
            goto L_0x0281
        L_0x027d:
            r0 = move-exception
            goto L_0x0293
        L_0x027f:
            r5 = r16
        L_0x0281:
            goto L_0x0296
        L_0x0282:
            r0 = move-exception
            r5 = r16
            goto L_0x0293
        L_0x0286:
            r0 = move-exception
            r5 = r16
            r8 = r18
            goto L_0x0293
        L_0x028c:
            r0 = move-exception
            r5 = r16
            r8 = r18
            r7 = r19
        L_0x0293:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0296:
            r0 = 0
            int r1 = r5.size()
        L_0x029b:
            if (r0 >= r1) goto L_0x02e6
            java.lang.Object r2 = r5.get(r0)
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x02db
            boolean r3 = r2.kicked
            if (r3 != 0) goto L_0x02db
            org.telegram.tgnet.TLRPC$InputChannel r3 = r2.migrated_to
            if (r3 == 0) goto L_0x02b4
            r16 = r13
            r17 = r14
            goto L_0x02df
        L_0x02b4:
            long r3 = r2.id
            long r3 = -r3
            java.lang.Object r3 = r12.get(r3)
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r3 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r3
            r16 = r13
            r17 = r14
            long r13 = r2.id
            long r13 = -r13
            r12.remove(r13)
            if (r3 == 0) goto L_0x02df
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r4 == 0) goto L_0x02d7
            boolean r4 = r2.megagroup
            if (r4 != 0) goto L_0x02d7
            r11.add(r3)
            goto L_0x02df
        L_0x02d7:
            r10.add(r3)
            goto L_0x02df
        L_0x02db:
            r16 = r13
            r17 = r14
        L_0x02df:
            int r0 = r0 + 1
            r13 = r16
            r14 = r17
            goto L_0x029b
        L_0x02e6:
            r16 = r13
            r17 = r14
            r0 = 0
            int r1 = r8.size()
        L_0x02ef:
            if (r0 >= r1) goto L_0x0304
            java.lang.Object r2 = r8.get(r0)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            boolean r3 = r2.deleted
            if (r3 == 0) goto L_0x02fc
            goto L_0x0301
        L_0x02fc:
            long r3 = r2.id
            r12.remove(r3)
        L_0x0301:
            int r0 = r0 + 1
            goto L_0x02ef
        L_0x0304:
            r0 = 0
            int r1 = r7.size()
        L_0x0309:
            if (r0 >= r1) goto L_0x031e
            java.lang.Object r2 = r7.get(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = (org.telegram.tgnet.TLRPC.EncryptedChat) r2
            int r3 = r2.id
            long r3 = (long) r3
            long r3 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r3)
            r12.remove(r3)
            int r0 = r0 + 1
            goto L_0x0309
        L_0x031e:
            r0 = 0
            int r1 = r12.size()
        L_0x0323:
            if (r0 >= r1) goto L_0x0352
            long r2 = r12.keyAt(r0)
            boolean r4 = org.telegram.messenger.DialogObject.isChatDialog(r2)
            if (r4 == 0) goto L_0x033e
            java.lang.Object r4 = r12.valueAt(r0)
            r10.remove(r4)
            java.lang.Object r4 = r12.valueAt(r0)
            r11.remove(r4)
            goto L_0x0345
        L_0x033e:
            java.lang.Object r4 = r12.valueAt(r0)
            r9.remove(r4)
        L_0x0345:
            int r0 = r0 + 1
            goto L_0x0323
        L_0x0348:
            r17 = r14
            r5 = r16
            r8 = r18
            r7 = r19
            r16 = r13
        L_0x0352:
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda9 r0 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda9
            r1 = r0
            r13 = r23
            r2 = r25
            r14 = r6
            r3 = r8
            r18 = r21
            r4 = r5
            r20 = r5
            r5 = r7
            r21 = r7
            r6 = r9
            r7 = r10
            r22 = r8
            r8 = r11
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.m3335xabefe22e():void");
    }

    /* renamed from: lambda$loadExceptions$10$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3334x1eb530ad(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList usersResult, ArrayList chatsResult, ArrayList channelsResult) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        getMessagesController().putEncryptedChats(encryptedChats, true);
        int i = this.currentType;
        if (i == 1) {
            this.exceptions = usersResult;
        } else if (i == 0) {
            this.exceptions = chatsResult;
        } else {
            this.exceptions = channelsResult;
        }
        updateRows(true);
    }

    private void updateRows(boolean notify) {
        ListAdapter listAdapter;
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList;
        this.rowCount = 0;
        int i = this.currentType;
        if (i != -1) {
            int i2 = 0 + 1;
            this.rowCount = i2;
            this.alertRow = 0;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.alertSection2Row = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.messageSectionRow = i3;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.previewRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.messageLedRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.messageVibrateRow = i6;
            if (i == 2) {
                this.messagePopupNotificationRow = -1;
            } else {
                this.rowCount = i7 + 1;
                this.messagePopupNotificationRow = i7;
            }
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.messageSoundRow = i8;
            if (Build.VERSION.SDK_INT >= 21) {
                int i9 = this.rowCount;
                this.rowCount = i9 + 1;
                this.messagePriorityRow = i9;
            } else {
                this.messagePriorityRow = -1;
            }
            int i10 = this.rowCount;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.groupSection2Row = i10;
            this.rowCount = i11 + 1;
            this.exceptionsAddRow = i11;
        } else {
            this.alertRow = -1;
            this.alertSection2Row = -1;
            this.messageSectionRow = -1;
            this.previewRow = -1;
            this.messageLedRow = -1;
            this.messageVibrateRow = -1;
            this.messagePopupNotificationRow = -1;
            this.messageSoundRow = -1;
            this.messagePriorityRow = -1;
            this.groupSection2Row = -1;
            this.exceptionsAddRow = -1;
        }
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2 = this.exceptions;
        if (arrayList2 == null || arrayList2.isEmpty()) {
            this.exceptionsStartRow = -1;
            this.exceptionsEndRow = -1;
        } else {
            int i12 = this.rowCount;
            this.exceptionsStartRow = i12;
            int size = i12 + this.exceptions.size();
            this.rowCount = size;
            this.exceptionsEndRow = size;
        }
        if (this.currentType != -1 || ((arrayList = this.exceptions) != null && !arrayList.isEmpty())) {
            int i13 = this.rowCount;
            this.rowCount = i13 + 1;
            this.exceptionsSection2Row = i13;
        } else {
            this.exceptionsSection2Row = -1;
        }
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList3 = this.exceptions;
        if (arrayList3 == null || arrayList3.isEmpty()) {
            this.deleteAllRow = -1;
            this.deleteAllSectionRow = -1;
        } else {
            int i14 = this.rowCount;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.deleteAllRow = i14;
            this.rowCount = i15 + 1;
            this.deleteAllSectionRow = i15;
        }
        if (notify && (listAdapter = this.adapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (!(ringtone == null || (rng = RingtoneManager.getRingtone(getParentActivity(), ringtone)) == null)) {
                if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    name = LocaleController.getString("SoundDefault", NUM);
                } else {
                    name = rng.getTitle(getParentActivity());
                }
                rng.stop();
            }
            SharedPreferences.Editor editor = getNotificationsSettings().edit();
            int i = this.currentType;
            if (i == 1) {
                if (name == null || ringtone == null) {
                    editor.putString("GlobalSound", "NoSound");
                    editor.putString("GlobalSoundPath", "NoSound");
                } else {
                    editor.putString("GlobalSound", name);
                    editor.putString("GlobalSoundPath", ringtone.toString());
                }
            } else if (i == 0) {
                if (name == null || ringtone == null) {
                    editor.putString("GroupSound", "NoSound");
                    editor.putString("GroupSoundPath", "NoSound");
                } else {
                    editor.putString("GroupSound", name);
                    editor.putString("GroupSoundPath", ringtone.toString());
                }
            } else if (i == 2) {
                if (name == null || ringtone == null) {
                    editor.putString("ChannelSound", "NoSound");
                    editor.putString("ChannelSoundPath", "NoSound");
                } else {
                    editor.putString("ChannelSound", name);
                    editor.putString("ChannelSoundPath", ringtone.toString());
                }
            }
            getNotificationsController().deleteNotificationChannelGlobal(this.currentType);
            editor.commit();
            getNotificationsController().updateServerNotificationsSettings(this.currentType);
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(requestCode);
            if (holder != null) {
                this.adapter.onBindViewHolder(holder, requestCode);
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        /* access modifiers changed from: private */
        public ArrayList<NotificationsSettingsActivity.NotificationException> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda4(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-NotificationsCustomSettingsActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3336x1bdfa523(int searchId) {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1 notificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1 = new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1(this, query);
            this.searchRunnable = notificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1;
            dispatchQueue.postRunnable(notificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void m3339xa1b56767(String query) {
            AndroidUtilities.runOnUIThread(new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda0(this, query));
        }

        /* renamed from: lambda$processSearch$3$org-telegram-ui-NotificationsCustomSettingsActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3338xe4abCLASSNAMEd(String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0, false, 0, 0);
            Utilities.searchQueue.postRunnable(new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda2(this, query, new ArrayList<>(NotificationsCustomSettingsActivity.this.exceptions)));
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v0, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v26, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX WARNING: Code restructure failed: missing block: B:60:0x017f, code lost:
            if (r9[0].contains(" " + r15) == false) goto L_0x0184;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:66:0x019f, code lost:
            if (r3.contains(" " + r15) != false) goto L_0x01a1;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x0201 A[LOOP:1: B:53:0x0151->B:83:0x0201, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:97:0x01ba A[SYNTHETIC] */
        /* renamed from: lambda$processSearch$2$org-telegram-ui-NotificationsCustomSettingsActivity$SearchAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m3337x8ea48bc(java.lang.String r23, java.util.ArrayList r24) {
            /*
                r22 = this;
                r0 = r22
                java.lang.String r1 = r23.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x0023
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r0.updateSearchResults(r2, r3, r4)
                return
            L_0x0023:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r2 = r2.getTranslitString(r1)
                boolean r3 = r1.equals(r2)
                if (r3 != 0) goto L_0x0037
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0038
            L_0x0037:
                r2 = 0
            L_0x0038:
                r3 = 1
                r4 = 0
                if (r2 == 0) goto L_0x003e
                r5 = 1
                goto L_0x003f
            L_0x003e:
                r5 = 0
            L_0x003f:
                int r5 = r5 + r3
                java.lang.String[] r5 = new java.lang.String[r5]
                r5[r4] = r1
                if (r2 == 0) goto L_0x0048
                r5[r3] = r2
            L_0x0048:
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                java.util.ArrayList r8 = new java.util.ArrayList
                r8.<init>()
                r9 = 2
                java.lang.String[] r9 = new java.lang.String[r9]
                r10 = 0
            L_0x005b:
                int r11 = r24.size()
                if (r10 >= r11) goto L_0x0225
                r11 = r24
                java.lang.Object r12 = r11.get(r10)
                org.telegram.ui.NotificationsSettingsActivity$NotificationException r12 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r12
                r13 = 0
                long r14 = r12.did
                boolean r14 = org.telegram.messenger.DialogObject.isEncryptedDialog(r14)
                if (r14 == 0) goto L_0x00b3
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r14 = r14.getMessagesController()
                long r3 = r12.did
                int r3 = org.telegram.messenger.DialogObject.getEncryptedChatId(r3)
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$EncryptedChat r3 = r14.getEncryptedChat(r3)
                if (r3 == 0) goto L_0x00ae
                org.telegram.ui.NotificationsCustomSettingsActivity r4 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                r14 = r1
                r17 = r2
                long r1 = r3.user_id
                java.lang.Long r1 = java.lang.Long.valueOf(r1)
                org.telegram.tgnet.TLRPC$User r1 = r4.getUser(r1)
                if (r1 == 0) goto L_0x00b1
                java.lang.String r2 = r1.first_name
                java.lang.String r4 = r1.last_name
                java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r4)
                r4 = 0
                r9[r4] = r2
                java.lang.String r2 = r1.username
                r4 = 1
                r9[r4] = r2
                goto L_0x00b1
            L_0x00ae:
                r14 = r1
                r17 = r2
            L_0x00b1:
                goto L_0x012d
            L_0x00b3:
                r14 = r1
                r17 = r2
                long r1 = r12.did
                boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r1)
                if (r1 == 0) goto L_0x00f4
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
                long r2 = r12.did
                java.lang.Long r2 = java.lang.Long.valueOf(r2)
                org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
                if (r1 == 0) goto L_0x00ed
                boolean r2 = r1.deleted
                if (r2 == 0) goto L_0x00db
                r18 = r5
                r15 = 1
                r16 = 0
                goto L_0x021a
            L_0x00db:
                java.lang.String r2 = r1.first_name
                java.lang.String r3 = r1.last_name
                java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
                r3 = 0
                r9[r3] = r2
                java.lang.String r2 = r1.username
                r3 = 1
                r9[r3] = r2
                r13 = r1
                goto L_0x012d
            L_0x00ed:
                r18 = r5
                r15 = 1
                r16 = 0
                goto L_0x021a
            L_0x00f4:
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
                long r2 = r12.did
                long r2 = -r2
                java.lang.Long r2 = java.lang.Long.valueOf(r2)
                org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
                if (r1 == 0) goto L_0x012d
                boolean r2 = r1.left
                if (r2 != 0) goto L_0x0126
                boolean r2 = r1.kicked
                if (r2 != 0) goto L_0x0126
                org.telegram.tgnet.TLRPC$InputChannel r2 = r1.migrated_to
                if (r2 == 0) goto L_0x011a
                r18 = r5
                r15 = 1
                r16 = 0
                goto L_0x021a
            L_0x011a:
                java.lang.String r2 = r1.title
                r3 = 0
                r9[r3] = r2
                java.lang.String r2 = r1.username
                r3 = 1
                r9[r3] = r2
                r13 = r1
                goto L_0x012d
            L_0x0126:
                r18 = r5
                r15 = 1
                r16 = 0
                goto L_0x021a
            L_0x012d:
                r1 = 0
                r2 = r9[r1]
                r3 = r9[r1]
                java.lang.String r3 = r3.toLowerCase()
                r9[r1] = r3
                org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
                r4 = r9[r1]
                java.lang.String r3 = r3.getTranslitString(r4)
                r4 = r9[r1]
                if (r4 == 0) goto L_0x014f
                r4 = r9[r1]
                boolean r1 = r4.equals(r3)
                if (r1 == 0) goto L_0x014f
                r3 = 0
            L_0x014f:
                r1 = 0
                r4 = 0
            L_0x0151:
                int r15 = r5.length
                if (r4 >= r15) goto L_0x020f
                r15 = r5[r4]
                r16 = 0
                r18 = r9[r16]
                r19 = r1
                java.lang.String r1 = " "
                if (r18 == 0) goto L_0x0182
                r18 = r5
                r5 = r9[r16]
                boolean r5 = r5.startsWith(r15)
                if (r5 != 0) goto L_0x01a1
                r5 = r9[r16]
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r1)
                r11.append(r15)
                java.lang.String r11 = r11.toString()
                boolean r5 = r5.contains(r11)
                if (r5 != 0) goto L_0x01a1
                goto L_0x0184
            L_0x0182:
                r18 = r5
            L_0x0184:
                if (r3 == 0) goto L_0x01a5
                boolean r5 = r3.startsWith(r15)
                if (r5 != 0) goto L_0x01a1
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r1)
                r5.append(r15)
                java.lang.String r1 = r5.toString()
                boolean r1 = r3.contains(r1)
                if (r1 == 0) goto L_0x01a5
            L_0x01a1:
                r1 = 1
                r5 = r1
                r1 = r15
                goto L_0x01b8
            L_0x01a5:
                r1 = 1
                r5 = r9[r1]
                if (r5 == 0) goto L_0x01b5
                r5 = r9[r1]
                r1 = r15
                boolean r5 = r5.startsWith(r1)
                if (r5 == 0) goto L_0x01b6
                r5 = 2
                goto L_0x01b8
            L_0x01b5:
                r1 = r15
            L_0x01b6:
                r5 = r19
            L_0x01b8:
                if (r5 == 0) goto L_0x0201
                r11 = 0
                r15 = 1
                if (r5 != r15) goto L_0x01ca
                java.lang.CharSequence r11 = org.telegram.messenger.AndroidUtilities.generateSearchName(r2, r11, r1)
                r8.add(r11)
                r20 = r2
                r21 = r3
                goto L_0x01f8
            L_0x01ca:
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r20 = r2
                java.lang.String r2 = "@"
                r11.append(r2)
                r21 = r3
                r3 = r9[r15]
                r11.append(r3)
                java.lang.String r3 = r11.toString()
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r2)
                r11.append(r1)
                java.lang.String r2 = r11.toString()
                r11 = 0
                java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r11, r2)
                r8.add(r2)
            L_0x01f8:
                r7.add(r12)
                if (r13 == 0) goto L_0x021a
                r6.add(r13)
                goto L_0x021a
            L_0x0201:
                r20 = r2
                r21 = r3
                r15 = 1
                int r4 = r4 + 1
                r11 = r24
                r1 = r5
                r5 = r18
                goto L_0x0151
            L_0x020f:
                r19 = r1
                r20 = r2
                r21 = r3
                r18 = r5
                r15 = 1
                r16 = 0
            L_0x021a:
                int r10 = r10 + 1
                r1 = r14
                r2 = r17
                r5 = r18
                r3 = 1
                r4 = 0
                goto L_0x005b
            L_0x0225:
                r0.updateSearchResults(r6, r7, r8)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.SearchAdapter.m3337x8ea48bc(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<Object> result, ArrayList<NotificationsSettingsActivity.NotificationException> exceptions, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda3(this, exceptions, names, result));
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-NotificationsCustomSettingsActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3340x5892f8ec(ArrayList exceptions, ArrayList names, ArrayList result) {
            if (NotificationsCustomSettingsActivity.this.searching) {
                this.searchRunnable = null;
                this.searchResult = exceptions;
                this.searchResultNames = names;
                this.searchAdapterHelper.mergeResults(result);
                if (NotificationsCustomSettingsActivity.this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                    NotificationsCustomSettingsActivity.this.emptyView.showTextView();
                }
                notifyDataSetChanged();
            }
        }

        public Object getObject(int position) {
            if (position >= 0 && position < this.searchResult.size()) {
                return this.searchResult.get(position);
            }
            int position2 = position - (this.searchResult.size() + 1);
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            if (position2 < 0 || position2 >= globalSearch.size()) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(position2);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            int count = this.searchResult.size();
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            if (!globalSearch.isEmpty()) {
                return count + globalSearch.size() + 1;
            }
            return count;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, 4, 0, false, true);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new GraySectionCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    UserCell cell = (UserCell) holder.itemView;
                    boolean z = true;
                    if (position < this.searchResult.size()) {
                        NotificationsSettingsActivity.NotificationException notificationException = this.searchResult.get(position);
                        CharSequence charSequence = this.searchResultNames.get(position);
                        if (position == this.searchResult.size() - 1) {
                            z = false;
                        }
                        cell.setException(notificationException, charSequence, z);
                        cell.setAddButtonVisible(false);
                        return;
                    }
                    int position2 = position - (this.searchResult.size() + 1);
                    ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
                    cell.setData(globalSearch.get(position2), (CharSequence) null, LocaleController.getString("NotificationsOn", NUM), 0, position2 != globalSearch.size() - 1);
                    cell.setAddButtonVisible(true);
                    return;
                case 1:
                    ((GraySectionCell) holder.itemView).setText(LocaleController.getString("AddToExceptions", NUM));
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == this.searchResult.size()) {
                return 1;
            }
            return 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 0 || type == 4) ? false : true;
        }

        public int getItemCount() {
            return NotificationsCustomSettingsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    view = new UserCell(this.mContext, 6, 0, false);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new TextColorCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 6:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean enabled;
            int color;
            int option;
            String value;
            int value2;
            int value3;
            String value4;
            int offUntil;
            String text;
            int iconType;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", NUM));
                        return;
                    }
                    return;
                case 1:
                    TextCheckCell checkCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences preferences = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (i == NotificationsCustomSettingsActivity.this.previewRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            enabled = preferences.getBoolean("EnablePreviewAll", true);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            enabled = preferences.getBoolean("EnablePreviewGroup", true);
                        } else {
                            enabled = preferences.getBoolean("EnablePreviewChannel", true);
                        }
                        checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", NUM), enabled, true);
                        return;
                    }
                    return;
                case 2:
                    UserCell cell = (UserCell) viewHolder.itemView;
                    NotificationsSettingsActivity.NotificationException exception = (NotificationsSettingsActivity.NotificationException) NotificationsCustomSettingsActivity.this.exceptions.get(i - NotificationsCustomSettingsActivity.this.exceptionsStartRow);
                    if (i != NotificationsCustomSettingsActivity.this.exceptionsEndRow - 1) {
                        z = true;
                    }
                    cell.setException(exception, (CharSequence) null, z);
                    return;
                case 3:
                    TextColorCell textColorCell = (TextColorCell) viewHolder.itemView;
                    SharedPreferences preferences2 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        color = preferences2.getInt("MessagesLed", -16776961);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        color = preferences2.getInt("GroupLed", -16776961);
                    } else {
                        color = preferences2.getInt("ChannelLed", -16776961);
                    }
                    int a = 0;
                    while (true) {
                        if (a < 9) {
                            if (TextColorCell.colorsToSave[a] == color) {
                                color = TextColorCell.colors[a];
                            } else {
                                a++;
                            }
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", NUM), color, true);
                    return;
                case 4:
                    if (i == NotificationsCustomSettingsActivity.this.deleteAllSectionRow || ((i == NotificationsCustomSettingsActivity.this.groupSection2Row && NotificationsCustomSettingsActivity.this.exceptionsSection2Row == -1) || (i == NotificationsCustomSettingsActivity.this.exceptionsSection2Row && NotificationsCustomSettingsActivity.this.deleteAllRow == -1))) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 5:
                    TextSettingsCell textCell = (TextSettingsCell) viewHolder.itemView;
                    SharedPreferences preferences3 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (i == NotificationsCustomSettingsActivity.this.messageSoundRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value4 = preferences3.getString("GlobalSound", LocaleController.getString("SoundDefault", NUM));
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value4 = preferences3.getString("GroupSound", LocaleController.getString("SoundDefault", NUM));
                        } else {
                            value4 = preferences3.getString("ChannelSound", LocaleController.getString("SoundDefault", NUM));
                        }
                        if (value4.equals("NoSound")) {
                            value4 = LocaleController.getString("NoSound", NUM);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", NUM), value4, true);
                        return;
                    } else if (i == NotificationsCustomSettingsActivity.this.messageVibrateRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value3 = preferences3.getInt("vibrate_messages", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value3 = preferences3.getInt("vibrate_group", 0);
                        } else {
                            value3 = preferences3.getInt("vibrate_channel", 0);
                        }
                        if (value3 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDefault", NUM), true);
                            return;
                        } else if (value3 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Short", NUM), true);
                            return;
                        } else if (value3 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                            return;
                        } else if (value3 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Long", NUM), true);
                            return;
                        } else if (value3 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("OnlyIfSilent", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsCustomSettingsActivity.this.messagePriorityRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value2 = preferences3.getInt("priority_messages", 1);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value2 = preferences3.getInt("priority_group", 1);
                        } else {
                            value2 = preferences3.getInt("priority_channel", 1);
                        }
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), false);
                            return;
                        } else if (value2 == 1 || value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM), false);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityLow", NUM), false);
                            return;
                        } else if (value2 == 5) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsCustomSettingsActivity.this.messagePopupNotificationRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            option = preferences3.getInt("popupAll", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            option = preferences3.getInt("popupGroup", 0);
                        } else {
                            option = preferences3.getInt("popupChannel", 0);
                        }
                        if (option == 0) {
                            value = LocaleController.getString("NoPopup", NUM);
                        } else if (option == 1) {
                            value = LocaleController.getString("OnlyWhenScreenOn", NUM);
                        } else if (option == 2) {
                            value = LocaleController.getString("OnlyWhenScreenOff", NUM);
                        } else {
                            value = LocaleController.getString("AlwaysShowPopup", NUM);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PopupNotification", NUM), value, true);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    NotificationsCheckCell checkCell2 = (NotificationsCheckCell) viewHolder.itemView;
                    checkCell2.setDrawLine(false);
                    StringBuilder builder = new StringBuilder();
                    SharedPreferences preferences4 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        text = LocaleController.getString("NotificationsForPrivateChats", NUM);
                        offUntil = preferences4.getInt("EnableAll2", 0);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        text = LocaleController.getString("NotificationsForGroups", NUM);
                        offUntil = preferences4.getInt("EnableGroup2", 0);
                    } else {
                        text = LocaleController.getString("NotificationsForChannels", NUM);
                        offUntil = preferences4.getInt("EnableChannel2", 0);
                    }
                    int currentTime = NotificationsCustomSettingsActivity.this.getConnectionsManager().getCurrentTime();
                    boolean z2 = offUntil < currentTime;
                    boolean enabled2 = z2;
                    if (z2) {
                        builder.append(LocaleController.getString("NotificationsOn", NUM));
                        iconType = 0;
                    } else if (offUntil - 31536000 >= currentTime) {
                        builder.append(LocaleController.getString("NotificationsOff", NUM));
                        iconType = 0;
                    } else {
                        builder.append(LocaleController.formatString("NotificationsOffUntil", NUM, LocaleController.stringForMessageListDate((long) offUntil)));
                        iconType = 2;
                    }
                    int i2 = currentTime;
                    checkCell2.setTextAndValueAndCheck(text, builder, enabled2, iconType, false);
                    return;
                case 7:
                    TextCell textCell2 = (TextCell) viewHolder.itemView;
                    if (i == NotificationsCustomSettingsActivity.this.exceptionsAddRow) {
                        String string = LocaleController.getString("NotificationsAddAnException", NUM);
                        if (NotificationsCustomSettingsActivity.this.exceptionsStartRow != -1) {
                            z = true;
                        }
                        textCell2.setTextAndIcon(string, NUM, z);
                        textCell2.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                        return;
                    } else if (i == NotificationsCustomSettingsActivity.this.deleteAllRow) {
                        textCell2.setText(LocaleController.getString("NotificationsDeleteAllException", NUM), false);
                        textCell2.setColors((String) null, "windowBackgroundWhiteRedText5");
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (NotificationsCustomSettingsActivity.this.exceptions != null && NotificationsCustomSettingsActivity.this.exceptions.isEmpty()) {
                boolean enabled = NotificationsCustomSettingsActivity.this.getNotificationsController().isGlobalNotificationsEnabled(NotificationsCustomSettingsActivity.this.currentType);
                switch (holder.getItemViewType()) {
                    case 0:
                        HeaderCell headerCell = (HeaderCell) holder.itemView;
                        if (holder.getAdapterPosition() == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                            headerCell.setEnabled(enabled, (ArrayList<Animator>) null);
                            return;
                        } else {
                            headerCell.setEnabled(true, (ArrayList<Animator>) null);
                            return;
                        }
                    case 1:
                        ((TextCheckCell) holder.itemView).setEnabled(enabled, (ArrayList<Animator>) null);
                        return;
                    case 3:
                        ((TextColorCell) holder.itemView).setEnabled(enabled, (ArrayList<Animator>) null);
                        return;
                    case 5:
                        ((TextSettingsCell) holder.itemView).setEnabled(enabled, (ArrayList<Animator>) null);
                        return;
                    default:
                        return;
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                return 0;
            }
            if (position == NotificationsCustomSettingsActivity.this.previewRow) {
                return 1;
            }
            if (position >= NotificationsCustomSettingsActivity.this.exceptionsStartRow && position < NotificationsCustomSettingsActivity.this.exceptionsEndRow) {
                return 2;
            }
            if (position == NotificationsCustomSettingsActivity.this.messageLedRow) {
                return 3;
            }
            if (position == NotificationsCustomSettingsActivity.this.groupSection2Row || position == NotificationsCustomSettingsActivity.this.alertSection2Row || position == NotificationsCustomSettingsActivity.this.exceptionsSection2Row || position == NotificationsCustomSettingsActivity.this.deleteAllSectionRow) {
                return 4;
            }
            if (position == NotificationsCustomSettingsActivity.this.alertRow) {
                return 6;
            }
            if (position == NotificationsCustomSettingsActivity.this.exceptionsAddRow || position == NotificationsCustomSettingsActivity.this.deleteAllRow) {
                return 7;
            }
            return 5;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda12(this);
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextColorCell.class, TextSettingsCell.class, UserCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$12$org-telegram-ui-NotificationsCustomSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3333x2384854f() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}

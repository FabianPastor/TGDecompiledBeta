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
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.NotificationsSettingsActivity;

public class NotificationsCustomSettingsActivity extends BaseFragment {
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

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList) {
        this(i, arrayList, false);
    }

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList, boolean z) {
        this.rowCount = 0;
        this.exceptionsDict = new HashMap<>();
        this.currentType = i;
        this.exceptions = arrayList;
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            NotificationsSettingsActivity.NotificationException notificationException = this.exceptions.get(i2);
            this.exceptionsDict.put(Long.valueOf(notificationException.did), notificationException);
        }
        if (z) {
            loadExceptions();
        }
    }

    public boolean onFragmentCreate() {
        updateRows(true);
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.searching = false;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == -1) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsExceptions", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Notifications", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
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
                        String obj = editText.getText().toString();
                        if (obj.length() != 0) {
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
                        NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs(obj);
                    }
                }
            }).setSearchFieldHint(LocaleController.getString("Search", NUM));
        }
        this.searchAdapter = new SearchAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", NUM));
        this.emptyView.showTextView();
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda10(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(NotificationsCustomSettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(View view, int i, float f, float f2) {
        NotificationsSettingsActivity.NotificationException notificationException;
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList;
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2;
        NotificationsSettingsActivity.NotificationException notificationException2;
        long j;
        String str;
        boolean z;
        View view2 = view;
        int i2 = i;
        if (getParentActivity() != null) {
            boolean z2 = false;
            if (this.listView.getAdapter() == this.searchAdapter || (i2 >= this.exceptionsStartRow && i2 < this.exceptionsEndRow)) {
                RecyclerView.Adapter adapter2 = this.listView.getAdapter();
                SearchAdapter searchAdapter2 = this.searchAdapter;
                if (adapter2 == searchAdapter2) {
                    Object object = searchAdapter2.getObject(i2);
                    if (object instanceof NotificationsSettingsActivity.NotificationException) {
                        arrayList2 = this.searchAdapter.searchResult;
                        notificationException2 = (NotificationsSettingsActivity.NotificationException) object;
                    } else {
                        boolean z3 = object instanceof TLRPC$User;
                        if (z3) {
                            j = ((TLRPC$User) object).id;
                        } else {
                            j = -((TLRPC$Chat) object).id;
                        }
                        if (this.exceptionsDict.containsKey(Long.valueOf(j))) {
                            notificationException2 = this.exceptionsDict.get(Long.valueOf(j));
                        } else {
                            NotificationsSettingsActivity.NotificationException notificationException3 = new NotificationsSettingsActivity.NotificationException();
                            notificationException3.did = j;
                            if (z3) {
                                notificationException3.did = ((TLRPC$User) object).id;
                            } else {
                                notificationException3.did = -((TLRPC$Chat) object).id;
                            }
                            notificationException2 = notificationException3;
                            z2 = true;
                        }
                        arrayList2 = this.exceptions;
                    }
                    notificationException = notificationException2;
                    arrayList = arrayList2;
                } else {
                    ArrayList<NotificationsSettingsActivity.NotificationException> arrayList3 = this.exceptions;
                    int i3 = i2 - this.exceptionsStartRow;
                    if (i3 >= 0 && i3 < arrayList3.size()) {
                        arrayList = arrayList3;
                        notificationException = arrayList3.get(i3);
                    } else {
                        return;
                    }
                }
                if (notificationException != null) {
                    AlertsCreator.showCustomNotificationsDialog(this, notificationException.did, -1, (ArrayList<NotificationsSettingsActivity.NotificationException>) null, this.currentAccount, (MessagesStorage.IntCallback) null, new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda8(this, z2, arrayList, notificationException, i));
                    return;
                }
                return;
            }
            if (i2 == this.exceptionsAddRow) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putBoolean("checkCanWrite", false);
                int i4 = this.currentType;
                if (i4 == 0) {
                    bundle.putInt("dialogsType", 6);
                } else if (i4 == 2) {
                    bundle.putInt("dialogsType", 5);
                } else {
                    bundle.putInt("dialogsType", 4);
                }
                DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda11(this));
                presentFragment(dialogsActivity);
            } else {
                Uri uri = null;
                if (i2 == this.deleteAllRow) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("NotificationsDeleteAllExceptionTitle", NUM));
                    builder.setMessage(LocaleController.getString("NotificationsDeleteAllExceptionAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda0(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                } else if (i2 == this.alertRow) {
                    boolean isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(this.currentType);
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view2;
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i2);
                    if (!isGlobalNotificationsEnabled) {
                        getNotificationsController().setGlobalNotificationsEnabled(this.currentType, 0);
                        notificationsCheckCell.setChecked(true);
                        if (findViewHolderForAdapterPosition != null) {
                            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i2);
                        }
                        checkRowsEnabled();
                    } else {
                        AlertsCreator.showCustomNotificationsDialog(this, 0, this.currentType, this.exceptions, this.currentAccount, new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda7(this, notificationsCheckCell, findViewHolderForAdapterPosition, i2));
                    }
                    z2 = isGlobalNotificationsEnabled;
                } else if (i2 == this.previewRow) {
                    if (view.isEnabled()) {
                        SharedPreferences notificationsSettings = getNotificationsSettings();
                        SharedPreferences.Editor edit = notificationsSettings.edit();
                        int i5 = this.currentType;
                        if (i5 == 1) {
                            z = notificationsSettings.getBoolean("EnablePreviewAll", true);
                            edit.putBoolean("EnablePreviewAll", !z);
                        } else if (i5 == 0) {
                            z = notificationsSettings.getBoolean("EnablePreviewGroup", true);
                            edit.putBoolean("EnablePreviewGroup", !z);
                        } else {
                            z = notificationsSettings.getBoolean("EnablePreviewChannel", true);
                            edit.putBoolean("EnablePreviewChannel", !z);
                        }
                        z2 = z;
                        edit.commit();
                        getNotificationsController().updateServerNotificationsSettings(this.currentType);
                    } else {
                        return;
                    }
                } else if (i2 == this.messageSoundRow) {
                    if (view.isEnabled()) {
                        try {
                            SharedPreferences notificationsSettings2 = getNotificationsSettings();
                            Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                            intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                            intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                            intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                            Uri uri2 = Settings.System.DEFAULT_NOTIFICATION_URI;
                            String path = uri2 != null ? uri2.getPath() : null;
                            int i6 = this.currentType;
                            if (i6 == 1) {
                                str = notificationsSettings2.getString("GlobalSoundPath", path);
                            } else if (i6 == 0) {
                                str = notificationsSettings2.getString("GroupSoundPath", path);
                            } else {
                                str = notificationsSettings2.getString("ChannelSoundPath", path);
                            }
                            if (str != null && !str.equals("NoSound")) {
                                uri = str.equals(path) ? uri2 : Uri.parse(str);
                            }
                            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", uri);
                            startActivityForResult(intent, i2);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else {
                        return;
                    }
                } else if (i2 == this.messageLedRow) {
                    if (view.isEnabled()) {
                        showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), 0, this.currentType, new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda2(this, i2)));
                    } else {
                        return;
                    }
                } else if (i2 == this.messagePopupNotificationRow) {
                    if (view.isEnabled()) {
                        showDialog(AlertsCreator.createPopupSelectDialog(getParentActivity(), this.currentType, new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda3(this, i2)));
                    } else {
                        return;
                    }
                } else if (i2 == this.messageVibrateRow) {
                    if (view.isEnabled()) {
                        int i7 = this.currentType;
                        showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0, i7 == 1 ? "vibrate_messages" : i7 == 0 ? "vibrate_group" : "vibrate_channel", new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda4(this, i2)));
                    } else {
                        return;
                    }
                } else if (i2 == this.messagePriorityRow) {
                    if (view.isEnabled()) {
                        showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), 0, this.currentType, new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda5(this, i2)));
                    } else {
                        return;
                    }
                }
            }
            if (view2 instanceof TextCheckCell) {
                ((TextCheckCell) view2).setChecked(!z2);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(boolean z, ArrayList arrayList, NotificationsSettingsActivity.NotificationException notificationException, int i, int i2) {
        int indexOf;
        if (i2 != 0) {
            SharedPreferences notificationsSettings = getNotificationsSettings();
            notificationException.hasCustom = notificationsSettings.getBoolean("custom_" + notificationException.did, false);
            int i3 = notificationsSettings.getInt("notify2_" + notificationException.did, 0);
            notificationException.notify = i3;
            if (i3 != 0) {
                int i4 = notificationsSettings.getInt("notifyuntil_" + notificationException.did, -1);
                if (i4 != -1) {
                    notificationException.muteUntil = i4;
                }
            }
            if (z) {
                this.exceptions.add(notificationException);
                this.exceptionsDict.put(Long.valueOf(notificationException.did), notificationException);
                updateRows(true);
            } else {
                this.listView.getAdapter().notifyItemChanged(i);
            }
            this.actionBar.closeSearchField();
        } else if (!z) {
            ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2 = this.exceptions;
            if (arrayList != arrayList2 && (indexOf = arrayList2.indexOf(notificationException)) >= 0) {
                this.exceptions.remove(indexOf);
                this.exceptionsDict.remove(Long.valueOf(notificationException.did));
            }
            arrayList.remove(notificationException);
            if (arrayList == this.exceptions) {
                if (this.exceptionsAddRow != -1 && arrayList.isEmpty()) {
                    this.listView.getAdapter().notifyItemChanged(this.exceptionsAddRow);
                    this.listView.getAdapter().notifyItemRemoved(this.deleteAllRow);
                    this.listView.getAdapter().notifyItemRemoved(this.deleteAllSectionRow);
                }
                this.listView.getAdapter().notifyItemRemoved(i);
                updateRows(false);
                checkRowsEnabled();
            } else {
                updateRows(true);
                this.searchAdapter.notifyDataSetChanged();
            }
            this.actionBar.closeSearchField();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putLong("dialog_id", ((Long) arrayList.get(0)).longValue());
        bundle.putBoolean("exception", true);
        ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(bundle);
        profileNotificationsActivity.setDelegate(new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda12(this));
        presentFragment(profileNotificationsActivity, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(NotificationsSettingsActivity.NotificationException notificationException) {
        this.exceptions.add(0, notificationException);
        updateRows(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(DialogInterface dialogInterface, int i) {
        SharedPreferences.Editor edit = getNotificationsSettings().edit();
        int size = this.exceptions.size();
        for (int i2 = 0; i2 < size; i2++) {
            NotificationsSettingsActivity.NotificationException notificationException = this.exceptions.get(i2);
            SharedPreferences.Editor remove = edit.remove("notify2_" + notificationException.did);
            remove.remove("custom_" + notificationException.did);
            getMessagesStorage().setDialogFlags(notificationException.did, 0);
            TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(notificationException.did);
            if (tLRPC$Dialog != null) {
                tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
            }
        }
        edit.commit();
        int size2 = this.exceptions.size();
        for (int i3 = 0; i3 < size2; i3++) {
            getNotificationsController().updateServerNotificationsSettings(this.exceptions.get(i3).did, false);
        }
        this.exceptions.clear();
        this.exceptionsDict.clear();
        updateRows(true);
        getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(NotificationsCheckCell notificationsCheckCell, RecyclerView.ViewHolder viewHolder, int i, int i2) {
        int i3;
        SharedPreferences notificationsSettings = getNotificationsSettings();
        int i4 = this.currentType;
        int i5 = 0;
        if (i4 == 1) {
            i3 = notificationsSettings.getInt("EnableAll2", 0);
        } else if (i4 == 0) {
            i3 = notificationsSettings.getInt("EnableGroup2", 0);
        } else {
            i3 = notificationsSettings.getInt("EnableChannel2", 0);
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        if (i3 >= currentTime && i3 - 31536000 < currentTime) {
            i5 = 2;
        }
        notificationsCheckCell.setChecked(getNotificationsController().isGlobalNotificationsEnabled(this.currentType), i5);
        if (viewHolder != null) {
            this.adapter.onBindViewHolder(viewHolder, i);
        }
        checkRowsEnabled();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    private void checkRowsEnabled() {
        if (this.exceptions.isEmpty()) {
            int childCount = this.listView.getChildCount();
            ArrayList arrayList = new ArrayList();
            boolean isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(this.currentType);
            for (int i = 0; i < childCount; i++) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(this.listView.getChildAt(i));
                int itemViewType = holder.getItemViewType();
                if (itemViewType == 0) {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (holder.getAdapterPosition() == this.messageSectionRow) {
                        headerCell.setEnabled(isGlobalNotificationsEnabled, arrayList);
                    }
                } else if (itemViewType == 1) {
                    ((TextCheckCell) holder.itemView).setEnabled(isGlobalNotificationsEnabled, arrayList);
                } else if (itemViewType == 3) {
                    ((TextColorCell) holder.itemView).setEnabled(isGlobalNotificationsEnabled, arrayList);
                } else if (itemViewType == 5) {
                    ((TextSettingsCell) holder.itemView).setEnabled(isGlobalNotificationsEnabled, arrayList);
                }
            }
            if (!arrayList.isEmpty()) {
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(arrayList);
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
        getMessagesStorage().getStorageQueue().postRunnable(new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda1(this));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v17, resolved type: org.telegram.messenger.MessagesStorage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: java.util.ArrayList} */
    /* JADX WARNING: type inference failed for: r4v9, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0117, code lost:
        if (r4.deleted != false) goto L_0x0185;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0141, code lost:
        if (r4.deleted != false) goto L_0x0185;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0246  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0260 A[LOOP:3: B:109:0x025e->B:110:0x0260, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0279  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0207  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadExceptions$11() {
        /*
            r22 = this;
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            android.util.LongSparseArray r1 = new android.util.LongSparseArray
            r1.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            org.telegram.messenger.UserConfig r10 = r22.getUserConfig()
            long r10 = r10.clientUserId
            android.content.SharedPreferences r12 = r22.getNotificationsSettings()
            java.util.Map r13 = r12.getAll()
            java.util.Set r14 = r13.entrySet()
            java.util.Iterator r14 = r14.iterator()
        L_0x0048:
            boolean r15 = r14.hasNext()
            r16 = r5
            if (r15 == 0) goto L_0x01a4
            java.lang.Object r15 = r14.next()
            java.util.Map$Entry r15 = (java.util.Map.Entry) r15
            java.lang.Object r17 = r15.getKey()
            r5 = r17
            java.lang.String r5 = (java.lang.String) r5
            r17 = r14
            java.lang.String r14 = "notify2_"
            boolean r18 = r5.startsWith(r14)
            if (r18 == 0) goto L_0x0194
            r18 = r4
            java.lang.String r4 = ""
            java.lang.String r4 = r5.replace(r14, r4)
            java.lang.Long r5 = org.telegram.messenger.Utilities.parseLong(r4)
            r14 = r7
            r19 = r8
            long r7 = r5.longValue()
            r20 = 0
            int r5 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
            if (r5 == 0) goto L_0x018f
            int r5 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r5 == 0) goto L_0x018f
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r5 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException
            r5.<init>()
            r5.did = r7
            r20 = r10
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "custom_"
            r10.append(r11)
            r10.append(r7)
            java.lang.String r10 = r10.toString()
            r11 = 0
            boolean r10 = r12.getBoolean(r10, r11)
            r5.hasCustom = r10
            java.lang.Object r10 = r15.getValue()
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r10 = r10.intValue()
            r5.notify = r10
            if (r10 == 0) goto L_0x00d3
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "notifyuntil_"
            r10.append(r11)
            r10.append(r4)
            java.lang.String r4 = r10.toString()
            java.lang.Object r4 = r13.get(r4)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x00d3
            int r4 = r4.intValue()
            r5.muteUntil = r4
        L_0x00d3:
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)
            if (r4 == 0) goto L_0x0120
            int r4 = org.telegram.messenger.DialogObject.getEncryptedChatId(r7)
            org.telegram.messenger.MessagesController r10 = r22.getMessagesController()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = r10.getEncryptedChat(r11)
            if (r10 != 0) goto L_0x00f6
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3.add(r4)
            r1.put(r7, r5)
            goto L_0x011b
        L_0x00f6:
            org.telegram.messenger.MessagesController r4 = r22.getMessagesController()
            long r7 = r10.user_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r7)
            if (r4 != 0) goto L_0x0115
            long r7 = r10.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r7)
            r0.add(r4)
            long r7 = r10.user_id
            r1.put(r7, r5)
            goto L_0x011b
        L_0x0115:
            boolean r4 = r4.deleted
            if (r4 == 0) goto L_0x011b
            goto L_0x0185
        L_0x011b:
            r6.add(r5)
            goto L_0x0191
        L_0x0120:
            boolean r4 = org.telegram.messenger.DialogObject.isUserDialog(r7)
            if (r4 == 0) goto L_0x0148
            org.telegram.messenger.MessagesController r4 = r22.getMessagesController()
            java.lang.Long r10 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r10)
            if (r4 != 0) goto L_0x013f
            java.lang.Long r4 = java.lang.Long.valueOf(r7)
            r0.add(r4)
            r1.put(r7, r5)
            goto L_0x0144
        L_0x013f:
            boolean r4 = r4.deleted
            if (r4 == 0) goto L_0x0144
            goto L_0x0185
        L_0x0144:
            r6.add(r5)
            goto L_0x0191
        L_0x0148:
            org.telegram.messenger.MessagesController r4 = r22.getMessagesController()
            long r10 = -r7
            java.lang.Long r15 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r15)
            if (r4 != 0) goto L_0x0162
            java.lang.Long r4 = java.lang.Long.valueOf(r10)
            r2.add(r4)
            r1.put(r7, r5)
            goto L_0x0185
        L_0x0162:
            boolean r7 = r4.left
            if (r7 != 0) goto L_0x0185
            boolean r7 = r4.kicked
            if (r7 != 0) goto L_0x0185
            org.telegram.tgnet.TLRPC$InputChannel r7 = r4.migrated_to
            if (r7 == 0) goto L_0x016f
            goto L_0x0185
        L_0x016f:
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r7 == 0) goto L_0x017f
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x017f
            r8 = r19
            r8.add(r5)
            goto L_0x0199
        L_0x017f:
            r8 = r19
            r14.add(r5)
            goto L_0x0199
        L_0x0185:
            r7 = r14
            r5 = r16
            r14 = r17
            r4 = r18
            r8 = r19
            goto L_0x01a0
        L_0x018f:
            r20 = r10
        L_0x0191:
            r8 = r19
            goto L_0x0199
        L_0x0194:
            r18 = r4
            r14 = r7
            r20 = r10
        L_0x0199:
            r7 = r14
            r5 = r16
            r14 = r17
            r4 = r18
        L_0x01a0:
            r10 = r20
            goto L_0x0048
        L_0x01a4:
            r18 = r4
            r14 = r7
            r11 = 0
            int r4 = r1.size()
            if (r4 == 0) goto L_0x029c
            boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x01f8 }
            java.lang.String r5 = ","
            if (r4 != 0) goto L_0x01c1
            org.telegram.messenger.MessagesStorage r4 = r22.getMessagesStorage()     // Catch:{ Exception -> 0x01f8 }
            java.lang.String r3 = android.text.TextUtils.join(r5, r3)     // Catch:{ Exception -> 0x01f8 }
            r4.getEncryptedChatsInternal(r3, r9, r0)     // Catch:{ Exception -> 0x01f8 }
        L_0x01c1:
            boolean r3 = r0.isEmpty()     // Catch:{ Exception -> 0x01f8 }
            if (r3 != 0) goto L_0x01d9
            org.telegram.messenger.MessagesStorage r3 = r22.getMessagesStorage()     // Catch:{ Exception -> 0x01d5 }
            java.lang.String r0 = android.text.TextUtils.join(r5, r0)     // Catch:{ Exception -> 0x01d5 }
            r4 = r18
            r3.getUsersInternal(r0, r4)     // Catch:{ Exception -> 0x01f4 }
            goto L_0x01db
        L_0x01d5:
            r0 = move-exception
            r4 = r18
            goto L_0x01f5
        L_0x01d9:
            r4 = r18
        L_0x01db:
            boolean r0 = r2.isEmpty()     // Catch:{ Exception -> 0x01f4 }
            if (r0 != 0) goto L_0x01f1
            org.telegram.messenger.MessagesStorage r0 = r22.getMessagesStorage()     // Catch:{ Exception -> 0x01f4 }
            java.lang.String r2 = android.text.TextUtils.join(r5, r2)     // Catch:{ Exception -> 0x01f4 }
            r5 = r16
            r0.getChatsInternal(r2, r5)     // Catch:{ Exception -> 0x01ef }
            goto L_0x0200
        L_0x01ef:
            r0 = move-exception
            goto L_0x01fd
        L_0x01f1:
            r5 = r16
            goto L_0x0200
        L_0x01f4:
            r0 = move-exception
        L_0x01f5:
            r5 = r16
            goto L_0x01fd
        L_0x01f8:
            r0 = move-exception
            r5 = r16
            r4 = r18
        L_0x01fd:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0200:
            int r0 = r5.size()
            r2 = 0
        L_0x0205:
            if (r2 >= r0) goto L_0x023f
            java.lang.Object r3 = r5.get(r2)
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC$Chat) r3
            boolean r7 = r3.left
            if (r7 != 0) goto L_0x023c
            boolean r7 = r3.kicked
            if (r7 != 0) goto L_0x023c
            org.telegram.tgnet.TLRPC$InputChannel r7 = r3.migrated_to
            if (r7 == 0) goto L_0x021a
            goto L_0x023c
        L_0x021a:
            long r12 = r3.id
            long r12 = -r12
            java.lang.Object r7 = r1.get(r12)
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r7 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r7
            long r12 = r3.id
            long r12 = -r12
            r1.remove(r12)
            if (r7 == 0) goto L_0x023c
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r10 == 0) goto L_0x0239
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0239
            r8.add(r7)
            goto L_0x023c
        L_0x0239:
            r14.add(r7)
        L_0x023c:
            int r2 = r2 + 1
            goto L_0x0205
        L_0x023f:
            int r0 = r4.size()
            r2 = 0
        L_0x0244:
            if (r2 >= r0) goto L_0x0259
            java.lang.Object r3 = r4.get(r2)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            boolean r7 = r3.deleted
            if (r7 == 0) goto L_0x0251
            goto L_0x0256
        L_0x0251:
            long r12 = r3.id
            r1.remove(r12)
        L_0x0256:
            int r2 = r2 + 1
            goto L_0x0244
        L_0x0259:
            int r0 = r9.size()
            r2 = 0
        L_0x025e:
            if (r2 >= r0) goto L_0x0273
            java.lang.Object r3 = r9.get(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = (org.telegram.tgnet.TLRPC$EncryptedChat) r3
            int r3 = r3.id
            long r12 = (long) r3
            long r12 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r12)
            r1.remove(r12)
            int r2 = r2 + 1
            goto L_0x025e
        L_0x0273:
            int r0 = r1.size()
        L_0x0277:
            if (r11 >= r0) goto L_0x02a0
            long r2 = r1.keyAt(r11)
            boolean r2 = org.telegram.messenger.DialogObject.isChatDialog(r2)
            if (r2 == 0) goto L_0x0292
            java.lang.Object r2 = r1.valueAt(r11)
            r14.remove(r2)
            java.lang.Object r2 = r1.valueAt(r11)
            r8.remove(r2)
            goto L_0x0299
        L_0x0292:
            java.lang.Object r2 = r1.valueAt(r11)
            r6.remove(r2)
        L_0x0299:
            int r11 = r11 + 1
            goto L_0x0277
        L_0x029c:
            r5 = r16
            r4 = r18
        L_0x02a0:
            org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda6 r0 = new org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda6
            r1 = r0
            r2 = r22
            r3 = r4
            r4 = r5
            r5 = r9
            r7 = r14
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.lambda$loadExceptions$11():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadExceptions$10(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        int i = this.currentType;
        if (i == 1) {
            this.exceptions = arrayList4;
        } else if (i == 0) {
            this.exceptions = arrayList5;
        } else {
            this.exceptions = arrayList6;
        }
        updateRows(true);
    }

    private void updateRows(boolean z) {
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
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.messageSoundRow = i8;
            if (Build.VERSION.SDK_INT >= 21) {
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
        if (z && (listAdapter = this.adapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        Ringtone ringtone;
        if (i2 == -1) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (!(uri == null || (ringtone = RingtoneManager.getRingtone(getParentActivity(), uri)) == null)) {
                if (uri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    str = LocaleController.getString("SoundDefault", NUM);
                } else {
                    str = ringtone.getTitle(getParentActivity());
                }
                ringtone.stop();
            }
            SharedPreferences.Editor edit = getNotificationsSettings().edit();
            int i3 = this.currentType;
            if (i3 == 1) {
                if (str == null || uri == null) {
                    edit.putString("GlobalSound", "NoSound");
                    edit.putString("GlobalSoundPath", "NoSound");
                } else {
                    edit.putString("GlobalSound", str);
                    edit.putString("GlobalSoundPath", uri.toString());
                }
            } else if (i3 == 0) {
                if (str == null || uri == null) {
                    edit.putString("GroupSound", "NoSound");
                    edit.putString("GroupSoundPath", "NoSound");
                } else {
                    edit.putString("GroupSound", str);
                    edit.putString("GroupSoundPath", uri.toString());
                }
            } else if (i3 == 2) {
                if (str == null || uri == null) {
                    edit.putString("ChannelSound", "NoSound");
                    edit.putString("ChannelSoundPath", "NoSound");
                } else {
                    edit.putString("ChannelSound", str);
                    edit.putString("ChannelSoundPath", uri.toString());
                }
            }
            getNotificationsController().deleteNotificationChannelGlobal(this.currentType);
            edit.commit();
            getNotificationsController().updateServerNotificationsSettings(this.currentType);
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
            if (findViewHolderForAdapterPosition != null) {
                this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda4(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i) {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void searchDialogs(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1 notificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1 = new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1(this, str);
            this.searchRunnable = notificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1;
            dispatchQueue.postRunnable(notificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchDialogs$1(String str) {
            AndroidUtilities.runOnUIThread(new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda0(this, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$3(String str) {
            this.searchAdapterHelper.queryServerSearch(str, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0, false, 0, 0);
            Utilities.searchQueue.postRunnable(new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda2(this, str, new ArrayList(NotificationsCustomSettingsActivity.this.exceptions)));
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v1, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v2, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v3, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v12, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v22, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x0169, code lost:
            if (r10[r5].contains(" " + r15) == false) goto L_0x016e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x0189, code lost:
            if (r6.contains(" " + r15) != false) goto L_0x018b;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0140  */
        /* JADX WARNING: Removed duplicated region for block: B:79:0x01e0 A[LOOP:1: B:52:0x013e->B:79:0x01e0, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:89:0x01a1 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$processSearch$2(java.lang.String r20, java.util.ArrayList r21) {
            /*
                r19 = this;
                r0 = r19
                java.lang.String r1 = r20.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x0023
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r0.updateSearchResults(r1, r2, r3)
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
                r5 = 0
                if (r2 == 0) goto L_0x003e
                r6 = 1
                goto L_0x003f
            L_0x003e:
                r6 = 0
            L_0x003f:
                int r6 = r6 + r3
                java.lang.String[] r7 = new java.lang.String[r6]
                r7[r5] = r1
                if (r2 == 0) goto L_0x0048
                r7[r3] = r2
            L_0x0048:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r8 = new java.util.ArrayList
                r8.<init>()
                r9 = 2
                java.lang.String[] r10 = new java.lang.String[r9]
                r11 = 0
            L_0x005b:
                int r12 = r21.size()
                if (r11 >= r12) goto L_0x01fd
                r12 = r21
                java.lang.Object r13 = r12.get(r11)
                org.telegram.ui.NotificationsSettingsActivity$NotificationException r13 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r13
                long r14 = r13.did
                boolean r14 = org.telegram.messenger.DialogObject.isEncryptedDialog(r14)
                if (r14 == 0) goto L_0x00b0
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r14 = r14.getMessagesController()
                long r3 = r13.did
                int r3 = org.telegram.messenger.DialogObject.getEncryptedChatId(r3)
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$EncryptedChat r3 = r14.getEncryptedChat(r3)
                if (r3 == 0) goto L_0x00ad
                org.telegram.ui.NotificationsCustomSettingsActivity r4 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                r16 = r6
                long r5 = r3.user_id
                java.lang.Long r3 = java.lang.Long.valueOf(r5)
                org.telegram.tgnet.TLRPC$User r3 = r4.getUser(r3)
                if (r3 == 0) goto L_0x0116
                java.lang.String r4 = r3.first_name
                java.lang.String r5 = r3.last_name
                java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r4, r5)
                r5 = 0
                r10[r5] = r4
                java.lang.String r3 = r3.username
                r4 = 1
                r10[r4] = r3
                goto L_0x0116
            L_0x00ad:
                r16 = r6
                goto L_0x0116
            L_0x00b0:
                r16 = r6
                long r3 = r13.did
                boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r3)
                if (r3 == 0) goto L_0x00eb
                org.telegram.ui.NotificationsCustomSettingsActivity r3 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                long r4 = r13.did
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
                if (r3 == 0) goto L_0x00e3
                boolean r4 = r3.deleted
                if (r4 == 0) goto L_0x00d1
                goto L_0x00e3
            L_0x00d1:
                java.lang.String r4 = r3.first_name
                java.lang.String r5 = r3.last_name
                java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r4, r5)
                r5 = 0
                r10[r5] = r4
                java.lang.String r4 = r3.username
                r5 = 1
                r10[r5] = r4
                r5 = 0
                goto L_0x0118
            L_0x00e3:
                r18 = r7
                r17 = r16
            L_0x00e7:
                r5 = 1
                r9 = 0
                goto L_0x01f2
            L_0x00eb:
                org.telegram.ui.NotificationsCustomSettingsActivity r3 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                long r4 = r13.did
                long r4 = -r4
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
                if (r3 == 0) goto L_0x0116
                boolean r4 = r3.left
                if (r4 != 0) goto L_0x00e3
                boolean r4 = r3.kicked
                if (r4 != 0) goto L_0x00e3
                org.telegram.tgnet.TLRPC$InputChannel r4 = r3.migrated_to
                if (r4 == 0) goto L_0x010b
                goto L_0x00e3
            L_0x010b:
                java.lang.String r4 = r3.title
                r5 = 0
                r10[r5] = r4
                java.lang.String r4 = r3.username
                r6 = 1
                r10[r6] = r4
                goto L_0x0118
            L_0x0116:
                r5 = 0
                r3 = 0
            L_0x0118:
                r4 = r10[r5]
                r6 = r10[r5]
                java.lang.String r6 = r6.toLowerCase()
                r10[r5] = r6
                org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
                r14 = r10[r5]
                java.lang.String r6 = r6.getTranslitString(r14)
                r14 = r10[r5]
                if (r14 == 0) goto L_0x0139
                r14 = r10[r5]
                boolean r14 = r14.equals(r6)
                if (r14 == 0) goto L_0x0139
                r6 = 0
            L_0x0139:
                r9 = r16
                r14 = 0
                r16 = 0
            L_0x013e:
                if (r14 >= r9) goto L_0x01ec
                r15 = r7[r14]
                r17 = r10[r5]
                r18 = r7
                java.lang.String r7 = " "
                if (r17 == 0) goto L_0x016c
                r17 = r9
                r9 = r10[r5]
                boolean r9 = r9.startsWith(r15)
                if (r9 != 0) goto L_0x018b
                r9 = r10[r5]
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r7)
                r5.append(r15)
                java.lang.String r5 = r5.toString()
                boolean r5 = r9.contains(r5)
                if (r5 != 0) goto L_0x018b
                goto L_0x016e
            L_0x016c:
                r17 = r9
            L_0x016e:
                if (r6 == 0) goto L_0x018e
                boolean r5 = r6.startsWith(r15)
                if (r5 != 0) goto L_0x018b
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r7)
                r5.append(r15)
                java.lang.String r5 = r5.toString()
                boolean r5 = r6.contains(r5)
                if (r5 == 0) goto L_0x018e
            L_0x018b:
                r5 = 1
                r7 = 1
                goto L_0x019f
            L_0x018e:
                r5 = 1
                r7 = r10[r5]
                if (r7 == 0) goto L_0x019d
                r7 = r10[r5]
                boolean r7 = r7.startsWith(r15)
                if (r7 == 0) goto L_0x019d
                r7 = 2
                goto L_0x019f
            L_0x019d:
                r7 = r16
            L_0x019f:
                if (r7 == 0) goto L_0x01e0
                if (r7 != r5) goto L_0x01ad
                r6 = 0
                java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r6, r15)
                r8.add(r4)
                r9 = 0
                goto L_0x01d7
            L_0x01ad:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r6 = "@"
                r4.append(r6)
                r7 = r10[r5]
                r4.append(r7)
                java.lang.String r4 = r4.toString()
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r7.append(r6)
                r7.append(r15)
                java.lang.String r6 = r7.toString()
                r9 = 0
                java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r9, r6)
                r8.add(r4)
            L_0x01d7:
                r2.add(r13)
                if (r3 == 0) goto L_0x01f2
                r1.add(r3)
                goto L_0x01f2
            L_0x01e0:
                r9 = 0
                int r14 = r14 + 1
                r16 = r7
                r9 = r17
                r7 = r18
                r5 = 0
                goto L_0x013e
            L_0x01ec:
                r18 = r7
                r17 = r9
                goto L_0x00e7
            L_0x01f2:
                int r11 = r11 + 1
                r6 = r17
                r7 = r18
                r3 = 1
                r5 = 0
                r9 = 2
                goto L_0x005b
            L_0x01fd:
                r0.updateSearchResults(r1, r2, r8)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.SearchAdapter.lambda$processSearch$2(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<Object> arrayList, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2, ArrayList<CharSequence> arrayList3) {
            AndroidUtilities.runOnUIThread(new NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda3(this, arrayList2, arrayList3, arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$4(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            if (NotificationsCustomSettingsActivity.this.searching) {
                this.searchRunnable = null;
                this.searchResult = arrayList;
                this.searchResultNames = arrayList2;
                this.searchAdapterHelper.mergeResults(arrayList3);
                if (NotificationsCustomSettingsActivity.this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                    NotificationsCustomSettingsActivity.this.emptyView.showTextView();
                }
                notifyDataSetChanged();
            }
        }

        public Object getObject(int i) {
            if (i >= 0 && i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            int size = i - (this.searchResult.size() + 1);
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            if (size < 0 || size >= globalSearch.size()) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(size);
        }

        public int getItemCount() {
            int size = this.searchResult.size();
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            return !globalSearch.isEmpty() ? size + globalSearch.size() + 1 : size;
        }

        /* JADX WARNING: type inference failed for: r7v3, types: [org.telegram.ui.Cells.GraySectionCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r7, int r8) {
            /*
                r6 = this;
                if (r8 == 0) goto L_0x000a
                org.telegram.ui.Cells.GraySectionCell r7 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r8 = r6.mContext
                r7.<init>(r8)
                goto L_0x001f
            L_0x000a:
                org.telegram.ui.Cells.UserCell r7 = new org.telegram.ui.Cells.UserCell
                android.content.Context r1 = r6.mContext
                r2 = 4
                r3 = 0
                r4 = 0
                r5 = 1
                r0 = r7
                r0.<init>(r1, r2, r3, r4, r5)
                java.lang.String r8 = "windowBackgroundWhite"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r7.setBackgroundColor(r8)
            L_0x001f:
                org.telegram.ui.Components.RecyclerListView$Holder r8 = new org.telegram.ui.Components.RecyclerListView$Holder
                r8.<init>(r7)
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                if (i < this.searchResult.size()) {
                    NotificationsSettingsActivity.NotificationException notificationException = this.searchResult.get(i);
                    CharSequence charSequence = this.searchResultNames.get(i);
                    if (i == this.searchResult.size() - 1) {
                        z = false;
                    }
                    userCell.setException(notificationException, charSequence, z);
                    userCell.setAddButtonVisible(false);
                    return;
                }
                int size = i - (this.searchResult.size() + 1);
                ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
                userCell.setData(globalSearch.get(size), (CharSequence) null, LocaleController.getString("NotificationsOn", NUM), 0, size != globalSearch.size() - 1);
                userCell.setAddButtonVisible(true);
            } else if (itemViewType == 1) {
                ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("AddToExceptions", NUM));
            }
        }

        public int getItemViewType(int i) {
            return i == this.searchResult.size() ? 1 : 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 0 || itemViewType == 4) ? false : true;
        }

        public int getItemCount() {
            return NotificationsCustomSettingsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            switch (i) {
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r4v0 */
        /* JADX WARNING: type inference failed for: r4v2 */
        /* JADX WARNING: type inference failed for: r4v3, types: [int] */
        /* JADX WARNING: type inference failed for: r4v6 */
        /* JADX WARNING: type inference failed for: r4v7 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r13, int r14) {
            /*
                r12 = this;
                int r0 = r13.getItemViewType()
                r1 = 0
                r2 = -1
                r3 = 2
                r4 = 0
                r5 = 1
                switch(r0) {
                    case 0: goto L_0x03e5;
                    case 1: goto L_0x03a2;
                    case 2: goto L_0x037c;
                    case 3: goto L_0x032b;
                    case 4: goto L_0x02e0;
                    case 5: goto L_0x00f9;
                    case 6: goto L_0x0056;
                    case 7: goto L_0x000e;
                    default: goto L_0x000c;
                }
            L_0x000c:
                goto L_0x03fd
            L_0x000e:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextCell r13 = (org.telegram.ui.Cells.TextCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.exceptionsAddRow
                if (r14 != r0) goto L_0x003b
                r14 = 2131626635(0x7f0e0a8b, float:1.8880512E38)
                java.lang.String r0 = "NotificationsAddAnException"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r0 = 2131165249(0x7var_, float:1.794471E38)
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.exceptionsStartRow
                if (r1 == r2) goto L_0x002f
                r4 = 1
            L_0x002f:
                r13.setTextAndIcon((java.lang.String) r14, (int) r0, (boolean) r4)
                java.lang.String r14 = "windowBackgroundWhiteBlueIcon"
                java.lang.String r0 = "windowBackgroundWhiteBlueButton"
                r13.setColors(r14, r0)
                goto L_0x03fd
            L_0x003b:
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.deleteAllRow
                if (r14 != r0) goto L_0x03fd
                r14 = 2131626645(0x7f0e0a95, float:1.8880532E38)
                java.lang.String r0 = "NotificationsDeleteAllException"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r13.setText(r14, r4)
                java.lang.String r14 = "windowBackgroundWhiteRedText5"
                r13.setColors(r1, r14)
                goto L_0x03fd
            L_0x0056:
                android.view.View r13 = r13.itemView
                r6 = r13
                org.telegram.ui.Cells.NotificationsCheckCell r6 = (org.telegram.ui.Cells.NotificationsCheckCell) r6
                r6.setDrawLine(r4)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                org.telegram.ui.NotificationsCustomSettingsActivity r13 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                android.content.SharedPreferences r13 = r13.getNotificationsSettings()
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != r5) goto L_0x0082
                r14 = 2131626655(0x7f0e0a9f, float:1.8880552E38)
                java.lang.String r0 = "NotificationsForPrivateChats"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                java.lang.String r0 = "EnableAll2"
                int r13 = r13.getInt(r0, r4)
            L_0x0080:
                r7 = r14
                goto L_0x00aa
            L_0x0082:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x009a
                r14 = 2131626654(0x7f0e0a9e, float:1.888055E38)
                java.lang.String r0 = "NotificationsForGroups"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                java.lang.String r0 = "EnableGroup2"
                int r13 = r13.getInt(r0, r4)
                goto L_0x0080
            L_0x009a:
                r14 = 2131626652(0x7f0e0a9c, float:1.8880546E38)
                java.lang.String r0 = "NotificationsForChannels"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                java.lang.String r0 = "EnableChannel2"
                int r13 = r13.getInt(r0, r4)
                goto L_0x0080
            L_0x00aa:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.tgnet.ConnectionsManager r14 = r14.getConnectionsManager()
                int r14 = r14.getCurrentTime()
                if (r13 >= r14) goto L_0x00b8
                r9 = 1
                goto L_0x00b9
            L_0x00b8:
                r9 = 0
            L_0x00b9:
                if (r9 == 0) goto L_0x00c9
                r13 = 2131626668(0x7f0e0aac, float:1.8880579E38)
                java.lang.String r14 = "NotificationsOn"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r8.append(r13)
            L_0x00c7:
                r10 = 0
                goto L_0x00f3
            L_0x00c9:
                r0 = 31536000(0x1e13380, float:8.2725845E-38)
                int r0 = r13 - r0
                if (r0 < r14) goto L_0x00dd
                r13 = 2131626666(0x7f0e0aaa, float:1.8880575E38)
                java.lang.String r14 = "NotificationsOff"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r8.append(r13)
                goto L_0x00c7
            L_0x00dd:
                r14 = 2131626667(0x7f0e0aab, float:1.8880577E38)
                java.lang.Object[] r0 = new java.lang.Object[r5]
                long r1 = (long) r13
                java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r1)
                r0[r4] = r13
                java.lang.String r13 = "NotificationsOffUntil"
                java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r13, r14, r0)
                r8.append(r13)
                r10 = 2
            L_0x00f3:
                r11 = 0
                r6.setTextAndValueAndCheck(r7, r8, r9, r10, r11)
                goto L_0x03fd
            L_0x00f9:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextSettingsCell r13 = (org.telegram.ui.Cells.TextSettingsCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                android.content.SharedPreferences r0 = r0.getNotificationsSettings()
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.messageSoundRow
                if (r14 != r1) goto L_0x015d
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                r1 = 2131627824(0x7f0e0var_, float:1.8882923E38)
                java.lang.String r2 = "SoundDefault"
                if (r14 != r5) goto L_0x0123
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r2, r1)
                java.lang.String r1 = "GlobalSound"
                java.lang.String r14 = r0.getString(r1, r14)
                goto L_0x0140
            L_0x0123:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x0136
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r2, r1)
                java.lang.String r1 = "GroupSound"
                java.lang.String r14 = r0.getString(r1, r14)
                goto L_0x0140
            L_0x0136:
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r2, r1)
                java.lang.String r1 = "ChannelSound"
                java.lang.String r14 = r0.getString(r1, r14)
            L_0x0140:
                java.lang.String r0 = "NoSound"
                boolean r1 = r14.equals(r0)
                if (r1 == 0) goto L_0x014f
                r14 = 2131626494(0x7f0e09fe, float:1.8880226E38)
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
            L_0x014f:
                r0 = 2131627823(0x7f0e0f2f, float:1.8882921E38)
                java.lang.String r1 = "Sound"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r0, r14, r5)
                goto L_0x03fd
            L_0x015d:
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.messageVibrateRow
                r2 = 4
                if (r14 != r1) goto L_0x01f4
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != r5) goto L_0x0175
                java.lang.String r14 = "vibrate_messages"
                int r14 = r0.getInt(r14, r4)
                goto L_0x018a
            L_0x0175:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x0184
                java.lang.String r14 = "vibrate_group"
                int r14 = r0.getInt(r14, r4)
                goto L_0x018a
            L_0x0184:
                java.lang.String r14 = "vibrate_channel"
                int r14 = r0.getInt(r14, r4)
            L_0x018a:
                r0 = 2131628267(0x7f0e10eb, float:1.8883822E38)
                java.lang.String r1 = "Vibrate"
                if (r14 != 0) goto L_0x01a3
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131628268(0x7f0e10ec, float:1.8883824E38)
                java.lang.String r1 = "VibrationDefault"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x03fd
            L_0x01a3:
                if (r14 != r5) goto L_0x01b7
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131627772(0x7f0e0efc, float:1.8882818E38)
                java.lang.String r1 = "Short"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x03fd
            L_0x01b7:
                if (r14 != r3) goto L_0x01cb
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131628269(0x7f0e10ed, float:1.8883826E38)
                java.lang.String r1 = "VibrationDisabled"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x03fd
            L_0x01cb:
                r3 = 3
                if (r14 != r3) goto L_0x01e0
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626160(0x7f0e08b0, float:1.8879548E38)
                java.lang.String r1 = "Long"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x03fd
            L_0x01e0:
                if (r14 != r2) goto L_0x03fd
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626700(0x7f0e0acc, float:1.8880644E38)
                java.lang.String r1 = "OnlyIfSilent"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x03fd
            L_0x01f4:
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.messagePriorityRow
                if (r14 != r1) goto L_0x0279
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != r5) goto L_0x020b
                java.lang.String r14 = "priority_messages"
                int r14 = r0.getInt(r14, r5)
                goto L_0x0220
            L_0x020b:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x021a
                java.lang.String r14 = "priority_group"
                int r14 = r0.getInt(r14, r5)
                goto L_0x0220
            L_0x021a:
                java.lang.String r14 = "priority_channel"
                int r14 = r0.getInt(r14, r5)
            L_0x0220:
                r0 = 2131626657(0x7f0e0aa1, float:1.8880556E38)
                java.lang.String r1 = "NotificationsImportance"
                if (r14 != 0) goto L_0x0239
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626670(0x7f0e0aae, float:1.8880583E38)
                java.lang.String r1 = "NotificationsPriorityHigh"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x03fd
            L_0x0239:
                if (r14 == r5) goto L_0x0267
                if (r14 != r3) goto L_0x023e
                goto L_0x0267
            L_0x023e:
                if (r14 != r2) goto L_0x0252
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626671(0x7f0e0aaf, float:1.8880585E38)
                java.lang.String r1 = "NotificationsPriorityLow"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x03fd
            L_0x0252:
                r2 = 5
                if (r14 != r2) goto L_0x03fd
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626672(0x7f0e0ab0, float:1.8880587E38)
                java.lang.String r1 = "NotificationsPriorityMedium"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x03fd
            L_0x0267:
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626674(0x7f0e0ab2, float:1.888059E38)
                java.lang.String r1 = "NotificationsPriorityUrgent"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x03fd
            L_0x0279:
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.messagePopupNotificationRow
                if (r14 != r1) goto L_0x03fd
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != r5) goto L_0x0290
                java.lang.String r14 = "popupAll"
                int r14 = r0.getInt(r14, r4)
                goto L_0x02a5
            L_0x0290:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x029f
                java.lang.String r14 = "popupGroup"
                int r14 = r0.getInt(r14, r4)
                goto L_0x02a5
            L_0x029f:
                java.lang.String r14 = "popupChannel"
                int r14 = r0.getInt(r14, r4)
            L_0x02a5:
                if (r14 != 0) goto L_0x02b1
                r14 = 2131626476(0x7f0e09ec, float:1.888019E38)
                java.lang.String r0 = "NoPopup"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x02d2
            L_0x02b1:
                if (r14 != r5) goto L_0x02bd
                r14 = 2131626702(0x7f0e0ace, float:1.8880648E38)
                java.lang.String r0 = "OnlyWhenScreenOn"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x02d2
            L_0x02bd:
                if (r14 != r3) goto L_0x02c9
                r14 = 2131626701(0x7f0e0acd, float:1.8880646E38)
                java.lang.String r0 = "OnlyWhenScreenOff"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x02d2
            L_0x02c9:
                r14 = 2131624260(0x7f0e0144, float:1.8875695E38)
                java.lang.String r0 = "AlwaysShowPopup"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
            L_0x02d2:
                r0 = 2131627179(0x7f0e0cab, float:1.8881615E38)
                java.lang.String r1 = "PopupNotification"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r0, r14, r5)
                goto L_0x03fd
            L_0x02e0:
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.deleteAllSectionRow
                java.lang.String r1 = "windowBackgroundGrayShadow"
                if (r14 == r0) goto L_0x031b
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.groupSection2Row
                if (r14 != r0) goto L_0x02fa
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.exceptionsSection2Row
                if (r0 == r2) goto L_0x031b
            L_0x02fa:
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.exceptionsSection2Row
                if (r14 != r0) goto L_0x030b
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.deleteAllRow
                if (r14 != r2) goto L_0x030b
                goto L_0x031b
            L_0x030b:
                android.view.View r13 = r13.itemView
                android.content.Context r14 = r12.mContext
                r0 = 2131165450(0x7var_a, float:1.7945117E38)
                android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r14, (int) r0, (java.lang.String) r1)
                r13.setBackgroundDrawable(r14)
                goto L_0x03fd
            L_0x031b:
                android.view.View r13 = r13.itemView
                android.content.Context r14 = r12.mContext
                r0 = 2131165451(0x7var_b, float:1.794512E38)
                android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r14, (int) r0, (java.lang.String) r1)
                r13.setBackgroundDrawable(r14)
                goto L_0x03fd
            L_0x032b:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextColorCell r13 = (org.telegram.ui.Cells.TextColorCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                android.content.SharedPreferences r14 = r14.getNotificationsSettings()
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.currentType
                r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
                if (r0 != r5) goto L_0x0347
                java.lang.String r0 = "MessagesLed"
                int r14 = r14.getInt(r0, r1)
                goto L_0x035c
            L_0x0347:
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.currentType
                if (r0 != 0) goto L_0x0356
                java.lang.String r0 = "GroupLed"
                int r14 = r14.getInt(r0, r1)
                goto L_0x035c
            L_0x0356:
                java.lang.String r0 = "ChannelLed"
                int r14 = r14.getInt(r0, r1)
            L_0x035c:
                r0 = 9
                if (r4 >= r0) goto L_0x036e
                int[] r0 = org.telegram.ui.Cells.TextColorCell.colorsToSave
                r0 = r0[r4]
                if (r0 != r14) goto L_0x036b
                int[] r14 = org.telegram.ui.Cells.TextColorCell.colors
                r14 = r14[r4]
                goto L_0x036e
            L_0x036b:
                int r4 = r4 + 1
                goto L_0x035c
            L_0x036e:
                r0 = 2131626081(0x7f0e0861, float:1.8879388E38)
                java.lang.String r1 = "LedColor"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndColor(r0, r14, r5)
                goto L_0x03fd
            L_0x037c:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.UserCell r13 = (org.telegram.ui.Cells.UserCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                java.util.ArrayList r0 = r0.exceptions
                org.telegram.ui.NotificationsCustomSettingsActivity r2 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r2 = r2.exceptionsStartRow
                int r2 = r14 - r2
                java.lang.Object r0 = r0.get(r2)
                org.telegram.ui.NotificationsSettingsActivity$NotificationException r0 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r0
                org.telegram.ui.NotificationsCustomSettingsActivity r2 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r2 = r2.exceptionsEndRow
                int r2 = r2 - r5
                if (r14 == r2) goto L_0x039e
                r4 = 1
            L_0x039e:
                r13.setException(r0, r1, r4)
                goto L_0x03fd
            L_0x03a2:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextCheckCell r13 = (org.telegram.ui.Cells.TextCheckCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                android.content.SharedPreferences r0 = r0.getNotificationsSettings()
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.previewRow
                if (r14 != r1) goto L_0x03fd
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != r5) goto L_0x03c3
                java.lang.String r14 = "EnablePreviewAll"
                boolean r14 = r0.getBoolean(r14, r5)
                goto L_0x03d8
            L_0x03c3:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x03d2
                java.lang.String r14 = "EnablePreviewGroup"
                boolean r14 = r0.getBoolean(r14, r5)
                goto L_0x03d8
            L_0x03d2:
                java.lang.String r14 = "EnablePreviewChannel"
                boolean r14 = r0.getBoolean(r14, r5)
            L_0x03d8:
                r0 = 2131626290(0x7f0e0932, float:1.8879812E38)
                java.lang.String r1 = "MessagePreview"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndCheck(r0, r14, r5)
                goto L_0x03fd
            L_0x03e5:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.HeaderCell r13 = (org.telegram.ui.Cells.HeaderCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.messageSectionRow
                if (r14 != r0) goto L_0x03fd
                r14 = 2131627523(0x7f0e0e03, float:1.8882313E38)
                java.lang.String r0 = "SETTINGS"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r13.setText(r14)
            L_0x03fd:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (NotificationsCustomSettingsActivity.this.exceptions != null && NotificationsCustomSettingsActivity.this.exceptions.isEmpty()) {
                boolean isGlobalNotificationsEnabled = NotificationsCustomSettingsActivity.this.getNotificationsController().isGlobalNotificationsEnabled(NotificationsCustomSettingsActivity.this.currentType);
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 0) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (viewHolder.getAdapterPosition() == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                        headerCell.setEnabled(isGlobalNotificationsEnabled, (ArrayList<Animator>) null);
                    } else {
                        headerCell.setEnabled(true, (ArrayList<Animator>) null);
                    }
                } else if (itemViewType == 1) {
                    ((TextCheckCell) viewHolder.itemView).setEnabled(isGlobalNotificationsEnabled, (ArrayList<Animator>) null);
                } else if (itemViewType == 3) {
                    ((TextColorCell) viewHolder.itemView).setEnabled(isGlobalNotificationsEnabled, (ArrayList<Animator>) null);
                } else if (itemViewType == 5) {
                    ((TextSettingsCell) viewHolder.itemView).setEnabled(isGlobalNotificationsEnabled, (ArrayList<Animator>) null);
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                return 0;
            }
            if (i == NotificationsCustomSettingsActivity.this.previewRow) {
                return 1;
            }
            if (i >= NotificationsCustomSettingsActivity.this.exceptionsStartRow && i < NotificationsCustomSettingsActivity.this.exceptionsEndRow) {
                return 2;
            }
            if (i == NotificationsCustomSettingsActivity.this.messageLedRow) {
                return 3;
            }
            if (i == NotificationsCustomSettingsActivity.this.groupSection2Row || i == NotificationsCustomSettingsActivity.this.alertSection2Row || i == NotificationsCustomSettingsActivity.this.exceptionsSection2Row || i == NotificationsCustomSettingsActivity.this.deleteAllSectionRow) {
                return 4;
            }
            if (i == NotificationsCustomSettingsActivity.this.alertRow) {
                return 6;
            }
            return (i == NotificationsCustomSettingsActivity.this.exceptionsAddRow || i == NotificationsCustomSettingsActivity.this.deleteAllRow) ? 7 : 5;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        NotificationsCustomSettingsActivity$$ExternalSyntheticLambda9 notificationsCustomSettingsActivity$$ExternalSyntheticLambda9 = new NotificationsCustomSettingsActivity$$ExternalSyntheticLambda9(this);
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextColorCell.class, TextSettingsCell.class, UserCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        NotificationsCustomSettingsActivity$$ExternalSyntheticLambda9 notificationsCustomSettingsActivity$$ExternalSyntheticLambda92 = notificationsCustomSettingsActivity$$ExternalSyntheticLambda9;
        ThemeDescription themeDescription = r2;
        ThemeDescription themeDescription2 = new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) notificationsCustomSettingsActivity$$ExternalSyntheticLambda92, "windowBackgroundWhiteGrayText");
        arrayList.add(themeDescription);
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) notificationsCustomSettingsActivity$$ExternalSyntheticLambda92, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        NotificationsCustomSettingsActivity$$ExternalSyntheticLambda9 notificationsCustomSettingsActivity$$ExternalSyntheticLambda93 = notificationsCustomSettingsActivity$$ExternalSyntheticLambda9;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, notificationsCustomSettingsActivity$$ExternalSyntheticLambda93, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, notificationsCustomSettingsActivity$$ExternalSyntheticLambda93, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, notificationsCustomSettingsActivity$$ExternalSyntheticLambda93, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, notificationsCustomSettingsActivity$$ExternalSyntheticLambda93, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, notificationsCustomSettingsActivity$$ExternalSyntheticLambda93, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, notificationsCustomSettingsActivity$$ExternalSyntheticLambda93, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, notificationsCustomSettingsActivity$$ExternalSyntheticLambda93, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$12() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }
}

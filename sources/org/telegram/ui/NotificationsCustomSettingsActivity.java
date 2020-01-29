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
import android.util.SparseArray;
import android.view.View;
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
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;

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

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList) {
        this(i, arrayList, false);
    }

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList, boolean z) {
        this.rowCount = 0;
        this.exceptionsDict = new HashMap<>();
        this.currentType = i;
        this.exceptions = arrayList;
        int size = this.exceptions.size();
        for (int i2 = 0; i2 < size; i2++) {
            NotificationsSettingsActivity.NotificationException notificationException = this.exceptions.get(i2);
            this.exceptionsDict.put(Long.valueOf(notificationException.did), notificationException);
        }
        if (z) {
            loadExceptions();
        }
    }

    public boolean onFragmentCreate() {
        updateRows();
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
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", NUM));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                NotificationsCustomSettingsActivity.this.lambda$createView$9$NotificationsCustomSettingsActivity(view, i, f, f2);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && NotificationsCustomSettingsActivity.this.searching && NotificationsCustomSettingsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(NotificationsCustomSettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$9$NotificationsCustomSettingsActivity(View view, int i, float f, float f2) {
        NotificationsSettingsActivity.NotificationException notificationException;
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList;
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2;
        NotificationsSettingsActivity.NotificationException notificationException2;
        int i2;
        String str;
        boolean z;
        View view2 = view;
        int i3 = i;
        if (getParentActivity() != null) {
            boolean z2 = false;
            if (this.listView.getAdapter() == this.searchAdapter || (i3 >= this.exceptionsStartRow && i3 < this.exceptionsEndRow)) {
                RecyclerView.Adapter adapter2 = this.listView.getAdapter();
                SearchAdapter searchAdapter2 = this.searchAdapter;
                if (adapter2 == searchAdapter2) {
                    Object object = searchAdapter2.getObject(i3);
                    if (object instanceof NotificationsSettingsActivity.NotificationException) {
                        arrayList2 = this.searchAdapter.searchResult;
                        notificationException2 = (NotificationsSettingsActivity.NotificationException) object;
                    } else {
                        boolean z3 = object instanceof TLRPC.User;
                        if (z3) {
                            i2 = ((TLRPC.User) object).id;
                        } else {
                            i2 = -((TLRPC.Chat) object).id;
                        }
                        long j = (long) i2;
                        if (this.exceptionsDict.containsKey(Long.valueOf(j))) {
                            notificationException2 = this.exceptionsDict.get(Long.valueOf(j));
                        } else {
                            NotificationsSettingsActivity.NotificationException notificationException3 = new NotificationsSettingsActivity.NotificationException();
                            notificationException3.did = j;
                            if (z3) {
                                notificationException3.did = (long) ((TLRPC.User) object).id;
                            } else {
                                notificationException3.did = (long) (-((TLRPC.Chat) object).id);
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
                    int i4 = i3 - this.exceptionsStartRow;
                    if (i4 >= 0 && i4 < arrayList3.size()) {
                        arrayList = arrayList3;
                        notificationException = arrayList3.get(i4);
                    } else {
                        return;
                    }
                }
                if (notificationException != null) {
                    AlertsCreator.showCustomNotificationsDialog(this, notificationException.did, -1, (ArrayList<NotificationsSettingsActivity.NotificationException>) null, this.currentAccount, (MessagesStorage.IntCallback) null, new MessagesStorage.IntCallback(z2, arrayList, notificationException, i) {
                        private final /* synthetic */ boolean f$1;
                        private final /* synthetic */ ArrayList f$2;
                        private final /* synthetic */ NotificationsSettingsActivity.NotificationException f$3;
                        private final /* synthetic */ int f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run(int i) {
                            NotificationsCustomSettingsActivity.this.lambda$null$0$NotificationsCustomSettingsActivity(this.f$1, this.f$2, this.f$3, this.f$4, i);
                        }
                    });
                    return;
                }
                return;
            }
            if (i3 == this.exceptionsAddRow) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putBoolean("checkCanWrite", false);
                int i5 = this.currentType;
                if (i5 == 0) {
                    bundle.putInt("dialogsType", 6);
                } else if (i5 == 2) {
                    bundle.putInt("dialogsType", 5);
                } else {
                    bundle.putInt("dialogsType", 4);
                }
                DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                        NotificationsCustomSettingsActivity.this.lambda$null$2$NotificationsCustomSettingsActivity(dialogsActivity, arrayList, charSequence, z);
                    }
                });
                presentFragment(dialogsActivity);
            } else {
                Uri uri = null;
                if (i3 == this.deleteAllRow) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("NotificationsDeleteAllExceptionTitle", NUM));
                    builder.setMessage(LocaleController.getString("NotificationsDeleteAllExceptionAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            NotificationsCustomSettingsActivity.this.lambda$null$3$NotificationsCustomSettingsActivity(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                } else if (i3 == this.alertRow) {
                    boolean isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(this.currentType);
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view2;
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i3);
                    if (!isGlobalNotificationsEnabled) {
                        getNotificationsController().setGlobalNotificationsEnabled(this.currentType, 0);
                        notificationsCheckCell.setChecked(!isGlobalNotificationsEnabled);
                        if (findViewHolderForAdapterPosition != null) {
                            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i3);
                        }
                        checkRowsEnabled();
                    } else {
                        AlertsCreator.showCustomNotificationsDialog(this, 0, this.currentType, this.exceptions, this.currentAccount, new MessagesStorage.IntCallback(notificationsCheckCell, findViewHolderForAdapterPosition, i3) {
                            private final /* synthetic */ NotificationsCheckCell f$1;
                            private final /* synthetic */ RecyclerView.ViewHolder f$2;
                            private final /* synthetic */ int f$3;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void run(int i) {
                                NotificationsCustomSettingsActivity.this.lambda$null$4$NotificationsCustomSettingsActivity(this.f$1, this.f$2, this.f$3, i);
                            }
                        });
                    }
                    z2 = isGlobalNotificationsEnabled;
                } else if (i3 == this.previewRow) {
                    if (view.isEnabled()) {
                        SharedPreferences notificationsSettings = getNotificationsSettings();
                        SharedPreferences.Editor edit = notificationsSettings.edit();
                        int i6 = this.currentType;
                        if (i6 == 1) {
                            z = notificationsSettings.getBoolean("EnablePreviewAll", true);
                            edit.putBoolean("EnablePreviewAll", !z);
                        } else if (i6 == 0) {
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
                } else if (i3 == this.messageSoundRow) {
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
                            if (this.currentType == 1) {
                                str = notificationsSettings2.getString("GlobalSoundPath", path);
                            } else if (this.currentType == 0) {
                                str = notificationsSettings2.getString("GroupSoundPath", path);
                            } else {
                                str = notificationsSettings2.getString("ChannelSoundPath", path);
                            }
                            if (str != null && !str.equals("NoSound")) {
                                uri = str.equals(path) ? uri2 : Uri.parse(str);
                            }
                            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", uri);
                            startActivityForResult(intent, i3);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else {
                        return;
                    }
                } else if (i3 == this.messageLedRow) {
                    if (view.isEnabled()) {
                        showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), 0, this.currentType, new Runnable(i3) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                NotificationsCustomSettingsActivity.this.lambda$null$5$NotificationsCustomSettingsActivity(this.f$1);
                            }
                        }));
                    } else {
                        return;
                    }
                } else if (i3 == this.messagePopupNotificationRow) {
                    if (view.isEnabled()) {
                        showDialog(AlertsCreator.createPopupSelectDialog(getParentActivity(), this.currentType, new Runnable(i3) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                NotificationsCustomSettingsActivity.this.lambda$null$6$NotificationsCustomSettingsActivity(this.f$1);
                            }
                        }));
                    } else {
                        return;
                    }
                } else if (i3 == this.messageVibrateRow) {
                    if (view.isEnabled()) {
                        int i7 = this.currentType;
                        showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0, i7 == 1 ? "vibrate_messages" : i7 == 0 ? "vibrate_group" : "vibrate_channel", new Runnable(i3) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                NotificationsCustomSettingsActivity.this.lambda$null$7$NotificationsCustomSettingsActivity(this.f$1);
                            }
                        }));
                    } else {
                        return;
                    }
                } else if (i3 == this.messagePriorityRow) {
                    if (view.isEnabled()) {
                        showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), 0, this.currentType, new Runnable(i3) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                NotificationsCustomSettingsActivity.this.lambda$null$8$NotificationsCustomSettingsActivity(this.f$1);
                            }
                        }));
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

    public /* synthetic */ void lambda$null$0$NotificationsCustomSettingsActivity(boolean z, ArrayList arrayList, NotificationsSettingsActivity.NotificationException notificationException, int i, int i2) {
        int indexOf;
        if (i2 != 0) {
            SharedPreferences notificationsSettings = getNotificationsSettings();
            notificationException.hasCustom = notificationsSettings.getBoolean("custom_" + notificationException.did, false);
            notificationException.notify = notificationsSettings.getInt("notify2_" + notificationException.did, 0);
            if (notificationException.notify != 0) {
                int i3 = notificationsSettings.getInt("notifyuntil_" + notificationException.did, -1);
                if (i3 != -1) {
                    notificationException.muteUntil = i3;
                }
            }
            if (z) {
                this.exceptions.add(notificationException);
                this.exceptionsDict.put(Long.valueOf(notificationException.did), notificationException);
                updateRows();
                this.adapter.notifyDataSetChanged();
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
            if (this.exceptionsAddRow != -1 && arrayList.isEmpty() && arrayList == this.exceptions) {
                this.listView.getAdapter().notifyItemChanged(this.exceptionsAddRow);
                this.listView.getAdapter().notifyItemRemoved(this.deleteAllRow);
                this.listView.getAdapter().notifyItemRemoved(this.deleteAllSectionRow);
            }
            this.listView.getAdapter().notifyItemRemoved(i);
            updateRows();
            checkRowsEnabled();
            this.actionBar.closeSearchField();
        }
    }

    public /* synthetic */ void lambda$null$2$NotificationsCustomSettingsActivity(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putLong("dialog_id", ((Long) arrayList.get(0)).longValue());
        bundle.putBoolean("exception", true);
        ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(bundle);
        profileNotificationsActivity.setDelegate(new ProfileNotificationsActivity.ProfileNotificationsActivityDelegate() {
            public final void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException) {
                NotificationsCustomSettingsActivity.this.lambda$null$1$NotificationsCustomSettingsActivity(notificationException);
            }
        });
        presentFragment(profileNotificationsActivity, true);
    }

    public /* synthetic */ void lambda$null$1$NotificationsCustomSettingsActivity(NotificationsSettingsActivity.NotificationException notificationException) {
        this.exceptions.add(0, notificationException);
        updateRows();
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$3$NotificationsCustomSettingsActivity(DialogInterface dialogInterface, int i) {
        SharedPreferences.Editor edit = getNotificationsSettings().edit();
        int size = this.exceptions.size();
        for (int i2 = 0; i2 < size; i2++) {
            NotificationsSettingsActivity.NotificationException notificationException = this.exceptions.get(i2);
            SharedPreferences.Editor remove = edit.remove("notify2_" + notificationException.did);
            remove.remove("custom_" + notificationException.did);
            getMessagesStorage().setDialogFlags(notificationException.did, 0);
            TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(notificationException.did);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
        }
        edit.commit();
        int size2 = this.exceptions.size();
        for (int i3 = 0; i3 < size2; i3++) {
            getNotificationsController().updateServerNotificationsSettings(this.exceptions.get(i3).did, false);
        }
        this.exceptions.clear();
        this.exceptionsDict.clear();
        updateRows();
        getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$4$NotificationsCustomSettingsActivity(NotificationsCheckCell notificationsCheckCell, RecyclerView.ViewHolder viewHolder, int i, int i2) {
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

    public /* synthetic */ void lambda$null$5$NotificationsCustomSettingsActivity(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    public /* synthetic */ void lambda$null$6$NotificationsCustomSettingsActivity(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    public /* synthetic */ void lambda$null$7$NotificationsCustomSettingsActivity(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    public /* synthetic */ void lambda$null$8$NotificationsCustomSettingsActivity(int i) {
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
                this.animatorSet = new AnimatorSet();
                this.animatorSet.playTogether(arrayList);
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
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                NotificationsCustomSettingsActivity.this.lambda$loadExceptions$11$NotificationsCustomSettingsActivity();
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v37, resolved type: java.util.ArrayList} */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00fd, code lost:
        if (r8.deleted != false) goto L_0x0144;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0192, code lost:
        if (r3.deleted != false) goto L_0x01b1;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0267  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0282 A[LOOP:3: B:112:0x0280->B:113:0x0282, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x029a  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0226  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadExceptions$11$NotificationsCustomSettingsActivity() {
        /*
            r21 = this;
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
            org.telegram.messenger.UserConfig r10 = r21.getUserConfig()
            int r10 = r10.clientUserId
            android.content.SharedPreferences r11 = r21.getNotificationsSettings()
            java.util.Map r12 = r11.getAll()
            java.util.Set r13 = r12.entrySet()
            java.util.Iterator r13 = r13.iterator()
        L_0x0048:
            boolean r14 = r13.hasNext()
            if (r14 == 0) goto L_0x01bf
            java.lang.Object r14 = r13.next()
            java.util.Map$Entry r14 = (java.util.Map.Entry) r14
            java.lang.Object r16 = r14.getKey()
            r15 = r16
            java.lang.String r15 = (java.lang.String) r15
            r16 = r13
            java.lang.String r13 = "notify2_"
            boolean r17 = r15.startsWith(r13)
            if (r17 == 0) goto L_0x01a7
            r17 = r5
            java.lang.String r5 = ""
            java.lang.String r5 = r15.replace(r13, r5)
            java.lang.Long r13 = org.telegram.messenger.Utilities.parseLong(r5)
            r15 = r3
            r18 = r4
            long r3 = r13.longValue()
            r19 = 0
            int r13 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r13 == 0) goto L_0x01a0
            r13 = r7
            r19 = r8
            long r7 = (long) r10
            int r20 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r20 == 0) goto L_0x0199
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r7 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException
            r7.<init>()
            r7.did = r3
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r20 = r10
            java.lang.String r10 = "custom_"
            r8.append(r10)
            r8.append(r3)
            java.lang.String r8 = r8.toString()
            r10 = 0
            boolean r8 = r11.getBoolean(r8, r10)
            r7.hasCustom = r8
            java.lang.Object r8 = r14.getValue()
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r8 = r8.intValue()
            r7.notify = r8
            int r8 = r7.notify
            if (r8 == 0) goto L_0x00d7
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = "notifyuntil_"
            r8.append(r10)
            r8.append(r5)
            java.lang.String r5 = r8.toString()
            java.lang.Object r5 = r12.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 == 0) goto L_0x00d7
            int r5 = r5.intValue()
            r7.muteUntil = r5
        L_0x00d7:
            int r5 = (int) r3
            r14 = r11
            r8 = 32
            long r10 = r3 << r8
            int r8 = (int) r10
            if (r5 == 0) goto L_0x0151
            if (r5 <= 0) goto L_0x0105
            org.telegram.messenger.MessagesController r8 = r21.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r10)
            if (r8 != 0) goto L_0x00fb
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r0.add(r5)
            r1.put(r3, r7)
            goto L_0x0100
        L_0x00fb:
            boolean r3 = r8.deleted
            if (r3 == 0) goto L_0x0100
            goto L_0x0144
        L_0x0100:
            r6.add(r7)
            goto L_0x019c
        L_0x0105:
            org.telegram.messenger.MessagesController r8 = r21.getMessagesController()
            int r5 = -r5
            java.lang.Integer r10 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r10)
            if (r8 != 0) goto L_0x011f
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2.add(r5)
            r1.put(r3, r7)
            goto L_0x0144
        L_0x011f:
            boolean r3 = r8.left
            if (r3 != 0) goto L_0x0144
            boolean r3 = r8.kicked
            if (r3 != 0) goto L_0x0144
            org.telegram.tgnet.TLRPC$InputChannel r3 = r8.migrated_to
            if (r3 == 0) goto L_0x012c
            goto L_0x0144
        L_0x012c:
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r3 == 0) goto L_0x013d
            boolean r3 = r8.megagroup
            if (r3 != 0) goto L_0x013d
            r11 = r19
            r11.add(r7)
            goto L_0x01a5
        L_0x013d:
            r11 = r19
            r13.add(r7)
            goto L_0x01a5
        L_0x0144:
            r7 = r13
            r11 = r14
            r3 = r15
            r13 = r16
            r5 = r17
            r4 = r18
            r8 = r19
            goto L_0x01bb
        L_0x0151:
            r11 = r19
            if (r8 == 0) goto L_0x01a5
            org.telegram.messenger.MessagesController r5 = r21.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$EncryptedChat r5 = r5.getEncryptedChat(r10)
            if (r5 != 0) goto L_0x016f
            java.lang.Integer r5 = java.lang.Integer.valueOf(r8)
            r8 = r15
            r8.add(r5)
            r1.put(r3, r7)
            goto L_0x0195
        L_0x016f:
            r8 = r15
            org.telegram.messenger.MessagesController r3 = r21.getMessagesController()
            int r4 = r5.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 != 0) goto L_0x0190
            int r3 = r5.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r0.add(r3)
            int r3 = r5.user_id
            long r3 = (long) r3
            r1.put(r3, r7)
            goto L_0x0195
        L_0x0190:
            boolean r3 = r3.deleted
            if (r3 == 0) goto L_0x0195
            goto L_0x01b1
        L_0x0195:
            r6.add(r7)
            goto L_0x01b1
        L_0x0199:
            r20 = r10
            r14 = r11
        L_0x019c:
            r8 = r15
            r11 = r19
            goto L_0x01b1
        L_0x01a0:
            r13 = r7
            r20 = r10
            r14 = r11
            r11 = r8
        L_0x01a5:
            r8 = r15
            goto L_0x01b1
        L_0x01a7:
            r18 = r4
            r17 = r5
            r13 = r7
            r20 = r10
            r14 = r11
            r11 = r8
            r8 = r3
        L_0x01b1:
            r3 = r8
            r8 = r11
            r7 = r13
            r11 = r14
            r13 = r16
            r5 = r17
            r4 = r18
        L_0x01bb:
            r10 = r20
            goto L_0x0048
        L_0x01bf:
            r18 = r4
            r17 = r5
            r13 = r7
            r11 = r8
            r10 = 0
            r8 = r3
            int r3 = r1.size()
            if (r3 == 0) goto L_0x02ba
            boolean r3 = r8.isEmpty()     // Catch:{ Exception -> 0x0217 }
            java.lang.String r4 = ","
            if (r3 != 0) goto L_0x01e0
            org.telegram.messenger.MessagesStorage r3 = r21.getMessagesStorage()     // Catch:{ Exception -> 0x0217 }
            java.lang.String r5 = android.text.TextUtils.join(r4, r8)     // Catch:{ Exception -> 0x0217 }
            r3.getEncryptedChatsInternal(r5, r9, r0)     // Catch:{ Exception -> 0x0217 }
        L_0x01e0:
            boolean r3 = r0.isEmpty()     // Catch:{ Exception -> 0x0217 }
            if (r3 != 0) goto L_0x01f8
            org.telegram.messenger.MessagesStorage r3 = r21.getMessagesStorage()     // Catch:{ Exception -> 0x01f4 }
            java.lang.String r0 = android.text.TextUtils.join(r4, r0)     // Catch:{ Exception -> 0x01f4 }
            r5 = r18
            r3.getUsersInternal(r0, r5)     // Catch:{ Exception -> 0x0213 }
            goto L_0x01fa
        L_0x01f4:
            r0 = move-exception
            r5 = r18
            goto L_0x0214
        L_0x01f8:
            r5 = r18
        L_0x01fa:
            boolean r0 = r2.isEmpty()     // Catch:{ Exception -> 0x0213 }
            if (r0 != 0) goto L_0x0210
            org.telegram.messenger.MessagesStorage r0 = r21.getMessagesStorage()     // Catch:{ Exception -> 0x0213 }
            java.lang.String r2 = android.text.TextUtils.join(r4, r2)     // Catch:{ Exception -> 0x0213 }
            r4 = r17
            r0.getChatsInternal(r2, r4)     // Catch:{ Exception -> 0x020e }
            goto L_0x021f
        L_0x020e:
            r0 = move-exception
            goto L_0x021c
        L_0x0210:
            r4 = r17
            goto L_0x021f
        L_0x0213:
            r0 = move-exception
        L_0x0214:
            r4 = r17
            goto L_0x021c
        L_0x0217:
            r0 = move-exception
            r4 = r17
            r5 = r18
        L_0x021c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021f:
            int r0 = r4.size()
            r2 = 0
        L_0x0224:
            if (r2 >= r0) goto L_0x0260
            java.lang.Object r3 = r4.get(r2)
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC.Chat) r3
            boolean r7 = r3.left
            if (r7 != 0) goto L_0x025d
            boolean r7 = r3.kicked
            if (r7 != 0) goto L_0x025d
            org.telegram.tgnet.TLRPC$InputChannel r7 = r3.migrated_to
            if (r7 == 0) goto L_0x0239
            goto L_0x025d
        L_0x0239:
            int r7 = r3.id
            int r7 = -r7
            long r7 = (long) r7
            java.lang.Object r7 = r1.get(r7)
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r7 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r7
            int r8 = r3.id
            int r8 = -r8
            long r14 = (long) r8
            r1.remove(r14)
            if (r7 == 0) goto L_0x025d
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r8 == 0) goto L_0x025a
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x025a
            r11.add(r7)
            goto L_0x025d
        L_0x025a:
            r13.add(r7)
        L_0x025d:
            int r2 = r2 + 1
            goto L_0x0224
        L_0x0260:
            int r0 = r5.size()
            r2 = 0
        L_0x0265:
            if (r2 >= r0) goto L_0x027b
            java.lang.Object r3 = r5.get(r2)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC.User) r3
            boolean r7 = r3.deleted
            if (r7 == 0) goto L_0x0272
            goto L_0x0278
        L_0x0272:
            int r3 = r3.id
            long r7 = (long) r3
            r1.remove(r7)
        L_0x0278:
            int r2 = r2 + 1
            goto L_0x0265
        L_0x027b:
            int r0 = r9.size()
            r2 = 0
        L_0x0280:
            if (r2 >= r0) goto L_0x0294
            java.lang.Object r3 = r9.get(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = (org.telegram.tgnet.TLRPC.EncryptedChat) r3
            int r3 = r3.id
            long r7 = (long) r3
            r3 = 32
            long r7 = r7 << r3
            r1.remove(r7)
            int r2 = r2 + 1
            goto L_0x0280
        L_0x0294:
            int r0 = r1.size()
        L_0x0298:
            if (r10 >= r0) goto L_0x02be
            long r2 = r1.keyAt(r10)
            int r3 = (int) r2
            if (r3 >= 0) goto L_0x02b0
            java.lang.Object r2 = r1.valueAt(r10)
            r13.remove(r2)
            java.lang.Object r2 = r1.valueAt(r10)
            r11.remove(r2)
            goto L_0x02b7
        L_0x02b0:
            java.lang.Object r2 = r1.valueAt(r10)
            r6.remove(r2)
        L_0x02b7:
            int r10 = r10 + 1
            goto L_0x0298
        L_0x02ba:
            r4 = r17
            r5 = r18
        L_0x02be:
            org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$jIvwOL310iFc7BZAadFlNMU4qZs r0 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$jIvwOL310iFc7BZAadFlNMU4qZs
            r1 = r0
            r2 = r21
            r3 = r5
            r5 = r9
            r7 = r13
            r8 = r11
            r1.<init>(r3, r4, r5, r6, r7, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.lambda$loadExceptions$11$NotificationsCustomSettingsActivity():void");
    }

    public /* synthetic */ void lambda$null$10$NotificationsCustomSettingsActivity(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6) {
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
        updateRows();
        this.adapter.notifyDataSetChanged();
    }

    private void updateRows() {
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList;
        this.rowCount = 0;
        int i = this.currentType;
        if (i != -1) {
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.alertRow = i2;
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.alertSection2Row = i3;
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.messageSectionRow = i4;
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.previewRow = i5;
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.messageLedRow = i6;
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.messageVibrateRow = i7;
            if (i == 2) {
                this.messagePopupNotificationRow = -1;
            } else {
                int i8 = this.rowCount;
                this.rowCount = i8 + 1;
                this.messagePopupNotificationRow = i8;
            }
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.messageSoundRow = i9;
            if (Build.VERSION.SDK_INT >= 21) {
                int i10 = this.rowCount;
                this.rowCount = i10 + 1;
                this.messagePriorityRow = i10;
            } else {
                this.messagePriorityRow = -1;
            }
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.groupSection2Row = i11;
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.exceptionsAddRow = i12;
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
            int i13 = this.rowCount;
            this.exceptionsStartRow = i13;
            this.rowCount = i13 + this.exceptions.size();
            this.exceptionsEndRow = this.rowCount;
        }
        if (this.currentType != -1 || ((arrayList = this.exceptions) != null && !arrayList.isEmpty())) {
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.exceptionsSection2Row = i14;
        } else {
            this.exceptionsSection2Row = -1;
        }
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList3 = this.exceptions;
        if (arrayList3 == null || arrayList3.isEmpty()) {
            this.deleteAllRow = -1;
            this.deleteAllSectionRow = -1;
            return;
        }
        int i15 = this.rowCount;
        this.rowCount = i15 + 1;
        this.deleteAllRow = i15;
        int i16 = this.rowCount;
        this.rowCount = i16 + 1;
        this.deleteAllSectionRow = i16;
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
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                public /* synthetic */ SparseArray<TLRPC.User> getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public final void onDataSetChanged() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$new$0$NotificationsCustomSettingsActivity$SearchAdapter();
                }

                public /* synthetic */ void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$NotificationsCustomSettingsActivity$SearchAdapter() {
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
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, 0, false, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$tfF2DKTrRdUn7ry8GPc_cT9UHcw r1 = new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$searchDialogs$1$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchDialogs$1$NotificationsCustomSettingsActivity$SearchAdapter(String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$processSearch$3$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$3$NotificationsCustomSettingsActivity$SearchAdapter(String str) {
            this.searchAdapterHelper.queryServerSearch(str, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, 0, false, 0);
            Utilities.searchQueue.postRunnable(new Runnable(str, new ArrayList(NotificationsCustomSettingsActivity.this.exceptions)) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x0144, code lost:
            if (r9[r5].contains(" " + r4) == false) goto L_0x0149;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:61:0x0164, code lost:
            if (r8.contains(" " + r4) != false) goto L_0x0166;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x011d  */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x01bb A[LOOP:1: B:48:0x011a->B:76:0x01bb, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x017c A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x00c0 A[EDGE_INSN: B:85:0x00c0->B:35:0x00c0 ?: BREAK  , SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String r19, java.util.ArrayList r20) {
            /*
                r18 = this;
                r0 = r18
                java.lang.String r1 = r19.trim()
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
                java.lang.String[] r6 = new java.lang.String[r6]
                r6[r5] = r1
                if (r2 == 0) goto L_0x0048
                r6[r3] = r2
            L_0x0048:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                r8 = 2
                java.lang.String[] r9 = new java.lang.String[r8]
                r10 = 0
            L_0x005b:
                int r11 = r20.size()
                if (r10 >= r11) goto L_0x01cd
                r11 = r20
                java.lang.Object r12 = r11.get(r10)
                org.telegram.ui.NotificationsSettingsActivity$NotificationException r12 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r12
                long r13 = r12.did
                int r15 = (int) r13
                r16 = 32
                long r13 = r13 >> r16
                int r14 = (int) r13
                if (r15 == 0) goto L_0x00c5
                if (r15 <= 0) goto L_0x0099
                org.telegram.ui.NotificationsCustomSettingsActivity r13 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r13 = r13.getMessagesController()
                java.lang.Integer r14 = java.lang.Integer.valueOf(r15)
                org.telegram.tgnet.TLRPC$User r13 = r13.getUser(r14)
                boolean r14 = r13.deleted
                if (r14 == 0) goto L_0x0088
                goto L_0x00c0
            L_0x0088:
                if (r13 == 0) goto L_0x00f5
                java.lang.String r14 = r13.first_name
                java.lang.String r15 = r13.last_name
                java.lang.String r14 = org.telegram.messenger.ContactsController.formatName(r14, r15)
                r9[r5] = r14
                java.lang.String r14 = r13.username
                r9[r3] = r14
                goto L_0x00f6
            L_0x0099:
                org.telegram.ui.NotificationsCustomSettingsActivity r13 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r13 = r13.getMessagesController()
                int r14 = -r15
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                org.telegram.tgnet.TLRPC$Chat r13 = r13.getChat(r14)
                if (r13 == 0) goto L_0x00f5
                boolean r14 = r13.left
                if (r14 != 0) goto L_0x00c0
                boolean r14 = r13.kicked
                if (r14 != 0) goto L_0x00c0
                org.telegram.tgnet.TLRPC$InputChannel r14 = r13.migrated_to
                if (r14 == 0) goto L_0x00b7
                goto L_0x00c0
            L_0x00b7:
                java.lang.String r14 = r13.title
                r9[r5] = r14
                java.lang.String r14 = r13.username
                r9[r3] = r14
                goto L_0x00f6
            L_0x00c0:
                r17 = r6
                r6 = 0
                goto L_0x01c5
            L_0x00c5:
                org.telegram.ui.NotificationsCustomSettingsActivity r13 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r13 = r13.getMessagesController()
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                org.telegram.tgnet.TLRPC$EncryptedChat r13 = r13.getEncryptedChat(r14)
                if (r13 == 0) goto L_0x00f5
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r14 = r14.getMessagesController()
                int r13 = r13.user_id
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                org.telegram.tgnet.TLRPC$User r13 = r14.getUser(r13)
                if (r13 == 0) goto L_0x00f5
                java.lang.String r14 = r13.first_name
                java.lang.String r15 = r13.last_name
                java.lang.String r14 = org.telegram.messenger.ContactsController.formatName(r14, r15)
                r9[r5] = r14
                java.lang.String r13 = r13.username
                r9[r3] = r13
            L_0x00f5:
                r13 = 0
            L_0x00f6:
                r14 = r9[r5]
                r15 = r9[r5]
                java.lang.String r15 = r15.toLowerCase()
                r9[r5] = r15
                org.telegram.messenger.LocaleController r15 = org.telegram.messenger.LocaleController.getInstance()
                r8 = r9[r5]
                java.lang.String r8 = r15.getTranslitString(r8)
                r15 = r9[r5]
                if (r15 == 0) goto L_0x0117
                r15 = r9[r5]
                boolean r15 = r15.equals(r8)
                if (r15 == 0) goto L_0x0117
                r8 = 0
            L_0x0117:
                r15 = 0
                r16 = 0
            L_0x011a:
                int r4 = r6.length
                if (r15 >= r4) goto L_0x00c0
                r4 = r6[r15]
                r17 = r9[r5]
                java.lang.String r3 = " "
                if (r17 == 0) goto L_0x0147
                r17 = r6
                r6 = r9[r5]
                boolean r6 = r6.startsWith(r4)
                if (r6 != 0) goto L_0x0166
                r6 = r9[r5]
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r3)
                r5.append(r4)
                java.lang.String r5 = r5.toString()
                boolean r5 = r6.contains(r5)
                if (r5 != 0) goto L_0x0166
                goto L_0x0149
            L_0x0147:
                r17 = r6
            L_0x0149:
                if (r8 == 0) goto L_0x0169
                boolean r5 = r8.startsWith(r4)
                if (r5 != 0) goto L_0x0166
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r3)
                r5.append(r4)
                java.lang.String r3 = r5.toString()
                boolean r3 = r8.contains(r3)
                if (r3 == 0) goto L_0x0169
            L_0x0166:
                r3 = 1
                r5 = 1
                goto L_0x017a
            L_0x0169:
                r3 = 1
                r5 = r9[r3]
                if (r5 == 0) goto L_0x0178
                r5 = r9[r3]
                boolean r5 = r5.startsWith(r4)
                if (r5 == 0) goto L_0x0178
                r5 = 2
                goto L_0x017a
            L_0x0178:
                r5 = r16
            L_0x017a:
                if (r5 == 0) goto L_0x01bb
                if (r5 != r3) goto L_0x0188
                r5 = 0
                java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r14, r5, r4)
                r7.add(r4)
                r6 = 0
                goto L_0x01b2
            L_0x0188:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "@"
                r5.append(r6)
                r8 = r9[r3]
                r5.append(r8)
                java.lang.String r5 = r5.toString()
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r6)
                r8.append(r4)
                java.lang.String r4 = r8.toString()
                r6 = 0
                java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r6, r4)
                r7.add(r4)
            L_0x01b2:
                r2.add(r12)
                if (r13 == 0) goto L_0x01c5
                r1.add(r13)
                goto L_0x01c5
            L_0x01bb:
                r6 = 0
                int r15 = r15 + 1
                r16 = r5
                r6 = r17
                r5 = 0
                goto L_0x011a
            L_0x01c5:
                int r10 = r10 + 1
                r6 = r17
                r5 = 0
                r8 = 2
                goto L_0x005b
            L_0x01cd:
                r0.updateSearchResults(r1, r2, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.SearchAdapter.lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2, ArrayList<CharSequence> arrayList3) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList2, arrayList3, arrayList) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$updateSearchResults$4$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$4$NotificationsCustomSettingsActivity$SearchAdapter(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
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
                goto L_0x0020
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
            L_0x0020:
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.ui.Cells.TextCheckCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: org.telegram.ui.Cells.UserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: org.telegram.ui.Cells.TextColorCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v14, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: org.telegram.ui.Cells.NotificationsCheckCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: org.telegram.ui.Cells.TextCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                java.lang.String r4 = "windowBackgroundWhite"
                switch(r5) {
                    case 0: goto L_0x006a;
                    case 1: goto L_0x005b;
                    case 2: goto L_0x004a;
                    case 3: goto L_0x003b;
                    case 4: goto L_0x0033;
                    case 5: goto L_0x0024;
                    case 6: goto L_0x0015;
                    default: goto L_0x0006;
                }
            L_0x0006:
                org.telegram.ui.Cells.TextCell r5 = new org.telegram.ui.Cells.TextCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r5.setBackgroundColor(r4)
                goto L_0x0078
            L_0x0015:
                org.telegram.ui.Cells.NotificationsCheckCell r5 = new org.telegram.ui.Cells.NotificationsCheckCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r5.setBackgroundColor(r4)
                goto L_0x0078
            L_0x0024:
                org.telegram.ui.Cells.TextSettingsCell r5 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r5.setBackgroundColor(r4)
                goto L_0x0078
            L_0x0033:
                org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r4 = r3.mContext
                r5.<init>(r4)
                goto L_0x0078
            L_0x003b:
                org.telegram.ui.Cells.TextColorCell r5 = new org.telegram.ui.Cells.TextColorCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r5.setBackgroundColor(r4)
                goto L_0x0078
            L_0x004a:
                org.telegram.ui.Cells.UserCell r5 = new org.telegram.ui.Cells.UserCell
                android.content.Context r0 = r3.mContext
                r1 = 6
                r2 = 0
                r5.<init>(r0, r1, r2, r2)
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r5.setBackgroundColor(r4)
                goto L_0x0078
            L_0x005b:
                org.telegram.ui.Cells.TextCheckCell r5 = new org.telegram.ui.Cells.TextCheckCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r5.setBackgroundColor(r4)
                goto L_0x0078
            L_0x006a:
                org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r5.setBackgroundColor(r4)
            L_0x0078:
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r5)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
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
                    case 0: goto L_0x03ec;
                    case 1: goto L_0x03a9;
                    case 2: goto L_0x0383;
                    case 3: goto L_0x0332;
                    case 4: goto L_0x02e6;
                    case 5: goto L_0x00fc;
                    case 6: goto L_0x0059;
                    case 7: goto L_0x000e;
                    default: goto L_0x000c;
                }
            L_0x000c:
                goto L_0x0404
            L_0x000e:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextCell r13 = (org.telegram.ui.Cells.TextCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.exceptionsAddRow
                if (r14 != r0) goto L_0x003d
                r14 = 2131625783(0x7f0e0737, float:1.8878784E38)
                java.lang.String r0 = "NotificationsAddAnException"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r0 = 2131165248(0x7var_, float:1.7944708E38)
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.exceptionsStartRow
                if (r1 == r2) goto L_0x002f
                r4 = 1
            L_0x002f:
                r13.setTextAndIcon(r14, r0, r4)
                java.lang.String r14 = "windowBackgroundWhiteBlueIcon"
                java.lang.String r0 = "windowBackgroundWhiteBlueButton"
                r13.setColors(r14, r0)
                goto L_0x0404
            L_0x003d:
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.deleteAllRow
                if (r14 != r0) goto L_0x0404
                r14 = 2131625790(0x7f0e073e, float:1.8878798E38)
                java.lang.String r0 = "NotificationsDeleteAllException"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r13.setText(r14, r4)
                java.lang.String r14 = "windowBackgroundWhiteRedText5"
                r13.setColors(r1, r14)
                goto L_0x0404
            L_0x0059:
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
                if (r14 != r5) goto L_0x0085
                r14 = 2131625800(0x7f0e0748, float:1.8878818E38)
                java.lang.String r0 = "NotificationsForPrivateChats"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                java.lang.String r0 = "EnableAll2"
                int r13 = r13.getInt(r0, r4)
            L_0x0083:
                r7 = r14
                goto L_0x00ad
            L_0x0085:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x009d
                r14 = 2131625799(0x7f0e0747, float:1.8878816E38)
                java.lang.String r0 = "NotificationsForGroups"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                java.lang.String r0 = "EnableGroup2"
                int r13 = r13.getInt(r0, r4)
                goto L_0x0083
            L_0x009d:
                r14 = 2131625797(0x7f0e0745, float:1.8878812E38)
                java.lang.String r0 = "NotificationsForChannels"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                java.lang.String r0 = "EnableChannel2"
                int r13 = r13.getInt(r0, r4)
                goto L_0x0083
            L_0x00ad:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.tgnet.ConnectionsManager r14 = r14.getConnectionsManager()
                int r14 = r14.getCurrentTime()
                if (r13 >= r14) goto L_0x00bb
                r9 = 1
                goto L_0x00bc
            L_0x00bb:
                r9 = 0
            L_0x00bc:
                if (r9 == 0) goto L_0x00cc
                r13 = 2131625810(0x7f0e0752, float:1.8878838E38)
                java.lang.String r14 = "NotificationsOn"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r8.append(r13)
            L_0x00ca:
                r10 = 0
                goto L_0x00f6
            L_0x00cc:
                r0 = 31536000(0x1e13380, float:8.2725845E-38)
                int r0 = r13 - r0
                if (r0 < r14) goto L_0x00e0
                r13 = 2131625808(0x7f0e0750, float:1.8878834E38)
                java.lang.String r14 = "NotificationsOff"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r8.append(r13)
                goto L_0x00ca
            L_0x00e0:
                r14 = 2131625809(0x7f0e0751, float:1.8878836E38)
                java.lang.Object[] r0 = new java.lang.Object[r5]
                long r1 = (long) r13
                java.lang.String r13 = org.telegram.messenger.LocaleController.stringForMessageListDate(r1)
                r0[r4] = r13
                java.lang.String r13 = "NotificationsOffUntil"
                java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r13, r14, r0)
                r8.append(r13)
                r10 = 2
            L_0x00f6:
                r11 = 0
                r6.setTextAndValueAndCheck(r7, r8, r9, r10, r11)
                goto L_0x0404
            L_0x00fc:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextSettingsCell r13 = (org.telegram.ui.Cells.TextSettingsCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                android.content.SharedPreferences r0 = r0.getNotificationsSettings()
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.messageSoundRow
                if (r14 != r1) goto L_0x0160
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                r1 = 2131626613(0x7f0e0a75, float:1.8880467E38)
                java.lang.String r2 = "SoundDefault"
                if (r14 != r5) goto L_0x0126
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r2, r1)
                java.lang.String r1 = "GlobalSound"
                java.lang.String r14 = r0.getString(r1, r14)
                goto L_0x0143
            L_0x0126:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x0139
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r2, r1)
                java.lang.String r1 = "GroupSound"
                java.lang.String r14 = r0.getString(r1, r14)
                goto L_0x0143
            L_0x0139:
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r2, r1)
                java.lang.String r1 = "ChannelSound"
                java.lang.String r14 = r0.getString(r1, r14)
            L_0x0143:
                java.lang.String r0 = "NoSound"
                boolean r1 = r14.equals(r0)
                if (r1 == 0) goto L_0x0152
                r14 = 2131625673(0x7f0e06c9, float:1.887856E38)
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
            L_0x0152:
                r0 = 2131626612(0x7f0e0a74, float:1.8880465E38)
                java.lang.String r1 = "Sound"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r0, r14, r5)
                goto L_0x0404
            L_0x0160:
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.messageVibrateRow
                r2 = 4
                if (r14 != r1) goto L_0x01fa
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != r5) goto L_0x0179
                java.lang.String r14 = "vibrate_messages"
                int r14 = r0.getInt(r14, r4)
                goto L_0x0190
            L_0x0179:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x0189
                java.lang.String r14 = "vibrate_group"
                int r14 = r0.getInt(r14, r4)
                goto L_0x0190
            L_0x0189:
                java.lang.String r14 = "vibrate_channel"
                int r14 = r0.getInt(r14, r4)
            L_0x0190:
                r0 = 2131626922(0x7f0e0baa, float:1.8881094E38)
                java.lang.String r1 = "Vibrate"
                if (r14 != 0) goto L_0x01a9
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626923(0x7f0e0bab, float:1.8881096E38)
                java.lang.String r1 = "VibrationDefault"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x0404
            L_0x01a9:
                if (r14 != r5) goto L_0x01bd
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626575(0x7f0e0a4f, float:1.888039E38)
                java.lang.String r1 = "Short"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x0404
            L_0x01bd:
                if (r14 != r3) goto L_0x01d1
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626924(0x7f0e0bac, float:1.8881098E38)
                java.lang.String r1 = "VibrationDisabled"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x0404
            L_0x01d1:
                r3 = 3
                if (r14 != r3) goto L_0x01e6
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131625467(0x7f0e05fb, float:1.8878143E38)
                java.lang.String r1 = "Long"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x0404
            L_0x01e6:
                if (r14 != r2) goto L_0x0404
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131625839(0x7f0e076f, float:1.8878897E38)
                java.lang.String r1 = "OnlyIfSilent"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x0404
            L_0x01fa:
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.messagePriorityRow
                if (r14 != r1) goto L_0x027f
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != r5) goto L_0x0211
                java.lang.String r14 = "priority_messages"
                int r14 = r0.getInt(r14, r5)
                goto L_0x0226
            L_0x0211:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x0220
                java.lang.String r14 = "priority_group"
                int r14 = r0.getInt(r14, r5)
                goto L_0x0226
            L_0x0220:
                java.lang.String r14 = "priority_channel"
                int r14 = r0.getInt(r14, r5)
            L_0x0226:
                r0 = 2131625802(0x7f0e074a, float:1.8878822E38)
                java.lang.String r1 = "NotificationsImportance"
                if (r14 != 0) goto L_0x023f
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131625812(0x7f0e0754, float:1.8878843E38)
                java.lang.String r1 = "NotificationsPriorityHigh"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x0404
            L_0x023f:
                if (r14 == r5) goto L_0x026d
                if (r14 != r3) goto L_0x0244
                goto L_0x026d
            L_0x0244:
                if (r14 != r2) goto L_0x0258
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131625813(0x7f0e0755, float:1.8878845E38)
                java.lang.String r1 = "NotificationsPriorityLow"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x0404
            L_0x0258:
                r2 = 5
                if (r14 != r2) goto L_0x0404
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131625814(0x7f0e0756, float:1.8878847E38)
                java.lang.String r1 = "NotificationsPriorityMedium"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x0404
            L_0x026d:
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131625816(0x7f0e0758, float:1.887885E38)
                java.lang.String r1 = "NotificationsPriorityUrgent"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x0404
            L_0x027f:
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.messagePopupNotificationRow
                if (r14 != r1) goto L_0x0404
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != r5) goto L_0x0296
                java.lang.String r14 = "popupAll"
                int r14 = r0.getInt(r14, r4)
                goto L_0x02ab
            L_0x0296:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x02a5
                java.lang.String r14 = "popupGroup"
                int r14 = r0.getInt(r14, r4)
                goto L_0x02ab
            L_0x02a5:
                java.lang.String r14 = "popupChannel"
                int r14 = r0.getInt(r14, r4)
            L_0x02ab:
                if (r14 != 0) goto L_0x02b7
                r14 = 2131625659(0x7f0e06bb, float:1.8878532E38)
                java.lang.String r0 = "NoPopup"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x02d8
            L_0x02b7:
                if (r14 != r5) goto L_0x02c3
                r14 = 2131625841(0x7f0e0771, float:1.8878901E38)
                java.lang.String r0 = "OnlyWhenScreenOn"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x02d8
            L_0x02c3:
                if (r14 != r3) goto L_0x02cf
                r14 = 2131625840(0x7f0e0770, float:1.88789E38)
                java.lang.String r0 = "OnlyWhenScreenOff"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x02d8
            L_0x02cf:
                r14 = 2131624161(0x7f0e00e1, float:1.8875494E38)
                java.lang.String r0 = "AlwaysShowPopup"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
            L_0x02d8:
                r0 = 2131626225(0x7f0e08f1, float:1.887968E38)
                java.lang.String r1 = "PopupNotification"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r0, r14, r5)
                goto L_0x0404
            L_0x02e6:
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.deleteAllSectionRow
                java.lang.String r1 = "windowBackgroundGrayShadow"
                if (r14 == r0) goto L_0x0322
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.groupSection2Row
                if (r14 != r0) goto L_0x0301
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.exceptionsSection2Row
                if (r0 == r2) goto L_0x0322
            L_0x0301:
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.exceptionsSection2Row
                if (r14 != r0) goto L_0x0312
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.deleteAllRow
                if (r14 != r2) goto L_0x0312
                goto L_0x0322
            L_0x0312:
                android.view.View r13 = r13.itemView
                android.content.Context r14 = r12.mContext
                r0 = 2131165406(0x7var_de, float:1.7945028E38)
                android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r14, (int) r0, (java.lang.String) r1)
                r13.setBackgroundDrawable(r14)
                goto L_0x0404
            L_0x0322:
                android.view.View r13 = r13.itemView
                android.content.Context r14 = r12.mContext
                r0 = 2131165407(0x7var_df, float:1.794503E38)
                android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r14, (int) r0, (java.lang.String) r1)
                r13.setBackgroundDrawable(r14)
                goto L_0x0404
            L_0x0332:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextColorCell r13 = (org.telegram.ui.Cells.TextColorCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                android.content.SharedPreferences r14 = r14.getNotificationsSettings()
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.currentType
                r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
                if (r0 != r5) goto L_0x034e
                java.lang.String r0 = "MessagesLed"
                int r14 = r14.getInt(r0, r1)
                goto L_0x0363
            L_0x034e:
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.currentType
                if (r0 != 0) goto L_0x035d
                java.lang.String r0 = "GroupLed"
                int r14 = r14.getInt(r0, r1)
                goto L_0x0363
            L_0x035d:
                java.lang.String r0 = "ChannelLed"
                int r14 = r14.getInt(r0, r1)
            L_0x0363:
                r0 = 9
                if (r4 >= r0) goto L_0x0375
                int[] r0 = org.telegram.ui.Cells.TextColorCell.colorsToSave
                r0 = r0[r4]
                if (r0 != r14) goto L_0x0372
                int[] r14 = org.telegram.ui.Cells.TextColorCell.colors
                r14 = r14[r4]
                goto L_0x0375
            L_0x0372:
                int r4 = r4 + 1
                goto L_0x0363
            L_0x0375:
                r0 = 2131625420(0x7f0e05cc, float:1.8878047E38)
                java.lang.String r1 = "LedColor"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndColor(r0, r14, r5)
                goto L_0x0404
            L_0x0383:
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
                if (r14 == r2) goto L_0x03a5
                r4 = 1
            L_0x03a5:
                r13.setException(r0, r1, r4)
                goto L_0x0404
            L_0x03a9:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextCheckCell r13 = (org.telegram.ui.Cells.TextCheckCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                android.content.SharedPreferences r0 = r0.getNotificationsSettings()
                org.telegram.ui.NotificationsCustomSettingsActivity r1 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r1 = r1.previewRow
                if (r14 != r1) goto L_0x0404
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != r5) goto L_0x03ca
                java.lang.String r14 = "EnablePreviewAll"
                boolean r14 = r0.getBoolean(r14, r5)
                goto L_0x03df
            L_0x03ca:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x03d9
                java.lang.String r14 = "EnablePreviewGroup"
                boolean r14 = r0.getBoolean(r14, r5)
                goto L_0x03df
            L_0x03d9:
                java.lang.String r14 = "EnablePreviewChannel"
                boolean r14 = r0.getBoolean(r14, r5)
            L_0x03df:
                r0 = 2131625542(0x7f0e0646, float:1.8878295E38)
                java.lang.String r1 = "MessagePreview"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndCheck(r0, r14, r5)
                goto L_0x0404
            L_0x03ec:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.HeaderCell r13 = (org.telegram.ui.Cells.HeaderCell) r13
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.messageSectionRow
                if (r14 != r0) goto L_0x0404
                r14 = 2131626395(0x7f0e099b, float:1.8880025E38)
                java.lang.String r0 = "SETTINGS"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r13.setText(r14)
            L_0x0404:
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

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$NotificationsCustomSettingsActivity$YpE9rwbQwhMphj8bqCEt9ez12V8 r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                NotificationsCustomSettingsActivity.this.lambda$getThemeDescriptions$12$NotificationsCustomSettingsActivity();
            }
        };
        $$Lambda$NotificationsCustomSettingsActivity$YpE9rwbQwhMphj8bqCEt9ez12V8 r8 = r10;
        $$Lambda$NotificationsCustomSettingsActivity$YpE9rwbQwhMphj8bqCEt9ez12V8 r7 = r10;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextColorCell.class, TextSettingsCell.class, UserCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText"), new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink"), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription((View) this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon")};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$12$NotificationsCustomSettingsActivity() {
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

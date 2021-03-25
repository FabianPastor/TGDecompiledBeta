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
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;

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
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
            addItem.setIsSearchField(true);
            addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
            });
            addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                NotificationsCustomSettingsActivity.this.lambda$createView$9$NotificationsCustomSettingsActivity(view, i, f, f2);
            }
        });
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
    /* renamed from: lambda$createView$9 */
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
                        boolean z3 = object instanceof TLRPC$User;
                        if (z3) {
                            i2 = ((TLRPC$User) object).id;
                        } else {
                            i2 = -((TLRPC$Chat) object).id;
                        }
                        long j = (long) i2;
                        if (this.exceptionsDict.containsKey(Long.valueOf(j))) {
                            notificationException2 = this.exceptionsDict.get(Long.valueOf(j));
                        } else {
                            NotificationsSettingsActivity.NotificationException notificationException3 = new NotificationsSettingsActivity.NotificationException();
                            notificationException3.did = j;
                            if (z3) {
                                notificationException3.did = (long) ((TLRPC$User) object).id;
                            } else {
                                notificationException3.did = (long) (-((TLRPC$Chat) object).id);
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
                        public final /* synthetic */ boolean f$1;
                        public final /* synthetic */ ArrayList f$2;
                        public final /* synthetic */ NotificationsSettingsActivity.NotificationException f$3;
                        public final /* synthetic */ int f$4;

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
                            public final /* synthetic */ NotificationsCheckCell f$1;
                            public final /* synthetic */ RecyclerView.ViewHolder f$2;
                            public final /* synthetic */ int f$3;

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
                            int i7 = this.currentType;
                            if (i7 == 1) {
                                str = notificationsSettings2.getString("GlobalSoundPath", path);
                            } else if (i7 == 0) {
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
                            public final /* synthetic */ int f$1;

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
                            public final /* synthetic */ int f$1;

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
                        int i8 = this.currentType;
                        showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0, i8 == 1 ? "vibrate_messages" : i8 == 0 ? "vibrate_group" : "vibrate_channel", new Runnable(i3) {
                            public final /* synthetic */ int f$1;

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
                            public final /* synthetic */ int f$1;

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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$NotificationsCustomSettingsActivity(boolean z, ArrayList arrayList, NotificationsSettingsActivity.NotificationException notificationException, int i, int i2) {
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
    /* renamed from: lambda$null$2 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$NotificationsCustomSettingsActivity(NotificationsSettingsActivity.NotificationException notificationException) {
        this.exceptions.add(0, notificationException);
        updateRows(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$NotificationsCustomSettingsActivity(DialogInterface dialogInterface, int i) {
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
    /* renamed from: lambda$null$4 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$NotificationsCustomSettingsActivity(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$6 */
    public /* synthetic */ void lambda$null$6$NotificationsCustomSettingsActivity(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$NotificationsCustomSettingsActivity(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
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
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00fb, code lost:
        if (r8.deleted != false) goto L_0x0142;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0190, code lost:
        if (r3.deleted != false) goto L_0x01af;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0265  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0280 A[LOOP:3: B:112:0x027e->B:113:0x0280, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0299  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0224  */
    /* renamed from: lambda$loadExceptions$11 */
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
            if (r14 == 0) goto L_0x01bd
            java.lang.Object r14 = r13.next()
            java.util.Map$Entry r14 = (java.util.Map.Entry) r14
            java.lang.Object r16 = r14.getKey()
            r15 = r16
            java.lang.String r15 = (java.lang.String) r15
            r16 = r13
            java.lang.String r13 = "notify2_"
            boolean r17 = r15.startsWith(r13)
            if (r17 == 0) goto L_0x01a5
            r17 = r5
            java.lang.String r5 = ""
            java.lang.String r5 = r15.replace(r13, r5)
            java.lang.Long r13 = org.telegram.messenger.Utilities.parseLong(r5)
            r15 = r3
            r18 = r4
            long r3 = r13.longValue()
            r19 = 0
            int r13 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r13 == 0) goto L_0x019e
            r13 = r7
            r19 = r8
            long r7 = (long) r10
            int r20 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r20 == 0) goto L_0x0197
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
            if (r8 == 0) goto L_0x00d5
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r10 = "notifyuntil_"
            r8.append(r10)
            r8.append(r5)
            java.lang.String r5 = r8.toString()
            java.lang.Object r5 = r12.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 == 0) goto L_0x00d5
            int r5 = r5.intValue()
            r7.muteUntil = r5
        L_0x00d5:
            int r5 = (int) r3
            r14 = r11
            r8 = 32
            long r10 = r3 << r8
            int r8 = (int) r10
            if (r5 == 0) goto L_0x014f
            if (r5 <= 0) goto L_0x0103
            org.telegram.messenger.MessagesController r8 = r21.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r10)
            if (r8 != 0) goto L_0x00f9
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r0.add(r5)
            r1.put(r3, r7)
            goto L_0x00fe
        L_0x00f9:
            boolean r3 = r8.deleted
            if (r3 == 0) goto L_0x00fe
            goto L_0x0142
        L_0x00fe:
            r6.add(r7)
            goto L_0x019a
        L_0x0103:
            org.telegram.messenger.MessagesController r8 = r21.getMessagesController()
            int r5 = -r5
            java.lang.Integer r10 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r10)
            if (r8 != 0) goto L_0x011d
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2.add(r5)
            r1.put(r3, r7)
            goto L_0x0142
        L_0x011d:
            boolean r3 = r8.left
            if (r3 != 0) goto L_0x0142
            boolean r3 = r8.kicked
            if (r3 != 0) goto L_0x0142
            org.telegram.tgnet.TLRPC$InputChannel r3 = r8.migrated_to
            if (r3 == 0) goto L_0x012a
            goto L_0x0142
        L_0x012a:
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r3 == 0) goto L_0x013b
            boolean r3 = r8.megagroup
            if (r3 != 0) goto L_0x013b
            r11 = r19
            r11.add(r7)
            goto L_0x01a3
        L_0x013b:
            r11 = r19
            r13.add(r7)
            goto L_0x01a3
        L_0x0142:
            r7 = r13
            r11 = r14
            r3 = r15
            r13 = r16
            r5 = r17
            r4 = r18
            r8 = r19
            goto L_0x01b9
        L_0x014f:
            r11 = r19
            if (r8 == 0) goto L_0x01a3
            org.telegram.messenger.MessagesController r5 = r21.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$EncryptedChat r5 = r5.getEncryptedChat(r10)
            if (r5 != 0) goto L_0x016d
            java.lang.Integer r5 = java.lang.Integer.valueOf(r8)
            r8 = r15
            r8.add(r5)
            r1.put(r3, r7)
            goto L_0x0193
        L_0x016d:
            r8 = r15
            org.telegram.messenger.MessagesController r3 = r21.getMessagesController()
            int r4 = r5.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 != 0) goto L_0x018e
            int r3 = r5.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r0.add(r3)
            int r3 = r5.user_id
            long r3 = (long) r3
            r1.put(r3, r7)
            goto L_0x0193
        L_0x018e:
            boolean r3 = r3.deleted
            if (r3 == 0) goto L_0x0193
            goto L_0x01af
        L_0x0193:
            r6.add(r7)
            goto L_0x01af
        L_0x0197:
            r20 = r10
            r14 = r11
        L_0x019a:
            r8 = r15
            r11 = r19
            goto L_0x01af
        L_0x019e:
            r13 = r7
            r20 = r10
            r14 = r11
            r11 = r8
        L_0x01a3:
            r8 = r15
            goto L_0x01af
        L_0x01a5:
            r18 = r4
            r17 = r5
            r13 = r7
            r20 = r10
            r14 = r11
            r11 = r8
            r8 = r3
        L_0x01af:
            r3 = r8
            r8 = r11
            r7 = r13
            r11 = r14
            r13 = r16
            r5 = r17
            r4 = r18
        L_0x01b9:
            r10 = r20
            goto L_0x0048
        L_0x01bd:
            r18 = r4
            r17 = r5
            r13 = r7
            r11 = r8
            r10 = 0
            r8 = r3
            int r3 = r1.size()
            if (r3 == 0) goto L_0x02b9
            boolean r3 = r8.isEmpty()     // Catch:{ Exception -> 0x0215 }
            java.lang.String r4 = ","
            if (r3 != 0) goto L_0x01de
            org.telegram.messenger.MessagesStorage r3 = r21.getMessagesStorage()     // Catch:{ Exception -> 0x0215 }
            java.lang.String r5 = android.text.TextUtils.join(r4, r8)     // Catch:{ Exception -> 0x0215 }
            r3.getEncryptedChatsInternal(r5, r9, r0)     // Catch:{ Exception -> 0x0215 }
        L_0x01de:
            boolean r3 = r0.isEmpty()     // Catch:{ Exception -> 0x0215 }
            if (r3 != 0) goto L_0x01f6
            org.telegram.messenger.MessagesStorage r3 = r21.getMessagesStorage()     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r0 = android.text.TextUtils.join(r4, r0)     // Catch:{ Exception -> 0x01f2 }
            r5 = r18
            r3.getUsersInternal(r0, r5)     // Catch:{ Exception -> 0x0211 }
            goto L_0x01f8
        L_0x01f2:
            r0 = move-exception
            r5 = r18
            goto L_0x0212
        L_0x01f6:
            r5 = r18
        L_0x01f8:
            boolean r0 = r2.isEmpty()     // Catch:{ Exception -> 0x0211 }
            if (r0 != 0) goto L_0x020e
            org.telegram.messenger.MessagesStorage r0 = r21.getMessagesStorage()     // Catch:{ Exception -> 0x0211 }
            java.lang.String r2 = android.text.TextUtils.join(r4, r2)     // Catch:{ Exception -> 0x0211 }
            r4 = r17
            r0.getChatsInternal(r2, r4)     // Catch:{ Exception -> 0x020c }
            goto L_0x021d
        L_0x020c:
            r0 = move-exception
            goto L_0x021a
        L_0x020e:
            r4 = r17
            goto L_0x021d
        L_0x0211:
            r0 = move-exception
        L_0x0212:
            r4 = r17
            goto L_0x021a
        L_0x0215:
            r0 = move-exception
            r4 = r17
            r5 = r18
        L_0x021a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021d:
            int r0 = r4.size()
            r2 = 0
        L_0x0222:
            if (r2 >= r0) goto L_0x025e
            java.lang.Object r3 = r4.get(r2)
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC$Chat) r3
            boolean r7 = r3.left
            if (r7 != 0) goto L_0x025b
            boolean r7 = r3.kicked
            if (r7 != 0) goto L_0x025b
            org.telegram.tgnet.TLRPC$InputChannel r7 = r3.migrated_to
            if (r7 == 0) goto L_0x0237
            goto L_0x025b
        L_0x0237:
            int r7 = r3.id
            int r7 = -r7
            long r7 = (long) r7
            java.lang.Object r7 = r1.get(r7)
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r7 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r7
            int r8 = r3.id
            int r8 = -r8
            long r14 = (long) r8
            r1.remove(r14)
            if (r7 == 0) goto L_0x025b
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r8 == 0) goto L_0x0258
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0258
            r11.add(r7)
            goto L_0x025b
        L_0x0258:
            r13.add(r7)
        L_0x025b:
            int r2 = r2 + 1
            goto L_0x0222
        L_0x025e:
            int r0 = r5.size()
            r2 = 0
        L_0x0263:
            if (r2 >= r0) goto L_0x0279
            java.lang.Object r3 = r5.get(r2)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            boolean r7 = r3.deleted
            if (r7 == 0) goto L_0x0270
            goto L_0x0276
        L_0x0270:
            int r3 = r3.id
            long r7 = (long) r3
            r1.remove(r7)
        L_0x0276:
            int r2 = r2 + 1
            goto L_0x0263
        L_0x0279:
            int r0 = r9.size()
            r2 = 0
        L_0x027e:
            if (r2 >= r0) goto L_0x0292
            java.lang.Object r3 = r9.get(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = (org.telegram.tgnet.TLRPC$EncryptedChat) r3
            int r3 = r3.id
            long r7 = (long) r3
            r3 = 32
            long r7 = r7 << r3
            r1.remove(r7)
            int r2 = r2 + 1
            goto L_0x027e
        L_0x0292:
            int r0 = r1.size()
            r15 = 0
        L_0x0297:
            if (r15 >= r0) goto L_0x02bd
            long r2 = r1.keyAt(r15)
            int r3 = (int) r2
            if (r3 >= 0) goto L_0x02af
            java.lang.Object r2 = r1.valueAt(r15)
            r13.remove(r2)
            java.lang.Object r2 = r1.valueAt(r15)
            r11.remove(r2)
            goto L_0x02b6
        L_0x02af:
            java.lang.Object r2 = r1.valueAt(r15)
            r6.remove(r2)
        L_0x02b6:
            int r15 = r15 + 1
            goto L_0x0297
        L_0x02b9:
            r4 = r17
            r5 = r18
        L_0x02bd:
            org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$K64KuNwjcB-bKy0b6spOQTqmxZA r0 = new org.telegram.ui.-$$Lambda$NotificationsCustomSettingsActivity$K64KuNwjcB-bKy0b6spOQTqmxZA
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
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
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                public /* synthetic */ SparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                public /* synthetic */ SparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public final void onDataSetChanged(int i) {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$new$0$NotificationsCustomSettingsActivity$SearchAdapter(i);
                }

                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$NotificationsCustomSettingsActivity$SearchAdapter(int i) {
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
                this.searchAdapterHelper.queryServerSearch((String) null, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$wKH7EZWmFuFyynypPZLlXYXe6Y r1 = new Runnable(str) {
                public final /* synthetic */ String f$1;

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
        public void lambda$searchDialogs$1(String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$processSearch$3$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$processSearch$3 */
        public /* synthetic */ void lambda$processSearch$3$NotificationsCustomSettingsActivity$SearchAdapter(String str) {
            this.searchAdapterHelper.queryServerSearch(str, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0, false, 0, 0);
            Utilities.searchQueue.postRunnable(new Runnable(str, new ArrayList(NotificationsCustomSettingsActivity.this.exceptions)) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v0, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v1, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v2, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x0148, code lost:
            if (r10[r5].contains(" " + r3) == false) goto L_0x014d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:60:0x0168, code lost:
            if (r4.contains(" " + r3) != false) goto L_0x016a;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x011f  */
        /* JADX WARNING: Removed duplicated region for block: B:75:0x01bf A[LOOP:1: B:48:0x011d->B:75:0x01bf, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x00c0 A[EDGE_INSN: B:83:0x00c0->B:35:0x00c0 ?: BREAK  , SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x0180 A[SYNTHETIC] */
        /* renamed from: lambda$null$2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String r21, java.util.ArrayList r22) {
            /*
                r20 = this;
                r0 = r20
                java.lang.String r1 = r21.trim()
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
                int r12 = r22.size()
                if (r11 >= r12) goto L_0x01d7
                r12 = r22
                java.lang.Object r13 = r12.get(r11)
                org.telegram.ui.NotificationsSettingsActivity$NotificationException r13 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r13
                long r14 = r13.did
                int r9 = (int) r14
                r16 = 32
                long r14 = r14 >> r16
                int r15 = (int) r14
                if (r9 == 0) goto L_0x00c8
                if (r9 <= 0) goto L_0x0099
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r14 = r14.getMessagesController()
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
                org.telegram.tgnet.TLRPC$User r9 = r14.getUser(r9)
                boolean r14 = r9.deleted
                if (r14 == 0) goto L_0x0088
                goto L_0x00c0
            L_0x0088:
                if (r9 == 0) goto L_0x00f8
                java.lang.String r14 = r9.first_name
                java.lang.String r15 = r9.last_name
                java.lang.String r14 = org.telegram.messenger.ContactsController.formatName(r14, r15)
                r10[r5] = r14
                java.lang.String r14 = r9.username
                r10[r3] = r14
                goto L_0x00f9
            L_0x0099:
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r14 = r14.getMessagesController()
                int r9 = -r9
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
                org.telegram.tgnet.TLRPC$Chat r9 = r14.getChat(r9)
                if (r9 == 0) goto L_0x00f8
                boolean r14 = r9.left
                if (r14 != 0) goto L_0x00c0
                boolean r14 = r9.kicked
                if (r14 != 0) goto L_0x00c0
                org.telegram.tgnet.TLRPC$InputChannel r14 = r9.migrated_to
                if (r14 == 0) goto L_0x00b7
                goto L_0x00c0
            L_0x00b7:
                java.lang.String r14 = r9.title
                r10[r5] = r14
                java.lang.String r14 = r9.username
                r10[r3] = r14
                goto L_0x00f9
            L_0x00c0:
                r19 = r6
                r18 = r7
                r5 = 1
                r7 = 0
                goto L_0x01cc
            L_0x00c8:
                org.telegram.ui.NotificationsCustomSettingsActivity r9 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
                java.lang.Integer r14 = java.lang.Integer.valueOf(r15)
                org.telegram.tgnet.TLRPC$EncryptedChat r9 = r9.getEncryptedChat(r14)
                if (r9 == 0) goto L_0x00f8
                org.telegram.ui.NotificationsCustomSettingsActivity r14 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                org.telegram.messenger.MessagesController r14 = r14.getMessagesController()
                int r9 = r9.user_id
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
                org.telegram.tgnet.TLRPC$User r9 = r14.getUser(r9)
                if (r9 == 0) goto L_0x00f8
                java.lang.String r14 = r9.first_name
                java.lang.String r15 = r9.last_name
                java.lang.String r14 = org.telegram.messenger.ContactsController.formatName(r14, r15)
                r10[r5] = r14
                java.lang.String r9 = r9.username
                r10[r3] = r9
            L_0x00f8:
                r9 = 0
            L_0x00f9:
                r14 = r10[r5]
                r15 = r10[r5]
                java.lang.String r15 = r15.toLowerCase()
                r10[r5] = r15
                org.telegram.messenger.LocaleController r15 = org.telegram.messenger.LocaleController.getInstance()
                r4 = r10[r5]
                java.lang.String r4 = r15.getTranslitString(r4)
                r15 = r10[r5]
                if (r15 == 0) goto L_0x011a
                r15 = r10[r5]
                boolean r15 = r15.equals(r4)
                if (r15 == 0) goto L_0x011a
                r4 = 0
            L_0x011a:
                r15 = 0
                r17 = 0
            L_0x011d:
                if (r15 >= r6) goto L_0x00c0
                r3 = r7[r15]
                r18 = r10[r5]
                r19 = r6
                java.lang.String r6 = " "
                if (r18 == 0) goto L_0x014b
                r18 = r7
                r7 = r10[r5]
                boolean r7 = r7.startsWith(r3)
                if (r7 != 0) goto L_0x016a
                r7 = r10[r5]
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r6)
                r5.append(r3)
                java.lang.String r5 = r5.toString()
                boolean r5 = r7.contains(r5)
                if (r5 != 0) goto L_0x016a
                goto L_0x014d
            L_0x014b:
                r18 = r7
            L_0x014d:
                if (r4 == 0) goto L_0x016d
                boolean r5 = r4.startsWith(r3)
                if (r5 != 0) goto L_0x016a
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r6)
                r5.append(r3)
                java.lang.String r5 = r5.toString()
                boolean r5 = r4.contains(r5)
                if (r5 == 0) goto L_0x016d
            L_0x016a:
                r5 = 1
                r6 = 1
                goto L_0x017e
            L_0x016d:
                r5 = 1
                r6 = r10[r5]
                if (r6 == 0) goto L_0x017c
                r6 = r10[r5]
                boolean r6 = r6.startsWith(r3)
                if (r6 == 0) goto L_0x017c
                r6 = 2
                goto L_0x017e
            L_0x017c:
                r6 = r17
            L_0x017e:
                if (r6 == 0) goto L_0x01bf
                if (r6 != r5) goto L_0x018c
                r4 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r14, r4, r3)
                r8.add(r3)
                r7 = 0
                goto L_0x01b6
            L_0x018c:
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
                r7.append(r3)
                java.lang.String r3 = r7.toString()
                r7 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r7, r3)
                r8.add(r3)
            L_0x01b6:
                r2.add(r13)
                if (r9 == 0) goto L_0x01cc
                r1.add(r9)
                goto L_0x01cc
            L_0x01bf:
                r7 = 0
                int r15 = r15 + 1
                r17 = r6
                r7 = r18
                r6 = r19
                r3 = 1
                r5 = 0
                goto L_0x011d
            L_0x01cc:
                int r11 = r11 + 1
                r7 = r18
                r6 = r19
                r3 = 1
                r5 = 0
                r9 = 2
                goto L_0x005b
            L_0x01d7:
                r0.updateSearchResults(r1, r2, r8)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.SearchAdapter.lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2, ArrayList<CharSequence> arrayList3) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList2, arrayList3, arrayList) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ ArrayList f$2;
                public final /* synthetic */ ArrayList f$3;

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

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$4 */
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
                r14 = 2131626430(0x7f0e09be, float:1.8880096E38)
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
                goto L_0x0404
            L_0x003d:
                org.telegram.ui.NotificationsCustomSettingsActivity r0 = org.telegram.ui.NotificationsCustomSettingsActivity.this
                int r0 = r0.deleteAllRow
                if (r14 != r0) goto L_0x0404
                r14 = 2131626440(0x7f0e09c8, float:1.8880116E38)
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
                r14 = 2131626450(0x7f0e09d2, float:1.8880137E38)
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
                r14 = 2131626449(0x7f0e09d1, float:1.8880135E38)
                java.lang.String r0 = "NotificationsForGroups"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                java.lang.String r0 = "EnableGroup2"
                int r13 = r13.getInt(r0, r4)
                goto L_0x0083
            L_0x009d:
                r14 = 2131626447(0x7f0e09cf, float:1.888013E38)
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
                r13 = 2131626463(0x7f0e09df, float:1.8880163E38)
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
                r13 = 2131626461(0x7f0e09dd, float:1.8880159E38)
                java.lang.String r14 = "NotificationsOff"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
                r8.append(r13)
                goto L_0x00ca
            L_0x00e0:
                r14 = 2131626462(0x7f0e09de, float:1.888016E38)
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
                r1 = 2131627488(0x7f0e0de0, float:1.8882242E38)
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
                r14 = 2131626291(0x7f0e0933, float:1.8879814E38)
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
            L_0x0152:
                r0 = 2131627487(0x7f0e0ddf, float:1.888224E38)
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
                r0 = 2131627878(0x7f0e0var_, float:1.8883033E38)
                java.lang.String r1 = "Vibrate"
                if (r14 != 0) goto L_0x01a9
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131627879(0x7f0e0var_, float:1.8883035E38)
                java.lang.String r1 = "VibrationDefault"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x0404
            L_0x01a9:
                if (r14 != r5) goto L_0x01bd
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131627440(0x7f0e0db0, float:1.8882144E38)
                java.lang.String r1 = "Short"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x0404
            L_0x01bd:
                if (r14 != r3) goto L_0x01d1
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131627880(0x7f0e0var_, float:1.8883037E38)
                java.lang.String r1 = "VibrationDisabled"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x0404
            L_0x01d1:
                r3 = 3
                if (r14 != r3) goto L_0x01e6
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131625997(0x7f0e080d, float:1.8879218E38)
                java.lang.String r1 = "Long"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r5)
                goto L_0x0404
            L_0x01e6:
                if (r14 != r2) goto L_0x0404
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626495(0x7f0e09ff, float:1.8880228E38)
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
                r0 = 2131626452(0x7f0e09d4, float:1.888014E38)
                java.lang.String r1 = "NotificationsImportance"
                if (r14 != 0) goto L_0x023f
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626465(0x7f0e09e1, float:1.8880167E38)
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
                r0 = 2131626466(0x7f0e09e2, float:1.888017E38)
                java.lang.String r1 = "NotificationsPriorityLow"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x0404
            L_0x0258:
                r2 = 5
                if (r14 != r2) goto L_0x0404
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626467(0x7f0e09e3, float:1.8880171E38)
                java.lang.String r1 = "NotificationsPriorityMedium"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r13.setTextAndValue(r14, r0, r4)
                goto L_0x0404
            L_0x026d:
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r0 = 2131626469(0x7f0e09e5, float:1.8880175E38)
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
                r14 = 2131626275(0x7f0e0923, float:1.8879782E38)
                java.lang.String r0 = "NoPopup"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x02d8
            L_0x02b7:
                if (r14 != r5) goto L_0x02c3
                r14 = 2131626497(0x7f0e0a01, float:1.8880232E38)
                java.lang.String r0 = "OnlyWhenScreenOn"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x02d8
            L_0x02c3:
                if (r14 != r3) goto L_0x02cf
                r14 = 2131626496(0x7f0e0a00, float:1.888023E38)
                java.lang.String r0 = "OnlyWhenScreenOff"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x02d8
            L_0x02cf:
                r14 = 2131624250(0x7f0e013a, float:1.8875674E38)
                java.lang.String r0 = "AlwaysShowPopup"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
            L_0x02d8:
                r0 = 2131626955(0x7f0e0bcb, float:1.888116E38)
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
                r0 = 2131165448(0x7var_, float:1.7945113E38)
                android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r14, (int) r0, (java.lang.String) r1)
                r13.setBackgroundDrawable(r14)
                goto L_0x0404
            L_0x0322:
                android.view.View r13 = r13.itemView
                android.content.Context r14 = r12.mContext
                r0 = 2131165449(0x7var_, float:1.7945115E38)
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
                r0 = 2131625920(0x7f0e07c0, float:1.8879062E38)
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
                r0 = 2131626110(0x7f0e087e, float:1.8879447E38)
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
                r14 = 2131627207(0x7f0e0cc7, float:1.8881672E38)
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$NotificationsCustomSettingsActivity$Q0JA7CkShx4ZcJ91r5xSgqVbIo r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                NotificationsCustomSettingsActivity.this.lambda$getThemeDescriptions$12$NotificationsCustomSettingsActivity();
            }
        };
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
        $$Lambda$NotificationsCustomSettingsActivity$Q0JA7CkShx4ZcJ91r5xSgqVbIo r9 = r11;
        ThemeDescription themeDescription = r2;
        ThemeDescription themeDescription2 = new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteGrayText");
        arrayList.add(themeDescription);
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$NotificationsCustomSettingsActivity$Q0JA7CkShx4ZcJ91r5xSgqVbIo r8 = r11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
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
    /* renamed from: lambda$getThemeDescriptions$12 */
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

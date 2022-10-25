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
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
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
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;
/* loaded from: classes3.dex */
public class NotificationsCustomSettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ListAdapter adapter;
    private int alertRow;
    private int alertSection2Row;
    private AnimatorSet animatorSet;
    private int currentType;
    private int deleteAllRow;
    private int deleteAllSectionRow;
    private EmptyTextProgressView emptyView;
    private ArrayList<NotificationsSettingsActivity.NotificationException> exceptions;
    private int exceptionsAddRow;
    private HashMap<Long, NotificationsSettingsActivity.NotificationException> exceptionsDict;
    private int exceptionsEndRow;
    private int exceptionsSection2Row;
    private int exceptionsStartRow;
    private int groupSection2Row;
    private RecyclerListView listView;
    private int messageLedRow;
    private int messagePopupNotificationRow;
    private int messagePriorityRow;
    private int messageSectionRow;
    private int messageSoundRow;
    private int messageVibrateRow;
    private int previewRow;
    private int rowCount;
    private SearchAdapter searchAdapter;
    private boolean searchWas;
    private boolean searching;
    int topicId;

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList) {
        this(i, arrayList, false);
    }

    public NotificationsCustomSettingsActivity(int i, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList, boolean z) {
        this.rowCount = 0;
        this.exceptionsDict = new HashMap<>();
        this.topicId = 0;
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        updateRows(true);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.searching = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == -1) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    NotificationsCustomSettingsActivity.this.finishFragment();
                }
            }
        });
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList = this.exceptions;
        if (arrayList != null && !arrayList.isEmpty()) {
            this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.2
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchExpand() {
                    NotificationsCustomSettingsActivity.this.searching = true;
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(true);
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchCollapse() {
                    NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs(null);
                    NotificationsCustomSettingsActivity.this.searching = false;
                    NotificationsCustomSettingsActivity.this.searchWas = false;
                    NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoExceptions", R.string.NoExceptions));
                    NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.adapter);
                    NotificationsCustomSettingsActivity.this.adapter.notifyDataSetChanged();
                    NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(true);
                    NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(false);
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(false);
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onTextChanged(EditText editText) {
                    if (NotificationsCustomSettingsActivity.this.searchAdapter == null) {
                        return;
                    }
                    String obj = editText.getText().toString();
                    if (obj.length() != 0) {
                        NotificationsCustomSettingsActivity.this.searchWas = true;
                        if (NotificationsCustomSettingsActivity.this.listView != null) {
                            NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                            NotificationsCustomSettingsActivity.this.emptyView.showProgress();
                            NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.searchAdapter);
                            NotificationsCustomSettingsActivity.this.searchAdapter.notifyDataSetChanged();
                            NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(false);
                            NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(true);
                        }
                    }
                    NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs(obj);
                }
            }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        }
        this.searchAdapter = new SearchAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", R.string.NoExceptions));
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
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i, float f, float f2) {
                NotificationsCustomSettingsActivity.this.lambda$createView$8(context, view, i, f, f2);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(NotificationsCustomSettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
            }
        });
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(Context context, View view, final int i, float f, float f2) {
        final ArrayList<NotificationsSettingsActivity.NotificationException> arrayList;
        final NotificationsSettingsActivity.NotificationException notificationException;
        final boolean z;
        long j;
        NotificationsSettingsActivity.NotificationException notificationException2;
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2;
        boolean z2;
        if (getParentActivity() == null) {
            return;
        }
        boolean z3 = false;
        if (this.listView.getAdapter() == this.searchAdapter || (i >= this.exceptionsStartRow && i < this.exceptionsEndRow)) {
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            SearchAdapter searchAdapter = this.searchAdapter;
            if (adapter == searchAdapter) {
                Object object = searchAdapter.getObject(i);
                if (!(object instanceof NotificationsSettingsActivity.NotificationException)) {
                    boolean z4 = object instanceof TLRPC$User;
                    if (z4) {
                        j = ((TLRPC$User) object).id;
                    } else {
                        j = -((TLRPC$Chat) object).id;
                    }
                    if (this.exceptionsDict.containsKey(Long.valueOf(j))) {
                        notificationException2 = this.exceptionsDict.get(Long.valueOf(j));
                    } else {
                        NotificationsSettingsActivity.NotificationException notificationException3 = new NotificationsSettingsActivity.NotificationException();
                        notificationException3.did = j;
                        if (z4) {
                            notificationException3.did = ((TLRPC$User) object).id;
                        } else {
                            notificationException3.did = -((TLRPC$Chat) object).id;
                        }
                        notificationException2 = notificationException3;
                        z3 = true;
                    }
                    arrayList2 = this.exceptions;
                } else {
                    arrayList2 = this.searchAdapter.searchResult;
                    notificationException2 = (NotificationsSettingsActivity.NotificationException) object;
                }
                notificationException = notificationException2;
                arrayList = arrayList2;
                z = z3;
            } else {
                ArrayList<NotificationsSettingsActivity.NotificationException> arrayList3 = this.exceptions;
                int i2 = i - this.exceptionsStartRow;
                if (i2 < 0 || i2 >= arrayList3.size()) {
                    return;
                }
                arrayList = arrayList3;
                notificationException = arrayList3.get(i2);
                z = false;
            }
            if (notificationException == null) {
                return;
            }
            final long j2 = notificationException.did;
            final boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(j2);
            ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, this.currentAccount, null, true, true, new ChatNotificationsPopupWrapper.Callback() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.3
                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public /* synthetic */ void dismiss() {
                    ChatNotificationsPopupWrapper.Callback.CC.$default$dismiss(this);
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public /* synthetic */ void openExceptions() {
                    ChatNotificationsPopupWrapper.Callback.CC.$default$openExceptions(this);
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public void toggleSound() {
                    String sharedPrefKey = NotificationsController.getSharedPrefKey(j2, NotificationsCustomSettingsActivity.this.topicId);
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(((BaseFragment) NotificationsCustomSettingsActivity.this).currentAccount);
                    boolean z5 = !notificationsSettings.getBoolean("sound_enabled_" + sharedPrefKey, true);
                    notificationsSettings.edit().putBoolean("sound_enabled_" + sharedPrefKey, z5).apply();
                    if (BulletinFactory.canShowBulletin(NotificationsCustomSettingsActivity.this)) {
                        NotificationsCustomSettingsActivity notificationsCustomSettingsActivity = NotificationsCustomSettingsActivity.this;
                        BulletinFactory.createSoundEnabledBulletin(notificationsCustomSettingsActivity, !z5, notificationsCustomSettingsActivity.getResourceProvider()).show();
                    }
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public void muteFor(int i3) {
                    if (i3 != 0) {
                        NotificationsCustomSettingsActivity.this.getNotificationsController().muteUntil(j2, NotificationsCustomSettingsActivity.this.topicId, i3);
                        if (BulletinFactory.canShowBulletin(NotificationsCustomSettingsActivity.this)) {
                            NotificationsCustomSettingsActivity notificationsCustomSettingsActivity = NotificationsCustomSettingsActivity.this;
                            BulletinFactory.createMuteBulletin(notificationsCustomSettingsActivity, 5, i3, notificationsCustomSettingsActivity.getResourceProvider()).show();
                        }
                    } else {
                        if (NotificationsCustomSettingsActivity.this.getMessagesController().isDialogMuted(j2, NotificationsCustomSettingsActivity.this.topicId)) {
                            toggleMute();
                        }
                        if (BulletinFactory.canShowBulletin(NotificationsCustomSettingsActivity.this)) {
                            NotificationsCustomSettingsActivity notificationsCustomSettingsActivity2 = NotificationsCustomSettingsActivity.this;
                            BulletinFactory.createMuteBulletin(notificationsCustomSettingsActivity2, 4, i3, notificationsCustomSettingsActivity2.getResourceProvider()).show();
                        }
                    }
                    update();
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public void showCustomize() {
                    if (j2 != 0) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("dialog_id", j2);
                        ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(bundle);
                        profileNotificationsActivity.setDelegate(new ProfileNotificationsActivity.ProfileNotificationsActivityDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.3.1
                            @Override // org.telegram.ui.ProfileNotificationsActivity.ProfileNotificationsActivityDelegate
                            public void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException4) {
                            }

                            @Override // org.telegram.ui.ProfileNotificationsActivity.ProfileNotificationsActivityDelegate
                            public void didRemoveException(long j3) {
                                setDefault();
                            }
                        });
                        NotificationsCustomSettingsActivity.this.presentFragment(profileNotificationsActivity);
                    }
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public void toggleMute() {
                    NotificationsCustomSettingsActivity.this.getNotificationsController().muteDialog(j2, NotificationsCustomSettingsActivity.this.topicId, !NotificationsCustomSettingsActivity.this.getMessagesController().isDialogMuted(j2, NotificationsCustomSettingsActivity.this.topicId));
                    NotificationsCustomSettingsActivity notificationsCustomSettingsActivity = NotificationsCustomSettingsActivity.this;
                    BulletinFactory.createMuteBulletin(notificationsCustomSettingsActivity, notificationsCustomSettingsActivity.getMessagesController().isDialogMuted(j2, NotificationsCustomSettingsActivity.this.topicId), null).show();
                    update();
                }

                private void update() {
                    if (NotificationsCustomSettingsActivity.this.getMessagesController().isDialogMuted(j2, NotificationsCustomSettingsActivity.this.topicId) != isGlobalNotificationsEnabled) {
                        setDefault();
                    } else {
                        setNotDefault();
                    }
                }

                private void setNotDefault() {
                    SharedPreferences notificationsSettings = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    NotificationsSettingsActivity.NotificationException notificationException4 = notificationException;
                    notificationException4.hasCustom = notificationsSettings.getBoolean("custom_" + notificationException.did, false);
                    NotificationsSettingsActivity.NotificationException notificationException5 = notificationException;
                    notificationException5.notify = notificationsSettings.getInt("notify2_" + notificationException.did, 0);
                    if (notificationException.notify != 0) {
                        int i3 = notificationsSettings.getInt("notifyuntil_" + notificationException.did, -1);
                        if (i3 != -1) {
                            notificationException.muteUntil = i3;
                        }
                    }
                    if (z) {
                        NotificationsCustomSettingsActivity.this.exceptions.add(notificationException);
                        NotificationsCustomSettingsActivity.this.exceptionsDict.put(Long.valueOf(notificationException.did), notificationException);
                        NotificationsCustomSettingsActivity.this.updateRows(true);
                    } else {
                        NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemChanged(i);
                    }
                    ((BaseFragment) NotificationsCustomSettingsActivity.this).actionBar.closeSearchField();
                }

                /* JADX INFO: Access modifiers changed from: private */
                public void setDefault() {
                    int indexOf;
                    if (z) {
                        return;
                    }
                    if (arrayList != NotificationsCustomSettingsActivity.this.exceptions && (indexOf = NotificationsCustomSettingsActivity.this.exceptions.indexOf(notificationException)) >= 0) {
                        NotificationsCustomSettingsActivity.this.exceptions.remove(indexOf);
                        NotificationsCustomSettingsActivity.this.exceptionsDict.remove(Long.valueOf(notificationException.did));
                    }
                    arrayList.remove(notificationException);
                    if (arrayList == NotificationsCustomSettingsActivity.this.exceptions) {
                        if (NotificationsCustomSettingsActivity.this.exceptionsAddRow != -1 && arrayList.isEmpty()) {
                            NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemChanged(NotificationsCustomSettingsActivity.this.exceptionsAddRow);
                            NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemRemoved(NotificationsCustomSettingsActivity.this.deleteAllRow);
                            NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemRemoved(NotificationsCustomSettingsActivity.this.deleteAllSectionRow);
                        }
                        NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemRemoved(i);
                        NotificationsCustomSettingsActivity.this.updateRows(false);
                        NotificationsCustomSettingsActivity.this.checkRowsEnabled();
                    } else {
                        NotificationsCustomSettingsActivity.this.updateRows(true);
                        NotificationsCustomSettingsActivity.this.searchAdapter.notifyItemChanged(i);
                    }
                    ((BaseFragment) NotificationsCustomSettingsActivity.this).actionBar.closeSearchField();
                }
            }, getResourceProvider());
            chatNotificationsPopupWrapper.lambda$update$11(j2, this.topicId, null);
            chatNotificationsPopupWrapper.showAsOptions(this, view, f, f2);
            return;
        }
        if (i == this.exceptionsAddRow) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putBoolean("checkCanWrite", false);
            int i3 = this.currentType;
            if (i3 == 0) {
                bundle.putInt("dialogsType", 6);
            } else if (i3 == 2) {
                bundle.putInt("dialogsType", 5);
            } else {
                bundle.putInt("dialogsType", 4);
            }
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda10
                @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                public final void didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList4, CharSequence charSequence, boolean z5) {
                    NotificationsCustomSettingsActivity.this.lambda$createView$1(dialogsActivity2, arrayList4, charSequence, z5);
                }
            });
            presentFragment(dialogsActivity);
        } else if (i == this.deleteAllRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("NotificationsDeleteAllExceptionTitle", R.string.NotificationsDeleteAllExceptionTitle));
            builder.setMessage(LocaleController.getString("NotificationsDeleteAllExceptionAlert", R.string.NotificationsDeleteAllExceptionAlert));
            builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i4) {
                    NotificationsCustomSettingsActivity.this.lambda$createView$2(dialogInterface, i4);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        } else if (i == this.alertRow) {
            boolean isGlobalNotificationsEnabled2 = getNotificationsController().isGlobalNotificationsEnabled(this.currentType);
            final NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
            final RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
            if (!isGlobalNotificationsEnabled2) {
                getNotificationsController().setGlobalNotificationsEnabled(this.currentType, 0);
                notificationsCheckCell.setChecked(true);
                if (findViewHolderForAdapterPosition != null) {
                    this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
                }
                checkRowsEnabled();
            } else {
                AlertsCreator.showCustomNotificationsDialog(this, 0L, 0, this.currentType, this.exceptions, this.currentAccount, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda7
                    @Override // org.telegram.messenger.MessagesStorage.IntCallback
                    public final void run(int i4) {
                        NotificationsCustomSettingsActivity.this.lambda$createView$3(notificationsCheckCell, findViewHolderForAdapterPosition, i, i4);
                    }
                });
            }
            z3 = isGlobalNotificationsEnabled2;
        } else if (i == this.previewRow) {
            if (!view.isEnabled()) {
                return;
            }
            SharedPreferences notificationsSettings = getNotificationsSettings();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            int i4 = this.currentType;
            if (i4 == 1) {
                z2 = notificationsSettings.getBoolean("EnablePreviewAll", true);
                edit.putBoolean("EnablePreviewAll", !z2);
            } else if (i4 == 0) {
                z2 = notificationsSettings.getBoolean("EnablePreviewGroup", true);
                edit.putBoolean("EnablePreviewGroup", !z2);
            } else {
                z2 = notificationsSettings.getBoolean("EnablePreviewChannel", true);
                edit.putBoolean("EnablePreviewChannel", !z2);
            }
            z3 = z2;
            edit.commit();
            getNotificationsController().updateServerNotificationsSettings(this.currentType);
        } else if (i == this.messageSoundRow) {
            if (!view.isEnabled()) {
                return;
            }
            try {
                Bundle bundle2 = new Bundle();
                bundle2.putInt("type", this.currentType);
                presentFragment(new NotificationsSoundActivity(bundle2, getResourceProvider()));
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (i == this.messageLedRow) {
            if (!view.isEnabled()) {
                return;
            }
            showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), 0L, 0, this.currentType, new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.this.lambda$createView$4(i);
                }
            }));
        } else if (i == this.messagePopupNotificationRow) {
            if (!view.isEnabled()) {
                return;
            }
            showDialog(AlertsCreator.createPopupSelectDialog(getParentActivity(), this.currentType, new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.this.lambda$createView$5(i);
                }
            }));
        } else if (i == this.messageVibrateRow) {
            if (!view.isEnabled()) {
                return;
            }
            int i5 = this.currentType;
            showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0L, 0, i5 == 1 ? "vibrate_messages" : i5 == 0 ? "vibrate_group" : "vibrate_channel", new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.this.lambda$createView$6(i);
                }
            }));
        } else if (i == this.messagePriorityRow) {
            if (!view.isEnabled()) {
                return;
            }
            showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), 0L, 0, this.currentType, new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.this.lambda$createView$7(i);
                }
            }));
        }
        if (!(view instanceof TextCheckCell)) {
            return;
        }
        ((TextCheckCell) view).setChecked(!z3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putLong("dialog_id", ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId);
        bundle.putBoolean("exception", true);
        ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(bundle, getResourceProvider());
        profileNotificationsActivity.setDelegate(new ProfileNotificationsActivity.ProfileNotificationsActivityDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.ProfileNotificationsActivity.ProfileNotificationsActivityDelegate
            public final void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException) {
                NotificationsCustomSettingsActivity.this.lambda$createView$0(notificationException);
            }

            @Override // org.telegram.ui.ProfileNotificationsActivity.ProfileNotificationsActivityDelegate
            public /* synthetic */ void didRemoveException(long j) {
                ProfileNotificationsActivity.ProfileNotificationsActivityDelegate.CC.$default$didRemoveException(this, j);
            }
        });
        presentFragment(profileNotificationsActivity, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(NotificationsSettingsActivity.NotificationException notificationException) {
        this.exceptions.add(0, notificationException);
        updateRows(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DialogInterface dialogInterface, int i) {
        SharedPreferences.Editor edit = getNotificationsSettings().edit();
        int size = this.exceptions.size();
        for (int i2 = 0; i2 < size; i2++) {
            NotificationsSettingsActivity.NotificationException notificationException = this.exceptions.get(i2);
            SharedPreferences.Editor remove = edit.remove("notify2_" + notificationException.did);
            remove.remove("custom_" + notificationException.did);
            getMessagesStorage().setDialogFlags(notificationException.did, 0L);
            TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(notificationException.did);
            if (tLRPC$Dialog != null) {
                tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
            }
        }
        edit.commit();
        int size2 = this.exceptions.size();
        for (int i3 = 0; i3 < size2; i3++) {
            getNotificationsController().updateServerNotificationsSettings(this.exceptions.get(i3).did, this.topicId, false);
        }
        this.exceptions.clear();
        this.exceptionsDict.clear();
        updateRows(true);
        getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(NotificationsCheckCell notificationsCheckCell, RecyclerView.ViewHolder viewHolder, int i, int i2) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(int i) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
        if (findViewHolderForAdapterPosition != null) {
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkRowsEnabled() {
        if (!this.exceptions.isEmpty()) {
            return;
        }
        int childCount = this.listView.getChildCount();
        ArrayList<Animator> arrayList = new ArrayList<>();
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
        if (arrayList.isEmpty()) {
            return;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.animatorSet = animatorSet2;
        animatorSet2.playTogether(arrayList);
        this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(NotificationsCustomSettingsActivity.this.animatorSet)) {
                    NotificationsCustomSettingsActivity.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.setDuration(150L);
        this.animatorSet.start();
    }

    private void loadExceptions() {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsCustomSettingsActivity.this.lambda$loadExceptions$10();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0117, code lost:
        if (r4.deleted != false) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0141, code lost:
        if (r4.deleted != false) goto L30;
     */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0246  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x0260 A[LOOP:3: B:113:0x025e->B:114:0x0260, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:117:0x0279  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0207  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadExceptions$10() {
        /*
            Method dump skipped, instructions count: 688
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.lambda$loadExceptions$10():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadExceptions$9(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRows(boolean z) {
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
        if (arrayList2 != null && !arrayList2.isEmpty()) {
            int i12 = this.rowCount;
            this.exceptionsStartRow = i12;
            int size = i12 + this.exceptions.size();
            this.rowCount = size;
            this.exceptionsEndRow = size;
        } else {
            this.exceptionsStartRow = -1;
            this.exceptionsEndRow = -1;
        }
        if (this.currentType != -1 || ((arrayList = this.exceptions) != null && !arrayList.isEmpty())) {
            int i13 = this.rowCount;
            this.rowCount = i13 + 1;
            this.exceptionsSection2Row = i13;
        } else {
            this.exceptionsSection2Row = -1;
        }
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList3 = this.exceptions;
        if (arrayList3 != null && !arrayList3.isEmpty()) {
            int i14 = this.rowCount;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.deleteAllRow = i14;
            this.rowCount = i15 + 1;
            this.deleteAllSectionRow = i15;
        } else {
            this.deleteAllRow = -1;
            this.deleteAllSectionRow = -1;
        }
        if (!z || (listAdapter = this.adapter) == null) {
            return;
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        Ringtone ringtone;
        if (i2 == -1) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (uri != null && (ringtone = RingtoneManager.getRingtone(getParentActivity(), uri)) != null) {
                if (uri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    str = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                } else {
                    str = ringtone.getTitle(getParentActivity());
                }
                ringtone.stop();
            }
            SharedPreferences.Editor edit = getNotificationsSettings().edit();
            int i3 = this.currentType;
            if (i3 == 1) {
                if (str != null && uri != null) {
                    edit.putString("GlobalSound", str);
                    edit.putString("GlobalSoundPath", uri.toString());
                } else {
                    edit.putString("GlobalSound", "NoSound");
                    edit.putString("GlobalSoundPath", "NoSound");
                }
            } else if (i3 == 0) {
                if (str != null && uri != null) {
                    edit.putString("GroupSound", str);
                    edit.putString("GroupSoundPath", uri.toString());
                } else {
                    edit.putString("GroupSound", "NoSound");
                    edit.putString("GroupSoundPath", "NoSound");
                }
            } else if (i3 == 2) {
                if (str != null && uri != null) {
                    edit.putString("ChannelSound", str);
                    edit.putString("ChannelSoundPath", uri.toString());
                } else {
                    edit.putString("ChannelSound", "NoSound");
                    edit.putString("ChannelSoundPath", "NoSound");
                }
            }
            getNotificationsController().deleteNotificationChannelGlobal(this.currentType);
            edit.commit();
            getNotificationsController().updateServerNotificationsSettings(this.currentType);
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(i);
            if (findViewHolderForAdapterPosition == null) {
                return;
            }
            this.adapter.onBindViewHolder(findViewHolderForAdapterPosition, i);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        getNotificationCenter().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        getNotificationCenter().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ListAdapter listAdapter;
        if (i != NotificationCenter.notificationsSettingsUpdated || (listAdapter = this.adapter) == null) {
            return;
        }
        listAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<NotificationsSettingsActivity.NotificationException> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public final void onDataSetChanged(int i) {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$new$0(i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i) {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void searchDialogs(final String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults(null);
                this.searchAdapterHelper.queryServerSearch(null, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0L, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$searchDialogs$1(str);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchDialogs$1(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$processSearch$3(str);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$3(final String str) {
            this.searchAdapterHelper.queryServerSearch(str, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0L, false, 0, 0);
            final ArrayList arrayList = new ArrayList(NotificationsCustomSettingsActivity.this.exceptions);
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$processSearch$2(str, arrayList);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Code restructure failed: missing block: B:62:0x0169, code lost:
            if (r10[r5].contains(" " + r15) == false) goto L40;
         */
        /* JADX WARN: Code restructure failed: missing block: B:69:0x0189, code lost:
            if (r6.contains(" " + r15) != false) goto L63;
         */
        /* JADX WARN: Removed duplicated region for block: B:52:0x0130  */
        /* JADX WARN: Removed duplicated region for block: B:57:0x0140  */
        /* JADX WARN: Removed duplicated region for block: B:84:0x01e0 A[LOOP:1: B:56:0x013e->B:84:0x01e0, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:94:0x01a1 A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void lambda$processSearch$2(java.lang.String r20, java.util.ArrayList r21) {
            /*
                Method dump skipped, instructions count: 513
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.SearchAdapter.lambda$processSearch$2(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(final ArrayList<Object> arrayList, final ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2, final ArrayList<CharSequence> arrayList3) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$updateSearchResults$4(arrayList2, arrayList3, arrayList);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$4(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            if (!NotificationsCustomSettingsActivity.this.searching) {
                return;
            }
            this.searchRunnable = null;
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            this.searchAdapterHelper.mergeResults(arrayList3);
            if (NotificationsCustomSettingsActivity.this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public Object getObject(int i) {
            if (i >= 0 && i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            int size = i - (this.searchResult.size() + 1);
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            if (size >= 0 && size < globalSearch.size()) {
                return this.searchAdapterHelper.getGlobalSearch().get(size);
            }
            return null;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int size = this.searchResult.size();
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            return !globalSearch.isEmpty() ? size + globalSearch.size() + 1 : size;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1788onCreateViewHolder(ViewGroup viewGroup, int i) {
            View userCell;
            if (i == 0) {
                userCell = new UserCell(this.mContext, 4, 0, false, true);
                userCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else {
                userCell = new GraySectionCell(this.mContext);
            }
            return new RecyclerListView.Holder(userCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType != 0) {
                if (itemViewType != 1) {
                    return;
                }
                ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("AddToExceptions", R.string.AddToExceptions));
                return;
            }
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
            userCell.setData(globalSearch.get(size), null, LocaleController.getString("NotificationsOn", R.string.NotificationsOn), 0, size != globalSearch.size() - 1);
            userCell.setAddButtonVisible(true);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return i == this.searchResult.size() ? 1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 0 || itemViewType == 4) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return NotificationsCustomSettingsActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1788onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            switch (i) {
                case 0:
                    headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    headerCell = new TextCheckCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    headerCell = new UserCell(this.mContext, 6, 0, false);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    headerCell = new TextColorCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    headerCell = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    headerCell = new TextSettingsCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 6:
                    headerCell = new NotificationsCheckCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    headerCell = new TextCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            return new RecyclerListView.Holder(headerCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            boolean z;
            int i2;
            int i3;
            CharSequence string;
            int i4;
            int i5;
            String string2;
            long j;
            String string3;
            int i6;
            int i7;
            boolean z2 = false;
            boolean z3 = false;
            int i8 = 0;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i != NotificationsCustomSettingsActivity.this.messageSectionRow) {
                        return;
                    }
                    headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                    return;
                case 1:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (i != NotificationsCustomSettingsActivity.this.previewRow) {
                        return;
                    }
                    if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            z = notificationsSettings.getBoolean("EnablePreviewGroup", true);
                        } else {
                            z = notificationsSettings.getBoolean("EnablePreviewChannel", true);
                        }
                    } else {
                        z = notificationsSettings.getBoolean("EnablePreviewAll", true);
                    }
                    textCheckCell.setTextAndCheck(LocaleController.getString("MessagePreview", R.string.MessagePreview), z, true);
                    return;
                case 2:
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    NotificationsSettingsActivity.NotificationException notificationException = (NotificationsSettingsActivity.NotificationException) NotificationsCustomSettingsActivity.this.exceptions.get(i - NotificationsCustomSettingsActivity.this.exceptionsStartRow);
                    if (i != NotificationsCustomSettingsActivity.this.exceptionsEndRow - 1) {
                        z2 = true;
                    }
                    userCell.setException(notificationException, null, z2);
                    return;
                case 3:
                    TextColorCell textColorCell = (TextColorCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings2 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            i2 = notificationsSettings2.getInt("GroupLed", -16776961);
                        } else {
                            i2 = notificationsSettings2.getInt("ChannelLed", -16776961);
                        }
                    } else {
                        i2 = notificationsSettings2.getInt("MessagesLed", -16776961);
                    }
                    while (true) {
                        if (i8 < 9) {
                            if (TextColorCell.colorsToSave[i8] == i2) {
                                i2 = TextColorCell.colors[i8];
                            } else {
                                i8++;
                            }
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), i2, true);
                    return;
                case 4:
                    if (i == NotificationsCustomSettingsActivity.this.deleteAllSectionRow || ((i == NotificationsCustomSettingsActivity.this.groupSection2Row && NotificationsCustomSettingsActivity.this.exceptionsSection2Row == -1) || (i == NotificationsCustomSettingsActivity.this.exceptionsSection2Row && NotificationsCustomSettingsActivity.this.deleteAllRow == -1))) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 5:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings3 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (i == NotificationsCustomSettingsActivity.this.messageSoundRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                            if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                                string2 = notificationsSettings3.getString("GroupSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                                j = notificationsSettings3.getLong("GroupSoundDocId", 0L);
                            } else {
                                string2 = notificationsSettings3.getString("ChannelSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                                j = notificationsSettings3.getLong("ChannelDocId", 0L);
                            }
                        } else {
                            string2 = notificationsSettings3.getString("GlobalSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                            j = notificationsSettings3.getLong("GlobalSoundDocId", 0L);
                        }
                        if (j != 0) {
                            TLRPC$Document document = NotificationsCustomSettingsActivity.this.getMediaDataController().ringtoneDataStore.getDocument(j);
                            if (document == null) {
                                string2 = LocaleController.getString("CustomSound", R.string.CustomSound);
                            } else {
                                string2 = NotificationsSoundActivity.trimTitle(document, FileLoader.getDocumentFileName(document));
                            }
                        } else if (string2.equals("NoSound")) {
                            string2 = LocaleController.getString("NoSound", R.string.NoSound);
                        } else if (string2.equals("Default")) {
                            string2 = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), string2, true);
                        return;
                    } else if (i == NotificationsCustomSettingsActivity.this.messageVibrateRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                            if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                                i5 = notificationsSettings3.getInt("vibrate_group", 0);
                            } else {
                                i5 = notificationsSettings3.getInt("vibrate_channel", 0);
                            }
                        } else {
                            i5 = notificationsSettings3.getInt("vibrate_messages", 0);
                        }
                        if (i5 == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                            return;
                        } else if (i5 == 1) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                            return;
                        } else if (i5 == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                            return;
                        } else if (i5 == 3) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                            return;
                        } else if (i5 != 4) {
                            return;
                        } else {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent), true);
                            return;
                        }
                    } else if (i == NotificationsCustomSettingsActivity.this.messagePriorityRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                            if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                                i4 = notificationsSettings3.getInt("priority_group", 1);
                            } else {
                                i4 = notificationsSettings3.getInt("priority_channel", 1);
                            }
                        } else {
                            i4 = notificationsSettings3.getInt("priority_messages", 1);
                        }
                        if (i4 == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), false);
                            return;
                        } else if (i4 == 1 || i4 == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent), false);
                            return;
                        } else if (i4 == 4) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), false);
                            return;
                        } else if (i4 != 5) {
                            return;
                        } else {
                            textSettingsCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), false);
                            return;
                        }
                    } else if (i != NotificationsCustomSettingsActivity.this.messagePopupNotificationRow) {
                        return;
                    } else {
                        if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                            if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                                i3 = notificationsSettings3.getInt("popupGroup", 0);
                            } else {
                                i3 = notificationsSettings3.getInt("popupChannel", 0);
                            }
                        } else {
                            i3 = notificationsSettings3.getInt("popupAll", 0);
                        }
                        if (i3 == 0) {
                            string = LocaleController.getString("NoPopup", R.string.NoPopup);
                        } else if (i3 == 1) {
                            string = LocaleController.getString("OnlyWhenScreenOn", R.string.OnlyWhenScreenOn);
                        } else if (i3 == 2) {
                            string = LocaleController.getString("OnlyWhenScreenOff", R.string.OnlyWhenScreenOff);
                        } else {
                            string = LocaleController.getString("AlwaysShowPopup", R.string.AlwaysShowPopup);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("PopupNotification", R.string.PopupNotification), string, true);
                        return;
                    }
                case 6:
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) viewHolder.itemView;
                    notificationsCheckCell.setDrawLine(false);
                    StringBuilder sb = new StringBuilder();
                    SharedPreferences notificationsSettings4 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            string3 = LocaleController.getString("NotificationsForGroups", R.string.NotificationsForGroups);
                            i6 = notificationsSettings4.getInt("EnableGroup2", 0);
                        } else {
                            string3 = LocaleController.getString("NotificationsForChannels", R.string.NotificationsForChannels);
                            i6 = notificationsSettings4.getInt("EnableChannel2", 0);
                        }
                    } else {
                        string3 = LocaleController.getString("NotificationsForPrivateChats", R.string.NotificationsForPrivateChats);
                        i6 = notificationsSettings4.getInt("EnableAll2", 0);
                    }
                    String str = string3;
                    int currentTime = NotificationsCustomSettingsActivity.this.getConnectionsManager().getCurrentTime();
                    boolean z4 = i6 < currentTime;
                    if (z4) {
                        sb.append(LocaleController.getString("NotificationsOn", R.string.NotificationsOn));
                    } else if (i6 - 31536000 >= currentTime) {
                        sb.append(LocaleController.getString("NotificationsOff", R.string.NotificationsOff));
                    } else {
                        sb.append(LocaleController.formatString("NotificationsOffUntil", R.string.NotificationsOffUntil, LocaleController.stringForMessageListDate(i6)));
                        i7 = 2;
                        notificationsCheckCell.setTextAndValueAndCheck(str, sb, z4, i7, false);
                        return;
                    }
                    i7 = 0;
                    notificationsCheckCell.setTextAndValueAndCheck(str, sb, z4, i7, false);
                    return;
                case 7:
                    TextCell textCell = (TextCell) viewHolder.itemView;
                    if (i != NotificationsCustomSettingsActivity.this.exceptionsAddRow) {
                        if (i != NotificationsCustomSettingsActivity.this.deleteAllRow) {
                            return;
                        }
                        textCell.setText(LocaleController.getString("NotificationsDeleteAllException", R.string.NotificationsDeleteAllException), false);
                        textCell.setColors(null, "windowBackgroundWhiteRedText5");
                        return;
                    }
                    String string4 = LocaleController.getString("NotificationsAddAnException", R.string.NotificationsAddAnException);
                    int i9 = R.drawable.msg_contact_add;
                    if (NotificationsCustomSettingsActivity.this.exceptionsStartRow != -1) {
                        z3 = true;
                    }
                    textCell.setTextAndIcon(string4, i9, z3);
                    textCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (NotificationsCustomSettingsActivity.this.exceptions == null || !NotificationsCustomSettingsActivity.this.exceptions.isEmpty()) {
                return;
            }
            boolean isGlobalNotificationsEnabled = NotificationsCustomSettingsActivity.this.getNotificationsController().isGlobalNotificationsEnabled(NotificationsCustomSettingsActivity.this.currentType);
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (viewHolder.getAdapterPosition() == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                    headerCell.setEnabled(isGlobalNotificationsEnabled, null);
                } else {
                    headerCell.setEnabled(true, null);
                }
            } else if (itemViewType == 1) {
                ((TextCheckCell) viewHolder.itemView).setEnabled(isGlobalNotificationsEnabled, null);
            } else if (itemViewType == 3) {
                ((TextColorCell) viewHolder.itemView).setEnabled(isGlobalNotificationsEnabled, null);
            } else if (itemViewType != 5) {
            } else {
                ((TextSettingsCell) viewHolder.itemView).setEnabled(isGlobalNotificationsEnabled, null);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                NotificationsCustomSettingsActivity.this.lambda$getThemeDescriptions$11();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextColorCell.class, TextSettingsCell.class, UserCell.class, NotificationsCheckCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, "avatar_text"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$11() {
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

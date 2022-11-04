package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_dialogFilter;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_messages_updateDialogFilter;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.FilterUsersActivity;
/* loaded from: classes3.dex */
public class FilterCreateActivity extends BaseFragment {
    private ListAdapter adapter;
    private boolean creatingNew;
    private ActionBarMenuItem doneItem;
    private int excludeAddRow;
    private int excludeArchivedRow;
    private int excludeEndRow;
    private boolean excludeExpanded;
    private int excludeHeaderRow;
    private int excludeMutedRow;
    private int excludeReadRow;
    private int excludeSectionRow;
    private int excludeShowMoreRow;
    private int excludeStartRow;
    private MessagesController.DialogFilter filter;
    private boolean hasUserChanged;
    private int imageRow;
    private int includeAddRow;
    private int includeBotsRow;
    private int includeChannelsRow;
    private int includeContactsRow;
    private int includeEndRow;
    private boolean includeExpanded;
    private int includeGroupsRow;
    private int includeHeaderRow;
    private int includeNonContactsRow;
    private int includeSectionRow;
    private int includeShowMoreRow;
    private int includeStartRow;
    private RecyclerListView listView;
    private boolean nameChangedManually;
    private int namePreSectionRow;
    private int nameRow;
    private int nameSectionRow;
    private ArrayList<Long> newAlwaysShow;
    private int newFilterFlags;
    private String newFilterName;
    private ArrayList<Long> newNeverShow;
    private LongSparseIntArray newPinned;
    private int removeRow;
    private int removeSectionRow;
    private int rowCount;

    /* loaded from: classes3.dex */
    public static class HintInnerCell extends FrameLayout {
        private RLottieImageView imageView;

        public HintInnerCell(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.filter_new, 100, 100);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.playAnimation();
            addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$HintInnerCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FilterCreateActivity.HintInnerCell.this.lambda$new$0(view);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(156.0f), NUM));
        }
    }

    public FilterCreateActivity() {
        this(null, null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter) {
        this(dialogFilter, null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter, ArrayList<Long> arrayList) {
        this.rowCount = 0;
        this.filter = dialogFilter;
        if (dialogFilter == null) {
            MessagesController.DialogFilter dialogFilter2 = new MessagesController.DialogFilter();
            this.filter = dialogFilter2;
            dialogFilter2.id = 2;
            while (getMessagesController().dialogFiltersById.get(this.filter.id) != null) {
                this.filter.id++;
            }
            this.filter.name = "";
            this.creatingNew = true;
        }
        MessagesController.DialogFilter dialogFilter3 = this.filter;
        this.newFilterName = dialogFilter3.name;
        this.newFilterFlags = dialogFilter3.flags;
        ArrayList<Long> arrayList2 = new ArrayList<>(this.filter.alwaysShow);
        this.newAlwaysShow = arrayList2;
        if (arrayList != null) {
            arrayList2.addAll(arrayList);
        }
        this.newNeverShow = new ArrayList<>(this.filter.neverShow);
        this.newPinned = this.filter.pinnedDialogs.clone();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        updateRows();
        return super.onFragmentCreate();
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.creatingNew) {
            this.rowCount = 0 + 1;
            this.imageRow = 0;
            this.namePreSectionRow = -1;
        } else {
            this.imageRow = -1;
            this.rowCount = 0 + 1;
            this.namePreSectionRow = 0;
        }
        int i = this.rowCount;
        int i2 = i + 1;
        this.rowCount = i2;
        this.nameRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.nameSectionRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.includeHeaderRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.includeAddRow = i4;
        int i6 = this.newFilterFlags;
        if ((MessagesController.DIALOG_FILTER_FLAG_CONTACTS & i6) != 0) {
            this.rowCount = i5 + 1;
            this.includeContactsRow = i5;
        } else {
            this.includeContactsRow = -1;
        }
        if ((MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS & i6) != 0) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.includeNonContactsRow = i7;
        } else {
            this.includeNonContactsRow = -1;
        }
        if ((MessagesController.DIALOG_FILTER_FLAG_GROUPS & i6) != 0) {
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.includeGroupsRow = i8;
        } else {
            this.includeGroupsRow = -1;
        }
        if ((MessagesController.DIALOG_FILTER_FLAG_CHANNELS & i6) != 0) {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.includeChannelsRow = i9;
        } else {
            this.includeChannelsRow = -1;
        }
        if ((MessagesController.DIALOG_FILTER_FLAG_BOTS & i6) != 0) {
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.includeBotsRow = i10;
        } else {
            this.includeBotsRow = -1;
        }
        if (!this.newAlwaysShow.isEmpty()) {
            this.includeStartRow = this.rowCount;
            int size = (this.includeExpanded || this.newAlwaysShow.size() < 8) ? this.newAlwaysShow.size() : Math.min(5, this.newAlwaysShow.size());
            int i11 = this.rowCount + size;
            this.rowCount = i11;
            this.includeEndRow = i11;
            if (size != this.newAlwaysShow.size()) {
                int i12 = this.rowCount;
                this.rowCount = i12 + 1;
                this.includeShowMoreRow = i12;
            } else {
                this.includeShowMoreRow = -1;
            }
        } else {
            this.includeStartRow = -1;
            this.includeEndRow = -1;
            this.includeShowMoreRow = -1;
        }
        int i13 = this.rowCount;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.includeSectionRow = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.excludeHeaderRow = i14;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.excludeAddRow = i15;
        int i17 = this.newFilterFlags;
        if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED & i17) != 0) {
            this.rowCount = i16 + 1;
            this.excludeMutedRow = i16;
        } else {
            this.excludeMutedRow = -1;
        }
        if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ & i17) != 0) {
            int i18 = this.rowCount;
            this.rowCount = i18 + 1;
            this.excludeReadRow = i18;
        } else {
            this.excludeReadRow = -1;
        }
        if ((i17 & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
            int i19 = this.rowCount;
            this.rowCount = i19 + 1;
            this.excludeArchivedRow = i19;
        } else {
            this.excludeArchivedRow = -1;
        }
        if (!this.newNeverShow.isEmpty()) {
            this.excludeStartRow = this.rowCount;
            int size2 = (this.excludeExpanded || this.newNeverShow.size() < 8) ? this.newNeverShow.size() : Math.min(5, this.newNeverShow.size());
            int i20 = this.rowCount + size2;
            this.rowCount = i20;
            this.excludeEndRow = i20;
            if (size2 != this.newNeverShow.size()) {
                int i21 = this.rowCount;
                this.rowCount = i21 + 1;
                this.excludeShowMoreRow = i21;
            } else {
                this.excludeShowMoreRow = -1;
            }
        } else {
            this.excludeStartRow = -1;
            this.excludeEndRow = -1;
            this.excludeShowMoreRow = -1;
        }
        int i22 = this.rowCount;
        int i23 = i22 + 1;
        this.rowCount = i23;
        this.excludeSectionRow = i22;
        if (!this.creatingNew) {
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.removeRow = i23;
            this.rowCount = i24 + 1;
            this.removeSectionRow = i24;
        } else {
            this.removeRow = -1;
            this.removeSectionRow = -1;
        }
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.creatingNew) {
            this.actionBar.setTitle(LocaleController.getString("FilterNew", R.string.FilterNew));
        } else {
            TextPaint textPaint = new TextPaint(1);
            textPaint.setTextSize(AndroidUtilities.dp(20.0f));
            this.actionBar.setTitle(Emoji.replaceEmoji(this.filter.name, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.FilterCreateActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    if (!FilterCreateActivity.this.checkDiscard()) {
                        return;
                    }
                    FilterCreateActivity.this.finishFragment();
                } else if (i != 1) {
                } else {
                    FilterCreateActivity.this.processDone();
                }
            }
        });
        this.doneItem = createMenu.addItem(1, LocaleController.getString("Save", R.string.Save).toUpperCase());
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(this, context) { // from class: org.telegram.ui.FilterCreateActivity.2
            @Override // android.view.ViewGroup, android.view.View
            public boolean requestFocus(int i, Rect rect) {
                return false;
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                FilterCreateActivity.this.lambda$createView$4(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda13
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                boolean lambda$createView$5;
                lambda$createView$5 = FilterCreateActivity.this.lambda$createView$5(view, i);
                return lambda$createView$5;
            }
        });
        checkDoneButton(false);
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view, final int i) {
        if (getParentActivity() == null) {
            return;
        }
        boolean z = true;
        if (i == this.includeShowMoreRow) {
            this.includeExpanded = true;
            updateRows();
        } else if (i == this.excludeShowMoreRow) {
            this.excludeExpanded = true;
            updateRows();
        } else {
            int i2 = this.includeAddRow;
            if (i == i2 || i == this.excludeAddRow) {
                ArrayList<Long> arrayList = i == this.excludeAddRow ? this.newNeverShow : this.newAlwaysShow;
                if (i != i2) {
                    z = false;
                }
                FilterUsersActivity filterUsersActivity = new FilterUsersActivity(z, arrayList, this.newFilterFlags);
                filterUsersActivity.setDelegate(new FilterUsersActivity.FilterUsersActivityDelegate() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda14
                    @Override // org.telegram.ui.FilterUsersActivity.FilterUsersActivityDelegate
                    public final void didSelectChats(ArrayList arrayList2, int i3) {
                        FilterCreateActivity.this.lambda$createView$0(i, arrayList2, i3);
                    }
                });
                presentFragment(filterUsersActivity);
            } else if (i == this.removeRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("FilterDelete", R.string.FilterDelete));
                builder.setMessage(LocaleController.getString("FilterDeleteAlert", R.string.FilterDeleteAlert));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        FilterCreateActivity.this.lambda$createView$3(dialogInterface, i3);
                    }
                });
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView == null) {
                    return;
                }
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            } else if (i == this.nameRow) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
                pollEditTextCell.getTextView().requestFocus();
                AndroidUtilities.showKeyboard(pollEditTextCell.getTextView());
            } else if (!(view instanceof UserCell)) {
            } else {
                UserCell userCell = (UserCell) view;
                CharSequence name = userCell.getName();
                Object currentObject = userCell.getCurrentObject();
                if (i >= this.includeSectionRow) {
                    z = false;
                }
                showRemoveAlert(i, name, currentObject, z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(int i, ArrayList arrayList, int i2) {
        this.newFilterFlags = i2;
        if (i == this.excludeAddRow) {
            this.newNeverShow = arrayList;
            for (int i3 = 0; i3 < this.newNeverShow.size(); i3++) {
                Long l = this.newNeverShow.get(i3);
                this.newAlwaysShow.remove(l);
                this.newPinned.delete(l.longValue());
            }
        } else {
            this.newAlwaysShow = arrayList;
            for (int i4 = 0; i4 < this.newAlwaysShow.size(); i4++) {
                this.newNeverShow.remove(this.newAlwaysShow.get(i4));
            }
            ArrayList arrayList2 = new ArrayList();
            int size = this.newPinned.size();
            for (int i5 = 0; i5 < size; i5++) {
                Long valueOf = Long.valueOf(this.newPinned.keyAt(i5));
                if (!DialogObject.isEncryptedDialog(valueOf.longValue()) && !this.newAlwaysShow.contains(valueOf)) {
                    arrayList2.add(valueOf);
                }
            }
            int size2 = arrayList2.size();
            for (int i6 = 0; i6 < size2; i6++) {
                this.newPinned.delete(((Long) arrayList2.get(i6)).longValue());
            }
        }
        fillFilterName();
        checkDoneButton(false);
        updateRows();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(DialogInterface dialogInterface, int i) {
        final AlertDialog alertDialog;
        if (getParentActivity() != null) {
            alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.show();
        } else {
            alertDialog = null;
        }
        TLRPC$TL_messages_updateDialogFilter tLRPC$TL_messages_updateDialogFilter = new TLRPC$TL_messages_updateDialogFilter();
        tLRPC$TL_messages_updateDialogFilter.id = this.filter.id;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_updateDialogFilter, new RequestDelegate() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                FilterCreateActivity.this.lambda$createView$2(alertDialog, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(final AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                FilterCreateActivity.this.lambda$createView$1(alertDialog);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(AlertDialog alertDialog) {
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        getMessagesController().removeFilter(this.filter);
        getMessagesStorage().deleteDialogFilter(this.filter);
        finishFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$5(View view, int i) {
        boolean z = false;
        if (view instanceof UserCell) {
            UserCell userCell = (UserCell) view;
            CharSequence name = userCell.getName();
            Object currentObject = userCell.getCurrentObject();
            if (i < this.includeSectionRow) {
                z = true;
            }
            showRemoveAlert(i, name, currentObject, z);
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        return checkDiscard();
    }

    private void fillFilterName() {
        String string;
        if (this.creatingNew) {
            if (!TextUtils.isEmpty(this.newFilterName) && this.nameChangedManually) {
                return;
            }
            int i = this.newFilterFlags;
            int i2 = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
            int i3 = i & i2;
            String str = "";
            if ((i3 & i2) == i2) {
                if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ & i) != 0) {
                    string = LocaleController.getString("FilterNameUnread", R.string.FilterNameUnread);
                } else {
                    if ((i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                        string = LocaleController.getString("FilterNameNonMuted", R.string.FilterNameNonMuted);
                    }
                    string = str;
                }
            } else {
                int i4 = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                if ((i3 & i4) != 0) {
                    if (((i4 ^ (-1)) & i3) == 0) {
                        string = LocaleController.getString("FilterContacts", R.string.FilterContacts);
                    }
                    string = str;
                } else {
                    int i5 = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                    if ((i3 & i5) != 0) {
                        if (((i5 ^ (-1)) & i3) == 0) {
                            string = LocaleController.getString("FilterNonContacts", R.string.FilterNonContacts);
                        }
                        string = str;
                    } else {
                        int i6 = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                        if ((i3 & i6) != 0) {
                            if (((i6 ^ (-1)) & i3) == 0) {
                                string = LocaleController.getString("FilterGroups", R.string.FilterGroups);
                            }
                            string = str;
                        } else {
                            int i7 = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                            if ((i3 & i7) != 0) {
                                if (((i7 ^ (-1)) & i3) == 0) {
                                    string = LocaleController.getString("FilterBots", R.string.FilterBots);
                                }
                                string = str;
                            } else {
                                int i8 = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                                if ((i3 & i8) != 0 && ((i8 ^ (-1)) & i3) == 0) {
                                    string = LocaleController.getString("FilterChannels", R.string.FilterChannels);
                                }
                                string = str;
                            }
                        }
                    }
                }
            }
            if (string == null || string.length() <= 12) {
                str = string;
            }
            this.newFilterName = str;
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.nameRow);
            if (findViewHolderForAdapterPosition == null) {
                return;
            }
            this.adapter.onViewAttachedToWindow(findViewHolderForAdapterPosition);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkDiscard() {
        if (this.doneItem.getAlpha() == 1.0f) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            if (this.creatingNew) {
                builder.setTitle(LocaleController.getString("FilterDiscardNewTitle", R.string.FilterDiscardNewTitle));
                builder.setMessage(LocaleController.getString("FilterDiscardNewAlert", R.string.FilterDiscardNewAlert));
                builder.setPositiveButton(LocaleController.getString("FilterDiscardNewSave", R.string.FilterDiscardNewSave), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FilterCreateActivity.this.lambda$checkDiscard$6(dialogInterface, i);
                    }
                });
            } else {
                builder.setTitle(LocaleController.getString("FilterDiscardTitle", R.string.FilterDiscardTitle));
                builder.setMessage(LocaleController.getString("FilterDiscardAlert", R.string.FilterDiscardAlert));
                builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FilterCreateActivity.this.lambda$checkDiscard$7(dialogInterface, i);
                    }
                });
            }
            builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FilterCreateActivity.this.lambda$checkDiscard$8(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$6(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$7(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$8(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    private void showRemoveAlert(final int i, CharSequence charSequence, Object obj, final boolean z) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (z) {
            builder.setTitle(LocaleController.getString("FilterRemoveInclusionTitle", R.string.FilterRemoveInclusionTitle));
            if (obj instanceof String) {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionText", R.string.FilterRemoveInclusionText, charSequence));
            } else if (obj instanceof TLRPC$User) {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionUserText", R.string.FilterRemoveInclusionUserText, charSequence));
            } else {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionChatText", R.string.FilterRemoveInclusionChatText, charSequence));
            }
        } else {
            builder.setTitle(LocaleController.getString("FilterRemoveExclusionTitle", R.string.FilterRemoveExclusionTitle));
            if (obj instanceof String) {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionText", R.string.FilterRemoveExclusionText, charSequence));
            } else if (obj instanceof TLRPC$User) {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionUserText", R.string.FilterRemoveExclusionUserText, charSequence));
            } else {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionChatText", R.string.FilterRemoveExclusionChatText, charSequence));
            }
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("StickersRemove", R.string.StickersRemove), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                FilterCreateActivity.this.lambda$showRemoveAlert$9(i, z, dialogInterface, i2);
            }
        });
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showRemoveAlert$9(int i, boolean z, DialogInterface dialogInterface, int i2) {
        if (i == this.includeContactsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ (-1);
        } else if (i == this.includeNonContactsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ (-1);
        } else if (i == this.includeGroupsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ (-1);
        } else if (i == this.includeChannelsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ (-1);
        } else if (i == this.includeBotsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_BOTS ^ (-1);
        } else if (i == this.excludeArchivedRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ (-1);
        } else if (i == this.excludeMutedRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ (-1);
        } else if (i == this.excludeReadRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ (-1);
        } else if (z) {
            this.newAlwaysShow.remove(i - this.includeStartRow);
        } else {
            this.newNeverShow.remove(i - this.excludeStartRow);
        }
        fillFilterName();
        updateRows();
        checkDoneButton(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processDone() {
        saveFilterToServer(this.filter, this.newFilterFlags, this.newFilterName, this.newAlwaysShow, this.newNeverShow, this.newPinned, this.creatingNew, false, this.hasUserChanged, true, true, this, new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                FilterCreateActivity.this.lambda$processDone$10();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$10() {
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        finishFragment();
    }

    private static void processAddFilter(MessagesController.DialogFilter dialogFilter, int i, String str, ArrayList<Long> arrayList, ArrayList<Long> arrayList2, boolean z, boolean z2, boolean z3, boolean z4, BaseFragment baseFragment, Runnable runnable) {
        if (dialogFilter.flags != i || z3) {
            dialogFilter.pendingUnreadCount = -1;
            if (z4) {
                dialogFilter.unreadCount = -1;
            }
        }
        dialogFilter.flags = i;
        dialogFilter.name = str;
        dialogFilter.neverShow = arrayList2;
        dialogFilter.alwaysShow = arrayList;
        if (z) {
            baseFragment.getMessagesController().addFilter(dialogFilter, z2);
        } else {
            baseFragment.getMessagesController().onFilterUpdate(dialogFilter);
        }
        baseFragment.getMessagesStorage().saveDialogFilter(dialogFilter, z2, true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void saveFilterToServer(final MessagesController.DialogFilter dialogFilter, final int i, final String str, final ArrayList<Long> arrayList, final ArrayList<Long> arrayList2, final LongSparseIntArray longSparseIntArray, final boolean z, final boolean z2, final boolean z3, final boolean z4, final boolean z5, final BaseFragment baseFragment, final Runnable runnable) {
        AlertDialog alertDialog;
        ArrayList<TLRPC$InputPeer> arrayList3;
        ArrayList<Long> arrayList4;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        int i2 = 3;
        if (z5) {
            alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.show();
        } else {
            alertDialog = null;
        }
        TLRPC$TL_messages_updateDialogFilter tLRPC$TL_messages_updateDialogFilter = new TLRPC$TL_messages_updateDialogFilter();
        tLRPC$TL_messages_updateDialogFilter.id = dialogFilter.id;
        int i3 = 1;
        tLRPC$TL_messages_updateDialogFilter.flags |= 1;
        TLRPC$TL_dialogFilter tLRPC$TL_dialogFilter = new TLRPC$TL_dialogFilter();
        tLRPC$TL_messages_updateDialogFilter.filter = tLRPC$TL_dialogFilter;
        tLRPC$TL_dialogFilter.contacts = (i & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0;
        tLRPC$TL_dialogFilter.non_contacts = (i & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0;
        tLRPC$TL_dialogFilter.groups = (i & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0;
        tLRPC$TL_dialogFilter.broadcasts = (i & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0;
        tLRPC$TL_dialogFilter.bots = (i & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0;
        tLRPC$TL_dialogFilter.exclude_muted = (i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0;
        tLRPC$TL_dialogFilter.exclude_read = (i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0;
        tLRPC$TL_dialogFilter.exclude_archived = (i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0;
        tLRPC$TL_dialogFilter.id = dialogFilter.id;
        tLRPC$TL_dialogFilter.title = str;
        MessagesController messagesController = baseFragment.getMessagesController();
        ArrayList<Long> arrayList5 = new ArrayList<>();
        if (longSparseIntArray.size() != 0) {
            int size = longSparseIntArray.size();
            for (int i4 = 0; i4 < size; i4++) {
                long keyAt = longSparseIntArray.keyAt(i4);
                if (!DialogObject.isEncryptedDialog(keyAt)) {
                    arrayList5.add(Long.valueOf(keyAt));
                }
            }
            Collections.sort(arrayList5, new Comparator() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda8
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$saveFilterToServer$11;
                    lambda$saveFilterToServer$11 = FilterCreateActivity.lambda$saveFilterToServer$11(LongSparseIntArray.this, (Long) obj, (Long) obj2);
                    return lambda$saveFilterToServer$11;
                }
            });
        }
        int i5 = 0;
        while (i5 < i2) {
            if (i5 == 0) {
                arrayList3 = tLRPC$TL_messages_updateDialogFilter.filter.include_peers;
                arrayList4 = arrayList;
            } else if (i5 == i3) {
                arrayList3 = tLRPC$TL_messages_updateDialogFilter.filter.exclude_peers;
                arrayList4 = arrayList2;
            } else {
                arrayList3 = tLRPC$TL_messages_updateDialogFilter.filter.pinned_peers;
                arrayList4 = arrayList5;
            }
            int size2 = arrayList4.size();
            for (int i6 = 0; i6 < size2; i6++) {
                long longValue = arrayList4.get(i6).longValue();
                if ((i5 != 0 || longSparseIntArray.indexOfKey(longValue) < 0) && !DialogObject.isEncryptedDialog(longValue)) {
                    if (longValue > 0) {
                        TLRPC$User user = messagesController.getUser(Long.valueOf(longValue));
                        if (user != null) {
                            TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                            tLRPC$TL_inputPeerUser.user_id = longValue;
                            tLRPC$TL_inputPeerUser.access_hash = user.access_hash;
                            arrayList3.add(tLRPC$TL_inputPeerUser);
                        }
                    } else {
                        long j = -longValue;
                        TLRPC$Chat chat = messagesController.getChat(Long.valueOf(j));
                        if (chat != null) {
                            if (ChatObject.isChannel(chat)) {
                                TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
                                tLRPC$TL_inputPeerChannel.channel_id = j;
                                tLRPC$TL_inputPeerChannel.access_hash = chat.access_hash;
                                arrayList3.add(tLRPC$TL_inputPeerChannel);
                            } else {
                                TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
                                tLRPC$TL_inputPeerChat.chat_id = j;
                                arrayList3.add(tLRPC$TL_inputPeerChat);
                            }
                        }
                    }
                }
            }
            i5++;
            i2 = 3;
            i3 = 1;
        }
        final AlertDialog alertDialog2 = alertDialog;
        baseFragment.getConnectionsManager().sendRequest(tLRPC$TL_messages_updateDialogFilter, new RequestDelegate() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda10
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                FilterCreateActivity.lambda$saveFilterToServer$13(z5, alertDialog2, dialogFilter, i, str, arrayList, arrayList2, z, z2, z3, z4, baseFragment, runnable, tLObject, tLRPC$TL_error);
            }
        });
        if (z5) {
            return;
        }
        processAddFilter(dialogFilter, i, str, arrayList, arrayList2, z, z2, z3, z4, baseFragment, runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$saveFilterToServer$11(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveFilterToServer$13(final boolean z, final AlertDialog alertDialog, final MessagesController.DialogFilter dialogFilter, final int i, final String str, final ArrayList arrayList, final ArrayList arrayList2, final boolean z2, final boolean z3, final boolean z4, final boolean z5, final BaseFragment baseFragment, final Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                FilterCreateActivity.lambda$saveFilterToServer$12(z, alertDialog, dialogFilter, i, str, arrayList, arrayList2, z2, z3, z4, z5, baseFragment, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveFilterToServer$12(boolean z, AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter, int i, String str, ArrayList arrayList, ArrayList arrayList2, boolean z2, boolean z3, boolean z4, boolean z5, BaseFragment baseFragment, Runnable runnable) {
        if (z) {
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            processAddFilter(dialogFilter, i, str, arrayList, arrayList2, z2, z3, z4, z5, baseFragment, runnable);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean canBeginSlide() {
        return checkDiscard();
    }

    private boolean hasChanges() {
        this.hasUserChanged = false;
        if (this.filter.alwaysShow.size() != this.newAlwaysShow.size()) {
            this.hasUserChanged = true;
        }
        if (this.filter.neverShow.size() != this.newNeverShow.size()) {
            this.hasUserChanged = true;
        }
        if (!this.hasUserChanged) {
            Collections.sort(this.filter.alwaysShow);
            Collections.sort(this.newAlwaysShow);
            if (!this.filter.alwaysShow.equals(this.newAlwaysShow)) {
                this.hasUserChanged = true;
            }
            Collections.sort(this.filter.neverShow);
            Collections.sort(this.newNeverShow);
            if (!this.filter.neverShow.equals(this.newNeverShow)) {
                this.hasUserChanged = true;
            }
        }
        if (TextUtils.equals(this.filter.name, this.newFilterName) && this.filter.flags == this.newFilterFlags) {
            return this.hasUserChanged;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkDoneButton(boolean z) {
        boolean z2 = true;
        boolean z3 = !TextUtils.isEmpty(this.newFilterName) && this.newFilterName.length() <= 12;
        if (z3) {
            if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == 0 && this.newAlwaysShow.isEmpty()) {
                z2 = false;
            }
            z3 = (!z2 || this.creatingNew) ? z2 : hasChanges();
        }
        if (this.doneItem.isEnabled() == z3) {
            return;
        }
        this.doneItem.setEnabled(z3);
        float f = 1.0f;
        if (z) {
            ViewPropertyAnimator scaleX = this.doneItem.animate().alpha(z3 ? 1.0f : 0.0f).scaleX(z3 ? 1.0f : 0.0f);
            if (!z3) {
                f = 0.0f;
            }
            scaleX.scaleY(f).setDuration(180L).start();
            return;
        }
        this.doneItem.setAlpha(z3 ? 1.0f : 0.0f);
        this.doneItem.setScaleX(z3 ? 1.0f : 0.0f);
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if (!z3) {
            f = 0.0f;
        }
        actionBarMenuItem.setScaleY(f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTextLeft(View view) {
        if (view instanceof PollEditTextCell) {
            PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
            String str = this.newFilterName;
            int length = 12 - (str != null ? str.length() : 0);
            if (length <= 3.6000004f) {
                pollEditTextCell.setText2(String.format("%d", Integer.valueOf(length)));
                SimpleTextView textView2 = pollEditTextCell.getTextView2();
                String str2 = length < 0 ? "windowBackgroundWhiteRedText5" : "windowBackgroundWhiteGrayText3";
                textView2.setTextColor(Theme.getColor(str2));
                textView2.setTag(str2);
                textView2.setAlpha((pollEditTextCell.getTextView().isFocused() || length < 0) ? 1.0f : 0.0f);
                return;
            }
            pollEditTextCell.setText2("");
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
            return (itemViewType == 3 || itemViewType == 0 || itemViewType == 2 || itemViewType == 5) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return FilterCreateActivity.this.rowCount;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(PollEditTextCell pollEditTextCell, View view, boolean z) {
            pollEditTextCell.getTextView2().setAlpha((z || FilterCreateActivity.this.newFilterName.length() > 12) ? 1.0f : 0.0f);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1817onCreateViewHolder(ViewGroup viewGroup, int i) {
            PollEditTextCell pollEditTextCell;
            View view;
            if (i == 0) {
                FrameLayout headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                pollEditTextCell = headerCell;
            } else if (i == 1) {
                UserCell userCell = new UserCell(this.mContext, 6, 0, false);
                userCell.setSelfAsSavedMessages(true);
                userCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                pollEditTextCell = userCell;
            } else if (i == 2) {
                final PollEditTextCell pollEditTextCell2 = new PollEditTextCell(this.mContext, null);
                pollEditTextCell2.createErrorTextView();
                pollEditTextCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                pollEditTextCell2.addTextWatcher(new TextWatcher() { // from class: org.telegram.ui.FilterCreateActivity.ListAdapter.1
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable editable) {
                        if (pollEditTextCell2.getTag() != null) {
                            return;
                        }
                        String obj = editable.toString();
                        if (!TextUtils.equals(obj, FilterCreateActivity.this.newFilterName)) {
                            FilterCreateActivity.this.nameChangedManually = !TextUtils.isEmpty(obj);
                            FilterCreateActivity.this.newFilterName = obj;
                        }
                        RecyclerView.ViewHolder findViewHolderForAdapterPosition = FilterCreateActivity.this.listView.findViewHolderForAdapterPosition(FilterCreateActivity.this.nameRow);
                        if (findViewHolderForAdapterPosition != null) {
                            FilterCreateActivity.this.setTextLeft(findViewHolderForAdapterPosition.itemView);
                        }
                        FilterCreateActivity.this.checkDoneButton(true);
                    }
                });
                EditTextBoldCursor textView = pollEditTextCell2.getTextView();
                pollEditTextCell2.setShowNextButton(true);
                textView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.FilterCreateActivity$ListAdapter$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view2, boolean z) {
                        FilterCreateActivity.ListAdapter.this.lambda$onCreateViewHolder$0(pollEditTextCell2, view2, z);
                    }
                });
                textView.setImeOptions(NUM);
                pollEditTextCell = pollEditTextCell2;
            } else {
                if (i == 3) {
                    view = new ShadowSectionCell(this.mContext);
                } else if (i == 4) {
                    FrameLayout textCell = new TextCell(this.mContext);
                    textCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    pollEditTextCell = textCell;
                } else if (i == 5) {
                    view = new HintInnerCell(this.mContext);
                } else {
                    view = new TextInfoPrivacyCell(this.mContext);
                }
                return new RecyclerListView.Holder(view);
            }
            view = pollEditTextCell;
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 2) {
                FilterCreateActivity.this.setTextLeft(viewHolder.itemView);
                PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell.setTag(1);
                pollEditTextCell.setTextAndHint(FilterCreateActivity.this.newFilterName != null ? FilterCreateActivity.this.newFilterName : "", LocaleController.getString("FilterNameHint", R.string.FilterNameHint), false);
                pollEditTextCell.setTag(null);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 2) {
                EditTextBoldCursor textView = ((PollEditTextCell) viewHolder.itemView).getTextView();
                if (!textView.isFocused()) {
                    return;
                }
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:57:0x0184, code lost:
            if (r12 == (r10.this$0.includeEndRow - 1)) goto L68;
         */
        /* JADX WARN: Code restructure failed: missing block: B:59:0x0187, code lost:
            r3 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:68:0x01bd, code lost:
            if (r12 == (r10.this$0.excludeEndRow - 1)) goto L68;
         */
        /* JADX WARN: Removed duplicated region for block: B:72:0x01ca  */
        /* JADX WARN: Removed duplicated region for block: B:83:0x0201  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
                Method dump skipped, instructions count: 898
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilterCreateActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == FilterCreateActivity.this.includeHeaderRow || i == FilterCreateActivity.this.excludeHeaderRow) {
                return 0;
            }
            if (i >= FilterCreateActivity.this.includeStartRow && i < FilterCreateActivity.this.includeEndRow) {
                return 1;
            }
            if ((i >= FilterCreateActivity.this.excludeStartRow && i < FilterCreateActivity.this.excludeEndRow) || i == FilterCreateActivity.this.includeContactsRow || i == FilterCreateActivity.this.includeNonContactsRow || i == FilterCreateActivity.this.includeGroupsRow || i == FilterCreateActivity.this.includeChannelsRow || i == FilterCreateActivity.this.includeBotsRow || i == FilterCreateActivity.this.excludeReadRow || i == FilterCreateActivity.this.excludeArchivedRow || i == FilterCreateActivity.this.excludeMutedRow) {
                return 1;
            }
            if (i == FilterCreateActivity.this.nameRow) {
                return 2;
            }
            if (i == FilterCreateActivity.this.nameSectionRow || i == FilterCreateActivity.this.namePreSectionRow || i == FilterCreateActivity.this.removeSectionRow) {
                return 3;
            }
            if (i == FilterCreateActivity.this.imageRow) {
                return 5;
            }
            return (i == FilterCreateActivity.this.includeSectionRow || i == FilterCreateActivity.this.excludeSectionRow) ? 6 : 4;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                FilterCreateActivity.this.lambda$getThemeDescriptions$14();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, PollEditTextCell.class, UserCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"ImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
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
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$14() {
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

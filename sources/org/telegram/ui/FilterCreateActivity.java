package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.View;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
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

public class FilterCreateActivity extends BaseFragment {
    private ListAdapter adapter;
    private boolean creatingNew;
    private ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public int excludeAddRow;
    /* access modifiers changed from: private */
    public int excludeArchivedRow;
    /* access modifiers changed from: private */
    public int excludeEndRow;
    private boolean excludeExpanded;
    /* access modifiers changed from: private */
    public int excludeHeaderRow;
    /* access modifiers changed from: private */
    public int excludeMutedRow;
    /* access modifiers changed from: private */
    public int excludeReadRow;
    /* access modifiers changed from: private */
    public int excludeSectionRow;
    /* access modifiers changed from: private */
    public int excludeShowMoreRow;
    /* access modifiers changed from: private */
    public int excludeStartRow;
    private MessagesController.DialogFilter filter;
    private boolean hasUserChanged;
    /* access modifiers changed from: private */
    public int imageRow;
    /* access modifiers changed from: private */
    public int includeAddRow;
    /* access modifiers changed from: private */
    public int includeBotsRow;
    /* access modifiers changed from: private */
    public int includeChannelsRow;
    /* access modifiers changed from: private */
    public int includeContactsRow;
    /* access modifiers changed from: private */
    public int includeEndRow;
    private boolean includeExpanded;
    /* access modifiers changed from: private */
    public int includeGroupsRow;
    /* access modifiers changed from: private */
    public int includeHeaderRow;
    /* access modifiers changed from: private */
    public int includeNonContactsRow;
    /* access modifiers changed from: private */
    public int includeSectionRow;
    /* access modifiers changed from: private */
    public int includeShowMoreRow;
    /* access modifiers changed from: private */
    public int includeStartRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int namePreSectionRow;
    /* access modifiers changed from: private */
    public int nameRow;
    /* access modifiers changed from: private */
    public int nameSectionRow;
    /* access modifiers changed from: private */
    public ArrayList<Integer> newAlwaysShow;
    private int newFilterFlags;
    /* access modifiers changed from: private */
    public String newFilterName;
    /* access modifiers changed from: private */
    public ArrayList<Integer> newNeverShow;
    private LongSparseArray<Integer> newPinned;
    /* access modifiers changed from: private */
    public int removeRow;
    /* access modifiers changed from: private */
    public int removeSectionRow;
    /* access modifiers changed from: private */
    public int rowCount;

    public static class HintInnerCell extends FrameLayout {
        private RLottieImageView imageView;

        public HintInnerCell(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(NUM, 100, 100);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.playAnimation();
            addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    FilterCreateActivity.HintInnerCell.this.lambda$new$0$FilterCreateActivity$HintInnerCell(view);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$FilterCreateActivity$HintInnerCell(View view) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(156.0f), NUM));
        }
    }

    public FilterCreateActivity() {
        this((MessagesController.DialogFilter) null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter) {
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
        this.newAlwaysShow = new ArrayList<>(this.filter.alwaysShow);
        this.newNeverShow = new ArrayList<>(this.filter.neverShow);
        this.newPinned = this.filter.pinnedDialogs.clone();
    }

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
        this.rowCount = i3 + 1;
        this.includeHeaderRow = i3;
        if (this.newAlwaysShow.size() < 100) {
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.includeAddRow = i4;
        } else {
            this.includeAddRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.includeContactsRow = i5;
        } else {
            this.includeContactsRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.includeNonContactsRow = i6;
        } else {
            this.includeNonContactsRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.includeGroupsRow = i7;
        } else {
            this.includeGroupsRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.includeChannelsRow = i8;
        } else {
            this.includeChannelsRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.includeBotsRow = i9;
        } else {
            this.includeBotsRow = -1;
        }
        this.includeStartRow = this.rowCount;
        int size = (this.includeExpanded || this.newAlwaysShow.size() < 8) ? this.newAlwaysShow.size() : Math.min(5, this.newAlwaysShow.size());
        int i10 = this.rowCount + size;
        this.rowCount = i10;
        this.includeEndRow = i10;
        if (size != this.newAlwaysShow.size()) {
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.includeShowMoreRow = i11;
        } else {
            this.includeShowMoreRow = -1;
        }
        int i12 = this.rowCount;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.includeSectionRow = i12;
        this.rowCount = i13 + 1;
        this.excludeHeaderRow = i13;
        if (this.newNeverShow.size() < 100) {
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.excludeAddRow = i14;
        } else {
            this.excludeAddRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
            int i15 = this.rowCount;
            this.rowCount = i15 + 1;
            this.excludeMutedRow = i15;
        } else {
            this.excludeMutedRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
            int i16 = this.rowCount;
            this.rowCount = i16 + 1;
            this.excludeReadRow = i16;
        } else {
            this.excludeReadRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
            int i17 = this.rowCount;
            this.rowCount = i17 + 1;
            this.excludeArchivedRow = i17;
        } else {
            this.excludeArchivedRow = -1;
        }
        this.excludeStartRow = this.rowCount;
        int size2 = (this.excludeExpanded || this.newNeverShow.size() < 8) ? this.newNeverShow.size() : Math.min(5, this.newNeverShow.size());
        int i18 = this.rowCount + size2;
        this.rowCount = i18;
        this.excludeEndRow = i18;
        if (size2 != this.newNeverShow.size()) {
            int i19 = this.rowCount;
            this.rowCount = i19 + 1;
            this.excludeShowMoreRow = i19;
        } else {
            this.excludeShowMoreRow = -1;
        }
        int i20 = this.rowCount;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.excludeSectionRow = i20;
        if (!this.creatingNew) {
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.removeRow = i21;
            this.rowCount = i22 + 1;
            this.removeSectionRow = i22;
        } else {
            this.removeRow = -1;
            this.removeSectionRow = -1;
        }
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.creatingNew) {
            this.actionBar.setTitle(LocaleController.getString("FilterNew", NUM));
        } else {
            this.actionBar.setTitle(this.filter.name);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (FilterCreateActivity.this.checkDiscard()) {
                        FilterCreateActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    FilterCreateActivity.this.processDone();
                }
            }
        });
        this.doneItem = createMenu.addItem(1, (CharSequence) LocaleController.getString("Save", NUM).toUpperCase());
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        AnonymousClass2 r2 = new RecyclerListView(this, context) {
            public boolean requestFocus(int i, Rect rect) {
                return false;
            }
        };
        this.listView = r2;
        r2.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                FilterCreateActivity.this.lambda$createView$4$FilterCreateActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return FilterCreateActivity.this.lambda$createView$5$FilterCreateActivity(view, i);
            }
        });
        checkDoneButton(false);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$4$FilterCreateActivity(View view, int i) {
        if (getParentActivity() != null) {
            boolean z = true;
            if (i == this.includeShowMoreRow) {
                this.includeExpanded = true;
                updateRows();
            } else if (i == this.excludeShowMoreRow) {
                this.excludeExpanded = true;
                updateRows();
            } else if (i == this.includeAddRow || i == this.excludeAddRow) {
                ArrayList<Integer> arrayList = i == this.excludeAddRow ? this.newNeverShow : this.newAlwaysShow;
                if (i != this.includeAddRow) {
                    z = false;
                }
                FilterUsersActivity filterUsersActivity = new FilterUsersActivity(z, arrayList, this.newFilterFlags);
                filterUsersActivity.setDelegate(new FilterUsersActivity.FilterUsersActivityDelegate(i) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void didSelectChats(ArrayList arrayList, int i) {
                        FilterCreateActivity.this.lambda$null$0$FilterCreateActivity(this.f$1, arrayList, i);
                    }
                });
                presentFragment(filterUsersActivity);
            } else if (i == this.removeRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("FilterDelete", NUM));
                builder.setMessage(LocaleController.getString("FilterDeleteAlert", NUM));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FilterCreateActivity.this.lambda$null$3$FilterCreateActivity(dialogInterface, i);
                    }
                });
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            } else if (i == this.nameRow) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
                pollEditTextCell.getTextView().requestFocus();
                AndroidUtilities.showKeyboard(pollEditTextCell.getTextView());
            } else if (view instanceof UserCell) {
                CharSequence name = ((UserCell) view).getName();
                if (i >= this.includeSectionRow) {
                    z = false;
                }
                showRemoveAlert(i, name, z);
            }
        }
    }

    public /* synthetic */ void lambda$null$0$FilterCreateActivity(int i, ArrayList arrayList, int i2) {
        this.newFilterFlags = i2;
        if (i == this.excludeAddRow) {
            this.newNeverShow = arrayList;
            for (int i3 = 0; i3 < this.newNeverShow.size(); i3++) {
                Integer num = this.newNeverShow.get(i3);
                this.newAlwaysShow.remove(num);
                this.newPinned.remove((long) num.intValue());
            }
        } else {
            this.newAlwaysShow = arrayList;
            for (int i4 = 0; i4 < this.newAlwaysShow.size(); i4++) {
                this.newNeverShow.remove(this.newAlwaysShow.get(i4));
            }
            ArrayList arrayList2 = new ArrayList();
            int size = this.newPinned.size();
            for (int i5 = 0; i5 < size; i5++) {
                Integer valueOf = Integer.valueOf((int) this.newPinned.keyAt(i5));
                if (valueOf.intValue() != 0 && !this.newAlwaysShow.contains(valueOf)) {
                    arrayList2.add(valueOf);
                }
            }
            int size2 = arrayList2.size();
            for (int i6 = 0; i6 < size2; i6++) {
                this.newPinned.remove((long) ((Integer) arrayList2.get(i6)).intValue());
            }
        }
        checkDoneButton(false);
        updateRows();
    }

    public /* synthetic */ void lambda$null$3$FilterCreateActivity(DialogInterface dialogInterface, int i) {
        AlertDialog alertDialog;
        if (getParentActivity() != null) {
            alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
        } else {
            alertDialog = null;
        }
        TLRPC$TL_messages_updateDialogFilter tLRPC$TL_messages_updateDialogFilter = new TLRPC$TL_messages_updateDialogFilter();
        tLRPC$TL_messages_updateDialogFilter.id = this.filter.id;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_updateDialogFilter, new RequestDelegate(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                FilterCreateActivity.this.lambda$null$2$FilterCreateActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$FilterCreateActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                FilterCreateActivity.this.lambda$null$1$FilterCreateActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$FilterCreateActivity(AlertDialog alertDialog) {
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        getMessagesController().removeFilter(this.filter);
        getMessagesStorage().deleteDialogFilter(this.filter);
        finishFragment();
    }

    public /* synthetic */ boolean lambda$createView$5$FilterCreateActivity(View view, int i) {
        boolean z = false;
        if (!(view instanceof UserCell)) {
            return false;
        }
        CharSequence name = ((UserCell) view).getName();
        if (i < this.includeSectionRow) {
            z = true;
        }
        showRemoveAlert(i, name, z);
        return true;
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (this.doneItem.getAlpha() != 1.0f) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (this.creatingNew) {
            builder.setTitle(LocaleController.getString("FilterDiscardNewTitle", NUM));
            builder.setMessage(LocaleController.getString("FilterDiscardNewAlert", NUM));
            builder.setPositiveButton(LocaleController.getString("FilterDiscardNewSave", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FilterCreateActivity.this.lambda$checkDiscard$6$FilterCreateActivity(dialogInterface, i);
                }
            });
        } else {
            builder.setTitle(LocaleController.getString("FilterDiscardTitle", NUM));
            builder.setMessage(LocaleController.getString("FilterDiscardAlert", NUM));
            builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FilterCreateActivity.this.lambda$checkDiscard$7$FilterCreateActivity(dialogInterface, i);
                }
            });
        }
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                FilterCreateActivity.this.lambda$checkDiscard$8$FilterCreateActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$6$FilterCreateActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$7$FilterCreateActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$8$FilterCreateActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    private void showRemoveAlert(int i, CharSequence charSequence, boolean z) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (z) {
            builder.setTitle(LocaleController.getString("FilterRemoveInclusionTitle", NUM));
            builder.setMessage(LocaleController.formatString("FilterRemoveInclusionText", NUM, charSequence));
        } else {
            builder.setTitle(LocaleController.getString("FilterRemoveExclusionTitle", NUM));
            builder.setMessage(LocaleController.formatString("FilterRemoveExclusionText", NUM, charSequence));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("StickersRemove", NUM), new DialogInterface.OnClickListener(i, z) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                FilterCreateActivity.this.lambda$showRemoveAlert$9$FilterCreateActivity(this.f$1, this.f$2, dialogInterface, i);
            }
        });
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    public /* synthetic */ void lambda$showRemoveAlert$9$FilterCreateActivity(int i, boolean z, DialogInterface dialogInterface, int i2) {
        if (i == this.includeContactsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ -1;
        } else if (i == this.includeNonContactsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ -1;
        } else if (i == this.includeGroupsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ -1;
        } else if (i == this.includeChannelsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ -1;
        } else if (i == this.includeBotsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_BOTS ^ -1;
        } else if (i == this.excludeArchivedRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ -1;
        } else if (i == this.excludeMutedRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ -1;
        } else if (i == this.excludeReadRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ -1;
        } else if (z) {
            this.newAlwaysShow.remove(i - this.includeStartRow);
        } else {
            this.newNeverShow.remove(i - this.excludeStartRow);
        }
        updateRows();
        checkDoneButton(true);
    }

    /* access modifiers changed from: private */
    public void processDone() {
        saveFilterToServer(this.filter, this.newFilterFlags, this.newFilterName, this.newAlwaysShow, this.newNeverShow, this.newPinned, this.creatingNew, false, this.hasUserChanged, true, this, new Runnable() {
            public final void run() {
                FilterCreateActivity.this.lambda$processDone$10$FilterCreateActivity();
            }
        });
    }

    public /* synthetic */ void lambda$processDone$10$FilterCreateActivity() {
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        finishFragment();
    }

    private static void processAddFilter(MessagesController.DialogFilter dialogFilter, int i, String str, ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2, boolean z, boolean z2, boolean z3, BaseFragment baseFragment, Runnable runnable) {
        if (dialogFilter.flags != i || z3) {
            dialogFilter.unreadCount = -1;
            dialogFilter.pendingUnreadCount = -1;
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

    public static void saveFilterToServer(MessagesController.DialogFilter dialogFilter, int i, String str, ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2, LongSparseArray<Integer> longSparseArray, boolean z, boolean z2, boolean z3, boolean z4, BaseFragment baseFragment, Runnable runnable) {
        AlertDialog alertDialog;
        ArrayList<Integer> arrayList3;
        ArrayList<TLRPC$InputPeer> arrayList4;
        int i2;
        MessagesController.DialogFilter dialogFilter2 = dialogFilter;
        LongSparseArray<Integer> longSparseArray2 = longSparseArray;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (z4) {
                alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
                alertDialog.setCanCacnel(false);
                alertDialog.show();
            } else {
                alertDialog = null;
            }
            TLRPC$TL_messages_updateDialogFilter tLRPC$TL_messages_updateDialogFilter = new TLRPC$TL_messages_updateDialogFilter();
            tLRPC$TL_messages_updateDialogFilter.id = dialogFilter2.id;
            tLRPC$TL_messages_updateDialogFilter.flags |= 1;
            TLRPC$TL_dialogFilter tLRPC$TL_dialogFilter = new TLRPC$TL_dialogFilter();
            tLRPC$TL_messages_updateDialogFilter.filter = tLRPC$TL_dialogFilter;
            tLRPC$TL_dialogFilter.contacts = (i & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0;
            tLRPC$TL_messages_updateDialogFilter.filter.non_contacts = (i & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0;
            tLRPC$TL_messages_updateDialogFilter.filter.groups = (i & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0;
            tLRPC$TL_messages_updateDialogFilter.filter.broadcasts = (i & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0;
            tLRPC$TL_messages_updateDialogFilter.filter.bots = (i & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0;
            tLRPC$TL_messages_updateDialogFilter.filter.exclude_muted = (i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0;
            tLRPC$TL_messages_updateDialogFilter.filter.exclude_read = (i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0;
            tLRPC$TL_messages_updateDialogFilter.filter.exclude_archived = (i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0;
            TLRPC$TL_dialogFilter tLRPC$TL_dialogFilter2 = tLRPC$TL_messages_updateDialogFilter.filter;
            tLRPC$TL_dialogFilter2.id = dialogFilter2.id;
            tLRPC$TL_dialogFilter2.title = str;
            MessagesController messagesController = baseFragment.getMessagesController();
            ArrayList<Integer> arrayList5 = new ArrayList<>();
            if (longSparseArray.size() != 0) {
                int size = longSparseArray.size();
                for (int i3 = 0; i3 < size; i3++) {
                    int keyAt = (int) longSparseArray2.keyAt(i3);
                    if (keyAt != 0) {
                        arrayList5.add(Integer.valueOf(keyAt));
                    }
                }
                Collections.sort(arrayList5, new Comparator(longSparseArray2) {
                    private final /* synthetic */ LongSparseArray f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return FilterCreateActivity.lambda$saveFilterToServer$11(this.f$0, (Integer) obj, (Integer) obj2);
                    }
                });
            }
            int i4 = 0;
            for (int i5 = 3; i4 < i5; i5 = 3) {
                if (i4 == 0) {
                    arrayList4 = tLRPC$TL_messages_updateDialogFilter.filter.include_peers;
                    arrayList3 = arrayList;
                } else if (i4 == 1) {
                    arrayList4 = tLRPC$TL_messages_updateDialogFilter.filter.exclude_peers;
                    arrayList3 = arrayList2;
                } else {
                    arrayList4 = tLRPC$TL_messages_updateDialogFilter.filter.pinned_peers;
                    arrayList3 = arrayList5;
                }
                int size2 = arrayList3.size();
                for (int i6 = 0; i6 < size2; i6++) {
                    long intValue = (long) arrayList3.get(i6).intValue();
                    if ((i4 != 0 || longSparseArray2.indexOfKey(intValue) < 0) && (i2 = (int) intValue) != 0) {
                        if (i2 > 0) {
                            TLRPC$User user = messagesController.getUser(Integer.valueOf(i2));
                            if (user != null) {
                                TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                                tLRPC$TL_inputPeerUser.user_id = i2;
                                tLRPC$TL_inputPeerUser.access_hash = user.access_hash;
                                arrayList4.add(tLRPC$TL_inputPeerUser);
                            }
                        } else {
                            int i7 = -i2;
                            TLRPC$Chat chat = messagesController.getChat(Integer.valueOf(i7));
                            if (chat != null) {
                                if (ChatObject.isChannel(chat)) {
                                    TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
                                    tLRPC$TL_inputPeerChannel.channel_id = i7;
                                    tLRPC$TL_inputPeerChannel.access_hash = chat.access_hash;
                                    arrayList4.add(tLRPC$TL_inputPeerChannel);
                                } else {
                                    TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
                                    tLRPC$TL_inputPeerChat.chat_id = i7;
                                    arrayList4.add(tLRPC$TL_inputPeerChat);
                                }
                            }
                        }
                    }
                }
                i4++;
            }
            $$Lambda$FilterCreateActivity$iOWSplbXi4sbEfSQ_9SB8NmcvtI r13 = r0;
            ConnectionsManager connectionsManager = baseFragment.getConnectionsManager();
            $$Lambda$FilterCreateActivity$iOWSplbXi4sbEfSQ_9SB8NmcvtI r0 = new RequestDelegate(z4, alertDialog, dialogFilter, i, str, arrayList, arrayList2, z, z2, z3, baseFragment, runnable) {
                private final /* synthetic */ boolean f$0;
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ BaseFragment f$10;
                private final /* synthetic */ Runnable f$11;
                private final /* synthetic */ MessagesController.DialogFilter f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ String f$4;
                private final /* synthetic */ ArrayList f$5;
                private final /* synthetic */ ArrayList f$6;
                private final /* synthetic */ boolean f$7;
                private final /* synthetic */ boolean f$8;
                private final /* synthetic */ boolean f$9;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                    this.f$9 = r10;
                    this.f$10 = r11;
                    this.f$11 = r12;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11) {
                        private final /* synthetic */ boolean f$0;
                        private final /* synthetic */ AlertDialog f$1;
                        private final /* synthetic */ BaseFragment f$10;
                        private final /* synthetic */ Runnable f$11;
                        private final /* synthetic */ MessagesController.DialogFilter f$2;
                        private final /* synthetic */ int f$3;
                        private final /* synthetic */ String f$4;
                        private final /* synthetic */ ArrayList f$5;
                        private final /* synthetic */ ArrayList f$6;
                        private final /* synthetic */ boolean f$7;
                        private final /* synthetic */ boolean f$8;
                        private final /* synthetic */ boolean f$9;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                            this.f$8 = r9;
                            this.f$9 = r10;
                            this.f$10 = r11;
                            this.f$11 = r12;
                        }

                        public final void run() {
                            FilterCreateActivity.lambda$null$12(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
                        }
                    });
                }
            };
            connectionsManager.sendRequest(tLRPC$TL_messages_updateDialogFilter, r13);
            if (!z4) {
                processAddFilter(dialogFilter, i, str, arrayList, arrayList2, z, z2, z3, baseFragment, runnable);
            }
        }
    }

    static /* synthetic */ int lambda$saveFilterToServer$11(LongSparseArray longSparseArray, Integer num, Integer num2) {
        int intValue = ((Integer) longSparseArray.get((long) num.intValue())).intValue();
        int intValue2 = ((Integer) longSparseArray.get((long) num2.intValue())).intValue();
        if (intValue > intValue2) {
            return 1;
        }
        return intValue < intValue2 ? -1 : 0;
    }

    static /* synthetic */ void lambda$null$12(boolean z, AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter, int i, String str, ArrayList arrayList, ArrayList arrayList2, boolean z2, boolean z3, boolean z4, BaseFragment baseFragment, Runnable runnable) {
        if (z) {
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            processAddFilter(dialogFilter, i, str, arrayList, arrayList2, z2, z3, z4, baseFragment, runnable);
        }
    }

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

    /* access modifiers changed from: private */
    public void checkDoneButton(boolean z) {
        boolean z2 = true;
        boolean z3 = !TextUtils.isEmpty(this.newFilterName) && this.newFilterName.length() <= 12;
        if (z3) {
            if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == 0 && this.newAlwaysShow.isEmpty()) {
                z2 = false;
            }
            z3 = (!z2 || this.creatingNew) ? z2 : hasChanges();
        }
        if (this.doneItem.isEnabled() != z3) {
            this.doneItem.setEnabled(z3);
            float f = 1.0f;
            if (z) {
                ViewPropertyAnimator scaleX = this.doneItem.animate().alpha(z3 ? 1.0f : 0.0f).scaleX(z3 ? 1.0f : 0.0f);
                if (!z3) {
                    f = 0.0f;
                }
                scaleX.scaleY(f).setDuration(180).start();
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
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View view) {
        if (view instanceof PollEditTextCell) {
            PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
            String str = this.newFilterName;
            int length = 12 - (str != null ? str.length() : 0);
            if (((float) length) <= 3.6000004f) {
                pollEditTextCell.setText2(String.format("%d", new Object[]{Integer.valueOf(length)}));
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

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 3 || itemViewType == 0 || itemViewType == 2 || itemViewType == 5) ? false : true;
        }

        public int getItemCount() {
            return FilterCreateActivity.this.rowCount;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$FilterCreateActivity$ListAdapter(PollEditTextCell pollEditTextCell, View view, boolean z) {
            pollEditTextCell.getTextView2().setAlpha((z || FilterCreateActivity.this.newFilterName.length() > 12) ? 1.0f : 0.0f);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: org.telegram.ui.Cells.TextCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                java.lang.String r5 = "windowBackgroundWhite"
                if (r6 == 0) goto L_0x0074
                r0 = 1
                if (r6 == r0) goto L_0x0060
                r1 = 2
                if (r6 == r1) goto L_0x0030
                r0 = 3
                if (r6 == r0) goto L_0x0028
                r0 = 4
                if (r6 == r0) goto L_0x0019
                org.telegram.ui.FilterCreateActivity$HintInnerCell r5 = new org.telegram.ui.FilterCreateActivity$HintInnerCell
                android.content.Context r6 = r4.mContext
                r5.<init>(r6)
                goto L_0x0083
            L_0x0019:
                org.telegram.ui.Cells.TextCell r6 = new org.telegram.ui.Cells.TextCell
                android.content.Context r0 = r4.mContext
                r6.<init>(r0)
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r6.setBackgroundColor(r5)
                goto L_0x0082
            L_0x0028:
                org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r6 = r4.mContext
                r5.<init>(r6)
                goto L_0x0083
            L_0x0030:
                org.telegram.ui.Cells.PollEditTextCell r6 = new org.telegram.ui.Cells.PollEditTextCell
                android.content.Context r1 = r4.mContext
                r2 = 0
                r6.<init>(r1, r2)
                r6.createErrorTextView()
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r6.setBackgroundColor(r5)
                org.telegram.ui.FilterCreateActivity$ListAdapter$1 r5 = new org.telegram.ui.FilterCreateActivity$ListAdapter$1
                r5.<init>(r6)
                r6.addTextWatcher(r5)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r6.getTextView()
                r6.setShowNextButton(r0)
                org.telegram.ui.-$$Lambda$FilterCreateActivity$ListAdapter$tKk_I0DToUum9Cy_Qlofvvar_k r0 = new org.telegram.ui.-$$Lambda$FilterCreateActivity$ListAdapter$tKk_I0DToUum9Cy_Qlofvvar_k
                r0.<init>(r6)
                r5.setOnFocusChangeListener(r0)
                r0 = 268435462(0x10000006, float:2.5243567E-29)
                r5.setImeOptions(r0)
                goto L_0x0082
            L_0x0060:
                org.telegram.ui.Cells.UserCell r6 = new org.telegram.ui.Cells.UserCell
                android.content.Context r1 = r4.mContext
                r2 = 6
                r3 = 0
                r6.<init>(r1, r2, r3, r3)
                r6.setSelfAsSavedMessages(r0)
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r6.setBackgroundColor(r5)
                goto L_0x0082
            L_0x0074:
                org.telegram.ui.Cells.HeaderCell r6 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r0 = r4.mContext
                r6.<init>(r0)
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r6.setBackgroundColor(r5)
            L_0x0082:
                r5 = r6
            L_0x0083:
                org.telegram.ui.Components.RecyclerListView$Holder r6 = new org.telegram.ui.Components.RecyclerListView$Holder
                r6.<init>(r5)
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilterCreateActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 2) {
                FilterCreateActivity.this.setTextLeft(viewHolder.itemView);
                PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell.setTag(1);
                pollEditTextCell.setTextAndHint(FilterCreateActivity.this.newFilterName != null ? FilterCreateActivity.this.newFilterName : "", LocaleController.getString("FilterNameHint", NUM), false);
                pollEditTextCell.setTag((Object) null);
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 2) {
                EditTextBoldCursor textView = ((PollEditTextCell) viewHolder.itemView).getTextView();
                if (textView.isFocused()) {
                    textView.clearFocus();
                    AndroidUtilities.hideKeyboard(textView);
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:44:0x0139, code lost:
            if (r12 == (org.telegram.ui.FilterCreateActivity.access$1100(r10.this$0) - 1)) goto L_0x013c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x0171, code lost:
            if (r12 == (org.telegram.ui.FilterCreateActivity.access$1500(r10.this$0) - 1)) goto L_0x013c;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
                r10 = this;
                int r0 = r11.getItemViewType()
                if (r0 == 0) goto L_0x02c3
                r1 = -1
                r2 = 0
                r3 = 1
                if (r0 == r3) goto L_0x0101
                r4 = 3
                if (r0 == r4) goto L_0x00c5
                r4 = 4
                if (r0 == r4) goto L_0x0013
                goto L_0x02f0
            L_0x0013:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextCell r11 = (org.telegram.ui.Cells.TextCell) r11
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.removeRow
                if (r12 != r0) goto L_0x0034
                r12 = 0
                java.lang.String r0 = "windowBackgroundWhiteRedText5"
                r11.setColors(r12, r0)
                r12 = 2131625203(0x7f0e04f3, float:1.8877607E38)
                java.lang.String r0 = "FilterDelete"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12, r2)
                goto L_0x02f0
            L_0x0034:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeShowMoreRow
                r4 = 2131165265(0x7var_, float:1.7944742E38)
                java.lang.String r5 = "FilterShowMoreChats"
                java.lang.String r6 = "windowBackgroundWhiteBlueText4"
                java.lang.String r7 = "switchTrackChecked"
                if (r12 != r0) goto L_0x005e
                r11.setColors(r7, r6)
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                java.util.ArrayList r12 = r12.newAlwaysShow
                int r12 = r12.size()
                int r12 = r12 + -5
                java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12)
                r11.setTextAndIcon((java.lang.String) r12, (int) r4, (boolean) r2)
                goto L_0x02f0
            L_0x005e:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeShowMoreRow
                if (r12 != r0) goto L_0x007e
                r11.setColors(r7, r6)
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                java.util.ArrayList r12 = r12.newNeverShow
                int r12 = r12.size()
                int r12 = r12 + -5
                java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12)
                r11.setTextAndIcon((java.lang.String) r12, (int) r4, (boolean) r2)
                goto L_0x02f0
            L_0x007e:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeAddRow
                r4 = 2131165248(0x7var_, float:1.7944708E38)
                if (r12 != r0) goto L_0x00a3
                r11.setColors(r7, r6)
                r12 = 2131625187(0x7f0e04e3, float:1.8877575E38)
                java.lang.String r0 = "FilterAddChats"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeStartRow
                if (r0 == r1) goto L_0x009e
                r2 = 1
            L_0x009e:
                r11.setTextAndIcon((java.lang.String) r12, (int) r4, (boolean) r2)
                goto L_0x02f0
            L_0x00a3:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeAddRow
                if (r12 != r0) goto L_0x02f0
                r11.setColors(r7, r6)
                r12 = 2131625231(0x7f0e050f, float:1.8877664E38)
                java.lang.String r0 = "FilterRemoveChats"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeStartRow
                if (r0 == r1) goto L_0x00c0
                r2 = 1
            L_0x00c0:
                r11.setTextAndIcon((java.lang.String) r12, (int) r4, (boolean) r2)
                goto L_0x02f0
            L_0x00c5:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.removeSectionRow
                java.lang.String r2 = "windowBackgroundGrayShadow"
                if (r12 == r0) goto L_0x00f1
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeSectionRow
                if (r12 != r0) goto L_0x00e1
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                int r12 = r12.removeSectionRow
                if (r12 != r1) goto L_0x00e1
                goto L_0x00f1
            L_0x00e1:
                android.view.View r11 = r11.itemView
                android.content.Context r12 = r10.mContext
                r0 = 2131165417(0x7var_e9, float:1.794505E38)
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r0, (java.lang.String) r2)
                r11.setBackgroundDrawable(r12)
                goto L_0x02f0
            L_0x00f1:
                android.view.View r11 = r11.itemView
                android.content.Context r12 = r10.mContext
                r0 = 2131165418(0x7var_ea, float:1.7945053E38)
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r0, (java.lang.String) r2)
                r11.setBackgroundDrawable(r12)
                goto L_0x02f0
            L_0x0101:
                android.view.View r11 = r11.itemView
                r4 = r11
                org.telegram.ui.Cells.UserCell r4 = (org.telegram.ui.Cells.UserCell) r4
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeStartRow
                if (r12 < r11) goto L_0x013e
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeEndRow
                if (r12 >= r11) goto L_0x013e
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                java.util.ArrayList r11 = r11.newAlwaysShow
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeStartRow
                int r0 = r12 - r0
                java.lang.Object r11 = r11.get(r0)
                java.lang.Integer r11 = (java.lang.Integer) r11
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeShowMoreRow
                if (r0 != r1) goto L_0x013b
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeEndRow
                int r0 = r0 - r3
                if (r12 == r0) goto L_0x013c
            L_0x013b:
                r2 = 1
            L_0x013c:
                r9 = r2
                goto L_0x0174
            L_0x013e:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.excludeStartRow
                if (r12 < r11) goto L_0x0220
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.excludeEndRow
                if (r12 >= r11) goto L_0x0220
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                java.util.ArrayList r11 = r11.newNeverShow
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeStartRow
                int r0 = r12 - r0
                java.lang.Object r11 = r11.get(r0)
                java.lang.Integer r11 = (java.lang.Integer) r11
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeShowMoreRow
                if (r0 != r1) goto L_0x013b
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeEndRow
                int r0 = r0 - r3
                if (r12 == r0) goto L_0x013c
                goto L_0x013b
            L_0x0174:
                int r12 = r11.intValue()
                if (r12 <= 0) goto L_0x01b4
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
                org.telegram.tgnet.TLRPC$User r5 = r12.getUser(r11)
                if (r5 == 0) goto L_0x02f0
                boolean r11 = r5.bot
                if (r11 == 0) goto L_0x0195
                r11 = 2131624438(0x7f0e01f6, float:1.8876056E38)
                java.lang.String r12 = "Bot"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            L_0x0193:
                r7 = r11
                goto L_0x01ad
            L_0x0195:
                boolean r11 = r5.contact
                if (r11 == 0) goto L_0x01a3
                r11 = 2131625201(0x7f0e04f1, float:1.8877603E38)
                java.lang.String r12 = "FilterContact"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x0193
            L_0x01a3:
                r11 = 2131625227(0x7f0e050b, float:1.8877656E38)
                java.lang.String r12 = "FilterNonContact"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x0193
            L_0x01ad:
                r6 = 0
                r8 = 0
                r4.setData(r5, r6, r7, r8, r9)
                goto L_0x02f0
            L_0x01b4:
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
                int r11 = r11.intValue()
                int r11 = -r11
                java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
                org.telegram.tgnet.TLRPC$Chat r5 = r12.getChat(r11)
                if (r5 == 0) goto L_0x02f0
                int r11 = r5.participants_count
                if (r11 == 0) goto L_0x01d5
                java.lang.String r12 = "Members"
                java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r12, r11)
            L_0x01d3:
                r7 = r11
                goto L_0x0219
            L_0x01d5:
                java.lang.String r11 = r5.username
                boolean r11 = android.text.TextUtils.isEmpty(r11)
                if (r11 == 0) goto L_0x01fb
                boolean r11 = org.telegram.messenger.ChatObject.isChannel(r5)
                if (r11 == 0) goto L_0x01f1
                boolean r11 = r5.megagroup
                if (r11 != 0) goto L_0x01f1
                r11 = 2131624586(0x7f0e028a, float:1.8876356E38)
                java.lang.String r12 = "ChannelPrivate"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x01d3
            L_0x01f1:
                r11 = 2131625647(0x7f0e06af, float:1.8878508E38)
                java.lang.String r12 = "MegaPrivate"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x01d3
            L_0x01fb:
                boolean r11 = org.telegram.messenger.ChatObject.isChannel(r5)
                if (r11 == 0) goto L_0x020f
                boolean r11 = r5.megagroup
                if (r11 != 0) goto L_0x020f
                r11 = 2131624589(0x7f0e028d, float:1.8876362E38)
                java.lang.String r12 = "ChannelPublic"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x01d3
            L_0x020f:
                r11 = 2131625650(0x7f0e06b2, float:1.8878514E38)
                java.lang.String r12 = "MegaPublic"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x01d3
            L_0x0219:
                r6 = 0
                r8 = 0
                r4.setData(r5, r6, r7, r8, r9)
                goto L_0x02f0
            L_0x0220:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeContactsRow
                if (r12 != r11) goto L_0x0237
                r11 = 2131625202(0x7f0e04f2, float:1.8877605E38)
                java.lang.String r12 = "FilterContacts"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                java.lang.String r12 = "contacts"
            L_0x0233:
                r6 = r11
                r5 = r12
                goto L_0x02bc
            L_0x0237:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeNonContactsRow
                if (r12 != r11) goto L_0x024b
                r11 = 2131625228(0x7f0e050c, float:1.8877658E38)
                java.lang.String r12 = "FilterNonContacts"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                java.lang.String r12 = "non_contacts"
                goto L_0x0233
            L_0x024b:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeGroupsRow
                if (r12 != r11) goto L_0x025f
                r11 = 2131625218(0x7f0e0502, float:1.8877638E38)
                java.lang.String r12 = "FilterGroups"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                java.lang.String r12 = "groups"
                goto L_0x0233
            L_0x025f:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeChannelsRow
                if (r12 != r11) goto L_0x0273
                r11 = 2131625198(0x7f0e04ee, float:1.8877597E38)
                java.lang.String r12 = "FilterChannels"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                java.lang.String r12 = "channels"
                goto L_0x0233
            L_0x0273:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeBotsRow
                if (r12 != r11) goto L_0x0287
                r11 = 2131625197(0x7f0e04ed, float:1.8877595E38)
                java.lang.String r12 = "FilterBots"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                java.lang.String r12 = "bots"
                goto L_0x0233
            L_0x0287:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.excludeMutedRow
                if (r12 != r11) goto L_0x029b
                r11 = 2131625220(0x7f0e0504, float:1.8877642E38)
                java.lang.String r12 = "FilterMuted"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                java.lang.String r12 = "muted"
                goto L_0x0233
            L_0x029b:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.excludeReadRow
                if (r12 != r11) goto L_0x02af
                r11 = 2131625229(0x7f0e050d, float:1.887766E38)
                java.lang.String r12 = "FilterRead"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                java.lang.String r12 = "read"
                goto L_0x0233
            L_0x02af:
                r11 = 2131625194(0x7f0e04ea, float:1.887759E38)
                java.lang.String r12 = "FilterArchived"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                java.lang.String r12 = "archived"
                goto L_0x0233
            L_0x02bc:
                r7 = 0
                r8 = 0
                r9 = 1
                r4.setData(r5, r6, r7, r8, r9)
                return
            L_0x02c3:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.HeaderCell r11 = (org.telegram.ui.Cells.HeaderCell) r11
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeHeaderRow
                if (r12 != r0) goto L_0x02dc
                r12 = 2131625219(0x7f0e0503, float:1.887764E38)
                java.lang.String r0 = "FilterInclude"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x02f0
            L_0x02dc:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeHeaderRow
                if (r12 != r0) goto L_0x02f0
                r12 = 2131625214(0x7f0e04fe, float:1.887763E38)
                java.lang.String r0 = "FilterExclude"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
            L_0x02f0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilterCreateActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

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
            if (i == FilterCreateActivity.this.nameSectionRow || i == FilterCreateActivity.this.includeSectionRow || i == FilterCreateActivity.this.excludeSectionRow || i == FilterCreateActivity.this.namePreSectionRow || i == FilterCreateActivity.this.removeSectionRow) {
                return 3;
            }
            return i == FilterCreateActivity.this.imageRow ? 5 : 4;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$FilterCreateActivity$9HYG1Sbc9K2QtwnShcFhVFgroIk r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                FilterCreateActivity.this.lambda$getThemeDescriptions$14$FilterCreateActivity();
            }
        };
        $$Lambda$FilterCreateActivity$9HYG1Sbc9K2QtwnShcFhVFgroIk r8 = r10;
        $$Lambda$FilterCreateActivity$9HYG1Sbc9K2QtwnShcFhVFgroIk r7 = r10;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, PollEditTextCell.class, UserCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"ImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText"), new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink")};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$14$FilterCreateActivity() {
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

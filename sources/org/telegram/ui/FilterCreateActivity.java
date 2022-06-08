package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
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
    public boolean nameChangedManually;
    /* access modifiers changed from: private */
    public int namePreSectionRow;
    /* access modifiers changed from: private */
    public int nameRow;
    /* access modifiers changed from: private */
    public int nameSectionRow;
    /* access modifiers changed from: private */
    public ArrayList<Long> newAlwaysShow;
    private int newFilterFlags;
    /* access modifiers changed from: private */
    public String newFilterName;
    /* access modifiers changed from: private */
    public ArrayList<Long> newNeverShow;
    private LongSparseIntArray newPinned;
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
            this.imageView.setOnClickListener(new FilterCreateActivity$HintInnerCell$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
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
        this((MessagesController.DialogFilter) null, (ArrayList<Long>) null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter) {
        this(dialogFilter, (ArrayList<Long>) null);
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

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.creatingNew) {
            this.actionBar.setTitle(LocaleController.getString("FilterNew", NUM));
        } else {
            TextPaint textPaint = new TextPaint(1);
            textPaint.setTextSize((float) AndroidUtilities.dp(20.0f));
            this.actionBar.setTitle(Emoji.replaceEmoji(this.filter.name, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new FilterCreateActivity$$ExternalSyntheticLambda12(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new FilterCreateActivity$$ExternalSyntheticLambda13(this));
        checkDoneButton(false);
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view, int i) {
        if (getParentActivity() != null) {
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
                    filterUsersActivity.setDelegate(new FilterCreateActivity$$ExternalSyntheticLambda14(this, i));
                    presentFragment(filterUsersActivity);
                } else if (i == this.removeRow) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("FilterDelete", NUM));
                    builder.setMessage(LocaleController.getString("FilterDeleteAlert", NUM));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new FilterCreateActivity$$ExternalSyntheticLambda2(this));
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
    }

    /* access modifiers changed from: private */
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(DialogInterface dialogInterface, int i) {
        AlertDialog alertDialog;
        if (getParentActivity() != null) {
            alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.show();
        } else {
            alertDialog = null;
        }
        TLRPC$TL_messages_updateDialogFilter tLRPC$TL_messages_updateDialogFilter = new TLRPC$TL_messages_updateDialogFilter();
        tLRPC$TL_messages_updateDialogFilter.id = this.filter.id;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_updateDialogFilter, new FilterCreateActivity$$ExternalSyntheticLambda9(this, alertDialog));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new FilterCreateActivity$$ExternalSyntheticLambda6(this, alertDialog));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(AlertDialog alertDialog) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$5(View view, int i) {
        boolean z = false;
        if (!(view instanceof UserCell)) {
            return false;
        }
        UserCell userCell = (UserCell) view;
        CharSequence name = userCell.getName();
        Object currentObject = userCell.getCurrentObject();
        if (i < this.includeSectionRow) {
            z = true;
        }
        showRemoveAlert(i, name, currentObject, z);
        return true;
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:49:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void fillFilterName() {
        /*
            r5 = this;
            boolean r0 = r5.creatingNew
            if (r0 == 0) goto L_0x00c5
            java.lang.String r0 = r5.newFilterName
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0012
            boolean r0 = r5.nameChangedManually
            if (r0 == 0) goto L_0x0012
            goto L_0x00c5
        L_0x0012:
            int r0 = r5.newFilterFlags
            int r1 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS
            r2 = r0 & r1
            r3 = r2 & r1
            java.lang.String r4 = ""
            if (r3 != r1) goto L_0x003e
            int r1 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ
            r1 = r1 & r0
            if (r1 == 0) goto L_0x002e
            r0 = 2131625860(0x7f0e0784, float:1.887894E38)
            java.lang.String r1 = "FilterNameUnread"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x00a8
        L_0x002e:
            int r1 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED
            r0 = r0 & r1
            if (r0 == 0) goto L_0x00a7
            r0 = 2131625859(0x7f0e0783, float:1.8878938E38)
            java.lang.String r1 = "FilterNameNonMuted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x00a8
        L_0x003e:
            int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CONTACTS
            r1 = r2 & r0
            if (r1 == 0) goto L_0x0053
            r0 = r0 ^ -1
            r0 = r0 & r2
            if (r0 != 0) goto L_0x00a7
            r0 = 2131625837(0x7f0e076d, float:1.8878893E38)
            java.lang.String r1 = "FilterContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x00a8
        L_0x0053:
            int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS
            r1 = r2 & r0
            if (r1 == 0) goto L_0x0068
            r0 = r0 ^ -1
            r0 = r0 & r2
            if (r0 != 0) goto L_0x00a7
            r0 = 2131625867(0x7f0e078b, float:1.8878954E38)
            java.lang.String r1 = "FilterNonContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x00a8
        L_0x0068:
            int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_GROUPS
            r1 = r2 & r0
            if (r1 == 0) goto L_0x007d
            r0 = r0 ^ -1
            r0 = r0 & r2
            if (r0 != 0) goto L_0x00a7
            r0 = 2131625854(0x7f0e077e, float:1.8878928E38)
            java.lang.String r1 = "FilterGroups"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x00a8
        L_0x007d:
            int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_BOTS
            r1 = r2 & r0
            if (r1 == 0) goto L_0x0092
            r0 = r0 ^ -1
            r0 = r0 & r2
            if (r0 != 0) goto L_0x00a7
            r0 = 2131625827(0x7f0e0763, float:1.8878873E38)
            java.lang.String r1 = "FilterBots"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x00a8
        L_0x0092:
            int r0 = org.telegram.messenger.MessagesController.DIALOG_FILTER_FLAG_CHANNELS
            r1 = r2 & r0
            if (r1 == 0) goto L_0x00a7
            r0 = r0 ^ -1
            r0 = r0 & r2
            if (r0 != 0) goto L_0x00a7
            r0 = 2131625828(0x7f0e0764, float:1.8878875E38)
            java.lang.String r1 = "FilterChannels"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x00a8
        L_0x00a7:
            r0 = r4
        L_0x00a8:
            if (r0 == 0) goto L_0x00b3
            int r1 = r0.length()
            r2 = 12
            if (r1 <= r2) goto L_0x00b3
            goto L_0x00b4
        L_0x00b3:
            r4 = r0
        L_0x00b4:
            r5.newFilterName = r4
            org.telegram.ui.Components.RecyclerListView r0 = r5.listView
            int r1 = r5.nameRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findViewHolderForAdapterPosition(r1)
            if (r0 == 0) goto L_0x00c5
            org.telegram.ui.FilterCreateActivity$ListAdapter r1 = r5.adapter
            r1.onViewAttachedToWindow(r0)
        L_0x00c5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilterCreateActivity.fillFilterName():void");
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
            builder.setPositiveButton(LocaleController.getString("FilterDiscardNewSave", NUM), new FilterCreateActivity$$ExternalSyntheticLambda3(this));
        } else {
            builder.setTitle(LocaleController.getString("FilterDiscardTitle", NUM));
            builder.setMessage(LocaleController.getString("FilterDiscardAlert", NUM));
            builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new FilterCreateActivity$$ExternalSyntheticLambda0(this));
        }
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new FilterCreateActivity$$ExternalSyntheticLambda1(this));
        showDialog(builder.create());
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$6(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$7(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$8(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    private void showRemoveAlert(int i, CharSequence charSequence, Object obj, boolean z) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (z) {
            builder.setTitle(LocaleController.getString("FilterRemoveInclusionTitle", NUM));
            if (obj instanceof String) {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionText", NUM, charSequence));
            } else if (obj instanceof TLRPC$User) {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionUserText", NUM, charSequence));
            } else {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionChatText", NUM, charSequence));
            }
        } else {
            builder.setTitle(LocaleController.getString("FilterRemoveExclusionTitle", NUM));
            if (obj instanceof String) {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionText", NUM, charSequence));
            } else if (obj instanceof TLRPC$User) {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionUserText", NUM, charSequence));
            } else {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionChatText", NUM, charSequence));
            }
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("StickersRemove", NUM), new FilterCreateActivity$$ExternalSyntheticLambda4(this, i, z));
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showRemoveAlert$9(int i, boolean z, DialogInterface dialogInterface, int i2) {
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
        fillFilterName();
        updateRows();
        checkDoneButton(true);
    }

    /* access modifiers changed from: private */
    public void processDone() {
        saveFilterToServer(this.filter, this.newFilterFlags, this.newFilterName, this.newAlwaysShow, this.newNeverShow, this.newPinned, this.creatingNew, false, this.hasUserChanged, true, true, this, new FilterCreateActivity$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: private */
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

    public static void saveFilterToServer(MessagesController.DialogFilter dialogFilter, int i, String str, ArrayList<Long> arrayList, ArrayList<Long> arrayList2, LongSparseIntArray longSparseIntArray, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, BaseFragment baseFragment, Runnable runnable) {
        AlertDialog alertDialog;
        ArrayList<Long> arrayList3;
        ArrayList<TLRPC$InputPeer> arrayList4;
        MessagesController.DialogFilter dialogFilter2 = dialogFilter;
        LongSparseIntArray longSparseIntArray2 = longSparseIntArray;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            int i2 = 3;
            if (z5) {
                alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
                alertDialog.setCanCancel(false);
                alertDialog.show();
            } else {
                alertDialog = null;
            }
            TLRPC$TL_messages_updateDialogFilter tLRPC$TL_messages_updateDialogFilter = new TLRPC$TL_messages_updateDialogFilter();
            tLRPC$TL_messages_updateDialogFilter.id = dialogFilter2.id;
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
            tLRPC$TL_dialogFilter.id = dialogFilter2.id;
            tLRPC$TL_dialogFilter.title = str;
            MessagesController messagesController = baseFragment.getMessagesController();
            ArrayList<Long> arrayList5 = new ArrayList<>();
            if (longSparseIntArray.size() != 0) {
                int size = longSparseIntArray.size();
                for (int i4 = 0; i4 < size; i4++) {
                    long keyAt = longSparseIntArray2.keyAt(i4);
                    if (!DialogObject.isEncryptedDialog(keyAt)) {
                        arrayList5.add(Long.valueOf(keyAt));
                    }
                }
                Collections.sort(arrayList5, new FilterCreateActivity$$ExternalSyntheticLambda8(longSparseIntArray2));
            }
            int i5 = 0;
            while (i5 < i2) {
                if (i5 == 0) {
                    arrayList4 = tLRPC$TL_messages_updateDialogFilter.filter.include_peers;
                    arrayList3 = arrayList;
                } else if (i5 == i3) {
                    arrayList4 = tLRPC$TL_messages_updateDialogFilter.filter.exclude_peers;
                    arrayList3 = arrayList2;
                } else {
                    arrayList4 = tLRPC$TL_messages_updateDialogFilter.filter.pinned_peers;
                    arrayList3 = arrayList5;
                }
                int size2 = arrayList3.size();
                for (int i6 = 0; i6 < size2; i6++) {
                    long longValue = arrayList3.get(i6).longValue();
                    if ((i5 != 0 || longSparseIntArray2.indexOfKey(longValue) < 0) && !DialogObject.isEncryptedDialog(longValue)) {
                        if (longValue > 0) {
                            TLRPC$User user = messagesController.getUser(Long.valueOf(longValue));
                            if (user != null) {
                                TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
                                tLRPC$TL_inputPeerUser.user_id = longValue;
                                tLRPC$TL_inputPeerUser.access_hash = user.access_hash;
                                arrayList4.add(tLRPC$TL_inputPeerUser);
                            }
                        } else {
                            long j = -longValue;
                            TLRPC$Chat chat = messagesController.getChat(Long.valueOf(j));
                            if (chat != null) {
                                if (ChatObject.isChannel(chat)) {
                                    TLRPC$TL_inputPeerChannel tLRPC$TL_inputPeerChannel = new TLRPC$TL_inputPeerChannel();
                                    tLRPC$TL_inputPeerChannel.channel_id = j;
                                    tLRPC$TL_inputPeerChannel.access_hash = chat.access_hash;
                                    arrayList4.add(tLRPC$TL_inputPeerChannel);
                                } else {
                                    TLRPC$TL_inputPeerChat tLRPC$TL_inputPeerChat = new TLRPC$TL_inputPeerChat();
                                    tLRPC$TL_inputPeerChat.chat_id = j;
                                    arrayList4.add(tLRPC$TL_inputPeerChat);
                                }
                            }
                        }
                    }
                }
                i5++;
                i2 = 3;
                i3 = 1;
            }
            FilterCreateActivity$$ExternalSyntheticLambda10 filterCreateActivity$$ExternalSyntheticLambda10 = r0;
            ConnectionsManager connectionsManager = baseFragment.getConnectionsManager();
            FilterCreateActivity$$ExternalSyntheticLambda10 filterCreateActivity$$ExternalSyntheticLambda102 = new FilterCreateActivity$$ExternalSyntheticLambda10(z5, alertDialog, dialogFilter, i, str, arrayList, arrayList2, z, z2, z3, z4, baseFragment, runnable);
            connectionsManager.sendRequest(tLRPC$TL_messages_updateDialogFilter, filterCreateActivity$$ExternalSyntheticLambda10);
            if (!z5) {
                processAddFilter(dialogFilter, i, str, arrayList, arrayList2, z, z2, z3, z4, baseFragment, runnable);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$saveFilterToServer$11(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveFilterToServer$12(boolean z, AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter, int i, String str, ArrayList arrayList, ArrayList arrayList2, boolean z2, boolean z3, boolean z4, boolean z5, BaseFragment baseFragment, Runnable runnable) {
        if (z) {
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            processAddFilter(dialogFilter, i, str, arrayList, arrayList2, z2, z3, z4, z5, baseFragment, runnable);
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(PollEditTextCell pollEditTextCell, View view, boolean z) {
            pollEditTextCell.getTextView2().setAlpha((z || FilterCreateActivity.this.newFilterName.length() > 12) ? 1.0f : 0.0f);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v13, resolved type: org.telegram.ui.Cells.TextCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                java.lang.String r5 = "windowBackgroundWhite"
                if (r6 == 0) goto L_0x007f
                r0 = 1
                if (r6 == r0) goto L_0x006b
                r1 = 2
                if (r6 == r1) goto L_0x003b
                r0 = 3
                if (r6 == r0) goto L_0x0033
                r0 = 4
                if (r6 == r0) goto L_0x0024
                r5 = 5
                if (r6 == r5) goto L_0x001c
                org.telegram.ui.Cells.TextInfoPrivacyCell r5 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r6 = r4.mContext
                r5.<init>(r6)
                goto L_0x008e
            L_0x001c:
                org.telegram.ui.FilterCreateActivity$HintInnerCell r5 = new org.telegram.ui.FilterCreateActivity$HintInnerCell
                android.content.Context r6 = r4.mContext
                r5.<init>(r6)
                goto L_0x008e
            L_0x0024:
                org.telegram.ui.Cells.TextCell r6 = new org.telegram.ui.Cells.TextCell
                android.content.Context r0 = r4.mContext
                r6.<init>(r0)
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r6.setBackgroundColor(r5)
                goto L_0x008d
            L_0x0033:
                org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r6 = r4.mContext
                r5.<init>(r6)
                goto L_0x008e
            L_0x003b:
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
                org.telegram.ui.FilterCreateActivity$ListAdapter$$ExternalSyntheticLambda0 r0 = new org.telegram.ui.FilterCreateActivity$ListAdapter$$ExternalSyntheticLambda0
                r0.<init>(r4, r6)
                r5.setOnFocusChangeListener(r0)
                r0 = 268435462(0x10000006, float:2.5243567E-29)
                r5.setImeOptions(r0)
                goto L_0x008d
            L_0x006b:
                org.telegram.ui.Cells.UserCell r6 = new org.telegram.ui.Cells.UserCell
                android.content.Context r1 = r4.mContext
                r2 = 6
                r3 = 0
                r6.<init>(r1, r2, r3, r3)
                r6.setSelfAsSavedMessages(r0)
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r6.setBackgroundColor(r5)
                goto L_0x008d
            L_0x007f:
                org.telegram.ui.Cells.HeaderCell r6 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r0 = r4.mContext
                r6.<init>(r0)
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r6.setBackgroundColor(r5)
            L_0x008d:
                r5 = r6
            L_0x008e:
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

        /* JADX WARNING: Code restructure failed: missing block: B:54:0x0185, code lost:
            if (r12 == (org.telegram.ui.FilterCreateActivity.access$1200(r10.this$0) - 1)) goto L_0x0188;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x01be, code lost:
            if (r12 == (org.telegram.ui.FilterCreateActivity.access$1600(r10.this$0) - 1)) goto L_0x0188;
         */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x01cb  */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x0205  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
                r10 = this;
                int r0 = r11.getItemViewType()
                if (r0 == 0) goto L_0x0368
                r1 = -1
                r2 = 0
                r3 = 1
                if (r0 == r3) goto L_0x014d
                r4 = 3
                r5 = 2131165436(0x7var_fc, float:1.794509E38)
                r6 = 2131165435(0x7var_fb, float:1.7945087E38)
                java.lang.String r7 = "windowBackgroundGrayShadow"
                if (r0 == r4) goto L_0x012b
                r4 = 4
                if (r0 == r4) goto L_0x0075
                r2 = 6
                if (r0 == r2) goto L_0x001e
                goto L_0x0395
            L_0x001e:
                android.view.View r0 = r11.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r0
                org.telegram.ui.FilterCreateActivity r2 = org.telegram.ui.FilterCreateActivity.this
                int r2 = r2.includeSectionRow
                if (r12 != r2) goto L_0x0037
                r2 = 2131625856(0x7f0e0780, float:1.8878932E38)
                java.lang.String r3 = "FilterIncludeInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x004b
            L_0x0037:
                org.telegram.ui.FilterCreateActivity r2 = org.telegram.ui.FilterCreateActivity.this
                int r2 = r2.excludeSectionRow
                if (r12 != r2) goto L_0x004b
                r2 = 2131625851(0x7f0e077b, float:1.8878922E38)
                java.lang.String r3 = "FilterExcludeInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
            L_0x004b:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeSectionRow
                if (r12 != r0) goto L_0x0068
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                int r12 = r12.removeSectionRow
                if (r12 != r1) goto L_0x0068
                android.view.View r11 = r11.itemView
                android.content.Context r12 = r10.mContext
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r5, (java.lang.String) r7)
                r11.setBackgroundDrawable(r12)
                goto L_0x0395
            L_0x0068:
                android.view.View r11 = r11.itemView
                android.content.Context r12 = r10.mContext
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r6, (java.lang.String) r7)
                r11.setBackgroundDrawable(r12)
                goto L_0x0395
            L_0x0075:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextCell r11 = (org.telegram.ui.Cells.TextCell) r11
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.removeRow
                if (r12 != r0) goto L_0x0095
                r12 = 0
                java.lang.String r0 = "windowBackgroundWhiteRedText5"
                r11.setColors(r12, r0)
                r12 = 2131625838(0x7f0e076e, float:1.8878895E38)
                java.lang.String r0 = "FilterDelete"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12, r2)
                goto L_0x0395
            L_0x0095:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeShowMoreRow
                r1 = 2131165259(0x7var_b, float:1.794473E38)
                java.lang.String r4 = "FilterShowMoreChats"
                java.lang.String r5 = "windowBackgroundWhiteBlueText4"
                java.lang.String r6 = "switchTrackChecked"
                if (r12 != r0) goto L_0x00c0
                r11.setColors(r6, r5)
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                java.util.ArrayList r12 = r12.newAlwaysShow
                int r12 = r12.size()
                int r12 = r12 + -5
                java.lang.Object[] r0 = new java.lang.Object[r2]
                java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r4, r12, r0)
                r11.setTextAndIcon((java.lang.String) r12, (int) r1, (boolean) r2)
                goto L_0x0395
            L_0x00c0:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeShowMoreRow
                if (r12 != r0) goto L_0x00e2
                r11.setColors(r6, r5)
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                java.util.ArrayList r12 = r12.newNeverShow
                int r12 = r12.size()
                int r12 = r12 + -5
                java.lang.Object[] r0 = new java.lang.Object[r2]
                java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r4, r12, r0)
                r11.setTextAndIcon((java.lang.String) r12, (int) r1, (boolean) r2)
                goto L_0x0395
            L_0x00e2:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeAddRow
                r1 = 2131165677(0x7var_ed, float:1.7945578E38)
                if (r12 != r0) goto L_0x0108
                r11.setColors(r6, r5)
                r0 = 2131625814(0x7f0e0756, float:1.8878847E38)
                java.lang.String r4 = "FilterAddChats"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r4 = org.telegram.ui.FilterCreateActivity.this
                int r4 = r4.includeSectionRow
                if (r12 == r4) goto L_0x0103
                r2 = 1
            L_0x0103:
                r11.setTextAndIcon((java.lang.String) r0, (int) r1, (boolean) r2)
                goto L_0x0395
            L_0x0108:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeAddRow
                if (r12 != r0) goto L_0x0395
                r11.setColors(r6, r5)
                r0 = 2131625870(0x7f0e078e, float:1.887896E38)
                java.lang.String r4 = "FilterRemoveChats"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r4 = org.telegram.ui.FilterCreateActivity.this
                int r4 = r4.excludeSectionRow
                if (r12 == r4) goto L_0x0126
                r2 = 1
            L_0x0126:
                r11.setTextAndIcon((java.lang.String) r0, (int) r1, (boolean) r2)
                goto L_0x0395
            L_0x012b:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.removeSectionRow
                if (r12 != r0) goto L_0x0140
                android.view.View r11 = r11.itemView
                android.content.Context r12 = r10.mContext
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r5, (java.lang.String) r7)
                r11.setBackgroundDrawable(r12)
                goto L_0x0395
            L_0x0140:
                android.view.View r11 = r11.itemView
                android.content.Context r12 = r10.mContext
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r6, (java.lang.String) r7)
                r11.setBackgroundDrawable(r12)
                goto L_0x0395
            L_0x014d:
                android.view.View r11 = r11.itemView
                r4 = r11
                org.telegram.ui.Cells.UserCell r4 = (org.telegram.ui.Cells.UserCell) r4
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeStartRow
                if (r12 < r11) goto L_0x018b
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeEndRow
                if (r12 >= r11) goto L_0x018b
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                java.util.ArrayList r11 = r11.newAlwaysShow
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeStartRow
                int r0 = r12 - r0
                java.lang.Object r11 = r11.get(r0)
                java.lang.Long r11 = (java.lang.Long) r11
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeShowMoreRow
                if (r0 != r1) goto L_0x0189
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeEndRow
                int r0 = r0 - r3
                if (r12 == r0) goto L_0x0188
                goto L_0x0189
            L_0x0188:
                r3 = 0
            L_0x0189:
                r9 = r3
                goto L_0x01c1
            L_0x018b:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.excludeStartRow
                if (r12 < r11) goto L_0x0273
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.excludeEndRow
                if (r12 >= r11) goto L_0x0273
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                java.util.ArrayList r11 = r11.newNeverShow
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeStartRow
                int r0 = r12 - r0
                java.lang.Object r11 = r11.get(r0)
                java.lang.Long r11 = (java.lang.Long) r11
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeShowMoreRow
                if (r0 != r1) goto L_0x0189
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeEndRow
                int r0 = r0 - r3
                if (r12 == r0) goto L_0x0188
                goto L_0x0189
            L_0x01c1:
                long r0 = r11.longValue()
                r5 = 0
                int r12 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r12 <= 0) goto L_0x0205
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
                org.telegram.tgnet.TLRPC$User r5 = r12.getUser(r11)
                if (r5 == 0) goto L_0x0395
                boolean r11 = r5.bot
                if (r11 == 0) goto L_0x01e6
                r11 = 2131624698(0x7f0e02fa, float:1.8876583E38)
                java.lang.String r12 = "Bot"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            L_0x01e4:
                r7 = r11
                goto L_0x01fe
            L_0x01e6:
                boolean r11 = r5.contact
                if (r11 == 0) goto L_0x01f4
                r11 = 2131625836(0x7f0e076c, float:1.8878891E38)
                java.lang.String r12 = "FilterContact"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x01e4
            L_0x01f4:
                r11 = 2131625866(0x7f0e078a, float:1.8878952E38)
                java.lang.String r12 = "FilterNonContact"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x01e4
            L_0x01fe:
                r6 = 0
                r8 = 0
                r4.setData(r5, r6, r7, r8, r9)
                goto L_0x0395
            L_0x0205:
                org.telegram.ui.FilterCreateActivity r12 = org.telegram.ui.FilterCreateActivity.this
                org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
                long r0 = r11.longValue()
                long r0 = -r0
                java.lang.Long r11 = java.lang.Long.valueOf(r0)
                org.telegram.tgnet.TLRPC$Chat r5 = r12.getChat(r11)
                if (r5 == 0) goto L_0x0395
                int r11 = r5.participants_count
                if (r11 == 0) goto L_0x0228
                java.lang.Object[] r12 = new java.lang.Object[r2]
                java.lang.String r0 = "Members"
                java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r0, r11, r12)
            L_0x0226:
                r7 = r11
                goto L_0x026c
            L_0x0228:
                java.lang.String r11 = r5.username
                boolean r11 = android.text.TextUtils.isEmpty(r11)
                if (r11 == 0) goto L_0x024e
                boolean r11 = org.telegram.messenger.ChatObject.isChannel(r5)
                if (r11 == 0) goto L_0x0244
                boolean r11 = r5.megagroup
                if (r11 != 0) goto L_0x0244
                r11 = 2131624939(0x7f0e03eb, float:1.8877072E38)
                java.lang.String r12 = "ChannelPrivate"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x0226
            L_0x0244:
                r11 = 2131626577(0x7f0e0a51, float:1.8880394E38)
                java.lang.String r12 = "MegaPrivate"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x0226
            L_0x024e:
                boolean r11 = org.telegram.messenger.ChatObject.isChannel(r5)
                if (r11 == 0) goto L_0x0262
                boolean r11 = r5.megagroup
                if (r11 != 0) goto L_0x0262
                r11 = 2131624942(0x7f0e03ee, float:1.8877078E38)
                java.lang.String r12 = "ChannelPublic"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x0226
            L_0x0262:
                r11 = 2131626580(0x7f0e0a54, float:1.88804E38)
                java.lang.String r12 = "MegaPublic"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x0226
            L_0x026c:
                r6 = 0
                r8 = 0
                r4.setData(r5, r6, r7, r8, r9)
                goto L_0x0395
            L_0x0273:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeContactsRow
                if (r12 != r11) goto L_0x0295
                r11 = 2131625837(0x7f0e076d, float:1.8878893E38)
                java.lang.String r0 = "FilterContacts"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeSectionRow
                if (r12 == r0) goto L_0x028e
                r2 = 1
            L_0x028e:
                java.lang.String r12 = "contacts"
            L_0x0290:
                r6 = r11
                r5 = r12
                r9 = r2
                goto L_0x0362
            L_0x0295:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeNonContactsRow
                if (r12 != r11) goto L_0x02b3
                r11 = 2131625867(0x7f0e078b, float:1.8878954E38)
                java.lang.String r0 = "FilterNonContacts"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeSectionRow
                if (r12 == r0) goto L_0x02b0
                r2 = 1
            L_0x02b0:
                java.lang.String r12 = "non_contacts"
                goto L_0x0290
            L_0x02b3:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeGroupsRow
                if (r12 != r11) goto L_0x02d1
                r11 = 2131625854(0x7f0e077e, float:1.8878928E38)
                java.lang.String r0 = "FilterGroups"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeSectionRow
                if (r12 == r0) goto L_0x02ce
                r2 = 1
            L_0x02ce:
                java.lang.String r12 = "groups"
                goto L_0x0290
            L_0x02d1:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeChannelsRow
                if (r12 != r11) goto L_0x02ef
                r11 = 2131625828(0x7f0e0764, float:1.8878875E38)
                java.lang.String r0 = "FilterChannels"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeSectionRow
                if (r12 == r0) goto L_0x02ec
                r2 = 1
            L_0x02ec:
                java.lang.String r12 = "channels"
                goto L_0x0290
            L_0x02ef:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.includeBotsRow
                if (r12 != r11) goto L_0x030d
                r11 = 2131625827(0x7f0e0763, float:1.8878873E38)
                java.lang.String r0 = "FilterBots"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeSectionRow
                if (r12 == r0) goto L_0x030a
                r2 = 1
            L_0x030a:
                java.lang.String r12 = "bots"
                goto L_0x0290
            L_0x030d:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.excludeMutedRow
                if (r12 != r11) goto L_0x032c
                r11 = 2131625857(0x7f0e0781, float:1.8878934E38)
                java.lang.String r0 = "FilterMuted"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeSectionRow
                if (r12 == r0) goto L_0x0328
                r2 = 1
            L_0x0328:
                java.lang.String r12 = "muted"
                goto L_0x0290
            L_0x032c:
                org.telegram.ui.FilterCreateActivity r11 = org.telegram.ui.FilterCreateActivity.this
                int r11 = r11.excludeReadRow
                if (r12 != r11) goto L_0x034b
                r11 = 2131625868(0x7f0e078c, float:1.8878956E38)
                java.lang.String r0 = "FilterRead"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeSectionRow
                if (r12 == r0) goto L_0x0347
                r2 = 1
            L_0x0347:
                java.lang.String r12 = "read"
                goto L_0x0290
            L_0x034b:
                r11 = 2131625824(0x7f0e0760, float:1.8878867E38)
                java.lang.String r0 = "FilterArchived"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r11)
                int r12 = r12 + r3
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeSectionRow
                if (r12 == r0) goto L_0x035e
                r2 = 1
            L_0x035e:
                java.lang.String r12 = "archived"
                goto L_0x0290
            L_0x0362:
                r7 = 0
                r8 = 0
                r4.setData(r5, r6, r7, r8, r9)
                return
            L_0x0368:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.HeaderCell r11 = (org.telegram.ui.Cells.HeaderCell) r11
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.includeHeaderRow
                if (r12 != r0) goto L_0x0381
                r12 = 2131625855(0x7f0e077f, float:1.887893E38)
                java.lang.String r0 = "FilterInclude"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0395
            L_0x0381:
                org.telegram.ui.FilterCreateActivity r0 = org.telegram.ui.FilterCreateActivity.this
                int r0 = r0.excludeHeaderRow
                if (r12 != r0) goto L_0x0395
                r12 = 2131625849(0x7f0e0779, float:1.8878918E38)
                java.lang.String r0 = "FilterExclude"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
            L_0x0395:
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
            if (i == FilterCreateActivity.this.nameSectionRow || i == FilterCreateActivity.this.namePreSectionRow || i == FilterCreateActivity.this.removeSectionRow) {
                return 3;
            }
            if (i == FilterCreateActivity.this.imageRow) {
                return 5;
            }
            return (i == FilterCreateActivity.this.includeSectionRow || i == FilterCreateActivity.this.excludeSectionRow) ? 6 : 4;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        FilterCreateActivity$$ExternalSyntheticLambda11 filterCreateActivity$$ExternalSyntheticLambda11 = new FilterCreateActivity$$ExternalSyntheticLambda11(this);
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, PollEditTextCell.class, UserCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"ImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        FilterCreateActivity$$ExternalSyntheticLambda11 filterCreateActivity$$ExternalSyntheticLambda112 = filterCreateActivity$$ExternalSyntheticLambda11;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) filterCreateActivity$$ExternalSyntheticLambda112, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) filterCreateActivity$$ExternalSyntheticLambda112, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        FilterCreateActivity$$ExternalSyntheticLambda11 filterCreateActivity$$ExternalSyntheticLambda113 = filterCreateActivity$$ExternalSyntheticLambda11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterCreateActivity$$ExternalSyntheticLambda113, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterCreateActivity$$ExternalSyntheticLambda113, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterCreateActivity$$ExternalSyntheticLambda113, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterCreateActivity$$ExternalSyntheticLambda113, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterCreateActivity$$ExternalSyntheticLambda113, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterCreateActivity$$ExternalSyntheticLambda113, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, filterCreateActivity$$ExternalSyntheticLambda113, "avatar_backgroundPink"));
        return arrayList;
    }

    /* access modifiers changed from: private */
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

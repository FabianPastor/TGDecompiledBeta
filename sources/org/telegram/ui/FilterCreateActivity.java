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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
    private static final int MAX_NAME_LENGTH = 12;
    private static final int done_button = 1;
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

        /* renamed from: lambda$new$0$org-telegram-ui-FilterCreateActivity$HintInnerCell  reason: not valid java name */
        public /* synthetic */ void m2158lambda$new$0$orgtelegramuiFilterCreateActivity$HintInnerCell(View v) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(156.0f), NUM));
        }
    }

    public FilterCreateActivity() {
        this((MessagesController.DialogFilter) null, (ArrayList<Long>) null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter) {
        this(dialogFilter, (ArrayList<Long>) null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter, ArrayList<Long> alwaysShow) {
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
        this.newFilterName = this.filter.name;
        this.newFilterFlags = this.filter.flags;
        ArrayList<Long> arrayList = new ArrayList<>(this.filter.alwaysShow);
        this.newAlwaysShow = arrayList;
        if (alwaysShow != null) {
            arrayList.addAll(alwaysShow);
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
        this.rowCount = i4 + 1;
        this.includeAddRow = i4;
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
        if (!this.newAlwaysShow.isEmpty()) {
            this.includeStartRow = this.rowCount;
            int count = (this.includeExpanded || this.newAlwaysShow.size() < 8) ? this.newAlwaysShow.size() : Math.min(5, this.newAlwaysShow.size());
            int i10 = this.rowCount + count;
            this.rowCount = i10;
            this.includeEndRow = i10;
            if (count != this.newAlwaysShow.size()) {
                int i11 = this.rowCount;
                this.rowCount = i11 + 1;
                this.includeShowMoreRow = i11;
            } else {
                this.includeShowMoreRow = -1;
            }
        } else {
            this.includeStartRow = -1;
            this.includeEndRow = -1;
            this.includeShowMoreRow = -1;
        }
        int i12 = this.rowCount;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.includeSectionRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.excludeHeaderRow = i13;
        this.rowCount = i14 + 1;
        this.excludeAddRow = i14;
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
        if (!this.newNeverShow.isEmpty()) {
            this.excludeStartRow = this.rowCount;
            int count2 = (this.excludeExpanded || this.newNeverShow.size() < 8) ? this.newNeverShow.size() : Math.min(5, this.newNeverShow.size());
            int i18 = this.rowCount + count2;
            this.rowCount = i18;
            this.excludeEndRow = i18;
            if (count2 != this.newNeverShow.size()) {
                int i19 = this.rowCount;
                this.rowCount = i19 + 1;
                this.excludeShowMoreRow = i19;
            } else {
                this.excludeShowMoreRow = -1;
            }
        } else {
            this.excludeStartRow = -1;
            this.excludeEndRow = -1;
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
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.creatingNew) {
            this.actionBar.setTitle(LocaleController.getString("FilterNew", NUM));
        } else {
            TextPaint paint = new TextPaint(1);
            paint.setTextSize((float) AndroidUtilities.dp(20.0f));
            this.actionBar.setTitle(Emoji.replaceEmoji(this.filter.name, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (FilterCreateActivity.this.checkDiscard()) {
                        FilterCreateActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    FilterCreateActivity.this.processDone();
                }
            }
        });
        this.doneItem = menu.addItem(1, (CharSequence) LocaleController.getString("Save", NUM).toUpperCase());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        AnonymousClass2 r4 = new RecyclerListView(context) {
            public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
                return false;
            }
        };
        this.listView = r4;
        r4.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new FilterCreateActivity$$ExternalSyntheticLambda3(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new FilterCreateActivity$$ExternalSyntheticLambda4(this));
        checkDoneButton(false);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2153lambda$createView$4$orgtelegramuiFilterCreateActivity(View view, int position) {
        if (getParentActivity() != null) {
            boolean z = true;
            if (position == this.includeShowMoreRow) {
                this.includeExpanded = true;
                updateRows();
            } else if (position == this.excludeShowMoreRow) {
                this.excludeExpanded = true;
                updateRows();
            } else if (position == this.includeAddRow || position == this.excludeAddRow) {
                ArrayList<Long> arrayList = position == this.excludeAddRow ? this.newNeverShow : this.newAlwaysShow;
                if (position != this.includeAddRow) {
                    z = false;
                }
                FilterUsersActivity fragment = new FilterUsersActivity(z, arrayList, this.newFilterFlags);
                fragment.setDelegate(new FilterCreateActivity$$ExternalSyntheticLambda5(this, position));
                presentFragment(fragment);
            } else if (position == this.removeRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("FilterDelete", NUM));
                builder.setMessage(LocaleController.getString("FilterDeleteAlert", NUM));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new FilterCreateActivity$$ExternalSyntheticLambda8(this));
                AlertDialog alertDialog = builder.create();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            } else if (position == this.nameRow) {
                PollEditTextCell cell = (PollEditTextCell) view;
                cell.getTextView().requestFocus();
                AndroidUtilities.showKeyboard(cell.getTextView());
            } else if (view instanceof UserCell) {
                UserCell cell2 = (UserCell) view;
                CharSequence name = cell2.getName();
                Object currentObject = cell2.getCurrentObject();
                if (position >= this.includeSectionRow) {
                    z = false;
                }
                showRemoveAlert(position, name, currentObject, z);
            }
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2149lambda$createView$0$orgtelegramuiFilterCreateActivity(int position, ArrayList ids, int flags) {
        this.newFilterFlags = flags;
        if (position == this.excludeAddRow) {
            this.newNeverShow = ids;
            for (int a = 0; a < this.newNeverShow.size(); a++) {
                Long id = this.newNeverShow.get(a);
                this.newAlwaysShow.remove(id);
                this.newPinned.delete(id.longValue());
            }
        } else {
            this.newAlwaysShow = ids;
            for (int a2 = 0; a2 < this.newAlwaysShow.size(); a2++) {
                this.newNeverShow.remove(this.newAlwaysShow.get(a2));
            }
            ArrayList<Long> toRemove = new ArrayList<>();
            int N = this.newPinned.size();
            for (int a3 = 0; a3 < N; a3++) {
                Long did = Long.valueOf(this.newPinned.keyAt(a3));
                if (!DialogObject.isEncryptedDialog(did.longValue()) && !this.newAlwaysShow.contains(did)) {
                    toRemove.add(did);
                }
            }
            int N2 = toRemove.size();
            for (int a4 = 0; a4 < N2; a4++) {
                this.newPinned.delete(toRemove.get(a4).longValue());
            }
        }
        fillFilterName();
        checkDoneButton(false);
        updateRows();
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2152lambda$createView$3$orgtelegramuiFilterCreateActivity(DialogInterface dialog, int which) {
        AlertDialog progressDialog = null;
        if (getParentActivity() != null) {
            progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
        }
        TLRPC.TL_messages_updateDialogFilter req = new TLRPC.TL_messages_updateDialogFilter();
        req.id = this.filter.id;
        getConnectionsManager().sendRequest(req, new FilterCreateActivity$$ExternalSyntheticLambda14(this, progressDialog));
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2151lambda$createView$2$orgtelegramuiFilterCreateActivity(AlertDialog progressDialogFinal, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new FilterCreateActivity$$ExternalSyntheticLambda11(this, progressDialogFinal));
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2150lambda$createView$1$orgtelegramuiFilterCreateActivity(AlertDialog progressDialogFinal) {
        if (progressDialogFinal != null) {
            try {
                progressDialogFinal.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        getMessagesController().removeFilter(this.filter);
        getMessagesStorage().deleteDialogFilter(this.filter);
        finishFragment();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ boolean m2154lambda$createView$5$orgtelegramuiFilterCreateActivity(View view, int position) {
        boolean z = false;
        if (!(view instanceof UserCell)) {
            return false;
        }
        UserCell cell = (UserCell) view;
        CharSequence name = cell.getName();
        Object currentObject = cell.getCurrentObject();
        if (position < this.includeSectionRow) {
            z = true;
        }
        showRemoveAlert(position, name, currentObject, z);
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

    private void fillFilterName() {
        if (!this.creatingNew) {
            return;
        }
        if (TextUtils.isEmpty(this.newFilterName) || !this.nameChangedManually) {
            int flags = this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
            String newName = "";
            if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & flags) == MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) {
                if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                    newName = LocaleController.getString("FilterNameUnread", NUM);
                } else if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                    newName = LocaleController.getString("FilterNameNonMuted", NUM);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_CONTACTS & flags) != 0) {
                if ((flags & (MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ -1)) == 0) {
                    newName = LocaleController.getString("FilterContacts", NUM);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS & flags) != 0) {
                if ((flags & (MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ -1)) == 0) {
                    newName = LocaleController.getString("FilterNonContacts", NUM);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_GROUPS & flags) != 0) {
                if ((flags & (MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ -1)) == 0) {
                    newName = LocaleController.getString("FilterGroups", NUM);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_BOTS & flags) != 0) {
                if ((flags & (MessagesController.DIALOG_FILTER_FLAG_BOTS ^ -1)) == 0) {
                    newName = LocaleController.getString("FilterBots", NUM);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_CHANNELS & flags) != 0 && (flags & (MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ -1)) == 0) {
                newName = LocaleController.getString("FilterChannels", NUM);
            }
            if (newName != null && newName.length() > 12) {
                newName = "";
            }
            this.newFilterName = newName;
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.nameRow);
            if (holder != null) {
                this.adapter.onViewAttachedToWindow(holder);
            }
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
            builder.setPositiveButton(LocaleController.getString("FilterDiscardNewSave", NUM), new FilterCreateActivity$$ExternalSyntheticLambda0(this));
        } else {
            builder.setTitle(LocaleController.getString("FilterDiscardTitle", NUM));
            builder.setMessage(LocaleController.getString("FilterDiscardAlert", NUM));
            builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new FilterCreateActivity$$ExternalSyntheticLambda6(this));
        }
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new FilterCreateActivity$$ExternalSyntheticLambda7(this));
        showDialog(builder.create());
        return false;
    }

    /* renamed from: lambda$checkDiscard$6$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2146lambda$checkDiscard$6$orgtelegramuiFilterCreateActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$7$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2147lambda$checkDiscard$7$orgtelegramuiFilterCreateActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$8$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2148lambda$checkDiscard$8$orgtelegramuiFilterCreateActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    private void showRemoveAlert(int position, CharSequence name, Object object, boolean include) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (include) {
            builder.setTitle(LocaleController.getString("FilterRemoveInclusionTitle", NUM));
            if (object instanceof String) {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionText", NUM, name));
            } else if (object instanceof TLRPC.User) {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionUserText", NUM, name));
            } else {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionChatText", NUM, name));
            }
        } else {
            builder.setTitle(LocaleController.getString("FilterRemoveExclusionTitle", NUM));
            if (object instanceof String) {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionText", NUM, name));
            } else if (object instanceof TLRPC.User) {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionUserText", NUM, name));
            } else {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionChatText", NUM, name));
            }
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("StickersRemove", NUM), new FilterCreateActivity$$ExternalSyntheticLambda9(this, position, include));
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* renamed from: lambda$showRemoveAlert$9$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2157lambda$showRemoveAlert$9$orgtelegramuiFilterCreateActivity(int position, boolean include, DialogInterface dialogInterface, int i) {
        if (position == this.includeContactsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ -1;
        } else if (position == this.includeNonContactsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ -1;
        } else if (position == this.includeGroupsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ -1;
        } else if (position == this.includeChannelsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ -1;
        } else if (position == this.includeBotsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_BOTS ^ -1;
        } else if (position == this.excludeArchivedRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ -1;
        } else if (position == this.excludeMutedRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ -1;
        } else if (position == this.excludeReadRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ -1;
        } else if (include) {
            this.newAlwaysShow.remove(position - this.includeStartRow);
        } else {
            this.newNeverShow.remove(position - this.excludeStartRow);
        }
        fillFilterName();
        updateRows();
        checkDoneButton(true);
    }

    /* access modifiers changed from: private */
    public void processDone() {
        saveFilterToServer(this.filter, this.newFilterFlags, this.newFilterName, this.newAlwaysShow, this.newNeverShow, this.newPinned, this.creatingNew, false, this.hasUserChanged, true, true, this, new FilterCreateActivity$$ExternalSyntheticLambda10(this));
    }

    /* renamed from: lambda$processDone$10$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2156lambda$processDone$10$orgtelegramuiFilterCreateActivity() {
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        finishFragment();
    }

    private static void processAddFilter(MessagesController.DialogFilter filter2, int newFilterFlags2, String newFilterName2, ArrayList<Long> newAlwaysShow2, ArrayList<Long> newNeverShow2, boolean creatingNew2, boolean atBegin, boolean hasUserChanged2, boolean resetUnreadCounter, BaseFragment fragment, Runnable onFinish) {
        if (filter2.flags != newFilterFlags2 || hasUserChanged2) {
            filter2.pendingUnreadCount = -1;
            if (resetUnreadCounter) {
                filter2.unreadCount = -1;
            }
        }
        filter2.flags = newFilterFlags2;
        filter2.name = newFilterName2;
        filter2.neverShow = newNeverShow2;
        filter2.alwaysShow = newAlwaysShow2;
        if (creatingNew2) {
            fragment.getMessagesController().addFilter(filter2, atBegin);
        } else {
            fragment.getMessagesController().onFilterUpdate(filter2);
        }
        fragment.getMessagesStorage().saveDialogFilter(filter2, atBegin, true);
        if (onFinish != null) {
            onFinish.run();
        }
    }

    public static void saveFilterToServer(MessagesController.DialogFilter filter2, int newFilterFlags2, String newFilterName2, ArrayList<Long> newAlwaysShow2, ArrayList<Long> newNeverShow2, LongSparseIntArray newPinned2, boolean creatingNew2, boolean atBegin, boolean hasUserChanged2, boolean resetUnreadCounter, boolean progress, BaseFragment fragment, Runnable onFinish) {
        AlertDialog progressDialog;
        ArrayList<TLRPC.InputPeer> toArray;
        ArrayList<Long> fromArray;
        ArrayList<Long> pinArray;
        ArrayList<Long> fromArray2;
        MessagesController.DialogFilter dialogFilter = filter2;
        LongSparseIntArray longSparseIntArray = newPinned2;
        if (fragment != null && fragment.getParentActivity() != null) {
            int i = 3;
            boolean z = false;
            if (progress) {
                AlertDialog progressDialog2 = new AlertDialog(fragment.getParentActivity(), 3);
                progressDialog2.setCanCancel(false);
                progressDialog2.show();
                progressDialog = progressDialog2;
            } else {
                progressDialog = null;
            }
            TLRPC.TL_messages_updateDialogFilter req = new TLRPC.TL_messages_updateDialogFilter();
            req.id = dialogFilter.id;
            int i2 = 1;
            req.flags |= 1;
            req.filter = new TLRPC.TL_dialogFilter();
            req.filter.contacts = (newFilterFlags2 & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0;
            req.filter.non_contacts = (newFilterFlags2 & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0;
            req.filter.groups = (newFilterFlags2 & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0;
            req.filter.broadcasts = (newFilterFlags2 & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0;
            req.filter.bots = (newFilterFlags2 & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0;
            req.filter.exclude_muted = (newFilterFlags2 & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0;
            req.filter.exclude_read = (newFilterFlags2 & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0;
            TLRPC.TL_dialogFilter tL_dialogFilter = req.filter;
            if ((newFilterFlags2 & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
                z = true;
            }
            tL_dialogFilter.exclude_archived = z;
            req.filter.id = dialogFilter.id;
            req.filter.title = newFilterName2;
            MessagesController messagesController = fragment.getMessagesController();
            ArrayList<Long> pinArray2 = new ArrayList<>();
            if (newPinned2.size() != 0) {
                int N = newPinned2.size();
                for (int a = 0; a < N; a++) {
                    long key = longSparseIntArray.keyAt(a);
                    if (!DialogObject.isEncryptedDialog(key)) {
                        pinArray2.add(Long.valueOf(key));
                    }
                }
                Collections.sort(pinArray2, new FilterCreateActivity$$ExternalSyntheticLambda13(longSparseIntArray));
            }
            int b = 0;
            while (b < i) {
                if (b == 0) {
                    fromArray = newAlwaysShow2;
                    toArray = req.filter.include_peers;
                } else if (b == i2) {
                    fromArray = newNeverShow2;
                    toArray = req.filter.exclude_peers;
                } else {
                    fromArray = pinArray2;
                    toArray = req.filter.pinned_peers;
                }
                int a2 = 0;
                int N2 = fromArray.size();
                while (a2 < N2) {
                    long did = fromArray.get(a2).longValue();
                    if (b == 0 && longSparseIntArray.indexOfKey(did) >= 0) {
                        fromArray2 = fromArray;
                        pinArray = pinArray2;
                    } else if (DialogObject.isEncryptedDialog(did)) {
                        fromArray2 = fromArray;
                        pinArray = pinArray2;
                    } else if (did > 0) {
                        TLRPC.User user = messagesController.getUser(Long.valueOf(did));
                        if (user != null) {
                            TLRPC.InputPeer inputPeer = new TLRPC.TL_inputPeerUser();
                            inputPeer.user_id = did;
                            inputPeer.access_hash = user.access_hash;
                            toArray = toArray;
                            toArray.add(inputPeer);
                        }
                        fromArray2 = fromArray;
                        pinArray = pinArray2;
                    } else {
                        fromArray2 = fromArray;
                        TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-did));
                        if (chat == null) {
                            pinArray = pinArray2;
                        } else if (ChatObject.isChannel(chat)) {
                            TLRPC.InputPeer inputPeer2 = new TLRPC.TL_inputPeerChannel();
                            pinArray = pinArray2;
                            inputPeer2.channel_id = -did;
                            inputPeer2.access_hash = chat.access_hash;
                            toArray.add(inputPeer2);
                        } else {
                            pinArray = pinArray2;
                            TLRPC.InputPeer inputPeer3 = new TLRPC.TL_inputPeerChat();
                            inputPeer3.chat_id = -did;
                            toArray.add(inputPeer3);
                        }
                    }
                    a2++;
                    fromArray = fromArray2;
                    pinArray2 = pinArray;
                }
                ArrayList<Long> arrayList = pinArray2;
                b++;
                i = 3;
                i2 = 1;
            }
            ArrayList<Long> arrayList2 = pinArray2;
            MessagesController messagesController2 = messagesController;
            FilterCreateActivity$$ExternalSyntheticLambda1 filterCreateActivity$$ExternalSyntheticLambda1 = r0;
            FilterCreateActivity$$ExternalSyntheticLambda1 filterCreateActivity$$ExternalSyntheticLambda12 = new FilterCreateActivity$$ExternalSyntheticLambda1(progress, progressDialog, filter2, newFilterFlags2, newFilterName2, newAlwaysShow2, newNeverShow2, creatingNew2, atBegin, hasUserChanged2, resetUnreadCounter, fragment, onFinish);
            fragment.getConnectionsManager().sendRequest(req, filterCreateActivity$$ExternalSyntheticLambda1);
            if (!progress) {
                processAddFilter(filter2, newFilterFlags2, newFilterName2, newAlwaysShow2, newNeverShow2, creatingNew2, atBegin, hasUserChanged2, resetUnreadCounter, fragment, onFinish);
            }
        }
    }

    static /* synthetic */ int lambda$saveFilterToServer$11(LongSparseIntArray newPinned2, Long o1, Long o2) {
        int idx1 = newPinned2.get(o1.longValue());
        int idx2 = newPinned2.get(o2.longValue());
        if (idx1 > idx2) {
            return 1;
        }
        if (idx1 < idx2) {
            return -1;
        }
        return 0;
    }

    static /* synthetic */ void lambda$saveFilterToServer$12(boolean progress, AlertDialog progressDialog, MessagesController.DialogFilter filter2, int newFilterFlags2, String newFilterName2, ArrayList newAlwaysShow2, ArrayList newNeverShow2, boolean creatingNew2, boolean atBegin, boolean hasUserChanged2, boolean resetUnreadCounter, BaseFragment fragment, Runnable onFinish) {
        if (progress) {
            if (progressDialog != null) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            processAddFilter(filter2, newFilterFlags2, newFilterName2, newAlwaysShow2, newNeverShow2, creatingNew2, atBegin, hasUserChanged2, resetUnreadCounter, fragment, onFinish);
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
    public void checkDoneButton(boolean animated) {
        boolean z = true;
        boolean enabled = !TextUtils.isEmpty(this.newFilterName) && this.newFilterName.length() <= 12;
        if (enabled) {
            if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == 0 && this.newAlwaysShow.isEmpty()) {
                z = false;
            }
            enabled = z;
            if (enabled && !this.creatingNew) {
                enabled = hasChanges();
            }
        }
        if (this.doneItem.isEnabled() != enabled) {
            this.doneItem.setEnabled(enabled);
            float f = 1.0f;
            if (animated) {
                ViewPropertyAnimator scaleX = this.doneItem.animate().alpha(enabled ? 1.0f : 0.0f).scaleX(enabled ? 1.0f : 0.0f);
                if (!enabled) {
                    f = 0.0f;
                }
                scaleX.scaleY(f).setDuration(180).start();
                return;
            }
            this.doneItem.setAlpha(enabled ? 1.0f : 0.0f);
            this.doneItem.setScaleX(enabled ? 1.0f : 0.0f);
            ActionBarMenuItem actionBarMenuItem = this.doneItem;
            if (!enabled) {
                f = 0.0f;
            }
            actionBarMenuItem.setScaleY(f);
        }
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View cell) {
        if (cell instanceof PollEditTextCell) {
            PollEditTextCell textCell = (PollEditTextCell) cell;
            String str = this.newFilterName;
            int left = 12 - (str != null ? str.length() : 0);
            if (((float) left) <= 3.6000004f) {
                textCell.setText2(String.format("%d", new Object[]{Integer.valueOf(left)}));
                SimpleTextView textView = textCell.getTextView2();
                String key = left < 0 ? "windowBackgroundWhiteRedText5" : "windowBackgroundWhiteGrayText3";
                textView.setTextColor(Theme.getColor(key));
                textView.setTag(key);
                textView.setAlpha((((PollEditTextCell) cell).getTextView().isFocused() || left < 0) ? 1.0f : 0.0f);
                return;
            }
            textCell.setText2("");
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 3 || type == 0 || type == 2 || type == 5) ? false : true;
        }

        public int getItemCount() {
            return FilterCreateActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    UserCell cell = new UserCell(this.mContext, 6, 0, false);
                    cell.setSelfAsSavedMessages(true);
                    cell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = cell;
                    break;
                case 2:
                    final PollEditTextCell cell2 = new PollEditTextCell(this.mContext, (View.OnClickListener) null);
                    cell2.createErrorTextView();
                    cell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    cell2.addTextWatcher(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                            if (cell2.getTag() == null) {
                                String newName = s.toString();
                                if (!TextUtils.equals(newName, FilterCreateActivity.this.newFilterName)) {
                                    boolean unused = FilterCreateActivity.this.nameChangedManually = !TextUtils.isEmpty(newName);
                                    String unused2 = FilterCreateActivity.this.newFilterName = newName;
                                }
                                RecyclerView.ViewHolder holder = FilterCreateActivity.this.listView.findViewHolderForAdapterPosition(FilterCreateActivity.this.nameRow);
                                if (holder != null) {
                                    FilterCreateActivity.this.setTextLeft(holder.itemView);
                                }
                                FilterCreateActivity.this.checkDoneButton(true);
                            }
                        }
                    });
                    EditTextBoldCursor editText = cell2.getTextView();
                    cell2.setShowNextButton(true);
                    editText.setOnFocusChangeListener(new FilterCreateActivity$ListAdapter$$ExternalSyntheticLambda0(this, cell2));
                    editText.setImeOptions(NUM);
                    view = cell2;
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 5:
                    view = new HintInnerCell(this.mContext);
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-FilterCreateActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2159x999fa0b5(PollEditTextCell cell, View v, boolean hasFocus) {
            cell.getTextView2().setAlpha((hasFocus || FilterCreateActivity.this.newFilterName.length() > 12) ? 1.0f : 0.0f);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 2) {
                FilterCreateActivity.this.setTextLeft(holder.itemView);
                PollEditTextCell textCell = (PollEditTextCell) holder.itemView;
                textCell.setTag(1);
                textCell.setTextAndHint(FilterCreateActivity.this.newFilterName != null ? FilterCreateActivity.this.newFilterName : "", LocaleController.getString("FilterNameHint", NUM), false);
                textCell.setTag((Object) null);
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 2) {
                EditTextBoldCursor editText = ((PollEditTextCell) holder.itemView).getTextView();
                if (editText.isFocused()) {
                    editText.clearFocus();
                    AndroidUtilities.hideKeyboard(editText);
                }
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean divider;
            Long id;
            String status;
            String status2;
            boolean divider2;
            String str;
            String name;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == FilterCreateActivity.this.includeHeaderRow) {
                        headerCell.setText(LocaleController.getString("FilterInclude", NUM));
                        return;
                    } else if (position == FilterCreateActivity.this.excludeHeaderRow) {
                        headerCell.setText(LocaleController.getString("FilterExclude", NUM));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    UserCell userCell = (UserCell) holder.itemView;
                    if (position >= FilterCreateActivity.this.includeStartRow && position < FilterCreateActivity.this.includeEndRow) {
                        id = (Long) FilterCreateActivity.this.newAlwaysShow.get(position - FilterCreateActivity.this.includeStartRow);
                        if (!(FilterCreateActivity.this.includeShowMoreRow == -1 && position == FilterCreateActivity.this.includeEndRow - 1)) {
                            z = true;
                        }
                        divider = z;
                    } else if (position < FilterCreateActivity.this.excludeStartRow || position >= FilterCreateActivity.this.excludeEndRow) {
                        if (position == FilterCreateActivity.this.includeContactsRow) {
                            name = LocaleController.getString("FilterContacts", NUM);
                            str = "contacts";
                            if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                z = true;
                            }
                            divider2 = z;
                        } else if (position == FilterCreateActivity.this.includeNonContactsRow) {
                            name = LocaleController.getString("FilterNonContacts", NUM);
                            str = "non_contacts";
                            if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                z = true;
                            }
                            divider2 = z;
                        } else if (position == FilterCreateActivity.this.includeGroupsRow) {
                            name = LocaleController.getString("FilterGroups", NUM);
                            str = "groups";
                            if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                z = true;
                            }
                            divider2 = z;
                        } else if (position == FilterCreateActivity.this.includeChannelsRow) {
                            name = LocaleController.getString("FilterChannels", NUM);
                            str = "channels";
                            if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                z = true;
                            }
                            divider2 = z;
                        } else if (position == FilterCreateActivity.this.includeBotsRow) {
                            name = LocaleController.getString("FilterBots", NUM);
                            str = "bots";
                            if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                z = true;
                            }
                            divider2 = z;
                        } else if (position == FilterCreateActivity.this.excludeMutedRow) {
                            name = LocaleController.getString("FilterMuted", NUM);
                            str = "muted";
                            if (position + 1 != FilterCreateActivity.this.excludeSectionRow) {
                                z = true;
                            }
                            divider2 = z;
                        } else if (position == FilterCreateActivity.this.excludeReadRow) {
                            name = LocaleController.getString("FilterRead", NUM);
                            str = "read";
                            if (position + 1 != FilterCreateActivity.this.excludeSectionRow) {
                                z = true;
                            }
                            divider2 = z;
                        } else {
                            name = LocaleController.getString("FilterArchived", NUM);
                            str = "archived";
                            if (position + 1 != FilterCreateActivity.this.excludeSectionRow) {
                                z = true;
                            }
                            divider2 = z;
                        }
                        userCell.setData(str, name, (CharSequence) null, 0, divider2);
                        return;
                    } else {
                        id = (Long) FilterCreateActivity.this.newNeverShow.get(position - FilterCreateActivity.this.excludeStartRow);
                        if (!(FilterCreateActivity.this.excludeShowMoreRow == -1 && position == FilterCreateActivity.this.excludeEndRow - 1)) {
                            z = true;
                        }
                        divider = z;
                    }
                    if (id.longValue() > 0) {
                        TLRPC.User user = FilterCreateActivity.this.getMessagesController().getUser(id);
                        if (user != null) {
                            if (user.bot) {
                                status2 = LocaleController.getString("Bot", NUM);
                            } else if (user.contact) {
                                status2 = LocaleController.getString("FilterContact", NUM);
                            } else {
                                status2 = LocaleController.getString("FilterNonContact", NUM);
                            }
                            userCell.setData(user, (CharSequence) null, status2, 0, divider);
                            return;
                        }
                        return;
                    }
                    TLRPC.Chat chat = FilterCreateActivity.this.getMessagesController().getChat(Long.valueOf(-id.longValue()));
                    if (chat != null) {
                        if (chat.participants_count != 0) {
                            status = LocaleController.formatPluralString("Members", chat.participants_count);
                        } else if (TextUtils.isEmpty(chat.username)) {
                            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                status = LocaleController.getString("MegaPrivate", NUM);
                            } else {
                                status = LocaleController.getString("ChannelPrivate", NUM);
                            }
                        } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            status = LocaleController.getString("MegaPublic", NUM);
                        } else {
                            status = LocaleController.getString("ChannelPublic", NUM);
                        }
                        userCell.setData(chat, (CharSequence) null, status, 0, divider);
                        return;
                    }
                    return;
                case 3:
                    if (position == FilterCreateActivity.this.removeSectionRow) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 4:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == FilterCreateActivity.this.removeRow) {
                        textCell.setColors((String) null, "windowBackgroundWhiteRedText5");
                        textCell.setText(LocaleController.getString("FilterDelete", NUM), false);
                        return;
                    } else if (position == FilterCreateActivity.this.includeShowMoreRow) {
                        textCell.setColors("switchTrackChecked", "windowBackgroundWhiteBlueText4");
                        textCell.setTextAndIcon(LocaleController.formatPluralString("FilterShowMoreChats", FilterCreateActivity.this.newAlwaysShow.size() - 5), NUM, false);
                        return;
                    } else if (position == FilterCreateActivity.this.excludeShowMoreRow) {
                        textCell.setColors("switchTrackChecked", "windowBackgroundWhiteBlueText4");
                        textCell.setTextAndIcon(LocaleController.formatPluralString("FilterShowMoreChats", FilterCreateActivity.this.newNeverShow.size() - 5), NUM, false);
                        return;
                    } else if (position == FilterCreateActivity.this.includeAddRow) {
                        textCell.setColors("switchTrackChecked", "windowBackgroundWhiteBlueText4");
                        String string = LocaleController.getString("FilterAddChats", NUM);
                        if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                            z = true;
                        }
                        textCell.setTextAndIcon(string, NUM, z);
                        return;
                    } else if (position == FilterCreateActivity.this.excludeAddRow) {
                        textCell.setColors("switchTrackChecked", "windowBackgroundWhiteBlueText4");
                        String string2 = LocaleController.getString("FilterRemoveChats", NUM);
                        if (position + 1 != FilterCreateActivity.this.excludeSectionRow) {
                            z = true;
                        }
                        textCell.setTextAndIcon(string2, NUM, z);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == FilterCreateActivity.this.includeSectionRow) {
                        cell.setText(LocaleController.getString("FilterIncludeInfo", NUM));
                    } else if (position == FilterCreateActivity.this.excludeSectionRow) {
                        cell.setText(LocaleController.getString("FilterExcludeInfo", NUM));
                    }
                    if (position == FilterCreateActivity.this.excludeSectionRow && FilterCreateActivity.this.removeSectionRow == -1) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == FilterCreateActivity.this.includeHeaderRow || position == FilterCreateActivity.this.excludeHeaderRow) {
                return 0;
            }
            if (position >= FilterCreateActivity.this.includeStartRow && position < FilterCreateActivity.this.includeEndRow) {
                return 1;
            }
            if ((position >= FilterCreateActivity.this.excludeStartRow && position < FilterCreateActivity.this.excludeEndRow) || position == FilterCreateActivity.this.includeContactsRow || position == FilterCreateActivity.this.includeNonContactsRow || position == FilterCreateActivity.this.includeGroupsRow || position == FilterCreateActivity.this.includeChannelsRow || position == FilterCreateActivity.this.includeBotsRow || position == FilterCreateActivity.this.excludeReadRow || position == FilterCreateActivity.this.excludeArchivedRow || position == FilterCreateActivity.this.excludeMutedRow) {
                return 1;
            }
            if (position == FilterCreateActivity.this.nameRow) {
                return 2;
            }
            if (position == FilterCreateActivity.this.nameSectionRow || position == FilterCreateActivity.this.namePreSectionRow || position == FilterCreateActivity.this.removeSectionRow) {
                return 3;
            }
            if (position == FilterCreateActivity.this.imageRow) {
                return 5;
            }
            if (position == FilterCreateActivity.this.includeSectionRow || position == FilterCreateActivity.this.excludeSectionRow) {
                return 6;
            }
            return 4;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDelegate = new FilterCreateActivity$$ExternalSyntheticLambda2(this);
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, PollEditTextCell.class, UserCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"ImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = themeDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = themeDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$14$org-telegram-ui-FilterCreateActivity  reason: not valid java name */
    public /* synthetic */ void m2155x50dd8b1d() {
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

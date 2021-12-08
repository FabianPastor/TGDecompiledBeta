package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ArchiveHintCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;

public class DialogsAdapter extends RecyclerListView.SelectionAdapter {
    private ArchiveHintCell archiveHintCell;
    /* access modifiers changed from: private */
    public Drawable arrowDrawable;
    /* access modifiers changed from: private */
    public int currentAccount;
    private int currentCount;
    private int dialogsCount;
    /* access modifiers changed from: private */
    public boolean dialogsListFrozen;
    /* access modifiers changed from: private */
    public int dialogsType;
    /* access modifiers changed from: private */
    public int folderId;
    private boolean forceShowEmptyCell;
    private boolean hasHints;
    private boolean isOnlySelect;
    private boolean isReordering;
    private long lastSortTime;
    private Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_contact> onlineContacts;
    private long openedDialogId;
    /* access modifiers changed from: private */
    public DialogsActivity parentFragment;
    private DialogsPreloader preloader;
    private int prevContactsCount;
    private int prevDialogsCount;
    private PullForegroundDrawable pullForegroundDrawable;
    private ArrayList<Long> selectedDialogs;
    private boolean showArchiveHint;

    public DialogsAdapter(DialogsActivity fragment, Context context, int type, int folder, boolean onlySelect, ArrayList<Long> selected, int account) {
        this.mContext = context;
        this.parentFragment = fragment;
        this.dialogsType = type;
        this.folderId = folder;
        this.isOnlySelect = onlySelect;
        this.hasHints = folder == 0 && type == 0 && !onlySelect;
        this.selectedDialogs = selected;
        this.currentAccount = account;
        if (folder == 1) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            this.showArchiveHint = preferences.getBoolean("archivehint", true);
            preferences.edit().putBoolean("archivehint", false).commit();
        }
        if (folder == 0) {
            this.preloader = new DialogsPreloader();
        }
    }

    public void setOpenedDialogId(long id) {
        this.openedDialogId = id;
    }

    public void onReorderStateChanged(boolean reordering) {
        this.isReordering = reordering;
    }

    public int fixPosition(int position) {
        if (this.hasHints) {
            position -= MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
        }
        if (this.showArchiveHint) {
            return position - 2;
        }
        int i = this.dialogsType;
        if (i == 11 || i == 13) {
            return position - 2;
        }
        if (i == 12) {
            return position - 1;
        }
        return position;
    }

    public boolean isDataSetChanged() {
        int current = this.currentCount;
        return current != getItemCount() || current == 1;
    }

    public void setDialogsType(int type) {
        this.dialogsType = type;
        notifyDataSetChanged();
    }

    public int getDialogsType() {
        return this.dialogsType;
    }

    public int getItemCount() {
        int i;
        int i2;
        int i3;
        int i4;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        int size = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
        this.dialogsCount = size;
        boolean z = true;
        if (this.forceShowEmptyCell || (i3 = this.dialogsType) == 7 || i3 == 8 || i3 == 11 || size != 0 || ((i4 = this.folderId) == 0 && !messagesController.isLoadingDialogs(i4) && MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId))) {
            int count = this.dialogsCount;
            int i5 = this.dialogsType;
            if (i5 == 7 || i5 == 8) {
                if (this.dialogsCount == 0) {
                    count++;
                }
            } else if (!messagesController.isDialogsEndReached(this.folderId) || this.dialogsCount == 0) {
                count++;
            }
            boolean hasContacts = false;
            if (this.hasHints) {
                count += messagesController.hintDialogs.size() + 2;
            } else if (this.dialogsType == 0 && messagesController.dialogs_dict.size() <= 10 && (i2 = this.folderId) == 0 && messagesController.isDialogsEndReached(i2)) {
                if (ContactsController.getInstance(this.currentAccount).contacts.isEmpty() && !ContactsController.getInstance(this.currentAccount).doneLoadingContacts) {
                    this.onlineContacts = null;
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("DialogsAdapter loadingContacts=");
                        if (!ContactsController.getInstance(this.currentAccount).contacts.isEmpty() || ContactsController.getInstance(this.currentAccount).doneLoadingContacts) {
                            z = false;
                        }
                        sb.append(z);
                        sb.append("dialogsCount=");
                        sb.append(this.dialogsCount);
                        sb.append(" dialogsType=");
                        sb.append(this.dialogsType);
                        FileLog.d(sb.toString());
                    }
                    this.currentCount = 0;
                    return 0;
                } else if (!ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
                    if (!(this.onlineContacts != null && this.prevDialogsCount == messagesController.dialogs_dict.size() && this.prevContactsCount == ContactsController.getInstance(this.currentAccount).contacts.size())) {
                        ArrayList<TLRPC.TL_contact> arrayList = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                        this.onlineContacts = arrayList;
                        this.prevContactsCount = arrayList.size();
                        this.prevDialogsCount = messagesController.dialogs_dict.size();
                        long selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
                        int a = 0;
                        int N = this.onlineContacts.size();
                        while (a < N) {
                            long userId = this.onlineContacts.get(a).user_id;
                            if (userId == selfId || messagesController.dialogs_dict.get(userId) != null) {
                                this.onlineContacts.remove(a);
                                a--;
                                N--;
                            }
                            a++;
                        }
                        if (this.onlineContacts.isEmpty()) {
                            this.onlineContacts = null;
                        }
                        sortOnlineContacts(false);
                    }
                    ArrayList<TLRPC.TL_contact> arrayList2 = this.onlineContacts;
                    if (arrayList2 != null) {
                        count += arrayList2.size() + 2;
                        hasContacts = true;
                    }
                }
            }
            int i6 = this.folderId;
            if (i6 == 0 && this.onlineContacts != null && !hasContacts) {
                this.onlineContacts = null;
            }
            if (i6 == 1 && this.showArchiveHint) {
                count += 2;
            }
            if (i6 == 0 && (i = this.dialogsCount) != 0) {
                count++;
                if (i > 10 && this.dialogsType == 0) {
                    count++;
                }
            }
            int i7 = this.dialogsType;
            if (i7 == 11 || i7 == 13) {
                count += 2;
            } else if (i7 == 12) {
                count++;
            }
            this.currentCount = count;
            return count;
        }
        this.onlineContacts = null;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("DialogsAdapter dialogsCount=" + this.dialogsCount + " dialogsType=" + this.dialogsType + " isLoadingDialogs=" + messagesController.isLoadingDialogs(this.folderId) + " isDialogsEndReached=" + MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId));
        }
        if (this.folderId != 1 || !this.showArchiveHint) {
            this.currentCount = 0;
            return 0;
        }
        this.currentCount = 2;
        return 2;
    }

    public TLObject getItem(int i) {
        int i2;
        int i3;
        ArrayList<TLRPC.TL_contact> arrayList = this.onlineContacts;
        if (arrayList == null || ((i2 = this.dialogsCount) != 0 && i < i2)) {
            if (this.showArchiveHint) {
                i -= 2;
            } else {
                int i4 = this.dialogsType;
                if (i4 == 11 || i4 == 13) {
                    i -= 2;
                } else if (i4 == 12) {
                    i--;
                }
            }
            ArrayList<TLRPC.Dialog> arrayList2 = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
            if (this.hasHints) {
                int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
                if (i < count + 2) {
                    return MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1);
                }
                i -= count + 2;
            }
            if (i < 0 || i >= arrayList2.size()) {
                return null;
            }
            return arrayList2.get(i);
        }
        if (i2 == 0) {
            i3 = i - 3;
        } else {
            i3 = i - (i2 + 2);
        }
        if (i3 < 0 || i3 >= arrayList.size()) {
            return null;
        }
        return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(i3).user_id));
    }

    public void sortOnlineContacts(boolean notify) {
        if (this.onlineContacts == null) {
            return;
        }
        if (!notify || SystemClock.elapsedRealtime() - this.lastSortTime >= 2000) {
            this.lastSortTime = SystemClock.elapsedRealtime();
            try {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                Collections.sort(this.onlineContacts, new DialogsAdapter$$ExternalSyntheticLambda1(MessagesController.getInstance(this.currentAccount), currentTime));
                if (notify) {
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ int lambda$sortOnlineContacts$0(MessagesController messagesController, int currentTime, TLRPC.TL_contact o1, TLRPC.TL_contact o2) {
        TLRPC.User user1 = messagesController.getUser(Long.valueOf(o2.user_id));
        TLRPC.User user2 = messagesController.getUser(Long.valueOf(o1.user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                    return (status2 < 0 || status1 != 0) ? 1 : 0;
                }
                return -1;
            } else if (status1 > status2) {
                return 1;
            } else {
                return status1 < status2 ? -1 : 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            return status1 < status2 ? -1 : 0;
        }
    }

    public void setDialogsListFrozen(boolean frozen) {
        this.dialogsListFrozen = frozen;
    }

    public ViewPager getArchiveHintCellPager() {
        ArchiveHintCell archiveHintCell2 = this.archiveHintCell;
        if (archiveHintCell2 != null) {
            return archiveHintCell2.getViewPager();
        }
        return null;
    }

    public void notifyDataSetChanged() {
        this.hasHints = this.folderId == 0 && this.dialogsType == 0 && !this.isOnlySelect && !MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty();
        super.notifyDataSetChanged();
    }

    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof DialogCell) {
            DialogCell dialogCell = (DialogCell) holder.itemView;
            dialogCell.onReorderStateChanged(this.isReordering, false);
            dialogCell.setDialogIndex(fixPosition(holder.getAdapterPosition()));
            dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialogCell.getDialogId())), false);
        }
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int viewType = holder.getItemViewType();
        return (viewType == 1 || viewType == 5 || viewType == 3 || viewType == 8 || viewType == 7 || viewType == 9 || viewType == 10 || viewType == 11) ? false : true;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        int i = viewType;
        switch (i) {
            case 0:
                DialogCell dialogCell = new DialogCell(this.parentFragment, this.mContext, true, false, this.currentAccount, (Theme.ResourcesProvider) null);
                dialogCell.setArchivedPullAnimation(this.pullForegroundDrawable);
                dialogCell.setPreloader(this.preloader);
                view = dialogCell;
                break;
            case 1:
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.setViewType(7);
                view = flickerLoadingView;
                break;
            case 2:
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setText(LocaleController.getString("RecentlyViewed", NUM));
                TextView textView = new TextView(this.mContext);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
                textView.setText(LocaleController.getString("RecentlyViewedHide", NUM));
                int i2 = 3;
                textView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
                if (!LocaleController.isRTL) {
                    i2 = 5;
                }
                headerCell.addView(textView, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, 17.0f, 15.0f, 17.0f, 0.0f));
                textView.setOnClickListener(new DialogsAdapter$$ExternalSyntheticLambda0(this));
                view = headerCell;
                break;
            case 3:
                FrameLayout frameLayout = new FrameLayout(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), NUM));
                    }
                };
                frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
                View v = new View(this.mContext);
                v.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                frameLayout.addView(v, LayoutHelper.createFrame(-1, -1.0f));
                view = frameLayout;
                break;
            case 4:
                view = new DialogMeUrlCell(this.mContext);
                break;
            case 5:
                view = new DialogsEmptyCell(this.mContext);
                break;
            case 6:
                view = new UserCell(this.mContext, 8, 0, false);
                break;
            case 7:
                view = new HeaderCell(this.mContext);
                break;
            case 8:
                view = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                combinedDrawable.setFullsize(true);
                view.setBackgroundDrawable(combinedDrawable);
                break;
            case 9:
                this.archiveHintCell = new ArchiveHintCell(this.mContext);
                view = this.archiveHintCell;
                break;
            case 10:
                view = new LastEmptyView(this.mContext);
                break;
            case 11:
                view = new TextInfoPrivacyCell(this.mContext) {
                    private long lastUpdateTime;
                    private float moveProgress;
                    private int movement;
                    private int originalX;
                    private int originalY;

                    /* access modifiers changed from: protected */
                    public void afterTextDraw() {
                        if (DialogsAdapter.this.arrowDrawable != null) {
                            Rect bounds = DialogsAdapter.this.arrowDrawable.getBounds();
                            Drawable access$000 = DialogsAdapter.this.arrowDrawable;
                            int i = this.originalX;
                            access$000.setBounds(i, this.originalY, bounds.width() + i, this.originalY + bounds.height());
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onTextDraw() {
                        if (DialogsAdapter.this.arrowDrawable != null) {
                            Rect bounds = DialogsAdapter.this.arrowDrawable.getBounds();
                            int dx = (int) (this.moveProgress * ((float) AndroidUtilities.dp(3.0f)));
                            this.originalX = bounds.left;
                            this.originalY = bounds.top;
                            DialogsAdapter.this.arrowDrawable.setBounds(this.originalX + dx, this.originalY + AndroidUtilities.dp(1.0f), this.originalX + dx + bounds.width(), this.originalY + AndroidUtilities.dp(1.0f) + bounds.height());
                            long newUpdateTime = SystemClock.elapsedRealtime();
                            long dt = newUpdateTime - this.lastUpdateTime;
                            if (dt > 17) {
                                dt = 17;
                            }
                            this.lastUpdateTime = newUpdateTime;
                            if (this.movement == 0) {
                                float f = this.moveProgress + (((float) dt) / 664.0f);
                                this.moveProgress = f;
                                if (f >= 1.0f) {
                                    this.movement = 1;
                                    this.moveProgress = 1.0f;
                                }
                            } else {
                                float f2 = this.moveProgress - (((float) dt) / 664.0f);
                                this.moveProgress = f2;
                                if (f2 <= 0.0f) {
                                    this.movement = 0;
                                    this.moveProgress = 0.0f;
                                }
                            }
                            getTextView().invalidate();
                        }
                    }
                };
                CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                combinedDrawable2.setFullsize(true);
                view.setBackgroundDrawable(combinedDrawable2);
                break;
            default:
                view = new TextCell(this.mContext);
                break;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, i == 5 ? -1 : -2));
        return new RecyclerListView.Holder(view);
    }

    /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Adapters-DialogsAdapter  reason: not valid java name */
    public /* synthetic */ void m1351x3c8bd2a9(View view1) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        int position;
        boolean z = true;
        switch (holder.getItemViewType()) {
            case 0:
                DialogCell cell = (DialogCell) holder.itemView;
                TLRPC.Dialog dialog = (TLRPC.Dialog) getItem(i);
                TLRPC.Dialog nextDialog = (TLRPC.Dialog) getItem(i + 1);
                cell.useSeparator = nextDialog != null;
                cell.fullSeparator = dialog.pinned && nextDialog != null && !nextDialog.pinned;
                if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                    if (dialog.id != this.openedDialogId) {
                        z = false;
                    }
                    cell.setDialogSelected(z);
                }
                cell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialog.id)), false);
                cell.setDialog(dialog, this.dialogsType, this.folderId);
                DialogsPreloader dialogsPreloader = this.preloader;
                if (dialogsPreloader != null && i < 10) {
                    dialogsPreloader.add(dialog.id);
                    return;
                }
                return;
            case 4:
                ((DialogMeUrlCell) holder.itemView).setRecentMeUrl((TLRPC.RecentMeUrl) getItem(i));
                return;
            case 5:
                DialogsEmptyCell cell2 = (DialogsEmptyCell) holder.itemView;
                int i2 = this.dialogsType;
                if (i2 != 7 && i2 != 8) {
                    if (this.onlineContacts == null) {
                        z = false;
                    }
                    cell2.setType(z ? 1 : 0);
                    return;
                } else if (MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
                    cell2.setType(2);
                    return;
                } else {
                    cell2.setType(3);
                    return;
                }
            case 6:
                UserCell cell3 = (UserCell) holder.itemView;
                int i3 = this.dialogsCount;
                if (i3 == 0) {
                    position = i - 3;
                } else {
                    position = (i - i3) - 2;
                }
                cell3.setData(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(position).user_id)), (CharSequence) null, (CharSequence) null, 0);
                return;
            case 7:
                HeaderCell cell4 = (HeaderCell) holder.itemView;
                int i4 = this.dialogsType;
                if (i4 != 11 && i4 != 12 && i4 != 13) {
                    cell4.setText(LocaleController.getString("YourContacts", NUM));
                    return;
                } else if (i == 0) {
                    cell4.setText(LocaleController.getString("ImportHeader", NUM));
                    return;
                } else {
                    cell4.setText(LocaleController.getString("ImportHeaderContacts", NUM));
                    return;
                }
            case 11:
                TextInfoPrivacyCell cell5 = (TextInfoPrivacyCell) holder.itemView;
                cell5.setText(LocaleController.getString("TapOnThePencil", NUM));
                if (this.arrowDrawable == null) {
                    Drawable drawable = this.mContext.getResources().getDrawable(NUM);
                    this.arrowDrawable = drawable;
                    drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText4"), PorterDuff.Mode.MULTIPLY));
                }
                TextView textView = cell5.getTextView();
                textView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.arrowDrawable, (Drawable) null);
                textView.getLayoutParams().width = -2;
                return;
            case 12:
                TextCell cell6 = (TextCell) holder.itemView;
                cell6.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
                String string = LocaleController.getString("CreateGroupForImport", NUM);
                if (this.dialogsCount == 0) {
                    z = false;
                }
                cell6.setTextAndIcon(string, NUM, z);
                cell6.setIsInDialogs();
                cell6.setOffsetFromImage(75);
                return;
            default:
                return;
        }
    }

    public int getItemViewType(int i) {
        int i2;
        if (this.onlineContacts != null) {
            int i3 = this.dialogsCount;
            if (i3 == 0) {
                if (i == 0) {
                    return 5;
                }
                if (i == 1) {
                    return 8;
                }
                if (i == 2) {
                    return 7;
                }
                return 6;
            } else if (i < i3) {
                return 0;
            } else {
                if (i == i3) {
                    return 8;
                }
                if (i == i3 + 1) {
                    return 7;
                }
                if (i == this.currentCount - 1) {
                    return 10;
                }
                return 6;
            }
        } else {
            if (this.hasHints) {
                int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
                if (i >= count + 2) {
                    i -= count + 2;
                } else if (i == 0) {
                    return 2;
                } else {
                    if (i == count + 1) {
                        return 3;
                    }
                    return 4;
                }
            } else if (!this.showArchiveHint) {
                int i4 = this.dialogsType;
                if (i4 == 11 || i4 == 13) {
                    if (i == 0) {
                        return 7;
                    }
                    if (i == 1) {
                        return 12;
                    }
                    i -= 2;
                } else if (i4 == 12) {
                    if (i == 0) {
                        return 7;
                    }
                    i--;
                }
            } else if (i == 0) {
                return 9;
            } else {
                if (i == 1) {
                    return 8;
                }
                i -= 2;
            }
            int i5 = this.folderId;
            if (i5 == 0 && this.dialogsCount > 10 && i == this.currentCount - 2 && this.dialogsType == 0) {
                return 11;
            }
            int size = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, i5, this.dialogsListFrozen).size();
            if (i != size) {
                return i > size ? 10 : 0;
            }
            if (this.forceShowEmptyCell || (i2 = this.dialogsType) == 7 || i2 == 8 || MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
                return size == 0 ? 5 : 10;
            }
            return 1;
        }
    }

    public void notifyItemMoved(int fromPosition, int toPosition) {
        char c = 0;
        ArrayList<TLRPC.Dialog> dialogs = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
        int fromIndex = fixPosition(fromPosition);
        int toIndex = fixPosition(toPosition);
        TLRPC.Dialog fromDialog = dialogs.get(fromIndex);
        TLRPC.Dialog toDialog = dialogs.get(toIndex);
        int i = this.dialogsType;
        if (i == 7 || i == 8) {
            MessagesController.DialogFilter[] dialogFilterArr = MessagesController.getInstance(this.currentAccount).selectedDialogFilter;
            if (this.dialogsType == 8) {
                c = 1;
            }
            MessagesController.DialogFilter filter = dialogFilterArr[c];
            int idx1 = filter.pinnedDialogs.get(fromDialog.id);
            filter.pinnedDialogs.put(fromDialog.id, filter.pinnedDialogs.get(toDialog.id));
            filter.pinnedDialogs.put(toDialog.id, idx1);
        } else {
            int oldNum = fromDialog.pinnedNum;
            fromDialog.pinnedNum = toDialog.pinnedNum;
            toDialog.pinnedNum = oldNum;
        }
        Collections.swap(dialogs, fromIndex, toIndex);
        super.notifyItemMoved(fromPosition, toPosition);
    }

    public void setArchivedPullDrawable(PullForegroundDrawable drawable) {
        this.pullForegroundDrawable = drawable;
    }

    public void didDatabaseCleared() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.clear();
        }
    }

    public void resume() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.resume();
        }
    }

    public void pause() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.pause();
        }
    }

    public static class DialogsPreloader {
        private final int MAX_NETWORK_REQUEST_COUNT = 6;
        private final int MAX_REQUEST_COUNT = 4;
        private final int NETWORK_REQUESTS_RESET_TIME = 60000;
        Runnable clearNetworkRequestCount = new DialogsAdapter$DialogsPreloader$$ExternalSyntheticLambda0(this);
        int currentRequestCount;
        HashSet<Long> dialogsReadyMap = new HashSet<>();
        HashSet<Long> loadingDialogs = new HashSet<>();
        int networkRequestCount;
        ArrayList<Long> preloadDialogsPool = new ArrayList<>();
        HashSet<Long> preloadedErrorMap = new HashSet<>();
        boolean resumed;

        /* renamed from: lambda$new$0$org-telegram-ui-Adapters-DialogsAdapter$DialogsPreloader  reason: not valid java name */
        public /* synthetic */ void m1352x8b23716d() {
            this.networkRequestCount = 0;
            start();
        }

        public void add(long dialog_id) {
            if (!isReady(dialog_id) && !this.preloadedErrorMap.contains(Long.valueOf(dialog_id)) && !this.loadingDialogs.contains(Long.valueOf(dialog_id)) && !this.preloadDialogsPool.contains(Long.valueOf(dialog_id))) {
                this.preloadDialogsPool.add(Long.valueOf(dialog_id));
                start();
            }
        }

        /* access modifiers changed from: private */
        public void start() {
            if (preloadIsAvilable() && this.resumed && !this.preloadDialogsPool.isEmpty() && this.currentRequestCount < 4 && this.networkRequestCount <= 6) {
                final long dialog_id = this.preloadDialogsPool.remove(0).longValue();
                this.currentRequestCount++;
                this.loadingDialogs.add(Long.valueOf(dialog_id));
                MessagesController.getInstance(UserConfig.selectedAccount).ensureMessagesLoaded(dialog_id, 0, new MessagesController.MessagesLoadedCallback() {
                    public void onMessagesLoaded(boolean fromCache) {
                        AndroidUtilities.runOnUIThread(new DialogsAdapter$DialogsPreloader$1$$ExternalSyntheticLambda1(this, fromCache, dialog_id));
                    }

                    /* renamed from: lambda$onMessagesLoaded$0$org-telegram-ui-Adapters-DialogsAdapter$DialogsPreloader$1  reason: not valid java name */
                    public /* synthetic */ void m1354xCLASSNAMEb96d0(boolean fromCache, long dialog_id) {
                        if (!fromCache) {
                            DialogsPreloader.this.networkRequestCount++;
                            if (DialogsPreloader.this.networkRequestCount >= 6) {
                                AndroidUtilities.cancelRunOnUIThread(DialogsPreloader.this.clearNetworkRequestCount);
                                AndroidUtilities.runOnUIThread(DialogsPreloader.this.clearNetworkRequestCount, 60000);
                            }
                        }
                        if (DialogsPreloader.this.loadingDialogs.remove(Long.valueOf(dialog_id))) {
                            DialogsPreloader.this.dialogsReadyMap.add(Long.valueOf(dialog_id));
                            DialogsPreloader.this.updateList();
                            DialogsPreloader dialogsPreloader = DialogsPreloader.this;
                            dialogsPreloader.currentRequestCount--;
                            DialogsPreloader.this.start();
                        }
                    }

                    public void onError() {
                        AndroidUtilities.runOnUIThread(new DialogsAdapter$DialogsPreloader$1$$ExternalSyntheticLambda0(this, dialog_id));
                    }

                    /* renamed from: lambda$onError$1$org-telegram-ui-Adapters-DialogsAdapter$DialogsPreloader$1  reason: not valid java name */
                    public /* synthetic */ void m1353x11937a70(long dialog_id) {
                        if (DialogsPreloader.this.loadingDialogs.remove(Long.valueOf(dialog_id))) {
                            DialogsPreloader.this.preloadedErrorMap.add(Long.valueOf(dialog_id));
                            DialogsPreloader dialogsPreloader = DialogsPreloader.this;
                            dialogsPreloader.currentRequestCount--;
                            DialogsPreloader.this.start();
                        }
                    }
                });
            }
        }

        private boolean preloadIsAvilable() {
            return false;
        }

        public void updateList() {
        }

        public boolean isReady(long currentDialogId) {
            return this.dialogsReadyMap.contains(Long.valueOf(currentDialogId));
        }

        public boolean preloadedError(long currendDialogId) {
            return this.preloadedErrorMap.contains(Long.valueOf(currendDialogId));
        }

        public void remove(long currentDialogId) {
            this.preloadDialogsPool.remove(Long.valueOf(currentDialogId));
        }

        public void clear() {
            this.dialogsReadyMap.clear();
            this.preloadedErrorMap.clear();
            this.loadingDialogs.clear();
            this.preloadDialogsPool.clear();
            this.currentRequestCount = 0;
            this.networkRequestCount = 0;
            AndroidUtilities.cancelRunOnUIThread(this.clearNetworkRequestCount);
            updateList();
        }

        public void resume() {
            this.resumed = true;
            start();
        }

        public void pause() {
            this.resumed = false;
        }
    }

    public int getCurrentCount() {
        return this.currentCount;
    }

    public void setForceShowEmptyCell(boolean forceShowEmptyCell2) {
        this.forceShowEmptyCell = forceShowEmptyCell2;
    }

    public class LastEmptyView extends View {
        public boolean moving;

        public LastEmptyView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int size = DialogsAdapter.this.parentFragment.getDialogsArray(DialogsAdapter.this.currentAccount, DialogsAdapter.this.dialogsType, DialogsAdapter.this.folderId, DialogsAdapter.this.dialogsListFrozen).size();
            int archiveHeight = 0;
            boolean hasArchive = DialogsAdapter.this.dialogsType == 0 && MessagesController.getInstance(DialogsAdapter.this.currentAccount).dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
            View parent = (View) getParent();
            int paddingTop = parent.getPaddingTop();
            if (size == 0 || (paddingTop == 0 && !hasArchive)) {
                height = 0;
            } else {
                int height2 = View.MeasureSpec.getSize(heightMeasureSpec);
                if (height2 == 0) {
                    height2 = parent.getMeasuredHeight();
                }
                if (height2 == 0) {
                    height2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                }
                int cellHeight = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
                int dialogsHeight = (size * cellHeight) + (size - 1);
                if (DialogsAdapter.this.onlineContacts != null) {
                    dialogsHeight += (DialogsAdapter.this.onlineContacts.size() * AndroidUtilities.dp(58.0f)) + (DialogsAdapter.this.onlineContacts.size() - 1) + AndroidUtilities.dp(52.0f);
                }
                if (hasArchive) {
                    archiveHeight = cellHeight + 1;
                }
                if (dialogsHeight < height2) {
                    height = (height2 - dialogsHeight) + archiveHeight;
                    if (paddingTop != 0 && (height = height - AndroidUtilities.statusBarHeight) < 0) {
                        height = 0;
                    }
                } else if (dialogsHeight - height2 < archiveHeight) {
                    height = archiveHeight - (dialogsHeight - height2);
                    if (paddingTop != 0) {
                        height -= AndroidUtilities.statusBarHeight;
                    }
                    if (height < 0) {
                        height = 0;
                    }
                } else {
                    height = 0;
                }
            }
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), height);
        }
    }
}

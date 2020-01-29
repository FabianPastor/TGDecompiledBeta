package org.telegram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.StickersActivity;

public class StickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int archivedInfoRow;
    /* access modifiers changed from: private */
    public int archivedRow;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int featuredInfoRow;
    /* access modifiers changed from: private */
    public int featuredRow;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int loopRow;
    /* access modifiers changed from: private */
    public int masksInfoRow;
    /* access modifiers changed from: private */
    public int masksRow;
    /* access modifiers changed from: private */
    public boolean needReorder;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersShadowRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;
    /* access modifiers changed from: private */
    public int suggestInfoRow;
    /* access modifiers changed from: private */
    public int suggestRow;

    static /* synthetic */ void lambda$sendReorder$2(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 0) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            StickersActivity.this.listAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                StickersActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public StickersActivity(int i) {
        this.currentType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(this.currentType);
        if (this.currentType == 0) {
            MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        sendReorder();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("StickersName", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Masks", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    StickersActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        this.listView.setFocusable(true);
        this.listView.setTag(7);
        this.layoutManager = new LinearLayoutManager(context);
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                StickersActivity.this.lambda$createView$1$StickersActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$StickersActivity(View view, int i) {
        if (i >= this.stickersStartRow && i < this.stickersEndRow && getParentActivity() != null) {
            sendReorder();
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType).get(i - this.stickersStartRow);
            ArrayList<TLRPC.Document> arrayList = tL_messages_stickerSet.documents;
            if (arrayList != null && !arrayList.isEmpty()) {
                showDialog(new StickersAlert(getParentActivity(), this, (TLRPC.InputStickerSet) null, tL_messages_stickerSet, (StickersAlert.StickersAlertDelegate) null));
            }
        } else if (i == this.featuredRow) {
            sendReorder();
            presentFragment(new FeaturedStickersActivity());
        } else if (i == this.archivedRow) {
            sendReorder();
            presentFragment(new ArchivedStickersActivity(this.currentType));
        } else if (i == this.masksRow) {
            presentFragment(new StickersActivity(1));
        } else if (i == this.suggestRow) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("SuggestStickers", NUM));
                String[] strArr = {LocaleController.getString("SuggestStickersAll", NUM), LocaleController.getString("SuggestStickersInstalled", NUM), LocaleController.getString("SuggestStickersNone", NUM)};
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                builder.setView(linearLayout);
                int i2 = 0;
                while (i2 < strArr.length) {
                    RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                    radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                    radioColorCell.setTag(Integer.valueOf(i2));
                    radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
                    radioColorCell.setTextAndValue(strArr[i2], SharedConfig.suggestStickers == i2);
                    linearLayout.addView(radioColorCell);
                    radioColorCell.setOnClickListener(new View.OnClickListener(builder) {
                        private final /* synthetic */ AlertDialog.Builder f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(View view) {
                            StickersActivity.this.lambda$null$0$StickersActivity(this.f$1, view);
                        }
                    });
                    i2++;
                }
                showDialog(builder.create());
            }
        } else if (i == this.loopRow) {
            SharedConfig.toggleLoopStickers();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.loopStickers);
            }
        }
    }

    public /* synthetic */ void lambda$null$0$StickersActivity(AlertDialog.Builder builder, View view) {
        SharedConfig.setSuggestStickers(((Integer) view.getTag()).intValue());
        this.listAdapter.notifyItemChanged(this.suggestRow);
        builder.getDismissRunnable().run();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() == this.currentType) {
                updateRows();
            }
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyItemChanged(0);
            }
        } else if (i == NotificationCenter.archivedStickersCountDidLoad && objArr[0].intValue() == this.currentType) {
            updateRows();
        }
    }

    /* access modifiers changed from: private */
    public void sendReorder() {
        if (this.needReorder) {
            MediaDataController.getInstance(this.currentAccount).calcNewHash(this.currentType);
            this.needReorder = false;
            TLRPC.TL_messages_reorderStickerSets tL_messages_reorderStickerSets = new TLRPC.TL_messages_reorderStickerSets();
            tL_messages_reorderStickerSets.masks = this.currentType == 1;
            ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int i = 0; i < stickerSets.size(); i++) {
                tL_messages_reorderStickerSets.order.add(Long.valueOf(stickerSets.get(i).set.id));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reorderStickerSets, $$Lambda$StickersActivity$eNJ1JVvMbECvyP_jXUZlvL1o67I.INSTANCE);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(this.currentType));
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        this.suggestInfoRow = -1;
        if (this.currentType == 0) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.suggestRow = i;
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.loopRow = i2;
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.featuredRow = i3;
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.featuredInfoRow = i4;
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.masksRow = i5;
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.masksInfoRow = i6;
        } else {
            this.featuredRow = -1;
            this.featuredInfoRow = -1;
            this.masksRow = -1;
            this.masksInfoRow = -1;
            this.loopRow = -1;
        }
        if (MediaDataController.getInstance(this.currentAccount).getArchivedStickersCount(this.currentType) != 0) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.archivedRow = i7;
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.archivedInfoRow = i8;
        } else {
            this.archivedRow = -1;
            this.archivedInfoRow = -1;
        }
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
        if (!stickerSets.isEmpty()) {
            int i9 = this.rowCount;
            this.stickersStartRow = i9;
            this.stickersEndRow = i9 + stickerSets.size();
            this.rowCount += stickerSets.size();
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.stickersShadowRow = i10;
        } else {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return StickersActivity.this.rowCount;
        }

        public long getItemId(int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return MediaDataController.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType).get(i - StickersActivity.this.stickersStartRow).set.id;
            }
            if (i == StickersActivity.this.suggestRow || i == StickersActivity.this.suggestInfoRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.featuredRow || i == StickersActivity.this.featuredInfoRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.masksInfoRow) {
                return -2147483648L;
            }
            return (long) i;
        }

        private void processSelectionOption(int i, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            if (i == 0) {
                MediaDataController instance = MediaDataController.getInstance(StickersActivity.this.currentAccount);
                Activity parentActivity = StickersActivity.this.getParentActivity();
                TLRPC.StickerSet stickerSet = tL_messages_stickerSet.set;
                instance.removeStickersSet(parentActivity, stickerSet, !stickerSet.archived ? 1 : 2, StickersActivity.this, true);
            } else if (i == 1) {
                MediaDataController.getInstance(StickersActivity.this.currentAccount).removeStickersSet(StickersActivity.this.getParentActivity(), tL_messages_stickerSet.set, 0, StickersActivity.this, true);
            } else if (i == 2) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    Locale locale = Locale.US;
                    intent.putExtra("android.intent.extra.TEXT", String.format(locale, "https://" + MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix + "/addstickers/%s", new Object[]{tL_messages_stickerSet.set.short_name}));
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", NUM)), 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (i == 3) {
                try {
                    Locale locale2 = Locale.US;
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(locale2, "https://" + MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix + "/addstickers/%s", new Object[]{tL_messages_stickerSet.set.short_name})));
                    Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            String str2;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType);
                int access$300 = i - StickersActivity.this.stickersStartRow;
                StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSets.get(access$300);
                if (access$300 != stickerSets.size() - 1) {
                    z = true;
                }
                stickerSetCell.setStickersSet(tL_messages_stickerSet, z);
            } else if (itemViewType != 1) {
                if (itemViewType != 2) {
                    if (itemViewType != 3) {
                        if (itemViewType == 4 && i == StickersActivity.this.loopRow) {
                            ((TextCheckCell) viewHolder.itemView).setTextAndCheck(LocaleController.getString("LoopAnimatedStickers", NUM), SharedConfig.loopStickers, true);
                        }
                    } else if (i == StickersActivity.this.stickersShadowRow) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    } else if (i == StickersActivity.this.suggestInfoRow) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    }
                } else if (i == StickersActivity.this.featuredRow) {
                    int size = MediaDataController.getInstance(StickersActivity.this.currentAccount).getUnreadStickerSets().size();
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    String string = LocaleController.getString("FeaturedStickers", NUM);
                    if (size != 0) {
                        str2 = String.format("%d", new Object[]{Integer.valueOf(size)});
                    } else {
                        str2 = "";
                    }
                    textSettingsCell.setTextAndValue(string, str2, false);
                } else if (i == StickersActivity.this.archivedRow) {
                    if (StickersActivity.this.currentType == 0) {
                        ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedStickers", NUM), false);
                    } else {
                        ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedMasks", NUM), false);
                    }
                } else if (i == StickersActivity.this.masksRow) {
                    ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("Masks", NUM), false);
                } else if (i == StickersActivity.this.suggestRow) {
                    int i2 = SharedConfig.suggestStickers;
                    if (i2 == 0) {
                        str = LocaleController.getString("SuggestStickersAll", NUM);
                    } else if (i2 != 1) {
                        str = LocaleController.getString("SuggestStickersNone", NUM);
                    } else {
                        str = LocaleController.getString("SuggestStickersInstalled", NUM);
                    }
                    ((TextSettingsCell) viewHolder.itemView).setTextAndValue(LocaleController.getString("SuggestStickers", NUM), str, true);
                }
            } else if (i == StickersActivity.this.featuredInfoRow) {
                String string2 = LocaleController.getString("FeaturedStickersInfo", NUM);
                int indexOf = string2.indexOf("@stickers");
                if (indexOf != -1) {
                    try {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string2);
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                            public void onClick(View view) {
                                MessagesController.getInstance(StickersActivity.this.currentAccount).openByUserName("stickers", StickersActivity.this, 3);
                            }
                        }, indexOf, indexOf + 9, 18);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(spannableStringBuilder);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(string2);
                    }
                } else {
                    ((TextInfoPrivacyCell) viewHolder.itemView).setText(string2);
                }
            } else if (i == StickersActivity.this.archivedInfoRow) {
                if (StickersActivity.this.currentType == 0) {
                    ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedStickersInfo", NUM));
                } else {
                    ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedMasksInfo", NUM));
                }
            } else if (i == StickersActivity.this.masksInfoRow) {
                ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("MasksInfo", NUM));
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2 || itemViewType == 4;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1$StickersActivity$ListAdapter(View view) {
            CharSequence[] charSequenceArr;
            int[] iArr;
            CharSequence[] charSequenceArr2;
            StickersActivity.this.sendReorder();
            TLRPC.TL_messages_stickerSet stickersSet = ((StickerSetCell) view.getParent()).getStickersSet();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) StickersActivity.this.getParentActivity());
            builder.setTitle(stickersSet.set.title);
            if (StickersActivity.this.currentType == 0) {
                if (stickersSet.set.official) {
                    iArr = new int[]{0};
                    charSequenceArr2 = new CharSequence[]{LocaleController.getString("StickersHide", NUM)};
                } else {
                    iArr = new int[]{0, 1, 2, 3};
                    charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", NUM), LocaleController.getString("StickersRemove", NUM), LocaleController.getString("StickersShare", NUM), LocaleController.getString("StickersCopy", NUM)};
                    builder.setItems(charSequenceArr, new DialogInterface.OnClickListener(iArr, stickersSet) {
                        private final /* synthetic */ int[] f$1;
                        private final /* synthetic */ TLRPC.TL_messages_stickerSet f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            StickersActivity.ListAdapter.this.lambda$null$0$StickersActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    StickersActivity.this.showDialog(builder.create());
                }
            } else if (stickersSet.set.official) {
                iArr = new int[]{0};
                charSequenceArr2 = new CharSequence[]{LocaleController.getString("StickersRemove", NUM)};
            } else {
                iArr = new int[]{0, 1, 2, 3};
                charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", NUM), LocaleController.getString("StickersRemove", NUM), LocaleController.getString("StickersShare", NUM), LocaleController.getString("StickersCopy", NUM)};
                builder.setItems(charSequenceArr, new DialogInterface.OnClickListener(iArr, stickersSet) {
                    private final /* synthetic */ int[] f$1;
                    private final /* synthetic */ TLRPC.TL_messages_stickerSet f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        StickersActivity.ListAdapter.this.lambda$null$0$StickersActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
                    }
                });
                StickersActivity.this.showDialog(builder.create());
            }
            charSequenceArr = charSequenceArr2;
            builder.setItems(charSequenceArr, new DialogInterface.OnClickListener(iArr, stickersSet) {
                private final /* synthetic */ int[] f$1;
                private final /* synthetic */ TLRPC.TL_messages_stickerSet f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    StickersActivity.ListAdapter.this.lambda$null$0$StickersActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            StickersActivity.this.showDialog(builder.create());
        }

        public /* synthetic */ void lambda$null$0$StickersActivity$ListAdapter(int[] iArr, TLRPC.TL_messages_stickerSet tL_messages_stickerSet, DialogInterface dialogInterface, int i) {
            processSelectionOption(iArr[i], tL_messages_stickerSet);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextCheckCell textCheckCell;
            if (i == 0) {
                StickerSetCell stickerSetCell = new StickerSetCell(this.mContext, 1);
                stickerSetCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                stickerSetCell.setOnOptionsClick(new View.OnClickListener() {
                    public final void onClick(View view) {
                        StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$1$StickersActivity$ListAdapter(view);
                    }
                });
                textCheckCell = stickerSetCell;
            } else if (i == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                textCheckCell = textInfoPrivacyCell;
            } else if (i == 2) {
                TextSettingsCell textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textCheckCell = textSettingsCell;
            } else if (i == 3) {
                textCheckCell = new ShadowSectionCell(this.mContext);
            } else if (i != 4) {
                textCheckCell = null;
            } else {
                TextCheckCell textCheckCell2 = new TextCheckCell(this.mContext);
                textCheckCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textCheckCell = textCheckCell2;
            }
            textCheckCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(textCheckCell);
        }

        public int getItemViewType(int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == StickersActivity.this.featuredInfoRow || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.masksInfoRow) {
                return 1;
            }
            if (i == StickersActivity.this.featuredRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.suggestRow) {
                return 2;
            }
            if (i == StickersActivity.this.stickersShadowRow || i == StickersActivity.this.suggestInfoRow) {
                return 3;
            }
            if (i == StickersActivity.this.loopRow) {
                return 4;
            }
            return 0;
        }

        public void swapElements(int i, int i2) {
            if (i != i2) {
                boolean unused = StickersActivity.this.needReorder = true;
            }
            ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType);
            stickerSets.set(i - StickersActivity.this.stickersStartRow, stickerSets.get(i2 - StickersActivity.this.stickersStartRow));
            stickerSets.set(i2 - StickersActivity.this.stickersStartRow, stickerSets.get(i - StickersActivity.this.stickersStartRow));
            notifyItemMoved(i, i2);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class, TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"), new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu")};
    }
}

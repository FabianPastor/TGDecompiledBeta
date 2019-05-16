package org.telegram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_reorderStickerSets;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class StickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private int archivedInfoRow;
    private int archivedRow;
    private int currentType;
    private int featuredInfoRow;
    private int featuredRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int masksInfoRow;
    private int masksRow;
    private boolean needReorder;
    private int rowCount;
    private int stickersEndRow;
    private int stickersShadowRow;
    private int stickersStartRow;
    private int suggestInfoRow;
    private int suggestRow;

    public class TouchHelperCallback extends Callback {
        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 0) {
                return Callback.makeMovementFlags(0, 0);
            }
            return Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            StickersActivity.this.listAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            if (i != 0) {
                StickersActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return StickersActivity.this.rowCount;
        }

        public long getItemId(int i) {
            if (i < StickersActivity.this.stickersStartRow || i >= StickersActivity.this.stickersEndRow) {
                return (i == StickersActivity.this.suggestRow || i == StickersActivity.this.suggestInfoRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.featuredRow || i == StickersActivity.this.featuredInfoRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.masksInfoRow) ? -2147483648L : (long) i;
            } else {
                return ((TL_messages_stickerSet) DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType).get(i - StickersActivity.this.stickersStartRow)).set.id;
            }
        }

        private void processSelectionOption(int i, TL_messages_stickerSet tL_messages_stickerSet) {
            if (i == 0) {
                DataQuery instance = DataQuery.getInstance(StickersActivity.this.currentAccount);
                Activity parentActivity = StickersActivity.this.getParentActivity();
                StickerSet stickerSet = tL_messages_stickerSet.set;
                instance.removeStickersSet(parentActivity, stickerSet, !stickerSet.archived ? 1 : 2, StickersActivity.this, true);
            } else if (i == 1) {
                DataQuery.getInstance(StickersActivity.this.currentAccount).removeStickersSet(StickersActivity.this.getParentActivity(), tL_messages_stickerSet.set, 0, StickersActivity.this, true);
            } else {
                String str = "/addstickers/%s";
                String str2 = "https://";
                Locale locale;
                StringBuilder stringBuilder;
                if (i == 2) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        locale = Locale.US;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str2);
                        stringBuilder.append(MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix);
                        stringBuilder.append(str);
                        intent.putExtra("android.intent.extra.TEXT", String.format(locale, stringBuilder.toString(), new Object[]{tL_messages_stickerSet.set.short_name}));
                        StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", NUM)), 500);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (i == 3) {
                    try {
                        ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                        locale = Locale.US;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str2);
                        stringBuilder.append(MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix);
                        stringBuilder.append(str);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("label", String.format(locale, stringBuilder.toString(), new Object[]{tL_messages_stickerSet.set.short_name})));
                        Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            String string;
            String format;
            if (itemViewType == 0) {
                ArrayList stickerSets = DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType);
                i -= StickersActivity.this.stickersStartRow;
                StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i);
                if (i != stickerSets.size() - 1) {
                    z = true;
                }
                stickerSetCell.setStickersSet(tL_messages_stickerSet, z);
            } else if (itemViewType != 1) {
                if (itemViewType != 2) {
                    if (itemViewType == 3) {
                        String str = "windowBackgroundGrayShadow";
                        if (i == StickersActivity.this.stickersShadowRow) {
                            viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                        } else if (i == StickersActivity.this.suggestInfoRow) {
                            viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                        }
                    }
                } else if (i == StickersActivity.this.featuredRow) {
                    i = DataQuery.getInstance(StickersActivity.this.currentAccount).getUnreadStickerSets().size();
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    string = LocaleController.getString("FeaturedStickers", NUM);
                    if (i != 0) {
                        format = String.format("%d", new Object[]{Integer.valueOf(i)});
                    } else {
                        format = "";
                    }
                    textSettingsCell.setTextAndValue(string, format, false);
                } else if (i == StickersActivity.this.archivedRow) {
                    if (StickersActivity.this.currentType == 0) {
                        ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedStickers", NUM), false);
                    } else {
                        ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedMasks", NUM), false);
                    }
                } else if (i == StickersActivity.this.masksRow) {
                    ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("Masks", NUM), false);
                } else if (i == StickersActivity.this.suggestRow) {
                    i = SharedConfig.suggestStickers;
                    if (i == 0) {
                        format = LocaleController.getString("SuggestStickersAll", NUM);
                    } else if (i != 1) {
                        format = LocaleController.getString("SuggestStickersNone", NUM);
                    } else {
                        format = LocaleController.getString("SuggestStickersInstalled", NUM);
                    }
                    ((TextSettingsCell) viewHolder.itemView).setTextAndValue(LocaleController.getString("SuggestStickers", NUM), format, true);
                }
            } else if (i == StickersActivity.this.featuredInfoRow) {
                format = LocaleController.getString("FeaturedStickersInfo", NUM);
                string = "@stickers";
                int indexOf = format.indexOf(string);
                if (indexOf != -1) {
                    try {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(format);
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline(string) {
                            public void onClick(View view) {
                                MessagesController.getInstance(StickersActivity.this.currentAccount).openByUserName("stickers", StickersActivity.this, 1);
                            }
                        }, indexOf, indexOf + 9, 18);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(spannableStringBuilder);
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(format);
                        return;
                    }
                }
                ((TextInfoPrivacyCell) viewHolder.itemView).setText(format);
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

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1$StickersActivity$ListAdapter(View view) {
            int[] iArr;
            CharSequence[] charSequenceArr;
            CharSequence[] charSequenceArr2;
            StickersActivity.this.sendReorder();
            TL_messages_stickerSet stickersSet = ((StickerSetCell) view.getParent()).getStickersSet();
            Builder builder = new Builder(StickersActivity.this.getParentActivity());
            builder.setTitle(stickersSet.set.title);
            String str = "StickersCopy";
            String str2 = "StickersShare";
            String str3 = "StickersRemove";
            String str4 = "StickersHide";
            if (StickersActivity.this.currentType == 0) {
                if (stickersSet.set.official) {
                    iArr = new int[]{0};
                    charSequenceArr = new CharSequence[]{LocaleController.getString(str4, NUM)};
                } else {
                    iArr = new int[]{0, 1, 2, 3};
                    charSequenceArr2 = new CharSequence[]{LocaleController.getString(str4, NUM), LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM)};
                    builder.setItems(charSequenceArr2, new -$$Lambda$StickersActivity$ListAdapter$4oHDK9bhePlEa0rCLASSNAMEWtPkp-42Q(this, iArr, stickersSet));
                    StickersActivity.this.showDialog(builder.create());
                }
            } else if (stickersSet.set.official) {
                iArr = new int[]{0};
                charSequenceArr = new CharSequence[]{LocaleController.getString(str3, NUM)};
            } else {
                iArr = new int[]{0, 1, 2, 3};
                charSequenceArr2 = new CharSequence[]{LocaleController.getString(str4, NUM), LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM)};
                builder.setItems(charSequenceArr2, new -$$Lambda$StickersActivity$ListAdapter$4oHDK9bhePlEa0rCLASSNAMEWtPkp-42Q(this, iArr, stickersSet));
                StickersActivity.this.showDialog(builder.create());
            }
            charSequenceArr2 = charSequenceArr;
            builder.setItems(charSequenceArr2, new -$$Lambda$StickersActivity$ListAdapter$4oHDK9bhePlEa0rCLASSNAMEWtPkp-42Q(this, iArr, stickersSet));
            StickersActivity.this.showDialog(builder.create());
        }

        public /* synthetic */ void lambda$null$0$StickersActivity$ListAdapter(int[] iArr, TL_messages_stickerSet tL_messages_stickerSet, DialogInterface dialogInterface, int i) {
            processSelectionOption(iArr[i], tL_messages_stickerSet);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textInfoPrivacyCell;
            View textSettingsCell;
            String str = "windowBackgroundWhite";
            if (i != 0) {
                if (i == 1) {
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i != 2) {
                    textInfoPrivacyCell = i != 3 ? null : new ShadowSectionCell(this.mContext);
                } else {
                    textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                }
                textInfoPrivacyCell.setLayoutParams(new LayoutParams(-1, -2));
                return new Holder(textInfoPrivacyCell);
            }
            textSettingsCell = new StickerSetCell(this.mContext, 1);
            textSettingsCell.setBackgroundColor(Theme.getColor(str));
            textSettingsCell.setOnOptionsClick(new -$$Lambda$StickersActivity$ListAdapter$xPbh5swVjrfEVlhBcsKoz2sSeSI(this));
            textInfoPrivacyCell = textSettingsCell;
            textInfoPrivacyCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(textInfoPrivacyCell);
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
            return 0;
        }

        public void swapElements(int i, int i2) {
            if (i != i2) {
                StickersActivity.this.needReorder = true;
            }
            ArrayList stickerSets = DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType);
            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i - StickersActivity.this.stickersStartRow);
            stickerSets.set(i - StickersActivity.this.stickersStartRow, stickerSets.get(i2 - StickersActivity.this.stickersStartRow));
            stickerSets.set(i2 - StickersActivity.this.stickersStartRow, tL_messages_stickerSet);
            notifyItemMoved(i, i2);
        }
    }

    static /* synthetic */ void lambda$sendReorder$2(TLObject tLObject, TL_error tL_error) {
    }

    public StickersActivity(int i) {
        this.currentType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DataQuery.getInstance(this.currentAccount).checkStickers(this.currentType);
        if (this.currentType == 0) {
            DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
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
        this.listView.setTag(Integer.valueOf(7));
        this.layoutManager = new LinearLayoutManager(context);
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$StickersActivity$52bQnWSJW3OnfBam1s3y37TWiNA(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$StickersActivity(View view, int i) {
        if (i >= this.stickersStartRow && i < this.stickersEndRow && getParentActivity() != null) {
            sendReorder();
            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) DataQuery.getInstance(this.currentAccount).getStickerSets(this.currentType).get(i - this.stickersStartRow);
            ArrayList arrayList = tL_messages_stickerSet.documents;
            if (arrayList != null && !arrayList.isEmpty()) {
                showDialog(new StickersAlert(getParentActivity(), this, null, tL_messages_stickerSet, null));
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
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("SuggestStickers", NUM));
            builder.setItems(new CharSequence[]{LocaleController.getString("SuggestStickersAll", NUM), LocaleController.getString("SuggestStickersInstalled", NUM), LocaleController.getString("SuggestStickersNone", NUM)}, new -$$Lambda$StickersActivity$43apBJTj1nQwZFS8l5r8_cQqass(this));
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$null$0$StickersActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.setSuggestStickers(i);
        this.listAdapter.notifyItemChanged(this.suggestRow);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            if (((Integer) objArr[0]).intValue() == this.currentType) {
                updateRows();
            }
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(0);
            }
        } else if (i == NotificationCenter.archivedStickersCountDidLoad && ((Integer) objArr[0]).intValue() == this.currentType) {
            updateRows();
        }
    }

    private void sendReorder() {
        if (this.needReorder) {
            DataQuery.getInstance(this.currentAccount).calcNewHash(this.currentType);
            this.needReorder = false;
            TL_messages_reorderStickerSets tL_messages_reorderStickerSets = new TL_messages_reorderStickerSets();
            tL_messages_reorderStickerSets.masks = this.currentType == 1;
            ArrayList stickerSets = DataQuery.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int i = 0; i < stickerSets.size(); i++) {
                tL_messages_reorderStickerSets.order.add(Long.valueOf(((TL_messages_stickerSet) stickerSets.get(i)).set.id));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reorderStickerSets, -$$Lambda$StickersActivity$eNJ1JVvMbECvyP_jXUZlvL1o67I.INSTANCE);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(this.currentType));
        }
    }

    private void updateRows() {
        int i;
        this.rowCount = 0;
        if (this.currentType == 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.suggestRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.featuredRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.featuredInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.masksRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.masksInfoRow = i;
        } else {
            this.featuredRow = -1;
            this.featuredInfoRow = -1;
            this.masksRow = -1;
            this.masksInfoRow = -1;
        }
        if (DataQuery.getInstance(this.currentAccount).getArchivedStickersCount(this.currentType) != 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.archivedRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.archivedInfoRow = i;
        } else {
            this.archivedRow = -1;
            this.archivedInfoRow = -1;
        }
        ArrayList stickerSets = DataQuery.getInstance(this.currentAccount).getStickerSets(this.currentType);
        if (stickerSets.isEmpty()) {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        } else {
            int i2 = this.rowCount;
            this.stickersStartRow = i2;
            this.stickersEndRow = i2 + stickerSets.size();
            this.rowCount += stickerSets.size();
            i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersShadowRow = i;
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[19];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextInfoPrivacyCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteLinkText");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextSettingsCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[13] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        int i = ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE;
        clsArr = new Class[]{StickerSetCell.class};
        strArr = new String[1];
        strArr[0] = "optionsButton";
        themeDescriptionArr[17] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "stickers_menuSelector");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, null, null, null, "stickers_menu");
        return themeDescriptionArr;
    }
}

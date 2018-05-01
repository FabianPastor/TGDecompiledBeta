package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
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
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class StickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private int archivedInfoRow;
    private int archivedRow;
    private int currentType;
    private int featuredInfoRow;
    private int featuredRow;
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

    /* renamed from: org.telegram.ui.StickersActivity$1 */
    class C22921 extends ActionBarMenuOnItemClick {
        C22921() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                StickersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.StickersActivity$2 */
    class C22932 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.StickersActivity$2$1 */
        class C17091 implements OnClickListener {
            C17091() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SharedConfig.setSuggestStickers(i);
                StickersActivity.this.listAdapter.notifyItemChanged(StickersActivity.this.suggestRow);
            }
        }

        C22932() {
        }

        public void onItemClick(View view, int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow && StickersActivity.this.getParentActivity() != null) {
                StickersActivity.this.sendReorder();
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType).get(i - StickersActivity.this.stickersStartRow);
                view = tL_messages_stickerSet.documents;
                if (view != null) {
                    if (view.isEmpty() == null) {
                        StickersActivity.this.showDialog(new StickersAlert(StickersActivity.this.getParentActivity(), StickersActivity.this, null, tL_messages_stickerSet, null));
                    }
                }
            } else if (i == StickersActivity.this.featuredRow) {
                StickersActivity.this.sendReorder();
                StickersActivity.this.presentFragment(new FeaturedStickersActivity());
            } else if (i == StickersActivity.this.archivedRow) {
                StickersActivity.this.sendReorder();
                StickersActivity.this.presentFragment(new ArchivedStickersActivity(StickersActivity.this.currentType));
            } else if (i == StickersActivity.this.masksRow) {
                StickersActivity.this.presentFragment(new StickersActivity(1));
            } else if (i == StickersActivity.this.suggestRow) {
                view = new Builder(StickersActivity.this.getParentActivity());
                view.setTitle(LocaleController.getString("SuggestStickers", C0446R.string.SuggestStickers));
                view.setItems(new CharSequence[]{LocaleController.getString("SuggestStickersAll", C0446R.string.SuggestStickersAll), LocaleController.getString("SuggestStickersInstalled", C0446R.string.SuggestStickersInstalled), LocaleController.getString("SuggestStickersNone", C0446R.string.SuggestStickersNone)}, new C17091());
                StickersActivity.this.showDialog(view.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.StickersActivity$3 */
    class C22943 implements RequestDelegate {
        public void run(TLObject tLObject, TL_error tL_error) {
        }

        C22943() {
        }
    }

    public class TouchHelperCallback extends Callback {
        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onSwiped(ViewHolder viewHolder, int i) {
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != null) {
                return Callback.makeMovementFlags(0, 0);
            }
            return Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return null;
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
            viewHolder.itemView.setPressed(null);
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.StickersActivity$ListAdapter$2 */
        class C17112 implements View.OnClickListener {
            C17112() {
            }

            public void onClick(View view) {
                int[] iArr;
                CharSequence[] charSequenceArr;
                CharSequence[] charSequenceArr2;
                StickersActivity.this.sendReorder();
                view = ((StickerSetCell) view.getParent()).getStickersSet();
                Builder builder = new Builder(StickersActivity.this.getParentActivity());
                builder.setTitle(view.set.title);
                if (StickersActivity.this.currentType == 0) {
                    if (view.set.official) {
                        iArr = new int[]{0};
                        charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", C0446R.string.StickersHide)};
                        builder.setItems(charSequenceArr, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ListAdapter.this.processSelectionOption(iArr[i], view);
                            }
                        });
                        StickersActivity.this.showDialog(builder.create());
                    }
                    iArr = new int[]{0, 1, 2, 3};
                    charSequenceArr2 = new CharSequence[]{LocaleController.getString("StickersHide", C0446R.string.StickersHide), LocaleController.getString("StickersRemove", C0446R.string.StickersRemove), LocaleController.getString("StickersShare", C0446R.string.StickersShare), LocaleController.getString("StickersCopy", C0446R.string.StickersCopy)};
                } else if (view.set.official) {
                    iArr = new int[]{0};
                    charSequenceArr = new CharSequence[]{LocaleController.getString("StickersRemove", C0446R.string.StickersHide)};
                    builder.setItems(charSequenceArr, /* anonymous class already generated */);
                    StickersActivity.this.showDialog(builder.create());
                } else {
                    iArr = new int[]{0, 1, 2, 3};
                    charSequenceArr2 = new CharSequence[]{LocaleController.getString("StickersHide", C0446R.string.StickersHide), LocaleController.getString("StickersRemove", C0446R.string.StickersRemove), LocaleController.getString("StickersShare", C0446R.string.StickersShare), LocaleController.getString("StickersCopy", C0446R.string.StickersCopy)};
                }
                charSequenceArr = charSequenceArr2;
                builder.setItems(charSequenceArr, /* anonymous class already generated */);
                StickersActivity.this.showDialog(builder.create());
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return StickersActivity.this.rowCount;
        }

        public long getItemId(int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return ((TL_messages_stickerSet) DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType).get(i - StickersActivity.this.stickersStartRow)).set.id;
            }
            if (!(i == StickersActivity.this.suggestRow || i == StickersActivity.this.suggestInfoRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.featuredRow || i == StickersActivity.this.featuredInfoRow || i == StickersActivity.this.masksRow)) {
                if (i != StickersActivity.this.masksInfoRow) {
                    return (long) i;
                }
            }
            return -2147483648L;
        }

        private void processSelectionOption(int i, TL_messages_stickerSet tL_messages_stickerSet) {
            if (i == 0) {
                DataQuery.getInstance(StickersActivity.this.currentAccount).removeStickersSet(StickersActivity.this.getParentActivity(), tL_messages_stickerSet.set, tL_messages_stickerSet.set.archived == 0 ? 1 : 2, StickersActivity.this, true);
            } else if (i == 1) {
                DataQuery.getInstance(StickersActivity.this.currentAccount).removeStickersSet(StickersActivity.this.getParentActivity(), tL_messages_stickerSet.set, 0, StickersActivity.this, true);
            } else if (i == 2) {
                try {
                    i = new Intent("android.intent.action.SEND");
                    i.setType("text/plain");
                    r3 = Locale.US;
                    r4 = new StringBuilder();
                    r4.append("https://");
                    r4.append(MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix);
                    r4.append("/addstickers/%s");
                    i.putExtra("android.intent.extra.TEXT", String.format(r3, r4.toString(), new Object[]{tL_messages_stickerSet.set.short_name}));
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(i, LocaleController.getString("StickersShare", C0446R.string.StickersShare)), 500);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else if (i == 3) {
                try {
                    ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                    r3 = Locale.US;
                    r4 = new StringBuilder();
                    r4.append("https://");
                    r4.append(MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix);
                    r4.append("/addstickers/%s");
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("label", String.format(r3, r4.toString(), new Object[]{tL_messages_stickerSet.set.short_name})));
                    Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0446R.string.LinkCopied), 0).show();
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = true;
            String str;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ArrayList stickerSets = DataQuery.getInstance(StickersActivity.this.currentAccount).getStickerSets(StickersActivity.this.currentType);
                    i -= StickersActivity.this.stickersStartRow;
                    StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                    TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i);
                    if (i == stickerSets.size() - 1) {
                        z = false;
                    }
                    stickerSetCell.setStickersSet(tL_messages_stickerSet, z);
                    return;
                case 1:
                    if (i == StickersActivity.this.featuredInfoRow) {
                        i = LocaleController.getString("FeaturedStickersInfo", C0446R.string.FeaturedStickersInfo);
                        str = "@stickers";
                        int indexOf = i.indexOf(str);
                        if (indexOf != -1) {
                            try {
                                CharSequence spannableStringBuilder = new SpannableStringBuilder(i);
                                spannableStringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                                    public void onClick(View view) {
                                        MessagesController.getInstance(StickersActivity.this.currentAccount).openByUserName("stickers", StickersActivity.this, 1);
                                    }
                                }, indexOf, str.length() + indexOf, 18);
                                ((TextInfoPrivacyCell) viewHolder.itemView).setText(spannableStringBuilder);
                                return;
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                                ((TextInfoPrivacyCell) viewHolder.itemView).setText(i);
                                return;
                            }
                        }
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(i);
                        return;
                    } else if (i == StickersActivity.this.archivedInfoRow) {
                        if (StickersActivity.this.currentType == 0) {
                            ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedStickersInfo", C0446R.string.ArchivedStickersInfo));
                            return;
                        } else {
                            ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedMasksInfo", C0446R.string.ArchivedMasksInfo));
                            return;
                        }
                    } else if (i == StickersActivity.this.masksInfoRow) {
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("MasksInfo", C0446R.string.MasksInfo));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    if (i == StickersActivity.this.featuredRow) {
                        i = DataQuery.getInstance(StickersActivity.this.currentAccount).getUnreadStickerSets().size();
                        TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                        str = LocaleController.getString("FeaturedStickers", C0446R.string.FeaturedStickers);
                        if (i != 0) {
                            i = String.format("%d", new Object[]{Integer.valueOf(i)});
                        } else {
                            i = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        textSettingsCell.setTextAndValue(str, i, false);
                        return;
                    } else if (i == StickersActivity.this.archivedRow) {
                        if (StickersActivity.this.currentType == 0) {
                            ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedStickers", C0446R.string.ArchivedStickers), false);
                            return;
                        } else {
                            ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("ArchivedMasks", C0446R.string.ArchivedMasks), false);
                            return;
                        }
                    } else if (i == StickersActivity.this.masksRow) {
                        ((TextSettingsCell) viewHolder.itemView).setText(LocaleController.getString("Masks", C0446R.string.Masks), false);
                        return;
                    } else if (i == StickersActivity.this.suggestRow) {
                        switch (SharedConfig.suggestStickers) {
                            case 0:
                                i = LocaleController.getString("SuggestStickersAll", C0446R.string.SuggestStickersAll);
                                break;
                            case 1:
                                i = LocaleController.getString("SuggestStickersInstalled", C0446R.string.SuggestStickersInstalled);
                                break;
                            default:
                                i = LocaleController.getString("SuggestStickersNone", C0446R.string.SuggestStickersNone);
                                break;
                        }
                        ((TextSettingsCell) viewHolder.itemView).setTextAndValue(LocaleController.getString("SuggestStickers", C0446R.string.SuggestStickers), i, true);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (i == StickersActivity.this.stickersShadowRow) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == StickersActivity.this.suggestInfoRow) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getItemViewType();
            if (viewHolder != null) {
                if (viewHolder != 2) {
                    return null;
                }
            }
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new StickerSetCell(this.mContext, 1);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    ((StickerSetCell) viewGroup).setOnOptionsClick(new C17112());
                    break;
                case 1:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    viewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    viewGroup = new TextSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public int getItemViewType(int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (!(i == StickersActivity.this.featuredInfoRow || i == StickersActivity.this.archivedInfoRow)) {
                if (i != StickersActivity.this.masksInfoRow) {
                    if (!(i == StickersActivity.this.featuredRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.masksRow)) {
                        if (i != StickersActivity.this.suggestRow) {
                            if (i != StickersActivity.this.stickersShadowRow) {
                                if (i != StickersActivity.this.suggestInfoRow) {
                                    return 0;
                                }
                            }
                            return 3;
                        }
                    }
                    return 2;
                }
            }
            return 1;
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

    public StickersActivity(int i) {
        this.currentType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DataQuery.getInstance(this.currentAccount).checkStickers(this.currentType);
        if (this.currentType == 0) {
            DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.archivedStickersCountDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.archivedStickersCountDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        sendReorder();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("StickersName", C0446R.string.StickersName));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Masks", C0446R.string.Masks));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C22921());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setFocusable(true);
        this.listView.setTag(Integer.valueOf(7));
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(1);
        this.listView.setLayoutManager(linearLayoutManager);
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C22932());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoaded) {
            if (((Integer) objArr[0]).intValue() == this.currentType) {
                updateRows();
            }
        } else if (i == NotificationCenter.featuredStickersDidLoaded) {
            if (this.listAdapter != 0) {
                this.listAdapter.notifyItemChanged(0);
            }
        } else if (i == NotificationCenter.archivedStickersCountDidLoaded && ((Integer) objArr[0]).intValue() == this.currentType) {
            updateRows();
        }
    }

    private void sendReorder() {
        if (this.needReorder) {
            DataQuery.getInstance(this.currentAccount).calcNewHash(this.currentType);
            this.needReorder = false;
            TLObject tL_messages_reorderStickerSets = new TL_messages_reorderStickerSets();
            tL_messages_reorderStickerSets.masks = this.currentType == 1;
            ArrayList stickerSets = DataQuery.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int i = 0; i < stickerSets.size(); i++) {
                tL_messages_reorderStickerSets.order.add(Long.valueOf(((TL_messages_stickerSet) stickerSets.get(i)).set.id));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_reorderStickerSets, new C22943());
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(this.currentType));
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentType == 0) {
            int i = this.rowCount;
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
            this.stickersStartRow = this.rowCount;
            this.stickersEndRow = this.rowCount + stickerSets.size();
            this.rowCount += stickerSets.size();
            i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersShadowRow = i;
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[19];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, null, null, null, Theme.key_stickers_menuSelector);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, null, null, null, Theme.key_stickers_menu);
        return themeDescriptionArr;
    }
}

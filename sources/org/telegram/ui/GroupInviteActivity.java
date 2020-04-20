package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ExportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class GroupInviteActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int chat_id;
    /* access modifiers changed from: private */
    public int copyLinkRow;
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public TLRPC$ExportedChatInvite invite;
    /* access modifiers changed from: private */
    public int linkInfoRow;
    /* access modifiers changed from: private */
    public int linkRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    /* access modifiers changed from: private */
    public int revokeLinkRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int shadowRow;
    /* access modifiers changed from: private */
    public int shareLinkRow;

    public GroupInviteActivity(int i) {
        this.chat_id = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
        this.loading = true;
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.linkRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.linkInfoRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.copyLinkRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.revokeLinkRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.shareLinkRow = i4;
        this.rowCount = i5 + 1;
        this.shadowRow = i5;
        return true;
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLink", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupInviteActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                GroupInviteActivity.this.lambda$createView$1$GroupInviteActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$GroupInviteActivity(View view, int i) {
        if (getParentActivity() != null) {
            if (i == this.copyLinkRow || i == this.linkRow) {
                if (this.invite != null) {
                    try {
                        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                        Toast.makeText(getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } else if (i == this.shareLinkRow) {
                if (this.invite != null) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        intent.putExtra("android.intent.extra.TEXT", this.invite.link);
                        getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", NUM)), 500);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
            } else if (i == this.revokeLinkRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setMessage(LocaleController.getString("RevokeAlert", NUM));
                builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        GroupInviteActivity.this.lambda$null$0$GroupInviteActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        }
    }

    public /* synthetic */ void lambda$null$0$GroupInviteActivity(DialogInterface dialogInterface, int i) {
        generateLink(true);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            int intValue = objArr[1].intValue();
            if (objArr[0].id == this.chat_id && intValue == this.classGuid) {
                TLRPC$ExportedChatInvite exportedInvite = MessagesController.getInstance(this.currentAccount).getExportedInvite(this.chat_id);
                this.invite = exportedInvite;
                if (!(exportedInvite instanceof TLRPC$TL_chatInviteExported)) {
                    generateLink(false);
                    return;
                }
                this.loading = false;
                ListAdapter listAdapter2 = this.listAdapter;
                if (listAdapter2 != null) {
                    listAdapter2.notifyDataSetChanged();
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void generateLink(boolean z) {
        this.loading = true;
        TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
        tLRPC$TL_messages_exportChatInvite.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chat_id);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate(z) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                GroupInviteActivity.this.lambda$generateLink$3$GroupInviteActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$generateLink$3$GroupInviteActivity(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, z) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                GroupInviteActivity.this.lambda$null$2$GroupInviteActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$GroupInviteActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            this.invite = (TLRPC$ExportedChatInvite) tLObject;
            if (z) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setMessage(LocaleController.getString("RevokeAlertNewLink", NUM));
                    builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                    builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                } else {
                    return;
                }
            }
        }
        this.loading = false;
        this.listAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == GroupInviteActivity.this.revokeLinkRow || adapterPosition == GroupInviteActivity.this.copyLinkRow || adapterPosition == GroupInviteActivity.this.shareLinkRow || adapterPosition == GroupInviteActivity.this.linkRow;
        }

        public int getItemCount() {
            if (GroupInviteActivity.this.loading) {
                return 0;
            }
            return GroupInviteActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 1) {
                view = new TextBlockCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else {
                view = new TextInfoPrivacyCell(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == GroupInviteActivity.this.copyLinkRow) {
                    textSettingsCell.setText(LocaleController.getString("CopyLink", NUM), true);
                } else if (i == GroupInviteActivity.this.shareLinkRow) {
                    textSettingsCell.setText(LocaleController.getString("ShareLink", NUM), false);
                } else if (i == GroupInviteActivity.this.revokeLinkRow) {
                    textSettingsCell.setText(LocaleController.getString("RevokeLink", NUM), true);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == GroupInviteActivity.this.shadowRow) {
                    textInfoPrivacyCell.setText("");
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == GroupInviteActivity.this.linkInfoRow) {
                    TLRPC$Chat chat = MessagesController.getInstance(GroupInviteActivity.this.currentAccount).getChat(Integer.valueOf(GroupInviteActivity.this.chat_id));
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        textInfoPrivacyCell.setText(LocaleController.getString("LinkInfo", NUM));
                    } else {
                        textInfoPrivacyCell.setText(LocaleController.getString("ChannelLinkInfo", NUM));
                    }
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            } else if (itemViewType == 2) {
                ((TextBlockCell) viewHolder.itemView).setText(GroupInviteActivity.this.invite != null ? GroupInviteActivity.this.invite.link : "error", false);
            }
        }

        public int getItemViewType(int i) {
            if (i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.shareLinkRow || i == GroupInviteActivity.this.revokeLinkRow) {
                return 0;
            }
            if (i == GroupInviteActivity.this.shadowRow || i == GroupInviteActivity.this.linkInfoRow) {
                return 1;
            }
            if (i == GroupInviteActivity.this.linkRow) {
                return 2;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextBlockCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        return arrayList;
    }
}

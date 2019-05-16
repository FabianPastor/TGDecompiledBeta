package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_exportChatInvite;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class GroupInviteActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chat_id;
    private int copyLinkRow;
    private EmptyTextProgressView emptyView;
    private ExportedChatInvite invite;
    private int linkInfoRow;
    private int linkRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private int revokeLinkRow;
    private int rowCount;
    private int shadowRow;
    private int shareLinkRow;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == GroupInviteActivity.this.revokeLinkRow || adapterPosition == GroupInviteActivity.this.copyLinkRow || adapterPosition == GroupInviteActivity.this.shareLinkRow || adapterPosition == GroupInviteActivity.this.linkRow;
        }

        public int getItemCount() {
            return GroupInviteActivity.this.loading ? 0 : GroupInviteActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor(str));
            } else if (i != 1) {
                textSettingsCell = new TextBlockCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor(str));
            } else {
                textSettingsCell = new TextInfoPrivacyCell(this.mContext);
            }
            return new Holder(textSettingsCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
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
                String str = "windowBackgroundGrayShadow";
                if (i == GroupInviteActivity.this.shadowRow) {
                    textInfoPrivacyCell.setText("");
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                } else if (i == GroupInviteActivity.this.linkInfoRow) {
                    Chat chat = MessagesController.getInstance(GroupInviteActivity.this.currentAccount).getChat(Integer.valueOf(GroupInviteActivity.this.chat_id));
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        textInfoPrivacyCell.setText(LocaleController.getString("LinkInfo", NUM));
                    } else {
                        textInfoPrivacyCell.setText(LocaleController.getString("ChannelLinkInfo", NUM));
                    }
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
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

    public GroupInviteActivity(int i) {
        this.chat_id = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
        this.loading = true;
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.linkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.linkInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.copyLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.revokeLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shadowRow = i;
        return true;
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLink", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupInviteActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$GroupInviteActivity$0RYe3qGmyjz46oz7qfkeGrq-SgU(this));
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
                        FileLog.e(e);
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
                        FileLog.e(e2);
                    }
                }
            } else if (i == this.revokeLinkRow) {
                Builder builder = new Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("RevokeAlert", NUM));
                builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new -$$Lambda$GroupInviteActivity$6ivlxIl3wzffUm1tdYZ92-Ta-UA(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                showDialog(builder.create());
            }
        }
    }

    public /* synthetic */ void lambda$null$0$GroupInviteActivity(DialogInterface dialogInterface, int i) {
        generateLink(true);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = (ChatFull) objArr[0];
            int intValue = ((Integer) objArr[1]).intValue();
            if (chatFull.id == this.chat_id && intValue == this.classGuid) {
                this.invite = MessagesController.getInstance(this.currentAccount).getExportedInvite(this.chat_id);
                if (this.invite instanceof TL_chatInviteExported) {
                    this.loading = false;
                    ListAdapter listAdapter = this.listAdapter;
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                generateLink(false);
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void generateLink(boolean z) {
        this.loading = true;
        TL_messages_exportChatInvite tL_messages_exportChatInvite = new TL_messages_exportChatInvite();
        tL_messages_exportChatInvite.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chat_id);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_exportChatInvite, new -$$Lambda$GroupInviteActivity$GRgS5oE61g396ll3j_eBPQt5fnk(this, z)), this.classGuid);
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$generateLink$3$GroupInviteActivity(boolean z, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$GroupInviteActivity$Kxy8dT4YrMqik2lxA2i8M0Px_2A(this, tL_error, tLObject, z));
    }

    public /* synthetic */ void lambda$null$2$GroupInviteActivity(TL_error tL_error, TLObject tLObject, boolean z) {
        if (tL_error == null) {
            this.invite = (ExportedChatInvite) tLObject;
            if (z) {
                if (getParentActivity() != null) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("RevokeAlertNewLink", NUM));
                    builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                    builder.setNegativeButton(LocaleController.getString("OK", NUM), null);
                    showDialog(builder.create());
                } else {
                    return;
                }
            }
        }
        this.loading = false;
        this.listAdapter.notifyDataSetChanged();
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[14];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextBlockCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[9] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextSettingsCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        return themeDescriptionArr;
    }
}

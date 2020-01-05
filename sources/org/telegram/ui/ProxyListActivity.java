package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SharedConfig.ProxyInfo;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ProxyListActivity extends BaseFragment implements NotificationCenterDelegate {
    private int callsDetailRow;
    private int callsRow;
    private int connectionsHeaderRow;
    private int currentConnectionState;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int proxyAddRow;
    private int proxyDetailRow;
    private int proxyEndRow;
    private int proxyStartRow;
    private int rowCount;
    private int useProxyDetailRow;
    private boolean useProxyForCalls;
    private int useProxyRow;
    private boolean useProxySettings;

    public class TextDetailProxyCell extends FrameLayout {
        private Drawable checkDrawable;
        private ImageView checkImageView;
        private int color;
        private ProxyInfo currentInfo;
        private TextView textView;
        private TextView valueTextView;

        public TextDetailProxyCell(Context context) {
            super(context);
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            int i2 = 56;
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 56 : 21), 10.0f, (float) (LocaleController.isRTL ? 21 : 56), 0.0f));
            this.valueTextView = new TextView(context);
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
            this.valueTextView.setEllipsize(TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
            TextView textView = this.valueTextView;
            int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
            float f = (float) (LocaleController.isRTL ? 56 : 21);
            if (LocaleController.isRTL) {
                i2 = 21;
            }
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, i3, f, 35.0f, (float) i2, 0.0f));
            this.checkImageView = new ImageView(context);
            this.checkImageView.setImageResource(NUM);
            this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText3"), Mode.MULTIPLY));
            this.checkImageView.setScaleType(ScaleType.CENTER);
            this.checkImageView.setContentDescription(LocaleController.getString("Edit", NUM));
            ImageView imageView = this.checkImageView;
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(imageView, LayoutHelper.createFrame(48, 48.0f, i | 48, 8.0f, 8.0f, 8.0f, 0.0f));
            this.checkImageView.setOnClickListener(new -$$Lambda$ProxyListActivity$TextDetailProxyCell$X7y0ocxBETGvRg05YaI-_kXxQ00(this));
            setWillNotDraw(false);
        }

        public /* synthetic */ void lambda$new$0$ProxyListActivity$TextDetailProxyCell(View view) {
            ProxyListActivity.this.presentFragment(new ProxySettingsActivity(this.currentInfo));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, NUM));
        }

        public void setProxy(ProxyInfo proxyInfo) {
            TextView textView = this.textView;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(proxyInfo.address);
            stringBuilder.append(":");
            stringBuilder.append(proxyInfo.port);
            textView.setText(stringBuilder.toString());
            this.currentInfo = proxyInfo;
        }

        public void updateStatus() {
            String str = "Ping";
            String str2 = ", ";
            String str3 = "windowBackgroundWhiteGrayText2";
            ProxyInfo proxyInfo;
            String str4;
            TextView textView;
            StringBuilder stringBuilder;
            if (SharedConfig.currentProxy != this.currentInfo || !ProxyListActivity.this.useProxySettings) {
                proxyInfo = this.currentInfo;
                if (proxyInfo.checking) {
                    this.valueTextView.setText(LocaleController.getString("Checking", NUM));
                } else if (proxyInfo.available) {
                    str4 = "Available";
                    if (proxyInfo.ping != 0) {
                        textView = this.valueTextView;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(LocaleController.getString(str4, NUM));
                        stringBuilder.append(str2);
                        stringBuilder.append(LocaleController.formatString(str, NUM, Long.valueOf(this.currentInfo.ping)));
                        textView.setText(stringBuilder.toString());
                    } else {
                        this.valueTextView.setText(LocaleController.getString(str4, NUM));
                    }
                    str3 = "windowBackgroundWhiteGreenText";
                } else {
                    this.valueTextView.setText(LocaleController.getString("Unavailable", NUM));
                    str3 = "windowBackgroundWhiteRedText4";
                }
            } else if (ProxyListActivity.this.currentConnectionState == 3 || ProxyListActivity.this.currentConnectionState == 5) {
                str4 = "Connected";
                if (this.currentInfo.ping != 0) {
                    textView = this.valueTextView;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(LocaleController.getString(str4, NUM));
                    stringBuilder.append(str2);
                    stringBuilder.append(LocaleController.formatString(str, NUM, Long.valueOf(this.currentInfo.ping)));
                    textView.setText(stringBuilder.toString());
                } else {
                    this.valueTextView.setText(LocaleController.getString(str4, NUM));
                }
                proxyInfo = this.currentInfo;
                if (!(proxyInfo.checking || proxyInfo.available)) {
                    proxyInfo.availableCheckTime = 0;
                }
                str3 = "windowBackgroundWhiteBlueText6";
            } else {
                this.valueTextView.setText(LocaleController.getString("Connecting", NUM));
            }
            this.color = Theme.getColor(str3);
            this.valueTextView.setTag(str3);
            this.valueTextView.setTextColor(this.color);
            Drawable drawable = this.checkDrawable;
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(this.color, Mode.MULTIPLY));
            }
        }

        public void setChecked(boolean z) {
            if (z) {
                if (this.checkDrawable == null) {
                    this.checkDrawable = getResources().getDrawable(NUM).mutate();
                }
                Drawable drawable = this.checkDrawable;
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(this.color, Mode.MULTIPLY));
                }
                if (LocaleController.isRTL) {
                    this.valueTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, this.checkDrawable, null);
                    return;
                } else {
                    this.valueTextView.setCompoundDrawablesWithIntrinsicBounds(this.checkDrawable, null, null, null);
                    return;
                }
            }
            this.valueTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        public void setValue(CharSequence charSequence) {
            this.valueTextView.setText(charSequence);
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateStatus();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    private class ListAdapter extends SelectionAdapter {
        public static final int PAYLOAD_CHECKED_CHANGED = 0;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ProxyListActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            String str = "windowBackgroundGrayShadow";
            if (itemViewType != 0) {
                boolean z = true;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    if (i == ProxyListActivity.this.proxyAddRow) {
                        textSettingsCell.setText(LocaleController.getString("AddProxy", NUM), false);
                    }
                } else if (itemViewType == 2) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == ProxyListActivity.this.connectionsHeaderRow) {
                        headerCell.setText(LocaleController.getString("ProxyConnections", NUM));
                    }
                } else if (itemViewType == 3) {
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i == ProxyListActivity.this.useProxyRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("UseProxySettings", NUM), ProxyListActivity.this.useProxySettings, false);
                    } else if (i == ProxyListActivity.this.callsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("UseProxyForCalls", NUM), ProxyListActivity.this.useProxyForCalls, false);
                    }
                } else if (itemViewType == 4) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ProxyListActivity.this.callsDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("UseProxyForCallsInfo", NUM));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                    }
                } else if (itemViewType == 5) {
                    TextDetailProxyCell textDetailProxyCell = (TextDetailProxyCell) viewHolder.itemView;
                    ProxyInfo proxyInfo = (ProxyInfo) SharedConfig.proxyList.get(i - ProxyListActivity.this.proxyStartRow);
                    textDetailProxyCell.setProxy(proxyInfo);
                    if (SharedConfig.currentProxy != proxyInfo) {
                        z = false;
                    }
                    textDetailProxyCell.setChecked(z);
                }
            } else if (i == ProxyListActivity.this.proxyDetailRow && ProxyListActivity.this.callsRow == -1) {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
            } else {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i, List list) {
            if (viewHolder.getItemViewType() == 3 && list.contains(Integer.valueOf(0))) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (i == ProxyListActivity.this.useProxyRow) {
                    textCheckCell.setChecked(ProxyListActivity.this.useProxySettings);
                    return;
                } else if (i == ProxyListActivity.this.callsRow) {
                    textCheckCell.setChecked(ProxyListActivity.this.useProxyForCalls);
                    return;
                } else {
                    return;
                }
            }
            super.onBindViewHolder(viewHolder, i, list);
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 3) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition == ProxyListActivity.this.useProxyRow) {
                    textCheckCell.setChecked(ProxyListActivity.this.useProxySettings);
                } else if (adapterPosition == ProxyListActivity.this.callsRow) {
                    textCheckCell.setChecked(ProxyListActivity.this.useProxyForCalls);
                }
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == ProxyListActivity.this.useProxyRow || adapterPosition == ProxyListActivity.this.callsRow || adapterPosition == ProxyListActivity.this.proxyAddRow || (adapterPosition >= ProxyListActivity.this.proxyStartRow && adapterPosition < ProxyListActivity.this.proxyEndRow);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            if (i != 0) {
                String str = "windowBackgroundWhite";
                if (i == 1) {
                    textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                } else if (i == 2) {
                    textSettingsCell = new HeaderCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                } else if (i == 3) {
                    textSettingsCell = new TextCheckCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                } else if (i == 4) {
                    textSettingsCell = new TextInfoPrivacyCell(this.mContext);
                    textSettingsCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i != 5) {
                    textSettingsCell = null;
                } else {
                    textSettingsCell = new TextDetailProxyCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                }
            } else {
                textSettingsCell = new ShadowSectionCell(this.mContext);
            }
            textSettingsCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(textSettingsCell);
        }

        public int getItemViewType(int i) {
            if (i == ProxyListActivity.this.useProxyDetailRow || i == ProxyListActivity.this.proxyDetailRow) {
                return 0;
            }
            if (i == ProxyListActivity.this.proxyAddRow) {
                return 1;
            }
            if (i == ProxyListActivity.this.useProxyRow || i == ProxyListActivity.this.callsRow) {
                return 3;
            }
            if (i == ProxyListActivity.this.connectionsHeaderRow) {
                return 2;
            }
            return (i < ProxyListActivity.this.proxyStartRow || i >= ProxyListActivity.this.proxyEndRow) ? 4 : 5;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        SharedConfig.loadProxyList();
        this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        boolean z = globalMainSettings.getBoolean("proxy_enabled", false) && !SharedConfig.proxyList.isEmpty();
        this.useProxySettings = z;
        this.useProxyForCalls = globalMainSettings.getBoolean("proxy_enabled_calls", false);
        updateRows(true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("ProxySettings", NUM));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ProxyListActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$ProxyListActivity$_EXt_SJXR8hGLT7iiDQoSoNBA_A(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$ProxyListActivity$xYgyGp8TqWacHgvLq8TnwAzgoII(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$ProxyListActivity(View view, int i) {
        String str = "proxy_enabled";
        String str2 = "proxy_secret";
        String str3 = "proxy_port";
        String str4 = "proxy_user";
        String str5 = "proxy_pass";
        String str6 = "proxy_ip";
        Editor edit;
        Holder holder;
        Editor edit2;
        ProxyInfo proxyInfo;
        int i2;
        if (i == this.useProxyRow) {
            if (SharedConfig.currentProxy == null) {
                if (SharedConfig.proxyList.isEmpty()) {
                    presentFragment(new ProxySettingsActivity());
                    return;
                }
                SharedConfig.currentProxy = (ProxyInfo) SharedConfig.proxyList.get(0);
                if (!this.useProxySettings) {
                    MessagesController.getGlobalMainSettings();
                    edit = MessagesController.getGlobalMainSettings().edit();
                    edit.putString(str6, SharedConfig.currentProxy.address);
                    edit.putString(str5, SharedConfig.currentProxy.password);
                    edit.putString(str4, SharedConfig.currentProxy.username);
                    edit.putInt(str3, SharedConfig.currentProxy.port);
                    edit.putString(str2, SharedConfig.currentProxy.secret);
                    edit.commit();
                }
            }
            this.useProxySettings ^= 1;
            MessagesController.getGlobalMainSettings();
            ((TextCheckCell) view).setChecked(this.useProxySettings);
            if (!this.useProxySettings) {
                holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.callsRow);
                if (holder != null) {
                    ((TextCheckCell) holder.itemView).setChecked(false);
                }
                this.useProxyForCalls = false;
            }
            edit2 = MessagesController.getGlobalMainSettings().edit();
            edit2.putBoolean(str, this.useProxySettings);
            edit2.commit();
            boolean z = this.useProxySettings;
            proxyInfo = SharedConfig.currentProxy;
            ConnectionsManager.setProxySettings(z, proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password, proxyInfo.secret);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
            for (i2 = this.proxyStartRow; i2 < this.proxyEndRow; i2++) {
                Holder holder2 = (Holder) this.listView.findViewHolderForAdapterPosition(i2);
                if (holder2 != null) {
                    ((TextDetailProxyCell) holder2.itemView).updateStatus();
                }
            }
        } else {
            String str7 = "proxy_enabled_calls";
            if (i == this.callsRow) {
                this.useProxyForCalls ^= 1;
                ((TextCheckCell) view).setChecked(this.useProxyForCalls);
                edit2 = MessagesController.getGlobalMainSettings().edit();
                edit2.putBoolean(str7, this.useProxyForCalls);
                edit2.commit();
            } else {
                i2 = this.proxyStartRow;
                if (i >= i2 && i < this.proxyEndRow) {
                    proxyInfo = (ProxyInfo) SharedConfig.proxyList.get(i - i2);
                    this.useProxySettings = true;
                    edit = MessagesController.getGlobalMainSettings().edit();
                    edit.putString(str6, proxyInfo.address);
                    edit.putString(str5, proxyInfo.password);
                    edit.putString(str4, proxyInfo.username);
                    edit.putInt(str3, proxyInfo.port);
                    edit.putString(str2, proxyInfo.secret);
                    edit.putBoolean(str, this.useProxySettings);
                    if (!proxyInfo.secret.isEmpty()) {
                        this.useProxyForCalls = false;
                        edit.putBoolean(str7, false);
                    }
                    edit.commit();
                    SharedConfig.currentProxy = proxyInfo;
                    for (i = this.proxyStartRow; i < this.proxyEndRow; i++) {
                        Holder holder3 = (Holder) this.listView.findViewHolderForAdapterPosition(i);
                        if (holder3 != null) {
                            TextDetailProxyCell textDetailProxyCell = (TextDetailProxyCell) holder3.itemView;
                            textDetailProxyCell.setChecked(textDetailProxyCell.currentInfo == proxyInfo);
                            textDetailProxyCell.updateStatus();
                        }
                    }
                    updateRows(false);
                    holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.useProxyRow);
                    if (holder != null) {
                        ((TextCheckCell) holder.itemView).setChecked(true);
                    }
                    boolean z2 = this.useProxySettings;
                    proxyInfo = SharedConfig.currentProxy;
                    ConnectionsManager.setProxySettings(z2, proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password, proxyInfo.secret);
                } else if (i == this.proxyAddRow) {
                    presentFragment(new ProxySettingsActivity());
                }
            }
        }
    }

    public /* synthetic */ boolean lambda$createView$2$ProxyListActivity(View view, int i) {
        int i2 = this.proxyStartRow;
        if (i < i2 || i >= this.proxyEndRow) {
            return false;
        }
        ProxyInfo proxyInfo = (ProxyInfo) SharedConfig.proxyList.get(i - i2);
        Builder builder = new Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("DeleteProxy", NUM));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ProxyListActivity$OajISs3QuB64bwKXTSHCR_ffCLASSNAME(this, proxyInfo, i));
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$1$ProxyListActivity(ProxyInfo proxyInfo, int i, DialogInterface dialogInterface, int i2) {
        SharedConfig.deleteProxy(proxyInfo);
        proxyInfo = SharedConfig.currentProxy;
        Integer valueOf = Integer.valueOf(0);
        if (proxyInfo == null) {
            this.useProxyForCalls = false;
            this.useProxySettings = false;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        updateRows(false);
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemRemoved(i);
            if (SharedConfig.currentProxy == null) {
                this.listAdapter.notifyItemChanged(this.useProxyRow, valueOf);
                this.listAdapter.notifyItemChanged(this.callsRow, valueOf);
            }
        }
    }

    private void updateRows(boolean z) {
        int i = 0;
        this.rowCount = 0;
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.useProxyRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.useProxyDetailRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.connectionsHeaderRow = i2;
        if (SharedConfig.proxyList.isEmpty()) {
            this.proxyStartRow = -1;
            this.proxyEndRow = -1;
        } else {
            i2 = this.rowCount;
            this.proxyStartRow = i2;
            this.rowCount = i2 + SharedConfig.proxyList.size();
            this.proxyEndRow = this.rowCount;
        }
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.proxyAddRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.proxyDetailRow = i2;
        ProxyInfo proxyInfo = SharedConfig.currentProxy;
        if (proxyInfo == null || proxyInfo.secret.isEmpty()) {
            if (this.callsRow == -1) {
                i = 1;
            }
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsDetailRow = i2;
            if (!(z || i == 0)) {
                this.listAdapter.notifyItemChanged(this.proxyDetailRow);
                this.listAdapter.notifyItemRangeInserted(this.proxyDetailRow + 1, 2);
            }
        } else {
            if (this.callsRow != -1) {
                i = 1;
            }
            this.callsRow = -1;
            this.callsDetailRow = -1;
            if (!(z || r0 == 0)) {
                this.listAdapter.notifyItemChanged(this.proxyDetailRow);
                this.listAdapter.notifyItemRangeRemoved(this.proxyDetailRow + 1, 2);
            }
        }
        checkProxyList();
        if (z) {
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    private void checkProxyList() {
        int size = SharedConfig.proxyList.size();
        for (int i = 0; i < size; i++) {
            ProxyInfo proxyInfo = (ProxyInfo) SharedConfig.proxyList.get(i);
            if (!proxyInfo.checking && SystemClock.elapsedRealtime() - proxyInfo.availableCheckTime >= 120000) {
                proxyInfo.checking = true;
                proxyInfo.proxyCheckPingId = ConnectionsManager.getInstance(this.currentAccount).checkProxy(proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password, proxyInfo.secret, new -$$Lambda$ProxyListActivity$FREOr2lMcXdjYTgHJySmzXcovWk(proxyInfo));
            }
        }
    }

    static /* synthetic */ void lambda$null$3(ProxyInfo proxyInfo, long j) {
        proxyInfo.availableCheckTime = SystemClock.elapsedRealtime();
        proxyInfo.checking = false;
        if (j == -1) {
            proxyInfo.available = false;
            proxyInfo.ping = 0;
        } else {
            proxyInfo.ping = j;
            proxyInfo.available = true;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, proxyInfo);
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Holder holder;
        if (i == NotificationCenter.proxySettingsChanged) {
            updateRows(true);
        } else if (i == NotificationCenter.didUpdateConnectionState) {
            i = ConnectionsManager.getInstance(i2).getConnectionState();
            if (this.currentConnectionState != i) {
                this.currentConnectionState = i;
                if (this.listView != null && SharedConfig.currentProxy != null) {
                    i = SharedConfig.proxyList.indexOf(SharedConfig.currentProxy);
                    if (i >= 0) {
                        holder = (Holder) this.listView.findViewHolderForAdapterPosition(i + this.proxyStartRow);
                        if (holder != null) {
                            ((TextDetailProxyCell) holder.itemView).updateStatus();
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.proxyCheckDone && this.listView != null) {
            i = SharedConfig.proxyList.indexOf((ProxyInfo) objArr[0]);
            if (i >= 0) {
                holder = (Holder) this.listView.findViewHolderForAdapterPosition(i + this.proxyStartRow);
                if (holder != null) {
                    ((TextDetailProxyCell) holder.itemView).updateStatus();
                }
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[25];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailProxyCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextSettingsCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextSettingsCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[11] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailProxyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG) | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteBlueText6");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG) | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG) | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGreenText");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG) | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteRedText4");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"checkImageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[21] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        return themeDescriptionArr;
    }
}

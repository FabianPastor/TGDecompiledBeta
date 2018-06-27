package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.VcardItem;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.upstream.DataSchemeDataSource;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

public class PhonebookShareActivity extends BaseFragment {
    private ListAdapter adapter;
    private BackupImageView avatarImage;
    private FrameLayout bottomLayout;
    private User currentUser;
    private PhonebookSelectActivityDelegate delegate;
    private int detailRow;
    private int emptyRow;
    private int extraHeight;
    private View extraHeightView;
    private boolean isImport;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private TextView nameTextView;
    private ArrayList<VcardItem> other = new ArrayList();
    private int overscrollRow;
    private int phoneDividerRow;
    private int phoneEndRow;
    private int phoneStartRow;
    private ArrayList<VcardItem> phones = new ArrayList();
    private int rowCount;
    private View shadowView;
    private TextView shareTextView;
    private int vcardEndRow;
    private int vcardStartRow;

    /* renamed from: org.telegram.ui.PhonebookShareActivity$7 */
    class C17017 implements OnClickListener {

        /* renamed from: org.telegram.ui.PhonebookShareActivity$7$1 */
        class C17001 implements DialogInterface.OnClickListener {
            C17001() {
            }

            private void fillRowWithType(String type, ContentValues row) {
                if (type.startsWith("X-")) {
                    row.put("data2", Integer.valueOf(0));
                    row.put("data3", type.substring(2));
                } else if ("PREF".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(12));
                } else if ("HOME".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(1));
                } else if ("MOBILE".equalsIgnoreCase(type) || "CELL".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(2));
                } else if ("OTHER".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(7));
                } else if ("WORK".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(3));
                } else if ("RADIO".equalsIgnoreCase(type) || "VOICE".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(14));
                } else if ("PAGER".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(6));
                } else if ("CALLBACK".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(8));
                } else if ("CAR".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(9));
                } else if ("ASSISTANT".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(19));
                } else if ("MMS".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(20));
                } else if (type.startsWith("FAX")) {
                    row.put("data2", Integer.valueOf(4));
                } else {
                    row.put("data2", Integer.valueOf(0));
                    row.put("data3", type);
                }
            }

            private void fillUrlRowWithType(String type, ContentValues row) {
                if (type.startsWith("X-")) {
                    row.put("data2", Integer.valueOf(0));
                    row.put("data3", type.substring(2));
                } else if ("HOMEPAGE".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(1));
                } else if ("BLOG".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(2));
                } else if ("PROFILE".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(3));
                } else if ("HOME".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(4));
                } else if ("WORK".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(5));
                } else if ("FTP".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(6));
                } else if ("OTHER".equalsIgnoreCase(type)) {
                    row.put("data2", Integer.valueOf(7));
                } else {
                    row.put("data2", Integer.valueOf(0));
                    row.put("data3", type);
                }
            }

            public void onClick(DialogInterface dialog, int which) {
                if (PhonebookShareActivity.this.getParentActivity() != null) {
                    int a;
                    VcardItem item;
                    ContentValues row;
                    Intent intent = null;
                    if (which == 0) {
                        intent = new Intent("android.intent.action.INSERT");
                        intent.setType("vnd.android.cursor.dir/raw_contact");
                    } else if (which == 1) {
                        intent = new Intent("android.intent.action.INSERT_OR_EDIT");
                        intent.setType("vnd.android.cursor.item/contact");
                    }
                    intent.putExtra("name", ContactsController.formatName(PhonebookShareActivity.this.currentUser.first_name, PhonebookShareActivity.this.currentUser.last_name));
                    ArrayList<ContentValues> data = new ArrayList();
                    for (a = 0; a < PhonebookShareActivity.this.phones.size(); a++) {
                        item = (VcardItem) PhonebookShareActivity.this.phones.get(a);
                        row = new ContentValues();
                        row.put("mimetype", "vnd.android.cursor.item/phone_v2");
                        row.put("data1", item.getValue(false));
                        fillRowWithType(item.getRawType(false), row);
                        data.add(row);
                    }
                    boolean orgAdded = false;
                    for (a = 0; a < PhonebookShareActivity.this.other.size(); a++) {
                        item = (VcardItem) PhonebookShareActivity.this.other.get(a);
                        if (item.type == 1) {
                            row = new ContentValues();
                            row.put("mimetype", "vnd.android.cursor.item/email_v2");
                            row.put("data1", item.getValue(false));
                            fillRowWithType(item.getRawType(false), row);
                            data.add(row);
                        } else if (item.type == 3) {
                            row = new ContentValues();
                            row.put("mimetype", "vnd.android.cursor.item/website");
                            row.put("data1", item.getValue(false));
                            fillUrlRowWithType(item.getRawType(false), row);
                            data.add(row);
                        } else if (item.type == 4) {
                            row = new ContentValues();
                            row.put("mimetype", "vnd.android.cursor.item/note");
                            row.put("data1", item.getValue(false));
                            data.add(row);
                        } else if (item.type == 5) {
                            row = new ContentValues();
                            row.put("mimetype", "vnd.android.cursor.item/contact_event");
                            row.put("data1", item.getValue(false));
                            row.put("data2", Integer.valueOf(3));
                            data.add(row);
                        } else if (item.type == 2) {
                            row = new ContentValues();
                            row.put("mimetype", "vnd.android.cursor.item/postal-address_v2");
                            String[] args = item.getRawValue();
                            if (args.length > 0) {
                                row.put("data5", args[0]);
                            }
                            if (args.length > 1) {
                                row.put("data6", args[1]);
                            }
                            if (args.length > 2) {
                                row.put("data4", args[2]);
                            }
                            if (args.length > 3) {
                                row.put("data7", args[3]);
                            }
                            if (args.length > 4) {
                                row.put("data8", args[4]);
                            }
                            if (args.length > 5) {
                                row.put("data9", args[5]);
                            }
                            if (args.length > 6) {
                                row.put("data10", args[6]);
                            }
                            type = item.getRawType(false);
                            if ("HOME".equalsIgnoreCase(type)) {
                                row.put("data2", Integer.valueOf(1));
                            } else if ("WORK".equalsIgnoreCase(type)) {
                                row.put("data2", Integer.valueOf(2));
                            } else if ("OTHER".equalsIgnoreCase(type)) {
                                row.put("data2", Integer.valueOf(3));
                            }
                            data.add(row);
                        } else if (item.type == 20) {
                            row = new ContentValues();
                            row.put("mimetype", "vnd.android.cursor.item/im");
                            String imType = item.getRawType(true);
                            type = item.getRawType(false);
                            row.put("data1", item.getValue(false));
                            if ("AIM".equalsIgnoreCase(imType)) {
                                row.put("data5", Integer.valueOf(0));
                            } else if ("MSN".equalsIgnoreCase(imType)) {
                                row.put("data5", Integer.valueOf(1));
                            } else if ("YAHOO".equalsIgnoreCase(imType)) {
                                row.put("data5", Integer.valueOf(2));
                            } else if ("SKYPE".equalsIgnoreCase(imType)) {
                                row.put("data5", Integer.valueOf(3));
                            } else if ("QQ".equalsIgnoreCase(imType)) {
                                row.put("data5", Integer.valueOf(4));
                            } else if ("GOOGLE-TALK".equalsIgnoreCase(imType)) {
                                row.put("data5", Integer.valueOf(5));
                            } else if ("ICQ".equalsIgnoreCase(imType)) {
                                row.put("data5", Integer.valueOf(6));
                            } else if ("JABBER".equalsIgnoreCase(imType)) {
                                row.put("data5", Integer.valueOf(7));
                            } else if ("NETMEETING".equalsIgnoreCase(imType)) {
                                row.put("data5", Integer.valueOf(8));
                            } else {
                                row.put("data5", Integer.valueOf(-1));
                                row.put("data6", item.getRawType(true));
                            }
                            if ("HOME".equalsIgnoreCase(type)) {
                                row.put("data2", Integer.valueOf(1));
                            } else if ("WORK".equalsIgnoreCase(type)) {
                                row.put("data2", Integer.valueOf(2));
                            } else if ("OTHER".equalsIgnoreCase(type)) {
                                row.put("data2", Integer.valueOf(3));
                            }
                            data.add(row);
                        } else if (item.type == 6 && !orgAdded) {
                            orgAdded = true;
                            row = new ContentValues();
                            row.put("mimetype", "vnd.android.cursor.item/organization");
                            for (int b = a; b < PhonebookShareActivity.this.other.size(); b++) {
                                VcardItem orgItem = (VcardItem) PhonebookShareActivity.this.other.get(b);
                                if (orgItem.type == 6) {
                                    type = orgItem.getRawType(true);
                                    if ("ORG".equalsIgnoreCase(type)) {
                                        String[] value = orgItem.getRawValue();
                                        if (value.length != 0) {
                                            if (value.length >= 1) {
                                                row.put("data1", value[0]);
                                            }
                                            if (value.length >= 2) {
                                                row.put("data5", value[1]);
                                            }
                                        }
                                    } else if ("TITLE".equalsIgnoreCase(type)) {
                                        row.put("data4", orgItem.getValue(false));
                                    } else if ("ROLE".equalsIgnoreCase(type)) {
                                        row.put("data4", orgItem.getValue(false));
                                    }
                                    String orgType = orgItem.getRawType(true);
                                    if ("WORK".equalsIgnoreCase(orgType)) {
                                        row.put("data2", Integer.valueOf(1));
                                    } else if ("OTHER".equalsIgnoreCase(orgType)) {
                                        row.put("data2", Integer.valueOf(2));
                                    }
                                }
                            }
                            data.add(row);
                        }
                    }
                    intent.putExtra("finishActivityOnSaveCompleted", true);
                    intent.putParcelableArrayListExtra(DataSchemeDataSource.SCHEME_DATA, data);
                    try {
                        PhonebookShareActivity.this.getParentActivity().startActivity(intent);
                        PhonebookShareActivity.this.finishFragment();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            }
        }

        C17017() {
        }

        public void onClick(View v) {
            if (!PhonebookShareActivity.this.isImport) {
                StringBuilder builder;
                if (PhonebookShareActivity.this.currentUser.restriction_reason != null) {
                    builder = new StringBuilder(PhonebookShareActivity.this.currentUser.restriction_reason);
                } else {
                    builder = new StringBuilder(String.format(Locale.US, "BEGIN:VCARD\nVERSION:3.0\nFN:%1$s\nEND:VCARD", new Object[]{ContactsController.formatName(PhonebookShareActivity.this.currentUser.first_name, PhonebookShareActivity.this.currentUser.last_name)}));
                }
                int idx = builder.lastIndexOf("END:VCARD");
                if (idx >= 0) {
                    int a;
                    VcardItem item;
                    int b;
                    PhonebookShareActivity.this.currentUser.phone = null;
                    for (a = PhonebookShareActivity.this.phones.size() - 1; a >= 0; a--) {
                        item = (VcardItem) PhonebookShareActivity.this.phones.get(a);
                        if (item.checked) {
                            if (PhonebookShareActivity.this.currentUser.phone == null) {
                                PhonebookShareActivity.this.currentUser.phone = item.getValue(false);
                            }
                            for (b = 0; b < item.vcardData.size(); b++) {
                                builder.insert(idx, ((String) item.vcardData.get(b)) + "\n");
                            }
                        }
                    }
                    for (a = PhonebookShareActivity.this.other.size() - 1; a >= 0; a--) {
                        item = (VcardItem) PhonebookShareActivity.this.other.get(a);
                        if (item.checked) {
                            for (b = item.vcardData.size() - 1; b >= 0; b--) {
                                builder.insert(idx, ((String) item.vcardData.get(b)) + "\n");
                            }
                        }
                    }
                    PhonebookShareActivity.this.currentUser.restriction_reason = builder.toString();
                }
                PhonebookShareActivity.this.delegate.didSelectContact(PhonebookShareActivity.this.currentUser);
                PhonebookShareActivity.this.finishFragment();
            } else if (PhonebookShareActivity.this.getParentActivity() != null) {
                Builder builder2 = new Builder(PhonebookShareActivity.this.getParentActivity());
                builder2.setTitle(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                builder2.setItems(new CharSequence[]{LocaleController.getString("CreateNewContact", R.string.CreateNewContact), LocaleController.getString("AddToExistingContact", R.string.AddToExistingContact)}, new C17001());
                builder2.show();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhonebookShareActivity$8 */
    class C17028 implements OnPreDrawListener {
        C17028() {
        }

        public boolean onPreDraw() {
            if (PhonebookShareActivity.this.fragmentView != null) {
                PhonebookShareActivity.this.needLayout();
                PhonebookShareActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            return true;
        }
    }

    public class TextCheckBoxCell extends FrameLayout {
        private CheckBoxSquare checkBox;
        private ImageView imageView;
        private TextView textView;
        final /* synthetic */ PhonebookShareActivity this$0;
        private TextView valueTextView;

        public TextCheckBoxCell(PhonebookShareActivity this$0, Context context) {
            int i;
            float f;
            float f2;
            int i2;
            int i3 = 3;
            this.this$0 = this$0;
            super(context);
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setSingleLine(false);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.textView.setEllipsize(TruncateAt.END);
            View view = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            i |= 48;
            if (LocaleController.isRTL) {
                f = (float) (this$0.isImport ? 17 : 64);
            } else {
                f = 71.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 71.0f;
            } else {
                f2 = (float) (this$0.isImport ? 17 : 64);
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i, f, 10.0f, f2, 0.0f));
            this.valueTextView = new TextView(context);
            this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            TextView textView = this.valueTextView;
            if (LocaleController.isRTL) {
                i2 = 5;
            } else {
                i2 = 3;
            }
            textView.setGravity(i2);
            view = this.valueTextView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            if (LocaleController.isRTL) {
                f = (float) (this$0.isImport ? 17 : 64);
            } else {
                f = 71.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 71.0f;
            } else {
                f2 = (float) (this$0.isImport ? 17 : 64);
            }
            addView(view, LayoutHelper.createFrame(-2, -2.0f, i, f, 35.0f, f2, 0.0f));
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), Mode.MULTIPLY));
            view = this.imageView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 48, LocaleController.isRTL ? 0.0f : 16.0f, 20.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
            if (!this$0.isImport) {
                this.checkBox = new CheckBoxSquare(context, false);
                this.checkBox.setDuplicateParentStateEnabled(false);
                this.checkBox.setFocusable(false);
                this.checkBox.setFocusableInTouchMode(false);
                this.checkBox.setClickable(false);
                View view2 = this.checkBox;
                if (!LocaleController.isRTL) {
                    i3 = 5;
                }
                addView(view2, LayoutHelper.createFrame(18, 18.0f, i3 | 16, 19.0f, 0.0f, 19.0f, 0.0f));
            }
        }

        public void invalidate() {
            super.invalidate();
            this.checkBox.invalidate();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            measureChildWithMargins(this.textView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            measureChildWithMargins(this.valueTextView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            measureChildWithMargins(this.imageView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            if (this.checkBox != null) {
                measureChildWithMargins(this.checkBox, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(64.0f), (this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight()) + AndroidUtilities.dp(20.0f)));
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int y = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0f);
            this.valueTextView.layout(this.valueTextView.getLeft(), y, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + y);
        }

        public void setVCardItem(VcardItem item, int icon) {
            this.textView.setText(item.getValue(true));
            this.valueTextView.setText(item.getType());
            if (this.checkBox != null) {
                this.checkBox.setChecked(item.checked, false);
            }
            if (icon != 0) {
                this.imageView.setImageResource(icon);
            } else {
                this.imageView.setImageDrawable(null);
            }
        }

        public void setChecked(boolean checked) {
            if (this.checkBox != null) {
                this.checkBox.setChecked(checked, true);
            }
        }

        public boolean isChecked() {
            return this.checkBox != null && this.checkBox.isChecked();
        }
    }

    /* renamed from: org.telegram.ui.PhonebookShareActivity$1 */
    class C24021 extends ActionBarMenuOnItemClick {
        C24021() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PhonebookShareActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhonebookShareActivity$4 */
    class C24034 implements OnItemClickListener {
        C24034() {
        }

        public void onItemClick(View view, int position) {
            VcardItem item;
            boolean z = true;
            if (position >= PhonebookShareActivity.this.phoneStartRow && position < PhonebookShareActivity.this.phoneEndRow) {
                item = (VcardItem) PhonebookShareActivity.this.phones.get(position - PhonebookShareActivity.this.phoneStartRow);
            } else if (position < PhonebookShareActivity.this.vcardStartRow || position >= PhonebookShareActivity.this.vcardEndRow) {
                item = null;
            } else {
                item = (VcardItem) PhonebookShareActivity.this.other.get(position - PhonebookShareActivity.this.vcardStartRow);
            }
            if (item != null) {
                if (!PhonebookShareActivity.this.isImport) {
                    if (item.checked) {
                        z = false;
                    }
                    item.checked = z;
                    if (position >= PhonebookShareActivity.this.phoneStartRow && position < PhonebookShareActivity.this.phoneEndRow) {
                        boolean hasChecked = false;
                        for (int a = 0; a < PhonebookShareActivity.this.phones.size(); a++) {
                            if (((VcardItem) PhonebookShareActivity.this.phones.get(a)).checked) {
                                hasChecked = true;
                                break;
                            }
                        }
                        PhonebookShareActivity.this.bottomLayout.setEnabled(hasChecked);
                        PhonebookShareActivity.this.shareTextView.setAlpha(hasChecked ? 1.0f : 0.5f);
                    }
                    ((TextCheckBoxCell) view).setChecked(item.checked);
                } else if (item.type == 0) {
                    try {
                        Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + item.getValue(false)));
                        intent.addFlags(268435456);
                        PhonebookShareActivity.this.getParentActivity().startActivityForResult(intent, 500);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                } else if (item.type == 1) {
                    Browser.openUrl(PhonebookShareActivity.this.getParentActivity(), "mailto:" + item.getValue(false));
                } else if (item.type == 3) {
                    String url = item.getValue(false);
                    if (!url.startsWith("http")) {
                        url = "http://" + url;
                    }
                    Browser.openUrl(PhonebookShareActivity.this.getParentActivity(), url);
                } else {
                    Builder builder = new Builder(PhonebookShareActivity.this.getParentActivity());
                    builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                try {
                                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", item.getValue(false)));
                                    Toast.makeText(PhonebookShareActivity.this.getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        }
                    });
                    PhonebookShareActivity.this.showDialog(builder.create());
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.PhonebookShareActivity$5 */
    class C24045 implements OnItemLongClickListener {
        C24045() {
        }

        public boolean onItemClick(View view, int position) {
            VcardItem item;
            if (position >= PhonebookShareActivity.this.phoneStartRow && position < PhonebookShareActivity.this.phoneEndRow) {
                item = (VcardItem) PhonebookShareActivity.this.phones.get(position - PhonebookShareActivity.this.phoneStartRow);
            } else if (position < PhonebookShareActivity.this.vcardStartRow || position >= PhonebookShareActivity.this.vcardEndRow) {
                item = null;
            } else {
                item = (VcardItem) PhonebookShareActivity.this.other.get(position - PhonebookShareActivity.this.vcardStartRow);
            }
            if (item == null) {
                return false;
            }
            Builder builder = new Builder(PhonebookShareActivity.this.getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        try {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", item.getValue(false)));
                            Toast.makeText(PhonebookShareActivity.this.getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            });
            PhonebookShareActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PhonebookShareActivity$6 */
    class C24056 extends OnScrollListener {
        C24056() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int i = 0;
            if (PhonebookShareActivity.this.layoutManager.getItemCount() != 0) {
                int height = 0;
                View child = recyclerView.getChildAt(0);
                if (child != null) {
                    if (PhonebookShareActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                        int dp = AndroidUtilities.dp(88.0f);
                        if (child.getTop() < 0) {
                            i = child.getTop();
                        }
                        height = dp + i;
                    }
                    if (PhonebookShareActivity.this.extraHeight != height) {
                        PhonebookShareActivity.this.extraHeight = height;
                        PhonebookShareActivity.this.needLayout();
                    }
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PhonebookShareActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == PhonebookShareActivity.this.overscrollRow) {
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(88.0f));
                        return;
                    } else {
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(16.0f));
                        return;
                    }
                case 1:
                    VcardItem item;
                    int icon;
                    TextCheckBoxCell cell = holder.itemView;
                    if (position < PhonebookShareActivity.this.phoneStartRow || position >= PhonebookShareActivity.this.phoneEndRow) {
                        item = (VcardItem) PhonebookShareActivity.this.other.get(position - PhonebookShareActivity.this.vcardStartRow);
                        if (position == PhonebookShareActivity.this.vcardStartRow) {
                            icon = R.drawable.profile_info;
                        } else {
                            icon = 0;
                        }
                    } else {
                        item = (VcardItem) PhonebookShareActivity.this.phones.get(position - PhonebookShareActivity.this.phoneStartRow);
                        if (position == PhonebookShareActivity.this.phoneStartRow) {
                            icon = R.drawable.profile_phone;
                        } else {
                            icon = 0;
                        }
                    }
                    cell.setVCardItem(item, icon);
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return (position >= PhonebookShareActivity.this.phoneStartRow && position < PhonebookShareActivity.this.phoneEndRow) || (position >= PhonebookShareActivity.this.vcardStartRow && position < PhonebookShareActivity.this.vcardEndRow);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new EmptyCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextCheckBoxCell(PhonebookShareActivity.this, this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new DividerCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view.setPadding(AndroidUtilities.dp(72.0f), 0, 0, 0);
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == PhonebookShareActivity.this.emptyRow || position == PhonebookShareActivity.this.overscrollRow) {
                return 0;
            }
            if ((position >= PhonebookShareActivity.this.phoneStartRow && position < PhonebookShareActivity.this.phoneEndRow) || (position >= PhonebookShareActivity.this.vcardStartRow && position < PhonebookShareActivity.this.vcardEndRow)) {
                return 1;
            }
            if (position == PhonebookShareActivity.this.phoneDividerRow || position != PhonebookShareActivity.this.detailRow) {
                return 2;
            }
            return 3;
        }
    }

    public PhonebookShareActivity(Contact contact, Uri uri, File file, String name) {
        ArrayList<User> result = null;
        ArrayList<VcardItem> items = new ArrayList();
        if (uri != null) {
            result = AndroidUtilities.loadVCardFromStream(uri, this.currentAccount, false, items, name);
        } else if (file != null) {
            result = AndroidUtilities.loadVCardFromStream(Uri.fromFile(file), this.currentAccount, false, items, name);
            file.delete();
            this.isImport = true;
        } else if (contact.key != null) {
            result = AndroidUtilities.loadVCardFromStream(Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, contact.key), this.currentAccount, true, items, name);
        } else {
            this.currentUser = contact.user;
            VcardItem item = new VcardItem();
            item.type = 0;
            ArrayList arrayList = item.vcardData;
            String str = "TEL;MOBILE:+" + this.currentUser.phone;
            item.fullData = str;
            arrayList.add(str);
            this.phones.add(item);
        }
        if (result != null) {
            for (int a = 0; a < items.size(); a++) {
                item = (VcardItem) items.get(a);
                if (item.type == 0) {
                    boolean exists = false;
                    for (int b = 0; b < this.phones.size(); b++) {
                        if (((VcardItem) this.phones.get(b)).getValue(false).equals(item.getValue(false))) {
                            exists = true;
                            break;
                        }
                    }
                    if (exists) {
                        item.checked = false;
                    } else {
                        this.phones.add(item);
                    }
                } else {
                    this.other.add(item);
                }
            }
            if (result != null && !result.isEmpty()) {
                this.currentUser = (User) result.get(0);
                if (contact != null && contact.user != null) {
                    this.currentUser.photo = contact.user.photo;
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (this.currentUser == null) {
            return false;
        }
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.overscrollRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emptyRow = i;
        if (this.phones.isEmpty()) {
            this.phoneStartRow = -1;
            this.phoneEndRow = -1;
        } else {
            this.phoneStartRow = this.rowCount;
            this.rowCount += this.phones.size();
            this.phoneEndRow = this.rowCount;
        }
        if (this.other.isEmpty()) {
            this.phoneDividerRow = -1;
            this.vcardStartRow = -1;
            this.vcardEndRow = -1;
        } else {
            if (this.phones.isEmpty()) {
                this.phoneDividerRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.phoneDividerRow = i;
            }
            this.vcardStartRow = this.rowCount;
            this.rowCount += this.other.size();
            this.vcardEndRow = this.rowCount;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.detailRow = i;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_avatar_actionBarIconBlue), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new C24021());
        this.fragmentView = new FrameLayout(context) {
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child != PhonebookShareActivity.this.listView) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (PhonebookShareActivity.this.parentLayout == null) {
                    return result;
                }
                int actionBarHeight = 0;
                int childCount = getChildCount();
                for (int a = 0; a < childCount; a++) {
                    View view = getChildAt(a);
                    if (view != child && (view instanceof ActionBar) && view.getVisibility() == 0) {
                        if (((ActionBar) view).getCastShadows()) {
                            actionBarHeight = view.getMeasuredHeight();
                        }
                        PhonebookShareActivity.this.parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                        return result;
                    }
                }
                PhonebookShareActivity.this.parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                return result;
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager c25423 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = c25423;
        recyclerListView.setLayoutManager(c25423);
        this.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setOnItemClickListener(new C24034());
        this.listView.setOnItemLongClickListener(new C24045());
        frameLayout.addView(this.actionBar);
        this.extraHeightView = new View(context);
        this.extraHeightView.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        this.shadowView = new View(context);
        this.shadowView.setBackgroundResource(R.drawable.header_shadow);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_profile_title));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setPivotX(0.0f);
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 8.0f, 10.0f, 0.0f));
        needLayout();
        this.listView.setOnScrollListener(new C24056());
        this.bottomLayout = new FrameLayout(context);
        this.bottomLayout.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(Theme.getColor(Theme.key_passport_authorizeBackground), Theme.getColor(Theme.key_passport_authorizeBackgroundSelected)));
        frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.setOnClickListener(new C17017());
        this.shareTextView = new TextView(context);
        this.shareTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.shareTextView.setTextColor(Theme.getColor(Theme.key_passport_authorizeText));
        if (this.isImport) {
            this.shareTextView.setText(LocaleController.getString("AddContactChat", R.string.AddContactChat));
        } else {
            this.shareTextView.setText(LocaleController.getString("ContactShare", R.string.ContactShare));
        }
        this.shareTextView.setTextSize(1, 14.0f);
        this.shareTextView.setGravity(17);
        this.shareTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomLayout.addView(this.shareTextView, LayoutHelper.createFrame(-2, -1, 17));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        frameLayout.addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        Drawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setProfile(true);
        avatarDrawable.setInfo(5, this.currentUser.first_name, this.currentUser.last_name, false);
        avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
        TLObject photo = null;
        if (this.currentUser.photo != null) {
            photo = this.currentUser.photo.photo_small;
        }
        this.avatarImage.setImage(photo, "50_50", avatarDrawable);
        this.nameTextView.setText(ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name));
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void setDelegate(PhonebookSelectActivityDelegate phonebookSelectActivityDelegate) {
        this.delegate = phonebookSelectActivityDelegate;
    }

    private void needLayout() {
        int i;
        int i2 = 0;
        if (this.actionBar.getOccupyStatusBar()) {
            i = AndroidUtilities.statusBarHeight;
        } else {
            i = 0;
        }
        int newTop = i + ActionBar.getCurrentActionBarHeight();
        if (this.listView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) newTop);
            }
        }
        if (this.avatarImage != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(diff);
            this.shadowView.setTranslationY((float) (this.extraHeight + newTop));
            this.avatarImage.setScaleX(((18.0f * diff) + 42.0f) / 42.0f);
            this.avatarImage.setScaleY(((18.0f * diff) + 42.0f) / 42.0f);
            if (this.actionBar.getOccupyStatusBar()) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            float avatarY = ((((float) i2) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (1.0f + diff))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * diff);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            this.nameTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
            this.nameTextView.setTranslationY((((float) Math.floor((double) avatarY)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((7.0f * AndroidUtilities.density) * diff))));
            this.nameTextView.setScaleX((0.12f * diff) + 1.0f);
            this.nameTextView.setScaleY((0.12f * diff) + 1.0f);
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new C17028());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[18];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[9] = new ThemeDescription(this.shareTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_passport_authorizeText);
        r9[10] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_passport_authorizeBackground);
        r9[11] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_passport_authorizeBackgroundSelected);
        r9[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareUnchecked);
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareDisabled);
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareBackground);
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, Theme.key_checkboxSquareCheck);
        return r9;
    }
}

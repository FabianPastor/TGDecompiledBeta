package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_themeSettings;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.ActionBar.Theme;

public class DocumentObject {

    public static class ThemeDocument extends TLRPC$TL_document {
        public Theme.ThemeAccent accent;
        public Theme.ThemeInfo baseTheme;
        public TLRPC$TL_themeSettings themeSettings;
        public TLRPC$Document wallpaper;

        public ThemeDocument(TLRPC$TL_themeSettings tLRPC$TL_themeSettings) {
            this.themeSettings = tLRPC$TL_themeSettings;
            Theme.ThemeInfo theme = Theme.getTheme(Theme.getBaseThemeKey(tLRPC$TL_themeSettings));
            this.baseTheme = theme;
            this.accent = theme.createNewAccent(tLRPC$TL_themeSettings);
            TLRPC$WallPaper tLRPC$WallPaper = this.themeSettings.wallpaper;
            if (tLRPC$WallPaper instanceof TLRPC$TL_wallPaper) {
                TLRPC$Document tLRPC$Document = ((TLRPC$TL_wallPaper) tLRPC$WallPaper).document;
                this.wallpaper = tLRPC$Document;
                this.id = tLRPC$Document.id;
                this.access_hash = tLRPC$Document.access_hash;
                this.file_reference = tLRPC$Document.file_reference;
                this.user_id = tLRPC$Document.user_id;
                this.date = tLRPC$Document.date;
                this.file_name = tLRPC$Document.file_name;
                this.mime_type = tLRPC$Document.mime_type;
                this.size = tLRPC$Document.size;
                this.thumbs = tLRPC$Document.thumbs;
                this.version = tLRPC$Document.version;
                this.dc_id = tLRPC$Document.dc_id;
                this.key = tLRPC$Document.key;
                this.iv = tLRPC$Document.iv;
                this.attributes = tLRPC$Document.attributes;
                return;
            }
            this.id = -2147483648L;
            this.dc_id = Integer.MIN_VALUE;
        }
    }
}

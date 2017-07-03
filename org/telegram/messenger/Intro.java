package org.telegram.messenger;

public class Intro {
    public static native void on_draw_frame();

    public static native void on_surface_changed(int i, int i2, float f, int i3, int i4, int i5, int i6, int i7);

    public static native void on_surface_created();

    public static native void set_date(float f);

    public static native void set_date0(float f);

    public static native void set_fast_textures(int i, int i2, int i3, int i4);

    public static native void set_free_textures(int i, int i2);

    public static native void set_ic_textures(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9);

    public static native void set_page(int i);

    public static native void set_pages_textures(int i, int i2, int i3, int i4, int i5, int i6);

    public static native void set_powerful_textures(int i, int i2, int i3, int i4);

    public static native void set_private_textures(int i, int i2);

    public static native void set_scroll_offset(float f);

    public static native void set_telegram_textures(int i, int i2);
}

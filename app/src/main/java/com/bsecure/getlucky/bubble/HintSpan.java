package com.bsecure.getlucky.bubble;

import android.content.res.ColorStateList;
import android.text.style.TextAppearanceSpan;

/**
 * Subclass of TextAppearanceSpan just to work with how Spans get detected
 *
 * Created by Prudhvi Kumar on 9/10/17.
 */
class HintSpan extends TextAppearanceSpan {
    HintSpan(String family, int style, int size, ColorStateList color, ColorStateList linkColor) {
        super(family, style, size, color, linkColor);
    }
}

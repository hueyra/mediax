package com.github.hueyra.mediax.other;

import android.content.Context;
import android.content.ContextWrapper;

import com.github.hueyra.mediax.language.PictureLanguageUtils;

public class PictureContextWrapper extends ContextWrapper {

    public PictureContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context, int language) {
        PictureLanguageUtils.setAppLanguage(context, language);
        return new PictureContextWrapper(context);
    }
}

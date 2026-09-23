package pe.com.dms.inventariosoft.utils.interfaces;

import android.text.Editable;

/**
 * Created by Alvaro Santa Cruz on 03/02/2017.
 */

public interface TextWatcher extends android.text.TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    default void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    default void afterTextChanged(Editable s) {
        onChanged(s.toString());
    }

    void onChanged(String s);
}
package pe.com.dms.inventariosoft.utils.interfaces;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Usuario on 12/10/2017.
 */

public interface SpinnerListener extends AdapterView.OnItemSelectedListener {
    @Override
    default void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onSelected(position);
    }

    @Override
    default void onNothingSelected(AdapterView<?> parent) {

    }

    void onSelected(int pos);
}

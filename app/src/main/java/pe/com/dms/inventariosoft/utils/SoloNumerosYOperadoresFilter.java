package pe.com.dms.inventariosoft.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Filtro que solo permite dígitos, operadores (+ ×) y punto decimal.
 */
public class SoloNumerosYOperadoresFilter implements InputFilter {

    private static final String PERMITIDOS = "0123456789+×*xX.=";

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                                Spanned dest, int dstart, int dend) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (PERMITIDOS.indexOf(c) >= 0) {
                sb.append(c);
            }
        }
        if (sb.length() == end - start) {
            return null;
        }
        return sb.toString();
    }
}

package pe.com.dms.inventariosoft.utils;

import android.app.Dialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager; //Se agrego el WindowManager
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import pe.com.dms.inventariosoft.R;

/**
 * Muestra un teclado de calculadora en un BottomSheet cuando se toca el campo de cantidad.
 */
public class TecladoCalculadoraHelper {

    public static void attach(final EditText etCantidad, final boolean isDecimal) {
        etCantidad.setShowSoftInputOnFocus(false);
        etCantidad.setFocusableInTouchMode(true);
        etCantidad.setCursorVisible(true);
        
        // FORZAMOS TIPO TEXTO para que Android no bloquee los signos + y x
        etCantidad.setInputType(android.text.InputType.TYPE_CLASS_TEXT | 
                               android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        // Filtro dinámico basado en si es decimal o no
        final String permitidos = isDecimal ? "0123456789+×*xX.=" : "0123456789+×*xX=";
        etCantidad.setFilters(new InputFilter[]{ (source, start, end, dest, dstart, dend) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (permitidos.indexOf(c) >= 0) {
                    sb.append(c);
                }
            }
            if (sb.length() == end - start) return null;
            return sb.toString();
        }});

        View.OnClickListener showCalculator = v -> showCalculatorDialog(etCantidad, isDecimal);
        
        etCantidad.setOnClickListener(showCalculator);
        etCantidad.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showCalculatorDialog(etCantidad, isDecimal);
            }
        });
    }

    private static void showCalculatorDialog(final EditText etTarget, final boolean isDecimal) {
        BottomSheetDialog dialog = new BottomSheetDialog(etTarget.getContext());
        View view = LayoutInflater.from(etTarget.getContext()).inflate(R.layout.layout_teclado_calculadora, null);
        dialog.setContentView(view);
        setupButtons(view, etTarget, dialog, isDecimal);

        if(dialog.getWindow() != null){
            //nueva funcion añadida
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.show();
    }

    private static void setupButtons(View root, final EditText etTarget, final Dialog dialog, final boolean isDecimal) {
        int[] idsNumeros = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };
        
        for (int id : idsNumeros) {
            Button btn = root.findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(v -> insertarTexto(etTarget, btn.getText().toString()));
            }
        }

        View btnSuma = root.findViewById(R.id.btnSuma);
        if (btnSuma != null) btnSuma.setOnClickListener(v -> insertarTexto(etTarget, "+"));
        
        View btnMultiplicacion = root.findViewById(R.id.btnMultiplicacion);
        if (btnMultiplicacion != null) btnMultiplicacion.setOnClickListener(v -> insertarTexto(etTarget, "×"));

        View btnPunto = root.findViewById(R.id.btnPunto);
        if (btnPunto != null) {
            if (isDecimal) {
                btnPunto.setVisibility(View.VISIBLE);
                btnPunto.setOnClickListener(v -> insertarTexto(etTarget, "."));
            } else {
                btnPunto.setVisibility(View.INVISIBLE);
            }
        }

        View btnBorrar = root.findViewById(R.id.btnBorrar);
        if (btnBorrar != null) btnBorrar.setOnClickListener(v -> borrarUltimoCaracter(etTarget));
        
        View btnLimpiar = root.findViewById(R.id.btnLimpiar);
        if (btnLimpiar != null) btnLimpiar.setOnClickListener(v -> etTarget.setText(""));

        View btnIgual = root.findViewById(R.id.btnIgual);
        if (btnIgual != null) btnIgual.setOnClickListener(v -> {
            evaluarYReemplazar(etTarget, etTarget.getText().toString(), isDecimal);
            dialog.dismiss();
        });
    }

    private static void insertarTexto(EditText et, String texto) {
        int inicio = Math.max(et.getSelectionStart(), 0);
        int fin = Math.max(et.getSelectionEnd(), 0);
        et.getText().replace(Math.min(inicio, fin), Math.max(inicio, fin), texto);
    }

    private static void borrarUltimoCaracter(EditText et) {
        Editable texto = et.getText();
        int cursor = et.getSelectionStart();
        if (cursor > 0) {
            texto.delete(cursor - 1, cursor);
        }
    }

    private static void evaluarYReemplazar(EditText et, String expresion, boolean isDecimal) {
        String texto = expresion.trim();
        if (texto.isEmpty()) return;

        if (texto.matches("^-?\\d+(\\.\\d+)?$")) return;

        try {
            double resultado = ExpressionEvaluator.evaluar(texto);
            if (resultado < 0) resultado = 0;

            String formateado;
            if (!isDecimal) {
                formateado = String.valueOf((long) Math.round(resultado));
            } else {
                formateado = (resultado == Math.floor(resultado))
                        ? String.valueOf((long) resultado)
                        : String.valueOf(Math.round(resultado * 100.0) / 100.0);
            }

            et.setText(formateado);
            et.setSelection(formateado.length());
        } catch (Exception e) {
            Toast.makeText(et.getContext(), "Expresión inválida", Toast.LENGTH_SHORT).show();
        }
    }
}

package pe.com.dms.inventariosoft.utils;

/**
 * Evalúa expresiones simples tipo "5x5+2" respetando precedencia de operadores.
 * Gramática:
 *   expresion := termino (('+' | '-') termino)*
 *   termino   := factor (('*' | '/') factor)*
 *   factor    := ['-'] numero
 */
public class ExpressionEvaluator {

    public static double evaluar(String expresionOriginal) {
        String expr = normalizar(expresionOriginal);
        if (expr.isEmpty()) {
            throw new IllegalArgumentException("Expresión vacía");
        }
        Parser parser = new Parser(expr);
        double resultado = parser.parseExpresion();
        if (!parser.finalizado()) {
            throw new IllegalArgumentException("Expresión inválida: " + expresionOriginal);
        }
        return resultado;
    }

    private static String normalizar(String expresion) {
        return expresion
                .replace(" ", "")
                .replace("x", "*")
                .replace("X", "*")
                .replace("×", "*") // Multiplicación visual
                .replace("÷", "/") // División visual
                .replace("−", "-") // Resta visual (guion largo)
                .replace(",", "."); // por si alguien escribe coma decimal
    }

    private static class Parser {
        private final String expr;
        private int pos = 0;

        Parser(String expr) { this.expr = expr; }

        boolean finalizado() { return pos >= expr.length(); }

        double parseExpresion() {
            double valor = parseTermino();
            while (!finalizado() && (peek() == '+' || peek() == '-')) {
                char op = next();
                double siguiente = parseTermino();
                valor = (op == '+') ? valor + siguiente : valor - siguiente;
            }
            return valor;
        }

        double parseTermino() {
            double valor = parseFactor();
            while (!finalizado() && (peek() == '*' || peek() == '/')) {
                char op = next();
                double siguiente = parseFactor();
                if (op == '*') {
                    valor *= siguiente;
                } else {
                    if (siguiente == 0) {
                        throw new ArithmeticException("División entre cero");
                    }
                    valor /= siguiente;
                }
            }
            return valor;
        }

        double parseFactor() {
            boolean negativo = false;
            if (!finalizado() && peek() == '-') {
                negativo = true;
                next();
            } else if (!finalizado() && peek() == '+') {
                next();
            }
            double numero = parseNumero();
            return negativo ? -numero : numero;
        }

        double parseNumero() {
            int inicio = pos;
            while (!finalizado() && (Character.isDigit(peek()) || peek() == '.')) {
                pos++;
            }
            if (inicio == pos) {
                throw new IllegalArgumentException("Se esperaba un número en la posición " + pos);
            }
            return Double.parseDouble(expr.substring(inicio, pos));
        }

        char peek() { return expr.charAt(pos); }
        char next() { return expr.charAt(pos++); }
    }
}

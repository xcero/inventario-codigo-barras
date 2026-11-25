package io.github.xcero.inventario.persistencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lector y ayudante de JSON minimo -- HERRAMIENTA, no ejercicio (como Code128Patrones).
 *
 * Java no trae JSON incluido; los proyectos reales usan una libreria (Gson, Jackson). Esta clase hace lo
 * justo para el lab, sin descargar nada:
 *
 *   JsonSimple.leer(texto)  -> convierte texto JSON en objetos de Java:
 *        objeto {...}   -> Map<String, Object>   (las llaves en el mismo orden del archivo)
 *        lista  [...]   -> List<Object>
 *        "texto"        -> String
 *        12 / 3.5       -> Double  (todos los numeros; usa ((Number) x).intValue() si necesitas int)
 *        true / false   -> Boolean
 *        null           -> null
 *
 *   JsonSimple.texto("Hackers \"Blu Ray\"")  -> "\"Hackers \\\"Blu Ray\\\"\""
 *        pone comillas y escapa lo necesario. Usalo SIEMPRE que escribas un String en JSON: un nombre con
 *        comillas o con \ rompe el archivo si lo pegas tal cual.
 *
 * Como lee (por si te da curiosidad): "descenso recursivo". Mira el primer caracter y decide: '{' es objeto,
 * '[' es lista, '"' es texto, etc. Un objeto contiene valores, y para leer cada valor se llama otra vez al
 * mismo metodo (recursion). Es la misma idea con la que funcionan los compiladores.
 */
public final class JsonSimple {

    private final String s;
    private int i;

    private JsonSimple(String s) {
        this.s = s;
    }

    /** Lee un texto JSON completo. Lanza IllegalArgumentException si el JSON esta mal formado. */
    public static Object leer(String json) {
        JsonSimple p = new JsonSimple(json);
        p.espacios();
        Object valor = p.valor();
        p.espacios();
        if (p.i != json.length()) throw p.error("sobra texto despues del JSON");
        return valor;
    }

    /** Un String listo para JSON: con comillas y con los caracteres especiales escapados. null -> null. */
    public static String texto(String valor) {
        if (valor == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : valor.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.append('"').toString();
    }

    // ----------------------------------------------------------------------------------------------

    private Object valor() {
        if (i >= s.length()) throw error("se esperaba un valor");
        char c = s.charAt(i);
        if (c == '{') return objeto();
        if (c == '[') return lista();
        if (c == '"') return cadena();
        if (c == '-' || Character.isDigit(c)) return numero();
        if (s.startsWith("true", i)) { i += 4; return Boolean.TRUE; }
        if (s.startsWith("false", i)) { i += 5; return Boolean.FALSE; }
        if (s.startsWith("null", i)) { i += 4; return null; }
        throw error("valor inesperado '" + c + "'");
    }

    private Map<String, Object> objeto() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        i++; // {
        espacios();
        if (siguiente('}')) return mapa;
        while (true) {
            espacios();
            if (i >= s.length() || s.charAt(i) != '"') throw error("se esperaba el nombre de un campo entre comillas");
            String llave = cadena();
            espacios();
            esperar(':');
            espacios();
            mapa.put(llave, valor());
            espacios();
            if (siguiente('}')) return mapa;
            esperar(',');
        }
    }

    private List<Object> lista() {
        List<Object> lista = new ArrayList<>();
        i++; // [
        espacios();
        if (siguiente(']')) return lista;
        while (true) {
            espacios();
            lista.add(valor());
            espacios();
            if (siguiente(']')) return lista;
            esperar(',');
        }
    }

    private String cadena() {
        StringBuilder sb = new StringBuilder();
        i++; // comilla de apertura
        while (i < s.length()) {
            char c = s.charAt(i++);
            if (c == '"') return sb.toString();
            if (c != '\\') { sb.append(c); continue; }
            if (i >= s.length()) break;
            char e = s.charAt(i++);
            switch (e) {
                case '"', '\\', '/' -> sb.append(e);
                case 'n' -> sb.append('\n');
                case 'r' -> sb.append('\r');
                case 't' -> sb.append('\t');
                case 'b' -> sb.append('\b');
                case 'f' -> sb.append('\f');
                case 'u' -> {
                    if (i + 4 > s.length()) throw error("\\u incompleto");
                    sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                    i += 4;
                }
                default -> throw error("escape desconocido \\" + e);
            }
        }
        throw error("texto sin comilla de cierre");
    }

    private Double numero() {
        int inicio = i;
        if (s.charAt(i) == '-') i++;
        while (i < s.length() && "0123456789.eE+-".indexOf(s.charAt(i)) >= 0) i++;
        try {
            return Double.parseDouble(s.substring(inicio, i));
        } catch (NumberFormatException ex) {
            throw error("numero invalido");
        }
    }

    private void espacios() {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
    }

    private boolean siguiente(char c) {
        if (i < s.length() && s.charAt(i) == c) { i++; return true; }
        return false;
    }

    private void esperar(char c) {
        if (!siguiente(c)) throw error("se esperaba '" + c + "'");
    }

    private IllegalArgumentException error(String mensaje) {
        return new IllegalArgumentException("JSON invalido en la posicion " + i + ": " + mensaje);
    }
}

package io.github.xcero.inventario.codigobarras;

/**
 * Tabla de barras de Code 128 -- DATOS, no ejercicio.
 *
 * Cada simbolo de Code 128 son 3 barras y 3 espacios (el de parada, 4 barras y 3 espacios). Cada
 * digito del patron es el ANCHO de un elemento en "modulos" (la barra mas delgada = 1 modulo),
 * empezando siempre por una barra negra y alternando: barra, espacio, barra, espacio...
 *
 *   Ejemplo, el valor 104 (Inicio B) = "211214":
 *     barra 2 | espacio 1 | barra 1 | espacio 2 | barra 1 | espacio 4   -> 11 modulos en total
 *
 * Indice del arreglo = valor del simbolo (0..106).
 */
public final class Code128Patrones {

    private Code128Patrones() { }  // solo metodos static: nadie necesita crear un objeto

    public static final int INICIO_B = 104;
    public static final int INICIO_C = 105;
    public static final int PARADA = 106;

    private static final String[] PATRONES = {
        "212222", "222122", "222221", "121223", "121322", "131222", "122213", "122312", "132212", "221213",
        "221312", "231212", "112232", "122132", "122231", "113222", "123122", "123221", "223211", "221132",
        "221231", "213212", "223112", "312131", "311222", "321122", "321221", "312212", "322112", "322211",
        "212123", "212321", "232121", "111323", "131123", "131321", "112313", "132113", "132311", "211313",
        "231113", "231311", "112133", "112331", "132131", "113123", "113321", "133121", "313121", "211331",
        "231131", "213113", "213311", "213131", "311123", "311321", "331121", "312113", "312311", "332111",
        "314111", "221411", "431111", "111224", "111422", "121124", "121421", "141122", "141221", "112214",
        "112412", "122114", "122411", "142112", "142211", "241211", "221114", "413111", "241112", "134111",
        "111242", "121142", "121241", "114212", "124112", "124211", "411212", "421112", "421211", "212141",
        "214121", "412121", "111143", "111341", "131141", "114113", "114311", "411113", "411311", "113141",
        "114131", "311141", "411131", "211412", "211214", "211232", "2331112",
    };

    /** Los anchos (1..4) de barras y espacios de un simbolo. */
    public static int[] anchos(int valor) {
        String patron = PATRONES[valor];
        int[] anchos = new int[patron.length()];
        for (int i = 0; i < patron.length(); i++) {
            anchos[i] = patron.charAt(i) - '0';   // el caracter '3' menos el caracter '0' = el numero 3
        }
        return anchos;
    }

    /**
     * Convierte el texto que devuelve {@link Code128#codeIt(String)} (pensado para la FUENTE Code 128 de
     * grandzebu) a los valores de simbolo 0..106 que se dibujan. Regla de esa fuente:
     * caracter menor que 127 -> valor = caracter - 32; si no -> valor = caracter - 105.
     */
    public static int[] valoresDesdeFuente(String codificado) {
        int[] valores = new int[codificado.length()];
        for (int i = 0; i < codificado.length(); i++) {
            char c = codificado.charAt(i);
            valores[i] = c < 127 ? c - 32 : c - 105;
        }
        return valores;
    }
}

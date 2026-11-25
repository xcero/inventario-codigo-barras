package io.github.xcero.inventario.codigobarras;

/**
 * EAN-13: el codigo de 13 digitos impreso en casi todo producto comercial (la libra de cafe, una
 * bebida...). El ultimo digito es de CONTROL: se calcula con los otros 12, asi el lector detecta un
 * codigo mal leido. Validarlo antes de buscar en el inventario evita "producto no encontrado" por un
 * digito equivocado.
 *
 * =================== LAB: Ejercicio 7 (reto) ===================
 * Algoritmo con los 12 primeros digitos, contando posiciones desde 1 a la izquierda:
 *   1. suma los digitos en posicion IMPAR (1, 3, 5 ... 11)
 *   2. suma los digitos en posicion PAR   (2, 4, 6 ... 12) y multiplica esa suma por 3
 *   3. total = paso1 + paso2
 *   4. digito de control = (10 - (total % 10)) % 10
 *
 * Ejemplo: 400638133393 -> digito 1, asi que el codigo completo es 4006381333931.
 * Pista: el caracter '7' a numero es  c - '0'  (ya lo usa Code128Patrones.anchos).
 */
public final class Ean13 {

    private Ean13() { }

    /** Digito de control para los 12 primeros digitos. */
    public static int digitoControl(String doceDigitos) {
        throw new UnsupportedOperationException("Ejercicio 7: calcula el digito de control EAN-13");
    }

    /** true si son exactamente 13 digitos y el ultimo coincide con digitoControl(primeros 12). */
    public static boolean esValido(String codigo) {
        throw new UnsupportedOperationException("Ejercicio 7: valida el codigo EAN-13");
    }
}

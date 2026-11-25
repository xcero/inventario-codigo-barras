package io.github.xcero.inventario;

import io.github.xcero.inventario.codigobarras.Code128;
import io.github.xcero.inventario.codigobarras.Code128Patrones;
import io.github.xcero.inventario.codigobarras.Ean13;
import io.github.xcero.inventario.codigobarras.PanelCodigoBarras;
import io.github.xcero.inventario.modelo.DVD;
import io.github.xcero.inventario.modelo.Product;
import io.github.xcero.inventario.servicio.Inventory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Revisa los ejercicios del LAB.md. Correlo despues de cada cambio:
 *   [OK]        el ejercicio esta bien
 *   [FALLA]     ya lo empezaste, pero algo no da lo esperado (lee el detalle)
 *   [PENDIENTE] todavia no lo escribes
 *
 * No necesitas modificar este archivo. Si un caso no te queda claro, leelo: cada prueba es corta.
 */
public class Verificar {

    private static int ok, fallas, pendientes;

    public static void main(String[] args) {
        System.out.println("=== Verificador del lab de inventario ===\n");

        caso("1. Tu primer package: reportes.ReporteInventario.resumen(...)", Verificar::ejercicio1);
        caso("2. Polimorfismo: el DVD cobra 5% extra en el valor del inventario", Verificar::ejercicio2);
        caso("3a. No se aceptan dos productos con el mismo ID", Verificar::ejercicio3a);
        caso("3b. Cantidad o precio negativos quedan en 0", Verificar::ejercicio3b);
        caso("4. Code128: digito de control correcto (texto)", () -> ejercicio4("Hola", new int[] {104, 40, 79, 76, 65, 69, 106}));
        caso("4. Code128: digito de control correcto (numeros, tabla C)", () -> ejercicio4("123456", new int[] {105, 12, 34, 56, 44, 106}));
        caso("5a. Buscar producto por codigo de barras", Verificar::ejercicio5a);
        caso("5b. Entrada y salida de stock por codigo de barras", Verificar::ejercicio5b);
        caso("6. Dibujo de las barras (comparado contra la tabla de Code 128)", Verificar::ejercicio6);
        caso("7. Reto: digito de control EAN-13", Verificar::ejercicio7);

        System.out.printf("%nResultado: %d OK, %d con falla, %d pendientes%n", ok, fallas, pendientes);
        if (fallas == 0 && pendientes == 0) {
            System.out.println("Todo listo. Sube tus cambios a GitHub (ver LAB.md, paso final).");
        }
    }

    // ------------------------------------------------------------------------------------------

    private static void ejercicio1() throws Exception {
        Class<?> clase;
        try {
            clase = Class.forName("io.github.xcero.inventario.reportes.ReporteInventario");
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("no existe la clase reportes.ReporteInventario");
        }
        Method resumen = clase.getMethod("resumen", Inventory.class);
        Inventory inv = new Inventory();
        inv.addProduct(new Product(1, "Cafe", 2, 10));
        inv.addProduct(new Product(2, "Azucar", 1, 5));
        String texto = (String) resumen.invoke(null, inv);
        esperar(texto != null && texto.contains("2 productos") && texto.contains("25"),
                "resumen debe decir \"2 productos\" y el valor 25.00; dio: " + texto);
    }

    private static void ejercicio2() {
        Inventory inv = new Inventory();
        inv.addProduct(new DVD(1, "Pelicula", 1, 100));
        double valor = inv.calculateInventoryValue();
        esperar(Math.abs(valor - 105.0) < 0.001,
                "un DVD de $100 debe valer $105 en el inventario; dio $" + valor
                + " (pista: Inventory no esta usando el calculateInventoryValue() de cada producto)");
    }

    private static void ejercicio3a() {
        Inventory inv = new Inventory();
        inv.addProduct(new Product(1, "Cafe", 1, 1));
        inv.addProduct(new Product(1, "Otro con el mismo ID", 1, 1));
        int cantidad = inv.getAllProducts().size();
        esperar(cantidad == 1, "se agregaron " + cantidad + " productos con ID 1; debe quedar 1");
    }

    private static void ejercicio3b() {
        Product p = new Product(1, "Cafe", -5, -3);
        esperar(p.getQuantity() == 0 && p.getPrice() == 0,
                "new Product(1, \"Cafe\", -5, -3) quedo con cantidad " + p.getQuantity() + " y precio " + p.getPrice());
        p.setQuantity(-1);
        esperar(p.getQuantity() == 0, "setQuantity(-1) dejo la cantidad en " + p.getQuantity());
    }

    private static void ejercicio4(String texto, int[] esperados) {
        int[] valores = Code128Patrones.valoresDesdeFuente(Code128.codeIt(texto));
        esperar(java.util.Arrays.equals(valores, esperados),
                "\"" + texto + "\" deberia codificar " + java.util.Arrays.toString(esperados)
                + " y dio " + java.util.Arrays.toString(valores)
                + " (pista: la fuente de grandzebu usa 105, no 100, para los caracteres >= 127)");
    }

    private static void ejercicio5a() {
        Inventory inv = inventarioConCodigos();
        Product p = inv.buscarPorCodigoBarras("7412000000011");
        esperar(p != null && p.getId() == 1, "no encontro el producto 1 por su codigo 7412000000011");
        esperar(inv.buscarPorCodigoBarras(new String("7412000000011")) != null,
                "con un String nuevo no lo encuentra: compara con equals(), no con ==");
        esperar(inv.buscarPorCodigoBarras("0000") == null, "un codigo que no existe debe devolver null");
    }

    private static void ejercicio5b() {
        Inventory inv = inventarioConCodigos();
        esperar(inv.registrarMovimiento("7412000000011", 5), "una entrada de 5 debe devolver true");
        esperar(inv.getProductById(1).getQuantity() == 15, "despues de entrar 5 deben ser 15");
        esperar(inv.registrarMovimiento("7412000000011", -15), "sacar todo (15) es valido");
        esperar(!inv.registrarMovimiento("7412000000011", -1), "no se puede sacar mas de lo que hay");
        esperar(inv.getProductById(1).getQuantity() == 0, "el stock no puede quedar negativo");
        esperar(!inv.registrarMovimiento("no-existe", 1), "un codigo inexistente debe devolver false");
    }

    private static void ejercicio6() throws Exception {
        String texto = "CAFE-001";
        File png = new PanelCodigoBarras(texto).guardarPng(System.getProperty("java.io.tmpdir") + File.separator + "verificar-codigo.png");
        BufferedImage img = ImageIO.read(png);
        int y = 10 + PanelCodigoBarras.ALTO_BARRAS / 2;          // una linea a media altura de las barras
        List<Integer> anchos = new ArrayList<>();                 // anchos de barra/espacio, desde la 1a barra
        int x = 0;
        while (x < img.getWidth() && !esNegro(img.getRGB(x, y))) x++;
        if (x >= img.getWidth()) throw new UnsupportedOperationException("no hay barras negras dibujadas");
        int ultimaBarra = img.getWidth() - 1;
        while (ultimaBarra > x && !esNegro(img.getRGB(ultimaBarra, y))) ultimaBarra--;
        while (x <= ultimaBarra) {
            boolean negro = esNegro(img.getRGB(x, y));
            int inicio = x;
            while (x <= ultimaBarra && esNegro(img.getRGB(x, y)) == negro) x++;
            anchos.add(x - inicio);
        }
        List<Integer> esperado = new ArrayList<>();
        for (int valor : Code128Patrones.valoresDesdeFuente(Code128.codeIt(texto))) {
            for (int a : Code128Patrones.anchos(valor)) esperado.add(a * PanelCodigoBarras.MODULO);
        }
        esperar(anchos.equals(esperado), "las barras no coinciden con la tabla. Esperado (px): " + esperado
                + "\n             dibujado (px): " + anchos);
        esperar(primeraBarra(img, y) >= PanelCodigoBarras.MARGEN, "falta el margen blanco a la izquierda");
    }

    private static void ejercicio7() {
        esperar(Ean13.digitoControl("400638133393") == 1, "400638133393 -> el digito debe ser 1");
        esperar(Ean13.digitoControl("750103131130") == 9, "750103131130 -> el digito debe ser 9");
        esperar(Ean13.esValido("4006381333931"), "4006381333931 es valido");
        esperar(!Ean13.esValido("4006381333932"), "4006381333932 NO es valido (ultimo digito cambiado)");
        esperar(!Ean13.esValido("12345"), "un codigo que no tiene 13 digitos no es valido");
    }

    // ------------------------------------------------------------------------------------------

    private static Inventory inventarioConCodigos() {
        Inventory inv = new Inventory();
        Product cafe = new Product(1, "Cafe 1 lb", 10, 6.5);
        cafe.setCodigoBarras("7412000000011");
        Product azucar = new Product(2, "Azucar 1 lb", 4, 1.1);
        azucar.setCodigoBarras("CAFE-001");
        inv.addProduct(cafe);
        inv.addProduct(azucar);
        return inv;
    }

    private static boolean esNegro(int rgb) {
        int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
        return r < 80 && g < 80 && b < 80;
    }

    private static int primeraBarra(BufferedImage img, int y) {
        int x = 0;
        while (x < img.getWidth() && !esNegro(img.getRGB(x, y))) x++;
        return x;
    }

    private interface Prueba { void correr() throws Exception; }

    private static void esperar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static void caso(String nombre, Prueba prueba) {
        try {
            prueba.correr();
            ok++;
            System.out.println("[OK]        " + nombre);
        } catch (UnsupportedOperationException e) {
            pendientes++;
            System.out.println("[PENDIENTE] " + nombre + (e.getMessage() != null ? "  (" + e.getMessage() + ")" : ""));
        } catch (AssertionError e) {
            fallas++;
            System.out.println("[FALLA]     " + nombre + "\n             " + e.getMessage());
        } catch (Throwable e) {
            Throwable causa = e.getCause() != null ? e.getCause() : e;
            if (causa instanceof UnsupportedOperationException) {
                pendientes++;
                System.out.println("[PENDIENTE] " + nombre + "  (" + causa.getMessage() + ")");
            } else {
                fallas++;
                System.out.println("[FALLA]     " + nombre + "\n             " + causa);
            }
        }
    }
}

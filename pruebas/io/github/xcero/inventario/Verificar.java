package io.github.xcero.inventario;

import io.github.xcero.inventario.codigobarras.Code128;
import io.github.xcero.inventario.codigobarras.Code128Patrones;
import io.github.xcero.inventario.codigobarras.Ean13;
import io.github.xcero.inventario.codigobarras.PanelCodigoBarras;
import io.github.xcero.inventario.modelo.DVD;
import io.github.xcero.inventario.modelo.Product;
import io.github.xcero.inventario.persistencia.AlmacenJson;
import io.github.xcero.inventario.persistencia.JsonSimple;
import io.github.xcero.inventario.servicio.Inventory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        caso("8. Guardar el inventario en JSON", Verificar::ejercicio8);
        caso("9. Cargar el inventario desde JSON (con su tipo: CD/DVD)", Verificar::ejercicio9);
        caso("10a. Consulta: buscar por nombre", Verificar::ejercicio10a);
        caso("10b. Consulta: stock bajo", Verificar::ejercicio10b);
        caso("10c. Consulta: ordenados por valor", Verificar::ejercicio10c);

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

    @SuppressWarnings("unchecked")
    private static void ejercicio8() throws Exception {
        Inventory inv = new Inventory();
        Product cafe = new Product(1, "Cafe \"Lempa\" 1 lb", 10, 6.5);
        cafe.setCodigoBarras("7412000000011");
        inv.addProduct(cafe);
        inv.addProduct(new DVD(2, "Hackers", 3, 19));
        Path ruta = Files.createTempFile("inventario", ".json");
        AlmacenJson.guardar(inv, ruta);
        String texto = Files.readString(ruta, StandardCharsets.UTF_8);
        Object raiz;
        try {
            raiz = JsonSimple.leer(texto);
        } catch (IllegalArgumentException e) {
            throw new AssertionError("el archivo no es JSON valido: " + e.getMessage() + "\n             contenido: " + texto);
        }
        esperar(raiz instanceof Map, "la raiz debe ser un objeto { \"productos\": [...] }");
        Object lista = ((Map<String, Object>) raiz).get("productos");
        esperar(lista instanceof List && ((List<Object>) lista).size() == 2, "\"productos\" debe ser una lista con 2 productos");
        Map<String, Object> p1 = (Map<String, Object>) ((List<Object>) lista).get(0);
        Map<String, Object> p2 = (Map<String, Object>) ((List<Object>) lista).get(1);
        esperar("Cafe \"Lempa\" 1 lb".equals(p1.get("nombre")), "el nombre con comillas se perdio o rompio: " + p1.get("nombre") + " (usa JsonSimple.texto)");
        esperar("7412000000011".equals(p1.get("codigoBarras")), "falta codigoBarras (como texto) en el primer producto");
        esperar(p1.get("precio") instanceof Number && Math.abs(((Number) p1.get("precio")).doubleValue() - 6.5) < 1e-9, "precio debe ser 6.5; dio " + p1.get("precio"));
        esperar(p1.get("id") instanceof Number && ((Number) p1.get("id")).intValue() == 1
                && p1.get("cantidad") instanceof Number && ((Number) p1.get("cantidad")).intValue() == 10, "id/cantidad del primer producto");
        esperar("DVD".equals(p2.get("tipo")), "el segundo producto debe tener \"tipo\": \"DVD\"; dio " + p2.get("tipo"));
        esperar(p2.containsKey("codigoBarras") && p2.get("codigoBarras") == null, "sin codigo de barras, escribe \"codigoBarras\": null");
    }

    private static void ejercicio9() throws Exception {
        String json = """
            {
              "productos": [
                { "tipo": "Product", "id": 1, "nombre": "Cafe \\"Lempa\\" 1 lb", "cantidad": 10, "precio": 6.5, "codigoBarras": "7412000000011" },
                { "tipo": "DVD", "id": 2, "nombre": "Hackers", "cantidad": 1, "precio": 100, "codigoBarras": null },
                { "tipo": "CD", "id": 3, "nombre": "Musica", "cantidad": 2, "precio": 5, "codigoBarras": null }
              ]
            }
            """;
        Path ruta = Files.createTempFile("inventario", ".json");
        Files.writeString(ruta, json, StandardCharsets.UTF_8);
        Inventory inv = AlmacenJson.cargar(ruta);
        esperar(inv != null && inv.getAllProducts().size() == 3, "debe cargar 3 productos");
        Product cafe = inv.getProductById(1);
        esperar(cafe != null && "Cafe \"Lempa\" 1 lb".equals(cafe.getName()), "el nombre del producto 1 no coincide: " + (cafe == null ? null : cafe.getName()));
        esperar(cafe.getQuantity() == 10 && Math.abs(cafe.getPrice() - 6.5) < 1e-9, "cantidad/precio del producto 1");
        esperar("7412000000011".equals(cafe.getCodigoBarras()), "no se cargo el codigo de barras");
        esperar(inv.getProductById(2) instanceof DVD, "el producto 2 debe volver a ser un DVD (usa \"tipo\")");
        esperar(inv.getProductById(3) instanceof io.github.xcero.inventario.modelo.CD, "el producto 3 debe volver a ser un CD");
        Path noExiste = ruta.resolveSibling("no-existe-" + System.nanoTime() + ".json");
        Inventory vacio = AlmacenJson.cargar(noExiste);
        esperar(vacio != null && vacio.getAllProducts().isEmpty(), "si el archivo no existe, debe devolver un inventario vacio");
    }

    private static Inventory inventarioConsultas() {
        Inventory inv = new Inventory();
        inv.addProduct(new Product(1, "Cafe 1 lb", 10, 6.5));    // valor 65
        inv.addProduct(new Product(2, "Azucar", 2, 1.1));       // valor 2.2
        inv.addProduct(new Product(3, "CAFE molido", 1, 50));   // valor 50
        inv.addProduct(new Product(4, "Frijol", 5, 1));         // valor 5
        return inv;
    }

    private static List<Integer> ids(List<Product> lista) {
        List<Integer> r = new ArrayList<>();
        for (Product p : lista) r.add(p.getId());
        return r;
    }

    private static void ejercicio10a() {
        Inventory inv = inventarioConsultas();
        List<Integer> r = ids(inv.buscarPorNombre("cafe"));
        esperar(r.equals(List.of(1, 3)), "buscar \"cafe\" debe dar [1, 3] (sin importar mayusculas); dio " + r);
        esperar(inv.buscarPorNombre("xyz").isEmpty(), "sin resultados debe devolver una lista vacia, no null");
    }

    private static void ejercicio10b() {
        List<Integer> r = ids(inventarioConsultas().stockBajo(2));
        esperar(r.equals(List.of(2, 3)), "stockBajo(2) debe dar [2, 3] (cantidad <= 2); dio " + r);
    }

    private static void ejercicio10c() {
        Inventory inv = inventarioConsultas();
        List<Integer> r = ids(inv.ordenadosPorValor());
        esperar(r.equals(List.of(1, 3, 4, 2)), "ordenados por valor (mayor a menor) debe dar [1, 3, 4, 2]; dio " + r);
        esperar(ids(inv.getAllProducts()).equals(List.of(1, 2, 3, 4)), "ordenar no debe cambiar el orden del inventario original");
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

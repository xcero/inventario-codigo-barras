package io.github.xcero.inventario.persistencia;

import io.github.xcero.inventario.servicio.Inventory;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Guarda y carga el inventario en un archivo JSON, para que los productos no se pierdan al cerrar el programa.
 *
 * Formato que deben producir/leer los ejercicios 8 y 9 (un objeto con la lista de productos):
 *
 *   {
 *     "productos": [
 *       { "tipo": "Product", "id": 1, "nombre": "Cafe 1 lb", "cantidad": 10, "precio": 6.5, "codigoBarras": "7412000000011" },
 *       { "tipo": "DVD",     "id": 2, "nombre": "Hackers",   "cantidad": 3,  "precio": 19.0, "codigoBarras": null }
 *     ]
 *   }
 *
 * "tipo" es CLAVE: sin el, al cargar no sabrias si crear un Product, un CD o un DVD, y el DVD perderia su 5%
 * (ejercicio 2). Se obtiene con producto.getClass().getSimpleName().
 *
 * Archivos: java.nio.file.Files.writeString(ruta, texto, StandardCharsets.UTF_8) y Files.readString(ruta, UTF_8).
 */
public final class AlmacenJson {

    private AlmacenJson() { }

    // =================== LAB: Ejercicio 8 -- guardar ===================
    // Arma el texto JSON con un StringBuilder recorriendo inventario.getAllProducts() y escribelo en "ruta".
    // Pistas:
    //  - Los String SIEMPRE con JsonSimple.texto(...): pone comillas y escapa. Los numeros van tal cual.
    //  - Coma ENTRE productos, no despues del ultimo (pregunta "si no es el primero, agrega coma antes").
    //  - Los decimales con punto: String.valueOf(6.5) da "6.5" siempre; String.format("%.2f") puede dar "6,50"
    //    segun el idioma de Windows, y eso NO es JSON valido.
    public static void guardar(Inventory inventario, Path ruta) throws IOException {
        throw new UnsupportedOperationException("Ejercicio 8: guarda el inventario en JSON");
    }

    // =================== LAB: Ejercicio 9 -- cargar ===================
    // Lee el archivo, conviertelo con JsonSimple.leer(...) y reconstruye el inventario.
    // Pistas:
    //  - JsonSimple.leer devuelve Object: haz cast. La raiz es Map<String, Object>; "productos" es List<Object>;
    //    cada producto es Map<String, Object>.
    //  - Los numeros llegan como Double: ((Number) p.get("id")).intValue().
    //  - Segun "tipo" crea new CD(...), new DVD(...) o new Product(...) (un switch sobre el String).
    //  - "codigoBarras" puede venir null: setCodigoBarras((String) p.get("codigoBarras")) funciona igual.
    //  - Si el archivo no existe (Files.exists(ruta) == false) devuelve un inventario vacio, sin error.
    public static Inventory cargar(Path ruta) throws IOException {
        throw new UnsupportedOperationException("Ejercicio 9: carga el inventario desde JSON");
    }
}

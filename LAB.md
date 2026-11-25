# Lab: inventario con código de barras en Java

Parte del proyecto final de **Java Fundamentals** (repo `xcero/Fundamentals`) y lo convierte en un proyecto
propio, ordenado en *packages*, con manejo de stock por código de barras. Tú escribes el código; el
programa `Verificar` te dice qué está bien y qué falta. Los pasos 8 a 11 hacen que el inventario se guarde en un
archivo y se pueda consultar.

```
[OK]        el ejercicio está bien
[FALLA]     ya lo empezaste, pero algo no da lo esperado (lee el detalle)
[PENDIENTE] todavía no lo escribes
```

---

## Paso 0. Abrir y correr

1. En VS Code: **File → Open Folder…** → `inventario-java`. Espera a que la extensión de Java termine de
   cargar (abajo a la izquierda dice *Java: Ready*).
2. **Terminal → Run Task… → Verificar ejercicios**. Debe compilar y mostrar 10 casos, ninguno en OK todavía.
3. Prueba también el menú **Run and Debug** (Ctrl+Shift+D): ahí están *Inventario (consola)*,
   *Inventario (ventana)* y *Ver código de barras*. Con **F5** se corre lo que esté elegido; puedes poner
   *breakpoints* haciendo clic a la izquierda del número de línea.

---

## Paso 1. ¿Qué es un package? (ejercicio 1)

En Fundamentals todo estaba en un solo package, `proyecto`. Aquí cada responsabilidad tiene el suyo:

```
src/io/github/xcero/inventario/
├── modelo/        Product, CD, DVD          -> QUÉ es cada cosa
├── servicio/      Inventory                 -> qué se HACE con ellas
├── codigobarras/  Code128, Ean13, dibujo    -> todo lo del código de barras
└── app/           InventoryTest, ventanas   -> los main que ejecutas
```

Tres reglas que hay que tener claras:

1. **Carpeta = package.** El archivo `src/io/github/xcero/inventario/modelo/Product.java` tiene que empezar
   con `package io.github.xcero.inventario.modelo;`. Si no coinciden, no compila.
2. **El nombre va al revés que un dominio**: `io.github.xcero` porque tu código vive en `xcero.github.io`.
   Así dos personas nunca chocan con una clase `Product`.
3. **Entre packages se importa.** `Inventory` está en `servicio` y usa `Product` de `modelo`, por eso tiene
   `import io.github.xcero.inventario.modelo.Product;`. Al mudar tu código de `proyecto` solo cambiaron la
   línea `package` y esos `import`; además se agregó a `Product` el campo `codigoBarras` y a `Inventory` los
   esqueletos del ejercicio 5.

**Ejercicio 1 — crea tu primer package desde cero.**

1. En el explorador de VS Code, clic derecho en `src/io/github/xcero/inventario` → **New Folder** → `reportes`.
2. Clic derecho en `reportes` → **New File** → `ReporteInventario.java`.
3. Escribe la clase. La primera línea es el `package`; luego el `import` de `Inventory`:

   ```java
   package io.github.xcero.inventario.reportes;

   import io.github.xcero.inventario.servicio.Inventory;

   public class ReporteInventario {
       // static: se llama sin crear un objeto -> ReporteInventario.resumen(miInventario)
       public static String resumen(Inventory inventario) {
           // TODO: devolver algo como "3 productos, valor total $134.00"
       }
   }
   ```

   Pista: `inventario.getAllProducts().size()` y `String.format("%.2f", numero)`.
4. Corre **Verificar ejercicios**. El caso 1 debe pasar a `[OK]`.

**Lo mismo, a mano, en la terminal** (vale la pena hacerlo una vez para entender qué hace VS Code):

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java -Path src, pruebas).FullName
java -cp bin io.github.xcero.inventario.Verificar
```

- `-d bin` → javac crea dentro de `bin` las mismas carpetas del package, con los `.class`.
- `-cp bin` → *classpath*: dónde busca Java las clases.
- Al ejecutar se usa el **nombre completo** de la clase (package + clase), no la ruta del archivo.

---

## Paso 2. Polimorfismo (ejercicio 2)

`DVD` sobrescribe `calculateInventoryValue()` para cobrar 5% extra, pero `Inventory.calculateInventoryValue()`
hace `product.getPrice() * product.getQuantity()` y se salta ese método: el DVD nunca cobra el 5%.

- Cambia `Inventory` para que use el `calculateInventoryValue()` **de cada producto**. Java llama al del DVD
  cuando el objeto es un DVD, aunque la variable sea `Product`: eso es *polimorfismo*.
- En `InventoryTest`, las opciones 5 y 6 del menú (“Add CD” / “Add DVD”) crean un `new Product(...)`. Cámbialas
  para que creen `new CD(...)` y `new DVD(...)` (y agrega los `import` que falten).

---

## Paso 3. Validaciones (ejercicio 3)

- **3a.** `addProduct` acepta dos productos con el mismo ID. Usa `getProductById` para no agregar el repetido.
  (Reto: que devuelva `boolean` y el menú diga “Ese ID ya existe”.)
- **3b.** Cantidad o precio negativos deben quedar en 0 (en una versión anterior de `Product` tenías la idea en
  un método comentado, `setQuantityOnHand`). Ponlo en `setQuantity`/`setPrice` y haz que el **constructor use
  los setters**, así la regla vive en un solo lugar.
- Limpieza: `Product` tiene `getQuantityOnHand()` y `getProductId()` que devuelven `null`/`0` y nadie usa.
  Bórralos, y también el campo `productid`.

---

## Paso 4. Arreglar tu Code128 (ejercicio 4)

El verificador lo muestra: para "Hola" tu `Code128.codeIt` calcula el dígito de control **74**, y el
correcto es **69**. Con un dígito de control malo, el lector (o el teléfono) rechaza el código.

La fuente Code 128 de grandzebu representa los valores 95 a 106 con caracteres **105** posiciones más arriba,
no 100. Busca en `Code128.java` los `100` y piensa cuál debe ser `105` (son tres). Por qué pasaba: los
caracteres de inicio (`209` = Inicio B) se restaban como `209 - 100 = 109`, pero el Inicio B vale `104`.

---

## Paso 5. Stock por código de barras (ejercicio 5)

En la práctica, el lector de código de barras **es un teclado**: escribe el código y presiona Enter. Pasa lo
mismo con un lector USB y con el **teléfono usado como lector** (apps que envían lo escaneado a la PC como si
lo teclearas). Tu programa no necesita nada especial: solo leer una línea y buscarla.

- **5a.** `Inventory.buscarPorCodigoBarras(String)`. Compara con `equals`, nunca con `==` (con `==` comparas
  si son el mismo objeto en memoria, no el mismo texto; el verificador lo prueba).
- **5b.** `Inventory.registrarMovimiento(codigo, cantidad)`: entrada (+) o salida (−). Nunca deja el stock
  negativo.
- **5c (sin verificador).** Agrega al menú de consola la opción **“Escanear”**:
  1. pide una cantidad por defecto (1 = entrada; -1 = salida);
  2. en un ciclo, lee códigos con `scanner.nextLine()` hasta que se escriba `fin`;
  3. por cada código: `registrarMovimiento`; muestra el producto y su nuevo stock, o “código no registrado”.

  Ojo: después de `scanner.nextInt()` queda un Enter pendiente. Haz un `scanner.nextLine()` antes del ciclo
  (tu código ya lo hace en la opción 1, por la misma razón).
- Agrega al alta de producto (opción 1) la pregunta “Código de barras”.

---

## Paso 6. Dibujar el código de barras y leerlo con el teléfono (ejercicio 6)

Es lo mismo que tu clase `Triangulos`: un `JComponent` que pinta en `paintComponent`. Allí usabas
`g.fillPolygon(...)`; aquí cada barra es un rectángulo, `g.fillRect(x, y, ancho, alto)`. El paso a paso está en
el comentario de `PanelCodigoBarras.paintComponent`.

- `Code128Patrones` ya tiene la tabla de barras (son datos del estándar, no hace falta memorizarlos).
- El verificador lee la imagen que generas y la compara **barra por barra** con la tabla.
- La prueba real: corre **Ver código de barras** (F5), apunta la **cámara del teléfono** (o Google Lens) a la
  pantalla y debe leer `CAFE-001`. También queda guardado `codigo-CAFE-001.png`, listo para imprimir.
- Si el teléfono no lo lee: revisa que el ejercicio 4 esté en OK (un dígito de control malo lo invalida) y que
  haya margen blanco a los lados.

Idea para después: un botón en `InventoryTestGUI` que muestre el código del producto elegido. Reemplaza el
viejo “Generar archivo códigos de barra”, que dependía de tener la fuente instalada.

---

## Paso 7. Reto: EAN-13 (ejercicio 7)

Los productos comerciales traen **EAN-13**: 13 dígitos, y el último es de control. Si lo validas antes de
buscar, un código mal leído no termina en “producto no encontrado”. El algoritmo está en `Ean13.java`.
Úsalo en la opción “Escanear”: si el código tiene 13 dígitos y no es válido, avisa “lectura inválida”.

---

## Paso 8. Guardar el inventario en un archivo JSON (ejercicio 8)

Hoy el inventario vive **solo en memoria**: una `ArrayList` dentro de `Inventory`. Al cerrar el programa se
pierde todo, y los 3 productos que ves al abrir están escritos a mano en el `main`. Para que sea consultable de
verdad, primero hay que **guardarlo**.

Se guarda en **JSON**, el formato de texto que usan casi todas las APIs (MIRA incluida):

```json
{
  "productos": [
    { "tipo": "Product", "id": 1, "nombre": "Cafe 1 lb", "cantidad": 10, "precio": 6.5, "codigoBarras": "7412000000011" },
    { "tipo": "DVD",     "id": 2, "nombre": "Hackers",   "cantidad": 3,  "precio": 19.0, "codigoBarras": null }
  ]
}
```

- `{ }` es un **objeto** (pares `"campo": valor`), `[ ]` es una **lista**. Los textos van entre comillas; los
  números, `true`/`false` y `null`, sin comillas.
- **`"tipo"` es la clave del ejercicio**: sin él, al cargar no sabrías si crear un `Product`, un `CD` o un `DVD`,
  y el DVD perdería su 5% (ejercicio 2). Se obtiene con `producto.getClass().getSimpleName()`.

Escribe `AlmacenJson.guardar(inventario, ruta)` (en el package nuevo `persistencia`):

1. Arma el texto con un `StringBuilder`, recorriendo `inventario.getAllProducts()`.
2. **Todo `String` va con `JsonSimple.texto(...)`**: le pone comillas y escapa lo especial. Un nombre como
   `Cafe "Lempa"` rompe el archivo si lo pegas tal cual (el verificador lo prueba).
3. Coma **entre** productos, no después del último.
4. Decimales con punto: `String.valueOf(6.5)` → `6.5`. `String.format("%.2f", ...)` puede dar `6,50` según el
   idioma de Windows, y eso no es JSON válido.
5. Escribe el archivo con `Files.writeString(ruta, texto, StandardCharsets.UTF_8)`.

## Paso 9. Cargar el inventario desde JSON (ejercicio 9)

Leer JSON es mucho más difícil que escribirlo, así que el lector ya está hecho: `JsonSimple.leer(texto)`
(léelo si tienes curiosidad, está comentado). Convierte el texto en objetos de Java:

| JSON | Java |
|---|---|
| `{ ... }` | `Map<String, Object>` |
| `[ ... ]` | `List<Object>` |
| `"texto"` | `String` |
| `12`, `6.5` | `Double` → usa `((Number) x).intValue()` o `.doubleValue()` |
| `null` | `null` |

Escribe `AlmacenJson.cargar(ruta)`:

1. Si el archivo no existe (`Files.exists(ruta)`), devuelve un `Inventory` vacío, sin error.
2. `Files.readString(ruta, StandardCharsets.UTF_8)` → `JsonSimple.leer(...)` → cast a `Map<String, Object>`.
3. Recorre `(List<Object>) raiz.get("productos")`; cada elemento es un `Map<String, Object>`.
4. Con un `switch` sobre `"tipo"` crea `new CD(...)`, `new DVD(...)` o `new Product(...)`, y ponle el código
   de barras.

Concepto nuevo: **cast**. `JsonSimple.leer` devuelve `Object` porque no sabe qué hay en el archivo; tú sí sabes
que la raíz es un objeto, así que le dices a Java "trátalo como `Map`": `(Map<String, Object>) raiz`. Si te
equivocas, Java lanza `ClassCastException`.

## Paso 10. Consultas (ejercicio 10)

Con los datos guardados, el inventario se vuelve útil si puedes **preguntarle cosas**. En `Inventory`:

- **10a.** `buscarPorNombre("cafe")` → los productos cuyo nombre contiene el texto, sin importar mayúsculas.
- **10b.** `stockBajo(2)` → los productos con cantidad ≤ 2: la lista de "hay que reponer".
- **10c.** `ordenadosPorValor()` → todos, del de mayor valor de inventario al menor.

Las tres devuelven una lista **nueva**: nunca la lista interna `products`, porque quien la reciba podría
modificar tu inventario sin pasar por `addProduct`. Si no hay resultados, devuelven una lista vacía y no
`null`. El verificador revisa también que ordenar no cambie el orden del inventario original.

Para 10c investiga `Comparator`: `lista.sort((a, b) -> Double.compare(b.calculateInventoryValue(), a.calculateInventoryValue()))`
ordena de mayor a menor. Después prueba la versión corta con `Comparator.comparingDouble(...).reversed()`.

## Paso 11. Conectarlo al programa (sin verificador)

Ahora haz que el programa use todo lo anterior, en la consola (`InventoryTest`) y después en la ventana:

1. **Al iniciar**: `Inventory inv = AlmacenJson.cargar(Path.of("inventario.json"));`. Si quedó vacío (primera
   vez), agrega los 3 productos de ejemplo.
2. **Después de agregar un producto o registrar un movimiento**: `AlmacenJson.guardar(inv, ruta)`. Así no se
   pierde nada aunque cierres la ventana con la X.
3. **Opciones nuevas del menú**: "Buscar por nombre", "Productos con stock bajo" y "Ranking por valor".
4. Prueba real: agrega un producto, cierra el programa, vuelve a abrirlo y busca ese producto. Abre
   `inventario.json` en VS Code para ver lo que guardaste.

`inventario.json` ya está en `.gitignore`: son datos de tu máquina, no código, así que no se suben a GitHub.

---

## Paso final. Subir a GitHub

Cuando el verificador quede en verde (o cuando quieras guardar el avance):

```powershell
git add -A
git commit -m "Ejercicio X: ..."
git push
```

Un commit por ejercicio deja un historial que se lee como tu progreso.

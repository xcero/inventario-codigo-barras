package io.github.xcero.inventario.servicio;
import io.github.xcero.inventario.modelo.Product;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Product> products; // Hacer products privado

    public Inventory() {
        this.products = new ArrayList<>();
    }

    // Método para imprimir todos los IDs de productos en el inventario
    public void printAllProductIds() {
        System.out.println("IDs actuales en el inventario:");
        for (Product product : products) {
            System.out.println("ID: " + product.getId());
        }
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    // Método para devolver una copia de todos los productos (evita modificaciones externas)
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // Método para buscar un producto por ID
    public Product getProductById(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null; // Retorna null si no encuentra el producto
    }

    // Método para imprimir el inventario
    public void printInventory() {
        if (products.isEmpty()) {
            System.out.println("El inventario está vacío.");
        } else {
            for (Product product : products) {
                System.out.println(product);
            }
        }
    }

    // Método para calcular el valor total del inventario
    public double calculateInventoryValue() {
        double totalValue = 0;
        for (Product product : products) {
            totalValue += product.getPrice() * product.getQuantity();
        }
        return totalValue;
    }

    // ===================== LAB: Ejercicio 10 (consultas) =====================
    // Ya puedes buscar por ID; estas tres hacen el inventario "consultable". Las tres devuelven una lista NUEVA
    // (nunca la lista interna "products"), y una lista vacia -- no null -- si no hay resultados.

    // 10a. Productos cuyo nombre CONTIENE el texto, sin importar mayusculas: "cafe" encuentra "Cafe 1 lb".
    // Pista: nombre.toLowerCase().contains(texto.toLowerCase()).
    public List<Product> buscarPorNombre(String texto) {
        throw new UnsupportedOperationException("Ejercicio 10a: escribe buscarPorNombre");
    }

    // 10b. Productos con cantidad MENOR O IGUAL al minimo: la lista de "hay que reponer".
    public List<Product> stockBajo(int minimo) {
        throw new UnsupportedOperationException("Ejercicio 10b: escribe stockBajo");
    }

    // 10c. Todos los productos ordenados por valor de inventario (calculateInventoryValue), del mayor al menor.
    // Pista: copia la lista y usa lista.sort(...) con un Comparator; o investiga Comparator.comparingDouble.
    public List<Product> ordenadosPorValor() {
        throw new UnsupportedOperationException("Ejercicio 10c: escribe ordenadosPorValor");
    }

    // ===================== LAB: Ejercicio 5 (codigo de barras) =====================
    // Lo que hace un lector de codigo de barras -- o el telefono usado como lector -- es "teclear"
    // el codigo y un Enter. Tu programa solo tiene que buscar ese texto.

    // Devuelve el producto cuyo codigoBarras es igual al texto leido, o null si no existe.
    // Pista: igual que getProductById, pero compara String con equals(), nunca con ==.
    public Product buscarPorCodigoBarras(String codigo) {
        throw new UnsupportedOperationException("Ejercicio 5a: escribe buscarPorCodigoBarras");
    }

    // Entrada (cantidad positiva) o salida (negativa) de stock por codigo de barras.
    // Devuelve true si se aplico. Reglas: codigo inexistente -> false; el stock nunca queda negativo -> false.
    public boolean registrarMovimiento(String codigo, int cantidad) {
        throw new UnsupportedOperationException("Ejercicio 5b: escribe registrarMovimiento");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Product product : products) {
            sb.append(product.toString()).append("\n");
        }
        return sb.toString();
    }
}

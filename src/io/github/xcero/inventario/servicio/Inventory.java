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

package io.github.xcero.inventario.app;

import io.github.xcero.inventario.codigobarras.PanelCodigoBarras;
import java.io.File;
import javax.swing.JFrame;

/**
 * Muestra un codigo de barras en pantalla y lo guarda como PNG. Sirve para probar el Ejercicio 6:
 * apunta la camara del telefono (Google Lens o la app de camara) a la pantalla y debe leer el texto.
 *
 * Uso:  java -cp bin io.github.xcero.inventario.app.VentanaCodigoBarras "CAFE-001"
 */
public class VentanaCodigoBarras {

    public static void main(String[] args) throws Exception {
        String texto = args.length > 0 ? args[0] : "CAFE-001";

        PanelCodigoBarras panel = new PanelCodigoBarras(texto);
        File png = panel.guardarPng("codigo-" + texto.replaceAll("[^A-Za-z0-9-]", "_") + ".png");
        System.out.println("Imagen guardada en " + png.getAbsolutePath());

        JFrame ventana = new JFrame("Codigo de barras: " + texto);
        ventana.add(new PanelCodigoBarras(texto));
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}

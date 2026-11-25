package io.github.xcero.inventario.codigobarras;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * Dibuja un codigo de barras Code 128 -- igual que tu clase Triangulos: un JComponent que pinta en
 * paintComponent. Alli usabas g.fillPolygon(...); aqui cada barra es un rectangulo: g.fillRect(...).
 *
 * Sin la FUENTE de grandzebu: las barras se dibujan a mano, asi que funciona en cualquier PC, se puede
 * guardar como PNG e imprimir, y el telefono la lee con la camara.
 */
public class PanelCodigoBarras extends JComponent {

    /** Ancho en pixeles de 1 modulo (la barra mas delgada). Con menos de 2 el telefono no la lee. */
    public static final int MODULO = 3;
    /** Margen blanco a cada lado: Code 128 exige al menos 10 modulos ("zona quieta"). */
    public static final int MARGEN = 10 * MODULO;
    public static final int ALTO_BARRAS = 120;

    private final String texto;
    private final int[] valores;

    public PanelCodigoBarras(String texto) {
        this.texto = texto;
        String codificado = Code128.codeIt(texto);
        if (codificado == null) {
            throw new IllegalArgumentException("Code 128 solo acepta caracteres ASCII 32..126: " + texto);
        }
        this.valores = Code128Patrones.valoresDesdeFuente(codificado);
        setPreferredSize(new Dimension(anchoTotal(), ALTO_BARRAS + 40));
    }

    /** Cada simbolo mide 11 modulos y el de parada 13; mas los dos margenes. */
    private int anchoTotal() {
        int modulos = 0;
        for (int valor : valores) {
            for (int ancho : Code128Patrones.anchos(valor)) {
                modulos += ancho;
            }
        }
        return modulos * MODULO + 2 * MARGEN;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo blanco: el lector necesita contraste (y el margen blanco a los lados).
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // =================== LAB: Ejercicio 6 -- dibuja las barras ===================
        // Tienes en "valores" los simbolos a dibujar (inicio, datos, digito de control, parada).
        // Para cada valor, Code128Patrones.anchos(valor) te da los anchos: barra, espacio, barra, ...
        //
        //   int x = MARGEN;                                  // empiezas despues del margen
        //   para cada valor en valores:
        //       int[] anchos = Code128Patrones.anchos(valor);
        //       para i = 0 .. anchos.length - 1:
        //           int w = anchos[i] * MODULO;
        //           si i es PAR -> es barra: pinta negro un rectangulo (x, 10, w, ALTO_BARRAS)
        //           (si es impar es espacio: no pintas nada)
        //           x += w;                                  // avanzas SIEMPRE, barra o espacio
        //
        // Pistas: "i es par" -> i % 2 == 0.  Negro -> g.setColor(Color.BLACK).
        // Cuando lo tengas, borra la linea de abajo que escribe "pendiente".
        g.setColor(Color.RED);
        g.drawString("Ejercicio 6 pendiente: dibuja las barras en paintComponent", MARGEN, 40);

        // El texto legible debajo de las barras (esto ya esta hecho).
        g.setColor(Color.BLACK);
        g.drawString(texto, MARGEN, ALTO_BARRAS + 30);
    }

    /** Guarda el codigo como imagen PNG (para imprimir etiquetas o escanear desde otra pantalla). */
    public File guardarPng(String archivo) throws IOException {
        BufferedImage imagen = new BufferedImage(anchoTotal(), ALTO_BARRAS + 40, BufferedImage.TYPE_INT_RGB);
        setSize(imagen.getWidth(), imagen.getHeight());
        Graphics2D g = imagen.createGraphics();
        paintComponent(g);   // el MISMO dibujo de la pantalla, pero sobre la imagen
        g.dispose();
        File destino = new File(archivo);
        ImageIO.write(imagen, "png", destino);
        return destino;
    }
}

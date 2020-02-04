package com.nick.wood.rendering;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class RenderHandler {

    private BufferedImage view;
    private int[] pixels;

    public RenderHandler(int width, int height) {

        view = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();

    }

    public void render(Graphics g) {

        g.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);

    }

    public void changeScreenSize(int width, int height) {
        view = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();

    }

    private void setPixel(int pixel, int x, int y) {
        int pixelIndex = x + (y * view.getWidth());
        if (pixelIndex < pixels.length) {
            pixels[pixelIndex] = pixel;
        }
    }


    public void renderPixelMatrix(int[][] cells) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                setPixel(cells[x][y], x, y);
            }
        }
    }

    public void clear() {
        Arrays.fill(pixels, 0);
    }
}

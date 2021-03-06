package ru.draw;

import ru.draw.Point;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Image creator
 */
public class ImageCreator {

    /**
     * @param point point in (0, 0) on left-bottom
     * @return point in (0, 0) on left-up
     */
    private static Point makeNormal(Point point) {
        return new Point(point.x, 1000 - point.y);
    }

    /**
     * @param point point on graphic
     * @return point in (0, 0) on left-up
     */
    private static Point makeNormalOnGraphic(Point point) {
        return makeNormal(new Point(point.x + 100, point.y + 100));
    }

    /**
     * Draw line
     *
     * @param graphics {@link Graphics}
     * @param start    start point
     * @param finish   finish point
     */
    private static void lineOnGraphic(Graphics graphics, Point start, Point finish) {
        start = makeNormalOnGraphic(start);
        finish = makeNormalOnGraphic(finish);
        graphics.drawLine(start.x, start.y, finish.x, finish.y);
    }

    /**
     * Draw point
     *
     * @param graphics {@link Graphics}
     * @param point    point
     */
    private static void pointOnGraphic(Graphics graphics, Point point) {
        point = makeNormalOnGraphic(point);
        graphics.fillOval(point.x - 4, point.y - 4, 8, 8);
    }

    public static BufferedImage draw(String file, String method, ArrayList<Point> test, ArrayList<Point> train) {
        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(0, 0, 1000, 1000);
        graphics.setColor(Color.BLACK);
        lineOnGraphic(graphics, new Point(0, 0), new Point(800, 0));
        lineOnGraphic(graphics, new Point(0, 0), new Point(0, 800));
        lineOnGraphic(graphics, new Point(800, 0), new Point(800 - 10, -10));
        lineOnGraphic(graphics, new Point(800, 0), new Point(800 - 10, 10));
        lineOnGraphic(graphics, new Point(0, 800), new Point(-10, 790));
        lineOnGraphic(graphics, new Point(0, 800), new Point(10, 790));
        int size = test.size();
        int perOne = 800 / size;
        IntStream.range(0, size).forEach(element -> {
                    lineOnGraphic(graphics, new Point(-10, element * perOne), new Point(10, element * perOne));
                    lineOnGraphic(graphics, new Point(element * perOne, -10), new Point(element * perOne, 10));
                    graphics.drawString(element * 100 / size + "%", 10, 1000 - (element * perOne + 100));
                    graphics.drawString(String.valueOf(element * 4), 100 + element * perOne, 990);
                }
        );
        graphics.drawString(String.format("File: %s, Method: %s", file, method), 10, 10);
        graphics.drawString("Red - adding, blue - find", 10, 30);
        graphics.drawString("@vladrus13", 930, 980);
        graphics.setColor(Color.RED);
        for (Point point : test) {
            pointOnGraphic(graphics, point);
        }
        graphics.setColor(Color.BLUE);
        for (Point point : train) {
            pointOnGraphic(graphics, point);
        }
        return image;
    }
}
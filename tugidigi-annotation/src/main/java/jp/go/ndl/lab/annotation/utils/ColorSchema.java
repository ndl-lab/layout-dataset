package jp.go.ndl.lab.annotation.utils;

import java.awt.*;

public class ColorSchema {

    public static Color RED = new Color(255, 75, 0);
    public static Color YELLOW = new Color(255, 241, 0);
    public static Color GREEN = new Color(3, 175, 122);
    public static Color BLUE = new Color(0, 90, 255);
    public static Color CYAN = new Color(77, 196, 255);
    public static Color PINK = new Color(255, 128, 130);
    public static Color ORANGE = new Color(246, 170, 0);
    public static Color PURPLE = new Color(153, 0, 153);
    public static Color BROWN = new Color(128, 64, 0);

    public static Color[] COLORS = {RED, YELLOW, GREEN, BLUE, CYAN, PINK, ORANGE, PURPLE, BROWN};

    public static Color getColor(int index) {
        return COLORS[index % COLORS.length];
    }

}

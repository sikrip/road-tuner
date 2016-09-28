package sikrip.roaddyno.standalone;

import static java.awt.Color.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

final class ColorProvider {

    static final List<Color> colors = new ArrayList<>();

    static {
        reset();
    }

    private ColorProvider() {
    }

    static void reset() {

        colors.clear();

        push(RED);
        push(BLUE);
        push(GREEN);
        push(GRAY);
        push(BLACK);
        push(MAGENTA);

        push(YELLOW);
        push(WHITE);
        push(LIGHT_GRAY);
        push(PINK);
        push(ORANGE);
        push(CYAN);
    }

    static Color pop() {
        if (colors.isEmpty()) {
            throw new IllegalArgumentException("No more color available");
        }
        return colors.remove(0);
    }

    static void push(Color color) {
        colors.add(color);
    }
}

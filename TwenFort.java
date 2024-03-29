import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.CharKey;
import java.util.concurrent.TimeUnit;

// Making testing easy
class Testeez {
    static void check ( String label, Object x, Object y ) throws Exception {
        if ( x != y ) {
            throw new Exception("\n" + label + ": " + x + " should equal " + y + " but it don't :(");
        }
    }
}

class Model {
    int grid[][];

    Model() {
        this.grid = new int[4][4];
        grid[0][0] = 2;
        grid[0][3] = 2;
    }

    public void spawn(int x0, int dx,
                      int y0, int dy) {
        int x = x0;
        int y = y0;
        while ( x < 4 && y < 4 ) {
            if ( grid[x][y] == 0 ) {
                grid[x][y] = 2;
                break;
            }
            x += dx;
            y += dy;
        }
    }

    public boolean merge(boolean horiz,
                         int x2, int dx1,
                         int y2, int dy1) {
        boolean merge = false;
        int x1 = x2 + dx1;
        int y1 = y2 + dy1;
        while ( 0 <= y1 && y1 < 4 && 0 <= x1 && x1 < 4 ) {
            int x3 = horiz ? x1 : x2;
            int y3 = horiz ? y2 : y1;
            if ( grid[x3][y3] == grid[x1][y1] ) {
                grid[x3][y3] += grid[x1][y1];
                grid[x1][y1] = 0;
                merge = true;
                break;
            } else if ( grid[x1][y1] != 0 ) {
                break;
            }
            y1 += dy1;
            x1 += dx1;
        }
        return merge;
    }

    public boolean move(int last_x1, int x2, int dx1,
                        int last_y1, int y2, int dy1) {
        boolean merge = false;
        int x1 = x2 + dx1;
        int y1 = y2 + dy1;
        while ( 0 <= x1 && x1 < 4 && 0 <= y1 && y1 < 4 ) {
            if ( grid[x1][y1] != 0 ) {
                break;
            } else {
                grid[x1][y1] = grid[x1-dx1][y1-dy1];
                grid[x1-dx1][y1-dy1] = 0;
                if ( y1 == last_y1 || x1 == last_x1 ) {
                    merge = true;
                }
            }
            x1 += dx1;
            y1 += dy1;
        }
        return merge;
    }

    public void shift(boolean horiz, int dx1, int dy1,
                      int x2_start, int dx2, int x2_end,
                      int y2_start, int dy2, int y2_end) {
        boolean merge = false;
        int x1 = 0;
        int y1 = 0;
        while ( x1 < 4 && y1 < 4 ) {
            int x2 = x2_start == -1 ? x1 : x2_start;
            int y2 = y2_start == -1 ? y1 : y2_start;
            while ( 0 <= x2 && x2 < 4 && 0 <= y2 && y2 < 4 ) {
                if ( grid[x2][y2] != 0 ) {
                    merge |= merge(horiz, x2, dx2, y2, dy2);
                    merge |= move(x2_start, x2, -dx2, y2_start, y2, -dy2);
                }
                x2 += dx2;
                y2 += dy2;
            }
            x1 += dx1;
            y1 += dy1;
        }
        if ( merge ) {
            spawn(x2_end, dx1, y2_end, dy1);
        }
    }

    public Model react( CharKey k ) {
        if ( k.isRightArrow() ) {
            shift( true, 1, 0, -1, +0, 0, +3, -1, 0);
        } else if ( k.isLeftArrow() ) {
            shift( true, 1, 0, -1, +0, 0, +0, +1, 3);
        } else if ( k.isUpArrow() ) {
            shift(false, 0, 1, +0, +1, 3, -1, +0, 0);
        } else if ( k.isDownArrow() ) {
            shift(false, 0, 1, +3, -1, 0, -1, +0, 0);
        }
        return this;
    }

    static int value2color( ConsoleSystemInterface s, int v ) {
        switch (v) {
        case    2: return s.GRAY;
        case    4: return s.WHITE;
        case    8: return s.TEAL;
        case   16: return s.BLUE;
        case   32: return s.CYAN;
        case   64: return s.GREEN;
        case  128: return s.LEMON;
        case  256: return s.PURPLE;
        case  512: return s.MAGENTA;
        case 1024: return s.RED;
        default:   return s.YELLOW;
        }
    }

    static int STARTW = 20;
    static int STARTH = 0;
    static int WIDTH = 7;
    static int HEIGHT = 6;
    public void draw ( ConsoleSystemInterface s ) {
        int score = 0;
        for ( int x = 0; x < 4; x++ ) {
            for ( int y = 0; y < 4; y++ ) {
                int C0 = STARTW + WIDTH * y;
                int R0 = STARTH + HEIGHT * x;
                s.print(C0, R0, "+------+", s.LIGHT_GRAY);
                for ( int j = 1; j < HEIGHT; j++ ) {
                    s.print(C0, R0 + j, "|      |", s.LIGHT_GRAY);
                }
                int v = grid[x][y];
                if ( v != 0 ) {
                    String pad = " ";
                    if ( v < 1000 ) { pad = " "; }
                    if ( v < 100 ) { pad = "  "; }
                    if ( v < 10 ) { pad = "   "; }
                    s.print(C0 + 1, R0 + 3, (pad + v), value2color(s, v));
                }
                score += v;
            }
        }
        s.print(STARTW, STARTH + HEIGHT * 4, "+------+------+------+------+", s.LIGHT_GRAY);
        s.print(0, 0, "Score: " + score, s.WHITE);
    }

    public static void test () throws Exception {
        Model m = new Model();
    }
}

class TwenFortTest {
    public static void main ( String[] args ) throws Exception {
        Model.test();
    }
}

class TwenFortRun {
    public static void main ( String[] args ) {
        ConsoleSystemInterface s =
            new WSwingConsoleInterface("ASCII 2048 by Jay!", true);

        Model m = new Model();
        while (true) {
            s.cls();
            m.draw(s);
            s.refresh();
            m = m.react(s.inkey());
        }
    }
}

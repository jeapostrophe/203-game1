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
    static int MAXH = 24;
    static int MAXW = 79;
    static int MAX = MAXH;
    int grid[][];

    Model() {
        this.grid = new int[4][4];
        grid[0][0] = 2;
        grid[0][3] = 2;
    }

    public void spawn(int x0, int dx, int y0, int dy) {
        int x = x0;
        int y = y0;
        while ( x < 4 ) {
            if ( grid[x][y] == 0 ) {
                grid[x][y] = 2;
                break;
            }
            x += dx;
            y += dy;
        }
    }

    public boolean mergeHoriz(int x, int y2, int dy1) {
        boolean merge = false;
        for ( int y1 = y2 + dy1; 0 <= y1 && y1 < 4; y1 += dy1 ) {
            if ( grid[x][y2] == grid[x][y1] ) {
                grid[x][y2] += grid[x][y1];
                grid[x][y1] = 0;
                merge = true;
                break;
            } else if ( grid[x][y1] != 0 ) {
                break;
            }
        }
        return merge;
    }
    public boolean mergeVert(int y, int x2, int dx1) {
        boolean merge = false;
        for ( int x1 = x2 + dx1; 0 <= x1 && x1 < 4; x1 += dx1 ) {
            if ( grid[x2][y] == grid[x1][y] ) {
                grid[x2][y] += grid[x1][y];
                grid[x1][y] = 0;
                merge = true;
                break;
            } else if ( grid[x1][y] != 0 ) {
                break;
            }
        }
        return merge;
    }

    public boolean moveHoriz(int x, int last_y1, int y2, int dy1) {
        boolean merge = false;
        for ( int y1 = y2 + dy1; 0 <= y1 && y1 < 4; y1 += dy1 ) {
            if ( grid[x][y1] != 0 ) {
                break;
            } else {
                grid[x][y1] = grid[x][y1-dy1];
                grid[x][y1-dy1] = 0;
                if ( y1 == last_y1 ) {
                    merge = true;
                }
            }
        }
        return merge;
    }
    public boolean moveVert(int y, int last_x1, int x2, int dx1) {
        boolean merge = false;
        for ( int x1 = x2 + dx1; 0 <= x1 && x1 < 4; x1 += dx1 ) {
            if ( grid[x1][y] != 0 ) {
                break;
            } else {
                grid[x1][y] = grid[x1-dx1][y];
                grid[x1-dx1][y] = 0;
                if ( x1 == last_x1 ) {
                    merge = true;
                }
            }
        }
        return merge;
    }

    public Model shiftHoriz(int y2_start, int dy2, int y2_end) {
        boolean merge = false;
        for ( int x = 0; x < 4; x++ ) {
            for ( int y2 = y2_start; 0 <= y2 && y2 < 4; y2 += dy2 ) {
                if ( grid[x][y2] != 0 ) {
                    merge |= mergeHoriz(x, y2, dy2);
                    merge |= moveHoriz(x, y2_start, y2, -dy2);
                }
            }
        }
        if ( merge ) {
            spawn(0, 1, y2_end, 0);
        }
        return this;
    }
    public Model shiftVert(int x2_start, int dx2, int x2_end) {
        boolean merge = false;
        for ( int y = 0; y < 4; y++ ) {
            for ( int x2 = x2_start; 0 <= x2 && x2 < 4; x2 += dx2 ) {
                if ( grid[x2][y] != 0 ) {
                    merge |= mergeVert(y, x2, dx2);
                    merge |= moveVert(y, x2_start, x2, -dx2);
                }
            }
        }
        if ( merge ) {
            spawn(x2_end, 0, 0, 1);
        }
        return this;
    }

    public Model react( CharKey k ) {
        if ( k.isRightArrow() ) {
            return shiftHoriz(3, -1, 0);
        } else if ( k.isLeftArrow() ) {
            return shiftHoriz(0, +1, 3);
        } else if ( k.isUpArrow() ) {
            return shiftVert(0, +1, 3);
        } else if ( k.isDownArrow() ) {
            return shiftVert(3, -1, 0);
        } else {
            return this;
        }
        // xxx detect no move possible for end
    }

    public void draw ( ConsoleSystemInterface s ) {
        int STARTW = (MAXW / 4);
        int STARTH = 0;
        int WIDTH = 6;
        int HEIGHT = 6;
        int score = 0;
        for ( int x = 0; x < 4; x++ ) {
            for ( int y = 0; y < 4; y++ ) {
                score += grid[y][x];
                for ( int i = 0; i < WIDTH; i++ ) {
                    for ( int j = 0; j < HEIGHT; j++ ) {
                        String c;
                        int col = s.WHITE;
                        if ( ( i == 0 && j == 0 ) ) {
                            c = "+";
                            col = s.LIGHT_GRAY;
                        } else if ( i == 0 ) {
                            c = "|";
                            col = s.LIGHT_GRAY;
                        } else if ( j == 0 ) {
                            c = "-";
                            if ( x == 3 && i == WIDTH-1 ) {
                                c = "-+";
                            }
                            col = s.LIGHT_GRAY;
                        } else if ( x == 3 && i == WIDTH-1 ) {
                            c = " |";
                            col = s.LIGHT_GRAY;
                        } else {
                            c = "";
                        }
                        if ( i == 1 && j == 3 && grid[y][x] != 0 ) {
                            c = "" + grid[y][x];
                            switch (grid[y][x]) {
                            case 2: col = s.WHITE; break;
                            case 4: col = s.BLUE; break;
                            case 8: col = s.CYAN; break;
                            case 16: col = s.YELLOW; break;
                            case 32: col = s.GREEN; break;
                            case 64: col = s.LEMON; break;
                            case 128: col = s.MAGENTA; break;
                            case 256: col = s.PURPLE; break;
                            case 512: col = s.RED; break;
                            case 1024: col = s.TEAL; break;
                            default: col = s.YELLOW; break;
                            }
                        }
                        s.print(STARTW + WIDTH * x + i,
                                STARTH + HEIGHT * y + j,
                                c,
                                col);
                    }
                }
            }
        }
        s.print(STARTW, STARTH + HEIGHT * 4, "+-----+-----+-----+-----+", s.LIGHT_GRAY);
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
    public static void main ( String[] args ) throws InterruptedException {
        ConsoleSystemInterface s =
            new WSwingConsoleInterface("ASCII 2048", true);

        Model m = new Model();
        while (true) {
            s.cls();
            m.draw(s);
            s.refresh();
            CharKey k = s.inkey();
            m = m.react(k);
        }
    }
}

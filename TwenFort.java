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

    public void spawnHoriz(int col) {
        for ( int x = 0; x < 4; x++ ) {
            if ( grid[x][col] == 0 ) {
                grid[x][col] = 2;
                break;
            }
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
            spawnHoriz(y2_end);
        }
        return this;
    }

    public Model shiftRight() {
        return shiftHoriz(3, -1, 0);
    }

    public Model shiftLeft() {
        return shiftHoriz(0, +1, 3);
    }

    public Model shiftDown() {
        return this;
    }

    public Model shiftUp() {
        return this;
    }

    public Model react( CharKey k ) {
        if ( k.isRightArrow() ) {
            return this.shiftRight();
        } else if ( k.isLeftArrow() ) {
            return this.shiftLeft();
        } else if ( k.isUpArrow() ) {
            return this.shiftUp();
        } else if ( k.isDownArrow() ) {
            return this.shiftRight();
        } else {
            return this;
        }
        // xxx detect no move possible for end
    }

    public void draw ( ConsoleSystemInterface s ) {
        // xxx show score
        // xxx use colors
        int STARTW = (MAXW / 4);
        int STARTH = 0;
        int WIDTH = 6;
        int HEIGHT = 6;
        for ( int x = 0; x < 4; x++ ) {
            for ( int y = 0; y < 4; y++ ) {
                for ( int i = 0; i < WIDTH; i++ ) {
                    for ( int j = 0; j < HEIGHT; j++ ) {
                        String c;
                        if ( ( i == 0 && j == 0 ) ) {
                            c = "+";
                        } else if ( i == 0 ) {
                            c = "|";
                        } else if ( j == 0 ) {
                            c = "-";
                            if ( x == 3 && i == WIDTH-1 ) {
                                c = "-+";
                            }
                        } else if ( x == 3 && i == WIDTH-1 ) {
                            c = " |";
                        } else {
                            c = "";
                        }
                        if ( i == 1 && j == 3 && grid[y][x] != 0 ) {
                            c = "" + grid[y][x];
                        }
                        s.print(STARTW + WIDTH * x + i,
                                STARTH + HEIGHT * y + j,
                                c,
                                s.WHITE);
                    }
                }
            }
        }
        s.print(STARTW, STARTH + HEIGHT * 4, "+-----+-----+-----+-----+");
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

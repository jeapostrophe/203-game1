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

    public Model react( CharKey k ) {
        // XXX This isn't right
        if ( k.isRightArrow() ) {
            for ( int x = 0; x < 4; x++ ) {
                for ( int y = 0; y < 3; y++ ) {
                    if ( grid[x][y+1] == grid[x][y] ) {
                        grid[x][y+1] += grid[x][y];
                        grid[x][y] = 0;
                    }
                    if ( grid[x][y+1] == 0 ) {
                        grid[x][y+1] = grid[x][y];
                        grid[x][y] = 0;
                    }
                }
            }
            // XXX Not quite right
            for ( int x = 0; x < 4; x++ ) {
                if ( grid[x][0] == 0 ) {
                    grid[x][0] = 2;
                    break;
                }
            }
            return this;
        } else if ( k.isDownArrow() ) {
            for ( int y = 0; y < 4; y++ ) {
                for ( int x = 0; x < 3; x++ ) {
                    if ( grid[x+1][y] == grid[x][y] ) {
                        grid[x+1][y] += grid[x][y];
                        grid[x][y] = 0;
                    }
                    if ( grid[x+1][y] == 0 ) {
                        grid[x+1][y] = grid[x][y];
                        grid[x][y] = 0;
                    }
                }
            }
            // XXX Not quite right
            for ( int y = 0; y < 4; y++ ) {
                if ( grid[0][y] == 0 ) {
                    grid[0][y] = 2;
                    break;
                }
            }
            return this;
        } else {
            return this;
        }
    }

    public void draw ( ConsoleSystemInterface s ) {
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

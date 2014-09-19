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
    int height;
    int dh;

    Model() {
        this(MAX, -1);
    }
    private Model( int height, int dh ) {
        this.height = height;
        this.dh = dh;
    }

    public Model tick () {
        int nh = height + dh;
        if ( nh < 0 ) {
            return new Model( 0, - dh );
        } else if ( nh > MAX ) {
            return new Model( MAX, - dh );
        } else {
            return new Model( nh, dh );
        }
    }

    public Model react( CharKey k ) {
        if ( k.isDownArrow() ) {
            return new Model( height, - dh );
        } else {
            return this;
        }
    }

    public void draw ( ConsoleSystemInterface s ) {
        String disp;
        switch (height % 4) {
         case 0: disp =  "/"; break;
         case 1: disp =  "|"; break;
         case 2: disp = "\\"; break;
        default: disp =  "-"; break;
        }
        s.print(MAXW / 2, height, disp, s.WHITE);
    }

    public static void test () throws Exception {
        Model m = new Model();

        // We can look at .height because the tester is inside the
        // class
        Testeez.check("tick once", m.tick().height, MAX - 1 );
    }
}

class Game1Test {
    public static void main ( String[] args ) throws Exception {
        Model.test();
        System.out.println("Wow, every test passed! You must be totally proud. I am too. Marry me?");
    }
}

class Game1Run {
    public enum DemoMode { ANIMATION, SLIDESHOW, REACTIVE };
    static DemoMode mode = DemoMode.ANIMATION;

    public static void main ( String[] args ) throws InterruptedException {
        ConsoleSystemInterface s =
            new WSwingConsoleInterface("Game1 by the Jay", true);

        s.cls();
        s.print(1, 0, "Press any key to get this party started.", s.RED);
        s.refresh();
        s.inkey();

        Model m = new Model();

        while (true) {
            s.cls();
            m.draw(s);
            s.refresh();
            if ( mode == DemoMode.ANIMATION ) {
                // If you want an animation, then ignore the input and
                // sleep before continuing
                TimeUnit.MILLISECONDS.sleep(16 * 4);
            } else if ( mode == DemoMode.SLIDESHOW ) {
                // If you want the player to press a specific key
                // before continuing:
                s.waitKey(CharKey.SPACE);
            } else if ( mode == DemoMode.REACTIVE ) {
                // If you want the player to press any key and/or you
                // want to inspect the key before continuing
                CharKey k = s.inkey();
                m = m.react(k);
            }
            m = m.tick();
        }
    }
}

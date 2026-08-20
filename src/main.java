import java.util.concurrent.ThreadLocalRandom;

public class main  {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("start");
        //sun / planet mass
        double sol_mass = 1.989 * Math.pow(10, 30);
        double earth_mass = 5.9722 * Math.pow(10, 24);
        double mars_mass = 6.39 * Math.pow(10, 23);
        //sun / planet positions
        double[][] sol = new double[][]{{0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
        double[][] earth = new double[][]{{150000000000.0, 0.0, 0.0}, {0.0, 30.0, 0.0}};
        earth = new double[][]{{0, 0.0, 0.0}, {0.0, 0, 0.0}}; // testing hvor jorda er midtpunktet
        double[][] mars = new double[][]{{218000000, 0.0, 0.0}, {0.0, 24, 0.0}};

        Spherical_stellar_object Sol = new Spherical_stellar_object(sol[0],sol[1], sol_mass, "sun", 0 , 0);
        Spherical_stellar_object Earth = new Spherical_stellar_object(earth[0],earth[1], earth_mass, "earth", 6378137 , 6357000, 24, 0, new double[]{0, 0});
        Spherical_stellar_object Mars = new Spherical_stellar_object(mars[0],mars[1], mars_mass, "mars", 1, 1);
        Spherical_stellar_object Earth2 = new Spherical_stellar_object(earth_mass, "earth", Sol, 0,0,0,0,150000000000.0,0,6378137,6357000);

        Sattelite[] test = new Sattelite[10];

        for (int i = 0; i < test.length-1; i++) {
            int lan = ThreadLocalRandom.current().nextInt(0, 360);
            int arg = ThreadLocalRandom.current().nextInt(0, 360);
            int ano = ThreadLocalRandom.current().nextInt(0, 360);
            int inc = ThreadLocalRandom.current().nextInt(0, 360);
            double en = (double) ThreadLocalRandom.current().nextInt(50, 99) / 100;
            test[i] = new Sattelite(1, "s"+i, Earth2, lan, arg, ano, inc, 100000 + 6378137, 0);
        }

        test[test.length-1] = new Sattelite(1, "geo", Earth2, 0, 0, 0, 0, 35786000 + 6378137, 0);

        Earth2.define_POI_covrage(0.1);
        Stellar_object[] solar_system = new Stellar_object[]{Sol,Earth2};

        boolean print = true;
        double dt = 0.01;
        double maxtime = 60*60*24*365;

        double max_error = 1;
        double min_error = 0.1;

        Optimizer optimize = new Optimizer(maxtime, dt, print, test, solar_system, max_error, min_error);

        if (print) {
            GUI gui = new GUI();

            System.out.println("Launch ui");
            gui.starter(optimize);
            System.out.println("Stopping software");
            optimize.end();
        }
        System.out.println("Have a good day");
    }
}

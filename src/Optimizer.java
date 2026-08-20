import java.util.Arrays;

class Optimizer {
    private Solar_orbits[] simmulations;
    private Thread[] threads;
    final static Object p_sync = new Object();
    final static Object m_p_sync = new Object();
    private static boolean[] p_v_sync;
    private static double[][][] print_values;
    private static double[] print_times;
    private Stellar_object[] sim_init_stellar_map;
    private static double max_time;
    private static double dt;
    private static boolean fin = false;
    private static int print_skipps = 1;
    private static double max_error;
    private static double min_error;
    private static boolean immovable_star;
    private static boolean find_optimum;

    public Optimizer(double max_time, double dt, boolean print, Sattelite[] org_sattelites, Stellar_object[] star_system, double max, double min, boolean immovable_sun, boolean find_local_optimum) throws InterruptedException {
        immovable_star = immovable_sun;
        find_optimum = find_local_optimum;
        max_error = max;
        min_error = min;
        sim_init_stellar_map = star_system;
        Optimizer.dt = dt;
        Optimizer.max_time = max_time;
        int cores = 1;
        threads = new Thread[cores];
        p_v_sync = new boolean[cores];
        print_times = new double[cores];
        print_values = new double[cores][org_sattelites.length+star_system.length][3];

        long startTime = System.nanoTime();

        for (int i = 0; i < threads.length; i++) {
            create_sim(org_sattelites,i, print);
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        if(!print){
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }
        }

        long stopTime = System.nanoTime();
        System.out.println("sim runtime: " + ((stopTime - startTime)/1000000000));
    }
    public double[][][] get_print_values(int skips){

        print_sim_sync(-1,skips);

        return print_values;
    }
    public double[] get_print_times(){
        return print_times;
    }
    public double get_dt(){
        return dt;
    }

    static void print_sim_sync(int id, int skipp) {
        if(id == -1){
            print_skipps = skipp;
            synchronized (m_p_sync) {
                Arrays.fill(p_v_sync, true);
                synchronized (p_sync) {
                    p_sync.notifyAll();
                }
            }
        }
        else {
            synchronized (p_sync) {
                p_v_sync[id] = false;
                while (!p_v_sync[id] && !fin) {
                    try {
                        synchronized (m_p_sync) {
                            m_p_sync.notifyAll();
                        }
                        p_sync.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void create_sim(Sattelite[] sattelites, int id, boolean printable){
        threads[id] = new Thread(new Runnable() {
            private int id;
            private boolean printable;
            public Runnable init(int id, boolean printable) {
                this.id = id;
                this.printable = printable;
                return this;
            }
            @Override
            public void run() {
                System.out.println("run thread: " + id);
                Solar_orbits sim = new Solar_orbits(sim_init_stellar_map, sattelites, immovable_star);
                double time = 0;
                double pros = 0;
                int t_skip = 0;
                int n = 0;
                while (find_optimum || n < 1) {
                    while ((time < max_time || printable) && !fin) {

                        //double error = sim.euler_solar_calc(dt);
                        double error = sim.symplectic_4th_order_solar_calc(dt);
                        //sim.runge_kutta_butcher_solar_calc(dt);
                        //double error = sim.runge_kutta_fehlberg_solar_calc(dt);

                        if (printable) {
                            print_values[id] = sim.get_map();
                            if (t_skip >= print_skipps) {
                                print_sim_sync(id, 0);
                                t_skip = 0;
                            }
                            t_skip++;
                            print_times[id] = time;
                        }
                        time += dt;

                        if (error > max_error) {
                            dt = dt / 2;
                        }
                        if (error < min_error) {
                            dt = dt * 1.1;
                        }

                        if (time / max_time * 100 > pros + 5 && !printable) {
                            pros = time / max_time * 100;
                            System.out.println("th_id: " + id + " | " + (int) pros + " %");
                        }
                    }
                    n++;
                }
            }
        }.init(id,printable));
    }
    public void end() throws InterruptedException {
        fin = true;
        print_sim_sync(-1,1);
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
    }
}

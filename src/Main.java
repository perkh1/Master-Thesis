import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        double sol_mass = 1.989 * Math.pow(10,30);
        double earth_mass = 5.972 * Math.pow(10,24);
        double mars_mass = 6.39 * Math.pow(10,23);
        double[][] sol = new double[][]{{0.0,0.0,0.0},{0.0,0.0,0.0}};
        double[][] earth = new double[][]{{150000000.0,0.0,0.0},{0.0,30.0,0.0}};
        double[][] mars = new double[][]{{218000000,0.0,0.0},{0.0,24,0.0}};

        Spherical_stellar_object Sol = new Spherical_stellar_object(sol,sol_mass,696340,696000);
        Spherical_stellar_object Earth = new Spherical_stellar_object(earth,earth_mass,6378,6357,24,0,new double[]{0,0});
        Spherical_stellar_object Mars = new Spherical_stellar_object(mars,mars_mass,1,1);
        Stellar_object[] stellar_map = new Stellar_object[]{Sol,Earth,Mars};

        double[][] test_sat_vals = new double[][]{{150001000.0,0.0,0.0},{0.0,50,0.0}};

        Sattelite test_sat = new Sattelite(test_sat_vals,10);

        Solar_orbits m1 = new Solar_orbits(stellar_map, test_sat);

        int max_time = 60*60*24*365;
        //max_time = 10;
        double dt = 1;
        max_time = (int) (max_time / dt);
        long start = System.currentTimeMillis();
        PrintWriter writer1 = new PrintWriter("solar_map_pos.txt", "UTF-8");
        PrintWriter writer2 = new PrintWriter("earth_map_pos.txt", "UTF-8");
        double[] earth_origo = m1.get_map()[1];
        int p = 5;
        int np = -1;

        long calctime = 0;
        for (int i = 0; i < max_time; i++) {

            double[][]out = m1.get_map();
            Sattelite o_sat = m1.get_satt();
            writer1.println("*");
            writer2.println("*");
            for (double[] j:out) {
                double x = j[0]-earth_origo[0];
                double y = j[1]-earth_origo[1];
                double z = j[2]-earth_origo[2];
                writer1.println(j[0] + ","+j[1]+","+j[2]);
                writer2.println(x + ","+y+","+z);
            }
            double[] o_sat_pos = o_sat.getOrbit_values()[0];

            writer1.println(o_sat_pos[0] + ","+o_sat_pos[1]+","+o_sat_pos[2]);
            double x = o_sat_pos[0]-earth_origo[0];
            double y = o_sat_pos[1]-earth_origo[1];
            double z = o_sat_pos[2]-earth_origo[2];
            writer2.println(x + ","+y+","+z);
            if((i*100 / max_time) > np){
                np += p;
                System.out.println((i*100 / max_time)  + "%");
            }
            long temptime = System.currentTimeMillis();
            m1.solar_calc(dt);
            calctime += System.currentTimeMillis() - temptime;
        }
        writer1.println("end");
        writer2.println("end");
        writer1.close();
        writer2.close();
        System.out.println("fin");
        long end = System.currentTimeMillis() - start;
        System.out.println(end / 1000 + " sec, runtime");
        System.out.println(calctime / 1000 + " sec, calctime");
/*
        String command = "python /c start python plot.py";
        Process p = Runtime.getRuntime().exec(command);
*/
    }
}

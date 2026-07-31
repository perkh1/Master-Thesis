public class Solar_orbits {
    private Stellar_object[] stellar_map;
    private double G = 6.674 * Math.pow(10,-11);
    private Sattelite[] satts;
    public Solar_orbits(Stellar_object[] stellar_map_imp, Sattelite[] sats){
        stellar_map = stellar_map_imp;
        satts = sats;
    }
    public void solar_calc(double dt){
        /*
        Spherical_stellar_object a = (Spherical_stellar_object) stellar_map[1];
        if(a.Collision(satt.getOrbit_values(0))){
            //satt.collide();
        }
         */
        Stellar_object[] new_solar_map = stellar_map.clone();
        for (int i = 0; i < stellar_map.length; i++) {
            double[] vel = force_calc(stellar_map[i]);
            stellar_map[i].orbit_calc(dt, vel);

        }
        for (int i = 0; i < satts.length; i++) {
            if(!satts[i].isHas_collided()) {
                double[] sat_vel = force_calc(satts[i]);
                satts[i].orbit_calc(dt, sat_vel);
            }
        }
        stellar_map = new_solar_map;

        for (int i = 0; i < stellar_map.length; i++) {
            stellar_map[i].finish_orbit_calc();
            stellar_map[i].rotate(dt);
        }

        for (int i = 0; i < satts.length; i++) {
            satts[i].finish_orbit_calc();
        }

    }
    public double[][] get_map(){
        double[][] map = new double[stellar_map.length + satts.length][3];
        int n = 0;
        for (int i = 0; i < stellar_map.length; i++) {
            map[i] = stellar_map[i].getOrbit_values(0);
            n++;
        }
        for (int i = 0; i < satts.length; i++) {
            map[n+i] = satts[i].getOrbit_values(0);
        }
        return map;
    }
    public Sattelite[] get_sattelites(){
        return satts;
    }

    private double[] force_calc(Stellar_object obj ){
        double[] obj_orbit_poss = obj.getOrbit_values(0);
        double[] force_dir = new double[]{0,0,0};
        for (int i = 0; i < stellar_map.length; i++) {
            double[] stellar_calc_poss = stellar_map[i].getOrbit_values(0);
            double[] re_pos = new double[]{
                    stellar_calc_poss[0] - obj_orbit_poss[0],
                    stellar_calc_poss[1] - obj_orbit_poss[1],
                    stellar_calc_poss[2] - obj_orbit_poss[2]
            };
            double dist = Math.pow(Math.pow(re_pos[0],2) + Math.pow(re_pos[1],2) + Math.pow(re_pos[2],2),0.5);
            if(dist != 0) {
                double[] dir = new double[]{
                        re_pos[0] / dist,
                        re_pos[1] / dist,
                        re_pos[2] / dist
                };

                double force = stellar_map[i].getObj_mass() * G / Math.pow(dist, 2);

                force_dir[0] += force * dir[0];
                force_dir[1] += force * dir[1];
                force_dir[2] += force * dir[2];
            }
        }
        // add other

        return force_dir;
    }
}

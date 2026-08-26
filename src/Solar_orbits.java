public class Solar_orbits {
    private Stellar_object[] stellar_map;
    private double G = 6.6743 * Math.pow(10,-11);
    private Sattelite[] satts;
    private int immovable_star = 0;
    public Solar_orbits(Stellar_object[] stellar_map_imp, Sattelite[] sats, boolean immovable_sun){
        if(immovable_sun){
            immovable_star = 1;
        };
        stellar_map = stellar_map_imp;
        satts = sats;
    }
    public double euler_solar_calc(double dt){
        /*
        Spherical_stellar_object a = (Spherical_stellar_object) stellar_map[1];
        if(a.Collision(satt.getOrbit_values(0))){
            //satt.collide();
        }
         */
        for (int i = immovable_star; i < stellar_map.length; i++) {
            double[] vel = force_calc(stellar_map[i]);
            stellar_map[i].euler_orbit_calc(dt, vel);

        }
        for (int i = 0; i < satts.length; i++) {
            if(!satts[i].isHas_collided()) {
                double[] sat_vel = force_calc(satts[i]);
                satts[i].euler_orbit_calc(dt, sat_vel);
            }
        }
        for (int i = immovable_star; i < stellar_map.length; i++) {
            stellar_map[i].euler_set_halfpoint();
        }

        for (int i = 0; i < satts.length; i++) {
            satts[i].euler_set_halfpoint();
        }

        double error = 0;

        for (int i = immovable_star; i < stellar_map.length; i++) {
            double[] vel = force_calc(stellar_map[i]);
            double error_temp = stellar_map[i].euler_finish_orbit_calc(dt,vel);
            if (error_temp > error){
                error = error_temp;
            }
            stellar_map[i].rotate(dt);
        }

        for (int i = 0; i < satts.length; i++) {
            double[] sat_vel = force_calc(satts[i]);
            double error_temp = satts[i].euler_finish_orbit_calc(dt,sat_vel);
            if (error_temp > error){
                error = error_temp;
            }
        }

        return error;
    }
    public double symplectic_4th_order_solar_calc(double dt){

        double c1_c4 = 1/(2*(2-Math.pow(2,1.0/3)));
        double c2_c3 = (1-Math.pow(2,1.0/3))/(2*(2-Math.pow(2,1.0/3)));
        double d1_d3 = 1/(2-Math.pow(2,1.0/3));
        double d2 = -(Math.pow(2,1.0/3))/(2-Math.pow(2,1.0/3));

        double[] c = new double[]{c1_c4,c2_c3,c2_c3,c1_c4};
        double[] d = new double[]{d1_d3,d2,d1_d3,0};


        /*
        Spherical_stellar_object a = (Spherical_stellar_object) stellar_map[1];
        if(a.Collision(satt.getOrbit_values(0))){
            //satt.collide();
        }
         */
        double error = 0;
        for (int k = 0; k < c.length; k++) {
            for (int i = immovable_star; i < stellar_map.length; i++) {
                double[] vel = force_calc(stellar_map[i]);
                stellar_map[i].symplectic_orbit_calc(dt, vel,c[k],d[k],k);

            }
            for (int i = 0; i < satts.length; i++) {
                if (!satts[i].isHas_collided()) {
                    double[] sat_vel = force_calc(satts[i]);
                    satts[i].symplectic_orbit_calc(dt, sat_vel,c[k],d[k],k);
                }
            }
            for (int i = immovable_star; i < stellar_map.length; i++) {
                stellar_map[i].symplectic_finish_orbit_calc();
                stellar_map[i].rotate(dt);
            }
            for (int i = 0; i < satts.length; i++) {
                satts[i].symplectic_finish_orbit_calc();
            }
        }
        for (int i = immovable_star; i < stellar_map.length; i++) {
            stellar_map[i].symplectic_reset();
        }
        for (int i = 0; i < satts.length; i++) {
            satts[i].symplectic_reset();
        }
        dt = dt/2;
        for (int n = 0; n < 2; n++) {
            for (int k = 0; k < c.length; k++) {
                for (int i = immovable_star; i < stellar_map.length; i++) {
                    double[] vel = force_calc(stellar_map[i]);
                    stellar_map[i].symplectic_orbit_calc(dt, vel, c[k], d[k], k);
                }
                for (int i = 0; i < satts.length; i++) {
                    if (!satts[i].isHas_collided()) {
                        double[] sat_vel = force_calc(satts[i]);
                        satts[i].symplectic_orbit_calc(dt, sat_vel, c[k], d[k], k);
                    }
                }
                for (int i = immovable_star; i < stellar_map.length; i++) {
                    stellar_map[i].symplectic_finish_orbit_calc();
                }
                for (int i = 0; i < satts.length; i++) {
                    satts[i].symplectic_finish_orbit_calc();
                }
            }
        }
        for (int i = immovable_star; i < stellar_map.length; i++) {
            double t_error =  stellar_map[i].symplectic_error_calc();
            if(error < t_error){
                error = t_error;
            }
        }
        for (int i = 0; i < satts.length; i++) {
            double t_error = satts[i].symplectic_error_calc();

            satts[i].update_ref_position();

            if(error < t_error){
                error = t_error;
            }
        }

        return error;
    }
    public void runge_kutta_butcher_solar_calc(double dt){
        double[] k_butcher_mag_vals = new double[]{0.25,0.25,0.5,0.75,1.0};
        double[][] k_butcher_y_mag_vals = new double[][]{
                {0.25},
                {0.125,0.125},
                {0,-0.5,1},
                {3.0/16,0,0,9.0/16},
                {-3.0/7,2.0/7,12.0/7,-12.0/7,8.0/7}
        };

        double[] k_mods = new double[]{7.0/90,0,32.0/90,12.0/90,32.0/90,7.0/90};

        int order = 6;

        for (int i = immovable_star; i < stellar_map.length; i++) {
            stellar_map[i].set_runge_kutta_order(order);
        }

        for (int i = 0; i < satts.length; i++) {
            satts[i].set_runge_kutta_order(order);
        }

        /*
        Spherical_stellar_object a = (Spherical_stellar_object) stellar_map[1];
        if(a.Collision(satt.getOrbit_values(0))){
            //satt.collide();
        }
         */

        for (int k = 0; k < order; k++) {
            for (int i = immovable_star; i < stellar_map.length; i++) {
                double[] vel = force_calc(stellar_map[i]);
                stellar_map[i].runge_kutta_calc(dt, vel,k, k_butcher_mag_vals);
                stellar_map[i].runge_kutta_y_set(k,dt,k_butcher_y_mag_vals,k_butcher_mag_vals);

            }
            for (int i = 0; i < satts.length; i++) {
                if(!satts[i].isHas_collided()) {
                    double[] sat_vel = force_calc(satts[i]);
                    satts[i].runge_kutta_calc(dt, sat_vel,k, k_butcher_mag_vals);
                    satts[i].runge_kutta_y_set(k,dt,k_butcher_y_mag_vals,k_butcher_mag_vals);
                }
            }
            for (int i = immovable_star; i < stellar_map.length; i++) {
                stellar_map[i].runge_kutta_update();
            }

            for (int i = 0; i < satts.length; i++) {
                satts[i].runge_kutta_update();
            }
        }
        for (int i = immovable_star; i < stellar_map.length; i++) {
            stellar_map[i].runge_kutta_finish(dt,k_mods);
            stellar_map[i].rotate(dt);
        }

        for (int i = 0; i < satts.length; i++) {
            satts[i].runge_kutta_finish(dt,k_mods);
        }
    }
    public double runge_kutta_fehlberg_solar_calc(double dt){
        double[] k_fehlberg_mag_vals = new double[]{0,0.2,0.3,3.0/5,1.0,7.0/8};
        double[][] k_fehlberg_y_mag_vals = new double[][]{
                {},
                {0.2},
                {0.3/40,9.0/40},
                {3.0/10,-9.0/10,6.0/5},
                {-11.0/54,5.0/2,-70.0/27,35.0/27},
                {1631.0/55296,175.0/512,575.0/13824,44275.0/110592,253.0/4096}
        };
        double[] k_mods_4 = new double[]{
                37.0/378,
                0,
                250.0/621,
                125.0/594,
                0,
                512.0/1771};
        double[] k_mods_5 = new double[]{
                825.0/27648,
                0,
                18575.0/48384,
                13525.0/55296,
                277.0/14336,
                1.0/4};

        int order = k_fehlberg_mag_vals.length;

        for (int i = immovable_star; i < stellar_map.length; i++) {
            stellar_map[i].set_runge_kutta_order(order);
        }

        for (int i = 0; i < satts.length; i++) {
            satts[i].set_runge_kutta_order(order);
        }

        /*
        Spherical_stellar_object a = (Spherical_stellar_object) stellar_map[1];
        if(a.Collision(satt.getOrbit_values(0))){
            //satt.collide();
        }
         */

        for (int k = 0; k < order; k++) {
            for (int i = immovable_star; i < stellar_map.length; i++) {
                //stellar_map[i].runge_kutta_y_set(k,dt,k_fehlberg_y_mag_vals);
                double[] vel = force_calc(stellar_map[i]);
                stellar_map[i].runge_kutta_calc(dt, vel,k, k_fehlberg_mag_vals);
                stellar_map[i].runge_kutta_y_revert();

            }
            for (int i = 0; i < satts.length; i++) {
                if(!satts[i].isHas_collided()) {
                    //satts[i].runge_kutta_y_set(k,dt,k_fehlberg_y_mag_vals);
                    double[] sat_vel = force_calc(satts[i]);
                    satts[i].runge_kutta_calc(dt, sat_vel,k, k_fehlberg_mag_vals);
                    satts[i].runge_kutta_y_revert();
                }
            }
            for (int i = immovable_star; i < stellar_map.length; i++) {
                stellar_map[i].runge_kutta_update();
            }

            for (int i = 0; i < satts.length; i++) {
                satts[i].runge_kutta_update();
            }
        }
        double max_error = 0;

        for (int i = immovable_star; i < stellar_map.length; i++) {
            double error = stellar_map[i].runge_kutta_finish(dt,k_mods_4,k_mods_5);
            stellar_map[i].rotate(dt);
            if (error > max_error){
                max_error = error;
            }
        }

        for (int i = 0; i < satts.length; i++) {
            double error = satts[i].runge_kutta_finish(dt,k_mods_4,k_mods_5);
            if (error > max_error){
                max_error = error;
            }
        }

        return max_error;
    }

    public double[][] get_map(){
        int map_leng = stellar_map.length + satts.length;
        /* adds POIs to debug gui
        for (int i = 0; i < stellar_map.length; i++) {
            POI[][] pois = stellar_map[i].get_POI_covrage_points();
            if (pois != null){
                for (int j = 0; j < pois.length; j++) {
                    map_leng += pois[j].length;
                }
            }
        }
        */

        double[][] map = new double[map_leng][3];
        int n = 0;
        for (int i = 0; i < stellar_map.length; i++) {

            map[n] = stellar_map[i].getOrbit_position();
            n++;

        }
        for (int i = 0; i < satts.length; i++) {
            map[n] = satts[i].getOrbit_position();
            n++;
        }
        /* adds POIs to debug gui
        for (int i = 0; i < stellar_map.length; i++) {
            POI[][] pois = stellar_map[i].get_POI_covrage_points();
            if (pois != null) {
                for (int j = 0; j < pois.length; j++) {
                    for (int k = 0; k < pois[j].length; k++) {
                        map[n] = pois[j][k].get_abs_pos();
                        n++;
                    }
                }
            }
        }

         */
        return map;
    }
    public Sattelite[] get_sattelites(){
        return satts;
    }

    private double[] force_calc(Stellar_object obj ){
        double[] obj_orbit_poss = obj.getOrbit_position();
        double[] force_dir = new double[]{0,0,0};
        for (int i = 0; i < stellar_map.length; i++) {
            double[] stellar_calc_poss = stellar_map[i].getOrbit_position();
            double[] re_pos = new double[]{
                    stellar_calc_poss[0] - obj_orbit_poss[0],
                    stellar_calc_poss[1] - obj_orbit_poss[1],
                    stellar_calc_poss[2] - obj_orbit_poss[2]
            };
            double dist = Math.sqrt(Math.pow(re_pos[0],2) + Math.pow(re_pos[1],2) + Math.pow(re_pos[2],2));
            if(dist > 10) {
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

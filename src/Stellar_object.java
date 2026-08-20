import java.math.BigDecimal;
import java.math.MathContext;

public class Stellar_object extends Object_root {
    double[] orbit_posistion; // [x,y,z] == posistion
    double[] orbit_speed; // [x,y,z] == speed
    double[] half_orbit_posistion; // [x,y,z] == posistion for euler error estimate
    double[] half_orbit_speed; // [x,y,z] == speed for euler error estimate
    private double[] new_pos = new double[3];
    private double obj_mass;
    private String name;
    POI[][] POI_covrage_points;
    private double[][] k_6;
    private double[] k_x_pos = new double[3];
    private double[] k_x_speed = new double[3];


    public Stellar_object(double[] poss, double[] speed, double mass, String name){
        orbit_posistion = poss;
        orbit_speed = speed;
        obj_mass = mass;
        this.name = name;
    }
    public POI[][] get_POI_covrage_points(){
        return POI_covrage_points;
    }
    public double[] getOrbit_speed(){
        return orbit_speed;
    }
    public double[] getOrbit_position(){
        return orbit_posistion;
    }
    public double getObj_mass(){
        return obj_mass;
    }
    public void euler_orbit_calc(double dt, double[] object_vel){
        new_pos = orbit_posistion.clone();
        half_orbit_speed = orbit_speed.clone();
        half_orbit_posistion = orbit_posistion.clone();

        orbit_speed[0] += object_vel[0] * dt;
        orbit_speed[1] += object_vel[1] * dt;
        orbit_speed[2] += object_vel[2] * dt;

        new_pos[0] += orbit_speed[0] * dt;
        new_pos[1] += orbit_speed[1] * dt;
        new_pos[2] += orbit_speed[2] * dt;

        half_orbit_speed[0] += object_vel[0] * dt/2;
        half_orbit_speed[1] += object_vel[1] * dt/2;
        half_orbit_speed[2] += object_vel[2] * dt/2;

        half_orbit_posistion[0] += half_orbit_speed[0] * dt/2;
        half_orbit_posistion[1] += half_orbit_speed[1] * dt/2;
        half_orbit_posistion[2] += half_orbit_speed[2] * dt/2;
    }
    public void euler_set_halfpoint(){
        orbit_posistion = half_orbit_posistion.clone();
    }
    public double euler_finish_orbit_calc(double dt,double[] object_vel){
        half_orbit_speed[0] += object_vel[0] * dt/2;
        half_orbit_speed[1] += object_vel[1] * dt/2;
        half_orbit_speed[2] += object_vel[2] * dt/2;

        orbit_posistion[0] += half_orbit_speed[0] * dt/2;
        orbit_posistion[1] += half_orbit_speed[1] * dt/2;
        orbit_posistion[2] += half_orbit_speed[2] * dt/2;

        double dist_half = Math.sqrt(Math.pow(orbit_posistion[0],2)+Math.pow(orbit_posistion[1],2)+Math.pow(orbit_posistion[2],2));

        orbit_posistion = new_pos.clone();

        double dist = Math.sqrt(Math.pow(orbit_posistion[0],2)+Math.pow(orbit_posistion[1],2)+Math.pow(orbit_posistion[2],2));

        return Math.abs(dist-dist_half);
    }

    public void symplectic_orbit_calc(double dt, double[] object_vel, double c, double d, double k){
        new_pos = orbit_posistion.clone();
        if(k == 0) {
            k_x_pos = orbit_posistion.clone();
            k_x_speed = orbit_speed.clone();
        }
        if(c != 0) {
            double t_vel_x = object_vel[0] * dt * c;
            double t_vel_y = object_vel[1] * dt * c;
            double t_vel_z = object_vel[2] * dt * c;

            orbit_speed[0] += t_vel_x;
            orbit_speed[1] += t_vel_y;
            orbit_speed[2] += t_vel_z;

        }
        if(d != 0) {
            new_pos[0] += orbit_speed[0] * dt * d;
            new_pos[1] += orbit_speed[1] * dt * d;
            new_pos[2] += orbit_speed[2] * dt * d;
        }
    }
    public void symplectic_finish_orbit_calc(){
        orbit_posistion = new_pos.clone();
    }
    public void symplectic_reset(){
        half_orbit_posistion = orbit_posistion.clone();
        half_orbit_speed = orbit_speed.clone();
        orbit_posistion = k_x_pos.clone();
        orbit_speed = k_x_speed.clone();
    }
    public double symplectic_error_calc(){
        double error_x = orbit_posistion[0] - half_orbit_posistion[0];
        double error_y = orbit_posistion[1] - half_orbit_posistion[1];
        double error_z = orbit_posistion[2] - half_orbit_posistion[2];
        double error = Math.sqrt(error_x*error_x + error_y*error_y + error_z*error_z);
        return error;
    }

    public void set_runge_kutta_order(int x){
        k_6 = new double[x][3];
    }

    public void runge_kutta_y_revert(){
        //orbit_posistion = k_x_pos[1].clone();
    }
    public void runge_kutta_calc(double dt, double[] object_vel, int k, double[] k_x_mag_vals){
        if(k == 0) {
            k_x_pos = orbit_posistion.clone();
            k_x_speed = orbit_speed.clone();
        }
        k_6[k] = object_vel.clone();

        /*
        k_6[k][0] = orbit_speed[0] + object_vel[0] * dt;
        k_6[k][1] = orbit_speed[1] + object_vel[1] * dt;
        k_6[k][2] = orbit_speed[2] + object_vel[2] * dt;
         */

        /*
        if(k < k_x_mag_vals.length-1) {
            double temp_dt = k_x_mag_vals[k+1];
            orbit_speed[0] = k_x_speed[0][0] + object_vel[0] * temp_dt;
            orbit_speed[1] = k_x_speed[0][1] + object_vel[1] * temp_dt;
            orbit_speed[2] = k_x_speed[0][2] + object_vel[2] * temp_dt;
            new_pos[0] = k_x_pos[0][0] + k_6[k][0] * temp_dt;
            new_pos[1] = k_x_pos[0][1] + k_6[k][1] * temp_dt;
            new_pos[2] = k_x_pos[0][2] + k_6[k][2] * temp_dt;
        }
         */
    }
    public void runge_kutta_y_set(int k, double dt, double[][] k_y_mag_vals, double[] k_x_mag_vals){
        if(k < k_6.length-1) {
            double[] temp_spe = k_x_speed.clone();
            //double[] temp_pos = k_x_pos.clone();
            for (int i = 0; i < k_y_mag_vals[k].length; i++) {
                if (k_y_mag_vals[k][i] != 0) {

                    temp_spe[0] += k_6[i][0] * dt * k_y_mag_vals[k][i];
                    temp_spe[1] += k_6[i][1] * dt * k_y_mag_vals[k][i];
                    temp_spe[2] += k_6[i][2] * dt * k_y_mag_vals[k][i];
/*
                    temp_pos[0] += k_6[i][0] * dt * k_y_mag_vals[k][i];
                    temp_pos[1] += k_6[i][1] * dt * k_y_mag_vals[k][i];
                    temp_pos[2] += k_6[i][2] * dt * k_y_mag_vals[k][i];
 */
                }
            }

            //new_pos = temp_pos.clone();

            double temp_dt = k_x_mag_vals[k]*dt;

            new_pos[0] = k_x_pos[0] + temp_spe[0] * temp_dt;
            new_pos[1] = k_x_pos[1] + temp_spe[1] * temp_dt;
            new_pos[2] = k_x_pos[2] + temp_spe[2] * temp_dt;

            orbit_speed = temp_spe.clone();
            /*
            orbit_speed[0] = (new_pos[0] - k_x_pos[0]) / temp_dt;
            orbit_speed[1] = (new_pos[1] - k_x_pos[1]) / temp_dt;
            orbit_speed[2] = (new_pos[2] - k_x_pos[2]) / temp_dt;
             */
        }
    }
    public void runge_kutta_update(){
        orbit_posistion = new_pos.clone();
    }
    public void runge_kutta_finish(double dt, double[] k_mods){

/*
        orbit_speed[0] = 0;
        orbit_speed[1] = 0;
        orbit_speed[2] = 0;
 */
        orbit_speed = k_x_speed.clone();

        for (int i = 0; i < k_mods.length; i++) {
            orbit_speed[0] += k_mods[i]*k_6[i][0]*dt;
            orbit_speed[1] += k_mods[i]*k_6[i][1]*dt;
            orbit_speed[2] += k_mods[i]*k_6[i][2]*dt;
        }


        orbit_posistion[0] = k_x_pos[0] + orbit_speed[0]*dt;
        orbit_posistion[1] = k_x_pos[1] + orbit_speed[1]*dt;
        orbit_posistion[2] = k_x_pos[2] + orbit_speed[2]*dt;
/*
        double prev_speed = Math.sqrt(Math.pow(k_x_speed[0],2)+Math.pow(k_x_speed[1],2)+Math.pow(k_x_speed[2],2));
        double new_speed = Math.sqrt(Math.pow(orbit_speed[0],2)+Math.pow(orbit_speed[1],2)+Math.pow(orbit_speed[2],2));
        double prev_pos = Math.sqrt(Math.pow(k_x_pos[0],2)+Math.pow(k_x_pos[1],2)+Math.pow(k_x_pos[2],2));
        double new_pos = Math.sqrt(Math.pow(orbit_posistion[0],2)+Math.pow(orbit_posistion[1],2)+Math.pow(orbit_posistion[2],2));
        if (Objects.equals(name, "s0")) {
            System.out.println(new_speed - prev_speed);
            System.out.println(new_pos - prev_pos);
            System.out.println("");


        }

 */

    }
    public double runge_kutta_finish(double dt, double[] k_mods_4, double[] k_mods_5){
        orbit_speed[0] = 0;
        orbit_speed[1] = 0;
        orbit_speed[2] = 0;

        double error_x = 0;
        double error_y = 0;
        double error_z = 0;

        for (int i = 0; i < k_mods_5.length; i++) {
            orbit_speed[0] += k_mods_5[i]*k_6[i][0];
            orbit_speed[1] += k_mods_5[i]*k_6[i][1];
            orbit_speed[2] += k_mods_5[i]*k_6[i][2];

            error_x += k_mods_4[i]*k_6[i][0];
            error_y += k_mods_4[i]*k_6[i][1];
            error_z += k_mods_4[i]*k_6[i][2];
        }


        orbit_posistion[0] = k_x_pos[0] + orbit_speed[0]*dt;
        orbit_posistion[1] = k_x_pos[1] + orbit_speed[1]*dt;
        orbit_posistion[2] = k_x_pos[2] + orbit_speed[2]*dt;

        double error =
                Math.abs(orbit_posistion[0] - (k_x_pos[0] + error_x * dt))
                +Math.abs(orbit_posistion[1] - (k_x_pos[1] + error_y * dt))
                +Math.abs(orbit_posistion[2] - (k_x_pos[2] + error_z * dt));
        return error;

    }
    public void rotate(double dt){}

    public void convert(Spherical_stellar_object refrence,
                        double long_ascending_node, // omega
                        double p_argument, // w
                        double anomaly, // v
                        double inclination, //i
                        double semi_major, // a
                        double eccentrisisty // e
    ){
        ref = refrence;
        double mu = refrence.getObj_mass() * 6.6743 * Math.pow(10,-11);
        double[] ref_axis = refrence.get_r_axis();
        double t_long_ascending_node = long_ascending_node * Math.PI/180;
        double t_anomaly = anomaly * Math.PI/180;
        double t_inclination = inclination * Math.PI/180;
        double t_p_argument = p_argument * Math.PI/180;
        //x y z , x == right, y == upp, z == 3d upp
        double rad = (semi_major*(1-Math.pow(eccentrisisty,2)))/(1 + eccentrisisty * Math.cos(t_anomaly));
        double speed = Math.sqrt(mu*((2/rad)-(1/semi_major)));


        double xp = rad * Math.cos(t_anomaly);
        double yp = rad * Math.sin(t_anomaly);
        double p_sqrt = Math.sqrt(mu / (semi_major * (1 - Math.pow(eccentrisisty, 2))));
        double xp_d = -p_sqrt *Math.sin(t_anomaly);
        double yp_d = p_sqrt *(eccentrisisty + Math.cos(t_anomaly));

        double x = xp * (Math.cos(t_long_ascending_node) * Math.cos(t_p_argument)
                - Math.sin(t_long_ascending_node)*Math.sin(t_p_argument)*Math.cos(t_inclination))
                - yp * (Math.cos(t_long_ascending_node)*Math.sin(t_p_argument)
                + Math.sin(t_long_ascending_node)*Math.cos(t_p_argument)*Math.cos(t_inclination));

        double y = xp * (Math.sin(t_long_ascending_node) * Math.cos(t_p_argument)
                + Math.cos(t_long_ascending_node)*Math.sin(t_p_argument)*Math.cos(t_inclination))
                - yp * (Math.sin(t_long_ascending_node)*Math.sin(t_p_argument)
                - Math.cos(t_long_ascending_node)*Math.cos(t_p_argument)*Math.cos(t_inclination));

        double z = xp * Math.sin(t_p_argument) * Math.sin(t_inclination) + yp * Math.cos(t_p_argument) * Math.sin(t_inclination);


        double[] out_posistion = new double[]{x,y,z};

        double x_s = xp_d * (Math.cos(t_long_ascending_node) * Math.cos(t_p_argument)
                - Math.sin(t_long_ascending_node)*Math.sin(t_p_argument)*Math.cos(t_inclination))
                - yp_d * (Math.cos(t_long_ascending_node)*Math.sin(t_p_argument)
                + Math.sin(t_long_ascending_node)*Math.cos(t_p_argument)*Math.cos(t_inclination));

        double y_s = xp_d * (Math.sin(t_long_ascending_node) * Math.cos(t_p_argument)
                + Math.cos(t_long_ascending_node)*Math.sin(t_p_argument)*Math.cos(t_inclination))
                - yp_d * (Math.sin(t_long_ascending_node)*Math.sin(t_p_argument)
                - Math.cos(t_long_ascending_node)*Math.cos(t_p_argument)*Math.cos(t_inclination));

        double z_s = xp_d * Math.sin(t_p_argument) * Math.sin(t_inclination) + yp_d * Math.cos(t_p_argument) * Math.sin(t_inclination);

        double[] out_speed = new double[]{x_s,y_s,z_s};

        //need to rotate to fit the planet tilt
        out_posistion = ref_rotate(out_posistion);
        out_speed = ref_rotate(out_speed);

        double[] ref_pos = refrence.getOrbit_position();
        double[] ref_speed = refrence.getOrbit_speed();

        out_posistion[0] += ref_pos[0];
        out_posistion[1] += ref_pos[1];
        out_posistion[2] += ref_pos[2];

        out_speed[0] += ref_speed[0];
        out_speed[1] += ref_speed[1];
        out_speed[2] += ref_speed[2];

        double p_speed = Math.sqrt(Math.pow(out_speed[0], 2) + Math.pow(out_speed[1], 2) + Math.pow(out_speed[2], 2));
        double p_pos = Math.sqrt(Math.pow(out_posistion[0], 2) + Math.pow(out_posistion[1], 2) + Math.pow(out_posistion[2], 2));
        System.out.println(name + " | " + p_speed + " | " + speed + " | " + p_pos);

        orbit_posistion = out_posistion.clone();
        orbit_speed = out_speed.clone();
    }


}
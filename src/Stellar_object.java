import java.util.Objects;

public class Stellar_object extends Object_root {
    double[][] orbit_values; // [0][x] == posistion, [1][x] == speed
    private double[] new_pos = new double[3];
    private double obj_mass;
    private String name;
    POI[][] POI_covrage_points;
    private double[][] k_6 = new double[6][3];
    private double[][] k_6_x_pos = new double[2][3];
    private double[][] k_6_x_speed = new double[2][3];
    private double[] k_6_mag_vals = new double[]{0,0.25,0.25,0.5,0.75,1.0};
    private double[][] k_6_y_mag_vals = new double[][]{
            {},
            {0.25},
            {0.125,0.125},
            {0,-0.5,1},
            {3.0/16,0,0,9.0/16},
            {-3.0/7,2.0/7,12.0/7,-12.0/7,8.0/7}
    };
    public Stellar_object(double[][] poss, double mass, String name){
        orbit_values = poss;
        obj_mass = mass;
        this.name = name;
    }
    public POI[][] get_POI_covrage_points(){
        return POI_covrage_points;
    }
    public double[][] getOrbit_values(){
        return orbit_values;
    }
    public double[] getOrbit_values(int n){
        return orbit_values[n];
    }
    public double getOrbit_values(int n, int k){
        return orbit_values[n][k];
    }
    public double getObj_mass(){
        return obj_mass;
    }
    public void euler_orbit_calc(double dt, double[] object_vel){
        new_pos = orbit_values[0];

        orbit_values[1][0] += object_vel[0] * dt;
        orbit_values[1][1] += object_vel[1] * dt;
        orbit_values[1][2] += object_vel[2] * dt;

        new_pos[0] += orbit_values[1][0] * dt;
        new_pos[1] += orbit_values[1][1] * dt;
        new_pos[2] += orbit_values[1][2] * dt;
    }
    public void euler_finish_orbit_calc(){
        orbit_values[0] = new_pos;
    }
    public void runge_kutta_5_y_set(int k,double dt){
        if(k != 0){
            double[] temp = k_6_x_speed[0];
            k_6_x_speed[1] = orbit_values[1];
            for (int i = 0; i < k_6_y_mag_vals[k].length; i++) {
                if (k_6_y_mag_vals[k][i] != 0) {
                    temp[0] += k_6[i][0] * dt * k_6_y_mag_vals[k][i];
                    temp[1] += k_6[i][1] * dt * k_6_y_mag_vals[k][i];
                    temp[2] += k_6[i][2] * dt * k_6_y_mag_vals[k][i];
                }
            }
            orbit_values[1] = temp;
        }
    }
    public void runge_kutta_5_y_revert(){
        orbit_values[1] = k_6_x_speed[1];
    }
    public void runge_kutta_calc_5(double dt, double[] object_vel, int k){
        if(k == 0) {
            k_6_x_pos[0] = orbit_values[0];
            k_6_x_speed[0] = orbit_values[1];
        }

        k_6[k][0] = object_vel[0];
        k_6[k][1] = object_vel[1];
        k_6[k][2] = object_vel[2];

        if(k < 5) {
            double temp_dt = k_6_mag_vals[k+1] * dt;
            orbit_values[1][0] = k_6_x_speed[0][0] + object_vel[0] * temp_dt;
            orbit_values[1][1] = k_6_x_speed[0][1] + object_vel[1] * temp_dt;
            orbit_values[1][2] = k_6_x_speed[0][2] + object_vel[2] * temp_dt;
            new_pos[0] = k_6_x_pos[0][0] + orbit_values[1][0] * temp_dt;
            new_pos[1] = k_6_x_pos[0][1] + orbit_values[1][1] * temp_dt;
            new_pos[2] = k_6_x_pos[0][2] + orbit_values[1][2] * temp_dt;
        }
    }
    public void runge_kutta_update(){
        orbit_values[0] = new_pos;
    }
    public void runge_kutta_finish(double dt){
        orbit_values[1][0] = k_6_x_speed[0][0] + ((7*k_6[0][0]+32*k_6[2][0]+12*k_6[3][0]+32*k_6[4][0]+7*k_6[5][0])/90)*dt;
        orbit_values[1][1] = k_6_x_speed[0][1] + ((7*k_6[0][1]+32*k_6[2][1]+12*k_6[3][1]+32*k_6[4][1]+7*k_6[5][1])/90)*dt;
        orbit_values[1][2] = k_6_x_speed[0][2] + ((7*k_6[0][2]+32*k_6[2][2]+12*k_6[3][2]+32*k_6[4][2]+7*k_6[5][2])/90)*dt;

        double print_speed = Math.sqrt(Math.pow(orbit_values[1][0],2)+Math.pow(orbit_values[1][1],2)+Math.pow(orbit_values[1][2],2));
        if(Objects.equals(name, "s0")){
            System.out.println(print_speed);
        }

        orbit_values[0][0] = k_6_x_pos[0][0] + orbit_values[1][0]*dt;
        orbit_values[0][1] = k_6_x_pos[0][1] + orbit_values[1][1]*dt;
        orbit_values[0][2] = k_6_x_pos[0][2] + orbit_values[1][2]*dt;
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


        out_posistion[0] += refrence.getOrbit_values(0,0);
        out_posistion[1] += refrence.getOrbit_values(0,1);
        out_posistion[2] += refrence.getOrbit_values(0,2);

        out_speed[0] += refrence.getOrbit_values(1,0);
        out_speed[1] += refrence.getOrbit_values(1,1);
        out_speed[2] += refrence.getOrbit_values(1,2);

        double p_speed = Math.sqrt(Math.pow(out_speed[0], 2) + Math.pow(out_speed[1], 2) + Math.pow(out_speed[2], 2));
        double p_pos = Math.sqrt(Math.pow(out_posistion[0], 2) + Math.pow(out_posistion[1], 2) + Math.pow(out_posistion[2], 2));
        System.out.println(name + " | " + p_speed + " | " + speed + " | " + p_pos);

        orbit_values = new double[][]{out_posistion,out_speed};
    }


}
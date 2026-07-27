public class Stellar_object extends Object_root {
    double[][] orbit_values; // [0][x] == posistion, [1][x] == speed
    private double[] new_pos;
    private double obj_mass;
    private String name;
    public Stellar_object(double[][] poss, double mass, String name){
        orbit_values = poss;
        obj_mass = mass;
        this.name = name;
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
    public void orbit_calc(double dt, double[] object_vel){
        new_pos = orbit_values[0];

        orbit_values[1][0] += object_vel[0] * dt;
        orbit_values[1][1] += object_vel[1] * dt;
        orbit_values[1][2] += object_vel[2] * dt;

        new_pos[0] += orbit_values[1][0] * dt;
        new_pos[1] += orbit_values[1][1] * dt;
        new_pos[2] += orbit_values[1][2] * dt;
    }
    public void finish_orbit_calc(){
        orbit_values[0][0] = new_pos[0];
        orbit_values[0][1] = new_pos[1];
        orbit_values[0][2] = new_pos[2];
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
        out_posistion = ref_rotate(refrence,out_posistion);
        out_speed = ref_rotate(refrence,out_speed);


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
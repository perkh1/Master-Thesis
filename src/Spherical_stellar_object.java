public class Spherical_stellar_object extends Stellar_object{
    private double radius_equator;
    private double radius_polar;
    private double rotation = 0; //defined as 1 = 1 hour into the rotation
    private double rotation_speed = 24; //defined as 1 = 1 hour in a day
    private double[] r_axis = new double[]{0,0}; // defined by degrees x = long_ascending y = inclination
    //shape is x^2 / rad_e + y^2 / rad_e + z^2 / rad_pol

    //base spherical stellar object
    public Spherical_stellar_object(double[] poss, double[] speed, double mass, String name, double eq, double pol) {
        super(poss,speed, mass, name);
        radius_equator = eq;
        radius_polar = pol;
    }
    //with rotation
    public Spherical_stellar_object(double[] poss, double[] speed, double mass, String name, double eq, double pol, double rotation_speeed, double s_rotation, double[] rr_axis) {
        super(poss,speed, mass, name);
        radius_equator = eq;
        radius_polar = pol;
        rotation_speed = rotation_speeed;
        rotation = s_rotation;
        r_axis = rr_axis;
    }
    public Spherical_stellar_object(double mass, String name,
        Spherical_stellar_object refrence,
        double long_ascending_node, // omega
        double p_argument, // w
        double anomaly, // v
        double inclination, //i
        double semi_major, // a
        double eccentrisisty, // e
        double rad_equator,
        double rad_polar
    ) {
        super(new double[]{},new double[]{}, mass, name);
        radius_equator = rad_equator;
        radius_polar = rad_polar;
        convert(
                refrence,
                long_ascending_node, // omega
                p_argument, // w
                anomaly, // v
                inclination, //i
                semi_major, //
                eccentrisisty // e
        );
    }
    public Boolean Collision(double[] obj_poss){
        double calc =
                Math.pow((obj_poss[0]-this.orbit_posistion[0])/radius_equator,2) +
                Math.pow((obj_poss[1]-this.orbit_posistion[1])/radius_equator,2)+
                Math.pow((obj_poss[2]-this.orbit_posistion[2])/radius_polar,2);

        if(calc < 1.015){
            //System.out.println("Bang");
            return false;
        }
        if(calc < 500){
            //System.out.println(calc);
        }
        return false;
    }
    public void rotate(double dt){
        rotation+=dt/3600; //secunds to hours
        if (rotation > rotation_speed){
            rotation -= rotation_speed;
        }
        /*
        if(POI_covrage_points != null) {
            for (int i = 0; i < POI_covrage_points.length; i++) {
                for (int j = 0; j < POI_covrage_points[i].length; j++) {
                    POI_covrage_points[i][j].uppdate_pos();
                }
            }
        }

         */
    }
    public double get_rotation(){
        return rotation / rotation_speed;
    }
    public double[] get_r_axis(){
        return r_axis;
    }
    public double get_minor(){
        if(radius_equator < radius_polar){
            return radius_equator;
        }
        else {
            return radius_polar;
        }
    }
    public double get_major(){
        if(radius_equator > radius_polar){
            return radius_equator;
        }
        else {
            return radius_polar;
        }
    }
    public void define_POI_covrage(double[] bounding_latitude, double[] bounding_longitude, double density){
        int num_lat = (int) ((bounding_latitude[1]-bounding_latitude[0]) * density);
        POI_covrage_points = new POI[num_lat+1][];
        int x = 0;
        for (double i = bounding_latitude[0]; i <= bounding_latitude[1]; i += 1/density) {
            int num_long = (int) (Math.cos(i*Math.PI / 180) * density * 360 + 1);
            POI[] temp_POI = new POI[num_long];

            int y = 0;
            for (double j = bounding_longitude[0]; j < bounding_longitude[1] && y < temp_POI.length; j += 360.0 / num_long) {
                temp_POI[y] = new POI(this,j,i,0);
                y++;
            }
            POI_covrage_points[x] = temp_POI;
            x++;
        }
    }
    public void define_POI_covrage(double density){
        define_POI_covrage(new double[]{-90,90},new double[]{-180,180},density);
    }
    public POI[][] get_POI_covrage(){
        return POI_covrage_points;
    }

}

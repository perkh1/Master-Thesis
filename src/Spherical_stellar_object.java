public class Spherical_stellar_object extends Stellar_object{
    private double radius_equator;
    private double radius_polar;
    private double rotation = 0; //defined as 1 = 1 hour into the rotation
    private double rotation_speed = 24; //defined as 1 = 1 hour in a day
    private double[] r_axis = new double[]{0,0}; // defined by degrees x = long_ascending y = inclination
    //shape is x^2 / rad_e + y^2 / rad_e + z^2 / rad_pol

    //base spherical stellar object
    public Spherical_stellar_object(double[][] poss, double mass, double eq, double pol) {
        super(poss, mass);
        radius_equator = eq;
        radius_polar = pol;
    }
    //with rotation
    public Spherical_stellar_object(double[][] poss, double mass, double eq, double pol, double rotation_speeed, double s_rotation, double[] rr_axis) {
        super(poss, mass);
        radius_equator = eq;
        radius_polar = pol;
        rotation_speed = rotation_speeed;
        rotation = s_rotation;
        r_axis = rr_axis;
    }

    public Boolean Collision(double[] obj_poss){
        double calc =
                Math.pow((obj_poss[0]-this.orbit_values[0][0])/radius_equator,2) +
                Math.pow((obj_poss[1]-this.orbit_values[0][1])/radius_equator,2)+
                Math.pow((obj_poss[2]-this.orbit_values[0][2])/radius_polar,2);

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
        rotation+=dt;
        if (rotation > rotation_speed){
            rotation -= rotation_speed;
        }
    }
    public double get_rotation(){
        return rotation;
    }
    public double[] get_r_axis(){
        return r_axis;
    }
}

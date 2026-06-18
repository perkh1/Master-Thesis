public class Spherical_stellar_object extends Stellar_object{
    private double radius_equator;
    private double radius_polar;
    //shape is x^2 / rad_e + y^2 / rad_e + z^2 / rad_pol
    public Spherical_stellar_object(double[][] poss, double mass, double eq, double pol) {
        super(poss, mass);
        radius_equator = eq;
        radius_polar = pol;
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
}

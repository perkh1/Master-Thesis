import java.util.Objects;

public class Sattelite extends Stellar_object{
    private boolean has_collided = false;
    private double[] ref_possition;

    public Sattelite(double[] poss , double[] speed, double mass, String name) {
        super(poss, speed, mass, name);
    }
    public Sattelite(double mass, String name,
                     Spherical_stellar_object refrence,
                     double long_ascending_node, // omega
                     double p_argument, // w
                     double anomaly, // v
                     double inclination, //i
                     double semi_major, // a
                     double eccentrisisty // e
                     ) {

        super(new double[]{},new double[]{}, mass, name);
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
    public void collide(){
        has_collided = true;
    }
    public boolean isHas_collided() {
        return has_collided;
    }
    public void update_ref_position(){
        double[] ref_pos = ref.getOrbit_position();
        double[] dir = new double[3];
        dir[0] = orbit_posistion[0]-ref_pos[0];
        dir[1] = orbit_posistion[1]-ref_pos[1];
        dir[2] = orbit_posistion[2]-ref_pos[2];
        dir = inverse_ref_rotate(dir);

        double r = Math.sqrt(dir[0]*dir[0]+dir[1]*dir[1]+dir[2]*dir[2]);
        double azimuth = Math.atan2(dir[1],dir[0]);
        double polar_angle = Math.acos(dir[2]/r);

        /*
        if(Objects.equals(name, "geo")){
            System.out.println(r + " | " + azimuth/3.14*180 + " | " + polar_angle/Math.PI*180);
        }
         */
    }
    public void update_solar_position(Spherical_stellar_object sun){
        double[] ref_pos = sun.getOrbit_position();
        double[] dir = new double[3];
        dir[0] = orbit_posistion[0]-ref_pos[0];
        dir[1] = orbit_posistion[1]-ref_pos[1];
        dir[2] = orbit_posistion[2]-ref_pos[2];

        double r = Math.sqrt(dir[0]*dir[0]+dir[1]*dir[1]+dir[2]*dir[2]);
        double azimuth = Math.atan2(dir[1],dir[0]);
        double polar_angle = Math.acos(dir[2]/r);

    }

}

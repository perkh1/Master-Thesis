public class Sattelite extends Stellar_object{
    private boolean has_collided = false;

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
}

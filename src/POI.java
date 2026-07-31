// point of intrest (on a spherical stellar object)
public class POI extends Object_root{
    private double[] abs_pos;
    private double[] pos;
    private double[] inter_pos;
    private double in_covrage = 0;
    public POI(Spherical_stellar_object refrence, double longitude, double latitude, double altitude){
        ref = refrence;
        double long_t = longitude * Math.PI / 180;
        double lat_t = latitude * Math.PI / 180;
        double ref_semi_minor = ref.get_minor();
        double ref_semi_major = ref.get_major();

        double e = Math.sqrt(1-Math.pow(ref_semi_minor,2) / Math.pow(ref_semi_major,2));

        double N = ref_semi_major / Math.sqrt(1-Math.pow(e,2) * Math.pow(Math.sin(lat_t),2));

        double x = (N + altitude) * Math.cos(lat_t) * Math.cos(long_t);
        double y = (N + altitude) * Math.cos(lat_t) * Math.sin(long_t);
        double z = (N * (1-Math.pow(e,2) + altitude)) * Math.sin(lat_t);

        inter_pos = new double[]{x,y,z};
        pos = inter_pos;
        abs_pos = ref_rotate(inter_pos);
        abs_pos[0] += ref.getOrbit_values(0,0);
        abs_pos[1] += ref.getOrbit_values(0,1);
        abs_pos[2] += ref.getOrbit_values(0,2);
    }
    public void uppdate_pos(){
        double rot = ref.get_rotation() * 2 * 3.14;
        double[] temp_pos = inter_pos.clone();
        temp_pos[0] = Math.cos(rot) * inter_pos[0] - Math.sin(rot) * inter_pos[1];
        temp_pos[1] = Math.sin(rot) * inter_pos[0] + Math.cos(rot) * inter_pos[1] ;
        pos = temp_pos;
        abs_pos = ref_rotate(temp_pos);
        abs_pos[0] += ref.getOrbit_values(0,0);
        abs_pos[1] += ref.getOrbit_values(0,1);
        abs_pos[2] += ref.getOrbit_values(0,2);
    }
    public double[] get_abs_pos(){
        return abs_pos;
    }
    public double[] get_pos(){
        return pos;
    }
    public void update_covrage(double dt){
        in_covrage += dt;
    }

    public double get_covrage() {
        return in_covrage;
    }
}

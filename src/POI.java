// point of intrest (on a spherical stellar object)
public class POI extends Object_root{
    private double[] abs_pos;
    private double[] inter_pos;
    public POI(Spherical_stellar_object refrence, double longitude, double latitude, double altitude){

        double long_t = longitude * Math.PI / 180;
        double lat_t = latitude * Math.PI / 180;
        double ref_semi_minor = refrence.get_minor();
        double ref_semi_major = refrence.get_major();

        double e = Math.sqrt(1-Math.pow(ref_semi_minor,2) / Math.pow(ref_semi_major,2));

        double N = ref_semi_major / Math.sqrt(1-Math.pow(e,2) * Math.pow(Math.sin(lat_t),2));

        double x = (N + altitude) * Math.cos(lat_t) * Math.cos(long_t);
        double y = (N + altitude) * Math.cos(lat_t) * Math.sin(long_t);
        double z = (N * (1-Math.pow(e,2) + altitude)) * Math.sin(lat_t);

        inter_pos = new double[]{x,y,z};
        abs_pos = ref_rotate(refrence, inter_pos);
        abs_pos[0] += refrence.getOrbit_values(0,0);
        abs_pos[1] += refrence.getOrbit_values(0,1);
        abs_pos[2] += refrence.getOrbit_values(0,2);
    }
    public void time_rotate(Spherical_stellar_object ref){
        double rot = ref.get_rotation() * 2 * 3.14;
        double[] temp_pos = inter_pos.clone();
        temp_pos[0] *= Math.cos(rot);
        temp_pos[1] *= Math.sin(rot);
        abs_pos = ref_rotate(ref, temp_pos);
        abs_pos[0] += ref.getOrbit_values(0,0);
        abs_pos[1] += ref.getOrbit_values(0,1);
        abs_pos[2] += ref.getOrbit_values(0,2);
    }
    public double[] get_pos(){
        return abs_pos;
    }
}

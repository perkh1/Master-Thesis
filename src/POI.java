// point of intrest (on a spherical stellar object)
public class POI {
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


    }
}

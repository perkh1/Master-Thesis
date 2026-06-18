public class Stellar_object {
    double[][] orbit_values;
    private double[] new_pos;
    private double obj_mass;
    public Stellar_object(double[][] poss, double mass){
        orbit_values = poss;
        obj_mass = mass;
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
}

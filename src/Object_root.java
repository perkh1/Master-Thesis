public class Object_root {
    Spherical_stellar_object ref;
    double[] ref_rotate(double[] imp){
        double arg = ref.get_r_axis()[0];
        double inc = ref.get_r_axis()[1];
        double x = imp[0] * (Math.pow(Math.cos(arg),2) * Math.cos(inc) + Math.pow(Math.sin(arg),2));
        x += imp[1] * (Math.sin(arg)*Math.cos(arg)*(1-Math.cos(inc)));
        x -= imp[2] * (Math.cos(arg)*Math.sin(inc));

        double y = imp[0] * (Math.sin(arg)*Math.cos(arg)*(1-Math.cos(inc)));
        y += imp[1] * (Math.pow(Math.sin(arg),2) * Math.cos(inc) + Math.pow(Math.cos(arg),2));
        y += imp[2] * (Math.sin(arg)*Math.sin(inc));

        double z = imp[0] * (Math.cos(arg)*Math.sin(inc)) - imp[1]*(Math.sin(arg)*Math.sin(inc))+ imp[2]*Math.cos(inc);

        return new double[]{x,y,z};
    }
}

public class Sattelite extends Stellar_object{
    private boolean has_collided = false;

    public Sattelite(double[][] poss, double mass) {
        super(poss, mass);
    }
    public void collide(){
        has_collided = true;
    }
    public boolean isHas_collided() {
        return has_collided;
    }
}

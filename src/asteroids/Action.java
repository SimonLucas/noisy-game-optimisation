package asteroids;

/**
 * Created by Simon M. Lucas
 * sml@essex.ac.uk
 * Date: 26/12/11
 * Time: 15:09
 */
public class Action {
    public double thrust;
    public double turn;
    public boolean shoot;

    public Action() {}

    public Action(double thrust, double turn, boolean shoot) {
        this.thrust = thrust;
        this.turn = turn;
        this.shoot = shoot;
    }

    public Action(Action a) {
        thrust = a.thrust;
        turn = a.turn;
        shoot = a.shoot;
    }

    public String toString() {
        return thrust + " : " + turn + " : " + shoot;
    }
}

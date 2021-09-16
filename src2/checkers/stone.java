package src2.checkers;
import java.awt.geom.Ellipse2D;

public class stone extends Ellipse2D.Double{
    
    boolean damone = false;
    boolean dead = false;
    String dotDirection;

    stone(){

        width = checkers.blocksize;
        height = checkers.blocksize;
    }

    stone(double x, double y){
        this.x = x;
        this.y =y;
        width = checkers.blocksize;
        height = checkers.blocksize;
    }

    stone(double x, double y, boolean dot){
        this.x = x;
        this.y =y;
        width = 10;
        height = 10;
    }

    stone(double x, double y, boolean dot, String dd){
        this.x = x;
        this.y =y;
        width = 10;
        height = 10;
        dotDirection =dd;
    }

    stone(String isdamone, double x, double y){
        this.x = x;
        this.y =y;
        width = checkers.blocksize;
        height = checkers.blocksize;
        if (isdamone.equals("t")) damone=true;
        else damone=false;
    }

    public void changePos(double x, double y){
        this.x = x;
        this.y =y;
    }
}

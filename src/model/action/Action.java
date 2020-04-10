package model.action;


public abstract class Action {

    public abstract Action executeAndInverse();

    public abstract String getName();

    public abstract String getInverseName();
}

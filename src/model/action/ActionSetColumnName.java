package model.action;

import model.dataframe.Dataframe;

public class ActionSetColumnName extends Action {
    private Dataframe df;
    private int col;
    private String newName;

    public ActionSetColumnName(Dataframe df, int col, String newName) {
        this.df = df;
        this.col = col;
        this.newName = newName;
    }

    @Override
    public Action executeAndInverse() {
        String oldName = df.getColumnName(col);
        df.setColumnName(col, newName);
        return new ActionSetColumnName(df, col, oldName);
    }

    @Override
    public String getName() {
        return "Rename column";
    }

    @Override
    public String getInverseName() {
        return "Rename column";
    }
}

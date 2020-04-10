package model.action;

import model.dataframe.Column;
import model.dataframe.Dataframe;

public class ActionAddColumn extends Action {
    private Dataframe df;
    private Column column;
    private int pos;

    public ActionAddColumn(Dataframe df, Column removed, int col) {
        this.df = df;
        this.column = removed;
        this.pos = col;
    }

    @Override
    public Action executeAndInverse() {
        df.addColumn(column, pos);
        return new ActionRemoveColumn(df, pos);
    }

    @Override
    public String getName() {
        return "Add column";
    }

    @Override
    public String getInverseName() {
        return "Remove column";
    }
}

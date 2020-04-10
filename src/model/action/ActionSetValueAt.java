package model.action;

import model.dataframe.Dataframe;

public class ActionSetValueAt extends Action {
    private int col;
    private int row;
    private String value;
    private Dataframe df;

    public ActionSetValueAt(Dataframe df, int col, int row, String value) {
        this.df = df;
        this.col = col;
        this.row = row;
        this.value = value;
    }

    @Override
    public Action executeAndInverse() {
        String oldValue = df.getValueAt(col, row);
        df.setValueAt(col, row, value);
        return new ActionSetValueAt(df, col, row, oldValue);
    }

    @Override
    public String getName() {
        return "Set value";
    }

    @Override
    public String getInverseName() {
        return "Set value";
    }
}

package model.dataframe;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DataframeTest {

    @Test
    void constructor() {
        try {
            new Dataframe();

            fail("Created dataframe with 0 columns");
        } catch (IllegalArgumentException ignored) {}

        try {
            new Dataframe(new Column("colname1"), new Column("colname2", "value1"));

            fail("Created dataframe with varying length of columns");
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    void ncol() {
        Dataframe df = new Dataframe(new Column("colname1"));
        assertEquals(1, df.ncol());

        Dataframe df2 = new Dataframe(new Column("colname1"),
                new Column("colname2"));
        assertEquals(2, df2.ncol());
    }

    @Test
    void nrow() {
        Dataframe df = new Dataframe(new Column("colname1"));
        assertEquals(0, df.nrow());

        Dataframe df2 = new Dataframe(new Column("colname1", 1),
                new Column("colname2", "value1"));
        assertEquals(1, df2.nrow());

        Dataframe df3 = new Dataframe(new Column("colname1", 1, 2),
                new Column("colname2", "value1", "value2"));
        assertEquals(2, df3.nrow());
    }

    @Test
    void getValueAt() {
        Dataframe df = new Dataframe(new Column("colname1"));

        Dataframe df2 = new Dataframe(new Column("colname1", 1),
                new Column("colname2", "value1"));

        Dataframe df3 = new Dataframe(new Column("colname1", 1, 2),
                new Column("colname2", "value1", "value2"));

        try {
            Object val = df.getValueAt(0, 0);
            fail("Dataframe df returned value at invalid position (0, 0): " + val);
        } catch (IllegalArgumentException ignored) {}

        try {
            Object val = df.getValueAt(-1, 0);
            fail("Dataframe df returned value at invalid position (-1, 0): " + val);
        } catch (IllegalArgumentException ignored) {}

        try {
            Object val = df.getValueAt(0, -1);
            fail("Dataframe df returned value at invalid position (0, -1): " + val);
        } catch (IllegalArgumentException ignored) {}

        try {
            Object val = df2.getValueAt(0, 3);
            fail("Dataframe df2 returned value at invalid position (0, 3): " + val);
        } catch (IllegalArgumentException ignored) {}

        try {
            Object val = df2.getValueAt(2, 0);
            fail("Dataframe df2 returned value at invalid position (2, 0): " + val);
        } catch (IllegalArgumentException ignored) {}

        assertEquals("1", df2.getValueAt(0, 0));
        assertEquals("value1", df2.getValueAt(1, 0));

        assertEquals("1", df3.getValueAt(0, 0));
        assertEquals("value1", df3.getValueAt(1, 0));
        assertEquals("2", df3.getValueAt(0, 1));
        assertEquals("value2", df3.getValueAt(1, 1));
    }

    @Test
    void setValueAt() {
        Dataframe df = new Dataframe(new Column("colname1"));

        Dataframe df2 = new Dataframe(new Column("colname1", 1),
                new Column("colname2", "value1"));

        Dataframe df3 = new Dataframe(new Column("colname1", 1, 2),
                new Column("colname2", "value1", "value2"));

        try {
            df.setValueAt(0, 0, "42");
            fail("Dataframe df allowed to set value at invalid position (0, 0): ");
        } catch (IllegalArgumentException ignored) {}

        try {
            df.setValueAt(-1, 0, "42");
            fail("Dataframe df allowed to set value at invalid position (-1, 0): ");
        } catch (IllegalArgumentException ignored) {}

        try {
            df.setValueAt(0, -1, "42");
            fail("Dataframe df allowed to set value at invalid position (0, -1): ");
        } catch (IllegalArgumentException ignored) {}

        try {
            df2.setValueAt(0, 3, "42");
            fail("Dataframe df2 allowed to set value at invalid position (0, 3): ");
        } catch (IllegalArgumentException ignored) {}

        try {
            df2.setValueAt(2, 0, "42");
            fail("Dataframe df2 allowed to set value at invalid position (2, 0): ");
        } catch (IllegalArgumentException ignored) {}

        df2.setValueAt(0, 0, "-42");
        assertEquals("-42", df2.getValueAt(0, 0));
        df2.setValueAt(1, 0, "newValue1");
        assertEquals("newValue1", df2.getValueAt(1, 0));

        df3.setValueAt(0, 0, "42");
        assertEquals("42", df3.getValueAt(0, 0));
        df3.setValueAt(1, 0, "newValue1");
        assertEquals("newValue1", df3.getValueAt(1, 0));
        df3.setValueAt(0, 1, "21");
        assertEquals("21", df3.getValueAt(0, 1));
        df3.setValueAt(1, 1, "abc");
        assertEquals("abc", df3.getValueAt(1, 1));


        df3.setValueAt(0, 1, "a");
        assertEquals("a", df3.getValueAt(0, 1));
        assertEquals("42", df3.getValueAt(0, 0));
    }

    @Test
    void addColumn() {
        Dataframe df = new Dataframe(new Column("colname1", 1, 2));
        Column col = new Column("colname2", "value1", "value2");
        df.addColumn(col, 1);
        assertEquals("1", df.getValueAt(0, 0));
        assertEquals("value1", df.getValueAt(1, 0));

        Dataframe df2 = new Dataframe(new Column("colname1", 1, 2));
        Column col2 = new Column("colname2", "value1", "value2");
        df2.addColumn(col2, 0);
        assertEquals("value1", df2.getValueAt(0, 0));
        assertEquals("1", df2.getValueAt(1, 0));
    }

    @Test
    void removeColumn() {
        Dataframe df = new Dataframe(new Column("colname1"));

        try {
            df.removeColumn(1);
            fail("Removed column with invalid (too big) index.");
        } catch (IllegalArgumentException ignored) {}

        try {
            df.removeColumn(-1);
            fail("Removed column with invalid (negative) index.");
        } catch (IllegalArgumentException ignored) {}

        try {
            df.removeColumn(0);
            fail("Removed column leaving empty dataframe");
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    void getColumnName() {
        Dataframe df = new Dataframe(new Column("colname1"));


        Dataframe df2 = new Dataframe(new Column("colname1b", 1),
                new Column("colname2b", "value1"));

        Dataframe df3 = new Dataframe(new Column("colname1c", 1, 2),
                new Column("colname2c", "value1", "value2"));

        try {
            String name = df2.getColumnName(-1);
            fail("Dataframe df2 allowed to get column name at negative position: " + name);
        } catch (IllegalArgumentException ignore) {}

        try {
            String name = df2.getColumnName(2);
            fail("Dataframe df2 allowed to get column name at too big position: " + name);
        } catch (IllegalArgumentException ignore) {}

        assertEquals("colname1", df.getColumnName(0));

        assertEquals("colname1b", df2.getColumnName(0));
        assertEquals("colname2b", df2.getColumnName(1));

        assertEquals("colname1c", df3.getColumnName(0));
        assertEquals("colname2c", df3.getColumnName(1));
    }

    @Test
    void setColumnName() {
        Dataframe df = new Dataframe(new Column("colname1"));


        Dataframe df2 = new Dataframe(new Column("colname1b", 1),
                new Column("colname2b", "value1"));

        Dataframe df3 = new Dataframe(new Column("colname1c", 1, 2),
                new Column("colname2c", "value1", "value2"));

        try {
            df2.setColumnName(-1, "asdasd");
            fail("Dataframe df2 allowed to set column name at negative position.");
        } catch (IllegalArgumentException ignore) {}

        try {
            df2.setColumnName(2 ,"dsasafd");
            fail("Dataframe df2 allowed to set column name at too big position.");
        } catch (IllegalArgumentException ignore) {}

        df.setColumnName(0, "acd");
        assertEquals("acd", df.getColumnName(0));

        df2.setColumnName(0, "");
        assertEquals("", df2.getColumnName(0));
        df2.setColumnName(1, "colname2b");
        assertEquals("colname2b", df2.getColumnName(1));

        df3.setColumnName(0, "0");
        assertEquals("0", df3.getColumnName(0));
        df3.setColumnName(1, "sdfsadfsadf");
        df3.setColumnName(1, "colname2c");
        assertEquals("colname2c", df3.getColumnName(1));
    }

    @Test
    void swapColumns() {
        Dataframe df = new Dataframe(new Column("colname1"));

        Dataframe df2 = new Dataframe(new Column("colname1b", 1),
                new Column("colname2b", "value1"));

        try {
            df2.swapColumns(0, 2);
            fail("Dataframe df2 allowed to swap columns at invalid position (too big).");
        } catch (IllegalArgumentException ignore) {}

        try {
            df2.swapColumns(-1, 0);
            fail("Dataframe df2 allowed to swap columns at invalid position. (negative)");
        } catch (IllegalArgumentException ignore) {}

        df.swapColumns(0, 0);
        assertEquals(df.getColumnName(0), "colname1");


        df2.swapColumns(0, 1);
        assertEquals(df2.getColumnName(0), "colname2b");
        assertEquals(df2.getColumnName(1), "colname1b");
    }

    private void printDataframe(Dataframe df) {
        for (int i = 0; i < df.ncol(); i++) {
            System.out.print(i + ". [" + df.getColumnName(i) + "]: " );
            for (int j = 0; j < df.nrow(); j++) {
                System.out.print(df.getValueAt(i, j) + ", ");
            }
            System.out.print("\n");
        }
    }

    @Test
    void readCSV() throws IOException {
        Dataframe dataframe1 = Dataframe.readCSV("test/resources/test1.csv", true);
        printDataframe(dataframe1);

        assertEquals("a", dataframe1.getColumnName(0));
        assertEquals("cdef", dataframe1.getColumnName(2));

        assertEquals("g", dataframe1.getValueAt(2, 0));
        assertEquals("ijk", dataframe1.getValueAt(2, 2));
    }

    @Test
    void writeCSV() throws IOException {
        String path1 = "test/resources/test1.csv";
        Dataframe dataframe1 = Dataframe.readCSV(path1, true);

        String path2 = "test/resources/test1-copy.csv";
        dataframe1.writeCSV(path2, true);

        Dataframe dataframe2 = Dataframe.readCSV(path2, true);
        printDataframe(dataframe2);

        assertEquals(dataframe1, dataframe2);
    }

}
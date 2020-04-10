package model.dataframe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColumnTest {

    @Test
    void getName() {
        String name1 = "name1";
        Column column1 = new Column(name1);
        assertEquals(name1, column1.getName());

        String name2 = "Name2";
        Column column2 = new Column(name2, 12, 0, 3);
        assertEquals(name2, column2.getName());

        String name3 = "";
        Column column3 = new Column(name3);
        assertEquals(name3, column3.getName());
    }

    @Test
    void setName() {
        String name1 = "name1";
        Column column1 = new Column(name1);
        assertEquals(name1, column1.getName());

        String name2 = "name1";
        column1.setName(name2);
        assertEquals(name2, column1.getName());

        String name3 = "";
        column1.setName(name3);
        assertEquals(name3, column1.getName());

        column1.setName(name1);
        assertEquals(name1, column1.getName());
    }

    @Test
    void length() {
        Column column1 = new Column("sadsad");
        assertEquals(0, column1.length());

        Column column2 = new Column("sadsad", 0);
        assertEquals(1, column2.length());

        Column column3 = new Column("sadsad", 23442, -234, 23, 0, -1);
        assertEquals(5, column3.length());
    }

    @Test
    void getValueAt() {
        Column column1 = new Column("sadsad", -3);
        assertEquals("-3", column1.getValueAt(0));

        Column column2 = new Column("sadsad", -3, 3, 0, 13, 45);
        assertEquals("-3", column2.getValueAt(0));
        assertEquals("3", column2.getValueAt(1));
        assertEquals("45", column2.getValueAt(4));
    }

    @Test
    void getValueAtInvalidArgument() {
        Column column2 = new Column("sadsad", -3, 3, 0, 13, 45);

        int posNeg = -1;
        try {
            String a = column2.getValueAt(posNeg);

            fail("Column returned value at negative position: " + a);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Not possible to access row " + posNeg + ".");
        }

        int posTooBig = 5;
        try {
            String a = column2.getValueAt(posTooBig);

            fail("Column returned value at too big position: " + a);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Column contains " + column2.length() +
                    " rows. Not possible to select row " + posTooBig + ".");
        }
    }

    @Test
    void setValueAt() {
        Column column1 = new Column("sadsad", -3);
        column1.setValueAt(0, "7");
        assertEquals("7", column1.getValueAt(0));
        column1.setValueAt(0, "-3");
        assertEquals("-3", column1.getValueAt(0));

        Column column2 = new Column("sadsad", -3, 3, 0, 13, 45);
        column2.setValueAt(0, "8");
        assertEquals("8", column2.getValueAt(0));
        column2.setValueAt(1, "-345");
        assertEquals("-345", column2.getValueAt(1));
        column2.setValueAt(4, "107");
        assertEquals("107", column2.getValueAt(4));
    }

    @Test
    void setValueAtInvalidArgument() {
        Column column2 = new Column("sadsad", -3, 3, 0, 13, 45);

        int posNeg = -1;
        try {
            column2.setValueAt(posNeg, "42");

            fail("Column allowed to set value at negative position.");
        } catch (IllegalArgumentException ignore) {}

        int posTooBig = 5;
        try {
            column2.setValueAt(posTooBig, "42");

            fail("Column allowed to set value at too big position.");
        } catch (IllegalArgumentException ignore) {}
    }

    @Test
    void equals() {
        Column column1 = new Column("sadsad", -3, 3, 0, 13, 45);
        Column column2 = new Column("sadsad", -3, 3, 0, 13, 45);
        assertEquals(column1, column2);

        Column column3 = new Column("sadsad");
        Column column4 = new Column("sadsad");
        assertEquals(column3, column4);

        assertNotEquals(column1, column3); //different length

        Column column5 = new Column("s", -3, 3, 0, 13, 45);
        assertNotEquals(column1, column5); //different name


        Column column6 = new Column("sadsad", "c");
        Column column7 = new Column("sadsad", "c");
        assertEquals(column6, column7);


        Column column8 = new Column("sadsad", "c ");
        assertNotEquals(column6, column8); //different values

        Column column9 = new Column("sadsad", -3, 0, 3, 13, 45);
        assertNotEquals(column1, column9); //different permutation of values
    }

}
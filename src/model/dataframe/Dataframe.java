package model.dataframe;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class Dataframe {
    private ArrayList<Column> columns;

    public Dataframe(Column... columns) {
        if (columns.length >= 1) {
            int nrow = columns[0].length();
            boolean areAllSameLength = Arrays.stream(columns).allMatch(column -> (column.length() == nrow));
            if (areAllSameLength) {
                this.columns = new ArrayList<>();
                Collections.addAll(this.columns, columns);
            } else {
                throw new IllegalArgumentException("All columns must be same length.");
            }

        } else {
            throw new IllegalArgumentException("Dataframe must contain at least 1 column.");
        }
    }

    public Column getColumn(int col) {
        if (col >= this.ncol()) {
            throw new IllegalArgumentException("Dataframe contains " + this.ncol() +
                    " columns. Not possible to select column " + col + ".");
        }

        if (col < 0) {
            throw new IllegalArgumentException("Not possible to access column " + col + ".");
        }

        return this.columns.get(col);
    }

    public int ncol() {
        return this.columns.size();
    }

    public int nrow() {
        return this.getColumn(0).length();
    }

    public String getValueAt(int col, int row) {
        if (col >= this.ncol()) {
            throw new IllegalArgumentException("Dataframe contains " + this.ncol() +
                    " columns. Not possible to select column " + col + ".");
        }

        if (row >= this.nrow()) {
            throw new IllegalArgumentException("Dataframe contains " + this.nrow() +
                    " columns. Not possible to select column " + row + ".");
        }

        if (col < 0) {
            throw new IllegalArgumentException("Not possible to access column " + col + ".");
        }

        if (row < 0) {
            throw new IllegalArgumentException("Not possible to access row " + col + ".");
        }


        return this.getColumn(col).getValueAt(row);
    }

    public void setValueAt(int col, int row, String value) {
        if (col >= this.ncol()) {
            throw new IllegalArgumentException("Dataframe contains " + this.ncol() +
                    " columns. Not possible to access column " + col + ".");
        }

        if (row >= this.nrow()) {
            throw new IllegalArgumentException("Dataframe contains " + this.nrow() +
                    " rows. Not possible to access row " + row + ".");
        }

        if (col < 0) {
            throw new IllegalArgumentException("Not possible to access column " + col + ".");
        }

        if (row < 0) {
            throw new IllegalArgumentException("Not possible to access row " + col + ".");
        }


        this.getColumn(col).setValueAt(row, value);
    }

    /**
     *
     * @param column column to add
     * @param pos column target position
     */
    public void addColumn(Column column, int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("Not possible to add column at negative position." + pos);
        }

        if (pos > this.ncol()) {
            throw new IllegalArgumentException("Dataframe contains " + this.ncol() +
                    " columns. Not possible to add column at position " + pos + ".");
        }

        if (column.length() != this.nrow()) {
            throw new IllegalArgumentException("Dataframe contains " + this.nrow() +
                    " columns. Not possible to merge with column containing " + column.length() + " rows.");
        }

        this.columns.add(pos, column);
    }

    public void removeColumn(int col) {
        if (col >= this.ncol()) {
            throw new IllegalArgumentException("Dataframe contains " + this.ncol() +
                    " columns. Not possible to access column " + col + ".");
        }

        if (col < 0) {
            throw new IllegalArgumentException("Not possible to access column " + col + ".");
        }

        if (this.ncol() == 1) {
            throw new IllegalArgumentException("Not possible to remove column, because this would leave dataframe with 0 columns.");
        }

        this.columns.remove(col);
    }

    public String getColumnName(int col) {
        if (col >= this.ncol()) {
            throw new IllegalArgumentException("Dataframe contains " + this.ncol() +
                    " columns. Not possible to access column " + col + ".");
        }

        if (col < 0) {
            throw new IllegalArgumentException("Not possible to access column " + col + ".");
        }

        return this.getColumn(col).getName();
    }

    public void setColumnName(int col, String name) {
        if (col >= this.ncol()) {
            throw new IllegalArgumentException("Dataframe contains " + this.ncol() +
                    " columns. Not possible to access column " + col + ".");
        }

        if (col < 0) {
            throw new IllegalArgumentException("Not possible to access column " + col + ".");
        }

        this.getColumn(col).setName(name);
    }

    public void swapColumns(int col1, int col2) {
        if (col1 >= this.ncol()) {
            throw new IllegalArgumentException("Dataframe contains " + this.ncol() +
                    " columns. Not possible to access column " + col1 + ".");
        }

        if (col1 < 0) {
            throw new IllegalArgumentException("Not possible to access column " + col1 + ".");
        }

        if (col2 >= this.ncol()) {
            throw new IllegalArgumentException("Dataframe contains " + this.ncol() +
                    " columns. Not possible to access column " + col2 + ".");
        }

        if (col2 < 0) {
            throw new IllegalArgumentException("Not possible to access column " + col2 + ".");
        }

        Column columnBackuped = this.getColumn(col1);
        this.columns.set(col1, this.getColumn(col2));
        this.columns.set(col2, columnBackuped);
    }

    public static Dataframe readCSV(String path, boolean hasHeader) throws IOException {
        FileReader fileReader = new FileReader(path);
        CSVReader csvReader = new CSVReader(fileReader);

        String[] record;
        record = csvReader.readNext();
        int ncol = record.length;
        String[] header = new String[ncol];

        if (hasHeader) {
            header = record;
            record = csvReader.readNext();
        } else {
            for (int i = 0; i < ncol; i++) {
                header[i] = "column " + i;
            }
        }

        // reading columns into Lists
        ArrayList<ArrayList<String>> readByColumns = new ArrayList<>();
        for (int i = 0; i < ncol; i++) {
            readByColumns.add(new ArrayList<>());
        }
        while (record != null) {
            if (record.length == ncol) {
                for (int i = 0; i < ncol; i++) {
                    readByColumns.get(i).add(record[i]);
                }
            } else if (record.length == 1 && record[0].trim().isEmpty()) {
                //skipping line
            }  else {
                throw new IOException("Invalid CSV File. Varying length of records.");
            }

            record = csvReader.readNext();
        }

        // checking casting to int / double, and creating Dataframe
        Column[] columns = new Column[ncol];
        for (int i = 0; i < ncol; i++) {
            columns[i] = new Column(header[i], readByColumns.get(i));
        }

        csvReader.close();
        fileReader.close();

        return new Dataframe(columns);
    }

    public void writeCSV(String path, boolean hasHeader) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(path));

        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        if (hasHeader) {
            String[] header = new String[this.ncol()];
            for (int j = 0; j < this.ncol(); j++) {
                header[j] = this.getColumnName(j);
            }
            csvWriter.writeNext(header);
        }

        for (int i = 0; i < this.nrow(); i++) {
            String[] record = new String[this.ncol()];
            for (int j = 0; j < this.ncol(); j++) {
                record[j] = this.getValueAt(j, i);
            }
            csvWriter.writeNext(record);
        }

        csvWriter.close();
        writer.close();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!Dataframe.class.equals(obj.getClass())) {
            return false;
        }

        Dataframe df2 = (Dataframe) obj;

        if (df2.ncol() != this.ncol()) {
            return false;
        }

        for (int i = 0; i < this.ncol(); i++) {
            if (!df2.getColumn(i).equals(this.getColumn(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns);
    }

    public void select(List<Integer> indices) {
        for (Column column : this.columns) {
            column.select(indices);
        }
    }

    public List<Integer> arrangePermutation(int col, Comparator<String> comparator) {
         return this.getColumn(col).arrangePermutation(comparator);
    }

    public List<Integer> filterIndices(int col, Predicate<String> predicate) {
        return this.getColumn(col).filterIndices(predicate);
    }

}

package ui.directedgraph;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

class TableRenderer extends DefaultTableCellRenderer {

    private int row, col;
    Color randomColour;
    boolean colorCell = true;

    private Vector<Object> allCommands = new Vector<Object>();
    private Vector<Color> allColors = new Vector<Color>();
    List<String> onPathBehaviors;
    List<String> offPathBehaviors;

    public TableRenderer(List<String> onPathBehavior, List<String> offPathBehavior) {

        this.onPathBehaviors = onPathBehavior;
        this.offPathBehaviors = offPathBehavior;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        this.row = row;
        this.col = column;

        if (this.col == 0) {
            this.colorCell = false;
        } else {
            this.colorCell = true;
        }

        if (value != null && value != "") {
            if (onPathBehaviors.contains(value.toString() + table.getColumnName(column))) {
                this.colorCell = true;
                randomColour = Color.green;
            } else if (offPathBehaviors.contains(value.toString() + table.getColumnName(column))) {
                randomColour = Color.red;
            }
        } else {
            this.colorCell = false;
        }
        // Allow superclass to return rendering component.

        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    protected void setValue(Object v) {

        super.setValue(v);

        if (this.colorCell) {
            setBackground(randomColour);
        } else {
            setForeground(UIManager.getColor("Table.foreground"));
            setBackground(UIManager.getColor("Table.background"));
        }

    }
}

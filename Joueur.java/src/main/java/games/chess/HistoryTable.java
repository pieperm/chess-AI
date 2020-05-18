package games.chess;

import java.util.*;

/**
 * History table that tracks the score for effective actions
 */
public class HistoryTable {

    LinkedHashMap<String, Integer> table;  // maps actions --> scores

    /**
     * Constructor for HistoryTable, initializes HashMap
     */
    public HistoryTable() {
        table = new LinkedHashMap<>();
    }

    /**
     * Get the score associated with a particular action
     * @param action the action in UCI notation
     * @return the relative score of the action, representing its effectiveness
     */
    public Integer getScore(String action) {
        return table.get(action);
    }

    /**
     * Determine whether an action exists in the history table
     * @param action the action in UCI notation
     * @return true if the action is a key in the table, false if not
     */
    public boolean containsAction(String action) {
        return table.containsKey(action);
    }

    /**
     * Adds or updates the score for a specific action
     * @param action the action in UCI notation
     * @param score the score to correlate with the action
     */
    public void setScore(String action, Integer score) {
        table.put(action, score);
    }

    /**
     * Inserts the action into the table if it is not yet in it, giving
     * it an initial score of 0. If the action is already in the table,
     * the score is increased by increment
     * @param action the action to insert or update in the table
     * @param increment the amount to increase the score by if the action is already present
     */
    public void insertOrIncrement(String action, Integer increment) {
        if(containsAction(action)) {
            setScore(action, getScore(action) + increment);
        } else {
            setScore(action, 0);
        }
    }

    /**
     * Sorts the HistoryTable
     */
    public List<Map.Entry<String, Integer>> sort() {
        List<Map.Entry<String, Integer>> sortedTable =
                new ArrayList<>(table.entrySet());
        sortedTable.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return sortedTable;
    }

    /**
     * Prints the history table in format "action: score"
     * Each entry is on its own line
     */
    public void print() {
        for(String action : table.keySet()) {
            System.out.println(action + ": " + getScore(action));
        }
    }

}

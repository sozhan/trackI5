package tmm.tracki5.model;

/**
 * Created by Arun on 19/02/16.
 */
public class TaskListModel {
    String name;
    int value; /* 0 -&gt; checkbox disable, 1 -&gt; checkbox enable */

    public TaskListModel(String name, int value){
        this.name = name;
        this.value = value;
    }
    public String getName(){
        return this.name;
    }
    public int getValue(){
        return this.value;
    }
}

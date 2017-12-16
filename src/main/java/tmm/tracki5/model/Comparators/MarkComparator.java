package tmm.tracki5.model.Comparators;

import java.util.Comparator;

/**
 * Created by Mohan on 4/3/2016.
 */
public class MarkComparator implements Comparator<Object> {
    @Override
    public int compare(Object lhs, Object rhs) {
        int lhsMark = 0;
        int rhsMark = 0;
        try
        {
            lhsMark = lhs.getClass().getField("Mark").getInt(lhs);
            rhsMark = rhs.getClass().getField("Mark").getInt(rhs);
        }
        catch (NoSuchFieldException ex)
        {

        }
        catch (IllegalAccessException iex) { }
        return (lhsMark - rhsMark);

    }
}

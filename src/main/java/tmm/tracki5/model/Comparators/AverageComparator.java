package tmm.tracki5.model.Comparators;

import java.util.Comparator;

/**
 * Created by Mohan on 4/3/2016.
 */
public class AverageComparator implements Comparator<Object> {
    public int compare(Object lhs, Object rhs)
    {
        double lhsAvg = 0;
        double rhsAvg = 0;
        try
        {
            lhsAvg = lhs.getClass().getField("Average").getDouble(lhs);
            rhsAvg = rhs.getClass().getField("Average").getDouble(rhs);
        }
        catch (NoSuchFieldException ex)
        {

        }
        catch (IllegalAccessException iex) { }
        return (int)((lhsAvg - rhsAvg) * 100);      //For more precision.
    }
}

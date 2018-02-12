package my.beautycamera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Administrator
 *         Created by Administrator on 2018-01-24.
 */

public class FilterCompareMgr
{
    public static final int KEY_ADD = 1;
    public static final int KEY_REMOVE = 2;


    public static class TEST
    {
        int m_uri = 0;
    }

    /**
     * @param srcArr 原数据
     * @param dstArr 新数据
     * @return
     */
    public static HashMap<Integer, ArrayList<Integer>> compare(ArrayList<TEST> srcArr, ArrayList<TEST> dstArr)
    {
        HashMap<Integer, ArrayList<Integer>> out = new HashMap<>();
        ArrayList<Integer> add = new ArrayList<>();
        ArrayList<Integer> remove = new ArrayList<>();
        out.put(KEY_ADD, add);
        out.put(KEY_REMOVE, remove);

        if (srcArr != null && dstArr != null)
        {
            label:
            for (TEST dst : dstArr)
            {
                if (dst != null)
                {
                    int dstId = dst.m_uri;
                    boolean hasSame = false;
                    Iterator<TEST> srcItera = srcArr.iterator();
                    while (srcItera.hasNext())
                    {
                        TEST src = srcItera.next();
                        if (src != null && src.m_uri == dstId)
                        {
                            hasSame = true;
                            srcItera.remove();
                            continue label;
                        }
                    }
                    if (!hasSame)
                    {
                        add.add(dstId);
                    }
                }
            }

            if (srcArr.size() > 0)
            {
                for (TEST itemInfo : srcArr)
                {
                    if (itemInfo != null)
                    {
                        remove.add(itemInfo.m_uri);
                    }
                }
            }
        }
        return out;
    }
}

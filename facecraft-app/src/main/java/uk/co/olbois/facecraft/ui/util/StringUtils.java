package uk.co.olbois.facecraft.ui.util;

import java.util.List;

public class StringUtils {

    public  static <T> String join(List<T> strings, String s) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for(T obj : strings) {
            if (!isFirst)
                builder.append(s);
            builder.append(obj.toString());
            isFirst = false;
        }
        return builder.toString();
    }
}

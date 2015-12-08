package org.cloudoholiq.catalog.common;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by vrastil on 12.1.2015.
 */
public class StringUtil {

    public static String normalizeId(String st){
        if(st == null || st.isEmpty()){
            return null;
        }
        String trim = st.trim();
        String deAccent = deAccent(trim);
        return deAccent.replaceAll("\\s+", "-").toLowerCase();
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

}

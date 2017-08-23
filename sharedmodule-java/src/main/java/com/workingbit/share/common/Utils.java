package com.workingbit.share.common;

import org.apache.commons.lang3.ObjectUtils;

/**
 * Created by Aleksey Popryaduhin on 12:01 12/08/2017.
 */
public class Utils {

  public static boolean isBlank(String s) {
    if (s == null) {
      return true;
    }
    for (int i = 0; i < s.length(); i++) {
      if (!Character.isWhitespace(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static <T> T clone(T object) {
    return ObjectUtils.clone(object);
  }
}

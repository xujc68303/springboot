package com.util.string;

import java.util.Iterator;

/**
 * @Version 1.0
 * @ClassName StringUilt
 * @Author jiachenXu
 * @Date 2019/9/21
 * @Description 字符串工具类
 */
public class StringUtil {

    /**
     * 空字符串
     */
    public static final String EMPTY_STRING = "";

    /*==============================================================================================================
     *
     * 判空函数：
     *
     * 一下方法用来判断一个字符串是否为：
     * 特殊需求 空格使用isEnpty
     * 1.null
     * 2.enpty - ""
     * 3.blank - "全是空白" 空由Character.isWhitespace定义
     * ==============================================================================================================
     */

    /**
     * 检查字符串是否为<code>null<code/>或者<code>""<code/>
     * StringUtil.isEnpty(null)         = true
     * StringUtil.isEnpty("")           = true
     * StringUtil.isEnpty(" ")          = false
     * StringUtil.isEnpty("code")       = false
     * StringUtil.isEnpty(" code ")     = false
     *
     * @param param 要检查的字符串
     * @return 如果为空，则返回<code>true</code>
     */
    public static boolean isEnpty(String param) {
        return ((param == null) || (param.length( ) == 0));
    }

    /**
     * 检查字符串是否不是<code>null<code/>和字符串<code>""<code/>
     * StringUtil.isNotEnpty(null)        = false
     * StringUtil.isNotEnpty("")          = false
     * StringUtil.isNotEnpty(" ")         = true
     * StringUtil.isNotEnpty("code")      = true
     * StringUtil.isNotEnpty("  code ")   = true
     *
     * @param param 要检查的字符串
     * @return 如果不为空，则返回<code>true<code/>
     */
    public static boolean isNotEnpty(String param) {
        return ((param != null) && (param.length( ) > 0));
    }

    /**
     * 检查字符串是否不是<code>null<code/>和字符串<code>""<code/>或只有空白字符
     * StringUtil.isblank(null)        = true
     * StringUtil.isblank("")          = true
     * StringUtil.isblank(" ")         = true
     * StringUtil.isblank("code")      = false
     * StringUtil.isblank(" code ")    = false
     *
     * @param param 要检查的字符串
     * @return 如果为空白，则返回<code>true<code/>
     */
    public static boolean isblank(String param) {
        int length;
        if ((param == null) || ((length = param.length( )) == 0)) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(param.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符串是否不是<code>null<code/>和字符串<code>""<code/>或只有空白字符
     * StringUtil.isblank(null)        = false
     * StringUtil.isblank("")          = false
     * StringUtil.isblank(" ")         = false
     * StringUtil.isblank("code")      = true
     * StringUtil.isblank(" code ")    = true
     *
     * @param param 要检查的字符串
     * @return 如果不为空白，则返回<code>true<code/>
     */
    public static boolean isNotBlank(String param) {
        int length;
        if ((param == null) || ((length = param.length( )) == 0)) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(param.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /*==============================================================================================================
     * 默认函数值保证不会出现NullPointerException
     *
     *
     * 当字符串为null、isEnpty、isBlank时，将字符串转化为指定的默认字符串
     * ==============================================================================================================
     */

    /**
     * 如果字符串是<code>null<code/>则返回空字符串<code>""<code/>,否则返回字符串本身
     * StringUtil.defaultIfNull(null)       = ""
     * StringUtil.defaultIfNull("")         = ""
     * StringUtil.defaultIfNull(" ")        = " "
     * StringUtil.defaultIfNull("code")     = "code"
     * StringUtil.defaultIfNull(" code ")   = " code "
     *
     * @param str 要转化的字符串
     * @return 转化的字符串本身或者<code>""<code/>
     */
    public static String defaultIfNull(String str) {
        return (str == null) ? EMPTY_STRING : str;
    }

    /**
     * 如果字符串是<code>null<code/>,则返回指定默认字符串，否则返回字符串本身
     * StringUtil.defaultIfNull(null, "defaultParam")       = "defaultParam"
     * StringUtil.defaultIfNull("", "defaultParam")         = "defaultParam"
     * StringUtil.defaultIfNull(" ", "defaultParam")        = " "
     * StringUtil.defaultIfNull("code", "defaultParam")     = "code"
     * StringUtil.defaultIfNull(" code ", "defaultParam")   = " code "
     *
     * @param str          要转化的字符串
     * @param defaultParam 默认字符串
     * @return 字符串本身或者指定的默认字符串
     */
    public static String defaultIfNull(String str, String defaultParam) {
        return (str == null) ? defaultParam : str;
    }

    /**
     * 如果字符串是<code>null<code/>或空字符串<code>""<code/>,则返回空字符串<code>""<code/>，否则返回字符串本身
     *
     * <p>
     * 此方法实际上和<code>defaultIfNull(String)</code>等效
     * <pre>
     * StringUtil.defaultIfEmpty(null)       = ""
     * StringUtil.defaultIfEmpty("")         = ""
     * StringUtil.defaultIfEmpty(" ")        = " "
     * StringUtil.defaultIfEmpty("code")     = "code"
     * </pre>
     * </p>
     *
     * @param str 要转换的字符串
     * @return 字符串本身或者空字符串<code>""</code>
     */
    public static String defaultIfEmpty(String str) {
        return (str == null) ? EMPTY_STRING : str;
    }

    /**
     * 如果字符串是<code>null<code/>或空字符串<code>""</code>，则返回指定默认字符串，否则返回字符串本身
     * <pre>
     * StringUtil.defaultIfEmpty(null, "default")       = "default"
     * StringUtil.defaultIfEmpty("", "default")         = "default"
     * StringUtil.defaultIfEmpty(" ", "default")        = " "
     * StringUtil.defaultIfEmpty("code", "default")     = "code"
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或者指定的默认字符串
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return ((str == null) || (str.length( ) == 0)) ? defaultStr : str;
    }

    /**
     * 如果字符串是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符串,则返回空字符串<code>""</code>,否则返回字符串本身
     *
     * <pre>
     * StringUtil.defaultIfBlank(null)     = ""
     * StringUtil.defaultIfBlank("")       = ""
     * StringUtil.defaultIfBlank(" ")      = ""
     * StringUtil.defaultIfBlank("code")   = "code"
     * </pre>
     *
     * @param str 要转换的字符串
     * @return 字符串本身或者空字符串<code>""</code>
     */
    public static String defaultIfBlank(String str) {
        return isblank(str) ? EMPTY_STRING : str;
    }

    /**
     * 如果字符串是<code>null</code>或空字符串<code>""</code>,则返回指定默认字符串，否则返回字符串本身
     *
     * <pre>
     * StringUtil.defaultIfBlank(null, "default")   = "default"
     * StringUtil.defaultIfBlank("", "default")     = "default"
     * StringUtil.defaultIfBlank(" ", "default")    = "default"
     * StringUtil.defaultIfBlank("code", "default") = "code"
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或者指定的默认字符串
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isblank(str) ? defaultStr : str;
    }

    /* ============================================================================== */
    /* 去空白（或指定字符）的函数                                                          */
    /*                                                                                */
    /* 以下函数用来除去一个字符串中的空白或者指定字符                                          */
    /* ============================================================================== */

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <p>注意，和<code>String.trim()</code>不同，此方法使用<code>Character.isWhitespace()</code>来判断空白，
     * 因而可以除去英文字符串之外的其他空白，如中文空格
     *
     * <pre>
     *  StringUtil.trim(null)     = null
     *  StringUtil.trim("")       = ""
     *  StringUtil.trim(" ")      = ""
     *  StringUtil.trim("code")   = "code"
     *  StringUtil.trim(" code ") = "code"
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串， 如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trim(String str) {
        return trim(str, null, 0);
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <p>注意，和<code>String.trim()</code>不同，此方法使用<code>Character.isWhitespace()</code>来判断空白，
     * 因而可以除去英文字符串之外的其他空白，如中文空格
     *
     * <pre>
     *  StringUtil.trim(null, *)          = null
     *  StringUtil.trim("", *)            = ""
     *  StringUtil.trim("code", null)     = "code"
     *  StringUtil.trim(null, *)          = null
     *  StringUtil.trim(null, *)          = null
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符串，如果为<code>null</code>表示除去空白字符
     * @return 除去空白的字符串，如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trim(String str, String stripChars) {
        return trim(str, stripChars, 0);
    }

    /**
     * 默认除去字符串头尾部空白字符串，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <p>注意，和<code>String.trim()</code>不同，此方法使用<code>Character.isWhitespace()</code>来判断空白，
     * 因而可以除去英文字符串之外的其他空白，如中文空格
     *
     * <pre>
     * StringUtil.trimToNull(null)           = null
     * StringUtil.trimToNull("")             = ""
     * StringUtil.trimToNull("code")         = "code"
     * StringUtil.trimToNull("code")         = "code"
     * StringUtil.trimToNull(" code")        = "code"
     * StringUtil.trimToNull("code ",)       = "code"
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去指定的字符串，如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trimToNull(String str) {
        return trimToNull(str, null);
    }

    /**
     * 除去字符串头尾部的指定字符串，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <p>注意，和<code>String.trim()</code>不同，此方法使用<code>Character.isWhitespace()</code>来判断空白，
     * 因而可以除去英文字符串之外的其他空白，如中文空格
     *
     * <pre>
     * StringUtil.trimToNull(null, *)           = null
     * StringUtil.trimToNull("", *)             = ""
     * StringUtil.trimToNull("code", "")        = "code"
     * StringUtil.trimToNull("code", null)      = "code"
     * StringUtil.trimToNull(" code", null)     = "code"
     * StringUtil.trimToNull("code ", null)     = "code"
     * StringUtil.trimToNull(" codex", "x")     = "code"
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符串，如果为<code>null</code>表示除去空白字符
     * @return 除去指定的字符串，如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trimToNull(String str, String stripChars) {
        String result = trim(str, stripChars);
        if (result == null) {
            return EMPTY_STRING;
        }
        return result;
    }

    /**
     * 除去字符串头尾部空白字符串，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <p>注意，和<code>String.trim()</code>不同，此方法使用<code>Character.isWhitespace()</code>来判断空白，
     * 因而可以除去英文字符串之外的其他空白，如中文空格
     *
     * <pre>
     * StringUtil.trimToEmpty(null)           = null
     * StringUtil.trimToEmpty("")             = ""
     * StringUtil.trimToEmpty("code")         = "code"
     * StringUtil.trimToEmpty("code")         = "code"
     * StringUtil.trimToEmpty(" code",)       = "code"
     * StringUtil.trimToEmpty("code ")        = "code"
     * StringUtil.trimToEmpty(" codex", "x")  = "code"
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去指定的字符串，如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trimToEmpty(String str) {
        return trimToNull(str, null);
    }

    /**
     * 除去字符串头尾部的指定字符串，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <p>注意，和<code>String.trim()</code>不同，此方法使用<code>Character.isWhitespace()</code>来判断空白，
     * 因而可以除去英文字符串之外的其他空白，如中文空格
     *
     * <pre>
     * StringUtil.trimToEmpty(null, *)                      = null
     * StringUtil.trimToEmpty("", *)                        = ""
     * StringUtil.trimToEmpty("code", "")                   = "code"
     * StringUtil.trimToEmpty("code", null)                 = "code"
     * StringUtil.trimToEmpty(" code", null)                = "code"
     * StringUtil.trimToEmpty("code ", null)                = "code"
     * StringUtil.trimToEmptytrimToEmpty(" codex", "x")     = "code"
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符串，如果为<code>null</code>表示除去空白字符
     * @return 除去指定的字符串，如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trimToEmpty(String str, String stripChars) {
        String result = trim(str, stripChars);
        if (result == null) {
            return EMPTY_STRING;
        }
        return result;
    }

    /**
     * 除去字符串头尾部的指定字符串，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <p>注意，和<code>String.trim()</code>不同，此方法使用<code>Character.isWhitespace()</code>来判断空白，
     * 因而可以除去英文字符串之外的其他空白，如中文空格
     *
     * <pre>
     *  StringUtil.trimStart(null)     = null
     *  StringUtil.trimStart("")       = ""
     *  StringUtil.trimStart("code")   = "code"
     *  StringUtil.trimStart(" code")  = "code"
     *  StringUtil.trimStart("code ")  = "code"
     *  StringUtil.trimStart(" code ") = "code"
     *
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trimStart(String str) {
        return trim(str, null, -1);
    }

    /**
     * 除去字符串头尾部的指定字符串，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <pre>
     * StringUtil.trimStart(null, *)           = null
     * StringUtil.trimStart("", *)             = null
     * StringUtil.trimStart("code", "")        = "code"
     * StringUtil.trimStart("code", null)      = "code"
     * StringUtil.trimStart(" code", null)     = "code"
     * StringUtil.trimStart("code ", null)     = "code"
     * StringUtil.trimStart(" code ", null)    = "code"
     * StringUtil.trimStart("xcode", "x")      = "code"
     *
     *
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符串，如果为<code>null</code>表示除去空白字符
     * @return 除去指定的字符串，如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trimStart(String str, String stripChars) {
        return trim(str, stripChars, -1);
    }

    /**
     * 除去字符串头尾部的指定字符串，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <p>注意，和<code>String.trim()</code>不同，此方法使用<code>Character.isWhitespace()</code>来判断空白，
     * 因而可以除去英文字符串之外的其他空白，如中文空格
     *
     * <pre>
     * StringUtil.trimEnd(null)        = null
     * StringUtil.trimEnd("")          = ""
     * StringUtil.trimEnd("code")      = "code"
     * StringUtil.trimEnd(" code")     = "code"
     * StringUtil.trimEnd("code ")     = "code"
     * StringUtil.trimEnd(" code ")    = "code"
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去的字符串，如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trimEnd(String str) {
        return trim(str, null, 1);
    }

    /**
     * 除去字符串头尾部的指定字符串，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <p>注意，和<code>String.trim()</code>不同，此方法使用<code>Character.isWhitespace()</code>来判断空白，
     * 因而可以除去英文字符串之外的其他空白，如中文空格
     *
     * <pre>
     * StringUtil.trimEnd(null, *)           = null
     * StringUtil.trimEnd("", *)             = ""
     * StringUtil.trimEnd("code", "")        = "code"
     * StringUtil.trimEnd("code", null)      = "code"
     * StringUtil.trimEnd(" code", null)     = "code"
     * StringUtil.trimEnd("code ", null)     = "code"
     * StringUtil.trimEnd(" codex", "x")     = "code"
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去指定的字符串，如果原字符串为<code>null</code>， 则返回<code>null</code>
     */
    public static String trimEnd(String str, String stripChars) {
        return trim(str, null, 1);
    }

    /**
     * 除去字符串头尾部的指定字符串，如果字符串是<code>null</code>, 依然返回<code>null</code>
     *
     * <pre>
     * StringUtil.trim(null, *)             = null
     * StringUtil.trim("", *)               = ""
     * StringUtil.trim("code", *)           = "code"
     * StringUtil.trim(" code", *)          = "code"
     * StringUtil.trim("code ", *)          = "code"
     * StringUtil.trim(" code ",*)          = "code"
     * StringUtil.trim(" abcyx", "xyz")     = "abc"
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * @param mode       <code>-1</code>表示trimStart, <code>0</code>trim全部， <code>1</code>表示trimEnd
     * @return 除去指定字符后的字符串，如果原字符串为<code>null</code>, 则返回<code>null</code>
     */
    public static String trim(String str, String stripChars, int mode) {
        if (str == null) {
            return EMPTY_STRING;
        }

        int length = str.length( );
        int start = 0;
        int end = length;

        // 扫描字符串头部
        if (mode <= 0) {
            if (isblank(stripChars)) {
                while ((start < end) && (Character.isWhitespace(str.charAt(start)))) {
                    start++;
                }
            } else if (stripChars.length( ) == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                    start++;
                }
            }
        }

        // 扫描字符串尾部
        if (mode >= 0) {
            if (isblank(stripChars)) {
                while ((start < end) && (Character.isWhitespace(str.charAt(end - 1)))) {
                    end--;
                }
            } else if (stripChars.length( ) == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                    end--;
                }
            }
        }

        if ((start > 0) || (end < length)) {
            return str.substring(start, end);
        }

        return str;
    }

    /* =================================================================================== */
    /* 比较函数                                                                             */
    /*                                                                                     */
    /* 以下方式用来比较两个字符串是否相同                                                        */
    /* =================================================================================== */

    /**
     * 比较两个字符串（大小写敏感）
     *
     * <pre>
     * StringUtil.equals(null, null)     = true
     * StringUtil.equals(null, "code")   = false
     * StringUtil.equals("code", null)   = false
     * StringUtil.equals("code", "code") = true
     * StringUtil.equals("code", "CODE") = false
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是<code>null</code>, 则返回<code>true</code>
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equals(str2);
    }

    /**
     * 比较两个字符串（大小写敏感）
     *
     * <pre>
     * StringUtil.equalsIgnoreCase(null, null)     = true
     * StringUtil.equalsIgnoreCase(null, "code")   = false
     * StringUtil.equalsIgnoreCase("code", null)   = false
     * StringUtil.equalsIgnoreCase("code", "code") = true
     * StringUtil.equalsIgnoreCase("code", "CODE") = true
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是<code>null</code>, 则返回<code>true</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equalsIgnoreCase(str2);
    }

    /* ================================================================================== */
    /* 字符串类型判断函数                                                                     */
    /*                                                                                    */
    /* 判断字符串的类型是否为：字母、数字、空白等                                                 */
    /* ================================================================================== */

    /**
     * 判断字符串中是否包含unicode字母
     *
     * <p><code>null</code>将返回<code>false</code> 空字符串<code>""</code>将返回<code>true</code>
     *
     * <pre>
     * StringUtil.isAlpha(null)         = false
     * StringUtil.isAlpha("")           = true
     * StringUtil.isAlpha(" ")          = false
     * StringUtil.isAlpha("code")       = true
     * StringUtil.isAlpha("code2c")     = false
     * StringUtil.isAlpha("code-c")     = false
     * </pre>
     *
     * @param str 要检查的字符串
     * @return 如果字符串非<code>null</code>并且全有unicode字母组成，则返回<code>true</code>
     */
    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length( );

        for (int i = 0; i < length; i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串中是否只包含unicode字母和空格
     *
     * <p><code>' '</code> <code>null</code>将返回<code>false</code>, 空字符串<code>""</code>将返回<code>true
     * </code>
     *
     * <pre>
     * StringUtil.isAlphaSpace(null)         = false
     * StringUtil.isAlphaSpace("")           = true
     * StringUtil.isAlphaSpace(" ")          = true
     * StringUtil.isAlphaSpace("code")       = true
     * StringUtil.isAlphaSpace("cod e")      = true
     * StringUtil.isAlphaSpace("code2c")     = false
     * StringUtil.isAlphaSpace("code-c")     = false
     * </pre>
     *
     * @param str 要检查的字符串
     * @return 如果字符串非<code>null</code>并且全有unicode字母组成，则返回<code>true</code>
     */
    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length( );

        for (int i = 0; i < length; i++) {
            if (!Character.isLetter(str.charAt(i)) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串中是否只包含unicode字母和数字
     *
     * <p><code>null</code>将返回<code>false</code>， 空字符串<code>""</code>将返回<code>true</code>
     *
     * <pre>
     * StringUtil.isAlphanumeric(null)         = false
     * StringUtil.isAlphanumeric("")           = true
     * StringUtil.isAlphanumeric(" ")          = false
     * StringUtil.isAlphanumeric("code")       = true
     * StringUtil.isAlphanumeric("cod e")      = false
     * StringUtil.isAlphanumeric("code2c")     = true
     * StringUtil.isAlphanumeric("code-c")     = false
     * </pre>
     *
     * @param str 要检查的字符串
     * @return 如果字符串非<code>null</code>并且全有unicode字母组成，则返回<code>true</code>
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length( );

        for (int i = 0; i < length; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串中是否只包含unicode字母、数字和空格<code>' '</code>
     *
     * <p><code>null</code>将返回<code>false</code>， 空字符串<code>""</code>将返回<code>true</code>
     *
     * <pre>
     * StringUtil.isAlphanumericSpace(null)         = false
     * StringUtil.isAlphanumericSpace("")           = true
     * StringUtil.isAlphanumericSpace(" ")          = true
     * StringUtil.isAlphanumericSpace("code")       = true
     * StringUtil.isAlphanumericSpace("cod e")      = true
     * StringUtil.isAlphanumericSpace("code2c")     = true
     * StringUtil.isAlphanumericSpace("code-c")     = false
     * </pre>
     *
     * @param str 要检查的字符串
     * @return 如果字符串非<code>null</code>并且全有unicode字母组成，则返回<code>true</code>
     */
    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length( );

        for (int i = 0; i < length; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i)) && (str.charAt(i)) != ' ') {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串中是否只包含unicode数字
     *
     * <p><code>null</code>将返回<code>false</code>， 空字符串<code>""</code>将返回<code>true</code>
     *
     * <pre>
     * StringUtil.isNumeric(null)         = false
     * StringUtil.isNumeric("")           = true
     * StringUtil.isNumeric(" ")          = false
     * StringUtil.isNumeric("123")        = true
     * StringUtil.isNumeric("12 3")       = false
     * StringUtil.isNumeric("code2c")     = false
     * StringUtil.isNumeric("12-3")       = false
     * StringUtil.isNumeric("12.3")       = false
     * </pre>
     *
     * @param str 要检查的字符串
     * @return 如果字符串非<code>null</code>并且全有unicode字母组成，则返回<code>true</code>
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length( );

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串中是否只包含unicode数字
     *
     * <p><code>null</code>将返回<code>false</code>， 空字符串<code>""</code>将返回<code>true</code>
     *
     * <pre>
     * StringUtil.isNumericSpace(null)         = false
     * StringUtil.isNumericSpace("")           = true
     * StringUtil.isNumericSpace(" ")          = true
     * StringUtil.isNumericSpace("123")        = true
     * StringUtil.isNumericSpace("12 3")       = true
     * StringUtil.isNumericSpace("code2c")     = false
     * StringUtil.isNumericSpace("12-3")       = false
     * StringUtil.isNumericSpace("12.3")       = false
     * </pre>
     *
     * @param str 要检查的字符串
     * @return 如果字符串非<code>null</code>并且全有unicode字母组成，则返回<code>true</code>
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length( );

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i)) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串中是否只包含unicode空白
     *
     * <p><code>null</code>将返回<code>false</code>， 空字符串<code>""</code>将返回<code>true</code>
     *
     * <pre>
     * StringUtil.isWhitespace(null)         = false
     * StringUtil.isWhitespace("")           = true
     * StringUtil.isWhitespace(" ")          = true
     * StringUtil.isWhitespace("code")       = false
     * StringUtil.isWhitespace("code2c")     = false
     * StringUtil.isWhitespace("12-3")       = false
     * </pre>
     *
     * @param str 要检查的字符串
     * @return 如果字符串非<code>null</code>并且全有unicode字母组成，则返回<code>true</code>
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length( );

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /* ========================================================================================= */
    /* 大小写转换                                                                                  */
    /*                                                                                            */
    /* 以下函数用于字符串大小写转换                                                                                           */
    /* ========================================================================================= */

    /**
     * 将字符串转换成大写
     *
     * <p>如果字符串是<code>null</code>则返回<code>null</code>
     *
     * <pre>
     * StringUtil.toUpperCase(null)     = null
     * StringUtil.toUpperCase("")       = null
     * StringUtil.toUpperCase("code")   = "CODE"
     * StringUtil.toUpperCase("coDe")   = "CODE"
     * </pre>
     *
     * @param str 要转换的字符串
     * @return 大写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toUpperCase(String str) {
        if (isEnpty(str)) {
            return str;
        }

        return str.toUpperCase( );
    }

    /**
     * 将字符串转换成大写
     *
     * <p>如果字符串是<code>null</code>则返回<code>null</code>
     *
     * <pre>
     * StringUtil.toUpperCase(null)     = null
     * StringUtil.toUpperCase("")       = null
     * StringUtil.toUpperCase("code")   = "CODE"
     * StringUtil.toUpperCase("coDe")   = "CODE"
     * </pre>
     *
     * @param str 要转换的字符串
     * @return 大写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toLowerCase(String str) {
        if (isEnpty(str)) {
            return EMPTY_STRING;
        }

        return str.toLowerCase( );
    }

    /**
     * 将字符串的首字符转换为大写，其他字符不变
     *
     * <pre>
     * StringUtil.caitalize(null)     = null
     * StringUtil.caitalize("")       = null
     * StringUtil.caitalize("code")   = "Code"
     * </pre>
     *
     * @param str 要转换的字符串
     * @return 首字符为大写的字符串，如果原字符串为<code>null</code>, 则返回<code>null</code>
     */
    public static String caitalize(String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length( )) == 0)) {
            return str;
        }

        return new StringBuffer(strLen)
                .append(Character.toTitleCase(str.charAt(0)))
                .append(str.substring(1))
                .toString( );
    }

    /**
     * 将字符串的首字符转换为小写，其他字符不变
     *
     * <pre>
     * StringUtil.caitalize(null)     = null
     * StringUtil.caitalize("")       = null
     * StringUtil.caitalize("Code")   = "code"
     * </pre>
     *
     * @param str 要转换的字符串
     * @return 首字符为大写的字符串，如果原字符串为<code>null</code>, 则返回<code>null</code>
     */
    public static String upcapitalize(String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length( )) == 0)) {
            return str;
        }

        return new StringBuffer(strLen)
                .append(Character.toLowerCase(str.charAt(0)))
                .append(str.substring(1))
                .toString( );
    }

    /**
     * 将字符串的首字符转换为小写，其他字符不变
     *
     * <pre>
     * StringUtil.swapCase(null)                   = null
     * StringUtil.swapCase("")                     = null
     * StringUtil.swapCase("The dog has a BONE")   = "the DOG HAS A bone"
     * </pre>
     *
     * @param str 要转换的字符串
     * @return 首字符为大写的字符串，如果原字符串为<code>null</code>, 则返回<code>null</code>
     */
    public static String swapCase(String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length( )) == 0)) {
            return str;
        }

        StringBuffer buffer = new StringBuffer(strLen);

        char ch = 0;
        for (int i = 0; i < strLen; i++) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toLowerCase(ch);
            }
            buffer.append(ch);
        }

        return buffer.toString( );
    }

    /* =========================================================================== */
    /* 字符串连接函数                                                                */
    /*                                                                             */
    /* 将多个对象按指定分隔符连接成字符串                                                */
    /* =========================================================================== */

    /**
     * 将数组中的元素连接成一个字符串
     *
     * <pre>
     * StringUtil.join(null)               = null
     * StringUtil.join([])                 = ""
     * StringUtil.join([null])             = ""
     * StringUtil.join(["a","b","c"])      = "abc"
     * StringUtil.join([null, "", "a"])    = ",,a"
     * </pre>
     *
     * @param array 要连接的数组
     * @return 连接后的字符串，如果原数组为<code>null</code>，则返回<code>null</code>
     */
    public static String join(Object[] array) {
        return join(array, null);
    }

    /**
     * 将数组中的元素连接成一个字符串
     *
     * <pre>
     * StringUtil.join(null, *)               = null
     * StringUtil.join([], *)                 = ""
     * StringUtil.join([null], *)             = ""
     * StringUtil.join(["a","b","c"], -)      = "a-b-c"
     * StringUtil.join(["a","b","c"], null)   = "abc"
     * StringUtil.join(["a","b","c"], "")     = "abc"
     * StringUtil.join(["a","b","c"], ",")    = "a,b,c"
     * </pre>
     *
     * @param array     要连接的数组
     * @param separator 分隔符
     * @return 连接后的字符串，如果原数组为<code>null</code>，则返回<code>null</code>
     */
    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }

        if (separator == null) {
            separator = EMPTY_STRING;
        }

        int arraySize = array.length;

        int bufSize =
                (arraySize == 0)
                        ? 0
                        : (arraySize
                        * (((array[0] == null ? 16 : array[0].toString( ).length( )))
                        + ((separator != null) ? separator.length( ) : 0)));

        StringBuffer buffer = new StringBuffer(bufSize);
        for (int i = 0; i < arraySize; i++) {
            if ((separator != null) && (i > 0)) {
                buffer.append(separator);
            }
            if (array[i] != null) {
                buffer.append(array[i]);
            }
        }
        return buffer.toString( );
    }

    /**
     * 将数组中的元素连接成一个字符串
     *
     * <pre>
     * StringUtil.join(null, *)               = null
     * StringUtil.join([], *)                 = ""
     * StringUtil.join([null], *)             = ""
     * StringUtil.join(["a","b","c"], -)      = "a-b-c"
     * StringUtil.join(["a","b","c"], null)   = "abc"
     * StringUtil.join(["a","b","c"], "")     = "abc"
     * StringUtil.join(["a","b","c"], ",")    = "a,b,c"
     * StringUtil.join([null,"","a"], ",")    = ",,a"
     * </pre>
     *
     * @param iterator  要连接的<code>Iterator</code>
     * @param separator 分隔符
     * @return 连接后的字符串，如果原数组为<code>null</code>，则返回<code>null</code>
     */
    public static String join(Iterator iterator, char separator) {
        if (iterator == null) {
            return null;
        }
        // 默认16
        StringBuffer buffer = new StringBuffer(256);

        while (iterator.hasNext( )) {
            Object obj = iterator.next( );
            if (obj != null) {
                buffer.append(obj);
            }

            if (iterator.hasNext( )) {
                buffer.append(separator);
            }
        }

        return buffer.toString( );
    }

    /* ==================================================================================== */
    /* 字符串查找函数                                                                         */
    /*                                                                                      */
    /* 在字符串中查找指定字符                                                                   */
    /* ==================================================================================== */

    /**
     * 在字符串中查找指定字符，并返回第一个匹配的索引值，如果字符串为<code>null</code>或未找到，则返回<code>-1</code>
     *
     * <pre>
     * StringUtil.indexOf(null, *)          = -1
     * StringUtil.indexOf("", *)            = -1
     * StringUtil.indexOf("aabaabaa", "a")  = 0
     * StringUtil.indexOf("aabaabaa", b)    = 2
     * </pre>
     *
     * @param str        要扫描的字符串
     * @param searchChar 查找的字符
     * @return 第一个匹配的索引值
     */
    public static int indexOf(String str, char searchChar) {
        if ((str == null) || (str.length( ) == 0)) {
            return -1;
        }
        return str.indexOf(searchChar);
    }
}

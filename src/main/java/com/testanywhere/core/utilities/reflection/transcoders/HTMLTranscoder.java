package com.testanywhere.core.utilities.reflection.transcoders;

import com.testanywhere.core.utilities.logging.TextManager;
import com.testanywhere.core.utilities.reflection.ArrayUtils;
import com.testanywhere.core.utilities.reflection.ClassFields.FieldsFilter;
import com.testanywhere.core.utilities.reflection.ConstructorUtils;
import com.testanywhere.core.utilities.reflection.ReflectUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Provides methods for encoding and decoding HTML <br/>
 * Note that the HTML parser is not supported
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HTMLTranscoder implements Transcoder
{
    private static final int MAXIMUM_LEVEL = 7;
    private static final String SPACES = "  ";

    private Collection<ObjectEncoder> encoders;
    private boolean humanOutput;
    private boolean includeNulls;
    private boolean includeClassField;
    private int maxLevel;

    public static final char SPACE = ' ';
    public static final char AMP   = '&';
    /**
     * single quote (')
     */
    public static final char APOS  = '\'';
    public static final char BANG  = '!';
    public static final char EQ    = '=';
    public static final char GT    = '>';
    public static final char LT    = '<';
    public static final char QUEST = '?';
    public static final char QUOT  = '"';
    public static final char SLASH = '/';
    public static final char EOL   = '\n';

    /**
     * Default constructor:
     * See other constructors for options
     */
    public HTMLTranscoder()
    {
        super();
        this.initialize();
    }

    /**
     * @param humanOutput if true then enable human readable output (includes indentation and line breaks)
     * @param includeNulls if true then create output tags for null values
     * @param includeClassField if true then include the value from the "getClass()" method as "class" when encoding beans and maps
     */
    public HTMLTranscoder(boolean humanOutput, boolean includeNulls, boolean includeClassField)
    {
        this();

        this.humanOutput = humanOutput;
        this.includeNulls = includeNulls;
        this.includeClassField = includeClassField;
    }

    public String getHandledFormat() {
        return "html";
    }

    public String encode(Object object, String name, Map<String, Object> properties)
    {
    	return this.encode(object, name, properties, this.maxLevel);
    }
    
    public String encode(Object object, String name, Map<String, Object> properties, int maxDepth)
    {
        if (object != null) {
            if (!TextManager.validString(name))
                name = DATA_KEY;
        }
        return HTMLTranscoder.makeHTML(object, name, properties, this.humanOutput, this.includeNulls, this.includeClassField, maxDepth, this.encoders);
    }

    public Map<String, Object> decode(String string) {
        throw new UnsupportedOperationException("Decoding from HTML is not supported");
    }

    public void setEncoders(List<ObjectEncoder> encoders) {
        this.encoders = encoders;
    }
    public Collection<ObjectEncoder> getEncoders() {
        return this.encoders;
    }
    public void addEncoder(ObjectEncoder objectEncoder)
    {
        this.encoders.add(objectEncoder);
    }

    /**
     * @param maxLevel the number of objects to follow when traveling through the object,
     * 0 means only the fields in the initial object, default is 7
     */
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * Convert an object into a well-formed, element-normal HTML string.
     * @param object any object
     * @return the HTML string version of the object
     */
    public static String makeHTML(Object object)
    {
        return HTMLTranscoder.makeHTML(object, null, null, false, true, false, HTMLTranscoder.MAXIMUM_LEVEL, null);
    }

    /**
     * Convert an object into a well-formed, element-normal HTML string.
     * @param object any object
     * @param tagName (optional) enclosing root tag
     * @param humanOutput true of human readable output
     * @param includeNulls true to include null values when generating tags
     * @param maxLevel TODO
     * @return the HTML string version of the object
     */
    public static String makeHTML(Object object, String tagName, Map<String, Object> properties, boolean humanOutput, boolean includeNulls, boolean includeClassField, int maxLevel, Collection<ObjectEncoder> encoders) {
        return "<table border='1'>\n" 
            + HTMLTranscoder.toHTML(object, tagName, 0, maxLevel, humanOutput, includeNulls, includeClassField, properties, encoders)
            + "</table>\n";
    }

    private static String toHTML(Object object, String tagName, int level, int maxLevel, boolean humanOutput, boolean includeNulls, boolean includeClassField, Map<String, Object> properties, Collection<ObjectEncoder> encoders) {
        StringBuilder sb = new StringBuilder();

        if (object == null) {
            if (includeNulls) {
                // nulls are empty tags always
                tagName = HTMLTranscoder.validate(tagName == null ? "null" : tagName);
                HTMLTranscoder.makeLevelSpaces(sb, level, humanOutput);
                sb.append("<tr><td>");
                sb.append(tagName);
                sb.append("</td><td>");
                sb.append("<i>NULL</i>");
                sb.append("</td></tr>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
            }
        } else {
            Class<?> type = ConstructorUtils.getWrapper(object.getClass());
            if ( ConstructorUtils.isClassSimple(type) ) {
                // Simple (String, Number, etc.)
                tagName = HTMLTranscoder.validate(tagName == null ? HTMLTranscoder.makeElementName(type) : tagName);
                HTMLTranscoder.makeLevelSpaces(sb, level, humanOutput);
                String value = HTMLTranscoder.escapeForXML( object.toString() );
                sb.append("<tr><td>");
                sb.append(tagName);
                sb.append("</td><td>");
                sb.append(value);
                sb.append("</td></tr>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
            } else if ( ConstructorUtils.isClassArray(type) ) {
                // ARRAY
                tagName = validate(tagName == null ? "array" : tagName);
                int length = ArrayUtils.size((Object[])object);
                Class<?> elementType = ArrayUtils.type((Object[])object);
                HTMLTranscoder.makeLevelSpaces(sb, level, humanOutput);
                sb.append("<tr><td width='3%'>");
                sb.append(tagName);
                sb.append(" type=array");
                sb.append(" length="+length);
                sb.append("</td><td>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
                HTMLTranscoder.makeLevelSpaces(sb, level+1, humanOutput);
                sb.append("<table border='1'>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
                for (int i = 0; i < length; ++i) {
                    sb.append( HTMLTranscoder.toHTML(Array.get(object, i), HTMLTranscoder.makeElementName(elementType), level+2, maxLevel, humanOutput, includeNulls, includeClassField, properties, encoders) );
                }
                HTMLTranscoder.makeLevelSpaces(sb, level+1, humanOutput);
                sb.append("</table>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
                HTMLTranscoder.makeLevelSpaces(sb, level, humanOutput);
                sb.append("</td></tr>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
            } else if ( ConstructorUtils.isClassCollection(type) ) {
                // COLLECTION
                tagName = HTMLTranscoder.validate(tagName == null ? "collection" : tagName);
                Collection<Object> collection = (Collection<Object>) object;
                HTMLTranscoder.makeLevelSpaces(sb, level, humanOutput);
                sb.append("<tr><td width='3%'>");
                sb.append(tagName);
                sb.append(" type=collection");
                sb.append(" size="+collection.size());
                sb.append("</td><td>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
                HTMLTranscoder.makeLevelSpaces(sb, level+1, humanOutput);
                sb.append("<table border='1'>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
                for (Object element : collection) {
                    Class<?> elementType = null;
                    if (element != null) {
                        elementType = element.getClass();
                    }
                    sb.append( HTMLTranscoder.toHTML(element, HTMLTranscoder.makeElementName(elementType), level+2, maxLevel, humanOutput, includeNulls, includeClassField, properties, encoders) );
                }
                HTMLTranscoder.makeLevelSpaces(sb, level+1, humanOutput);
                sb.append("</table>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
                HTMLTranscoder.makeLevelSpaces(sb, level, humanOutput);
                sb.append("</td></tr>");
                HTMLTranscoder.makeEOL(sb, humanOutput);
            } else {
                // must be a bean or map, make sure it is a map
                tagName = HTMLTranscoder.validate(tagName == null ? HTMLTranscoder.makeElementName(type) : tagName);
                // special handling for certain object types
                String special = TranscoderUtils.checkObjectSpecial(object);
                if (special != null) {
                    if ("".equals(special)) {
                        // skip this one entirely
                    } else {
                        // just use the value in special to represent this
                        HTMLTranscoder.makeLevelSpaces(sb, level, humanOutput);
                        String value = HTMLTranscoder.escapeForXML( special );
                        sb.append("<tr><td>");
                        sb.append(tagName);
                        sb.append("</td><td>");
                        sb.append(value);
                        sb.append("</td></tr>");
                        HTMLTranscoder.makeEOL(sb, humanOutput);
                    }
                } else {
                    // normal handling
                    if ((maxLevel*2) <= level) {
                        // if the max level was reached then stop
                        sb.append("<tr><td width='3%'>");
                        sb.append(tagName);
                        sb.append("</td><td>");
                        sb.append( "MAX level reached (" );
                        sb.append( level );
                        sb.append( "):" );
                        sb.append( HTMLTranscoder.escapeForXML(object.toString()) );
                        sb.append("</td></tr>");
                        HTMLTranscoder.makeEOL(sb, humanOutput);
                    } else {
                        String xmlType = "bean";
                        Map<String, Object> map = null;
                        if (Map.class.isAssignableFrom(type)) {
                            xmlType = "map";
                            map = (Map<String, Object>) object;
                        } else {
                            // reflect over objects
                            map = ReflectUtils.getInstance().getObjectValues(object, FieldsFilter.SERIALIZABLE, includeClassField);
                        }
                        HTMLTranscoder.makeLevelSpaces(sb, level, humanOutput);
                        sb.append("<tr><td width='3%'>");
                        sb.append(tagName);
                        sb.append(" type="+xmlType);
                        sb.append(" size="+map.size());
                        sb.append("</td><td>");
                        HTMLTranscoder.makeEOL(sb, humanOutput);
                        HTMLTranscoder.makeLevelSpaces(sb, level+1, humanOutput);
                        sb.append("<table border='1'>");
                        HTMLTranscoder.makeEOL(sb, humanOutput);
                        for (Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey() != null) {
                                sb.append( HTMLTranscoder.toHTML(entry.getValue(), entry.getKey().toString(), level+2, maxLevel, humanOutput, includeNulls, includeClassField, properties, encoders) );
                            }
                        }
                        HTMLTranscoder.makeLevelSpaces(sb, level+1, humanOutput);
                        sb.append("</table>");
                        HTMLTranscoder.makeEOL(sb, humanOutput);
                        HTMLTranscoder.makeLevelSpaces(sb, level, humanOutput);
                        sb.append("</td></tr>");
                        HTMLTranscoder.makeEOL(sb, humanOutput);
                    }
                }
            }
        }
        return sb.toString();
    }

    private static String makeElementName(Class<?> type) {
        String name = "element";
        if (type != null) {
            if (! Map.class.isAssignableFrom(type))
                name = type.getSimpleName();
        }
        return name;
    }

    private static void makeEOL(StringBuilder sb, boolean includeEOL) {
        if (includeEOL) sb.append(EOL);
    }

    private static void makeLevelSpaces(StringBuilder sb, int level, boolean includeEOL) {
        level++;
        if (includeEOL)
            for (int i = 0; i < level; i++)
                sb.append(SPACES);
    }

    /**
     * Escape a string for XML encoding: replace special characters with XML escapes:
     * <pre>
     * &amp; <small>(ampersand)</small> is replaced by &amp;amp;
     * &lt; <small>(less than)</small> is replaced by &amp;lt;
     * &gt; <small>(greater than)</small> is replaced by &amp;gt;
     * &quot; <small>(double quote)</small> is replaced by &amp;quot;
     * </pre>
     * @param string The string to be escaped.
     * @return The escaped string.
     */
    public static String escapeForXML(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = string.length(); i < len; i++) {
            char c = string.charAt(i);
            switch (c) {
            case AMP:
                sb.append("&amp;");
                break;
            case LT:
                sb.append("&lt;");
                break;
            case GT:
                sb.append("&gt;");
                break;
            case QUOT:
                sb.append("&quot;");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Validates that a string contains no spaces and is non-null/non-empty
     * Throw an exception if the string contains whitespace. 
     * Whitespace is not allowed in tagNames and attributes.
     * @param string any string
     * @throws IllegalArgumentException
     */
    public static String validate(String string) {
        if (! TextManager.validString(string) )
            throw new IllegalArgumentException("string is NULL or empty");
        int length = string.length();
        for ( int i = 0; i < length; i += 1) {
            if (Character.isWhitespace(string.charAt(i)))
                throw new IllegalArgumentException("'" + string + "' contains a space character.");
        }
        return string;
    }

    private void initialize()
    {
        this.encoders = new ArrayList<>();
        this.humanOutput = true;
        this.includeNulls = true;
        this.includeClassField = false;
        this.maxLevel = HTMLTranscoder.MAXIMUM_LEVEL;
    }
}

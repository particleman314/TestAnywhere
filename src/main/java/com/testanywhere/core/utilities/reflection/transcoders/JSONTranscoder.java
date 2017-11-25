package com.testanywhere.core.utilities.reflection.transcoders;

import com.testanywhere.core.utilities.reflection.ArrayUtils;
import com.testanywhere.core.utilities.reflection.ClassFields.FieldsFilter;
import com.testanywhere.core.utilities.reflection.ConstructorUtils;
import com.testanywhere.core.utilities.reflection.ConversionUtils;
import com.testanywhere.core.utilities.reflection.ReflectUtils;
import com.testanywhere.core.utilities.reflection.map.ArrayOrderedMap;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.Map.Entry;

/**
 * Provides methods for encoding and decoding JSON
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JSONTranscoder implements Transcoder
{
    private static final Object MARK_OBJECT_END = new Object();
    private static final Object MARK_ARRAY_END = new Object();
    private static final Object MARK_COLON = new Object();
    private static final Object MARK_COMMA = new Object();
    private static final Object MARK_END_INPUT = new Object();
    public static final int FIRST = 0;
    public static final int CURRENT = 1;
    public static final int NEXT = 2;

    private static Map<Character, Character> escapes = new HashMap<Character, Character>();
    static
    {
        JSONTranscoder.escapes.put(new Character('"'), new Character('"'));
        JSONTranscoder.escapes.put(new Character('\\'), new Character('\\'));
        JSONTranscoder.escapes.put(new Character('/'), new Character('/'));
        JSONTranscoder.escapes.put(new Character('b'), new Character('\b'));
        JSONTranscoder.escapes.put(new Character('f'), new Character('\f'));
        JSONTranscoder.escapes.put(new Character('n'), new Character('\n'));
        JSONTranscoder.escapes.put(new Character('r'), new Character('\r'));
        JSONTranscoder.escapes.put(new Character('t'), new Character('\t'));
    }

    private static char[] hex = "0123456789ABCDEF".toCharArray();

    private static final int MAXIMUM_LEVEL = 10;
    protected static final String SPACES = "  ";

    private Collection<ObjectEncoder> encoders;
    private boolean humanOutput;
    private boolean includeNulls;
    private boolean includeClassField;
    private int maxLevel;

    public static final char OBJ_BEG    = '{';
    public static final char OBJ_END    = '}';
    public static final char OBJ_SEP    = ':';
    public static final char ARRAY_BEG  = '[';
    public static final char ARRAY_END  = ']';
    public static final char JSON_SEP   = ',';

    // based on code from: http://www.json.org/java/org/json/XML.java

    public static final char SPACE = ' ';
    public static final char AMP   = '&';
    /**
     * single quote (')
     */
    public static final char APOS  = '\'';
    public static final char BANG  = '!';
    public static final char BACK  = '\\';
    public static final char EOL   = '\n';
    public static final char EQ    = '=';
    public static final char GT    = '>';
    public static final char LT    = '<';
    public static final char QUEST = '?';
    public static final char QUOT  = '"';
    public static final char SLASH = '/';

    public static final String BOOLEAN_TRUE = "true";
    public static final String BOOLEAN_FALSE = "false";
    public static final String NULL = "null";

    /**
     * Default constructor:
     * See other constructors for options
     */
    public JSONTranscoder()
    {
        super();
        this.initialize();
    }

    /**
     * @param humanOutput if true then enable human readable output (includes indentation and line breaks)
     * @param includeNulls if true then create output tags for null values
     * @param includeClassField if true then include the value from the "getClass()" method as "class" when encoding beans and maps
     */
    public JSONTranscoder(boolean humanOutput, boolean includeNulls, boolean includeClassField)
    {
        this();

        this.humanOutput = humanOutput;
        this.includeNulls = includeNulls;
        this.includeClassField = includeClassField;
    }

    public String getHandledFormat() {
        return "json";
    }

    public String encode(Object object, String name, Map<String, Object> properties)
    {
        return encode(object, name, properties, this.maxLevel);
    }

    public String encode(Object object, String name, Map<String, Object> properties, int maxDepth)
    {
        // allow the transcoder to deal with the data directly, no need to convert it to a map first
        String encoded = JSONTranscoder.makeJSON(object, properties, this.humanOutput, this.includeNulls, this.includeClassField, maxDepth, null);
        return encoded;
    }

    public Map<String, Object> decode(String string) {
        Map<String, Object> decoded;
        Object decode = new JsonReader().read(string);
        if (decode instanceof Map) {
            decoded = (Map<String, Object>) decode;
        } else {
            // for JSON if the result is not a map then simply put the result into a map
            decoded = new ArrayOrderedMap<>();
            decoded.put(Transcoder.DATA_KEY, decode);
        }
        return decoded;
    }

    public void setEncoders(Collection<ObjectEncoder> encoders) {
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
     * Convert an object into a well-formed, element-normal XML string.
     * @param object any object
     * @return the JSON string version of the object
     */
    public static String makeJSON(Object object) {
        return JSONTranscoder.makeJSON(object, null, false, true, false, JSONTranscoder.MAXIMUM_LEVEL, null);
    }

    /**
     * Convert an object into a well-formed, element-normal XML string.
     * @param object any object
     * @param humanOutput true of human readable output
     * @param includeNulls true to include null values when generating tags
     * @param includeClassField if true then include the value from the "getClass()" method as "class" when encoding beans and maps
     * @param maxLevel maximum level to traverse the objects before stopping
     * @param encoders the external encoders to allow to process complex objects
     * @return the JSON string version of the object
     */
    public static String makeJSON(Object object, Map<String, Object> properties, boolean humanOutput, boolean includeNulls, boolean includeClassField, int maxLevel, Collection<ObjectEncoder> encoders) {
        return JSONTranscoder.toJSON(object, 0, maxLevel, humanOutput, includeNulls, includeClassField, properties, encoders);
    }

    protected static String toJSON(Object object, int level, int maxLevel, boolean humanOutput, boolean includeNulls, boolean includeClassField, Map<String, Object> properties, Collection<ObjectEncoder> encoders) {
        StringBuilder sb = new StringBuilder();

        if (object == null) {
            if (includeNulls)
                // nulls use the constant
                sb.append(NULL);
        } else {
            Class<?> type = ConstructorUtils.getWrapper(object.getClass());
            if ( ConstructorUtils.isClassSimple(type) ) {
                // Simple (String, Number, etc.)
                if (Date.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type)) {
                    // date
                    Date d = (Date) object;
                    sb.append(d.getTime());
                } else if (Number.class.isAssignableFrom(type)) {
                    // number
                    sb.append(object.toString());
                } else if (Boolean.class.isAssignableFrom(type)) {
                    // boolean
                    if ( ((Boolean)object).booleanValue() ) {
                        sb.append(BOOLEAN_TRUE);
                    } else {
                        sb.append(BOOLEAN_FALSE);
                    }
                } else {
                    sb.append(QUOT);
                    sb.append( JSONTranscoder.escapeForJSON(object.toString()) );
                    sb.append(QUOT);
                }
            } else if ( ConstructorUtils.isClassArray(type) ) {
                // ARRAY
                int length = ArrayUtils.size((Object[])object);
                sb.append(ARRAY_BEG);
                if (length > 0) {
                    for (int i = 0; i < length; ++i) {
                        if (i > 0) {
                            sb.append(JSON_SEP);
                        }
                        JSONTranscoder.makeEOL(sb, humanOutput);
                        JSONTranscoder.makeLevelSpaces(sb, level+1, humanOutput);
                        sb.append( toJSON(Array.get(object, i), level+1, maxLevel, humanOutput, includeNulls, includeClassField, properties, encoders) );
                    }
                    JSONTranscoder.makeEOL(sb, humanOutput);
                    JSONTranscoder.makeLevelSpaces(sb, level, humanOutput);
                }
                sb.append(ARRAY_END);
            } else if ( ConstructorUtils.isClassCollection(type) ) {
                // COLLECTION
                Collection<Object> collection = (Collection<Object>) object;
                sb.append(ARRAY_BEG);
                if (! collection.isEmpty()) {
                    boolean first = true;
                    for (Object element : collection) {
                        if (first) {
                            first = false;
                        } else {
                            sb.append(JSON_SEP);
                        }
                        JSONTranscoder.makeEOL(sb, humanOutput);
                        JSONTranscoder.makeLevelSpaces(sb, level+1, humanOutput);
                        sb.append( JSONTranscoder.toJSON(element, level+1, maxLevel, humanOutput, includeNulls, includeClassField, properties, encoders) );
                    }
                    JSONTranscoder.makeEOL(sb, humanOutput);
                    JSONTranscoder.makeLevelSpaces(sb, level, humanOutput);
                }
                sb.append(ARRAY_END);
            } else {
                // must be a bean or map, make sure it is a map
                // special handling for certain object types
                String special = TranscoderUtils.handleObjectEncoding(object, encoders);
                if (special != null) {
                    if ("".equals(special)) {
                        // skip this one entirely
                        sb.append(NULL);
                    } else {
                        // just use the value in special to represent this
                        sb.append(QUOT);
                        sb.append( JSONTranscoder.escapeForJSON(special) );
                        sb.append(QUOT);
                    }
                } else {
                    // normal handling
                    if (maxLevel <= level) {
                        // if the max level was reached then stop
                        sb.append(QUOT);
                        sb.append( "MAX level reached (" );
                        sb.append( level );
                        sb.append( "):" );
                        sb.append( JSONTranscoder.escapeForJSON(object.toString()) );
                        sb.append(QUOT);
                    } else {
                        Map<String, Object> map = null;
                        if (Map.class.isAssignableFrom(type)) {
                            map = (Map<String, Object>) object;
                        } else {
                            // reflect over objects
                            map = ReflectUtils.getInstance().getObjectValues(object, FieldsFilter.SERIALIZABLE, includeClassField);
                        }
                        // add in the optional properties if it makes sense to do so
                        if (level == 0 && properties != null && ! properties.isEmpty()) {
                            map.putAll(properties);
                        }
                        sb.append(OBJ_BEG);
                        boolean first = true;
                        for (Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey() != null) {
                                Object value = entry.getValue();
                                if (value != null || includeNulls) {
                                    if (first) {
                                        first = false;
                                    } else {
                                        sb.append(JSON_SEP);
                                    }
                                    JSONTranscoder.makeEOL(sb, humanOutput);
                                    JSONTranscoder.makeLevelSpaces(sb, level+1, humanOutput);
                                    sb.append(QUOT);
                                    sb.append(entry.getKey());
                                    sb.append(QUOT);
                                    sb.append(OBJ_SEP);
                                    if (humanOutput) { sb.append(SPACE); }
                                    sb.append( JSONTranscoder.toJSON(value, level+1, maxLevel, humanOutput, includeNulls, includeClassField, properties, encoders) );
                                }
                            }
                        }
                        JSONTranscoder.makeEOL(sb, humanOutput);
                        JSONTranscoder.makeLevelSpaces(sb, level, humanOutput);
                        sb.append(OBJ_END);
                    }
                }
            }
        }
        return sb.toString();
    }

    private static void makeEOL(StringBuilder sb, boolean includeEOL) {
        if (includeEOL) sb.append(EOL);
    }

    private static void makeLevelSpaces(StringBuilder sb, int level, boolean includeEOL) {
        if (includeEOL) {
            for (int i = 0; i < level; i++)
                sb.append(SPACES);
        }
    }

    /**
     * Escape a string for JSON encoding
     * @param string any string
     * @return the escaped string
     */
    public static String escapeForJSON(String string) {
        StringBuilder sb = new StringBuilder();
        if (string != null) {
            for (int i = 0, len = string.length(); i < len; i++) {
                char c = string.charAt(i);
                switch (c) {
                case QUOT:
                    sb.append("\\\"");
                    break;
                case BACK:
                    sb.append("\\\\");
                    break;
                case SLASH:
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (Character.isISOControl(c)) {
                        sb.append("\\u");
                        int n = c;
                        for (int j = 0; j < 4; ++j) {
                            int digit = (n & 0xf000) >> 12;
                            sb.append(hex[digit]);
                            n <<= 4;
                        }
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Create Java objects from JSON (note that only simple java objects, maps, and arrays will be returned) <br/>
     * Dates will come back in as UTC timecodes or possibly strings which you will need to parse manually <br/>
     * Numbers will come in as int if they are small, long if they are big, and BigInteger if they are huge,
     * floating point is handled similarly: float, double, BigDecimal <br/>
     * JSON arrays come back as a List always, similarly, any collection or array that was output will come back as a list <br/>
     * You can use the {@link ConversionUtils} to help with conversion if needed <br/>
     *
     * Derived from code at:
     * https://svn.sourceforge.net/svnroot/stringtree/trunk/src/delivery/java/org/stringtree/json/JSONWriter.java
     */
    public class JsonReader {

        private CharacterIterator it; // TODO crikey - thread safety
        private char c; // TODO crikey - thread safety
        private Object token; // TODO crikey - thread safety
        private StringBuffer buf = new StringBuffer(); // TODO crikey - thread safety

        private char next() {
            this.c = this.it.next();
            return this.c;
        }

        private void skipWhiteSpace() {
            while (Character.isWhitespace(this.c))
                next();
        }

        public Object read(CharacterIterator it) {
            return this.read(it, NEXT);
        }

        public Object read(String string) {
            CharacterIterator ci = new StringCharacterIterator(string);
            return this.read(ci, FIRST);
        }

        /**
         * 3rd public input method, should mostly not be used directly
         * Called ONLY by the other 2 read methods ({@link #read(String)} and {@link #read(CharacterIterator)})
         * @param ci
         * @param start
         * @return the Object which represents the JSON string, null for invalid
         */
        public Object read(CharacterIterator ci, int start) {
            this.it = ci;
            switch (start) {
            case FIRST:
                this.c = this.it.first();
                break;
            case CURRENT:
                this.c = this.it.current();
                break;
            case NEXT:
                this.c = this.it.next();
                break;
            }
            Object o = this.read();
            if (MARK_END_INPUT.equals(o) || MARK_COLON.equals(o)) {
                o = null; // SPECIAL cases - empty or invalid input
            }
            return o;
        }

        private Object read() {
            this.skipWhiteSpace();
            char ch = this.c;
            next();
            switch (ch) {
                case '"': this.token = string(); break;
                case '[': this.token = array(); break;
                case ']': this.token = MARK_ARRAY_END; break;
                case ',': this.token = MARK_COMMA; break;
                case '{': this.token = object(); break;
                case '}': this.token = MARK_OBJECT_END; break;
                case ':': this.token = MARK_COLON; break;
                case 't':
                    this.next(); this.next(); this.next(); // assumed r-u-e
                    this.token = Boolean.TRUE;
                    break;
                case'f':
                    this.next(); this.next(); this.next(); this.next(); // assumed a-l-s-e
                    this.token = Boolean.FALSE;
                    break;
                case 'n':
                    this.next(); this.next(); this.next(); // assumed u-l-l
                    this.token = null;
                    break;
                case StringCharacterIterator.DONE:
                    this.token = MARK_END_INPUT;
                    break;
                default:
                    if (Character.isDigit(ch) || ch == '-') {
                        // Push this back on so it's part of the number
                        this.c = this.it.previous();
                        this.token = number();
                    }
            }
            // System.out.println("token: " + token); // enable this line to see the token stream
            return this.token;
        }

        private Object object() {
            Map<Object, Object> ret = new ArrayOrderedMap<>();
            Object key = this.read();
            while (this.token != MARK_OBJECT_END && this.token != MARK_END_INPUT) {
                read(); // should be a colon
                if (this.token != MARK_OBJECT_END) {
                    ret.put(key, this.read());
                    if (this.read() == MARK_COMMA) {
                        key = this.read();
                    }
                }
            }
            if (MARK_END_INPUT.equals(this.token)) {
                ret = null; // did not end the object in a valid way
            }
            return ret;
        }

        private Object array() {
            Collection<Object> ret = new ArrayList<>();
            Object value = read();
            while (this.token != MARK_ARRAY_END && this.token != MARK_END_INPUT) {
                ret.add(value);
                if (this.read() == MARK_COMMA) {
                    value = this.read();
                }
            }
            if (MARK_END_INPUT.equals(token)) {
                ret = null; // did not end the array in a valid way
            }
            return ret;
        }

        private Object number() {
            int length = 0;
            boolean isFloatingPoint = false;
            buf.setLength(0);

            if (this.c == '-') {
                this.add();
            }
            length += this.addDigits();
            if (this.c == '.') {
                this.add();
                length += this.addDigits();
                isFloatingPoint = true;
            }
            if (this.c == 'e' || this.c == 'E') {
                this.add();
                if (this.c == '+' || this.c == '-')
                    this.add();
                this.addDigits();
                isFloatingPoint = true;
            }

            String s = this.buf.toString();
            // more friendly handling of numbers
            Object num = null;
            if (isFloatingPoint) {
                if (length < 10) {
                    num = Float.valueOf(s);
                } else if (length < 17) {
                    num = Double.valueOf(s);
                } else {
                    num = new BigDecimal(s);
                }
            } else {
                if (length < 10) {
                    num = Integer.valueOf(s);
                } else if (length < 19) {
                    num = Long.valueOf(s);
                } else {
                    num = new BigInteger(s);
                }
            }
            return num;
        }

        private int addDigits() {
            int ret;
            for (ret = 0; Character.isDigit(c); ++ret) {
                this.add();
            }
            return ret;
        }

        private Object string() {
            this.buf.setLength(0);
            while (this.c != '"' && c != StringCharacterIterator.DONE) {
                if (this.c == '\\') {
                    next();
                    if (this.c == 'u') {
                        this.add(unicode());
                    } else {
                        Object value = JSONTranscoder.escapes.get(new Character(c));
                        if (value != null)
                            this.add(((Character) value).charValue());
                    }
                } else {
                    add();
                }
            }
            // unterminated string will terminate automatically
            this.next();
            return buf.toString();
        }

        private void add(char cc) {
            this.buf.append(cc);
            this.next();
        }

        private void add() {
            this.add(this.c);
        }

        private char unicode() {
            int value = 0;
            for (int i = 0; i < 4; ++i) {
                switch (next()) {
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    value = (value << 4) + this.c - '0';
                    break;
                case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                    value = (value << 4) + (this.c - 'a') + 10;
                    break;
                case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                    value = (value << 4) + (this.c - 'A') + 10;
                    break;
                }
            }
            return (char) value;
        }
    }

    private void initialize()
    {
        this.encoders = new ArrayList<>();
        this.humanOutput = false;
        this.includeNulls = true;
        this.includeClassField = false;
        this.maxLevel = JSONTranscoder.MAXIMUM_LEVEL;
    }

}

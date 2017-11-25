package com.testanywhere.core.utilities.class_support;

final class TypeDescriptor
{
    private static final int SHIFT_1 = 8;
    private static final int SHIFT_2 = 16;
    private static final int SHIFT_3 = 24;

    /**
     * The sort of array reference types. See .
     */
    public static final int ARRAY = 9;

    /**
     * The sort of the <tt>boolean</tt> type. See .
     */
    public static final int BOOLEAN = 1;

    /**
     * The <tt>boolean</tt> type.
     */
    public static final TypeDescriptor BOOLEAN_TYPE = new TypeDescriptor(BOOLEAN, null, ('Z' << SHIFT_3) | (5 << SHIFT_1) | 1, 1);

    /**
     * The sort of the <tt>byte</tt> type. See .
     */
    public static final int BYTE = 3;

    /**
     * The <tt>byte</tt> type.
     */
    public static final TypeDescriptor BYTE_TYPE = new TypeDescriptor(BYTE, null, ('B' << SHIFT_1) | (5 << SHIFT_3) | 1,
            1);

    /**
     * The sort of the <tt>char</tt> type. See .
     */
    public static final int CHAR = 2;

    /**
     * The <tt>char</tt> type.
     */
    public static final TypeDescriptor CHAR_TYPE = new TypeDescriptor(CHAR, null, ('C' << SHIFT_3) | (6 << SHIFT_1) | 1,
            1);

    /**
     * The sort of the <tt>double</tt> type. See .
     */
    public static final int DOUBLE = 8;

    /**
     * The <tt>double</tt> type.
     */
    public static final TypeDescriptor DOUBLE_TYPE = new TypeDescriptor(DOUBLE, null, ('D' << SHIFT_3) | (3 << SHIFT_2) | (3 << SHIFT_1)
            | 2, 1);

    /**
     * The sort of the <tt>float</tt> type. See .
     */
    public static final int FLOAT = 6;

    /**
     * The <tt>float</tt> type.
     */
    public static final TypeDescriptor FLOAT_TYPE = new TypeDescriptor(FLOAT, null, ('F' << SHIFT_3) | (2 << SHIFT_2) | (2 << SHIFT_1)
            | 1, 1);

    /**
     * The sort of the <tt>int</tt> type. See .
     */
    public static final int INT = 5;

    /**
     * The <tt>int</tt> type.
     */
    public static final TypeDescriptor INT_TYPE = new TypeDescriptor(INT, null, ('I' << SHIFT_3) | (0) | 1, 1);

    /**
     * The sort of the <tt>long</tt> type. See .
     */
    public static final int LONG = 7;

    /**
     * The <tt>long</tt> type.
     */
    public static final TypeDescriptor LONG_TYPE = new TypeDescriptor(LONG, null, ('J' << SHIFT_3) | (1 << SHIFT_2) | (1 << SHIFT_1) | 2,
            1);

    /**
     * The sort of method types. See .
     */
    public static final int METHOD = 11;

    /**
     * The sort of object reference types. See .
     */
    public static final int OBJECT = 10;

    /**
     * The sort of the <tt>short</tt> type. See .
     */
    public static final int SHORT = 4;

    /**
     * The <tt>short</tt> type.
     */
    public static final TypeDescriptor SHORT_TYPE = new TypeDescriptor(SHORT, null, ('S' << SHIFT_3) | (7 << SHIFT_1)
            | 1, 1);

    /**
     * The sort of the <tt>void</tt> type. See .
     */
    public static final int VOID = 0;

    /**
     * The <tt>void</tt> type.
     */
    public static final TypeDescriptor VOID_TYPE = new TypeDescriptor(VOID, null, ('V' << SHIFT_3) | (5 << SHIFT_2) | 0,
            1);

    /**
     * A buffer containing the internal name of this Java type. This field is only used for reference
     * types.
     */
    private final char[] buf;

    /**
     * The length of the internal name of this Java type.
     */
    private final int len;

    /**
     * The offset of the internal name of this Java type in {@link #buf buf} or, for primitive types,
     * the size, descriptor and getOpcode offsets for this type (byte 0 contains the size, byte 1 the
     * descriptor, byte 2 the offset for IALOAD or IASTORE, byte 3 the offset for all other
     * instructions).
     */
    private final int off;

    /**
     * The sort of this Java type.
     */
    private final int sort;

    /**
     * Constructs a reference type.
     *
     * @param sort the sort of the reference type to be constructed.
     * @param buf a buffer containing the descriptor of the previous type.
     * @param off the offset of this descriptor in the previous buffer.
     * @param len the length of this descriptor.
     */
    private TypeDescriptor(final int sort, final char[] buf, final int off, final int len)
    {
        this.sort = sort;
        this.buf = buf;
        this.off = off;
        this.len = len;
    }

    /**
     * Returns a TypeDescriptor for the internal type.
     */
    public static TypeDescriptor getInternalType(final String internalType)
    {
        return getType(internalType.toCharArray(), 0);
    }

    /**
     * Returns the Java type corresponding to the given internal name.
     *
     * @param internalName an internal name.
     * @return the Java type corresponding to the given internal name.
     */
    public static TypeDescriptor getObjectType(final String internalName)
    {
        char[] buf = internalName.toCharArray();
        return new TypeDescriptor(buf[0] == '[' ? ARRAY : OBJECT, buf, 0, buf.length);
    }

    /**
     * Returns the Java types corresponding to the argument types of the given method descriptor.
     *
     * @param methodDescriptor a method descriptor.
     * @return the Java types corresponding to the argument types of the given method descriptor.
     */
    public static TypeDescriptor[] getArgumentTypes(final String methodDescriptor)
    {
        char[] buf = methodDescriptor.toCharArray();
        int off = 1;
        int size = 0;
        while (true) {
            char car = buf[off++];
            if (car == ')')
            {
                break;
            }
            else if (car == 'L')
            {
                //noinspection StatementWithEmptyBody
                while (buf[off++] != ';') {}
                ++size;
            }
            else if (car != '[')
            {
                ++size;
            }
        }
        TypeDescriptor[] args = new TypeDescriptor[size];
        off = 1;
        size = 0;
        while (buf[off] != ')')
        {
            args[size] = getType(buf, off);
            off += args[size].len + (args[size].sort == OBJECT ? 2 : 0);
            size += 1;
        }
        return args;
    }

    /**
     * Returns the Java type corresponding to the return type of the given method descriptor.
     *
     * @param methodDescriptor a method descriptor.
     * @return the Java type corresponding to the return type of the given method descriptor.
     */
    public static TypeDescriptor getReturnType(final String methodDescriptor)
    {
        char[] buf = methodDescriptor.toCharArray();
        return getType(buf, methodDescriptor.indexOf(')') + 1);
    }

    /**
     * Returns the Java type corresponding to the given type descriptor. For method descriptors, buf
     * is supposed to contain nothing more than the descriptor itself.
     *
     * @param buf a buffer containing a type descriptor.
     * @param off the offset of this descriptor in the previous buffer.
     * @return the Java type corresponding to the given type descriptor.
     */
    private static TypeDescriptor getType(final char[] buf, final int off)
    {
        int len;
        switch (buf[off])
        {
            case 'V':
                return VOID_TYPE;
            case 'Z':
                return BOOLEAN_TYPE;
            case 'C':
                return CHAR_TYPE;
            case 'B':
                return BYTE_TYPE;
            case 'S':
                return SHORT_TYPE;
            case 'I':
                return INT_TYPE;
            case 'F':
                return FLOAT_TYPE;
            case 'J':
                return LONG_TYPE;
            case 'D':
                return DOUBLE_TYPE;
            case '[':
                len = 1;
                while (buf[off + len] == '[')
                {
                    ++len;
                }
                if (buf[off + len] == 'L')
                {
                    ++len;
                    while (buf[off + len] != ';')
                    {
                        ++len;
                    }
                }
                return new TypeDescriptor(ARRAY, buf, off, len + 1);
            case 'L':
                len = 1;
                while (buf[off + len] != ';')
                {
                    ++len;
                }
                return new TypeDescriptor(OBJECT, buf, off + 1, len - 1);
            // case '(':
            default:
                return new TypeDescriptor(METHOD, buf, off, buf.length - off);
        }
    }

    /**
     * Returns the raw class corresponding to this type descriptor. Primitive types return their
     * corresponding wrappers.
     *
     * @return the raw class corresponding to this type descriptor
     */
    public Class<?> getType() {
        return getType(null);
    }

    /**
     * Returns the raw class corresponding to this type descriptor. Primitive types return their
     * corresponding wrappers.
     *
     * @param loader the class loader used to load the actual raw class.
     * @return the raw class corresponding to this type descriptor
     */
    public Class<?> getType(ClassLoader loader)
    {
        try
        {
            switch (sort)
            {
                case VOID:
                    return Void.class;
                case BOOLEAN:
                    return Boolean.class;
                case CHAR:
                    return Character.class;
                case BYTE:
                    return Byte.class;
                case SHORT:
                    return Short.class;
                case INT:
                    return Integer.class;
                case FLOAT:
                    return Float.class;
                case LONG:
                    return Long.class;
                case DOUBLE:
                    return Double.class;
                case ARRAY:
                    TypeDescriptor elementType = getElementType();
                    StringBuilder sb = new StringBuilder();
                    for (int i = getDimensions(); i > 0; --i)
                        sb.append("[");
                    sb.append('L').append(elementType.getType().getName()).append(';');
                    return loader == null ? Class.forName(sb.toString()) : loader.loadClass(sb.toString());
                case OBJECT:
                    String clazz = new String(buf, off, len).replace('/', '.');
                    return loader == null ? Class.forName(clazz) : loader.loadClass(clazz);
                default:
                    return null;
            }
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Returns the number of dimensions of this array type. This method should only be used for an
     * array type.
     *
     * @return the number of dimensions of this array type.
     */
    public int getDimensions()
    {
        int i = 1;
        while (buf[off + i] == '[') { ++i; }
        return i;
    }

    /**
     * Returns the type of the elements of this array type. This method should only be used for an
     * array type.
     *
     * @return Returns the type of the elements of this array type.
     */
    public TypeDescriptor getElementType() {
        return getType(buf, off + getDimensions());
    }

    public String toString() {
        return getType().getName();
    }
}

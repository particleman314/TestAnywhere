package com.testanywhere.core.classes.support.cache;

import com.testanywhere.core.utilities.class_support.functional_support.ConstantsInterface;

public class CacheConstants implements ConstantsInterface
{
    public static final int NO_EXPIRY = -1;
    public static final int NO_SIZE_LIMIT = -1;

    // This ensures it is initialized when import by any class
    static
    {
        CacheConstants.getInstance();
    }

    // This makes for a singleton object to keep the only copy
    private static boolean isInitialized = false;

    public enum TEMPORAL_CACHE {
        NONE(NO_EXPIRY),
        ALL(0);

        private Integer value = null;

        TEMPORAL_CACHE(Integer i)
        {
            this.setValue(i);
        }

        public Integer getValue() {
            return this.value;
        }

        public void setValue(final Integer value) {
            this.value = value;
        }
    }

    public enum SIZE_CACHE {
        NONE(NO_SIZE_LIMIT),
        ALL(0);

        private Integer value = null;

        SIZE_CACHE(Integer i)
        {
            this.setValue(i);
        }

        public Integer getValue() {
            return this.value;
        }

        public void setValue(final Integer value) {
            this.value = value;
        }
    }

    public static CacheConstants getInstance()
    {
        if ( ! CacheConstants.isInitialized ) {
            CacheConstants.isInitialized = true;
            CacheConstants.initialize();
        }
        return CacheConstantsHolder.INSTANCE;
    }

    private static class CacheConstantsHolder
    {
        public static final CacheConstants INSTANCE = new CacheConstants();
    }

    // Allow a means to reset parameters if they are allowed to change
    @Override
    public void reset() {
        CacheConstants.initialize();
    }

    private static void initialize()
    {}

    private CacheConstants()
    {}
}

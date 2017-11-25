package com.testanywhere.core.utilities.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> {

   private final Type type;

   protected TypeReference() {
      Type superclass = getClass().getGenericSuperclass();
      if (superclass instanceof Class) {
         throw new RuntimeException("Missing type parameter.");
      }
      this.type = ( (ParameterizedType)superclass ).getActualTypeArguments()[0];
   }

   public Type getType() {
      return this.type;
   }

   @SuppressWarnings("unchecked")
   public boolean equals(Object o) {
      return ( o instanceof TypeReference ) ? ( (TypeReference)o ).type.equals(this.type) : false;
   }

   public int hashCode() {
      return this.type.hashCode();
   }
}


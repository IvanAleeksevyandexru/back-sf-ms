/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.smartengines.common;

public class StringsSetIterator {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public StringsSetIterator(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(StringsSetIterator obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        jnisecommonJNI.delete_StringsSetIterator(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public StringsSetIterator(StringsSetIterator other) {
    this(jnisecommonJNI.new_StringsSetIterator(StringsSetIterator.getCPtr(other), other), true);
  }

  public String GetValue() {
    return jnisecommonJNI.StringsSetIterator_GetValue(swigCPtr, this);
  }

  public boolean Equals(StringsSetIterator rvalue) {
    return jnisecommonJNI.StringsSetIterator_Equals(swigCPtr, this, StringsSetIterator.getCPtr(rvalue), rvalue);
  }

  public void Advance() {
    jnisecommonJNI.StringsSetIterator_Advance(swigCPtr, this);
  }

}

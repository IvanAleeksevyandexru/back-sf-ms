/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.smartengines.id;

import com.smartengines.common.*;

public class IdEngine {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public IdEngine(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(IdEngine obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        jniidengineJNI.delete_IdEngine(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public IdSessionSettings CreateSessionSettings() {
    long cPtr = jniidengineJNI.IdEngine_CreateSessionSettings(swigCPtr, this);
    return (cPtr == 0) ? null : new IdSessionSettings(cPtr, true);
  }

  public IdSession SpawnSession(IdSessionSettings settings, String signature, IdFeedback feedback_reporter) {
    long cPtr = jniidengineJNI.IdEngine_SpawnSession__SWIG_0(swigCPtr, this, IdSessionSettings.getCPtr(settings), settings, signature, IdFeedback.getCPtr(feedback_reporter), feedback_reporter);
    return (cPtr == 0) ? null : new IdSession(cPtr, true);
  }

  public IdSession SpawnSession(IdSessionSettings settings, String signature) {
    long cPtr = jniidengineJNI.IdEngine_SpawnSession__SWIG_1(swigCPtr, this, IdSessionSettings.getCPtr(settings), settings, signature);
    return (cPtr == 0) ? null : new IdSession(cPtr, true);
  }

  public IdFaceSessionSettings CreateFaceSessionSettings() {
    long cPtr = jniidengineJNI.IdEngine_CreateFaceSessionSettings(swigCPtr, this);
    return (cPtr == 0) ? null : new IdFaceSessionSettings(cPtr, false);
  }

  public IdFaceSession SpawnFaceSession(IdFaceSessionSettings settings, String signature) {
    long cPtr = jniidengineJNI.IdEngine_SpawnFaceSession(swigCPtr, this, IdFaceSessionSettings.getCPtr(settings), settings, signature);
    return (cPtr == 0) ? null : new IdFaceSession(cPtr, false);
  }

  public IdFieldProcessingSessionSettings CreateFieldProcessingSessionSettings() {
    long cPtr = jniidengineJNI.IdEngine_CreateFieldProcessingSessionSettings(swigCPtr, this);
    return (cPtr == 0) ? null : new IdFieldProcessingSessionSettings(cPtr, false);
  }

  public IdFieldProcessingSession SpawnFieldProcessingSession(IdFieldProcessingSessionSettings settings, String signature) {
    long cPtr = jniidengineJNI.IdEngine_SpawnFieldProcessingSession(swigCPtr, this, IdFieldProcessingSessionSettings.getCPtr(settings), settings, signature);
    return (cPtr == 0) ? null : new IdFieldProcessingSession(cPtr, false);
  }

  public static IdEngine Create(String config_path, boolean lazy_configuration, int init_concurrency) {
    long cPtr = jniidengineJNI.IdEngine_Create__SWIG_0(config_path, lazy_configuration, init_concurrency);
    return (cPtr == 0) ? null : new IdEngine(cPtr, true);
  }

  public static IdEngine Create(String config_path, boolean lazy_configuration) {
    long cPtr = jniidengineJNI.IdEngine_Create__SWIG_1(config_path, lazy_configuration);
    return (cPtr == 0) ? null : new IdEngine(cPtr, true);
  }

  public static IdEngine Create(String config_path) {
    long cPtr = jniidengineJNI.IdEngine_Create__SWIG_2(config_path);
    return (cPtr == 0) ? null : new IdEngine(cPtr, true);
  }

  public static IdEngine Create(byte[] config_data, boolean lazy_configuration, int init_concurrency) {
    long cPtr = jniidengineJNI.IdEngine_Create__SWIG_3(config_data, lazy_configuration, init_concurrency);
    return (cPtr == 0) ? null : new IdEngine(cPtr, true);
  }

  public static IdEngine Create(byte[] config_data, boolean lazy_configuration) {
    long cPtr = jniidengineJNI.IdEngine_Create__SWIG_4(config_data, lazy_configuration);
    return (cPtr == 0) ? null : new IdEngine(cPtr, true);
  }

  public static IdEngine Create(byte[] config_data) {
    long cPtr = jniidengineJNI.IdEngine_Create__SWIG_5(config_data);
    return (cPtr == 0) ? null : new IdEngine(cPtr, true);
  }

  public static String GetVersion() {
    return jniidengineJNI.IdEngine_GetVersion();
  }

}

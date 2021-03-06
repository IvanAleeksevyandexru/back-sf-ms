/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.smartengines.id;

import com.smartengines.common.*;

public class IdFaceSession {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public IdFaceSession(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(IdFaceSession obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        jniidengineJNI.delete_IdFaceSession(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public String GetActivationRequest() {
    return jniidengineJNI.IdFaceSession_GetActivationRequest(swigCPtr, this);
  }

  public void Activate(String activation_response) {
    jniidengineJNI.IdFaceSession_Activate(swigCPtr, this, activation_response);
  }

  public boolean IsActivated() {
    return jniidengineJNI.IdFaceSession_IsActivated(swigCPtr, this);
  }

  public void AddFaceImage(Image face_image) {
    jniidengineJNI.IdFaceSession_AddFaceImage(swigCPtr, this, Image.getCPtr(face_image), face_image);
  }

  public IdFaceSimilarityResult GetSimilarityWith(Image compare_image) {
    return new IdFaceSimilarityResult(jniidengineJNI.IdFaceSession_GetSimilarityWith(swigCPtr, this, Image.getCPtr(compare_image), compare_image), true);
  }

  public IdFaceLivenessResult GetLivenessResult() {
    return new IdFaceLivenessResult(jniidengineJNI.IdFaceSession_GetLivenessResult(swigCPtr, this), false);
  }

  public IdFaceSimilarityResult GetSimilarity(Image face_image_a, Image face_image_b) {
    return new IdFaceSimilarityResult(jniidengineJNI.IdFaceSession_GetSimilarity(swigCPtr, this, Image.getCPtr(face_image_a), face_image_a, Image.getCPtr(face_image_b), face_image_b), true);
  }

  public IdFaceSimilarityResult GetFaceResult(Image face_image) {
    return new IdFaceSimilarityResult(jniidengineJNI.IdFaceSession_GetFaceResult(swigCPtr, this, Image.getCPtr(face_image), face_image), true);
  }

  public void Reset() {
    jniidengineJNI.IdFaceSession_Reset(swigCPtr, this);
  }

}

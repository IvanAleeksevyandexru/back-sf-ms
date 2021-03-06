/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.smartengines.common;

public class Image {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public Image(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(Image obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        jnisecommonJNI.delete_Image(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public static Image CreateEmpty() {
    long cPtr = jnisecommonJNI.Image_CreateEmpty();
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public static Image FromFile(String image_filename, Size max_size) {
    long cPtr = jnisecommonJNI.Image_FromFile__SWIG_0(image_filename, Size.getCPtr(max_size), max_size);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public static Image FromFile(String image_filename) {
    long cPtr = jnisecommonJNI.Image_FromFile__SWIG_1(image_filename);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public static Image FromFileBuffer(byte[] data, Size max_size) {
    long cPtr = jnisecommonJNI.Image_FromFileBuffer__SWIG_0(data, Size.getCPtr(max_size), max_size);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public static Image FromFileBuffer(byte[] data) {
    long cPtr = jnisecommonJNI.Image_FromFileBuffer__SWIG_1(data);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public static Image FromBuffer(byte[] raw_data, int width, int height, int stride, int channels) {
    long cPtr = jnisecommonJNI.Image_FromBuffer(raw_data, width, height, stride, channels);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public static Image FromYUVBuffer(byte[] yuv_data, int width, int height) {
    long cPtr = jnisecommonJNI.Image_FromYUVBuffer(yuv_data, width, height);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public static Image FromBase64Buffer(String base64_buffer, Size max_size) {
    long cPtr = jnisecommonJNI.Image_FromBase64Buffer__SWIG_0(base64_buffer, Size.getCPtr(max_size), max_size);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public static Image FromBase64Buffer(String base64_buffer) {
    long cPtr = jnisecommonJNI.Image_FromBase64Buffer__SWIG_1(base64_buffer);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public Image CloneDeep() {
    long cPtr = jnisecommonJNI.Image_CloneDeep(swigCPtr, this);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public Image CloneShallow() {
    long cPtr = jnisecommonJNI.Image_CloneShallow(swigCPtr, this);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void Clear() {
    jnisecommonJNI.Image_Clear(swigCPtr, this);
  }

  public int GetRequiredBufferLength() {
    return jnisecommonJNI.Image_GetRequiredBufferLength(swigCPtr, this);
  }

  public int CopyToBuffer(byte[] buffer) {
    return jnisecommonJNI.Image_CopyToBuffer(swigCPtr, this, buffer);
  }

  public void Save(String image_filename) {
    jnisecommonJNI.Image_Save(swigCPtr, this, image_filename);
  }

  public int GetRequiredBase64BufferLength() {
    return jnisecommonJNI.Image_GetRequiredBase64BufferLength(swigCPtr, this);
  }

  public int CopyBase64ToBuffer(String out_buffer, int buffer_length) {
    return jnisecommonJNI.Image_CopyBase64ToBuffer(swigCPtr, this, out_buffer, buffer_length);
  }

  public MutableString GetBase64String() {
    return new MutableString(jnisecommonJNI.Image_GetBase64String(swigCPtr, this), true);
  }

  public double EstimateFocusScore(double quantile) {
    return jnisecommonJNI.Image_EstimateFocusScore__SWIG_0(swigCPtr, this, quantile);
  }

  public double EstimateFocusScore() {
    return jnisecommonJNI.Image_EstimateFocusScore__SWIG_1(swigCPtr, this);
  }

  public void Resize(Size new_size) {
    jnisecommonJNI.Image_Resize(swigCPtr, this, Size.getCPtr(new_size), new_size);
  }

  public Image CloneResized(Size new_size) {
    long cPtr = jnisecommonJNI.Image_CloneResized(swigCPtr, this, Size.getCPtr(new_size), new_size);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void Crop(Quadrangle quad) {
    jnisecommonJNI.Image_Crop__SWIG_0(swigCPtr, this, Quadrangle.getCPtr(quad), quad);
  }

  public Image CloneCropped(Quadrangle quad) {
    long cPtr = jnisecommonJNI.Image_CloneCropped__SWIG_0(swigCPtr, this, Quadrangle.getCPtr(quad), quad);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void Crop(Quadrangle quad, Size size) {
    jnisecommonJNI.Image_Crop__SWIG_1(swigCPtr, this, Quadrangle.getCPtr(quad), quad, Size.getCPtr(size), size);
  }

  public Image CloneCropped(Quadrangle quad, Size size) {
    long cPtr = jnisecommonJNI.Image_CloneCropped__SWIG_1(swigCPtr, this, Quadrangle.getCPtr(quad), quad, Size.getCPtr(size), size);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void Crop(Rectangle rect) {
    jnisecommonJNI.Image_Crop__SWIG_2(swigCPtr, this, Rectangle.getCPtr(rect), rect);
  }

  public Image CloneCropped(Rectangle rect) {
    long cPtr = jnisecommonJNI.Image_CloneCropped__SWIG_2(swigCPtr, this, Rectangle.getCPtr(rect), rect);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public Image CloneCroppedShallow(Rectangle rect) {
    long cPtr = jnisecommonJNI.Image_CloneCroppedShallow(swigCPtr, this, Rectangle.getCPtr(rect), rect);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void Mask(Rectangle rect, int pixel_expand) {
    jnisecommonJNI.Image_Mask__SWIG_0(swigCPtr, this, Rectangle.getCPtr(rect), rect, pixel_expand);
  }

  public void Mask(Rectangle rect) {
    jnisecommonJNI.Image_Mask__SWIG_1(swigCPtr, this, Rectangle.getCPtr(rect), rect);
  }

  public Image CloneMasked(Rectangle rect, int pixel_expand) {
    long cPtr = jnisecommonJNI.Image_CloneMasked__SWIG_0(swigCPtr, this, Rectangle.getCPtr(rect), rect, pixel_expand);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public Image CloneMasked(Rectangle rect) {
    long cPtr = jnisecommonJNI.Image_CloneMasked__SWIG_1(swigCPtr, this, Rectangle.getCPtr(rect), rect);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void Mask(Quadrangle quad, int pixel_expand) {
    jnisecommonJNI.Image_Mask__SWIG_2(swigCPtr, this, Quadrangle.getCPtr(quad), quad, pixel_expand);
  }

  public void Mask(Quadrangle quad) {
    jnisecommonJNI.Image_Mask__SWIG_3(swigCPtr, this, Quadrangle.getCPtr(quad), quad);
  }

  public Image CloneMasked(Quadrangle quad, int pixel_expand) {
    long cPtr = jnisecommonJNI.Image_CloneMasked__SWIG_2(swigCPtr, this, Quadrangle.getCPtr(quad), quad, pixel_expand);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public Image CloneMasked(Quadrangle quad) {
    long cPtr = jnisecommonJNI.Image_CloneMasked__SWIG_3(swigCPtr, this, Quadrangle.getCPtr(quad), quad);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void FlipVertical() {
    jnisecommonJNI.Image_FlipVertical(swigCPtr, this);
  }

  public Image CloneFlippedVertical() {
    long cPtr = jnisecommonJNI.Image_CloneFlippedVertical(swigCPtr, this);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void FlipHorizontal() {
    jnisecommonJNI.Image_FlipHorizontal(swigCPtr, this);
  }

  public Image CloneFlippedHorizontal() {
    long cPtr = jnisecommonJNI.Image_CloneFlippedHorizontal(swigCPtr, this);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void Rotate90(int times) {
    jnisecommonJNI.Image_Rotate90(swigCPtr, this, times);
  }

  public Image CloneRotated90(int times) {
    long cPtr = jnisecommonJNI.Image_CloneRotated90(swigCPtr, this, times);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public void AverageChannels() {
    jnisecommonJNI.Image_AverageChannels(swigCPtr, this);
  }

  public Image CloneAveragedChannels() {
    long cPtr = jnisecommonJNI.Image_CloneAveragedChannels(swigCPtr, this);
    return (cPtr == 0) ? null : new Image(cPtr, true);
  }

  public int GetWidth() {
    return jnisecommonJNI.Image_GetWidth(swigCPtr, this);
  }

  public int GetHeight() {
    return jnisecommonJNI.Image_GetHeight(swigCPtr, this);
  }

  public Size GetSize() {
    return new Size(jnisecommonJNI.Image_GetSize(swigCPtr, this), true);
  }

  public int GetStride() {
    return jnisecommonJNI.Image_GetStride(swigCPtr, this);
  }

  public int GetChannels() {
    return jnisecommonJNI.Image_GetChannels(swigCPtr, this);
  }

  public boolean IsMemoryOwner() {
    return jnisecommonJNI.Image_IsMemoryOwner(swigCPtr, this);
  }

  public void ForceMemoryOwner() {
    jnisecommonJNI.Image_ForceMemoryOwner(swigCPtr, this);
  }

  public void Serialize(Serializer serializer) {
    jnisecommonJNI.Image_Serialize(swigCPtr, this, Serializer.getCPtr(serializer), serializer);
  }

}

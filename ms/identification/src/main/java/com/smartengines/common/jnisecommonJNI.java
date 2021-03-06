/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.smartengines.common;

public class jnisecommonJNI {
  public final static native long new_SerializationParameters__SWIG_0();
  public final static native void delete_SerializationParameters(long jarg1);
  public final static native long new_SerializationParameters__SWIG_1(long jarg1, SerializationParameters jarg1_);
  public final static native boolean SerializationParameters_HasIgnoredObjectType(long jarg1, SerializationParameters jarg1_, String jarg2);
  public final static native void SerializationParameters_AddIgnoredObjectType(long jarg1, SerializationParameters jarg1_, String jarg2);
  public final static native void SerializationParameters_RemoveIgnoredObjectType(long jarg1, SerializationParameters jarg1_, String jarg2);
  public final static native long SerializationParameters_IgnoredObjectTypesBegin(long jarg1, SerializationParameters jarg1_);
  public final static native long SerializationParameters_IgnoredObjectTypesEnd(long jarg1, SerializationParameters jarg1_);
  public final static native boolean SerializationParameters_HasIgnoredKey(long jarg1, SerializationParameters jarg1_, String jarg2);
  public final static native void SerializationParameters_AddIgnoredKey(long jarg1, SerializationParameters jarg1_, String jarg2);
  public final static native void SerializationParameters_RemoveIgnoredKey(long jarg1, SerializationParameters jarg1_, String jarg2);
  public final static native long SerializationParameters_IgnoredKeysBegin(long jarg1, SerializationParameters jarg1_);
  public final static native long SerializationParameters_IgnoredKeysEnd(long jarg1, SerializationParameters jarg1_);
  public final static native void delete_Serializer(long jarg1);
  public final static native void Serializer_Reset(long jarg1, Serializer jarg1_);
  public final static native String Serializer_GetCStr(long jarg1, Serializer jarg1_);
  public final static native String Serializer_SerializerType(long jarg1, Serializer jarg1_);
  public final static native long Serializer_CreateJSONSerializer(long jarg1, SerializationParameters jarg1_);
  public final static native long new_Rectangle__SWIG_0();
  public final static native long new_Rectangle__SWIG_1(int jarg1, int jarg2, int jarg3, int jarg4);
  public final static native void Rectangle_Serialize(long jarg1, Rectangle jarg1_, long jarg2, Serializer jarg2_);
  public final static native void Rectangle_x_set(long jarg1, Rectangle jarg1_, int jarg2);
  public final static native int Rectangle_x_get(long jarg1, Rectangle jarg1_);
  public final static native void Rectangle_y_set(long jarg1, Rectangle jarg1_, int jarg2);
  public final static native int Rectangle_y_get(long jarg1, Rectangle jarg1_);
  public final static native void Rectangle_width_set(long jarg1, Rectangle jarg1_, int jarg2);
  public final static native int Rectangle_width_get(long jarg1, Rectangle jarg1_);
  public final static native void Rectangle_height_set(long jarg1, Rectangle jarg1_, int jarg2);
  public final static native int Rectangle_height_get(long jarg1, Rectangle jarg1_);
  public final static native void delete_Rectangle(long jarg1);
  public final static native long new_Point__SWIG_0();
  public final static native long new_Point__SWIG_1(double jarg1, double jarg2);
  public final static native void Point_Serialize(long jarg1, Point jarg1_, long jarg2, Serializer jarg2_);
  public final static native void Point_x_set(long jarg1, Point jarg1_, double jarg2);
  public final static native double Point_x_get(long jarg1, Point jarg1_);
  public final static native void Point_y_set(long jarg1, Point jarg1_, double jarg2);
  public final static native double Point_y_get(long jarg1, Point jarg1_);
  public final static native void delete_Point(long jarg1);
  public final static native long new_Size__SWIG_0();
  public final static native long new_Size__SWIG_1(int jarg1, int jarg2);
  public final static native void Size_Serialize(long jarg1, Size jarg1_, long jarg2, Serializer jarg2_);
  public final static native void Size_width_set(long jarg1, Size jarg1_, int jarg2);
  public final static native int Size_width_get(long jarg1, Size jarg1_);
  public final static native void Size_height_set(long jarg1, Size jarg1_, int jarg2);
  public final static native int Size_height_get(long jarg1, Size jarg1_);
  public final static native void delete_Size(long jarg1);
  public final static native long new_Quadrangle__SWIG_0();
  public final static native long new_Quadrangle__SWIG_1(long jarg1, Point jarg1_, long jarg2, Point jarg2_, long jarg3, Point jarg3_, long jarg4, Point jarg4_);
  public final static native long Quadrangle_GetPoint(long jarg1, Quadrangle jarg1_, int jarg2);
  public final static native long Quadrangle_GetMutablePoint(long jarg1, Quadrangle jarg1_, int jarg2);
  public final static native void Quadrangle_SetPoint(long jarg1, Quadrangle jarg1_, int jarg2, long jarg3, Point jarg3_);
  public final static native long Quadrangle_GetBoundingRectangle(long jarg1, Quadrangle jarg1_);
  public final static native void Quadrangle_Serialize(long jarg1, Quadrangle jarg1_, long jarg2, Serializer jarg2_);
  public final static native void delete_Quadrangle(long jarg1);
  public final static native long new_QuadranglesMapIterator(long jarg1, QuadranglesMapIterator jarg1_);
  public final static native void delete_QuadranglesMapIterator(long jarg1);
  public final static native String QuadranglesMapIterator_GetKey(long jarg1, QuadranglesMapIterator jarg1_);
  public final static native long QuadranglesMapIterator_GetValue(long jarg1, QuadranglesMapIterator jarg1_);
  public final static native boolean QuadranglesMapIterator_Equals(long jarg1, QuadranglesMapIterator jarg1_, long jarg2, QuadranglesMapIterator jarg2_);
  public final static native void QuadranglesMapIterator_Advance(long jarg1, QuadranglesMapIterator jarg1_);
  public final static native long new_Polygon__SWIG_0();
  public final static native long new_Polygon__SWIG_1(long jarg1, Point jarg1_, int jarg2);
  public final static native long new_Polygon__SWIG_2(long jarg1, Polygon jarg1_);
  public final static native void delete_Polygon(long jarg1);
  public final static native int Polygon_GetPointsCount(long jarg1, Polygon jarg1_);
  public final static native long Polygon_GetPoints(long jarg1, Polygon jarg1_);
  public final static native long Polygon_GetPoint(long jarg1, Polygon jarg1_, int jarg2);
  public final static native long Polygon_GetMutablePoint(long jarg1, Polygon jarg1_, int jarg2);
  public final static native void Polygon_SetPoint(long jarg1, Polygon jarg1_, int jarg2, long jarg3, Point jarg3_);
  public final static native void Polygon_Resize(long jarg1, Polygon jarg1_, int jarg2);
  public final static native long Polygon_GetBoundingRectangle(long jarg1, Polygon jarg1_);
  public final static native void Polygon_Serialize(long jarg1, Polygon jarg1_, long jarg2, Serializer jarg2_);
  public final static native boolean ProjectiveTransform_CanCreate__SWIG_0(long jarg1, Quadrangle jarg1_, long jarg2, Quadrangle jarg2_);
  public final static native boolean ProjectiveTransform_CanCreate__SWIG_1(long jarg1, Quadrangle jarg1_, long jarg2, Size jarg2_);
  public final static native long ProjectiveTransform_Create__SWIG_0();
  public final static native long ProjectiveTransform_Create__SWIG_1(long jarg1, Quadrangle jarg1_, long jarg2, Quadrangle jarg2_);
  public final static native long ProjectiveTransform_Create__SWIG_2(long jarg1, Quadrangle jarg1_, long jarg2, Size jarg2_);
  public final static native void delete_ProjectiveTransform(long jarg1);
  public final static native long ProjectiveTransform_Clone(long jarg1, ProjectiveTransform jarg1_);
  public final static native long ProjectiveTransform_TransformPoint(long jarg1, ProjectiveTransform jarg1_, long jarg2, Point jarg2_);
  public final static native long ProjectiveTransform_TransformQuad(long jarg1, ProjectiveTransform jarg1_, long jarg2, Quadrangle jarg2_);
  public final static native long ProjectiveTransform_TransformPolygon(long jarg1, ProjectiveTransform jarg1_, long jarg2, Polygon jarg2_);
  public final static native boolean ProjectiveTransform_IsInvertable(long jarg1, ProjectiveTransform jarg1_);
  public final static native void ProjectiveTransform_Invert(long jarg1, ProjectiveTransform jarg1_);
  public final static native long ProjectiveTransform_CloneInverted(long jarg1, ProjectiveTransform jarg1_);
  public final static native void ProjectiveTransform_Serialize(long jarg1, ProjectiveTransform jarg1_, long jarg2, Serializer jarg2_);
  public final static native long new_MutableString__SWIG_0();
  public final static native long new_MutableString__SWIG_1(String jarg1);
  public final static native long new_MutableString__SWIG_2(long jarg1, MutableString jarg1_);
  public final static native void delete_MutableString(long jarg1);
  public final static native String MutableString_GetCStr(long jarg1, MutableString jarg1_);
  public final static native int MutableString_GetLength(long jarg1, MutableString jarg1_);
  public final static native void MutableString_Serialize(long jarg1, MutableString jarg1_, long jarg2, Serializer jarg2_);
  public final static native long new_OcrCharVariant__SWIG_0();
  public final static native long new_OcrCharVariant__SWIG_1(long jarg1, MutableString jarg1_, double jarg2);
  public final static native long new_OcrCharVariant__SWIG_2(String jarg1, double jarg2);
  public final static native void delete_OcrCharVariant(long jarg1);
  public final static native String OcrCharVariant_GetCharacter(long jarg1, OcrCharVariant jarg1_);
  public final static native void OcrCharVariant_SetCharacter__SWIG_0(long jarg1, OcrCharVariant jarg1_, long jarg2, MutableString jarg2_);
  public final static native void OcrCharVariant_SetCharacter__SWIG_1(long jarg1, OcrCharVariant jarg1_, String jarg2);
  public final static native double OcrCharVariant_GetConfidence(long jarg1, OcrCharVariant jarg1_);
  public final static native void OcrCharVariant_SetConfidence(long jarg1, OcrCharVariant jarg1_, double jarg2);
  public final static native void OcrCharVariant_Serialize(long jarg1, OcrCharVariant jarg1_, long jarg2, Serializer jarg2_);
  public final static native long new_OcrChar__SWIG_0();
  public final static native long new_OcrChar__SWIG_1(long jarg1, OcrCharVariant jarg1_, int jarg2, boolean jarg3, long jarg4, Quadrangle jarg4_);
  public final static native long new_OcrChar__SWIG_2(long jarg1, OcrChar jarg1_);
  public final static native void delete_OcrChar(long jarg1);
  public final static native int OcrChar_GetVariantsCount(long jarg1, OcrChar jarg1_);
  public final static native long OcrChar_GetVariants(long jarg1, OcrChar jarg1_);
  public final static native long OcrChar_GetVariant(long jarg1, OcrChar jarg1_, int jarg2);
  public final static native long OcrChar_GetMutableVariant(long jarg1, OcrChar jarg1_, int jarg2);
  public final static native void OcrChar_SetVariant(long jarg1, OcrChar jarg1_, int jarg2, long jarg3, OcrCharVariant jarg3_);
  public final static native void OcrChar_Resize(long jarg1, OcrChar jarg1_, int jarg2);
  public final static native boolean OcrChar_GetIsHighlighted(long jarg1, OcrChar jarg1_);
  public final static native void OcrChar_SetIsHighlighted(long jarg1, OcrChar jarg1_, boolean jarg2);
  public final static native long OcrChar_GetQuadrangle(long jarg1, OcrChar jarg1_);
  public final static native long OcrChar_GetMutableQuadrangle(long jarg1, OcrChar jarg1_);
  public final static native void OcrChar_SetQuadrangle(long jarg1, OcrChar jarg1_, long jarg2, Quadrangle jarg2_);
  public final static native void OcrChar_SortVariants(long jarg1, OcrChar jarg1_);
  public final static native long OcrChar_GetFirstVariant(long jarg1, OcrChar jarg1_);
  public final static native void OcrChar_Serialize(long jarg1, OcrChar jarg1_, long jarg2, Serializer jarg2_);
  public final static native long new_OcrString__SWIG_0();
  public final static native long new_OcrString__SWIG_1(String jarg1);
  public final static native long new_OcrString__SWIG_2(long jarg1, OcrChar jarg1_, int jarg2);
  public final static native long new_OcrString__SWIG_3(long jarg1, OcrString jarg1_);
  public final static native void delete_OcrString(long jarg1);
  public final static native int OcrString_GetCharsCount(long jarg1, OcrString jarg1_);
  public final static native long OcrString_GetChars(long jarg1, OcrString jarg1_);
  public final static native long OcrString_GetChar(long jarg1, OcrString jarg1_, int jarg2);
  public final static native long OcrString_GetMutableChar(long jarg1, OcrString jarg1_, int jarg2);
  public final static native void OcrString_SetChar(long jarg1, OcrString jarg1_, int jarg2, long jarg3, OcrChar jarg3_);
  public final static native void OcrString_AppendChar(long jarg1, OcrString jarg1_, long jarg2, OcrChar jarg2_);
  public final static native void OcrString_AppendString(long jarg1, OcrString jarg1_, long jarg2, OcrString jarg2_);
  public final static native void OcrString_Resize(long jarg1, OcrString jarg1_, int jarg2);
  public final static native void OcrString_SortVariants(long jarg1, OcrString jarg1_);
  public final static native long OcrString_GetFirstString(long jarg1, OcrString jarg1_);
  public final static native void OcrString_Serialize(long jarg1, OcrString jarg1_, long jarg2, Serializer jarg2_);
  public final static native long Image_CreateEmpty();
  public final static native long Image_FromFile__SWIG_0(String jarg1, long jarg2, Size jarg2_);
  public final static native long Image_FromFile__SWIG_1(String jarg1);
  public final static native long Image_FromFileBuffer__SWIG_0(byte[] jarg1, long jarg3, Size jarg3_);
  public final static native long Image_FromFileBuffer__SWIG_1(byte[] jarg1);
  public final static native long Image_FromBuffer(byte[] jarg1, int jarg3, int jarg4, int jarg5, int jarg6);
  public final static native long Image_FromYUVBuffer(byte[] jarg1, int jarg3, int jarg4);
  public final static native long Image_FromBase64Buffer__SWIG_0(String jarg1, long jarg2, Size jarg2_);
  public final static native long Image_FromBase64Buffer__SWIG_1(String jarg1);
  public final static native void delete_Image(long jarg1);
  public final static native long Image_CloneDeep(long jarg1, Image jarg1_);
  public final static native long Image_CloneShallow(long jarg1, Image jarg1_);
  public final static native void Image_Clear(long jarg1, Image jarg1_);
  public final static native int Image_GetRequiredBufferLength(long jarg1, Image jarg1_);
  public final static native int Image_CopyToBuffer(long jarg1, Image jarg1_, byte[] jarg2);
  public final static native void Image_Save(long jarg1, Image jarg1_, String jarg2);
  public final static native int Image_GetRequiredBase64BufferLength(long jarg1, Image jarg1_);
  public final static native int Image_CopyBase64ToBuffer(long jarg1, Image jarg1_, String jarg2, int jarg3);
  public final static native long Image_GetBase64String(long jarg1, Image jarg1_);
  public final static native double Image_EstimateFocusScore__SWIG_0(long jarg1, Image jarg1_, double jarg2);
  public final static native double Image_EstimateFocusScore__SWIG_1(long jarg1, Image jarg1_);
  public final static native void Image_Resize(long jarg1, Image jarg1_, long jarg2, Size jarg2_);
  public final static native long Image_CloneResized(long jarg1, Image jarg1_, long jarg2, Size jarg2_);
  public final static native void Image_Crop__SWIG_0(long jarg1, Image jarg1_, long jarg2, Quadrangle jarg2_);
  public final static native long Image_CloneCropped__SWIG_0(long jarg1, Image jarg1_, long jarg2, Quadrangle jarg2_);
  public final static native void Image_Crop__SWIG_1(long jarg1, Image jarg1_, long jarg2, Quadrangle jarg2_, long jarg3, Size jarg3_);
  public final static native long Image_CloneCropped__SWIG_1(long jarg1, Image jarg1_, long jarg2, Quadrangle jarg2_, long jarg3, Size jarg3_);
  public final static native void Image_Crop__SWIG_2(long jarg1, Image jarg1_, long jarg2, Rectangle jarg2_);
  public final static native long Image_CloneCropped__SWIG_2(long jarg1, Image jarg1_, long jarg2, Rectangle jarg2_);
  public final static native long Image_CloneCroppedShallow(long jarg1, Image jarg1_, long jarg2, Rectangle jarg2_);
  public final static native void Image_Mask__SWIG_0(long jarg1, Image jarg1_, long jarg2, Rectangle jarg2_, int jarg3);
  public final static native void Image_Mask__SWIG_1(long jarg1, Image jarg1_, long jarg2, Rectangle jarg2_);
  public final static native long Image_CloneMasked__SWIG_0(long jarg1, Image jarg1_, long jarg2, Rectangle jarg2_, int jarg3);
  public final static native long Image_CloneMasked__SWIG_1(long jarg1, Image jarg1_, long jarg2, Rectangle jarg2_);
  public final static native void Image_Mask__SWIG_2(long jarg1, Image jarg1_, long jarg2, Quadrangle jarg2_, int jarg3);
  public final static native void Image_Mask__SWIG_3(long jarg1, Image jarg1_, long jarg2, Quadrangle jarg2_);
  public final static native long Image_CloneMasked__SWIG_2(long jarg1, Image jarg1_, long jarg2, Quadrangle jarg2_, int jarg3);
  public final static native long Image_CloneMasked__SWIG_3(long jarg1, Image jarg1_, long jarg2, Quadrangle jarg2_);
  public final static native void Image_FlipVertical(long jarg1, Image jarg1_);
  public final static native long Image_CloneFlippedVertical(long jarg1, Image jarg1_);
  public final static native void Image_FlipHorizontal(long jarg1, Image jarg1_);
  public final static native long Image_CloneFlippedHorizontal(long jarg1, Image jarg1_);
  public final static native void Image_Rotate90(long jarg1, Image jarg1_, int jarg2);
  public final static native long Image_CloneRotated90(long jarg1, Image jarg1_, int jarg2);
  public final static native void Image_AverageChannels(long jarg1, Image jarg1_);
  public final static native long Image_CloneAveragedChannels(long jarg1, Image jarg1_);
  public final static native int Image_GetWidth(long jarg1, Image jarg1_);
  public final static native int Image_GetHeight(long jarg1, Image jarg1_);
  public final static native long Image_GetSize(long jarg1, Image jarg1_);
  public final static native int Image_GetStride(long jarg1, Image jarg1_);
  public final static native int Image_GetChannels(long jarg1, Image jarg1_);
  public final static native boolean Image_IsMemoryOwner(long jarg1, Image jarg1_);
  public final static native void Image_ForceMemoryOwner(long jarg1, Image jarg1_);
  public final static native void Image_Serialize(long jarg1, Image jarg1_, long jarg2, Serializer jarg2_);
  public final static native long new_StringsVectorIterator(long jarg1, StringsVectorIterator jarg1_);
  public final static native void delete_StringsVectorIterator(long jarg1);
  public final static native String StringsVectorIterator_GetValue(long jarg1, StringsVectorIterator jarg1_);
  public final static native boolean StringsVectorIterator_Equals(long jarg1, StringsVectorIterator jarg1_, long jarg2, StringsVectorIterator jarg2_);
  public final static native void StringsVectorIterator_Advance(long jarg1, StringsVectorIterator jarg1_);
  public final static native long new_StringsSetIterator(long jarg1, StringsSetIterator jarg1_);
  public final static native void delete_StringsSetIterator(long jarg1);
  public final static native String StringsSetIterator_GetValue(long jarg1, StringsSetIterator jarg1_);
  public final static native boolean StringsSetIterator_Equals(long jarg1, StringsSetIterator jarg1_, long jarg2, StringsSetIterator jarg2_);
  public final static native void StringsSetIterator_Advance(long jarg1, StringsSetIterator jarg1_);
  public final static native long new_StringsMapIterator(long jarg1, StringsMapIterator jarg1_);
  public final static native void delete_StringsMapIterator(long jarg1);
  public final static native String StringsMapIterator_GetKey(long jarg1, StringsMapIterator jarg1_);
  public final static native String StringsMapIterator_GetValue(long jarg1, StringsMapIterator jarg1_);
  public final static native boolean StringsMapIterator_Equals(long jarg1, StringsMapIterator jarg1_, long jarg2, StringsMapIterator jarg2_);
  public final static native void StringsMapIterator_Advance(long jarg1, StringsMapIterator jarg1_);
  public final static native void delete_StringsSet(long jarg1);
  public final static native int StringsSet_GetStringsCount(long jarg1, StringsSet jarg1_);
  public final static native boolean StringsSet_HasString(long jarg1, StringsSet jarg1_, String jarg2);
  public final static native long StringsSet_StringsBegin(long jarg1, StringsSet jarg1_);
  public final static native long StringsSet_StringsEnd(long jarg1, StringsSet jarg1_);
}

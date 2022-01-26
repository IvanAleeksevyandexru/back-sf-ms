/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.smartengines.id;

import com.smartengines.common.*;

public class IdSessionSettings {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public IdSessionSettings(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(IdSessionSettings obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        jniidengineJNI.delete_IdSessionSettings(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public IdSessionSettings Clone() {
    long cPtr = jniidengineJNI.IdSessionSettings_Clone(swigCPtr, this);
    return (cPtr == 0) ? null : new IdSessionSettings(cPtr, true);
  }

  public int GetOptionsCount() {
    return jniidengineJNI.IdSessionSettings_GetOptionsCount(swigCPtr, this);
  }

  public String GetOption(String option_name) {
    return jniidengineJNI.IdSessionSettings_GetOption(swigCPtr, this, option_name);
  }

  public boolean HasOption(String option_name) {
    return jniidengineJNI.IdSessionSettings_HasOption(swigCPtr, this, option_name);
  }

  public void SetOption(String option_name, String option_value) {
    jniidengineJNI.IdSessionSettings_SetOption(swigCPtr, this, option_name, option_value);
  }

  public void RemoveOption(String option_name) {
    jniidengineJNI.IdSessionSettings_RemoveOption(swigCPtr, this, option_name);
  }

  public StringsMapIterator OptionsBegin() {
    return new StringsMapIterator(jniidengineJNI.IdSessionSettings_OptionsBegin(swigCPtr, this), true);
  }

  public StringsMapIterator OptionsEnd() {
    return new StringsMapIterator(jniidengineJNI.IdSessionSettings_OptionsEnd(swigCPtr, this), true);
  }

  public int GetSupportedModesCount() {
    return jniidengineJNI.IdSessionSettings_GetSupportedModesCount(swigCPtr, this);
  }

  public boolean HasSupportedMode(String mode_name) {
    return jniidengineJNI.IdSessionSettings_HasSupportedMode(swigCPtr, this, mode_name);
  }

  public StringsSetIterator SupportedModesBegin() {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_SupportedModesBegin(swigCPtr, this), true);
  }

  public StringsSetIterator SupportedModesEnd() {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_SupportedModesEnd(swigCPtr, this), true);
  }

  public String GetCurrentMode() {
    return jniidengineJNI.IdSessionSettings_GetCurrentMode(swigCPtr, this);
  }

  public void SetCurrentMode(String mode_name) {
    jniidengineJNI.IdSessionSettings_SetCurrentMode(swigCPtr, this, mode_name);
  }

  public int GetInternalEnginesCount() {
    return jniidengineJNI.IdSessionSettings_GetInternalEnginesCount(swigCPtr, this);
  }

  public boolean HasInternalEngine(String engine_name) {
    return jniidengineJNI.IdSessionSettings_HasInternalEngine(swigCPtr, this, engine_name);
  }

  public StringsSetIterator InternalEngineNamesBegin() {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_InternalEngineNamesBegin(swigCPtr, this), true);
  }

  public StringsSetIterator InternalEngineNamesEnd() {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_InternalEngineNamesEnd(swigCPtr, this), true);
  }

  public int GetSupportedDocumentTypesCount(String engine_name) {
    return jniidengineJNI.IdSessionSettings_GetSupportedDocumentTypesCount(swigCPtr, this, engine_name);
  }

  public boolean HasSupportedDocumentType(String engine_name, String doc_name) {
    return jniidengineJNI.IdSessionSettings_HasSupportedDocumentType(swigCPtr, this, engine_name, doc_name);
  }

  public StringsSetIterator SupportedDocumentTypesBegin(String engine_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_SupportedDocumentTypesBegin(swigCPtr, this, engine_name), true);
  }

  public StringsSetIterator SupportedDocumentTypesEnd(String engine_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_SupportedDocumentTypesEnd(swigCPtr, this, engine_name), true);
  }

  public int GetEnabledDocumentTypesCount() {
    return jniidengineJNI.IdSessionSettings_GetEnabledDocumentTypesCount(swigCPtr, this);
  }

  public boolean HasEnabledDocumentType(String doc_name) {
    return jniidengineJNI.IdSessionSettings_HasEnabledDocumentType(swigCPtr, this, doc_name);
  }

  public StringsSetIterator EnabledDocumentTypesBegin() {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_EnabledDocumentTypesBegin(swigCPtr, this), true);
  }

  public StringsSetIterator EnabledDocumentTypesEnd() {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_EnabledDocumentTypesEnd(swigCPtr, this), true);
  }

  public void AddEnabledDocumentTypes(String doc_type_mask) {
    jniidengineJNI.IdSessionSettings_AddEnabledDocumentTypes(swigCPtr, this, doc_type_mask);
  }

  public void RemoveEnabledDocumentTypes(String doc_type_mask) {
    jniidengineJNI.IdSessionSettings_RemoveEnabledDocumentTypes(swigCPtr, this, doc_type_mask);
  }

  public IdDocumentInfo GetDocumentInfo(String doc_name) {
    return new IdDocumentInfo(jniidengineJNI.IdSessionSettings_GetDocumentInfo(swigCPtr, this, doc_name), false);
  }

  public int GetSupportedFieldsCount(String doc_name) {
    return jniidengineJNI.IdSessionSettings_GetSupportedFieldsCount(swigCPtr, this, doc_name);
  }

  public boolean HasSupportedField(String doc_name, String field_name) {
    return jniidengineJNI.IdSessionSettings_HasSupportedField(swigCPtr, this, doc_name, field_name);
  }

  public StringsSetIterator SupportedFieldsBegin(String doc_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_SupportedFieldsBegin(swigCPtr, this, doc_name), true);
  }

  public StringsSetIterator SupportedFieldsEnd(String doc_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_SupportedFieldsEnd(swigCPtr, this, doc_name), true);
  }

  public IdFieldType GetFieldType(String doc_name, String field_name) {
    return IdFieldType.swigToEnum(jniidengineJNI.IdSessionSettings_GetFieldType(swigCPtr, this, doc_name, field_name));
  }

  public int GetEnabledFieldsCount(String doc_name) {
    return jniidengineJNI.IdSessionSettings_GetEnabledFieldsCount(swigCPtr, this, doc_name);
  }

  public boolean HasEnabledField(String doc_name, String field_name) {
    return jniidengineJNI.IdSessionSettings_HasEnabledField(swigCPtr, this, doc_name, field_name);
  }

  public StringsSetIterator EnabledFieldsBegin(String doc_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_EnabledFieldsBegin(swigCPtr, this, doc_name), true);
  }

  public StringsSetIterator EnabledFieldsEnd(String doc_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_EnabledFieldsEnd(swigCPtr, this, doc_name), true);
  }

  public void EnableField(String doc_name, String field_name) {
    jniidengineJNI.IdSessionSettings_EnableField(swigCPtr, this, doc_name, field_name);
  }

  public void DisableField(String doc_name, String field_name) {
    jniidengineJNI.IdSessionSettings_DisableField(swigCPtr, this, doc_name, field_name);
  }

  public boolean IsForensicsEnabled() {
    return jniidengineJNI.IdSessionSettings_IsForensicsEnabled(swigCPtr, this);
  }

  public void EnableForensics() {
    jniidengineJNI.IdSessionSettings_EnableForensics(swigCPtr, this);
  }

  public void DisableForensics() {
    jniidengineJNI.IdSessionSettings_DisableForensics(swigCPtr, this);
  }

  public int GetSupportedForensicFieldsCount(String doc_name) {
    return jniidengineJNI.IdSessionSettings_GetSupportedForensicFieldsCount(swigCPtr, this, doc_name);
  }

  public boolean HasSupportedForensicField(String doc_name, String field_name) {
    return jniidengineJNI.IdSessionSettings_HasSupportedForensicField(swigCPtr, this, doc_name, field_name);
  }

  public StringsSetIterator SupportedForensicFieldsBegin(String doc_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_SupportedForensicFieldsBegin(swigCPtr, this, doc_name), true);
  }

  public StringsSetIterator SupportedForensicFieldsEnd(String doc_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_SupportedForensicFieldsEnd(swigCPtr, this, doc_name), true);
  }

  public IdFieldType GetForensicFieldType(String doc_name, String field_name) {
    return IdFieldType.swigToEnum(jniidengineJNI.IdSessionSettings_GetForensicFieldType(swigCPtr, this, doc_name, field_name));
  }

  public int GetEnabledForensicFieldsCount(String doc_name) {
    return jniidengineJNI.IdSessionSettings_GetEnabledForensicFieldsCount(swigCPtr, this, doc_name);
  }

  public boolean HasEnabledForensicField(String doc_name, String field_name) {
    return jniidengineJNI.IdSessionSettings_HasEnabledForensicField(swigCPtr, this, doc_name, field_name);
  }

  public StringsSetIterator EnabledForensicFieldsBegin(String doc_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_EnabledForensicFieldsBegin(swigCPtr, this, doc_name), true);
  }

  public StringsSetIterator EnabledForensicFieldsEnd(String doc_name) {
    return new StringsSetIterator(jniidengineJNI.IdSessionSettings_EnabledForensicFieldsEnd(swigCPtr, this, doc_name), true);
  }

  public void EnableForensicField(String doc_name, String field_name) {
    jniidengineJNI.IdSessionSettings_EnableForensicField(swigCPtr, this, doc_name, field_name);
  }

  public void DisableForensicField(String doc_name, String field_name) {
    jniidengineJNI.IdSessionSettings_DisableForensicField(swigCPtr, this, doc_name, field_name);
  }

}
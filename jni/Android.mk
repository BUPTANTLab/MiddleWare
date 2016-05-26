LOCAL_PATH := $(call my-dir)
  
include $(CLEAR_VARS) 
  
LOCAL_MODULE    := udpSend
LOCAL_SRC_FILES := com_ANT_MiddleWare_jni_udpSend.cpp
LOCAL_LDLIBS:=  -L$(SYSROOT)/usr/lib -llog
  
include $(BUILD_SHARED_LIBRARY)
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS    := -lm -llog

LOCAL_MODULE := scan
LOCAL_SRC_FILES := scaner.c

include $(BUILD_SHARED_LIBRARY)
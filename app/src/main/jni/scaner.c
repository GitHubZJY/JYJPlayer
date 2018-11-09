#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <dirent.h>
#include <sys/stat.h>
#include <sys/statfs.h>
#include <sys/types.h>
#include <fcntl.h> // for open
#include <unistd.h> // for close

#include <assert.h>

#define  LOG_TAG    "JniScan"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  ARRAY_LENGTH 100
#define FILTER_NOMEDIA 0x1
#define FILTER_HIDDEN 0x2
#define FILTER_WITHOUT_RECURSIVE 0x4;
#define FILTER_FLAG_ADS_CACHE 0x8;


int is_exclude_dir(const char *name);
int is_video_file(const char *name);
void check_file(JNIEnv *env, jobject obj);
int check_extend_name(const char* ori, const char* extend);
int check_contain(const char *ori, const char *extend);
void scan_dir(JNIEnv *env, jobject obj, jobjectArray dirs);
void notify_file_found(JNIEnv *env, jobject obj);
void analyze_filter(long filter);
char * switch_lowercase(char *str);

// 实现链表的机构体,  包含多个char*指针, 数据和指向下一个链地址的指针, 用于实现文件夹广度遍历(相对递归的深度遍历, 这里使用的是循环和链表, 函数进出栈的操作没那么多, 速度快点, 后期还可以扩展成扫描到N个文件就回调一次或者一个文件夹回调一次的形式)
// 估计没什么人看这里面写得什么鬼了,  有问题找我 panguowei@gomo.com
struct LNode
{
   char* path;
//   char* name;
   char* parent;
   long last_modify;
   long create;
   long file_size;
   struct LNode* next;
};

// 用于实现文件夹广度遍历的两个结构体指针,  分别指向当前要遍历的链表的表头和表尾
struct LNode* folderFirst;
struct LNode* folderLast;

// 用于实现返回到java层的视频文件结果的链表的结构体指针, 分别指向视频文件链表的表头和表尾
struct LNode* fileFirst;
struct LNode* fileLast;

// 标记扫描到的视频文件数目
int file_count;

// jni回调java层的方法和类名
jmethodID method_id_on_single_folder_found;
jmethodID method_id_on_folder_found;
jclass video_info_clazz;
jmethodID method_id_video_info_init;
jmethodID method_id_video_info_set_path;
//jmethodID method_id_video_info_set_name;
jmethodID method_id_video_info_set_last_modify;
jmethodID method_id_video_info_set_create_time;
jmethodID method_id_video_info_set_size;
jmethodID method_id_video_info_set_parent;

// scan params,  主要是用于标记是否扫描隐藏文件夹和带有.nomedia的目录
int filter_hidden;
int filter_nomedia;
int filter_without_recursive;
int filter_flag_ads_cache;

// temp & shared
int i;
/*
 * Class:     com_gomo_quickvideo_filescan_JniUtils
 * Method:    getDocumentFiles
 * Signature: (Ljava/lang/String;[Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT void JNICALL Java_com_gomo_quickvideo_filescan_JniUtils_getDocumentFiles
  (JNIEnv *env, jobject obj, jobjectArray jdirs, jlong filter)
 {

    // const char *path = (*env) -> GetStringUTFChars(env, jdirPath, NULL);
	LOGE("begin to call scan_dir() in the JNI");
    analyze_filter(filter);
	scan_dir(env, obj, jdirs);
}

void analyze_filter(long filter) {
	LOGE("analyze_filter filter= %d \n", filter);
    filter_nomedia = filter & FILTER_NOMEDIA;
    filter_hidden = filter & FILTER_HIDDEN;
    filter_without_recursive = filter & FILTER_WITHOUT_RECURSIVE;
    filter_flag_ads_cache = filter & FILTER_FLAG_ADS_CACHE;

	LOGE("analyze_filter  filter_nomedia=%d \n", filter_nomedia);
	LOGE("analyze_filter  filter_hidden=%d \n", filter_hidden);
	LOGE("analyze_filter  filter_without_recursive=%d \n", filter_without_recursive);
	LOGE("analyze_filter  filter_flag_ads_cache=%d \n", filter_flag_ads_cache);
}


void scan_dir(JNIEnv *env, jobject obj, jobjectArray dirs) {
    folderFirst = (struct LNode*) malloc(sizeof(struct LNode)); //创建一个结构体含有N个数据
    folderFirst->path = NULL;
    folderFirst->next = NULL;
    folderLast = folderFirst;

    fileFirst = (struct LNode*) malloc(sizeof(struct LNode)); //创建一个结构体含有N个数据
    fileLast = fileFirst;
    fileFirst->path = NULL;
    fileFirst->next = NULL;
    file_count = 0;

    int length_dirs = (*env)->GetArrayLength(env, dirs) - 1;

    while (length_dirs >= 0) {
        jstring str = (*env)->GetObjectArrayElement(env, dirs, length_dirs);
        char* directory =  (char*)(*env)->GetStringUTFChars(env, str, NULL);

        if (folderLast->path != NULL) {
            folderLast->next = (struct LNode*) malloc(sizeof(struct LNode));
            folderLast = folderLast->next;
            folderLast->path = NULL;
            folderLast->next = NULL;
        }

        folderLast->path = (char*) malloc(sizeof(char) * strlen(directory) + 1);
        strcpy(folderLast->path, directory);
        (*env)->ReleaseStringUTFChars(env, str, directory);
        length_dirs--;
    }

    struct LNode* temp = folderFirst;
    while (temp != NULL) {
       LOGE("jni scan path list in: %s", temp->path);
       temp = temp->next;
    }
    temp = NULL;

//    folderFirst->path = directory;
 //   folderFirst->next = NULL;

    method_id_on_folder_found = 0;
    method_id_on_single_folder_found = 0;
    video_info_clazz = 0;
    method_id_video_info_init = 0;
    method_id_video_info_set_path = 0;
  //  method_id_video_info_set_name = 0;
    method_id_video_info_set_last_modify = 0;
    method_id_video_info_set_create_time = 0;
    method_id_video_info_set_size = 0;
    method_id_video_info_set_parent = 0;

    check_file(env, obj);
}



void check_file(JNIEnv *env, jobject obj)
{
    DIR *dp;
    struct dirent *entry;
    struct stat *statbuf = (struct stat*) malloc(sizeof(struct stat));
    // 获取文件信息大小用到的statbuf
    while (folderFirst != NULL)
    {

           int scan = -1;
           // 先看有没有读取的权限, 没有的话, 直接就scan = 0, 后面不扫描了
           if(access(folderFirst->path, 4) != 0) {
                scan = 0;
           } else if (filter_nomedia == 1)
           {
                char* nomedia_path = (char*) malloc(sizeof(char) * (strlen(folderFirst->path) + strlen("/.nomedia") + 1));
                strcpy(nomedia_path, folderFirst->path);
                strcat(nomedia_path, "/.nomedia");
                // 如果access返回0, 说明这个.nomedia文件存在, 就不要扫描这个文件夹
                // 如果access返回-1, 说明这个.nomedia文件不存在, 可以扫描这个文件夹
                scan = access(nomedia_path, 0);
                if (scan == 0)
                {
                  LOGE("avoid .nomedia folder=  %s", nomedia_path);
                }
                free(nomedia_path);
                nomedia_path = NULL;
           }
       // chdir(folderFirst->path);
       // LOGE("openDir pop:  %s \n", folderFirst->path);
       if(scan < 0 && (dp = opendir(folderFirst->path)) != NULL)
           {
           // 扫描目录本身
           while ((entry = readdir(dp)) != NULL)
           {
                // LOGE("check dir:  %s \n", entry->d_name);
           		if (entry->d_type == DT_DIR) {
           		     // 不等于"." & 不等于  ".." &  [扫描隐藏文件夹 || 以 "." 开头] & 要递归扫描 & [不过滤广告缓存文件夹 || 不是广告文件夹]
           			if ((strcmp(entry->d_name, ".") != 0)
           					&& (strcmp(entry->d_name, "..") != 0)
           					&& (filter_hidden || entry->d_name[0] != '.') && !filter_without_recursive && (!filter_flag_ads_cache || !is_exclude_dir(entry->d_name))) {
                           struct LNode* folder = (struct LNode*) malloc(sizeof(struct LNode));
                           folder->path = (char*) malloc(sizeof(char) * (strlen(entry->d_name) + strlen(folderFirst->path) + 2));
                           folder->next = NULL;
                           strcpy(folder->path, folderFirst->path);
                           strcat(folder->path, "/");
                           strcat(folder->path, entry->d_name);
                           folderLast->next = folder;
                           folderLast = folder;
                           folder = NULL;
                           // LOGE("child push:  %s \n", folderLast->path);
           			}
           		} else
           	    {
           	          // LOGE("child file:  %s \n", entry->d_name);
                       if (is_video_file(entry->d_name)) {
                           //LOGE("VIDEO file: start %s \n", entry->d_name);

                           struct LNode* file = (struct LNode*) malloc(sizeof(struct LNode));
                           file->parent = (char*) malloc(sizeof(char) * (strlen(folderFirst->path) + 1));
                           file->path = (char*) malloc(sizeof(char) * (strlen(entry->d_name) + strlen(folderFirst->path) + 2));
                           //file->name = (char*) malloc(sizeof(char) * (strlen(entry->d_name) + 1));
                           file->next = NULL;
                           strcpy(file->parent, folderFirst->path);
                           //strcpy(file->name, entry->d_name);
                           strcpy(file->path, folderFirst->path);
                           strcat(file->path, "/");
                           strcat(file->path, entry->d_name);
                           //struct stat *statbuf;
                         //  LOGE("VIDEO file: stat  %s \n", file->path);
                           int res = stat(file->path, statbuf);
                         //  LOGE("VIDEO file: stat end res=%d ", res);
                         //  LOGE("VIDEO file:  size= %d", statbuf);
                         //  LOGE("VIDEO file:  size= %d", statbuf->st_mtime);
                           file->file_size = statbuf->st_size;
                         //   LOGE("VIDEO file:  size finish");
                         //  LOGE("VIDEO file:  size= %d \n", file->file_size);
                           //LOGE("VIDEO file:  setFileSized %s \n", file->path);
                           file->last_modify = statbuf->st_mtime;
                           file->create = statbuf->st_ctime;
                          // LOGE("VIDEO file:  lastModify= %d \n", file->last_modify);
                           fileLast->next = file;
                           fileLast = file;
                           file_count++;
                           file = NULL;
                          // LOGE("VIDEO file: end  %s \n", fileLast->path);
                           // notify_file_found(env, obj);
                       }
           	    }
           }
           closedir(dp);
       }
       struct LNode* nextNode = folderFirst->next;
       free(folderFirst);
       folderFirst = nextNode;
       nextNode = NULL;
    }
    free(statbuf);
    statbuf = NULL;
    notify_file_found(env, obj);

   //LOGE("check P8");
    // 释放资源
 //   if (method_id_on_folder_found != 0) {
   //     (*env)->DeleteLocalRef(env, method_id_on_folder_found);
        method_id_on_folder_found = 0;
   // }
 //   if (method_id_on_single_folder_found != 0) {
   //     (*env)->DeleteLocalRef(env, method_id_on_single_folder_found);
        method_id_on_single_folder_found = 0;
   // }

    //if (video_info_clazz != 0) {
     //   (*env)->DeleteLocalRef(env, video_info_clazz);
        video_info_clazz = 0;
    //}
    //if (method_id_video_info_init != 0) {
     //   (*env)->DeleteLocalRef(env, method_id_video_info_init);
        method_id_video_info_init = 0;
    //}

    // LOGE("check P9");

    //if ( method_id_video_info_set_path != 0) {
    //    (*env)->DeleteLocalRef(env, method_id_video_info_set_path);
        method_id_video_info_set_path = 0;
    //}

    //if (method_id_video_info_set_name != 0) {
     //   (*env)->DeleteLocalRef(env, method_id_video_info_set_name);
    //    method_id_video_info_set_name = 0;
    //}

    //if (method_id_video_info_set_last_modify != 0) {
     //   (*env)->DeleteLocalRef(env, method_id_video_info_set_last_modify);
         method_id_video_info_set_last_modify = 0;

         method_id_video_info_set_create_time = 0;
    //}

    //if (method_id_video_info_set_size != 0) {
     //   (*env)->DeleteLocalRef(env, method_id_video_info_set_size);
         method_id_video_info_set_size = 0;
    //}

    //if (method_id_video_info_set_parent != 0) {
     //   (*env)->DeleteLocalRef(env, method_id_video_info_set_parent);
         method_id_video_info_set_parent = 0;
    //}
}

void notify_file_found(JNIEnv *env, jobject obj)
{

    LOGE("file_count= %d", file_count);
    if (method_id_on_folder_found == 0) {
        //在C语言中调用Java的空方法
        //1.找到java代码native方法所在的字节码文件
        //jclass (*FindClass)(JNIEnv*, const char*);
        jclass clazz = (*env)->FindClass(env, "com/gomo/quickvideo/filescan/JniUtils");
        if(clazz != 0){
       //     LOGE("find class error");
        //} else {
         // LOGE("find class");
          //2.找到class里面对应的方法
          // jmethodID (*GetMethodID)(JNIEnv*, jclass, const char*, const char*);
          // 填入方法签名,  对应 JniUtils#onFileScaned(String[])  void方法,  可以用javap -s xxxxx.class打印签名
          method_id_on_folder_found = (*env)->GetMethodID(env,clazz,"onFileScaned","([Lcom/gomo/quickvideo/filescan/model/bean/VideoInfo;)V");
          //if(method_id_on_folder_found == 0){
          //   LOGE("find method_id_on_folder_found error");
          //} else {
          //   LOGE("find method_id_on_folder_found");
          //}
        }
    }
    if (method_id_on_single_folder_found == 0) {
        //在C语言中调用Java的空方法
        //1.找到java代码native方法所在的字节码文件
        //jclass (*FindClass)(JNIEnv*, const char*);
        jclass clazz = (*env)->FindClass(env, "com/gomo/quickvideo/filescan/JniUtils");
        if(clazz != 0){
       //     LOGE("find class error");
        //} else {
         // LOGE("find class");
          //2.找到class里面对应的方法
          // jmethodID (*GetMethodID)(JNIEnv*, jclass, const char*, const char*);
          // 填入方法签名,  对应 JniUtils#onFileScaned(String[])  void方法,  可以用javap -s xxxxx.class打印签名
          method_id_on_single_folder_found = (*env)->GetMethodID(env,clazz,"onFolderScaned","([Lcom/gomo/quickvideo/filescan/model/bean/VideoInfo;)V");
          //if(method_id_on_single_folder_found == 0){
          //   LOGE("find method_id_on_single_folder_found error");
          //} else {
          //   LOGE("find method_id_on_single_folder_found");
          //}
        }
    }

    if (method_id_on_folder_found != 0 && method_id_on_single_folder_found != 0) {
            //3.调用方法
           if (video_info_clazz == 0)
           {
              video_info_clazz = (*env)->FindClass(env, "com/gomo/quickvideo/filescan/model/bean/VideoInfo");
           }

         //  if (video_info_clazz == 0) {
          //       LOGE("video_info_clazz error ");
          // } else {
           //      LOGE("video_info_clazz success ");
          // }

           if (method_id_video_info_init == 0) {
              method_id_video_info_init = (*env)->GetMethodID(env,video_info_clazz,"<init>","()V");
           }

          // if (method_id_video_info_init == 0) {
            //                LOGE("method_id_video_info_init error ");
              //        } else {
               //             LOGE("method_id_video_info_init success ");
                //      }

           //if (method_id_video_info_set_path == 0) {
              method_id_video_info_set_path = (*env)->GetMethodID(env,video_info_clazz,"setPath","(Ljava/lang/String;)V");
          // }

          // if (method_id_video_info_set_last_modify == 0) {
              method_id_video_info_set_last_modify = (*env)->GetMethodID(env,video_info_clazz,"setLastModify","(J)V");
           //}

           //if (method_id_video_info_set_create_time == 0) {
              method_id_video_info_set_create_time = (*env)->GetMethodID(env,video_info_clazz,"setCreateTime","(J)V");
           //}

           //if (method_id_video_info_set_name == 0) {
      //         method_id_video_info_set_name = (*env)->GetMethodID(env,video_info_clazz,"setName","(Ljava/lang/String;)V");
           //}

           //if (method_id_video_info_set_size == 0) {
                method_id_video_info_set_size = (*env)->GetMethodID(env,video_info_clazz,"setSize","(J)V");
           //}

           //if (method_id_video_info_set_parent == 0) {
                method_id_video_info_set_parent = (*env)->GetMethodID(env,video_info_clazz,"setParentFileName","(Ljava/lang/String;)V");
           //}

          //  LOGE("init all method");
           //if (video_info_clazz == 0)
           //{
          // LOGE("FindClass error ");

           //} else {
           //LOGE("FindClass good ");

          // }

            if (file_count == 0)
            {
                    if (filter_without_recursive)
                    {
                        (*env)->CallVoidMethod(env, obj, method_id_on_single_folder_found, NULL);
                        return;
                    } else
                    {
                        (*env)->CallVoidMethod(env, obj, method_id_on_folder_found, NULL);
                        return;
                    }
            }
           jobjectArray args = (*env)->NewObjectArray(env, file_count, video_info_clazz, 0);

            //if (args == 0)
           //{
           //LOGE("NewObjectArray error ");

           //} else {
           //LOGE("NewStringUTF str= %s", fileFirst->path);
           // int tempLength = (*env)->GetArrayLength(env, args);
           //LOGE("NewObjectArray good length= %d", tempLength);

           //}
//           LOGE("check P1");

           i = 0;
           struct LNode* tempHead = fileFirst->next;
           struct LNode* wrapHead = NULL;
           fileFirst->next = NULL;
           jobject video_info = NULL;
           jstring setparent = NULL;
           jstring setpath = NULL;
          // jstring setname = NULL;
           jlong modify = 0;
           jlong create = 0;
           jlong size = 0;
           while (tempHead != NULL)
           {
        //LOGE("check P2");

                 //if (tempHead->path == NULL)
                   //             {
                     //           LOGE("tempHead path=  NULL");

                       //         } else
                         //       {

                           //     LOGE("tempHead path= %s", tempHead->path);
                             //   }
                // LOGE("call fill method");
                 video_info = (*env)->NewObject(env, video_info_clazz, method_id_video_info_init);
                //LOGE("call fill method finished 0");
                 setparent = (*env)->NewStringUTF(env, tempHead->parent);
                (*env)->CallVoidMethod(env, video_info, method_id_video_info_set_parent, setparent);
                 setpath = (*env)->NewStringUTF(env, tempHead->path);
                (*env)->CallVoidMethod(env, video_info, method_id_video_info_set_path, setpath);
                //LOGE("call fill method finished 1");
               //  setname = (*env)->NewStringUTF(env, tempHead->name);
              //  (*env)->CallVoidMethod(env, video_info, method_id_video_info_set_name, setname);
                //LOGE("call fill method finished 2");
                 modify = tempHead->last_modify;
                 modify = modify * 1000;
                (*env)->CallVoidMethod(env, video_info, method_id_video_info_set_last_modify, modify);
                 create = tempHead->create;
                 create = create * 1000;
                 (*env)->CallVoidMethod(env, video_info, method_id_video_info_set_create_time, create);
                 size = tempHead->file_size;
                (*env)->CallVoidMethod(env, video_info, method_id_video_info_set_size, size);
              //  LOGE("check P4");
            //    LOGE("call fill method parent %s", tempHead->parent);
           //     LOGE("call fill method path %s", tempHead->path);
            //    LOGE("call fill method name %s", tempHead->name);
            //    jstring str = (*env)->NewStringUTF(env, tempHead->path);
         //       if (str == 0) {
           //         LOGE("NewStringUTF error");
             //   } else {
               //     LOGE("NewStringUTF good");
                //}
                // LOGE("SetObjectArrayElement str= %d ", i);

                (*env)->SetObjectArrayElement(env, args, i++, video_info);

       //             LOGE("SetObjectArrayElement good");
           //     LOGE("check P5");
                (*env)->DeleteLocalRef(env, video_info);
                video_info = NULL;
                (*env)->ReleaseStringUTFChars(env, setparent, tempHead->parent);
                (*env)->DeleteLocalRef(env, setparent);
                setparent = NULL;
                (*env)->ReleaseStringUTFChars(env, setpath, tempHead->path);
                (*env)->DeleteLocalRef(env, setpath);
                setpath = NULL;
              //  (*env)->ReleaseStringUTFChars(env, setname, tempHead->name);
              //  (*env)->DeleteLocalRef(env, setname);
              //  setname = NULL;
                modify = NULL;
                size = NULL;

                wrapHead = tempHead;
                tempHead = tempHead->next;
               // free(wrapHead->path);
                wrapHead->path = NULL;
               // free(wrapHead->name);
               // wrapHead->name = NULL;
               // free(wrapHead->parent);
                wrapHead->parent = NULL;
                wrapHead->next = NULL;
               // free(wrapHead);
                wrapHead = NULL;
           }


           tempHead = NULL;
     //               LOGE("try execute call void method");
    //  void (*CallVoidMethod)(JNIEnv*, jobject, jmethodID, ...);

           if (filter_without_recursive)
           {
               (*env)->CallVoidMethod(env, obj, method_id_on_single_folder_found, args);
   // LOGE("method_id_on_single_folder_found called");
           } else
           {
               (*env)->CallVoidMethod(env, obj, method_id_on_folder_found, args);
   // LOGE("method_id_on_folder_found called");
           }
    }


            //free(fileFirst->path);
            fileFirst->next = NULL;
            fileLast = fileFirst;
      //      LOGE("check P7");
}

int is_video_file(const char *name)
{
// sdk不支持播放的类型：v8,tts,m3u8,asx,h264
    return
                                       check_extend_name(name, ".3g2")
                               		|| check_extend_name(name, ".3gp")
                               		|| check_extend_name(name, ".3gp2")
                               		|| check_extend_name(name, ".3gpp")
                               		|| check_extend_name(name, ".4k")
                               		|| check_extend_name(name, ".amv")
                               		|| check_extend_name(name, ".asf")
                               		|| check_extend_name(name, ".avi")
                               		|| check_extend_name(name, ".divx")
                               		|| check_extend_name(name, ".dv")
                               		|| check_extend_name(name, ".dvavi")
                               		|| check_extend_name(name, ".f4v")
                               		|| check_extend_name(name, ".flv")
                               		|| check_extend_name(name, ".gvi")
                               		|| check_extend_name(name, ".gxf")
                               		|| check_extend_name(name, ".iso")
                               		|| check_extend_name(name, ".m1v")
                               		|| check_extend_name(name, ".m2t")
                               		|| check_extend_name(name, ".m2ts")
                               		|| check_extend_name(name, ".m2v")
                               		|| check_extend_name(name, ".m4v")
                               		|| check_extend_name(name, ".mats")
                               		|| check_extend_name(name, ".mkv")
                               		|| check_extend_name(name, ".mov")
                               		|| check_extend_name(name, ".mp2")
                               		|| check_extend_name(name, ".mp2v")
                               		|| check_extend_name(name, ".mp4")
                               		|| check_extend_name(name, ".mp4v")
                               		|| check_extend_name(name, ".mpe")
                               		|| check_extend_name(name, ".mpeg")
                               		|| check_extend_name(name, ".mpeg1")
                               		|| check_extend_name(name, ".mpeg2")
                               		|| check_extend_name(name, ".mpeg4")
                               		|| check_extend_name(name, ".mpg")
                               		|| check_extend_name(name, ".mpv2")
                               		|| check_extend_name(name, ".mts")
                               		|| check_extend_name(name, ".mtv")
                               		|| check_extend_name(name, ".mxf")
                               		|| check_extend_name(name, ".mxg")
                               		|| check_extend_name(name, ".navi")
                               		|| check_extend_name(name, ".ndivx")
                               		|| check_extend_name(name, ".nsv")
                               		|| check_extend_name(name, ".nuv")
                               		|| check_extend_name(name, ".ogm")
                               		|| check_extend_name(name, ".ogx")
                               		|| check_extend_name(name, ".ps")
                               		|| check_extend_name(name, ".ra")
                               		|| check_extend_name(name, ".ram")
                               		|| check_extend_name(name, ".rec")
                               		|| check_extend_name(name, ".rm")
                               		|| check_extend_name(name, ".rmvb")
                               		|| check_extend_name(name, ".vdat")
                               		|| check_extend_name(name, ".vob")
                               		|| check_extend_name(name, ".vro")
                               		|| check_extend_name(name, ".tak")
                               		|| check_extend_name(name, ".tod")
                               		|| check_extend_name(name, ".ts")
                               		|| check_extend_name(name, ".webm")
                               		|| check_extend_name(name, ".wm")
                               		|| check_extend_name(name, ".wmv")
                               		|| check_extend_name(name, ".wtv")
                               		|| check_extend_name(name, ".xesc")
                               		|| check_extend_name(name, ".xvid");

}


int is_exclude_dir(const char *name)
{
// 广告缓存列表 & 其他排除列表
    return
    (strcmp(name, "Camera") == 0)
    // other
    || check_contain(name, "cache")
    || check_contain(name, "temp")
    || check_contain(name, "wandoujia")
    // 社交app同名文件夹
    || check_contain(name, "musically")
    || check_contain(name, "facebook")
    || check_contain(name, "instagram")
    || check_contain(name, "twitter")
    || check_contain(name, "vine")
    || check_contain(name, "youtube")
    || check_contain(name, "snapchat")
    || check_contain(name, "wechat")
    || check_contain(name, "tencent")
    || check_contain(name, "messager")
    || check_contain(name, "whatsapp");

}

int check_contain(const char *ori, const char *extend) {

    if (strstr(switch_lowercase(ori), extend)) {
        return 1;
    }

    return 0;
}

char * switch_lowercase(char *str) {
    assert(str);
    char *ret = str;
    while (*str != '\0') {
        if (*str >= 'A' && *str <= 'Z') {
            *str = *str + 'a' - 'A';
            str++;
        } else
            str++;
        }
    return ret;             //返回该字符串数组的首地址
}


int check_extend_name(const char* ori, const char* extend) {
    int lext = strlen(extend);
    int lori = strlen(ori);
    if (lext > lori)
    {
        return 0;
    }

    for ( i = 0; i < lext; i++) {
        char c1 = extend[i];
        char c2 = ori[lori - lext + i];
        if (c1 >= 'A' && c1 <= 'Z')
                {
                    c1 += 'a' - 'A';
                }
                if (c2 >= 'A' && c2 <= 'Z')
                {
                    c2 += 'a' - 'A';
                }

        if (c1 != c2) {
            return 0;
        }
    }
    return 1;
}
#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> /* netbd.h is needed for struct hostent =) */
#include <errno.h>
#include "com_ANT_MiddleWare_jni_udpSend.h"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "udpSend jni", __VA_ARGS__)

int sockfd;
int port;
struct hostent *he; /* structure that will get information about remote host */
struct sockaddr_in server; /* server's address information */
socklen_t len;

JNIEXPORT void JNICALL Java_com_ANT_MiddleWare_jni_udpSend_init
(JNIEnv *, jobject, jint p)
{
	port = p;

	if ((he = gethostbyname("192.168.1.255")) == NULL)
	{
		LOGE("gethostbyname() error\n");
		return;
	}

	if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) == -1)
	{
		LOGE("socket() error\n");
		return;
	}
	bzero(&server, sizeof(server));
	server.sin_family = AF_INET;
	server.sin_port = htons(port);
	server.sin_addr = *((struct in_addr *) he->h_addr);

	len = sizeof(struct sockaddr_in);
	LOGE("init %d", port);
}

JNIEXPORT jint JNICALL Java_com_ANT_MiddleWare_jni_udpSend_send(JNIEnv * env,
jobject, jbyteArray strIn)
{
	char* data = (char*) env->GetByteArrayElements(strIn, 0);
	int s = sendto(sockfd, data, strlen(data), 0, (struct sockaddr *) &server, len);
	if(s<=0){
		LOGE("errno %d", errno);
	}else{
		LOGE("s %d", s);
	}
	return s;
}

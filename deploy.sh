#!/bin/bash
BUILD_JAR=$(ls /home/ec2-user/jenkins/build/libs/*.jar) # jar 위치
JAR_NAME=$(basename $BUILD_JAR)
echo "> build 파일명: $JAR_NAME" >> /home/ec2-user/deploy.log

echo "> build 파일 복사" >> /home/ec2-user/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케기션이 없으므로 종료하지 않습니다." >> /home/ec2-user/deploy.log
else
 echo "> kill -15 $CURRENT_PID"
 kill -15 $CURRENT_PID
 sleep 5
fi

DEPLOY_JAR=$DEPOY_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포"    >> /home/ec2-user/deploy.log
nohup java -jar $DEPLOY_JAR >> /home/ec2-user/deploy.log 2>/home/ec2-user/deploy_err.log &
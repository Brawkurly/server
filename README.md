# server
Moneytoring프로젝트의 server repository입니다. </br>
# Getting Started
### Prerequisites
- java > 11
- springframework boot > 2.7.3
### build
스프링부트 gradle 빌드
```c
$ gradlew build
```

### docker
도커 설치
```c
# 패키지 업데이트
$ sudo apt-get update

# 필요 패키지 설치
$ sudo apt-get install apt-transport-https ca-certifacates curl gnupg-agent software-properties-commo
    
# Docker의 Official GPG Key를 등록
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# stable repository를 등록
$ echo \
  "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 도커 설치
$ sudo apt-get update && sudo apt-get install docker-ce docker-ce-cli containerd.io
```

도커 compose 설치
```c
# 도커 컴포즈 설치
$ sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 실행 권한 부여
$ sudo chmod +x /usr/local/bin/docker-compose
```

### run
배포 실행
```c
# spring-boot 도커와 nginx 도커 안에 각각 jar 파일과 build 폴더를 넣기

# 실행
$ sudo docker-compose up --build
```

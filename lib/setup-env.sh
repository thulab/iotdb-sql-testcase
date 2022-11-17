#!/bin/bash

CURRENT_PATH=$(cd $(dirname $0);pwd)
target_path=/data/nginx
log_path=${CURRENT_PATH}/env-setup.log

function log() {
    echo -e "\033[32m $1 \033[0m" || true
    echo $1 >> ${log_path} || true
}
function warn() {
    echo -e "\033[33m $1 \033[0m" || true
    echo $1 >> ${log_path} || true
}
function err() {
    echo -e "\033[31m $1 \033[0m" || true
    echo $1 >> ${log_path} || true
}

function print_usage() {
    log "Usage:"
    echo "$(basename $0) -u <user name> [-d target_path] "
    echo "Description:"
    echo "  -u username 指定运行test cases时的user ."
    echo "  -d target_path 可选，指定放置jar的本地路径, 默认值$target_path"
    echo "e.g"
    echo "  $(basename $0) -u atmos"
    echo "  $(basename $0) -u atmos -d /data/nginx"
    exit -1
}

while getopts 'hu:d:' OPT; do
    case $OPT in
        u)
            user="$OPTARG"
            log "user=$user"
            ;;
        d)
            target_path="$OPTARG"
            log "target_path=$target_path"
            ;;
        h)
            print_usage
            ;;
        ?)
            print_usage
            ;;
    esac
done
set -e


# 判断操作系统
function get_system_info(){
    os_type=`grep ^ID= /etc/os-release` || true
    if [[ $os_type =~ "centos" ]]; then
        info=$(cat /etc/redhat-release);
        echo "centos";
        return 0;
    elif [[ $os_type =~ "ubuntu" ]]; then
        echo "ubuntu";
        return 0;
    else
        err "Operation system must be one of CentOS, ubuntu.";
        exit -9;
    fi
}

function install_docker(){
    install_flag=1
    docker -v || install_flag=0
    #echo "install_flag=$install_flag";
    if [ $install_flag == 1 ]; then
        warn "Docker has already existed!"
        return
    else
         log "install docker."
    fi
    sys_version=$(get_system_info)
    echo "sys_version=${sys_version}" || true
    if [ ${sys_version} = "ubuntu" ]; then
        log "install docker";
        cd $CURRENT_PATH/docker/${sys_version};
        dpkg -i *.deb || err "请仔细查看上面的日志，如果有错误，及时修正。";
    elif [ ${sys_version} = "centos" ]; then
        yum install -y *.rpm || err "请仔细查看上面的日志，如果有错误，及时修正。";
        log "安装docker";
        cd $CURRENT_PATH/docker/${sys_version} ;
        yum install -y *.rpm || err "请仔细查看上面的日志，如果有错误，及时修正。";
    else
        err "not supported yet!"
    fi
   cd $CURRENT_PATH
   log "启动docker"
   systemctl start docker
   log "restart docker" ;
   systemctl restart docker ;
   log "设置开机自启动"
   systemctl enable docker
}
function check_user(){
    if [ "$(id -u)" != "0" ]; then
        err '请以root身份运行该程序'
        exit -2
    fi
}
function start_nginx(){
    cd $CURRENT_PATH
    if [[ $(docker inspect nginx-server -f '{{ .State.Status}}')  == "running" ]]; then
        warn "nginx-server is already running."
    else
        log "start nginx-server."
        docker load -i nginx_v1.23.2-alpine.tar
        if [[ ! -d $target_path ]]; then
            mkdir -p $target_path
            chown $user $target_path
            warn "需要按照文档将 jar 放到 $target_path:"
            warn "cp iotdb-sql-testcase/lib/trigger_jar/local/*.jar $target_path"
            warn "cp iotdb-sql-testcase/lib/udf_jar/local/*.jar $target_path"
        fi
        if [[ $target_path != "/data/nginx" ]]; then
            warn "\n需要手动执行下面命令，以修改 trigger/UDF 的 test case: "
            warn "cd iotdb-sql-testcase/scripts/processData"
            warn "sed -i 's#/data/nginx#${target_path}g' trigger/*.run"
            warn "sed -i 's#/data/nginx#${target_path}g' udf/*.run"
        fi
        sleep 5
        export target_path=$target_path && docker-compose -f docker-compose.yml up -d
    fi
    #wget http://127.0.0.1:8085/stateful-test-for-http-0.14-SNAPSHOT.jar
}
function main(){
    check_user
    if [[ -z $user ]]; then
        err "必须指定运行testcase的用户名"
        print_usage
    fi
    if [[ -z ${target_path} ]]; then
        err "必须指定放置jar的本地路径"
        print_usage
    fi
    
    install_docker
    log "为$user添加docker用户组"
    if [[ $(id $user) =~ docker ]]; then
        warn "  无需再次添加"
    else
        usermod -aG docker $user || true
    fi
    docker-compose -v || if [[  $? -ne 0 ]]; then
        log "install docker-compose"
        cp $CURRENT_PATH/docker-compose /usr/bin/docker-compose
        chmod +x /usr/bin/docker-compose
    fi
    start_nginx
    log "succeeded."
}

main

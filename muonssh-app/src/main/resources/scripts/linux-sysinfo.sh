

CMD_PATH=""

export PATH=$PATH:/bin:/sbin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/sbin

sysinfo(){

KERNEL_NAME=`uname -s`
KERNEL_RELEASE=`uname -r`
KERNEL_VERSION=`uname -v`
MACHINE_ARCH=`uname -m`
HARDWARE=`uname -i`
HOST_NAME=`uname -n`
USER=`whoami`
GROUP=`id -gn`

UPTIME=`uptime -s`

if [ -e /etc/os-release ]; then
	cat /etc/os-release|grep PRETTY_NAME=|sed 's/PRETTY_NAME=//g'|sed 's/"//g'
elif [ -e /etc/lsb-release ]; then
	cat /etc/lsb-release|grep PRETTY_NAME=|sed 's/PRETTY_NAME=//g'|sed 's/"//g'
fi

if [ -e /proc/cpuinfo ]; then
	CPU=`cat /proc/cpuinfo|grep 'model name'|head -n 1|sed -E 's/model name\s*\:+\s*//g'`
	PC=`grep processor /proc/cpuinfo | wc -l`
	echo "CPU model               $CPU"
	echo "CPU count               $PC"
fi;

if [ -e /proc/meminfo ];then
	MEM=`cat /proc/meminfo|grep 'MemTotal:'|sed -E 's/MemTotal:\s+//g'`
	SWAP=`cat /proc/meminfo|grep 'SwapTotal:'|sed -E 's/SwapTotal:\s+//g'`
	echo "Total memory            $MEM"
	echo "Total swap              $SWAP"
fi

echo "Kernel name             $KERNEL_NAME"
echo "Kernel release          $KERNEL_RELEASE"
echo "Kerne version           $KERNEL_VERSION"
echo "Architecture            $MACHINE_ARCH $HARDWARE"
echo "Hostname                $HOST_NAME"
echo "System is up since      $UPTIME"
echo "Logged in user          $USER"
echo "User group              $GROUP"

echo " "


#echo "Uptime:"
#uptime
#
#echo " "

#echo "Distro info:"
#if [ -e /etc/lsb-release ]; then
#	cat /etc/lsb-release
#else
#	cat /etc/*release
#fi



echo "Network interfaces:"


ip a|awk '{ if (/^[0-9]+:/){iface=$2} if(/inet /){printf("%s\t\t\t%s\n",substr(iface,1,length(iface)-1),$2)}}'

}

sysinfo
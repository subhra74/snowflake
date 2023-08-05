os=$(uname)
case $os in
	Linux	) ps=$(ps -e -o pid=pid -o pcpu -o rss -o etime -o ppid -o user -o nice -o args -ww --sort pid);;
	HP-UX	) ps=$(export UNIX95=1; ps -e -o pid=pid -o pcpu -o vsz -o etime -o ppid -o user -o nice -o args);;
	FreeBSD	) ps=$(ps -a -x -o pid=pid -o pcpu -o rss -o etime -o ppid -o user -o nice -o args -ww);;
	NetBSD	) ps=$(ps -a -x -o pid=pid -o pcpu -o rss -o etime -o ppid -o user -o nice -o args -ww);;
	OpenBSD	) ps=$(ps -a -x -o pid=pid -o pcpu -o rss -o etime -o ppid -o user -o nice -o args -ww);;
esac
echo "$ps"
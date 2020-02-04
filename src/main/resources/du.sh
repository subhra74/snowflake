du -a "$FOLDER" | while read line ; do
	filename=`echo $line|cut -d' ' -f2`
	if [ -d $filename ]; then
   		echo "d $line"
   	else
   		echo "f $line"
   	fi
done

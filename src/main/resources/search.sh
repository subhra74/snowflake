#Variables required
# 1. $CONTENT			if content search is required, set it to 1
# 2. $UNCOMPRESS		if content search within compressed files is required, set it to 1
# 3. $PATTERN			if content search is required, set it to the search keyword
# 4. $CRITERIA			criteria about the search like -type or -name ( -name should not be set for content search )
#
#
# LOCATION				-- where to search
# NAME					-- name of the file to find
# SIZE					--size argument of search
# MTIME					--mtime
# TYPE					--type of file ( file or folder or both

display_file_info(){
	if [ -d $1 ]; then
		echo "d|$1"
	elif [ -f $1 ]; then
		echo "f|$1"
	elif [ -L $1 ]; then
		echo "l|$1"
	else
		echo "o|$1"
	fi
}

search_in_file(){
	if [ ! -z $UNCOMPRESS ];then
		if gunzip -c "$1" 2>/dev/null|grep -q "$2" ; then
			FILE=$1
		elif unzip -p "$1" 2>/dev/null|grep -q "$2" ; then
			FILE=$1
		elif bzip2 -dc "$1" 2>/dev/null|grep -q "$2" ; then
			FILE=$1
		elif unxz -dc "$1" 2>/dev/null|grep -q "$2" ; then
			FILE=$1
		fi
	fi
	
	if [ -z $FILE ]; then
		if grep -q "$2" "$1" ; then
			FILE=$1
		fi
	fi

	if [ ! -z $FILE ]; then
		display_file_info $FILE
	fi

}

#export -f display_file_info
#export -f search_in_file

if [ -z "$CONTENT" ]; then
	#search only in file name and other attributes
	if [ ! -z "$NAME" ]; then
		find "$LOCATION" -name "$NAME" $CRITERIA  -exec sh -c 'display_file_info(){
																	if [ -d "$1" ]; then
																		echo "d|$1"
																	elif [ -f "$1" ]; then
																		echo "f|$1"
																	elif [ -L "$1" ]; then
																		echo "l|$1"
																	else
																		echo "o|$1"
																	fi
																}
																display_file_info "$0" ' {} \;
	else
		find "$LOCATION" $CRITERIA  -exec sh -c 'display_file_info(){
													if [ -d "$1" ]; then
														echo "d|$1"
													elif [ -f "$1" ]; then
														echo "f|$1"
													elif [ -L "$1" ]; then
														echo "l|$1"
													else
														echo "o|$1"
													fi
												}
												display_file_info "$0" ' {} \;
	fi
else
	#search within contents and other attributes
	find  "$LOCATION" $CRITERIA -exec sh -c 'display_file_info(){
													if [ -d "$1" ]; then
														echo "d|$1"
													elif [ -f "$1" ]; then
														echo "f|$1"
													elif [ -L "$1" ]; then
														echo "l|$1"
													else
														echo "o|$1"
													fi
												}
	search_in_file(){
	if [ ! -z "$UNCOMPRESS" ];then
		if gunzip -c "$1" 2>/dev/null|grep -q "$2" ; then
			FILE="$1"
		elif unzip -p "$1" 2>/dev/null|grep -q "$2" ; then
			FILE="$1"
		elif bzip2 -dc "$1" 2>/dev/null|grep -q "$2" ; then
			FILE="$1"
		elif unxz -dc "$1" 2>/dev/null|grep -q "$2" ; then
			FILE="$1"
		fi
	fi
	
	if [ -z "$FILE" ]; then
		if grep -q "$2" "$1" ; then
			FILE="$1"
		fi
	fi

	if [ ! -z "$FILE" ]; then
		display_file_info "$FILE"
	fi

}
search_in_file "$0" "$PATTERN" ' {} \;
fi
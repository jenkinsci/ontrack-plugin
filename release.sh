#!/bin/bash

# Help function
function show_help {
	echo "Ontrack Jenkins release script."
	echo ""
	echo "Available options are:"
	echo "    -h, --help                    Displays this help"
	echo "    -v, --version                 Version to create"
	echo "    -n, --next-version            Next version"
}

# Check function

function check {
	if [ "$1" == "" ]
	then
		echo $2
		exit 1
	fi
}

# Defaults

VERSION=
NEXT_VERSION=

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-v=*|--version=*)
			VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-n=*|--next-version=*)
            NEXT_VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checking

check "$VERSION" "Version (--version) is required."
check "$NEXT_VERSION" "Next version (--next-version) is required."

# Logging

echo "Version      = ${VERSION}"
echo "Next version = ${NEXT_VERSION}"

# Checks everything is OK

mvn clean verify

# Sets the version and commits

mvn versions:set -DnewVersion=${VERSION} -DgenerateBackupPoms=false
git commit -am "Version ${VERSION}"
git push

# Deployment

mvn clean deploy

# Upgrade of the version

mvn versions:set -DnewVersion="${NEXT_VERSION}-SNAPSHOT" -DgenerateBackupPoms=false
git commit -am "Starting ${NEXT_VERSION}"
git push

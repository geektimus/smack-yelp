#!/usr/bin/env sh

function usage() {
    echo "This script requires a URL to be downloaded"
    echo "./download_dataset.sh http://example.com/dataset.tar.gz [destination]"
    exit 1
}

function download_dataset() {
    if [ -z "$fileURL" ]
    then
        echo "No argument supplied"
        usage
    else
        cd $fileDst && curl $fileURL | tar xvz
        exit $?
    fi
}

if [ $# -eq 0 ]
then
    echo "No arguments supplied"
    usage
    exit 1
fi

fileURL=$1
fileDst=${2:-downloader}

[ ! -d "$fileDst" ] && mkdir $fileDst
download_dataset
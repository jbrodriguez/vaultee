#!/bin/bash
 
# works with a file called VERSION in the current directory,
# the contents of which should be a semantic version number
# such as "1.2"
 
# this script will display the current version, automatically
# suggest a "minor" version update, and ask for input to use
# the suggestion, or a newly entered value.
 
# once the new version number is determined, the script will
# pull a list of changes from git history, prepend this to
# a file called CHANGES (under the title of the new version
# number) and create a GIT tag.

 
if [ -f VERSION ]; then
    BASE_STRING=`cat VERSION`
    BASE_LIST=(`echo $BASE_STRING | tr '.' ' '`)



    BASE_DATE=`date +"%Y%m%d"`

    BASE_COMMITS=`git rev-list --all | wc -l | tr -d ' '`

    V_MAJOR=${BASE_LIST[0]}
    V_MINOR=${BASE_LIST[1]}
    echo "Current version : $BASE_STRING"
    V_MINOR=$((V_MINOR + 1))
    SUGGESTED_VERSION="$V_MAJOR.$V_MINOR"
    read -p "Enter a version number [$SUGGESTED_VERSION]: " INPUT_STRING
    if [ "$INPUT_STRING" = "" ]; then
        INPUT_STRING=$SUGGESTED_VERSION
    fi
    echo "Will set new version to be $INPUT_STRING"

    shortversion=$INPUT_STRING
    longversion="$shortversion-$BASE_DATE.$BASE_COMMITS"

    BASE_TRANSFORM=`sed "s/value('shortversion'\, '\(.*\)'/value('shortversion', '$shortversion'/" web/js/services.js | sed "s/value('longversion'\, '\(.*\)'/value('longversion', '$longversion'/"`

    # $BASE_SHORT > ./web/js/tmpservices.js
    echo "$BASE_TRANSFORM" > web/js/tmpservices.js
    mv web/js/tmpservices.js web/js/services.js
else
    echo "Could not find a VERSION file"
    read -p "Do you want to create a version file and start from scratch? [y]" RESPONSE
    if [ "$RESPONSE" = "" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "Y" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "Yes" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "yes" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "YES" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "y" ]; then
        echo "0.1" > VERSION
    fi
 
fi
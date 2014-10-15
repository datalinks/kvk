# Name:    createProject.sh
# Ver:     November 2013 - v0.1
# Descr:   Shell wrapper for creating kvk datapower project structure 
#  	   uses maven archetype 
# Author:  Chris Vugrinec (chris.vugrinec@kvk.nl)

clear
if [ $# -ne 1 ]
  then
    echo "Syntax is: ./createProject.sh <projectname> "
    echo ""
    echo ""
    exit 1
fi

project_name=$1
echo "Project Name = "$project_name
mvn archetype:generate \
  -DarchetypeGroupId=com.kvk.dp \
  -DarchetypeArtifactId=ilf \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.kvk.dp.ilf \
  -DartifactId=$project_name

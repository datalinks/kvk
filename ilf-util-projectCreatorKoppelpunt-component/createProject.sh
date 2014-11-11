#!/bin/bash
# Name:    createProject.sh
# Ver:     November 2013 - v0.2
# Descr:   v0.1 Shell wrapper for creating kvk datapower project structure 
#  	   uses maven archetype 
#	   v0.2 Added the check for naming conventions	
# Author:  Chris Vugrinec (chris.vugrinec@kvk.nl)

rm -f svn
rm -f project
rm -f archetype
rm -f aflevername

function buildStuff(){

echo "Building template for Project Name = "$project_name" using archeTypeId "$archeTypeId
mvn archetype:generate -B \
  -DarchetypeGroupId=nl.kvk.dp \
  -DarchetypeArtifactId=$archeTypeId \
  -DarchetypeVersion=1.0 \
  -DgroupId=nl.kvk.dp.ilf \
  -DartifactId=$project_name
}

function buildKoppelpuntStuff(){
fWsaTo=$1
fWsaAction=$2
tWsaTo=$3
tWsaAction=$4
afleverEndPointName=$5
expectedVersion=$6

echo "WRAPPER:  Building template for Project Name = "$project_name" using archeTypeId "$archeTypeId" with functionalToAction "$fWsaTo-$fWsaAction

mvn archetype:generate -B \
  -DarchetypeGroupId=nl.kvk.dp \
  -DarchetypeArtifactId=$archeTypeId \
  -DarchetypeVersion=1.0 \
  -DgroupId=nl.kvk.dp.ilf \
  -DartifactId=$project_name \
  -DprojectName=$project_name \
  -Denv=dev

echo "DAR:   Building template for Project Name = "$project_name" using archeTypeId "$archeTypeId" with functionalToAction "$fWsaTo-$fWsaAction

mvn archetype:generate -B \
  -DarchetypeGroupId=nl.kvk.dp \
  -DarchetypeArtifactId=$archeTypeId-dar \
  -DarchetypeVersion=1.0 \
  -DgroupId=nl.kvk.dp.ilf \
  -DartifactId=$project_name-dar \
  -DprojectName=$project_name \
  -Denv=dev


echo "COMPONENT:   Building template for Project Name = "$project_name" using archeTypeId "$archeTypeId" with functionalToAction "$fWsaTo-$fWsaAction

mvn archetype:generate -B \
  -DfWsaTo=$fWsaTo \
  -DfWsaAction=$fWsaAction \
  -DtWsaTo=$tWsaTo \
  -DtWsaAction=$tWsaAction \
  -DexpectedVersion=$expectedVersion \
  -DafleverEndPointName=$afleverEndPointName \
  -DarchetypeGroupId=nl.kvk.dp \
  -DarchetypeArtifactId=$archeTypeId-component \
  -DarchetypeVersion=1.0 \
  -DgroupId=nl.kvk.dp.ilf \
  -DartifactId=$project_name-component \
  -DprojectName=$project_name \
  -Denv=dev
}


function buildAfleverStuff(){

echo "DAR:  Building template for Project Name = "$project_name" using archeTypeId "$archeTypeId" with functionalToAction "$fWsaTo-$fWsaAction
mvn archetype:generate -B \
  -DarchetypeGroupId=nl.kvk.dp \
  -DarchetypeArtifactId=$archeTypeId-dar \
  -DarchetypeVersion=1.0 \
  -DgroupId=nl.kvk.dp.ilf \
  -DartifactId=$project_name-dar \
  -DprojectName=$project_name

echo "COMPONENT:  Building template for Project Name = "$project_name" using archeTypeId "$archeTypeId" with functionalToAction "$fWsaTo-$fWsaAction
mvn archetype:generate -B \
  -DexpectedVersion=0.1 \
  -DafleverEndPointName=$project_name \
  -DarchetypeGroupId=nl.kvk.dp \
  -DarchetypeArtifactId=$archeTypeId-component \
  -DarchetypeVersion=1.0 \
  -DgroupId=nl.kvk.dp.ilf \
  -DartifactId=$project_name-component \
  -DprojectName=$project_name

echo "WRAPPER:  Building template for Project Name = "$project_name" using archeTypeId "$archeTypeId" with functionalToAction "$fWsaTo-$fWsaAction
mvn archetype:generate -B \
  -DarchetypeGroupId=nl.kvk.dp \
  -DarchetypeArtifactId=$archeTypeId \
  -DarchetypeVersion=1.0 \
  -DgroupId=nl.kvk.dp.ilf \
  -DartifactId=$project_name \
  -DprojectName=$project_name


}



function showError(){
   projectName=$1
   archeTypeId=$2
   archeTypeIndicator=$3
   echo ""
   echo ""
   echo "FOUT: projectnaam voor type $archeTypeId moet aan het einde $archeTypeIndicator bevatten"
   echo "jij hebt nu $projectName als projectnaam gegeven!!!"
   echo ""
   echo ""
   exit 1
}

function renameToOrAction(){
   toOrActionName=$1
   echo $toOrActionName | sed 's/\/\///' | sed 's/http:ws.kvk.nl\///' | sed 's/\//_/g'
}

function showErrorToAction(){
   action=$1
   echo ""
   echo ""
   if [[ $action == *prefix* ]]
   then
       echo "FOUT: Your functional or technical TO or ACTION should start with $toActionPrefix"
   fi
   if [[ $action == *sameTo* ]]
   then
       echo "FOUT: Your Functional TO name can not be the same like the Technical TO name"
   fi
   if [[ $action == *sameAction* ]]
   then
       echo "FOUT: Your Function ACTION name can not be the same like the Technical ACTION name"
   fi
   if [[ $action == *afleverEndPointName* ]]
   then
       echo "FOUT: Logical name of your aflever service should end in -a**" 
   fi
   echo ""
   echo ""
   exit 1
}

java -jar ilf-util-projectCreator/DpProjectCreator-jfx.jar
clear
if [[ -f project && -f archetype ]]
then
   project_name=`cat project`
   archeTypeId=`cat archetype`
   toActionPrefix="http://ws.kvk.nl/"
   rm -f project
   rm -f archetype
else
   echo "projectCreator aborted...nothing created"
   exit 1;
fi 

echo "projectname is: "$project_name
echo "archetype is: "$archeTypeId

string_length=`expr length $project_name` 
start_pos=`expr $string_length-4`
type_determinator=`echo ${project_name:$start_pos:$string_length}`


#  KOPPELPUNT
if [[ $archeTypeId == *ilf-koppelpunt* ]]
then
   if [[ $type_determinator != *-k* ]]
   then
       showError $project_name $archeTypeId -k
       exit 1
   else
      echo $project_name > koppelpuntname
      java -jar ilf-util-projectCreator/DpKoppelpuntCreator-jfx.jar
      fWsaTo=`cat fwsato`
      fWsaAction=`cat fwsaaction`
      tWsaTo=`cat twsato`
      tWsaAction=`cat twsaaction`
      afleverEndPointName=`cat aflevername`
      echo "cleanup of tmp files...."

      if [ -f svn ]
      then
         svnUse=`cat svn`
         rm -f svn
      fi
      if [ -f ldif ]
      then
         ldifUse=`cat ldif`
         rm -f ldif
      fi
      rm -f twsaaction
      rm -f twsato
      rm -f fwsaaction
      rm -f fwsato
      rm -f aflevername
      rm -f koppelpuntname


      # Prefix check
      if [[ $fWsaTo != $toActionPrefix* || $fWsaAction != $toActionPrefix* || $tWsaTo != $toActionPrefix* || $tWsaAction != $toActionPrefix*  ]]
      then
 	showErrorToAction prefix
        exit 1
      fi

      fWsaTo2=`renameToOrAction $fWsaTo`
      fWsaAction2=`renameToOrAction $fWsaAction`
      tWsaTo2=`renameToOrAction $tWsaTo`
      tWsaAction2=`renameToOrAction $tWsaAction`

      # Check same TO name
      if [[ $fWsaTo2 == $tWsaTo2 ]]
      then
        showErrorToAction sameTo
      fi
      # Check same Action name
      if [[ $fWsaAction2 == $tWsaAction2 ]]
      then
        showErrorToAction sameAction
      fi
      # Check aflever name
      if [[ $afleverEndPointName != *-a* ]]
      then
        showErrorToAction afleverEndPointName 
      fi

      #echo "What will be the expected version of $afleverEndPointName , NOTE...this will be tested after creation, for e.g. 1.0"
      expectedVersion="1.0"
      buildKoppelpuntStuff $fWsaTo2 $fWsaAction2 $tWsaTo2 $tWsaAction2 $afleverEndPointName $expectedVersion
      echo "Moving archetypes into master  $project_name"
      mv $project_name-dar $project_name
      mv $project_name-component $project_name
      #cd $project_name/$project_name-component
      #mvn test 
   fi
   cd $project_name
   mvn -Denv=dev eclipse:eclipse
   mvn -Denv=dev clean
   domain=`echo $project_name | head -c 7`
   if [[ $ldifUse = "true" ]]
   then
      curDir=`pwd`
      echo "I am now in $curDir"
      cd ..
      mv *.ldif $project_name/$project_name-component/src/resources/export/
      echo "LDIF File can be fount at: $project_name/$project_name-component/src/resources/export/"
      cd -
   fi
   if [[ $svnUse = "true" ]]
   then
      svn import http://svn.k94.kvk.nl/repo-integratielaag/Datapower/$domain/$project_name/trunk -m "initial import by projectCreatorTool"
      cd ..
      rm -rf $project_name
      svn co http://svn.k94.kvk.nl/repo-integratielaag/Datapower/$domain/$project_name/trunk $project_name
   fi

# JENKINS stuff
   cat ilf-util-projectCreator/koppelpunt-jenkins.xml  | sed "s/_domainName_/$domain/" | sed "s/_application_/$project_name/" > ilf-util-projectCreator/koppelpunt-jenkins-use.xml
   echo trying command: "java -jar ilf-util-projectCreator/jenkins-cli.jar -s http://se94alm034.k94.kvk.nl:8080/il/ create-job $project_name-deploy2dev < ilf-util-projectCreator/koppelpunt-jenkins-use.xml"
   result=`java -jar ilf-util-projectCreator/jenkins-cli.jar -s http://se94alm034.k94.kvk.nl:8080/il/ create-job $project_name < ilf-util-projectCreator/koppelpunt-jenkins-use.xml`
   if [[ $result == *Exception* ]]
   then
        java -jar ilf-util-projectCreator/jenkins-cli.jar -s http://se94alm034.k94.kvk.nl:8080/il/ create-job $project_name < ilf-util-projectCreator/koppelpunt-jenkins-use.xml
   fi
   echo "if for some reason an exception occurs....copy and paste the previous command"

fi

# FUNCTIONAL AANLEVER
if [[ $archeTypeId == *ilf-functional-aanlever* ]]
then
   if [[ $type_determinator != *-f* ]]
   then
       showError $project_name $archeTypeId -f 
       exit 1
   else
      buildStuff
   fi
fi

# TECHNICAL AANLEVER
if [[ $archeTypeId == *ilf-technical-aanlever* ]]
then
   if [[ $type_determinator != *-t* ]]
   then
        showError $project_name $archeTypeId -t
        exit 1
   else
      buildStuff
   fi
fi

# ROUTE
if [[ $archeTypeId == *ilf-route* ]]
then
   if [[ $type_determinator != *-r* ]]
   then
        showError $project_name $archeTypeId -r
        exit 1
   else
      buildStuff
   fi
fi

# AFLEVER
if [[ $archeTypeId == *ilf-aflever* ]]
then
   if [[ $type_determinator != *-a* ]]
   then
        showError $project_name $archeTypeId -a
        exit 1
   else
         buildAfleverStuff
      	 mv $project_name-dar $project_name
      	 mv $project_name-component $project_name
   fi
   cd $project_name
   mvn eclipse:eclipse
   mvn clean
   domain=`echo $project_name | head -c 7`
   cd ..
   if [[ -f svn ]] 
   then
      cd -
      svn import http://svn.k94.kvk.nl/repo-integratielaag/Datapower/$domain/$project_name/trunk -m "initial import by projectCreatorTool"
      cd ..
      rm -rf $project_name
      svn co http://svn.k94.kvk.nl/repo-integratielaag/Datapower/$domain/$project_name/trunk $project_name 
      rm -f svn
   fi
   cat ilf-util-projectCreator/aanleverservice-jenkins.xml  | sed "s/_domainName_/$domain/" | sed "s/_aanleverserviceName_/$project_name/" > ilf-util-projectCreator/aanleverservice-jenkins-use.xml
   echo trying command: "java -jar ilf-util-projectCreator/jenkins-cli.jar -s http://se94alm034.k94.kvk.nl:8080/il/ create-job $project_name < ilf-util-projectCreator/aanleverservice-jenkins-use.xml"
   result=`java -jar ilf-util-projectCreator/jenkins-cli.jar -s http://se94alm034.k94.kvk.nl:8080/il/ create-job $project_name < ilf-util-projectCreator/aanleverservice-jenkins-use.xml`
   if [[ $result == *java.io.EOFException* ]]
   then
        java -jar ilf-util-projectCreator/jenkins-cli.jar -s http://se94alm034.k94.kvk.nl:8080/il/ create-job $project_name < ilf-util-projectCreator/aanleverservice-jenkins-use.xml
   fi
   echo "if for some reason an exception occurs....copy and paste the previous command" 
   #java -jar ilf-util-projectCreator/jenkins-cli.jar -s http://se94alm034.k94.kvk.nl:8080/il get-job ilf-fac-documentumService-a1 > aanleverservice-jenkins.xml 
fi

# DISTRIBUEER
if [[ $archeTypeId == *ilf-distribueer* ]]
then
   if [[ $type_determinator != *-d* ]]
   then
        showError $project_name $archeTypeId -d
        exit 1
   else
      buildStuff
   fi
fi

# TRANSFORMEER
if [[ $archeTypeId == *ilf-transformeer* ]]
then
   if [[ $type_determinator != *-x* ]]
   then
        showError $project_name $archeTypeId -x
        exit 1
   else
      buildStuff
   fi
fi


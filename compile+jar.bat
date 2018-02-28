@echo off

REM dir "$(dirname "$0")"

set classpath=.;libraries\*
set folders=.\virtualassistant\ai\*.java .\virtualassistant\chatbot\*.java .\virtualassistant\data\news\*.java .\virtualassistant\data\system\*.java .\virtualassistant\data\stocks\*.java .\virtualassistant\data\datastore\*.java .\virtualassistant\gui\*.java .\virtualassistant\misc\*.java

REM Compile all java files

javac -cp %classpath% %folders% virtualassistant\IVirtualAssistant.java virtualassistant\VirtualAssistant.java || { echo 'Compiling Failed' ; exit 1; }

set gui=.\virtualassistant\gui\*.class .\virtualassistant\gui\sample.fxml .\virtualassistant\gui\images\* virtualassistant\gui\styles\* 
set classes=virtualassistant\ai\*.class virtualassistant\chatbot\*.class virtualassistant\chatbot\grammars\* virtualassistant\data\datastore\*.class virtualassistant\data\news\*.class virtualassistant\data\stocks\*.class virtualassistant\data\system\*.class virtualassistant\misc\*.class

echo 'Making jar file...'

jar cfm virtualassistant.jar Manifest.txt %gui% %classes% virtualassistant/*.class 


echo 'Succesfully created jar file'


REM echo 'Run "java -jar virtualassistant.jar" to run program';
pause

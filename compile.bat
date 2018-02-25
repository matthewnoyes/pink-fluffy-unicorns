@echo off

set classpath=C:\Users\Admin\Documents\Github\pink-fluffy-unicorns\virtualassistant\data\jsoup-1.11.2.jar;C:\Users\Admin\Documents\Github\pink-fluffy-unicorns\virtualassistant\data\datastore\JSON-parser.jar
REM Compile all java files

javac -cp %classpath% virtualassistant/ai/*.java virtualassistant/chatbot/*.java virtualassistant/data/news/*.java virtualassistant/data/system/*.java virtualassistant/data/stocks/*.java virtualassistant/data/datastore/*.java virtualassistant/gui/*.java virtualassistant/misc/*.java virtualassistant/IVirtualAssistant.java virtualassistant/VirtualAssistant.java || { echo 'Compiling Failed' ; exit 1; }

jar cfe virtualassistant.jar virtualassistant.gui.Main * || { echo 'Creating jar file failed' ; exit 1; }

echo 'Succesfully created jar file';
REM echo 'Run "java -jar virtualassistant.jar" to run program';
pause

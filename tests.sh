cd "$(dirname "$0")"

classpath='.:libraries/*';
folders='./virtualassistant/ai/*.java ./virtualassistant/chatbot/*.java ./virtualassistant/data/news/*.java ./virtualassistant/data/system/*.java ./virtualassistant/data/stocks/*.java ./virtualassistant/data/datastore/*.java ./virtualassistant/gui/*.java ./virtualassistant/misc/*.java ./tests/UnitTest.java';
#Compile all java files
javac -cp $classpath $folders ./virtualassistant/IVirtualAssistant.java ./virtualassistant/VirtualAssistant.java || { echo 'Compiling Failed' ; exit 1; }

#jar cfe virtualassistant.jar virtualassistant.gui.Main ./virtualassistant/* || { echo 'Creating jar file failed' ; exit 1; }
gui='./virtualassistant/gui/*.class ./virtualassistant/gui/sample.fxml ./virtualassistant/gui/images/* ./virtualassistant/gui/styles/*';
classes='./virtualassistant/ai/*.class ./virtualassistant/chatbot/*.class ./virtualassistant/data/datastore/*.class ./virtualassistant/data/news/*.class ./virtualassistant/data/stocks/*.class ./virtualassistant/data/system/*.class ./virtualassistant/misc/*.class';
jar cfm virtualassistanttests.jar TestManifest.txt $gui $classes ./virtualassistant/*.class ./tests/*.class || { echo 'Creating jar file failed' ; exit 1; }


echo 'Succesfully created jar file';
echo 'Run "java -jar virtualassistanttests.jar" to run program';

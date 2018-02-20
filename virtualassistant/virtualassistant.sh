#Compile all java files
javac ./ai/*.java chatbot/*.java data/datastore/*.java data/news/*.java data/stocks/*.java gui/*.java misc/*.java IVirtualAssistant.java VirtualAssistant.java -cp virtualassistant/data/jsoup-1.11.2.jar || { echo 'Compiling Failed' ; exit 1; }

jar cfe virtualassistant.jar virtualassistant.VirtualAssistant * || { echo 'Creating jar file failed' ; exit 1; }

echo 'Succesfully created jar file';
echo 'Run "java -jar virtualassistant.jar" to run program';

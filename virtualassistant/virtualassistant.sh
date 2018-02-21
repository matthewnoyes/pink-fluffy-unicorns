cd "$(dirname "$0")"

classpath='.:./data/jsoup-1.11.2.jar:./data/datastore/JSON-parser.jar';

#Compile all java files
javac -cp $classpath ./ai/*.java chatbot/*.java data/news/*.java data/stocks/*.java data/datastore/*.java gui/*.java misc/*.java IVirtualAssistant.java VirtualAssistant.java || { echo 'Compiling Failed' ; exit 1; }

jar cfe virtualassistant.jar virtualassistant.VirtualAssistant * || { echo 'Creating jar file failed' ; exit 1; }

echo 'Succesfully created jar file';
#echo 'Run "java -jar virtualassistant.jar" to run program';

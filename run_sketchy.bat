@echo off

set classpath=C:\Users\Admin\Documents\Github\pink-fluffy-unicorns\virtualassistant\data\jsoup-1.11.2.jar;C:\Users\Admin\Documents\Github\pink-fluffy-unicorns\virtualassistant\data\datastore\JSON-parser.jar


echo 'Runinng...'

java -cp %classpath% virtualassistant.gui.Main

pause
@echo off
set /p address=������webservice��ַ(��?wsdl): 
set /p package=���������: 
set /p dest=������Ŀ¼: 
call wsdl2java -p %package% -d %dest% -client %address%
call dir /s /B %dest%\*.java > %dest%\sources.txt
call javac @%dest%\sources.txt
call del %dest%\sources.txt %dest%\*.java /s /q /a
call jar -cf %dest%\client.jar -C %dest%\ .
rmdir /s/q %dest%\com
start %dest%
pause

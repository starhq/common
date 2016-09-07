@echo off
set /p address=请输入webservice地址(带?wsdl): 
set /p package=请输入包名: 
set /p dest=请输入目录: 
call wsdl2java -p %package% -d %dest% -client %address%
call dir /s /B %dest%\*.java > %dest%\sources.txt
call javac @%dest%\sources.txt
call del %dest%\sources.txt %dest%\*.java /s /q /a
call jar -cf %dest%\client.jar -C %dest%\ .
rmdir /s/q %dest%\com
start %dest%
pause

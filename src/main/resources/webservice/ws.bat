@echo off
set /p address=请输入webservice地址(带?wsdl的)
wsimport %address%
jar -cf client.jar .
rmdir /s/q com
@echo off
set /p address=������webservice��ַ(��?wsdl��)
wsimport %address%
jar -cf client.jar .
rmdir /s/q com
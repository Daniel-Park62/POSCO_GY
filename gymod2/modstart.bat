@REM TEXT 파일에 정보를 저장하는 MODBUS IF 

@echo off
@taskkill /f /im "nodbmod.exe" 2> nul
@set GWIP=192.168.0.223
@set TAGPORT=1502
@SET DEVPORT=1503
@SET SCOUNT=3
@set TimeInterval=6
@set DIRNM=.
@nodbmod.exe

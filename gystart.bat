@echo off
taskkill /f /im "gymod.exe" 
set GWIP_WS=192.168.0.223
set GWIP_DS=192.168.0.223

start gymon.bat
gymod.exe

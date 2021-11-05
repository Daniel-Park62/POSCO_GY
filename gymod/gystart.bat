@echo off
@taskkill /f /im "gymod.exe" 2> nul
set GWIP_WS=192.168.0.223
set GWIP_DS=192.168.0.233
gymod.exe

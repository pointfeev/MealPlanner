@ECHO OFF

NET SESSION > NUL
IF %ERRORLEVEL% NEQ 0 GOTO ELEVATE
GOTO TASK

:ELEVATE
CD /d %~dp0
MSHTA "javascript: var shell = new ActiveXObject('shell.application'); shell.ShellExecute('%~nx0', '', '', 'runas', 1); close();"
EXIT

:TASK
SC START "PanGPS" > NUL || SC STOP "PanGPS" > NUL && TASKKILL /F /IM "PanGPA.exe" > NUL
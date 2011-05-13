
; This script describes how the the mctool is supposed to be installed on windows

;--------------------------------

; The name of the installer
Name "Multicast Test Tool Installer"

; The file to write
OutFile "mctool-setup.exe"

; The default installation directory
InstallDir $PROGRAMFILES\MCTool

; The text to prompt the user to enter a directory
DirText "This will install the Multicast Test Tool on your computer. Choose a directory"

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important

; Set output path to the installation directory.
SetOutPath $INSTDIR

; Put file there
File /oname=mctool.jar ..\target\mctool-*-jar-with-dependencies.jar
File ..\src\main\resources\images\logo.ico

; Now create shortcuts
SetShellVarContext all
CreateDirectory "$SMPROGRAMS\SPAM"
CreateShortCut "$SMPROGRAMS\SPAM\Multicast Testing Tool.lnk" "$SYSDIR\javaw.exe" '-jar "$INSTDIR\mctool.jar"' "$INSTDIR\logo.ico"
CreateShortCut "$SMPROGRAMS\SPAM\Uninstall.lnk" "$INSTDIR\Uninstall.exe"

; Tell the compiler to write an uninstaller and to look for a "Uninstall" section
WriteUninstaller $INSTDIR\Uninstall.exe

SectionEnd ; end the section


; The uninstall section
Section "Uninstall"

Delete $INSTDIR\Uninstall.exe
Delete $INSTDIR\mctool.jar
RMDir $INSTDIR

SetShellVarContext all
Delete "$SMPROGRAMS\SPAM\Multicast Testing Tool.lnk"
Delete "$SMPROGRAMS\SPAM\Uninstall.lnk"
RMDIR "$SMPROGRAMS\SPAM"

SectionEnd 
#!/bin/bash
clear
echo "Have muCommander use your OS wide JavaAppletPlugin"
echo ==================================================
echo
echo "Pre-requisites check:"

simlink? () {
test "$(readlink "${1}")";
}

info_plist=/Library/Internet\ Plug-Ins/JavaAppletPlugin.plugin/Contents/info.plist
APP_DIR="/Applications"
# for dev only
# APP_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

if [[ -f "${info_plist}" ]]; then echo "  ✓ compatible Java JRE found 👍"; else echo "No compatible Java JRE found 😞 - aborting." && echo && exit; fi

if [ ! -d "${APP_DIR}/muCommander.app" ]; then echo "No muCommander app found in your Applications folder 😞 - aborting." && exit; else echo "  ✓ muCommander app found in ${APP_DIR} 👍"; fi
echo --------------------------------------------------
echo
echo "This script will remove the Java JRE bundled into your muCommander app and"
echo "instead link to your OS wide JRE install, then launch the modified app."
echo
read -p "Proceed now (y|n)? " -n 1 -r
if [[ $REPLY =~ ^[Yy]$ ]]
then
echo
rm -Rf ${APP_DIR}/muCommander.app/Contents/PlugIns/macOS/Contents/Home/jre
echo "  ✓ removed the Java bundle in the muCommander app 👍"

mkdir -p ${APP_DIR}/muCommander.app/Contents/PlugIns/macOS/Contents/Home
cp -a "$info_plist" ${APP_DIR}/muCommander.app/Contents/PlugIns/macOS/Contents/
ln -s "/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home" ${APP_DIR}/muCommander.app/Contents/PlugIns/macOS/Contents/Home/jre
echo "  ✓ placed SymLinks to your OS wide Java Plugin into your muCommander app 👍"
echo
echo "..launching the modified muCommander app now!"
open -n "${APP_DIR}/muCommander.app"
else echo
fi
echo
echo "This window will automatically close in 4 seconds..."
sleep 4
osascript -e 'tell app "Terminal"' -e 'close (every window whose name contains ".command")' -e 'if number of windows = 0 then quit' -e 'end tell' & exit;

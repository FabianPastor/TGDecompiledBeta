#!/bin/bash
for file in `grep --exclude-dir=.git -rli './' -e '[0-9]\{10\}[),;\ }]\{1\}'`
do
echo $file;
sed -i 's/[0-9]\{10\}\([),;\ }]\{1\}\)/NUM\1/g' $file
done  

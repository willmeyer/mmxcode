cd target/mmxcode-app-0.0.1-SNAPSHOT-dist.dir
chmod u+x mmxcode/*.sh
zip -o -r mmxcode.zip ./
cd ../../
scp target/mmxcode-app-0.0.1-SNAPSHOT-dist.dir/mmxcode.zip willmeyer@pacer.dreamhost.com:willmeyer.com/downloads/mmxcode

 

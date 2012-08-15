java ^
 -Djava.library.path=../.. ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.logLevel=SEVERE^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=false ^
 -classpath ../../aparapi.jar;mandel.jar ^
 com.amd.aparapi.sample.mandel.Main



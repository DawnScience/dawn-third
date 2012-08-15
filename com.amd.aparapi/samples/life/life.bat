java ^
 -Djava.library.path=../.. ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.enableProfiling=true ^
 -classpath ../../aparapi.jar;life.jar ^
 com.amd.aparapi.sample.life.Main



#!/bin/bash

java -classpath $HOME/local/clojure/clojure.jar:$HOME/Java/commons-codec-1.4/commons-codec-1.4.jar:$HOME/Java/commons-lang-2.5/commons-lang-2.5.jar:$HOME/Clojure/clojure-contrib-1.1.0/clojure-contrib.jar:. clojure.main base64_test.clj


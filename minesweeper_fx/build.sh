#!/bin/bash

if [[ -z $PATH_TO_FX ]]; then
    echo "\$PATH_TO_FX is not set"
    exit 1
fi

cd src &&\
javac \
    -sourcepath java/ \
    --module-path $PATH_TO_FX \
    --add-modules javafx.controls,javafx.fxml java/gr/ntua/medialab/application/App.java

#!/bin/bash

if [[ -z $PATH_TO_FX ]]; then
    echo "\$PATH_TO_FX is not set"
    exit 1
fi

cd src &&\
java \
    # Specifying a different output folder for the classfiles will break the program,
    # unless you make copies of the various "fxml" folders and the "resources" folder,
    # as paths inside the program are all relative.
    -cp java/ \
    --module-path $PATH_TO_FX \
    --add-modules javafx.controls,javafx.fxml gr.ntua.medialab.application.App

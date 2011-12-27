#!/bin/bash

# Set MMXCODE_HOME to where you install this
MMXCODE_HOME="."

# Do work
java -jar $MMXCODE_HOME/mmxcode-app-0.0.1-SNAPSHOT-jar-with-dependencies.jar $@

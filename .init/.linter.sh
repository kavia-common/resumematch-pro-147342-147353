#!/bin/bash
cd /home/kavia/workspace/code-generation/resumematch-pro-147342-147353/backend
./gradlew checkstyleMain
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi


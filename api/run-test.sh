#!/usr/bin/env bash

sbt test pack
cd target/pack
./bin/run-test-server
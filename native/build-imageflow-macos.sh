#!/bin/bash

# you should install rust and cargo and other dependencies

git clone https://github.com/imazen/imageflow.git libimageflow
brew install nasm
cargo install dssim
cd libimageflow
./build.sh

#!/bin/bash
rm -rf build
git clone git@github.com:kouphax/kouphax.github.com.git ./build
bundle exec "middleman build --clean"
cd build
git add -A
git commit -m "[automated] pushing updated site"
git push origin master
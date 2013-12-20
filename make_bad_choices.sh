#!/bin/bash
rm -rf build
mkdir build
cd build
git init
git remote add origin git@github.com:kouphax/kouphax.github.com.git
git pull origin master
rm -rf *
cd ..
bundle exec middleman build
cd build
git add -A
git commit -m "New push"
git push origin master
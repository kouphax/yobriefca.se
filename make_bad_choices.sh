#!/bin/bash

rm -rf build

bundle exec middleman build

cd build

git init

git add -A

git commit -m "New push"

git remote add origin git@github.com:kouphax/kouphax.github.com.git

git push --force origin master
cd ..
bundle exec middleman > /dev/null &
PID=$!
echo "Middleman Started with pid of $PID"
echo "Sleeping for 5 seconds to let middleman startup"
sleep 5s
cd -
ruby gist_test.rb
kill $PID

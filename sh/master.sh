kill -9 `ps -ef|grep locust |awk -F " " '{print $2}'`
locust -f  $1 --no-web --no-reset -c $2 -r 500 -t $3 --master --csv $4 --expect-slaves 7 &
locust -f  $1 --no-web --no-reset --slave   &
locust -f  $1 --no-web --no-reset --slave   &
locust -f  $1 --no-web --no-reset --slave   &

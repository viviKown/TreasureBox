kill -9 `ps -ef|grep locust |awk -F " " '{print $2}'`
locust -f  $1 --no-web --no-reset --slave --master-host 10.100.0.13 &
locust -f  $1 --no-web --no-reset --slave --master-host 10.100.0.13 &
locust -f  $1 --no-web --no-reset --slave --master-host 10.100.0.13 &
locust -f  $1 --no-web --no-reset --slave --master-host 10.100.0.13 &



# -*-coding:utf-8 -*- #


import os
import TOcr
import TFile
import TPicture
import TPeripheral
import time
import TLog
import TMenu
import TAudio
import TTcli

a = "run_test_each_night"
b = "re_run"
c = "re_hui"
dir_name = os.path.dirname(__file__)
dir_running_in_night = os.path.join(dir_name, b)
dir_running_in_night2 = os.path.join(dir_name, c)
# dir_running_in_night = os.path.join(dir_name, "running_in_night_tmp")
print(dir_running_in_night)
# print(dir_running_in_night2)
scripts = os.listdir(dir_running_in_night)
# scripts2 = os.listdir(dir_running_in_night2)
# scripts.extend(scripts2)
# print(len(scripts), scripts)

timer = time.time()
COUNT = 0
folder_list = [dir_running_in_night]
for folder in folder_list:
    for _ in range(1):
        for script in scripts:
            COUNT = COUNT + 1
            script = os.path.join(folder, script)
            print(script)
            os.system('python -u "%s"' % script)
            # os.popen("python -u %s" % script)
            time.sleep(5)






# -*- coding: utf-8 -*-
import os
import shutil

import qrcode
import requests
from io import BytesIO
import TPerformance
from pyzbar import pyzbar
from PIL import Image,ImageEnhance

def generate_qr_code(img_save_dir):
    if os.path.exists(img_save_dir):
        shutil.rmtree(img_save_dir)
    os.makedirs(img_save_dir)
    for i in range(1,3560):
        content = i
        qr = qrcode.QRCode(version=1,
                           error_correction=qrcode.constants.ERROR_CORRECT_L,
                           box_size=20,
                           border=8,
                           )
        qr.add_data(content)
        qr.make(fit=True)
        img = qr.make_image()
        img_path = img_save_dir + '/' + "%04d" % i +'.jpg'
        img.save(img_path)
    print("生成二维码完毕")


def get_qr_code(img_save_dir):
    """
    读取二维码的内容
    :param img_save_dir: 二维码图片存放目录
    :return:识别结果列表
    """
    txt_list = []
    identify_result = []
    img_list = TPerformance.get_img_list_for_dir(img_save_dir)
    print(len(img_list))
    for img in  img_list:
        temp = img
        img = Image.open(img)
        txt_list = pyzbar.decode(img)
        for t in txt_list:
            data_in_img = t.data.decode("utf-8")
            print(data_in_img,temp)
            if data_in_img != '':
                identify_result.append(int(data_in_img))
            else:
                identify_result.append(data_in_img)
    print(len(identify_result),identify_result)
    print(len(set(identify_result)),set(identify_result))
    # return identify_result


def composite_img(img_save_dir,new_dir):
    if os.path.exists(new_dir):
        shutil.rmtree(new_dir)
    os.makedirs(new_dir)
    img_list = TPerformance.get_img_list_for_dir(img_save_dir)
    base_img = "./base370.jpg"
    base_img = Image.open(base_img)
    for i in range(len(img_list)):
        main_img = Image.open(img_list[i])
        base_img.paste(main_img, (780,230,1150,600))
        index = i + 1
        save_path = new_dir + '/' + "image%04d" % index +'.jpg'
        base_img.save(save_path)
    print("生成合成图完毕")


def composite_change_img(img_save_dir,new_dir):
    base_img_dir = r"D:\PycharmWorkPlace\VOD\base_img"
    if os.path.exists(new_dir):
        shutil.rmtree(new_dir)
    os.makedirs(new_dir)
    img_list = TPerformance.get_img_list_for_dir(img_save_dir)
    base_img_list = TPerformance.get_img_list_for_dir(base_img_dir)
    # base_img = "./base370.jpg"
    # base_img = Image.open(base_img)
    for i in range(len(img_list)):
        base_img = Image.open(base_img_list[i])
        main_img = Image.open(img_list[i])
        base_img.paste(main_img, (1480,690,2220,1430))
        index = i + 1
        save_path = new_dir + '/' + "image%04d" % index +'.jpg'
        base_img.save(save_path)
    print("生成合成图完毕")

if __name__ == "__main__":
    img_dir = "D:\PycharmWorkPlace\VOD\qr_img"
    # generate_qr_code(img_dir)
    # get_qr_code(img_dir)
    #
    sd_dir = "D:\PycharmWorkPlace\VOD\chane_img_sd"
    dir_4k = "D:\PycharmWorkPlace\VOD\qr_img_4k"
    # composite_change_img(img_dir,sd_dir)
    #
    # print("识别裁剪后的合成图二维码")
    # s_dir = r"D:\20img"
    # c = r"D:\123"
    get_qr_code(r"D:\60img")